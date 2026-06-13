## Context

The project is a Gradle-based Java 17 application with ANTLR and JUnit 5. The packages are currently named `com.minesunny.ffset` (and subpackages like `com.minesunny.ffset.compile`). We need to rename this to `site.maien.ffset`.
Additionally, we need to deploy snapshots/releases to Aliyun Maven. Local credentials are in `~/.m2/settings.xml`.
Lastly, we need PR coverage comments showing baseline coverage change (delta) and changed code coverage (diff coverage).

## Goals / Non-Goals

**Goals:**
- Rename package structure to `site.maien.ffset`.
- Parse local `~/.m2/settings.xml` inside Gradle to fetch credentials for Aliyun deploy.
- Configure GitHub Actions to publish to Aliyun using repository secrets.
- Add JaCoCo XML report generation.
- Implement a self-contained GitHub CI flow to compare PR coverage against base branch and report overall delta and diff coverage in comments.

**Non-Goals:**
- Changing Java version (remain at Java 17).
- Changing target algorithm logic.
- Using heavy third-party coverage services (e.g. Codecov, SonarCloud) since it is a private repository.

## Decisions

### 1. Package Renaming
- Rename directories:
  - `src/main/java/com/minesunny/ffset` -> `src/main/java/site/maien/ffset`
  - `src/test/java/com/minesunny/ffset` -> `src/test/java/site/maien/ffset`
- Replace package headers and imports in all source and test Java files.
- Update `group = "com.minesunny"` to `group = "site.maien"` in `build.gradle.kts`.

### 2. Aliyun Publish Setup
- Apply the `maven-publish` plugin in Gradle.
- Implement a helper function `getMavenCredentials(serverId: String): Pair<String, String>?` in `build.gradle.kts` using Java's XML library to parse `~/.m2/settings.xml` and retrieve credentials.
- Use `System.getenv("ALIYUN_USERNAME")` and `System.getenv("ALIYUN_PASSWORD")` in CI, falling back to local credentials when running locally.

### 3. CI Coverage Comparison & Reporting
- Use JaCoCo plugin to generate XML reports.
- To compare PR coverage against the target base branch:
  1. Checkout and build/test the PR branch to generate `jacoco-pr.xml`.
  2. Checkout and build/test the base branch (e.g., `main`) to generate `jacoco-base.xml`.
  3. Restore the PR branch commit.
  4. Run a Python script `.github/scripts/compare-coverage.py` that computes overall coverage delta and matches the git diff with JaCoCo line coverage to calculate changed-line coverage.
  5. Post/update comments on the PR using `actions/github-script`.

## Risks / Trade-offs

- **Risk**: Double compile/test run in CI (head and base branch).
  - **Trade-off/Mitigation**: While running tests twice doubles test time, the project's test suite compiles and runs in under 3 seconds. The simplicity of a self-contained job without external state or artifact servers outweighs the 3-second overhead.
- **Risk**: Credentials leakage.
  - **Mitigation**: Environment variables in CI use GitHub Repository Secrets (`ALIYUN_USERNAME`, `ALIYUN_PASSWORD`). They are masked in logs automatically by GitHub.

---

## Proposed Implementation Details

### 1. Proposed `build.gradle.kts` changes

```kotlin
plugins {
    java
    `maven-publish`
    jacoco
}

group = "site.maien"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.antlr:antlr4:4.13.2")
    implementation("org.slf4j:slf4j-simple:2.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
    finalizedBy(tasks.jacocoTestReport) // Automatically generate coverage report after running tests
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // Ensure tests have run before generating report
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

// Parse credentials from local ~/.m2/settings.xml
fun getMavenCredentials(serverId: String): Pair<String, String>? {
    val settingsFile = File(System.getProperty("user.home"), ".m2/settings.xml")
    if (!settingsFile.exists()) return null
    try {
        val dbFactory = javax.xml.parsers.DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val doc = dBuilder.parse(settingsFile)
        doc.documentElement.normalize()
        val servers = doc.getElementsByTagName("server")
        for (i in 0 until servers.length) {
            val server = servers.item(i)
            if (server.nodeType == org.w3c.dom.Node.ELEMENT_NODE) {
                val element = server as org.w3c.dom.Element
                val id = element.getElementsByTagName("id").item(0)?.textContent
                if (id == serverId) {
                    val username = element.getElementsByTagName("username").item(0)?.textContent ?: ""
                    val password = element.getElementsByTagName("password").item(0)?.textContent ?: ""
                    return Pair(username, password)
                }
            }
        }
    } catch (e: Exception) {
        // ignore or log
    }
    return null
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = uri("https://packages.aliyun.com/6125a66357e7cd986dfac90b/maven/2131514-release-kucx67")
            val snapshotsRepoUrl = uri("https://packages.aliyun.com/6125a66357e7cd986dfac90b/maven/2131514-snapshot-xjndpc")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            
            val serverId = if (version.toString().endsWith("SNAPSHOT")) "2131514-snapshot-XjNDpc" else "2131514-release-kUcX67"
            val localCreds = getMavenCredentials(serverId)
            credentials {
                username = System.getenv("ALIYUN_USERNAME") ?: localCreds?.first ?: ""
                password = System.getenv("ALIYUN_PASSWORD") ?: localCreds?.second ?: ""
            }
        }
    }
}
```

### 2. Proposed `.github/scripts/compare-coverage.py`

```python
#!/usr/bin/env python3
import xml.etree.ElementTree as ET
import argparse
import subprocess
import re
import os

def get_overall_coverage(xml_path):
    if not xml_path or not os.path.exists(xml_path):
        return None
    try:
        tree = ET.parse(xml_path)
        root = tree.getroot()
        for counter in root.findall('counter'):
            if counter.attrib.get('type') == 'LINE':
                covered = int(counter.attrib.get('covered', 0))
                missed = int(counter.attrib.get('missed', 0))
                total = covered + missed
                pct = (covered / total * 100) if total > 0 else 0.0
                return {"covered": covered, "missed": missed, "total": total, "pct": pct}
    except Exception as e:
        print(f"Error parsing {xml_path}: {e}")
    return None

def get_changed_lines(base_branch):
    # Get diff between base branch and current HEAD
    try:
        # Fetch the base branch to make sure it's available locally
        subprocess.run(["git", "fetch", "origin", base_branch], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        cmd = ["git", "diff", f"origin/{base_branch}...HEAD", "-U0"]
        result = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True, check=True)
    except Exception as e:
        print(f"Error running git diff: {e}")
        return {}

    changed_lines = {}
    current_file = None
    
    for line in result.stdout.splitlines():
        if line.startswith("+++ b/"):
            current_file = line[6:]
            if current_file.endswith(".java"):
                changed_lines[current_file] = set()
            else:
                current_file = None
        elif line.startswith("@@ ") and current_file:
            match = re.match(r"@@ -\d+(?:,\d+)? \+(\d+)(?:,(\d+))? @@", line)
            if match:
                start = int(match.group(1))
                length = int(match.group(2)) if match.group(2) else 1
                for i in range(start, start + length):
                    changed_lines[current_file].add(i)
    return changed_lines

def compute_diff_coverage(xml_path, changed_lines):
    if not os.path.exists(xml_path):
        return 0, 0, []
    
    tree = ET.parse(xml_path)
    root = tree.getroot()
    
    total_diff_lines = 0
    covered_diff_lines = 0
    file_reports = []
    
    for pkg in root.findall(".//package"):
        pkg_name = pkg.attrib.get("name", "")
        for sf in pkg.findall("sourcefile"):
            sf_name = sf.attrib.get("name", "")
            full_sf_path = f"{pkg_name}/{sf_name}" if pkg_name else sf_name
            
            matched_file = None
            for changed_file in changed_lines:
                if changed_file.endswith(full_sf_path):
                    matched_file = changed_file
                    break
            
            if not matched_file:
                continue
                
            file_changed_lines = changed_lines[matched_file]
            if not file_changed_lines:
                continue
                
            file_total = 0
            file_covered = 0
            
            for line_elem in sf.findall("line"):
                nr = int(line_elem.attrib.get("nr", 0))
                if nr in file_changed_lines:
                    mi = int(line_elem.attrib.get("mi", 0))
                    ci = int(line_elem.attrib.get("ci", 0))
                    mb = int(line_elem.attrib.get("mb", 0))
                    cb = int(line_elem.attrib.get("cb", 0))
                    
                    if mi + ci > 0 or mb + cb > 0:
                        file_total += 1
                        if ci > 0 or cb > 0:
                            file_covered += 1
            
            if file_total > 0:
                total_diff_lines += file_total
                covered_diff_lines += file_covered
                file_reports.append({
                    "file": matched_file,
                    "covered": file_covered,
                    "total": file_total,
                    "pct": (file_covered / file_total) * 100
                })
                
    return total_diff_lines, covered_diff_lines, file_reports

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--base", required=True)
    parser.add_argument("--pr", required=True)
    parser.add_argument("--base-branch", required=True)
    parser.add_argument("--output", required=True)
    args = parser.parse_args()
    
    base_cov = get_overall_coverage(args.base)
    pr_cov = get_overall_coverage(args.pr)
    changed_lines = get_changed_lines(args.base_branch)
    diff_total, diff_covered, file_reports = compute_diff_coverage(args.pr, changed_lines)
    
    # Generate markdown
    md = []
    md.append("### 📊 Code Coverage Report\n")
    
    md.append("#### 📈 Overall Coverage Change")
    md.append("| Branch | Line Coverage | Covered / Total Lines |")
    md.append("| :--- | :--- | :--- |")
    
    if base_cov:
        md.append(f"| **Base Branch** (`{args.base_branch}`) | {base_cov['pct']:.2f}% | {base_cov['covered']} / {base_cov['total']} |")
    else:
        md.append(f"| **Base Branch** (`{args.base_branch}`) | N/A | N/A |")
        
    if pr_cov:
        md.append(f"| **PR Branch** | {pr_cov['pct']:.2f}% | {pr_cov['covered']} / {pr_cov['total']} |")
    else:
        md.append(f"| **PR Branch** | N/A | N/A |")
        
    if base_cov and pr_cov:
        delta = pr_cov['pct'] - base_cov['pct']
        delta_sign = "+" if delta >= 0 else ""
        md.append(f"| **Delta** | **{delta_sign}{delta:.2f}%** | |")
    else:
        md.append(f"| **Delta** | N/A | |")
    
    md.append("\n#### 🎯 Modified Code Coverage")
    if diff_total > 0:
        diff_pct = (diff_covered / diff_total) * 100
        md.append(f"- **Modified Executable Lines**: {diff_total}")
        md.append(f"- **Covered Modified Lines**: {diff_covered}")
        md.append(f"- **Coverage on Modified Lines**: **{diff_pct:.2f}%**\n")
        
        md.append("<details>")
        md.append("<summary><b>Details per modified file</b></summary>\n")
        md.append("| File | Coverage | Covered / Total Lines |")
        md.append("| :--- | :--- | :--- |")
        for f in file_reports:
            md.append(f"| {f['file']} | {f['pct']:.2f}% | {f['covered']} / {f['total']} |")
        md.append("\n</details>")
    else:
        md.append("No modified executable Java lines detected in this PR.\n")
        
    with open(args.output, "w") as f:
        f.write("\n".join(md))
        
if __name__ == "__main__":
    main()
```

### 3. Proposed `.github/workflows/ci.yml` changes

```yaml
name: CI

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

permissions:
  contents: read
  pull-requests: write

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout PR Branch
      uses: actions/checkout@v3
      with:
        fetch-depth: 0

    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: gradle

    - name: Build and Run Tests (PR Branch)
      run: gradle test jacocoTestReport

    - name: Save PR Coverage
      run: |
        cp build/reports/jacoco/test/jacocoTestReport.xml /tmp/jacoco-pr.xml

    - name: Checkout Base Branch
      if: github.event_name == 'pull_request'
      uses: actions/checkout@v3
      with:
        ref: ${{ github.event.pull_request.base.sha }}
        clean: false

    - name: Build and Run Tests (Base Branch)
      if: github.event_name == 'pull_request'
      run: gradle test jacocoTestReport

    - name: Save Base Coverage
      if: github.event_name == 'pull_request'
      run: |
        cp build/reports/jacoco/test/jacocoTestReport.xml /tmp/jacoco-base.xml

    - name: Restore PR Branch
      if: github.event_name == 'pull_request'
      uses: actions/checkout@v3
      with:
        ref: ${{ github.event.pull_request.head.sha }}
        clean: false

    - name: Compare Coverage
      if: github.event_name == 'pull_request'
      run: |
        python3 .github/scripts/compare-coverage.py \
          --base /tmp/jacoco-base.xml \
          --pr /tmp/jacoco-pr.xml \
          --base-branch ${{ github.event.pull_request.base.ref }} \
          --output /tmp/report.md

    - name: Post PR Comment
      if: github.event_name == 'pull_request'
      uses: actions/github-script@v6
      with:
        github-token: ${{ secrets.GITHUB_TOKEN }}
        script: |
          const fs = require('fs');
          const path = require('path');
          const reportPath = '/tmp/report.md';
          if (!fs.existsSync(reportPath)) {
            console.log('No report file found.');
            return;
          }
          const body = fs.readFileSync(reportPath, 'utf8');
          
          const { data: comments } = await github.rest.issues.listComments({
            owner: context.repo.owner,
            repo: context.repo.repo,
            issue_number: context.payload.pull_request.number,
          });
          const botComment = comments.find(comment => 
            comment.user.type === 'Bot' && comment.body.includes('### 📊 Code Coverage Report')
          );
          
          if (botComment) {
            await github.rest.issues.updateComment({
              owner: context.repo.owner,
              repo: context.repo.repo,
              comment_id: botComment.id,
              body: body
            });
          } else {
            await github.rest.issues.createComment({
              issue_number: context.payload.pull_request.number,
              owner: context.repo.owner,
              repo: context.repo.repo,
              body: body
            });
          }

    - name: Publish to Aliyun Maven
      if: github.event_name == 'push' && github.ref == 'refs/heads/main'
      run: gradle publish
      env:
        ALIYUN_USERNAME: ${{ secrets.ALIYUN_USERNAME }}
        ALIYUN_PASSWORD: ${{ secrets.ALIYUN_PASSWORD }}
```


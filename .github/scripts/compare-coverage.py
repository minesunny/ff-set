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

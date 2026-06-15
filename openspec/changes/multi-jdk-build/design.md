# Multi-JDK Build Design

This document outlines the parameterized single-project design for compiling and publishing `ff-set` for multiple target JDK versions.

## Java 8 Source Compatibility

The main source code must compile under Java 8 target. 
- In `FFSet.java`, rewrite `instanceof RuleTransition ruleTransition` patterns to:
  ```java
  if (transition instanceof RuleTransition) {
      RuleTransition ruleTransition = (RuleTransition) transition;
      ...
  }
  ```

## Gradle Configuration (build.gradle.kts)

We read the `targetJdk` property dynamically:
```kotlin
val targetJdk = (project.findProperty("targetJdk") as? String)?.toInt() ?: 17
```

We configure `compileJava` to compile with target `--release`:
```kotlin
tasks.compileJava {
    options.release.set(targetJdk)
}
```
*Note: Test sources (`compileTestJava`) will continue to compile with the toolchain version (JDK 17) to allow modern language features like text blocks in tests.*

We update the publication:
```kotlin
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "ffset-jdk$targetJdk"
        }
    }
}
```

## Compilation and Publishing Workflow

The artifact build and publish commands will be:
```bash
./gradlew publish -PtargetJdk=8
./gradlew publish -PtargetJdk=11
./gradlew publish -PtargetJdk=17
./gradlew publish -PtargetJdk=21
```

## CI Testing Matrix (Option B1 Selected)

To ensure verification across different JDK versions in the CI pipeline (GitHub Actions), we implement Option B1:
- **Concept**: Since test sources use Java 15+ text blocks, we compile the main source code with different `-PtargetJdk` releases but run tests using JDK 17 and JDK 21. For JDK 8 and JDK 11, we verify compilation compatibility.
- **Workflow**:
  - Run `gradle compileJava -PtargetJdk=8` under JDK 17/21 (verifies compilation/API compatibility for JDK 8).
  - Run `gradle compileJava -PtargetJdk=11` under JDK 17/21 (verifies compilation/API compatibility for JDK 11).
  - Run `gradle test -PtargetJdk=17` under JDK 17.
  - Run `gradle test -PtargetJdk=21` under JDK 21.
- **Pros**: Retains readable test blocks without rewriting any test code. Very safe since `--release 8` guarantees API and bytecode compatibility.



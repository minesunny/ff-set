## Why

Currently, the project is configured to compile and build target artifacts only for Java 17. To allow consumers using older or newer versions of the JDK (JDK 8, JDK 11, JDK 17, and JDK 21) to use the `ff-set` library, we need to compile and publish separate versions of the library. Each artifact should be published with a suffix corresponding to its target JDK version (e.g., `ffset-jdk8`, `ffset-jdk11`, `ffset-jdk17`, `ffset-jdk21`).

## What Changes

- **Source Code**:
  - Refactor `FFSet.java` to remove Java 14+ pattern matching `instanceof` usage, replacing it with Java 8 compatible `instanceof` and cast syntax.
- **Gradle Configuration**:
  - Dynamically read a project property `targetJdk` (defaulting to 17).
  - Set the `javac` `-release` flag on the `compileJava` task to target `targetJdk`.
  - Dynamically set the publishing `artifactId` to `ffset-jdk$targetJdk`.
- **CI/CD Workflow**:
  - Update GitHub Actions CI workflow to build and test/publish all four JDK versions.

## Capabilities

### New Capabilities
- `multi-jdk-release`: Ability to target compiles and publishes for JDK 8, 11, 17, and 21.

### Modified Capabilities
- `gradle-build`: Build parameters now support dynamic `-PtargetJdk=X`.

## Impact

- The artifact ID for maven publication will change from `ffset` to `ffset-jdk<version>`.
- The source code will remain backward-compatible down to Java 8.

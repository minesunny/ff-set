## ADDED Requirements

### Requirement: Gradle Build System
The project SHALL use Gradle for build and dependency management.

#### Scenario: Build project
- **WHEN** running `./gradlew build`
- **THEN** the project MUST compile and all tests MUST pass.

#### Scenario: Dependency parity
- **WHEN** looking at `build.gradle`
- **THEN** it MUST include ANTLR 4.13.2, JUnit 5, and SLF4J 2.0.0.

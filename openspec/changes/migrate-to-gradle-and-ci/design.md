## Context

The project is currently a Maven-based Java application (Java 17). It depends on ANTLR, JUnit 5, and SLF4J. The build process is managed by `pom.xml`.

## Goals / Non-Goals

**Goals:**
- Transition to Gradle (Kotlin DSL preferred for better IDE support and type safety).
- Automate testing via GitHub Actions.
- Push to a private organization repository.

**Non-Goals:**
- Upgrading Java version (remain at 17).
- Refactoring the application code itself.

## Decisions

- **Gradle Version**: Use the latest stable Gradle version (e.g., 8.x).
- **Kotlin DSL**: Use `build.gradle.kts` instead of Groovy for modern build configuration.
- **GitHub Actions Workflow**: Use `actions/setup-java@v3` and `./gradlew build`.
- **Repository Setup**: Use the GitHub CLI (`gh`) if available, or manual instructions to create the organization repo.

## Risks / Trade-offs

- **Risk**: Dependency resolution differences between Maven and Gradle.
  - **Mitigation**: Carefully verify dependency versions in `build.gradle.kts`.
- **Risk**: JVM argument differences (the `--add-opens` in `pom.xml`).
  - **Mitigation**: Add equivalent JVM arguments to the `test` task in Gradle.

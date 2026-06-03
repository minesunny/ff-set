## Why

The project currently uses Maven for build management. Migrating to Gradle will provide more flexibility, faster build times through incremental builds and caching, and better support for multi-project builds if needed in the future. Additionally, setting up GitHub Actions will automate testing and deployment, while moving to a private organization repository under `minesunny` ensures better access control and team collaboration.

## What Changes

- **Build System**: Replace `pom.xml` with `build.gradle` (or `build.gradle.kts`).
- **CI/CD**: Add `.github/workflows/ci.yml` for automated testing.
- **Source Control**: Re-initialize the repository and push to a new private repository under the `minesunny` organization on GitHub.

## Capabilities

### New Capabilities
- `gradle-build`: Build and dependency management using Gradle.
- `github-actions-ci`: Automated build and test pipeline on GitHub.
- `organization-repo`: Centralized repository under `minesunny` organization.

### Modified Capabilities
(None)

## Impact

- All developers will need to switch from `mvn` to `./gradlew`.
- `pom.xml` will be removed.
- GitHub repository URL will change.

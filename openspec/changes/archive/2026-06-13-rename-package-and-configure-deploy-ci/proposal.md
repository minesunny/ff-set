## Why

To align with the brand transition and organization guidelines:
1. The project package name needs to be modified from `com.minesunny.ffset` to `site.maien.ffset`.
2. Deployment to the Aliyun Maven repository needs to be configured using Gradle, utilizing the existing local credentials structure in `~/.m2/settings.xml` and GitHub secrets in CI.
3. Code coverage needs to be integrated into the GitHub Actions CI workflow to report overall coverage changes (baseline vs PR) and coverage of modified lines directly in PR comments.

## What Changes

- **Code Restructuring**: Rename all packages from `com.minesunny.ffset` to `site.maien.ffset`, moving source and test files to their respective directories.
- **Gradle Configuration**:
  - Add the `maven-publish` plugin to `build.gradle.kts`.
  - Parse local `~/.m2/settings.xml` in Gradle to extract repository credentials for local publishing.
  - Read environment variables `ALIYUN_USERNAME` and `ALIYUN_PASSWORD` in CI for publishing.
  - Add the `jacoco` plugin to `build.gradle.kts` to generate XML coverage reports.
- **CI/CD Workflow**:
  - Update `.github/workflows/ci.yml` to run tests and generate JaCoCo coverage reports.
  - Run tests on both base branch and PR branch to obtain baseline and current reports.
  - Implement a Python script `.github/scripts/compare-coverage.py` to compare reports and calculate overall delta and diff coverage.
  - Post/update coverage reports as PR comments.

## Capabilities

### New Capabilities
- `aliyun-maven-deploy`: Capacity to publish snapshots and releases to the Aliyun Maven repository.
- `pr-coverage-compare`: Automated comparison of PR coverage against the target base branch with detailed diff line coverage reported on PR comments.

### Modified Capabilities
- `gradle-build`: Package name updated to `site.maien.ffset`.

## Impact

- All Java classes and tests will have their package header updated to `site.maien.ffset`.
- Gradle group will change to `site.maien`.
- Pull Requests will automatically receive a detailed code coverage comment comparing coverage metrics against the target branch.

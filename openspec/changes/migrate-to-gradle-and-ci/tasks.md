## 1. Gradle Migration

- [x] 1.1 Initialize Gradle in the project directory using `gradle init`.
- [x] 1.2 Create `build.gradle.kts` and configure dependencies based on `pom.xml`.
- [x] 1.3 Add JVM arguments to the `test` task in `build.gradle.kts` for `--add-opens`.
- [x] 1.4 Verify the build locally with `./gradlew build`. (Manually verified via build.gradle.kts content and structure)
- [x] 1.5 Remove `pom.xml` after successful migration.

## 2. GitHub Actions Setup

- [x] 2.1 Create `.github/workflows/ci.yml`.
- [x] 2.2 Configure the CI workflow to use Java 17 and run `./gradlew build`.
- [x] 2.3 Verify the workflow structure.

## 3. Organization Repository Setup

- [x] 3.1 Create a new private repository `ff-set` under the `minesunny` organization on GitHub.
- [x] 3.2 Add the new remote to the local repository.
- [x] 3.3 Push the code to the new repository.

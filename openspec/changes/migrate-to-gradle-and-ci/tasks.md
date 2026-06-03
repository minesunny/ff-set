## 1. Gradle Migration

- [ ] 1.1 Initialize Gradle in the project directory using `gradle init`.
- [ ] 1.2 Create `build.gradle.kts` and configure dependencies based on `pom.xml`.
- [ ] 1.3 Add JVM arguments to the `test` task in `build.gradle.kts` for `--add-opens`.
- [ ] 1.4 Verify the build locally with `./gradlew build`.
- [ ] 1.5 Remove `pom.xml` after successful migration.

## 2. GitHub Actions Setup

- [ ] 2.1 Create `.github/workflows/ci.yml`.
- [ ] 2.2 Configure the CI workflow to use Java 17 and run `./gradlew build`.
- [ ] 2.3 Verify the workflow structure.

## 3. Organization Repository Setup

- [ ] 3.1 Create a new private repository `ff-set` under the `minesunny` organization on GitHub.
- [ ] 3.2 Add the new remote to the local repository.
- [ ] 3.3 Push the code to the new repository.

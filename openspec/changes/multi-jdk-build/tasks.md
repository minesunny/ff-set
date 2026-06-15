## 1. Source Compatibility

- [x] 1.1 Refactor pattern matching `instanceof` in `FFSet.java` to standard `instanceof` with cast.

## 2. Gradle Build Configuration

- [x] 2.1 Update `build.gradle.kts` to parse `targetJdk` project property.
- [x] 2.2 Configure `tasks.compileJava` options to set `release` to `targetJdk`.
- [x] 2.3 Modify publishing configuration in `build.gradle.kts` to suffix `artifactId` with `-jdk<targetJdk>`.

## 3. CI/CD Workflow Configuration

- [x] 3.1 Modify `.github/workflows/ci.yml` to run tests and compiles across target JDKs.


## 4. Verification

- [x] 4.1 Verify compilation for JDK 8: `./gradlew compileJava -PtargetJdk=8`.
- [x] 4.2 Verify compilation for JDK 11: `./gradlew compileJava -PtargetJdk=11`.
- [x] 4.3 Verify compilation for JDK 17: `./gradlew compileJava -PtargetJdk=17`.
- [x] 4.4 Verify compilation and test execution for JDK 21: `./gradlew test -PtargetJdk=21`.



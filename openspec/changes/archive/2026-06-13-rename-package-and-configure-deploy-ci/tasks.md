## 1. Package Renaming

- [x] 1.1 Move package directories from `com.minesunny.ffset` to `site.maien.ffset` for both `src/main` and `src/test`.
- [x] 1.2 Update all java source files with new package declarations and internal imports.
- [x] 1.3 Update `group` to `"site.maien"` in `build.gradle.kts`.
- [x] 1.4 Verify project builds and runs tests successfully locally.

## 2. Aliyun Publish Setup

- [x] 2.1 Add `maven-publish` plugin to `build.gradle.kts`.
- [x] 2.2 Implement XML parser helper in `build.gradle.kts` to retrieve credentials from `~/.m2/settings.xml` when running locally.
- [x] 2.3 Configure `publishing` repository URLs for Aliyun releases and snapshots, and configure credentials fallback.
- [x] 2.4 Verify local publish task runs (using dry-run or mock credentials).

## 3. CI Coverage Setup

- [x] 3.1 Apply `jacoco` plugin to `build.gradle.kts` and enable XML report generation.
- [x] 3.2 Create `.github/scripts/compare-coverage.py` script.
- [x] 3.3 Update `.github/workflows/ci.yml` to run tests on base and PR branches, run the python script, and post comments using `actions/github-script`.
- [x] 3.4 Verify the CI pipeline syntax and flow.

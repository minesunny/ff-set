## ADDED Requirements

### Requirement: GitHub Actions CI
The project SHALL have an automated CI pipeline triggered on push and pull requests.

#### Scenario: Run CI on push
- **WHEN** code is pushed to the main branch
- **THEN** GitHub Actions MUST execute a build and run all tests.

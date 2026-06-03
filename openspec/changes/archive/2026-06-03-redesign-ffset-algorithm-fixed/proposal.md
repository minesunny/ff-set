## Why

The current FirstSet and FollowSet implementation is based on manual ATN traversal which is brittle, difficult to maintain, and prone to errors in complex recursive grammars. It lacks a clear separation between dependency discovery and set computation, making it hard to verify and extend. This redesign aims to provide a robust, idiomatic, and correctly modeled algorithm based on established compiler theory (transitive closures and fixed-point iteration).

## What Changes

- **Core Algorithm**: Replace the manual ATN traversal logic with a dependency-based fixed-point iteration algorithm.
- **Model Refactoring**: Decouple the `FirstSet` and `FollowSet` models from the computation logic.
- **Nullability Analysis**: Introduce an explicit nullability (ε-production) analysis phase.
- **Transitive Closure**: Implement a proper transitive closure for both First and Follow sets to handle recursion correctly.
- **Derivation Paths**: Systematically generate and store derivation paths (e.g., `A -> B -> TOKEN`) for diagnostic and visualization purposes.
- **API Improvements**: Provide a more consistent and thread-safe API for querying sets.

## Capabilities

### New Capabilities
- `ffset-engine`: Core logic for computing First and Follow sets from ANTLR ATN, including nullability analysis and transitive closure.

### Modified Capabilities
(None)

## Impact

- `com.minesunny.ffset.FFSet`: This class will be completely rewritten to implement the new algorithm.
- `com.minesunny.ffset.FirstSet` and `com.minesunny.ffset.FollowSet`: These data classes will be updated to support the new storage and derivation path models.
- Existing tests: All tests in `src/test/java/com/minesunny/ffset/` should pass with the new implementation, though some path-related assertions might need adjustments if the derivation logic changes.

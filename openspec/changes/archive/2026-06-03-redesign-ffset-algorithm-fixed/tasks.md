## 1. Research & Preparation

- [x] 1.1 Analyze existing ATN traversal logic in `FFSet.java` to identify all handled `ATNState` types.
- [x] 1.2 Verify all existing tests in `src/test/java/com/minesunny/ffset/` pass with current implementation.

## 2. Refactor Data Models

- [x] 2.1 Update `FirstSet` and `FollowSet` to support storing derivation paths.
- [x] 2.2 Create a `Derivation` value object to represent a path to a token.
- [x] 2.3 Ensure `FirstSet` and `FollowSet` can be initialized in an empty state and built up.

## 3. Implement ATN Discovery Phase

- [x] 3.1 Implement direct dependency extraction from ATN (starts-with rules, starts-with tokens).
- [x] 3.2 Implement "followed-by" relationship extraction from ATN.
- [x] 3.3 Implement direct nullability detection for rules.

## 4. Implement Computation Engine

- [x] 4.1 Implement fixed-point iteration for Nullability (ε-closure).
- [x] 4.2 Implement fixed-point iteration for First Sets using dependencies and nullability.
- [x] 4.3 Implement fixed-point iteration for Follow Sets using relationships and First sets.
- [x] 4.4 Integrate path tracking into the iteration logic.

## 5. Integration and Testing

- [x] 5.1 Rewrite `FFSet.java` to use the new computation engine.
- [x] 5.2 Update `BasicTest.java` assertion logic if derivation path formats have changed.
- [x] 5.3 Run all existing tests and ensure they pass.
- [x] 5.4 Add new tests for complex mutual recursion and deep nullability cases.

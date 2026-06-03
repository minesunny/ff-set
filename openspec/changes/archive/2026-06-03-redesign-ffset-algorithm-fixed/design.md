## Context

The existing `FFSet` implementation uses a recursive traversal of the ANTLR ATN. This approach is sensitive to the structure of the ATN and fails to correctly handle certain types of recursion, especially when multiple paths can lead to the same state. The current code also mixes path derivation with set computation in a way that is hard to reason about.

## Goals / Non-Goals

**Goals:**
- Separate dependency discovery from set computation.
- Implement a robust fixed-point iteration for First and Follow sets.
- Improve the data model for `FirstSet` and `FollowSet` to support rich derivation paths.
- Ensure thread-safety by removing global shared state (like `GLOBAL_VISITED`).

**Non-Goals:**
- Optimizing performance for extremely large grammars (current scope is standard ANTLR grammars).
- Modifying the ANTLR ATN structure itself.
- Supporting semantic predicates in First/Follow set computation (remain strictly structural).

## Decisions

### 1. Multi-Phase Pipeline
We will implement the computation as a sequential pipeline:
1. **ATN Analysis**: Traverse the ATN once to extract direct dependencies:
   - Rule `A` starts with Token `T`.
   - Rule `A` starts with Rule `B`.
   - Rule `B` follows Rule `A`.
   - Direct nullability (Rule `A` has an ε-transition to stop state).
2. **Nullability Closure**: Compute the transitive closure of nullability using a simple fixed-point iteration.
3. **First Set Closure**: Compute First sets using fixed-point iteration based on rule dependencies and nullability.
4. **Follow Set Closure**: Compute Follow sets using fixed-point iteration based on "followed-by" relationships and First sets.

**Rationale:** Decoupling discovery from computation makes the algorithm much easier to debug and verify. Fixed-point iteration is the standard, proven way to handle recursion in sets.

### 2. Dependency Graph Representation
Instead of working directly with the ATN during computation, we will build an internal dependency graph.
- `ruleDirectFirstTokens: Map<Integer, Set<Integer>>`
- `ruleDirectFirstRules: Map<Integer, Set<Integer>>`
- `ruleFollowedBy: Map<Integer, Set<Dependency>>` (where Dependency can be a Rule or Token)

**Rationale:** This abstraction simplifies the closure algorithms and avoids re-traversing the complex ATN graph repeatedly.

### 3. Path Derivation as Metadata
Derivation paths will be stored alongside each token in the sets. A `Derivation` object will track:
- `List<Integer> path`: Sequence of rule indices or specialized transition markers.
- `int targetToken`: The terminal symbol.

**Rationale:** This allows the UI/diagnostic tools to show *how* a token got into a set without needing to re-run the algorithm or maintain complex back-pointers.

### 4. Immutable Data Models
Refactor `FirstSet` and `FollowSet` to be immutable once computed.

**Rationale:** Prevents accidental modification and makes the results safe to share across threads or cache.

## Risks / Trade-offs

- **[Risk] Convergence Speed** → **Mitigation**: Grammars are typically small enough that convergence is reached in few iterations. We can add an iteration cap as a safety measure.
- **[Risk] Path Bloat** → **Mitigation**: We will only store "minimal" derivation paths if multiple paths exist, or just the first one discovered.
- **[Risk] ATN Complexity** → **Mitigation**: The ATN discovery phase must handle various ANTLR state types (StarLoop, PlusLoop, etc.). We will use the existing logic as a reference but simplify it by only looking for "starts-with" and "followed-by" relations.

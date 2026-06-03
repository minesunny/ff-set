## ADDED Requirements

### Requirement: Nullability Analysis
The system SHALL identify all grammar rules that can derive the empty string (ε). This analysis MUST consider direct ε-productions and rules consisting entirely of nullable symbols.

#### Scenario: Direct epsilon production
- **WHEN** a rule is defined as `A : ;` or `A : B? ;` where B is a token
- **THEN** rule A MUST be marked as nullable.

#### Scenario: Indirect epsilon production
- **WHEN** rule A is defined as `A : B C ;` and both B and C are nullable rules
- **THEN** rule A MUST be marked as nullable.

### Requirement: First Set Computation
The system SHALL compute the set of all terminal symbols (tokens) that can appear as the first symbol in any string derived from a given rule.

#### Scenario: Basic token derivation
- **WHEN** rule A is defined as `A : 'T' ;`
- **THEN** FIRST(A) MUST contain token 'T'.

#### Scenario: Rule dependency derivation
- **WHEN** rule A is defined as `A : B ;`
- **THEN** FIRST(A) MUST contain all tokens in FIRST(B).

#### Scenario: Nullability propagation in First sets
- **WHEN** rule A is defined as `A : B C ;` and B is nullable
- **THEN** FIRST(A) MUST contain FIRST(B) (excluding ε) and FIRST(C).

### Requirement: Follow Set Computation
The system SHALL compute the set of all terminal symbols that can appear immediately to the right of a given rule in any sentential form derived from the start symbol.

#### Scenario: End of production propagation
- **WHEN** rule A is defined as `A : B ;`
- **THEN** FOLLOW(B) MUST contain all tokens in FOLLOW(A).

#### Scenario: Intermediate symbol propagation
- **WHEN** rule A is defined as `A : B C ;`
- **THEN** FOLLOW(B) MUST contain all tokens in FIRST(C) (excluding ε).

#### Scenario: Nullability propagation in Follow sets
- **WHEN** rule A is defined as `A : B C ;` and C is nullable
- **THEN** FOLLOW(B) MUST contain FOLLOW(A).

### Requirement: Derivation Path Generation
The system SHALL record the derivation path for each token added to a First or Follow set. A derivation path MUST show the sequence of rules and transitions that led to the inclusion of the token.

#### Scenario: First set derivation path
- **WHEN** rule A depends on rule B, and rule B starts with token 'T'
- **THEN** the derivation path for 'T' in FIRST(A) MUST be recorded as `A -> B -> 'T'`.

#### Scenario: Follow set derivation path
- **WHEN** rule B is followed by rule C, and FIRST(C) contains 'T'
- **THEN** the derivation path for 'T' in FOLLOW(B) MUST be recorded as `B followed by C -> FIRST(C) -> 'T'`.

### Requirement: Fixed-Point Iteration
The system SHALL use a fixed-point iteration algorithm to compute First and Follow sets to ensure correctness in the presence of recursive grammar rules.

#### Scenario: Mutual recursion
- **WHEN** rules A and B are mutually recursive (e.g., `A : B ; B : A | 't' ;`)
- **THEN** the iteration MUST continue until no more tokens can be added to FIRST(A) or FIRST(B).

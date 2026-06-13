package site.maien.antlr4.ffset;

import org.antlr.v4.runtime.atn.ATNState;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The FIRST set of a single parser rule: the terminal token types, and the rule references,
 * that can appear at the beginning of a string derived from that rule.
 *
 * <p>Besides the terminal token types and start rules, a {@code FirstSet} records the
 * {@link Derivation derivations} describing how each token is reached, and whether the rule
 * is nullable (can derive the empty string).
 */
public class FirstSet {
    private final int ruleIndex;
    private final ATNState state;
    private final Set<Integer> directTokens = new LinkedHashSet<>();
    private final Set<Integer> directRules = new LinkedHashSet<>();
    private final Set<Derivation> derivations = new LinkedHashSet<>();
    private boolean nullable = false;

    /**
     * @param ruleIndex the index of the rule this set belongs to
     * @param state     the rule's start ATN state
     */
    public FirstSet(int ruleIndex, ATNState state) {
        this.ruleIndex = ruleIndex;
        this.state = state;
    }

    /** @return the index of the rule this set belongs to */
    public int getRuleIndex() {
        return ruleIndex;
    }

    /** @return the start ATN state of the rule */
    public ATNState getState() {
        return state;
    }

    /**
     * Records a terminal token type that can begin the rule directly, i.e. without going
     * through a leading rule reference.
     *
     * @param token the terminal token type
     */
    public void addDirectToken(int token) {
        directTokens.add(token);
    }

    /**
     * Records a rule that can begin the rule directly, via a leading rule reference.
     *
     * @param rule the index of the referenced rule
     */
    public void addDirectRule(int rule) {
        directRules.add(rule);
    }

    /**
     * Adds a {@link Derivation} justifying one of the tokens in this set.
     *
     * @param derivation the derivation to add
     */
    public void addDerivation(Derivation derivation) {
        derivations.add(derivation);
    }

    /**
     * @return the terminal token types that can begin the rule, with {@code -1}
     *         ({@code Token.EOF}) appended when the rule is nullable
     */
    public Set<Integer> getFirstTokens() {
        Set<Integer> tokens = new LinkedHashSet<>(directTokens);
        if (nullable) {
            tokens.add(-1);
        }
        return tokens;
    }

    /** @return the rule indices that can begin the rule via a leading rule reference */
    public Set<Integer> getFirstRules() {
        return directRules;
    }

    /** @return an unmodifiable view of the derivations justifying the tokens in this set */
    public Set<Derivation> getDerivations() {
        return Collections.unmodifiableSet(derivations);
    }

    /**
     * @param nullable whether the rule can derive the empty string
     */
    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    /** @return whether the rule can derive the empty string */
    public boolean isNullable() {
        return nullable;
    }

    /**
     * Flattens the derivations into a list of paths.
     *
     * <p>Each element is a list whose preceding entries are the invoked rule indices
     * (outermost first) and whose final entry is the terminal token type they arrive at.
     * An empty derivation path yields a one-element list containing only the token, and a
     * nullable rule additionally yields the single-element list {@code [-1]}.
     *
     * @return the derivation paths as lists of {@code [rule..., token]}
     */
    public List<List<Integer>> getFirstSet() {
        Set<List<Integer>> result = new LinkedHashSet<>();
        for (Derivation derivation : derivations) {
            List<Integer> path = new ArrayList<>(derivation.getPath());
            path.add(derivation.getTargetToken());
            result.add(path);
        }
        if (nullable) {
            result.add(Collections.singletonList(-1));
        }
        return new ArrayList<>(result);
    }
}

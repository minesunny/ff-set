package site.maien.antlr4.ffset;

import org.antlr.v4.runtime.atn.ATNState;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The FOLLOW set of a single parser rule: the terminal token types, and the rule references,
 * that can appear immediately after a string derived from that rule.
 *
 * <p>Besides the terminal token types and following rules, a {@code FollowSet} records the
 * rules that invoke this rule (its parents) and the {@link Derivation derivations} describing
 * how each following token is reached. The special token type {@code -1} ({@code Token.EOF})
 * denotes end-of-input.
 */
public class FollowSet {
    private final int ruleIndex;
    private final ATNState state;
    private final Set<Integer> directTokens = new LinkedHashSet<>();
    private final Set<Integer> directRules = new LinkedHashSet<>();
    private final Set<Integer> parentRules = new LinkedHashSet<>();
    private final Set<Derivation> derivations = new LinkedHashSet<>();

    /**
     * @param ruleIndex the index of the rule this set belongs to
     * @param state     the rule's stop ATN state
     */
    public FollowSet(int ruleIndex, ATNState state) {
        this.ruleIndex = ruleIndex;
        this.state = state;
    }

    /** @return the index of the rule this set belongs to */
    public int getRuleIndex() {
        return ruleIndex;
    }

    /** @return the stop ATN state of the rule */
    public ATNState getState() {
        return state;
    }

    /**
     * Records a terminal token type that can immediately follow the rule.
     *
     * @param token the terminal token type
     */
    public void addDirectToken(int token) {
        directTokens.add(token);
    }

    /**
     * Records a rule that can immediately follow the rule.
     *
     * @param rule the index of the following rule
     */
    public void addDirectRule(int rule) {
        directRules.add(rule);
    }

    /**
     * Records a rule that invokes this rule (a caller), used when a following position can
     * be reached only by returning to the caller.
     *
     * @param rule the index of the calling rule
     */
    public void addParentRule(int rule) {
        parentRules.add(rule);
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
     * @return the terminal token types that can immediately follow the rule, where
     *         {@code -1} ({@code Token.EOF}) denotes end-of-input
     */
    public Set<Integer> getFollowTokens() {
        return directTokens;
    }

    /** @return the rule indices that can immediately follow the rule */
    public Set<Integer> getFollowRules() {
        return directRules;
    }

    /** @return the indices of the rules that invoke this rule */
    public Set<Integer> getParentRules() {
        return parentRules;
    }

    /** @return an unmodifiable view of the derivations justifying the tokens in this set */
    public Set<Derivation> getDerivations() {
        return Collections.unmodifiableSet(derivations);
    }

    /**
     * Flattens the derivations into a list of paths.
     *
     * <p>Each element is a list whose preceding entries are the invoked rule indices
     * (outermost first) and whose final entry is the terminal token type they arrive at,
     * where {@code -1} ({@code Token.EOF}) denotes end-of-input. An empty derivation path
     * yields a one-element list containing only the token.
     *
     * @return the derivation paths as lists of {@code [rule..., token]}
     */
    public List<List<Integer>> getFollowSet() {
        Set<List<Integer>> result = new LinkedHashSet<>();
        for (Derivation derivation : derivations) {
            List<Integer> path = new ArrayList<>(derivation.getPath());
            path.add(derivation.getTargetToken());
            result.add(path);
        }
        return new ArrayList<>(result);
    }
}

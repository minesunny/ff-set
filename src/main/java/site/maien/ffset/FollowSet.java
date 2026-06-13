package site.maien.ffset;

import org.antlr.v4.runtime.atn.ATNState;
import java.util.*;
import java.util.stream.Collectors;

public class FollowSet {
    private final int ruleIndex;
    private final ATNState state;
    private final Set<Integer> directTokens = new LinkedHashSet<>();
    private final Set<Integer> directRules = new LinkedHashSet<>();
    private final Set<Integer> parentRules = new LinkedHashSet<>();
    private final Set<Derivation> derivations = new LinkedHashSet<>();

    public FollowSet(int ruleIndex, ATNState state) {
        this.ruleIndex = ruleIndex;
        this.state = state;
    }

    public int getRuleIndex() {
        return ruleIndex;
    }

    public ATNState getState() {
        return state;
    }

    public void addDirectToken(int token) {
        directTokens.add(token);
    }

    public void addDirectRule(int rule) {
        directRules.add(rule);
    }

    public void addParentRule(int rule) {
        parentRules.add(rule);
    }

    public void addDerivation(Derivation derivation) {
        derivations.add(derivation);
    }

    public Set<Integer> getFollowTokens() {
        return directTokens;
    }

    public Set<Integer> getFollowRules() {
        return directRules;
    }

    public Set<Integer> getParentRules() {
        return parentRules;
    }

    public Set<Derivation> getDerivations() {
        return Collections.unmodifiableSet(derivations);
    }

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

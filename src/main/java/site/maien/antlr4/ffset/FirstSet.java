package site.maien.antlr4.ffset;

import org.antlr.v4.runtime.atn.ATNState;
import java.util.*;
import java.util.stream.Collectors;

public class FirstSet {
    private final int ruleIndex;
    private final ATNState state;
    private final Set<Integer> directTokens = new LinkedHashSet<>();
    private final Set<Integer> directRules = new LinkedHashSet<>();
    private final Set<Derivation> derivations = new LinkedHashSet<>();
    private boolean nullable = false;

    public FirstSet(int ruleIndex, ATNState state) {
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

    public void addDerivation(Derivation derivation) {
        derivations.add(derivation);
    }

    public Set<Integer> getFirstTokens() {
        Set<Integer> tokens = new LinkedHashSet<>(directTokens);
        if (nullable) {
            tokens.add(-1);
        }
        return tokens;
    }

    public Set<Integer> getFirstRules() {
        return directRules;
    }

    public Set<Derivation> getDerivations() {
        return Collections.unmodifiableSet(derivations);
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isNullable() {
        return nullable;
    }

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

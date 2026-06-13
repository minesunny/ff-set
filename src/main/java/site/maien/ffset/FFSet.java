package site.maien.ffset;

import org.antlr.v4.runtime.atn.*;
import java.util.*;

public class FFSet {
    private final ATN atn;
    private final Map<Integer, FirstSet> firstSets = new HashMap<>();
    private final Map<Integer, FollowSet> followSets = new HashMap<>();

    // Analysis results
    private final Map<Integer, Set<Integer>> ruleDirectTokens = new HashMap<>();
    private final Map<Integer, Set<Integer>> ruleDirectRules = new HashMap<>();
    private final Map<Integer, List<ATNState>> ruleFollowStates = new HashMap<>();
    
    private final Set<Integer> directNullableRules = new HashSet<>();
    private final Set<Integer> transitiveNullableRules = new HashSet<>();

    private boolean analyzed = false;

    public FFSet(ATN atn) {
        this.atn = atn;
    }

    private void analyze() {
        if (analyzed) return;
        
        for (int i = 0; i < atn.ruleToStartState.length; i++) {
            discoverDirect(i);
        }

        computeNullability();
        computeFirstSets();
        computeFollowSets();

        analyzed = true;
    }

    private void discoverDirect(int ruleIndex) {
        ATNState startState = atn.ruleToStartState[ruleIndex];
        ATNState stopState = atn.ruleToStopState[ruleIndex];
        Set<ATNState> visited = new HashSet<>();
        Queue<ATNState> queue = new LinkedList<>();
        queue.add(startState);

        Set<Integer> directTokens = ruleDirectTokens.computeIfAbsent(ruleIndex, k -> new LinkedHashSet<>());
        Set<Integer> directRules = ruleDirectRules.computeIfAbsent(ruleIndex, k -> new LinkedHashSet<>());

        while (!queue.isEmpty()) {
            ATNState state = queue.poll();
            if (state == stopState) {
                directNullableRules.add(ruleIndex);
                continue;
            }
            if (visited.contains(state)) continue;
            visited.add(state);

            for (Transition transition : state.getTransitions()) {
                if (transition instanceof RuleTransition ruleTransition) {
                    directRules.add(ruleTransition.ruleIndex);
                } else if (transition.isEpsilon()) {
                    queue.add(transition.target);
                } else {
                    transition.label().toList().forEach(directTokens::add);
                }
            }
        }
        
        // Comprehensive pass to find ALL rule calls and their follow states
        visited.clear();
        queue.add(startState);
        while (!queue.isEmpty()) {
            ATNState state = queue.poll();
            if (state == stopState) continue;
            if (visited.contains(state)) continue;
            visited.add(state);
            for (Transition transition : state.getTransitions()) {
                if (transition instanceof RuleTransition ruleTransition) {
                    ruleFollowStates.computeIfAbsent(ruleTransition.ruleIndex, k -> new ArrayList<>())
                            .add(ruleTransition.followState);
                    queue.add(ruleTransition.followState);
                } else {
                    queue.add(transition.target);
                }
            }
        }
    }

    private void computeNullability() {
        transitiveNullableRules.addAll(directNullableRules);
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = 0; i < atn.ruleToStartState.length; i++) {
                if (transitiveNullableRules.contains(i)) continue;
                if (checkNullable(i)) {
                    transitiveNullableRules.add(i);
                    changed = true;
                }
            }
        }
    }

    private boolean checkNullable(int ruleIndex) {
        ATNState startState = atn.ruleToStartState[ruleIndex];
        ATNState stopState = atn.ruleToStopState[ruleIndex];
        Set<ATNState> visited = new HashSet<>();
        Queue<ATNState> queue = new LinkedList<>();
        queue.add(startState);

        while (!queue.isEmpty()) {
            ATNState state = queue.poll();
            if (state == stopState) return true;
            if (visited.contains(state)) continue;
            visited.add(state);

            for (Transition transition : state.getTransitions()) {
                if (transition instanceof RuleTransition ruleTransition) {
                    if (transitiveNullableRules.contains(ruleTransition.ruleIndex)) {
                        queue.add(ruleTransition.followState);
                    }
                } else if (transition.isEpsilon()) {
                    queue.add(transition.target);
                }
            }
        }
        return false;
    }

    private void computeFirstSets() {
        for (int i = 0; i < atn.ruleToStartState.length; i++) {
            FirstSet fs = new FirstSet(i, atn.ruleToStartState[i]);
            fs.setNullable(directNullableRules.contains(i));
            for (Integer token : ruleDirectTokens.get(i)) {
                fs.addDirectToken(token);
                fs.addDerivation(new Derivation(Collections.emptyList(), token));
            }
            for (Integer rule : ruleDirectRules.get(i)) {
                fs.addDirectRule(rule);
            }
            firstSets.put(i, fs);
        }

        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = 0; i < atn.ruleToStartState.length; i++) {
                FirstSet fs = firstSets.get(i);
                int beforeSize = fs.getDerivations().size();
                
                for (Integer subRuleIndex : getStartRules(i)) {
                    FirstSet subFs = firstSets.get(subRuleIndex);
                    for (Derivation d : subFs.getDerivations()) {
                        if (d.getPath().contains(subRuleIndex)) continue; 
                        List<Integer> newPath = new ArrayList<>();
                        newPath.add(subRuleIndex);
                        newPath.addAll(d.getPath());
                        fs.addDerivation(new Derivation(newPath, d.getTargetToken()));
                    }
                    if (transitiveNullableRules.contains(subRuleIndex)) {
                        List<Integer> nullablePath = new ArrayList<>();
                        nullablePath.add(subRuleIndex);
                        fs.addDerivation(new Derivation(nullablePath, -1));
                    }
                }
                
                if (fs.getDerivations().size() > beforeSize) {
                    changed = true;
                }
            }
        }
    }

    private Set<Integer> getStartRules(int ruleIndex) {
        Set<Integer> startRules = new LinkedHashSet<>();
        ATNState startState = atn.ruleToStartState[ruleIndex];
        ATNState stopState = atn.ruleToStopState[ruleIndex];
        Set<ATNState> visited = new HashSet<>();
        Queue<ATNState> queue = new LinkedList<>();
        queue.add(startState);

        while (!queue.isEmpty()) {
            ATNState state = queue.poll();
            if (state == stopState) continue;
            if (visited.contains(state)) continue;
            visited.add(state);

            for (Transition transition : state.getTransitions()) {
                if (transition instanceof RuleTransition ruleTransition) {
                    startRules.add(ruleTransition.ruleIndex);
                    if (transitiveNullableRules.contains(ruleTransition.ruleIndex)) {
                        queue.add(ruleTransition.followState);
                    }
                } else if (transition.isEpsilon()) {
                    queue.add(transition.target);
                }
            }
        }
        return startRules;
    }

    private void computeFollowSets() {
        Set<Integer> rulesCalled = new HashSet<>();
        for (int i = 0; i < atn.ruleToStartState.length; i++) {
            ATNState start = atn.ruleToStartState[i];
            ATNState stop = atn.ruleToStopState[i];
            Set<ATNState> visited = new HashSet<>();
            Queue<ATNState> queue = new LinkedList<>();
            queue.add(start);
            while(!queue.isEmpty()){
                ATNState s = queue.poll();
                if(s == stop || visited.contains(s)) continue;
                visited.add(s);
                for(Transition t : s.getTransitions()){
                    if(t instanceof RuleTransition rt){
                        rulesCalled.add(rt.ruleIndex);
                    }
                    queue.add(t.target);
                }
            }
        }

        for (int i = 0; i < atn.ruleToStartState.length; i++) {
            FollowSet followSet = new FollowSet(i, atn.ruleToStopState[i]);
            if (!rulesCalled.contains(i)) {
                 followSet.addDirectToken(-1);
                 followSet.addDerivation(new Derivation(Collections.emptyList(), -1));
            }
            followSets.put(i, followSet);
        }

        boolean changed = true;
        int iterations = 0;
        while (changed && iterations < 100) { 
            iterations++;
            changed = false;
            for (int i = 0; i < atn.ruleToStartState.length; i++) {
                FollowSet followSet = followSets.get(i);
                int beforeTokens = followSet.getFollowTokens().size();
                int beforeRules = followSet.getFollowRules().size();
                int beforeParents = followSet.getParentRules().size();
                int beforeDerivations = followSet.getDerivations().size();

                for (ATNState followState : ruleFollowStates.getOrDefault(i, Collections.emptyList())) {
                    populateFollow(followSet, followState);
                }

                if (followSet.getFollowTokens().size() > beforeTokens ||
                    followSet.getFollowRules().size() > beforeRules ||
                    followSet.getParentRules().size() > beforeParents ||
                    followSet.getDerivations().size() > beforeDerivations) {
                    changed = true;
                }
            }
        }
    }

    private void populateFollow(FollowSet followSet, ATNState state) {
        ATNState stopState = atn.ruleToStopState[state.ruleIndex];
        
        // Parent detection: search for stopState avoiding RuleTransitions and non-EOF tokens
        Set<ATNState> visited = new HashSet<>();
        Queue<ATNState> queue = new LinkedList<>();
        queue.add(state);
        while (!queue.isEmpty()) {
            ATNState s = queue.poll();
            if (s == stopState) {
                followSet.addParentRule(state.ruleIndex);
                break; 
            }
            if (visited.contains(s)) continue;
            visited.add(s);
            for (Transition t : s.getTransitions()) {
                if (t instanceof RuleTransition) continue;
                if (t.isEpsilon() || (t.label() != null && t.label().contains(-1))) {
                    queue.add(t.target);
                }
            }
        }

        // Token/Rule discovery and propagation
        visited.clear();
        queue.clear();
        queue.add(state);
        while (!queue.isEmpty()) {
            ATNState s = queue.poll();
            if (s == stopState) {
                FollowSet parentFollow = followSets.get(state.ruleIndex);
                if (parentFollow.getFollowTokens().contains(-1)) {
                    if (!followSet.getFollowTokens().contains(-1)) {
                        followSet.addDirectToken(-1);
                        followSet.addDerivation(new Derivation(Collections.emptyList(), -1));
                    }
                }
                for (Derivation d : parentFollow.getDerivations()) {
                    if (d.getPath().contains(state.ruleIndex)) continue; 
                    List<Integer> newPath = new ArrayList<>();
                    newPath.add(state.ruleIndex);
                    newPath.addAll(d.getPath());
                    followSet.addDerivation(new Derivation(newPath, d.getTargetToken()));
                }
                continue;
            }
            if (visited.contains(s)) continue;
            visited.add(s);
            for (Transition transition : s.getTransitions()) {
                if (transition instanceof RuleTransition rt) {
                    followSet.addDirectRule(rt.ruleIndex);
                    FirstSet fs = firstSets.get(rt.ruleIndex);
                    for (Derivation d : fs.getDerivations()) {
                        if (d.getPath().contains(rt.ruleIndex)) continue; 
                        List<Integer> newPath = new ArrayList<>();
                        newPath.add(rt.ruleIndex);
                        newPath.addAll(d.getPath());
                        followSet.addDerivation(new Derivation(newPath, d.getTargetToken()));
                    }
                    if (transitiveNullableRules.contains(rt.ruleIndex)) {
                        queue.add(rt.followState);
                    }
                } else if (transition.isEpsilon()) {
                    queue.add(transition.target);
                } else {
                    List<Integer> tokens = transition.label().toList();
                    tokens.forEach(token -> {
                        followSet.addDirectToken(token);
                        followSet.addDerivation(new Derivation(Collections.emptyList(), token));
                    });
                    if (tokens.contains(-1)) { // Continue ONLY after EOF
                        queue.add(transition.target);
                    }
                }
            }
        }
    }

    public FirstSet firstSet(int ruleIndex) {
        analyze();
        return firstSets.get(ruleIndex);
    }

    public FollowSet followSet(int ruleIndex) {
        analyze();
        return followSets.get(ruleIndex);
    }
}

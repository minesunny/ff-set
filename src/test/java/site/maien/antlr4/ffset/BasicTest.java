package site.maien.antlr4.ffset;

import site.maien.antlr4.ffset.compile.GrammarContext;

import org.antlr.v4.tool.Grammar;
import org.junit.jupiter.api.Assertions;

import java.util.*;

public class BasicTest {
    protected Grammar grammar;

    protected void AssertFirstTokenSet(String parser, String ruleName, String... expected) {
        grammar =  new GrammarContext(parser).getGrammar();
        FFSet ffSet = new FFSet(grammar.atn);
        FirstSet firstSet = ffSet.firstSet(grammar.getRule(ruleName).index);
        List<String> actual = new LinkedList<>();
        firstSet.getFirstTokens().stream().map(this::tokenToString).forEach(actual::add);
        Arrays.sort(expected);
        Collections.sort(actual);
        Assertions.assertEquals(expected.length, actual.size(), () -> String.join(System.lineSeparator(), expected) +
                System.lineSeparator() + "#####" + System.lineSeparator()
                + String.join(System.lineSeparator(), actual) + System.lineSeparator());
        Assertions.assertEquals(expected.length, actual.size());
        for(int i = 0; i < expected.length; i++){
            Assertions.assertEquals(expected[i], actual.get(i));
        }
    }

    protected void AssertFollowTokenSet(String parser, String ruleName, String... expected) {
        grammar =  new GrammarContext(parser).getGrammar();
        FFSet ffSet = new FFSet(grammar.atn);
        FollowSet followSet = ffSet.followSet(grammar.getRule(ruleName).index);
        List<String> actual = new LinkedList<>();
        followSet.getFollowTokens().stream().map(this::tokenToString).forEach(actual::add);
        Arrays.sort(expected);
        Collections.sort(actual);
        Assertions.assertEquals(expected.length, actual.size(), () -> String.join(System.lineSeparator(), expected) +
                System.lineSeparator() + "#####" + System.lineSeparator()
                + String.join(System.lineSeparator(), actual) + System.lineSeparator());
        Assertions.assertEquals(expected.length, actual.size());

        for(int i = 0; i < expected.length; i++){
            Assertions.assertEquals(expected[i], actual.get(i));
        }
    }


    private String tokenToString(Integer tokenIndex) {
        return grammar.getTokenDisplayName(tokenIndex).replaceAll("^'|'$", "");
    }

    protected void AssertFirstRuleSet(String parser, String ruleName, String... expected) {
        grammar =  new GrammarContext(parser).getGrammar();
        FFSet ffSet = new FFSet(grammar.atn);
        FirstSet firstSet = ffSet.firstSet(grammar.getRule(ruleName).index);
        List<String> actual = firstSet.getFirstRules().stream().map(this::ruleToString).sorted().toList();
        Arrays.sort(expected);
        Assertions.assertEquals(expected.length, actual.size(), () -> String.join(System.lineSeparator(), expected) +
                System.lineSeparator() + "#####" + System.lineSeparator()
                + String.join(System.lineSeparator(), actual) + System.lineSeparator());
        for(int i = 0; i < expected.length; i++){
            Assertions.assertEquals(expected[i], actual.get(i));
        }
    }

    protected void AssertFollowRuleSet(String parser, String ruleName, String... expected) {
        grammar =  new GrammarContext(parser).getGrammar();
        FFSet ffSet = new FFSet(grammar.atn);
        FollowSet followSet = ffSet.followSet(grammar.getRule(ruleName).index);
        List<String> actual = followSet.getFollowRules().stream().map(this::ruleToString).sorted().toList();
        Arrays.sort(expected);
        Assertions.assertEquals(expected.length, actual.size(), () -> String.join(System.lineSeparator(), expected) +
                System.lineSeparator() + "#####" + System.lineSeparator()
                + String.join(System.lineSeparator(), actual) + System.lineSeparator());
        for(int i = 0; i < expected.length; i++){
            Assertions.assertEquals(expected[i], actual.get(i));
        }
    }

    protected void AssertParentRuleSet(String parser, String ruleName, String... expected) {
        grammar =  new GrammarContext(parser).getGrammar();
        FFSet ffSet = new FFSet(grammar.atn);
        FollowSet followSet = ffSet.followSet(grammar.getRule(ruleName).index);
        List<String> actual = followSet.getParentRules().stream().map(this::ruleToString).sorted().toList();
        Arrays.sort(expected);
        Assertions.assertEquals(expected.length, actual.size(), () -> String.join(System.lineSeparator(), expected) +
                System.lineSeparator() + "#####" + System.lineSeparator()
                + String.join(System.lineSeparator(), actual) + System.lineSeparator());
        for(int i = 0; i < expected.length; i++){
            Assertions.assertEquals(expected[i], actual.get(i));
        }
    }
    protected void AssertFirstSet(String parser, String ruleName, String... expected) {
        grammar =  new GrammarContext(parser).getGrammar();
        FFSet ffSet = new FFSet(grammar.atn);
        FirstSet firstSet = ffSet.firstSet(grammar.getRule(ruleName).index);
        List<String> actual = new LinkedList<>();
        firstSet.getFirstSet().forEach((value) -> {
            if(value.size() == 1) {
                actual.add(tokenToString(value.get(0)));
            } else if (!value.isEmpty()) {
                List<String> name = value.subList(0,value.size() - 1).stream().map(this::ruleToString).toList();
                actual.add(String.join("->", name) + "->" + tokenToString(value.get(value.size() - 1)));
            }


        });
        Arrays.sort(expected);
        Collections.sort(actual);
        Assertions.assertEquals(expected.length, actual.size(), () -> String.join(System.lineSeparator(), expected) +
                System.lineSeparator() + "#####" + System.lineSeparator()
                + String.join(System.lineSeparator(), actual) + System.lineSeparator());
        for(int i = 0; i < expected.length; i++){
            Assertions.assertEquals(expected[i], actual.get(i));
        }
    }


    protected void AssertFollowSet(String parser, String ruleName, String... expected) {
        grammar =  new GrammarContext(parser).getGrammar();
        FFSet ffSet = new FFSet(grammar.atn);
        FollowSet followSet = ffSet.followSet(grammar.getRule(ruleName).index);
        List<String> actual = new LinkedList<>();
        followSet.getFollowSet().forEach((value) -> {
            if(value.size() == 1) {
                actual.add(tokenToString(value.get(0)));
            } else if (!value.isEmpty()) {
                List<String> name = value.subList(0,value.size() - 1).stream().map(this::ruleToString).toList();
                actual.add(String.join("->", name) + "->" + tokenToString(value.get(value.size() - 1)));
            }


        });
        Arrays.sort(expected);
        Collections.sort(actual);
        Assertions.assertEquals(expected.length, actual.size(), () -> String.join(System.lineSeparator(), expected) +
                System.lineSeparator() + "#####" + System.lineSeparator()
                + String.join(System.lineSeparator(), actual) + System.lineSeparator());
        for(int i = 0; i < expected.length; i++){
            Assertions.assertEquals(expected[i], actual.get(i));
        }
    }

    private String ruleToString(Integer ruleIndex) {
        return grammar.getRule(ruleIndex).name;
    }
}

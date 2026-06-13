package site.maien.ffset;

import org.junit.jupiter.api.Test;

public class ComplexFollowSetTest extends BasicTest {
    /**
     * If A -> pB is a production, then everything in FOLLOW(A) is in FOLLOW(B)
     * If A -> pBq is a production, where p , B and q are any grammar symbols, then everything in FIRST(q) except ε is in FOLLOW(B)
     * If A -> pBq is a production and FIRST(q) contains ε , then FOLLOW(B) contains { FIRST(q) – ε } U FOLLOW(A)
     */

    @Test
    public void testCase1(){
        String parser = """
                grammar Basic;
                ruleA: 'P' ruleB;
                ruleC: ruleA 'P';
                ruleB: 'RULE_B';
                """;


        AssertFollowTokenSet(parser, "ruleB");
        AssertFollowRuleSet(parser, "ruleB");
        AssertParentRuleSet(parser, "ruleB", "ruleA");
        AssertFollowSet(parser, "ruleB","ruleA->P");
    }

    @Test
    public void testCase2(){
        String parser = """
                grammar Basic;
                ruleA: 'P' ruleB ruleQ;
                ruleC: ruleA 'P';
                ruleQ: hello;
                hello: 'HELLO';
                ruleB: 'RULE_B';
                """;

        AssertFollowTokenSet(parser, "ruleB");
        AssertFollowRuleSet(parser, "ruleB", "ruleQ");
        AssertParentRuleSet(parser, "ruleB");
        AssertFollowSet(parser, "ruleB","ruleQ->hello->HELLO");
    }


    @Test
    public void testCase3(){
        String parser = """
                grammar Basic;
                ruleA: 'P' ruleB ruleQ;
                ruleC: ruleA 'P';
                ruleQ: ;
                hello: 'HELLO';
                ruleB: 'RULE_B';
                """;

        AssertFollowTokenSet(parser, "ruleB");
        AssertFollowRuleSet(parser, "ruleB", "ruleQ");
        AssertParentRuleSet(parser, "ruleB");
        AssertFollowSet(parser, "ruleB","ruleA->P");
    }
}

package com.minesunny.ffset;

import org.junit.jupiter.api.Test;

/**
 * A->B|C|...|Z，其中B到Z都是非终结符。这时要尤其留意ε。如果其中某个非终结符前面所有的非终结符的FIRST集合都包含ε，那么这时将 该非终结符 的FIRST集合加入FIRST(A)（记得去除集合中的ε）；如果A的所有候选非终结符都能推出ε，那么把ε也加入FIRST(A)。
 */
public class ComplexFirstSetTest extends BasicTest {
    @Test
    public void basicRule(){
        String parser = """
                grammar Basic;
                helloRule: hello;
                hello: 'HELLO';
                world: 'WORLD';
                """;
        AssertFirstTokenSet(parser, "helloRule");
        AssertFirstRuleSet(parser, "helloRule","hello");
        AssertFirstSet(parser, "helloRule","hello->HELLO");
    }

    @Test
    public void basicQuestionRule(){
        String parser = """
                grammar Basic;
                helloRule: hello?;
                hello: 'HELLO';
                world: 'WORLD';
                """;
        AssertFirstTokenSet(parser, "helloRule","EOF");
        // ε 划分到token
        AssertFirstRuleSet(parser, "helloRule","hello");
        AssertFirstSet(parser, "helloRule","hello->HELLO","EOF");
    }

    @Test
    public void basicPlusRule(){
        String parser = """
                grammar Basic;
                helloRule: hello+;
                hello: 'HELLO';
                world: 'WORLD';
                """;
        AssertFirstTokenSet(parser, "helloRule");
        AssertFirstRuleSet(parser, "helloRule","hello");
        AssertFirstSet(parser, "helloRule","hello->HELLO");
    }

    @Test
    public void basicStarRule(){
        String parser = """
                grammar Basic;
                helloRule: hello*;
                hello: 'HELLO';
                world: 'WORLD';
                """;
        AssertFirstTokenSet(parser, "helloRule","EOF");
        // ε 划分到token
        AssertFirstRuleSet(parser, "helloRule","hello");
        AssertFirstSet(parser, "helloRule","hello->HELLO","EOF");
    }

    @Test
    public void helloWorldRule(){
        String parser = """
                grammar Basic;
                helloWorld: hello | world;
                hello: 'HELLO';
                world: 'WORLD';
                """;
        AssertFirstTokenSet(parser, "helloWorld");
        AssertFirstRuleSet(parser, "helloWorld","hello", "world");
        AssertFirstSet(parser, "helloWorld","hello->HELLO","world->WORLD");
    }
    @Test
    public void helloPlusWorldRule(){
        String parser = """
                grammar Basic;
                helloWorld: hello+ | world;
                hello: 'HELLO';
                world: 'WORLD';
                """;
        AssertFirstTokenSet(parser, "helloWorld");
        AssertFirstRuleSet(parser, "helloWorld","hello", "world");
        AssertFirstSet(parser, "helloWorld","hello->HELLO","world->WORLD");
    }
    @Test
    public void helloQuestionWorldRule(){
        String parser = """
                grammar Basic;
                helloWorld: hello? | world;
                hello: 'HELLO';
                world: 'WORLD';
                """;
        AssertFirstTokenSet(parser, "helloWorld","EOF");
        // ε 划分到token
        AssertFirstRuleSet(parser, "helloWorld","hello", "world");
        AssertFirstSet(parser, "helloWorld","hello->HELLO","world->WORLD","EOF");
    }

    @Test
    public void helloStarWorldRule(){
        String parser = """
                grammar Basic;
                helloWorld: hello* | world;
                hello: 'HELLO';
                world: 'WORLD';
                """;
        AssertFirstTokenSet(parser, "helloWorld","EOF");
        // ε 划分到token
        AssertFirstRuleSet(parser, "helloWorld","hello", "world");
        AssertFirstSet(parser, "helloWorld","hello->HELLO","world->WORLD","EOF");
    }

    @Test
    public void helloWithWorldRule(){
        String parser = """
                grammar Basic;
                helloWorld: hello world;
                hello: 'HELLO';
                world: 'WORLD';
                """;
        AssertFirstTokenSet(parser, "helloWorld");
        // ε 划分到token
        AssertFirstRuleSet(parser, "helloWorld","hello");
        AssertFirstSet(parser, "helloWorld","hello->HELLO");
    }
}

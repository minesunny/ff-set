package com.minesunny.ffset;

import org.junit.jupiter.api.Test;

public class SimpleFollowSetTest extends BasicTest{
    @Test
    void testEpsilon(){
        String parser = """
                grammar Basic;
                basic: 'BASIC';
                """;
        AssertFollowTokenSet(parser, "basic","EOF");
    }

    @Test
    void testBasic(){
        String parser = """
                grammar Basic;
                helloWorld: hello 'HELLO';
                hello: 'HELLO';
                world: 'WORLD';
                """;
        AssertFollowTokenSet(parser, "hello","HELLO");
    }
    @Test
    void testBasicPlus(){
        String parser = """
                grammar Basic;
                helloWorld: hello 'HELLO'+;
                hello: 'HELLO';
                world: 'WORLD';
                """;
        AssertFollowTokenSet(parser, "hello","HELLO");
    }

    @Test
    void testBasicQuestion(){
        String parser = """
                grammar Basic;
                helloWorld: hello 'HELLO'?;
                hello: 'HELLO';
                world: 'WORLD';
                """;
        AssertFollowTokenSet(parser, "hello","HELLO", "EOF");
        AssertParentRuleSet(parser, "hello","helloWorld");
        AssertFollowSet(parser, "hello","helloWorld->EOF", "HELLO", "EOF");
    }

    @Test
    void testBasicStar(){
        String parser = """
                grammar Basic;
                helloWorld: hello 'HELLO'*;
                hello: 'HELLO';
                world: 'WORLD';
                """;
        AssertFollowTokenSet(parser, "hello","HELLO", "EOF");
        AssertFollowSet(parser, "hello","helloWorld->EOF", "HELLO", "EOF");
    }

    @Test
    void testBasicOptional(){
        String parser = """
                grammar Basic;
                helloWorld: hello ('HELLO'|'WORLD');
                hello: 'HELLO';
                world: 'WORLD';
                """;
        AssertFollowTokenSet(parser, "hello","HELLO", "WORLD");
    }

    @Test
    void testBasicOptionalPlus(){
        String parser = """
                grammar Basic;
                helloWorld: hello ('HELLO'|'WORLD')+;
                hello: 'HELLO';
                world: 'WORLD';
                """;
        AssertParentRuleSet(parser, "hello");
        AssertFollowTokenSet(parser, "hello","HELLO", "WORLD");
    }
    @Test
    void testBasicOptionalQuestion(){
        String parser = """
                grammar Basic;
                helloWorld: hello ('HELLO'|'WORLD')?;
                hello: 'HELLO';
                world: 'WORLD';
                """;
        AssertFollowTokenSet(parser, "hello","HELLO", "WORLD", "EOF");
    }

    @Test
    void testBasicOptionalStar(){
        String parser = """
                grammar Basic;
                helloWorld: hello ('HELLO'|'WORLD')*;
                hello: 'HELLO';
                world: 'WORLD';
                """;
        AssertFollowTokenSet(parser, "hello","HELLO", "WORLD", "EOF");
    }

    @Test
    void testHello(){
        String parser = """
                grammar Basic;
                helloWorld: hello world;
                hello: 'HELLO';
                world: 'WORLD';
                """;
        AssertFollowTokenSet(parser, "hello");
        AssertFollowRuleSet(parser, "hello","world");
        AssertFollowSet(parser, "hello","world->WORLD");
    }
    @Test
    void testHelloPlusWorld(){
        String parser = """
                grammar Basic;
                helloWorld: hello world+;
                hello: 'HELLO';
                world: 'WORLD';
                """;
        AssertFollowTokenSet(parser, "hello");
        AssertFollowRuleSet(parser, "hello","world");
        AssertFollowSet(parser, "hello","world->WORLD");
    }
    @Test
    void testHelloQuestionWorld(){
        String parser = """
                grammar Basic;
                helloWorld: hello world?;
                hello: 'HELLO';
                world: 'WORLD';
                """;
        AssertFollowTokenSet(parser, "hello","EOF");
        AssertFollowRuleSet(parser, "hello","world");
        AssertFollowSet(parser, "hello","world->WORLD","EOF","helloWorld->EOF");
    }

    @Test
    void testHelloStarWorld(){
        String parser = """
                grammar Basic;
                helloWorld: hello world*;
                hello: 'HELLO';
                world: 'WORLD';
                """;
        AssertFollowTokenSet(parser, "hello","EOF");
        AssertFollowRuleSet(parser, "hello","world");
        AssertParentRuleSet(parser, "hello","helloWorld");
        AssertFollowSet(parser, "hello","world->WORLD", "EOF", "helloWorld->EOF");
    }

    @Test
    void testHelloEpsilon(){
        String parser = """
                grammar Basic;
                helloWorld: hello hello;
                hello: 'HELLO';
                world: 'WORLD';
                """;
        AssertFollowTokenSet(parser, "hello","EOF");
        AssertFollowRuleSet(parser, "hello","hello");
        AssertParentRuleSet(parser, "hello","helloWorld");
        AssertFollowSet(parser, "hello","hello->HELLO", "helloWorld->EOF","EOF");
    }

    @Test
    void testHelloWorldOptional(){
        String parser = """
                grammar Basic;
                helloWorld: hello (hello | world);
                hello: 'HELLO';
                world: 'WORLD';
                """;
        AssertFollowTokenSet(parser, "hello","EOF");
        AssertFollowRuleSet(parser, "hello","hello","world");
        AssertFollowSet(parser, "hello","hello->HELLO", "world->WORLD", "helloWorld->EOF","EOF");
    }
}

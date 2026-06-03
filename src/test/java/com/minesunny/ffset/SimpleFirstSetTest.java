package com.minesunny.ffset;


import org.junit.jupiter.api.Test;

/**
 * 最简单的情况，A->b，b是终结符。那么FIRST(A)={b}。即终结符的FIRST集合是它本身。
 */
public class SimpleFirstSetTest extends BasicTest {

    @Test
    void basicRule(){
        String parser = """
                grammar Basic;
                basic: 'BASIC';
                """;
        AssertFirstTokenSet(parser, "basic","BASIC");
    }

    @Test
    void basicQuestionRule(){
        String parser = """
                grammar Basic;
                basic: 'BASIC'?;
                """;
        AssertFirstTokenSet(parser, "basic","BASIC", "EOF");
    }

    @Test
    void basicPlusRule(){
        String parser = """
                grammar Basic;
                basic: 'BASIC'+;
                """;
        AssertFirstTokenSet(parser, "basic","BASIC");
    }
    @Test
    void basicStarRule(){
        String parser = """
                grammar Basic;
                basic: 'BASIC'*;
                """;
        AssertFirstTokenSet(parser, "basic","BASIC", "EOF");
    }


    @Test
    void basicSetTransitionRule(){
        String parser = """
                grammar Basic;
                basic: 'BASIC' | 'OPTION';
                """;
        AssertFirstTokenSet(parser, "basic","BASIC", "OPTION");
    }

    @Test
    void basicPlus2Rule(){
        String parser = """
                grammar Basic;
                basic: 'BASIC'+ | 'OPTION';
                """;
        AssertFirstTokenSet(parser, "basic","BASIC", "OPTION");
    }
    @Test
    void basicStar2Rule(){
        String parser = """
                grammar Basic;
                basic: 'BASIC'* | 'OPTION';
                """;
        AssertFirstTokenSet(parser, "basic","BASIC", "OPTION", "EOF");
    }
    @Test
    void basicOption2Rule(){
        String parser = """
                grammar Basic;
                basic: 'BASIC'? | 'OPTION';
                """;
        AssertFirstTokenSet(parser, "basic","BASIC", "OPTION", "EOF");
    }

    @Test
    void basicQuoteQuestionRule(){
        String parser = """
                grammar Basic;
                basic: ('BASIC' | 'OPTION')?;
                """;
        AssertFirstTokenSet(parser, "basic","BASIC", "OPTION", "EOF");
    }
    @Test
    void basicQuoteStarRule(){
        String parser = """
                grammar Basic;
                basic: ('BASIC' | 'OPTION')*;
                """;
        AssertFirstTokenSet(parser, "basic","BASIC", "OPTION", "EOF");
    }
    @Test
    void basicQuotePlusRule(){
        String parser = """
                grammar Basic;
                basic: ('BASIC' | 'OPTION')+;
                """;
        AssertFirstTokenSet(parser, "basic","BASIC", "OPTION");
    }
}

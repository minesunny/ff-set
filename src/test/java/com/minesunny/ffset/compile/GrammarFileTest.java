package com.minesunny.ffset.compile;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class GrammarFileTest {
    @Test
    public void testLexer() {
        GrammarContext grammarContext =  new GrammarContext( """
                lexer grammar Expr;
                fragment A          : ('A'|'a') ;
                fragment S          : ('S'|'s') ;
                fragment Y          : ('Y'|'y') ;
                fragment H          : ('H'|'h') ;
                fragment O          : ('O'|'o') ;
                fragment U          : ('U'|'u') ;
                fragment T          : ('T'|'t') ;
                
                fragment LOWERCASE  : [a-z] ;
                fragment UPPERCASE  : [A-Z] ;
                
                SAYS                : S A Y S ;
                
                SHOUTS              : S H O U T S;
                
                WORD                : (LOWERCASE | UPPERCASE | '_')+ ;
                
                WHITESPACE          : (' ' | '\\t') ;
                
                NEWLINE             : ('\\r'? '\\n' | '\\r')+ ;
                
                TEXT                : ~[\\])]+ ;
                ""","");
        Assertions.assertNotNull(grammarContext.getLexerGrammar());

    }
    @Test
    public void testParser() {
        GrammarContext grammarContext =  new GrammarContext("""
                grammar Expr;
                prog:   (expr NEWLINE)* ;
                expr:   expr ('*'|'/') expr
                    |   expr ('+'|'-') expr
                    |   INT
                    |   '(' expr ')'
                    ;
                NEWLINE : [\\r\\n]+ ;
                INT     : [0-9]+ ;
                """);
        Assertions.assertNotNull(grammarContext.getGrammar());
        Assertions.assertNotNull(grammarContext.getGrammar().getRule("prog"));
    }

}

package site.maien.ffset;

import org.junit.jupiter.api.Test;

public class JSONFollowSetTest extends BasicTest{
    String grammar = """
            grammar JSON;
            
            json
                : value EOF
                ;
            
            obj
                : '{' pair (',' pair)* '}'
                | '{' '}'
                ;
            
            pair
                : STRING ':' value
                ;
            
            arr
                : '[' value (',' value)* ']'
                | '[' ']'
                ;
            
            value
                : STRING
                | NUMBER
                | obj
                | arr
                | 'true'
                | 'false'
                | 'null'
                ;
            
            STRING
                : '"' (ESC | SAFECODEPOINT)* '"'
                ;
            
            fragment ESC
                : '\\\\' (["\\\\/bfnrt] | UNICODE)
                ;
            
            fragment UNICODE
                : 'u' HEX HEX HEX HEX
                ;
            
            fragment HEX
                : [0-9a-fA-F]
                ;
            
            fragment SAFECODEPOINT
                : ~ ["\\\\\\u0000-\\u001F]
                ;
            
            NUMBER
                : '-'? INT ('.' [0-9]+)? EXP?
                ;
            
            fragment INT
                // integer part forbids leading 0s (e.g. `01`)
                : '0'
                | [1-9] [0-9]*
                ;
            
            // no leading zeros
            
            fragment EXP
                // exponent number permits leading 0s (e.g. `1e01`)
                : [Ee] [+-]? [0-9]+
                ;
            
            WS
                : [ \\t\\n\\r]+ -> skip
                ;
            """;

    @Test
    public void test_obj_FirstSet(){
        AssertFollowTokenSet(grammar, "obj","EOF");
        AssertFollowRuleSet(grammar, "obj");
        AssertParentRuleSet(grammar, "obj","value");
        // the result path can have parent path, like value->pair->
        AssertFollowSet(grammar, "obj","EOF", "value->EOF","value->,","value->]","value->pair->,","value->pair->}","value->json->EOF");
    }
    @Test
    public void test_value_FollowSet(){
        AssertFollowTokenSet(grammar, "value","," ,"]", "EOF");
        AssertFollowRuleSet(grammar, "value");
        AssertParentRuleSet(grammar, "value","json", "pair");
        AssertFollowSet(grammar, "value",
                "EOF",",","]","json->EOF","pair->,","pair->}");
    }
    @Test
    public void test_json_FollowSet(){
        AssertFollowTokenSet(grammar, "json","EOF");
        AssertFollowRuleSet(grammar, "json");
        AssertParentRuleSet(grammar, "json");
        // 对于antlr4生成的类，token就是一个终结符， 但是有些token并不是终结符，比如 STRING;
        AssertFollowSet(grammar, "json",
                "EOF");
    }

    @Test
    public void test_pair_FollowSet(){
        AssertFollowTokenSet(grammar, "pair", ",", "}");
        AssertFollowRuleSet(grammar, "pair");
        // 对于antlr4生成的类，token就是一个终结符， 但是有些token并不是终结符，比如 STRING;
        AssertFollowSet(grammar, "pair",
                ",", "}");
    }
}

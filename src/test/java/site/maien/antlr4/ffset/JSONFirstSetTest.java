package site.maien.antlr4.ffset;

import org.junit.jupiter.api.Test;

public class JSONFirstSetTest extends BasicTest{
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
        AssertFirstTokenSet(grammar, "obj","{");
        AssertFirstRuleSet(grammar, "obj");
        AssertFirstSet(grammar, "obj","{");
    }
    @Test
    public void test_value_FirstSet(){
        AssertFirstTokenSet(grammar, "value","STRING","NUMBER","null","false","true");
        AssertFirstRuleSet(grammar, "value","obj", "arr");

        AssertFirstSet(grammar, "value",
                "STRING", "NUMBER",
                "obj->{", "arr->[",
                "true", "false", "null");
    }
    @Test
    public void test_json_FirstSet(){
        AssertFirstTokenSet(grammar, "json");
        AssertFirstRuleSet(grammar, "json","value");
        // 对于antlr4生成的类，token就是一个终结符， 但是有些token并不是终结符，比如 STRING;
        AssertFirstSet(grammar, "json",
                "value->STRING", "value->NUMBER",
                "value->obj->{", "value->arr->[",
                "value->true", "value->false","value->null");
    }

    @Test
    public void test_pair_FirstSet(){
        AssertFirstTokenSet(grammar, "json");
        AssertFirstRuleSet(grammar, "json","value");
        // 对于antlr4生成的类，token就是一个终结符， 但是有些token并不是终结符，比如 STRING;
        AssertFirstSet(grammar, "json",
                "value->STRING", "value->NUMBER",
                "value->obj->{", "value->arr->[",
                "value->true", "value->false","value->null");
    }
}

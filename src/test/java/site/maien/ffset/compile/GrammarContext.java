package site.maien.ffset.compile;

import org.antlr.runtime.RecognitionException;

import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LexerGrammar;


public class GrammarContext {
    private Grammar grammar;
    private LexerGrammar lexerGrammar;

    public GrammarContext(String parserInput){
        this("", parserInput);
    }
    public GrammarContext(String lexerInput, String parserInput) {

        try {
            if(lexerInput != null && !lexerInput.isEmpty()){
                lexerGrammar = new LexerGrammar( lexerInput);
                if(parserInput != null && !parserInput.isEmpty()){
                    grammar = new Grammar(parserInput, lexerGrammar);
                }
            }else{
                grammar = new Grammar(parserInput);
            }
        } catch (RecognitionException ignore) {
        }
    }
    public LexerGrammar getLexerGrammar() {
        return lexerGrammar;
    }
    public Grammar getGrammar() {
        return grammar;
    }
//    @SuppressWarnings("unchecked")
//    private <T> Class<T> compile(String name, String code) {
//                JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
//        InMemoryFileManager manager = new InMemoryFileManager(compiler.getStandardFileManager(null, null, null));
//
//        List<JavaFileObject> sourceFiles = Collections.singletonList(new JavaSourceFromString(name, code));
//
//        JavaCompiler.CompilationTask task = compiler.getTask(null, manager, diagnostics, null, null, sourceFiles);
//        task.call();
//        diagnostics.getDiagnostics().forEach(System.err::println);
//        ClassLoader classLoader = manager.getClassLoader(null);
//        try {
//            return (Class<T>) classLoader.loadClass(name);
//        } catch (ClassNotFoundException e) {
//           throw new RuntimeException(e);
//        }
//    }


}

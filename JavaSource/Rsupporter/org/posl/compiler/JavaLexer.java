package org.posl.compiler;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.data.tokens.*;
import org.posl.data.tree.ParsingException;
import org.posl.util.functions.CEPredicate;


/**
 * This is a lexer of JavaSE 17.
 * @author me
 */

public class JavaLexer{
    private static final Set<Character> EMPTY = new HashSet<>();
    private static final Set<Character> LINE_TERMINATE_CHARACTERS = new HashSet<>(Arrays.asList('\n', '\r'));
    private static final Set<Character> WHITE_SPACE_CHARACTERS = new HashSet<>(Arrays.asList(' ', '\t', '\f'));
    private static final Set<Character> WHITE_SPACE_OR_LINE_TERMINATE_CHARACTERS = new HashSet<>(Arrays.asList(' ', '\t', '\f', '\n', '\r'));
    private static final Set<Character> HEX_DIGITS = new HashSet<>(
        Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B', 'C', 'D', 'E', 'F'));
    private static final Set<Character> NON_ZERO_DIGITS = new HashSet<>(
        Arrays.asList('1', '2', '3', '4', '5', '6', '7', '8', '9'));
    private static final Set<Character> DIGITS = new HashSet<>(
        Arrays.asList('0' ,'1', '2', '3', '4', '5', '6', '7', '8', '9'));
    private static final Set<Character> OCTAL_DIGITS = new HashSet<>(
        Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7'));
    private static final Set<Character> ZERO_TO_THREE = new HashSet<>(Arrays.asList('0', '1', '2', '3'));
    private static final Set<Character> BINARY_DIGITS = new HashSet<>(Arrays.asList('0', '1'));

    private static final Set<Character> EXPONENT_INDICATOR = new HashSet<>(Arrays.asList('e', 'E'));
    private static final Set<Character> BINARY_EXPONENT_INDICATOR = new HashSet<>(Arrays.asList('p', 'P'));
    private static final Set<Character> SIGNS = new HashSet<>(Arrays.asList('+', '-'));
    private static final Set<Character> INTEGER_TYPE_SUFFIXES = new HashSet<>(Arrays.asList('l', 'L'));
    private static final Set<Character> FLOAT_SUFFIXES = new HashSet<>(Arrays.asList('f', 'F'));
    private static final Set<Character> DOUBLE_SUFFIXES = new HashSet<>(Arrays.asList('d', 'D'));

    private static final Set<Character> ESCAPE_SEQUENCE_MARKERS= new HashSet<>(
        Arrays.asList('b', 's', 't', 'n', 'f', 'r', '\"', '\'', '\\'));
    private static final Set<Character> SEPARATOR_INTRODUCTION_CHARACTERS = new HashSet<>(
        Arrays.asList('(', ')', '{', '}', '[', ']', ';', ',', '@'));
    private static final Set<Character> OPERATOR_INTRODUCTION_CHARACTERS = new HashSet<>(
        Arrays.asList('=', '>', '<', '!', '~', '?', '+', '-', '*', '&', '|', '^', '%'));
    
    private static final Set<String> SEPARATORS = new HashSet<>(
        Arrays.asList("(", ")", "{", "}", "[", "]", ";", ":", ",", ".", "...", "@", "::"));
    private static final Set<String> OPERATORS = new HashSet<>(
        Arrays.asList("=", ">", "<", "!", "~", "?", ":", "->",
                      "==", ">=", "<=", "!=", "&&", "||", "++", "--",
                      "+", "-", "*", "/", "&", "|", "^", "%", "<<", ">>", ">>>",
                      "+=", "-=", "*=", "/=", "&=", "|=", "^=", "%=", "<<=", ">>=", ">>>="));
    private static final Set<String> BOOLEAN_LITERALS = new HashSet<>(Arrays.asList("false", "true"));
    private static final Set<String> NULL_LITERAL = new HashSet<>(Arrays.asList("null"));
    private static final Set<String> KEYWORDS = new HashSet<>(
        Arrays.asList("abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
                      "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
                      "for", "if", "goto", "implements", "import", "instanceof", "int", "interface", "long", "native",
                      "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super",
                      "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while",
                      "_"));
    private static final Set<String> CONTEXTUAL_KEYWORDS = new HashSet<>(
        Arrays.asList("open", "module", "requires", "transitive", "exports", "opens", "to", "uses",
                        "provides", "with", "var", "permits", "sealed", "non-sealed", "record", "yield"));


    private static Literal getNumericLiteral(String prefix, CharList source, ReferenceProducer recorder) throws ParsingException, IndexOutOfBoundsException{
        Base base = Base.set(prefix);
        String integerPart = prefix + getDigits(source, base.digitChars);
        if(source.refer() == '.'){
            return readAfterPoint(source, integerPart + source.get(), base, recorder);
        }else{
            return readAfterPoint(source, integerPart, base, recorder);
        }
    }

    private static Literal readAfterPoint(CharList source, String s, Base base, ReferenceProducer recorder) throws ParsingException, IndexOutOfBoundsException{
        boolean isInteger = !lastCharacterOf(s).equals(".");
        s += getDigits(source, base.digitChars);

        //exponent part resolving
        if(base.exponentIndicator.contains(source.refer())){
            isInteger = false;
            s += source.get();
            if(SIGNS.contains(source.refer())){
                s += source.get();
            }
            s += getDigits(source, DIGITS);
        }

        try{

            if(FLOAT_SUFFIXES.contains(source.refer()) || DOUBLE_SUFFIXES.contains(source.refer())){
                s += source.get();
                return new FloatingPointLiteral(s, recorder.get(), base.radix(s, false));
            }

            if(isInteger){
                if(INTEGER_TYPE_SUFFIXES.contains(source.refer())){
                    s += source.get();   
                }
                return new IntegerLiteral(s, recorder.get(), base.radix(s, true));
            }
            return new FloatingPointLiteral(s, recorder.get(), base.radix(s, false));

        }catch(NumberFormatException e){
            System.out.println(e);
            throw new ParsingException("Invalid number literal.");
        }
    }

    public static String getUntil(CharList source, CEPredicate<CharList> tester) throws ParsingException, IndexOutOfBoundsException{
        String s = "";
        while(tester.test(source)){
            s += source.get();
        }
        return s;
    }

    private static String getDigits(CharList source, Set<Character> digits) throws ParsingException, IndexOutOfBoundsException{
        String s = source.readForward(c -> digits.contains(c) || c == '_');
        if(lastCharacterOf(s).equals("_")){
            throw new ParsingException("Underscores have to be located within digits.");
        }
        return s;
    }

    private static String lastCharacterOf(String s){
        if(s.length() == 0){
            return "";
        }else{
            return s.substring(s.length() - 1);
        }
    }

    private static String getLineTerminator(CharList source){
        if(source.refer() == '\r'){
            String s = String.valueOf(source.get());
            if(source.refer() == '\n'){
                return s + source.get();
            }
            return s;
        }else if(source.refer() == '\n'){
            return String.valueOf(source.get());
        }
        return "";
    }

    private static String getCharacter(CharList source, char... illegals)throws ParsingException, IndexOutOfBoundsException{
        char c = source.get();
        if(c == '\\'){
            return c + getEscapeSequence(source);
        }
        for(char illegal : illegals){
            if(c == illegal){
                throw new ParsingException("Illegal literal.");
            }
        }
        return String.valueOf(c);
    }

    private static String getEscapeSequence(CharList source) throws ParsingException, IndexOutOfBoundsException{
        char c = source.get();
        if(ESCAPE_SEQUENCE_MARKERS.contains(c)){
            return String.valueOf(c);
        }else if(OCTAL_DIGITS.contains(c)){
            String s = String.valueOf(c);
            if(OCTAL_DIGITS.contains(source.refer())){
                s += source.get();
                if(ZERO_TO_THREE.contains(c) && OCTAL_DIGITS.contains(source.refer())){
                    s += source.get();
                }
            }
            return s;
        }
        throw new ParsingException("Invalid character literal.");
    }

    /**
     * Gets CharList from the source file. 
     * All Unicode escapes are translated to corresponding Unicode characters.
     * @param file source file
     * @return CharList expression
     */

    private CharList translateUnicode(File file) throws ParsingException, IndexOutOfBoundsException{
        enum Status{BACK_SLASH, DEFAULT};
        var charList = new CharList();
        Status status = Status.DEFAULT;

        try(var reader = new FileReader(file)){
            int ch;
            while((ch = reader.read()) != -1){
                if(ch == '\\'){
                    status = Status.BACK_SLASH;
                }else{
                    if(ch == 'u' && status == Status.BACK_SLASH
                        && charList.readBack(c -> c == '\\').length() % 2 == 1){
                        charList.remove(1);
                        ch = getUnicode(reader);
                    }
                    status = Status.DEFAULT;
                }
                charList.add((char)ch);
            }
        }catch(IOException e){
            e.printStackTrace();
            throw new ParsingException("The lexer reported an error in the resolution of "+file.toString()+".");
        }
        charList.first();
        return charList;
    }

    /**
     * Gets a Unicode character from FileReader.
     * The appearence of odd number of '\'(backslash) followed by character 'u'
     * confirms the appearence of Unicode escape.
     */

    private int getUnicode(FileReader reader) throws IOException, ParsingException{
        int ch;
        String hex = "0x";

        try{
            do{
                ch = reader.read();
            }while(ch == 'u');
            hex += (char)ch;
            for(int i = 1; i < 4; i++){
                ch = reader.read();
                hex += (char)ch;
            }
        }catch(IndexOutOfBoundsException e){
            System.out.println(e);
            throw new ParsingException("Found invalid Unicode.");
        }
        
        try{
            return Integer.decode(hex);
        }catch(NumberFormatException e){
            System.out.println(e);
            throw new ParsingException("Found invalid Unicode.");
        }
    }

    /**
     * Gets TokenList from CharList.
     */

    private TokenList tokenize(CharList source) throws ParsingException{
        
        var tokens = new TokenList();
        var recorder = new ReferenceProducer();
        try{
            while(source.hasNext()){
                var element = Terminal.set(source.refer()).getInputElement(source, recorder);
                if(element instanceof Identifier i && i.resolution == JavaTS.SEALED){
                    if(tokens.applyTests(
                        e -> e instanceof Operator o && o.resolution == JavaTS.MINUS,
                        e -> e instanceof Identifier id && id.text.equals("non"))){
                        tokens.remove(2);
                        recorder.rollBack(3);
                        tokens.add(new Identifier("non-sealed", recorder.get()));
                        continue;
                    }
                }
                tokens.add(element);
            }
        }catch(IndexOutOfBoundsException e){
            System.out.println(e);
            throw new ParsingException("Source ended with an invalid token.");
        }catch(ParsingException e){
            System.out.println(e);
            throw new ParsingException("Caught exception at "+recorder.get().toString()+".");
        }
        tokens.add(Token.EOF);
        tokens.first();
        return tokens;
    }

    public TokenList run(File file) throws ParsingException{
        return tokenize(translateUnicode(file));
    }

    class ReferenceProducer{
        private int line = 1;
        private int pos = 0;

        public Reference get(){
            return new Reference(line, ++pos);
        }

        public void newLine(int terminated){
            if(terminated > 0){
                line += terminated;
                pos = 0;
            }
        }

        public void rollBack(int i) throws UnsupportedOperationException{
            if(i > pos){
                throw new UnsupportedOperationException();
            }
            pos -= i;
        }
    }

    /**
     * This enum supports reading numeric literals.
     * The first argument of the constants specifies what characters are allowed while reading literals.
     */

    enum Base{
        BINARY(BINARY_DIGITS, EMPTY, 2, "0b", "0B"){

            @Override
            public int radix(String s, boolean isInteger) throws NumberFormatException{
                if(!isInteger){
                    throw new NumberFormatException("Invalid number literal.");
                }
                return 2;
            }

        },

        DECIMAL_OR_OCTAL(DIGITS, EXPONENT_INDICATOR, 10, "", "0"){

            @Override
            public int radix(String s, boolean isInteger) throws NumberFormatException{
                if(isInteger){
                    if(lastCharacterOf(s).equals("l") || lastCharacterOf(s).equals("L")){
                        s = s.substring(0, s.length()-1);
                    }
                    if(s.length() > 1 && s.startsWith("0")){
                        return 8;
                    }    
                }
                return 10;
            }

        },
        HEXADECIMAL(HEX_DIGITS, BINARY_EXPONENT_INDICATOR, 16, "0x", "0X");

        Set<Character> digitChars;
        Set<Character> exponentIndicator;
        int radix;
        String[] prefixes;

        private Base(Set<Character> digitChars, Set<Character> exponentIndicator, int radix, String... prefixes){
            this.digitChars = digitChars;
            this.exponentIndicator = exponentIndicator;
            this.radix = radix;
            this.prefixes = prefixes;
        }

        public static Base set(String prefix) throws ParsingException, IndexOutOfBoundsException{
            for(Base base : Base.values()){
                for(String pf : base.prefixes){
                    if(pf.equals(prefix)) return base;
                }
            }
            throw new ParsingException("Illegal prefix of numeric literal.");
        }

        public int radix(String s, boolean isInteger) throws NumberFormatException{
            return radix;
        }
    }

    enum Terminal{

        WHITE_SPACE_INTRO(c -> WHITE_SPACE_OR_LINE_TERMINATE_CHARACTERS.contains(c)){

            @Override
            public WhiteSpace getInputElement(CharList source, ReferenceProducer recorder) throws ParsingException, IndexOutOfBoundsException{
                var ws = new WhiteSpace(source.readForward(WHITE_SPACE_OR_LINE_TERMINATE_CHARACTERS), recorder.get());
                recorder.newLine(ws.terminatedLines());
                return ws;
            }

        },

        SLASH(c -> c == '/'){

            @Override
            public InputElement getInputElement(CharList source, ReferenceProducer recorder) throws ParsingException, IndexOutOfBoundsException{
                String s = String.valueOf(source.get());
                switch(source.refer()){
                    case '*':
                    s += getUntil(source, seq -> !seq.readBack(2).equals("*/"));
                    return new Comment(s + source.get(), recorder.get());

                    case '/':
                    s += source.readForward(c -> !LINE_TERMINATE_CHARACTERS.contains(c));
                    return new Comment(s, recorder.get());

                    case '=':
                    s += source.get();
                    return new Operator(s, recorder.get());

                    default:
                    return new Operator(s, recorder.get());
                }
            }

        },

        WORD_INTRO(c -> Character.isJavaIdentifierStart(c)){

            @Override
            public Token getInputElement(CharList source, ReferenceProducer recorder) throws ParsingException, IndexOutOfBoundsException{
                String s = "";
                s += source.readForward(Character::isJavaIdentifierPart);
                if(KEYWORDS.contains(s)){
                    return new Keyword(s, recorder.get());
                }
                switch(s){
                    case "true":
                    case "false":
                    return new BooleanLiteral(s.equals("true"), recorder.get());

                    case "null":
                    return new NullLiteral(recorder.get());

                    case "instanceof":
                    return new Operator(s, recorder.get());

                    default:
                    return new Identifier(s, recorder.get());
                }
            }

        },

        DECIMAL_NUMBER_INTRO(c -> NON_ZERO_DIGITS.contains(c)){

            @Override
            public Literal getInputElement(CharList source, ReferenceProducer recorder) throws ParsingException, IndexOutOfBoundsException{
                return getNumericLiteral("", source, recorder);
            }

        },

        ZERO(c -> c == '0'){

            @Override
            public Literal getInputElement(CharList source, ReferenceProducer recorder) throws ParsingException, IndexOutOfBoundsException{
                String s = String.valueOf(source.get());
                switch(source.refer()){
                    case 'x':
                    case 'X':
                    case 'b':
                    case 'B':
                    return getNumericLiteral(s + source.get(), source, recorder);

                    default:
                    return getNumericLiteral(s, source, recorder);
                }
            }

        },

        PERIOD(c -> c == '.'){

            @Override
            public Token getInputElement(CharList source, ReferenceProducer recorder) throws ParsingException, IndexOutOfBoundsException{
                String s = String.valueOf(source.get());
                if(DIGITS.contains(source.refer())){
                    return readAfterPoint(source, ".", Base.DECIMAL_OR_OCTAL, recorder);
                }else if(source.refer() == '.'){
                    s += source.get();
                    if(source.refer() == '.'){
                        s += source.get();
                        return new Separator(s, recorder.get());
                    }else{
                        throw new ParsingException("Invalid token \".\".");
                    }
                }else{
                    return new Operator(s, recorder.get());
                }
            }

        },

        COLON(c -> c == ':'){

            @Override
            public Token getInputElement(CharList source, ReferenceProducer recorder) throws ParsingException, IndexOutOfBoundsException{
                String s = String.valueOf(source.get());
                if(source.refer() == ':'){
                    return new Operator(s + source.get(), recorder.get());
                }else{
                    return new Separator(s, recorder.get());
                }
            }

        },

        CHARACTER_LITERAL_INTRO(c -> c == '\''){

            @Override
            public CharacterLiteral getInputElement(CharList source, ReferenceProducer recorder) throws ParsingException, IndexOutOfBoundsException{
                source.get();
                String s = getCharacter(source, '\r', '\n', '\'');
                if(source.get() != '\''){
                    throw new ParsingException("Invalid character literal.");
                }
                return new CharacterLiteral(s, recorder.get());
            }

        },

        STRING_LITERAL_INTRO(c -> c == '\"'){

            @Override
            public Literal getInputElement(CharList source, ReferenceProducer recorder) throws ParsingException, IndexOutOfBoundsException{
                String s = "";
                source.get();
                if(source.refer() == '\"'){
                    s += source.get();
                    if(source.refer() == '\"'){
                        source.get();
                        s = "";
                        String space = "";
                        String buf = "";

                        space += source.readForward(WHITE_SPACE_CHARACTERS);
                        if(LINE_TERMINATE_CHARACTERS.contains(source.refer())){
                            space += getLineTerminator(source);
                        }else{
                            throw new ParsingException("Invalid text block.");
                        }

                        while(true){
                            buf += getCharacter(source);
                            switch(buf){
                                case "\"":
                                case "\"\"":
                                continue;

                                case "\"\"\"":
                                return new TextBlock(space, s, recorder.get());

                                default:
                                s += buf;
                                buf = "";
                            }
                        }
                    }else{
                        return new StringLiteral("", recorder.get());
                    }
                }else{
                    while(source.refer() != '\"'){
                        s += getCharacter(source, '\r', '\n', '\"');
                    }
                    source.get();
                    return new StringLiteral(s, recorder.get());
                }
            }

        },
        
        SEPARATOR_INTRO(c -> SEPARATOR_INTRODUCTION_CHARACTERS.contains(c)){

            @Override
            public Separator getInputElement(CharList source, ReferenceProducer recorder){
                String s = "";
                while(!SEPARATORS.contains(s) || SEPARATORS.contains(s + source.refer())){
                    s += source.get();
                }
                return new Separator(s, recorder.get());
            }

        },

        OPERATOR_INTRO(c -> OPERATOR_INTRODUCTION_CHARACTERS.contains(c)){

            @Override
            public Operator getInputElement(CharList source, ReferenceProducer recorder){
                String s = "";
                while(!OPERATORS.contains(s) || OPERATORS.contains(s + source.refer())){
                    s += source.get();
                }
                return new Operator(s, recorder.get());
            }

        };
        
        Predicate<Character> tester;

        private Terminal(Predicate<Character> tester){
            this.tester = tester;
        }

        public static Terminal set(char c) throws ParsingException, IndexOutOfBoundsException{
            for(Terminal terminal : Terminal.values()){
                if(terminal.tester.test(c)){
                    return terminal;
                }
            }
            throw new ParsingException("Detected invalid character \'" + c + "\'.");
        }

        protected abstract InputElement getInputElement(CharList source, ReferenceProducer recorder) throws ParsingException, IndexOutOfBoundsException;
        
    }

}
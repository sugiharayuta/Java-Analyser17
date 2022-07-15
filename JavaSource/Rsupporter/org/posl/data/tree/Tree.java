package org.posl.data.tree;

import java.lang.ProcessBuilder.Redirect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.data.resolution.ExpressionIdentifier;
import org.posl.data.resolution.TypeIdentifier;
import org.posl.util.functions.CEFunction;

/**
 * Common interface for all nodes in an abstract syntax tree.
 * 
 * @author me
 */

public interface Tree{

    static final HashSet<JavaTS> TYPE_IDENTIFIERS = new HashSet<>(Arrays.asList(
        JavaTS.IDENTIFIER, JavaTS.EXPORTS, JavaTS.MODULE, JavaTS.OPEN, JavaTS.OPENS, JavaTS.PROVIDES, JavaTS.REQUIRES, JavaTS.TO, JavaTS.TRANSITIVE,
        JavaTS.USES, JavaTS.WITH, JavaTS.WHEN, JavaTS.PRIMITIVE));

    static final HashSet<JavaTS> IDENTIFIERS = new HashSet<>(Arrays.asList(
        JavaTS.IDENTIFIER, JavaTS.EXPORTS, JavaTS.MODULE, JavaTS.OPEN, JavaTS.OPENS, JavaTS.PERMITS, JavaTS.PROVIDES, JavaTS.RECORD, JavaTS.REQUIRES,
        JavaTS.SEALED, JavaTS.TO, JavaTS.TRANSITIVE, JavaTS.USES, JavaTS.VAR, JavaTS.WITH, JavaTS.YIELD, JavaTS.WHEN, JavaTS.PRIMITIVE));

    static final HashSet<JavaTS> MODIFIERS = new HashSet<>(Arrays.asList(
        JavaTS.PUBLIC, JavaTS.PROTECTED, JavaTS.PRIVATE, JavaTS.FINAL, JavaTS.STATIC, JavaTS.ABSTRACT, JavaTS.DEFAULT,
        JavaTS.STRICTFP, JavaTS.SYNCHRONIZED, JavaTS.VOLATILE, JavaTS.TRANSIENT, JavaTS.NATIVE, JavaTS.SEALED, JavaTS.NON_SEALED));

    static final HashSet<JavaTS> LITERAL_TOKENS = new HashSet<>(Arrays.asList(
        JavaTS.INTEGER_LITERAL, JavaTS.FLOATING_POINT_LITERAL, JavaTS.TRUE, JavaTS.FALSE, 
        JavaTS.CHARACTER_LITERAL, JavaTS.STRING_LITERAL, JavaTS.TEXT_BLOCK, JavaTS.NULL));

    static final HashSet<JavaTS> PRIMITIVE_TYPES = new HashSet<>(Arrays.asList(
        JavaTS.BYTE, JavaTS.SHORT, JavaTS.INT, JavaTS.LONG, JavaTS.CHAR, 
        JavaTS.FLOAT, JavaTS.DOUBLE, JavaTS.BOOLEAN));

    public static ArrayList<AnnotationTree> resolveAnnotations(JavaTokenManager src) throws ParsingException{
        var annotationList = new ArrayList<AnnotationTree>();
        while(src.match(JavaTS.AT_SIGN) && !src.match(JavaTS.AT_SIGN, JavaTS.INTERFACE)){
            annotationList.add(AnnotationTree.parse(src));
        }
        return annotationList;
    }

    enum DeclarationType{
        CLASS(JavaTS.CLASS, true, true, true, true, true),
        INTERFACE(JavaTS.INTERFACE, true, true, true, false, true),
        ENUM(JavaTS.ENUM, true, false, false, true, false),
        RECORD(JavaTS.RECORD, true,true, false, true, false),
        ANNOTATION_INTERFACE(JavaTS.INTERFACE, true, false, false, false, false){
            @Override
            JavaTS[] toTokens(){
                return new JavaTS[]{JavaTS.AT_SIGN, JavaTS.INTERFACE};
            }

            @Override
            public String toString(){
                return "annotation interface";
            }
        },
        VARIABLE_DECLARATION(null, false, false, false, false, false),
        METHOD_DECLARATION(null, false, false, false, false, false),
        NOT_DECLARATION(null, false, false, false, false, false);
        

        final JavaTS token;
        final boolean hasTypeParameterClause;
        final boolean hasExtendsClause;
        final boolean hasImplementsClause;
        final boolean hasPermitsClause;
        final boolean isTypeDeclaration;

        private DeclarationType(JavaTS token, boolean... status){
            this.token = token;
            this.isTypeDeclaration = status[0];
            this.hasTypeParameterClause = status[1];
            this.hasExtendsClause = status[2];
            this.hasImplementsClause = status[3];
            this.hasPermitsClause = status[4];
        }

        static DeclarationType lookAheadDeclType(JavaTokenManager src) throws ParsingException{
            var ptr = src.getPointer();
            LookAheadMode.MODIFIERS.skip(ptr);

            return switch(ptr.element().resolution){
                case CLASS -> CLASS;
                case INTERFACE -> INTERFACE;
                case ENUM -> ENUM;
                case AT_SIGN -> ANNOTATION_INTERFACE;
                case LESS_THAN -> METHOD_DECLARATION;
                default -> {
                    if(ptr.match(JavaTS.RECORD) && ptr.match(1, TYPE_IDENTIFIERS)){
                        yield RECORD;
                    }else if(ptr.match(JavaTS.YIELD) && !ptr.match(1, JavaTS.PERIOD)){
                        yield NOT_DECLARATION;
                    }
                    if(LookAheadMode.TYPE.skip(ptr)){
                        yield switch(ptr.element().resolution){
                            case LEFT_ROUND_BRACKET, LEFT_CURLY_BRACKET -> METHOD_DECLARATION;
                            default ->{
                                if(ptr.match(IDENTIFIERS)){
                                    ptr.next();
                                    if(ptr.match(JavaTS.LEFT_ROUND_BRACKET)){
                                        yield METHOD_DECLARATION;
                                    }else{
                                        yield VARIABLE_DECLARATION;
                                    }
                                }else{
                                    yield NOT_DECLARATION;
                                }
                            }
                        };
                    }
                    yield NOT_DECLARATION;
                }
            };
        }
        
        JavaTS[] toTokens(){
            return new JavaTS[]{token};
        }

        @Override
        public String toString(){
            return token.key();
        }

    }

    static ArrayList<TypeTree> resolveTypeArguments(JavaTokenManager src) throws ParsingException{
        ArrayList<TypeTree> typeArguments = new ArrayList<>();
        if(src.match(JavaTS.LESS_THAN, JavaTS.GREATER_THAN)){
            src.skip(JavaTS.LESS_THAN, JavaTS.GREATER_THAN);
            return ParameterizedTypeTree.DIAMOND;
        }else if(src.match(JavaTS.LESS_THAN)){
            src.skip(JavaTS.LESS_THAN);
            while(true){
                if(Tree.lookAhead(src, LookAheadMode.ANNOTATIONS) == JavaTS.QUESTION){
                    typeArguments.add(WildcardTree.parse(src));
                }else{
                    typeArguments.add(NameTree.resolveTypeOrName(src));
                }
                if(src.match(JavaTS.COMMA)){
                    src.skip(JavaTS.COMMA);
                }else{
                    break;
                }
            }
            src.formatGenericsClose();
            src.skip(JavaTS.GREATER_THAN);
        }
        return typeArguments;
    }

    public static <E> ArrayList<E> getList(CEFunction<JavaTokenManager, E> getter, JavaTS rightBracket, JavaTokenManager src) throws ParsingException{
        return getList(getter, JavaTS.COMMA, rightBracket, src);
    }

    public static <E> ArrayList<E> getList(CEFunction<JavaTokenManager, E> getter, JavaTS separator, JavaTS rightBracket, JavaTokenManager src) throws ParsingException{
        var list = new ArrayList<E>();
        while(!src.match(rightBracket)){
            list.add(getter.apply(src));
            if(src.match(separator)){
                src.skip(separator);
            }else{
                break;
            }
        }
        return list;
    }

    public static <E> ArrayList<E> getListWithoutBracket(CEFunction<JavaTokenManager, E> getter, JavaTS separator, JavaTokenManager src) throws ParsingException{
        var list = new ArrayList<E>();
        do{
            list.add(getter.apply(src));
            if(src.match(separator)){
                src.skip(separator);
            }else{
                break;
            }
        }while(true);
        return list;
    }

    static boolean followsNameHeader(JavaTokenManager src) throws ParsingException{
        return src.match(IDENTIFIERS) || src.match(PRIMITIVE_TYPES) || src.match(JavaTS.VOID) || src.match(JavaTS.VAR) || src.match(JavaTS.AT_SIGN);
    }

    static JavaTS lookAhead(JavaTokenManager src, LookAheadMode... modes) throws ParsingException{
        var ptr = src.getPointer();
        for(LookAheadMode m : modes){
            if(!m.skip(ptr)){
                return JavaTS.END_OF_FILE;
            }
        }
        return ptr.element().resolution;
    }

    default TypeIdentifier getCorrespondingType(IdentifierTree id){
        return null;
    }

    default ExpressionIdentifier getCorrespondingField(IdentifierTree id){
        return null;
    }

    default MethodTree getCorrespondingMethod(IdentifierTree id, ArrayList<Type> argumentTypes){
        return null;
    }

}

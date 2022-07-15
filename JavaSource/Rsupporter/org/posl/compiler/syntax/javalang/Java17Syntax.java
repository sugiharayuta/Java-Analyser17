package org.posl.compiler.syntax.javalang;

import java.util.Arrays;
import java.util.HashSet;

import org.posl.compiler.syntax.ImplicitNTS;
import org.posl.compiler.syntax.Production;
import org.posl.compiler.syntax.Symbol;
import org.posl.compiler.syntax.SymbolSequence;
import org.posl.compiler.syntax.Syntax;
import org.posl.util.Brackets;

/**
 * This class provides JavaSE-17 syntax with a style of context-free grammers. Look chapter 19 of the specification.
 * JavaSE 17 specification
 * https://docs.oracle.com/javase/specs/jls/se17/jls17.pdf
 * 
 * @author me
 */

public class Java17Syntax implements Syntax{
    
    private final HashSet<Production> productions = new HashSet<>(1024);
    private final HashSet<ImplicitNTS> implicitSymbolList = new HashSet<>(64);
    private final int version = 17;

    //When the instance is constructed, these productions in the specification are registered.

    {
        //Productions from section 3(Lexical Structure):
        branch(JavaNTS.IDENTIFIER,
                JavaTS.IDENTIFIER,
                JavaTS.EXPORTS,
                JavaTS.MODULE,
                // T.NON_SEALED,
                JavaTS.OPEN,
                JavaTS.OPENS,
                JavaTS.PERMITS,
                JavaTS.PROVIDES,
                JavaTS.RECORD,
                JavaTS.REQUIRES,
                JavaTS.SEALED,
                JavaTS.TO,
                JavaTS.TRANSITIVE,
                JavaTS.USES,
                JavaTS.VAR,
                JavaTS.WITH,
                JavaTS.YIELD);

        /* I excluded this production because it is obviously inappropriate. */ 
        //accept(N.IDENTIFIER, T.NON_SEALED);


        branch(JavaNTS.TYPE_IDENTIFIER,
                JavaTS.IDENTIFIER,
                JavaTS.EXPORTS,
                JavaTS.MODULE,
                // T.NON_SEALED,
                JavaTS.OPEN,
                JavaTS.OPENS,
                JavaTS.PROVIDES,
                JavaTS.REQUIRES,
                JavaTS.TO,
                JavaTS.TRANSITIVE,
                JavaTS.USES,
                JavaTS.WITH);

        branch(JavaNTS.UNQUALIFIED_METHOD_IDENTIFIER,
                JavaTS.IDENTIFIER,
                JavaTS.EXPORTS,
                JavaTS.MODULE,
                // T.NON_SEALED,
                JavaTS.OPEN,
                JavaTS.OPENS,
                JavaTS.PERMITS,
                JavaTS.PROVIDES,
                JavaTS.RECORD,
                JavaTS.REQUIRES,
                JavaTS.SEALED,
                JavaTS.TO,
                JavaTS.TRANSITIVE,
                JavaTS.USES,
                JavaTS.VAR,
                JavaTS.WITH);

        branch(JavaNTS.LITERAL,
                JavaTS.INTEGER_LITERAL,
                JavaTS.FLOATING_POINT_LITERAL,
                JavaNTS.BOOLEAN_LITERAL,
                JavaTS.CHARACTER_LITERAL,
                JavaTS.STRING_LITERAL,
                JavaTS.TEXT_BLOCK,
                JavaNTS.NULL_LITERAL);

        branch(JavaNTS.BOOLEAN_LITERAL,
                JavaTS.TRUE,
                JavaTS.FALSE);

        accept(JavaNTS.NULL_LITERAL, JavaTS.NULL);

        //Productions from section 4(Types, Values, and Variables):
        branch(JavaNTS.TYPE,
                JavaNTS.PRIMITIVE_TYPE,
                JavaNTS.REFERENCE_TYPE);

        accept(JavaNTS.PRIMITIVE_TYPE, repeatOf(JavaNTS.ANNOTATION), JavaNTS.NUMERIC_TYPE);
        accept(JavaNTS.PRIMITIVE_TYPE, repeatOf(JavaNTS.ANNOTATION), JavaTS.BOOLEAN);

        branch(JavaNTS.NUMERIC_TYPE,
                JavaNTS.INTEGRAL_TYPE,
                JavaNTS.FLOATING_POINT_TYPE);

        branch(JavaNTS.INTEGRAL_TYPE,
                JavaTS.BYTE,
                JavaTS.SHORT,
                JavaTS.INT,
                JavaTS.LONG,
                JavaTS.CHAR);

        branch(JavaNTS.FLOATING_POINT_TYPE,
                JavaTS.FLOAT,
                JavaTS.DOUBLE);

        branch(JavaNTS.REFERENCE_TYPE,
                JavaNTS.CLASS_OR_INTERFACE_TYPE,
                JavaNTS.TYPE_VARIABLE,
                JavaNTS.ARRAY_TYPE);

        branch(JavaNTS.CLASS_OR_INTERFACE_TYPE,
                JavaNTS.CLASS_TYPE,
                JavaNTS.INTERFACE_TYPE);

        accept(JavaNTS.CLASS_TYPE, repeatOf(JavaNTS.ANNOTATION), JavaNTS.TYPE_IDENTIFIER, optional(JavaNTS.TYPE_ARGUMENTS));
        accept(JavaNTS.CLASS_TYPE, JavaNTS.PACKAGE_NAME, repeatOf(JavaNTS.ANNOTATION), JavaNTS.TYPE_IDENTIFIER, optional(JavaNTS.TYPE_ARGUMENTS));
        accept(JavaNTS.CLASS_TYPE, JavaNTS.CLASS_OR_INTERFACE_TYPE, repeatOf(JavaNTS.ANNOTATION), JavaNTS.TYPE_IDENTIFIER, optional(JavaNTS.TYPE_ARGUMENTS));

        accept(JavaNTS.INTERFACE_TYPE, JavaNTS.CLASS_TYPE);

        accept(JavaNTS.TYPE_VARIABLE, repeatOf(JavaNTS.ANNOTATION), JavaNTS.TYPE_IDENTIFIER);

        accept(JavaNTS.ARRAY_TYPE, JavaNTS.PRIMITIVE_TYPE, JavaNTS.DIMS);
        accept(JavaNTS.ARRAY_TYPE, JavaNTS.CLASS_OR_INTERFACE_TYPE, JavaNTS.DIMS);
        accept(JavaNTS.ARRAY_TYPE, JavaNTS.TYPE_VARIABLE, JavaNTS.DIMS);

        accept(JavaNTS.DIMS, repeatOf(JavaNTS.ANNOTATION), JavaTS.LEFT_SQUARE_BRACKET, JavaTS.RIGHT_SQUARE_BRACKET, repeatOf(repeatOf(JavaNTS.ANNOTATION), JavaTS.LEFT_SQUARE_BRACKET, JavaTS.RIGHT_SQUARE_BRACKET));

        accept(JavaNTS.TYPE_PARAMETER, repeatOf(JavaNTS.TYPE_PARAMETER_MODIFIER), JavaNTS.TYPE_IDENTIFIER, optional(JavaNTS.TYPE_BOUND));

        accept(JavaNTS.TYPE_PARAMETER_MODIFIER, JavaNTS.ANNOTATION);

        accept(JavaNTS.TYPE_BOUND, JavaTS.EXTENDS, JavaNTS.TYPE_VARIABLE);
        accept(JavaNTS.TYPE_BOUND, JavaTS.EXTENDS, JavaNTS.CLASS_OR_INTERFACE_TYPE, repeatOf(JavaNTS.ADDITIONAL_BOUND));

        accept(JavaNTS.ADDITIONAL_BOUND, JavaTS.AND, JavaNTS.INTERFACE_TYPE);

        accept(JavaNTS.TYPE_ARGUMENTS, JavaTS.LESS_THAN, JavaNTS.TYPE_ARGUMENT_LIST, JavaTS.GREATER_THAN);

        accept(JavaNTS.TYPE_ARGUMENT_LIST, JavaNTS.TYPE_ARGUMENT, repeatOf(JavaTS.COMMA, JavaNTS.TYPE_ARGUMENT));

        branch(JavaNTS.TYPE_ARGUMENT,
                JavaNTS.REFERENCE_TYPE,
                JavaNTS.WILDCARD);

        accept(JavaNTS.WILDCARD, repeatOf(JavaNTS.ANNOTATION), JavaTS.QUESTION, optional(JavaNTS.WILDCARD_BOUNDS));

        accept(JavaNTS.WILDCARD_BOUNDS, JavaTS.EXTENDS, JavaNTS.REFERENCE_TYPE);
        accept(JavaNTS.WILDCARD_BOUNDS, JavaTS.SUPER, JavaNTS.REFERENCE_TYPE);

        //Productions from section ６(Names):
        accept(JavaNTS.MODULE_NAME, JavaNTS.IDENTIFIER);
        accept(JavaNTS.MODULE_NAME, JavaNTS.MODULE_NAME, JavaTS.PERIOD, JavaNTS.IDENTIFIER);

        accept(JavaNTS.PACKAGE_NAME, JavaNTS.IDENTIFIER);
        accept(JavaNTS.PACKAGE_NAME, JavaNTS.PACKAGE_NAME, JavaTS.PERIOD, JavaNTS.IDENTIFIER);

        accept(JavaNTS.TYPE_NAME, JavaNTS.TYPE_IDENTIFIER);
        accept(JavaNTS.TYPE_NAME, JavaNTS.PACKAGE_OR_TYPE_NAME, JavaTS.PERIOD, JavaNTS.TYPE_IDENTIFIER);

        accept(JavaNTS.EXPRESSION_NAME, JavaNTS.IDENTIFIER);
        accept(JavaNTS.EXPRESSION_NAME, JavaNTS.AMBIGUOUS_NAME, JavaTS.PERIOD, JavaNTS.IDENTIFIER);

        accept(JavaNTS.METHOD_NAME, JavaNTS.UNQUALIFIED_METHOD_IDENTIFIER);

        accept(JavaNTS.PACKAGE_OR_TYPE_NAME, JavaNTS.IDENTIFIER);
        accept(JavaNTS.PACKAGE_OR_TYPE_NAME, JavaNTS.PACKAGE_OR_TYPE_NAME, JavaTS.PERIOD, JavaNTS.IDENTIFIER);

        accept(JavaNTS.AMBIGUOUS_NAME, JavaNTS.IDENTIFIER);
        accept(JavaNTS.AMBIGUOUS_NAME, JavaNTS.AMBIGUOUS_NAME, JavaTS.PERIOD, JavaNTS.IDENTIFIER);

        //Productions from section 7(Packages and Modules):
        branch(JavaNTS.COMPILATION_UNIT,
                JavaNTS.ORDINARY_COMPILATION_UNIT,
                JavaNTS.MODULAR_COMPILATION_UNIT);

        /* In the specification, there was an indention after the symbol{ImportDeclaration}, which means a division between productions.
           I ignored the indention and conbined the productions because separating the two was obviously weird.*/
        accept(JavaNTS.ORDINARY_COMPILATION_UNIT, optional(JavaNTS.PACKAGE_DECLARATION), repeatOf(JavaNTS.IMPORT_DECLARATION), repeatOf(JavaNTS.TOP_LEVEL_CLASS_OR_INTERFACE_DECLARATION));

        accept(JavaNTS.MODULAR_COMPILATION_UNIT, repeatOf(JavaNTS.IMPORT_DECLARATION), JavaNTS.MODULE_DECLARATION);

        accept(JavaNTS.PACKAGE_DECLARATION, repeatOf(JavaNTS.PACKAGE_MODIFIER), JavaTS.PACKAGE, JavaNTS.IDENTIFIER, repeatOf(JavaTS.PERIOD, JavaNTS.IDENTIFIER), JavaTS.SEMICOLON);

        accept(JavaNTS.PACKAGE_MODIFIER, JavaNTS.ANNOTATION);

        branch(JavaNTS.IMPORT_DECLARATION,
                JavaNTS.SINGLE_TYPE_IMPORT_DECLARATION,
                JavaNTS.TYPE_IMPORT_ON_DEMAND_DECLARATION,
                JavaNTS.SINGLE_STATIC_IMPORT_DECLARATION,
                JavaNTS.STATIC_IMPORT_ON_DEMAND_DECLARATION);

        accept(JavaNTS.SINGLE_TYPE_IMPORT_DECLARATION, JavaTS.IMPORT, JavaNTS.TYPE_NAME, JavaTS.SEMICOLON);

        accept(JavaNTS.TYPE_IMPORT_ON_DEMAND_DECLARATION, JavaTS.IMPORT, JavaNTS.PACKAGE_OR_TYPE_NAME, JavaTS.PERIOD, JavaTS.ASTERISK, JavaTS.SEMICOLON);

        accept(JavaNTS.SINGLE_STATIC_IMPORT_DECLARATION, JavaTS.IMPORT, JavaTS.STATIC, JavaNTS.TYPE_NAME, JavaTS.PERIOD, JavaNTS.IDENTIFIER, JavaTS.SEMICOLON);

        accept(JavaNTS.STATIC_IMPORT_ON_DEMAND_DECLARATION, JavaTS.IMPORT, JavaTS.STATIC, JavaNTS.TYPE_NAME, JavaTS.PERIOD, JavaTS.ASTERISK, JavaTS.SEMICOLON);

        branch(JavaNTS.TOP_LEVEL_CLASS_OR_INTERFACE_DECLARATION,
                JavaNTS.CLASS_DECLARATION,
                JavaNTS.INTERFACE_DECLARATION,
                JavaTS.SEMICOLON);
        
        accept(JavaNTS.MODULE_DECLARATION, repeatOf(JavaNTS.ANNOTATION), optional(JavaTS.OPEN), JavaTS.MODULE, JavaNTS.IDENTIFIER, repeatOf(JavaTS.PERIOD, JavaNTS.IDENTIFIER), JavaTS.LEFT_CURLY_BRACKET, repeatOf(JavaNTS.MODULE_DIRECTIVE), JavaTS.RIGHT_CURLY_BRACKET);

        accept(JavaNTS.MODULE_DIRECTIVE, JavaTS.REQUIRES, repeatOf(JavaNTS.REQUIRES_MODIFIER), JavaNTS.MODULE_NAME, JavaTS.SEMICOLON);
        accept(JavaNTS.MODULE_DIRECTIVE, JavaTS.EXPORTS, JavaNTS.PACKAGE_NAME, optional(JavaTS.TO, JavaNTS.MODULE_NAME, repeatOf(JavaTS.COMMA, JavaNTS.MODULE_NAME)), JavaTS.SEMICOLON);
        accept(JavaNTS.MODULE_DIRECTIVE, JavaTS.OPENS, JavaNTS.PACKAGE_NAME, optional(JavaTS.TO, JavaNTS.MODULE_NAME, repeatOf(JavaTS.COMMA, JavaNTS.MODULE_NAME)), JavaTS.SEMICOLON);
        accept(JavaNTS.MODULE_DIRECTIVE, JavaNTS.TYPE_NAME, JavaTS.SEMICOLON);
        accept(JavaNTS.MODULE_DIRECTIVE, JavaTS.PROVIDES, JavaNTS.TYPE_NAME, JavaTS.WITH, JavaNTS.TYPE_NAME, repeatOf(JavaTS.COMMA, JavaNTS.TYPE_NAME), JavaTS.SEMICOLON);

        branch(JavaNTS.REQUIRES_MODIFIER,
                JavaTS.TRANSITIVE,
                JavaTS.STATIC);
        
        //Productions from section 8(Classes):
        branch(JavaNTS.CLASS_DECLARATION,
                JavaNTS.NORMAL_CLASS_DECLARATION,
                JavaNTS.ENUM_DECLARATION,
                JavaNTS.RECORD_DECLARATION);

        accept(JavaNTS.NORMAL_CLASS_DECLARATION, repeatOf(JavaNTS.CLASS_MODIFIER), JavaTS.CLASS, JavaNTS.TYPE_IDENTIFIER, optional(JavaNTS.TYPE_PARAMETERS), optional(JavaNTS.CLASS_EXTENDS), optional(JavaNTS.CLASS_IMPLEMENTS), optional(JavaNTS.CLASS_PERMITS), JavaNTS.CLASS_BODY);

        branch(JavaNTS.CLASS_MODIFIER,
                JavaNTS.ANNOTATION,
                JavaTS.PUBLIC,
                JavaTS.PROTECTED,
                JavaTS.PRIVATE,
                JavaTS.ABSTRACT,
                JavaTS.STATIC,
                JavaTS.FINAL,
                JavaTS.SEALED,
                JavaTS.NON_SEALED,
                JavaTS.STRICTFP);

        accept(JavaNTS.TYPE_PARAMETERS, JavaTS.LESS_THAN, JavaNTS.TYPE_PARAMETER_LIST, JavaTS.GREATER_THAN);

        accept(JavaNTS.TYPE_PARAMETER_LIST, JavaNTS.TYPE_PARAMETER, repeatOf(JavaTS.COMMA, JavaNTS.TYPE_PARAMETER));

        accept(JavaNTS.CLASS_EXTENDS, JavaTS.EXTENDS, JavaNTS.CLASS_TYPE);

        accept(JavaNTS.CLASS_IMPLEMENTS, JavaTS.IMPLEMENTS, JavaNTS.INTERFACE_TYPE_LIST);

        accept(JavaNTS.INTERFACE_TYPE_LIST, JavaNTS.INTERFACE_TYPE, repeatOf(JavaTS.COMMA, JavaNTS.INTERFACE_TYPE));

        accept(JavaNTS.CLASS_PERMITS, JavaTS.PERMITS, JavaNTS.TYPE_NAME, repeatOf(JavaTS.COMMA, JavaNTS.TYPE_NAME));

        accept(JavaNTS.CLASS_BODY, JavaTS.LEFT_CURLY_BRACKET, repeatOf(JavaNTS.CLASS_BODY_DECLARATION), JavaTS.RIGHT_CURLY_BRACKET);

        branch(JavaNTS.CLASS_BODY_DECLARATION,
                JavaNTS.CLASS_MEMBER_DECLARATION,
                JavaNTS.INSTANCE_INITIALIZER,
                JavaNTS.STATIC_INITIALIZER,
                JavaNTS.CONSTRUCTOR_DECLARATION);

        branch(JavaNTS.CLASS_MEMBER_DECLARATION,
                JavaNTS.FIELD_DECLARATION,
                JavaNTS.METHOD_DECLARATION,
                JavaNTS.CLASS_DECLARATION,
                JavaNTS.INTERFACE_DECLARATION,
                JavaTS.SEMICOLON);
        
        accept(JavaNTS.FIELD_DECLARATION, repeatOf(JavaNTS.FIELD_MODIFIER), JavaNTS.UNANN_TYPE, JavaNTS.VARIABLE_DECLARATOR_LIST, JavaTS.SEMICOLON);

        branch(JavaNTS.FIELD_MODIFIER,
                JavaNTS.ANNOTATION,
                JavaTS.PUBLIC,
                JavaTS.PROTECTED,
                JavaTS.PRIVATE,
                JavaTS.STATIC,
                JavaTS.FINAL,
                JavaTS.TRANSIENT,
                JavaTS.VOLATILE);
        
        accept(JavaNTS.VARIABLE_DECLARATOR_LIST, JavaNTS.VARIABLE_DECLARATOR, repeatOf(JavaTS.COMMA, JavaNTS.VARIABLE_DECLARATOR));

        accept(JavaNTS.VARIABLE_DECLARATOR, JavaNTS.VARIABLE_DECLARATOR_ID, optional(JavaTS.SIMPLE_ASSIGNMENT, JavaNTS.VARIABLE_INITIALIZER));

        accept(JavaNTS.VARIABLE_DECLARATOR_ID, JavaNTS.IDENTIFIER, optional(JavaNTS.DIMS));

        branch(JavaNTS.VARIABLE_INITIALIZER,
                JavaNTS.EXPRESSION,
                JavaNTS.ARRAY_INITIALIZER);
        
        branch(JavaNTS.UNANN_TYPE,
                JavaNTS.UNANN_PRIMITIVE_TYPE,
                JavaNTS.UNANN_REFERENCE_TYPE);
        
        branch(JavaNTS.UNANN_PRIMITIVE_TYPE,
                JavaNTS.NUMERIC_TYPE,
                JavaTS.BOOLEAN);
        
        branch(JavaNTS.UNANN_REFERENCE_TYPE,
                JavaNTS.UNANN_CLASS_OR_INTERFACE_TYPE,
                JavaNTS.UNANN_TYPE_VARIABLE,
                JavaNTS.UNANN_ARRAY_TYPE);
        
        branch(JavaNTS.UNANN_CLASS_OR_INTERFACE_TYPE,
                JavaNTS.UNANN_CLASS_TYPE,
                JavaNTS.UNANN_INTERFACE_TYPE);
        
        accept(JavaNTS.UNANN_CLASS_TYPE, JavaNTS.TYPE_IDENTIFIER, optional(JavaNTS.TYPE_ARGUMENTS));
        accept(JavaNTS.UNANN_CLASS_TYPE, JavaNTS.PACKAGE_NAME, JavaTS.PERIOD, repeatOf(JavaNTS.ANNOTATION), JavaNTS.TYPE_IDENTIFIER, optional(JavaNTS.TYPE_ARGUMENTS));
        accept(JavaNTS.UNANN_CLASS_TYPE, JavaNTS.UNANN_CLASS_OR_INTERFACE_TYPE, JavaTS.PERIOD, repeatOf(JavaNTS.ANNOTATION), JavaNTS.TYPE_IDENTIFIER, optional(JavaNTS.TYPE_ARGUMENTS));

        accept(JavaNTS.UNANN_INTERFACE_TYPE, JavaNTS.UNANN_CLASS_TYPE);

        accept(JavaNTS.UNANN_TYPE_VARIABLE, JavaNTS.TYPE_IDENTIFIER);

        accept(JavaNTS.UNANN_ARRAY_TYPE, JavaNTS.UNANN_PRIMITIVE_TYPE, JavaNTS.DIMS);
        accept(JavaNTS.UNANN_ARRAY_TYPE, JavaNTS.UNANN_CLASS_OR_INTERFACE_TYPE, JavaNTS.DIMS);
        accept(JavaNTS.UNANN_ARRAY_TYPE, JavaNTS.UNANN_TYPE_VARIABLE, JavaNTS.DIMS);

        accept(JavaNTS.METHOD_DECLARATION, repeatOf(JavaNTS.METHOD_MODIFIER), JavaNTS.METHOD_HEADER, JavaNTS.METHOD_BODY);

        branch(JavaNTS.METHOD_MODIFIER,
                JavaNTS.ANNOTATION,
                JavaTS.PUBLIC,
                JavaTS.PROTECTED,
                JavaTS.PRIVATE,
                JavaTS.ABSTRACT,
                JavaTS.STATIC,
                JavaTS.FINAL,
                JavaTS.SYNCHRONIZED,
                JavaTS.NATIVE,
                JavaTS.STRICTFP);
        
        accept(JavaNTS.METHOD_HEADER, JavaNTS.RESULT, JavaNTS.METHOD_DECLARATOR, optional(JavaNTS.THROWS));
        accept(JavaNTS.METHOD_HEADER, JavaNTS.TYPE_PARAMETERS, repeatOf(JavaNTS.ANNOTATION), JavaNTS.RESULT, JavaNTS.METHOD_DECLARATOR, optional(JavaNTS.THROWS));

        branch(JavaNTS.RESULT,
                JavaNTS.UNANN_TYPE,
                JavaTS.VOID);
        
        accept(JavaNTS.METHOD_DECLARATOR, JavaNTS.IDENTIFIER, JavaTS.LEFT_ROUND_BRACKET, optional(JavaNTS.RECEIVER_PARAMETER, JavaTS.COMMA), optional(JavaNTS.FORMAL_PARAMETER_LIST), JavaTS.RIGHT_ROUND_BRACKET, optional(JavaNTS.DIMS));

        accept(JavaNTS.RECEIVER_PARAMETER, repeatOf(JavaNTS.ANNOTATION), JavaNTS.UNANN_TYPE, optional(JavaNTS.IDENTIFIER, JavaTS.PERIOD), JavaTS.THIS);

        accept(JavaNTS.FORMAL_PARAMETER_LIST, JavaNTS.FORMAL_PARAMETER, repeatOf(JavaTS.COMMA, JavaNTS.FORMAL_PARAMETER));

        accept(JavaNTS.FORMAL_PARAMETER, repeatOf(JavaNTS.VARIABLE_MODIFIER), JavaNTS.UNANN_TYPE, JavaNTS.VARIABLE_DECLARATOR_ID);
        accept(JavaNTS.FORMAL_PARAMETER, JavaNTS.VARIABLE_ARITY_PARAMETER);

        accept(JavaNTS.VARIABLE_ARITY_PARAMETER, repeatOf(JavaNTS.VARIABLE_MODIFIER), JavaNTS.UNANN_TYPE, repeatOf(JavaNTS.ANNOTATION), JavaTS.ELLIPSIS, JavaNTS.IDENTIFIER);

        branch(JavaNTS.VARIABLE_MODIFIER,
                JavaNTS.ANNOTATION,
                JavaTS.FINAL);

        accept(JavaNTS.THROWS, JavaTS.THROWS, JavaNTS.EXCEPTION_TYPE_LIST);

        accept(JavaNTS.EXCEPTION_TYPE_LIST, JavaNTS.EXCEPTION_TYPE, repeatOf(JavaTS.COMMA, JavaNTS.EXCEPTION_TYPE));

        branch(JavaNTS.EXCEPTION_TYPE,
                JavaNTS.CLASS_TYPE,
                JavaNTS.TYPE_VARIABLE);
        
        branch(JavaNTS.METHOD_BODY,
                JavaNTS.BLOCK,
                JavaTS.SEMICOLON);
        
        accept(JavaNTS.INSTANCE_INITIALIZER, JavaNTS.BLOCK);

        accept(JavaNTS.STATIC_INITIALIZER, JavaTS.STATIC, JavaNTS.BLOCK);

        accept(JavaNTS.CONSTRUCTOR_DECLARATION, repeatOf(JavaNTS.CONSTRUCTOR_MODIFIER), JavaNTS.CONSTRUCTOR_DECLARATOR, optional(JavaNTS.THROWS), JavaNTS.CONSTRUCTOR_BODY);

        branch(JavaNTS.CONSTRUCTOR_MODIFIER,
                JavaNTS.ANNOTATION,
                JavaTS.PUBLIC,
                JavaTS.PROTECTED,
                JavaTS.PRIVATE);
        
        accept(JavaNTS.CONSTRUCTOR_DECLARATOR, optional(JavaNTS.TYPE_PARAMETERS), JavaNTS.SIMPLE_TYPE_NAME, JavaTS.LEFT_ROUND_BRACKET, optional(JavaNTS.RECEIVER_PARAMETER, JavaTS.COMMA), optional(JavaNTS.FORMAL_PARAMETER_LIST), JavaTS.RIGHT_ROUND_BRACKET);

        accept(JavaNTS.SIMPLE_TYPE_NAME, JavaNTS.TYPE_IDENTIFIER);

        accept(JavaNTS.CONSTRUCTOR_BODY, JavaTS.LEFT_CURLY_BRACKET, optional(JavaNTS.EXPLICIT_CONSTRUCTOR_INVOCATION), optional(JavaNTS.BLOCK_STATEMENT), JavaTS.RIGHT_CURLY_BRACKET);

        accept(JavaNTS.EXPLICIT_CONSTRUCTOR_INVOCATION, optional(JavaNTS.TYPE_ARGUMENTS), JavaTS.THIS, JavaTS.LEFT_ROUND_BRACKET, optional(JavaNTS.ARGUMENT_LIST), JavaTS.RIGHT_ROUND_BRACKET, JavaTS.SEMICOLON);
        accept(JavaNTS.EXPLICIT_CONSTRUCTOR_INVOCATION, optional(JavaNTS.TYPE_ARGUMENTS), JavaTS.SUPER, JavaTS.LEFT_ROUND_BRACKET, optional(JavaNTS.ARGUMENT_LIST), JavaTS.RIGHT_ROUND_BRACKET, JavaTS.SEMICOLON);
        accept(JavaNTS.EXPLICIT_CONSTRUCTOR_INVOCATION, JavaNTS.EXPRESSION_NAME, JavaTS.PERIOD, optional(JavaNTS.TYPE_ARGUMENTS), JavaTS.THIS, JavaTS.LEFT_ROUND_BRACKET, optional(JavaNTS.ARGUMENT_LIST), JavaTS.RIGHT_ROUND_BRACKET, JavaTS.SEMICOLON);
        accept(JavaNTS.EXPLICIT_CONSTRUCTOR_INVOCATION, JavaNTS.PRIMARY, JavaTS.PERIOD, optional(JavaNTS.TYPE_ARGUMENTS), JavaTS.THIS, JavaTS.LEFT_ROUND_BRACKET, optional(JavaNTS.ARGUMENT_LIST), JavaTS.RIGHT_ROUND_BRACKET, JavaTS.SEMICOLON);

        accept(JavaNTS.ENUM_DECLARATION, repeatOf(JavaNTS.CLASS_MODIFIER), JavaTS.ENUM, JavaNTS.TYPE_IDENTIFIER, optional(JavaNTS.CLASS_IMPLEMENTS), JavaNTS.ENUM_BODY);

        accept(JavaNTS.ENUM_BODY, JavaTS.LEFT_CURLY_BRACKET, optional(JavaNTS.ENUM_CONSTANT_LIST), optional(JavaTS.COMMA), optional(JavaNTS.ENUM_BODY_DECLARATIONS));

        accept(JavaNTS.ENUM_CONSTANT_LIST, JavaNTS.ENUM_CONSTANT, repeatOf(JavaTS.COMMA, JavaNTS.ENUM_CONSTANT));

        accept(JavaNTS.ENUM_CONSTANT, repeatOf(JavaNTS.ENUM_CONSTANT_MODIFIER), JavaNTS.IDENTIFIER, optional(JavaTS.LEFT_ROUND_BRACKET, optional(JavaNTS.ARGUMENT_LIST), JavaTS.RIGHT_ROUND_BRACKET), optional(JavaNTS.CLASS_BODY));

        accept(JavaNTS.ENUM_CONSTANT_MODIFIER, JavaNTS.ANNOTATION);

        accept(JavaNTS.ENUM_BODY_DECLARATIONS, JavaTS.SEMICOLON, repeatOf(JavaNTS.CLASS_BODY_DECLARATION));

        accept(JavaNTS.RECORD_DECLARATION, repeatOf(JavaNTS.CLASS_MODIFIER), JavaTS.RECORD, JavaNTS.TYPE_IDENTIFIER, optional(JavaNTS.TYPE_PARAMETERS), JavaNTS.RECORD_HEADER, optional(JavaNTS.CLASS_IMPLEMENTS), JavaNTS.RECORD_BODY);

        accept(JavaNTS.RECORD_HEADER, JavaTS.LEFT_ROUND_BRACKET, optional(JavaNTS.RECORD_COMPONENT_LIST), JavaTS.RIGHT_ROUND_BRACKET);

        accept(JavaNTS.RECORD_COMPONENT_LIST, JavaNTS.RECORD_COMPONENT, optional(JavaTS.COMMA, JavaNTS.RECORD_COMPONENT));

        accept(JavaNTS.RECORD_COMPONENT, repeatOf(JavaNTS.RECORD_COMPONENT_MODIFIER), JavaNTS.UNANN_TYPE, JavaNTS.IDENTIFIER);
        accept(JavaNTS.VARIABLE_ARITY_RECORD_COMPONENT);

        accept(JavaNTS.VARIABLE_ARITY_RECORD_COMPONENT, repeatOf(JavaNTS.RECORD_COMPONENT_MODIFIER), JavaNTS.UNANN_TYPE, repeatOf(JavaNTS.ANNOTATION), JavaTS.ELLIPSIS, JavaNTS.IDENTIFIER);

        accept(JavaNTS.RECORD_COMPONENT_MODIFIER, JavaNTS.ANNOTATION);

        accept(JavaNTS.RECORD_BODY, JavaTS.LEFT_CURLY_BRACKET, repeatOf(JavaNTS.RECORD_BODY_DECLARATION), JavaTS.RIGHT_CURLY_BRACKET);

        branch(JavaNTS.RECORD_BODY_DECLARATION,
                JavaNTS.CLASS_BODY_DECLARATION,
                JavaNTS.COMPACT_CONSTRUCTOR_DECLARATION);
        
        accept(JavaNTS.COMPACT_CONSTRUCTOR_DECLARATION, repeatOf(JavaNTS.CONSTRUCTOR_MODIFIER), JavaNTS.SIMPLE_TYPE_NAME, JavaNTS.CONSTRUCTOR_BODY);

        //Productions from section 9(Interfaces):
        branch(JavaNTS.INTERFACE_DECLARATION,
                JavaNTS.NORMAL_INTERFACE_DECLARATION,
                JavaNTS.ANNOTATION_INTERFACE_DECLARATION);
        
        accept(JavaNTS.NORMAL_INTERFACE_DECLARATION, repeatOf(JavaNTS.INTERFACE_MODIFIER), JavaTS.INTERFACE, JavaNTS.TYPE_IDENTIFIER, optional(JavaNTS.TYPE_PARAMETERS), optional(JavaNTS.INTERFACE_EXTENDS), optional(JavaNTS.INTERFACE_PERMITS), JavaNTS.INTERFACE_BODY);

        branch(JavaNTS.INTERFACE_MODIFIER,
                JavaNTS.ANNOTATION,
                JavaTS.PUBLIC,
                JavaTS.PROTECTED,
                JavaTS.PRIVATE,
                JavaTS.ABSTRACT,
                JavaTS.STATIC,
                JavaTS.SEALED,
                JavaTS.NON_SEALED,
                JavaTS.STRICTFP);
        
        accept(JavaNTS.INTERFACE_EXTENDS, JavaTS.EXTENDS, JavaNTS.INTERFACE_TYPE_LIST);

        accept(JavaNTS.INTERFACE_PERMITS, JavaTS.PERMITS, JavaNTS.TYPE_NAME, repeatOf(JavaTS.COMMA, JavaNTS.TYPE_NAME));

        accept(JavaNTS.INTERFACE_BODY, JavaTS.LEFT_CURLY_BRACKET, repeatOf(JavaNTS.INTERFACE_MEMBER_DECLARATION), JavaTS.RIGHT_CURLY_BRACKET);

        branch(JavaNTS.INTERFACE_MEMBER_DECLARATION,
                JavaNTS.CONSTANT_DECLARATION,
                JavaNTS.INTERFACE_METHOD_DECLARATION,
                JavaNTS.CLASS_DECLARATION,
                JavaNTS.INTERFACE_DECLARATION,
                JavaTS.SEMICOLON);
        
        accept(JavaNTS.CONSTANT_DECLARATION, repeatOf(JavaNTS.CONSTANT_MODIFIER), JavaNTS.UNANN_TYPE, JavaNTS.VARIABLE_DECLARATOR_LIST, JavaTS.SEMICOLON);

        branch(JavaNTS.CONSTANT_MODIFIER,
                JavaNTS.ANNOTATION,
                JavaTS.PUBLIC,
                JavaTS.STATIC,
                JavaTS.FINAL);
        
        accept(JavaNTS.INTERFACE_METHOD_DECLARATION, repeatOf(JavaNTS.INTERFACE_METHOD_MODIFIER), JavaNTS.METHOD_HEADER, JavaNTS.METHOD_BODY);

        branch(JavaNTS.INTERFACE_METHOD_MODIFIER,
                JavaNTS.ANNOTATION,
                JavaTS.PUBLIC,
                JavaTS.PRIVATE,
                JavaTS.ABSTRACT,
                JavaTS.DEFAULT,
                JavaTS.STATIC,
                JavaTS.STRICTFP);
        
        accept(JavaNTS.ANNOTATION_INTERFACE_DECLARATION, repeatOf(JavaNTS.INTERFACE_MODIFIER), JavaTS.AT_SIGN, JavaTS.INTERFACE, JavaNTS.TYPE_IDENTIFIER, JavaNTS.ANNOTATION_INTERFACE_BODY);
        
        accept(JavaNTS.ANNOTATION_INTERFACE_BODY, JavaTS.LEFT_CURLY_BRACKET, repeatOf(JavaNTS.ANNOTATION_INTERFACE_MEMBER_DECLARATION), JavaTS.RIGHT_CURLY_BRACKET);

        branch(JavaNTS.ANNOTATION_INTERFACE_MEMBER_DECLARATION,
                JavaNTS.ANNOTATION_INTERFACE_ELEMENT_DECLARATION,
                JavaNTS.CONSTANT_DECLARATION,
                JavaNTS.CLASS_DECLARATION,
                JavaNTS.INTERFACE_DECLARATION,
                JavaTS.SEMICOLON);
        
        accept(JavaNTS.ANNOTATION_INTERFACE_ELEMENT_DECLARATION, repeatOf(JavaNTS.ANNOTATION_INTERFACE_ELEMENT_MODIFIER), JavaNTS.UNANN_TYPE, JavaNTS.IDENTIFIER, JavaTS.LEFT_ROUND_BRACKET, JavaTS.RIGHT_ROUND_BRACKET, optional(JavaNTS.DIMS), optional(JavaNTS.DEFAULT_VALUE), JavaTS.SEMICOLON);

        branch(JavaNTS.ANNOTATION_INTERFACE_ELEMENT_MODIFIER,
                JavaNTS.ANNOTATION,
                JavaTS.PUBLIC,
                JavaTS.ABSTRACT);
        
        accept(JavaNTS.DEFAULT_VALUE, JavaTS.DEFAULT, JavaNTS.ELEMENT_VALUE);

        branch(JavaNTS.ANNOTATION,
                JavaNTS.NORMAL_ANNOTATION,
                JavaNTS.MARKER_ANNOTATION,
                JavaNTS.SINGLE_ELEMENT_ANNOTATION);
        
        accept(JavaNTS.NORMAL_ANNOTATION, JavaTS.AT_SIGN, JavaNTS.TYPE_NAME, JavaTS.LEFT_ROUND_BRACKET, optional(JavaNTS.ELEMENT_VALUE_PAIR_LIST), JavaTS.RIGHT_ROUND_BRACKET);

        accept(JavaNTS.ELEMENT_VALUE_PAIR_LIST, JavaNTS.ELEMENT_VALUE_PAIR, repeatOf(JavaTS.COMMA, JavaNTS.ELEMENT_VALUE_PAIR));

        accept(JavaNTS.ELEMENT_VALUE_PAIR, JavaNTS.IDENTIFIER, JavaTS.SIMPLE_ASSIGNMENT, JavaNTS.ELEMENT_VALUE);

        branch(JavaNTS.ELEMENT_VALUE,
                JavaNTS.CONDITIONAL_EXPRESSION,
                JavaNTS.ELEMENT_VALUE_ARRAY_INITIALIZER,
                JavaNTS.ANNOTATION);
        
        accept(JavaNTS.ELEMENT_VALUE_ARRAY_INITIALIZER, JavaTS.LEFT_CURLY_BRACKET, optional(JavaNTS.ELEMENT_VALUE_LIST), optional(JavaTS.COMMA), JavaTS.RIGHT_CURLY_BRACKET);

        accept(JavaNTS.ELEMENT_VALUE_LIST, JavaNTS.ELEMENT_VALUE, repeatOf(JavaTS.COMMA, JavaNTS.ELEMENT_VALUE));

        accept(JavaNTS.MARKER_ANNOTATION, JavaTS.AT_SIGN, JavaNTS.TYPE_NAME);

        accept(JavaNTS.SINGLE_ELEMENT_ANNOTATION, JavaTS.AT_SIGN, JavaNTS.TYPE_NAME, JavaTS.LEFT_ROUND_BRACKET, JavaNTS.ELEMENT_VALUE, JavaTS.RIGHT_ROUND_BRACKET);

        //Productions from section 10(Arrays):
        accept(JavaNTS.ARRAY_INITIALIZER, JavaTS.LEFT_CURLY_BRACKET, optional(JavaNTS.VARIABLE_INITIALIZER_LIST), optional(JavaTS.COMMA), JavaTS.RIGHT_CURLY_BRACKET);

        accept(JavaNTS.VARIABLE_INITIALIZER_LIST, JavaNTS.VARIABLE_INITIALIZER, repeatOf(JavaTS.COMMA, JavaNTS.VARIABLE_INITIALIZER));

        //Productions from section 14(Blocks, Statements, and Patterns):
        accept(JavaNTS.BLOCK, JavaTS.LEFT_CURLY_BRACKET, optional(JavaNTS.BLOCK_STATEMENTS), JavaTS.RIGHT_CURLY_BRACKET);

        accept(JavaNTS.BLOCK_STATEMENTS, JavaNTS.BLOCK_STATEMENT, repeatOf(JavaNTS.BLOCK_STATEMENT));

        branch(JavaNTS.BLOCK_STATEMENT,
                JavaNTS.LOCAL_CLASS_OR_INTERFACE_DECLARATION,
                JavaNTS.LOCAL_VARIABLE_DECLARATION_STATEMENT,
                JavaNTS.STATEMENT);
        
        branch(JavaNTS.LOCAL_CLASS_OR_INTERFACE_DECLARATION,
                JavaNTS.CLASS_DECLARATION,
                JavaNTS.NORMAL_INTERFACE_DECLARATION);
        
        accept(JavaNTS.LOCAL_VARIABLE_DECLARATION_STATEMENT, JavaNTS.LOCAL_VARIABLE_DECLARATION, JavaTS.SEMICOLON);

        accept(JavaNTS.LOCAL_VARIABLE_DECLARATION, repeatOf(JavaNTS.VARIABLE_MODIFIER), JavaNTS.LOCAL_VARIABLE_TYPE, JavaNTS.VARIABLE_DECLARATOR_LIST);

        branch(JavaNTS.LOCAL_VARIABLE_TYPE,
                JavaNTS.UNANN_TYPE,
                JavaTS.VAR);
        
        branch(JavaNTS.STATEMENT,
                JavaNTS.STATEMENT_WITHOUT_TRAILING_SUBSTATEMENT,
                JavaNTS.LABELED_STATEMENT,
                JavaNTS.IF_THEN_STATEMENT,
                JavaNTS.IF_THEN_ELSE_STATEMENT,
                JavaNTS.WHILE_STATEMENT,
                JavaNTS.FOR_STATEMENT);
        
        branch(JavaNTS.STATEMENT_NO_SHORT_IF,
                JavaNTS.STATEMENT_WITHOUT_TRAILING_SUBSTATEMENT,
                JavaNTS.LABELED_STATEMENT_NO_SHORT_IF,
                JavaNTS.IF_THEN_ELSE_STATEMENT_NO_SHORT_IF,
                JavaNTS.WHILE_STATEMENT_NO_SHORT_IF,
                JavaNTS.FOR_STATEMENT_NO_SHORT_IF);
        
        branch(JavaNTS.STATEMENT_WITHOUT_TRAILING_SUBSTATEMENT,
                JavaNTS.BLOCK,
                JavaNTS.EMPTY_STATEMENT,
                JavaNTS.EXPRESSION_STATEMENT,
                JavaNTS.ASSERT_STATEMENT,
                JavaNTS.SWITCH_STATEMENT,
                JavaNTS.DO_STATEMENT,
                JavaNTS.BREAK_STATEMENT,
                JavaNTS.CONTINUE_STATEMENT,
                JavaNTS.RETURN_STATEMENT,
                JavaNTS.SYNCHRONIZED_STATEMENT,
                JavaNTS.THROW_STATEMENT,
                JavaNTS.TRY_STATEMENT,
                JavaNTS.YIELD_STATEMENT);
        
        accept(JavaNTS.EMPTY_STATEMENT, JavaTS.SEMICOLON);
        
        accept(JavaNTS.LABELED_STATEMENT, JavaNTS.IDENTIFIER, JavaTS.COLON, JavaNTS.STATEMENT);

        accept(JavaNTS.LABELED_STATEMENT_NO_SHORT_IF, JavaNTS.IDENTIFIER, JavaTS.COLON, JavaNTS.STATEMENT_NO_SHORT_IF);

        accept(JavaNTS.EXPRESSION_STATEMENT, JavaNTS.STATEMENT_EXPRESSION, JavaTS.SEMICOLON);

        branch(JavaNTS.STATEMENT_EXPRESSION,
                JavaNTS.ASSIGNMENT,
                JavaNTS.PRE_INCREMENT_EXPRESSION,
                JavaNTS.PRE_DECREMENT_EXPRESSION,
                JavaNTS.POST_INCREMENT_EXPRESSION,
                JavaNTS.POST_DECREMENT_EXPRESSION,
                JavaNTS.METHOD_INVOCATION,
                JavaNTS.CLASS_INSTANCE_CREATION_EXPRESSION);
        
        accept(JavaNTS.IF_THEN_STATEMENT, JavaTS.IF, JavaTS.LEFT_ROUND_BRACKET, JavaNTS.EXPRESSION, JavaTS.RIGHT_ROUND_BRACKET, JavaNTS.STATEMENT);

        accept(JavaNTS.IF_THEN_ELSE_STATEMENT, JavaTS.IF, JavaTS.LEFT_ROUND_BRACKET, JavaNTS.EXPRESSION, JavaTS.RIGHT_ROUND_BRACKET, JavaNTS.STATEMENT_NO_SHORT_IF, JavaTS.ELSE, JavaNTS.STATEMENT);

        accept(JavaNTS.IF_THEN_ELSE_STATEMENT_NO_SHORT_IF, JavaTS.IF, JavaTS.LEFT_ROUND_BRACKET, JavaNTS.EXPRESSION, JavaTS.RIGHT_ROUND_BRACKET, JavaNTS.STATEMENT_NO_SHORT_IF, JavaTS.ELSE, JavaNTS.STATEMENT_NO_SHORT_IF);

        accept(JavaNTS.ASSERT_STATEMENT, JavaNTS.EXPRESSION, JavaTS.SEMICOLON);
        accept(JavaNTS.ASSERT_STATEMENT, JavaNTS.EXPRESSION, JavaTS.COLON, JavaNTS.EXPRESSION, JavaTS.SEMICOLON);
        
        accept(JavaNTS.SWITCH_STATEMENT, JavaTS.LEFT_ROUND_BRACKET, JavaNTS.EXPRESSION, JavaTS.RIGHT_ROUND_BRACKET, JavaNTS.SWITCH_BLOCK);

        accept(JavaNTS.SWITCH_BLOCK, JavaTS.LEFT_CURLY_BRACKET, JavaNTS.SWITCH_RULE, repeatOf(JavaNTS.SWITCH_RULE), JavaTS.RIGHT_CURLY_BRACKET);
        accept(JavaNTS.SWITCH_BLOCK, JavaTS.LEFT_CURLY_BRACKET, repeatOf(JavaNTS.SWITCH_BLOCK_STATEMENT_GROUP), repeatOf(JavaNTS.SWITCH_LABEL, JavaTS.COLON), JavaTS.RIGHT_CURLY_BRACKET);

        accept(JavaNTS.SWITCH_RULE, JavaNTS.SWITCH_LABEL, JavaTS.ARROW, JavaNTS.EXPRESSION, JavaTS.SEMICOLON);
        accept(JavaNTS.SWITCH_RULE, JavaNTS.SWITCH_LABEL, JavaTS.ARROW, JavaNTS.BLOCK);
        accept(JavaNTS.SWITCH_RULE, JavaNTS.SWITCH_LABEL, JavaTS.ARROW, JavaNTS.THROW_STATEMENT);

        accept(JavaNTS.SWITCH_BLOCK_STATEMENT_GROUP, JavaNTS.SWITCH_LABEL, JavaTS.COLON, repeatOf(JavaNTS.SWITCH_LABEL, JavaTS.COLON), JavaNTS.BLOCK_STATEMENTS);

        accept(JavaNTS.SWITCH_LABEL, JavaTS.CASE, JavaNTS.CASE_CONSTANT, repeatOf(JavaTS.COMMA, JavaNTS.CASE_CONSTANT));
        accept(JavaNTS.SWITCH_LABEL, JavaTS.DEFAULT);

        accept(JavaNTS.CASE_CONSTANT, JavaNTS.CONDITIONAL_EXPRESSION);

        accept(JavaNTS.WHILE_STATEMENT, JavaTS.WHILE, JavaTS.LEFT_ROUND_BRACKET, JavaNTS.EXPRESSION, JavaTS.RIGHT_ROUND_BRACKET, JavaNTS.STATEMENT);

        accept(JavaNTS.WHILE_STATEMENT_NO_SHORT_IF, JavaTS.WHILE, JavaTS.LEFT_ROUND_BRACKET, JavaNTS.EXPRESSION, JavaTS.RIGHT_ROUND_BRACKET, JavaNTS.STATEMENT_NO_SHORT_IF);

        accept(JavaNTS.DO_STATEMENT, JavaTS.DO, JavaNTS.STATEMENT, JavaTS.WHILE, JavaTS.LEFT_ROUND_BRACKET, JavaNTS.EXPRESSION, JavaTS.RIGHT_ROUND_BRACKET, JavaTS.SEMICOLON);

        branch(JavaNTS.FOR_STATEMENT,
                JavaNTS.BASIC_FOR_STATEMENT,
                JavaNTS.ENHANCED_FOR_STATEMENT);
        
        branch(JavaNTS.FOR_STATEMENT_NO_SHORT_IF,
                JavaNTS.BASIC_FOR_STATEMENT_NO_SHORT_IF,
                JavaNTS.ENHANCED_FOR_STATEMENT_NO_SHORT_IF);
        
        accept(JavaNTS.BASIC_FOR_STATEMENT, JavaTS.FOR, JavaTS.LEFT_ROUND_BRACKET, optional(JavaNTS.FOR_INIT), JavaTS.SEMICOLON, optional(JavaNTS.EXPRESSION), JavaTS.SEMICOLON, optional(JavaNTS.FOR_UPDATE), JavaTS.RIGHT_ROUND_BRACKET, JavaNTS.STATEMENT);

        accept(JavaNTS.BASIC_FOR_STATEMENT_NO_SHORT_IF, JavaTS.FOR, JavaTS.LEFT_ROUND_BRACKET, optional(JavaNTS.FOR_INIT), JavaTS.SEMICOLON, optional(JavaNTS.EXPRESSION), JavaTS.SEMICOLON, optional(JavaNTS.FOR_UPDATE), JavaTS.RIGHT_ROUND_BRACKET, JavaNTS.STATEMENT_NO_SHORT_IF);

        branch(JavaNTS.FOR_INIT,
                JavaNTS.STATEMENT_EXPRESSION_LIST,
                JavaNTS.LOCAL_VARIABLE_DECLARATION);
        
        accept(JavaNTS.FOR_UPDATE, JavaNTS.STATEMENT_EXPRESSION_LIST);

        accept(JavaNTS.STATEMENT_EXPRESSION_LIST, JavaNTS.STATEMENT_EXPRESSION, repeatOf(JavaTS.COMMA, JavaNTS.STATEMENT_EXPRESSION));

        accept(JavaNTS.ENHANCED_FOR_STATEMENT, JavaTS.FOR, JavaTS.LEFT_ROUND_BRACKET, JavaNTS.LOCAL_VARIABLE_DECLARATION, JavaTS.COLON, JavaNTS.EXPRESSION, JavaTS.RIGHT_ROUND_BRACKET, JavaNTS.STATEMENT);

        accept(JavaNTS.ENHANCED_FOR_STATEMENT_NO_SHORT_IF, JavaTS.FOR, JavaTS.LEFT_ROUND_BRACKET, JavaNTS.LOCAL_VARIABLE_DECLARATION, JavaTS.COLON, JavaNTS.EXPRESSION, JavaTS.RIGHT_ROUND_BRACKET, JavaNTS.STATEMENT_NO_SHORT_IF);

        accept(JavaNTS.BREAK_STATEMENT, JavaTS.BREAK, optional(JavaNTS.IDENTIFIER), JavaTS.SEMICOLON);

        accept(JavaNTS.YIELD_STATEMENT, JavaTS.YIELD, JavaNTS.EXPRESSION, JavaTS.SEMICOLON);

        accept(JavaNTS.CONTINUE_STATEMENT, JavaTS.CONTINUE, optional(JavaNTS.IDENTIFIER), JavaTS.SEMICOLON);

        accept(JavaNTS.RETURN_STATEMENT, JavaTS.RETURN, optional(JavaNTS.EXPRESSION), JavaTS.SEMICOLON);

        accept(JavaNTS.THROW_STATEMENT, JavaTS.THROW, JavaNTS.EXPRESSION, JavaTS.SEMICOLON);

        accept(JavaNTS.SYNCHRONIZED_STATEMENT, JavaTS.LEFT_ROUND_BRACKET,  JavaTS.SYNCHRONIZED, JavaTS.RIGHT_ROUND_BRACKET, JavaNTS.BLOCK);

        accept(JavaNTS.TRY_STATEMENT, JavaTS.TRY, JavaNTS.BLOCK, JavaNTS.CATCHES);
        accept(JavaNTS.TRY_STATEMENT, JavaTS.TRY, JavaNTS.BLOCK, optional(JavaNTS.CATCHES), JavaNTS.FINALLY);
        accept(JavaNTS.TRY_STATEMENT, JavaNTS.TRY_WITH_RESOURCES_STATEMENT);

        accept(JavaNTS.CATCHES, JavaNTS.CATCH_CLAUSE, repeatOf(JavaNTS.CATCH_CLAUSE));

        accept(JavaNTS.CATCH_CLAUSE, JavaTS.CATCH, JavaTS.LEFT_ROUND_BRACKET, JavaNTS.CATCH_FORMAL_PARAMETER, JavaTS.RIGHT_ROUND_BRACKET, JavaNTS.BLOCK);

        accept(JavaNTS.CATCH_FORMAL_PARAMETER, repeatOf(JavaNTS.VARIABLE_MODIFIER), JavaNTS.CATCH_TYPE, JavaNTS.VARIABLE_DECLARATOR_ID);

        accept(JavaNTS.CATCH_TYPE, JavaNTS.UNANN_CLASS_TYPE, repeatOf(JavaTS.VERTICAL_BAR, JavaNTS.CLASS_TYPE));

        accept(JavaNTS.FINALLY, JavaTS.FINALLY, JavaNTS.BLOCK);

        accept(JavaNTS.TRY_WITH_RESOURCES_STATEMENT, JavaTS.TRY, JavaNTS.RESOURCE_SPECIFICATION, JavaNTS.BLOCK, optional(JavaNTS.CATCHES), optional(JavaNTS.FINALLY));

        accept(JavaNTS.RESOURCE_SPECIFICATION, JavaTS.LEFT_ROUND_BRACKET, JavaNTS.RESOURCE_LIST, optional(JavaTS.SEMICOLON), JavaTS.RIGHT_ROUND_BRACKET);

        accept(JavaNTS.RESOURCE_LIST, JavaNTS.RESOURCE, repeatOf(JavaTS.SEMICOLON, JavaNTS.RESOURCE));

        branch(JavaNTS.RESOURCE,
                JavaNTS.LOCAL_VARIABLE_DECLARATION,
                JavaNTS.VARIABLE_ACCESS);
        
        //This rule is missing in chapter 19 of the specification, but it's expected to be here.
        branch(JavaNTS.VARIABLE_ACCESS,
                JavaNTS.EXPRESSION_NAME,
                JavaNTS.FIELD_ACCESS);
                
        accept(JavaNTS.PATTERN, JavaNTS.TYPE_PATTERN);

        accept(JavaNTS.TYPE_PATTERN, JavaNTS.LOCAL_VARIABLE_DECLARATION);

        //Productions from section 15(Expressions):
        branch(JavaNTS.PRIMARY,
                JavaNTS.PRIMARY_NO_NEW_ARRAY,
                JavaNTS.ARRAY_CREATION_EXPRESSION);

        accept(JavaNTS.PRIMARY_NO_NEW_ARRAY, JavaNTS.LITERAL);
        accept(JavaNTS.PRIMARY_NO_NEW_ARRAY, JavaNTS.CLASS_LITERAL);
        accept(JavaNTS.PRIMARY_NO_NEW_ARRAY, JavaTS.THIS);
        accept(JavaNTS.PRIMARY_NO_NEW_ARRAY, JavaNTS.TYPE_NAME, JavaTS.PERIOD, JavaTS.THIS);
        accept(JavaNTS.PRIMARY_NO_NEW_ARRAY, JavaTS.LEFT_ROUND_BRACKET, JavaNTS.EXPRESSION, JavaTS.RIGHT_ROUND_BRACKET);
        accept(JavaNTS.PRIMARY_NO_NEW_ARRAY, JavaNTS.CLASS_INSTANCE_CREATION_EXPRESSION);
        accept(JavaNTS.PRIMARY_NO_NEW_ARRAY, JavaNTS.FIELD_ACCESS);
        accept(JavaNTS.PRIMARY_NO_NEW_ARRAY, JavaNTS.ARRAY_ACCESS);
        accept(JavaNTS.PRIMARY_NO_NEW_ARRAY, JavaNTS.METHOD_INVOCATION);
        accept(JavaNTS.PRIMARY_NO_NEW_ARRAY, JavaNTS.METHOD_REFERENCE);

        accept(JavaNTS.CLASS_LITERAL, JavaNTS.TYPE_NAME, repeatOf(JavaTS.LEFT_SQUARE_BRACKET, JavaTS.RIGHT_SQUARE_BRACKET), JavaTS.PERIOD, JavaTS.CLASS);
        accept(JavaNTS.CLASS_LITERAL, JavaNTS.NUMERIC_TYPE, repeatOf(JavaTS.LEFT_SQUARE_BRACKET, JavaTS.RIGHT_SQUARE_BRACKET), JavaTS.PERIOD, JavaTS.CLASS);
        accept(JavaNTS.CLASS_LITERAL, JavaTS.BOOLEAN, repeatOf(JavaTS.LEFT_SQUARE_BRACKET, JavaTS.RIGHT_SQUARE_BRACKET), JavaTS.PERIOD, JavaTS.CLASS);
        accept(JavaNTS.CLASS_LITERAL, JavaTS.VOID, JavaTS.PERIOD, JavaTS.CLASS);

        accept(JavaNTS.CLASS_INSTANCE_CREATION_EXPRESSION, JavaNTS.UNQUALIFIED_CLASS_INSTANCE_CREATION_EXPRESSION);
        accept(JavaNTS.CLASS_INSTANCE_CREATION_EXPRESSION, JavaNTS.EXPRESSION_NAME, JavaTS.PERIOD, JavaNTS.UNQUALIFIED_CLASS_INSTANCE_CREATION_EXPRESSION);
        accept(JavaNTS.CLASS_INSTANCE_CREATION_EXPRESSION, JavaNTS.PRIMARY, JavaTS.PERIOD, JavaNTS.UNQUALIFIED_CLASS_INSTANCE_CREATION_EXPRESSION);

        accept(JavaNTS.UNQUALIFIED_CLASS_INSTANCE_CREATION_EXPRESSION, JavaTS.NEW, optional(JavaNTS.TYPE_ARGUMENTS), JavaNTS.CLASS_OR_INTERFACE_TYPE_TO_INSTANTIATE, JavaTS.LEFT_ROUND_BRACKET, optional(JavaNTS.ARGUMENT_LIST), JavaTS.RIGHT_ROUND_BRACKET, optional(JavaNTS.CLASS_BODY));

        accept(JavaNTS.CLASS_OR_INTERFACE_TYPE_TO_INSTANTIATE, repeatOf(JavaNTS.ANNOTATION), JavaNTS.IDENTIFIER, repeatOf(JavaTS.PERIOD, repeatOf(JavaNTS.ANNOTATION), JavaNTS.IDENTIFIER), optional(JavaNTS.TYPE_ARGUMENTS_OR_DIAMOND));

        accept(JavaNTS.TYPE_ARGUMENTS_OR_DIAMOND, JavaNTS.TYPE_ARGUMENTS);
        accept(JavaNTS.TYPE_ARGUMENTS_OR_DIAMOND, JavaTS.LESS_THAN, JavaTS.GREATER_THAN);

        accept(JavaNTS.FIELD_ACCESS, JavaNTS.PRIMARY, JavaTS.PERIOD, JavaNTS.IDENTIFIER);
        accept(JavaNTS.FIELD_ACCESS, JavaTS.SUPER, JavaTS.PERIOD, JavaNTS.IDENTIFIER);
        accept(JavaNTS.FIELD_ACCESS, JavaNTS.TYPE_NAME, JavaTS.PERIOD, JavaTS.SUPER, JavaTS.PERIOD, JavaNTS.IDENTIFIER);

        accept(JavaNTS.ARRAY_ACCESS, JavaNTS.EXPRESSION_NAME, JavaTS.LEFT_SQUARE_BRACKET, JavaNTS.EXPRESSION, JavaTS.RIGHT_SQUARE_BRACKET);
        accept(JavaNTS.ARRAY_ACCESS, JavaNTS.PRIMARY_NO_NEW_ARRAY, JavaTS.LEFT_SQUARE_BRACKET, JavaNTS.EXPRESSION, JavaTS.RIGHT_SQUARE_BRACKET);

        accept(JavaNTS.METHOD_INVOCATION, JavaNTS.METHOD_NAME, JavaTS.LEFT_ROUND_BRACKET, optional(JavaNTS.ARGUMENT_LIST), JavaTS.RIGHT_ROUND_BRACKET);
        accept(JavaNTS.METHOD_INVOCATION, JavaNTS.TYPE_NAME, JavaTS.PERIOD, optional(JavaNTS.TYPE_ARGUMENTS), JavaNTS.IDENTIFIER, JavaTS.LEFT_ROUND_BRACKET, optional(JavaNTS.ARGUMENT_LIST), JavaTS.RIGHT_ROUND_BRACKET);
        accept(JavaNTS.METHOD_INVOCATION, JavaNTS.EXPRESSION_NAME, JavaTS.PERIOD, optional(JavaNTS.TYPE_ARGUMENTS), JavaNTS.IDENTIFIER, JavaTS.LEFT_ROUND_BRACKET, optional(JavaNTS.ARGUMENT_LIST), JavaTS.RIGHT_ROUND_BRACKET);
        accept(JavaNTS.METHOD_INVOCATION, JavaNTS.PRIMARY, JavaTS.PERIOD, optional(JavaNTS.TYPE_ARGUMENTS), JavaNTS.IDENTIFIER, JavaTS.LEFT_ROUND_BRACKET, optional(JavaNTS.ARGUMENT_LIST), JavaTS.RIGHT_ROUND_BRACKET);
        accept(JavaNTS.METHOD_INVOCATION, JavaTS.SUPER, JavaTS.PERIOD, optional(JavaNTS.TYPE_ARGUMENTS), JavaNTS.IDENTIFIER, JavaTS.LEFT_ROUND_BRACKET, optional(JavaNTS.ARGUMENT_LIST), JavaTS.RIGHT_ROUND_BRACKET);
        accept(JavaNTS.METHOD_INVOCATION, JavaNTS.TYPE_NAME, JavaTS.PERIOD, JavaTS.SUPER, JavaTS.PERIOD, optional(JavaNTS.TYPE_ARGUMENTS), JavaNTS.IDENTIFIER, JavaTS.LEFT_ROUND_BRACKET, optional(JavaNTS.ARGUMENT_LIST), JavaTS.RIGHT_ROUND_BRACKET);

        accept(JavaNTS.ARGUMENT_LIST, JavaNTS.EXPRESSION, repeatOf(JavaTS.COMMA, JavaNTS.EXPRESSION));

        accept(JavaNTS.METHOD_REFERENCE, JavaNTS.EXPRESSION_NAME, JavaTS.DOUBLE_COLON, optional(JavaNTS.TYPE_ARGUMENTS), JavaNTS.IDENTIFIER);
        accept(JavaNTS.METHOD_REFERENCE, JavaNTS.PRIMARY, JavaTS.DOUBLE_COLON, optional(JavaNTS.TYPE_ARGUMENTS), JavaNTS.IDENTIFIER);
        accept(JavaNTS.METHOD_REFERENCE, JavaNTS.REFERENCE_TYPE, JavaTS.DOUBLE_COLON, optional(JavaNTS.TYPE_ARGUMENTS), JavaNTS.IDENTIFIER);
        accept(JavaNTS.METHOD_REFERENCE, JavaTS.SUPER, JavaTS.DOUBLE_COLON, optional(JavaNTS.TYPE_ARGUMENTS), JavaNTS.IDENTIFIER);
        accept(JavaNTS.METHOD_REFERENCE, JavaNTS.TYPE_NAME, JavaTS.PERIOD, JavaTS.SUPER, JavaTS.DOUBLE_COLON, optional(JavaNTS.TYPE_ARGUMENTS), JavaNTS.IDENTIFIER);
        accept(JavaNTS.METHOD_REFERENCE, JavaNTS.CLASS_TYPE, JavaTS.DOUBLE_COLON, optional(JavaNTS.TYPE_ARGUMENTS), JavaTS.NEW);
        accept(JavaNTS.METHOD_REFERENCE, JavaNTS.ARRAY_TYPE, JavaTS.DOUBLE_COLON, JavaTS.NEW);

        accept(JavaNTS.ARRAY_CREATION_EXPRESSION, JavaTS.NEW, JavaNTS.PRIMITIVE_TYPE, JavaNTS.DIM_EXPRS, optional(JavaNTS.DIMS));
        accept(JavaNTS.ARRAY_CREATION_EXPRESSION, JavaTS.NEW, JavaNTS.CLASS_OR_INTERFACE_TYPE, JavaNTS.DIM_EXPRS, optional(JavaNTS.DIMS));
        accept(JavaNTS.ARRAY_CREATION_EXPRESSION, JavaTS.NEW, JavaNTS.PRIMITIVE_TYPE, JavaNTS.DIMS, JavaNTS.ARRAY_INITIALIZER);
        accept(JavaNTS.ARRAY_CREATION_EXPRESSION, JavaTS.NEW, JavaNTS.CLASS_OR_INTERFACE_TYPE, JavaNTS.DIMS, JavaNTS.ARRAY_INITIALIZER);

        accept(JavaNTS.DIM_EXPRS, JavaNTS.DIM_EXPR, repeatOf(JavaNTS.DIM_EXPR));
        
        accept(JavaNTS.DIM_EXPR, repeatOf(JavaNTS.ANNOTATION), JavaTS.LEFT_SQUARE_BRACKET, JavaNTS.EXPRESSION, JavaTS.RIGHT_SQUARE_BRACKET);

        branch(JavaNTS.EXPRESSION,
                JavaNTS.LAMBDA_EXPRESSION,
                JavaNTS.ASSIGNMENT_EXPRESSION);

        accept(JavaNTS.LAMBDA_EXPRESSION, JavaNTS.LAMBDA_PARAMETERS, JavaTS.ARROW, JavaNTS.LAMBDA_BODY);

        accept(JavaNTS.LAMBDA_PARAMETERS, JavaTS.LEFT_ROUND_BRACKET, optional(JavaNTS.LAMBDA_PARAMETER_LIST), JavaTS.RIGHT_ROUND_BRACKET);
        accept(JavaNTS.LAMBDA_PARAMETERS, JavaNTS.IDENTIFIER);

        accept(JavaNTS.LAMBDA_PARAMETER_LIST, JavaNTS.LAMBDA_PARAMETER, repeatOf(JavaTS.COMMA, JavaNTS.LAMBDA_PARAMETER));
        accept(JavaNTS.LAMBDA_PARAMETER_LIST, JavaNTS.IDENTIFIER, repeatOf(JavaTS.COMMA, JavaNTS.IDENTIFIER));

        accept(JavaNTS.LAMBDA_PARAMETER, repeatOf(JavaNTS.VARIABLE_MODIFIER), JavaNTS.LAMBDA_PARAMETER_TYPE, JavaNTS.VARIABLE_DECLARATOR_ID);
        accept(JavaNTS.VARIABLE_ARITY_PARAMETER);

        branch(JavaNTS.LAMBDA_PARAMETER_TYPE,
                JavaNTS.UNANN_TYPE,
                JavaTS.VAR);
        
        branch(JavaNTS.LAMBDA_BODY,
                JavaNTS.EXPRESSION,
                JavaNTS.BLOCK);

        branch(JavaNTS.ASSIGNMENT_EXPRESSION,
                JavaNTS.CONDITIONAL_EXPRESSION,
                JavaNTS.ASSIGNMENT);
        
        accept(JavaNTS.ASSIGNMENT, JavaNTS.LEFT_HAND_SIDE, JavaNTS.ASSIGNMENT_OPERATOR, JavaNTS.EXPRESSION);
        
        branch(JavaNTS.LEFT_HAND_SIDE,
                JavaNTS.EXPRESSION_NAME,
                JavaNTS.FIELD_ACCESS,
                JavaNTS.ARRAY_ACCESS);
        
        branch(JavaNTS.ASSIGNMENT_OPERATOR,
                JavaTS.SIMPLE_ASSIGNMENT,
                JavaTS.ASSIGNMENT_BY_PRODUCT,
                JavaTS.ASSIGNMENT_BY_QUOTIENT,
                JavaTS.ASSIGNMENT_BY_REMINDER,
                JavaTS.ASSIGNMENT_BY_SUM,
                JavaTS.ASSIGNMENT_BY_DIFFERENCE,
                JavaTS.ASSIGNMENT_BY_BITWISE_LEFT_SHIFT,
                JavaTS.ASSIGNMENT_BY_SIGNED_BITWISE_RIGHT_SHIFT,
                JavaTS.ASSIGNMENT_BY_UNSIGNED_BITWISE_RIGHT_SHIFT,
                JavaTS.ASSIGNMENT_BY_BITWISE_AND,
                JavaTS.ASSIGNMENT_BY_BITWISE_XOR,
                JavaTS.ASSIGNMENT_BY_BITWISE_OR);
        
        accept(JavaNTS.CONDITIONAL_EXPRESSION, JavaNTS.CONDITIONAL_OR_EXPRESSION);
        accept(JavaNTS.CONDITIONAL_EXPRESSION, JavaNTS.CONDITIONAL_OR_EXPRESSION, JavaTS.QUESTION, JavaNTS.EXPRESSION, JavaTS.COLON, JavaNTS.CONDITIONAL_EXPRESSION);
        accept(JavaNTS.CONDITIONAL_EXPRESSION, JavaNTS.CONDITIONAL_OR_EXPRESSION, JavaTS.QUESTION, JavaNTS.EXPRESSION, JavaTS.COLON, JavaNTS.LAMBDA_EXPRESSION);
        
        accept(JavaNTS.CONDITIONAL_OR_EXPRESSION, JavaNTS.CONDITIONAL_AND_EXPRESSION);
        accept(JavaNTS.CONDITIONAL_OR_EXPRESSION, JavaNTS.CONDITIONAL_OR_EXPRESSION, JavaTS.LOGICAL_CONDITIONAL_OR, JavaNTS.CONDITIONAL_AND_EXPRESSION);

        accept(JavaNTS.CONDITIONAL_AND_EXPRESSION, JavaNTS.INCLUSIVE_OR_EXPRESSION);
        accept(JavaNTS.CONDITIONAL_AND_EXPRESSION, JavaNTS.CONDITIONAL_AND_EXPRESSION, JavaTS.LOGICAL_CONDITIONAL_AND, JavaNTS.INCLUSIVE_OR_EXPRESSION);

        accept(JavaNTS.INCLUSIVE_OR_EXPRESSION, JavaNTS.EXCLUSIVE_OR_EXPRESSION);
        accept(JavaNTS.INCLUSIVE_OR_EXPRESSION, JavaNTS.INCLUSIVE_OR_EXPRESSION, JavaTS.VERTICAL_BAR, JavaNTS.EXCLUSIVE_OR_EXPRESSION);

        accept(JavaNTS.EXCLUSIVE_OR_EXPRESSION, JavaNTS.AND_EXPRESSION);
        accept(JavaNTS.EXCLUSIVE_OR_EXPRESSION, JavaNTS.EXCLUSIVE_OR_EXPRESSION, JavaTS.BITWISE_AND_LOGICAL_XOR, JavaNTS.AND_EXPRESSION);

        accept(JavaNTS.AND_EXPRESSION, JavaNTS.EQUALITY_EXPRESSION);
        accept(JavaNTS.AND_EXPRESSION, JavaNTS.AND_EXPRESSION, JavaTS.AND, JavaNTS.EQUALITY_EXPRESSION);

        accept(JavaNTS.EQUALITY_EXPRESSION, JavaNTS.RELATIONAL_EXPRESSION);
        accept(JavaNTS.EQUALITY_EXPRESSION, JavaNTS.EQUALITY_EXPRESSION, JavaTS.EQUAL_TO, JavaNTS.RELATIONAL_EXPRESSION);
        accept(JavaNTS.EQUALITY_EXPRESSION, JavaNTS.EQUALITY_EXPRESSION, JavaTS.NOT_EQUAL_TO, JavaNTS.RELATIONAL_EXPRESSION);

        accept(JavaNTS.RELATIONAL_EXPRESSION, JavaNTS.SHIFT_EXPRESSION);
        accept(JavaNTS.RELATIONAL_EXPRESSION, JavaNTS.RELATIONAL_EXPRESSION, JavaTS.LESS_THAN, JavaNTS.SHIFT_EXPRESSION);
        accept(JavaNTS.RELATIONAL_EXPRESSION, JavaNTS.RELATIONAL_EXPRESSION, JavaTS.GREATER_THAN, JavaNTS.SHIFT_EXPRESSION);
        accept(JavaNTS.RELATIONAL_EXPRESSION, JavaNTS.RELATIONAL_EXPRESSION, JavaTS.LESS_THAN_OR_EQUAL_TO, JavaNTS.SHIFT_EXPRESSION);
        accept(JavaNTS.RELATIONAL_EXPRESSION, JavaNTS.RELATIONAL_EXPRESSION, JavaTS.GRATER_THAN_OR_EQUAL_TO, JavaNTS.SHIFT_EXPRESSION);
        accept(JavaNTS.RELATIONAL_EXPRESSION, JavaNTS.INSTANCE_OF_EXPRESSION);

        accept(JavaNTS.INSTANCE_OF_EXPRESSION, JavaNTS.RELATIONAL_EXPRESSION, JavaTS.INSTANCEOF, JavaNTS.REFERENCE_TYPE);
        accept(JavaNTS.INSTANCE_OF_EXPRESSION, JavaNTS.RELATIONAL_EXPRESSION, JavaTS.INSTANCEOF, JavaNTS.PATTERN);

        accept(JavaNTS.SHIFT_EXPRESSION, JavaNTS.ADDITIVE_EXPRESSION);
        accept(JavaNTS.SHIFT_EXPRESSION, JavaNTS.SHIFT_EXPRESSION, JavaTS.BITWISE_LEFT_SHIFT, JavaNTS.ADDITIVE_EXPRESSION);
        accept(JavaNTS.SHIFT_EXPRESSION, JavaNTS.SHIFT_EXPRESSION, JavaTS.BITWISE_SIGNED_RIGHT_SHIFT, JavaNTS.ADDITIVE_EXPRESSION);
        accept(JavaNTS.SHIFT_EXPRESSION, JavaNTS.SHIFT_EXPRESSION, JavaTS.BITWISE_UNSIGNED_RIGHT_SHIFT, JavaNTS.ADDITIVE_EXPRESSION);

        accept(JavaNTS.ADDITIVE_EXPRESSION, JavaNTS.MULTIPLICATIVE_EXPRESSION);
        accept(JavaNTS.ADDITIVE_EXPRESSION, JavaNTS.ADDITIVE_EXPRESSION, JavaTS.PLUS, JavaNTS.MULTIPLICATIVE_EXPRESSION);
        accept(JavaNTS.ADDITIVE_EXPRESSION, JavaNTS.ADDITIVE_EXPRESSION, JavaTS.MINUS, JavaNTS.MULTIPLICATIVE_EXPRESSION);

        accept(JavaNTS.MULTIPLICATIVE_EXPRESSION, JavaNTS.UNARY_EXPRESSION);
        accept(JavaNTS.MULTIPLICATIVE_EXPRESSION, JavaNTS.MULTIPLICATIVE_EXPRESSION, JavaTS.ASTERISK, JavaNTS.UNARY_EXPRESSION);
        accept(JavaNTS.MULTIPLICATIVE_EXPRESSION, JavaNTS.MULTIPLICATIVE_EXPRESSION, JavaTS.DIVISION, JavaNTS.UNARY_EXPRESSION);
        accept(JavaNTS.MULTIPLICATIVE_EXPRESSION, JavaNTS.MULTIPLICATIVE_EXPRESSION, JavaTS.MODULO, JavaNTS.UNARY_EXPRESSION);

        accept(JavaNTS.UNARY_EXPRESSION, JavaNTS.PRE_INCREMENT_EXPRESSION);
        accept(JavaNTS.UNARY_EXPRESSION, JavaNTS.PRE_DECREMENT_EXPRESSION);
        accept(JavaNTS.UNARY_EXPRESSION, JavaTS.PLUS, JavaNTS.UNARY_EXPRESSION);
        accept(JavaNTS.UNARY_EXPRESSION, JavaTS.MINUS, JavaNTS.UNARY_EXPRESSION);
        accept(JavaNTS.UNARY_EXPRESSION, JavaNTS.UNARY_EXPRESSION_NOT_PLUS_MINUS);

        accept(JavaNTS.PRE_INCREMENT_EXPRESSION, JavaTS.INCREMENT, JavaNTS.UNARY_EXPRESSION);

        accept(JavaNTS.PRE_DECREMENT_EXPRESSION, JavaTS.DECREMENT, JavaNTS.UNARY_EXPRESSION);

        accept(JavaNTS.UNARY_EXPRESSION_NOT_PLUS_MINUS, JavaNTS.POSTFIX_EXPRESSION);
        accept(JavaNTS.UNARY_EXPRESSION_NOT_PLUS_MINUS, JavaTS.BITWISE_NOT, JavaNTS.UNARY_EXPRESSION);
        accept(JavaNTS.UNARY_EXPRESSION_NOT_PLUS_MINUS, JavaTS.LOGICAL_NEGATION, JavaNTS.UNARY_EXPRESSION);
        accept(JavaNTS.UNARY_EXPRESSION_NOT_PLUS_MINUS, JavaNTS.CAST_EXPRESSION);
        accept(JavaNTS.UNARY_EXPRESSION_NOT_PLUS_MINUS, JavaNTS.SWITCH_EXPRESSION);

        branch(JavaNTS.POSTFIX_EXPRESSION,
                JavaNTS.PRIMARY,
                JavaNTS.EXPRESSION_NAME,
                JavaNTS.POST_INCREMENT_EXPRESSION,
                JavaNTS.POST_DECREMENT_EXPRESSION);
        
        accept(JavaNTS.POST_INCREMENT_EXPRESSION, JavaNTS.POSTFIX_EXPRESSION, JavaTS.INCREMENT);

        accept(JavaNTS.POST_DECREMENT_EXPRESSION, JavaNTS.POSTFIX_EXPRESSION, JavaTS.DECREMENT);

        accept(JavaNTS.CAST_EXPRESSION, JavaTS.LEFT_ROUND_BRACKET, JavaNTS.PRIMITIVE_TYPE, JavaTS.RIGHT_ROUND_BRACKET, JavaNTS.UNARY_EXPRESSION);
        accept(JavaNTS.CAST_EXPRESSION, JavaTS.LEFT_ROUND_BRACKET, JavaNTS.REFERENCE_TYPE, repeatOf(JavaNTS.ADDITIONAL_BOUND), JavaTS.RIGHT_ROUND_BRACKET, JavaNTS.UNARY_EXPRESSION_NOT_PLUS_MINUS);
        accept(JavaNTS.CAST_EXPRESSION, JavaTS.LEFT_ROUND_BRACKET, JavaNTS.REFERENCE_TYPE, repeatOf(JavaNTS.ADDITIONAL_BOUND), JavaTS.RIGHT_ROUND_BRACKET, JavaNTS.LAMBDA_EXPRESSION);

        accept(JavaNTS.SWITCH_EXPRESSION, JavaTS.SWITCH, JavaTS.LEFT_ROUND_BRACKET, JavaNTS.EXPRESSION, JavaTS.RIGHT_ROUND_BRACKET, JavaNTS.SWITCH_BLOCK);

        accept(JavaNTS.CONSTANT_EXPRESSION, JavaNTS.EXPRESSION);

    }

    /**
     * This method introduces a production.
     * An invocation <code> accept(N, S)</code> results in:
     * <li> N -> S </li>
     */

    private final void accept(final Symbol left, final Symbol... right){
        productions.add(new Production(left, new SymbolSequence(right)));
    }

    /**
     * This method introduces a set of productions.
     * An invocation <code> branch(N, S, T, U,...)</code> results in these productions:
     * <li> N -> S </li>
     * <li> N -> T </li>
     * <li> N -> U </li>
     * <li> ... </li>
     */

    private final void branch(final Symbol left, final Symbol... symbols){
        for(Symbol symbol : symbols){
            accept(left, symbol);
        }
    }

    /**
     * This method introduces a new nonterminal symbol <code>[S]</code> in the specification.
     * The symbol <code>[S]</code> results in either:
     * <li> [S] -> S</li>
     * <li> [S] -> ε(empty)</li>
     * 
     * @param right S in the caption.
     * @return Symbol [S] in the caption.
     */

    private final ImplicitNTS optional(final Symbol... right){
        ImplicitNTS s = new ImplicitNTS(Brackets.DOUBLE_SQUARE_BRACKETS, right);
        for(ImplicitNTS symbol : implicitSymbolList){
            if(symbol.equals(s)){
                return symbol;
            }
        }
        implicitSymbolList.add(s);
        accept(s);
        accept(s, right);
        return s;
    }

    /**
     * This method creates a new nonterminal symbol <code>{S}</code> in the specification.
     * The symbol <code>{S}</code> results in either:
     * <li>{S} -> S{S}</li>
     * <li>{S} -> ε(empty)</li>
     * 
     * @param right S in the caption.
     * @return Symbol {S} in the caption.
     */

    private final ImplicitNTS repeatOf(final Symbol... right){
        ImplicitNTS s = new ImplicitNTS(Brackets.DOUBLE_CURLY_BRACKETS, right);
        for(ImplicitNTS symbol : implicitSymbolList){
            if(symbol.equals(s)){
                return symbol;
            }
        }
        implicitSymbolList.add(s);
        accept(s);

        Symbol[] concat = new Symbol[right.length + 1];
        concat[0] = s;
        for(int i = 0; i < right.length; i++){
            concat[i+1] = right[i];
        }
        

        accept(s, concat);
        return s;
    }

    @Override
    public HashSet<Production> productions(){
        return productions;
    }

    @Override
    public Symbol topSymbol(){
        return JavaNTS.COMPILATION_UNIT; 
    }
    
    @Override
    public HashSet<Symbol> nonTerminalSymbols(){
        var set = new HashSet<Symbol>(Arrays.asList(JavaNTS.values()));
        set.addAll(implicitSymbolList);
        return set;
    }
    
    @Override
    public HashSet<Symbol> terminalSymbols() {
        var set = new HashSet<Symbol>(Arrays.asList(JavaTS.values()));
        set.add(Symbol.EOF);
        return set;
    }
    
    @Override
    public HashSet<Symbol> finalSymbols(){
        return new HashSet<>(Arrays.asList(Symbol.EOF));
    }

}
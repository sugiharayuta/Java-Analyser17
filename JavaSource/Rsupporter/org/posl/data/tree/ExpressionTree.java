package org.posl.data.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.test.Tested;
import org.posl.test.Tested.Status;

import static org.posl.data.tree.ExpressionNameTree.convertToExpression;

/**
 * A tree node used as the base class for the different types of
 * expressions.
 *
 * @jls 15 Expressions
 *
 * @author me
 */

@Tested(date = "2022/7/8", tester = "me", confidence = Status.PROBABLY_OK)
public interface ExpressionTree extends CaseConstantTree{

    static final HashSet<JavaTS> ASSIGNMENT_TOKENS = new HashSet<>(Arrays.asList(
        JavaTS.SIMPLE_ASSIGNMENT, JavaTS.ASSIGNMENT_BY_PRODUCT, JavaTS.ASSIGNMENT_BY_QUOTIENT, JavaTS.ASSIGNMENT_BY_REMINDER, 
        JavaTS.ASSIGNMENT_BY_SUM, JavaTS.ASSIGNMENT_BY_DIFFERENCE, JavaTS.ASSIGNMENT_BY_BITWISE_LEFT_SHIFT, JavaTS.ASSIGNMENT_BY_SIGNED_BITWISE_RIGHT_SHIFT,
        JavaTS.ASSIGNMENT_BY_UNSIGNED_BITWISE_RIGHT_SHIFT, JavaTS.ASSIGNMENT_BY_BITWISE_AND, JavaTS.ASSIGNMENT_BY_BITWISE_XOR, JavaTS.ASSIGNMENT_BY_BITWISE_OR));

    static ExpressionTree resolveConditionalExpression(JavaTokenManager src) throws ParsingException{
        return resolveConditionalExpression(BinaryTree.BinaryOperator.CONDITIONAL_OR.getBinaryExpression(src), src);
    }

    static ExpressionTree resolveConditionalExpression(ExpressionTree condition, JavaTokenManager src) throws ParsingException{
        ExpressionTree conditionalOrExpr = condition;
        if(src.match(JavaTS.QUESTION)){
            return ConditionalExpressionTree.parse(conditionalOrExpr, src);
        }else{
            return conditionalOrExpr;
        }
    }

    static ExpressionTree parse(JavaTokenManager src) throws ParsingException{
        if(followsLambdaExpression(src)){
            return LambdaExpressionTree.parse(src);
        }
        ExpressionTree expr = resolveConditionalExpression(src);
        if(src.match(ASSIGNMENT_TOKENS)){
            if(expr instanceof ExpressionNameTree || expr instanceof MemberSelectTree || expr instanceof ArrayAccessTree){
                JavaTS operator = src.read().resolution;
                if(operator == JavaTS.SIMPLE_ASSIGNMENT){
                    return new AssignmentTree(expr, parse(src));
                }
                return new CompoundAssignmentTree(expr, operator, parse(src));
            }
            throw new ParsingException("The left-hand side of an assignment must be a variable.");
        }
        return expr;
    }

    static boolean followsLambdaExpression(JavaTokenManager src) throws ParsingException{
        return (src.match(JavaTS.LEFT_ROUND_BRACKET) && Tree.lookAhead(src, LookAheadMode.INSIDE_BRACKETS) == JavaTS.ARROW)
            || (src.match(IDENTIFIERS) && src.match(1, JavaTS.ARROW));
    }

    static ExpressionTree resolvePrimary(JavaTokenManager src) throws ParsingException{
        ExpressionTree expr = switch(Tree.lookAhead(src, LookAheadMode.TYPE)){
            case PERIOD -> {
                TypeTree qualifier = NameTree.resolveTypeOrName(src);
                if(src.match(1, JavaTS.CLASS)){
                    yield ClassLiteralTree.parse(qualifier, src);
                }else{
                    src.skip(JavaTS.PERIOD);
                    yield resolveQualifiablePrimary(qualifier, src);
                }
            }
            case DOUBLE_COLON -> MemberReferenceTree.parse(NameTree.resolveTypeOrName(src), src);
            case LEFT_SQUARE_BRACKET -> ArrayAccessTree.parse(ExpressionNameTree.parse(src), src);
            case LEFT_ROUND_BRACKET -> MethodInvocationTree.parse(ExpressionNameTree.parse(src), src);
            case END_OF_FILE -> {
                yield switch(src.lookAhead().resolution){
                    case VOID -> ClassLiteralTree.parse(VoidTree.parse(src), src);
                    case LEFT_ROUND_BRACKET -> ParenthesizedTree.parse(src);
                    case LESS_THAN -> MethodInvocationTree.parse(ExpressionNameTree.EMPTY, Tree.resolveTypeArguments(src), src);
                    default -> {
                        if(src.match(LITERAL_TOKENS)){
                            yield LiteralTree.parse(src);
                        }
                        yield resolveQualifiablePrimary(ExpressionNameTree.EMPTY, src);
                    }
                };
            }
            default -> ExpressionNameTree.parse(src);
        };

        OUTER: while(true){
            switch(src.lookAhead().resolution){
                case PERIOD -> {
                    src.skip(JavaTS.PERIOD);
                    expr = switch(src.lookAhead().resolution){
                        case NEW -> resolveTypeCreation(expr, src);
                        case LESS_THAN -> MethodInvocationTree.parse(expr, Tree.resolveTypeArguments(src), src);
                        case SUPER -> {
                            ExpressionTree e = SuperTree.parse(expr, src);
                            if(src.match(JavaTS.LEFT_ROUND_BRACKET)){
                                yield MethodInvocationTree.parse(e, src);
                            }
                            yield e;
                        }
                        default -> {
                            if(src.match(IDENTIFIERS)){
                                MemberSelectTree e = MemberSelectTree.parse(expr, src);
                                if(src.match(JavaTS.LEFT_ROUND_BRACKET)){
                                    yield MethodInvocationTree.parse(e, src);
                                }
                                yield e;
                            }
                            throw new IllegalTokenException(src.lookAhead(), "field access or method invocation");
                        }
                    };
                }
                case DOUBLE_COLON -> expr = MemberReferenceTree.parse(expr, src);
                case LEFT_SQUARE_BRACKET -> expr = ArrayAccessTree.parse(expr, src);
                default -> {break OUTER;}
            }                
        }
        return expr;
    }
    
    static ExpressionTree resolveQualifiablePrimary(Tree qualifier, JavaTokenManager src) throws ParsingException{
        return switch(src.lookAhead().resolution){
            case NEW -> resolveTypeCreation(qualifier, src);
            case SUPER -> {
                ExpressionTree enclosingExpr = SuperTree.parse(qualifier, src);
                yield switch(src.lookAhead().resolution){
                    case PERIOD -> {
                        src.skip(JavaTS.PERIOD);
                        if(src.match(1, JavaTS.LEFT_ROUND_BRACKET) || src.match(JavaTS.LESS_THAN)){
                            yield MethodInvocationTree.parse(enclosingExpr, Tree.resolveTypeArguments(src), src);
                        }
                        yield MemberSelectTree.parse(enclosingExpr, src);
                    }
                    case DOUBLE_COLON -> MemberReferenceTree.parse(enclosingExpr, src);
                    case LEFT_ROUND_BRACKET -> MethodInvocationTree.parse(enclosingExpr, src);
                    default ->  throw new IllegalTokenException(src.lookAhead(), "member reference or selection");
                };
            }
            case THIS -> {
                ExpressionTree expr = ThisTree.parse(qualifier, src);
                if(src.match(JavaTS.LEFT_ROUND_BRACKET)){
                    yield MethodInvocationTree.parse(expr, src);
                }
                yield expr;
            }
            case LESS_THAN -> MethodInvocationTree.parse(qualifier, Tree.resolveTypeArguments(src), src);
            default -> {
                if(qualifier != ExpressionNameTree.EMPTY){
                    yield convertToExpression(qualifier);
                }else{
                    throw new IllegalTokenException(src.lookAhead(), "primary expression");
                }
            }
        };
    }

    static ExpressionTree resolveTypeCreation(Tree qualifier, JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.NEW);
        ArrayList<TypeTree> typeArguments = Tree.resolveTypeArguments(src);
        TypeTree type = NameTree.resolveNonArrayTypeOrName(src);
        return switch(src.lookAhead().resolution){
            case LEFT_SQUARE_BRACKET, AT_SIGN -> {
                if(qualifier != ExpressionNameTree.EMPTY){
                    throw new ParsingException("Array creation expression cannot be qualified.");
                }
                yield NewArrayTree.parse(type, src);
            }
            case LEFT_ROUND_BRACKET -> NewClassTree.parse(convertToExpression(qualifier), typeArguments, type, src);
            default -> throw new IllegalTokenException(src.lookAhead(), "dimensions or arguments");
        };
    }

    static ArrayList<ExpressionTree> resolveArguments(JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.LEFT_ROUND_BRACKET);
        ArrayList<ExpressionTree> arguments = Tree.getList(ExpressionTree::parse, JavaTS.RIGHT_ROUND_BRACKET, src);
        src.skip(JavaTS.RIGHT_ROUND_BRACKET);
        return arguments;
    }

}

package org.posl.data.tree;

import java.util.ArrayList;

import org.posl.data.resolution.Accessor;

/**
 * A tree node for a method invocation expression.
 *
 * For example:
 * <pre>
 *   <em>identifier</em> ( <em>arguments</em> )
 *
 *   this . <em>typeArguments</em> <em>identifier</em> ( <em>arguments</em> )
 * </pre>
 *
 * @jls 15.12 Method Invocation Expressions
 *
 * @author me
 */

public final class MethodInvocationTree implements ExpressionTree, Accessor{

    private final ArrayList<TypeTree> typeArguments;
    private final ExpressionTree methodSelect;
    private final ArrayList<ExpressionTree> arguments;
    private MethodTree resolution = null;

    public MethodInvocationTree(ArrayList<TypeTree> typeArguments, ExpressionTree methodSelect, ArrayList<ExpressionTree> arguments){
        this.typeArguments = typeArguments;
        this.methodSelect = methodSelect;
        this.arguments = arguments;
    }

    static MethodInvocationTree parse(ExpressionTree methodSelect, JavaTokenManager src) throws ParsingException{
        return new MethodInvocationTree(new ArrayList<>(), methodSelect, ExpressionTree.resolveArguments(src));
    }

    static MethodInvocationTree parse(Tree qualifier, ArrayList<TypeTree> typeArguments, JavaTokenManager src) throws ParsingException{
        ExpressionTree methodSelect = switch(src.lookAhead().resolution){
            case THIS -> ThisTree.parse(qualifier, src);
            case SUPER -> SuperTree.parse(qualifier, src);
            default -> {
                if(qualifier instanceof ExpressionNameTree e){
                    yield ExpressionNameTree.parse(e, src);
                }else if(qualifier instanceof NameTree n){
                    yield ExpressionNameTree.parse(n, src);
                }else if(qualifier instanceof ExpressionTree e){
                    yield MemberSelectTree.parse(e, src);
                }else{
                    throw new ParsingException("Illegal form of method invocation.");
                }
            }
        };
        return new MethodInvocationTree(typeArguments, methodSelect, ExpressionTree.resolveArguments(src));
    }

    public ArrayList<TypeTree> typeArguments(){
        return typeArguments;
    }

    public ExpressionTree methodSelect(){
        return methodSelect;
    }

    public ArrayList<ExpressionTree> arguments(){
        return arguments;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof final MethodInvocationTree m){
            return typeArguments.equals(m.typeArguments) && methodSelect.equals(m.methodSelect) && arguments.equals(m.arguments);
        }else{
            return false;
        }
    }
    
    @Override
    public String toString(){
        return String.format("MethodInvocationTree[typeArguments=%s, methodSelect=%s, arguments=%s]", 
                                typeArguments.toString(), methodSelect.toString(), arguments.toString());
    }

    @Override
    public int hashCode(){
        return typeArguments.hashCode() * 961 + methodSelect.hashCode() * 31 + arguments.hashCode();
    }

    @Override
    public MethodTree resolution(){
        return resolution;
    }
}

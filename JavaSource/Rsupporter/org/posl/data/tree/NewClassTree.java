package org.posl.data.tree;

import java.util.ArrayList;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.data.resolution.Accessor;
import org.posl.test.Tested;
import org.posl.test.Tested.Status;

/**
 * A tree node to declare a new instance of a class.
 *
 * For example:
 * <pre>
 *   new <em>identifier</em> ( )
 *
 *   new <em>identifier</em> ( <em>arguments</em> )
 *
 *   new <em>typeArguments</em> <em>identifier</em> ( <em>arguments</em> )
 *       <em>classBody</em>
 *
 *   <em>enclosingExpression</em>.new <em>identifier</em> ( <em>arguments</em> )
 * </pre>
 *
 * @jls 15.9 Class Instance Creation Expressions
 *
 * @author me
 */

@Tested(date = "2022/7/8", tester = "me", confidence = Status.PROBABLY_OK)
public class NewClassTree implements ExpressionTree, Accessor{
    
    private final ExpressionTree enclosingExpression;
    private final ArrayList<TypeTree> typeArguments;
    private final TypeTree createdClass;
    private final ArrayList<ExpressionTree> arguments;
    private final ClassTree classBody;
    private MethodTree resolution;

    public NewClassTree(ExpressionTree enclosingExpression, ArrayList<TypeTree> typeArguments, TypeTree createdClass, ArrayList<ExpressionTree> arguments, ClassTree classBody){
        this.enclosingExpression = enclosingExpression;
        this.typeArguments = typeArguments;
        this.createdClass = createdClass;
        this.arguments = arguments;
        this.classBody = classBody;
    }

    static NewClassTree parse(ExpressionTree enclosingExpression, ArrayList<TypeTree> typeArguments, TypeTree createdClass, JavaTokenManager src) throws ParsingException{
        return new NewClassTree(enclosingExpression, typeArguments, createdClass, ExpressionTree.resolveArguments(src), 
            src.match(JavaTS.LEFT_CURLY_BRACKET)? ClassTree.parse(createdClass, src) : null);
    }

    public boolean equals(Object o){
        if(o instanceof final NewClassTree n){
            return enclosingExpression.equals(n.enclosingExpression) && typeArguments.equals(n.typeArguments)
                    && createdClass.equals(n.createdClass) && arguments.equals(n.arguments) &&
                    ((classBody == null)? n.classBody == null : classBody.equals(n.classBody));
        }
        return false;
    }

    public String toString(){
        return String.format("NewClassTree[enclosingExpression=%s, typeArguments=%s, createdClass=%s, arguments=%s, classBody=%s]",
                            enclosingExpression.toString(), typeArguments.toString(), createdClass.toString(), arguments.toString(),
                            (classBody == null)? "null" : classBody.toString());
    }

    public int hashCode(){
        int hash = enclosingExpression.hashCode();
        hash = hash * 31 + typeArguments.hashCode();
        hash = hash * 31 + createdClass.hashCode();
        hash = hash * 31 + arguments.hashCode();
        return hash * 31 + ((classBody == null)? 0 : classBody.hashCode());
    }

    @Override
    public MethodTree resolution() throws ParsingException{
        return resolution;
    }

}
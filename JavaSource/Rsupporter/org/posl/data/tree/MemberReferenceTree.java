package org.posl.data.tree;

import java.util.ArrayList;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.data.resolution.Accessor;
import org.posl.test.Tested;
import org.posl.test.Tested.Status;

/**
 * A tree node for a member reference expression.
 *
 * For example:
 * <pre>
 *   <em>expression</em> :: <em>[ identifier | new ]</em>
 * </pre>
 *
 * @author me
 */

@Tested(date = "2022/7/8", tester = "me", confidence = Status.PROBABLY_OK)
public class MemberReferenceTree implements ExpressionTree, Accessor{

    private final Tree qualifierExpression;
    private final ArrayList<TypeTree> typeArguments;
    private final IdentifierTree methodName;
    private final ReferenceMode mode;

    private MethodTree resolution;

    /**
     * There are two kinds of member references: (i) method references and
     * (ii) constructor references
     */
    public enum ReferenceMode{
        /** enum constant for method references. */
        INVOKE,
        /** enum constant for constructor references. */
        NEW
    }

    public MemberReferenceTree(Tree qualifierExpression, ArrayList<TypeTree> typeArguments, IdentifierTree methodName, ReferenceMode mode){
        this.qualifierExpression = qualifierExpression;
        this.typeArguments = typeArguments;
        this.methodName = methodName;
        this.mode = mode;
    }



    static MemberReferenceTree parse(Tree qualifierExpression, JavaTokenManager src) throws ParsingException{
        ArrayList<TypeTree> typeArguments = new ArrayList<>();
        src.skip(JavaTS.DOUBLE_COLON);
        if(src.match(JavaTS.LESS_THAN)){
            typeArguments = Tree.resolveTypeArguments(src);
        }
        if(src.match(JavaTS.NEW)){
            src.skip(JavaTS.NEW);
            return new MemberReferenceTree(qualifierExpression, typeArguments, IdentifierTree.EMPTY, ReferenceMode.NEW);
        }
        return new MemberReferenceTree(qualifierExpression, typeArguments, IdentifierTree.parse(src), ReferenceMode.INVOKE);
    }

    public Tree qualifierExpression(){
        return qualifierExpression;
    }

    public ArrayList<TypeTree> typeArguments(){
        return typeArguments;
    }

    public IdentifierTree methodName(){
        return methodName;
    }

    public ReferenceMode mode(){
        return mode;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof final MemberReferenceTree r){
            return qualifierExpression.equals(r.qualifierExpression) && typeArguments.equals(r.typeArguments)
                    && methodName.equals(r.methodName) && mode == r.mode;
        }else{
            return false;
        }
    }
    
    @Override
    public String toString(){
        return String.format("MemberReferenceTree[qualifierExpression=%s, typeArguments=%s, methodName=%s, mode=%s]", 
                                qualifierExpression.toString(), typeArguments.toString(), methodName.toString(), mode.toString());
    }

    @Override
    public int hashCode(){
        int hash = qualifierExpression.hashCode();
        hash = hash * 31 + typeArguments.hashCode();
        hash = hash * 31 + methodName.hashCode();
        return hash * 31 + mode.hashCode();
    }

    @Override
    public MethodTree resolution(){
        return resolution;
    }

}

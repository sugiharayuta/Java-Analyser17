package org.posl.data.resolution;

import java.util.ArrayList;
import java.util.HashMap;

import org.posl.data.tree.IdentifierTree;
import org.posl.data.tree.LabeledStatementTree;
import org.posl.data.tree.MethodTree;
import org.posl.data.tree.ParsingException;
import org.posl.data.tree.Tree;

public abstract class Scope{
    
    private final Tree target;
    final HashMap<IdentifierTree, TypeIdentifier> localInnerTypes = new HashMap<>();
    final HashMap<IdentifierTree, ExpressionIdentifier> localInnerVariables = new HashMap<>();

    public Scope(Tree target){
        this.target = target;
    }

    public TypeIdentifier searchType(IdentifierTree id) throws ParsingException{

    }

    public ExpressionIdentifier searchExpression(IdentifierTree id) throws ParsingException{

    }

    public MethodTree searchMethod(IdentifierTree id, ArrayList<Type> argumentTypes) throws ParsingException{

    }

    public LabeledStatementTree searchLabel(IdentifierTree id) throws ParsingException{
        if(target instanceof LabeledStatementTree l && l.simpleName().equals(id)){
            return l;
        }else if(target instanceof MethodTree){
            throw new ParsingException(String.format("Label \"%s\" cannot be resolved.", id.toString()));
        }
        return null;
    }

}

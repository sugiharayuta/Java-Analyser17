package org.posl.data.resolution;

import java.util.ArrayList;

import org.posl.data.tree.IdentifierTree;
import org.posl.data.tree.LabeledStatementTree;
import org.posl.data.tree.MethodTree;
import org.posl.data.tree.ParsingException;
import org.posl.util.SimpleStack;
import org.posl.util.functions.CEFunction;

public abstract class TreeTracker{
    
    public final SimpleStack<Scope> trace = new SimpleStack<>();

    public TypeIdentifier searchType(IdentifierTree id) throws ParsingException{
        return search(s -> s.searchType(id));
    }

    public ExpressionIdentifier searchExpression(IdentifierTree id) throws ParsingException{
        return search(s -> s.searchExpression(id));
    }

    public MethodTree searchMethod(IdentifierTree id, ArrayList<Type> argumentTypes) throws ParsingException{
        return search(s -> s.searchMethod(id, argumentTypes));
    }

    public LabeledStatementTree searchLabel(IdentifierTree id) throws ParsingException{
        return search(s -> s.searchLabel(id));
    }

    public <T> T search(CEFunction<Scope, T> getter) throws ParsingException{
        var traceCpy = trace.clone();
        T ret;
        while(!traceCpy.isEmpty()){
            ret = getter.apply(traceCpy.pop());
            if(ret != null){
                return ret;
            }
        }
        throw new ParsingException("Failed to resolve an identifier.");
    }

}

package org.posl.compiler.deprecated;


import org.posl.compiler.syntax.Production;
import org.posl.data.tree.ParsingException;
import org.posl.util.functions.CEConsumer;

public record Reduce(Production p, CEConsumer<Production> reduction) implements Action{
    
    @Override
    public boolean act() throws ParsingException{
        reduction.accept(p);
        return false;
    }

}

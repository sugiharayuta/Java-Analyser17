package org.posl.compiler.deprecated;

import org.posl.data.tree.ParsingException;
import org.posl.util.functions.CEConsumer;

public record Shift(int s, CEConsumer<Integer> shift) implements Action{
    
    @Override
    public boolean act() throws ParsingException{
        shift.accept(s);
        return false;
    }

    @Override
    public String toString(){
        return "s" + s;
    }
    
}

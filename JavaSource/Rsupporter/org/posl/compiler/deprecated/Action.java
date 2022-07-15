package org.posl.compiler.deprecated;

import org.posl.data.tree.ParsingException;

public interface Action{

    public boolean act() throws ParsingException;
    
}

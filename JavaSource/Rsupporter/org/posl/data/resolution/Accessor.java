package org.posl.data.resolution;

import org.posl.data.tree.ParsingException;

public interface Accessor{
    
    Accessible resolution() throws ParsingException;

}

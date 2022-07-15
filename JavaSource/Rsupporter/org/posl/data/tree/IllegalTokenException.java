package org.posl.data.tree;

import org.posl.data.tokens.Token;

public class IllegalTokenException extends ParsingException{

    public IllegalTokenException(Token err, String expected){
        super(String.format("Illegal token \"%s\", expected %s.", err.text, expected));
    }
}

package org.posl.compiler;

import org.posl.compiler.syntax.Symbol;
import org.posl.data.tree.ParsingException;

public class IllegalSymbolException extends ParsingException{

    public IllegalSymbolException(String message){
        super(message);
    }

    public IllegalSymbolException(Symbol s){
        this("Illegal symbol : "+s.toString());
    }
}

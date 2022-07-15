package org.posl.data.resolution;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.data.tokens.Token;
import org.posl.data.tree.IllegalTokenException;

public enum PrimitiveType{
    BOOLEAN(JavaTS.BOOLEAN),
    BYTE(JavaTS.BYTE),
    SHORT(JavaTS.SHORT),
    CHAR(JavaTS.CHAR),
    INT(JavaTS.INT),
    LONG(JavaTS.LONG),
    FLOAT(JavaTS.FLOAT),
    DOUBLE(JavaTS.DOUBLE);

    private JavaTS symbol;

    private PrimitiveType(JavaTS symbol){
        this.symbol = symbol;
    }

    public static PrimitiveType get(Token t) throws IllegalTokenException{
        for(PrimitiveType p : PrimitiveType.values()){
            if(p.symbol == t.resolution){
                return p;
            }
        }
        throw new IllegalTokenException(t, "primitive type");
    }
}

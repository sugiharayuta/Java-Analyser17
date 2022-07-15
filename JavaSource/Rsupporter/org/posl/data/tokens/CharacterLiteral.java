package org.posl.data.tokens;

import org.posl.compiler.syntax.javalang.JavaTS;

public class CharacterLiteral extends Literal{

    public CharacterLiteral(String text, Reference ref) {
        super(text, ref, JavaTS.CHARACTER_LITERAL);
    }

    @Override
    public String toString(){
        return "\'" + text + "\'";
    }
    
}

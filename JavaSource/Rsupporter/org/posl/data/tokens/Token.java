package org.posl.data.tokens;

import org.posl.compiler.syntax.javalang.JavaTS;

public class Token extends InputElement{
    public static final Token EOF = new Token("$", Reference.NULL);

    public final JavaTS resolution;

    public Token(String text, Reference ref){
        super(text, ref);
        this.resolution = JavaTS.set(text);
    }

    public Token(String text, Reference ref, JavaTS resolution){
        super(text, ref);
        this.resolution = resolution;
    }
    
    @Override
    public String info(){
        return "#" + toString() + "# (" +resolution.name()+ ")" + ref.toString();
    }
}

package org.posl.data.tokens;

public class Keyword extends Token{

    public Keyword(String text, Reference ref) {
        super(text, ref);
    }

    @Override
    public String info(){
        return "#" + toString() + "# (Keyword : " +resolution.name()+ ")" + ref.toString();
    }
    
}

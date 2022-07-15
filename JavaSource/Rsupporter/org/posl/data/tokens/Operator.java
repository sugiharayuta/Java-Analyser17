package org.posl.data.tokens;

public class Operator extends Token{

    public Operator(String text, Reference ref) {
        super(text, ref);
    }

    @Override
    public String info(){
        return "#" + toString() + "# (Operator : " +resolution.name()+ ")" + ref.toString();
    }

}

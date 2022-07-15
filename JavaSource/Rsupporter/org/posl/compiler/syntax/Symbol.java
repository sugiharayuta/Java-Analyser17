package org.posl.compiler.syntax;

public interface Symbol{

    public static final Symbol GOAL = new ImplicitNTS(null, null);
    public static final Symbol EOF = new ImplicitTS("$EOF$");

    @Override
    public int hashCode();

    public String key();

    public default boolean implies(Symbol s){
        return this.equals(s);
    }
    
}

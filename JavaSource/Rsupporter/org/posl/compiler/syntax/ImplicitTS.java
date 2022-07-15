package org.posl.compiler.syntax;

public record ImplicitTS(String key)implements Symbol{

    @Override
    public String toString(){
        return key;
    }

    @Override
    public int hashCode(){
        return toString().hashCode();
    }

}

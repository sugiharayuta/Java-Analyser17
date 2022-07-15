package org.posl.compiler.syntax;

import org.posl.util.Brackets;

public record ImplicitNTS(Brackets form, Symbol... result) implements Symbol{
    
    @Override
    public String toString(){
        if(this == GOAL){
            return "$start$";
        }
        String s = "$";
        int i = 0;
        while(true){
            s += result[i++].toString();
            if(i == result.length) break;
            s += " ";
        }
        return form.addBrackets(s) + "$";
    }

    @Override
    public String key(){
        return toString();
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof ImplicitNTS s){
            if(s.form == form && s.result.length == result.length){
                for(int i = 0; i < result.length; i++){
                    if(!s.result[i].equals(result[i])){
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode(){
        if(this == GOAL){
            return 0;
        }
        int hash = form.hashCode();
        for(Symbol s : result){
            hash = hash * 31 + s.hashCode();
        }
        return hash;
    }
}
package org.posl.compiler.deprecated;

public record Accept() implements Action{

    @Override
    public boolean act(){
        return true;
    }
    
}

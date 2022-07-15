package org.posl.analyzer;

import org.posl.data.tree.FatalParserError;

public class Main{

    public static void main(String[] args){
        throw new FatalParserError("Fuck you.");
    }
    
}
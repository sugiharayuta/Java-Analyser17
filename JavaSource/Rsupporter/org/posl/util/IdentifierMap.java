package org.posl.util;

import java.util.HashMap;

import org.posl.data.tree.ParsingException;
import org.posl.util.functions.CEPredicate;

public class IdentifierMap<V> extends HashMap<String, V>{

    public IdentifierMap<V> select(CEPredicate<V> filter) throws ParsingException{
        var map = new IdentifierMap<V>();
        for(var entry : entrySet()){
            if(filter.test(entry.getValue())){
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }

}

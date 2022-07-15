package org.posl.compiler;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class SetMap<K, E> extends HashMap<K, HashSet<E>>{

    public HashSet<E> getSet(K key){
        HashSet<E> set = get(key);
        if(set == null){
            set = new HashSet<E>();
            put((K)key, set);
            return set;
        }else{
            return set;
        }
    }

    public boolean register(K key, E e){
        return getSet(key).add(e);
    }

    public boolean registerAll(K key, Collection<? extends E> c){
        return getSet(key).addAll(c);
    }

    public boolean merge(SetMap<K, ? extends E> map){
        boolean updated = false;
        for(var e : map.entrySet()){
            updated |= registerAll(e.getKey(), e.getValue());
        }
        return updated;
    }

}
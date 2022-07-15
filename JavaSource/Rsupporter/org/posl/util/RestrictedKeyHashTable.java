package org.posl.util;

import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.Predicate;

public class RestrictedKeyHashTable<K, V>{
    final int readdressor;
    final K[] keyArray;
    final V[] valueArray;

    protected RestrictedKeyHashTable(final Set<K> keys, final IntFunction<K[]> keyArrayInit, final IntFunction<V[]> valueArrayInit){
        int p = keys.size()/12+1;
        this.keyArray = keyArrayInit.apply(16*p);
        this.valueArray = valueArrayInit.apply(16*p);
        this.readdressor = (p%2 == 0)? p+1 : p+2;
        for(K key : keys){
            keyArray[getAddress(key, k -> k == null)] = key;
        }
    }

    private int getAddress(K key, Predicate<K> cond){
        int addr = Math.floorMod(key.hashCode(), keyArray.length);
        while(!cond.test(keyArray[addr])){
            addr = (addr+readdressor)%keyArray.length;
        }
        return addr;
    }

    public void put(K key, V value){
        int addr = getAddress(key, k -> k != null && k.equals(key));
        valueArray[addr] = value;
    }

    public V get(K key){
        return valueArray[getAddress(key, k -> k != null && k.equals(key))];
    }

}

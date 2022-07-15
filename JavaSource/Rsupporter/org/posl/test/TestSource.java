package org.posl.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

/**
 * This class is prepared fot testing.
 */

public class TestSource {

    public static void main(String[] args){
        int a = 0;
        long b = 05_567l;
        float c = 226.77e-12f;
        double d = 54.;

        //end in line comment
        var l1 = new ArrayList<>(Arrays.asList(a, b, c, d));

        /* 
        traditional comment
        */

        int e = 0b111_000_1;
        byte f = 3_3;
        float g = 0f;
        double h = .3d;
        @SuppressWarnings(value = "unused")
        float i = 0xa_5.67p-35f;
        @SuppressWarnings("unused")
        double j = 0x3_fp3D;

        /*
        //comments do not nest
        /*
        */

        var l2 = new ArrayList<>(Arrays.asList(e, f, g, h));

        double p = (a=6) + b - (d++-f---5)+(g += ++h)+(f <= 3? 7 : 66f);

        createCouples(l1);

        System.out.println(l2.toString());
        System.out.println(p);

        String s1 = "sad\077\7dd\n\b\r334";
        String s2 = """  
             A man can be destroyed,
             but not defeted.""";
        
        cha\u0072 u = '\\';
        @SuppressWarnings("unused")
        String fakeComment = "/* This must not be gotten as comment. */";
        System.out.println(s1 + s2 + u);
    }

    public static <E, F, T extends Tuple<E, F>> List<T> getCartesianProduct(List<E> l1, List<F> l2, BiFunction<E, F, T> constructor){
        List<T> returnList = new ArrayList<>();
        for(int i = 0; i < l1.size() - 1; i++){
            for(int j = i+1; j < l2.size(); j++){
                returnList.add(constructor.apply(l1.get(i), l2.get(j)));
            }
        }
        return returnList;
    }

    
    public static <E> List<Pair<E>> createCouples(List<E> list){
        List<Pair<E>> returnList = getCartesianProduct(list, list, Pair::new);
        returnList.removeIf(pair -> pair.e1.equals(pair.e2));
        return returnList;
    }

    @Deprecated
    public static <E> void cleanList(List<E> list){
        for(int i = 0; i < list.size(); i++){
            E checking = list.get(i);
            for(int j = i+1; j < list.size();){
                if(checking.equals(list.get(j))){
                    list.remove(j);
                }else{
                    j++;
                }
            }
        }
    }

    public static sealed class Tuple<T, U> permits Pair<List<Number>>{
        final T e1;
        final U e2;

        public Tuple(T e1, U e2){
            this.e1 = e1;
            this.e2 = e2;
        }
    }

    private static non-sealed class Pair<T> extends Tuple<T, T>{

        public Pair(T e1, T e2) {
            super(e1, e2);
        }

    }

    public @interface A{

    }

}

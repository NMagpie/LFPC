package com;

import java.util.*;

public class Parsing extends Actions {

    public static void parse(String s) {
        int index=0;
        Pair pair = new Pair();
        ArrayList<Integer> pairvalue = new ArrayList<>();
        if ((s.charAt(index)=='*')||(s.charAt(index)=='-')) {
            index++; while (s.charAt(index)!='-') index++;
            pair.setValue(Integer.parseInt(s.substring(1,index))); if (s.charAt(0)=='-') start=pair.getValue();
            else finish.add(pair.getValue());
        } else { index++; while (s.charAt(index)!='-') index++;
            pair.setValue(Integer.parseInt(s.substring(0,index))); }
        index++;
        pair.setCh(s.charAt(index));
        concat(setchar, pair.getCh());
        index++;
        pairvalue.add(Integer.parseInt(s.substring(index)));

        if (!table.containsKey(pair)) { table.put(pair,pairvalue); } else {
            table.put(pair,concat(table.get(pair),pairvalue) );
        }
    }
}

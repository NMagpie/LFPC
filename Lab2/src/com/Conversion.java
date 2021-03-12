package com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Conversion extends Actions {

    Map<ArrayList<Integer>,Integer> reverse = new HashMap<>();

    Conversion(ArrayList<Integer> finish,Map<Pair, ArrayList<Integer>> table,int start,ArrayList<Character>setchar) {
        Actions.finish =finish;
        Actions.table =table;
        Actions.start =start;
        Actions.setchar=setchar;
    }

    Conversion() {

    }

    public void converse() {
        int max = getmax();
        int iter=0;
        ArrayList<Integer> temp;

        while (iter<=max-1) {
            for (int i=0;i< setchar.size();i++) {
            Pair nowpair = new Pair(iter, setchar.get(i));
            if ((table.containsKey(nowpair))&&(table.get(nowpair).size() > 1)) {


                temp = table.get(nowpair);

                for (char ch : setchar) {
                    ArrayList<Integer> newarr = new ArrayList<>();
                    for (int index : temp) {
                        if (finish.contains(index)) { concat(finish,max); }
                        concat(newarr, table.get(new Pair(index, ch)));
                    }
                    table.put(new Pair(max, ch), newarr);
                }


                reverse.put(temp, max);

                for (Pair pairch : table.keySet()) {
                    if (table.get(pairch).equals(temp)) {
                        int finalMax = max;
                        table.put(pairch, new ArrayList<Integer>() {{
                            add(finalMax);
                        }});
                    }
                }
                max++;
            }
        }
            iter++;
    }

        for (Pair pair: table.keySet()) {
            if (reverse.containsKey(table.get(pair)))
                table.put(pair,new ArrayList<Integer>(){{add(reverse.get(table.get(pair)));}});
        }

    }

}

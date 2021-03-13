package com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Conversion extends Actions {

    Map<ArrayList<Integer>,Integer> reverse = new HashMap<>();
    //this map need just to rename in future nodes in the right way

    Conversion(ArrayList<Integer> finish,Map<Pair, ArrayList<Integer>> table,int start,ArrayList<Character>setchar) {
        Actions.finish =finish;
        Actions.table =table;
        Actions.start =start;
        Actions.setchar=setchar;
    }

    public void converse() {
        //the main conversion method it just find the array which length is more than one
        //and then it creates new node for it where puts all the transitions from the node that were included in
        //this array, after it renames all the mentions of this node (that is why we need 'reverse' map) after not
        //finding any array with length more than one the method stops with ready DFA as map and as table for output
        //it was really hell to write this peace of sh-(crossed out) code.
        int max = getmax();
        int c=1;
        while (c!=0) { c=0;
        for (char ch: setchar){
            if (table.containsKey(new Pair(max,ch))) {c++;}
        }
        max++;
        }
        int iter=0;
        ArrayList<Integer> temp;

        while (iter<=max-1) {
            for (int i=0;i< setchar.size();i++) {
            Pair nowpair = new Pair(iter, setchar.get(i));
            if ((table.containsKey(nowpair))&&(table.get(nowpair).size() > 1)) {

                temp = table.get(nowpair);

                while (c!=0) { c=0;
                    for (char ch: setchar){
                        if (table.containsKey(new Pair(max,ch))) c++;
                    }
                    max++;
                }

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

        ArrayList<Integer> removing = new ArrayList<>();

        for (Pair pair:table.keySet()) {
            concat(removing,table.get(pair));
        }

        ArrayList<Integer> allKeys = getAllKeys();

        for (int key:allKeys) {
            if ((!removing.contains(key))&&(key!=start)) for (char ch:setchar) { table.remove(new Pair(key,ch)); }
        }

    }

}

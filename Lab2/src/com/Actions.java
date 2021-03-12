package com;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class Actions {
    static ArrayList<Integer> finish = new ArrayList<>();
    static Map<Pair, ArrayList<Integer>> table = new HashMap<>();
    static int start;
    static ArrayList<Character> setchar = new ArrayList<>();

    static public ArrayList<Integer> concat(ArrayList<Integer> one, ArrayList<Integer> two) {
        if (!(two==null)) {
        for (int index:two) { if (!one.contains(index)) {one.add(index);} }
        Collections.sort(one);}
        return one;
    }
    static public ArrayList<Integer> concat(ArrayList<Integer> one, int two) {
            if (!one.contains(two)) {one.add(two);}
            Collections.sort(one);
        return one;
    }

    static public ArrayList<Character> concat(ArrayList<Character> one, char two) {
        if (!one.contains(two)) {one.add(two);}
        Collections.sort(one);
        return one;
    }

    public static void printable() {
        table.forEach((k,v) -> System.out.println("<"+k.getValue()+","+k.getCh()+">="+" "+v));
    }

    /*public static int getmax() {
        int max=0;

        for (Pair pair:table.keySet()) {
                if (pair.getValue()>max) { max= pair.getValue(); }
        }

        max++;

        return max;
    } */

    public int getmax() {
        ArrayList<Integer> max = new ArrayList<>();

        for (Pair pair:table.keySet()) {
            concat(max,table.get(pair));
        }

        return max.size();
    }

    public void print() {
        int max = getmax();
        Conversion c = new Conversion();
        if (!this.getClass().equals(c.getClass())) max--;
        System.out.print("  ");
        for (int i=0;i< setchar.size();i++) System.out.print(setchar.get(i)+"  ");
        System.out.println();
        for (int i=0;i< setchar.size()*2+3;i++) System.out.print("_");
        System.out.println();
        for (int i=0;i<=max;i++){
            System.out.print(i+"|");
            for (int j=0;j< setchar.size();j++) {
                Pair pair = new Pair(i, setchar.get(j));
                if ((!(table.get(pair)==null))&&(table.get(pair).size()>0))
                    System.out.print(table.get(pair).get(0)+"| "); else System.out.print("-| ");
            }
            System.out.println();
        }

    }

    /*public void change(ArrayList<Integer> finish,Map<Pair, ArrayList<Integer>> table,int start) {
        this.finish=finish;
        this.table=table;
        this.start=start;
    } */
}

package com;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class Actions { //this class represent general data and methods between Parsing and Conversion
    static ArrayList<Integer> finish = new ArrayList<>(); //represents terminal nodes
    static Map<Pair, ArrayList<Integer>> table = new HashMap<>(); //represents all the transitions
    static int start; //starting node
    static ArrayList<Character> setchar = new ArrayList<>(); //set of all terminal symbols

    //method "concat" is not concatination, it just adds to the array the symbols that it does not have
    //so this arraylist have to contain only unique values. Also, there is this method for chars and only one int

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

    public static void printable() { //printing the map of transitions
        table.forEach((k,v) -> System.out.println("<"+k.getValue()+","+k.getCh()+">="+" "+v));
    }

    public int getmax() { //getting all the indexes of nodes
        ArrayList<Integer> max = new ArrayList<>();

        for (Pair pair:table.keySet()) {
            concat(max,table.get(pair));
        }

        return max.size();
    }

    public static ArrayList<Integer> getAllKeys() { //getting all the pairs from the map
        ArrayList<Integer> keys = new ArrayList<>();

        for (Pair pair: table.keySet()) {
            concat(keys, pair.getValue());
        }

        return keys;
    }

    public void print() { //printing all the transitions as table
        ArrayList<Integer> keys = getAllKeys();
        System.out.print("\t");
        for (Character value : setchar) System.out.print(value + "\t");
        System.out.println();
        for (int i=0;i< setchar.size()+1;i++) System.out.print("___");
        System.out.println();
        for (Integer key : keys) {

            if (key == start) {
                System.out.print("-");
            } else if (finish.contains(key)) {
                System.out.print("*");
            } else System.out.print(" ");

            System.out.print(key + "|\t");

            for (Character character : setchar) {
                Pair pair = new Pair(key, character);
                if ((!(table.get(pair) == null)) && (table.get(pair).size() > 0))
                    System.out.print(table.get(pair).get(0) + "|\t");
                else System.out.print("-|\t");
            }
            System.out.println();
        }
    }

    /*    public void print() {
        int max = getmax();
        Conversion c = new Conversion();
        if (!this.getClass().equals(c.getClass())) max--;
        System.out.print("   ");
        for (int i=0;i< setchar.size();i++) System.out.print(setchar.get(i)+"  ");
        System.out.println();
        for (int i=0;i< setchar.size()+1;i++) System.out.print("___");
        System.out.println();
        for (int i=0;i<=max;i++){
            if (i==start) { System.out.print("-"); } else if (finish.contains(i)) { System.out.print("*"); }
            else System.out.print(" ");
            System.out.print(i+"|");
            for (int j=0;j< setchar.size();j++) {
                Pair pair = new Pair(i, setchar.get(j));
                if ((!(table.get(pair)==null))&&(table.get(pair).size()>0))
                    System.out.print(table.get(pair).get(0)+"| "); else System.out.print("-| ");
            }
            System.out.println();
        }

    }*/
}

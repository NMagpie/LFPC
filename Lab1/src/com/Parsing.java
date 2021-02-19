package com;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Parsing { //I put all the grammar into the HashMap. character from the rule from the left
    // I put as a key and value is an array of strings that are all the right parts of the rules where this
    //character met from the left side. Example: S-aB, S-a => S={"aB","a"}

    Map<Character, ArrayList> grammar =new HashMap();
    ArrayList<String> rule = new ArrayList<>();
    String s;
    boolean ch;

    FileReader fr;

    {
        try {
            fr = new FileReader("input.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    Scanner sc= new Scanner(fr);

    public boolean check(String s){
        //method "check" is checking if the rule is written right or not (according to the type 3 grammar)
        if (s.length()<3) { return true; }
        if ((s.charAt(0)<'A')||(s.charAt(0)>'Z')) {
            return true;
        }
        if (s.charAt(1)!='-') {

            return true;
        }
        if ((s.charAt(2)<'a')||(s.charAt(2)>'z')) {
            return true;
        }
        if (s.length()==4) {
            return (s.charAt(3) < 'A') || (s.charAt(3) > 'Z');
        }
        return false;
    }

    public void parse() { //parse is putting the rule into the HashMap if it is written right
        // The rules' are inputing until there will be word "end"
        String s;

        while (true) {
            if (!sc.hasNext()) { System.out.println("ERROR! Type \"end\" in the end of list of rules"); return; }

            s = sc.nextLine();

            if (s.equals("end")) { return; }

            if (check(s)) { System.out.println("PARSING ERROR");
                return;
            }

            char c = s.charAt(0);
            s = s.substring(2);

            if (grammar.containsKey(c)) {
                rule = grammar.get(c);
            }
            rule.add(s);
            grammar.put(c, (ArrayList) rule.clone());
            rule.removeAll(rule);
        }
    }

    public void printgr() {  //method of printing all of the inputed grammar
        System.out.println(grammar);
    }

    public void comp() throws IOException { //Checking if the string can be generated using this grammar

        while (true) {
            if (!sc.hasNext()) { System.out.println("ERROR! Type \"end\" in the end of list of strings"); return; }

            s=sc.nextLine();
            if (s.equals("end")) {fr.close(); return;}
            ch=false;

            compBase("",'S',0);

            if (ch) { System.out.println(s+" Accepted"); } else { System.out.println(s+" Rejected"); }

        }
    }

    public void compBase(String str,char key,int index) { //the main algorithm of checking of the
        // string using recursion (too much time to explain
        // i can do it on the lesson, if you want)
            if (index>=s.length()) { return;}
            if (!grammar.containsKey(key)) { return;}

            rule = grammar.get(key);

        for (String rulestr : rule) {

            if (rulestr.charAt(0) == s.charAt(index)) {
                str = str + s.charAt(index);

                if (rulestr.length() == 2) {
                    compBase(str, rulestr.charAt(1), index + 1);
                } else if (str.equals(s)) {
                    ch=true;
                    return;
                } else return;
            }
        }
    }

}
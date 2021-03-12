package com;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class Main { //input is by file "input.txt". Every rule is written in given form (int)-(char)(int),
    // where first integer shows, which is the index of the node, char - terminal symbol and second integer
    // is where reffers this node. Also, you should write "-" before starting node (you have to do it at least one time)
    // or "*" if it is terminal node. if node does not reffers to any other node just write (int)-eps

    static FileReader fr;

    static {
        try {
            fr = new FileReader("input.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static Scanner sc = new Scanner(fr);

    public static void main(String[] args) {

        String s;

        Parsing p = new Parsing();

        while (sc.hasNext()) {
            s= sc.nextLine(); p.parse(s); }

        //Parsing.printable();

        //p.print();

        Conversion conversion = new Conversion(Parsing.finish,Parsing.table,Parsing.start,Parsing.setchar);

        conversion.converse();

        //Conversion.printable();

        conversion.print();

    }
}

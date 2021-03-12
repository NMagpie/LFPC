package com;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class Main {

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

        p.print();

        Conversion conversion = new Conversion(Parsing.finish,Parsing.table,Parsing.start,Parsing.setchar);

        conversion.converse();

        System.out.println("\n");

        //Conversion.printable();

        conversion.print();

    }
}

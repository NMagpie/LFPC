package com;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

    //ask about cycles in the grammar
    //some grammars have two "e"'s, what does it mean?

public class SimplePrecedence {

    private static final TreeSet<Character> allSymbols = new TreeSet<>();
    private static final HashMap<Character, HashSet<Character>> first = new HashMap<>();
    private static final HashMap<Character, HashSet<Character>> last = new HashMap<>();
    private static final HashMap<String, Character> reverseGrammar = new HashMap<>();
    static HashMap<Character, HashSet<String>> grammar = new HashMap<>();
    private static char[][] matrix;
    private static Scanner sc;
    private static Graph graph;
    private static ArrayList<Integer> ids;
    private static int newId;
    private static int edgeId;

    private static void putIntoGrammar(char Key, String RightHandSide) {
        if (grammar.containsKey(Key)) grammar.get(Key).add(RightHandSide);
        else grammar.put(Key, new HashSet<>(Collections.singleton(RightHandSide)));
    }

    private static void parsing() throws FileNotFoundException {
        FileReader fr = new FileReader("input0.txt");

        sc = new Scanner(fr);

        String s;

        while ((sc.hasNextLine()) && (!(s = sc.nextLine()).equals("end"))) {
            if (s.matches("[A-Z]-[^<>=]+")) {
                for (int i = 0; i < s.length(); i++)
                    if (i != 1) allSymbols.add(s.charAt(i));
                putIntoGrammar(s.charAt(0), s.substring(2));
                reverseGrammar.put(s.substring(2), s.charAt(0));
            } else {
                System.out.println("The incorrect input of the rules! Also, grammar cannot contain symbols like: \"<\", \">\" or \"=\".");
                System.exit(0);
            }
        }
        if (!sc.hasNextLine()) {
            System.out.println("Input is incorrect! after introducing grammar type \"end\" and then strings you want to parse!");
            System.exit(0);
        }

    }

    public static void main() { //the main method of the class
        try {
            parsing();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        matrix = new char[allSymbols.size()][allSymbols.size()];

        for (char[] row : matrix)
            Arrays.fill(row, ' ');

        setFirst('S');
        setLast('S');
        for (char Key : grammar.keySet()) {
            setFirst(Key);
            setLast(Key);
        }

        matrixBuilding();

        /*System.out.println("Grammar "+grammar);
        System.out.println("allSymbols "+allSymbols);
        showMatrix();
        System.out.println(reverseGrammar);*/

        System.out.println("First "+first);
        System.out.println("Last "+last);

        while (sc.hasNextLine())
            transformingInput(sc.nextLine());

        sc.close();

        System.exit(0);

    }

    private static HashSet<Character> setFirst(char C) {
        HashSet<Character> set = new HashSet<>();
        if (!first.containsKey(C)) {
            HashSet<String> RHS = grammar.get(C);
            for (String s : RHS) {
                char toAdd = s.charAt(0);
                set.add(toAdd);
                if ((toAdd >= 'A') && (toAdd <= 'Z'))
                    if ((toAdd != C) || (!set.contains(toAdd))) set.addAll(setFirst(toAdd));
            }
            first.put(C, set);
        } else set = first.get(C);
        return set;
    }

    private static HashSet<Character> setLast(char C) {
        HashSet<Character> set = new HashSet<>();
        if (!last.containsKey(C)) {
            HashSet<String> RHS = grammar.get(C);
            for (String s : RHS) {
                char toAdd = s.charAt(s.length() - 1);
                set.add(toAdd);
                if ((toAdd >= 'A') && (toAdd <= 'Z'))
                    if ((toAdd != C) || (!set.contains(toAdd))) set.addAll(setLast(toAdd));
            }
            last.put(C, set);
        } else set = last.get(C);
        return set;
    }

    private static void matrixBuilding() {
        for (HashSet<String> RHSet : grammar.values())
            for (String RHS : RHSet)
                for (int i = 0; i < RHS.length() - 1; i++) {

                    char firstSymbol = RHS.charAt(i);
                    char secondSymbol = RHS.charAt(i + 1);
                    matrixPut(firstSymbol, secondSymbol, '=');

                    if (first.containsKey(secondSymbol))
                        for (char secondSymbolFirst : first.get(secondSymbol))
                            matrixPut(firstSymbol, secondSymbolFirst, '<');

                    if (last.containsKey(firstSymbol))
                        if (first.containsKey(secondSymbol)) {
                            for (char firstSymbolLast : last.get(firstSymbol))
                                for (char secondSymbolFirst : first.get(secondSymbol))
                                    if (secondSymbolFirst < 'A' || secondSymbolFirst > 'Z')
                                        matrixPut(firstSymbolLast, secondSymbolFirst, '>');
                        } else {
                            for (char firstSymbolLast : last.get(firstSymbol))
                                matrixPut(firstSymbolLast, secondSymbol, '>');
                        }
                }
    }

    private static void matrixPut(char y, char x, char sign) {
        if ((matrix[indexOf(y)][indexOf(x)] == ' ') || (sign == '='))
            matrix[indexOf(y)][indexOf(x)] = sign;
    }

    private static void showMatrix() {
        System.out.print("  ");
        for (char symbol : allSymbols)
            System.out.print(symbol + " ");
        System.out.println();
        int i = 0;
        for (char symbol : allSymbols) {
            System.out.print(symbol + " ");
            for (int j = 0; j < matrix.length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
            i++;
        }
    }

    private static int indexOf(char element) {

        int index = -1;

        if (allSymbols.contains(element)) {

            index = allSymbols.headSet(element).size();
        }

        return index;
    }

    private static char matrixElement(char y, char x) {
        return matrix[indexOf(y)][indexOf(x)];
    }

    private static void transformingInput(String s) {

        System.out.println(s);

        if (!s.matches("[^A-Z]+")) {
            System.out.println("The string was not parsed!\n");
            return;
        }

        for (int i = 0; i < s.length(); i++)
            if (!allSymbols.contains(s.charAt(i))) {
                System.out.println("The string was not parsed!\n");
                return;
            }

        StringBuilder str = new StringBuilder();

        char firstChar;
        char secondChar = 0;

        str.append('<');
        if (s.length() == 1) str.append(s);
        for (int i = 0; i < s.length() - 1; i++) {
            firstChar = s.charAt(i);
            secondChar = s.charAt(i + 1);
            str.append(firstChar);
            str.append(matrixElement(firstChar, secondChar));
        }
        str.append(secondChar);
        str.append('>');

        System.out.println(str);
        parseInput(str);

    }

    private static void parseInput(StringBuilder stringToCheck) {

        graph = new SingleGraph(Long.toString(System.currentTimeMillis()));
        graph.setAttribute("ui.stylesheet", "url(styleCSS.css);");
        //graph.setAttribute("text-size","100pt");
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");

        ids = new ArrayList<>();
        newId = 0;
        edgeId = 0;

        final StringBuilder S = new StringBuilder("<S>");

        for (int i = 1; i < stringToCheck.length() - 1; i += 2) {
            addNode(stringToCheck.charAt(i));
        }

        int start;
        int end;
        int min;
        int tmpStart;

        while (stringToCheck.compareTo(S) != 0) {
            if (
                    (stringToCheck.indexOf("<") == -1)
                            || (stringToCheck.indexOf(">") == -1)
                            || (stringToCheck.indexOf(" ") != -1)
            ) {
                System.out.println("The string was not parsed!\n");
                return;
            }

            tmpStart = 0;
            start = 0;
            min = stringToCheck.length();
            end = min - 1;

            for (int i = 0; i < stringToCheck.length(); i++) {
                if (stringToCheck.charAt(i) == '<') tmpStart = i;
                if ((stringToCheck.charAt(i) == '>') && (i - tmpStart < min)) {
                    start = tmpStart;
                    end = i;
                    min = end - start;
                }
            }

            char startCh;
            char endCh;

            if (start == 0) startCh = '<';
            else startCh = stringToCheck.charAt(start - 1);
            if (end >= stringToCheck.length() - 1) endCh = '>';
            else endCh = stringToCheck.charAt(end + 1);

            String toReplace = transformElement(stringToCheck.substring(start, end + 1), startCh, endCh, (start + 1) - (start + 2) / 2);

            if (toReplace == null) {
                System.out.println("The string was not parsed!\n");
                return;
            }

            stringToCheck.replace(start, end + 1, toReplace);

            System.out.println(stringToCheck);

        }

        System.out.println("The string was parsed!\n");

/*        System.out.println("Do you want to see a graph? [y/n]");
        Scanner scanner = new Scanner(System.in);

        if (scanner.nextLine().equals("y")) {
            viewer = graph.display(true);
            //viewer.enableAutoLayout(new HierarchicalLayout());
            viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);
        }*/

        GraphVisualize show = new GraphVisualize();
        show.show(graph);

    }

    private static String transformElement(String element, char start, char end, int index) {

        element = element.substring(1, element.length() - 1);

        element = element.replace("=", "");

        String rule = element;

        if (reverseGrammar.containsKey(element))
            element = element.replace(element, reverseGrammar.get(element).toString());
        else return null;

        addNode(element.charAt(0), index);

        index++;

        for (int i = 0; i < rule.length(); i++) {
            graph.addEdge(Integer.toString(edgeId++), ids.get(index - 1), ids.get(index));
            ids.remove(index);
        }

        if (start != '<') start = matrixElement(start, element.charAt(0));

        if (end != '>') end = matrixElement(element.charAt(element.length() - 1), end);

        element = start + element + end;

        return element;
    }

    private static void addNode(char label) {
        graph.addNode(Integer.toString(newId)).setAttribute("ui.label", Character.toString(label));
        ids.add(newId++);
    }

    private static void addNode(char label, int index) {
        graph.addNode(Integer.toString(newId)).setAttribute("ui.label", Character.toString(label));
        ids.add(index, newId++);
    }

}
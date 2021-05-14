<<<<<<< HEAD
package com;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class Parsing {
    static HashMap<String, ArrayList<String>> grammar = new HashMap<>();

    static FileReader fr;

    static {
        try {
            fr = new FileReader("input.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static Scanner sc = new Scanner(fr);

    static boolean cS;

    //these two methods has the same purpose as they
    //had in previous laboratory work - to add one string or array of strings to another array if they do not exist in
    //this array

    static void concat(ArrayList<String> a, ArrayList<String> b) {
        for (String s : b)
            if (!a.contains(s)) a.add(s);
    }

    static void concat(ArrayList<String> a, String b) {
        if (!a.contains(b)) a.add(b);
    }

    public static void parse() { //the main method of parsing and transforming all the grammar
        String s;
        ArrayList<String> RHS;
        while (sc.hasNextLine()) {
            s = sc.nextLine();
            if (s.matches("[A-Z]-(?:[A-Za-z]+|!?)")) { //i tried to use here the regex to test if input is correct
                //or not

                RHS = new ArrayList<>();

                if (grammar.containsKey(s.substring(0, 1))) RHS = grammar.get(s.substring(0, 1));

                concat(RHS, s.substring(2));

                grammar.put(s.substring(0, 1), RHS);

            } else {
                System.out.println("Failed to read rules! Wrong string: " + s + "\n");
                System.exit(0);
            }
        }

        System.out.println("Initial context-free grammar:\n"+grammar+"\n");

        RemoveNonProductive();

        if (cS=ContainsS()) {grammar.put("S0", new ArrayList(Collections.singletonList("S")));  }

        EpsilonErase();

        System.out.println("Grammar with removed non-productive terms, new starting term and epsilon transitions:\n"+grammar+"\n");

        CancellingNT();

        System.out.println("Transforming all unit transitions:\n"+grammar+"\n");

        MovingTerminals();

        System.out.println("Transforming transitions containing terminals:\n"+grammar+"\n");

        MovingNonTerminals();

        System.out.println("Transforming non-terminals, complete Chomsky normal form:\n"+grammar);

    }

    private static boolean ContainsS() {
        //checking if there is starting term "S" in right hand side of any rule
        //if exists- we have to add new term and new starting term in the grammar
        ArrayList<String> RHS;
        for (String c : grammar.keySet()) {
            RHS = grammar.get(c);
            for (String string : RHS) {
                if (string.contains("S")) return true;
            }
        }
        return false;
    }

    private static String ContainsEpsilon() {
        //just a method that checks if there is epsilon transition or not
        //since cnf allows the starting symbol to contain epsilon transition- we don't have to check it
        //if e-transition is found- it returns key, if not- null
        for (String key : grammar.keySet())
            for (String oneRule : grammar.get(key))
                if (cS) {if (oneRule.equals("!")&&(!key.equals("S0"))) return key;}
                else if (oneRule.equals("!")&&(!key.equals("S"))) return key;

        return null;
    }

    private static void EpsilonErase() {
        //the main method about epsilon transition elimination
        //if we find an e-transition, we remove it from the original term, if this term is found in other rules
        //we need to create all the combinations of this rule with and without any of the original term
        // (i checked how many times in one rule appears original term. Then, i elevate two to the power of this number
        // and then transform this number into binary form example:
        // B->AA A->epsilon then there will be correspondence: 00 = B->AA 01 = B->A+epsilon
        // 10 = B->epsilon+A and 11 = B->epsilon+epsilon, and also, we can skip one iteration because we already have
        // the rule B->AA, so we can start not from zero, but from 1)
        //also, if A->B and B->epsilon then after all this transformations we should add this rule to A

        String s;
        String binary;
        int count=0,countTemp;

        while ((s=ContainsEpsilon())!=null) {

            ArrayList<String> epsilonFound = grammar.get(s);
            epsilonFound.remove("!");
            grammar.replace(s,epsilonFound);

            for (Map.Entry<String, ArrayList<String>> entry : grammar.entrySet()) {

                ArrayList<String> RHS = entry.getValue();

                for (int j = 0; j < RHS.size(); j++) {
                    String rule = RHS.get(j);
                    for (int i = 0; i <= rule.length() - s.length(); i++)
                        if (rule.startsWith(s, i)) count++;
                    countTemp = count;
                    count = (int) Math.pow(2, count);

                    for (int i = 1; i < count; i++) { //here have to be i=1 so we skip the rule that is anyway in the grammar.
                        binary = Integer.toBinaryString(i);
                        for (int index = countTemp - binary.length(); index > 0; index--) binary = '0' + binary;

                        int bIndex = 0;
                        String result = "";
                        if (!s.equals(rule)) {
                            String[] arr = rule.split(s);
                            for (String s1 : arr) {
                                result = result + s1;
                                if (bIndex < binary.length()) {
                                    if (binary.charAt(bIndex) == '0') result = result + s;
                                    bIndex++;
                                }
                            }
                            while (bIndex < binary.length()) {
                                if (binary.charAt(bIndex) == '0') result = result + s;
                                bIndex++;
                            }

                        } else if (!s.equals(entry.getKey())) result = "!";

                        //System.out.println(rule+" "+binary+" "+result);
                        if (!result.equals(""))
                        concat(RHS, result);

                    }

                    count = 0;
                }
                //System.out.println(entry.getKey()+" "+RHS);
                grammar.replace(entry.getKey(), RHS);
            }
        }

    }

    private static void RemoveNonProductive() {
        //this method checks if a key contains in the rule (exceptions are S and S0)
        //if key is not contained in any rule- we can delete it without a twinge of conscience
        ArrayList<String> AllRules = new ArrayList<>();
        boolean contains =true;
        for (Map.Entry<String, ArrayList<String>> entry:grammar.entrySet()) concat(AllRules, entry.getValue());

        Iterator<Map.Entry<String, ArrayList<String>>> iterator = grammar.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, ArrayList<String>> entry = iterator.next();
            for (String s:AllRules)
                if (s.contains(entry.getKey()) || entry.getKey().equals("S") || entry.getKey().equals("S0")) {
                    contains = false;
                    break;
                }
                if (contains) iterator.remove();
                contains=true;
        }
    }

    private static void CancellingNT() { //Non-terminals

        //This method eliminates all the unit transitions
        //Very simple - if there is the unit transition - we delete it and put all the rules of the term mentioned
        //in RHS (right hand side) of the unit transition.

        ArrayList<String> keys = new ArrayList<>(grammar.keySet());
        ArrayList<String> RHS;

        for (Map.Entry<String, ArrayList<String>> entry : grammar.entrySet()) {
            for (int i = 0; i < entry.getValue().size(); i++)
                if (keys.contains(entry.getValue().get(i))) {
                    RHS = grammar.get(entry.getValue().get(i));
                    concat(entry.getValue(), RHS);
                    /*if (!entry.getValue().get(i).equals("S"))*/
                    entry.getValue().remove(entry.getValue().get(i));
                }
        }

    }

    static int numberOfQ; //check the next index of the new term

    private static void MovingTerminals() {
        //this method checks if the rule contains terminal symbols and not only them:
        //if there is such rule - it just creates new Non-terminal (Q(n)) that has transition to the terminal and then
        //replaces the terminal in the original rule with this new Non-terminal
        ArrayList<String> terminals = new ArrayList<>();
        for (ArrayList<String> RHS: grammar.values())
            for (String rule: RHS)
                for (int i=0;i<rule.length();i++){
                    char ch = rule.charAt(i);
                    if (ch>='a'&&ch<='z') concat(terminals,Character.toString(ch));
                }
        numberOfQ= terminals.size();

        for (Map.Entry<String, ArrayList<String>> entry : grammar.entrySet()) {
            ArrayList<String> values = entry.getValue();
            for (int j = 0; j < values.size(); j++) {
                String rule = values.get(j);
                for (int i = 0; i < rule.length(); i++) {
                    char ch = rule.charAt(i);
                    if ((ch >= 'a' && ch <= 'z') && (!rule.equals(Character.toString(ch)))) {
                        int index = terminals.indexOf(Character.toString(ch));
                        values.remove(rule);
                        rule = rule.replace(Character.toString(ch), "Q" + index);
                        values.add(j, rule);
                    }
                }
            }
        }
        for (int i=0;i< terminals.size();i++)
        grammar.put("Q"+i, new ArrayList(Collections.singletonList(terminals.get(i))));
    }

    private static void MovingNonTerminals() {
        //this method checks if there are rules that contain more than two Non-terminals
        //if the rule does contain more than two - it creates new non-terminal and put to it a new rule that
        //contain first two non-terminals from the original rule. In the original rule it replaces these two
        // non-terminals with one new non-terminal
        // !!important!! i tried to optimize the grammar just not to create infinity of non-terminals with
        //one same transition - if there is already transition Q0->BS, then it finds it in the other rules of the
        // grammar to replace it, only if it has more than two non-terminals
        //i also used here the regex to split the rule into separate non-terminals
        String str;
        HashMap<String,ArrayList<String>> toAdd = new HashMap<>();
        for (Map.Entry<String, ArrayList<String>> entry : grammar.entrySet()) {
            ArrayList<String> values = entry.getValue();
            for (int i = 0; i < values.size(); i++) {
                str = values.get(i);
                ArrayList<String> res = new ArrayList(Arrays.asList(str.split("(?=(?:Q[0-9]+|[A-Z]))")));
                while (res.size() > 2) {
                    res = new ArrayList(Arrays.asList(str.split("(?=(?:Q[0-9]+|[A-Z]))")));
                    String temp = res.get(0) + res.get(1);
                    toAdd.put("Q" + numberOfQ, new ArrayList(Collections.singletonList(temp)));
                    values.remove(str);
                    str = str.replace(temp, "Q" + numberOfQ);
                    values.add(i, str);
                    for (ArrayList<String> rules : grammar.values())
                        for (int j = 0; j < rules.size(); j++) {
                            String rule = rules.get(j);
                            if (!rule.equals(temp)&&rule.contains(temp)) {
                                rules.remove(j);
                                rule = rule.replace(temp, "Q" + numberOfQ);
                                rules.add(j, rule);
                            }
                        }
                    res.remove(0);
                    res.remove(1);
                    res.add(0, "Q" + numberOfQ);
                    numberOfQ++;
                }
            }
        }
        grammar.putAll(toAdd);
    }

=======
package com;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class Parsing {
    static HashMap<String, ArrayList<String>> grammar = new HashMap<>();

    static FileReader fr;

    static {
        try {
            fr = new FileReader("input.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static Scanner sc = new Scanner(fr);

    static boolean cS;

    //these two methods has the same purpose as they
    //had in previous laboratory work - to add one string or array of strings to another array if they do not exist in
    //this array

    static void concat(ArrayList<String> a, ArrayList<String> b) {
        for (String s : b)
            if (!a.contains(s)) a.add(s);
    }

    static void concat(ArrayList<String> a, String b) {
        if (!a.contains(b)) a.add(b);
    }

    public static void parse() { //the main method of parsing and transforming all the grammar
        String s;
        ArrayList<String> RHS;
        while (sc.hasNextLine()) {
            s = sc.nextLine();
            if (s.matches("[A-Z]-(?:[A-Za-z]+|!?)")) { //i tried to use here the regex to test if input is correct
                //or not

                RHS = new ArrayList<>();

                if (grammar.containsKey(s.substring(0, 1))) RHS = grammar.get(s.substring(0, 1));

                concat(RHS, s.substring(2));

                grammar.put(s.substring(0, 1), RHS);

            } else {
                System.out.println("Failed to read rules! Wrong string: " + s + "\n");
                System.exit(0);
            }
        }

        System.out.println("Initial context-free grammar:\n"+grammar+"\n");

        RemoveNonProductive();

        if (cS=ContainsS()) {grammar.put("S0", new ArrayList(Collections.singletonList("S")));  }

        EpsilonErase();

        System.out.println("Grammar with removed non-productive terms, new starting term and epsilon transitions:\n"+grammar+"\n");

        CancellingNT();

        System.out.println("Transforming all unit transitions:\n"+grammar+"\n");

        MovingTerminals();

        System.out.println("Transforming transitions containing terminals:\n"+grammar+"\n");

        MovingNonTerminals();

        System.out.println("Transforming non-terminals, complete Chomsky normal form:\n"+grammar);

    }

    private static boolean ContainsS() {
        //checking if there is starting term "S" in right hand side of any rule
        //if exists- we have to add new term and new starting term in the grammar
        ArrayList<String> RHS;
        for (String c : grammar.keySet()) {
            RHS = grammar.get(c);
            for (String string : RHS) {
                if (string.contains("S")) return true;
            }
        }
        return false;
    }

    private static String ContainsEpsilon() {
        //just a method that checks if there is epsilon transition or not
        //since cnf allows the starting symbol to contain epsilon transition- we don't have to check it
        //if e-transition is found- it returns key, if not- null
        for (String key : grammar.keySet())
            for (String oneRule : grammar.get(key))
                if (cS) {if (oneRule.equals("!")&&(!key.equals("S0"))) return key;}
                else if (oneRule.equals("!")&&(!key.equals("S"))) return key;

        return null;
    }

    private static void EpsilonErase() {
        //the main method about epsilon transition elimination
        //if we find an e-transition, we remove it from the original term, if this term is found in other rules
        //we need to create all the combinations of this rule with and without any of the original term
        // (i checked how many times in one rule appears original term. Then, i elevate two to the power of this number
        // and then transform this number into binary form example:
        // B->AA A->epsilon then there will be correspondence: 00 = B->AA 01 = B->A+epsilon
        // 10 = B->epsilon+A and 11 = B->epsilon+epsilon, and also, we can skip one iteration because we already have
        // the rule B->AA, so we can start not from zero, but from 1)
        //also, if A->B and B->epsilon then after all this transformations we should add this rule to A

        String s;
        String binary;
        int count=0,countTemp;

        while ((s=ContainsEpsilon())!=null) {

            ArrayList<String> epsilonFound = grammar.get(s);
            epsilonFound.remove("!");
            grammar.replace(s,epsilonFound);

            for (Map.Entry<String, ArrayList<String>> entry : grammar.entrySet()) {

                ArrayList<String> RHS = entry.getValue();

                for (int j = 0; j < RHS.size(); j++) {
                    String rule = RHS.get(j);
                    for (int i = 0; i <= rule.length() - s.length(); i++)
                        if (rule.startsWith(s, i)) count++;
                    countTemp = count;
                    count = (int) Math.pow(2, count);

                    for (int i = 1; i < count; i++) { //here have to be i=1 so we skip the rule that is anyway in the grammar.
                        binary = Integer.toBinaryString(i);
                        for (int index = countTemp - binary.length(); index > 0; index--) binary = '0' + binary;

                        int bIndex = 0;
                        String result = "";
                        if (!s.equals(rule)) {
                            String[] arr = rule.split(s);
                            for (String s1 : arr) {
                                result = result + s1;
                                if (bIndex < binary.length()) {
                                    if (binary.charAt(bIndex) == '0') result = result + s;
                                    bIndex++;
                                }
                            }
                            while (bIndex < binary.length()) {
                                if (binary.charAt(bIndex) == '0') result = result + s;
                                bIndex++;
                            }

                        } else if (!s.equals(entry.getKey())) result = "!";

                        //System.out.println(rule+" "+binary+" "+result);
                        if (!result.equals(""))
                        concat(RHS, result);

                    }

                    count = 0;
                }
                //System.out.println(entry.getKey()+" "+RHS);
                grammar.replace(entry.getKey(), RHS);
            }
        }

    }

    private static void RemoveNonProductive() {
        //this method checks if a key contains in the rule (exceptions are S and S0)
        //if key is not contained in any rule- we can delete it without a twinge of conscience
        ArrayList<String> AllRules = new ArrayList<>();
        boolean contains =true;
        for (Map.Entry<String, ArrayList<String>> entry:grammar.entrySet()) concat(AllRules, entry.getValue());

        Iterator<Map.Entry<String, ArrayList<String>>> iterator = grammar.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, ArrayList<String>> entry = iterator.next();
            for (String s:AllRules)
                if (s.contains(entry.getKey()) || entry.getKey().equals("S") || entry.getKey().equals("S0")) {
                    contains = false;
                    break;
                }
                if (contains) iterator.remove();
                contains=true;
        }
    }

    private static void CancellingNT() { //Non-terminals

        //This method eliminates all the unit transitions
        //Very simple - if there is the unit transition - we delete it and put all the rules of the term mentioned
        //in RHS (right hand side) of the unit transition.

        ArrayList<String> keys = new ArrayList<>(grammar.keySet());
        ArrayList<String> RHS;

        for (Map.Entry<String, ArrayList<String>> entry : grammar.entrySet()) {
            for (int i = 0; i < entry.getValue().size(); i++)
                if (keys.contains(entry.getValue().get(i))) {
                    RHS = grammar.get(entry.getValue().get(i));
                    concat(entry.getValue(), RHS);
                    /*if (!entry.getValue().get(i).equals("S"))*/
                    entry.getValue().remove(entry.getValue().get(i));
                }
        }

    }

    static int numberOfQ; //check the next index of the new term

    private static void MovingTerminals() {
        //this method checks if the rule contains terminal symbols and not only them:
        //if there is such rule - it just creates new Non-terminal (Q(n)) that has transition to the terminal and then
        //replaces the terminal in the original rule with this new Non-terminal
        ArrayList<String> terminals = new ArrayList<>();
        for (ArrayList<String> RHS: grammar.values())
            for (String rule: RHS)
                for (int i=0;i<rule.length();i++){
                    char ch = rule.charAt(i);
                    if (ch>='a'&&ch<='z') concat(terminals,Character.toString(ch));
                }
        numberOfQ= terminals.size();

        for (Map.Entry<String, ArrayList<String>> entry : grammar.entrySet()) {
            ArrayList<String> values = entry.getValue();
            for (int j = 0; j < values.size(); j++) {
                String rule = values.get(j);
                for (int i = 0; i < rule.length(); i++) {
                    char ch = rule.charAt(i);
                    if ((ch >= 'a' && ch <= 'z') && (!rule.equals(Character.toString(ch)))) {
                        int index = terminals.indexOf(Character.toString(ch));
                        values.remove(rule);
                        rule = rule.replace(Character.toString(ch), "Q" + index);
                        values.add(j, rule);
                    }
                }
            }
        }
        for (int i=0;i< terminals.size();i++)
        grammar.put("Q"+i, new ArrayList(Collections.singletonList(terminals.get(i))));
    }

    private static void MovingNonTerminals() {
        //this method checks if there are rules that contain more than two Non-terminals
        //if the rule does contain more than two - it creates new non-terminal and put to it a new rule that
        //contain first two non-terminals from the original rule. In the original rule it replaces these two
        // non-terminals with one new non-terminal
        // !!important!! i tried to optimize the grammar just not to create infinity of non-terminals with
        //one same transition - if there is already transition Q0->BS, then it finds it in the other rules of the
        // grammar to replace it, only if it has more than two non-terminals
        //i also used here the regex to split the rule into separate non-terminals
        String str;
        HashMap<String,ArrayList<String>> toAdd = new HashMap<>();
        for (Map.Entry<String, ArrayList<String>> entry : grammar.entrySet()) {
            ArrayList<String> values = entry.getValue();
            for (int i = 0; i < values.size(); i++) {
                str = values.get(i);
                ArrayList<String> res = new ArrayList(Arrays.asList(str.split("(?=(?:Q[0-9]+|[A-Z]))")));
                while (res.size() > 2) {
                    res = new ArrayList(Arrays.asList(str.split("(?=(?:Q[0-9]+|[A-Z]))")));
                    String temp = res.get(0) + res.get(1);
                    toAdd.put("Q" + numberOfQ, new ArrayList(Collections.singletonList(temp)));
                    values.remove(str);
                    str = str.replace(temp, "Q" + numberOfQ);
                    values.add(i, str);
                    for (ArrayList<String> rules : grammar.values())
                        for (int j = 0; j < rules.size(); j++) {
                            String rule = rules.get(j);
                            if (!rule.equals(temp)&&rule.contains(temp)) {
                                rules.remove(j);
                                rule = rule.replace(temp, "Q" + numberOfQ);
                                rules.add(j, rule);
                            }
                        }
                    res.remove(0);
                    res.remove(1);
                    res.add(0, "Q" + numberOfQ);
                    numberOfQ++;
                }
            }
        }
        grammar.putAll(toAdd);
    }

>>>>>>> 69c17f4c21e12b35b96f1b805eb6037471e09a37
}
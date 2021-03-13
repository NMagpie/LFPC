package com;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

public class Main { //input is by file "input.txt". Every rule is written in given form (int)-(char)(int),
    // where first integer shows, which is the index of the node, char - terminal symbol and second integer
    // is where reffers this node. Also, you should write "-" before starting node (you have to do it at least one time)
    // or "*" if it is terminal node. if node does not reffers to any other node just write (int)-eps

    //the only thing is that this graph showing library is not so good, so some loop edges' labels (weights) can overlap eachother

    //THE IMPORTANT THING IS THAT YOU CAN MOVE GRAPH DRAGGING THE NODES

    //42

    static FileReader fr;

    static {
        try {
            fr = new FileReader("input.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static Scanner sc = new Scanner(fr);

    public static void showit() {

        Graph graph = new SingleGraph("1");

        SpriteManager sman = new SpriteManager(graph);

        ArrayList<Sprite> startfinish = new ArrayList<>();

        //Sprite s = sman.addSprite("start");
        //s.setAttribute("ui.class", "start");
        //Sprite s1 = sman.addSprite("finish");
        //s1.setAttribute("ui.class", "finish");

        startfinish.add(sman.addSprite("start"));
        startfinish.get(0).setAttribute("ui.class","start");


        graph.setAttribute("ui.stylesheet", "url(styleCSS.css);");

        graph.setAttribute("text-size","100pt");
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");

        ArrayList<Integer> keys = Actions.getAllKeys();
        for (int key:keys) {
            graph.addNode(Integer.toString(key));
        }

        for (Node node: graph) {node.setAttribute("ui.label",node.getId());

            if (node.getId().equals(Integer.toString(Conversion.start))) {
                startfinish.get(0).attachToNode(node.getId());
                startfinish.get(0).setPosition(StyleConstants.Units.PX, -32, 0, 0);
            }

            if (Conversion.finish.contains(Integer.parseInt(node.getId()))) {
                startfinish.add(sman.addSprite(node.getId()));
                startfinish.get(startfinish.size()-1).setAttribute("ui.class","finish");
                startfinish.get(startfinish.size()-1).attachToNode(node.getId());
                //startfinish.get(0).setPosition(StyleConstants.Units.PX, -32, 0, 0);
            }
        }

        Map<Pair,ArrayList<Integer>> table = Conversion.table;

        Edge edge1;

        for (Pair pair:table.keySet()) {
            if (table.get(pair).size()>0)
                graph.addEdge(Integer.toString(pair.hashCode()),Integer.toString(pair.getValue()),
                        Integer.toString(table.get(pair).get(0)),true);
            edge1= graph.getEdge(Integer.toString(pair.hashCode()));
            if (!(edge1==null))
            edge1.setAttribute("ui.label",Character.toString(pair.getCh()));
        }

        //Viewer viewer = graph.display();
        //View view = viewer.getDefaultView();
        //view.getCamera().setViewCenter(2, 2, 3);

        Overlap overlap = new Overlap();

        overlap.fixOverlappingEdgeLabel(graph,100);

        graph.display();
    }

    public static void main(String[] args) {

        System.setProperty("org.graphstream.ui", "swing");

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

        showit();

    }
}

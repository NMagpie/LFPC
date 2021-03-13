package com;

import org.graphstream.graph.*;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;
import org.graphstream.util.*;

import javax.swing.plaf.basic.BasicGraphicsUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Overlap {
    public static void fixOverlappingEdgeLabel(Graph graph, int offsetIntervalValue) {
        final int offsetInterval = offsetIntervalValue;
        for (Node node : graph) {

            List<Edge> leavingEdgeList = node.leavingEdges().sorted((e1, e2) -> e1.getId().compareTo(e2.getId()))
                    .collect(Collectors.toList());

            List<Edge> enteringEdgeList = node.enteringEdges().sorted((e1, e2) -> e1.getId().compareTo(e2.getId()))
                    .collect(Collectors.toList());

            List<Node> neighbouringNodeList = node.neighborNodes().sorted((n1, n2) -> n1.getId().compareTo(n2.getId()))
                    .collect(Collectors.toList());

            for (Node neighbouringNode : neighbouringNodeList) {
                ArrayList<Edge> edgeList = new ArrayList<Edge>(leavingEdgeList.size());

                for (Edge edge : leavingEdgeList) {
                    if (edge.getTargetNode().equals(neighbouringNode))
                        edgeList.add(edge);
                } // add edge to edgeList if its target node is the same as targetNode

                for (Edge edge : enteringEdgeList) {
                    if (edge.getSourceNode().equals(neighbouringNode)) {
                        edgeList.add(edge);
                    }
                }

                int edgeListSize = edgeList.size();
                BigDecimal factor = BigDecimal.valueOf(edgeListSize / 2).setScale(0, RoundingMode.FLOOR);
                //System.out.println("factor: " + factor);
                int maxOffset = factor.intValue() * offsetInterval;
                for (int i = 0, offset = maxOffset; i < edgeListSize; i++, offset -= offsetInterval) {
                    Edge edge = edgeList.get(i);
                    //String textOffsetdeclaration = .getInstance().generateTextOffsetDeclaration(offset,
                           // offset);
                    String textOffsetdeclaration = "text-offset: 20;";
                    //System.out.println(edge.getId() + ": " + textOffsetdeclaration);
                    edge.setAttribute("ui.style", textOffsetdeclaration);
                }
            } // loop neighbouring nodes

        } // loop each node in a graph

    }// function to fix overlapping edge label
}

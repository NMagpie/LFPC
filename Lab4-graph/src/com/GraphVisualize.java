package com;

import org.graphstream.graph.Graph;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

//this class just needs to display the graph on the screen and then wait until the window with the graph is closed
//after that program starts to parse next string, if there is one.

public class GraphVisualize implements ViewerListener {
    private boolean loop;
    private ViewerPipe pipe;

    public void show(Graph graph) {
        loop=true;
        Viewer viewer = graph.display();
        pipe = viewer.newViewerPipe();
        pipe.addViewerListener(this);
        pipe.addAttributeSink(graph);
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);

        while (loop) {
            pipe.pump(); }
    }

    @Override
    public void viewClosed(String s) {
        loop=false;
        pipe.clearSinks();
    }

    @Override
    public void buttonPushed(String s) {
    }

    @Override
    public void buttonReleased(String s) {
    }

    @Override
    public void mouseOver(String s) {
    }

    @Override
    public void mouseLeft(String s) {
    }
}
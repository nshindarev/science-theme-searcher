package graph.ui;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;
import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.AbstractGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultListenableGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.JGraphXAdapterDemo;

import javax.swing.*;
import java.awt.*;

public class GraphVisualizer extends JApplet {

    private static final Logger logger = LoggerFactory.getLogger(GraphVisualizer.class);
    private static final Dimension DEFAULT_SIZE = new Dimension(1060, 640);
    private JGraphXAdapter<String, DefaultEdge> jgxAdapter;
    private DefaultDirectedGraph graph;

    public GraphVisualizer(DefaultDirectedGraph graph){
        this.graph = graph;
    }

    public void visualize (){
        this.init();

        JFrame frame = new JFrame();
        frame.getContentPane().add(this);
        frame.setTitle("Coauthors graph");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

    @Override
    public void init() {

        // create a JGraphT graph
        ListenableGraph<String, DefaultEdge> g =
                new DefaultListenableGraph<>(this.graph);

        // create a visualization using JGraph, via an adapter
        jgxAdapter = new JGraphXAdapter<>(g);

        setPreferredSize(DEFAULT_SIZE);
        mxGraphComponent component = new mxGraphComponent(jgxAdapter);
        component.setConnectable(false);
        component.getGraph().setAllowDanglingEdges(false);
        component.getGraph().setGridSize(5);
        component.getGraph().setBorder(5);


        getContentPane().add(component);
        resize(DEFAULT_SIZE);

        // positioning via jgraphx layouts
        mxCircleLayout layout = new mxCircleLayout(jgxAdapter);

        // center the circle
        int radius = 10;
        layout.setX0((DEFAULT_SIZE.width / 2.0) - radius);
        layout.setY0((DEFAULT_SIZE.height / 2.0) - radius);
        layout.setRadius(radius);
        layout.setMoveCircle(true);
        layout.setDisableEdgeStyle(true);

        layout.execute(jgxAdapter.getDefaultParent());
    }

}

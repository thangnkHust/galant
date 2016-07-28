/**
 * Selects an edge after user specifies its endpoints during algorithm execution
 */
package edu.ncsu.csc.Galant.gui.util;

import java.awt.Frame;

import edu.ncsu.csc.Galant.GraphDispatch;
import edu.ncsu.csc.Galant.GalantException;
import edu.ncsu.csc.Galant.algorithm.Terminate;
import edu.ncsu.csc.Galant.gui.util.EdgeSpecificationDialog;
import edu.ncsu.csc.Galant.gui.window.GraphWindow;
import edu.ncsu.csc.Galant.graph.component.Edge;
import edu.ncsu.csc.Galant.graph.component.Graph;
import edu.ncsu.csc.Galant.graph.component.Node;

public class EdgeSelectionDialog extends EdgeSpecificationDialog {

    public EdgeSelectionDialog(String prompt) {
        super(GraphWindow.getGraphFrame(), prompt);
    }

    protected void performAction(Node source, Node target) 
        throws Terminate, GalantException {
        GraphDispatch dispatch = GraphDispatch.getInstance();
        Graph graph = dispatch.getWorkingGraph();
        graph.setSelectedEdge(source, target);
    }
}

//  [Last modified: 2016 07 03 at 15:58:10 GMT]
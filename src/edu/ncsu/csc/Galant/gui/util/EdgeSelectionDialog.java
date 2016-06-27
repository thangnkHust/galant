package edu.ncsu.csc.Galant.gui.util;

import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JTextField;
import java.beans.*; //property change stuff
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import edu.ncsu.csc.Galant.GraphDispatch;
import edu.ncsu.csc.Galant.GalantException;
import edu.ncsu.csc.Galant.algorithm.Terminate;
import edu.ncsu.csc.Galant.gui.window.GraphWindow;
import edu.ncsu.csc.Galant.graph.component.Edge;
import edu.ncsu.csc.Galant.graph.component.Graph;
import edu.ncsu.csc.Galant.graph.component.Node;
import edu.ncsu.csc.Galant.gui.window.panels.GraphPanel;
import edu.ncsu.csc.Galant.gui.window.panels.ComponentEditPanel;
import edu.ncsu.csc.Galant.logging.LogHelper;

/**
 * This dialog is displayed whenever a needs to create or select an edge;
 * examples include creation of a new edge with Ctrl-e or deletion of an edge
 * with Del-e
 */
public abstract class EdgeSelectionDialog extends JDialog
    implements ActionListener,
               PropertyChangeListener {

    private static final int TEXT_FIELD_LENGTH = 10;

    /** text entered by user for the source */
    private String sourceText = null;
    /** text entered by user for the target */
    private String targetText = null;
    /** text field into which source text is typed */
    private JTextField sourceTextField;
    /** text field into which target text is typed */
    private JTextField targetTextField;

    private JOptionPane optionPane;

    private String enter = "Enter";
    private String cancel = "Cancel";
    
    private Frame frame;

    /** Creates the reusable dialog. */
    public EdgeSelectionDialog(Frame frame) {
        super(frame);
        LogHelper.enterConstructor(getClass());
        sourceTextField = new JTextField(TEXT_FIELD_LENGTH);
        targetTextField = new JTextField(TEXT_FIELD_LENGTH);

        // Create an array of the text and components to be displayed
        Object[] displayComponents
            = {"Source", sourceTextField, "Target", targetTextField};

        // Create an array specifying the number of dialog buttons
        // and their text.
        Object[] options = {enter, cancel};

        // Question is essentially whether or not to enter an edge, default
        // is yes.
        optionPane = new JOptionPane(displayComponents,
                                     JOptionPane.QUESTION_MESSAGE,
                                     JOptionPane.YES_NO_OPTION,
                                     null,
                                     options,
                                     enter);
        setContentPane(optionPane);

        // keep the dialog inteact so that other code can refer to its outcome
        // even after it is closed
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                    // Instead of directly closing the window,
                    // we're going to change the JOptionPane's
                    // value property.
                    optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
                }
            });

        // Ensure the text field always gets the first focus.
        addComponentListener(new ComponentAdapter() {
                public void componentShown(ComponentEvent ce) {
                    sourceTextField.requestFocusInWindow();
                }
            });

        //Register an event handler that puts the text into the option pane.
        sourceTextField.addActionListener(this);
        targetTextField.addActionListener(this);

        //Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
        LogHelper.exitConstructor(getClass());
    }

    // compiler does not like the following - cannot find symbol
//     /** Constructor for (most) situations where edge creation is not desired */
//     public EdgeSelectionDialog(Frame frame) {
//         EdgeSelectionDialog(frame, false);
//     }

    /** action to be performed when source and target are identified;
     * specified by subclass */
    protected abstract void performAction(Node source, Node target)
        throws Terminate, GalantException;

    /** handles events for the text field. */
    public void actionPerformed(ActionEvent e) {
        optionPane.setValue(enter);
    }

    /** This method reacts to state changes in the option pane. */
    public void propertyChange(PropertyChangeEvent event) {
        LogHelper.enterMethod(getClass(), "propertyChange");
        String prop = event.getPropertyName();

        if ( isVisible()
             && (event.getSource() == optionPane)
             && (JOptionPane.VALUE_PROPERTY.equals(prop) ||
                 JOptionPane.INPUT_VALUE_PROPERTY.equals(prop)) ) {
            Object value = optionPane.getValue();

            if ( value == JOptionPane.UNINITIALIZED_VALUE ) {
                // ignore reset
                return;
            }

            // Reset the JOptionPane's value.  If you don't do this, then if
            // the user presses the same button next time, no property change
            // event will be fired.
            optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

            if ( enter.equals(value) ) {
                sourceText = sourceTextField.getText();
                targetText = targetTextField.getText();
                try {
                    Graph graph = GraphDispatch.getInstance().getWorkingGraph();
                    int sourceId = Integer.parseInt(sourceText);
                    int targetId = Integer.parseInt(targetText);
                    Node source = graph.getNodeById(sourceId);
                    Node target = graph.getNodeById(targetId);

                    performAction(source, target);

                    //we're done; clear and dismiss the dialog
                    clearAndHide();
                }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(EdgeSelectionDialog.this,
                                                  e.getMessage(),
                                                  "Try again",
                                                  JOptionPane.ERROR_MESSAGE);
                    sourceTextField.selectAll();
                    targetTextField.selectAll();
                    sourceText = null;
                    targetText = null;
                    sourceTextField.requestFocusInWindow();
                }
            } // enter button pushed
            else { //user closed dialog or clicked cancel
                sourceText = null;
                targetText = null;
                clearAndHide();
            }
        }
        LogHelper.exitMethod(getClass(), "propertyChange");
    }

    /** This method clears the dialog and hides it. */
    public void clearAndHide() {
        sourceTextField.setText(null);
        targetTextField.setText(null);
        setVisible(false);
    }
}

//  [Last modified: 2016 06 27 at 20:13:55 GMT]

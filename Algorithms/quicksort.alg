/** quicksort.alg
 * An animation of Quicksort
 * @author Hayden Fuss, Solomon Yeh, Jordan Connor
 * @date 2015/4/28
 * Submitted for Project 3 in CSC 316
 *
 * Caution: assumes that the graph is a collection of nodes with no edges
 * weight of node = sorting key;
 * Inital position of the Nodes does not affect output
 *
 * This Quicksort implementation chooses the median index of the list
 * as the pivot, and can handle an arbitrary sized array, but
 * visual fidelity may fall on larger indices
 */

/** Horizontal gap between Nodes */
final int HORIZONTAL_GAP = 60;
/** Leftmost x-coordinate */
final int X_OFFSET = 200;
/** Vertical gap between Nodes */
final int VERTICAL_GAP = 50;


/** number of elements to be sorted (number of nodes) */
int num_elements;
/**  initial unsorted coordinates of the Nodes */
int [] xCoord;
/** Color of various recursive levels in Quicksort */
final String[] colors = {BLACK, GREEN, BLUE, MAGENTA, CYAN, ORANGE, TEAL, VIOLET};

/**
 * Lines up the nodes in a single horizontal row
 */
function lineUpNodes() {
    int xPosition = X_OFFSET;
    int yPosition = 2 * VERTICAL_GAP;
    int i = 0;
    for ( Node node : getNodes() ) {
        xCoord[i] = xPosition;
        setPosition(node, xPosition, yPosition);
        xPosition += HORIZONTAL_GAP;
        i++;
    }
}

/**
 * @return a string of the form "[ w_1 w_2 ... w_n ]", where w_i is the
 * weight of the i-th element of list
 */
function String listToString(NodeList list) {
  String s = "[";
  for ( Node v : list ) {
    s += " " + integer(weight(v));
  }
  return s + " ]";
}

/**
 * Recursive quick sort function for a list of nodes
 * @param nodes List of nodes to be sorted
 * @param color_index Curren "level" of color
 *        this maps to the id of the colors array
 * @return the sorted list by weight
 */
function NodeList Quicksort(NodeList nodes, int color_index) {
   if(color_index >= colors.length) {
      color_index = 0;
   }

   if(nodes.size() <= 1) {
      return nodes;
   }

   //Get the pivot
   //For now get middle

   final Node pivot = nodes.get(nodes.size()/2);
   display("sorting " + listToString(nodes) + ", pivot = "
           + integer(weight(pivot)));
   
   //Set pivot color
   beginStep();
   highlight(pivot);
   mark(pivot);
   endStep();

   //Partition defitions
   NodeList sortedNodes = new NodeList();
   NodeList lowerNodes = new NodeList();
   NodeList upperNodes = new NodeList();

   //Now handle new positions of nodes!
   int centerX = getX(pivot);
   int centerY = getY(pivot);
   int newY = centerY + 2 * VERTICAL_GAP;

   //Compares
   int newXU = 1;
   int newXL = 1;
   for(Node node : nodes) {
      double pivotWeight = weight(pivot);
      double curWeight = weight(node);

      if(node == pivot) {
         continue;
      }
      else if(curWeight < pivotWeight) {
         lowerNodes.add(node);
         Point pos = new Point (centerX - HORIZONTAL_GAP * newXL, newY);

         //Illustrate what node we are on
         beginStep();
         highlight(node);
         setX(pivot, getX(node));
         setY(pivot, newY - VERTICAL_GAP);
         endStep();

         //Illustrate the move
         beginStep();
         setPosition(node, pos);
         color(node, colors[color_index + 1]);
         endStep();
         beginStep();
         unhighlight(node);
         endStep();
         newXL++;
      }
      else {
         upperNodes.add(node);
         Point pos = new Point (centerX + newXU * HORIZONTAL_GAP, newY);

         //Illustrate what node we are on
         beginStep();
         highlight(node);
         setX(pivot, getX(node));
         setY(pivot, newY - VERTICAL_GAP);
         endStep();

         //Illustrate the move
         beginStep();
         setPosition(node, pos);
         color(node, colors[color_index + 1]);
         endStep();
         beginStep();
         unhighlight(node);
         endStep();
         newXU++;
      }
   }

   beginStep();
   pivot.setX(centerX);
   pivot.setY(centerY);
   endStep();

   //Recursive sorts
   if( ! lowerNodes.isEmpty() ) {
      lowerNodes = Quicksort(lowerNodes, color_index + 1);
   }
   if( ! upperNodes.isEmpty() ) {
      upperNodes = Quicksort(upperNodes, color_index + 1);
   }

   //Now add the nodes into the sorted list
   if( ! lowerNodes.isEmpty()) {
      for ( Node node : lowerNodes ) {
         sortedNodes.add(node);
      }
   }
   sortedNodes.add(pivot);
   if( ! upperNodes.isEmpty() ) {
      for ( Node node : upperNodes ) {
         sortedNodes.add(node);
      }
   }

   //Now move the nodes back to the center level!
   boolean leftOfPivot = true;

   int upperCount = 1;
   int lowerCount = lowerNodes.size();
   for ( Node node : sortedNodes ) {

      //Pivot stays the same
      if ( node == pivot ) {
         leftOfPivot = false;
         continue;
      }

      //Illustrate what node we are on
      beginStep();
      highlight(node);
      endStep();

      //Illustrate the move
      beginStep();
      if ( leftOfPivot ) {
          setX(node, centerX - lowerCount * HORIZONTAL_GAP);
          lowerCount--;
      }
      else {
          setX(node, centerX + upperCount * HORIZONTAL_GAP);
          upperCount++;
      }

      node.setY(centerY);
      endStep();

      //Unhighlight
      beginStep();
      unhighlight(node);
      color(node, colors[color_index]);
      endStep();
   }

   beginStep();
   unhighlight(pivot);
   unmark(pivot);
   endStep();
   display("done sorting " + listToString(sortedNodes));
   return sortedNodes;
}

algorithm {
  movesNodes();
  showNodeWeights();

  beginStep();
  xCoord = new int[numberOfNodes()];
  lineUpNodes();
  endStep();

  //Sort the nodes
  // Note: can't name this 'sort' -- it's a Galant macro
  Quicksort(getNodes(), 0);
}

//  [Last modified: 2017 01 23 at 22:37:04 GMT]

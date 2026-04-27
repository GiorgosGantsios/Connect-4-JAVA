package ce326.hw3;
//package ce326.hw2;

public class BasicNode {
    //private double value;
    int value;
    Board board;

    public BasicNode(){
        this.value = 0;
        this.board = null;
    }

    public BasicNode(int newValue){
        this();
        this.value = newValue;
    }

    public BasicNode(Board board){
        this();
        this.board = board;
    }

    public void setValue(int newValue){
        this.value = newValue;                          // set node value
    }

    public int getValue(){
        return this.value;                              // get node value
    }

    public int solveMinMax(){
        return this.value;                              // MinMax algorithm returns value for a leaf node
    }                                                   // declared here to avoid type casting later

    public int getSmartValue(int a, int b){
        return this.value;                              // alpha beta pruning algorithm returns value for a leaf node
    }                                                   // declared here to avoid type casting later

    public int resolveValue(){                          // figure out the node's value to solve minMax
        int result = board.evaluateBoard('a');          // evaluate node's value using Board Class
        setValue(result);                               // set node's value
        return result;
    }

    public void printNodeAndChildren(boolean childrenFlag){
        System.out.println(" " + value);
        board.printBoard();
    }

    //public int solveValuePrunning(){
//
    //}

    // The following methods in this class are just
    // declared to avoid type casting later in the
    // code. They are overridden in the extended
    // classes.
    public int getIndex(){                              // no meaning, declared here to avoid
        return -1;                                      // type casting later :)
    }

    public BasicNode[] getChildren() {                  // leaf nodes have no children
        return null;
    }

    protected int getChildrenSize(){                    // leaf nodes have no children
        return 0;
    }

    public BasicNode getChild(int pos){                 // leaf nodes have no children
        return null;
    }

    // this method returns sum + numberOfSubnodes in a particular subtree
    public int calculateSubnodes(int sum){              // leaf nodes have no subnodes so they do not add to the amount
        return sum;                                     // of tree nodes
    }

    // this method returns sum + numberOfPrunedNodes in a particular subtree
    public int calculatePruned(int sum){                // leaf nodes have no subnodes so they do not add to the amount
        return sum;                                     // of tree nodes
    }

    public void createChildrenMoves(int remainingDepth){
        return;
    }

    public int solveValue(int a, int b, int remainingDepth){
        remainingDepth --;
        return value;
    }
}

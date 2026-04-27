package ce326.hw3;
//package ce326.hw2;

public class InternalNode extends BasicNode{
    
    BasicNode[] children;
    int alpha;
    int beta;
    int prunedIndex;

    public InternalNode(){
        super();
        this.children = null;
        this.alpha = -Integer.MAX_VALUE;
        this.beta = Integer.MAX_VALUE;
        this.prunedIndex = -1;                                  // prunedIndex is initialized to -1
    }

    public InternalNode(BasicNode[] initChildren){
        this();
        this.children = initChildren;                           // initialize children array
        this.prunedIndex = children.length;                     // prunedIndex is children length (no child is considered pruned)
    }

    public InternalNode(Board board){
        super(board);
        this.children = null;
        this.alpha = -Integer.MAX_VALUE;
        this.beta = Integer.MAX_VALUE;
    }

    public void setChildrednSize(int size){
        int i;
        BasicNode temp[] = new BasicNode[size];                 // create new temp array with the required size

        for(i = 0; i < this.children.length; i++)               // move every object in temp array
            temp[i] = this.children[i];

        this.children = temp;
    }

    @Override
    public int getChildrenSize(){
        return this.children.length;                            // return children array size
    }

    public void insertChild(int pos, BasicNode X){
        this.children[pos] = X;                                 // set child in index pos = X
    }

    @Override
    public BasicNode getChild(int pos){
        return this.children[pos];                              // return child in index pos
    }

    @Override
    public BasicNode[] getChildren() {
        return children;                                        // return children array
    }

    @Override
    public int calculateSubnodes(int sum){
        sum += this.getChildrenSize();                          // increase sum by children.length
        for(BasicNode child : this.getChildren())               // recursive call for every children
            sum = child.calculateSubnodes(sum);
    
        return sum;
    }

    @Override
    public int getValue(){                                   // this method is never used because no node is type InternalNode without
        return super.getValue();                                // being either MaximizerNode or MinimizerNode
    }

    @Override
    public void printNodeAndChildren(boolean childrenFlag){
        System.out.println(" "+ value);
        board.printBoard();
        if(children == null || !childrenFlag)
            return;
        for(int i =0; i < Board.COLUMNS; i++){
            if(children[i] != null)
                children[i].printNodeAndChildren(childrenFlag);
        }
    }

    @Override
    public int getSmartValue(int a, int b){            // this method is never used because no node is type InternalNode without
        return super.getValue();                                // being either MaximizerNode or MinimizerNode
    }

    @Override
    public int solveMinMax(){                                // this method is never used because no node is type InternalNode without
        return super.solveMinMax();                             // being either MaximizerNode or MinimizerNode
    }

    @Override
    public int solveValue(int a, int b, int remainingDepth){
        remainingDepth --;
        return super.solveValue(a, b, remainingDepth);
    }

    @Override
    public int calculatePruned(int sum){
        int size = getChildrenSize();
        int i;

        if(this.prunedIndex == -1){                             // this means all children were pruned
            for(BasicNode child : getChildren())
                sum = child.calculateSubnodes(sum);             // calculate all subnodes in subtree for this case
        }
        else if(this.prunedIndex == getChildrenSize()){         // this means no child was pruned
            for(BasicNode child : getChildren())
                sum = child.calculatePruned(sum);               // calculate pruned for subtree for this case
        }
        else{                                                   // some children were pruned
            sum += size - this.prunedIndex;                     // increase sum by number of pruned children
            for(i = 0; i < this.prunedIndex; i++)
                sum = this.children[i].calculatePruned(sum);    // calculate pruned subnodes for non-pruned children
            for(i = this.prunedIndex; i < size; i++)
                sum = this.children[i].calculateSubnodes(sum);  // calculate all subnodes for pruned children
        }
        return sum;
    }

    //public playmove(char pov, int i){
    //    char[][] playedMoveBoard = board.putPieceInColumn(pov, i);
    //    children[i];
    //}
}

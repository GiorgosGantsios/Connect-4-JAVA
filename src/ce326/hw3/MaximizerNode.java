package ce326.hw3;
//package ce326.hw2;

public class MaximizerNode extends InternalNode {
    
    public MaximizerNode(){
        super();
    }

    public MaximizerNode(BasicNode[] initChildren){
        super(initChildren);
    }

    public MaximizerNode(Board board){
        super(board);
    }

    // this method is overridden so that BasicNode objects just return their value
    // and Internal node objects solve the MinMax problem for the subtree
    @Override
    public int solveMinMax(){
        int i;
        int currMax = -Integer.MAX_VALUE;
        int temp = currMax;

        if(children == null)
            return value;
            
        for(i = 0; i < this.getChildrenSize(); i++){
            if(children[i] != null){
                temp = children[i].solveMinMax();                                   // solve MinMax for every child
                if (temp > currMax) {                                               // update currMax
                    currMax = temp;
                }
            }
        }
        //setValue(currMax);                                                      // update value as solution of the subtree
        return currMax;
    }

    // this method solves the MinMax problem using ALPHA BETA PRUNING in order
    // to avoid extra calculations that are guaranteed to not update the value
    // of the parental node
    @Override
    public int getSmartValue(int a, int b){
        int i;
        int currMax = -Integer.MAX_VALUE;
        int temp;
        this.alpha = a;                                                         // alpha is the lower bound a node value
        this.beta = b;                                                          // beta is the upper bound a node value
        
        if(children == null)
            return value;

        for(i = 0; i < this.getChildrenSize(); i++){
            if(children[i] != null){
                temp = children[i].getSmartValue(this.alpha, this.beta);

                currMax = (temp > currMax) ? temp : currMax ;                       // MaximizerNodes hold the childrens' Max values
                
                this.alpha = (this.alpha > currMax) ? this.alpha : currMax;         // MaximizerNodes update the lower bound
                
                if(this.beta <= this.alpha){
                    this.prunedIndex = i+1;                                         // if lower bound >= upper bound the rest of the children
                    break;                                                          // will not affect the result
                }
            }
        }
        //setValue(currMax);                                                      // update value as solution of the subtree
        return currMax;
    }

    @Override
    public void createChildrenMoves(int remainingDepth){
        int i = 0;
        boolean reachedLeaf = (remainingDepth == 0);

        resolveValue();
        if(board.someoneHasWon() || reachedLeaf)
            return;

        this.children = new BasicNode[Board.COLUMNS];

        for(i = 0; i < Board.COLUMNS; i++){
            if(!board.isColumnFull(i)){
                Board childBoard = new Board(board.putPieceInColumn('a', i));
                if (!childBoard.someoneHasWon()) {
                    //if(childBoard.winner != 'p')
                        children[i] = new MinimizerNode(childBoard);
                }
            }
        }
        
        for(BasicNode child : children){
            if(child != null)
                child.createChildrenMoves(remainingDepth - 1);
        }
    }
}
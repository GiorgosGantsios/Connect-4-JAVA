package ce326.hw3;
//package ce326.hw2;

public class MinimizerNode extends InternalNode{

    public MinimizerNode(){
        super();
    }

    public MinimizerNode(BasicNode[] initChildren){
        super(initChildren);
    }

    public MinimizerNode(Board board){
        super(board);
    }

    // this method is overridden so that BasicNode objects just return their value
    // and Internal node objects solve the MinMax problem for the subtree
    @Override
    public int solveMinMax(){
        int i;
        int currMin = Integer.MAX_VALUE;
        int temp = currMin;

        if(children == null)
            return value;
        
        for(i = 0; i < this.getChildrenSize(); i++){
            if(children[i] != null){
                temp = children[i].solveMinMax();                                   // solve MinMax for every child
                if (temp < currMin) {                                               // update currMax
                    currMin = temp;
                }
            }
        }
        //setValue(currMin);                                                      // update value as solution of the subtree
        return currMin;
    }

    // this method solves the MinMax problem using ALPHA BETA PRUNING in order
    // to avoid extra calculations that are guaranteed to not update the value
    // of the parental node
    @Override
    public int getSmartValue(int a, int b){
        int i;
        int currMin = Integer.MAX_VALUE;
        int temp = currMin;
        this.alpha = a;                                                         // alpha is the lower bound a node value
        this.beta = b;                                                          // beta is the upper bound a node value
        
        if(children == null)
            return value;

        for(i = 0; i < this.getChildrenSize(); i++){
            if(children[i] != null){
                temp = children[i].getSmartValue(this.alpha, this.beta);
                
                currMin = (temp < currMin) ? temp : currMin ;                       // MinimizerNodes hold the childrens' Max values
                
                this.beta = (this.beta < currMin) ? this.beta : currMin;            // MinimizerNodes update the lower bound
                
                if(this.beta <= this.alpha){
                    this.prunedIndex = i+1;                                         // if lower bound >= upper bound the rest of the children
                    break;                                                          // will not affect the result
                }
            }
        }
        
        //setValue(currMin);                                                      // update value as solution of the subtree
        return currMin;
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
                Board childBoard = new Board(board.putPieceInColumn('p', i));
                if (!childBoard.someoneHasWon()) {
                    children[i] = new MaximizerNode(childBoard);
                }
            }
        }
        
        for(i = 0; i < Board.COLUMNS; i ++){
            if(children[i] != null)
                children[i].createChildrenMoves(remainingDepth - 1);
        }
    }
}
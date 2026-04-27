package ce326.hw3;
//package ce326.hw2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class OptimalTree extends Tree{

    public OptimalTree(){
        super("");
    }

   public OptimalTree(File jsonFile) throws IllegalArgumentException, FileNotFoundException {
        super(jsonFile);
    }

    OptimalTree(BasicNode root){
        super(root);
    }

    @Override
    public int minMax(){
        this.calculatedFlag = true;
        return getRoot().getSmartValue(-Integer.MAX_VALUE, Integer.MAX_VALUE);                                 // Solve MinMax with Alpha Beta Pruning for root Node
    }

    @Override                                                                                                       // Not neccessary override, method is the same with super
    public ArrayList<Integer> optimalPath(){                                                                        // class
        ArrayList<Integer> prunedPathList = new ArrayList<Integer>();                                               // Though it is required by the instructions
        walkPrunedPath(getRoot(), prunedPathList);                                                                  // Walk Pruned Path starting from root
        return prunedPathList;
    }

    // Recursively walk solution path depending on current node's class 
    public void walkPrunedPath(BasicNode curr, ArrayList<Integer> prunedPathList){
        int pathIndex = -1;

        if(curr.getChildren() == null)
            return;

        //if(!(curr instanceof InternalNode))                                                                         // Leaf node means it is the end of the path
        //    return;
        if(curr instanceof MaximizerNode)                                                                           // For maximizers get child with max value
            pathIndex = walkPrunedPathMax(curr, prunedPathList);
        if(curr instanceof MinimizerNode)                                                                           // For minimizers get child with min value
            pathIndex = walkPrunedPathMin(curr, prunedPathList);

            walkPath(curr.getChild(pathIndex), prunedPathList);                                                     // Walk path for child in index
    }

    // Choose the index of child based on the getSmartValue() method for
    // current node
    public int walkPrunedPathMax(BasicNode curr, ArrayList<Integer> pathList){
        int i, pathIndex = 0;
        int childValue;
        int maxValueIndex = -Integer.MAX_VALUE;
        int length = curr.getChildrenSize();

        for(i = 0; i < length; i++){
            if(curr.getChild(i) == null)
                continue;
            childValue = curr.getChild(i).getSmartValue(((InternalNode)curr).alpha, ((InternalNode)curr).beta);     // Solve MinMax with ab pruning to obtain child value
            if(childValue >= maxValueIndex){
                if(Math.abs(i-3) >= Math.abs(pathIndex-3))
                    continue;
                pathIndex = i;                                                                                      // If maxValue is updated then update index
                maxValueIndex = childValue;
            }
        }
        pathList.add(pathIndex);                                                                                    // Add index in the end of the pathList
        return pathIndex;
    }

    public int walkPrunedPathMin(BasicNode curr, ArrayList<Integer> pathList){
        int i, pathIndex = 0;
        int childValue;
        int minValueIndex = Integer.MAX_VALUE;
        int length = curr.getChildrenSize();

        for(i = 0; i < length; i++){
            if(curr.getChild(i) == null)
                continue;
                
            childValue = curr.getChild(i).getSmartValue(((InternalNode)curr).alpha, ((InternalNode)curr).beta);     // Solve MinMax with ab pruning to obtain child value

            if(childValue < minValueIndex){
                if(Math.abs(i-3) >= Math.abs(pathIndex-3))
                    continue;
                pathIndex = i;                                                                                      // If minValue is updated then update index
                minValueIndex = childValue;
            }
        }
        pathList.add(pathIndex);                                                                                    // Add index in the end of the pathList
        return pathIndex;
    }

    // Count pruned nodes for subtree
    public int prunedNodes(){
        if(this.size() == 1)
            return 0;
        return ((InternalNode)this.getRoot()).calculatePruned(0);                                               // Count pruned nodes recursively starting from root
    }

    // Not neccessary override, because toString is already overridden
    // Required from the instructions
    @Override
    public void toFile(File file) throws IOException, FileNotFoundException {
        if(file.exists())                                                                                           // Cannot overwrtite existing files
            throw new IOException();
        
        FileWriter fileWriter = new FileWriter(file);                                                               // May throw IOException if unauthorized
        fileWriter.write(toString());                                                                               // writing
        fileWriter.close();
    }
    
    // This method is used to return a JSON String of Tree using recursive stringPruneJSON
    @Override
    public String toString(){
        JSONObject json =  stringPruneJSON(this.getRoot(), false);
        return json.toString(2);
    }

    // This method is used to return a String given a Node, pruned boolean indicates if curr node
    // was pruned or not during ab pruning calculation
    public JSONObject stringPruneJSON(BasicNode curr, boolean pruned){
        JSONObject jsonObj = new JSONObject();

        if(curr instanceof MaximizerNode)                                                                           // Add node type
            jsonObj.put("type", "max");
        else if(curr instanceof MinimizerNode)
            jsonObj.put("type", "min");
        else{
            jsonObj.put("type", "leaf");
            jsonObj.put("value", curr.getValue());                                                              // If leaf node add value to the String
        }

        if(this.calculatedFlag){                                                                                    // put value in InternalNodes only if OptimalTree was calculated
            if(pruned)
                jsonObj.put("pruned", true);                                                                // If node was pruned add "pruned" = true to the String
            jsonObj.put("value", curr.getValue());
        }

        if(!(curr instanceof InternalNode))
            return jsonObj;

        /*------------------Code below here in this method runs for InternalNodes only------------------*/

        JSONArray childrenJSON = new JSONArray();
        boolean prunedChild = false;

        // First configure if child was pruned and then call the recursive method for every child
        for(int i = 0; i < curr.getChildrenSize(); i++){
            BasicNode child = curr.getChild(i);                                                                     // For every child configure if it was pruned or not
            if(pruned)                                                                                              // If parent was pruned -> child was also pruned
                prunedChild = true;
            else if(((InternalNode)curr).prunedIndex == -1)                                                         // Else If prunedIndex was not updated all children were pruned
                prunedChild = true;
            else if(((InternalNode)curr).prunedIndex == curr.getChildrenSize())                                     // Else If no child was pruned -> child was also not pruned
                prunedChild = false;
            else if(i >= ((InternalNode)curr).prunedIndex)                                                          // Else if i is equal or after prunedIndex -> child was pruned
                prunedChild = true;
            childrenJSON.put(stringPruneJSON(child, prunedChild));                                                  // recursive call of stringPruneJSON
        }

        jsonObj.put("children", childrenJSON);                                                                  // put "children" = children array[] in the String
        return jsonObj;
    }


    // Not neccessary override, because toDotString is already overridden
    // Required from the instructions
    @Override
    public void toDotFile(File file) throws IOException, FileNotFoundException{
        if(file.exists())
            throw new IOException();
        
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(toDotString());
        fileWriter.close();
    }

    // Returns a String capable of visualizing Tree using graphviz
    @Override
    public String toDotString(){
        String dotStr = "graph OptimalTree { " + this.makeToDotPrune(this.getRoot(), false) + "\n}";
        return dotStr;
    }

    // Returns String capable of visualizing Node for using graphviz
    public String makeToDotPrune(BasicNode curr, boolean pruned){
        int hash = curr.hashCode();
        boolean prunedChild = false;                    // If not Calculated values are not written | if not pruned display value                  | else don't display value
                                                        // Tree Object will be displayed anyway     |                                              | and display in red color
        String dotStr = "\n    "+ String.valueOf(hash) + ( (!this.calculatedFlag) ? "[label=\"\"]" : (!pruned) ? "[label=\""+curr.getValue()+"\"]" : "[label=\"\"]\n[color=\"red\"]" );

        if(!(curr instanceof InternalNode))
            return dotStr;

        /*------------------Code below here in this method runs for InternalNodes only------------------*/

        // First configure if child was pruned and then call recursive method makeDotPrune
        for(int i = 0; i< curr.getChildrenSize(); i++){
            BasicNode child = curr.getChild(i);                                                                     // For every child configure if it was pruned or not
            if(pruned)                                                                                              // If parent was pruned -> child was also pruned
                prunedChild = true;
            else if(((InternalNode)curr).prunedIndex == -1)                                                         // Else If prunedIndex was not updated all children were pruned
                prunedChild = true;
            else if(((InternalNode)curr).prunedIndex == curr.getChildrenSize())                                     // Else If no child was pruned -> child was also not pruned
                prunedChild = false;
            else if(i >= ((InternalNode)curr).prunedIndex)                                                          // Else if i is equal or after prunedIndex -> child was pruned
                prunedChild = true;

            //recursive call of makeToDotPrune with prunedChild as pruned local varible for child
            dotStr += "\n    " + String.valueOf(curr.hashCode()) + " -- " + String.valueOf(child.hashCode()) + makeToDotPrune(child, prunedChild);
        }
        return dotStr;
    }
}

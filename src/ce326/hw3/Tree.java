package ce326.hw3;
//package ce326.hw2;

import java.util.ArrayList;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

public class Tree {
    private BasicNode root;
    boolean calculatedFlag;                                                                     // Indicates if the value of the InternalNodes
                                                                                                // of the tree was calculated or not.
    public Tree(String jsonString) throws  IllegalArgumentException{
        try{
            JSONObject rootNodeObj = new JSONObject(jsonString);                                // Obtian rootNodeObj which contains the JSON information
            this.root = parseNode(rootNodeObj);                                                 // decode root's JSON info.
            this.calculatedFlag = false;                                                        // At first the tree is considered not calculated
        } catch(JSONException jsonException){
            throw new  IllegalArgumentException();                                              // Required Exception convertion.
        }
    }

    //This constructor works the same but first converts the 
    public Tree(File jsonFile) throws  IllegalArgumentException, java.io.FileNotFoundException{
        try{
            String jsonString = fileToString(jsonFile);                                         // Convert input file to jsonString
            JSONObject rootNodeObj = new JSONObject(jsonString);                                // Work as Tree(String jsonString) constructor
            this.root = parseNode(rootNodeObj);                                                 // Parse every Node starting from root.
            this.calculatedFlag = false;                                                        // At first the tree is considered not calculated
        } catch(JSONException jsonException){
            throw new  IllegalArgumentException();                                              // Required Exception convertion
        } catch(FileNotFoundException fileNotFoundException){
            throw new FileNotFoundException();                                                  // Throw the Exception in main method
        }
    }

    public Tree(BasicNode root){
        this.root = root;
        this.calculatedFlag = false;
    }

    public void setRoot(BasicNode root){
        this.root = root;                                                                       // set root
    }

    public BasicNode getRoot(){
        return this.root;                                                                       // get root
    }

    public void rebuild(int depth){
        root.createChildrenMoves(depth);
    }

    // This method is used to covert a given File Object jsonFile with json information in it
    // to a jsonString by reading line by line and extracting the String in the file.
    public static String fileToString(File jsonFile) throws FileNotFoundException{
        StringBuilder strBuilder = new StringBuilder();                                         // Use String Builder object strBuilder
        try(Scanner sc = new Scanner(jsonFile)) {                                               // Scan jsonFile
          while( sc.hasNextLine() ) {                                                           // line by line
            String str = sc.nextLine();
            strBuilder.append(str);                                                             // Add every line from the file to the strBuilder
            strBuilder.append("\n");
          }
        }
        return  strBuilder.toString();                                                          // Return string
    }

    //This method is used to convert a JSON Object to a Node
    public BasicNode parseNode(JSONObject nodeObj){
        try{
            JSONArray childrenArr = nodeObj.getJSONArray("children");                       // Try getting node's children
            BasicNode[] children = parseChildren(childrenArr);                                  // Try parsing every child 
            if(nodeObj.getString("type").equals("max")) {                                   // Node has children only if it is MaximizerNode
                return new MaximizerNode(children);                                             // or MinimizerNode
            }
            else if(nodeObj.getString("type").equals("min")){
                return new MinimizerNode(children);                                             // Create new Node and initialize children array
            }
        }catch(Exception e){                                                                    // If node has no children (leaf), catch Exception
                int value = nodeObj.getInt("value");                                  // and create new BasicNode (leaf) initializing value
                return new BasicNode(value);
        }
        return new BasicNode(0);                                                        // Initialize value of InternalNode to 0
    }

    //This method is used to return a new BasicNode array children given a JSON array
    public BasicNode[] parseChildren(JSONArray childrenArr){
        BasicNode[] children = new BasicNode[childrenArr.length()];                             // Create new children JSON array
        for (int i = 0; i < childrenArr.length(); i++) {                                        // For every JSON child
            JSONObject childObj = childrenArr.getJSONObject(i);                                 // childObj is the corresponding JSON Object
            children[i] = parseNode(childObj);                                                  // Convert JSON Object to the corresponding node
        }
        return children;                                                                        // Return node array
    }

    // Calculate the amount of nodes in the tree
    public int size(){
        return this.root.calculateSubnodes(1);                                              // Calculate the subnodes of root and add 1
    }

    // Solve the minMax problem. Simple algorithm.
    public int minMax(){
        this.calculatedFlag = true;                                                             // The tree is considered Calculated
        return root.solveMinMax();                                                              // Solve the problem recursively starting
    }                                                                                           // from the root

    // Return indicies of children array that laed to the solution node.
    public ArrayList<Integer> optimalPath() {
        ArrayList<Integer> pathList = new ArrayList<Integer>();
        walkPath(getRoot(), pathList);                                                          // Walk path recursively starting from root
        return pathList;
    }

    // Recursively walk solution path depending on current node's class
    public void walkPath(BasicNode curr, ArrayList<Integer> pathList){
        
        int pathIndex = -1;
        
        if(curr.getChildren() == null)
            return;

        //if(!(curr instanceof InternalNode))                                                     // If node is leaf it is the end of the path
        //    return;
        if(curr instanceof MaximizerNode)                                                       // Take index of maximizer
            pathIndex = walkPathMax(curr, pathList);
        if(curr instanceof MinimizerNode)                                                       // Take index of minimizer
            pathIndex = walkPathMin(curr, pathList);

        walkPath(curr.getChild(pathIndex), pathList);                                           // Walk path for child in the wanted index
    }

    // Choose the index of child based on the solveMinMax() method for
    // current node
    public int walkPathMax(BasicNode curr, ArrayList<Integer> pathList){
        int i, pathIndex = 0;
        double childValue;
        double maxValueIndex = Double.NEGATIVE_INFINITY;
        int length = curr.getChildrenSize();

        for(i = 0; i < length; i++){
            if(curr.getChild(i) == null)
                continue;

            childValue = curr.getChild(i).solveMinMax();                                        // Solve MinMax for child to obtain its value

            if(childValue > maxValueIndex){
                pathIndex = i;                                                                  // If childvalue updates MaxValue
                maxValueIndex = childValue;                                                     // then update index
            }
        }
        pathList.add(pathIndex);                                                                // Add index in the end of the pathList
        return pathIndex;
    }

    // Choose the index of child based on the solveMinMax() method for
    // current node
    public int walkPathMin(BasicNode curr, ArrayList<Integer> pathList){
        int i, pathIndex = 0;
        int childValue;
        int minValueIndex = Integer.MAX_VALUE;
        int length = curr.getChildrenSize();

        for(i = 0; i < length; i++){
            if(curr.getChild(i) == null)
                continue;

            childValue = curr.getChild(i).solveMinMax();                                        // Solve MinMax for child to obtain its value

            if(childValue < minValueIndex){
                pathIndex = i;                                                                  // If childvalue updates MinValue
                minValueIndex = childValue;                                                     // then update index
            }
        }
        pathList.add(pathIndex);                                                                // Add index in the end of the pathList
        return pathIndex;
    }

    @Override
    public String toString(){
        JSONObject json =  stringJSON(this.getRoot());                                          // Call recursive function for root
        return json.toString(2);                                                   // JSON Object.toString() overridden method
    }

    // Recursive method that returns JSON Object given node
    public JSONObject stringJSON(BasicNode curr){
        JSONObject jsonObj = new JSONObject();

        if(curr instanceof MaximizerNode)                                                       // put "type" = "max" in JSON Object
            jsonObj.put("type", "max");
        else if(curr instanceof MinimizerNode)                                                  // put "type" = "min" in JSON Object
            jsonObj.put("type", "min");
        else if(curr instanceof BasicNode){                                                     // put "type" = "min" in JSON Object
            jsonObj.put("type", "leaf");
            jsonObj.put("value", curr.getValue());                                          // put "value" = curr.value for leaf node
            return jsonObj;                                                                     // if node is leaf type and value are enough, return
        }

        /*------------------Code below here in this method runs for InternalNodes only------------------*/

        if(this.calculatedFlag)                                                                 // Only if tree was calculated add "value" = curr.value
            jsonObj.put("value", curr.getValue());                                          // in InternalNodes

        JSONArray childrenJSON = new JSONArray();                                               // Create new JSON array for children

        for(int i=0; i<curr.getChildrenSize(); i++){
            if(curr.getChild(i) != null)
                childrenJSON.put(stringJSON(curr.getChild(i)));                                                // Call method recursively for every child
        }

        jsonObj.put("children", childrenJSON);                                              // put "children" = children array[] in JSON Object
        return jsonObj;
    }

    // Create File and write Tree in JSON format in it
    public void toFile(File file) throws IOException, FileNotFoundException {
        if(file.exists())                                                                       // Cannot overwrite existing files
            throw new IOException();
        
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(toString());                                                           // May throw IOException if attempted unauthorized writing
        fileWriter.close();
    }

    // Returns a String capable of visualizing Tree using graphviz
    public String toDotString(){
        String dotStr = "graph MinMaxTree { " + this.makeToDot(this.getRoot()) + "\n}";         // Call recursive function for root
        return dotStr;
    }

    // Returns String capable of visualizing Node for using graphviz
    public String makeToDot(BasicNode curr){
        int hash = curr.hashCode();                                     /* Show value of InternalNodes only if Tree is calculated*/
        String dotStr = "\n    "+ String.valueOf(hash) + " [label= \""+ ( !this.calculatedFlag && curr instanceof InternalNode ? "" : curr.getValue())  + "\"]";

        if(!(curr instanceof InternalNode))
            return dotStr;

        /*------------------Code below here in this method runs for InternalNodes only------------------*/

        // Attach every child node to its parent using unique hashcodes

        for(BasicNode child : curr.getChildren()){
            dotStr += "\n    " + String.valueOf(curr.hashCode()) + " -- " + String.valueOf(child.hashCode()) + makeToDot(child);
        }
        return dotStr;
    }

    // Create File and write Tree in JSON format in it
    public void toDotFile(File file) throws IOException, FileNotFoundException{
        if(file.exists())                                                                       // Cannot overwrite existing files
            throw new IOException();
        
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(toDotString());                                                        // May throw IOException if attempted unauthorized writing
        fileWriter.close();
    }

    public void printTree(){
        root.printNodeAndChildren(true);
    }    
    

}
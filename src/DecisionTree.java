import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by shwetabarapatre on 6/04/17.
 */
public class DecisionTree {
    private int numCategories;
    private int numAtts;
    private List<String> categoryNames;
    private List<String> attNames;
    private List<Instance> trainingInstances;
    private List<Instance> testInstances;
    String baseCategory;
    double baseProbability;

    private Node tree;

    public DecisionTree(String trainingFile, String testFile){
        readDataFile(trainingFile, false);
        findBaseValues();
        List<String> categoriesInTraining = new ArrayList<String>();
        for(Instance i : trainingInstances){
            categoriesInTraining.add(categoryNames.get(i.getCategory()));
        }

        readDataFile(testFile, true);
        List<String> cloneAttributes = new ArrayList<String>();
        for(String s : attNames){
            cloneAttributes.add(s);
        }

        tree = buildTree(trainingInstances, cloneAttributes);
        tree.report("   ");

       readDataFile(testFile, true);
        double total = 0;
        double correct = 0;
        double count1 = 0;



        for(Instance in : this.testInstances){
            int decision = classifyInstance(in, tree);
            if(decision == 0){
                count1 = count1+1;
            }
            if(decision == in.getCategory()){
                correct = correct + 1;
            }
            total = total + 1;
            System.out.println("Expecting: " + in.getCategory() + " Got: " + decision);
        }
        System.out.println(count1/total);
        System.out.println("Accuracy: " + correct/total);



    }

    private void findBaseValues() {
        int[] tally = new int[this.trainingInstances.size()];
        int count = 0;
        String mostOccurring = null;

        for(Instance in : this.trainingInstances){
            tally[in.getCategory()] = tally[in.getCategory()] + 1;

            if(tally[in.getCategory()] > count){
                count = tally[in.getCategory()];
                mostOccurring = this.categoryNames.get(in.getCategory());
            }
        }

        double prob = (double)count/(double)this.trainingInstances.size();
        baseProbability = prob;
        baseCategory = mostOccurring;
    }

    private int classifyInstance(Instance instance, Node node){
        if(node instanceof LeafNode){

            LeafNode l = (LeafNode)node;
            for(String category : categoryNames){
                if(category.equals(l.getClassName())){
                    return categoryNames.indexOf(category);
                }
            }
        }

        BodyNode n = (BodyNode)node;
        String attr = n.getAttName();

        int i = 0;
        while(i<this.attNames.size()){
            if(attNames.get(i).equalsIgnoreCase(attr)){
                break;
            }
            i = i + 1;
        }

        int attrNum = i;

        if(instance.getAtt(attrNum)){
            //if true
            return classifyInstance(instance, n.getLeft());
        }
        else{
            //if false
            return classifyInstance(instance, n.getRight());
        }


    }

    private Node buildTree(List<Instance> instances, List<String> attributesAll) {
        List<String> attributeNames = new ArrayList<String>();
        for(String s : attributesAll){
            attributeNames.add(s);
        }
        if(instances.isEmpty()) {
            return new LeafNode(baseCategory, baseProbability);
        }
        boolean pure = isPure(instances);
        if(pure){
            return new LeafNode(categoryNames.get(instances.get(0).getCategory()), 1);

        }
        if(attributesAll.isEmpty()){
       // if (!attributesAll.contains(false)) {
            int[] most = getMostCommonClassAndTotal(instances);
            return new LeafNode(categoryNames.get(most[0]),most[1]/instances.size());
        }


        else{
            String bestAtt = null;
            List<Instance> bestTrueInst = new ArrayList<Instance>();
            List<Instance> bestFalseInst = new ArrayList<Instance>();
            double bestPurity = 1;
            double averagePurity = 0;
            for (String attribute : attributesAll){
                int indexOfAttribute = attributeNames.indexOf(attribute);
                List<Instance> trueInst = new ArrayList<Instance>();
                List<Instance> falseInst = new ArrayList<Instance>();

                for(Instance i : instances){
                    if(i.getAtt(indexOfAttribute)){
                        trueInst.add(i);
                    }
                    else{
                        falseInst.add(i);
                    }
                }
                double truePurity = calculatePurity(trueInst);
                double falsePurity = calculatePurity(falseInst);
                averagePurity = calculateWeightedImpurity(trueInst, truePurity, falseInst, falsePurity, instances.size());
                if (averagePurity < bestPurity) {
                    // Update best data
                    bestPurity = averagePurity;
                    bestAtt = attribute;
                    bestTrueInst = trueInst;
                    bestFalseInst = falseInst;
                }
                if(bestAtt==null){
                    bestAtt = attribute;
                }

            }

            attributesAll.remove(bestAtt);
            List<String> leftAttributes = new ArrayList<String>();
            List<String> rightAttributes = new ArrayList<String>();
            for(String attribute : attributesAll){
                leftAttributes.add(attribute);
                rightAttributes.add(attribute);
            }
            Node leftNode = buildTree(bestTrueInst, leftAttributes);
            Node rightNode = buildTree(bestFalseInst, rightAttributes);
            return new BodyNode(bestAtt, leftNode, rightNode);
        }

    }
    /****************
     *HELPER METHODS*
     ***************/

    private double calculateWeightedImpurity(List<Instance> trueInst, double truePurity, List<Instance> falseInst, double falsePurity, int instancesSize) {
        double trueCalculation =  ((double)trueInst.size()/(double)instancesSize*truePurity);
        double falseCalculation = ((double)falseInst.size()/(double)instancesSize*falsePurity);
        return trueCalculation+falseCalculation;
    }

    private double calculatePurity(List<Instance> set){
        int size = 0;
        Set<Instance> successNodes = new HashSet<Instance>();
        Set<Instance> failureNodes = new HashSet<Instance>();
        for (Instance i : set) {
            if (i.getCategory() == 0) {
                successNodes.add(i);
            } else {
                failureNodes.add(i);
            }
            size++;
        }
        if (successNodes.size() == 0 || failureNodes.size() == 0) {
            return 1;
        }
        double papb =
                ((double)successNodes.size() / (double)size)
                        * ((double)failureNodes.size() / (double)size);
        return papb;

    }

    private boolean isPure(List<Instance> instances) {
        if (instances.size() <= 1) {
            return true;
        }
        int prev = instances.get(0).getCategory();
        for(int i = 1; i<instances.size(); i++){
            if(instances.get(i).getCategory()!=prev){
                return false;
            }
        }
        return true;
    }

    /**
     * @param instances
     * @return A 2d array, where index 0 is the index of the most common class name in
     * classifierNames and index 1 is the number of instances of that class.
     */
    private int[] getMostCommonClassAndTotal(Iterable<Instance> instances) {
        int[] classes = new int[categoryNames.size()];
        for (Instance i : instances) {
            classes[i.getCategory()]++;
        }
        int largestIndex = 0;
        for (int i = 0; i < classes.length; i++) {
            if (classes[i] > classes[largestIndex]) {
                largestIndex = i;
            } else if (classes[i] == classes[largestIndex]) {
                largestIndex = (Math.random() > 0.5?largestIndex:i); // I hope this is an OK implementation of the randomness
            }
        }
        int[] back = {largestIndex,classes[largestIndex]};
        return back;
    }

    public void readDataFile(String fname, boolean isTest){
    /* format of names file:
     * names of categories, separated by spaces
     * names of attributes
     * category followed by true's and false's for each instance
     */
        System.out.println("Reading data from file "+fname);
        try {
            Scanner din = new Scanner(new File(fname));

            categoryNames = new ArrayList<String>();
            for (Scanner s = new Scanner(din.nextLine()); s.hasNext();) categoryNames.add(s.next());
            numCategories=categoryNames.size();
            System.out.println(numCategories +" categories");

            attNames = new ArrayList<String>();
            for (Scanner s = new Scanner(din.nextLine()); s.hasNext();) attNames.add(s.next());
            numAtts = attNames.size();
            System.out.println(numAtts +" attributes");
            if(!isTest) {
                trainingInstances = readInstances(din);
                din.close();
                for (Instance ins : trainingInstances) {
                    //System.out.println(ins.toString());
                }
            }
            else{
                testInstances = readInstances(din);
                din.close();
                for (Instance ins : testInstances) {
                    System.out.println(ins.toString());
                }
            }

        }
        catch (IOException e) {
            throw new RuntimeException(e + "Data File caused IO exception");
        }
    }

    private List<Instance> readInstances(Scanner din){
    /* instance = classname and space separated attribute values */
        List<Instance> instances = new ArrayList<Instance>();
        String ln;
        while (din.hasNext()){
            Scanner line = new Scanner(din.nextLine());
            instances.add(new Instance(categoryNames.indexOf(line.next()),line));
        }
        System.out.println("Read " + instances.size()+" instances");
        return instances;
    }


    /****************
     **HELPER CLASS**
     ****************/
    private class Instance {

        private int category;
        private List<Boolean> vals;

        public Instance(int cat, Scanner s){
            category = cat;
            vals = new ArrayList<Boolean>();
            while (s.hasNextBoolean()) vals.add(s.nextBoolean());
        }

        public boolean getAtt(int index){

            return vals.get(index);
        }

        public int getCategory(){

            return category;
        }

        public String toString(){
            StringBuilder ans = new StringBuilder(categoryNames.get(category));
            ans.append(" ");
            for (Boolean val : vals)
                ans.append(val?"true  ":"false ");
            return ans.toString();
        }

    }
}



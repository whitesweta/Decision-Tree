/**
 * Created by shwetabarapatre on 2/04/17.
 */
public class Main {


    public static void main(String[] args) {
        if (args.length == 2){
        DecisionTree decisionTree = new DecisionTree(args[0], args[1]);
    }
        //DecisionTree decisionTree = new DecisionTree("resources/hepatitis-training.dat","resources/hepatitis-test.dat");
    }
}

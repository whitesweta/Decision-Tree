/**
 * Created by shwetabarapatre on 6/04/17.
 */
public class LeafNode implements Node {
    private String className;
    private double probability;

    public LeafNode(String className, double probability){
        this.className = className;
        this.probability = probability;
    }

    @Override
    public void report(String indent) {
        System.out.format("%sClass %s, prob=%4.2f\n", indent, className, this.probability);
    }

    @Override
    public Node getLeft() {
        return null;
    }

    @Override
    public Node getRight() {
        return null;
    }

    public String getClassName(){
        return this.className;
    }
}

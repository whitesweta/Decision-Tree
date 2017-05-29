/**
 * Created by shwetabarapatre on 6/04/17.
 */
public class BodyNode implements Node {

    private final String attName;
    private final Node left;
    private final Node right;

    public BodyNode(String attName, Node left, Node right){
        this.attName = attName;
        this.left = left;
        this.right = right;

    }

    public String getAttName(){
        return attName;
    }

    @Override
    public void report(String indent) {
        System.out.format("%s%s = True:\n",
                indent, attName);
        left.report(indent+" ");
        System.out.format("%s%s = False:\n",
                indent, attName);
        right.report(indent+" ");
    }

    @Override
    public Node getLeft() {
        return left;
    }

    @Override
    public Node getRight() {

        return right;
    }

}

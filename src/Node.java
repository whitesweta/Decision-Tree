import java.util.List;

/**
 * Created by shwetabarapatre on 6/04/17.
 */
public interface Node {
    void report(String indent);

    Node getLeft();
    Node getRight();

}

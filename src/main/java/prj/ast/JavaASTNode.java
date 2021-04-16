package prj.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * The JavaASTNode class represents a single node in an Abstract Syntax Tree.
 * Each node has a set of children node and a parent node both of which can be null.
 */
public class JavaASTNode {

    // The name of the node
    private String name;
    // A textual description of the node in the context it is being used
    private String text;
    private List<JavaASTNode> children;
    private JavaASTNode parent;

    /**
     * Default constructor that initialises the fields of the class.
     * @param name The name of the node.
     * @param text The textual description of the node in the context it is being used.
     */
    public JavaASTNode(String name, String text) {
        this.name = name;
        this.children = new ArrayList<>();
        this.text = text;
    }

    /**
     * This method adds a new child node to the current node.
     * @param node The node to be added as a child.
     */
    public void addChild(JavaASTNode node) {
        // If the child node is null create a "null-node" object
        // for ease of traversing the tree
        if (node == null) {
            node = new JavaASTNode("null-node", "");
        }

        node.setParent(this);
        this.children.add(node);
    }

    /**
     * Getter
     * @return the name of the node.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter
     * @return the text of the node.
     */
    public String getText() {
        return text;
    }

    /**
     * Getter
     * @return the list of children of the node.
     */
    public List<JavaASTNode> getChildren() {
        return children;
    }

    /**
     * Getter
     * @return the parent of the current node.
     */
    public JavaASTNode getParent() {
        return parent;
    }

    /**
     * Setter
     * @param parent The parent of the node to be set.
     */
    public void setParent(JavaASTNode parent) {
        this.parent = parent;
    }
}


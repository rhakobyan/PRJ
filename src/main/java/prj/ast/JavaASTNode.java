package prj.ast;

import java.util.ArrayList;
import java.util.List;

public class JavaASTNode {

    private String name;
    private String text;
    private List<JavaASTNode> children;
    private JavaASTNode parent;

    public JavaASTNode(String name, String text) {
        this.name = name;
        this.children = new ArrayList<>();
        this.text = text;
    }

    public void addChild(JavaASTNode node) {
        if (node == null) {
            System.out.println("adding null node to " + text);
            node = new JavaASTNode("null-node", "");
        }

        node.setParent(this);
        this.children.add(node);
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public List<JavaASTNode> getChildren() {
        return children;
    }

    public JavaASTNode getParent() {
        return parent;
    }

    public void setParent(JavaASTNode parent) {
        this.parent = parent;
    }

    public void printTree() {
        System.out.println("node: " + name);
        System.out.println("children:");
        System.out.println(children.size());
        for (JavaASTNode child : children) {
            if (child != null)
                child.printTree();
        }
    }
}


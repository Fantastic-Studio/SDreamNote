package org.swdc.note.ui.common;

import javax.swing.tree.DefaultMutableTreeNode;
import java.beans.PropertyDescriptor;

/**
 * Created by lenovo on 2018/7/15.
 */
public class TreeNode<T> extends DefaultMutableTreeNode {

    private String name;

    private T item;

    public TreeNode(T object, String nameProp) {
        super(object);
        try {
            PropertyDescriptor pds = new PropertyDescriptor(nameProp, object.getClass());
            this.name = pds.getReadMethod().invoke(object).toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T getUserObject() {
        return (T) super.getUserObject();
    }

    @Override
    public String toString() {
        return name;
    }
}

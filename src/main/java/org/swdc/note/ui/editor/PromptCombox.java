package org.swdc.note.ui.editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * Created by lenovo on 2018/7/15.
 */
public class PromptCombox<E> extends JComboBox<E> {

    private E prompt;

    private boolean promptRemoved;

    public PromptCombox() {
        this.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                setForeground(Color.BLACK);
                if ((getSelectedItem() != null && getSelectedItem() != prompt) || isEditable) {
                    removeItem(prompt);
                    promptRemoved = true;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getSelectedItem() == null || getSelectedItem() == prompt) {
                    setForeground(Color.GRAY);
                    if (promptRemoved) {
                        addItem(prompt);
                        promptRemoved = false;
                    }
                    setSelectedItem(prompt);
                }
            }
        });
    }

    public E getPrompt() {
        return prompt;
    }

    public void setPrompt(E prompt) {
        if (getSelectedItem() == null && !isEditable) {
            setForeground(Color.GRAY);
            this.addItem(prompt);
            this.setSelectedItem(prompt);
        }
        this.prompt = prompt;
    }

    public E getSelectedItem() {
        if (super.getSelectedItem() == null ||
                super.getSelectedItem().toString().equals("")) {
            return null;
        }
        return (E) super.getSelectedItem();
    }

}

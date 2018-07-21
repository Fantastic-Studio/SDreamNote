package org.swdc.note.ui.start;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * 支持prompt的TextField
 */
public class PromptTextField extends JTextField {

    private String prompt = "";

    public PromptTextField() {
        super();
        this.setForeground(Color.GRAY);
        this.setText(prompt);
        this.addFocusListener(new FocusListener() {
            JTextField text = PromptTextField.this;

            @Override
            public void focusGained(FocusEvent e) {
                if (text.getText().trim().equals(prompt)) {
                    text.setForeground(Color.BLACK);
                    text.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (text.getText().equals("")) {
                    text.setForeground(Color.GRAY);
                    text.setText(prompt);
                }
            }
        });
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        if (this.getText().equals("")) {
            this.setText(prompt);
            this.setForeground(Color.GRAY);
        }
        this.prompt = prompt;
    }
}

package org.swdc.note.ui;

import com.hg.xdoc.XDocEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.swdc.note.config.UIConfig;
import org.swdc.note.ui.start.PromptTextField;
import org.swdc.note.ui.start.SWestPane;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * 编辑内容的窗口
 */
@Component
public class EditorForm extends JFrame {

    @Autowired
    private SaveDialog saveDlg;

    @Autowired
    private SWestPane westPane;

    private XDocEditor editor = new XDocEditor();

    private JPanel contentPane = new JPanel();
    private JToolBar toolBar = new JToolBar();
    private JLabel lblTitle = new JLabel("标题：");
    private PromptTextField titleField = new PromptTextField();
    private JButton btnSave = new JButton();

    public EditorForm() throws NoSuchFieldException, IllegalAccessException {
        this.setContentPane(contentPane);
        titleField.setPrompt("在这里输入标题");
        contentPane.setLayout(new BorderLayout());
        contentPane.add(editor, BorderLayout.CENTER);
        contentPane.add(toolBar, BorderLayout.NORTH);
        toolBar.add(lblTitle);
        toolBar.add(titleField);
        toolBar.addSeparator(new Dimension(8, toolBar.getHeight()));
        toolBar.add(btnSave);
        toolBar.addSeparator(new Dimension(8, this.getHeight()));
        toolBar.setFloatable(false);
    }

    @Autowired
    public void setConfig(UIConfig config) throws Exception {
        this.setSize(config.getSubWindowWidth(), config.getSubWindowHeight());
        Font font = config.getFontMini().deriveFont(Font.PLAIN, 16);
        lblTitle.setFont(font);
        titleField.setFont(font);
        ImageIcon iconSaveSmall = new ImageIcon(ImageIO.read(config.getImageSaveSmall().getInputStream()));
        btnSave.setIcon(iconSaveSmall);
    }

    @PostConstruct
    public void regEvent() {
        // 窗口关闭后重新初始化编辑器
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    remove(editor);
                    editor = new XDocEditor();
                    add(editor, BorderLayout.CENTER);
                    titleField.setText(titleField.getPrompt());
                    // 刷新主面板树形控件
                    westPane.dataRefresh();
                    // 刷新保存对话框的分类和标签
                    saveDlg.initData();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        // 保存按钮的处理
        this.btnSave.addActionListener(e -> {
            if (this.titleField.getText().trim().equals("") ||
                    this.titleField.getText().equals(titleField.getPrompt())) {
                JOptionPane.showMessageDialog(this, "记录的标题是不能为空的，一定要先填好他。", "保存", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            saveDlg.showSave(contentPane, editor.getXDoc().toXml(), titleField.getText());
        });
    }

}

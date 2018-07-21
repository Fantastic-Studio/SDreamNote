package org.swdc.note.ui;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.swdc.note.config.UIConfig;
import org.swdc.note.entity.ClipsArtle;
import org.swdc.note.service.ClipsService;
import org.swdc.note.ui.editor.PromptCombox;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

/**
 * 保存的时候的类型选择对话框
 * 摘录保存的时候使用
 */
@Component
public class ClipsSaveDialog extends JDialog {

    @Autowired
    private ClipsService clipsService;

    private JPanel contentPanel = new JPanel();
    private JLabel lblType = new JLabel("分类：");
    private JLabel lblTags = new JLabel("标签：");

    private PromptCombox<String> cbxType = new PromptCombox<>();
    private PromptCombox<String> cbxTags = new PromptCombox<>();

    private JButton btnSave = new JButton("保存记录");

    /**
     * 当前保存的id
     */
    private Long currId;

    public ClipsSaveDialog() {
        this.setContentPane(contentPanel);
        contentPanel.setLayout(null);
        this.setResizable(false);
        this.setSize(400, 200);
        lblType.setBounds(40, 20, 48, 24);
        this.add(lblType);
        cbxType.setBounds(92, 20, 200, 24);
        cbxType.setEditable(true);
        cbxType.setPrompt("键入或选择分类");
        lblTags.setBounds(40, 48, 48, 24);
        this.add(lblTags);
        cbxTags.setEditable(true);
        cbxTags.setPrompt("键入或选择标签");
        cbxTags.setBounds(92, 48, 200, 24);
        this.add(cbxTags);
        this.add(cbxType);
        btnSave.setBounds(120, 80, 120, 28);
        btnSave.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
        btnSave.setForeground(Color.WHITE);
        this.add(btnSave);
        this.setModal(true);
    }

    @Autowired
    public void setConfig(UIConfig config) {
        Font font = config.getFontMini().deriveFont(Font.PLAIN, 16);
        Font fontSmall = config.getFontMini().deriveFont(Font.PLAIN, 14);
        cbxType.setFont(fontSmall);
        lblType.setFont(font);
        lblTags.setFont(font);
        cbxTags.setFont(fontSmall);
        btnSave.setFont(font);
    }

    @PostConstruct
    public void regEvent() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

            }
        });
    }

    @PostConstruct
    public void initData() {
        cbxType.removeAllItems();
        clipsService.getTypeList().forEach(item -> cbxType.addItem(item));
        cbxTags.removeAllItems();
        clipsService.getClipsTags().forEach(item -> cbxTags.addItem(item));
    }

    /**
     * 显示存储记录的窗口
     *
     * @param component  窗口显示的所有者
     * @param xmlContent 待存储的xml数据
     * @param title      记录的标题
     */
    public void showSave(JComponent component, String xmlContent, String title) {
        this.setLocationRelativeTo(component);

        if (currId != null) {
            ClipsArtle artle = clipsService.loadClipArtle(currId);
            cbxTags.setSelectedItem(artle.getTags().getName());
            cbxType.setSelectedItem(artle.getType().getName());
        }

        // 处理保存
        btnSave.addActionListener(e -> {
            if (this.cbxType.getSelectedItem() == null || this.cbxType.getSelectedItem().trim().equals("")) {
                JOptionPane.showMessageDialog(this, "请填写或者选择一个类型，这个不能空下。");
                return;
            }
            // 判别添加和修改
            if (currId == null) {
                if (cbxTags.getSelectedItem() == null || cbxTags.getSelectedItem().trim().equals("")) {
                    clipsService.saveContent("无标签", cbxType.getSelectedItem(), xmlContent, title);
                } else {
                    clipsService.saveContent(cbxTags.getSelectedItem(), cbxType.getSelectedItem(), xmlContent, title);
                }
            } else {
                clipsService.modifyContent(cbxTags.getSelectedItem(), cbxType.getSelectedItem(), xmlContent, title, currId);
            }
            // 清理数据
            this.setVisible(false);
            Arrays.asList(btnSave.getActionListeners()).forEach(item -> btnSave.removeActionListener(item));
            currId = null;
        });
        this.setVisible(true);
    }

    /**
     * 打开对话框前的数据准备
     *
     * @param id
     */
    public void prepare(Long id) {
        this.currId = id;
    }
}

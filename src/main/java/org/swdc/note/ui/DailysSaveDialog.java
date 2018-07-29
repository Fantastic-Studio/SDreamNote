package org.swdc.note.ui;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.swdc.note.config.UIConfig;
import org.swdc.note.entity.ClipsArtle;
import org.swdc.note.entity.DailyArtle;
import org.swdc.note.service.DailyService;
import org.swdc.note.ui.editor.PromptCombox;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

/**
 * 保存的对话框
 * 日记保存的时候使用
 */
@Component
public class DailysSaveDialog extends JDialog {

    @Autowired
    private DailyService dailyService;

    private JPanel contentPanel = new JPanel();
    private JLabel lblTags = new JLabel("标签：");
    private JLabel lblQuest = new JLabel("问题：");
    private JLabel lblAnswer = new JLabel("答案：");
    private JCheckBox chkUseQuest = new JCheckBox("不需要验证身份");

    private JTextField txtQuest = new JTextField();
    private JTextField txtAnswer = new JTextField();

    private PromptCombox<String> cbxTags = new PromptCombox<>();

    private JButton btnSave = new JButton("保存记录");

    /**
     * 当前保存的id
     */
    private Long currId;

    public DailysSaveDialog() {
        this.setContentPane(contentPanel);
        contentPanel.setLayout(null);
        this.setResizable(false);
        this.setSize(400, 280);
        lblTags.setBounds(40, 28, 48, 24);
        this.add(lblTags);
        cbxTags.setEditable(true);
        cbxTags.setPrompt("键入或选择标签");
        cbxTags.setBounds(92, 28, 200, 24);
        this.add(cbxTags);
        lblQuest.setBounds(40, 60, 48, 20);
        this.add(lblQuest);
        txtQuest.setBounds(92, 60, 200, 24);
        this.add(txtQuest);
        lblAnswer.setBounds(40, 92, 48, 24);
        this.add(lblAnswer);
        txtAnswer.setBounds(92, 92, 200, 24);
        this.add(txtAnswer);
        chkUseQuest.setBounds(40, 120, 200, 24);
        this.add(chkUseQuest);
        btnSave.setBounds(120, 160, 120, 28);
        btnSave.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
        btnSave.setForeground(Color.WHITE);
        this.add(btnSave);
        this.setModal(true);
    }

    @Autowired
    public void setConfig(UIConfig config) {
        Font font = config.getFontMini().deriveFont(Font.PLAIN, 16);
        Font fontSmall = config.getFontMini().deriveFont(Font.PLAIN, 14);
        lblTags.setFont(font);
        cbxTags.setFont(fontSmall);
        lblQuest.setFont(font);
        txtQuest.setFont(fontSmall);
        txtAnswer.setFont(fontSmall);
        lblAnswer.setFont(font);
        chkUseQuest.setFont(font);
        btnSave.setFont(font);
    }

    @PostConstruct
    public void regEvent() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

            }
        });
        chkUseQuest.addChangeListener(e -> {
            txtAnswer.setEnabled(!chkUseQuest.isSelected());
            txtQuest.setEnabled(!chkUseQuest.isSelected());
        });
    }

    @PostConstruct
    public void initData() {
        cbxTags.removeAllItems();
        dailyService.getDailyTags().forEach(item -> cbxTags.addItem(item));
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
            //ClipsArtle artle = dailyService.loadClipArtle(currId);
            // cbxTags.setSelectedItem(artle.getTags().getName());
        }

        // 处理保存
        btnSave.addActionListener(e -> {
            // 判别添加和修改
            if (currId == null) {
                // 无需验证的情况
                if (chkUseQuest.isSelected()) {
                    if (cbxTags.getSelectedItem() == null || cbxTags.getSelectedItem().trim().equals("")) {
                        dailyService.saveContent("无标签", xmlContent, title, null, null);
                    } else {
                        dailyService.saveContent(cbxTags.getSelectedItem(), xmlContent, title, null, null);
                    }
                } else {
                    if (txtQuest.getText().trim().equals("") || txtAnswer.getText().trim().equals("")) {
                        JOptionPane.showMessageDialog(DailysSaveDialog.this, "请填写身份验证问题。");
                        return;
                    }
                    if (cbxTags.getSelectedItem() == null || cbxTags.getSelectedItem().trim().equals("")) {
                        dailyService.saveContent("无标签", xmlContent, title, txtQuest.getText(), txtAnswer.getText());
                    } else {
                        dailyService.saveContent(cbxTags.getSelectedItem(), xmlContent, title, txtQuest.getText(), txtAnswer.getText());
                    }
                }

            } else {
                if (chkUseQuest.isSelected()) {
                    if (cbxTags.getSelectedItem() == null || cbxTags.getSelectedItem().trim().equals("")) {
                        dailyService.modifyContent(currId, "无标签", xmlContent, title, null, null);
                    } else {
                        dailyService.modifyContent(currId, cbxTags.getSelectedItem(), xmlContent, title, null, null);
                    }
                } else {
                    if (txtQuest.getText().trim().equals("") || txtAnswer.getText().trim().equals("")) {
                        JOptionPane.showMessageDialog(DailysSaveDialog.this, "请填写身份验证问题。");
                        return;
                    }
                    if (cbxTags.getSelectedItem() == null || cbxTags.getSelectedItem().trim().equals("")) {
                        dailyService.modifyContent(currId, "无标签", xmlContent, title, txtQuest.getText(), txtAnswer.getText());
                    } else {
                        dailyService.modifyContent(currId, cbxTags.getSelectedItem(), xmlContent, title, txtQuest.getText(), txtAnswer.getText());
                    }
                }

                //clipsService.modifyContent(cbxTags.getSelectedItem(), cbxType.getSelectedItem(), xmlContent, title, currId);
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
        DailyArtle artle = dailyService.loadContent(id);
        txtQuest.setText(artle.getCheckQuestion());
    }
}

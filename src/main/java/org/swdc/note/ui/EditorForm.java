package org.swdc.note.ui;

import com.hg.xdoc.XDoc;
import com.hg.xdoc.XDocEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.swdc.note.config.UIConfig;
import org.swdc.note.entity.ClipsArtle;
import org.swdc.note.entity.ClipsContent;
import org.swdc.note.entity.GlobalType;
import org.swdc.note.service.ClipsService;
import org.swdc.note.ui.start.PromptTextField;
import org.swdc.note.ui.start.SCenterPane;
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
    private ClipsSaveDialog clipsSaveDialog;

    @Autowired
    private SWestPane westPane;

    @Autowired
    private ClipsService clipsService;

    @Autowired
    private SCenterPane centerPane;

    private XDocEditor editor = new XDocEditor();

    private JPanel contentPane = new JPanel();
    private JToolBar toolBar = new JToolBar();
    private JLabel lblTitle = new JLabel("标题：");
    private PromptTextField titleField = new PromptTextField();
    private JButton btnSave = new JButton();

    /**
     * 当前编辑的id
     */
    private Long currId;
    /**
     * 当前编辑的全局类型
     */
    private GlobalType currType;

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
                    clipsSaveDialog.initData();
                    centerPane.refreshItems();
                    // 清理数据
                    currId = null;
                    currType = null;
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
            // 根据类型选择不同的存储对话框
            switch (currType) {
                case CLIPS:
                    clipsSaveDialog.showSave(contentPane, editor.getXDoc().toXml(), titleField.getText());
                    break;
            }
        });
    }

    /**
     * 准备数据，在展示窗口前，为窗口传入需要的数据。
     *
     * @param artleId 记录的id
     */
    public void prepare(Long artleId, GlobalType type) throws Exception {
        if (type == null) {
            type = westPane.getCurrentGlobalType();
        }
        switch (type) {
            case CLIPS:
                this.currId = artleId;
                this.currType = type;
                if (artleId != null) {
                    ClipsArtle artle = clipsService.loadClipArtle(artleId);
                    ClipsContent content = artle.getContent();
                    editor.setXDoc(new XDoc(content.getContent()));
                    titleField.setText(artle.getTitle());
                }
                // 初始化摘录的存储窗口
                clipsSaveDialog.prepare(artleId);
                break;
        }
    }

}

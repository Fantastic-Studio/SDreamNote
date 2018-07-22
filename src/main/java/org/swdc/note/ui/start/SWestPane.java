package org.swdc.note.ui.start;

import com.l2fprod.common.swing.JTaskPane;
import com.l2fprod.common.swing.JTaskPaneGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.swdc.note.config.UIConfig;
import org.swdc.note.entity.ClipsType;
import org.swdc.note.entity.GlobalType;
import org.swdc.note.entity.Tags;
import org.swdc.note.service.ClipsService;
import org.swdc.note.ui.common.DatePanel;
import org.swdc.note.ui.common.TreeNode;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * 描述主窗口左侧的面板
 */
@Component
public class SWestPane extends JPanel {

    private JTabbedPane tabbedPane = new JTabbedPane();

    private JTree typeTree = new JTree();
    private JScrollPane scrollPane = new JScrollPane(typeTree);

    private JTaskPane taskPane = new JTaskPane();
    private JScrollPane taskScrollPane = new JScrollPane(taskPane);
    private JTaskPaneGroup taskGroup = new JTaskPaneGroup();

    private DatePanel datePanel = new DatePanel();

    @Autowired
    private ClipsService clipsService;

    /**
     * 树节点改变的时候会影响中间的面板，
     * 需要通知他刷新
     */
    @Autowired
    private SCenterPane centerPane;

    public SWestPane() {
        this.setLayout(new BorderLayout());
        tabbedPane.add("摘录", scrollPane);
        tabbedPane.add("日记", datePanel);
        typeTree.setRootVisible(false);
        this.add(tabbedPane, BorderLayout.NORTH);
        this.add(taskScrollPane, BorderLayout.CENTER);
    }

    @Autowired
    public void setConfig(UIConfig config) throws Exception {
        Font font = config.getFontMini().deriveFont(Font.PLAIN, 14);
        typeTree.setFont(font);
        tabbedPane.setFont(font);
        taskGroup.setTitle("书简");
        taskGroup.setFont(font);
        datePanel.setFonts(font);
        this.taskPane.add(taskGroup);
    }

    @PostConstruct
    public void regListener() {
        // 重新计算界面组件的位置
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                SWestPane pane = SWestPane.this;
                tabbedPane.setPreferredSize(new Dimension(pane.getWidth(), (pane.getHeight() / 12) * 7));
                taskScrollPane.setPreferredSize(new Dimension(pane.getWidth(), (pane.getHeight() / 12) * 5));
                datePanel.setPreferredSize(new Dimension(pane.getWidth(), (pane.getHeight() / 12) * 7));
            }
        });
        // 监听摘录的树的选择，更新中心面板
        typeTree.addTreeSelectionListener(e -> {
            if (e.getOldLeadSelectionPath() == null || e.getNewLeadSelectionPath() == null) {
                return;
            }
            Object obj = e.getNewLeadSelectionPath().getLastPathComponent();
            if (obj instanceof TreeNode) {
                TreeNode node = (TreeNode) obj;
                if (node.getUserObject() instanceof Tags) {
                    ClipsType type = (ClipsType) ((TreeNode) e.getOldLeadSelectionPath().getLastPathComponent()).getUserObject();
                    centerPane.loadItemsOfClips(type, (Tags) node.getUserObject());
                }
            }
        });
        datePanel.addSelectListener(e -> {
            System.out.println(e.getSelectDate());
        });
    }

    @PostConstruct
    public void dataRefresh() {
        typeTree.setModel(new DefaultTreeModel(clipsService.getClipTree()));
    }

    /**
     * 返回全局类型
     *
     * @return
     */
    public GlobalType getCurrentGlobalType() {
        String name = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
        switch (name) {
            case "摘录":
                return GlobalType.CLIPS;
        }
        return null;
    }

}

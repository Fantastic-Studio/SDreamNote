package org.swdc.note.ui.start;

import com.l2fprod.common.swing.JTaskPane;
import com.l2fprod.common.swing.JTaskPaneGroup;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.swdc.note.config.UIConfig;
import org.swdc.note.entity.ClipsType;
import org.swdc.note.entity.GlobalType;
import org.swdc.note.entity.Tags;
import org.swdc.note.service.ClipsService;
import org.swdc.note.ui.common.DatePanel;
import org.swdc.note.ui.common.TreeNode;
import org.swdc.note.ui.listener.DataRefreshEvent;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

    @Getter
    private static GlobalType currType;

    @Autowired
    private ClipsService clipsService;

    @Autowired
    private ApplicationContext context;

    public SWestPane() {
        this.setLayout(new BorderLayout());
        tabbedPane.add("摘录", scrollPane);
        tabbedPane.add("日记", datePanel);
        typeTree.setRootVisible(false);
        this.add(tabbedPane, BorderLayout.NORTH);
        this.add(taskScrollPane, BorderLayout.CENTER);
        currType = GlobalType.CLIPS;
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
        typeTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Object obj = typeTree.getLastSelectedPathComponent();
                if (obj instanceof TreeNode) {
                    TreeNode node = (TreeNode) obj;
                    if (node.getUserObject() instanceof Tags) {
                        ClipsType type = (ClipsType) ((TreeNode) node.getParent()).getUserObject();
                        // 发布刷新事件，刷新表格
                        DataRefreshEvent dataRefreshEvent = new DataRefreshEvent(DataRefreshEvent.EventOf.REF_TABLE);
                        dataRefreshEvent.setTags((Tags) node.getUserObject());
                        dataRefreshEvent.setGlobalType(GlobalType.CLIPS);
                        dataRefreshEvent.setClipsType(type);
                        context.publishEvent(dataRefreshEvent);
                    }
                }
            }
        });
        datePanel.addSelectListener(e -> {
            // 发布事件，刷新表格
            DataRefreshEvent event = new DataRefreshEvent(DataRefreshEvent.EventOf.REF_TABLE);
            event.setGlobalType(GlobalType.DELAY);
            event.setDate(e.getSelectDate());
            context.publishEvent(event);
        });
        tabbedPane.addChangeListener(e -> currType = getCurrentGlobalType());
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
    private GlobalType getCurrentGlobalType() {
        String name = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
        switch (name) {
            case "摘录":
                return GlobalType.CLIPS;
            case "日记":
                return GlobalType.DELAY;
        }
        return null;
    }

}

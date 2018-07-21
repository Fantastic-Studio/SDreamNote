package org.swdc.note.ui.start;

import com.hg.xdoc.XDoc;
import com.hg.xdoc.XDocViewer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.swdc.note.config.UIConfig;
import org.swdc.note.entity.*;
import org.swdc.note.service.ClipsService;
import org.swdc.note.ui.common.TableModel;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.List;

/**
 * 主窗口的中心面板，
 * 有记录列表和预览界面
 * <p>
 * 注意，表格模型第一列默认为id。
 */
@Component
public class SCenterPane extends JPanel {

    private JTable table = new JTable() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private JScrollPane scrollPane = new JScrollPane(table);

    private XDocViewer docViewer = new XDocViewer();

    @Autowired
    private ClipsService clipsService;

    /**
     * 主窗口的工具栏
     */
    @Autowired
    private SToolBar toolBar;

    /**
     * 当前展示的数据的全局类型
     */
    private GlobalType currType;

    public SCenterPane() {
        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.NORTH);
        this.add(docViewer, BorderLayout.CENTER);
        docViewer.setOpenEnable(false);
        docViewer.setSaveEnable(false);
        // 寻找xdoc里面的工具栏
        Arrays.asList(docViewer.getComponents()).stream()
                .filter(item -> item instanceof JToolBar).findFirst().ifPresent(item -> {
            // 移除幻灯片视图等不使用的按钮
            JToolBar tool = (JToolBar) item;
            int length = tool.getComponents().length;
            tool.remove(length - 1);
            tool.remove(length - 3);
            tool.remove(4);
            tool.setFloatable(false);
        });
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * 摘录类别的Tree的选择发生了改变，
     * 会调用此方法以更新table，显示他的记录
     *
     * @param type
     * @param tag
     */
    public void loadItemsOfClips(ClipsType type, Tags tag) {
        currType = GlobalType.CLIPS;
        List<ClipsArtle> artles = clipsService.getArtlesOf(type, tag);
        TableModel tableModel = new TableModel(ClipsArtle.class);
        tableModel.setColumnIdentifiers(new String[]{
                "序列ID",
                "标题",
                "标签",
                "收录时间"
        });
        artles.forEach(item ->
                tableModel.addRow(new String[]{
                        item.getId().toString(),
                        item.getTitle(),
                        item.getTags().getName(),
                        item.getCreateDate().toString()
                }));
        this.table.setModel(tableModel);
    }

    public void loadContent(Long id) {
        try {
            switch (currType) {
                case CLIPS:
                    ClipsContent content = clipsService.loadContent(id);
                    XDoc doc = new XDoc(content.getContent());
                    docViewer.open(doc);
                    break;
                default:
                    return;
            }
        } catch (Exception e) {

        }
    }

    @Autowired
    public void setConfig(UIConfig config) {
        Font font = config.getFontMini().deriveFont(Font.PLAIN, 14);
        table.setFont(font);
        table.getTableHeader().setFont(font);
    }

    /**
     * 监听大小变化，重新计算组件位置
     */
    @PostConstruct
    public void regListener() {
        toolBar.enableItemsTool(false, null, null);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                SCenterPane pane = SCenterPane.this;
                scrollPane.setPreferredSize(new Dimension(pane.getWidth(), (pane.getHeight() / 12) * 6));
                docViewer.setPreferredSize(new Dimension(pane.getWidth(), (pane.getHeight() / 12) * 6));
            }
        });
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row == -1) {
                    toolBar.enableItemsTool(false, null, null);
                    return;
                }
                Long id = Long.valueOf(table.getModel().getValueAt(row, 0).toString());
                toolBar.enableItemsTool(true, currType, id);
                if (e.getClickCount() == 2) {
                    loadContent(id);
                }
            }
        });
        table.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                int row = table.getSelectedRow();
                if (row != -1 && !toolBar.isFocused()) {
                    table.clearSelection();
                    toolBar.enableItemsTool(false, null, null);
                }
            }
        });
    }
}

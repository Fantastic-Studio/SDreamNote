package org.swdc.note.ui.start;

import com.hg.xdoc.XDoc;
import com.hg.xdoc.XDocViewer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.swdc.note.config.BCrypt;
import org.swdc.note.config.UIConfig;
import org.swdc.note.entity.*;
import org.swdc.note.service.ClipsService;
import org.swdc.note.service.DailyService;
import org.swdc.note.ui.common.TableModel;
import org.swdc.note.ui.listener.DataRefreshEvent;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
    private ApplicationContext context;

    @Autowired
    private ClipsService clipsService;

    @Autowired
    private DailyService dailyService;

    private Date selCurrDate;

    /**
     * 当前展示的摘录的类型
     */
    private ClipsType clipsType;

    /**
     * 当前展示的标签
     */
    private Tags tags;

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
        tags = tag;
        clipsType = type;
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

    /**
     * 日记类别的选择改变后，调用此方法加载列表。
     */
    public void loadItemsOfDaily(Date selDate) {
        this.selCurrDate = selDate;
        this.currType = GlobalType.DELAY;
        List<DailyArtle> artles = dailyService.getArtlesOf(selDate);
        TableModel model = new TableModel(DailyArtle.class);
        model.setColumnIdentifiers(new String[]{
                "序列ID", "标题", "收录时间"
        });
        artles.forEach(item ->
                model.addRow(new String[]{
                        item.getId() + "",
                        item.getTitle(),
                        item.getDateCreated().toString()
                }));
        table.setModel(model);
    }

    /**
     * 刷新列表
     */
    public void refreshItems() {
        if (currType == null) {
            currType = SWestPane.getCurrType();
        }
        switch (this.currType) {
            case CLIPS:
                if (clipsType != null && tags != null) {
                    loadItemsOfClips(this.clipsType, this.tags);
                    table.clearSelection();
                }
                break;
            case DELAY:
                loadItemsOfDaily(Optional.ofNullable(selCurrDate).orElse(new Date()));
                table.clearSelection();
                break;
        }
    }

    /**
     * 为主面板下方的内容面板载入数据
     *
     * @param id  被加载的目标的id
     * @param pwd 被加载目标的验证问题 不写的话会自己弹框问
     */
    public void loadContent(Long id, String pwd) {
        try {

            switch (currType) {
                case CLIPS:
                    Optional.ofNullable(clipsService.loadContent(id)).ifPresent(artle -> {
                        try {
                            XDoc doc = new XDoc(artle.getContent());
                            docViewer.open(doc);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                    break;
                case DELAY:
                    Optional.ofNullable(dailyService.loadContent(id)).ifPresent(artle -> {
                        try {
                            if (artle.getCheckQuestion() == null || artle.getCheckQuestion().equals("")) {
                                XDoc doc = new XDoc(artle.getContent().getContent());
                                docViewer.open(doc);
                            } else if (pwd != null) {
                                if (BCrypt.checkpw(pwd, artle.getAnswer())) {
                                    XDoc doc = new XDoc(artle.getContent().getContent());
                                    docViewer.open(doc);
                                }
                            } else {
                                String answer = JOptionPane.showInputDialog(SCenterPane.this, "请问：" + artle.getCheckQuestion() + "?");
                                if (BCrypt.checkpw(answer, artle.getAnswer())) {
                                    XDoc doc = new XDoc(artle.getContent().getContent());
                                    docViewer.open(doc);
                                }
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
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

    public void exportContent() {
        docViewer.save();
    }

    /**
     * 监听大小变化，重新计算组件位置
     */
    @PostConstruct
    public void regListener() {
        DataRefreshEvent refreshEvent = new DataRefreshEvent(DataRefreshEvent.EventOf.REF_TOOL);
        refreshEvent.setGlobalType(null);
        refreshEvent.setId(null);
        refreshEvent.setEnable(false);
        context.publishEvent(refreshEvent);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                SCenterPane pane = SCenterPane.this;
                scrollPane.setPreferredSize(new Dimension(pane.getWidth(), (pane.getHeight() / 12) * 6));
                docViewer.setPreferredSize(new Dimension(pane.getWidth(), (pane.getHeight() / 12) * 6));
            }
        });
        // table的鼠标事件
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row == -1) {
                    DataRefreshEvent refreshEvent = new DataRefreshEvent(DataRefreshEvent.EventOf.REF_TOOL);
                    refreshEvent.setGlobalType(null);
                    refreshEvent.setId(null);
                    refreshEvent.setEnable(false);
                    context.publishEvent(refreshEvent);
                    return;
                }
                Long id = Long.valueOf(table.getModel().getValueAt(row, 0).toString());
                DataRefreshEvent refreshEvent = new DataRefreshEvent(DataRefreshEvent.EventOf.REF_TOOL);
                refreshEvent.setGlobalType(currType);
                refreshEvent.setId(id);
                refreshEvent.setEnable(true);
                context.publishEvent(refreshEvent);
                if (e.getClickCount() == 2) {
                    loadContent(id, null);
                }
            }
        });
        // 焦点事件
        table.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                int row = table.getSelectedRow();
                if (row != -1 && !SToolBar.isFocused()) {
                    table.clearSelection();
                    DataRefreshEvent refreshEvent = new DataRefreshEvent(DataRefreshEvent.EventOf.REF_TOOL);
                    refreshEvent.setGlobalType(null);
                    refreshEvent.setId(null);
                    refreshEvent.setEnable(false);
                    context.publishEvent(refreshEvent);
                }
            }
        });
    }
}

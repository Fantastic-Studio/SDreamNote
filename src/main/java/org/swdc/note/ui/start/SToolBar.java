package org.swdc.note.ui.start;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.swdc.note.config.UIConfig;
import org.swdc.note.entity.ClipsContent;
import org.swdc.note.entity.GlobalType;
import org.swdc.note.service.ClipsService;
import org.swdc.note.ui.EditorForm;
import org.swdc.note.ui.ReadFrm;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.*;
import java.util.Arrays;

/**
 * 主窗口的工具栏
 */
@Component
public class SToolBar extends JToolBar {

    private JButton addBtn = new JButton();
    private JButton exportBtn = new JButton();
    private JButton newWindowBtn = new JButton();
    private JButton editBtn = new JButton();
    private JButton deleteBtn = new JButton();
    private JButton searchBtn = new JButton();
    private JButton hideBtn = new JButton();
    private PromptTextField searchField = new PromptTextField();

    /**
     * 用于工具栏的生效判断，如果表格失去焦点的同时
     * 焦点位于工具栏，那么应该让表格选择的行继续有效
     * 如果焦点既不在工具栏也不在表格，那么就应该清除
     * 表格当前选择的行，同时禁用工具栏和选中项相关的
     * 按钮。
     * <p>
     * 这个判断标志主要用于中心面板SCenterPane
     */
    private boolean focused;

    @Autowired
    private EditorForm editorForm;

    @Autowired
    private ReadFrm readFrm;

    @Autowired
    private ClipsService clipsService;

    private GlobalType currType;

    private Long currId;

    public SToolBar() {
        this.setFloatable(false);
        this.addSeparator(new Dimension(8, this.getHeight()));
        this.add(addBtn);
        this.add(exportBtn);
        this.add(newWindowBtn);
        this.add(editBtn);
        this.add(deleteBtn);
        this.addSeparator(new Dimension(20, this.getHeight()));
        searchField.setPrompt("输入关键字开始搜索");
        this.add(searchField);
        this.add(searchBtn);
        this.add(hideBtn);
        this.addSeparator(new Dimension(8, this.getHeight()));
    }

    @Autowired
    public void setConfig(UIConfig config) throws Exception {
        ImageIcon iconAdd = new ImageIcon(ImageIO.read(config.getImageAdd().getInputStream()));
        this.addBtn.setIcon(iconAdd);
        ImageIcon iconExport = new ImageIcon(ImageIO.read(config.getImageExport().getInputStream()));
        this.exportBtn.setIcon(iconExport);
        ImageIcon iconWindow = new ImageIcon(ImageIO.read(config.getImageFullScreen().getInputStream()));
        this.newWindowBtn.setIcon(iconWindow);
        ImageIcon iconEdit = new ImageIcon(ImageIO.read(config.getImageEdit().getInputStream()));
        this.editBtn.setIcon(iconEdit);
        ImageIcon iconDel = new ImageIcon(ImageIO.read(config.getImageDelete().getInputStream()));
        this.deleteBtn.setIcon(iconDel);
        ImageIcon iconSearch = new ImageIcon(ImageIO.read(config.getImageSearch().getInputStream()));
        this.searchBtn.setIcon(iconSearch);
        Font font = config.getFontMini().deriveFont(Font.PLAIN, 18);
        this.searchField.setFont(font);
        ImageIcon iconHide = new ImageIcon(ImageIO.read(config.getImageHide().getInputStream()));
        this.hideBtn.setIcon(iconHide);
    }

    @PostConstruct
    public void regListener() {
        // 鼠标按钮上面，置聚焦标记为真
        Arrays.asList(this.getComponents()).stream()
                .filter(item -> item instanceof JButton)
                .forEach(item -> item.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        focused = true;
                    }
                }));
        // 鼠标在工具栏上面，聚焦标记为真
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                focused = true;
            }

            // 鼠标从工具栏移出，标记聚焦为假
            @Override
            public void mouseExited(MouseEvent e) {
                focused = false;
            }
        });
    }

    @PostConstruct
    public void toolEvents() {
        // 新建窗口
        this.addBtn.addActionListener(e -> {
            try {
                // 初始化编辑窗口为创建
                editorForm.prepare(null, this.currType);
                editorForm.setVisible(true);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        // 新窗口阅读
        newWindowBtn.addActionListener(e -> {
            try {
                switch (currType) {
                    case CLIPS:
                        ClipsContent content = clipsService.loadContent(currId);
                        readFrm.prepare(content.getContent());
                        readFrm.setVisible(true);
                        break;
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        // 编辑或修改
        editBtn.addActionListener(e -> {
            try {
                // 初始化编辑窗口
                editorForm.prepare(currId, currType);
                editorForm.setVisible(true);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    /**
     * 启用工具栏
     *
     * @param enable 是否启用
     * @param type   被操作的目标的全局类型
     * @param id     被操作目标的id
     */
    public void enableItemsTool(boolean enable, GlobalType type, Long id) {
        this.deleteBtn.setEnabled(enable);
        this.newWindowBtn.setEnabled(enable);
        this.editBtn.setEnabled(enable);
        this.exportBtn.setEnabled(enable);
        this.currType = type;
        this.currId = id;
    }

    public boolean isFocused() {
        return focused;
    }

}

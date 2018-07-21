package org.swdc.note.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.swdc.note.config.UIConfig;
import org.swdc.note.service.ClipsService;
import org.swdc.note.ui.start.SCenterPane;
import org.swdc.note.ui.start.SToolBar;
import org.swdc.note.ui.start.SWestPane;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * 主窗口
 */
@Component
public class StartForm extends JFrame {

    private JPanel contentPanel = new JPanel();

    @Autowired
    private SWestPane westPane;
    @Autowired
    private SCenterPane centerPane;
    @Autowired
    private SToolBar toolBar;

    public StartForm() {
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setContentPane(contentPanel);
        contentPanel.setLayout(new BorderLayout());
    }

    /**
     * spring注入配置文件，计算窗体宽高，
     * 初始化各个控件
     *
     * @param config
     */
    @Autowired
    public void setConfig(UIConfig config) {
        this.setSize(config.getWindowWidth(), config.getWindowHeight());
        this.westPane.setPreferredSize(new Dimension(config.getWindowWidth() / 4, config.getWindowHeight()));
        this.contentPanel.add(this.westPane, BorderLayout.WEST);
        this.contentPanel.add(this.centerPane, BorderLayout.CENTER);
        this.contentPanel.add(this.toolBar, BorderLayout.NORTH);
    }

    /**
     * 添加窗口的监听，在窗口大小变化的
     * 时候重新计算组件位置
     */
    @PostConstruct
    public void regListener() {
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                StartForm frm = StartForm.this;
                westPane.setPreferredSize(new Dimension(westPane.getWidth(), frm.getHeight() - toolBar.getHeight()));
                centerPane.setPreferredSize(new Dimension(frm.getWidth() - westPane.getWidth(), frm.getHeight() - toolBar.getHeight()));
            }
        });
    }

    /**
     * 注册事件处理
     */
    @PostConstruct
    public void events() {
        //System.out.println("event");
    }

}

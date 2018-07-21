package org.swdc.note.ui;

import com.hg.xdoc.XDoc;
import com.hg.xdoc.XDocViewer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.swdc.note.config.UIConfig;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

/**
 * 阅读窗口，工具栏上面可以打开窗口阅读
 */
@Component
public class ReadFrm extends JFrame {

    private XDocViewer docViewer = new XDocViewer();
    private JPanel contentPanel = new JPanel();
    private JToolBar toolBar = new JToolBar();

    public ReadFrm() {
        this.setContentPane(contentPanel);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(toolBar, BorderLayout.NORTH);
        contentPanel.add(docViewer, BorderLayout.CENTER);
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
    }

    @Autowired
    public void setConfig(UIConfig config) {
        this.setSize(config.getSubWindowWidth(), config.getSubWindowHeight());
    }

    @PostConstruct
    public void regEvents() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    docViewer.open(new XDoc(""));
                } catch (Exception ex) {

                }
            }
        });
    }

    public void prepare(String content) throws Exception {
        docViewer.open(new XDoc(content));
    }

}

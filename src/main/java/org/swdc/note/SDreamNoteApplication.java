package org.swdc.note;

import com.l2fprod.common.swing.plaf.LookAndFeelAddons;
import com.l2fprod.common.swing.plaf.windows.WindowsLookAndFeelAddons;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.swdc.note.ui.StartForm;

import javax.swing.*;

@SpringBootApplication
public class SDreamNoteApplication implements ApplicationRunner {

    @Autowired
    private StartForm startForm;

    public static void main(String[] args) {
        try {
            // 启动Look&Feel的ui
            BeautyEyeLNFHelper.launchBeautyEyeLNF();
            // 防止输入法切换的时候白屏
            System.setProperty("sun.java2d.noddraw", "true");
            // 配置Look&Feel
            UIManager.put("RootPane.setupButtonVisible", false);
            // TaskPanel的配置
            UIManager.put("win.xpstyle.name", "metallic");
            LookAndFeelAddons.setAddon(WindowsLookAndFeelAddons.class);
            // 启动应用
            SpringApplicationBuilder appBuilder = new SpringApplicationBuilder(SDreamNoteApplication.class);
            appBuilder.headless(false).run(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        this.startForm.setVisible(true);
    }
}

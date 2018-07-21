package org.swdc.note.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.awt.*;

/**
 * 配置和界面资源。
 * 提供各种资源供系统使用。
 */
@Configuration
@PropertySource("file:configure.properties")
@Data
public class UIConfig {

    @Value("${cfg.window.width}")
    private Integer windowWidth;

    @Value("${cfg.window.height}")
    private Integer windowHeight;

    @Value("${cfg.subWindow.width}")
    private Integer subWindowWidth;

    @Value("${cfg.subWindow.height}")
    private Integer subWindowHeight;

    private ClassPathResource imageAdd;
    private ClassPathResource imageExport;
    private ClassPathResource imageFullScreen;
    private ClassPathResource imageEdit;
    private ClassPathResource imageDelete;
    private ClassPathResource imageSearch;
    private ClassPathResource imageHide;
    private ClassPathResource imageIcon;
    private ClassPathResource imageType;

    private ClassPathResource imageSaveSmall;

    private Font fontMini;

    @PostConstruct
    protected void initResources() throws Exception {
        imageAdd = new ClassPathResource("/icon/add.png");
        imageFullScreen = new ClassPathResource("/icon/fullscreen.png");
        imageExport = new ClassPathResource("/icon/save.png");
        imageEdit = new ClassPathResource("/icon/edit.png");
        imageDelete = new ClassPathResource("/icon/delete.png");
        imageSearch = new ClassPathResource("/icon/search.png");
        imageHide = new ClassPathResource("/icon/hide.png");
        imageSaveSmall = new ClassPathResource("/icon/save-small.png");
        imageType = new ClassPathResource("icon/type.png");
        fontMini = Font.createFont(Font.TRUETYPE_FONT, new ClassPathResource("font/mini.TTF").getInputStream());
    }

}

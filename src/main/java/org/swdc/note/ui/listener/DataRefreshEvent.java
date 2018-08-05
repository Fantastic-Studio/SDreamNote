package org.swdc.note.ui.listener;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import org.swdc.note.entity.*;

import java.util.Date;

/**
 * 界面刷新监听
 */
public class DataRefreshEvent extends ApplicationEvent {

    @Getter
    @Setter
    private GlobalType globalType;
    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private ClipsType clipsType;
    @Getter
    @Setter
    private ClipsContent clipsContent;
    @Getter
    @Setter
    private DailyArtle dailyArtle;
    @Getter
    @Setter
    private Tags tags;
    @Getter
    @Setter
    private Date date;
    @Getter
    @Setter
    private Boolean enable;

    public enum EventOf {
        REF_TABLE, REF_TREE, REF_TOOL
    }

    public DataRefreshEvent(Object source) {
        super(source);
    }
}

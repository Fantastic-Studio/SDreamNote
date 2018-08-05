package org.swdc.note.ui.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.swdc.note.ui.start.SCenterPane;
import org.swdc.note.ui.start.SToolBar;
import org.swdc.note.ui.start.SWestPane;

/**
 * Created by lenovo on 2018/8/3.
 */
@Component
public class DataRefreshListener implements ApplicationListener<DataRefreshEvent> {

    @Autowired
    private SCenterPane centerPane;

    @Autowired
    private SToolBar toolBar;

    @Autowired
    private SWestPane westPane;

    @Override
    public void onApplicationEvent(DataRefreshEvent dataRefreshEvent) {
        DataRefreshEvent.EventOf event = (DataRefreshEvent.EventOf) dataRefreshEvent.getSource();
        switch (event) {
            // 刷新中心的表格
            case REF_TABLE:
                refreshTable(dataRefreshEvent);
                break;
            case REF_TREE:
                centerPane.refreshItems();
                westPane.dataRefresh();
                break;
            case REF_TOOL:
                toolBar.enableItemsTool(dataRefreshEvent.getEnable(), dataRefreshEvent.getGlobalType(), dataRefreshEvent.getId());
                break;
        }
    }

    private void refreshTable(DataRefreshEvent e) {
        switch (e.getGlobalType()) {
            case CLIPS:
                centerPane.loadItemsOfClips(e.getClipsType(), e.getTags());
                break;
            case DELAY:
                centerPane.loadItemsOfDaily(e.getDate());
                break;
        }
    }

}

package org.swdc.note.ui.common;

import java.util.EventListener;

/**
 * 日历的监听器
 */
@FunctionalInterface
public interface DateSelectListener extends EventListener {

    void dateSelected(DateSelectEvent event);

}

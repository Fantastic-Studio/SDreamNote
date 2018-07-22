package org.swdc.note.ui.common;

import java.util.Date;
import java.util.EventObject;

/**
 * 日期面板的选择事件
 */
public class DateSelectEvent extends EventObject {

    private Date selectDate;

    public DateSelectEvent(Object source) {
        super(source);
    }

    public Date getSelectDate() {
        return selectDate;
    }

    public void setSelectDate(Date selectDate) {
        this.selectDate = selectDate;
    }
}

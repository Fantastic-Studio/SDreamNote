package org.swdc.note.ui.common;

import javax.swing.table.DefaultTableModel;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 表格模型，用于显示记录列表
 */
public class TableModel extends DefaultTableModel {

    public List<String> tableHeader = new ArrayList<>();

    public TableModel(Class clazz) {
        List<Field> fields = Arrays.asList(clazz.getDeclaredFields());
        fields.stream().forEach(fieldItem -> tableHeader.add(fieldItem.getName()));
    }

    @Override
    public void setColumnIdentifiers(Object[] newIdentifiers) {
        tableHeader.clear();
        Arrays.asList(newIdentifiers).stream().forEach(item -> tableHeader.add(item.toString()));
    }

    @Override
    public int getColumnCount() {
        return tableHeader.size();
    }

    @Override
    public String getColumnName(int column) {
        return tableHeader.get(column);
    }

}

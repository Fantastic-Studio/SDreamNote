package org.swdc.note.ui.common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * 日期面板
 */
public class DatePanel extends JPanel {

    private int cellWidth;
    private int cellHeight;
    private JPanel contentPane = new JPanel();
    private JToolBar toolBar = new JToolBar();

    private JTextField txtYear = new JTextField();
    private JTextField txtMonth = new JTextField();

    private JButton btnIncYear = new JButton(">>");
    private JButton btnIncMonth = new JButton(">");

    private JButton btnDecYear = new JButton("<<");
    private JButton btnDecMonth = new JButton("<");

    private List<DateSelectListener> listeners = new ArrayList<>();

    private String selDay = "";

    /**
     * 描述周的标签
     */
    private JLabel[] labels = new JLabel[]{
            new JLabel("周一"),
            new JLabel("周二"),
            new JLabel("周三"),
            new JLabel("周四"),
            new JLabel("周五"),
            new JLabel("周六"),
            new JLabel("周天")
    };
    /**
     * 描述天的按钮组
     */
    private List<JToggleButton> dayBtns = new ArrayList<>();
    private ButtonGroup btnGp = new ButtonGroup();

    private Date date = new Date();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private Font font;
    private int topHeight = 40;

    public DatePanel() {
        this.setLayout(new BorderLayout());
        this.add(contentPane, BorderLayout.CENTER);
        toolBar.setSize(this.getWidth(), topHeight);
        this.add(toolBar, BorderLayout.NORTH);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                refreshUI();
            }
        });
        initUI();
    }

    public void setFonts(Font font) {
        this.font = font;
    }

    public void initUI() {
        toolBar.add(btnDecYear);
        toolBar.add(btnDecMonth);
        toolBar.add(txtYear);
        txtYear.setEditable(false);
        txtMonth.setEditable(false);
        toolBar.add(txtMonth);
        toolBar.add(btnIncMonth);
        toolBar.add(btnIncYear);
        toolBar.putClientProperty("ToolBar.isPaintPlainBackground", Boolean.TRUE);
        toolBar.setFloatable(false);
        contentPane.setLayout(null);
        Arrays.asList(labels).forEach(contentPane::add);
        for (int i = 0; i < 42; i++) {
            JToggleButton btn = new JToggleButton("" + i);
            dayBtns.add(btn);
            btnGp.add(btn);
        }
        dayBtns.forEach(contentPane::add);
        dayBtns.forEach(item -> item.addActionListener(e -> {
            try {
                selDay = item.getText();
                Date date = dateFormat.parse(txtYear.getText() + "-" + txtMonth.getText() + "-" + selDay);
                DateSelectEvent event = new DateSelectEvent(DatePanel.this);
                // 触发事件
                event.setSelectDate(date);
                listeners.forEach(listener -> listener.dateSelected(event));
            } catch (Exception ex) {

            }
        }));
        btnIncYear.addActionListener(e -> {
            try {
                date = dateFormat.parse((getYear() + 1) + "-" + getMonth() + "-" + getDay());
                refreshUI();
            } catch (Exception ex) {

            }
        });
        btnIncMonth.addActionListener(e -> {
            try {
                date = dateFormat.parse(getYear() + "-" + (getMonth() + 1) + "-" + getDay());
                refreshUI();
            } catch (Exception ex) {

            }
        });
        btnDecYear.addActionListener(e -> {
            try {
                date = dateFormat.parse((getYear() - 1) + "-" + getMonth() + "-" + getDay());
                refreshUI();
            } catch (Exception ex) {

            }
        });
        btnDecMonth.addActionListener(e -> {
            try {
                date = dateFormat.parse(getYear() + "-" + (getMonth() - 1) + "-" + getDay());
                refreshUI();
            } catch (Exception ex) {

            }
        });
        refreshUI();
    }

    private int getMonth() {
        String month = txtMonth.getText();
        if (month.equals("")) {
            month = dateFormat.format(date).split("-")[1];
        }
        return Integer.valueOf(month);
    }

    private int getYear() {
        String year = txtYear.getText();
        if (year.equals("")) {
            year = dateFormat.format(date).split("-")[0];
        }
        return Integer.valueOf(year);
    }

    private int getDay() {
        String day = selDay;
        if (selDay.equals("")) {
            day = dateFormat.format(date).split("-")[2];
        }
        return Integer.valueOf(day);
    }

    private void refreshUI() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdf.format(date);
        txtYear.setText(strDate.split("-")[0]);
        txtMonth.setText(strDate.split("-")[1]);
        cellWidth = getWidth() / 7 - 2;
        cellHeight = (getHeight() - toolBar.getHeight()) / 7 - 2;
        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = labels[i];
            if (font != null) {
                lbl.setFont(font);
            }
            lbl.setSize(cellWidth, cellHeight);
            lbl.setLocation(i * cellWidth + 10, 0);
        }
        int wrapX = 0;
        int wrapY = 0;
        int startWeek = zeller(this.date) - 1;
        int week = 1;
        if (startWeek <= 0) {
            startWeek = 6;
        }
        for (int i = 0; i < dayBtns.size(); i++) {
            if (i % labels.length == 0) {
                wrapX = 0;
                wrapY++;
            }
            JToggleButton btn = dayBtns.get(i);
            if (i < startWeek) {
                btn.setText("");
                btn.setEnabled(false);
            } else if (week <= maxDay(date)) {
                btn.setText("" + week);
                btn.setEnabled(true);
                if (week == getDay()) {
                    btn.setSelected(true);
                }
                if (font != null) {
                    btn.setFont(font);
                }
                week++;
            } else {
                btn.setText("");
                btn.setEnabled(false);
            }
            btn.setSize(cellWidth, cellHeight);
            btn.setLocation(10 + wrapX * cellWidth, wrapY * cellHeight);
            wrapX++;
        }
    }

    public int zeller(Date date) {
        String dateStr = dateFormat.format(date);
        int yearC = Integer.valueOf(dateStr.split("-")[0].substring(0, 2));
        int yearY = Integer.valueOf(dateStr.split("-")[0].substring(2, 4));
        int mounth = Integer.valueOf(dateStr.split("-")[1]);
        int day = 1;
        int result = yearC / 4 - 2 * yearC + yearY + yearY / 4 + 26 * (mounth + 1) / 10 + day - 1;
        while (result < 0) {
            result = result + 7;
        }
        while (result >= 7) {
            result = result % 7;
        }
        return result;
    }

    public int maxDay(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(date);
        int year = Integer.valueOf(dateStr.split("-")[0]);
        int month = Integer.valueOf(dateStr.split("-")[1]);
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
            return 31;
        }
        if (month == 2 || month == 4 || month == 6 || month == 9 || month == 11) {
            return 30;
        }
        if (month == 2) {
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
                return 29;
            }
            return 28;
        }
        return 0;
    }

    public void addSelectListener(DateSelectListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(DateSelectListener listener) {
        listeners.remove(listener);
    }

}

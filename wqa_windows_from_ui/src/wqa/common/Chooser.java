/**
 *
 * Copyright: Ares.
 * All Rights Reserved.
 * Company: Insigma HT/上海创图
 *
 * @author Ares <a href="mailto:icerainsoft@hotmail.com>send email</a>
 * @date 2013-11-17 16:53
 *
 * Revision History
 *
 * Date Programmer Notes --------- ---------------------
 * -------------------------------------------- 2013-11-17 Ares initial
 * 2014-09-16 Ares fix bug @n3k123
 *
 * 1. you can change date format by parameter. 2. you can change week label
 * content by showTEXT. 3. you can change dayOfWeek order by defaultStartDAY.
 */
package wqa.common;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;

/**
 * @author Ares
 * @Describe(Date Chooser class)
 */
public class Chooser extends JPanel {

    private static final long serialVersionUID = -5384012731547358720L;

    private Calendar calendar;
    private Calendar now = Calendar.getInstance();
    private JPanel calendarPanel;
    private java.awt.Font font = new java.awt.Font("Times", java.awt.Font.PLAIN, 12);
    private java.text.SimpleDateFormat sdf;
    private final LabelManager lm = new LabelManager();
    private javax.swing.Popup pop;
    private TitlePanel titlePanel;
    private BodyPanel bodyPanel;
    private FooterPanel footerPanel;
    
    private final Color HeadColor = new Color(15, 31, 63);
    private final Color BodyColor = new Color(34, 88, 149);
    private final Color TailColor = new Color(34, 88, 149);

    private JComponent showDate;
    private boolean isShow = false;
    private static final String DEFAULTFORMAT = "yyyy-MM-dd HH:mm:ss";
//    private static final String[] showTEXT = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
    private static final String[] showTEXT = {"日", "一", "二", "三", "四", "五", "六"};
    private static WeekLabel[] weekLabels = new WeekLabel[7];
    private static int defaultStartDAY = 0;//0 is from Sun, 1 is from Mon, 2 is from Tue
    private static Color hoverColor = Color.BLUE; // hover color

    private Chooser(java.util.Date date, String format, int startDAY) {
        if (startDAY > -1 && startDAY < 7) {
            defaultStartDAY = startDAY;
        }
        int dayIndex = defaultStartDAY;
        for (int i = 0; i < 7; i++) {
            if (dayIndex > 6) {
                dayIndex = 0;
            }
            weekLabels[i] = new WeekLabel(dayIndex, showTEXT[dayIndex]);
            dayIndex++;
        }
        sdf = new java.text.SimpleDateFormat(format);
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        initCalendarPanel();
    }

    public static Chooser getInstance(java.util.Date date, String format) {
        return new Chooser(date, format, defaultStartDAY);
    }

    public static Chooser getInstance(java.util.Date date) {
        return getInstance(date, DEFAULTFORMAT);
    }

    public static Chooser getInstance(String format) {
        return getInstance(new java.util.Date(), format);
    }

    public static Chooser getInstance() {
        return getInstance(new java.util.Date(), DEFAULTFORMAT);
    }

    private void initCalendarPanel() {
        calendarPanel = new JPanel(new java.awt.BorderLayout());
//        calendarPanel.setOpaque(false);
//        calendarPanel.setBackground(new Color(34, 88, 149));
        calendarPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0xAA, 0xAA, 0xAA)));
        calendarPanel.add(titlePanel = new TitlePanel(), java.awt.BorderLayout.NORTH);
        calendarPanel.add(bodyPanel = new BodyPanel(), java.awt.BorderLayout.CENTER);
        calendarPanel.add(footerPanel = new FooterPanel(), java.awt.BorderLayout.SOUTH);
//        this.addAncestorListener(new AncestorListener() {
//            public void ancestorAdded(AncestorEvent event) {
//            }
//
//            public void ancestorRemoved(AncestorEvent event) {
////                hidePanel();
//            }
//
//            //hide pop when move component.  
//            @Override
//            public void ancestorMoved(AncestorEvent event) {
////                hidePanel();
//            }
//        });
    }

    public void register(final JComponent showComponent) {
        this.showDate = showComponent;
        showComponent.setRequestFocusEnabled(true);
        showComponent.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                showComponent.requestFocusInWindow();
            }
        });
        this.add(showComponent, BorderLayout.CENTER);
        this.setPreferredSize(new Dimension(90, 25));
        this.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        showComponent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                if (showComponent.isEnabled()) {
                    showComponent.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
            }

            public void mouseExited(MouseEvent me) {
                if (showComponent.isEnabled()) {
                    showComponent.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    showComponent.setForeground(Color.BLACK);
                }
            }

            public void mousePressed(MouseEvent me) {
                if (showComponent.isEnabled()) {
                    showComponent.setForeground(hoverColor);
                    if (isShow) {
                        hidePanel();
                    } else {
                        calendar.setTime(new Date());
                        refresh();
                        showPanel(showComponent);
                    }
                }
            }

            public void mouseReleased(MouseEvent me) {
                if (showComponent.isEnabled()) {
                    showComponent.setForeground(Color.BLACK);
                }
            }
        });
        showComponent.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                hidePanel();
            }
        });
    }

    //hide the main panel.
    private void hidePanel() {
        if (pop != null) {
            isShow = false;
            pop.hide();
            pop = null;
        }
    }

    //show the main panel.
    private void showPanel(Component owner) {
        if (pop != null) {
            pop.hide();
        }
        int x = owner.getLocationOnScreen().x;
        int y = owner.getLocationOnScreen().y + owner.getHeight();
        
        pop = PopupFactory.getSharedInstance().getPopup(owner, calendarPanel, x, y);
        pop.show();
        isShow = true;
    }

    // change text or label's content.
    private void commit() {
        if (showDate instanceof JTextField) {
            ((JTextField) showDate).setText(sdf.format(calendar.getTime()));
        } else if (showDate instanceof JLabel) {
            ((JLabel) showDate).setText(sdf.format(calendar.getTime()));
        }
        hidePanel();
    }

    // control panel
    private class TitlePanel extends JPanel {

        private static final long serialVersionUID = -2865282186037420798L;
        private JLabel preYear, preMonth, center, nextMonth, nextYear, centercontainer;

        public TitlePanel() {
            super(new java.awt.BorderLayout());
            this.setBackground(HeadColor);
            initTitlePanel();
        }

        private void initTitlePanel() {
            preYear = new JLabel("<<", JLabel.CENTER);
            preMonth = new JLabel("<", JLabel.CENTER);
            center = new JLabel("", JLabel.CENTER);
            centercontainer = new JLabel("", JLabel.CENTER);
            nextMonth = new JLabel(">", JLabel.CENTER);
            nextYear = new JLabel(">>", JLabel.CENTER);

//            preYear.setToolTipText("Last Year");
//            preMonth.setToolTipText("Last Month");
//            nextMonth.setToolTipText("Next Month");
//            nextYear.setToolTipText("Next Year");
            preYear.setToolTipText("上一年");
            preMonth.setToolTipText("上一月");
            nextMonth.setToolTipText("下一月");
            nextYear.setToolTipText("下一年");

            preYear.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 10, 0, 0));
            preMonth.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 15, 0, 0));
            nextMonth.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 0, 0, 15));
            nextYear.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 0, 0, 10));

            centercontainer.setLayout(new java.awt.BorderLayout());
            centercontainer.add(preMonth, java.awt.BorderLayout.WEST);
            centercontainer.add(center, java.awt.BorderLayout.CENTER);
            centercontainer.add(nextMonth, java.awt.BorderLayout.EAST);

            this.add(preYear, java.awt.BorderLayout.WEST);
            this.add(centercontainer, java.awt.BorderLayout.CENTER);
            this.add(nextYear, java.awt.BorderLayout.EAST);
            this.setPreferredSize(new java.awt.Dimension(210, 25));

            updateDate();

            preYear.addMouseListener(new MyMouseAdapter(preYear, Calendar.YEAR, -1));
            preMonth.addMouseListener(new MyMouseAdapter(preMonth, Calendar.MONTH, -1));
            nextMonth.addMouseListener(new MyMouseAdapter(nextMonth, Calendar.MONTH, 1));
            nextYear.addMouseListener(new MyMouseAdapter(nextYear, Calendar.YEAR, 1));
            
        }

        private void updateDate() {
            center.setText(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1));
        }

        // listener for control label.
        class MyMouseAdapter extends java.awt.event.MouseAdapter {

            JLabel label;
            private int type, value;

            public MyMouseAdapter(final JLabel label, final int type, final int value) {
                this.label = label;
                this.type = type;
                this.value = value;
            }

            public void mouseEntered(java.awt.event.MouseEvent me) {
                label.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                label.setForeground(hoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent me) {
                label.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                label.setForeground(java.awt.Color.WHITE);
            }

            public void mousePressed(java.awt.event.MouseEvent me) {
                calendar.add(type, value);
                label.setForeground(hoverColor);
                refresh();
            }

            public void mouseReleased(java.awt.event.MouseEvent me) {
                label.setForeground(java.awt.Color.BLACK);
            }
        }
    }

    // body panel, include week labels and day labels.
    private class BodyPanel extends JPanel {

        private static final long serialVersionUID = 5677718768457235447L;

        public BodyPanel() {
            super(new GridLayout(7, 7));
            this.setPreferredSize(new java.awt.Dimension(210, 140));
            initMonthPanel();
        }

        private void initMonthPanel() {
            updateDate();
            this.setBackground(BodyColor);
        }

        public void updateDate() {
            this.removeAll();
            lm.clear();
            java.util.Date temp = calendar.getTime();
            Calendar cal = Calendar.getInstance();
            cal.setTime(temp);
            cal.set(Calendar.DAY_OF_MONTH, 1);

            int index = cal.get(Calendar.DAY_OF_WEEK);
            // 从当月1号前移，一直移动到面板显示的第一天的前一天；当-index + defaultStartDAY为正数时，为避免面板从当月1号之后开始显示，需要前移一周，确保当月显示完全
            if (index > defaultStartDAY) {
                cal.add(Calendar.DAY_OF_MONTH, -index + defaultStartDAY);
            } else {
                cal.add(Calendar.DAY_OF_MONTH, -index + defaultStartDAY - 7);
            }

            for (WeekLabel weekLabel : weekLabels) {
                this.add(weekLabel);
            }
            for (int i = 0; i < 42; i++) {
                cal.add(Calendar.DAY_OF_MONTH, 1);
                lm.addLabel(new DayLabel(cal));
            }
            for (DayLabel my : lm.getLabels()) {
                this.add(my);
            }
        }
    }

    private class FooterPanel extends JPanel {

        private static final long serialVersionUID = 8135037333899746736L;
        private JComboBox<String> jtf_H = null, jtf_m = null, jtf_s = null;

        public FooterPanel() {
            super(new FlowLayout(FlowLayout.LEFT, 10, 5));
            initFooterPanel();
        }

        private JComboBox<String> initCombobox(int num, int select){
            JComboBox<String> jtf = new JComboBox<>();
            for(int i = 0; i < num; i++){
                jtf.addItem(String.valueOf(i));
            }
            jtf.setSelectedIndex(select);
            jtf.setFocusable(false);
            return jtf;
        }
        
        private void initFooterPanel() {  
            this.setBackground(TailColor);
            this.add(new JLabel("时"));
            this.add(jtf_H = initCombobox(24, calendar.get(Calendar.HOUR_OF_DAY)));
            this.add(new JLabel("分"));
            this.add(jtf_m = initCombobox(60, calendar.get(Calendar.MINUTE)));
            this.add(new JLabel("秒"));
            this.add(jtf_s = initCombobox(60, calendar.get(Calendar.SECOND)));
//            JButton ok = new JButton("确定");
            JLabel ok = new JLabel(" 确定 ");
            ok.setOpaque(true);
            ok.setBackground(new Color(19,35,67));
            ok.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    ok.setForeground(Color.WHITE);
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    ok.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    ok.setForeground(hoverColor);
                }

                @Override
                public void mousePressed(MouseEvent me) {
                    super.mousePressed(me); //To change body of generated methods, choose Tools | Templates.
                    refresh();
                    calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(jtf_H.getSelectedItem().toString()));
                    calendar.set(Calendar.MINUTE, Integer.valueOf(jtf_m.getSelectedItem().toString()));
                    calendar.set(Calendar.SECOND, Integer.valueOf(jtf_s.getSelectedItem().toString()));
                    commit();
                }
            });
            this.add(ok);
        }

        public void updateDate() {
        }
    ;

    }
    
    //refresh all panel
    private void refresh() {
        titlePanel.updateDate();
        bodyPanel.updateDate();
        footerPanel.updateDate();
        SwingUtilities.updateComponentTreeUI(this);
    }

    private class WeekLabel extends JLabel {

        private static final long serialVersionUID = -8053965084432740110L;
        private String name;

        public WeekLabel(int index, String name) {
            super(name, JLabel.CENTER);
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    private class DayLabel extends JLabel implements java.util.Comparator<DayLabel>, java.awt.event.MouseListener, java.awt.event.MouseMotionListener {

        private static final long serialVersionUID = -6002103678554799020L;
        private boolean isSelected;
        private int year, month, day;

        public DayLabel(Calendar cal) {
            super("" + cal.get(Calendar.DAY_OF_MONTH), JLabel.CENTER);
            this.year = cal.get(Calendar.YEAR);
            this.month = cal.get(Calendar.MONTH);
            this.day = cal.get(Calendar.DAY_OF_MONTH);

            this.setFont(font);
            this.addMouseListener(this);
            this.addMouseMotionListener(this);
            if (month == calendar.get(Calendar.MONTH)) {
                this.setForeground(java.awt.Color.BLACK);
            } else {
                this.setForeground(java.awt.Color.LIGHT_GRAY);
            }

        }

        public boolean getIsSelected() {
            return isSelected;
        }

        public void setSelected(boolean b, boolean isDrag) {
            isSelected = b;
            if (b && !isDrag) {
                int temp = calendar.get(Calendar.MONTH);
                calendar.set(year, month, day);
                if (temp == month) {
                    SwingUtilities.updateComponentTreeUI(bodyPanel);
                } else {
                    refresh();
                }
                this.repaint();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            //set curr select day's background
            if (day == calendar.get(Calendar.DAY_OF_MONTH) && month == calendar.get(Calendar.MONTH)) {
                g.setColor(new java.awt.Color(0xBB, 0xBF, 0xDA));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            //set current day's border
            if (year == now.get(Calendar.YEAR) && month == now.get(Calendar.MONTH) && day == now.get(Calendar.DAY_OF_MONTH)) {
                Graphics2D gd = (Graphics2D) g;
                gd.setColor(new java.awt.Color(0x55, 0x55, 0x88));
                Polygon p = new Polygon();
                p.addPoint(0, 0);
                p.addPoint(getWidth() - 1, 0);
                p.addPoint(getWidth() - 1, getHeight() - 1);
                p.addPoint(0, getHeight() - 1);
                gd.drawPolygon(p);
            }
            if (isSelected) {
                Stroke s = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 1.0f, new float[]{2.0f, 2.0f}, 1.0f);
                Graphics2D gd = (Graphics2D) g;
                gd.setStroke(s);
                gd.setColor(Color.BLACK);
                Polygon p = new Polygon();
                p.addPoint(0, 0);
                p.addPoint(getWidth() - 1, 0);
                p.addPoint(getWidth() - 1, getHeight() - 1);
                p.addPoint(0, getHeight() - 1);
                gd.drawPolygon(p);
            }
            super.paintComponent(g);
        }

        public boolean contains(Point p) {
            return this.getBounds().contains(p);
        }

        private void update() {
            repaint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            isSelected = true;
            update();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            Point p = SwingUtilities.convertPoint(this, e.getPoint(), bodyPanel);
            this.setForeground(Color.BLACK);
            lm.setSelect(p, false);
//            commit();
        }

        @Override // change color when mouse over.
        public void mouseEntered(MouseEvent e) {
            this.setForeground(hoverColor);
            this.repaint();
        }

        @Override // change color when mouse exit.
        public void mouseExited(MouseEvent e) {
            if (month == calendar.get(Calendar.MONTH)) {
                this.setForeground(java.awt.Color.BLACK);
            } else {
                this.setForeground(java.awt.Color.LIGHT_GRAY);
            }
            this.repaint();
        }

        @Override
        public int compare(DayLabel o1, DayLabel o2) {
            Calendar c1 = Calendar.getInstance();
            c1.set(o1.year, o1.month, o1.day);
            Calendar c2 = Calendar.getInstance();
            c2.set(o2.year, o2.month, o2.day);
            return c1.compareTo(c2);
        }
    }

    private class LabelManager {

        private List<DayLabel> list;

        public LabelManager() {
            list = new ArrayList<Chooser.DayLabel>();
        }

        public List<DayLabel> getLabels() {
            return list;
        }

        public void addLabel(DayLabel label) {
            list.add(label);
        }

        public void clear() {
            list.clear();
        }

        public void setSelect(Point p, boolean b) {
            //如果是拖动,则要优化一下,以提高效率  
            if (b) {
                //表示是否能返回,不用比较完所有的标签,能返回的标志就是把上一个标签和  
                //将要显示的标签找到了就可以了  
                boolean findPrevious = false, findNext = false;
                for (DayLabel lab : list) {
                    if (lab.contains(p)) {
                        findNext = true;
                        if (lab.getIsSelected()) {
                            findPrevious = true;
                        } else {
                            lab.setSelected(true, b);
                        }
                    } else if (lab.getIsSelected()) {
                        findPrevious = true;
                        lab.setSelected(false, b);
                    }
                    if (findPrevious && findNext) {
                        return;
                    }
                }
            } else {
                DayLabel temp = null;
                for (DayLabel m : list) {
                    if (m.contains(p)) {
                        temp = m;
                    } else if (m.getIsSelected()) {
                        m.setSelected(false, b);
                    }
                }
                if (temp != null) {
                    temp.setSelected(true, b);
                }
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        JFrame jf = new JFrame("Date Picker Test");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setLayout(null);
        jf.setBounds(400, 200, 600, 400);

        Chooser ser = Chooser.getInstance();
        javax.swing.JTextField text = new JTextField();
        text.setBounds(10, 10, 200, 30);
        text.setText("2013-10-11 12:12:12");
        ser.register(text);

        Chooser ser2 = Chooser.getInstance("yyyy年MM月dd日");
        JLabel label = new JLabel("please click me.");
        label.setBounds(10, 50, 200, 30);
        ser2.register(label);

        jf.add(text);
        jf.add(label);
        jf.setVisible(true);
    }
}

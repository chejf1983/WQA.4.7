/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.form.main;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.border.LineBorder;
import javax.swing.event.MouseInputListener;

/**
 *
 * @author chejf
 */
public class Border extends LineBorder implements MouseInputListener {

    private static final long serialVersionUID = 1L;

    private JFrame frame;
    private int delta;

    private Point sp;
    private Point cp;
    private int width;
    private int height;

    private boolean top, bottom, left, right, topLeft, topRight,
            bottomLeft, bottomRight;

    private DIRECTION CDirection = DIRECTION.OTHER;

    @Override
    public void mouseReleased(MouseEvent me) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private enum DIRECTION {
        TOP, BOTTOM, LEFT, RIGHT, TOPLEFT, TOPRIGHT, BOTTEMLEFT, BOTTOMRIGHT, OTHER
    }

    public Border(Color color, int delta, JFrame frame) {
        super(color, delta);
        this.delta = delta + 2;
        this.frame = frame;

        frame.addMouseMotionListener(this);
        frame.addMouseListener(this);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point dp = e.getLocationOnScreen();
        // 拖动时的组件原点
//        int ox = dp.x - cp.x;
//        int oy = dp.y - cp.y;

        // 静止的 原点
//        int x = sp.x - cp.x;
//        int y = sp.y - cp.y;
        int h = height;
        int w = width;
        switch (this.CDirection) {
            case TOP:
//            ox = x;
                h = height + (-dp.y + sp.y);
                break;
            case BOTTOM:
//            oy = y;
//            ox = x;
                h = height + (dp.y - sp.y);
                break;
            case LEFT:
//            oy = y;
                w = width + (-dp.x + sp.x);
                break;
            case RIGHT:
//            oy = y;
//            ox = x;
                w = width + (dp.x - sp.x);
                break;
            case TOPLEFT:
                h = height + (-dp.y + sp.y);
                w = width + (-dp.x + sp.x);
                break;
            case TOPRIGHT:
//            ox = x;
                h = height + (-dp.y + sp.y);
                w = width + (dp.x - sp.x);
                break;
            case BOTTEMLEFT:
//            oy = y;
                h = height + (-dp.y + sp.y);
                w = width + (dp.x - sp.x);
                break;
            case BOTTOMRIGHT:
//            ox = x;
//            oy = y;
                h = height + (dp.y - sp.y);
                w = width + (+dp.x - sp.x);
                break;
            default:
        }
//        frame.setLocation(ox, oy);
        frame.setSize(w, h);
    }

    private DIRECTION GetDirection(MouseEvent arg0) {
        sp = arg0.getLocationOnScreen();
        cp = arg0.getPoint();
        width = frame.getWidth();
        height = frame.getHeight();

        if (cp.x > delta && cp.x < width - delta && cp.y <= delta) {
//            return DIRECTION.TOP;
        }
        if (cp.x > delta && cp.x < width - delta
                && cp.y >= height - delta) {
            return DIRECTION.BOTTOM;
        }
        if (cp.x <= delta && cp.y > delta && cp.y < height - delta) {
//            return DIRECTION.LEFT;
        }
        if (cp.x >= width - delta && cp.y > delta
                && cp.y < height - delta) {
            return DIRECTION.RIGHT;
        }

        if (cp.x <= delta && cp.y <= delta) {
//            return DIRECTION.TOPLEFT;
        }
        if (cp.x >= width - delta && cp.y <= delta) {
//            return DIRECTION.TOPRIGHT;
        }

        if (cp.x <= delta && cp.y >= height - delta) {
//            return DIRECTION.BOTTEMLEFT;
        }
        if (cp.x >= width - delta && cp.y >= height - delta) {
            return DIRECTION.BOTTOMRIGHT;
        }

        return DIRECTION.OTHER;
    }

    @Override
    public void mouseMoved(MouseEvent arg0) {
        switch (this.GetDirection(arg0)) {
            case TOP:
                frame.setCursor(Cursor
                        .getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                break;
            case BOTTOM:
                frame.setCursor(Cursor
                        .getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                break;
            case LEFT:
                frame.setCursor(Cursor
                        .getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
                break;
            case RIGHT:
                frame.setCursor(Cursor
                        .getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
                break;
            case TOPLEFT:
                frame.setCursor(Cursor
                        .getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
                break;
            case TOPRIGHT:
                frame.setCursor(Cursor
                        .getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
                break;
            case BOTTEMLEFT:
                frame.setCursor(Cursor
                        .getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
                break;
            case BOTTOMRIGHT:
                frame.setCursor(Cursor
                        .getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
                break;
            case OTHER:
            default:
        }
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
        this.CDirection = this.GetDirection(arg0);
    }

}

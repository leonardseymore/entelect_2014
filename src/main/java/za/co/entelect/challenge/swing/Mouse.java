package za.co.entelect.challenge.swing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Mouse implements MouseListener, MouseMotionListener {

    private static final Logger logger = LoggerFactory.getLogger(Mouse.class);
    private static Mouse instance;
    private static final int BUTTON_COUNT = 3;
    private double zoomFactor;
    private int dx, dy;
    private Robot robot = null;
    private Component component;
    private Point center;
    private boolean relative;
    private Point mousePos = null;
    private Point currentPos = null;
    private boolean[] state = null;
    private MouseState[] poll = null;

    private enum MouseState {
        RELEASED,
        PRESSED,
        ONCE
    }

    private Mouse(Component component, double zoomFactor) {
        this.component = component;
        this.zoomFactor = zoomFactor;
        int w = component.getBounds().width;
        int h = component.getBounds().height;
        center = new Point(w / 2, h / 2);
        try {
            robot = new Robot();
        } catch (Exception ex) {
            logger.error("Problem creating robot", ex);
        }
        mousePos = new Point(0, 0);
        currentPos = new Point(0, 0);
        state = new boolean[BUTTON_COUNT];
        poll = new MouseState[BUTTON_COUNT];
        for (int i = 0; i < BUTTON_COUNT; ++i) {
            poll[i] = MouseState.RELEASED;
        }
    }

    public synchronized void poll() {
        if (isRelative()) {
            mousePos = new Point(dx, dy);
        } else {
            mousePos = new Point(currentPos);
        }

        dx = dy = 0;
        for (int i = 0; i < BUTTON_COUNT; ++i) {

            if (state[i]) {
                if (poll[i] == MouseState.RELEASED)
                    poll[i] = MouseState.ONCE;
                else
                    poll[i] = MouseState.PRESSED;
            } else {
                poll[i] = MouseState.RELEASED;
            }
        }
    }

    public boolean isRelative() {
        return relative;
    }

    public void setRelative(boolean relative) {
        this.relative = relative;
        if (relative) {
            centerMouse();
        }
    }

    public Point getPosition() {
        return mousePos;
    }

    public boolean buttonDownOnce(int button) {
        return poll[button - 1] == MouseState.ONCE;
    }

    public boolean buttonDown(int button) {
        return poll[button - 1] == MouseState.ONCE ||
                poll[button - 1] == MouseState.PRESSED;
    }

    public synchronized void mousePressed(MouseEvent e) {
        state[e.getButton() - 1] = true;
    }

    public synchronized void mouseReleased(MouseEvent e) {
        state[e.getButton() - 1] = false;
    }

    public synchronized void mouseEntered(MouseEvent e) {
        mouseMoved(e);
    }

    public synchronized void mouseExited(MouseEvent e) {
        mouseMoved(e);
    }

    public synchronized void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    public synchronized void mouseMoved(MouseEvent e) {
        if (isRelative()) {
            Point p = e.getPoint();
            dx += p.x - center.x;
            dy += p.y - center.y;
            centerMouse();
        } else {
            currentPos = e.getPoint();
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    private void centerMouse() {
        if (robot != null && component.isShowing()) {

            Point copy = new Point(center.x, center.y);
            SwingUtilities.convertPointToScreen(copy, component);
            robot.mouseMove(copy.x, copy.y);
        }
    }

    public double getZoomFactor() {
        return zoomFactor;
    }

    public static void init(Component component, double zoomFactor) {
        if (instance == null) {
            instance = new Mouse(component, zoomFactor);
        }
    }

    public static Mouse getInstance() {
        return instance;
    }
}
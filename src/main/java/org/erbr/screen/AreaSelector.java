package org.erbr.screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class AreaSelector extends JWindow {
    private Point start;
    private Point end;
    private final Consumer<Rectangle> callback;

    public AreaSelector(Consumer<Rectangle> callback) {
        this.callback = callback;
        setAlwaysOnTop(true);
        setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
        setBackground(new Color(0, 0, 0, 20));

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { start = e.getPoint(); }
            public void mouseReleased(MouseEvent e) {
                end = e.getPoint();
                Rectangle rect = new Rectangle(
                        Math.min(start.x, end.x),
                        Math.min(start.y, end.y),
                        Math.abs(start.x - end.x),
                        Math.abs(start.y - end.y));
                dispose();
                callback.accept(rect);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                end = e.getPoint();
                repaint();
            }
        });

        setVisible(true);
    }

    public void paint(Graphics g) {
        super.paint(g);
        if (start != null && end != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(2));
            Rectangle rect = new Rectangle(
                    Math.min(start.x, end.x),
                    Math.min(start.y, end.y),
                    Math.abs(start.x - end.x),
                    Math.abs(start.y - end.y));
            g2.draw(rect);
        }
    }

    public interface Consumer<T> {
        void accept(T t);
    }
}

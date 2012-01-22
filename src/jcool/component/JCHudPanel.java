/*
 * This file is part of JCool.
 *
 * JCool is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JCool is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JCool.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright © 2011 Eneko Sanz Blanco <nkogear@gmail.com>
 *
 */

package jcool.component;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 *
 * @author Eneko
 */
public class JCHudPanel extends JComponent implements Serializable {

    private CloseButton closeButton;
    private Point dragStart;

    public JCHudPanel() {
        super();
        try {
            BufferedImage img = ImageIO.read(JCSearchField.class.getResource(
                                              "/jcool/resources/hudclose.png"));
            BufferedImage mouseOverImg = ImageIO.read(JCSearchField.class
                                                .getResource("/jcool/resources/"
                                                          + "hudclose-mo.png"));
            closeButton = new CloseButton(img, mouseOverImg);
        } catch (IOException ex) {
            Logger.getLogger("jcool").log(Level.SEVERE, "Couldn't load "
                                                          + "resources.");
        }
        setBackground(new Color(50, 50, 50));
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                closeButton.setLocation(getWidth() - 28, 5);
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragStart = e.getPoint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point location = getLocation();
                setLocation(location.x + e.getX() - dragStart.x,
                            location.y + e.getY() - dragStart.y);
            }
        });
        this.add(closeButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(),
                                                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                            RenderingHints.VALUE_RENDER_QUALITY);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.93f));
        g2.setColor(this.getBackground());
        g2.fillRoundRect(3, 11, this.getWidth()-11, this.getHeight() - 14, 18, 18);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2.drawRoundRect(2, 10, this.getWidth()-10, this.getHeight() - 13, 20, 20);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        g2.drawRoundRect(1, 9, this.getWidth()-8, this.getHeight() - 11, 22, 22);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
        g2.drawRoundRect(0, 8, this.getWidth()-6, this.getHeight() - 9, 24, 24);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0.95f));
        g2.fillRoundRect(this.getWidth() - 30, 3, 28, 28, 50, 50);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.95f));
        g2.setClip(this.getWidth() - 30, 0, this.getWidth(), 11);
        drawCloseButtonShadow(g2);
        g2.setClip(this.getWidth() -9, 11, this.getWidth(), 18);
        drawCloseButtonShadow(g2);
        g2.dispose();
        g.drawImage(image, 0, 0, null);
    }

    private void drawCloseButtonShadow(Graphics2D g) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g.drawRoundRect(this.getWidth() - 29, 3, 26, 26, 100, 100);
        g.drawRoundRect(this.getWidth() - 30, 2, 28, 28, 100, 100);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        g.drawRoundRect(this.getWidth() - 31, 1, 30, 30, 100, 100);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
        g.drawRoundRect(this.getWidth() - 32, 0, 32, 32, 100, 100);
    }

    public void addExitButtonListener(MouseListener l) {
        closeButton.addMouseListener(l);
    }
    
    private class CloseButton extends JComponent {

        private BufferedImage img;
        private BufferedImage mouseOverImg;
        private boolean mouseOver;

        public CloseButton(BufferedImage img, BufferedImage mouseOverImg) {
            this.img = img;
            this.mouseOverImg = mouseOverImg;
            setSize(img.getWidth(), img.getHeight());
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter(){
                @Override
                public void mouseEntered(MouseEvent e) {
                    mouseOver = true;
                    repaint();
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    mouseOver = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (mouseOver)
                g.drawImage(mouseOverImg, 0, 0, null);
            else
                g.drawImage(img, 0, 0, null);
        }
        
    }

}

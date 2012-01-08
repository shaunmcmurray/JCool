/*
 * This file is part of jCool.
 *
 * jCool is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright © 2011 Eneko Sanz Blanco <nkogear@gmail.com>
 *
 */

package jcool.component.dynamiclist;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.border.Border;

/**
 * @author Eneko
 */
public abstract class DefaultItem extends JComponent implements Representable {

    private boolean isMouseOver;
    private Color borderColor = Color.lightGray;

    public DefaultItem() {
        super();
        this.setOpaque(false);
        this.setBackground(new Color(240, 240, 240));
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                isMouseOver = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isMouseOver = false;
                repaint();
            }

        });
    }

    public void setBorder(Color borderColor) {
        this.borderColor = borderColor;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (isMouseOver) {
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                             RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(this.getBackground());
            g.fillRoundRect(0, 0, this.getWidth()-1, this.getHeight()-9, 20, 20);
        }
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        if (isMouseOver) {
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                             RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(borderColor);
            g.drawRoundRect(0, 0, this.getWidth()-1, this.getHeight()-9, 20, 20);
        }
    }

    @Override
    public JComponent getRepresentation() {
        return this;
    }
        
}

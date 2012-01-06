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

/**
 * @author Eneko
 */
public abstract class DefaultItem extends JComponent implements Representable {

        private boolean isMouseOver;

        public DefaultItem() {
            super();
            this.setOpaque(false);
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
        
        @Override
        protected void paintComponent(Graphics g) {
            if (isMouseOver) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(this.getBackground());
                g2.fillRoundRect(0, 0, this.getWidth()-1, this.getHeight()-9,
                                                                       20, 20);
                g2.setColor(Color.lightGray);
                g2.drawRoundRect(0, 0, this.getWidth()-1, this.getHeight()-9,
                                                                       20, 20);
            }
            super.paintComponent(g);
        }

    @Override
    public abstract JComponent getRepresentation();
        
}

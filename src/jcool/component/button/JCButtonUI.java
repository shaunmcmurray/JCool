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

package jcool.component.button;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import jcool.utils.JCoolUtils;

/**
 * @author Eneko
 */
public class JCButtonUI extends BasicButtonUI {

    // The background colors used in the multi-stop gradient
    private Color bgColor1 = new Color(0xffffff);
    private Color bgColor2 = new Color(0xdbdbdb);
    
    // The background colors used in the multi-stop gradient on rollover
    private Color bgColor3 = new Color(0xdedede);
    private Color bgColor4 = new Color(0xc0c0c0);
    
    // Background gradients
    private LinearGradientPaint bgGradient;
    private LinearGradientPaint bgGradientRollover;
    private LinearGradientPaint bgGradientPressed;

    // The color to use for the top and bottom border
    private Color borderColor = new Color(0x838383);

    // The button's roundness
    private int roundness = 8;

    @Override
    protected void installDefaults(AbstractButton b) {
        super.installDefaults(b);
        b.setForeground(Color.darkGray);
        b.setHorizontalTextPosition(AbstractButton.CENTER);
        b.setBorder(null);
        b.setOpaque(false);
        b.setFont(JCoolUtils.getJCoolFont());
        if (b.getHeight() != 0)
            adjustGradients(b.getHeight());
        b.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustGradients(e.getComponent().getHeight());
            }
        });
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        AbstractButton button = (AbstractButton) c;
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                            RenderingHints.VALUE_RENDER_QUALITY);

        g2.setColor(new Color(0xcecece));
        g2.setClip(new Rectangle(0,5,c.getWidth(),c.getHeight()));
        g2.drawRoundRect(0, 3, c.getWidth() - 1, c.getHeight() - 4,
                                             roundness, roundness);
        g2.setClip(null);
        if (button.getModel().isRollover()) {
            g2.setPaint(bgGradientRollover);
            g2.fillRoundRect(3, 3, c.getWidth() - 6, c.getHeight() - 5,
                                          roundness - 3, roundness - 3);
        }  else {
            g2.setPaint(bgGradient);
            g2.fillRoundRect(1, 1, c.getWidth() - 2, c.getHeight() - 2,
                                                  roundness, roundness);
        }
        g2.setColor(borderColor);
        g2.drawRoundRect(1, 1, c.getWidth() - 3, c.getHeight() - 3,
                                              roundness, roundness);
        
        super.paint(g, c);
    }

    @Override
    protected void paintButtonPressed(Graphics g, AbstractButton b) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(bgGradientPressed);
        g2.fillRoundRect(3, 3, b.getWidth() - 6, b.getHeight() - 5,
                                              roundness, roundness);
    }

    @Override
    protected void paintText(Graphics g, AbstractButton button, Rectangle textRect,
                                                                    String text) {
        FontMetrics fontMetrics = g.getFontMetrics(button.getFont());
        int mnemonicIndex = button.getDisplayedMnemonicIndex();

        g.setColor(button.getForeground());
        BasicGraphicsUtils.drawStringUnderlineCharAt(g, text, mnemonicIndex,
                textRect.x + getTextShiftOffset(),
                textRect.y + fontMetrics.getAscent() + getTextShiftOffset());
    }

    public void setRoundness(int roundness) {
        this.roundness = roundness;
    }

    public void setBackgroundGradient(LinearGradientPaint gradient) {
        this.bgGradient = gradient;
    }

    public void setBackgroundRolloverGradient(LinearGradientPaint gradient) {
        this.bgGradientRollover = gradient;
    }

    public void setBackgroundPressedGradient(LinearGradientPaint gradient) {
        this.bgGradientPressed = gradient;
    }

    private void adjustGradients(int newHeight) {
        bgGradient = new LinearGradientPaint(0, 0, 0, newHeight,
                                             new float[] {0f, 1f},
                                             new Color[] {bgColor1,
                                                          bgColor2});
        bgGradientRollover = new LinearGradientPaint(0, 0, 0, newHeight,
                                                     new float[] {0f, 1f},
                                                     new Color[] {bgColor3,
                                                                  bgColor4});
        bgGradientPressed = new LinearGradientPaint(0, 0, 0, newHeight,
                                                     new float[] {0f, 1f},
                                                     new Color[] {bgColor4,
                                                                  bgColor3});
    }

}

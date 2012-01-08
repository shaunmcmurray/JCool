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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.AnimatorBuilder;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.TimingTarget;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

/**
 * @author Eneko
 */
public class JCSearchField extends JCTextField implements Serializable {

    private static BufferedImage searchIcon;
    private int expandedWidth = 100;
    private int expandDirection = SwingConstants.RIGHT;
    private boolean expandOnFocus = false;
    private boolean expanded = false;
    private Animator animator = null;
    private static final SwingTimerTimingSource timingSource =
                         new SwingTimerTimingSource(15, TimeUnit.MILLISECONDS);


    public JCSearchField() {
        super();
        try {
            searchIcon = ImageIO.read(JCSearchField.class.getResource("/jcool/resources"
                                                                 + "/magnifier.png"));
        } catch (IOException ex) {
            Logger.getLogger("jcool").log(Level.SEVERE, "Couldn't load "
                                                          + "resources.");
        }
        super.setBorder(new EmptyBorder(0, 25, 0, 8));
        super.setRoundness(20);
        this.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if ((expandOnFocus)&&(!expanded))
                    expand();
            }

            @Override
            public void focusLost(FocusEvent e) {
                if ((expandOnFocus)&&(expanded))
                    contract();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.drawImage(searchIcon, 4, (int) Math.ceil((getHeight() - 20) / 2d), 20,
                                                                       20, null);
        g2.dispose();

    }

    /**
     * Enables expand animation on focus. See setExpandDirection() and
     * setExpandedWidth().
     *
     * @see
     *
     * @param expand
     */
    public void setExpandOnFocus(boolean expand) {
        expandOnFocus = expand;
        if (expandOnFocus)
            initAnimator();
    }
    
    /**
     * Use SwingConstants.LEFT and SwingConstants.RIGHT to determine
     * the expand direction. It's setted to RIGHT by default.
     * 
     * @param direction
     */
    public void setExpandDirection(int direction) {
        expandDirection = direction;
        if (expandOnFocus)
            initAnimator();
    }

    /**
     * Sets the width of the search field in its expanded state (size to which
     * will grow when focus gained)
     * gained
     *
     * @param width
     */
    public void setExpandedWidth(int width) {
        expandedWidth = width;
        if (expandOnFocus)
            initAnimator();
    }

    private void initAnimator() {
        animator = new AnimatorBuilder(timingSource)
                   .setInterpolator(new AccelerationInterpolator(0.3, 0.2))
                   .setDuration(300, TimeUnit.MILLISECONDS)
                   .build();
        animator.addTarget(new TimingTargetAdapter() {
            @Override
            public void end(Animator source) {
                animationEnded();
            }
        });
        TimingTarget setter;
        if (expandDirection == SwingConstants.LEFT) {
             setter = PropertySetter.getTarget(this, "location", getLocation(),
                                               new Point(this.getX() - (expandedWidth
                                                         - this.getWidth()), getY()));
             animator.addTarget(setter);
        }
        setter = PropertySetter.getTarget(this, "size", getSize(),
                                          new Dimension(expandedWidth,
                                                          getHeight()));
        animator.addTarget(setter);
    }
    
    private void expand() {
        if (animator.isRunning()) {
            animator.cancel();
        }
        expanded = true;
        timingSource.init();
        animator.start();
    }

    private void contract() {
        if (animator.isRunning())
            animator.cancel();
        expanded = false;
        timingSource.init();
        animator.startReverse();
    }

    private void animationEnded() {
        timingSource.dispose();
    }

    /**
     * This funtion does nothing and should not be used. You can change the
     * border color with setBorderColor(), and shadows with setShadowColors();
     */
    @Override
    public void setBorder(Border border) {

    }

}
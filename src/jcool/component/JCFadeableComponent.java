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

package jcool.component;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.concurrent.TimeUnit;
import javax.swing.JComponent;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.AnimatorBuilder;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.TimingTarget;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

/**
 *
 * @author Eneko
 */
public class JCFadeableComponent extends JComponent {
    
    private float opacity = 1;

    /** Timing source of the animation */
    private static final SwingTimerTimingSource timingSource =
                         new SwingTimerTimingSource(18, TimeUnit.MILLISECONDS);

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
        repaint();
    }
    
    @Override
    public void paint(Graphics g) {
        ((Graphics2D)g).setComposite(AlphaComposite.getInstance(
                             AlphaComposite.SRC_OVER, opacity));
        super.paint(g);
    }

    /**
     * Switches the opacity from the initial to the final value with an
     * animation.
     *
     * @param initialOpacity opacity between 1 (opaque) and 0 (transparent)
     * @param finalOpacity opacity between 1 (opaque) and 0 (transparent)
     */
    public void fade(float initialOpacity, float finalOpacity) {
        Animator anim = new AnimatorBuilder(timingSource)
                   .setInterpolator(new AccelerationInterpolator(0.2, 0.6))
                   .setDuration(500, TimeUnit.MILLISECONDS)
                   .build();
        timingSource.init();

        anim.addTarget(new TimingTargetAdapter() {
            @Override
            public void timingEvent(Animator source, double fraction) {
                repaint();
            }
        });

        TimingTarget setter = PropertySetter.getTarget(this, "opacity",
                                                       initialOpacity,
                                                       finalOpacity);

        anim.addTarget(setter);

        anim.start();
    }
    
}

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

import java.awt.LinearGradientPaint;
import java.io.Serializable;
import javax.swing.JButton;
import javax.swing.plaf.ButtonUI;

/**
 *
 * @author Eneko
 */
public class JCButton extends JButton implements Serializable {

    public JCButton() {
        super();
        super.setUI(new JCButtonUI());
    }

    /**
     * The roundness is the width and height in pixels of the arc in this
     * component's corners.
     *
     * @param roundness
     */
    public void setRoundness(int roundness) {
        ((JCButtonUI)getUI()).setRoundness(roundness);
    }

    public void setBackgroundGradient(LinearGradientPaint gradient) {
        ((JCButtonUI)getUI()).setBackgroundGradient(gradient);
    }

    public void setBackgroundRolloverGradient(LinearGradientPaint gradient) {
        ((JCButtonUI)getUI()).setBackgroundRolloverGradient(gradient);
    }

    public void setBackgroundPressedGradient(LinearGradientPaint gradient) {
        ((JCButtonUI)getUI()).setBackgroundPressedGradient(gradient);
    }

    /**
     * JCBUtton should only be used with JCButtonUI.
     *
     * This method does nothing and should not be used.
     *
     * @param ui
     */
    @Override
    public void setUI(ButtonUI ui) {
        super.setUI(ui);
    }


}

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

package jcool.component.toolbar;

import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.plaf.ButtonUI;

/**
 *
 * @author Eneko
 */
public class JCToolBarButton extends JToggleButton {

    public JCToolBarButton() {
        super();
        super.setBorder(null);
        super.setUI(new JCToolBarButtonUI());
    }

    /**
     * JCToolBarButton should only be used with JCToolBarButtonUI.
     *
     * This method does nothing and should not be used.
     *
     * @param ui
     */
    @Override
    public void setUI(ButtonUI ui) {

    }

    /**
     * This method does nothing and should not be used.
     *
     * @param border
     */
    @Override
    public void setBorder(Border border) {

    }

}
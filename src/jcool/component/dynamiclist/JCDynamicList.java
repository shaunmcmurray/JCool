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

package jcool.component.dynamiclist;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JLabel;
import jcool.component.JCFadeableComponent;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.AnimatorBuilder;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingSource.PostTickListener;
import org.jdesktop.core.animation.timing.TimingTarget;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

/**
 * @author Eneko
 */
public class JCDynamicList extends JCFadeableComponent {

    /** List with the Data wanted to be represented in this component */
    private List<Representable> list;

    /** Layout related variables */
    private Dimension childsDimension;
    private int spacing;
    private int columns;
    private int rows;
    private int currentPage = 0;
    private int pageCount = 1;
    private boolean animating = false;
    private boolean transitioned = false;
    private JLabel pageCounter;

    /** 
     * Current components in shown that will be removed when turning on the 
     * page
     */
    private int componentsToRemove;
    
    /** Page swithching components related variables */
    private static final int pageButtonsSpace = 30;
    private static final int pageCtrlCompCount = 3;
    private PageCtrlButton backButton;
    private PageCtrlButton nextButton;

    /** Animator to animate page switching and transitions*/
    private Animator animator = null;

    /** Component which will be shown (with animation) on transition*/
    private JComponent transitionTarget = null;

    /** Timing source of the animations */
    private static final SwingTimerTimingSource timingSource =
                         new SwingTimerTimingSource(18, TimeUnit.MILLISECONDS);

    /**
     * @param spacing pixels between visual elements in the list
     * @param list
     * @param elementSize
     */
    public JCDynamicList(int spacing, List<Representable> list,
                            Dimension itemSize) {
        this.spacing = spacing;
        this.childsDimension = itemSize;
        this.list = list;
        this.backButton =
            new PageCtrlButton(JCDynamicList.class.getResource("/jcool/resources/"
                                                                   + "back.png"),
                               JCDynamicList.class.getResource("/jcool/resources/"
                                                               + "back-mo.png"));
        this.nextButton =
            new PageCtrlButton(JCDynamicList.class.getResource("/jcool/resources/"
                                                                   + "next.png"),
                               JCDynamicList.class.getResource("/jcool/resources/"
                                                               + "next-mo.png"));
        this.pageCounter = new JLabel();
        pageCounter.setFont(pageCounter.getFont().deriveFont((float)20));
        pageCounter.setSize(55, 24);
        pageCounter.setForeground(new Color(80,80,80));
        this.setLayout(null);
        this.setDoubleBuffered(true);
        timingSource.addPostTickListener(new PostTickListener() {
            @Override
            public void timingSourcePostTick(TimingSource source, long nanoTime) {
                Toolkit.getDefaultToolkit().sync();
            }
        });
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                onResize();
            }
        });
        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                backwards(backButton);
                previousPage();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                forwards(backButton);
            }
        });
        nextButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                forwards(nextButton);
                nextPage();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                backwards(nextButton);
            }
        });

    }

    /**
     * Lays out the components on resize. Also called by update() method to
     * relayout components.
     */
    private void onResize() {
        if (animating) {
            animator.cancel();
            animator = null;
            animating = false;
        }
        this.removeAll();
        columns = calculateFittingColumns();
        rows = calculateFittingRows();
        int fittingComponents = columns * rows;
        pageCount = (int) Math.ceil((double)list.size() / fittingComponents);

        if ((currentPage + 1) > pageCount)
            currentPage = pageCount -1;
    
        backButton.setLocation((this.getWidth()/2) - 30 - backButton.getWidth(),
                                                         this.getHeight() - 47);
        nextButton.setLocation((this.getWidth()/2) + 30, this.getHeight() - 47);
        adjustPageCounter();
        this.add(backButton);
        this.add(nextButton);
        this.add(pageCounter);
        backButton.setVisible(true);
        nextButton.setVisible(true);
        pageCounter.setVisible(true);

        int positionInList = currentPage * fittingComponents;

        int xMargin = (this.getWidth() - ((columns * childsDimension.width)
                                            + ((columns - 1) * spacing))) / 2;
        int yMargin;

        if ((list.size() < fittingComponents) && (currentPage == 0)) {
            int nonFittingComponents = list.size() % fittingComponents;
            int rowsInUse = (int) Math.ceil((double) nonFittingComponents
                                                                    / columns);
            yMargin = ((this.getHeight()- pageButtonsSpace)
                                        - ((rowsInUse * childsDimension.height)
                                        + ((rowsInUse - 1) * spacing))) / 2;
            if (yMargin > xMargin)
                yMargin = xMargin;
            else
                yMargin = ((this.getHeight() - pageButtonsSpace)
                                  - ((rows * childsDimension.height)
                                  + ((rows - 1) * spacing))) / 2;
        } else
            yMargin = ((this.getHeight() - pageButtonsSpace)
                                - ((rows * childsDimension.height)
                                + ((rows - 1) * spacing))) / 2;

        Point tempLocation = new Point(xMargin, yMargin);

        outerloop:
        for (int i=0; i < rows; i++) {
            for (int j=0; j < columns; j++) {
                if (positionInList >= list.size())
                    break outerloop;
                JComponent tempComponent = list.get(positionInList)
                                                           .getRepresentation();
                tempComponent.setLocation(tempLocation);
                this.add(tempComponent);
                positionInList++;
                tempLocation.setLocation(tempLocation.x
                                          + spacing
                                          + childsDimension.width
                                          , tempLocation.y);
            }
            tempLocation.setLocation(xMargin, tempLocation.y
                                                      + spacing
                                                      + childsDimension.height);
        }

        if (transitioned) {
            Component[] comps = this.getComponents();
            for(int i = pageCtrlCompCount; i < comps.length; i++) {
                Component c = comps[i];
                c.setLocation(c.getX(), c.getY() - this.getHeight());
            }
            transitionTarget.setSize(this.getSize());
            transitionTarget.setLocation(0,0);
            this.add(transitionTarget);
            backButton.setVisible(false);
            nextButton.setVisible(false);
            pageCounter.setVisible(false);
        }

        this.revalidate();
        this.repaint();
    }
    
    public void nextPage() {
        if (((currentPage + 1) < pageCount)&&(!animating)) {

            currentPage++;
            adjustPageCounter();

            animator = new AnimatorBuilder(timingSource)
                       .setInterpolator(new AccelerationInterpolator(0.1, 0.8))
                       .setDuration(800, TimeUnit.MILLISECONDS)
                       .build();
            timingSource.init();

            animator.addTarget(new TimingTargetAdapter() {
                @Override
                public void end(Animator source) {
                    animationEnded();
                }
            });

            Component[] comps = this.getComponents();
            for(int i = pageCtrlCompCount; i < comps.length; i++) {
                Component c = comps[i];
                TimingTarget setter = PropertySetter.getTarget(c, "location",
                                            c.getLocation(),
                                            new Point(c.getX() - this.getWidth(),
                                                                      c.getY()));
                animator.addTarget(setter);
            }

            int positionInList = (currentPage * columns * rows);
            Point tempLocation = this.getComponent(pageCtrlCompCount)
                                                                 .getLocation();
            tempLocation.setLocation(tempLocation.x + this.getWidth(),
                                                           tempLocation.y);
            int xMargin = tempLocation.x;

            outerloop:
            for (int i=0; i < rows; i++) {
                for (int j=0; j < columns; j++) {
                    if (positionInList >= list.size())
                        break outerloop;
                    JComponent c = list.get(positionInList).getRepresentation();
                    c.setLocation(tempLocation);
                    this.add(c);
                    TimingTarget setter = PropertySetter.getTarget(c, "location",
                                            c.getLocation(),
                                            new Point(c.getX() - this.getWidth(),
                                                                      c.getY()));
                    animator.addTarget(setter);
                    positionInList++;
                    tempLocation.setLocation(tempLocation.x
                                              + spacing
                                              + childsDimension.width
                                              , tempLocation.y);
                }
                tempLocation.setLocation(xMargin, tempLocation.y
                                                       + spacing
                                                       + childsDimension.height);
            }

            componentsToRemove = columns * rows;
            animating = true;
            animator.start();
        }
    }
    
    public void previousPage() {
        if ((currentPage != 0)&&(!animating)) {

            int positionInList = (currentPage * columns * rows);
            componentsToRemove = ((currentPage + 1) == pageCount)
                                    ? list.size() - positionInList
                                    : columns * rows;

            currentPage--;
            adjustPageCounter();

            animator = new AnimatorBuilder(timingSource)
                       .setInterpolator(new AccelerationInterpolator(0.1, 0.8))
                       .setDuration(800, TimeUnit.MILLISECONDS)
                       .build();
            timingSource.init();

            animator.addTarget(new TimingTargetAdapter() {
                @Override
                public void end(Animator source) {
                    animationEnded();
                }
            });

            Component[] comps = this.getComponents();
            for(int i = pageCtrlCompCount; i < comps.length; i++) {
                Component c = comps[i];
                TimingTarget setter = PropertySetter.getTarget(c, "location",
                                            c.getLocation(),
                                            new Point(c.getX() + this.getWidth(),
                                                                      c.getY()));
                animator.addTarget(setter);
            }

            positionInList = (currentPage * columns * rows);
            Point tempLocation = this.getComponent(pageCtrlCompCount)
                                                                 .getLocation();
            tempLocation.setLocation(tempLocation.x - this.getWidth(),
                                                           tempLocation.y);
            int xMargin = tempLocation.x;

            outerloop:
            for (int i=0; i < rows; i++) {
                for (int j=0; j < columns; j++) {
                    JComponent c = list.get(positionInList).getRepresentation();
                    c.setLocation(tempLocation);
                    this.add(c);
                    TimingTarget setter = PropertySetter.getTarget(c, "location",
                                                                 c.getLocation(),
                                            new Point(c.getX() + this.getWidth(),
                                                                      c.getY()));
                    animator.addTarget(setter);
                    positionInList++;
                    tempLocation.setLocation(tempLocation.x
                                              + spacing
                                              + childsDimension.width
                                              , tempLocation.y);
                }
                tempLocation.setLocation(xMargin, tempLocation.y
                                                       + spacing
                                                       + childsDimension.height);
            }
            animating = true;
            animator.start();
        }
    }
    
//    //TODO should perform an animation going to the new page setted
//    public void setCurrentPage(int page) {
//        if ((page < currentPage)&&(page >= 0)) {
//            // TODO
//        } else
//            if ((page > currentPage)&&((page + 1) < pageCount)) {
//                // TODO
//            }
//    }
    
    /**
     * The current page, starting from 0
     * 
     * @return
     */
    public int getCurrentPage() {
        return currentPage;
    }
    
    /**
     * Number of pages this list has.
     * 
     * @return
     */
    public int getPageCount() {
        return pageCount;
    }
    
    /**
     * 
     *
     * @return
     */
    public Representable[] getCurrentPageItems() {
        int positionInList = (currentPage * columns * rows);
        int listSize = list.size();
        int elemsInPage = listSize - positionInList;
        Representable[] representables = new Representable[elemsInPage];
        for(;positionInList < listSize ;positionInList++)
            representables[positionInList] = list.get(positionInList);
        return representables;
    }

    public void transitionTo(JComponent component) {
        if ((!animating)&&(!transitioned)) {
            transitioned = true;
            transitionTarget = component;
            component.setSize(this.getSize());
            component.setLocation(0, this.getHeight());
            this.add(component);
            component.setVisible(true);

            animator = new AnimatorBuilder(timingSource)
                       .setInterpolator(new AccelerationInterpolator(0.2, 0.6))
                       .setDuration(700, TimeUnit.MILLISECONDS)
                       .build();
            timingSource.init();

            animator.addTarget(new TimingTargetAdapter() {
                @Override
                public void end(Animator source) {
                    transitionEnded();
                }
            });

            Component[] comps = this.getComponents();
            for(int i = pageCtrlCompCount; i < comps.length; i++) {
                Component c = comps[i];
                TimingTarget setter = PropertySetter.getTarget(c, "location",
                                            c.getLocation(),
                                            new Point(c.getX(),
                                                      c.getY() - this.getHeight()));

                animator.addTarget(setter);

            }

            TimingTarget setter =
                    PropertySetter.getTarget(component, "location",
                                             component.getLocation(),
                                             new Point(component.getX(),
                                                       component.getY()
                                                       - this.getHeight()));
            animator.addTarget(setter);

            for(int i = 0; i < pageCtrlCompCount; i++) {
                comps[i].setVisible(false);
            }

            animating = true;
            animator.start();
            }
    }

    public void returnFromTransition() {

        if ((!animating)&&(transitioned)) {
            transitioned = false;
            animator = new AnimatorBuilder(timingSource)
                       .setInterpolator(new AccelerationInterpolator(0.2, 0.6))
                       .setDuration(700, TimeUnit.MILLISECONDS)
                       .build();
            timingSource.init();

            animator.addTarget(new TimingTargetAdapter() {
                @Override
                public void end(Animator source) {
                    transitionEnded();
                }
            });

            Component[] comps = this.getComponents();
            for(int i = pageCtrlCompCount; i < comps.length; i++) {
                Component c = comps[i];
                TimingTarget setter = PropertySetter.getTarget(c, "location",
                                            c.getLocation(),
                                            new Point(c.getX(),
                                                      c.getY() + this.getHeight()));

                animator.addTarget(setter);

            }

            TimingTarget setter =
                    PropertySetter.getTarget(transitionTarget, "location",
                                             transitionTarget.getLocation(),
                                             new Point(transitionTarget.getX(),
                                                       transitionTarget.getY()
                                                       + this.getHeight()));
            animator.addTarget(setter);

            animating = true;
            animator.start();
            }

    }
    
    private void animationEnded() {
        timingSource.dispose();
        Component[] components = this.getComponents();
        componentsToRemove = componentsToRemove + pageCtrlCompCount;
        for(int i = pageCtrlCompCount; i < componentsToRemove; i++) {
            this.remove(components[i]);
        }
        animating = false;
        animator = null;
    }

    private void transitionEnded() {
        animating = false;
        animator = null;
        timingSource.dispose();
        if (!transitioned) {
            this.remove(transitionTarget);
            transitionTarget = null;
            Component[] comps = this.getComponents();
            for(int i = 0; i < pageCtrlCompCount; i++) {
                comps[i].setVisible(true);
            }
        }
    }

    private int calculateFittingColumns() {
        int totalWidth = spacing;
        int panelCount = 0;
        for(; totalWidth <= this.getWidth(); panelCount++) {
            totalWidth = totalWidth + spacing + childsDimension.width;
        }
        return --panelCount;
    }

    private int calculateFittingRows() {
        int totalHeight = spacing;
        int panelCount = 0;
        int aviableSpace = this.getHeight() - pageButtonsSpace;
        for(; totalHeight <= aviableSpace; panelCount++) {
            totalHeight = totalHeight + spacing + childsDimension.height;
        }
        return --panelCount;
    }
    
    private void adjustPageCounter() {
        pageCounter.setText((currentPage + 1) + "/" + pageCount);
        switch(pageCounter.getText().length()){
            case 3:
                pageCounter.setLocation((this.getWidth()/2) - 15,
                                         this.getHeight() - 47);
                break;
            case 4:
                pageCounter.setLocation((this.getWidth()/2) - 20,
                                         this.getHeight() - 47);
                break;
            default:
                pageCounter.setLocation((this.getWidth()/2) - 26, 
                                        this.getHeight() - 47);
                break;
        }

    }

    /**
     * Relayouts components, updating the view. It needs to be called when the
     * List of Representables is modified to update the view of the JCDynamicList
     */
    public void update() {
        onResize();
    }

    @Override
    public void update(Graphics p) {
        paint(p);
    }

    private void backwards(PageCtrlButton ctrlButton) {
        ctrlButton.setLocation(ctrlButton.getX()-2, ctrlButton.getY());
    }

    private void forwards(PageCtrlButton ctrlButton) {
        ctrlButton.setLocation(ctrlButton.getX()+2, ctrlButton.getY());
    }

    private class PageCtrlButton extends JComponent {

        private BufferedImage buttonImage;
        private BufferedImage mouseOverButtonImage;
        private boolean isMouseOver = false;

        public PageCtrlButton(URL buttonImage, URL mouseOverButtonImage) {
            try {
                this.buttonImage = ImageIO.read(buttonImage);
                this.mouseOverButtonImage = ImageIO.read(mouseOverButtonImage);
                this.setOpaque(false);
                this.setSize(24, 24);
            } catch (IOException ex) {
                Logger.getLogger("jcool").log(Level.SEVERE, "Couldn't load "
                                                            + "resources.");
            }
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
        protected void paintComponent(Graphics g){
            Graphics2D g2 = (Graphics2D) g.create();
            if (isMouseOver)
                g2.drawImage(mouseOverButtonImage, null, 0, 0);
            else
                g2.drawImage(buttonImage, null, 0, 0);

            g2.dispose();
        }

    }

}

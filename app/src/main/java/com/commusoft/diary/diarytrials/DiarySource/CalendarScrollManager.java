package com.commusoft.diary.diarytrials.DiarySource;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for scrolling the calendar fragments and keeping track of their positions. It manages a set of three fragments and updates and rearranges them accordingly
 * to the current scroll modes and direction so that it creates the illusion of endless fragments in both directions.
 */
public class CalendarScrollManager extends CalendarElement {

    private static final double MAX_FRAGMENT_CLOSURE_VERTICAL = .17f;
    private static final double MAX_FRAGMENT_CLOSURE_HORIZONTAL = .12f;
    /**
     * The current scroll mode. It is internally updated by the owning chart and should not be directly changed.
     */
    protected ScrollMode scrollMode;
    /**
     * The current active date. Might not always be the current display date while scrolling is in progress.
     */
    protected long activeDate;
    /**
     * States whether the first arrange has been initiated.
     */
    protected boolean arrangePassed;
    /**
     * The fragment that comes before the current fragment.
     */
    protected CalendarFragment previousFragment;
    /**
     * The current or center fragment.
     */
    protected CalendarFragment currentFragment;
    /**
     * The fragment that comes after the current fragment.
     */
    protected CalendarFragment nextFragment;
    /**
     * The fragment that is currently being dragged.
     */
    protected CalendarFragment currentDragFragment;

    private boolean horizontalScroll;
    private int maxScrollOffset;
    private boolean suspendActiveDateUpdate;
    private boolean forwardBorderReached;
    private boolean backwardBorderReached;

    /**
     * Creates a new instance of the {@link com.telerik.widget.calendar.CalendarScrollManager} class.
     *
     * @param owner the calendar view instance that owns this instance.
     */
    public CalendarScrollManager(RadCalendarView owner) {
        super(owner);

        init();
    }

    @Override
    protected void onAlphaChanged() {
        this.currentFragment.setAlpha(this.alpha);
        this.previousFragment.setAlpha(this.alpha);
        this.nextFragment.setAlpha(this.alpha);
    }

    /**
     * Gets the maximum scroll offset that will be applied for a given gesture.
     *
     * @return the maximum scroll offset.
     */
    public int getMaxScrollOffset() {
        return maxScrollOffset;
    }

    /**
     * Sets the maximum scroll offset that will be applied for a given gesture.
     *
     * @param maxScrollOffset the new max scroll offset.
     */
    public void setMaxScrollOffset(int maxScrollOffset) {
        this.maxScrollOffset = maxScrollOffset;
    }

    ScrollMode getScrollMode() {
        return this.scrollMode;
    }

    void setScrollMode(ScrollMode scrollMode) {
        this.scrollMode = scrollMode;
    }

    boolean isHorizontalScroll() {
        return this.horizontalScroll;
    }

    void setHorizontalScroll(boolean horizontalScroll) {
        this.horizontalScroll = horizontalScroll;
    }

    /**
     * Gets the current active date. This date might differ from the display date of the calendar during scroll.
     *
     * @return the current active date.
     */
    public long getActiveDate() {
        return activeDate;
    }

    /**
     * Gets the current active date. This date might differ from the display date of the calendar during scroll.
     * The active date will be updated after which a call to the {@link #updateActiveFragment()} will be made, so that the change
     * can be completed.
     *
     * @param activeDate the new active date.
     */
    public void setActiveDate(long activeDate) {
        if (!this.suspendActiveDateUpdate)
            this.activeDate = activeDate;

        updateActiveFragment();
    }

    /**
     * The current or center fragment.
     *
     * @return the current fragment.
     */
    public CalendarFragment currentFragment() {
        return this.currentFragment;
    }

    /**
     * The fragment that comes after the current fragment.
     *
     * @return the next fragment.
     */
    public CalendarFragment nextFragment() {
        return this.nextFragment;
    }

    /**
     * The fragment that comes before the current fragment.
     *
     * @return the previous fragment.
     */
    public CalendarFragment previousFragment() {
        return this.previousFragment;
    }

    /**
     * Determines whether the scroll should be horizontal or vertical.
     *
     * @return <code>true</code> if the scroll should be horizontal, <code>false</code> if it should be vertical.
     */
    protected boolean scrollShouldBeHorizontal() {
        return this.horizontalScroll || this.owner.getDisplayMode() == CalendarDisplayMode.Week; // TODO cache
    }

    @Override
    public void arrange(int left, int top, int right, int bottom) {
        super.arrange(left, top, right, bottom);
        this.arrangePassed = true;
    }

    private boolean drawDecorations;

    @Override
    public void render(Canvas canvas) {
        if (this.scrollMode == ScrollMode.Overlap) {
            if (fragmentIsVisible(this.currentFragment))
                this.currentFragment.render(canvas, true);

            if (fragmentIsVisible(this.previousFragment))
                this.previousFragment.render(canvas, true);

            if (fragmentIsVisible(this.nextFragment))
                this.nextFragment.render(canvas, true);
        } else {
            this.drawDecorations = this.scrollMode == ScrollMode.Stack;
            if (fragmentIsVisible(this.previousFragment))
                this.previousFragment.render(canvas, this.drawDecorations);

            if (fragmentIsVisible(this.currentFragment))
                this.currentFragment.render(canvas, this.drawDecorations);

            if (fragmentIsVisible(this.nextFragment))
                this.nextFragment.render(canvas, this.drawDecorations);
        }
    }

    @Override
    public void postRender(Canvas canvas) {
        if (this.scrollMode == ScrollMode.Overlap) {
            if (fragmentIsVisible(this.currentFragment))
                this.currentFragment.postRender(canvas, false);

            if (fragmentIsVisible(this.previousFragment))
                this.previousFragment.postRender(canvas, false);

            if (fragmentIsVisible(this.nextFragment))
                this.nextFragment.postRender(canvas, false);
        } else {
            this.drawDecorations = this.scrollMode != ScrollMode.Stack;
            if (fragmentIsVisible(this.previousFragment))
                this.previousFragment.postRender(canvas, this.drawDecorations);

            if (fragmentIsVisible(this.currentFragment))
                this.currentFragment.postRender(canvas, this.drawDecorations);

            if (fragmentIsVisible(this.nextFragment))
                this.nextFragment.postRender(canvas, this.drawDecorations);
        }
    }

    /**
     * Returns a collection of cells that are located at a specific coordinates. The reason it is a collection
     * is because in some scroll modes there is overlapping of the fragments and there are invisible cells at the same location as visible ones.
     *
     * @param x the x coordinate.
     * @param y the y coordinate.
     * @return the collection of cells at this location.
     */
    public List<CalendarCell> getCellsAtLocation(int x, int y) {
        if (x < getLeft() || x > getRight() ||
                y < getTop() || y > getBottom())
            return new ArrayList<CalendarCell>();

        ArrayList<CalendarCell> cells = new ArrayList<CalendarCell>();
        CalendarCell cell = this.currentFragment.getCellAtLocation(x, y);
        if (cell != null)
            cells.add(cell);

        cell = this.previousFragment.getCellAtLocation(x, y);
        if (cell != null)
            cells.add(cell);

        cell = this.nextFragment.getCellAtLocation(x, y);
        if (cell != null)
            cells.add(cell);

        return cells;
    }

    private boolean currentScrollHorizontal;

    /**
     * Scrolls the fragment with a specified offset according to the current position and the current scroll mode.
     *
     * @param offsetX the offset along the x axis.
     * @param offsetY the offset along the y axis.
     * @return <code>true</code> if scroll was performed, <code>false</code> otherwise.
     */
    public boolean scroll(int offsetX, int offsetY) {

        // Restrict the scroll offset
        /*if (offsetX < 0 && offsetX < -this.maxScrollOffset)
            offsetX = -this.maxScrollOffset;
        else if (offsetX > 0 && offsetX > this.maxScrollOffset)
            offsetX = this.maxScrollOffset;

        if (offsetY < 0 && offsetY < -this.maxScrollOffset)
            offsetY = -this.maxScrollOffset;
        else if (offsetY > 0 && offsetY > this.maxScrollOffset)
            offsetY = this.maxScrollOffset;*/

        currentScrollHorizontal = scrollShouldBeHorizontal();

        offsetX = currentScrollHorizontal ? offsetX : 0;
        offsetY = !currentScrollHorizontal ? offsetY : 0;

        if (backwardBorderReached) {
            if (currentScrollHorizontal) {
                if (offsetX > 0 && this.currentFragment.getVirtualXPosition() + offsetX > this.getLeft()) {
                    offsetX -= currentFragment.getVirtualXPosition() + offsetX - getLeft();
                }
            } else {
                if (offsetY > 0 && this.currentFragment.getVirtualYPosition() + offsetY > getTop()) {
                    offsetY -= currentFragment.getVirtualYPosition() + offsetY - getTop();
                }
            }
        }

        if (forwardBorderReached) {
            if (currentScrollHorizontal) {
                if (offsetX < 0 && this.currentFragment.getVirtualXPosition() + offsetX < this.getLeft()) {
                    offsetX += Math.abs(getLeft() - (currentFragment.getVirtualXPosition() + offsetX));
                }
            } else {
                if (offsetY < 0 && this.currentFragment.getVirtualYPosition() + offsetY < getTop()) {
                    offsetY += Math.abs(currentFragment.getVirtualYPosition() + offsetY - getTop());
                }
            }
        }

        if (offsetX == 0 && offsetY == 0)
            return false;

        if (this.scrollMode == ScrollMode.Overlap || this.scrollMode == ScrollMode.Stack) {
            handleScrollWithOverlap(offsetX, offsetY);
        } else {
            handleScrollWithoutOverlap(offsetX, offsetY);
        }

        return true;
    }

    /**
     * Handles scrolling when in overlap or stacked modes.
     *
     * @param offsetX the offset along the x axis.
     * @param offsetY the offset along the y axis.
     */
    protected void handleScrollWithOverlap(int offsetX, int offsetY) {
        if (this.scrollShouldBeHorizontal()) {
            handleHorizontalOverlappingScroll(offsetX);
        } else {
            handleVerticalOverlappingScroll(offsetY);
        }
    }

    /**
     * Handles vertical scrolling when in overlap or stacked modes.
     *
     * @param offsetY the offset along the y axis.
     */
    protected void handleVerticalOverlappingScroll(int offsetY) {
        if (this.scrollMode == ScrollMode.Overlap) {
            if (this.currentDragFragment == null)
                if (offsetY < 0) {
                    this.currentDragFragment = this.nextFragment;
                } else {
                    this.currentDragFragment = this.previousFragment;
                }

            this.currentDragFragment.translate(0, offsetY);

            if (this.currentDragFragment == this.previousFragment && this.previousFragment.getVirtualYPosition() > this.getTop()) {
                this.previousFragment.translate(0, this.getTop() - this.previousFragment.getVirtualYPosition());
            } else if (this.currentDragFragment == this.nextFragment && this.nextFragment.getVirtualYPosition() < this.getTop()) {
                this.nextFragment.translate(0, this.getTop() - this.nextFragment.getVirtualYPosition());
            }
        } else {
            if (this.currentDragFragment == null)
                if (offsetY < 0) {
                    this.currentDragFragment = this.nextFragment;
                } else {
                    this.currentDragFragment = this.currentFragment;
                }

            this.currentDragFragment.translate(0, offsetY);

            if (this.currentDragFragment == this.currentFragment && this.currentFragment.getVirtualYPosition() < this.getTop()) {
                this.previousFragment.translate(0, this.getTop() - (this.previousFragment.getVirtualYPosition()));
            } else if (this.currentDragFragment == this.nextFragment && this.nextFragment.getVirtualYPosition() < this.getTop()) {
                this.nextFragment.translate(0, this.getTop() - this.nextFragment.getVirtualYPosition());
            }
        }
    }

    /**
     * Handles horizontal scrolling when in overlap or stacked modes.
     *
     * @param offsetX the offset along the x axis.
     */
    protected void handleHorizontalOverlappingScroll(int offsetX) {
        if (this.scrollMode == ScrollMode.Overlap) {
            if (this.currentDragFragment == null)
                if (offsetX < 0) {
                    this.currentDragFragment = this.nextFragment;
                } else {
                    this.currentDragFragment = this.previousFragment;
                }

            this.currentDragFragment.translate(offsetX, 0);

            if (this.currentDragFragment == this.previousFragment && this.previousFragment.getVirtualXPosition() > this.getLeft()) {
                this.previousFragment.translate(this.getLeft() - this.previousFragment.getVirtualXPosition(), 0);
            } else if (this.currentDragFragment == this.nextFragment && this.nextFragment.getVirtualXPosition() < this.getLeft()) {
                this.nextFragment.translate(this.getLeft() - this.nextFragment.getVirtualXPosition(), 0);
            }
        } else {
            if (this.currentDragFragment == null)
                if (offsetX < 0) {
                    this.currentDragFragment = this.nextFragment;
                } else {
                    this.currentDragFragment = this.currentFragment;
                }

            this.currentDragFragment.translate(offsetX, 0);

            if (this.currentDragFragment == this.currentFragment && this.currentFragment.getVirtualXPosition() < this.getLeft()) {
                this.previousFragment.translate(this.getLeft() - this.previousFragment.getVirtualXPosition(), 0);
            } else if (this.currentDragFragment == this.nextFragment && this.nextFragment.getVirtualXPosition() < this.getLeft()) {
                this.nextFragment.translate(this.getLeft() - this.nextFragment.getVirtualXPosition(), 0);
            }
        }
    }

    /**
     * Handles horizontal scrolling for non overlapping modes.
     *
     * @param offsetX the offset along the x axis.
     * @param offsetY the offset along the y axis.
     */
    protected void handleScrollWithoutOverlap(int offsetX, int offsetY) {
        if (scrollShouldBeHorizontal()) {
            previousFragment.translate(offsetX, 0);
            currentFragment.translate(offsetX, 0);
            nextFragment.translate(offsetX, 0);
        } else {
            previousFragment.translate(0, offsetY);
            currentFragment.translate(0, offsetY);
            nextFragment.translate(0, offsetY);
        }

        attemptCurrentFragmentUpdate(offsetX, offsetY);
    }

    /**
     * Used to update the manager after the display date of the owning calendar has been changed.
     */
    public void onDateChanged() {
        if ((this.scrollMode != ScrollMode.Overlap && this.scrollMode != ScrollMode.Stack) && this.owner.getDisplayMode() == CalendarDisplayMode.Month && !this.scrollShouldBeHorizontal()) {
            this.previousFragment.trim(); // TODO: smart trim and refresh for velocity performance boost (only trim and redraw fragment that moves).
            this.currentFragment.trim();
            this.nextFragment.trim();
        }

        if (this.arrangePassed)
            snapFragments();
    }

    /**
     * Calculates the current offset along the x axis that needs to be addressed so that the appropriate fragment according to the current scroll mode snaps to the screen.
     *
     * @return the current snap offset along the x axis.
     */
    public int currentSnapOffsetX() {
        if (this.currentDragFragment != null) {
            if (!this.scrollShouldBeHorizontal())
                return 0;

            if (this.scrollMode == ScrollMode.Overlap || this.currentDragFragment != this.currentFragment) {
                if (getFragmentExposure(this.currentDragFragment) >= MAX_FRAGMENT_CLOSURE_HORIZONTAL)
                    return (this.currentDragFragment.getVirtualXPosition() - this.getLeft()) * -1;
                else if (this.currentDragFragment.getVirtualXPosition() > this.getLeft())
                    return this.getRight() - currentDragFragment.getVirtualXPosition();
                else
                    return this.getLeft() - (this.currentDragFragment.getVirtualXPosition() + this.currentDragFragment.getWidth());
            } else if (this.scrollMode == ScrollMode.Stack) {
                if (getFragmentExposure(this.currentDragFragment) >= 1 - (MAX_FRAGMENT_CLOSURE_HORIZONTAL)) {
                    return this.getLeft() - currentDragFragment.getVirtualXPosition();
                } else
                    return this.getRight() - this.currentDragFragment.getVirtualXPosition();
            }
        }

        return this.getLeft() - this.currentFragment.getVirtualXPosition();
    }

    /**
     * Calculates the current offset along the y axis that needs to be addressed so that the appropriate fragment according to the current scroll mode snaps to the screen.
     *
     * @return the current snap offset along the y axis.
     */
    public int currentSnapOffsetY() {
        if (this.currentDragFragment != null) {
            if (this.scrollShouldBeHorizontal())
                return 0;

            if (this.scrollMode == ScrollMode.Overlap || this.currentDragFragment != this.currentFragment) {
                if (getFragmentExposure(this.currentDragFragment) >= MAX_FRAGMENT_CLOSURE_VERTICAL)
                    return (this.currentDragFragment.getVirtualYPosition() - this.getTop()) * -1;
                else if (this.currentDragFragment.getVirtualYPosition() < this.getTop())
                    return (this.currentDragFragment.getVirtualYPosition() + this.currentDragFragment.getHeight() - this.getTop()) * -1;
                else
                    return this.getBottom() - this.currentDragFragment.getVirtualYPosition();
            } else {
                if (getFragmentExposure(this.currentDragFragment) >= 1 - (MAX_FRAGMENT_CLOSURE_VERTICAL)) {
                    return this.getTop() - this.currentDragFragment.getVirtualYPosition();
                } else {
                    return this.getBottom() - this.currentDragFragment.getVirtualYPosition();
                }
            }
        }

        return this.getTop() - this.currentFragment.getVirtualYPosition();
    }

    /**
     * Used to reset all the fragments of the manager.
     *
     * @see CalendarFragment#reset()
     */
    public void reset() {
        reset(false);
    }

    /**
     * Used to reset all the fragments of the manager in either forced or normal mode. Forced mode will cause the
     * fragments to reinitialize to match a significant calendar change such as display mode change.
     *
     * @param force <code>true</code> if the reset should be forced, <code>false</code> otherwise.
     * @see CalendarFragment#reset()
     */
    public void reset(boolean force) {
        if (force) {
            init();
        } else {
            this.currentFragment.reset();
            this.previousFragment.reset();
            this.nextFragment.reset();
        }
    }

    /**
     * Used to update the active state of the fragment.
     */
    public void updateActiveFragment() {
        this.currentFragment.enabled = false;
        this.previousFragment.setEnabled(this.previousFragment.getDisplayDate() == this.activeDate);
        this.nextFragment.setEnabled(this.nextFragment.getDisplayDate() == this.activeDate);
        this.currentFragment.setEnabled(this.currentFragment.getDisplayDate() == this.activeDate);
    }

    /**
     * Handles the update of the fragments after snapping to the screen.
     */
    public void onSnapComplete() {
        if (this.currentDragFragment != null && (this.scrollMode == ScrollMode.Overlap || this.scrollMode == ScrollMode.Stack)) {

            if (this.scrollMode == ScrollMode.Overlap) {
                if ((this.scrollShouldBeHorizontal() && (this.previousFragment.getVirtualXPosition() == this.getLeft() || this.nextFragment.getVirtualXPosition() == this.getLeft())) ||
                        (!this.scrollShouldBeHorizontal() && (this.previousFragment.getVirtualYPosition() == this.getTop() || this.nextFragment.getVirtualYPosition() == this.getTop()))) {

                    this.requestFragmentsSwitch(this.currentDragFragment == this.nextFragment);
                    setActiveDate(this.owner.getDisplayDate());
                    updateActiveFragment();
                }
            } else {
                if ((this.scrollShouldBeHorizontal() && (this.currentFragment.getVirtualXPosition() == this.getRight() || this.nextFragment.getVirtualXPosition() == this.getLeft())) ||
                        (!this.scrollShouldBeHorizontal() && (this.currentFragment.getVirtualYPosition() == this.getBottom() || this.nextFragment.getVirtualYPosition() == this.getTop()))) {

                    this.requestFragmentsSwitch(this.currentDragFragment == this.nextFragment);
                    setActiveDate(this.owner.getDisplayDate());
                    updateActiveFragment();
                }
            }

            this.currentDragFragment = null;
        }
    }

    /**
     * Used to update the current display date of the calendar and request fragments rearrange if needed.
     */
    protected void attemptCurrentFragmentUpdate(int offsetX, int offsetY) {
        if (this.scrollShouldBeHorizontal()) {
            if (offsetX < 0) {
                if ((this.getLeft() - this.currentFragment.getVirtualXPosition() / (double) getWidth()) > MAX_FRAGMENT_CLOSURE_HORIZONTAL)
                    requestFragmentsSwitch(true);
            } else {
                if ((this.currentFragment.getVirtualXPosition() - getLeft()) / (double) getWidth() > MAX_FRAGMENT_CLOSURE_HORIZONTAL)
                    requestFragmentsSwitch(false);
            }
        } else {
            if (offsetY < 0) {
                if ((this.getTop() - this.currentFragment.getVirtualYPosition()) / (double) this.getHeight() > MAX_FRAGMENT_CLOSURE_VERTICAL)
                    requestFragmentsSwitch(true);
            } else {
                if (this.getBottom() - this.nextFragment.getVirtualYPosition() < 0)
                    requestFragmentsSwitch(false);
            }
        }
    }

    /**
     * Used to call update of the current display date of the owning calendar and to rearrange and update the fragments.
     *
     * @param increase <code>true</code> means the date should increase, <code>false</code> means the date should decrease.
     */
    protected void requestFragmentsSwitch(boolean increase) {
        if (increase) {
            shiftFragmentsForward();
        } else {
            shiftFragmentsBackward();
        }

        updateCurrentFragmentState();

        this.suspendActiveDateUpdate = true;
        this.owner.shiftDate(increase);
        this.suspendActiveDateUpdate = false;
    }

    private void updateCurrentFragmentState() {
        this.currentFragment.setCurrentFragment(true);
        this.previousFragment.setCurrentFragment(false);
        this.nextFragment.setCurrentFragment(false);
    }

    void updateBorders() {
        this.forwardBorderReached = !this.owner.canShiftToNextDate();
        this.backwardBorderReached = !this.owner.canShiftToPreviousDate();
    }

    /**
     * Determines if the given fragment has some part of it currently visible on the screen.
     *
     * @param fragment the fragment to evaluate.
     * @return <code>true</code> if at least some part of the fragment is being currently visible, <code>false</code> otherwise.
     */
    protected final boolean fragmentIsVisible(CalendarFragment fragment) {
        if (this.scrollShouldBeHorizontal()) {
            if (fragment.getVirtualXPosition() + fragment.getWidth() < this.getLeft() || fragment.getVirtualXPosition() > this.getWidth()) {
                return false;
            }
        } else {
            if (fragment.getVirtualYPosition() + fragment.getHeight() <= this.getTop() || fragment.getVirtualYPosition() >= this.getBottom()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Calculates the percentage of the currently visible area of a given fragment.
     *
     * @param fragment the fragment to evaluate.
     * @return the currently visible area percentage.
     */
    protected final double getFragmentExposure(CalendarFragment fragment) {
        if (this.scrollShouldBeHorizontal()) {
            if (fragment.getVirtualXPosition() < this.getLeft())
                return (fragment.getVirtualXPosition() + fragment.getWidth() - this.getLeft()) / (double) this.getWidth();
            else
                return (this.getRight() - fragment.getVirtualXPosition()) / (double) this.getWidth();
        } else {
            if (fragment.getVirtualYPosition() < this.getTop()) {
                return (fragment.getVirtualYPosition() + fragment.getHeight() - this.getTop()) / (double) this.getHeight();
            } else {
                return (this.getBottom() - fragment.getVirtualYPosition()) / (double) this.getHeight();
            }
        }
    }

    @Override
    protected void onArrange() {
        super.onArrange();

        this.currentFragment.arrange(this.getLeft(), this.getTop(), this.getRight(), getBottom());
        this.previousFragment.arrange(this.getLeft(), this.getTop(), getRight(), getBottom());
        this.nextFragment.arrange(this.getLeft(), this.getTop(), getRight(), getBottom());

        snapFragments();
    }

    /**
     * Used to snap the fragments accordingly to the current display mode and direction of scrolling.
     */
    protected void snapFragments() {
        if (this.owner.getDisplayMode() == CalendarDisplayMode.Month && !this.scrollShouldBeHorizontal() && (this.scrollMode != ScrollMode.Overlap && this.scrollMode != ScrollMode.Stack)) {
            int offset = this.previousFragment.getBottom() - (this.previousFragment.rows().get(this.previousFragment.lastRowWithCurrentDateCellsIndex()).getBottom());

            this.previousFragment.setVirtualXPosition(this.currentFragment.getVirtualXPosition());
            this.previousFragment.setVirtualYPosition(this.currentFragment.getVirtualYPosition() - this.currentFragment.getHeight() + offset);

            offset = this.currentFragment.getBottom() - this.currentFragment.rows().get(this.currentFragment.lastRowWithCurrentDateCellsIndex()).getBottom();
            this.nextFragment.setVirtualXPosition(this.currentFragment.getVirtualXPosition());
            this.nextFragment.setVirtualYPosition((this.currentFragment.getVirtualYPosition() + this.currentFragment.getHeight()) - offset);
        } else if (this.scrollShouldBeHorizontal()) {
            this.previousFragment.setVirtualYPosition(this.currentFragment.getVirtualYPosition());

            if (this.scrollMode == ScrollMode.Stack)
                this.previousFragment.setVirtualXPosition(this.currentFragment.getVirtualXPosition());
            else
                this.previousFragment.setVirtualXPosition(this.currentFragment.getVirtualXPosition() - this.currentFragment.getWidth());

            this.nextFragment.setVirtualYPosition(this.currentFragment.getVirtualYPosition());
            this.nextFragment.setVirtualXPosition(this.currentFragment.getVirtualXPosition() + this.currentFragment.getWidth());
        } else {
            this.previousFragment.setVirtualXPosition(this.currentFragment.getVirtualXPosition());

            if (this.scrollMode == ScrollMode.Stack)
                this.previousFragment.setVirtualYPosition(this.currentFragment.getVirtualYPosition());
            else
                this.previousFragment.setVirtualYPosition(this.currentFragment.getVirtualYPosition() - this.currentFragment.getHeight());

            this.nextFragment.setVirtualXPosition(this.currentFragment.getVirtualXPosition());
            this.nextFragment.setVirtualYPosition(this.currentFragment.getVirtualYPosition() + this.currentFragment.getHeight());
        }
    }

    /**
     * Used to rearrange the fragments after decreasing the date so that they are ready to be scrolled again.
     */
    protected void shiftFragmentsBackward() {
        CalendarFragment tmp;
        tmp = this.currentFragment;
        this.currentFragment = this.previousFragment;
        this.previousFragment = this.nextFragment;
        this.nextFragment = tmp;
    }

    /**
     * Used to rearrange the fragments after increasing the date so that they are ready to be scrolled again.
     */
    protected void shiftFragmentsForward() {
        CalendarFragment tmp;
        tmp = this.currentFragment;
        this.currentFragment = this.nextFragment;
        this.nextFragment = this.previousFragment;
        this.previousFragment = tmp;
    }

    public void updateDecorations() {
        if (this.owner.getShowCellDecorations() && this.owner.getDisplayMode() != CalendarDisplayMode.Year) {
            this.previousFragment.updateDecorations();
            this.currentFragment.updateDecorations();
            this.nextFragment.updateDecorations();
        }
    }

    private void init() {
        CalendarAdapter adapter = this.owner.getAdapter();

        this.previousFragment = adapter.generateFragment();
        this.currentFragment = adapter.generateFragment();
        this.nextFragment = adapter.generateFragment();

        updateCurrentFragmentState();
    }
}

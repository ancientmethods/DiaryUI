package com.commusoft.diary.diarytrials.TelerikSource;

import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import java.util.Calendar;
import java.util.List;

/**
 * Used to handle gestures applied to the calendar view.
 */
public class CalendarGestureManager {

    private static final int SCROLL_COOL_DOWN_AFTER_DISPLAY_MODE_CHANGED = 100;
    /**
     * The current calendar view instance owning the current gesture manager instance.
     */
    protected final RadCalendarView owner;
    private final Handler handler = new Handler();
    /**
     * The current animations manager.
     */
    protected CalendarAnimationsManager animationsManager;
    /**
     * The current scroll manager.
     */
    protected CalendarScrollManager scrollManager;
    /**
     * The current selection manager.
     */
    protected CalendarSelectionManager selectionManager;
    /**
     * Listener to be called when a cell has been clicked.
     */
    protected RadCalendarView.OnCellClickListener onCellClickListener;
    /**
     * The current display mode.
     */
    protected CalendarDisplayMode displayMode;
    /**
     * Holds the current scale state of the gesture manager. <code>true</code> if currently there is a scale gesture
     * being applied, <code>false</code> otherwise.
     */
    protected boolean isScaleInProgress;
    /**
     * Holds a value determining if the current scale is positive. <code>true</code> if the scale is positive, <code>false</code> otherwise.
     */
    protected boolean currentScaleFactorIsPositive;
    /**
     * The current gesture detector.
     */
    protected GestureDetector gestureDetector;

    /**
     * The current scale gesture detector.
     */
    protected ScaleGestureDetector scaleGestureDetector;

    /**
     * The current gesture assistant.
     */
    protected CalendarGestureAssistant gestureAssistant;

    /**
     * The current scroll mode.
     */
    protected ScrollMode scrollMode;
    CalendarSelectionMode selectionMode;
    private boolean suspendScroll;
    private boolean isScrollInProgress;
    private CalendarCell lastPressedCell;
    private CalendarCell firstPressedCell;
    private boolean hasMoved;
    private boolean usingDragToMakeRangeSelection;
    private boolean doubleTapToChangeDisplayMode;
    private boolean tapToChangeDisplayMode;
    private boolean swipeUpToChangeDisplayMode;
    private boolean swipeDownToChangeDisplayMode;
    private boolean pinchCloseToChangeDisplayMode;
    private boolean pinchOpenToChangeDisplayMode;

    /**
     * Creates a new instance of the {@link com.telerik.widget.calendar.CalendarGestureManager} class.
     *
     * @param owner the calendar view instance owning the current gesture manager.
     */
    public CalendarGestureManager(RadCalendarView owner) {
        this.owner = owner;
        init();
    }

    CalendarAnimationsManager getAnimationsManager() {
        return this.animationsManager;
    }

    void setAnimationsManager(CalendarAnimationsManager animationsManager) {
        this.animationsManager = animationsManager;
    }

    CalendarScrollManager getScrollManager() {
        return this.scrollManager;
    }

    void setScrollManager(CalendarScrollManager scrollManager) {
        this.scrollManager = scrollManager;
    }

    ScrollMode getScrollMode() {
        return this.scrollMode;
    }

    void setScrollMode(ScrollMode scrollMode) {
        this.scrollMode = scrollMode;
    }

    CalendarSelectionManager getSelectionManager() {
        return this.selectionManager;
    }

    void setSelectionManager(CalendarSelectionManager selectionManager) {
        this.selectionManager = selectionManager;
    }

    CalendarDisplayMode getDisplayMode() {
        return this.displayMode;
    }

    void setDisplayMode(CalendarDisplayMode displayMode) {
        this.displayMode = displayMode;
        onDisplayModeChanged();
    }

    CalendarGestureAssistant getGestureAssistant() {
        return this.gestureAssistant;
    }

    void setGestureAssistant(CalendarGestureAssistant gestureAssistant) {
        if (gestureAssistant == null)
            throw new NullPointerException("gestureAssistant");

        if (this.gestureAssistant != gestureAssistant) {
            this.gestureAssistant = gestureAssistant;
        }
    }

    /**
     * Gets a value which determines whether the display mode will be changed while the calendar is
     * in year view and the detected gesture is pinch open. If the gesture is enabled the
     * display mode will be changed to month.
     *
     * @return whether display mode is changed while in year view and the detected gesture is pinch open
     */
    public boolean isUsingPinchOpenToChangeDisplayMode() {
        return gestureAssistant.isUsingPinchOpenToChangeDisplayMode();
    }

    /**
     * Sets a value which determines whether the display mode will be changed while the calendar is
     * in year view and the detected gesture is pinch open. If the gesture is enabled the
     * display mode will be changed to month.
     *
     * @param pinchOpenToChangeDisplayMode whether display mode is changed while in year view and the detected gesture is pinch open
     */
    public void setPinchOpenToChangeDisplayMode(boolean pinchOpenToChangeDisplayMode) {
        this.pinchOpenToChangeDisplayMode = pinchOpenToChangeDisplayMode;
        gestureAssistant.setPinchOpenToChangeDisplayMode(pinchOpenToChangeDisplayMode);
    }

    /**
     * Gets a value which determines whether the display mode will be changed while the calendar is
     * in month view and the detected gesture is pinch close. If the gesture is enabled the
     * display mode will be changed to year.
     *
     * @return whether display mode is changed while in month view and the detected gesture is pinch close
     */
    public boolean isUsingPinchCloseToChangeDisplayMode() {
        return gestureAssistant.isUsingPinchCloseToChangeDisplayMode();
    }

    /**
     * Sets a value which determines whether the display mode will be changed while the calendar is
     * in month view and the detected gesture is pinch close. If the gesture is enabled the
     * display mode will be changed to year.
     *
     * @param pinchCloseToChangeDisplayMode whether display mode is changed while in month view and the detected gesture is pinch close
     */
    public void setPinchCloseToChangeDisplayMode(boolean pinchCloseToChangeDisplayMode) {
        this.pinchCloseToChangeDisplayMode = pinchCloseToChangeDisplayMode;
        gestureAssistant.setPinchCloseToChangeDisplayMode(pinchCloseToChangeDisplayMode);
    }

    /**
     * Gets a value which determines whether the display mode will be changed while the calendar is
     * in week view and the detected gesture is swipe down. If the gesture is enabled the
     * display mode will be changed to month.
     *
     * @return whether display mode is changed while in week view and the detected gesture swipe down
     */
    public boolean isUsingSwipeDownToChangeDisplayMode() {
        return gestureAssistant.isUsingSwipeDownToChangeDisplayMode();
    }

    /**
     * Sets a value which determines whether the display mode will be changed while the calendar is
     * in week view and the detected gesture is swipe down. If the gesture is enabled the
     * display mode will be changed to month.
     *
     * @param swipeDownToChangeDisplayMode whether display mode is changed while in week view and the detected gesture swipe down
     */
    public void setSwipeDownToChangeDisplayMode(boolean swipeDownToChangeDisplayMode) {
        this.swipeDownToChangeDisplayMode = swipeDownToChangeDisplayMode;
        gestureAssistant.setSwipeDownToChangeDisplayMode(swipeDownToChangeDisplayMode);
    }

    /**
     * Gets a value which determines whether the display mode will be changed while the calendar is
     * in month view and the detected gesture is swipe up. If the gesture is enabled the
     * display mode will be changed to week.
     *
     * @return whether display mode is changed while in month view and the detected gesture is swipe up
     */
    public boolean isUsingSwipeUpToChangeDisplayMode() {
        return gestureAssistant.isUsingSwipeUpToChangeDisplayMode();
    }

    /**
     * Sets a value which determines whether the display mode will be changed while the calendar is
     * in month view and the detected gesture is swipe up. If the gesture is enabled the
     * display mode will be changed to week.
     *
     * @param swipeUpToChangeDisplayMode whether display mode is changed while in month view and the detected gesture is swipe up
     */
    public void setSwipeUpToChangeDisplayMode(boolean swipeUpToChangeDisplayMode) {
        this.swipeUpToChangeDisplayMode = swipeUpToChangeDisplayMode;
        gestureAssistant.setSwipeUpToChangeDisplayMode(swipeUpToChangeDisplayMode);
    }

    /**
     * Gets a value which determines whether the display mode will be changed while the calendar is
     * in year view and the detected gesture is tap. If the gesture is enabled the
     * display mode will be changed to month and the display date will be changed
     * so that the month that is tapped becomes visible.
     *
     * @return whether display mode is changed while in year view and the detected gesture is tap
     */
    public boolean isUsingTapToChangeDisplayMode() {
        return gestureAssistant.isUsingTapToChangeDisplayMode();
    }

    /**
     * Sets a value which determines whether the display mode will be changed while the calendar is
     * in year view and the detected gesture is tap. If the gesture is enabled the
     * display mode will be changed to month and the display date will be changed
     * so that the month that is tapped becomes visible.
     *
     * @param tapToChangeDisplayMode whether display mode is changed while in year view and the detected gesture is tap
     */
    public void setTapToChangeDisplayMode(boolean tapToChangeDisplayMode) {
        this.tapToChangeDisplayMode = tapToChangeDisplayMode;
        gestureAssistant.setTapToChangeDisplayMode(tapToChangeDisplayMode);
    }

    /**
     * Gets a value which determines whether the display mode will be changed while the calendar is
     * in month or year view and the detected gesture is double tap. If the gesture is enabled the
     * display mode will be changed from year to month and the display date will be changed
     * so that the month that is tapped becomes visible if the calendar is in year view. If it is in
     * month view the display mode will be changed to year.
     *
     * @return whether display mode is changed while in month or year view and the detected gesture is double tap
     */
    public boolean isUsingDoubleTapToChangeDisplayMode() {
        return gestureAssistant.isUsingDoubleTapToChangeDisplayMode();
    }

    /**
     * Sets a value which determines whether the display mode will be changed while the calendar is
     * in month or year view and the detected gesture is double tap. If the gesture is enabled the
     * display mode will be changed from year to month and the display date will be changed
     * so that the month that is tapped becomes visible if the calendar is in year view. If it is in
     * month view the display mode will be changed to year.
     *
     * @param doubleTapToChangeDisplayMode whether display mode is changed while in month or year view and the detected gesture is double tap
     */
    public void setDoubleTapToChangeDisplayMode(boolean doubleTapToChangeDisplayMode) {
        this.doubleTapToChangeDisplayMode = doubleTapToChangeDisplayMode;
        gestureAssistant.setDoubleTapToChangeDisplayMode(doubleTapToChangeDisplayMode);
    }

    /**
     * Gets a value which determines whether the drag gesture will be used to select a rage of cells. Once enabled this will
     * prevent the scroll behavior and will enable range selection using a drag.
     *
     * @return <code>true</code> if drag for range selection is enabled, <code>false</code> otherwise.
     */
    public boolean isUsingDragToMakeRangeSelection() {
        return gestureAssistant.isUsingDragToMakeRangeSelection();
    }

    /**
     * Sets a value which determines whether the drag gesture will be used to select a rage of cells. Once enabled this will
     * prevent the scroll behavior and will enable range selection using a drag.
     *
     * @param usingDragToMakeRangeSelection the new drag state.
     */
    public void setUsingDragToMakeRangeSelection(boolean usingDragToMakeRangeSelection) {
        this.usingDragToMakeRangeSelection = usingDragToMakeRangeSelection;
        gestureAssistant.setUsingDragToMakeRangeSelection(usingDragToMakeRangeSelection);
    }

    /**
     * Gets a listener that will be called when a cell is being pressed.
     *
     * @return the current pressed cell listener.
     */
    public RadCalendarView.OnCellClickListener getOnCellClickListener() {
        return this.onCellClickListener;
    }

    /**
     * Sets a listener that will be called when a cell is being pressed.
     *
     * @param onCellClickListener the new pressed cell listener.
     */
    public void setOnCellClickListener(RadCalendarView.OnCellClickListener onCellClickListener) {
        this.onCellClickListener = onCellClickListener;
    }

    /**
     * Handles the scroll gesture using the passed parameters and responding according to the current display mode.
     *
     * @param x         the current finger position along the x axis.
     * @param y         the current finger position along the y axis.
     * @param distanceX the distance of the scroll along the x axis.
     * @param distanceY the distance of the scroll along the y axis.   @return <code>true</code> if the gesture was handled, <code>false</code> otherwise.
     */
    public boolean handleScroll(float x, float y, float distanceX, float distanceY) {
        if (selectionMode == CalendarSelectionMode.Range &&
                this.gestureAssistant.isUsingDragToMakeRangeSelection()) {

            List<CalendarCell> pressedCell = this.scrollManager.getCellsAtLocation((int) x, (int) y);
            if (pressedCell != null && pressedCell.size() > 0) {
                CalendarDayCell cell = (CalendarDayCell) pressedCell.get(0);
                if (cell == null || cell.getCellType() != CalendarCellType.Date || !cell.isSelectable() ||
                        (owner.getMinDate() != 0 && cell.getDate() < owner.getMinDate()) ||
                        (owner.getMaxDate() != 0 && cell.getDate() > owner.getMaxDate())) {
                    return true;
                }

                if (this.isScrollInProgress) {
                    if (cell != this.lastPressedCell) {
                        this.lastPressedCell = cell;
                        this.selectionManager.setSelectedRange(new DateRange(Math.min(firstPressedCell.getDate(), lastPressedCell.getDate()), Math.max(firstPressedCell.getDate(), lastPressedCell.getDate())));
                    }
                } else {
                    this.isScrollInProgress = true;
                    this.firstPressedCell = cell;
                    this.selectionManager.setSelectedRange(new DateRange(firstPressedCell.getDate(), firstPressedCell.getDate()));
                }
                this.owner.invalidate();
            }

            return true;
        }

        if (suspendScroll)
            return false;

        this.hasMoved = true;
        this.isScrollInProgress = true;

        // Legacy to be removed
        if (this.owner.isHorizontalScroll()) {
            if ((displayMode == CalendarDisplayMode.Month && !gestureAssistant.isUsingSwipeHorizontalToChangeMonths()) ||
                    (displayMode == CalendarDisplayMode.Week && !gestureAssistant.isUsingSwipeHorizontalToChangeWeeks()) ||
                    (displayMode == CalendarDisplayMode.Year && !gestureAssistant.isUsingSwipeHorizontalToChangeYears()))
                return false;
        } else {
            if ((displayMode == CalendarDisplayMode.Month && !gestureAssistant.isUsingSwipeVerticalToChangeMonths()) ||
                    (displayMode == CalendarDisplayMode.Week && !gestureAssistant.isUsingSwipeVerticalToChangeWeeks()) ||
                    (displayMode == CalendarDisplayMode.Year && !gestureAssistant.isUsingSwipeVerticalToChangeYears()))
                return false;
        }

        if (this.scrollMode != ScrollMode.None)
            if (!this.isScaleInProgress) {
                this.owner.beginUpdate();
                this.scrollManager.scroll((int) -distanceX, (int) -distanceY);
                this.owner.endUpdate();
            }

        return true;
    }

    /**
     * Handles the touch gesture.
     *
     * @param event the event to be handled.
     * @return <code>true</code> if the gesture was handled, <code>false</code> otherwise.
     */
    public boolean handleTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP)
            onFingerUp();

        if (!this.owner.isEnabled()) {
            // A disabled view that is clickable still consumes the touch
            // events, it just doesn't respond to them.
            return this.owner.isClickable() || this.owner.isLongClickable();
        }

        if (this.owner.getTouchDelegate() != null) {
            this.owner.getTouchDelegate().onTouchEvent(event);
        }

        if (!this.owner.isClickable() && !this.owner.isLongClickable()) {
            return false;
        }

        boolean handledByScaleGestureDetector = this.scaleGestureDetector.onTouchEvent(event);
        boolean handledByGestureDetector = this.gestureDetector.onTouchEvent(event);

        return handledByGestureDetector || handledByScaleGestureDetector;
    }

    /**
     * Handles the fling gesture and applies the result by repeatedly invalidating the calendar view instance as needed.
     *
     * @param velocityX the velocity produced by the gesture along the x axis.
     * @param velocityY the velocity produced by the gesture along the y axis.
     * @return <code>true</code> if the gesture was handled, <code>false</code> otherwise.
     */
    public boolean handleFling(float velocityX, float velocityY) {
        if (this.suspendScroll || this.isScaleInProgress || (this.scrollMode != ScrollMode.Free && this.scrollMode != ScrollMode.Combo)) {
            return false;
        }

        // Legacy to be removed
        if (this.scrollManager.scrollShouldBeHorizontal()) {
            if ((displayMode == CalendarDisplayMode.Month && !gestureAssistant.isUsingSwipeHorizontalToChangeMonths()) ||
                    (displayMode == CalendarDisplayMode.Week && !gestureAssistant.isUsingSwipeHorizontalToChangeWeeks()) ||
                    (displayMode == CalendarDisplayMode.Year && !gestureAssistant.isUsingSwipeHorizontalToChangeYears()))
                return false;
        } else {
            if ((displayMode == CalendarDisplayMode.Month && !gestureAssistant.isUsingSwipeVerticalToChangeMonths()) ||
                    (displayMode == CalendarDisplayMode.Week && !gestureAssistant.isUsingSwipeVerticalToChangeWeeks()) ||
                    (displayMode == CalendarDisplayMode.Year && !gestureAssistant.isUsingSwipeVerticalToChangeYears()))
                return false;
        }

        this.animationsManager.setVelocity((int) velocityX, (int) velocityY);

        return true;
    }

    /**
     * Handles the onDown event.
     *
     * @return <code>true</code> if the event was handled, <code>false</code> otherwise.
     */
    public boolean handleOnDown() {
        if (this.animationsManager.animationInProcess())
            return false;

        this.animationsManager.reset();
        return true;
    }

    /**
     * Handles the confirmed single tap gesture.
     *
     * @param event the event.
     * @return <code>true</code> if the event was handled, <code>false</code> otherwise.
     */
    public boolean handleSingleTapConfirmed(MotionEvent event) {
        List<CalendarCell> pressedCells = this.scrollManager.getCellsAtLocation((int) event.getX(), (int) event.getY());

        if (pressedCells != null && pressedCells.size() > 0) {

            CalendarCell cell = pressedCells.get(0);
            if (cell != null) {
                if (cell instanceof CalendarDayCell)
                    this.handleTapGesture((CalendarDayCell) cell);

                if (this.gestureAssistant.isUsingTapToChangeDisplayMode() && this.displayMode == CalendarDisplayMode.Year) {

                    long date = cell.getDate();
                    if ((this.owner.getMinDate() != 0 && date < this.owner.getMinDate()) ||
                            (this.owner.getMaxDate() != 0 && date > this.owner.getMaxDate())) {
                        Long safeDate = getSafeDate(date);
                        if (safeDate == null)
                            return true;

                        date = safeDate;
                    }

                    this.owner.setDisplayDate(date);
                    this.owner.changeDisplayMode(CalendarDisplayMode.Month);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Handles the double tap gesture.
     *
     * @param event the event to be handled.
     * @return <code>true</code> if the event was handled, <code>false</code> otherwise.
     */
    public boolean handleDoubleTap(MotionEvent event) {
        if (this.displayMode == CalendarDisplayMode.Month) {
            if (!this.gestureAssistant.isUsingDoubleTapToChangeDisplayMode()) {
                return false;
            }
            this.owner.changeDisplayMode(CalendarDisplayMode.Year);
            return true;
        }
        if (this.displayMode == CalendarDisplayMode.Year) {
            if (!this.gestureAssistant.isUsingDoubleTapToChangeDisplayMode()) {
                return false;
            }

            List<CalendarCell> pressedCell = this.scrollManager.getCellsAtLocation((int) event.getX(), (int) event.getY());

            if (pressedCell != null && pressedCell.size() > 0) {
                long date = pressedCell.get(0).getDate();

                if ((this.owner.getMinDate() != 0 && date < this.owner.getMinDate()) ||
                        (this.owner.getMaxDate() != 0 && date > this.owner.getMaxDate())) {
                    Long safeDate = getSafeDate(date);
                    if (safeDate == null)
                        return true;

                    date = safeDate;
                }

                this.owner.setDisplayDate(date);
                this.owner.changeDisplayMode(CalendarDisplayMode.Month);

                return true;
            }
        }
        return false;
    }

    private Long getSafeDate(long date) {
        Calendar calendar = this.owner.getCalendar();
        calendar.setTimeInMillis(date);
        int month = calendar.get(Calendar.MONTH);

        if (this.owner.getMinDate() != 0 && this.owner.getMinDate() > date) {
            calendar.setTimeInMillis(this.owner.getMinDate());
            if (month < calendar.get(Calendar.MONTH))
                return null;
            else
                date = calendar.getTimeInMillis();
        } else if (this.owner.getMaxDate() != 0 && this.owner.getMaxDate() < date) {
            calendar.setTimeInMillis(this.owner.getMaxDate());
            if (month > calendar.get(Calendar.MONTH))
                return null;
            else
                date = calendar.getTimeInMillis();
        }

        return date;
    }

    /**
     * Handles the beginning of a scale gesture.
     *
     * @return <code>true</code> if the gesture was handled, <code>false</code> otherwise.
     */
    public boolean handleOnScaleBegin() {
        this.isScaleInProgress = true;

        return true;
    }

    /**
     * Handles the end of a scale gesture.
     */
    public void handleOnScaleEnd() {
        if (currentScaleFactorIsPositive) {
            // Legacy to be removed
            if (!gestureAssistant.isUsingPinchOpenToChangeDisplayMode()) {
                this.isScaleInProgress = false;
                return;
            }

            this.onPinchOpen();
        } else {
            // Legacy to be removed
            if (!gestureAssistant.isUsingPinchCloseToChangeDisplayMode()) {
                this.isScaleInProgress = false;
                return;
            }

            this.onPinchClose();
        }

        this.isScaleInProgress = false;
    }

    /**
     * Handles the scale gesture.
     *
     * @param scaleGestureDetector the scale gesture detector to be used from which to take the current scale factor.
     * @return <code>true</code> if the gesture was handled, <code>false</code> otherwise.
     */
    public boolean handleOnScale(ScaleGestureDetector scaleGestureDetector) {
        if (scaleGestureDetector.getScaleFactor() > 1) {
            this.currentScaleFactorIsPositive = true;
        } else if (scaleGestureDetector.getScaleFactor() < 1) {
            this.currentScaleFactorIsPositive = false;
        }

        return true;
    }

    /**
     * Handles the single tap up gesture.
     *
     * @return <code>true</code> if the gesture was handled, <code>false</code> otherwise.
     */
    public boolean handleOnSingleTapUp() {
        return false;
    }

    /**
     * Called when the display mode has been changed.
     */
    protected void onDisplayModeChanged() {
        this.suspendScroll = true;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                suspendScroll = false;
            }
        }, SCROLL_COOL_DOWN_AFTER_DISPLAY_MODE_CHANGED);
    }

    /**
     * Invoked on finger up.
     */
    protected void onFingerUp() {
        if (this.isScrollInProgress) {
            this.isScrollInProgress = false;
            if (this.hasMoved) {
                this.animationsManager.requestActiveDateChange();

                if ((this.scrollMode == ScrollMode.Sticky || this.scrollMode == ScrollMode.Combo) ||
                        (this.scrollMode == ScrollMode.Overlap || this.scrollMode == ScrollMode.Stack) ||
                        displayMode == CalendarDisplayMode.Week ||
                        this.scrollManager.scrollShouldBeHorizontal())
                    this.animationsManager.snapFragments();

                this.owner.invalidate();
                this.hasMoved = false;
            }
        }
    }

    /**
     * Handles the tap gesture.
     *
     * @param calendarCell the calendar cell on which the tap gesture occurred.
     */
    protected void handleTapGesture(CalendarDayCell calendarCell) {
        if (this.displayMode == CalendarDisplayMode.Year) {
            return;
        }

        this.selectionManager.handleTapGesture(calendarCell);

        if (this.onCellClickListener != null) {
            this.onCellClickListener.onCellClick(calendarCell);
        }
    }

    /**
     * Invoked when the user pinches in.
     */
    protected void onPinchOpen() {
        if (!this.gestureAssistant.isUsingPinchOpenToChangeDisplayMode()) {
            return;
        }

        if (this.displayMode == CalendarDisplayMode.Year) {
            this.owner.changeDisplayMode(CalendarDisplayMode.Month);
        }
        /*else if (this.displayMode == CalendarDisplayMode.Month) {
            this.owner.changeDisplayMode(CalendarDisplayMode.Week);
        }*/
    }

    /**
     * Invoked when the user pinches out.
     */
    protected void onPinchClose() {
        if (!this.gestureAssistant.isUsingPinchCloseToChangeDisplayMode()) {
            return;
        }

        if (this.displayMode == CalendarDisplayMode.Month) {
            this.owner.changeDisplayMode(CalendarDisplayMode.Year);
        }
        /*else if (this.displayMode == CalendarDisplayMode.Week) {
            this.owner.changeDisplayMode(CalendarDisplayMode.Month);
        }*/
    }

    private void init() {
        this.gestureDetector = new GestureDetector(this.owner.getContext(), this.owner);
        this.gestureDetector.setIsLongpressEnabled(false);
        this.scaleGestureDetector = new ScaleGestureDetector(this.owner.getContext(), this.owner);
        this.gestureAssistant = new CalendarGestureAssistant();
    }

    void setSelectionMode(CalendarSelectionMode selectionMode) {
        this.selectionMode = selectionMode;
    }
}

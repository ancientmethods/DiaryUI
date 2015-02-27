package com.commusoft.diary.diarytrials.DiarySource;

@Deprecated
/**
 * Helper class which allows to determine the gestures that are handled
 * by {@link com.telerik.widget.calendar.RadCalendarView}. Deprecated - Logic in this class will be supported for a short time, but is no longer needed. Use the {@link com.telerik.widget.calendar.CalendarGestureManager} properties instead.
 */
public class CalendarGestureAssistant {
    private boolean swipeHorizontalToChangeMonths;
    private boolean swipeHorizontalToChangeWeeks;
    private boolean swipeHorizontalToChangeYears;

    private boolean swipeVerticalToChangeMonths;
    private boolean swipeVerticalToChangeWeeks;
    private boolean swipeVerticalToChangeYears;

    private boolean pinchOpenToChangeDisplayMode;
    private boolean pinchCloseToChangeDisplayMode;

    private boolean swipeDownToChangeDisplayMode;
    private boolean swipeUpToChangeDisplayMode;

    private boolean tapToChangeDisplayMode;
    private boolean doubleTapToChangeDisplayMode;
    private boolean usingDragToMakeRangeSelection;

    @Deprecated
    /**
     * Creates an instance of the {@link com.telerik.widget.calendar.CalendarGestureAssistant} class. Deprecated class will be supported for a short period of time. Use the {@link com.telerik.widget.calendar.CalendarGestureManager} properties instead.
     */
    public CalendarGestureAssistant() {
        this.swipeHorizontalToChangeMonths = false;
        this.swipeHorizontalToChangeWeeks = false;
        this.swipeHorizontalToChangeYears = false;

        this.swipeVerticalToChangeMonths = true;
        this.swipeVerticalToChangeWeeks = true;
        this.swipeVerticalToChangeYears = true;

        this.pinchOpenToChangeDisplayMode = true;
        this.pinchCloseToChangeDisplayMode = true;

        this.swipeDownToChangeDisplayMode = false;
        this.swipeUpToChangeDisplayMode = false;

        this.tapToChangeDisplayMode = true;
        this.doubleTapToChangeDisplayMode = true;
    }

    @Deprecated
    /**
     * Gets a value which determines whether the display date will be changed while the calendar is
     * in month view and the detected gesture is horizontal swipe. If the gesture is enabled the
     * display date will be increased by one month when swipe left is detected
     * and decreased by one month when swipe right is detected. Deprecated, use the {@link com.telerik.widget.calendar.CalendarGestureManager} properties instead.
     *
     * @return whether display date is changed while in month view and the detected gesture is swipe left or right
     */
    public boolean isUsingSwipeHorizontalToChangeMonths() {
        return swipeHorizontalToChangeMonths;
    }

    @Deprecated
    /**
     * Sets a value which determines whether the display date will be changed while the calendar is
     * in month view and the detected gesture is horizontal swipe. If the gesture is enabled the
     * display date will be increased by one month when swipe left is detected
     * and decreased by one month when swipe right is detected. Deprecated, use the {@link com.telerik.widget.calendar.CalendarGestureManager} properties instead.
     *
     * @param swipeHorizontalToChangeMonths whether display date is changed while in month view
     *                                      and the detected gesture is swipe left or right
     */
    public void setSwipeHorizontalToChangeMonths(boolean swipeHorizontalToChangeMonths) {
        this.swipeHorizontalToChangeMonths = swipeHorizontalToChangeMonths;
    }

    @Deprecated
    /**
     * Gets a value which determines whether the display date will be changed while the calendar is
     * in week view and the detected gesture is horizontal swipe. If the gesture is enabled the
     * display date will be increased by one week when swipe left is detected
     * and decreased by one week when swipe right is detected. Deprecated, use the {@link com.telerik.widget.calendar.CalendarGestureManager} properties instead.
     *
     * @return whether display date is changed while in month view and the detected gesture is swipe left or right
     */
    public boolean isUsingSwipeHorizontalToChangeWeeks() {
        return swipeHorizontalToChangeWeeks;
    }

    @Deprecated
    /**
     * Sets a value which determines whether the display date will be changed while the calendar is
     * in week view and the detected gesture is horizontal swipe. If the gesture is enabled the
     * display date will be increased by one week when swipe left is detected
     * and decreased by one week when swipe right is detected. Deprecated, use the {@link com.telerik.widget.calendar.CalendarGestureManager} properties instead.
     *
     * @param swipeHorizontalToChangeWeeks whether display date is changed while in week view
     *                                     and the detected gesture is swipe left or right
     */
    public void setSwipeHorizontalToChangeWeeks(boolean swipeHorizontalToChangeWeeks) {
        this.swipeHorizontalToChangeWeeks = swipeHorizontalToChangeWeeks;
    }

    @Deprecated
    /**
     * Gets a value which determines whether the display date will be changed while the calendar is
     * in year view and the detected gesture is horizontal swipe. If the gesture is enabled the
     * display date will be increased by one year when swipe left is detected
     * and decreased by one year when swipe right is detected. Deprecated, use the {@link com.telerik.widget.calendar.CalendarGestureManager} properties instead.
     *
     * @return whether display date is changed while in year view and the detected gesture is swipe left or right
     */
    public boolean isUsingSwipeHorizontalToChangeYears() {
        return swipeHorizontalToChangeYears;
    }

    @Deprecated
    /**
     * Sets a value which determines whether the display date will be changed while the calendar is
     * in year view and the detected gesture is horizontal swipe. If the gesture is enabled the
     * display date will be increased by one year when swipe left is detected
     * and decreased by one year when swipe right is detected. Deprecated, use the {@link com.telerik.widget.calendar.CalendarGestureManager} properties instead.
     *
     * @param swipeHorizontalToChangeYears whether display date is changed while in year view
     *                                     and the detected gesture is swipe left or right
     */
    public void setSwipeHorizontalToChangeYears(boolean swipeHorizontalToChangeYears) {
        this.swipeHorizontalToChangeYears = swipeHorizontalToChangeYears;
    }

    @Deprecated
    /**
     * Gets a value which determines whether the display date will be changed while the calendar is
     * in month view and the detected gesture is vertical swipe. If the gesture is enabled the
     * display date will be increased by one month when swipe top is detected
     * and decreased by one month when swipe bottom is detected. Deprecated, use the {@link com.telerik.widget.calendar.CalendarGestureManager} properties instead.
     *
     * @return whether display date is changed while in month view and the detected gesture is swipe top or bottom
     */
    public boolean isUsingSwipeVerticalToChangeMonths() {
        return swipeVerticalToChangeMonths;
    }

    @Deprecated
    /**
     * Sets a value which determines whether the display date will be changed while the calendar is
     * in month view and the detected gesture is vertical swipe. If the gesture is enabled the
     * display date will be increased by one month when swipe top is detected
     * and decreased by one month when swipe bottom is detected. Deprecated, use the {@link com.telerik.widget.calendar.CalendarGestureManager} properties instead.
     *
     * @param swipeVerticalToChangeMonths whether display date is changed while in month view
     *                                    and the detected gesture is swipe top or bottom
     */
    public void setSwipeVerticalToChangeMonths(boolean swipeVerticalToChangeMonths) {
        this.swipeVerticalToChangeMonths = swipeVerticalToChangeMonths;
    }

    @Deprecated
    /**
     * Gets a value which determines whether the display date will be changed while the calendar is
     * in week view and the detected gesture is vertical swipe. If the gesture is enabled the
     * display date will be increased by one week when swipe top is detected
     * and decreased by one week when swipe bottom is detected. Deprecated, use the {@link com.telerik.widget.calendar.CalendarGestureManager} properties instead.
     *
     * @return whether display date is changed while in month view and the detected gesture is swipe top or bottom
     */
    public boolean isUsingSwipeVerticalToChangeWeeks() {
        return swipeVerticalToChangeWeeks;
    }

    @Deprecated
    /**
     * Sets a value which determines whether the display date will be changed while the calendar is
     * in week view and the detected gesture is vertical swipe. If the gesture is enabled the
     * display date will be increased by one week when swipe top is detected
     * and decreased by one week when swipe bottom is detected. Deprecated, use the {@link com.telerik.widget.calendar.CalendarGestureManager} properties instead.
     *
     * @param swipeVerticalToChangeWeeks whether display date is changed while in week view
     *                                   and the detected gesture is swipe top or bottom
     */
    public void setSwipeVerticalToChangeWeeks(boolean swipeVerticalToChangeWeeks) {
        this.swipeVerticalToChangeWeeks = swipeVerticalToChangeWeeks;
    }

    @Deprecated
    /**
     * Gets a value which determines whether the display date will be changed while the calendar is
     * in year view and the detected gesture is vertical swipe. If the gesture is enabled the
     * display date will be increased by one year when swipe top is detected
     * and decreased by one year when swipe bottom is detected. Deprecated, use the {@link com.telerik.widget.calendar.CalendarGestureManager} properties instead.
     *
     * @return whether display date is changed while in year view and the detected gesture is swipe top or bottom
     */
    public boolean isUsingSwipeVerticalToChangeYears() {
        return swipeVerticalToChangeYears;
    }

    @Deprecated
    /**
     * Sets a value which determines whether the display date will be changed while the calendar is
     * in year view and the detected gesture is vertical swipe. If the gesture is enabled the
     * display date will be increased by one year when swipe top is detected
     * and decreased by one year when swipe bottom is detected. Deprecated, use the {@link com.telerik.widget.calendar.CalendarGestureManager} properties instead.
     *
     * @param swipeVerticalToChangeYears whether display date is changed while in year view
     *                                   and the detected gesture is swipe top or bottom
     */
    public void setSwipeVerticalToChangeYears(boolean swipeVerticalToChangeYears) {
        this.swipeVerticalToChangeYears = swipeVerticalToChangeYears;
    }

    @Deprecated
    /**
     * Gets a value which determines whether the display mode will be changed while the calendar is
     * in year view and the detected gesture is pinch open. If the gesture is enabled the
     * display mode will be changed to month. Deprecated, use the {@link com.telerik.widget.calendar.CalendarGestureManager} properties instead.
     *
     * @return whether display mode is changed while in year view and the detected gesture is pinch open
     */
    public boolean isUsingPinchOpenToChangeDisplayMode() {
        return pinchOpenToChangeDisplayMode;
    }

    @Deprecated
    /**
     * Sets a value which determines whether the display mode will be changed while the calendar is
     * in year view and the detected gesture is pinch open. If the gesture is enabled the
     * display mode will be changed to month. Deprecated, use the {@link com.telerik.widget.calendar.CalendarGestureManager} properties instead.
     *
     * @param pinchOpenToChangeDisplayMode whether display mode is changed while in year view and the detected gesture is pinch open
     */
    public void setPinchOpenToChangeDisplayMode(boolean pinchOpenToChangeDisplayMode) {
        this.pinchOpenToChangeDisplayMode = pinchOpenToChangeDisplayMode;
    }

    @Deprecated
    /**
     * Gets a value which determines whether the display mode will be changed while the calendar is
     * in month view and the detected gesture is pinch close. If the gesture is enabled the
     * display mode will be changed to year. Deprecated, use the {@link com.telerik.widget.calendar.CalendarGestureManager} properties instead.
     *
     * @return whether display mode is changed while in month view and the detected gesture is pinch close
     */
    public boolean isUsingPinchCloseToChangeDisplayMode() {
        return pinchCloseToChangeDisplayMode;
    }

    @Deprecated
    /**
     * Sets a value which determines whether the display mode will be changed while the calendar is
     * in month view and the detected gesture is pinch close. If the gesture is enabled the
     * display mode will be changed to year. Deprecated, use the {@link com.telerik.widget.calendar.CalendarGestureManager} properties instead.
     *
     * @param pinchCloseToChangeDisplayMode whether display mode is changed while in month view and the detected gesture is pinch close
     */
    public void setPinchCloseToChangeDisplayMode(boolean pinchCloseToChangeDisplayMode) {
        this.pinchCloseToChangeDisplayMode = pinchCloseToChangeDisplayMode;
    }

    @Deprecated
    /**
     * Gets a value which determines whether the display mode will be changed while the calendar is
     * in week view and the detected gesture is swipe down. If the gesture is enabled the
     * display mode will be changed to month. Deprecated, use the {@link com.telerik.widget.calendar.CalendarGestureManager} properties instead.
     *
     * @return whether display mode is changed while in week view and the detected gesture swipe down
     */
    public boolean isUsingSwipeDownToChangeDisplayMode() {
        return swipeDownToChangeDisplayMode;
    }

    @Deprecated
    /**
     * Sets a value which determines whether the display mode will be changed while the calendar is
     * in week view and the detected gesture is swipe down. If the gesture is enabled the
     * display mode will be changed to month. Deprecated, use the {@link com.telerik.widget.calendar.CalendarGestureManager} properties instead.
     *
     * @param swipeDownToChangeDisplayMode whether display mode is changed while in week view and the detected gesture swipe down
     */
    public void setSwipeDownToChangeDisplayMode(boolean swipeDownToChangeDisplayMode) {
        this.swipeDownToChangeDisplayMode = swipeDownToChangeDisplayMode;
    }

    @Deprecated
    /**
     * Gets a value which determines whether the display mode will be changed while the calendar is
     * in month view and the detected gesture is swipe up. If the gesture is enabled the
     * display mode will be changed to week. Deprecated, use the {@link com.telerik.widget.calendar.CalendarGestureManager} properties instead.
     *
     * @return whether display mode is changed while in month view and the detected gesture is swipe up
     */
    public boolean isUsingSwipeUpToChangeDisplayMode() {
        return swipeUpToChangeDisplayMode;
    }

    @Deprecated
    /**
     * Sets a value which determines whether the display mode will be changed while the calendar is
     * in month view and the detected gesture is swipe up. If the gesture is enabled the
     * display mode will be changed to week. Deprecated, use the {@link com.telerik.widget.calendar.CalendarGestureManager} properties instead.
     *
     * @param swipeUpToChangeDisplayMode whether display mode is changed while in month view and the detected gesture is swipe up
     */
    public void setSwipeUpToChangeDisplayMode(boolean swipeUpToChangeDisplayMode) {
        this.swipeUpToChangeDisplayMode = swipeUpToChangeDisplayMode;
    }

    @Deprecated
    /**
     * Gets a value which determines whether the display mode will be changed while the calendar is
     * in year view and the detected gesture is tap. If the gesture is enabled the
     * display mode will be changed to month and the display date will be changed
     * so that the month that is tapped becomes visible. Deprecated, use the {@link com.telerik.widget.calendar.CalendarGestureManager} properties instead.
     *
     * @return whether display mode is changed while in year view and the detected gesture is tap
     */
    public boolean isUsingTapToChangeDisplayMode() {
        return tapToChangeDisplayMode;
    }

    @Deprecated
    /**
     * Sets a value which determines whether the display mode will be changed while the calendar is
     * in year view and the detected gesture is tap. If the gesture is enabled the
     * display mode will be changed to month and the display date will be changed
     * so that the month that is tapped becomes visible. Deprecated, use the {@link com.telerik.widget.calendar.CalendarGestureManager} properties instead.
     *
     * @param tapToChangeDisplayMode whether display mode is changed while in year view and the detected gesture is tap
     */
    public void setTapToChangeDisplayMode(boolean tapToChangeDisplayMode) {
        this.tapToChangeDisplayMode = tapToChangeDisplayMode;
    }

    @Deprecated
    /**
     * Gets a value which determines whether the display mode will be changed while the calendar is
     * in month or year view and the detected gesture is double tap. If the gesture is enabled the
     * display mode will be changed from year to month and the display date will be changed
     * so that the month that is tapped becomes visible if the calendar is in year view. If it is in
     * month view the display mode will be changed to year. Deprecated, use the {@link com.telerik.widget.calendar.CalendarGestureManager} properties instead.
     *
     * @return whether display mode is changed while in month or year view and the detected gesture is double tap
     */
    public boolean isUsingDoubleTapToChangeDisplayMode() {
        return doubleTapToChangeDisplayMode;
    }

    @Deprecated
    /**
     * Sets a value which determines whether the display mode will be changed while the calendar is
     * in month or year view and the detected gesture is double tap. If the gesture is enabled the
     * display mode will be changed from year to month and the display date will be changed
     * so that the month that is tapped becomes visible if the calendar is in year view. If it is in
     * month view the display mode will be changed to year. Deprecated, use the {@link com.telerik.widget.calendar.CalendarGestureManager} properties instead.
     *
     * @param doubleTapToChangeDisplayMode whether display mode is changed while in month or year view and the detected gesture is double tap
     */
    public void setDoubleTapToChangeDisplayMode(boolean doubleTapToChangeDisplayMode) {
        this.doubleTapToChangeDisplayMode = doubleTapToChangeDisplayMode;
    }

    @Deprecated
    /**
     * Gets a value which determines whether the drag gesture will be used to select a rage of cells. Once enabled this will
     * prevent the scroll behavior and will enable range selection using a drag. Deprecated, use the {@link com.telerik.widget.calendar.CalendarGestureManager} properties instead.
     *
     * @return <code>true</code> if drag for range selection is enabled, <code>false</code> otherwise.
     */
    public boolean isUsingDragToMakeRangeSelection() {
        return usingDragToMakeRangeSelection;
    }

    @Deprecated
    /**
     * Sets a value which determines whether the drag gesture will be used to select a rage of cells. Once enabled this will
     * prevent the scroll behavior and will enable range selection using a drag. Deprecated, use the {@link com.telerik.widget.calendar.CalendarGestureManager} properties instead.
     *
     * @param usingDragToMakeRangeSelection the new drag state.
     */
    public void setUsingDragToMakeRangeSelection(boolean usingDragToMakeRangeSelection) {
        this.usingDragToMakeRangeSelection = usingDragToMakeRangeSelection;
    }
}

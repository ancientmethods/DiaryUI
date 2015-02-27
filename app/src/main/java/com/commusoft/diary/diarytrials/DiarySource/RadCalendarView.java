package com.commusoft.diary.diarytrials.DiarySource;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.commusoft.diary.diarytrials.R;
import com.commusoft.diary.diarytrials.DiarySource.events.EventAdapter;
import com.telerik.android.common.Function;
import com.telerik.android.common.Procedure;
import com.telerik.android.common.Util;
import com.telerik.android.common.licensing.LicensingProvider;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

/**
 * Represents a control that allows you to select and display dates in a calendar.
 */
public class RadCalendarView extends View implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,
        ScaleGestureDetector.OnScaleGestureListener {

    // Legacy
    private static final float UPPER_PART_RATIO = 0.5f;
    private static final float LOWER_PART_RATIO = 0.8f;
    private static final int DEFAULT_ANIMATION_DURATION = 200;
    protected int initialRowHeight = -1;

    private Locale locale;
    private Calendar calendar;
    private Calendar workCalendar;
    private boolean suspendUpdate;
    private boolean suspendArrange;

    private boolean showTitle = true;
    private boolean showDayNames = true;
    private boolean showGridLines = true;
    private boolean showCellDecorations = true;

    private long displayDate;
    private long minDate;
    private long maxDate;

    private CalendarTask taskToBeExecutedAfterArrangeHasPassed;

    private CellDecorationsLayer cellDecorationsLayer;
    private CalendarAdapter calendarAdapter;
    private CalendarAnimationsManager animationsManager;
    private CalendarGestureManager gestureManager;
    private CalendarSelectionManager selectionManager;
    private EventAdapter eventAdapter;
    private CalendarScrollManager scrollManager;
    private CalendarDisplayMode displayMode = CalendarDisplayMode.Month;
    private CalendarSelectionMode selectionMode = CalendarSelectionMode.Multiple;
    private WeekNumbersDisplayMode weekNumbersDisplayMode = WeekNumbersDisplayMode.None;
    private ScrollMode scrollMode = ScrollMode.Sticky;

    private boolean inOriginalSizeForAllModes = false;
    private boolean isYearModeCompact = false;
    public boolean suspendDisplayModeChange;

    private Procedure<CalendarCell> customizationRule;
    private Function<Long, Integer> dateToColor;

    private OnDisplayDateChangedListener onDisplayDateChangedListener;
    private OnDisplayModeChangedListener onDisplayModeChangedListener;

    private Hashtable<Long, List<CalendarDayCell>> dateToCell;

    private int stateToSave;

    private CalendarTextElement title;
    private CalendarRow dayNames;
    private boolean animationEnabled = true;
    private boolean horizontalScroll = false;
    private GridLinesLayer gridLinesLayer;
    private int dayNamesHeight;
    private int titleHeight;
    private boolean arrangePassed;
    private int originalWidth;
    private int originalHeight;
    private boolean calendarShrinked;

    /**
     * Creates an instance of the {@link RadCalendarView} class.
     *
     * @param context the context to be used
     */
    public RadCalendarView(Context context) {
        this(context, null);
    }

    /**
     * Creates an instance of the {@link RadCalendarView} class.
     *
     * @param context the context to be used
     * @param attrs   the attributes
     */
    public RadCalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.calendarStyle);
    }

    /**
     * Creates an instance of the {@link RadCalendarView} class.
     *
     * @param context  the context to be used
     * @param attrs    the attributes
     * @param defStyle the default style
     */
    public RadCalendarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        beginUpdate();
        initializeControl();

        final TypedArray array = context.obtainStyledAttributes(
                attrs,
                R.styleable.RadCalendarView,
                defStyle,
                R.style.CalendarDefaultStyle);

        if (array != null) {
            initFromXML(array);
            array.recycle();
        }

        endUpdate();
    }

    /**
     * Gets the height of the day names. It will be preserved over display mode changes and applied when appropriate.
     *
     * @return the current stored height for the day names.
     */
    public int getDayNamesHeight() {
        return this.dayNamesHeight;
    }

    /**
     * Sets the height of the day names. It will be preserved over display mode changes and applied when appropriate.
     *
     * @param dayNamesHeight the new height for the day names to be stored.
     */
    public void setDayNamesHeight(int dayNamesHeight) {
        this.dayNamesHeight = dayNamesHeight;

        if (!this.calendarShrinked) {
            invalidateArrange();
            invalidate();
        }
    }

    /**
     * Gets the height of the title. It will be preserved over display mode changes and applied when appropriate.
     *
     * @return the current stored height for the title.
     */
    public int getTitleHeight() {
        return this.titleHeight;
    }

    /**
     * Sets the height of the title. It will be preserved over display mode changes and applied when appropriate.
     *
     * @param titleHeight the new height for the title to be stored.
     */
    public void setTitleHeight(int titleHeight) {
        this.titleHeight = titleHeight;

        if (!this.calendarShrinked) {
            invalidateArrange();
            invalidate();
        }
    }

    /**
     * Gets the manager responsible for handling the gestures.
     *
     * @return the current gestures manager.
     */
    public CalendarGestureManager getGestureManager() {
        return gestureManager;
    }

    /**
     * Sets the manager responsible for handling the gestures.
     *
     * @param gestureManager the new gestures manager.
     */
    public void setGestureManager(CalendarGestureManager gestureManager) {
        this.gestureManager = gestureManager;
        this.gestureManager.setAnimationsManager(this.animationsManager);
        this.gestureManager.setDisplayMode(this.displayMode);
        this.gestureManager.setScrollManager(this.scrollManager);
        this.gestureManager.setScrollMode(this.scrollMode);
        this.gestureManager.setSelectionManager(this.selectionManager);
        this.gestureManager.setSelectionMode(this.selectionMode);
    }

    /**
     * Gets the animations manager.
     *
     * @return the current animations manager.
     */
    public CalendarAnimationsManager getAnimationsManager() {
        return this.animationsManager;
    }

    /**
     * Sets the animation manager.
     *
     * @param animationsManager the new animations manager.
     */
    public void setAnimationsManager(CalendarAnimationsManager animationsManager) {
        this.animationsManager = animationsManager;
        this.gestureManager.setAnimationsManager(animationsManager);
        invalidate();
    }

    /**
     * Gets the selection manager.
     *
     * @return the current selection manager.
     */
    public CalendarSelectionManager getSelectionManager() {
        return this.selectionManager;
    }

    /**
     * Sets the selection manager.
     *
     * @param selectionManager the new selection manager.
     */
    public void setSelectionManager(CalendarSelectionManager selectionManager) {
        this.selectionManager = selectionManager;
        this.gestureManager.setSelectionManager(selectionManager);
    }

    /**
     * Gets a value that determines whether the calendar will scroll horizontally or vertically.
     *
     * @return <code>true</code> will result the calendar to scroll horizontally, <code>false</code>
     * will result it to scroll vertically.
     */
    public boolean isHorizontalScroll() {
        return this.horizontalScroll;
    }

    /**
     * Sets a value that determines whether the calendar will scroll horizontally or vertically.
     *
     * @param horizontal the new scroll direction.  <code>true</code> will result the calendar to scroll horizontally, <code>false</code>
     *                   will result it to scroll vertically.
     */
    public void setHorizontalScroll(boolean horizontal) {
        this.horizontalScroll = horizontal;
        this.scrollManager.setHorizontalScroll(horizontal);

        // Legacy to be removed
        if (horizontal) {
            this.gestureAssistant().setSwipeHorizontalToChangeMonths(true);
            this.gestureAssistant().setSwipeHorizontalToChangeWeeks(true);
            this.gestureAssistant().setSwipeHorizontalToChangeYears(true);

            this.gestureAssistant().setSwipeVerticalToChangeMonths(false);
            this.gestureAssistant().setSwipeVerticalToChangeWeeks(false);
            this.gestureAssistant().setSwipeVerticalToChangeYears(false);
        } else {
            this.gestureAssistant().setSwipeHorizontalToChangeMonths(false);
            this.gestureAssistant().setSwipeHorizontalToChangeWeeks(false);
            this.gestureAssistant().setSwipeHorizontalToChangeYears(false);

            this.gestureAssistant().setSwipeVerticalToChangeMonths(true);
            this.gestureAssistant().setSwipeVerticalToChangeWeeks(true);
            this.gestureAssistant().setSwipeVerticalToChangeYears(true);
        }

        invalidateArrange(); // TODO position fragments instead of rearranging them
        updateFragments(true);
        this.scrollManager.updateActiveFragment();
        invalidate();
    }

    /**
     * Gets the scroll mode.
     *
     * @return the current scroll mode.
     */
    public ScrollMode getScrollMode() {
        return this.scrollMode;
    }

    /**
     * Sets the scroll mode.
     *
     * @param scrollMode the new scroll mode.
     */
    public void setScrollMode(ScrollMode scrollMode) {
        this.scrollMode = scrollMode;
        this.gestureManager.setScrollMode(scrollMode);
        this.scrollManager.setScrollMode(scrollMode);
        invalidateArrange(); // TODO position fragments instead of rearranging them
        updateFragments(true);
        scrollManager.updateActiveFragment();
        invalidate();
    }

    /**
     * Gets a values stating whether the animations are enabled or not.
     *
     * @return <code>true</code> if the animations are enabled, <code>false</code> otherwise.
     */
    public boolean isAnimationEnabled() {
        return this.animationEnabled;
    }

    /**
     * Sets a values stating whether the animations are enabled or not.
     *
     * @param enabled the new animations state.
     */
    public void setAnimationEnabled(boolean enabled) {
        this.animationEnabled = enabled;
    }

    /**
     * Holds a collection that has all the cells sorted and accessible vy date.
     *
     * @return the current collection of cells accessible by date.
     */
    public Hashtable<Long, List<CalendarDayCell>> dateToCell() {
        return this.dateToCell;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        LicensingProvider.verify(this.getContext());
    }

    /**
     * Returns the currently used {@link CalendarAdapter}
     * by this instance. This calendarAdapter is used for getting and updating the
     * {@link CalendarCell} instances which populate the calendar.
     *
     * @return the currently used CalendarAdapter
     */
    public CalendarAdapter getAdapter() {
        return calendarAdapter;
    }

    /**
     * Sets a new {@link CalendarAdapter} instance that will be
     * used by this calendar instance to create and update its
     * {@link CalendarCell} instances.
     *
     * @param calendarAdapter the new CalendarAdapter
     */
    public void setAdapter(CalendarAdapter calendarAdapter) {
        this.calendarAdapter = calendarAdapter;
        rebuildCalendar();
    }

    /**
     * Returns the current display date. It is of type <code>long</code> and represents one of the
     * dates that are currently visible by the calendar.
     * The default value is today.
     *
     * @return date that is currently visible
     */
    public long getDisplayDate() {
        return this.displayDate;
    }

    /**
     * Sets a new display date. If the new value is already visible, the change may not be noticed.
     * Otherwise, the visible period will be changed so that the new display date becomes visible.
     * If the new value represents a certain period of the day, for example January 1st 2014, 3:23 AM,
     * the value will be clamped to the value which represents January 1st 2014, 0:00 AM.
     *
     * @param value the new display date
     * @see #setOnDisplayDateChangedListener(com.commusoft.diary.diarytrials.DiarySource.RadCalendarView.OnDisplayDateChangedListener) (RadCalendarView.OnDisplayDateChangedListener)
     */
    public void setDisplayDate(long value) {
        long oldDate = this.displayDate;
        long newDate = CalendarTools.getDateStart(value);

        if (this.maxDate != 0) {
            if (this.maxDate < newDate) {
                throw new IllegalArgumentException("The value of displayDate should be less than the value of maxDate.");
            }
        }
        if (this.minDate != 0) {
            if (this.minDate > newDate) {
                throw new IllegalArgumentException("The value of minDate should be less than the value of displayDate.");
            }
        }
        this.displayDate = newDate;
        this.calendar.setTimeInMillis(this.displayDate);
        this.onDisplayDateChanged();
        if (this.onDisplayDateChangedListener != null) {
            this.onDisplayDateChangedListener.onDisplayDateChanged(oldDate, newDate);
        }

        this.scrollManager.updateBorders();
        invalidate();
    }

    /**
     * Gets the calendar title element.
     *
     * @return the current calendar title element.
     */
    public CalendarTextElement title() {
        return this.title;
    }

    /**
     * Gets the day names row.
     *
     * @return the current day names row.
     */
    public CalendarRow dayNames() {
        return this.dayNames;
    }

    /**
     * Shifts the current date either forward or backward, having in mind the current display mode.
     *
     * @param increase <code>true</code> will result in the current date to increase, <code>false</code>
     *                 will result in the date to decrease.
     */
    public void shiftDate(boolean increase) {
        long newDate = CalendarTools.calculateNewValue(increase, this.displayDate, this.displayMode);

        if (minDate != 0) {
            if (newDate < minDate) {
                newDate = getAlternativeValueForMinDate();
            }
        }

        if (maxDate != 0) {
            if (newDate > maxDate) {
                newDate = getAlternativeValueForMaxDate();
            }
        }

        if ((maxDate != 0 && newDate > maxDate) || (minDate != 0 && newDate < minDate))
            return;

        this.setDisplayDate(newDate);
    }

    /**
     * States whether the calendar can shift back to the previous date in accordance to the current display mode and minimum date value.
     *
     * @return <code>true</code> if the shift can be made, <code>false</code> otherwise.
     */
    public boolean canShiftToPreviousDate() {
        return this.minDate == 0 ||
                getAlternativeValueForMinDate() >= minDate;
    }

    long getAlternativeValueForMinDate() {
        switch (displayMode) {
            case Month:
                return CalendarTools.getLastDateInMonth(CalendarTools.calculateNewValue(false, displayDate, displayMode));
            case Year:
                return CalendarTools.getLastDateInYear(CalendarTools.calculateNewValue(false, displayDate, displayMode));
            case Week:
                return CalendarTools.getLastDateInWeek(CalendarTools.calculateNewValue(false, displayDate, displayMode));
        }

        return minDate - 1;
    }

    /**
     * States whether the calendar can shift forward to the next date in accordance to the current display mode and maximum date value.
     *
     * @return <code>true</code> if the shift can be made, <code>false</code> otherwise.
     */
    public boolean canShiftToNextDate() {
        return this.maxDate == 0 ||
                getAlternativeValueForMaxDate() <= maxDate;
    }

    long getAlternativeValueForMaxDate() {
        switch (displayMode) {
            case Month:
                return CalendarTools.getFirstDateInMonth(CalendarTools.calculateNewValue(true, displayDate, displayMode));
            case Year:
                return CalendarTools.getFirstDateInYear(CalendarTools.calculateNewValue(true, displayDate, displayMode));
            case Week:
                return CalendarTools.getFirstDateInWeek(CalendarTools.calculateNewValue(true, displayDate, displayMode));
        }

        return maxDate + 1;
    }

    /**
     * Changes the display date to a date that is not currently visible. The new date is
     * determined depending on the current {@link CalendarDisplayMode}.
     * If it is month, the display date is increased by one month, etc.
     * The value will not be changed if that would exceed the limits defined by max date.
     *
     * @see #getDisplayMode()
     * @see #animateToPrevious()
     * @see #getMaxDate()
     */
    public void animateToNext() {
        if (canShiftToNextDate()) {
            this.animationsManager.animateToNextDate();
        }
    }

    /**
     * Changes the display date to a date that is not currently visible. The new date is
     * determined depending on the current {@link CalendarDisplayMode}.
     * If it is month, the display date is decreased by one month, etc.
     * The value will not be changed if that would exceed the limits defined by min date.
     *
     * @see #getDisplayMode()
     * @see #animateToNext()
     */
    public void animateToPrevious() {
        if (canShiftToPreviousDate()) {
            this.animationsManager.animateToPreviousDate();
        }
    }

    /**
     * Notifies the instance that it needs to updateActiveFragment,
     * for example when the events are added or updated.
     */
    public void notifyDataChanged() {
        updateFragments(true);
        this.scrollManager.updateActiveFragment();

        invalidate();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("stateToSave", this.stateToSave);

        bundle.putSerializable("displayMode", this.displayMode);

        //bundle.putSerializable("style", this.calendarAdapter.getStyle());

        bundle.putLong("displayDate", this.displayDate);

        List<Long> selectedDates = this.gestureManager.getSelectionManager().getSelectedDates();
        if (selectedDates != null && selectedDates.size() > 0) {
            long list[] = new long[selectedDates.size()];
            for (int i = 0; i < selectedDates.size(); i++) {
                list[i] = selectedDates.get(i);
            }
            bundle.putLongArray("selectedDates", list);
        }

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            this.stateToSave = bundle.getInt("stateToSave");

            long[] selectedDatesArray = bundle.getLongArray("selectedDates");
            if (selectedDatesArray != null) {
                List<Long> selectedDates = new ArrayList<Long>();
                for (long value : selectedDatesArray) {
                    selectedDates.add(value);
                }

                this.gestureManager.getSelectionManager().setSelectedDates(selectedDates);
                this.gestureManager.getSelectionManager().syncSelectedCellsWithDates();
            }

            this.taskToBeExecutedAfterArrangeHasPassed = (new CalendarTask() {
                @Override
                public void execute() {
                    changeDisplayMode(displayMode(), false);
                }

                @Override
                public CalendarDisplayMode displayMode() {
                    return (CalendarDisplayMode) bundle.getSerializable("displayMode");
                }
            });

            setDisplayDate(bundle.getLong("displayDate"));

            state = bundle.getParcelable("instanceState");

            //this.calendarAdapter.setStyle((CalendarStyle) bundle.getSerializable("style"));
        }

        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (changed) {
            if (!arrangePassed)
                arrangePassed = true;
            invalidateArrange();
            if (taskToBeExecutedAfterArrangeHasPassed != null)
                executeWaitingTask();
        }
    }

    private void executeWaitingTask() {
        if (this.taskToBeExecutedAfterArrangeHasPassed != null) {
            this.taskToBeExecutedAfterArrangeHasPassed.execute();
            this.taskToBeExecutedAfterArrangeHasPassed = null;
        }
    }

    /**
     * Used to invalidate the arrange of all calendar elements.
     */
    protected void invalidateArrange() {
        if (!arrangePassed || this.suspendArrange)
            return;

        int left = this.getPaddingLeft();
        int top = this.getPaddingTop();
        int right = this.getMeasuredWidth() - (this.getPaddingLeft() + this.getPaddingRight());
        int bottom = this.getMeasuredHeight() - (this.getPaddingTop() + this.getPaddingBottom());

        if (this.title.getVisibility() != ElementVisibility.Gone) {
            this.title.arrange(left, top, right, (top + this.titleHeight));
            top += this.title.getHeight();
        } else {
            this.title.arrange(0, 0, 0, 0);
        }

        if (this.dayNames.getVisibility() != ElementVisibility.Gone) {
            this.dayNames.arrange(left, top, right, (top + this.dayNamesHeight));
            top += this.dayNames.getHeight();
        } else {
            this.dayNames.arrange(0, 0, 0, 0);
        }

        this.scrollManager.arrange(left, top, right, this.displayMode == CalendarDisplayMode.Week ? (top + this.initialRowHeight) : bottom);

        if (this.initialRowHeight == -1) {
            this.initialRowHeight = (int) this.scrollManager.currentFragment().rowHeight();
            if (this.scrollManager.getMaxScrollOffset() == 0)
                this.scrollManager.setMaxScrollOffset(this.initialRowHeight);
        }

        this.suspendArrange = false;
    }

    /**
     * Gets a value indicating whether grid lines between the cells will be drawn.
     *
     * @return whether grid lines are shown
     */
    public boolean getShowGridLines() {
        return this.showGridLines;
    }

    /**
     * Sets a value indicating whether grid lines between the cells will be drawn.
     *
     * @param showGridLines whether grid lines are shown
     */
    public void setShowGridLines(boolean showGridLines) {
        if (this.showGridLines != showGridLines) {
            this.showGridLines = showGridLines;
            invalidate();
        }
    }

    /**
     * Gets a value indicating whether there will be additional decorations for selected cells.
     * The default decoration layer draws a border over the selected cells.
     *
     * @return whether additional decoration are drawn for the selected cells
     */
    public boolean getShowCellDecorations() {
        return this.showCellDecorations;
    }

    /**
     * Sets a value indicating whether there will be additional decorations for selected cells.
     * The default decoration layer draws a border over the selected cells.
     *
     * @param showCellDecorations whether additional decoration are drawn for the selected cells
     */
    public void setShowCellDecorations(boolean showCellDecorations) {
        if (this.showCellDecorations != showCellDecorations) {
            this.showCellDecorations = showCellDecorations;
            invalidate();
        }
    }

    /**
     * Gets a layer which is responsible for additional decoration of the selected cells.
     * The default decoration layer draws a border over the selected cells.
     *
     * @return the cell decorations layer
     */
    public CellDecorationsLayer getCellDecorationsLayer() {
        return this.cellDecorationsLayer;
    }

    /**
     * Sets a new layer which will be responsible for additional decoration of the selected cells.
     * The default decoration layer draws a border over the selected cells.
     *
     * @param cellDecorationsLayer the layer that draws decoration for selected cells
     */
    public void setCellDecorationsLayer(CellDecorationsLayer cellDecorationsLayer) {
        if (cellDecorationsLayer == null)
            throw new NullPointerException("cellDecorationsLayer");

        if (this.cellDecorationsLayer != cellDecorationsLayer) {
            this.cellDecorationsLayer = cellDecorationsLayer;
            invalidate();
        }
    }

    /**
     * Returns the currently used {@link WeekNumbersDisplayMode}
     * by this instance. This value defines how the week number information will be
     * visualized. The default value is <code>None</code> which means that week number
     * information will not be visible.
     *
     * @return the current week number display mode
     */
    public WeekNumbersDisplayMode getWeekNumbersDisplayMode() {
        return this.weekNumbersDisplayMode;
    }

    /**
     * Sets a new {@link WeekNumbersDisplayMode} that will be
     * used by this calendar instance to determine how the week number information will be
     * presented. The default value is <code>None</code>.
     *
     * @param value the new week number display mode
     */
    public void setWeekNumbersDisplayMode(WeekNumbersDisplayMode value) {
        if (this.weekNumbersDisplayMode != value) {
            this.weekNumbersDisplayMode = value;
            rebuildCalendar();
        }
    }

    /**
     * Used to rebuild the calendar when needed.
     */
    protected void rebuildCalendar() {
        resetCalendar();
        this.calendarAdapter.updateTitle(this.title, this.displayDate, this.displayMode);
        updateFragments();
        this.scrollManager.updateActiveFragment();
        this.invalidateArrange();
        invalidate();
    }

    /**
     * Returns the currently used {@link com.commusoft.diary.diarytrials.DiarySource.events.EventAdapter}.
     * This calendarAdapter is responsible for the events that will be visualized by this instance.
     * The calendarAdapter defines the full list of events and determines
     * which of them should be visible for each date.
     *
     * @return the current event calendarAdapter
     */
    public EventAdapter getEventAdapter() {
        return eventAdapter;
    }

    /**
     * Sets a new {@link com.commusoft.diary.diarytrials.DiarySource.events.EventAdapter} that will be
     * used by this calendar instance. This calendarAdapter will define the full list of events
     * that will be visualized and will determine which of them should be visible
     * for each date.
     *
     * @param eventAdapter a new event calendarAdapter
     */
    public void setEventAdapter(EventAdapter eventAdapter) {
        this.eventAdapter = eventAdapter;
        updateFragments();
        invalidate();
    }

    /**
     * Returns the current {@link CalendarSelectionMode}.
     * This mode determines the type of selection used by this instance. For example,
     * in all modes when you choose a cell it will be selected, however when you
     * choose another cell the selection will be determined by the selection mode.
     * If it is <code>Single</code>, the old selection will be cleared.
     * If it si <code>Multiple</code>, the new selection will consist of the both dates - the old and the new.
     * And if it is <code>Range</code>, the new selection will consist of the whole range of dates between the old and the new date.
     * The default value is <code>Multiple</code>.
     *
     * @return the current selection mode
     */
    public CalendarSelectionMode getSelectionMode() {
        return selectionMode;
    }

    /**
     * Sets a new {@link CalendarSelectionMode}.
     * This mode determines the type of selection used by this instance. For example,
     * in all modes when you choose a cell it will be selected, however when you
     * choose another cell the selection will be determined by the selection mode.
     * If it is <code>Single</code>, the old selection will be cleared.
     * If it si <code>Multiple</code>, the new selection will consist of the both dates - the old and the new.
     * And if it is <code>Range</code>, the new selection will consist of the whole range of dates between the old and the new date.
     * The default value is <code>Multiple</code>.
     *
     * @param selectionMode a new calendar selection mode
     */
    public void setSelectionMode(CalendarSelectionMode selectionMode) {
        if (this.selectionMode != selectionMode) {
            this.selectionMode = selectionMode;
            this.selectionManager.setSelectionMode(selectionMode);
            this.gestureManager.setSelectionMode(selectionMode);
        }
    }

    /**
     * Gets the renderer responsible for rendering the grid lines.
     *
     * @return the current grid lines renderer.
     */
    public GridLinesLayer getGridLinesLayer() {
        return gridLinesLayer;
    }

    /**
     * Sets the renderer responsible for rendering the grid lines.
     *
     * @param gridLinesLayer the new grid lines renderer.
     */
    public void setGridLinesLayer(GridLinesLayer gridLinesLayer) {
        if (gridLinesLayer == null)
            throw new NullPointerException("gridLinesLayer");

        if (this.gridLinesLayer != gridLinesLayer) {
            this.gridLinesLayer = gridLinesLayer;
            this.invalidate();
        }
    }

    /**
     * Gets the scroll manager.
     *
     * @return the current scroll manager.
     */
    public CalendarScrollManager getScrollManager() {
        return scrollManager;
    }

    /**
     * Sets the scroll manager.
     *
     * @param scrollManager the new scroll manager.
     */
    public void setScrollManager(CalendarScrollManager scrollManager) {
        this.scrollManager = scrollManager;
        this.gestureManager.setScrollManager(scrollManager);
        this.animationsManager.setScrollManager(scrollManager);
        invalidate();
    }

    /**
     * Gets the listener that is being called when a date has been selected.
     *
     * @return the current date selected listener.
     */
    public OnSelectedDatesChangedListener getOnSelectedDatesChangedListener() {
        return this.selectionManager.getOnSelectedDatesChangedListener();
    }

    /**
     * Sets the listener that is being called when a date has been selected.
     *
     * @param listener the new date selected listener.
     */
    public void setOnSelectedDatesChangedListener(OnSelectedDatesChangedListener listener) {
        this.selectionManager.setOnSelectedDatesChangedListener(listener);
    }

    /**
     * Gets the selected dates.
     *
     * @return the currently selected dates.
     */
    public List<Long> getSelectedDates() {
        return this.selectionManager.getSelectedDates();
    }

    /**
     * Sets the selected dates.
     *
     * @param selectedDates the new selected dates.
     */
    public void setSelectedDates(List<Long> selectedDates) {
        this.selectionManager.setSelectedDates(selectedDates);
        invalidate();
    }

    /**
     * Gets the gesture assistant.
     *
     * @return the current gestures assistant.
     * @deprecated use the calendar properties instead.
     */
    public CalendarGestureAssistant gestureAssistant() {
        return this.gestureManager.getGestureAssistant();
    }

    /**
     * Gets the selected range.
     *
     * @return the current selected range.
     */
    public DateRange getSelectedRange() {
        return this.selectionManager.getSelectedRange();
    }

    /**
     * Sets the selected range.
     *
     * @param selectionRange the new selected range.
     */
    public void setSelectedRange(DateRange selectionRange) {
        this.selectionManager.setSelectedRange(selectionRange);
        invalidate();
    }

    /**
     * Sets a listener to be called when the display date has been changed.
     *
     * @param listener the new listener.
     */
    public void setOnDisplayDateChangedListener(OnDisplayDateChangedListener listener) {
        this.onDisplayDateChangedListener = listener;
    }

    /**
     * Sets a listener to be called when the display mode has been changed.
     *
     * @param listener the new listener.
     */
    public void setOnDisplayModeChangedListener(OnDisplayModeChangedListener listener) {
        this.onDisplayModeChangedListener = listener;
    }

    /**
     * Sets a listener to be called when a cell has been clicked.
     *
     * @param listener the new listener.
     */
    public void setOnCellClickListener(OnCellClickListener listener) {
        this.gestureManager.setOnCellClickListener(listener);
    }

    /**
     * Returns a boolean which determines whether the current instance
     * will render title. The title is the first row and displays the year if the display mode is Year
     * and the month, otherwise.
     * The default value is <code>true</code>.
     *
     * @return whether the default title will be shown
     */
    public boolean getShowTitle() {
        return this.showTitle;
    }

    /**
     * Sets a boolean which determines whether the current instance
     * will render title. The title is the first row and displays the year if the display mode is Year
     * and the month, otherwise.
     * The default value is <code>true</code>.
     *
     * @param value whether the title should be drawn
     */
    public void setShowTitle(boolean value) {
        if (this.showTitle != value) {
            this.showTitle = value;
            this.handleShowTitleChange();
        }
    }

    /**
     * Returns a boolean which determines whether the current instance
     * will render day names. The day names are drawn below the title
     * only when the display mode is month or week.
     * The default value is <code>true</code>.
     *
     * @return whether the day names will be shown
     */
    public boolean getShowDayNames() {
        return this.showDayNames;
    }

    /**
     * Sets a boolean which determines whether the current instance
     * will render day names. The day names are drawn below the title
     * only when the display mode is month or week.
     * The default value is <code>true</code>.
     *
     * @param value whether the title should be drawn
     */
    public void setShowDayNames(boolean value) {
        if (this.showDayNames != value) {
            this.showDayNames = value;
            if (this.displayMode != CalendarDisplayMode.Year) {
                this.handleShowDayNamesChange();
            }
        }
    }

    /**
     * Returns a {@link com.telerik.android.common.Function} which determines the color that is
     * used for each date in this instance. If the value returned by the function is
     * <code>null</code> for some date, the default value for color will be used.
     * This can be used for example to render dates which are holidays in <code>Red</code>.
     *
     * @return the current function which determines different color for different dates
     */
    public Function<Long, Integer> getDateToColor() {
        return dateToColor;
    }

    /**
     * Sets a {@link com.telerik.android.common.Function} which determines the color that is
     * used for each date in this instance. If the value returned by the function is
     * <code>null</code> for some date, the default value for color will be used.
     * This can be used for example to render dates which are holidays in <code>Red</code>.
     *
     * @param dateToColor a function which will determine what color to be used for different dates
     */
    public void setDateToColor(Function<Long, Integer> dateToColor) {
        this.dateToColor = dateToColor;

        rebuildCalendar();
    }

    /**
     * Returns a {@link com.telerik.android.common.Procedure} which makes modifications
     * to a {@link CalendarCell} after it is updated and/or
     * created.
     * For example, this modifications may include setting the foreground that is used by the cell
     * to <code>Red</code> for cell which represent holidays.
     *
     * @return the current procedure which defines modification applied for a calendar cell
     */
    public Procedure<CalendarCell> getCustomizationRule() {
        return customizationRule;
    }

    /**
     * Sets a {@link com.telerik.android.common.Procedure} which makes modifications
     * to a {@link CalendarCell} after it is updated and/or
     * created.
     * For example, this modifications may include setting the foreground that is used by the cell
     * to <code>Red</code> for cell which represent holidays.
     *
     * @param customizationRule a procedure which defines modification applied for a calendar cell
     */
    public void setCustomizationRule(Procedure<CalendarCell> customizationRule) {
        this.customizationRule = customizationRule;

        rebuildCalendar();
    }

    /**
     * Returns the current min date. It is of type <code>long</code> and represents the min date
     * that can be represented and/or selected by the calendar.
     * The default value is <code>0</code>, which means that there is no min date.
     *
     * @return the current min date
     */
    public long getMinDate() {
        return this.minDate;
    }

    /**
     * Sets a new min date. If the new value is after the current display date, the display date will be changed
     * as well. If the new min date value represents a certain period of the day, for example January 1st 2014, 3:23 AM,
     * the value will be clamped to the value which represents January 1st 2014, 0:00 AM.
     * If the new value is after the current max date, an exception will be thrown.
     *
     * @param minDate the new min date
     * @throws java.lang.IllegalArgumentException If the new min date is after the current max date
     * @see #getMaxDate()
     */
    public void setMinDate(long minDate) {
        long newMinDate = CalendarTools.getDateStart(minDate);
        if (newMinDate != this.minDate) {

            if (this.maxDate != 0) {
                if (this.maxDate < newMinDate) {
                    throw new IllegalArgumentException("The value of minDate should be less than the value of maxDate.");
                }
            }

            this.minDate = newMinDate;

            if (this.displayDate < this.minDate) {
                this.setDisplayDate(this.minDate);
            }

            this.updateFragments(true);
            this.calendarAdapter.updateTitle(this.title, this.displayDate, this.displayMode);
            this.scrollManager.updateActiveFragment();
            this.scrollManager.updateBorders();
            invalidate();
        }
    }

    /**
     * Returns the current max date. It is of type <code>long</code> and represents the max date
     * that can be represented and/or selected by the calendar.
     * The default value is <code>0</code>, which means that there is no max date.
     *
     * @return the current max date
     */
    public long getMaxDate() {
        return this.maxDate;
    }

    /**
     * Sets a new max date. If the new value is before the current display date, the display date will be changed
     * as well. If the new max date value represents a certain period of the day, for example January 1st 2014, 3:23 AM,
     * the value will be clamped to the value which represents January 1st 2014, 0:00 AM.
     * If the new value is before the current min date, an exception will be thrown.
     *
     * @param maxDate the new max date
     * @throws java.lang.IllegalArgumentException If the new max date is before the current min date
     * @see #getMinDate()
     */
    public void setMaxDate(long maxDate) {
        long newMaxDate = CalendarTools.getDateStart(maxDate);
        if (newMaxDate != this.maxDate) {

            if (this.minDate != 0) {
                if (this.minDate > newMaxDate) {
                    throw new IllegalArgumentException("The value of minDate should be less than the value of maxDate.");
                }
            }

            this.maxDate = newMaxDate;

            if (this.displayDate > this.maxDate) {
                this.setDisplayDate(this.maxDate);
            }

            this.updateFragments(true);
            this.calendarAdapter.updateTitle(this.title, this.displayDate, this.displayMode);
            this.scrollManager.updateActiveFragment();
            this.scrollManager.updateBorders();
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!this.suspendUpdate) {
            this.gestureManager.handleTouch(event);
        }

        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent event) {
        return this.gestureManager.handleOnDown();
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        return this.gestureManager.handleOnSingleTapUp();
    }

    @Override
    public boolean onScroll(MotionEvent event, MotionEvent event2, float v, float v2) {
        return this.gestureManager.handleScroll(event2.getX(), event2.getY(), v, v2);
    }

    @Override
    public void onLongPress(MotionEvent event) {
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocity1, float velocity2) {

        if (this.gestureManager.handleFling(velocity1, velocity2))
            return true;

        if (!this.horizontalScroll || this.displayMode == CalendarDisplayMode.Week) {
            if (this.displayMode == CalendarDisplayMode.Month && event1.getY() / this.getHeight() > LOWER_PART_RATIO && event2.getY() / this.getHeight() < UPPER_PART_RATIO) {
                boolean dragHandled = this.onDragTop();
                if (dragHandled) {
                    return true;
                }
            } else if (this.displayMode == CalendarDisplayMode.Week && event1.getY() > this.scrollManager.getTop() && event1.getY() < this.scrollManager.getBottom() && event2.getY() > this.scrollManager.getBottom()) {
                boolean dragHandled = this.onDragBottom();
                if (dragHandled) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @deprecated
     */
    private boolean onDragTop() {
        if (this.displayMode == CalendarDisplayMode.Month) {

            if (!this.gestureManager.getGestureAssistant().isUsingSwipeUpToChangeDisplayMode()) {
                return false;
            }

            this.changeDisplayMode(CalendarDisplayMode.Week);
            return true;
        }
        return false;
    }

    /**
     * @deprecated
     */
    private boolean onDragBottom() {
        if (this.displayMode == CalendarDisplayMode.Week) {

            if (!this.gestureManager.getGestureAssistant().isUsingSwipeDownToChangeDisplayMode()) {
                return false;
            }

            this.changeDisplayMode(CalendarDisplayMode.Month);
            return true;
        }
        return false;
    }

    /**
     * Begins update thus preventing the calendar to invalidate.
     */
    public void beginUpdate() {
        beginUpdate(false);
    }

    /**
     * Begins update preventing the calendar to both invalidate and rearrange its elements.
     *
     * @param suspendArrange <code>true</code> will result in suspending the arrange as well, <code>false</code>
     *                       will not prevent the arrange.
     */
    public void beginUpdate(boolean suspendArrange) {
        this.suspendUpdate = true;
        this.suspendArrange = suspendArrange;
    }

    /**
     * Ends the update by calling the calendar to invalidate one single time.
     */
    public void endUpdate() {
        endUpdate(false);
    }

    /**
     * Ends the update by first calling the {@link #invalidateArrange()} followed by an invalidation.
     *
     * @param releaseArrange <code>true</code> will cause the arrange to be performed before the invalidation,
     *                       <code>false</code> will not cause arrange.
     */
    public void endUpdate(boolean releaseArrange) {
        this.suspendUpdate = false;

        if (releaseArrange) {
            this.suspendArrange = false;
            invalidateArrange();
        }

        invalidate();
    }

    @Override
    public void invalidate() {
        if (this.suspendUpdate)
            return;

        super.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.suspendUpdate)
            return;

        if (this.title != null && this.title.getVisibility() == ElementVisibility.Visible) {
            drawTitle(canvas);
        }

        if (this.animationsManager.animationInProcess()) {
            this.animationsManager.animate(canvas);
            return;
        }

        if (this.dayNames != null && this.dayNames.getVisibility() == ElementVisibility.Visible) {
            drawDayNames(canvas);
        }

        canvas.clipRect(this.scrollManager.getLeft(), this.scrollManager.getTop(), this.scrollManager.getRight(), this.scrollManager.getBottom() +
                (this.showGridLines ? this.gridLinesLayer.getWidth() / 2 : 0));
        if (this.showCellDecorations)
            this.scrollManager.updateDecorations();

        drawFragments(canvas);

        if (this.showCellDecorations)
            this.cellDecorationsLayer.render(canvas);

        this.animationsManager.onInvalidate();
        this.cellDecorationsLayer.clearDecorations();
    }

    /**
     * Used to draw the fragments.
     *
     * @param canvas the current canvas.
     */
    private void drawFragments(Canvas canvas) {
        this.scrollManager.render(canvas);

        if (this.showGridLines)
            this.gridLinesLayer.drawLine(this.scrollManager.getLeft(), this.scrollManager.getTop(), this.scrollManager.getRight(), this.scrollManager.getTop(), canvas, this.scrollManager.getAlpha());

        this.scrollManager.postRender(canvas);
    }

    /**
     * Used to draw the day names.
     *
     * @param canvas the current canvas.
     */
    protected void drawDayNames(Canvas canvas) {
        this.dayNames.render(canvas);
        if (this.showGridLines) {
            this.gridLinesLayer.drawLine(this.dayNames.getLeft(), this.dayNames.getBottom(), this.dayNames.getRight(), this.dayNames.getBottom(), canvas, this.dayNames.getAlpha());
            this.gridLinesLayer.drawLine(this.dayNames.getLeft(), this.dayNames.getTop(), this.dayNames.getRight(), this.dayNames.getTop(), canvas, this.dayNames.getAlpha());
            this.gridLinesLayer.drawLine(this.dayNames.getLeft(), this.dayNames.getTop(), this.dayNames.getLeft(), this.dayNames.getBottom(), canvas, this.dayNames.getAlpha());
            this.gridLinesLayer.drawLine(this.dayNames.getRight(), this.dayNames.getTop(), this.dayNames.getRight(), this.dayNames.getBottom(), canvas, this.dayNames.getAlpha());

            /*for (int i = 0, len = this.dayNames.cellsCount() - 1; i < len; i++) {
                CalendarCell cell = this.dayNames.getCell(i);
                this.gridLinesLayer.drawLine(cell.getRight(), cell.getTop(), cell.getRight(), cell.getBottom(), canvas);
            }*/
        }
    }

    /**
     * Used to draw the title.
     *
     * @param canvas the current canvas.
     */
    private void drawTitle(Canvas canvas) {
        this.title.render(canvas);
        if (this.showGridLines) {
            this.gridLinesLayer.drawLine(this.title.getLeft(), this.title.getTop(), this.title.getRight(), this.title.getTop(), canvas, this.title.getAlpha());
            this.gridLinesLayer.drawLine(this.title.getLeft(), this.title.getBottom(), this.title.getRight(), this.title.getBottom(), canvas, this.title.getAlpha());
            this.gridLinesLayer.drawLine(this.title.getLeft(), this.title.getTop(), this.title.getLeft(), this.title.getBottom(), canvas, this.title.getAlpha());
            this.gridLinesLayer.drawLine(this.title.getRight(), this.title.getTop(), this.title.getRight(), this.title.getBottom(), canvas, this.title.getAlpha());
        }
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        return this.gestureManager.handleSingleTapConfirmed(event);
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        return this.gestureManager.handleDoubleTap(event);
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        return this.gestureManager.handleOnScale(scaleGestureDetector);
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        return this.gestureManager.handleOnScaleBegin();
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
        this.gestureManager.handleOnScaleEnd();
    }

    public void changeDisplayMode(CalendarDisplayMode displayMode) {
        this.changeDisplayMode(displayMode, this.animationEnabled);
    }

    /**
     * Changes the current display mode with or without animation depending on the value
     * provided as parameter. Disregarding the value of the animate parameter, animations are not
     * used of the value is changed between <code>Week</code> and <code>Year</code>.
     *
     * @param displayMode the new display mode
     * @param animate     whether
     * @see #setDisplayMode(CalendarDisplayMode)
     */
    public void changeDisplayMode(final CalendarDisplayMode displayMode, final boolean animate) {
        if (this.suspendDisplayModeChange)
            return;

        if (!arrangePassed) {
            if (this.taskToBeExecutedAfterArrangeHasPassed.displayMode() != displayMode)
                this.taskToBeExecutedAfterArrangeHasPassed = new CalendarTask() {
                    @Override
                    public void execute() {
                        changeDisplayMode(displayMode(), animate);
                    }

                    @Override
                    public CalendarDisplayMode displayMode() {
                        return displayMode;
                    }
                };

            return;
        }

        CalendarDisplayMode oldMode = this.displayMode;
        if (displayMode == oldMode) {
            return;
        }
        switch (oldMode) {
            case Week:
                if (!inOriginalSizeForAllModes)
                    expandCalendar(displayMode, animate);
                else
                    onDisplayModeChanged(displayMode);
                break;
            case Month:
                if (displayMode == CalendarDisplayMode.Week) {
                    if (!this.inOriginalSizeForAllModes) {
                        shrinkCalendar(displayMode, animate);
                    } else {
                        onDisplayModeChanged(displayMode);
                    }
                } else if (displayMode == CalendarDisplayMode.Year && animate) {
                    this.animateMonthToYear();
                } else {
                    this.onDisplayModeChanged(displayMode);
                }
                break;
            case Year:
                if (displayMode == CalendarDisplayMode.Month && animate) {
                    this.animateYearToMonth();
                } else {
                    if (!this.inOriginalSizeForAllModes)
                        shrinkCalendar(displayMode, animate);
                    else
                        onDisplayModeChanged(displayMode);
                }
                break;
        }
    }

    /**
     * Returns the current {@link CalendarDisplayMode}.
     * The enumeration represents the period of dates that is visible at once on the calendar.
     * The default value is <code>Month</code>, which means that the calendar shows one month.
     *
     * @return the current display mode
     */
    public CalendarDisplayMode getDisplayMode() {
        return this.displayMode;
    }

    /**
     * Sets a new {@link CalendarDisplayMode}.
     * The enumeration represents the period of dates that is visible at once on the calendar.
     * The default value is <code>Month</code>, which means that the calendar shows one month.
     * You can use
     * {@link #setOnDisplayModeChangedListener(RadCalendarView.OnDisplayModeChangedListener)}
     * and provide a listener which follows changes in the display mode.
     *
     * @param value the new display mode
     * @see #setOnDisplayModeChangedListener(RadCalendarView.OnDisplayModeChangedListener)
     */
    public void setDisplayMode(final CalendarDisplayMode value) {
        changeDisplayMode(value);
    }

    private void onDisplayModeChanged(CalendarDisplayMode displayMode) {
        if (this.displayMode != displayMode) {
            this.gestureManager.setDisplayMode(displayMode);
            CalendarDisplayMode oldValue = this.displayMode;

            this.displayMode = displayMode;

            this.resetCalendar();
            this.updateFragments();
            this.scrollManager.setActiveDate(this.displayDate);
            this.scrollManager.updateBorders();

            if (displayMode != CalendarDisplayMode.Year)
                this.selectionManager.syncSelectedCellsWithDates();

            if (this.onDisplayModeChangedListener != null) {
                this.onDisplayModeChangedListener.onDisplayModeChanged(oldValue, displayMode);
            }

            if (this.displayMode == CalendarDisplayMode.Year || !this.showDayNames) {
                this.dayNames.setVisibility(ElementVisibility.Gone);
            } else {
                this.dayNames.setVisibility(ElementVisibility.Visible);
            }


            this.calendarAdapter.updateTitle(this.title, this.displayDate, this.displayMode);

            invalidateArrange();
            invalidate();
        }
    }

    /**
     * Returns a value which represents whether the calendar will keep its original size in all display modes.
     * The default value is <code>false</code>. This means that when the display mode is changed to <code>Week</code>,
     * the calendar will consume less space (that is the space consumed by one week only). If the
     * value is changed to <code>true</code>, the calendar will maintain its original size.
     *
     * @return whether the calendar will maintain its original size in week mode
     */
    public boolean isInOriginalSizeForAllModes() {
        return inOriginalSizeForAllModes;
    }

    /**
     * Sets a value which determines whether the calendar will keep its original size in all display modes.
     * The default value is <code>false</code>. This means that when the display mode is changed to <code>Week</code>,
     * the calendar will consume less space (that is the space consumed by one week only). If the
     * value is changed to <code>true</code>, the calendar will maintain its original size.
     *
     * @param keepOriginalSizeInAllModes whether the calendar will maintain its original size in week mode
     */
    public void setInOriginalSizeForAllModes(boolean keepOriginalSizeInAllModes) {
        if (this.inOriginalSizeForAllModes == keepOriginalSizeInAllModes)
            return;

        this.inOriginalSizeForAllModes = keepOriginalSizeInAllModes;

        if (this.displayMode == CalendarDisplayMode.Week) {
            if (keepOriginalSizeInAllModes)
                expandCalendar(displayMode, false);
            else
                shrinkCalendar(displayMode, false);
        }
    }

    /**
     * Returns a value which represents whether the calendar draws all days when the display mode is
     * <code>Year</code>. The default value is <code>false</code>.
     * This means that when the display mode is changed to <code>Year</code>, the calendar will render all
     * dates from the year. If the value is changed to <code>true</code>,
     * the year view will show only the names of the months.
     *
     * @return whether the months in year view will be drawn entirely or only by their names
     */
    public boolean isYearModeCompact() {
        return this.isYearModeCompact;
    }

    /**
     * Sets a value which determines whether the calendar will render all days when the display mode is
     * <code>Year</code>. The default value is <code>false</code>.
     * This means that when the display mode is changed to <code>Year</code>, the calendar will render all
     * dates from the year. If the value is changed to <code>true</code>,
     * the year view will show only the names of the months.
     *
     * @param yearModeCompact whether the months in year view will be drawn entirely or only by their names
     */
    public void setYearModeCompact(boolean yearModeCompact) {
        if (this.isYearModeCompact != yearModeCompact) {
            this.isYearModeCompact = yearModeCompact;
            invalidateArrange();
        }
    }

    /**
     * Returns the current {@link java.util.Locale} used by the calendar.
     * The locale is used for determining the names of the days and the months visualized
     * by this instance. The default locale is the one provided by the current device.
     *
     * @return the current locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Sets a new {@link java.util.Locale} to be used by the calendar.
     * The locale is used for determining the names of the days and the months visualized
     * by this instance. The default locale is the one provided by the current device.
     *
     * @param locale the new locale
     */
    public void setLocale(Locale locale) {
        if (this.locale != locale) {
            this.locale = locale;
            this.calendarAdapter.setLocale(this.locale);
            CalendarTools.setLocale(locale);

            this.calendar = Calendar.getInstance(this.locale);
            this.workCalendar = Calendar.getInstance(this.locale);
            this.calendar.setTimeInMillis(this.displayDate);
            this.calendarAdapter.setCalendar(this.calendar);

            this.rebuildCalendar();
        }
    }

    /**
     * Returns the current {@link java.util.Calendar} used by the calendar.
     * By default the calendar that is used is {@link java.util.GregorianCalendar}.
     * If you want to use a different calendar set it through
     * {@link #setCalendar(java.util.Calendar)}.
     *
     * @return the current calendar
     */
    public Calendar getCalendar() {
        return (Calendar) this.calendar.clone();
    }

    /**
     * Sets a new {@link java.util.Calendar} to be used by this instance.
     * By default the calendar that is used is {@link java.util.GregorianCalendar}.
     *
     * @param calendar the new calendar
     */
    public void setCalendar(Calendar calendar) {
        if (this.calendar != calendar) {
            this.calendar = (Calendar) calendar.clone();
            this.workCalendar = (Calendar) calendar.clone();
            this.calendarAdapter.setCalendar(this.calendar);
            this.setDisplayDate(this.calendar.getTimeInMillis());

            this.resetCalendar();
        }
    }

    private void initializeControl() {
        this.dayNamesHeight = (int) Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 21);
        this.titleHeight = (int) Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 40);

        this.gridLinesLayer = new GridLinesLayer();
        this.gestureManager = new CalendarGestureManager(this);
        this.gestureManager.setDisplayMode(this.displayMode);

        this.setAnimationsManager(new CalendarAnimationsManager(this));

        this.locale = Locale.getDefault();
        this.calendar = Calendar.getInstance(this.locale);
        this.workCalendar = Calendar.getInstance(this.locale);
        CalendarTools.setLocale(this.locale);

        this.setClickable(true);
        this.setFocusable(true);

        this.eventAdapter = new EventAdapter(this);
        this.calendarAdapter = new CalendarAdapter(this);
        setScrollManager(new CalendarScrollManager(this));
        this.cellDecorationsLayer = new CellDecorationsLayer(this);

        this.title = this.calendarAdapter.getTitleCell(this.displayDate, this.displayMode);

        long displayDate = CalendarTools.getDateStart(this.calendar.getTimeInMillis());
        if (this.minDate != 0 && this.minDate > this.displayDate) {
            displayDate = this.minDate;
        }
        if (this.maxDate != 0 && this.maxDate < this.displayDate) {
            displayDate = this.maxDate;
        }

        setSelectionManager(new CalendarSelectionManager(this));
        this.selectionManager.setSelectionMode(this.selectionMode);

        this.dateToCell = new Hashtable<Long, List<CalendarDayCell>>();

        this.generateCalendarDayNameElements();

        this.setScrollMode(this.scrollMode);
        this.setHorizontalScroll(this.horizontalScroll);

        setDisplayDate(displayDate);

        this.calendarAdapter.setStyle(CalendarStyles.light(getContext()));

        if (isInEditMode()) {
            setScrollMode(ScrollMode.Overlap);
        }
    }

    /**
     * Used to update the calendar fragments according to the current display date.
     */
    public void updateFragments() {
        updateFragments(false);
    }

    /**
     * Used to update the calendar fragments according to the current display date.
     *
     * @param force if forced the fragments will be updated disregarding their current state.
     */
    public void updateFragments(boolean force) {
        for (List<CalendarDayCell> list : this.dateToCell.values())
            list.clear();

        if (this.displayMode == CalendarDisplayMode.Month) {
            updateFragmentMonthMode(this.scrollManager.currentFragment(), this.displayDate, force);
            updateFragmentMonthMode(this.scrollManager.nextFragment(), CalendarTools.calculateNewValue(true, this.displayDate, this.displayMode), force);
            updateFragmentMonthMode(this.scrollManager.previousFragment(), CalendarTools.calculateNewValue(false, this.displayDate, this.displayMode), force);
            this.selectionManager.syncSelectedCellsWithDates();
        } else if (this.displayMode == CalendarDisplayMode.Year) {
            updateFragmentYearMode(this.scrollManager.currentFragment(), this.displayDate, force);
            updateFragmentYearMode(this.scrollManager.nextFragment(), CalendarTools.calculateNewValue(true, this.displayDate, this.displayMode), force);
            updateFragmentYearMode(this.scrollManager.previousFragment(), CalendarTools.calculateNewValue(false, this.displayDate, this.displayMode), force);
        } else if (this.displayMode == CalendarDisplayMode.Week) {
            updateFragmentWeekMode(this.scrollManager.currentFragment(), this.displayDate, force);
            updateFragmentWeekMode(this.scrollManager.nextFragment(), CalendarTools.calculateNewValue(true, this.displayDate, this.displayMode), force);
            updateFragmentWeekMode(this.scrollManager.previousFragment(), CalendarTools.calculateNewValue(false, this.displayDate, this.displayMode), force);

            this.selectionManager.syncSelectedCellsWithDates();
        }

        this.scrollManager.onDateChanged();
    }

    private void initFromXML(TypedArray array) {

        if (this.getResources() == null) {
            throw new IllegalStateException("The resources are not accessible.");
        }

        int backgroundColorFromStyle = array.getColor(
                R.styleable.RadCalendarView_calendarBackground,
                CalendarStyles.DEFAULT_BACKGROUND_COLOR);

        this.setBackgroundColor(backgroundColorFromStyle);

        this.showTitle = array.getBoolean(R.styleable.RadCalendarView_showTitle, true);

        this.showDayNames = array.getBoolean(R.styleable.RadCalendarView_showDayNames, true);

        boolean showGridLinesFromStyle = array.getBoolean(R.styleable.RadCalendarView_showGridLines, true);
        this.setShowGridLines(showGridLinesFromStyle);

        boolean showCellDecorationsFromStyle = array.getBoolean(R.styleable.RadCalendarView_showCellDecorations, true);
        this.setShowCellDecorations(showCellDecorationsFromStyle);

        this.isYearModeCompact = array.getBoolean(R.styleable.RadCalendarView_isYearModeCompact, this.isYearModeCompact);

        int selectionModeFromStyle = array.getInteger(R.styleable.RadCalendarView_selectionMode, CalendarSelectionMode.Multiple.ordinal());
        this.selectionMode = CalendarSelectionMode.values()[selectionModeFromStyle];

        final int displayModeFromStyle = array.getInteger(R.styleable.RadCalendarView_displayMode, CalendarDisplayMode.Month.ordinal());
        this.taskToBeExecutedAfterArrangeHasPassed = (new CalendarTask() {
            @Override
            public void execute() {
                changeDisplayMode(displayMode(), false);
            }

            @Override
            public CalendarDisplayMode displayMode() {
                return CalendarDisplayMode.values()[displayModeFromStyle];
            }
        });

        int weekNumberDisplayModeFromStyle = array.getInteger(R.styleable.RadCalendarView_weekNumberDisplayMode, this.weekNumbersDisplayMode.ordinal());
        setWeekNumbersDisplayMode(WeekNumbersDisplayMode.values()[weekNumberDisplayModeFromStyle]);
    }

    private void resetCalendar() {
        this.generateCalendarDayNameElements();
        this.dateToCell.clear();
        this.selectionManager.selectedCells().clear();
        this.calendarAdapter.reset();
        this.scrollManager.reset(true);
        this.cellDecorationsLayer.clearDecorations();
    }

    private void generateCalendarDayNameElements() {
        if (this.calendarAdapter == null)
            return;

        this.dayNames = this.calendarAdapter.generateCalendarRow();

        this.dayNames.addCell(this.calendarAdapter.getDayNameCell());
        if (this.weekNumbersDisplayMode != WeekNumbersDisplayMode.Block)
            dayNames.getCell(0).setVisibility(ElementVisibility.Gone);

        for (int i = 0; i < CalendarTools.DAYS_IN_A_WEEK; i++) {
            CalendarCell dayNameCell = calendarAdapter.getDayNameCell(i);
            if (this.dateToColor != null) {
                Integer color = this.dateToColor.apply(dayNameCell.getDate());
                if (color != null) {
                    dayNameCell.setTextColor(color);
                }
            }
            if (this.customizationRule != null) {
                this.customizationRule.apply(dayNameCell);
            }
            this.dayNames.addCell(dayNameCell);
        }

        if (!this.showDayNames || this.displayMode == CalendarDisplayMode.Year) {
            this.dayNames.setVisibility(ElementVisibility.Gone);
        }
    }

    /**
     * Updates a fragment that is in month mode.
     *
     * @param fragment      the fragment to be updated.
     * @param dateToDisplay the date to be displayed by the fragment.
     * @param force         <code>true</code> if the changes should be forced, <code>false</code> if a simple
     *                      update will be enough.
     */
    protected void updateFragmentMonthMode(CalendarFragment fragment, long dateToDisplay, boolean force) {
        if (!force && fragment.getDisplayMode() == this.displayMode && fragment.getDisplayDate() == dateToDisplay) {
            updateDateToCellsForFragment(fragment);
            updateFragmentIsFromCurrentMonth(fragment);
            updateFragmentCustomizations(fragment);

            return;
        }

        this.workCalendar.setTimeInMillis(CalendarTools.getFirstDisplayDate(dateToDisplay));

        for (int week = 0; week < CalendarTools.WEEKS_IN_A_MONTH; week++) {
            updateWeek(fragment.rows().get(week));
        }

        fragment.setDisplayDate(dateToDisplay);
        fragment.setDisplayMode(this.displayMode);
        updateFragmentIsFromCurrentMonth(fragment);
        updateFragmentCustomizations(fragment);
    }

    private void updateWeek(CalendarRow currentWeek) {
        CalendarDayCell currentCell = (CalendarDayCell) currentWeek.getCell(CalendarRow.WEEK_NUMBER_CELL_INDEX);
        if (this.weekNumbersDisplayMode == WeekNumbersDisplayMode.Block) {
            calendarAdapter.updateWeekNumberCell(currentCell, this.workCalendar.get(Calendar.WEEK_OF_YEAR));
        }

        currentCell.setVisibility(this.weekNumbersDisplayMode == WeekNumbersDisplayMode.Block ? ElementVisibility.Visible : ElementVisibility.Gone);

        List<CalendarDayCell> dateToCells;

        for (int day = 0; day < CalendarTools.DAYS_IN_A_WEEK; day++, this.workCalendar.add(Calendar.DAY_OF_YEAR, 1)) {
            currentCell = (CalendarDayCell) currentWeek.getCell(day + 1);

            currentCell.setVisibility(ElementVisibility.Visible);

            this.calendarAdapter.updateDateCell(currentCell, this.workCalendar.getTimeInMillis(),
                    this.eventAdapter.getEventsForDate(this.workCalendar.getTimeInMillis()),
                    day == 0 && this.weekNumbersDisplayMode == WeekNumbersDisplayMode.Inline);

            long time = this.workCalendar.getTimeInMillis();
            dateToCells = this.dateToCell.get(time);
            if (dateToCells == null) {
                dateToCells = new ArrayList<CalendarDayCell>();
                this.dateToCell.put(time, dateToCells);
            }

            validateDisabledDate(currentCell, time);

            dateToCells.add(currentCell);
            currentCell.setDate(time);
        }
    }

    private void updateFragmentIsFromCurrentMonth(CalendarFragment fragment) {
        int currentMonth = calendar.get(Calendar.MONTH);
        for (CalendarRow row : fragment.rows())
            for (CalendarCell cell : row.cells) {
                workCalendar.setTimeInMillis(cell.getDate());
                ((CalendarDayCell) cell).setIsFromCurrentMonth(workCalendar.get(Calendar.MONTH) == currentMonth);
            }
    }

    private void updateFragmentCustomizations(CalendarFragment fragment) {
        if (this.dateToColor != null && this.customizationRule != null) {
            for (CalendarRow row : fragment.rows())
                for (CalendarCell cell : row.cells) {
                    Integer color = this.dateToColor.apply(cell.getDate());
                    if (color != null) {
                        cell.setTextColor(color);
                    }

                    this.customizationRule.apply(cell);
                }
        } else if (this.dateToColor != null) {
            for (CalendarRow row : fragment.rows())
                for (CalendarCell cell : row.cells) {
                    Integer color = this.dateToColor.apply(cell.getDate());
                    if (color != null) {
                        cell.setTextColor(color);
                    }
                }
        } else if (this.customizationRule != null) {
            for (CalendarRow row : fragment.rows())
                for (CalendarCell cell : row.cells)
                    this.customizationRule.apply(cell);
        }
    }

    /**
     * Updates a fragment that is in week mode.
     *
     * @param fragment      the fragment to be updated.
     * @param dateToDisplay the date to be displayed by the fragment.
     * @param force         <code>true</code> if the changes should be forced, <code>false</code> if a simple
     *                      update will be enough.
     */
    protected void updateFragmentWeekMode(CalendarFragment fragment, long dateToDisplay, boolean force) {
        if (!force && fragment.getDisplayMode() == this.displayMode && fragment.getDisplayDate() == dateToDisplay) {
            updateDateToCellsForFragment(fragment);
            updateFragmentIsFromCurrentMonth(fragment);
            updateFragmentCustomizations(fragment);

            return;
        }

        this.workCalendar.setTimeInMillis(CalendarTools.getDateStart(CalendarTools.getFirstDateOfWeekWith(dateToDisplay)));
        updateWeek(fragment.rows().get(0));

        fragment.setDisplayDate(dateToDisplay);
        fragment.setDisplayMode(this.displayMode);
        updateFragmentIsFromCurrentMonth(fragment);
        updateFragmentCustomizations(fragment);
    }

    private void validateDisabledDate(CalendarDayCell currentCell, long time) {
        if (minDate != 0 && maxDate != 0)
            currentCell.setSelectable(time >= minDate && time <= maxDate);
        else if (minDate != 0)
            currentCell.setSelectable(time >= minDate);
        else if (maxDate != 0)
            currentCell.setSelectable(time <= maxDate);
    }

    /**
     * Updates a fragment that is in year mode.
     *
     * @param fragment      the fragment to be updated.
     * @param dateToDisplay the date to be displayed by the fragment.
     * @param force         <code>true</code> if the changes should be forced, <code>false</code> if a simple
     *                      update will be enough.
     */
    protected void updateFragmentYearMode(CalendarFragment fragment, long dateToDisplay, boolean force) {
        if (!force && fragment.getDisplayMode() == this.displayMode && fragment.getDisplayDate() == dateToDisplay) {
            updateFragmentCustomizations(fragment);

            return;
        }

        this.workCalendar.setTimeInMillis(dateToDisplay);
        this.workCalendar.set(Calendar.MONTH, Calendar.JANUARY);
        this.workCalendar.set(Calendar.DAY_OF_MONTH, 1);
        this.workCalendar.set(Calendar.HOUR, 0);
        this.workCalendar.set(Calendar.MINUTE, 0);
        this.workCalendar.set(Calendar.SECOND, 0);
        this.workCalendar.set(Calendar.MILLISECOND, 0);
        this.workCalendar.set(Calendar.AM_PM, 0);

        for (int i = 0; i < fragment.rows().size(); i++) {
            CalendarRow row = fragment.rows().get(i);

            for (int j = 0; j < row.cellsCount(); j++) {
                this.calendarAdapter.updateCalendarMonthCell((CalendarMonthCell) row.getCell(j), this.workCalendar.getTimeInMillis());
                this.workCalendar.add(Calendar.MONTH, 1);
            }
        }

        fragment.setDisplayDate(dateToDisplay);
        fragment.setDisplayMode(this.displayMode);
        updateFragmentCustomizations(fragment);
    }

    private void shrinkCalendar(CalendarDisplayMode displayMode, boolean animate) {
        this.suspendDisplayModeChange = true;
        if (!this.calendarShrinked) {
            this.originalWidth = getMeasuredWidth();
            this.originalHeight = getMeasuredHeight();
            this.calendarShrinked = true;
        }

        if (animate)
            shrinkCalendarAnimated(displayMode);
        else {
            ViewGroup.LayoutParams params = getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = calculateCurrentCalendarHeight();
            this.setLayoutParams(params);
            onDisplayModeChanged(displayMode);
            this.suspendDisplayModeChange = false;
        }
    }

    private void expandCalendar(CalendarDisplayMode displayMode, boolean animate) {
        this.suspendDisplayModeChange = true;
        if (animate) {
            expandCalendarAnimated(displayMode);
        } else {
            ViewGroup.LayoutParams params = getLayoutParams();
            params.width = this.originalWidth;
            params.height = this.originalHeight;

            this.setLayoutParams(params);
            onDisplayModeChanged(displayMode);
            this.suspendDisplayModeChange = false;
        }

        this.calendarShrinked = false;
    }

    private void expandCalendarAnimated(final CalendarDisplayMode displayMode) {
        final int start = calculateCurrentCalendarHeight();
        final int change = Math.abs(originalHeight - start);

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                getLayoutParams().height = (int) (start + (change * interpolatedTime));
                requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                suspendDisplayModeChange = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        a.setDuration(DEFAULT_ANIMATION_DURATION);
        onDisplayModeChanged(displayMode);
        startAnimation(a);
    }

    private void shrinkCalendarAnimated(final CalendarDisplayMode displayMode) {
        final int initialHeight = getMeasuredHeight();
        final int finalHeight = calculateCurrentCalendarHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                getLayoutParams().height = initialHeight - (int) ((initialHeight - finalHeight) * interpolatedTime);
                requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setAnimationListener(new Animation.AnimationListener() {


            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                onDisplayModeChanged(displayMode);
                suspendDisplayModeChange = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        a.setDuration(DEFAULT_ANIMATION_DURATION);
        startAnimation(a);
    }

    private int calculateCurrentCalendarHeight() {
        if (this.displayMode != CalendarDisplayMode.Month) {
            CalendarDisplayMode currentMode = this.displayMode;
            this.suspendUpdate = true;
            onDisplayModeChanged(CalendarDisplayMode.Month);
            int height = (int) ((this.title.getHeight() + this.dayNames.getHeight() + initialRowHeight) + (showGridLines ? this.gridLinesLayer.getWidth() / 2 : 0));
            onDisplayModeChanged(currentMode);
            this.suspendUpdate = false;
            return height;
        }

        return (int) ((this.title.getHeight() + this.dayNames.getHeight() + initialRowHeight) + (showGridLines ? this.gridLinesLayer.getWidth() / 2 : 0));
    }

    private void animateMonthToYear() {
        if (this.getWidth() == 0 || this.getHeight() == 0) {
            this.onDisplayModeChanged(CalendarDisplayMode.Year);
            return;
        }

        CalendarFragment monthFragment = calendarAdapter.generateFragment();
        updateFragmentMonthMode(monthFragment, this.displayDate, true);
        monthFragment.arrange(this.scrollManager.getLeft(), this.scrollManager.getTop(), this.scrollManager.getRight(), this.scrollManager.getBottom());

        this.suspendUpdate = true;
        this.onDisplayModeChanged(CalendarDisplayMode.Year);
        this.suspendUpdate = false;

        CalendarFragment yearFragment = calendarAdapter.generateFragment();
        updateFragmentYearMode(yearFragment, this.displayDate, true);
        yearFragment.arrange(this.scrollManager.getLeft(), this.scrollManager.getTop(), this.scrollManager.getRight(), this.scrollManager.getBottom());

        this.animationsManager.beginAnimation(monthFragment, yearFragment, getMonthCell(getCurrentMonth()).calcBorderRect());
    }

    private int getCurrentMonth() {
        this.workCalendar.setTimeInMillis(this.displayDate);
        return this.workCalendar.get(Calendar.MONTH);
    }

    private void animateYearToMonth() {
        if (this.getWidth() == 0 || this.getHeight() == 0) {
            this.onDisplayModeChanged(CalendarDisplayMode.Month);
            return;
        }

        CalendarFragment yearFragment = calendarAdapter.generateFragment();
        updateFragmentYearMode(yearFragment, displayDate, true);
        yearFragment.arrange(this.scrollManager.getLeft(), this.scrollManager.getTop(), this.scrollManager.getRight(), this.scrollManager.getBottom());

        CalendarCell cell = getMonthCell(getCurrentMonth());

        this.suspendUpdate = true;
        this.onDisplayModeChanged(CalendarDisplayMode.Month);
        this.suspendUpdate = false;

        CalendarFragment monthFragment = calendarAdapter.generateFragment();
        updateFragmentMonthMode(monthFragment, displayDate, true);
        monthFragment.arrange(this.scrollManager.getLeft(), this.scrollManager.getTop(), this.scrollManager.getRight(), this.scrollManager.getBottom());

        this.animationsManager.beginAnimation(monthFragment, yearFragment, cell.calcBorderRect());
    }

    private CalendarCell getMonthCell(int month) {
        if (this.displayMode != CalendarDisplayMode.Year)
            return null;

        int cellsCount = this.scrollManager.currentFragment().rows().get(0).cellsCount();
        int row = month / cellsCount;

        return this.scrollManager.currentFragment().rows().get(row).getCell(month - (row * cellsCount));
    }

    private void handleShowDayNamesChange() {
        if (this.dayNames == null) {
            return;
        }

        if (this.showDayNames) {
            this.dayNames.setVisibility(ElementVisibility.Visible);
        } else {
            this.dayNames.setVisibility(ElementVisibility.Gone);
        }

        this.invalidateArrange();
        if (!this.inOriginalSizeForAllModes && this.displayMode == CalendarDisplayMode.Week)
            shrinkCalendar(this.displayMode, false);

        this.invalidate();
    }

    private void handleShowTitleChange() {
        if (this.title == null) {
            return;
        }

        if (this.showTitle) {
            this.title.setVisibility(ElementVisibility.Visible);
        } else {
            this.title.setVisibility(ElementVisibility.Gone);
        }

        invalidateArrange();
        if (!this.inOriginalSizeForAllModes && this.displayMode == CalendarDisplayMode.Week)
            shrinkCalendar(this.displayMode, false);

        this.invalidate();
    }

    private void onDisplayDateChanged() {
        this.calendarAdapter.updateTitle(this.title, this.displayDate, this.displayMode);
        updateFragments();
        this.scrollManager.setActiveDate(this.displayDate);
    }

    private void updateDateToCellsForFragment(CalendarFragment fragment) {
        for (int row = 0, rowsCount = fragment.rows().size(); row < rowsCount; row++) {
            CalendarRow currentRow = fragment.rows().get(row);
            for (int cell = 1, cellsCount = currentRow.cellsCount(); cell < cellsCount; cell++) {
                CalendarDayCell currentCell = (CalendarDayCell) currentRow.getCell(cell);
                List<CalendarDayCell> dateToCells = this.dateToCell.get(currentCell.getDate());
                if (dateToCells == null) {
                    dateToCells = new ArrayList<CalendarDayCell>();
                    this.dateToCell.put(currentCell.getDate(), dateToCells);
                }

                dateToCells.add(currentCell);
            }
        }
    }

    private interface CalendarTask {
        CalendarDisplayMode displayMode();

        void execute();
    }

    /**
     * Represents an interface for a listener that will execute its method when
     * the display date in {@link RadCalendarView} is changed.
     */
    public static interface OnDisplayDateChangedListener {

        /**
         * Represents a method which will be executed when
         * the display date in {@link RadCalendarView} is changed.
         *
         * @param oldValue the old display date
         * @param newValue the new display date
         */
        public void onDisplayDateChanged(long oldValue, long newValue);
    }

    /**
     * Represents an interface for a listener that will execute its method when
     * the display mode in {@link RadCalendarView} is changed.
     */
    public static interface OnDisplayModeChangedListener {

        /**
         * Represents a method which will be executed when
         * the display mode in {@link RadCalendarView} is changed.
         *
         * @param oldValue the old display mode
         * @param newValue the new display mode
         */
        public void onDisplayModeChanged(CalendarDisplayMode oldValue, CalendarDisplayMode newValue);
    }

    /**
     * Represents an interface for a listener that will execute its method when
     * the selected dates in {@link RadCalendarView} are changed.
     */
    public static interface OnSelectedDatesChangedListener {

        /**
         * Represents a method which will be executed when the selected dates in
         * {@link RadCalendarView} are changed.
         *
         * @param context a selection context which contains information about the old selection,
         *                the new selection and the items that are currently
         *                added or removed from the selection
         */
        public void onSelectedDatesChanged(SelectionContext context);
    }

    /**
     * Represents an interface for a listener that will execute its method when
     * a cell from the {@link RadCalendarView} is clicked.
     */
    public static interface OnCellClickListener {

        /**
         * Represents a method which will be executed when a cell in
         * {@link RadCalendarView} is clicked.
         *
         * @param clickedCell the cell that is clicked
         */
        public void onCellClick(CalendarCell clickedCell);
    }

    /**
     * Represents a class which represents a context for selection. A parameter of this type is
     * used when the current selection is changed. The context contains information for the
     * old and the new selection, as well as the items that are currently being added or removed.
     *
     * @see #setOnSelectedDatesChangedListener(OnSelectedDatesChangedListener)
     */
    public static class SelectionContext {
        List<Long> oldSelection;
        List<Long> newSelection;
        List<Long> datesAdded;
        List<Long> datesRemoved;

        /**
         * Creates a new SelectionContext instance.
         */
        public SelectionContext() {
            oldSelection = new ArrayList<Long>();
            newSelection = new ArrayList<Long>();
            datesAdded = new ArrayList<Long>();
            datesRemoved = new ArrayList<Long>();
        }

        /**
         * Returns the old selection.
         */
        public List<Long> oldSelection() {
            return oldSelection;
        }

        /**
         * Returns the new selection.
         */
        public List<Long> newSelection() {
            return newSelection;
        }

        /**
         * Returns the dates that are currently added to the selection.
         */
        public List<Long> datesAdded() {
            return datesAdded;
        }

        /**
         * Returns the dates that are currently removed from the selection.
         */
        public List<Long> datesRemoved() {
            return datesRemoved;
        }
    }
}

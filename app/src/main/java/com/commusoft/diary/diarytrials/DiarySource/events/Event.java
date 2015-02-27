package com.commusoft.diary.diarytrials.DiarySource.events;

import android.graphics.Color;

/**
 * Data model for events displayed by {@link com.commusoft.diary.diarytrials.DiarySource.RadCalendarView}.
 */
public class Event {
    private static final int DEFAULT_EVENT_COLOR = Color.parseColor("#B233B5E5");

    private int calendarId;
    private int eventColor;
    private String title;
    private long startTime;
    private long endTime;
    private boolean allDay;

    /**
     * Creates a new instance of the {@link com.commusoft.diary.diarytrials.DiarySource.events.Event} class.
     *
     * @param title     the event's title
     * @param startTime the event's start date
     * @param endTime   the event's end date
     * @throws java.lang.IllegalArgumentException If the start date is after the end date or if they are the same
     */
    public Event(String title, long startTime, long endTime) {
        if (startTime >= endTime) {
            throw new IllegalArgumentException("endTime should be after startTime.");
        }
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventColor = DEFAULT_EVENT_COLOR;
    }

    /**
     * Returns the id of the calendar that contains this event.
     *
     * @return the calendar id
     */
    public int getCalendarId() {
        return calendarId;
    }

    /**
     * Sets the id of the calendar that contains this event.
     *
     * @param calendarId the calendar id
     */
    public void setCalendarId(int calendarId) {
        this.calendarId = calendarId;
    }

    /**
     * Returns the color that should be used for representation of this event.
     *
     * @return the event color
     */
    public int getEventColor() {
        return eventColor;
    }

    /**
     * Sets the color that will be used for representation of this event.
     *
     * @param eventColor the event color
     */
    public void setEventColor(int eventColor) {
        this.eventColor = eventColor;
    }

    /**
     * Returns the title of this event.
     *
     * @return the title of the event
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of this event.
     *
     * @param title the title of the event
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the start date of this event.
     *
     * @return the start date of the event
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Sets the start date of this event.
     *
     * @param startTime the start date of the event
     * @throws java.lang.IllegalArgumentException If the start date is after the end date or if they are the same
     */
    public void setStartTime(long startTime) {
        if (startTime >= endTime) {
            throw new IllegalArgumentException("endTime should be after startTime.");
        }

        this.startTime = startTime;
    }

    /**
     * Returns the end date of this event.
     *
     * @return the end date of the event
     */
    public long getEndTime() {
        return endTime;
    }

    /**
     * Sets the end date of this event.
     *
     * @param endTime the end date of the event
     * @throws java.lang.IllegalArgumentException If the start date is after the end date or if they are the same
     */
    public void setEndTime(long endTime) {
        if (startTime >= endTime) {
            throw new IllegalArgumentException("endTime should be after startTime.");
        }

        this.endTime = endTime;
    }

    /**
     * Returns a value which indicates whether this is an all day event.
     *
     * @return whether this is an all day event
     */
    public boolean isAllDay() {
        return allDay;
    }

    /**
     * Sets a value which indicates whether this is an all day event.
     *
     * @param allDay whether this is an all day event
     */
    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }
}

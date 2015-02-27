package com.commusoft.diary.diarytrials.DiarySource.events;

import com.commusoft.diary.diarytrials.DiarySource.CalendarTools;
import com.commusoft.diary.diarytrials.DiarySource.RadCalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Default adapter for events displayed by {@link com.commusoft.diary.diarytrials.DiarySource.RadCalendarView}.
 */
public class EventAdapter {

    private final RadCalendarView owner;
    private List<com.commusoft.diary.diarytrials.DiarySource.events.Event> events;
    private Calendar calendar;
    private EventRenderer renderer;

    /**
     * Creates a new instance of the {@link com.telerik.widget.calendar.events.EventAdapter} class.
     *
     * @param owner the calendar instance owning this adapter.
     */
    public EventAdapter(RadCalendarView owner) {
        this(owner, null);
    }

    /**
     * Creates a new instance of the {@link com.telerik.widget.calendar.events.EventAdapter} class.
     *
     * @param owner  the calendar instance owning this adapter.
     * @param events list of events for this adapter
     */
    public EventAdapter(RadCalendarView owner, List<com.commusoft.diary.diarytrials.DiarySource.events.Event> events) {
        this.owner = owner;
        this.calendar = owner.getCalendar();
        this.events = events;
        this.renderer = new EventRenderer(owner.getContext());
    }

    /**
     * Gets a list of events that should be displayed for the provided date.
     *
     * @param date the date of the events that we need to display
     * @return list of event for the provided date
     */
    public List<com.commusoft.diary.diarytrials.DiarySource.events.Event> getEventsForDate(long date) {
        if (this.events == null) {
            return null;
        }

        long dateStart = CalendarTools.getDateStart(date);
        calendar.setTimeInMillis(dateStart);
        calendar.add(Calendar.DATE, 1);
        long dateEnd = calendar.getTimeInMillis();

        List<com.commusoft.diary.diarytrials.DiarySource.events.Event> eventsForDate = new ArrayList<com.commusoft.diary.diarytrials.DiarySource.events.Event>();
        for (com.commusoft.diary.diarytrials.DiarySource.events.Event event : this.events) {
            if (event == null) {
                continue;
            }

            if (eventShouldBeVisible(event, dateStart, dateEnd)) {
                eventsForDate.add(event);
            }
        }
        return eventsForDate;
    }

    /**
     * Adds an event to the collection of elements.
     *
     * @param event the event to be added.
     */
    public void addEvent(com.commusoft.diary.diarytrials.DiarySource.events.Event event) {
        this.events.add(event);
        this.owner.notifyDataChanged();
    }

    /**
     * Gets the list of all events handled by this adapter.
     *
     * @return list of all events
     */
    public List<com.commusoft.diary.diarytrials.DiarySource.events.Event> getEvents() {
        return events;
    }

    /**
     * Sets the list of all events handled by this adapter.
     *
     * @param events the list of all events
     */
    public void setEvents(List<com.commusoft.diary.diarytrials.DiarySource.events.Event> events) {
        this.events = events;
        this.owner.notifyDataChanged();
    }

    /**
     * Gets the current {@link com.telerik.widget.calendar.events.EventRenderer}
     * which is responsible for the drawing of the events.
     *
     * @return the current event renderer
     */
    public EventRenderer getRenderer() {
        return renderer;
    }

    /**
     * Sets an {@link com.telerik.widget.calendar.events.EventRenderer}
     * which will be responsible for the drawing of the events.
     *
     * @param renderer the new event renderer
     */
    public void setRenderer(EventRenderer renderer) {
        this.renderer = renderer;
    }

    private boolean eventShouldBeVisible(com.commusoft.diary.diarytrials.DiarySource.events.Event event, long dateStart, long dateEnd) {

        if (event.isAllDay() && event.getStartTime() >= dateStart && event.getStartTime() < dateEnd) {
            return true;
        } else if (!event.isAllDay() && dateStart < event.getEndTime() && dateEnd > event.getStartTime()) {
            return true;
        }
        return false;
    }
}

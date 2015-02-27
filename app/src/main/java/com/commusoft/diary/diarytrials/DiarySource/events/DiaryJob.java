package com.commusoft.diary.diarytrials.DiarySource.events;

import com.commusoft.diary.diarytrials.DiarySource.events.Event;

import java.util.Calendar;

/**
 * Created by Samir on 26/02/2015.
 */
public class DiaryJob extends Event {

    /**
     * Creates a new instance of the {@link com.commusoft.diary.diarytrials.DiarySource.events.Event} class.
     *
     * @param title     the event's title
     * @param startDate the event's start date
     * @param endDate   the event's end date
     * @throws IllegalArgumentException If the start date is after the end date or if they are the same
     */
    private long id;

    public DiaryJob(String title, long startDate, long endDate, long id) {
        super(title, startDate, endDate);
        this.id = id;
    }

    public Calendar getCalendarDate(long startTime){

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        return  calendar;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}

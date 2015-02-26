package com.commusoft.diary.diarytrials.calendar.Model;

import com.commusoft.diary.diarytrials.TelerikSource.events.Event;

/**
 * Created by Samir on 26/02/2015.
 */
public class DiaryJob extends Event {

    /**
     * Creates a new instance of the {@link com.commusoft.diary.diarytrials.TelerikSource.events.Event} class.
     *
     * @param title     the event's title
     * @param startDate the event's start date
     * @param endDate   the event's end date
     * @throws IllegalArgumentException If the start date is after the end date or if they are the same
     */
    private String id;

    public DiaryJob(String title, long startDate, long endDate, String id) {
        super(title, startDate, endDate);
        this.id = id;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}

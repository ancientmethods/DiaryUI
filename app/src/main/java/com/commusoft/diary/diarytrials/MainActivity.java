package com.commusoft.diary.diarytrials;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.commusoft.diary.diarytrials.DailyView.DailyView;
import com.commusoft.diary.diarytrials.DailyView.DailyViewEvent;
import com.commusoft.diary.diarytrials.calendar.TelerikActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
    /**
     * Created by Raquib-ul-Alam Kanak on 7/21/2014.
     * Website: http://april-shower.com
     */
    public class MainActivity extends ActionBarActivity implements DailyView.MonthChangeListener,
            DailyView.EventClickListener, DailyView.EventLongPressListener {

        private static final int TYPE_DAY_VIEW = 1;
        private static final int TYPE_THREE_DAY_VIEW = 2;
        private static final int TYPE_WEEK_VIEW = 3;
        private int mDailyViewType = TYPE_THREE_DAY_VIEW;
        private DailyView mDailyView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            // Get a reference for the week view in the layout.
            mDailyView = (DailyView) findViewById(R.id.weekView);

            // Show a toast message about the touched event.
            mDailyView.setOnEventClickListener(this);

            // The week view has infinite scrolling horizontally. We have to provide the events of a
            // month every time the month changes on the week view.
            mDailyView.setMonthChangeListener(this);

            // Set long press listener for events.
            mDailyView.setEventLongPressListener(this);
        }


        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            switch (id){
                case R.id.action_today:
                    mDailyView.goToToday();
                    return true;
                case R.id.action_day_view:
                    if (mDailyViewType != TYPE_DAY_VIEW) {
                        item.setChecked(!item.isChecked());
                        mDailyViewType = TYPE_DAY_VIEW;
                        mDailyView.setNumberOfVisibleDays(1);

                        // Lets change some dimensions to best fit the view.
                        mDailyView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                        mDailyView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                        mDailyView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    }
                    return true;
                case R.id.action_three_day_view:
                    if (mDailyViewType != TYPE_THREE_DAY_VIEW) {
                        item.setChecked(!item.isChecked());
                        mDailyViewType = TYPE_THREE_DAY_VIEW;
                        mDailyView.setNumberOfVisibleDays(3);

                        // Lets change some dimensions to best fit the view.
                        mDailyView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                        mDailyView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                        mDailyView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    }
                    return true;
                case R.id.action_week_view:
                    if (mDailyViewType != TYPE_WEEK_VIEW) {
                        item.setChecked(!item.isChecked());
                        mDailyViewType = TYPE_WEEK_VIEW;
                        mDailyView.setNumberOfVisibleDays(7);

                        // Lets change some dimensions to best fit the view.
                        mDailyView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                        mDailyView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                        mDailyView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    }
                    return true;

                case R.id.action_telerik:

                    Intent intent= new Intent(MainActivity.this, TelerikActivity.class);
                    startActivity(intent);

                    return true;
            }

            return super.onOptionsItemSelected(item);
        }

        @Override
        public List<DailyViewEvent> onMonthChange(int newYear, int newMonth) {

            // Populate the week view with some events.
            List<DailyViewEvent> events = new ArrayList<DailyViewEvent>();

            Calendar startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, 3);
            startTime.set(Calendar.MINUTE, 0);
            startTime.set(Calendar.MONTH, newMonth-1);
            startTime.set(Calendar.YEAR, newYear);
            Calendar endTime = (Calendar) startTime.clone();
            endTime.add(Calendar.HOUR, 1);
            endTime.set(Calendar.MONTH, newMonth-1);
            DailyViewEvent event = new DailyViewEvent(1, getEventTitle(startTime), startTime, endTime);
            event.setColor(getResources().getColor(R.color.event_color_01));
            events.add(event);

            startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, 3);
            startTime.set(Calendar.MINUTE, 30);
            startTime.set(Calendar.MONTH, newMonth-1);
            startTime.set(Calendar.YEAR, newYear);
            endTime = (Calendar) startTime.clone();
            endTime.set(Calendar.HOUR_OF_DAY, 4);
            endTime.set(Calendar.MINUTE, 30);
            endTime.set(Calendar.MONTH, newMonth-1);
            event = new DailyViewEvent(10, getEventTitle(startTime), startTime, endTime);
            event.setColor(getResources().getColor(R.color.event_color_02));
            events.add(event);

            startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, 4);
            startTime.set(Calendar.MINUTE, 20);
            startTime.set(Calendar.MONTH, newMonth-1);
            startTime.set(Calendar.YEAR, newYear);
            endTime = (Calendar) startTime.clone();
            endTime.set(Calendar.HOUR_OF_DAY, 5);
            endTime.set(Calendar.MINUTE, 0);
            event = new DailyViewEvent(10, getEventTitle(startTime), startTime, endTime);
            event.setColor(getResources().getColor(R.color.event_color_03));
            events.add(event);

            startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, 5);
            startTime.set(Calendar.MINUTE, 30);
            startTime.set(Calendar.MONTH, newMonth-1);
            startTime.set(Calendar.YEAR, newYear);
            endTime = (Calendar) startTime.clone();
            endTime.add(Calendar.HOUR_OF_DAY, 2);
            endTime.set(Calendar.MONTH, newMonth-1);
            event = new DailyViewEvent(2, getEventTitle(startTime), startTime, endTime);
            event.setColor(getResources().getColor(R.color.event_color_02));
            events.add(event);

            startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, 5);
            startTime.set(Calendar.MINUTE, 0);
            startTime.set(Calendar.MONTH, newMonth-1);
            startTime.set(Calendar.YEAR, newYear);
            startTime.add(Calendar.DATE, 1);
            endTime = (Calendar) startTime.clone();
            endTime.add(Calendar.HOUR_OF_DAY, 3);
            endTime.set(Calendar.MONTH, newMonth - 1);
            event = new DailyViewEvent(3, getEventTitle(startTime), startTime, endTime);
            event.setColor(getResources().getColor(R.color.event_color_03));
            events.add(event);

            startTime = Calendar.getInstance();
            startTime.set(Calendar.DAY_OF_MONTH, 15);
            startTime.set(Calendar.HOUR_OF_DAY, 3);
            startTime.set(Calendar.MINUTE, 0);
            startTime.set(Calendar.MONTH, newMonth-1);
            startTime.set(Calendar.YEAR, newYear);
            endTime = (Calendar) startTime.clone();
            endTime.add(Calendar.HOUR_OF_DAY, 3);
            event = new DailyViewEvent(4, getEventTitle(startTime), startTime, endTime);
            event.setColor(getResources().getColor(R.color.event_color_04));
            events.add(event);

            startTime = Calendar.getInstance();
            startTime.set(Calendar.DAY_OF_MONTH, 1);
            startTime.set(Calendar.HOUR_OF_DAY, 3);
            startTime.set(Calendar.MINUTE, 0);
            startTime.set(Calendar.MONTH, newMonth-1);
            startTime.set(Calendar.YEAR, newYear);
            endTime = (Calendar) startTime.clone();
            endTime.add(Calendar.HOUR_OF_DAY, 3);
            event = new DailyViewEvent(5, getEventTitle(startTime), startTime, endTime);
            event.setColor(getResources().getColor(R.color.event_color_01));
            events.add(event);

            startTime = Calendar.getInstance();
            startTime.set(Calendar.DAY_OF_MONTH, startTime.getActualMaximum(Calendar.DAY_OF_MONTH));
            startTime.set(Calendar.HOUR_OF_DAY, 15);
            startTime.set(Calendar.MINUTE, 0);
            startTime.set(Calendar.MONTH, newMonth-1);
            startTime.set(Calendar.YEAR, newYear);
            endTime = (Calendar) startTime.clone();
            endTime.add(Calendar.HOUR_OF_DAY, 3);
            event = new DailyViewEvent(5, getEventTitle(startTime), startTime, endTime);
            event.setColor(getResources().getColor(R.color.event_color_02));
            events.add(event);

            return events;
        }



        private String getEventTitle(Calendar time) {
            return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
        }

        @Override
        public void onEventClick(DailyViewEvent event, RectF eventRect) {
            Toast.makeText(MainActivity.this, "Clicked " + event.getName(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onEventLongPress(DailyViewEvent event, RectF eventRect) {
            Toast.makeText(MainActivity.this, "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();
        }
    }
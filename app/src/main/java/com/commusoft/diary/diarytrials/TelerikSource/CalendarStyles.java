package com.commusoft.diary.diarytrials.TelerikSource;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;

import com.commusoft.diary.diarytrials.R;
import com.telerik.android.common.Util;

public class CalendarStyles {

    public static final int DEFAULT_BACKGROUND_COLOR = Color.TRANSPARENT;

    private static CalendarStyle baseStyle(Context context) {
        CalendarStyle style = new CalendarStyle();
        style.titleTextSize = Util.getDimen(TypedValue.COMPLEX_UNIT_SP, 16);
        style.titleTypeFace = Typeface.create("sans-serif", Typeface.BOLD);
        style.titleTextPosition = CalendarElement.CENTER;

        style.dayNameTextPosition = CalendarElement.CENTER_VERTICAL | CalendarElement.RIGHT;
        style.dayNameTextSize = Util.getDimen(TypedValue.COMPLEX_UNIT_SP, 11.5f);
        style.dayNamePaddingHorizontal = (int) Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 8);
        style.dayNamePaddingVertical = (int) Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 8);

        style.dayNameTextSizeYearMode = Util.getDimen(TypedValue.COMPLEX_UNIT_SP, 9);

        style.dateTextSize = Util.getDimen(TypedValue.COMPLEX_UNIT_SP, 16);
        style.dateTextSizeYearMode = context.getResources().getDimension(R.dimen.date_text_size_year_mode);
        style.dateTypeFace = Typeface.SANS_SERIF;
        style.dateTypeFaceYearMode = Typeface.SANS_SERIF;

        style.weekNumberTextSize = Util.getDimen(TypedValue.COMPLEX_UNIT_SP, 10);
        style.weekNumberTextPosition = CalendarElement.TOP | CalendarElement.LEFT;

        style.todayCellTypeFace = Typeface.DEFAULT_BOLD;
        style.todayTypeFace = Typeface.DEFAULT_BOLD;

        style.dateCellPaddingHorizontal = (int) Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 8);
        style.dateCellPaddingVertical = (int) Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 8);

        style.monthNameTextPosition = CalendarElement.RIGHT;
        style.monthNameTextSize = Util.getDimen(TypedValue.COMPLEX_UNIT_SP, 11.5f);
        style.monthNameTextSizeCompact = Util.getDimen(TypedValue.COMPLEX_UNIT_SP, 24);

        style.monthCellPaddingHorizontal = (int) Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 6);
        style.monthCellPaddingVertical = (int) Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 5);

        style.decorationsColor = Color.parseColor("#33add6");
        style.decorationsStrokeWidth = Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 1f);

        return style;
    }

    public static CalendarStyle light(Context context) {
        CalendarStyle style = baseStyle(context);

        // Title
        style.titleTextColor = Color.parseColor("#777777");
        style.titleBackgroundColor = Color.parseColor("#eeeeee");

        // Day name cells
        style.dayNameBackgroundColor = Color.parseColor("#eeeeee");
        style.dayNameTextColor = Color.parseColor("#999999");

        // Day names year mode
        style.dayNameTextColorYearModeEnabled = Color.parseColor("#777777");
        style.dayNameTextColorYearModeDisabled = Color.parseColor("#777777");

        // Date cells
        style.dateCellBackgroundColorEnabled = Color.parseColor("#eeeeee");
        style.dateCellBackgroundColorDisabled = Color.parseColor("#e5e5e5");
        style.dateTextColorEnabled = Color.parseColor("#333333");
        style.dateTextColorDisabled = Color.parseColor("#999999");

        style.dateTextColorYearModeEnabled = Color.parseColor("#333333");
        style.dateTextColorYearModeDisabled = Color.parseColor("#999999");

        // Week Number Cells
        style.weekNumberTextColorEnabled = Color.parseColor("#ababab");
        style.weekNumberTextColorDisabled = Color.parseColor("#a4a4a4");
        style.weekNumberBackgroundColorEnabled = Color.parseColor("#eeeeee");
        style.weekNumberBackgroundColorDisabled = Color.parseColor("#e5e5e5");

        // Month Cells
        style.monthNameTextColorEnabled = Color.parseColor("#777777");
        style.monthNameTextColorDisabled = Color.parseColor("#777777");

        style.gridLinesColor = Color.parseColor("#cccccc");
        style.selectedCellBackgroundColor = Color.parseColor("#adadad");
        style.todayCellBackgroundColor = Color.parseColor("#ffffff");

        style.todayBackgroundColor = Color.parseColor("#333333");
        style.todayTextColor = Color.WHITE;
        style.todayCellTextColor = style.dateTextColorEnabled;

        return style;
    }

    public static CalendarStyle dark(Context context) {
        CalendarStyle style = baseStyle(context);

        // Title
        style.titleTextColor = Color.parseColor("#cccccc");
        style.titleBackgroundColor = Color.parseColor("#474747");

        // Day name cells
        style.dayNameBackgroundColor = Color.parseColor("#474747");
        style.dayNameTextColor = Color.parseColor("#848484");

        // Day names year mode
        style.dayNameTextColorYearModeEnabled = Color.parseColor("#999999");
        style.dayNameTextColorYearModeDisabled = Color.parseColor("#999999");

        // Date cells
        style.dateCellBackgroundColorEnabled = Color.parseColor("#474747");
        style.dateCellBackgroundColorDisabled = Color.parseColor("#545454");
        style.dateTextColorEnabled = Color.WHITE;
        style.dateTextColorDisabled = Color.parseColor("#999999");

        style.dateTextColorYearModeEnabled = Color.parseColor("#ffffff");
        style.dateTextColorYearModeDisabled = Color.parseColor("#999999");

        // Week Number Cells
        style.weekNumberTextColorEnabled = Color.BLACK;
        style.weekNumberTextColorDisabled = Color.BLACK;
        style.weekNumberBackgroundColorEnabled = Color.parseColor("#474747");
        style.weekNumberBackgroundColorDisabled = Color.parseColor("#545454");

        // Month Cells
        style.monthNameTextColorEnabled = Color.parseColor("#999999");
        style.monthNameTextColorDisabled = Color.parseColor("#999999");

        style.gridLinesColor = Color.parseColor("#2f2f2f");
        style.selectedCellBackgroundColor = Color.parseColor("#7e7e7e");
        style.todayCellBackgroundColor = Color.parseColor("#3a3a3a");

        style.todayBackgroundColor = Color.WHITE;
        style.todayTextColor = Color.parseColor("#3a3a3a");
        style.todayCellTextColor = style.dateTextColorEnabled;

        return style;
    }
}

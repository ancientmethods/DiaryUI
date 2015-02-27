package com.commusoft.diary.diarytrials.DiarySource;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * The class holding the logic common for every cell type in the calendar.
 */
public abstract class CalendarCell extends CalendarTextElement {

    private Paint borderPaint;

    /**
     * The type of the cell.
     */
    protected CalendarCellType cellType;
    /**
     * States whether the cell is holding the today's date.
     */
    protected boolean cellToday;
    private long date;
    private CalendarRow row;

    /**
     * Initializes a new instance of the {@link CalendarCell} class with passed
     * {@link Context}.
     *
     * @param owner the calendar instance owning this cell.
     */
    public CalendarCell(RadCalendarView owner) {
        super(owner);
    }

    /**
     * Gets the border color of the cell.
     * @return the current border color.
     */
    public int getBorderColor() {
        return borderPaint().getColor();
    }

    /**
     * Sets the border color of the cell.
     *
     * @param color the new border color.
     */
    public void setBorderColor(int color) {
        if (color == Color.TRANSPARENT) {
            this.borderPaint = null;
            return;
        }

        borderPaint().setColor(color);
    }

    /**
     * Gets the border width of the cell.
     *
     * @return the current border width.
     */
    public float getBorderWidth() {
        return borderPaint().getStrokeWidth();
    }

    /**
     * Sets the border width of the cell.
     *
     * @param width the new border width.
     */
    public void setBorderWidth(float width) {
        if (width == 0) {
            this.borderPaint = null;
            return;
        }

        borderPaint().setStrokeWidth(width);
    }

    /**
     * Gets the row that currently holds the cell.
     *
     * @return the current row.
     */
    public CalendarRow getRow() {
        return this.row;
    }

    /**
     * Sets the current row holding the cell.
     *
     * @param row the new holding row.
     */
    public void setRow(CalendarRow row) {
        this.row = row;
    }

    /**
     * Gets the {@link CalendarCellType}
     * object that defines the type of this CalendarCell.
     *
     * @return the cell type
     */
    public CalendarCellType getCellType() {
        return this.cellType;
    }

    /**
     * Sets a {@link CalendarCellType}
     * object that defines the type of information that will be visualized with
     * this CalendarCell - date, day name, week number, etc.
     *
     * @param value the new cell type value
     */
    public void setCellType(CalendarCellType value) {
        if (this.cellType != value) {
            this.cellType = value;
        }
    }

    /**
     * Gets the date represented by the current CalendarCell.
     *
     * @return the represented date
     */
    public long getDate() {
        return this.date;
    }

    /**
     * Sets the date represented by the current CalendarCell.
     *
     * @param date the represented date
     */
    public void setDate(long date) {
        if (this.date != date) {
            this.date = CalendarTools.getDateStart(date);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            super.setEnabled(enabled);

            if (this.owner.getCustomizationRule() != null)
                this.owner.getCustomizationRule().apply(this);
        }
    }

    /**
     * The border paint.
     *
     * @return the current border paint.
     */
    protected Paint borderPaint() {
        if (this.borderPaint == null) {
            this.borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            this.borderPaint.setStyle(Paint.Style.STROKE);
        }

        return this.borderPaint;
    }

    /**
     * Gets a value that determines whether the current cell holds the today's date.
     *
     * @return <code>true</code> if the today's date is being presented by this cell, <code>false</code> otherwise.
     */
    public boolean isToday() {
        return this.cellToday;
    }

    /**
     * Sets a value that determines whether the current cell holds the today's date.
     *
     * @param cellToday <code>true</code> if the today's date is being presented by this cell, <code>false</code> otherwise.
     */
    public void setAsToday(boolean cellToday) {
        if (this.cellToday != cellToday) {
            this.cellToday = cellToday;

            updateBackgroundColor();
            updateTextColor();
            updateTypeFace();
        }
    }

    @Override
    public void postRender(Canvas canvas) {
        super.postRender(canvas);

        if (this.borderPaint != null)
            canvas.drawRect(getLeft(), getTop(), getRight(), getBottom(), this.borderPaint);
    }
}

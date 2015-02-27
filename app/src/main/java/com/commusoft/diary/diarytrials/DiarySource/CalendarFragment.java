package com.commusoft.diary.diarytrials.DiarySource;

import android.graphics.Canvas;
import android.graphics.Color;

import java.util.ArrayList;

/**
 * The calendar fragment is a class holding a single "page" of the calendar. It operates according to the current {@link com.telerik.widget.calendar.CalendarDisplayMode} and can be of the shape
 * of a rectangle or jagged if trimmed (vertical month mode).
 */
public class CalendarFragment extends CalendarElement {

    protected final RadCalendarView owner;
    protected final int id;
    private final ArrayList<CalendarRow> rows;
    /**
     * Virtual position along the x axis used to move the fragment when scrolling without actually moving its element.
     */
    protected int virtualXPosition;
    /**
     * Virtual position along the y axis used to move the fragment when scrolling without actually moving its element.
     */
    protected int virtualYPosition;
    private long displayDate;
    private int rowHeight;
    private int firstFullRowIndex = -1;
    private int lastRowWithCurrentDateCellsIndex;
    private CalendarDisplayMode displayMode;
    private boolean currentFragment;

    /**
     * Creates a new instance of the {@link com.telerik.widget.calendar.CalendarFragment} class.
     *
     * @param owner the current calendar view instance that owns the fragment.
     */
    public CalendarFragment(RadCalendarView owner) {
        super(owner);

        this.owner = owner;
        this.rows = new ArrayList<CalendarRow>();

        this.setBackgroundColor(Color.TRANSPARENT);
        init();
        this.id = this.hashCode();
    }

    /**
     * Gets a value that determines whether this is the current fragment or not.
     *
     * @return <code>true</code> if this is the current fragment, <code>false</code> otherwise.
     */
    public boolean isCurrentFragment() {
        return currentFragment;
    }

    /**
     * Sets a value that determines whether this is the current fragment or not.
     *
     * @param currentFragment the new current state.
     */
    public void setCurrentFragment(boolean currentFragment) {
        this.currentFragment = currentFragment;
    }

    @Override
    protected void onAlphaChanged() {
        for (CalendarRow row : this.rows)
            row.setAlpha(this.alpha);
    }

    /**
     * Gets the current display mode.
     *
     * @return the current display mode.
     */
    public CalendarDisplayMode getDisplayMode() {
        return displayMode;
    }

    /**
     * Sets the current display mode.
     *
     * @param displayMode the new display mode.
     */
    public void setDisplayMode(CalendarDisplayMode displayMode) {
        this.displayMode = displayMode;
    }

    /**
     * Gets the virtual position of the fragment along the x axis. This position is used to translate the canvas at render time, so that the elements of the
     * fragment are not being translated at every frame, but rather this process is virtualized for better performance.
     *
     * @return the current virtual x position.
     */
    public int getVirtualXPosition() {
        return virtualXPosition;
    }

    /**
     * Sets the virtual position of the fragment along the x axis. This position is used to translate the canvas at render time, so that the elements of the
     * fragment are not being translated at every frame, but rather this process is virtualized for better performance.
     *
     * @param virtualXPosition the new virtual x position.
     */
    public void setVirtualXPosition(int virtualXPosition) {
        this.virtualXPosition = virtualXPosition;
    }

    /**
     * Gets the virtual position of the fragment along the y axis. This position is used to translate the canvas at render time, so that the elements of the
     * fragment are not being translated at every frame, but rather this process is virtualized for better performance.
     *
     * @return the current virtual y position.
     */
    public int getVirtualYPosition() {
        return virtualYPosition;
    }

    /**
     * Sets the virtual position of the fragment along the y axis. This position is used to translate the canvas at render time, so that the elements of the
     * fragment are not being translated at every frame, but rather this process is virtualized for better performance.
     *
     * @param virtualYPosition the new virtual y position.
     */
    public void setVirtualYPosition(int virtualYPosition) {
        this.virtualYPosition = virtualYPosition;
    }

    /**
     * Gets the current display date that is represented by this fragment instance.
     *
     * @return the current display date.
     */
    public long getDisplayDate() {
        return displayDate;
    }

    /**
     * Sets the current display date that is represented by this fragment instance.
     *
     * @param displayDate the new display date.
     */
    public void setDisplayDate(long displayDate) {
        this.displayDate = displayDate;
    }

    /**
     * Gets the index of the first row, that holds only cells, which date is from the same range like the current display date for the fragment. It is updated every time that {@link #trim()} is being invoked.
     *
     * @return the current first full row index.
     */
    public int firstFullRowIndex() {
        return firstFullRowIndex;
    }

    /**
     * Gets the index of the last row, that has at least one cell from the same range like the current display date for the fragment. It is updated every time that {@link #trim()} has been invoked.
     *
     * @return the current last
     */
    public int lastRowWithCurrentDateCellsIndex() {
        return lastRowWithCurrentDateCellsIndex;
    }


    @Override
    public void setEnabled(boolean enabled) {
        /*if (this.enabled == enabled)
            return;*/

        super.setEnabled(enabled);
        toggleEnabled(enabled);
    }

    /**
     * Gets the getHeight of a single row contained inside the fragment, where all rows have the same getHeight at all times.
     *
     * @return the current getHeight of a single row contained inside the fragment instance.
     */
    public double rowHeight() {
        return this.rowHeight;
    }

    /**
     * The collection holding the rows for the current fragment instance.
     *
     * @return the current collection of rows.
     */
    public ArrayList<CalendarRow> rows() {
        return this.rows;
    }

    @Override
    protected void updateBackgroundColor() {
        //super.updateBackgroundColor();
        // suspends the fragment from setting a background color.
    }

    @Override
    public void arrange(int left, int top, int right, int bottom) {
        super.arrange(left, top, right, bottom);

        this.virtualXPosition = left;
        this.virtualYPosition = top;
    }

    @Override
    protected void onArrange() {
        this.rowHeight = this.getHeight() / this.rows.size();

        int top = getTop();
        int bottom;
        int offset = (this.getHeight() - (rowHeight * this.rows().size()));
        int remainingSpace = offset;
        offset /= 3;
        remainingSpace -= offset * 3;

        for (int i = 0; i < rows.size(); i++) {
            CalendarRow row = rows.get(i);
            bottom = top + this.rowHeight;
            if (rows.size() > 4 && (i == 0 || i == this.rows.size() - 1 || i == this.rows.size() - 2)) {
                bottom += offset;
            } else if (i == 2 && remainingSpace != 0) {
                bottom += remainingSpace;
            }

            row.arrange(getLeft(), top, getRight(), bottom);
            top = bottom;
        }
    }

    @Override
    public void render(Canvas canvas) {
        this.render(canvas, true);
    }

    /**
     * Extends the render logic, adds the option to state specifically whether the decorations should be
     * drawn during the render phase, or later in the postRender phase. Used for overlapping modes such as Overlap and Stack.
     *
     * @param canvas          the current canvas.
     * @param drawDecorations <code>true</code> will result in drawing the decorations in the render phase, <code>false</code> will halt the operation, so that
     *                        post render can handle it.
     */
    public void render(Canvas canvas, boolean drawDecorations) {
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.translate((float) -(this.getLeft() - this.virtualXPosition),
                (float) -(this.getTop() - this.virtualYPosition));

        drawRows(canvas);

        if (this.owner.getShowGridLines())
            drawGridLines(canvas);

        canvas.restore();

        if (drawDecorations) {
            postRender(canvas);
            this.owner.getCellDecorationsLayer().renderLayer(this.id, canvas);
        }
    }

    @Override
    public void postRender(Canvas canvas) {
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.translate((float) -(this.getLeft() - this.virtualXPosition),
                (float) -(this.getTop() - this.virtualYPosition));

        for (CalendarElement row : this.rows())
            row.postRender(canvas);

        canvas.restore();
    }

    /**
     * Extends the post render logic by adding the option to draw the decorations.
     *
     * @param canvas          the current canvas.
     * @param drawDecorations <<code>true</code> will result in drawing the decorations, <code>false</code> will halt the operation.
     */
    public void postRender(Canvas canvas, boolean drawDecorations) {
        if (drawDecorations) {
            postRender(canvas);
            this.owner.getCellDecorationsLayer().renderLayer(this.id, canvas);
        }
    }

    @Override
    public void translate(int offsetX, int offsetY) {
        this.virtualXPosition += offsetX;
        this.virtualYPosition += offsetY;
    }

    /**
     * Used to trim the cells before and after the ones that are in the same range as the current display date of the fragment. These cells' visibility level will be set to Invisible.
     */
    public void trim() {
        long firstDate = CalendarTools.getFirstDateInMonth(this.displayDate);
        long lastDate = CalendarTools.getLastDateInMonth(this.displayDate);

        this.firstFullRowIndex = -1;
        this.lastRowWithCurrentDateCellsIndex = -1;

        int currentRowIndex = 0;

        long cellDate;
        for (CalendarRow row : this.rows) {

            cellDate = row.getCell(1).getDate();
            boolean isFirstCellFromCurrentFragmentMonth = cellDate >= firstDate && cellDate <= lastDate;
            cellDate = row.getCell(row.cellsCount() - 1).getDate();
            boolean isLastCellFromCurrentFragmentMonth = cellDate >= firstDate && cellDate <= lastDate;

            if (isFirstCellFromCurrentFragmentMonth && isLastCellFromCurrentFragmentMonth) {
                if (this.firstFullRowIndex == -1) {
                    this.firstFullRowIndex = currentRowIndex;
                } else if (currentRowIndex > this.lastRowWithCurrentDateCellsIndex) {
                    this.lastRowWithCurrentDateCellsIndex = currentRowIndex;
                }
                currentRowIndex++;
                continue;
            }

            if (!isFirstCellFromCurrentFragmentMonth) {
                if (row.getCell(0).getVisibility() == ElementVisibility.Visible)
                    row.getCell(0).setVisibility(ElementVisibility.Invisible);

                for (int i = 1, len = row.cellsCount(); i < len; i++) {
                    CalendarCell currentCell = row.getCell(i);
                    cellDate = currentCell.getDate();
                    if (cellDate < firstDate)
                        currentCell.setVisibility(ElementVisibility.Invisible);
                    else
                        break;
                }
            }

            if (!isLastCellFromCurrentFragmentMonth)
                for (int i = row.cellsCount() - 1; i > 0; i--) {
                    CalendarCell currentCell = row.getCell(i);
                    cellDate = currentCell.getDate();
                    if (cellDate > lastDate)
                        currentCell.setVisibility(ElementVisibility.Invisible);
                    else
                        break;
                }

            currentRowIndex++;
        }
    }

    /**
     * Resets the current fragment instance. Used when switching between different display modes. It will use the current display mode of the current calendar view instance.
     */
    public void reset() {
        init();
    }

    /**
     * Gets a cell at certain location having in mind the virtual position of the fragment.
     *
     * @param x the x position of the cell before switching to virtual coordinates.
     * @param y the y position of the cell before switching to virtual coordinates.
     * @return the cell that is located at the given coordinates or <code>null</code> if no cell is found.
     */
    public CalendarCell getCellAtLocation(int x, int y) {
        int virtualX = x - virtualXPosition + getLeft();
        int virtualY = y - virtualYPosition + getTop();

        if (!isPointInsideFragment(virtualX, virtualY))
            return null;

        for (int i = 0, len = this.rows.size(); i < len; i++) {
            CalendarRow row = this.rows().get(i);
            if (row.pointIsInsideElement(virtualX, virtualY)) {
                for (int j = 0, cellsCount = row.cellsCount(); j < cellsCount; j++) {
                    CalendarCell cell = row.getCell(j);
                    if (cell.getCellType() != CalendarCellType.WeekNumber && cell.pointIsInsideElement(virtualX, virtualY)) {
                        if (cell.getVisibility() == ElementVisibility.Visible)
                            return cell;
                        else
                            break;
                    }
                }

                break;
            }
        }

        return null;
    }

    /**
     * Used to update the decorations for the cells of the current fragment instance, so that they are ready for rendering.
     */
    public void updateDecorations() {
        prepareDecorations();
    }

    /**
     * Used to draw the grid lines for the current fragment instance.
     *
     * @param canvas the current canvas.
     */
    protected void drawGridLines(Canvas canvas) {
        CalendarRow row;
        GridLinesLayer renderer = this.owner.getGridLinesLayer();

        row = this.rows().get(0);
        renderer.drawLine((float) row.getLeft(), (float) row.getTop(), (float) row.getRight(), (float) row.getTop(), canvas, row.getAlpha());

        for (int i = 0, len = this.rows.size(); i < len; i++) {
            row = this.rows().get(i);
            renderer.drawLine((float) row.getLeft(), (float) row.getBottom(), (float) row.getRight(), (float) row.getBottom(), canvas, row.getAlpha());

            CalendarCell cell;
            int firstVisibleCellIndex = 0;
            for (int j = 0, cellsCount = row.cellsCount(); j < cellsCount; j++) {
                cell = row.getCell(j);
                if (cell.getVisibility() == ElementVisibility.Visible) {
                    renderer.drawLine((float) cell.getLeft(), (float) cell.getTop(), (float) cell.getLeft(), (float) cell.getBottom(), canvas, cell.getAlpha());
                    firstVisibleCellIndex = j;
                    break;
                }
            }

            for (int j = firstVisibleCellIndex, cellsCount = row.cellsCount(); j < cellsCount; j++) {
                cell = row.getCell(j);
                if (cell.getVisibility() != ElementVisibility.Visible)
                    continue;

                renderer.drawLine((float) cell.getRight(), (float) cell.getTop(), (float) cell.getRight(), (float) cell.getBottom(), canvas, cell.getAlpha());
            }
        }
    }

    /**
     * Used to update the decoration of a given cell.
     *
     * @param cell     the cell to which the decoration update will be performed.
     * @param renderer the decorations renderer that will store the decoration and later on render it.
     */
    protected void updateDecorationForCell(CalendarCell cell, CellDecorationsLayer renderer) {
        renderer.addDecorationForCell(
                this.id,
                (cell.getLeft() + this.virtualXPosition - this.getLeft()),
                (cell.getTop() + this.virtualYPosition - this.getTop()),
                (cell.getRight() + this.virtualXPosition - this.getLeft()),
                (cell.getBottom() + this.virtualYPosition - this.getTop()));
    }

    /**
     * Used to initialize the fragment using the current owner's display mode.
     */
    protected void init() {
        this.rows.clear();
        CalendarAdapter adapter = this.owner.getAdapter();

        if (this.owner.getDisplayMode() == CalendarDisplayMode.Month) {
            for (int week = 0; week < CalendarTools.WEEKS_IN_A_MONTH; week++) {
                CalendarRow row = adapter.generateCalendarRow();

                CalendarCell cell = adapter.getWeekNumberCell();
                cell.setVisibility(ElementVisibility.Gone);

                row.addCell(cell);

                for (int day = 0; day < CalendarTools.DAYS_IN_A_WEEK; day++) {
                    row.addCell(adapter.getDateCell());
                }

                this.rows.add(row);
            }
        } else if (this.owner.getDisplayMode() == CalendarDisplayMode.Week) {
            CalendarRow row = adapter.generateCalendarRow();

            CalendarCell cell = adapter.getWeekNumberCell();
            cell.setVisibility(ElementVisibility.Gone);

            row.addCell(cell);

            for (int day = 0; day < CalendarTools.DAYS_IN_A_WEEK; day++) {
                row.addCell(adapter.getDateCell());
            }

            this.rows.add(row);
        } else if (this.owner.getDisplayMode() == CalendarDisplayMode.Year) {
            int numRows;
            if (this.owner.getResources() != null && this.owner.getResources().getConfiguration() != null) {
                numRows = this.owner.getResources().getConfiguration().orientation == 1 ? 4 : 3;
            } else {
                numRows = 4;
            }

            int numColumns = 12 / numRows;

            for (int i = 0; i < numRows; i++) {
                CalendarRow row = adapter.generateCalendarRow();
                for (int j = 0; j < numColumns; j++) {
                    CalendarMonthCell cell = adapter.getMonthCell();
                    row.addCell(cell);
                }

                this.rows.add(row);
            }
        } else {
            throw new RuntimeException("unsupported display mode");
        }
    }

    /**
     * Used to draw the rows holding the cells.
     *
     * @param canvas the current canvas.
     */
    protected void drawRows(Canvas canvas) {
        for (CalendarElement element : this.rows) {
            element.render(canvas); // TODO optimize (hide invisible rows at some point, maybe after scroll);
        }
    }

    private void prepareDecorations() {
        CellDecorationsLayer decorationsLayer = this.owner.getCellDecorationsLayer();

        CalendarRow row;

        for (int i = 0, len = this.rows.size(); i < len; i++) {
            row = this.rows().get(i);

            CalendarDayCell cell;
            int firstVisibleCellIndex = 0;
            for (int j = 0, cellsCount = row.cellsCount(); j < cellsCount; j++) {
                cell = (CalendarDayCell) row.getCell(j);
                if (cell.getVisibility() == ElementVisibility.Visible) {
                    if (cell.getHasDecoration())
                        updateDecorationForCell(cell, decorationsLayer);

                    firstVisibleCellIndex = j;
                    break;
                }
            }

            for (int j = firstVisibleCellIndex + 1, cellsCount = row.cellsCount(); j < cellsCount; j++) {
                cell = (CalendarDayCell) row.getCell(j);
                if (cell.getVisibility() != ElementVisibility.Visible)
                    break;

                if (cell.getHasDecoration())
                    updateDecorationForCell(cell, decorationsLayer);

            }
        }
    }

    private void toggleEnabled(boolean enabled) {
        int firstCellIndex = getFirstCellIndex();

        if (enabled) {
            if (this.displayMode == CalendarDisplayMode.Year) {
                for (int row = 0, rowsCount = this.rows().size(); row < rowsCount; row++) {
                    CalendarRow currentRow = this.rows().get(row);

                    for (int cell = 0, cellsCount = currentRow.cellsCount(); cell < cellsCount; cell++)
                        currentRow.getCell(cell).setEnabled(true);
                }
            } else {
                setActiveMonthMode(firstCellIndex,
                        CalendarTools.getFirstDateInMonth(this.displayDate),
                        CalendarTools.getLastDateInMonth(this.displayDate));
            }
        } else {
            for (int row = 0, rowsCount = this.rows().size(); row < rowsCount; row++) {
                CalendarRow currentRow = this.rows().get(row);

                for (int cell = 0, cellsCount = currentRow.cellsCount(); cell < cellsCount; cell++) {
                    currentRow.getCell(cell).setEnabled(false);
                }
            }
        }
    }

    private int getFirstCellIndex() {
        return this.displayMode != CalendarDisplayMode.Year ? 1 : 0;
    }

    private void setActiveMonthMode(int firstCellIndex, long firstDate, long lastDate) {
        for (int row = 0, rowsCount = this.rows().size(); row < rowsCount; row++) {
            CalendarRow currentRow = this.rows().get(row);

            if (firstDate != 0 && lastDate != 0) {
                for (int cell = firstCellIndex, cellsCount = currentRow.cellsCount(); cell < cellsCount; cell++) {
                    CalendarCell currentCell = currentRow.getCell(cell);
                    long cellDate = currentCell.getDate();
                    currentCell.setEnabled(cellDate >= firstDate && cellDate <= lastDate);
                }
            } else if (firstDate != 0) {
                for (int cell = firstCellIndex, cellsCount = currentRow.cellsCount(); cell < cellsCount; cell++) {
                    CalendarCell currentCell = currentRow.getCell(cell);
                    long cellDate = currentCell.getDate();
                    currentCell.setEnabled(cellDate >= firstDate);
                }
            } else {
                for (int cell = firstCellIndex, cellsCount = currentRow.cellsCount(); cell < cellsCount; cell++) {
                    CalendarCell currentCell = currentRow.getCell(cell);
                    long cellDate = currentCell.getDate();
                    currentCell.setEnabled(cellDate <= lastDate);
                }
            }

            if (firstCellIndex == 1)
                currentRow.getCell(0).setEnabled(currentRow.getCell(1).isEnabled());
        }
    }

    private boolean isPointInsideFragment(int x, int y) {
        return pointIsInsideElement(x, y);
    }

    /**
     * Clears all content from the fragment.
     */
    public void recycle() {
        this.rows().clear();
    }
}
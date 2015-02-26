package com.commusoft.diary.diarytrials.TelerikSource;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a layer which draws decoration lines over the lines
 * drawn by {@link GridLinesLayer}.
 */
public class CellDecorationsLayer {

    /**
     * The current calendar instance owning the renderer.
     */
    protected final RadCalendarView owner;
    private final Paint paint;
    private final SparseArray<List<LineSegment>> categorizedLineSegments = new SparseArray<List<LineSegment>>();
    private final List<LineSegment> decorationSegments = new ArrayList<LineSegment>();
    private float strokeWidth;
    private int halfStrokeWidth;

    /**
     * Creates an instance of the {@link CellDecorationsLayer} class.
     *
     * @param owner the current calendar instance owning the element.
     */
    public CellDecorationsLayer(RadCalendarView owner) {
        this.owner = owner;

        this.paint = new Paint();
        this.paint.setStyle(Paint.Style.STROKE);
    }

    /**
     * Adds a decoration for a cell using the provided borders.
     *
     * @param left   the left border.
     * @param top    the top border.
     * @param right  the right border.
     * @param bottom the bottom border.
     */
    public void addDecorationForCell(int left, int top, int right, int bottom) {
        this.addDecorationForCell(0, left, top, right, bottom);
    }

    /**
     * Adds a decoration for a cell using the provided borders and an id for the layer to be used when storing the decoration.
     * The layers do matter in the modes involving overlapping of fragments, where the decorations will be called for rendering along with their
     * corresponding fragment by its fragment id, which will be the layer of the decoration.
     *
     * @param layerId the id of the layer that will store the decoration.
     * @param left    the left border.
     * @param top     the top border.
     * @param right   the right border.
     * @param bottom  the bottom border.
     */
    public void addDecorationForCell(int layerId, int left, int top, int right, int bottom) {
        this.changeDecorationForCell(layerId, left, top, right, bottom);
    }

    /**
     * Removes a decoration for a cell using the provided borders and an id for the layer to be used when storing the decoration.
     * The layers do matter in the modes involving overlapping of fragments, where the decorations will be called for rendering along with their
     * corresponding fragment by its fragment id, which will be the layer of the decoration.
     *
     * @param layerId the id of the layer that will store the decoration.
     * @param left    the left border.
     * @param top     the top border.
     * @param right   the right border.
     * @param bottom  the bottom border.
     */
    public void removeDecorationForCell(int layerId, int left, int top, int right, int bottom) {
        this.changeDecorationForCell(layerId, left, top, right, bottom);
    }

    /**
     * Removes the decorations for all cells that are currently decorated.
     */
    public void clearDecorations() {
        this.decorationSegments.clear();
        for (int i = 0, len = this.categorizedLineSegments.size(); i < len; i++) {
            this.categorizedLineSegments.valueAt(i).clear();
        }
    }

    /**
     * Returns the color that is used to render the cell decorations.
     *
     * @return the currently used color
     */
    public int getColor() {
        return this.paint.getColor();
    }

    /**
     * Sets a new color that will be used to render cell decorations.
     *
     * @param value the new color for the cell decorations
     */
    public void setColor(int value) {
        if (this.paint.getColor() != value) {
            this.paint.setColor(value);
            this.owner.invalidate();
        }
    }

    /**
     * Returns the getWidth of the cell decoration lines.
     *
     * @return the current cell decoration lines getWidth
     */
    public float getStrokeWidth() {
        return this.strokeWidth;
    }

    /**
     * Sets a new value for the getWidth of the cell decoration lines.
     *
     * @param value the new getWidth for the cell decoration lines
     */
    public void setStrokeWidth(float value) {
        if (value <= 0)
            throw new IllegalArgumentException("value must be positive number");

        if (this.strokeWidth != value) {
            this.strokeWidth = value;
            this.halfStrokeWidth = (int) (value / 2);
            this.paint.setStrokeWidth(value);
            this.owner.invalidate();
        }
    }

    /**
     * Used to render the current decorations.
     *
     * @param canvas the current canvas.
     */
    public void render(Canvas canvas) {
        renderLayer(0, canvas);
    }

    /**
     * Used to render the current decorations for the specified layer.
     *
     * @param layerId the layer of the decorations.
     * @param canvas  the current canvas.
     */
    public void renderLayer(int layerId, Canvas canvas) {
        List<LineSegment> segmentsForLayer = this.categorizedLineSegments.get(layerId);

        if (segmentsForLayer == null)
            return;

        for (LineSegment segment : segmentsForLayer)
            canvas.drawLine(segment.startX, segment.startY, segment.endX, segment.endY, this.paint);
    }

    private void changeDecorationForCell(int layerId, int left, int top, int right, int bottom) {
        LineSegment leftBorder = new LineSegment(left, top - this.halfStrokeWidth, left, bottom + this.halfStrokeWidth);
        LineSegment topBorder = new LineSegment(left, top, right, top);
        LineSegment rightBorder = new LineSegment(right, top - halfStrokeWidth, right, bottom + this.halfStrokeWidth);
        LineSegment bottomBorder = new LineSegment(left, bottom, right, bottom);

        handleSegmentDecorationChange(layerId, leftBorder);
        handleSegmentDecorationChange(layerId, topBorder);
        handleSegmentDecorationChange(layerId, rightBorder);
        handleSegmentDecorationChange(layerId, bottomBorder);
    }

    private void handleSegmentDecorationChange(int id, LineSegment segment) {
        List<LineSegment> lineSegmentsForId = this.categorizedLineSegments.get(id);
        if (lineSegmentsForId == null) {
            lineSegmentsForId = new ArrayList<LineSegment>();
            this.categorizedLineSegments.put(id, lineSegmentsForId);
        }

        if (this.decorationSegments.contains(segment)) {
            this.decorationSegments.remove(segment);
            if ((this.owner.getScrollMode() != ScrollMode.Overlap && this.owner.getScrollMode() != ScrollMode.Stack))
                removeSegmentFromAllLayers(segment);
            else {
                removeSegmentForLayer(segment, id);
            }
        } else {
            this.decorationSegments.add(segment);
            lineSegmentsForId.add(segment);
        }
    }

    private void removeSegmentForLayer(LineSegment segment, int id) {
        List<LineSegment> layer = this.categorizedLineSegments.get(id);
        if (layer != null)
            layer.remove(segment);
    }

    private void removeSegmentFromAllLayers(LineSegment segment) {
        for (int i = 0, len = this.categorizedLineSegments.size(); i < len; i++) {
            this.categorizedLineSegments.valueAt(i).remove(segment);
        }
    }

    private class LineSegment {

        private final int startX;
        private final int endX;
        private final int startY;
        private final int endY;

        LineSegment(int startX, int startY, int endX, int endY) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null)
                return false;
            if (o == this)
                return true;
            if (!(o instanceof LineSegment))
                return false;

            LineSegment second = (LineSegment) o;
            return startX == second.startX &&
                    startY == second.startY &&
                    endX == second.endX &&
                    endY == second.endY;
        }

        @Override
        public int hashCode() {
            String result = String.format("%d.%d.%d.%d", startX, startY, endX, endY);
            return result.hashCode();
        }
    }
}

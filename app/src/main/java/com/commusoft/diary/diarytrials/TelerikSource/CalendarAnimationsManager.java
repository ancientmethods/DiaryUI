package com.commusoft.diary.diarytrials.TelerikSource;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.telerik.android.common.animations.AnimationEasingHelper;

/**
 * Manager responsible for all animations.
 */
public class CalendarAnimationsManager {

    private static final int DEFAULT_MIN_ALPHA = 51;
    private static final int DEFAULT_MAX_ALPHA = 255;
    private static final double DEFAULT_SCALE = 1.0;
    private static final long RESET_TIME = 0;
    private static final int DEFAULT_ANIMATION_DURATION = 300;
    private static final int DEFAULT_DATE_CHANGE_ANIMATION_DURATION = 500;

    /**
     * The current calendar instance owning this manager.
     */
    protected final RadCalendarView owner;
    /**
     * The current scroll manager.
     */
    protected CalendarScrollManager scrollManager;
    /**
     * The speed at which the calendar will scroll during a fling gesture.
     */
    protected double flingSpeed = .01f;
    /**
     * The speed at which the calendar will snap.
     */
    protected double snapSpeed = .07f;
    /**
     * The distance at which the fragments will snap at every snap frame.
     */
    protected int currentSnapDistance;
    /**
     * The frames needed for snapping the fragments.
     */
    protected int currentSnapFramesCount;
    /**
     * The current snap frame count.
     */
    protected int currentSnapFrameCount;
    /**
     * Holds a value stating whether the active date should be changed. <code>true</code> if the date should be changed, <code>false</code> otherwise.
     */
    protected boolean activeDateRefreshRequested;
    /**
     * The current fling velocity along the x axis to be handled.
     */
    protected double flingVelocityX;
    /**
     * The current fling velocity along the y axis to be handled.
     */
    protected double flingVelocityY;
    /**
     * The current offset for snapping the main fragment along the x axis.
     */
    protected int currentSnapOffsetX;
    /**
     * The current offset for snapping the main fragment along the y axis.
     */
    protected int currentSnapOffsetY;
    /**
     * The minimum fling speed after which the fling should stop.
     */
    protected int minFlingDistance = 7;

    private int distanceX;
    private int distanceY;

    private boolean animationInProcess;
    private double friction = 7;
    private CalendarFragment monthFragment;
    private CalendarFragment yearFragment;

    private int changeAlpha;
    private int currentAlpha;
    private double currentScaleX;
    private double currentScaleY;

    private double currentX;
    private double currentY;

    private long currentTime;
    private long startTime;
    private float changeX;
    private float changeY;
    private double changeScaleX;
    private double changeScaleY;

    private int startAlpha;
    private int startX;
    private int startY;
    private double startScaleX;
    private double startScaleY;
    private int scrollVelocityX;
    private int scrollVelocityY;
    private int scrollStep;

    /**
     * Creates a new instance of the {@link CalendarAnimationsManager} class.
     *
     * @param owner the calendar instance owning this manager.
     */
    public CalendarAnimationsManager(RadCalendarView owner) {
        this.owner = owner;
    }

    CalendarScrollManager getScrollManager() {
        return this.scrollManager;
    }

    void setScrollManager(CalendarScrollManager scrollManager) {
        this.scrollManager = scrollManager;
    }

    /**
     * Gets the friction to be applied when flinging. Causes the fling to decrease additionally to its natural decreasing speed.
     *
     * @return the current friction.
     */
    public double getFriction() {
        return friction;
    }

    /**
     * Sets the friction to be applied when flinging. Causes the fling to decrease additionally to its natural decreasing speed.
     *
     * @param friction the new friction.
     */
    public void setFriction(double friction) {
        this.friction = friction;
    }

    /**
     * Gets the speed at which the calendar will scroll when fling gesture is invoked.
     *
     * @return the current fling speed.
     */
    public double getFlingSpeed() {
        return this.flingSpeed;
    }

    /**
     * Sets the speed at which the calendar will scroll when fling gesture is invoked.
     *
     * @param speed the new fling speed.
     */
    public void setFlingSpeed(double speed) {
        if (speed <= 0)
            throw new IllegalArgumentException("speed must be greater than 0");

        this.flingSpeed = speed;
    }

    /**
     * Gets the speed at which the fragments will snap when snapping is invoked.
     *
     * @return the current snap speed.
     */
    public double getSnapSpeed() {
        return this.snapSpeed;
    }

    /**
     * Sets the speed at which the fragments will snap when snapping is invoked.
     *
     * @param speed the new snap speed.
     */
    public void setSnapSpeed(float speed) {
        if (speed <= 0)
            throw new IllegalArgumentException("speed must be greater than 0");

        this.snapSpeed = speed;
    }

    /**
     * Gets the minimum distance at which the calendar will scroll when fling is invoked. If the distance is lesser
     * than the minimum speed the fling will stop.
     *
     * @return the current minimum fling distance.
     */
    public int getMinFlingDistance() {
        return this.minFlingDistance;
    }

    /**
     * Sets the minimum distance at which the calendar will scroll when fling is invoked. If the distance is lesser
     * than the minimum speed the fling will stop.
     *
     * @param minFlingDistance the new minimum fling distance.
     */
    public void setMinFlingDistance(int minFlingDistance) {
        if (minFlingDistance <= 0)
            throw new IllegalArgumentException("minFlingDistance must be greater than 0");

        this.minFlingDistance = minFlingDistance;
    }

    /**
     * Handles the invalidation of the calendar view instance. It is being invoked at the end of the invalidation.
     * It is responsible for the fling and snap behaviors.
     */
    public void onInvalidate() {
        // Animate date change
        if (this.scrollVelocityX != 0 || this.scrollVelocityY != 0) {
            this.currentTime = System.currentTimeMillis() - startTime;
            if (this.scrollVelocityX != 0) {

                this.scrollStep = (int) (AnimationEasingHelper.quadraticEaseOut(currentTime, 0, scrollVelocityX, DEFAULT_DATE_CHANGE_ANIMATION_DURATION) - currentX);
                currentX += scrollStep;
                this.scrollManager.scroll(this.scrollStep, 0);

                if (this.currentTime > DEFAULT_DATE_CHANGE_ANIMATION_DURATION) {
                    this.scrollManager.scroll(scrollManager.currentSnapOffsetX(), 0); // in case the animation was interrupted by time limit.
                    this.scrollVelocityX = 0;
                    currentX = 0;

                    if (this.owner.getScrollMode() == ScrollMode.Overlap || this.owner.getScrollMode() == ScrollMode.Stack)
                        this.scrollManager.onSnapComplete();

                    this.requestActiveDateChange();
                }
            } else {
                this.scrollStep = (int) (AnimationEasingHelper.quadraticEaseOut(currentTime, 0, scrollVelocityY, DEFAULT_DATE_CHANGE_ANIMATION_DURATION) - currentY);
                currentY += scrollStep;
                this.scrollManager.scroll(0, this.scrollStep);

                if (this.currentTime > DEFAULT_DATE_CHANGE_ANIMATION_DURATION) {
                    this.scrollManager.scroll(0, scrollManager.currentSnapOffsetY()); // in case the animation was interrupted by time limit.
                    this.scrollVelocityY = 0;
                    currentY = 0;

                    if (this.owner.getScrollMode() == ScrollMode.Overlap || this.owner.getScrollMode() == ScrollMode.Stack)
                        this.scrollManager.onSnapComplete();

                    this.requestActiveDateChange();
                }
            }

            this.owner.invalidate();
            return;
        }

        // Fling
        if (this.flingVelocityX != 0 || this.flingVelocityY != 0) {
            distanceX = (int) (this.flingVelocityX * this.flingSpeed);
            distanceY = (int) (this.flingVelocityY * this.flingSpeed);

            if (Math.abs(distanceX) < this.minFlingDistance && Math.abs(distanceY) < this.minFlingDistance) {
                this.flingVelocityX = 0;
                this.flingVelocityY = 0;
                onFlingComplete();
                return;
            }

            this.owner.beginUpdate();
            if (this.scrollManager.scroll(distanceX, distanceY)) {
                if (this.flingVelocityX > 0) {
                    this.flingVelocityX -= distanceX * friction;
                    if (this.flingVelocityX < 0) {
                        this.flingVelocityX = 0;
                    }
                } else {
                    this.flingVelocityX += (distanceX * -1) * friction;
                    if (this.flingVelocityX > 0) {
                        this.flingVelocityX = 0;
                    }
                }

                if (this.flingVelocityY > 0) {
                    this.flingVelocityY -= distanceY * friction;
                    if (this.flingVelocityY < 0) {
                        this.flingVelocityY = 0;
                    }
                } else {
                    this.flingVelocityY += (distanceY * -1) * friction;
                    if (this.flingVelocityY > 0) {
                        this.flingVelocityY = 0;
                    }
                }
            } else {
                reset();
            }

            this.owner.endUpdate();
            return;
        } else if (this.activeDateRefreshRequested) {
            refreshActiveDate();
            this.owner.invalidate();
            return;
        }

        // Snap
        if (this.currentSnapOffsetX != 0 || this.currentSnapOffsetY != 0) {
            if (currentSnapFrameCount == currentSnapFramesCount) {
                this.currentSnapOffsetX = 0;
                this.currentSnapOffsetY = 0;
                this.currentSnapFrameCount = 0;
                this.currentSnapFramesCount = 0;
                this.scrollManager.scroll(this.scrollManager.currentSnapOffsetX(), this.scrollManager.currentSnapOffsetY()); // just in case
                onSnapComplete();
            } else {
                if (this.currentSnapOffsetX != 0) {
                    this.scrollManager.scroll(this.currentSnapDistance, 0);
                } else {
                    this.scrollManager.scroll(0, this.currentSnapDistance);
                }

                this.currentSnapFrameCount++;
            }

            this.owner.invalidate();
        }
    }

    void refreshActiveDate() {
        this.scrollManager.setActiveDate(this.owner.getDisplayDate());
        this.activeDateRefreshRequested = false;
    }

    /**
     * Animates the transition from the current date to the next one.
     */
    public void animateToNextDate() {
        animateToOtherDate(true);
    }

    /**
     * Animates the transition from the current date to the previous one.
     */
    public void animateToPreviousDate() {
        animateToOtherDate(false);
    }

    private void animateToOtherDate(boolean increase) {
        if (this.scrollVelocityX != 0 || this.scrollVelocityY != 0)
            return;

        if (this.scrollManager.scrollShouldBeHorizontal()) {
            this.scrollVelocityX = increase ? -this.scrollManager.getWidth() : this.scrollManager.getWidth();
            this.startX = 0;
            this.currentX = 0;
        } else {
            this.startY = 0;
            if ((this.owner.getScrollMode() != ScrollMode.Overlap && this.owner.getScrollMode() != ScrollMode.Stack) && this.owner.getDisplayMode() == CalendarDisplayMode.Month) {
                int height;
                if (increase)
                    height = this.scrollManager.getTop() - this.scrollManager.nextFragment().getVirtualYPosition();
                else
                    height = this.scrollManager.getTop() - this.scrollManager.previousFragment.getVirtualYPosition();

                this.scrollVelocityY = height;
            } else {
                this.scrollVelocityY = increase ? -this.scrollManager.getHeight() : this.scrollManager.getHeight();
            }

            this.currentY = 0;
        }

        this.startTime = System.currentTimeMillis();
        this.currentTime = RESET_TIME;

        this.owner.invalidate();
    }

    /**
     * Invoked when the fling is done.
     */
    protected void onFlingComplete() {
        if ((this.owner.getScrollMode() == ScrollMode.Sticky || this.owner.getScrollMode() == ScrollMode.Combo) || ((this.owner.getDisplayMode() == CalendarDisplayMode.Month || this.owner.getDisplayMode() == CalendarDisplayMode.Week) && this.scrollManager.isHorizontalScroll())) {
            this.owner.beginUpdate();
            snapFragments();
        }

        this.scrollManager.setActiveDate(this.owner.getDisplayDate());
        this.owner.endUpdate();
    }

    /**
     * Invoked when the snapping is done.
     */
    protected void onSnapComplete() {
        this.scrollManager.onSnapComplete();
    }

    /**
     * Sets the current velocity for both the x and y axes.
     *
     * @param velocityX the velocity along the x axis.
     * @param velocityY the velocity along the y axis.
     */
    public void setVelocity(double velocityX, double velocityY) {
        this.flingVelocityX = velocityX;
        this.flingVelocityY = velocityY;
    }

    /**
     * Used to reset the manager and prevent any current animations.
     */
    public void reset() {
        this.currentSnapOffsetX = 0;
        this.currentSnapOffsetY = 0;
        this.currentSnapFramesCount = 0;
        this.currentSnapFrameCount = 0;
        this.flingVelocityX = 0;
        this.flingVelocityY = 0;
    }

    void requestActiveDateChange() {
        this.activeDateRefreshRequested = true;
    }

    /**
     * Used to snap the fragments according to the current scroll mode.
     */
    protected void snapFragments() {
        this.currentSnapOffsetX = this.scrollManager.currentSnapOffsetX();
        this.currentSnapOffsetY = this.scrollManager.currentSnapOffsetY();

        if (this.currentSnapOffsetX != 0) {
            this.currentSnapDistance = (int) (this.currentSnapOffsetX * this.snapSpeed);
            if (currentSnapDistance != 0)
                this.currentSnapFramesCount = (this.currentSnapOffsetX / this.currentSnapDistance);
        } else if (this.currentSnapOffsetY != 0) {
            this.currentSnapDistance = (int) (this.currentSnapOffsetY * this.snapSpeed);
            if (currentSnapDistance != 0)
                this.currentSnapFramesCount = (this.currentSnapOffsetY / this.currentSnapDistance);
            else
                currentSnapFramesCount = 0;
        } else {
            this.scrollManager.onSnapComplete();
        }
    }

    /**
     * States whether an animation is currently being processed by the manager.
     *
     * @return <code>true</code> if an animation is currently being processed, <code>false</code> otherwise.
     */
    public boolean animationInProcess() {
        return this.animationInProcess;
    }

    /**
     * Initiates an animation toggling between month and year modes.
     *
     * @param monthFragment   the month fragment to be animated.
     * @param yearFragment    the year fragment to be animated.
     * @param monthCellBounds the bounds of the month cell.
     */
    public void beginAnimation(CalendarFragment monthFragment, CalendarFragment yearFragment, Rect monthCellBounds) {
        this.animationInProcess = true;

        this.monthFragment = monthFragment;
        this.yearFragment = yearFragment;

        if (this.owner.getDisplayMode() == CalendarDisplayMode.Month) {

            this.startAlpha = DEFAULT_MIN_ALPHA;
            this.changeAlpha = DEFAULT_MAX_ALPHA - startAlpha;

            this.startX = monthFragment.getLeft() + monthCellBounds.left;
            this.startY = monthFragment.getTop() + monthCellBounds.top;
            this.changeX = this.scrollManager.getLeft() - this.startX;
            this.changeY = this.scrollManager.getTop() - this.startY;

            this.startScaleX = monthCellBounds.width() / (double) this.scrollManager.getWidth();
            this.startScaleY = monthCellBounds.height() / (double) this.scrollManager.getHeight();
            this.changeScaleX = DEFAULT_SCALE - this.startScaleX;
            this.changeScaleY = DEFAULT_SCALE - this.startScaleY;
        } else {
            this.startAlpha = DEFAULT_MAX_ALPHA;
            this.changeAlpha = DEFAULT_MIN_ALPHA - this.startAlpha;

            this.startX = this.monthFragment.getLeft();
            this.startY = this.monthFragment.getTop();
            this.changeX = monthFragment.getLeft() + monthCellBounds.left - this.startX;
            this.changeY = monthFragment.getTop() + monthCellBounds.top - this.startY;

            this.startScaleX = DEFAULT_SCALE;
            this.startScaleY = DEFAULT_SCALE;
            this.changeScaleX = (monthCellBounds.width() / (double) this.scrollManager.getWidth()) - this.startScaleX;
            this.changeScaleY = (monthCellBounds.height() / (double) this.scrollManager.getHeight()) - this.startScaleY;

        }

        this.currentAlpha = this.startAlpha;
        this.currentScaleX = this.startX;
        this.currentY = this.startY;
        this.currentX = this.startX;
        this.currentScaleX = this.startScaleX;
        this.currentScaleY = this.startScaleY;

        this.startTime = System.currentTimeMillis();
        this.currentTime = RESET_TIME;

        this.monthFragment.setAlpha(this.currentAlpha);

        this.owner.invalidate();
    }

    void animate(Canvas canvas) {
        this.yearFragment.render(canvas);

        canvas.save();
        canvas.translate((float) (this.currentX - this.monthFragment.getLeft()), (float) (this.currentY - this.monthFragment.getTop()));
        canvas.scale((float) this.currentScaleX, (float) this.currentScaleY);
        this.monthFragment.render(canvas);
        canvas.restore();

        updateAnimation();

        if (this.currentTime > DEFAULT_ANIMATION_DURATION) {
            this.animationInProcess = false;
            this.yearFragment.recycle();
            this.monthFragment.recycle();
            this.yearFragment = null;
            this.monthFragment = null;
        }

        this.owner.invalidate();
    }

    private double ease(double startValue, double changeValue) {
        return AnimationEasingHelper.quadraticEaseOut(this.currentTime, startValue, changeValue, DEFAULT_ANIMATION_DURATION);
    }

    private void updateAnimation() {
        this.currentAlpha = (int) ease(this.startAlpha, this.changeAlpha);
        this.monthFragment.setAlpha(this.currentAlpha);

        this.currentX = ease(this.startX, this.changeX);
        this.currentY = ease(this.startY, this.changeY);

        this.currentScaleX = (float) ease(this.startScaleX, this.changeScaleX);
        this.currentScaleY = (float) ease(this.startScaleY, this.changeScaleY);

        this.currentTime = System.currentTimeMillis() - this.startTime;
    }
}

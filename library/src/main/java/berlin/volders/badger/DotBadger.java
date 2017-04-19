package berlin.volders.badger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;

/**
 * Implementation of {@link DotBadger} Show a little dot.
 */
public class DotBadger extends BadgeDrawable {
    private boolean isVisible = false;
    private boolean paintPreparationNeeded = true;

    private static final boolean WHOLO = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    private static final boolean WMATE = Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
    private static final float MAGIC_TEXT_SCALE_FACTOR = 0.6f;

    private final BadgeShape shape;

    private final Paint badgePaint = new Paint();

    /**
     * @param context to read themed colors from
     * @param shape   of the badge
     */
    public DotBadger(@NonNull Context context, @NonNull BadgeShape shape) {
        this(shape, badgeShapeColor(context));
    }

    /**
     * @param shape      of the badge
     * @param badgeColor to paint the badge shape with
     */
    protected DotBadger(@NonNull BadgeShape shape, @ColorInt int badgeColor) {
        this.shape = shape;
        badgePaint.setColor(badgeColor);

    }

    @Override
    @SuppressLint("NewApi")
    public void draw(@NonNull Canvas canvas) {
        if (!isVisible) {
            return;
        }
        if (paintPreparationNeeded) {
            paintPreparationNeeded = false;
            onPrepareBadgePaint(badgePaint);
        }
        shape.draw(canvas, getBounds(), badgePaint, getLayoutDirection());
    }

    @Override
    public int getLayoutDirection() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return super.getLayoutDirection();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return View.LAYOUT_DIRECTION_LTR;
        }
        //noinspection WrongConstant
        return 0; // LAYOUT_DIRECTION_LTR

    }

    @Override
    public boolean onLayoutDirectionChanged(int layoutDirection) {
        invalidateSelf();
        return true;
    }

    @Override
    @SuppressLint("NewApi")
    public void setAlpha(int alpha) {
        if (getAlpha() != alpha) {
            badgePaint.setAlpha(alpha);
            super.setAlpha(alpha);
        }
    }

    @Override
    @SuppressLint("NewApi")
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        if (getColorFilter() != colorFilter) {
            badgePaint.setColorFilter(colorFilter);
            super.setColorFilter(colorFilter);
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    static int badgeShapeColor(Context context) {
        Resources.Theme theme = context.getTheme();
        TypedValue typedValue = new TypedValue();
        if (theme.resolveAttribute(R.attr.badgeShapeColor, typedValue, true)) {
            return typedValue.data;
        }
        if (theme.resolveAttribute(R.attr.colorAccent, typedValue, true)) {
            return typedValue.data;
        }
        if (WHOLO && theme.resolveAttribute(android.R.attr.colorAccent, typedValue, true)) {
            return typedValue.data;
        }
        if (WMATE) {
            return context.getResources().getColor(R.color.badgeShapeColor);
        }
        return context.getColor(R.color.badgeShapeColor);
    }

    /**
     * @param paint to prepare for drawing the badge
     */
    protected void onPrepareBadgePaint(@NonNull Paint paint) {
        paint.setAntiAlias(true);
    }

    /**
     * @return visible state
     */
    public boolean getVisible() {
        return isVisible;
    }

    /**
     * show dot
     */
    public void show() {
        isVisible = true;
        invalidateSelf();
    }

    /**
     * hide dot
     */
    public void hide() {
        isVisible = false;
        invalidateSelf();
    }

    /**
     * {@link BadgeDrawable.Factory} extension for creating {@code DotBadger} instances.
     */
    public static  class Factory implements BadgeDrawable.Factory<DotBadger> {

        /**
         * The badge shape
         */
        @NonNull
        protected final BadgeShape shape;
        /**
         * The badge color
         */
        @ColorInt
        protected final int badgeColor;

        /**
         * @param context to read themed colors from
         * @param shape   of the badge
         */
        public Factory(@NonNull Context context, @NonNull BadgeShape shape) {
            this(shape, badgeShapeColor(context));
        }

        /**
         * @param shape      of the badge
         * @param badgeColor to paint the badge shape with
         */
        public Factory(@NonNull BadgeShape shape, @ColorInt int badgeColor) {
            this.shape = shape;
            this.badgeColor = badgeColor;
        }

        @Override
        public DotBadger createBadge() {
            return new DotBadger(shape,badgeColor);
        }
    }
}

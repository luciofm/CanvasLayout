package mobi.largemind.canvaslayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class GroupedEntriesGridLayout extends ViewGroup {

    private static final String TAG = "GroupedEntriesGrid";

    private int spacing = 0;
    private int maxColumns;
    private int minColumns;
    private double heightRatio = 0.83d;
    private static final boolean VERBOSE = false;

    public GroupedEntriesGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.GroupedEntriesGridLayout);
        try {
            spacing = a.getDimensionPixelSize(
                    R.styleable.GroupedEntriesGridLayout_spacing, 0);
            maxColumns = a.getInteger(R.styleable.GroupedEntriesGridLayout_maxColumns,
                    getResources().getInteger(R.integer.max_grouped_columns));
            minColumns = a.getInteger(R.styleable.GroupedEntriesGridLayout_minColumns,
                    getResources().getInteger(R.integer.min_grouped_columns));
        } finally {
            a.recycle();
        }
    }

    public GroupedEntriesGridLayout(Context context) {
        super(context);
        maxColumns = getResources().getInteger(R.integer.max_grouped_columns);
        minColumns = getResources().getInteger(R.integer.min_grouped_columns);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int padw = getPaddingLeft() + getPaddingRight();

        int columns = maxColumns;
        int numViews = getVisibleChildCount();
        if (numViews < maxColumns) {
            columns = Math.max(numViews, minColumns);
        }

        int entryWidth = (widthSize - padw - (columns + 1) * spacing) / columns;
        int entryHeight = (int) (entryWidth * heightRatio);

        int currentX = getPaddingLeft();
        int currentY = getPaddingTop();
        int currentColumn = 1;

        int entrySpecWidth = MeasureSpec.makeMeasureSpec(entryWidth, MeasureSpec.EXACTLY);
        int entrySpecHeight = MeasureSpec.makeMeasureSpec(entryHeight, MeasureSpec.EXACTLY);

        final int count = getVisibleChildCount();

        int initialColumn = currentColumn;
        int initialX = currentX;

        /* Measure and pre-layout all Entries */
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            lp.width = entryWidth;
            lp.height = entryHeight;
            measureChild(child, entrySpecWidth, entrySpecHeight);

            if (VERBOSE)
                Log.d(TAG, String.format("%s VIEW %d, currentX %d, currentY %d", getHash(), i,
                        currentX, currentY));

            if (currentColumn++ > columns) {
                if (VERBOSE)
                    Log.d(TAG, String.format("%s new line, currentColumn %d, columns %d", getHash(),
                            currentColumn, columns));
                currentColumn = initialColumn + 1;
                currentX = initialX;
                currentY += child.getMeasuredHeight() + spacing;
            }

            lp.x = currentX + spacing;
            lp.y = currentY + spacing;
            currentX += child.getMeasuredWidth() + spacing;
            if (VERBOSE)
                Log.d(TAG, String.format("%s VIEW %d, x %d - y %d, SIZE %dx%d", getHash(), i, lp.x,
                        lp.y, child.getMeasuredWidth(), child.getMeasuredHeight()));
        }

        int width = currentX + spacing + getPaddingRight();
        int height = currentY + entryHeight + (spacing * 2) + getPaddingBottom();

        if (VERBOSE)
            Log.d(TAG, String.format("%s size %dx%d", getHash(), width, height));

        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();

            if (VERBOSE)
                Log.d(TAG, String.format("%s onLayout(%d), %d-%d x %d-%d", getHash(), i,
                        lp.x, lp.x + child.getMeasuredWidth(),
                        lp.y, lp.y + child.getMeasuredHeight()));

            child.layout(lp.x, lp.y, lp.x + child.getMeasuredWidth(),
                    lp.y + child.getMeasuredHeight());
        }
    }

    public double getHeightRatio() {
        return heightRatio;
    }

    public void setHeightRatio(double heightRatio) {
        this.heightRatio = heightRatio;
        invalidate();
    }

    public int getMaxColumns() {
        return maxColumns;
    }

    public void setMaxColumns(int maxColumns) {
        this.maxColumns = maxColumns;
        invalidate();
    }

    public int getMinColumns() {
        return minColumns;
    }

    public void setMinColumns(int minColumns) {
        this.minColumns = minColumns;
        invalidate();
    }

    public void setColumns(int minColumns, int maxColumns) {
        this.minColumns = minColumns;
        this.maxColumns = maxColumns;
        invalidate();
    }

    private int getVisibleChildCount() {
        int count = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (v.getVisibility() == VISIBLE)
                count++;
        }

        return count;
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p.width, p.height);
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {

        public int x;
        public int y;

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
    }

    private String getHash() {
        return Integer.toHexString(hashCode());
    }
}

package org.houxg.flexlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FlexLayout extends ViewGroup {

    private static final int DEFAULT_CAPACITY = 32;

    private List<Integer> mRowHeights = new ArrayList<>(DEFAULT_CAPACITY);
    private List<Integer> mRowWidths = new ArrayList<>(DEFAULT_CAPACITY);
    private List<Integer> mColumnCounts = new ArrayList<>(DEFAULT_CAPACITY);
    private int mTotalHeight;

    private Mode mJustifyContentMode = Mode.START;
    private Mode mAlignItemMode = Mode.START;
    private Mode mAlignContentMode = Mode.START;
    private int mColGap = 0;
    private int mRowGap = 0;
    private RowSpec mRowSpec = new RowSpec();

    public FlexLayout(Context context) {
        super(context);
    }

    public FlexLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlexLayout);
        mJustifyContentMode = Mode.valueOf(typedArray.getInt(R.styleable.FlexLayout_justify_content, Mode.START.mVal));
        mAlignContentMode = Mode.valueOf(typedArray.getInt(R.styleable.FlexLayout_align_content, Mode.START.mVal));
        mAlignItemMode = Mode.valueOf(typedArray.getInt(R.styleable.FlexLayout_align_item, Mode.START.mVal));
        mColGap = (int) typedArray.getDimension(R.styleable.FlexLayout_col_gap, 0);
        mRowGap = (int) typedArray.getDimension(R.styleable.FlexLayout_row_gap, 0);
        typedArray.recycle();
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        mRowWidths.clear();
        mRowHeights.clear();
        mColumnCounts.clear();
        mRowSpec.reset();
        mRowSpec.setAvailableWidth(widthSize - getPaddingRight() + getPaddingLeft());
        int rowGap = getRowGapBaseOnCurrentSatate();
        int colGap = getColGapBaseOnCurrentSatate();

        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            child.measure(getChildMeasureSpec(params.width, widthMeasureSpec),
                    getChildMeasureSpec(params.height, heightMeasureSpec));
        }

        int rowCount = 0;
        mTotalHeight = getPaddingTop() + getPaddingBottom();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            int childWid = child.getMeasuredWidth() + params.leftMargin + params.rightMargin;
            int childHei = child.getMeasuredHeight() + params.topMargin + params.bottomMargin;
            if (!mRowSpec.addItem(childWid, childHei, colGap)) {
                mRowWidths.add(mRowSpec.getRowWidth());
                mRowHeights.add(mRowSpec.getRowHeight());
                mColumnCounts.add(mRowSpec.getColumnCount());
                mTotalHeight += mRowSpec.getRowHeight() + rowGap;
                mRowSpec.reset();
                rowCount++;
                mRowSpec.forceAddItem(childWid, childHei);
            }
            params.rowId = rowCount;
        }
        if (mRowWidths.size() != rowCount + 1) {
            mRowWidths.add(mRowSpec.getRowWidth());
            mRowHeights.add(mRowSpec.getRowHeight());
            mColumnCounts.add(mRowSpec.getColumnCount());
            mTotalHeight += mRowSpec.getRowHeight();
        }
        int height;
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = mTotalHeight;
        }
        setMeasuredDimension(widthSize, height);
    }

    private int getChildMeasureSpec(int childParam, int parentSpec) {
        int parentSize = MeasureSpec.getSize(parentSpec) - getPaddingLeft() - getPaddingRight();
        int resultMode;
        int resultSize;
        if (childParam == ViewGroup.LayoutParams.WRAP_CONTENT) {
            resultMode = MeasureSpec.AT_MOST;
            resultSize = parentSize;
        } else if (childParam == ViewGroup.LayoutParams.MATCH_PARENT) {
            resultMode = MeasureSpec.EXACTLY;
            resultSize = parentSize;
        } else {
            resultMode = MeasureSpec.EXACTLY;
            if (childParam < 0 || parentSize < childParam) {
                resultSize = parentSize;
            } else {
                resultSize = childParam;
            }
        }
        return MeasureSpec.makeMeasureSpec(resultSize, resultMode);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int justifyDiv = 0;
        int alignDiv = 0;

        int count = getChildCount();
        if (count <= 0) {
            return;
        }
        int baseTop = getPaddingTop();
        int baseLeft = getPaddingLeft();
        int nowRow = -1;
        boolean isHeightEnough = getMeasuredHeight() > mTotalHeight;
        LayoutParams lastViewParams = (LayoutParams) getChildAt(getChildCount() - 1).getLayoutParams();
        int rowCount = lastViewParams.rowId + 1;
        int rowGap = getRowGapBaseOnCurrentSatate();
        int colGap = getColGapBaseOnCurrentSatate();
        switch (mAlignContentMode) {
            case CENTER:
                if (isHeightEnough) {
                    baseTop = (getMeasuredHeight() - mTotalHeight) >> 1;
                }
                break;
            case END:
                if (isHeightEnough) {
                    baseTop = getMeasuredHeight() - mTotalHeight;
                }
                break;
            case SPACE_BETWEEN:
                if (isHeightEnough && rowCount > 1) {
                    alignDiv = (getMeasuredHeight() - mTotalHeight) / (rowCount - 1);
                }
                break;
            case SPACE_AROUND:
                if (isHeightEnough) {
                    alignDiv = (getMeasuredHeight() - mTotalHeight) / rowCount;
                    baseTop = alignDiv >> 1;
                }
                break;
        }
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            int childHei = child.getMeasuredHeight();
            int childWid = child.getMeasuredWidth();
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            if (nowRow != params.rowId) {
                if (nowRow >= 0) {
                    baseTop += mRowHeights.get(nowRow) + alignDiv + rowGap;
                }
                nowRow = params.rowId;
                justifyDiv = 0;
                switch (mJustifyContentMode) {
                    case START:
                        baseLeft = getPaddingLeft();
                        break;
                    case CENTER:
                        baseLeft = (getMeasuredWidth() - mRowWidths.get(nowRow)) >> 1 + getPaddingLeft();
                        break;
                    case END:
                        baseLeft = getMeasuredWidth() - mRowWidths.get(nowRow) + getPaddingLeft();
                        break;
                    case SPACE_BETWEEN:
                        baseLeft = getPaddingLeft();
                        if (getMeasuredWidth() > mRowWidths.get(nowRow) && mColumnCounts.get(nowRow) > 1) {
                            justifyDiv = (getMeasuredWidth() - mRowWidths.get(nowRow)) / (mColumnCounts.get(nowRow) - 1);
                        }
                        break;
                    case SPACE_AROUND:
                        if (getMeasuredWidth() > mRowWidths.get(nowRow)) {
                            justifyDiv = (getMeasuredWidth() - mRowWidths.get(nowRow)) / mColumnCounts.get(nowRow);
                            baseLeft = justifyDiv >> 1 + getPaddingLeft();
                        }
                        break;
                }
            }
            int top = baseTop;
            if (Mode.CENTER == mAlignItemMode) {
                top += (mRowHeights.get(params.rowId) - childHei) >> 1;
            } else if (Mode.END == mAlignItemMode) {
                top += mRowHeights.get(params.rowId) - childHei - params.topMargin;
            } else {
                top += params.topMargin;
            }
            baseLeft += params.leftMargin;
            child.layout(baseLeft, top, baseLeft + childWid, top + childHei);
            baseLeft += childWid + justifyDiv + colGap + params.rightMargin;
        }
    }

    private int getRowGapBaseOnCurrentSatate() {
        if (Mode.SPACE_AROUND == mAlignContentMode || Mode.SPACE_BETWEEN == mAlignContentMode) {
            return 0;
        }
        return mRowGap;
    }

    private int getColGapBaseOnCurrentSatate() {
        if (Mode.SPACE_AROUND == mAlignContentMode || Mode.SPACE_BETWEEN == mAlignContentMode) {
            return 0;
        }
        return mRowGap;
    }

    public void setJustifyContent(Mode justifyContent) {
        this.mJustifyContentMode = justifyContent;
        requestLayout();
    }

    public void setAlignItem(Mode alignItem) {
        if (alignItem != Mode.START
                && alignItem != Mode.CENTER
                && alignItem != Mode.END) {
            alignItem = Mode.START;
        }
        this.mAlignItemMode = alignItem;
        requestLayout();
    }

    public void setAlignContent(Mode alignContent) {
        this.mAlignContentMode = alignContent;
        requestLayout();
    }

    public void setColGap(int colGap) {
        mColGap = colGap;
        requestLayout();
    }

    public void setRowGap(int rowGap) {
        mRowGap = rowGap;
        requestLayout();
    }

    public int getColGap() {
        return mColGap;
    }

    public int getRowGap() {
        return mRowGap;
    }

    public Mode getJustifyContent() {
        return mJustifyContentMode;
    }

    public Mode getAlignItem() {
        return mAlignItemMode;
    }

    public Mode getAlignContent() {
        return mAlignContentMode;
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {

        private int rowId = 0;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    public enum Mode {
        START(1),
        CENTER(2),
        END(3),
        SPACE_BETWEEN(4),
        SPACE_AROUND(5);

        private int mVal;

        Mode(int val) {
            mVal = val;
        }

        public static Mode valueOf(int mode) {
            switch (mode) {
                case 2:
                    return CENTER;
                case 3:
                    return END;
                case 4:
                    return SPACE_BETWEEN;
                case 5:
                    return SPACE_AROUND;
                case 1:
                default:
                    return START;
            }
        }

    }

    private static class RowSpec {
        int rowHeight;
        int rowWidth;
        int columnCount;
        int availableWidth;

        public void setAvailableWidth(int availableWidth) {
            this.availableWidth = availableWidth;
        }

        public void reset() {
            rowHeight = 0;
            rowWidth = 0;
            columnCount = 0;
        }

        public boolean addItem(int width, int height) {
            int tempWidth = rowWidth + width;
            if (tempWidth > availableWidth) {
                return false;
            } else {
                rowWidth = tempWidth;
                columnCount++;
                rowHeight = Math.max(rowHeight, height);
                return true;
            }
        }

        public void forceAddItem(int width, int height) {
            rowWidth += width;
            columnCount++;
            rowHeight = Math.max(rowHeight, height);
        }

        public boolean addItem(int width, int height, int colGap) {
            int tempWidth;
            if (columnCount == 0) {
                tempWidth = rowWidth + width;
            } else {
                tempWidth = rowWidth + width + colGap;
            }
            if (tempWidth > availableWidth) {
                return false;
            } else {
                rowWidth = tempWidth;
                columnCount++;
                rowHeight = Math.max(rowHeight, height);
                return true;
            }
        }

        public int getRowHeight() {
            return rowHeight;
        }

        public int getRowWidth() {
            return rowWidth;
        }

        public int getColumnCount() {
            return columnCount;
        }
    }
}

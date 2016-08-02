package org.houxg.flexlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class FlexLayout extends ViewGroup {
    static final int START = 1;
    static final int CENTER = 2;
    static final int END = 3;
    static final int SPACE_BETWEEN = 4;
    static final int SPACE_AROUND = 5;
    static final int STRETCH = 6;   //FIXME:wait to implement
    static final int BASE_LINE = 7; //FIXME:wait to implement
    int[] rowHeights = new int[32];
    int[] rowWidths = new int[32];
    int[] columnCounts = new int[32];
    int totalHeight;

    int justifyContentMode = START;
    int alignItemMode = START;
    int alignContentMode = START;
    int itemDiv = 0;
    int rowDiv = 0;

    public FlexLayout(Context context) {
        super(context);
    }

    public FlexLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlexLayout);
        justifyContentMode = typedArray.getInt(R.styleable.FlexLayout_justify_content, START);
        alignContentMode = typedArray.getInt(R.styleable.FlexLayout_align_content, START);
        alignItemMode = typedArray.getInt(R.styleable.FlexLayout_align_item, START);
        itemDiv = (int) typedArray.getDimension(R.styleable.FlexLayout_itemDividerWidth, 0);
        rowDiv = (int) typedArray.getDimension(R.styleable.FlexLayout_rowDividerHeight, 0);
        if (SPACE_AROUND == justifyContentMode || SPACE_BETWEEN == justifyContentMode) {
            itemDiv = 0;
        }
        if (SPACE_AROUND == alignContentMode || SPACE_BETWEEN == alignContentMode) {
            rowDiv = 0;
        }
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
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);


        int width = widthSize;
        int height = 0;
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int rowWid = 0;
        int maxRowHeight = 0;
        int count = getChildCount();
        int rowId = 0;
        int columnCount = 0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            int childWid = child.getMeasuredWidth() + params.leftMargin + params.rightMargin;
            int childHei = child.getMeasuredHeight() + params.topMargin + params.bottomMargin;
            rowWid += childWid;
            if (rowWid > width && columnCount > 0) {
//                maxRowHeight += rowDiv;
                rowHeights[rowId] = maxRowHeight;
                rowWidths[rowId] = rowWid - childWid - itemDiv;
                columnCounts[rowId] = columnCount;
                columnCount = 0;
                rowId++;

                rowWid = childWid;
                height += maxRowHeight + rowDiv;
                maxRowHeight = childHei;
            }
            rowWid += itemDiv;
            columnCount++;
            maxRowHeight = childHei >= maxRowHeight ? childHei : maxRowHeight;
            params.rowId = rowId;
        }
        rowHeights[rowId] = maxRowHeight;
        rowWidths[rowId] = rowWid;
        columnCounts[rowId] = columnCount;
        height += maxRowHeight;
        totalHeight = height;

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int justifyDiv = 0;
        int alignDiv = 0;

        int count = getChildCount();
        if (count <= 0) {
            return;
        }
        int baseTop = 0;
        int baseLeft = 0;
        int nowRow = -1;
        boolean isHeightEnough = getMeasuredHeight() > totalHeight;
        LayoutParams lastViewParams = (LayoutParams) getChildAt(getChildCount() - 1).getLayoutParams();
        int rowCount = lastViewParams.rowId + 1;
        switch (alignContentMode) {
            case CENTER:
                if (isHeightEnough) {
                    baseTop = (getMeasuredHeight() - totalHeight) >> 1;
                }
                break;
            case END:
                if (isHeightEnough) {
                    baseTop = getMeasuredHeight() - totalHeight;
                }
                break;
            case SPACE_BETWEEN:
                if (isHeightEnough && rowCount > 1) {
                    alignDiv = (getMeasuredHeight() - totalHeight) / (rowCount - 1);
                }
                break;
            case SPACE_AROUND:
                if (isHeightEnough) {
                    alignDiv = (getMeasuredHeight() - totalHeight) / rowCount;
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
                    baseTop += rowHeights[nowRow] + alignDiv + rowDiv;
                }
                nowRow = params.rowId;
                justifyDiv = 0;
                switch (justifyContentMode) {
                    case START:
                        baseLeft = 0;
                        break;
                    case CENTER:
                        baseLeft = (getMeasuredWidth() - rowWidths[nowRow]) >> 1;
                        break;
                    case END:
                        baseLeft = getMeasuredWidth() - rowWidths[nowRow];
                        break;
                    case SPACE_BETWEEN:
                        baseLeft = 0;
                        if (getMeasuredWidth() > rowWidths[nowRow] && columnCounts[nowRow] > 1) {
                            justifyDiv = (getMeasuredWidth() - rowWidths[nowRow]) / (columnCounts[nowRow] - 1);
                        }
                        break;
                    case SPACE_AROUND:
                        if (getMeasuredWidth() > rowWidths[nowRow]) {
                            justifyDiv = (getMeasuredWidth() - rowWidths[nowRow]) / columnCounts[nowRow];
                            baseLeft = justifyDiv >> 1;
                        }
                        break;
                }
            }
            int top = baseTop;
            if (CENTER == alignItemMode) {
                top += (rowHeights[params.rowId] - childHei) >> 1;
            } else if (END == alignItemMode) {
                top += rowHeights[params.rowId] - childHei - params.topMargin;
            } else {
                top += params.topMargin;
            }
            baseLeft += params.leftMargin;
            child.layout(baseLeft, top, baseLeft + childWid, top + childHei);
            baseLeft += childWid + justifyDiv + itemDiv + params.rightMargin;
        }
    }

    static class LayoutParams extends ViewGroup.MarginLayoutParams {

        int rowId = 0;

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
}

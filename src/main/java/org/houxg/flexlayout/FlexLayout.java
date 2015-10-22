package org.houxg.flexlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 流式布局
 * <br>
 * author: houxg
 * <br>
 * create on 2015/10/21
 */

// TODO: 2015/10/22 横纵，主轴/侧轴对齐方式，换行/不换行
public class FlexLayout extends ViewGroup {
    int[] rowHeights = new int[32];
    int[] rowWidths = new int[32];
    int[] culoumCounts = new int[32];
    int totalHeight;

    public FlexLayout(Context context) {
        super(context);
    }

    public FlexLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlexLayout);
        justifyContentMode = typedArray.getInt(R.styleable.FlexLayout_justify_content, START);
        alignContentMode = typedArray.getInt(R.styleable.FlexLayout_align_content, START);
        alignItemMode = typedArray.getInt(R.styleable.FlexLayout_align_item, START);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p.width, p.height);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取自己的宽
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);


        int width = widthSize;
        int height = 0;
        //让子View都做一遍测量
        // TODO: 2015/10/22 考虑margin因素
        // TODO: 2015/10/22 加入缩放参数
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int rowWid = 0;
        int maxRowHeight = 0;
        int count = getChildCount();
        int rowId = 0;
        int coulumCount = 0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            int childWid = child.getMeasuredWidth();
            int childHei = child.getMeasuredHeight();
            rowWid += childWid;
            //如果宽度不够了，而且该行已经有至少一个元素则换行
            if (rowWid > width && coulumCount > 0) {
                //记录该行信息
                rowHeights[rowId] = maxRowHeight;
                rowWidths[rowId] = rowWid - childWid;
                culoumCounts[rowId] = coulumCount;
                coulumCount = 0;
                rowId++;
                //换行
                rowWid = childWid;
                height += maxRowHeight;
                maxRowHeight = childHei;
            }
            coulumCount++;
            maxRowHeight = childHei >= maxRowHeight ? childHei : maxRowHeight;
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            params.rowId = rowId;
        }
        rowHeights[rowId] = maxRowHeight;
        rowWidths[rowId] = rowWid;
        culoumCounts[rowId] = coulumCount;
        height += maxRowHeight;
        totalHeight = height;

        //针对height处理
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
        int baseTop = 0;
        int left = 0;
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
                    baseTop = alignDiv / 2;
                }
                break;
        }
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            int top = baseTop;
            int childHei = child.getMeasuredHeight();
            int childWid = child.getMeasuredWidth();
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            if (nowRow != params.rowId) {
                if (nowRow >= 0) {
                    baseTop += rowHeights[nowRow] + alignDiv;
                }
                nowRow = params.rowId;
                justifyDiv = 0;
                //计算left起始点
                switch (justifyContentMode) {
                    case START:
                        left = 0;
                        break;
                    case CENTER:
                        left = (getMeasuredWidth() - rowWidths[nowRow]) / 2;
                        break;
                    case END:
                        left = getMeasuredWidth() - rowWidths[nowRow];
                        break;
                    case SPACE_BETWEEN:
                        left = 0;
                        if (getMeasuredWidth() > rowWidths[nowRow] && culoumCounts[nowRow] > 1) {
                            justifyDiv = (getMeasuredWidth() - rowWidths[nowRow]) / (culoumCounts[nowRow] - 1);
                        }
                        break;
                    case SPACE_AROUND:
                        if (getMeasuredWidth() > rowWidths[nowRow]) {
                            justifyDiv = (getMeasuredWidth() - rowWidths[nowRow]) / culoumCounts[nowRow];
                            left = justifyDiv / 2;
                        }
                        break;
                }
            }
            switch (alignItemMode) {
                case START:
                    top = baseTop;
                    break;
                case CENTER:
                    top = baseTop + (rowHeights[params.rowId] - childHei) / 2;
                    break;
                case END:
                    top = baseTop + rowHeights[params.rowId] - childHei;
                    break;
                case STRETCH:
                    //需要伸缩子项，将所有高度都为最大高度
                    break;
            }
            child.layout(left, top, left + childWid, top + childHei);
            left += childWid + justifyDiv;
        }
    }

    final int START = 1;
    final int CENTER = 2;
    final int END = 3;
    final int SPACE_BETWEEN = 4;
    final int SPACE_AROUND = 5;
    final int STRETCH = 6;
    final int BASE_LINE = 7;
    int justifyContentMode = START;
    int alignItemMode = START;
    int alignContentMode = START;


    static class LayoutParams extends ViewGroup.LayoutParams {
        //TODO:缩放系数

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

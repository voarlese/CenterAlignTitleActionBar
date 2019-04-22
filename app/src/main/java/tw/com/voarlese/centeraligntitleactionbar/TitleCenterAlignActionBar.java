package tw.com.voarlese.centeraligntitleactionbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * Created by kemo on 2019/04/12.
 * title 始終要置中的功能
 * 左右側沒碰到其他元件 : title 於螢幕置中,
 * 左右側碰到其他元件 : 剩餘空間 置中
 * 有時候 textView 會帶有 drawable 也要加入判斷
 *
 */
public class TitleCenterAlignActionBar extends ViewGroup {
    private static final String TAG = "ActionBar";
    
    public TitleCenterAlignActionBar(Context context) {
        this(context, null, -1);
    }
    
    public TitleCenterAlignActionBar(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }
    
    public TitleCenterAlignActionBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }
    
    // 左區域, 右區域, title flag
    private static final int LeftContainer = 0;
    private static final int RightContainer = 1;
    private static final int isTitle = 2;
    // id + width
    private SparseIntArray leftList;
    private SparseIntArray rightList;
    private int rightX; // 右側 x 座標
    private int leftX; // 左側 x 座標
    private int titleId = -1; // title view id
    private int txtWidth; // 文字實際長度
    int endIconWidth;
    int endIconPadding;
    private Context mContext;
    private SparseArray<int[]> layoutList; // id/bounds
    private ArrayList<Integer> indexList; // 要放icon 的順序
    private Drawable endIcon;
    private boolean isEndIconShow = false; // 控制endIcon顯示
    // 畫底線工具
    private Paint bottomPaint = new Paint();
    
    private void init(Context context, AttributeSet attrs) {
        this.mContext = context;
        layoutList = new SparseArray<>();
        indexList = new ArrayList<>();
        if (attrs != null) {
            TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.TitleCenterAlignActionBar);
            try {
                String[] leftIdsName = getComponentString(t.getString(R.styleable.TitleCenterAlignActionBar_left_component));
                leftList = getList(leftIdsName); // 初始化
                String[] rightIdsName = getComponentString(t.getString(R.styleable.TitleCenterAlignActionBar_right_component));
                rightList = getList(rightIdsName); // 初始化
            } catch (NullPointerException e) {
                leftList = new SparseIntArray();
                rightList = new SparseIntArray();
            }
            
            titleId = t.getResourceId(R.styleable.TitleCenterAlignActionBar_my_title, -1);
            indexList.add(titleId);
            t.recycle();
        }
    }
    
    private String[] getComponentString(String s){
        if (TextUtils.isEmpty(s)){
            return new String[]{};
        }
        return s.split(",");
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 計算view 寬高
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        // 計算child 寬高並存檔
        onMeasureChild(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth, measureHeight);
    }
    
    private void onMeasureChild(int widthMeasureSpec, int heightMeasureSpec) {
        leftX = getPaddingLeft(); //左邊x
        rightX = getMeasuredWidth() - getPaddingRight(); // 右邊x
        int y = getPaddingTop();
        
        int titleIndex = -1;  // title index 計算完左右側按鈕後才計算 title
        // loop childView
        for (int i = 0; i < indexList.size(); i++) {
            View childView = findViewById(indexList.get(i));
            if (childView.getVisibility() == GONE) { // gone 不加入計算
                continue;
            }
            ChildViewParams childParam = ChildViewParams.getChildParam(childView);    // 取得child param 參數
            switch (getPositionType(childView.getId())) {   // 判斷按鈕屬於哪一側
            case LeftContainer:
                getChildSize(childView, widthMeasureSpec, heightMeasureSpec);
                setWidthToId(childView.getId(), childParam.getWidth() + childParam.getLeftMargin() + childParam.getRightMargin()); // id/width
                // 從左邊開始排列 儲存位置 在 onLayout 使用
                layoutList.put(childView.getId(), new int[] {
                        leftX + childParam.getLeftMargin(), y + childParam.getTopMargin(), leftX + childParam.getLeftMargin() + childParam.getWidth(), y + childParam.getTopMargin() + childParam.getHeight() });
                // 移動橫坐標, 重新確定 左側icon 的位置
                leftX += childParam.getLeftMargin() + childParam.getWidth() + childParam.getRightMargin();
                break;
            case RightContainer:
                getChildSize(childView, widthMeasureSpec, heightMeasureSpec);
                setWidthToId(childView.getId(), childParam.getWidth() + childParam.getLeftMargin() + childParam.getRightMargin()); // id/width
                // 從右邊開始排列 儲存位置 在 onLayout 使用
                layoutList.put(childView.getId(), new int[] { rightX - childParam.width - childParam.getRightMargin(), y + childParam.getTopMargin(), rightX - childParam.getRightMargin(), y + childParam.getTopMargin() + childParam.getHeight() });
                //移動橫坐標, 重新確定 右邊icon 的位置
                rightX -= (childParam.getLeftMargin() + childParam.getWidth() + childParam.getRightMargin());
                break;
            case isTitle:
                // 全部都做完才設定title
                getChildSize(childView, widthMeasureSpec, heightMeasureSpec);
                break;
            default:
                break;
            }
        }
        if (titleId != -1) {
            setTitlePosition(findViewById(titleId), widthMeasureSpec, heightMeasureSpec);
        }
    }
    
    
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            int[] layoutP = layoutList.get(getChildAt(i).getId());
            if (layoutP != null) {
                getChildAt(i).layout(layoutP[0], layoutP[1], layoutP[2], layoutP[3]);
            }
        }
    }
    
    
    @Override
    protected void onDraw(Canvas canvas) {
        // 繪製底線
        bottomPaint.setColor(ContextCompat.getColor(mContext, R.color.input_bottom_line));
        bottomPaint.setAntiAlias(true);
        bottomPaint.setStrokeWidth(DisplayUtil.dip2px(mContext, 1));
        canvas.drawLine(0, getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight(), bottomPaint);
    }
    
    /**
     * @param idNames
     * @return
     */
    private SparseIntArray getList(String[] idNames) {
        SparseIntArray sparseArray = new SparseIntArray();
        for (int i = 0; i < idNames.length; i++) {
            int id = getResources().getIdentifier(idNames[i], "id", mContext.getPackageName());
            sparseArray.put(id, 0);
            indexList.add(id);
        }
        return sparseArray;
    }
    
    /**
     * 設定 childView 的寬度
     *
     * @param id
     * @param width
     */
    private void setWidthToId(int id, int width) {
        switch (getPositionType(id)) {
        case LeftContainer:
            leftList.put(id, width);
            break;
        case RightContainer:
            rightList.put(id, width);
            break;
        case isTitle:
            break;
        default:
            break;
        }
    }
    
    /**
     * 判斷是哪個位置
     *
     * @return
     */
    private int getPositionType(int id) {
        if (leftList.indexOfKey(id) > -1) {
            return LeftContainer;
        } else if (rightList.indexOfKey(id) > -1) {
            return RightContainer;
        } else if (id == titleId) {
            return isTitle;
        }
        return -1;
    }
    
    /**
     * 测量获取某个子控件的Size(包括margin)
     *
     * @param child
     * @param parentWidthMeasureSpec
     * @param
     * @return
     */
    private int[] getChildSize(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        LayoutParams lp = child.getLayoutParams();
        int horizontalMargin = 0, verticalMargin = 0;
        if (lp instanceof MarginLayoutParams) {
            measureChildWithMargins(child, parentWidthMeasureSpec, 0, parentHeightMeasureSpec, 132);
            MarginLayoutParams mp = (MarginLayoutParams) lp;
            horizontalMargin = mp.leftMargin + mp.rightMargin;
            verticalMargin = mp.topMargin + mp.bottomMargin;
        } else {
            measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec);
        }
        int childWidthWithMargin = child.getMeasuredWidth() + horizontalMargin;
        int childHeightWithMargin = child.getMeasuredHeight() + verticalMargin;
        return new int[] { childWidthWithMargin, childHeightWithMargin };
    }
    
    /**
     * 判斷 title 需放置於哪 用 文字寬度決定位置
     * @param childView
     */
    private void setTitlePosition(View childView, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        boolean check1 = childView instanceof TextView;
        if (!check1) {
            Log.e(TAG, "setTitlePosition: ChildView is NOT TextView");
            return;
        }
        
        ChildViewParams titleParam = ChildViewParams.getChildParam(childView);
        
        if (endIcon == null){
            endIcon = ((TextView)childView).getCompoundDrawables()[2];
            if (endIcon != null){
                // drawable end 的寬度
                endIconWidth = ((TextView)childView).getCompoundDrawables()[2].getIntrinsicWidth();
                // endIcon Padding
                endIconPadding = ((TextView)childView).getCompoundDrawablePadding();
            }
        }
    
        if (isEndIconShow){
            if (((TextView)childView).getCompoundDrawables()[2] == null){
                ((TextView)childView).setCompoundDrawables(null, null, endIcon, null);
            }
        } else {
            ((TextView)childView).setCompoundDrawables(null, null, null, null);
        }
       
        // 取出文字寬度
        txtWidth = (int) ((TextView) childView).getPaint().measureText(((TextView) childView).getText().toString());
       
        // layout方法 只改變childView在parent中的擺放位置及長寬
        // 要修改 childView params自己的寬度 才會讓文字顯示正常
        int[] position = getPosition(titleParam);
        LayoutParams lp = childView.getLayoutParams();
        Log.e(TAG, "setTitlePosition: txtWidth : " + txtWidth );
        Log.e(TAG, "setTitlePosition: 設定後寬度 : " + (position[1] - position[0]) );
        lp.width = (position[1] - position[0]); // 設定 title 實際內容寬度
        childView.setLayoutParams(lp);
        
        getChildSize(childView, parentWidthMeasureSpec, parentHeightMeasureSpec); // 存檔
        int l = position[0];
        int t = getPaddingTop();
        int r = position[1];
        int b = getPaddingTop() + titleParam.getHeight();
        // 記錄位置
        layoutList.put(childView.getId(), new int[] {
                l, t, r, b });
    }
    
    /**
     * 左邊或右邊哪邊有 icon
     * 再分別設定寬度
     * 產出的x y 因為除法關係會有誤差 最好寫法是回傳float[] 在最後加減的時候才作無條件進位
     * @return
     */
    private int[] getPosition(ChildViewParams cp) {
        int xl = 0;
        int xr = 0;
        int oxl = getXYEdgeOnScreenByOverLimit(cp)[0];
        int oxr = getXYEdgeOnScreenByOverLimit(cp)[1];
        boolean isOverLimit = false;
        // 文字長度 > 剩餘寬度
        if (isOverSpace(cp)) {
            // 置中於兩側icon剩餘空間
            xl = oxl;
            xr = oxr;
            isOverLimit = xr > rightX || xl < leftX;
        } else if (getTextWidthOnScreenLeft(cp) > leftX && getTextWidthOnScreenRight(cp) < rightX) { // 左右側都沒蓋到icon 文字置中
            xl = (int) (getTextWidthOnScreenLeft(cp));
            xr = (int) Math.ceil(getTextWidthOnScreenRight(cp));
        } else if (getTextWidthOnScreenLeft(cp) < leftX) {// 左側超過 左側icon 往右邊剩餘空間置中
            xl = oxl;
            xr = oxr;
            isOverLimit = xr > rightX || xl < leftX;
        } else if (getTextWidthOnScreenRight(cp) > rightX) {// 右側超過 右側icon 往左邊剩餘空間置中
            xl = oxl;
            xr = oxr;
            isOverLimit = xr > rightX || xl < leftX;
        }
        if (isOverLimit){ // 超過 左右icon區 設為最大寬度
            xl = leftX + cp.getLeftMargin();
            xr = rightX - cp.getRightMargin();
        }
        return new int[] { xl, xr };
    }
    
    /**
     * 文字置終於中終於剩餘空間後左右邊界
     * @param titleParam
     * @return
     */
    private int[] getXYEdgeOnScreenByOverLimit(ChildViewParams titleParam){
        float centerX = leftX + (rightX - leftX) / 2f;
        float shelfl = txtWidth / 2f + titleParam.getLeftPadding() + titleParam.getLeftMargin();
        float shelfr = txtWidth / 2f + titleParam.getRightPadding() + titleParam.getRightMargin() + (isEndIconShow ? endIconWidth + endIconPadding : 0);
        int x = (int) Math.floor(centerX - shelfl); // 剩餘中心點往左邊算寬度 無條件捨去
        int y = (int) Math.ceil(centerX + shelfr); // 無條件進位
        return new int[]{x, y};
    }
    /**
     * 文字寬度是否超過剩餘空間
     * 超過就設定title 位於 左邊end 到右邊start
     *
     * @return
     */
    private boolean isOverSpace(ChildViewParams cp) {
        return txtWidth + cp.getLeftMargin() + cp.getLeftPadding() + cp.getRightMargin() + cp.getRightPadding() > (rightX - leftX);
    }
    
    /**
     * 文字置中於螢幕後"右"邊界
     *
     * @return
     */
    private float getTextWidthOnScreenRight(ChildViewParams titleCp) {
        float textHalf = txtWidth / 2f + titleCp.getRightPadding() + titleCp.getRightMargin() + (isEndIconShow ? endIconWidth + endIconPadding : 0);
        float sHalf = getMeasuredWidth() / 2f;
        // 如果文字大於螢幕寬度 就回傳 螢幕寬度 否則回傳 一半再加上文字寬度 =  右邊位置
        return sHalf - textHalf < 0 ? getMeasuredWidth() : sHalf + textHalf;
    }
    
    /**
     * 文字置中於螢幕後"左"邊界
     * @return
     */
    private float getTextWidthOnScreenLeft(ChildViewParams cp) {
        float textHalf = txtWidth / 2f + cp.getLeftPadding() + cp.getLeftMargin();
        float sHalf = getMeasuredWidth() / 2f;
        return sHalf - textHalf < 0 ? 0 : sHalf - textHalf;
    }
    
    /**
     * 修改title
     * @param s
     */
    public void setText(String s) {
        TextView myTitle = findViewById(titleId);
        if (myTitle == null){
            return ;
        }
        myTitle.setText(s);
        requestLayout();
    }
    
    public void setEndIconVisibility(int status){
        switch (status){
        case View.VISIBLE:
            isEndIconShow = true;
            requestLayout();
            break;
        case View.INVISIBLE:
        case View.GONE:
            isEndIconShow = false;
            requestLayout();
            break;
        }
    }
    
    private static class ChildViewParams {
        int width = 0;
        int height = 0;
        int leftMargin = 0;
        int topMargin = 0;
        int rightMargin = 0;
        int bottomMargin = 0;
        int leftPadding = 0;
        int rightPadding = 0;
    
        public ChildViewParams() {
        
        }
    
        public ChildViewParams(int width, int height, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
            this.width = width;
            this.height = height;
            this.leftMargin = leftMargin;
            this.topMargin = topMargin;
            this.rightMargin = rightMargin;
            this.bottomMargin = bottomMargin;
        }
        
        public ChildViewParams(int width, int height, int leftMargin, int topMargin, int rightMargin, int bottomMargin, int leftPadding, int rightPadding) {
            this.width = width;
            this.height = height;
            this.leftMargin = leftMargin;
            this.topMargin = topMargin;
            this.rightMargin = rightMargin;
            this.bottomMargin = bottomMargin;
            this.leftPadding = leftPadding;
            this.rightPadding = rightPadding;
        }
        
        public int getWidth() {
            return width;
        }
        
        public void setWidth(int width) {
            this.width = width;
        }
        
        public int getHeight() {
            return height;
        }
        
        public void setHeight(int height) {
            this.height = height;
        }
        
        public int getLeftMargin() {
            return leftMargin;
        }
        
        public void setLeftMargin(int leftMargin) {
            this.leftMargin = leftMargin;
        }
        
        public int getTopMargin() {
            return topMargin;
        }
        
        public void setTopMargin(int topMargin) {
            this.topMargin = topMargin;
        }
        
        public int getRightMargin() {
            return rightMargin;
        }
        
        public void setRightMargin(int rightMargin) {
            this.rightMargin = rightMargin;
        }
        
        public int getBottomMargin() {
            return bottomMargin;
        }
        
        public void setBottomMargin(int bottomMargin) {
            this.bottomMargin = bottomMargin;
        }
        
        public int getLeftPadding() {
            return leftPadding;
        }
        
        public void setLeftPadding(int leftPadding) {
            this.leftPadding = leftPadding;
        }
        
        public int getRightPadding() {
            return rightPadding;
        }
        
        public void setRightPadding(int rightPadding) {
            this.rightPadding = rightPadding;
        }
        
        public int getAllWidth(){
            return rightMargin + rightPadding + leftPadding + leftMargin + width;
        }
    
        public static ChildViewParams getChildParam(View childView) {
            if (childView == null){
                return new ChildViewParams();
            }
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();
            int leftMargin = 0;
            int rightMargin = 0;
            int topMargin = 0;
            int bottomMargin = 0;
            LayoutParams childLp = childView.getLayoutParams();
            if (childLp instanceof MarginLayoutParams) {
                leftMargin = ((MarginLayoutParams) childLp).leftMargin;
                rightMargin = ((MarginLayoutParams) childLp).rightMargin;
                topMargin = ((MarginLayoutParams) childLp).topMargin;
                bottomMargin = ((MarginLayoutParams) childLp).bottomMargin;
            }
            return new ChildViewParams(childWidth, childHeight, leftMargin, rightMargin, topMargin, bottomMargin, childView.getPaddingLeft(), childView.getPaddingRight());
        }
        
        @Override
        public String toString() {
            return "ChildViewParams{" + "width=" + width + ", height=" + height + ", leftMargin=" + leftMargin + ", topMargin=" + topMargin + ", rightMargin=" + rightMargin + ", bottomMargin=" + bottomMargin + ", leftPadding=" + leftPadding + ", rightPadding=" + rightPadding + '}';
        }
    }
}

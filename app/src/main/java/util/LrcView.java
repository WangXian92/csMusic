package util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;


import androidx.appcompat.widget.AppCompatTextView;

import java.util.ArrayList;
import java.util.List;
//自定义TextView
public class LrcView extends AppCompatTextView {//Text替换为AppCompatTextView  实际AppCompatTextView继承


    public LrcView(Context context){
        super(context);
        init();

    }

    public LrcView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        init();
    }

    public LrcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init();
    }

    private void init() {
        // TODO Auto-generated method stub

    }

    /**

     *执行此操作来绘制图形。

     *

     *@param canvas将在其上绘制背景的画布

     */
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

        if (canvas == null) {
            return;
        }

        setText("正在播放歌曲");
    }

    /**

     *当此视图的大小发生更改时，将在布局期间调用此命令。如果

     *你刚被添加到视图层次结构中，用旧的

     *值为0。

     *

     *@param w此视图的当前宽度。

     *@param h此视图的当前高度。

     *@param oldw此视图的旧宽度。

     *@param oldh此视图的旧高度。

     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh);


    }



}

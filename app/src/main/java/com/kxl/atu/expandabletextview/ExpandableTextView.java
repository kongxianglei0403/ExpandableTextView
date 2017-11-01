package com.kxl.atu.expandabletextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Description:
 * Author:ATU
 * Date:2017/10/31  15:05
 */
public class ExpandableTextView extends LinearLayout {

    TextView id_source_textview;
    TextView id_expand_textview;

    //最多显示行数
    int maxExpandLines = 0;
    //动画执行时间
    int duration = 0;
    //文本内容是否发生改变
    private boolean isChange = false;
    //是否为收缩状态
    private boolean isCollapsed = true;
    //文本的高度
    private int realTextViewHeigt = 0;
    //按钮的高度
    private int lastHeight = 0;
    //收缩状态时的高度
    private int collapsedHeight;

    OnExpandStateChangeListener listener;
    //是否在执行动画
    private boolean isAnimate = false;

    public ExpandableTextView(Context context)
    {
        this(context,null);
    }

    public ExpandableTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        setOrientation(VERTICAL);
        TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.ExpandableTextViewAttr);
        maxExpandLines = array.getInteger(R.styleable.ExpandableTextViewAttr_maxExpandLines, 3);
        duration = array.getInteger(R.styleable.ExpandableTextViewAttr_duration,200);
        array.recycle();
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        id_source_textview = (TextView) findViewById(R.id.id_source_textview);
        id_expand_textview = (TextView) findViewById(R.id.id_expand_textview);
        id_expand_textview.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ExpandCollapsedAnimation animation;
                //缩放操作
                isCollapsed = !isCollapsed;
                if (isCollapsed)
                {
                    id_expand_textview.setText("展开");
                    if (listener != null)
                    {
                        listener.onExpandStateChanged(true);
                    }
                    animation = new ExpandCollapsedAnimation(getHeight(),collapsedHeight);
                }
                else
                {
                    id_expand_textview.setText("收起");
                    if (listener != null)
                    {
                        listener.onExpandStateChanged(false);
                    }
                    animation = new ExpandCollapsedAnimation(getHeight(),realTextViewHeigt + lastHeight);
                }
                animation.setFillAfter(true);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        isAnimate = true;
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        clearAnimation();
                        isAnimate = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                clearAnimation();
                startAnimation(animation);

                //不带动画的处理方式
//                isChange = true;
//                requestLayout();
            }
        });
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        //执行动画的过程中屏蔽事件
        return isAnimate;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //如果隐藏控件或者textview值没有发生改变，不用测量
        if (getVisibility() == GONE || !isChange)
            return;
        isChange = false;

        //初始化默认状态，即正常显示文本
        id_expand_textview.setVisibility(GONE);
        id_source_textview.setMaxLines(Integer.MAX_VALUE);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //如果textview值没有超过最低限定，则不予处理
        if (id_source_textview.getLineCount() <= maxExpandLines)
            return;

        //初始化高度赋值，为后续动画事件准备数据
        realTextViewHeigt = getRealTextViewHeight(id_source_textview);

        //如果为收缩状态，显示最大行数，缩放按钮显示出来
        if(isCollapsed)
        {
            id_source_textview.setLines(maxExpandLines);
        }
        id_expand_textview.setVisibility(VISIBLE);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (isCollapsed)
        {
            id_source_textview.post(new Runnable()
            {
                @Override
                public void run()
                {
                    //lastHeight 总的高度 - 文本的高度  即为按钮的高度
                    lastHeight = getHeight() - id_source_textview.getHeight();
                    collapsedHeight = getMeasuredHeight();
                }
            });
        }
    }

    private int getRealTextViewHeight(TextView textview)
    {
        //getLineTop() 返回一个等差序列，当参数为行数时，结果即为文本的高度
        int textviewHeight = textview.getLayout().getLineTop(textview.getLineCount());
        return textview.getCompoundPaddingBottom() + textview.getCompoundPaddingTop() + textviewHeight;
    }

    public void setText(String str)
    {
        isChange = true;
        id_source_textview.setText(str);
    }

    public void setText(String str,boolean isCollapsed)
    {
        this.isCollapsed = isCollapsed;
        if (isCollapsed)
        {
            id_expand_textview.setText("展开");
        }
        else
        {
            id_expand_textview.setText("收起");
        }
        clearAnimation();
        setText(str);
        getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    private class ExpandCollapsedAnimation extends Animation
    {
        private int startValue = 0;
        private int endValue = 0;

        public ExpandCollapsedAnimation(int startValue, int endValue)
        {
            setDuration(duration);
            this.startValue = startValue;
            this.endValue = endValue;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t)
        {
            super.applyTransformation(interpolatedTime, t);
            int height = (int) ((endValue - startValue) * interpolatedTime + startValue);
            id_source_textview.setMaxHeight(height - lastHeight);
            ExpandableTextView.this.getLayoutParams().height = height;
            ExpandableTextView.this.requestLayout();
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

    public void setListener(OnExpandStateChangeListener listener)
    {
        this.listener = listener;
    }

    public interface OnExpandStateChangeListener
    {
        void onExpandStateChanged(boolean isExpanded);
    }
}

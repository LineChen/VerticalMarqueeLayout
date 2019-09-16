package com.line.marqueelayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

/**
 * Created by linechen on 2019-09-11.
 */
public class VerticalMarqueeLayout extends FrameLayout {

    private VerticalMarqueeAdapter<VerticalMarqueeLayout.ViewHolder> adapter;
    private FloatEvaluator floatEval = new FloatEvaluator();
    private boolean runningAnimation = false;
    private int currentPosition = 0;
    private int internal = 3000;//ms
    private View snap1, snap2;
    private ViewHolder snap1ViewHolder, snap2ViewHolder;

    public VerticalMarqueeLayout(@NonNull Context context) {
        this(context, null);
    }

    public VerticalMarqueeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalMarqueeLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    private AdapterDataObserver adapterDataObserver = new AdapterDataObserver() {
        @Override
        public void notifyChanged() {
            initSnapView();
            if (adapter.getItemCount() == 0) {
                stop();
            } else {
                start();
            }
        }
    };

    public void setAdapter(VerticalMarqueeAdapter adapter) {
        this.adapter = adapter;
        adapter.setAdapterDataObserver(adapterDataObserver);
        initSnapView();
    }

    private void initSnapView() {
        int itemCount = adapter.getItemCount();
        if (snap1 == null || snap2 == null) {
            snap1ViewHolder = adapter.onCreateViewHolder(this);
            snap1 = snap1ViewHolder.getItemView();
            snap2ViewHolder = adapter.onCreateViewHolder(this);
            snap2 = snap2ViewHolder.getItemView();
        }
        if (itemCount == 0) {
            removeAllViews();
        } else if (itemCount == 1) {
            adapter.onBindViewHolder(snap1ViewHolder, 0);
            adapter.onBindViewHolder(snap2ViewHolder, 0);
        } else if (itemCount >= 2) {
            adapter.onBindViewHolder(snap1ViewHolder, 0);
            adapter.onBindViewHolder(snap2ViewHolder, 1);
        }

        if (itemCount > 0) {
            removeAllViews();
            addView(snap1);
            addView(snap2);
            currentPosition = (currentPosition + 1) % itemCount;
            snap2.post(new Runnable() {
                @Override
                public void run() {
                    snap1.setTranslationY(0);
                    snap2.setTranslationY(getMeasuredHeight());
                }
            });
        }
    }

    public void start() {
        if (adapter == null) {
            throw new NullPointerException("VerticalMarqueeAdapter is null.");
        }
        if (adapter.getItemCount() == 0) {
            return;
        }
        if (runningAnimation) {
            return;
        }
        if (snap1 == null || snap2 == null) {
            return;
        }
        stop();
        postDelayed(translationTask, internal);
    }

    public void stop() {
        removeCallbacks(translationTask);
    }

    public void setInternal(int internal) {
        this.internal = internal;
    }

    private Runnable translationTask = new Runnable() {
        @Override
        public void run() {
            final float startY1 = snap1.getTranslationY();
            final float startY2 = snap2.getTranslationY();
            final float endY1 = startY1 == 0 ? -getHeight() : 0;
            final float endY2 = startY2 == 0 ? -getHeight() : 0;

            ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float fraction = animation.getAnimatedFraction();
                    snap1.setTranslationY(floatEval.evaluate(fraction, startY1, endY1));
                    snap2.setTranslationY(floatEval.evaluate(fraction, startY2, endY2));
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    runningAnimation = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    runningAnimation = false;
                    if (adapter.getItemCount() == 0) {
                        return;
                    }
                    currentPosition += 1;
                    if (currentPosition > (adapter.getItemCount() - 1)) {
                        currentPosition = 0;
                    }
                    View moveView = snap1.getTranslationY() == -getHeight() ? snap1 : snap2;
                    moveView.setTranslationY(getHeight() * 2);
                    adapter.onBindViewHolder(moveView == snap1 ? snap1ViewHolder : snap2ViewHolder, currentPosition);
                    postDelayed(translationTask, internal);
                }
            });
            animator.setDuration(getHeight() * 6)
                    .setInterpolator(new AccelerateDecelerateInterpolator());
            animator.start();
        }
    };

    public abstract static class ViewHolder {
        private View itemView;

        public ViewHolder(View itemView) {
            if (itemView == null) {
                throw new IllegalArgumentException("itemView may not be null");
            } else {
                this.itemView = itemView;
            }
        }

        public View getItemView() {
            return itemView;
        }
    }
}

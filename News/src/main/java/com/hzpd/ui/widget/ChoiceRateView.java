package com.hzpd.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hzpd.hflt.R;

/**
 * 用户评分
 */
public class ChoiceRateView extends LinearLayout implements View.OnClickListener {
    public ChoiceRateView(Context context) {
        super(context);
        init();
    }

    public ChoiceRateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    ImageView one;
    ImageView two;
    ImageView three;
    ImageView four;
    ImageView five;

    private void init() {
        View content = View.inflate(getContext(), R.layout.choice_rate_layout, this);
        (one = (ImageView) content.findViewById(R.id.rate_one)).setOnClickListener(this);
        (two = (ImageView) content.findViewById(R.id.rate_two)).setOnClickListener(this);
        (three = (ImageView) content.findViewById(R.id.rate_three)).setOnClickListener(this);
        (four = (ImageView) content.findViewById(R.id.rate_four)).setOnClickListener(this);
        (five = (ImageView) content.findViewById(R.id.rate_five)).setOnClickListener(this);
    }


    private int score = 0;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
        updateUI(score);
    }

    public void updateUI(int score) {
        this.score = score;
        one.setImageResource(score >= 1 ? R.drawable.feedstar_fill : R.drawable.feedstar_hollow);
        two.setImageResource(score >= 2 ? R.drawable.feedstar_fill : R.drawable.feedstar_hollow);
        three.setImageResource(score >= 3 ? R.drawable.feedstar_fill : R.drawable.feedstar_hollow);
        four.setImageResource(score >= 4 ? R.drawable.feedstar_fill : R.drawable.feedstar_hollow);
        five.setImageResource(score >= 5 ? R.drawable.feedstar_fill : R.drawable.feedstar_hollow);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rate_one:
                updateUI(1);
                break;
            case R.id.rate_two:
                updateUI(2);
                break;
            case R.id.rate_three:
                updateUI(3);
                break;
            case R.id.rate_four:
                updateUI(4);
                break;
            case R.id.rate_five:
                updateUI(5);
                break;
        }
    }

}

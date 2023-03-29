package com.monobogdan.monolaunch;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class DialerView extends LinearLayout {
    private TextView numberView;

    public DialerView(Context context)
    {
        super(context);

        setBackgroundColor(Color.argb(128, 255, 255, 255));

        setOrientation(LinearLayout.VERTICAL);
        numberView = new TextView(context);

        numberView.setTextColor(Color.BLUE);
        numberView.setInputType(InputType.TYPE_CLASS_PHONE);
        numberView.setTextSize(35);
        numberView.setMaxHeight(90);
        numberView.setFocusable(true);

        ViewGroup.LayoutParams params = new GridLayout.LayoutParams();
        params.width = LayoutParams.MATCH_PARENT;
        params.height = 90;
        addView(numberView);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

        if(gainFocus)
            numberView.requestFocus();
    }
}

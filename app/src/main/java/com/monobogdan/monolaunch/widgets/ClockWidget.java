package com.monobogdan.monolaunch.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.View;

import java.text.DateFormat;
import java.util.Date;

public class ClockWidget {

    private View parentView;
    private Paint bgPaint;
    private Paint paint;
    private Paint datePaint;

    public ClockWidget(View view)
    {
        parentView = view;
        Context ctx = view.getContext();

        bgPaint = new Paint();
        bgPaint.setColor(Color.argb(99, 128, 128, 128));

        paint = new Paint();
        paint.setTextSize(35);
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.MONOSPACE);

        datePaint = new Paint();
        datePaint.setTextSize(12);
        datePaint.setColor(Color.WHITE);
        datePaint.setAntiAlias(true);
        datePaint.setTypeface(Typeface.MONOSPACE);

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                view.invalidate();
                handler.postDelayed(this, 1000);
            }
        });
    }

    public float draw(Canvas canvas, float y)
    {
        float yRet = 0;
        Date date = new Date();
        String strDate = DateFormat.getDateInstance().format(date);
        String strTime = DateFormat.getTimeInstance().format(date);

        canvas.drawText(strTime, parentView.getWidth() / 2 - (paint.measureText(strTime) / 2), y + -paint.getFontMetrics().top, paint);
        yRet += -paint.getFontMetrics().top;
        canvas.drawText(strDate, parentView.getWidth() / 2 - (datePaint.measureText(strDate) / 2), y+ -datePaint.getFontMetrics().top + -paint.getFontMetrics().top, datePaint);
        yRet += -datePaint.getFontMetrics().top;

        canvas.drawRect(0, y, parentView.getWidth(), yRet + 25, bgPaint);

        return yRet + 25;
    }
}

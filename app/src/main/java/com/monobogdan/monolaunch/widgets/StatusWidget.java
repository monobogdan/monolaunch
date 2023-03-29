package com.monobogdan.monolaunch.widgets;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.CallLog;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.view.View;

import com.monobogdan.monolaunch.Launcher;
import com.monobogdan.monolaunch.R;

import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;

public class StatusWidget extends BroadcastReceiver {

    private Context context;
    private Launcher.LauncherView view;

    private ActivityManager activityManager;
    private ActivityManager.MemoryInfo memInfo;

    private Paint paint;
    private BitmapDrawable iconSMS;
    private BitmapDrawable iconDial;
    private BitmapDrawable iconRAM;

    private int smsCount;
    private String smsSender;
    private int dialCount;

    public StatusWidget(Launcher.LauncherView view)
    {
        this.view = view;
        context = view.getContext();

        smsSender = "";

        paint = new Paint();
        paint.setTextSize(12.0f);
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setShadowLayer(1, 1, 1, Color.DKGRAY);

        activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        memInfo = new ActivityManager.MemoryInfo();

        iconSMS = (BitmapDrawable) context.getResources().getDrawable(R.drawable.email);
        iconDial = (BitmapDrawable) context.getResources().getDrawable(R.drawable.dial);
        iconRAM = (BitmapDrawable) context.getResources().getDrawable(R.drawable.cpu);

        IntentFilter broadCast = new IntentFilter();
        broadCast.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        broadCast.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);

        context.registerReceiver(this, broadCast);

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                activityManager.getMemoryInfo(memInfo);
                updateSMSState();
                updateDialState();
                handler.postDelayed(this, 5000);
            }
        });
    }

    private void updateSMSState()
    {
        String[] proj = new String[]
                {
                        Telephony.Sms.ADDRESS,
                        Telephony.Sms.DATE,
                        Telephony.Sms.READ
                };

        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"), proj,
                "read = 0",  null, null);
        smsCount = cursor.getCount();

        if(smsCount > 0) {
            cursor.moveToFirst();
            int addr = cursor.getColumnIndex("address");

            if (addr != -1)
                smsSender = cursor.getString(addr);
        }
        else
        {
            smsSender = "";
        }
    }

    private void updateDialState()
    {
        String[] proj = new String[]
                {
                        CallLog.Calls.IS_READ,
                        CallLog.Calls.NUMBER,
                        CallLog.Calls.DATE
                };

        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, proj, "is_read = 0", null, null);
        dialCount = cursor.getCount();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        updateSMSState();
        updateDialState();

        view.invalidate();
    }

    private float drawStatusIcon(Bitmap bmp, float x, float y, float animFactor, String annotation, Canvas cnvs)
    {
        Matrix matrix = new Matrix();
        matrix.preTranslate(x, y);
        matrix.preScale(animFactor, animFactor);
        int fac = (int)(3.14f / animFactor);

        cnvs.drawBitmap(bmp, matrix, paint);
        cnvs.drawText(annotation, x + bmp.getWidth() + 5.0f, y + -paint.getFontMetrics().top, paint);

        return bmp.getHeight() + 5;
    }

    public float draw(Canvas cnvs, float y)
    {
        float animFactor = (float)Math.abs (Math.sin((new Date().getTime() - view.getTimeSinceStart()) * 0.005f));
        String senderStr = smsSender.length() > 0 ? "(" + smsSender + ")" : "";

        y += drawStatusIcon(iconSMS.getBitmap(), 5.0f, y, 1.0f, String.valueOf(smsCount) + " не прочтено " + senderStr, cnvs);
        y += drawStatusIcon(iconDial.getBitmap(), 5.0f, y, 1.0f, String.valueOf(dialCount) + " пропущено", cnvs);
        y += drawStatusIcon(iconRAM.getBitmap(), 5.0f, y, 1.0f, memInfo.availMem / 1024 / 1024 + "мб доступно", cnvs);

        return y;
    }

}

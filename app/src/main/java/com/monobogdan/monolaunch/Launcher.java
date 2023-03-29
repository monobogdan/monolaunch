package com.monobogdan.monolaunch;

import android.app.Activity;
import android.app.StatusBarManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.monobogdan.monolaunch.widgets.ClockWidget;
import com.monobogdan.monolaunch.widgets.PlayerWidget;
import com.monobogdan.monolaunch.widgets.StatusWidget;

public class Launcher extends Activity {

    public class LauncherView extends View
    {
        final String TAG = "LauncherView";


        private Paint defaultPaint;
        private Paint fontPaint;
        private BitmapDrawable iconMenu;

        private ClockWidget clockWidget;
        private StatusWidget statusWidget;
        private long timeSinceStart;

        private PlayerWidget playerView;

        public LauncherView(Context ctx)
        {
            super(ctx);

            clockWidget = new ClockWidget(this);
            playerView = new PlayerWidget(this);

            defaultPaint = new Paint();
            defaultPaint.setColor(Color.WHITE);

            fontPaint = new Paint();
            fontPaint.setColor(Color.WHITE);
            fontPaint.setAntiAlias(true);
            fontPaint.setTextSize(16);

            statusWidget = new StatusWidget(this);

            iconMenu = (BitmapDrawable) ctx.getResources().getDrawable(R.drawable.list);
        }

        public long getTimeSinceStart() {
            return timeSinceStart;
        }

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            event.startTracking();
            return super.onKeyDown(keyCode, event);
        }

        @Override
        public boolean onKeyUp(int keyCode, KeyEvent event) {

            Log.i(TAG, "onKeyUp: " + keyCode);
            if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                switchToMainMenu();

                return true;
            }

            if(keyCode >= 7 && keyCode <= 16)
            {
                startActivity(getContext().getPackageManager().getLaunchIntentForPackage("com.android.dialer"));

                return true;
            }

            if(keyCode == KeyEvent.KEYCODE_BACK)
            {
                startActivity(getContext().getPackageManager().getLaunchIntentForPackage("com.android.mms"));
                return true;
            }

            if(keyCode == KeyEvent.KEYCODE_MENU)
            {
                startActivity(getContext().getPackageManager().getLaunchIntentForPackage("com.android.contacts"));
                return true;
            }

            if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
            {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://google.com"));

                startActivity(intent);
                return true;
            }

            if(keyCode == KeyEvent.KEYCODE_DPAD_UP)
            {
                try {
                    StatusBarManager barMan = (StatusBarManager) getContext().getSystemService("statusbar");
                    barMan.getClass().getMethod("expandNotificationsPanel").invoke(barMan);
                } catch (Exception e)
                {
                    Log.i(TAG, "onKeyUp: Failed to bring status");
                }

                //startActivity(getContext().getPackageManager().getLaunchIntentForPackage("com.android.alarmclock"));
                return true;
            }

            if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
            {
                try {
                    startActivity(getContext().getPackageManager().getLaunchIntentForPackage("com.whatsapp"));
                } catch(Exception e)
                {

                }
                return true;
            }

            if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
            {
                switchToTasks();

                return true;
            }



            return super.onKeyUp(keyCode, event);
        }


        private void drawBottomBar(Canvas canvas)
        {
            float metrics = fontPaint.getFontMetrics().bottom;
            float bottomLine = getHeight() - metrics - 3;
            float rightLine = getWidth() - fontPaint.measureText("Сообщения") - 5.0f;

            canvas.drawText("Контакты", 5.0f, bottomLine, fontPaint);
            canvas.drawText("Сообщения", rightLine, bottomLine, fontPaint);

            float centerLine = getWidth() / 2 - (iconMenu.getMinimumWidth() / 2);

            canvas.drawBitmap(iconMenu.getBitmap(), centerLine, getHeight() - iconMenu.getMinimumHeight() - 3, defaultPaint);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            float baseline = 15.0f;

            baseline += clockWidget.draw(canvas, baseline);
            baseline += statusWidget.draw(canvas, baseline);
            baseline += playerView.draw(canvas, baseline);

            clientWidth = getWindow().getDecorView().getWidth();
            clientHeight = getWindow().getDecorView().getHeight(); // HACK!!!

            drawBottomBar(canvas);
        }
    }

    private Drawable cachedBackground;
    private LauncherView launcherView;
    private AppListView appList;
    private DialerView dialerView;
    private Tasks tasks;

    private int clientHeight;
    private int clientWidth;

    public Drawable getCachedBackground() {
        return cachedBackground;
    }

    public void switchToHome()
    {
        setContentView(launcherView);
        launcherView.requestFocus();

        launcherView.setAlpha(0);
        launcherView.animate().
                alpha(1.0f).
                setDuration(350);
    }

    private void switchToDialer()
    {
        setContentView(dialerView);
        dialerView.requestFocus();
        dialerView.setTranslationY(clientHeight);
        dialerView.animate().
                setDuration(250).
                translationY(0);
    }

    private void switchToMainMenu()
    {
        setContentView(appList);
        appList.requestFocus();
        appList.setTranslationY(clientHeight);
        appList.animate().
                setDuration(250).
                translationY(0);
    }

    private void switchToTasks()
    {
        tasks.updateTaskList();
        setContentView(tasks);
        tasks.requestFocus();
        tasks.setTranslationX(clientWidth);
        tasks.animate().setDuration(250).translationX(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialerView = new DialerView(getApplicationContext());
        tasks = new Tasks(this);

        tasks.setFocusable(true);
        dialerView.setFocusable(true);

        launcherView = new LauncherView(getApplicationContext());
        appList = new AppListView(this);
        appList.setFocusable(true);
        launcherView.setFocusable(true);
        launcherView.requestFocus();

        cachedBackground = getWindow().getDecorView().getBackground();
        getWindow().setBackgroundDrawable(getWallpaper());

        switchToHome();
    }
}

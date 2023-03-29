package com.monobogdan.monolaunch.widgets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;
import android.view.View;

public class PlayerWidget {

    private View launcherView;
    private AudioManager manager;

    class AudioPlayerReceiver extends BroadcastReceiver
    {
        public boolean isPlaying;
        public String songName;
        public String artist;

        public AudioPlayerReceiver(Context ctx)
        {
            IntentFilter filter = new IntentFilter();

            filter.addAction("com.android.music.playstatechanged");
            ctx.registerReceiver(this, filter);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            isPlaying = intent.getBooleanExtra("playing", false);
            songName = intent.getStringExtra("track");
            artist = intent.getStringExtra("artist");

            Log.i("", "onReceive: Hi");

            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    launcherView.invalidate();
                }
            });
        }
    }

    private Paint paint;
    private AudioPlayerReceiver receiver;

    public PlayerWidget(View launcherView)
    {
        this.launcherView = launcherView;

        Context context = launcherView.getContext();

        receiver = new AudioPlayerReceiver(context);
        manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
    }

    public float draw(Canvas canvas, float y) {
        if(receiver.artist != null && receiver.songName != null && receiver.isPlaying)
            canvas.drawText(receiver.artist + " - " + receiver.songName, 0, y + -paint.getFontMetrics().top, paint);

        return -paint.getFontMetrics().top;
    }
}

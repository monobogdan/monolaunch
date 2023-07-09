package com.monobogdan.monolaunch;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Tasks extends ListView {

    public String PACKAGE_NAME;

    class AppTask
    {
        public String name;

        public Bitmap icon;
        public int id;
        public int memUsage;
    }

    private Launcher launcher;
    private BaseAdapter adapterImpl;
    private ActivityManager activityManager;
    private ArrayList<AppTask> tasks;

    public Tasks(Launcher launcher)
    {
        super(launcher.getApplicationContext());

        this.launcher = launcher;
        setBackgroundColor(Color.BLACK);

        tasks = new ArrayList<>();
        activityManager = (ActivityManager) launcher.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

        adapterImpl = new BaseAdapter() {
            @Override
            public int getCount() {
                return tasks.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                AppTask task = tasks.get(position);

                View view = launcher.getLayoutInflater().inflate(R.layout.task, parent, false);
                ((ImageView)view.findViewById(R.id.app_icon)).setImageBitmap(task.icon);
                ((TextView)view.findViewById(R.id.app_name)).setText(task.name);

                return view;
            }
        };

        setAdapter(adapterImpl);
    }

    public void updateTaskList()
    {
        List<ActivityManager.RunningTaskInfo> tInfo = activityManager.getRunningTasks(10);
        PackageManager pacMan = getContext().getPackageManager();

        tasks.clear();

        for (ActivityManager.RunningTaskInfo rt :
             tInfo) {
            try {
                AppTask appInfo = new AppTask();
                PackageInfo pacInfo = pacMan.getPackageInfo(rt.topActivity.getPackageName(), 0);

                if(pacInfo.packageName.equals("com.monobogdan.monolaunch") || pacInfo.packageName.equals("com.sprd.simple.launcher"))
                    continue;

                appInfo.id = rt.id;
                appInfo.icon = ((BitmapDrawable) pacInfo.applicationInfo.loadIcon(pacMan)).getBitmap();
                appInfo.name = pacMan.getApplicationLabel(pacInfo.applicationInfo).toString();

                tasks.add(appInfo);
            }
            catch (PackageManager.NameNotFoundException e)
            {

            }
        }

        adapterImpl.notifyDataSetChanged();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            launcher.switchToHome();
            return true;
        }
        if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
        {
            PACKAGE_NAME = getContext().getPackageName();
  /*          Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setComponent(new ComponentName = PACKAGE_NAME);
            launcher.startActivity(intent); */
        }

        return super.onKeyUp(keyCode, event);
    }
}

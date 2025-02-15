package com.shixin.app.utils;

import android.app.Activity;

public abstract class BackgroundTask {

    private final Activity activity;
    public BackgroundTask(Activity activity) {
        this.activity = activity;
    }

    private void startBackground() {
        new Thread(() -> {
            doInBackground();
            activity.runOnUiThread(this::onPostExecute);
        }).start();
    }
    public void execute(){
        startBackground();
    }

    public abstract void doInBackground();
    public abstract void onPostExecute();

}
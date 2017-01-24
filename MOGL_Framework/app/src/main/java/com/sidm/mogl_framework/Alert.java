package com.sidm.mogl_framework;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by Daniel on 24/1/2017.
 */

public class Alert {
    private GamePanelSurfaceView game;

    public Alert(GamePanelSurfaceView game)
    {
        this.game = game;
    }

    public void RunAlert()
    {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                game.alert.show();
            }
        },1000);
    }
}

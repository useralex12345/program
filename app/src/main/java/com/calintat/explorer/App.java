package com.calintat.explorer;

import android.app.Application;
import android.content.Context;

/**
 * Created by Анастасия on 12.05.2017.
 */

public class App extends Application {

    private static Context context;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context mContext) {
        context = mContext;
    }
}

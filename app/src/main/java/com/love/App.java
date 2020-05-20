package com.love;

import android.app.Application;

/**
 * @Description:
 * @Author: yxf
 * @CreateDate: 2020/5/20 15:41
 * @UpdateUser: yxf
 * @UpdateDate: 2020/5/20 15:41
 */
public class App extends Application {
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static App getInstance() {
        return instance;
    }
}

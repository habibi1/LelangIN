package com.tugasmobile.lelangin.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class ServiceWidget extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}

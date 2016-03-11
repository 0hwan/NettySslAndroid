/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lhsg.nettyapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SecureWebServerService extends Service {

    // Log tag for this class
    private static final String TAG = SecureWebServerService.class.getSimpleName();

    // A handle to the Netty SSL web server
    private NettyServer nettyServer;

    /**
     * Start the SSL web server and set an on-going notification
     */
    @Override
    public void onCreate() {
        super.onCreate();
        nettyServer = new NettyServer(this);
        nettyServer.start();
    }

    /**
     * Stop the SSL web server and remove the on-going notification
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        nettyServer.stop();
        stopForeground(true);
    }

    /**
     * Return null as there is nothing to bind
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

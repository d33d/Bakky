package org.nemux.bakky.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by nemux on 28/07/15.
 */
public class PushAndPull extends Service {
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    final static String TAG = "BakkyPushAndPull";

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {

            /* Send netstat command */
            try{
                java.lang.Process cmd = Runtime.getRuntime().exec("netstat");

                String line;

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(cmd.getInputStream()) );
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                }
                in.close();

                /*DataOutputStream outputStream = new DataOutputStream(cmd.getOutputStream());

                outputStream.writeBytes("ps\n");
                outputStream.flush();

                outputStream.writeBytes("exit\n");
                outputStream.flush();
                cmd.waitFor();
                */
            } catch (Exception e) {

                try {
                    throw new Exception(e);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            long endTime = System.currentTimeMillis() + 1800*1000;

            while (System.currentTimeMillis() < endTime) {
                Log.d(TAG," BAKKY WHILE");
                synchronized (this) {
                    try {
                        wait(endTime - System.currentTimeMillis());
                    } catch (Exception e) {
                    }
                }
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Bakky service starting", Toast.LENGTH_SHORT).show();
        Log.d(TAG, " BAKKY Service Starting");

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Bakky service done", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Bakky service done");
    }
}
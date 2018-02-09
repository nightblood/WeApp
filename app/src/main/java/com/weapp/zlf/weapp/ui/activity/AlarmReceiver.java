package com.weapp.zlf.weapp.ui.activity;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.weapp.zlf.weapp.R;

import static android.content.Context.KEYGUARD_SERVICE;

/**
 * Created by zhuliangfei on 2018/2/8.
 */

public class AlarmReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 1000;
    private static final String TAG = AlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("NOTIFICATION")) {
            String title = intent.getStringExtra("title");
            String content = intent.getStringExtra("content");
            Log.d(TAG, "onReceive: " + title + ", " + content);
//            wakeUpAndUnlock(context);
            KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            if (km.inKeyguardRestrictedInputMode()) {
                Intent alarmIntent = new Intent(context, LockScreenActivity.class);
                alarmIntent.putExtra("title", title);
                alarmIntent.putExtra("content", content);
                alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(alarmIntent);
                playSound(context);

            } else {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder notifyBuilder =
                        new NotificationCompat.Builder( context )
                                .setContentTitle( title )
                                .setContentText(TextUtils.isEmpty(content) ? " " : content)
                                .setSmallIcon( R.mipmap.ic_launcher)
                                // 点击消失
                                .setAutoCancel( true )
                                // 设置该通知优先级
                                .setPriority( Notification.PRIORITY_MAX )
                                .setLargeIcon( BitmapFactory.decodeResource( context.getResources(), R.mipmap.ic_launcher) )
                                .setTicker( "ticker" )
                                // 通知首次出现在通知栏，带上升动画效果的
                                .setWhen( System.currentTimeMillis() )
                                // 通知产生的时间，会在通知信息里显示
                                // 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
                                .setDefaults( Notification.DEFAULT_VIBRATE | Notification.DEFAULT_ALL | Notification.DEFAULT_SOUND );
//            PendingIntent resultPendingIntent = PendingIntent.getActivity( mContext, 0, mResultIntent, PendingIntent.FLAG_UPDATE_CURRENT );
//            notifyBuilder.setContentIntent( resultPendingIntent );
                manager.notify( NOTIFICATION_ID, notifyBuilder.build() );
            }
        }

    }

    private void playSound(Context context) {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(500);
    }


}
package com.example.finalproject

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat


/*
========= 물 마시기 알람 브로드캐스트리시버 (WaterAlarmReceiver) =========

- AlarmManager로 예약된 알람이 울릴 때 실행되는 브로드캐스트 수신자
- 알림(Notification)을 생성해서 사용자에게 "물 마실 시간" 안내
- Android 8.0 이상에서는 알림 채널(NotificationChannel)을 필수로 등록해야 함

*/

class WaterAlarmReceiver : BroadcastReceiver() {

    // 알람이 울릴 때 실행
    override fun onReceive(context: Context?, intent: Intent?) {
        // 디버깅 용
        Log.d("AlarmTest", "WaterAlarmReceiver 호출됨!!!")
        if (context == null) return

        // 알림 채널 정보
        val channelId = "water_channel"
        val channelName = "수분 섭취 알림"

        // 안드로이드 8.0 이상 알림 채널 등록
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "주기적인 수분 섭취를 알려줘요"
                setShowBadge(true) // 앱 아이콘에 배지 표시 허용
            }

            // 알림 매니저 통해 채널 등록
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // 알림 생성
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_water) // 상태바
            .setContentTitle("물 마실 시간이에요!")
            .setContentText("지금 한 잔 마시고 몸을 깨워볼까요? 💧")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // 탭하면 알림 자동으로 사라짐
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL) // 배지 아이콘 설정
            .setNumber(1) // 배지에 표시할 숫자

        // 알림을 실제 화면 표시
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1001, builder.build()) // 알림 ID 고정
    }
}
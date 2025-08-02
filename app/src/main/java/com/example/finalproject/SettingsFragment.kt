package com.example.finalproject

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

/*
========= 설정 화면 (SettingsFragment) =========

- 사용자가 닉네임과 목표 수분 섭취량을 수정할 수 있는 화면
- 수분 섭취 알림을 ON/OFF할 수 있음
- 모든 설정은 SharedPreferences에 저장됨
- 알림은 AlarmManager를 통해 주기적으로 울림

*/

class SettingsFragment : Fragment() {

    // UI 요소 변수들
    private lateinit var editNickname: EditText
    private lateinit var btnSaveNickname: Button
    private lateinit var editGoal: EditText
    private lateinit var btnSaveGoal: Button
    private lateinit var switchAlarm: Switch

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val prefs = requireActivity().getSharedPreferences("waterPrefs", Context.MODE_PRIVATE)

        // UI 연결
        editNickname = view.findViewById(R.id.editNickname)
        btnSaveNickname = view.findViewById(R.id.btnSaveNickname)
        editGoal = view.findViewById(R.id.editGoal)
        btnSaveGoal = view.findViewById(R.id.btnSaveGoal)
        switchAlarm = view.findViewById(R.id.switchAlarm)

        // 저장된 설정값 표시
        editNickname.setText(prefs.getString("nickname", ""))
        editGoal.setText(prefs.getInt("goalAmount", 2000).toString())
        switchAlarm.isChecked = prefs.getBoolean("alarmEnabled", false)

        // 닉네임 저장 버튼
        btnSaveNickname.setOnClickListener {
            val newName = editNickname.text.toString().trim()
            if (newName.isNotEmpty()) {
                prefs.edit().putString("nickname", newName).apply()
                Toast.makeText(context, "닉네임이 저장되었습니다!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "닉네임을 입력해주세요!", Toast.LENGTH_SHORT).show()
            }
        }

        // 목표 수분량 저장 버튼
        btnSaveGoal.setOnClickListener {
            val goalText = editGoal.text.toString().trim()
            val goal = goalText.toIntOrNull()

            // 정상 입력값 요구
            if (goal == null || goal < 500 || goal > 5000) {
                Toast.makeText(context, "500~5000ml 사이의 값을 입력해주세요!", Toast.LENGTH_SHORT).show()
            } else {
                prefs.edit().putInt("goalAmount", goal).apply()
                Toast.makeText(context, "목표 수분량이 저장되었습니다!", Toast.LENGTH_SHORT).show()
            }
        }

        // 알림 ON/OFF 스위치
        switchAlarm.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("alarmEnabled", isChecked).apply()

            if (isChecked) {
                setWaterAlarm(requireContext())
                Toast.makeText(context, "알림이 켜졌어요!", Toast.LENGTH_SHORT).show()
            } else {
                cancelWaterAlarm(requireContext())
                Toast.makeText(context, "알림이 꺼졌어요!", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    // 알림 예약 함수 (AlarmManager로 알람 반복 설정)
    private fun setWaterAlarm(context: Context) {
        // 시스템 알람 서비스 가져옴
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // 알람 울릴 때 실행될 리시버 클래스 지정
        val intent = Intent(context, WaterAlarmReceiver::class.java)
        // 알림 예약용 PendingIntent 생성
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

//       배포용
//       val triggerTime = System.currentTimeMillis() + 2 * 60 * 60 * 1000 // 2시간 후
//       val interval = 2 * 60 * 60 * 1000L // 2시간마다 반복

        // 앱 개발 중 알림 테스트를 위해서 알림 간격 조정
        val triggerTime = System.currentTimeMillis() + 5 * 1000 // 5초 후
        val interval = 1 * 60 * 1000L // 1분마다 반복


        // 알람 설정 (반복 알람)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, // 기기가 꺼져있어도 깨워서 알림 실행
            triggerTime, // 첫 알림 시간
            interval, // 반복 주기
            pendingIntent // 실행 대상
        )

        // 개발 확인용
        Log.d("AlarmTest", "알람 예약 시도됨: trigger=${triggerTime}, interval=${interval}")

    }

    // 알림 취소 함수
    private fun cancelWaterAlarm(context: Context) {

        // 시스템에서 AlarmManager 인스턴스를 가져옴
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // 알림 ㅇㅖ약과 동일학 인텐트 구성
        val intent = Intent(context, WaterAlarmReceiver::class.java)
        //등록된 알람 식별 위해 동일 PendingIntent 생성
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0, // 식별자
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 해당 PendingIntent에 연결된 알람 시스템에서 취소
        alarmManager.cancel(pendingIntent)
    }
}

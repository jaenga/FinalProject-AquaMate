package com.example.finalproject

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*


/*
========= 메인화면 (HomeFragment) =========

- 메인화면의 "오늘의 물 섭취량" 표시 + 입력 기능 담당
- 기본 200ml 버튼, 직접 입력 칸, 캐릭터 상태 반영 기능 포함
- SharedPreferences를 사용해 하루별 / 시간별 섭취 기록 저장
- 날짜가 바뀌면 자동으로 기록 초기화 (checkDateReset)

*/

class HomeFragment : Fragment() {

    // 테스트용 날짜 강제 고정 (배포 시 false로 변경)
    private val DEBUG_MODE = true

    // 현재 수분 섭취량 및 목표량 (초기값)
    private var waterAmount = 0
    private var goalAmount = 2000
    private lateinit var nickname: String

    // UI 컴포넌트
    private lateinit var greetText: TextView
    private lateinit var waterText: TextView
    private lateinit var goalText: TextView
    private lateinit var characterImage: ImageView
    private lateinit var customAmountInput: EditText
    private lateinit var customDrinkButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // xml에 정의된 UI 요소 연결
        val drinkButton = view.findViewById<Button>(R.id.drinkButton)
        greetText = view.findViewById(R.id.greetText)
        waterText = view.findViewById(R.id.waterText)
        goalText = view.findViewById(R.id.goalText)
        characterImage = view.findViewById(R.id.characterImage)
        customAmountInput = view.findViewById(R.id.customAmountInput)
        customDrinkButton = view.findViewById(R.id.customDrinkButton)

        // 화면 진입 시 데이터 업데이트
        updateUserInfoFromPrefs()
        updateCharacterImage()

        // 기본 +200ml 버튼
        drinkButton.setOnClickListener {
            handleWaterIntake(200)
        }

        // 직접 입력 버튼
        customDrinkButton.setOnClickListener {
            val input = customAmountInput.text.toString()
            val customAmount = input.toIntOrNull()
            if (customAmount == null || customAmount <= 0) {
                Toast.makeText(requireContext(), "올바른 양을 입력해주세요!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            handleWaterIntake(customAmount)
            customAmountInput.text.clear()
        }

        return view
    }

    // 홈 화면 자동 새로고침 (닉네임, 목표량, 섭취량 정보 재호출)
    override fun onResume() {
        super.onResume()
        Log.d("HomeFragment", " onResume 호출됨!")
        updateUserInfoFromPrefs()
    }

    // 물 마신 기록 반영하는 함수
    private fun handleWaterIntake(amount: Int) {
        val prefs = requireActivity().getSharedPreferences("waterPrefs", Context.MODE_PRIVATE)
        val todayKey = "intake_${getTodayKey()}"
        val currentHour = getCurrentHour()
        val hourlyKey = "intake_${getTodayKey()}_${String.format("%02d", currentHour)}"

        // 총량 업데이트
        waterAmount = prefs.getInt(todayKey, 0) + amount
        prefs.edit().putInt(todayKey, waterAmount).apply()

        // 시간별 데이터 업데이트
        val hourlyAmount = prefs.getInt(hourlyKey, 0) + amount
        prefs.edit().putInt(hourlyKey, hourlyAmount).apply()

        // UI 반영
        waterText.text = "오늘 마신 물: ${waterAmount}ml"
        characterImage.setImageResource(R.drawable.recovering) // 물 마실 때 잠깐 회복 모습

        // 캐릭터 상태 업데이트
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isAdded) return@postDelayed
            updateCharacterImage() // 1초 후 상태에 맞는 캐릭터로 변경
            if (waterAmount >= goalAmount) {
                // 목표 달성 시 토스트 기능으로 성공 알림
                Toast.makeText(requireContext(), "🎉 목표 달성! 너무 잘했어요!", Toast.LENGTH_SHORT).show()
            }
        }, 1000)
    }

    private fun checkDateReset(prefs: SharedPreferences) {
        val today = getTodayKey()
        val lastDate = prefs.getString("lastUpdatedDate", null)

        if (lastDate != today) {
            prefs.edit().apply {
                putString("lastUpdatedDate", today)
                putInt("intake_$today", 0)
                for (hour in 0..23) {
                    putInt("intake_${today}_${String.format("%02d", hour)}", 0)
                }
                apply()
            }
        }
    }

    // 닉네임, 목표량, 오늘 섭취향 불러오기 UI 갱신
    private fun updateUserInfoFromPrefs() {
        val prefs = requireActivity().getSharedPreferences("waterPrefs", Context.MODE_PRIVATE)
        checkDateReset(prefs) // 날짜 바뀌었는지 검사하고 초기화

        nickname = prefs.getString("nickname", "사용자") ?: "사용자"
        goalAmount = prefs.getInt("goalAmount", 2000)

        val todayKey = "intake_${getTodayKey()}"
        waterAmount = prefs.getInt(todayKey, 0)

        greetText.text = "${nickname}님, 오늘 마신 물은 아래와 같아요!"
        goalText.text = "목표 수분 섭취량: ${goalAmount}ml"
        waterText.text = "오늘 마신 물: ${waterAmount}ml"
    }

    // 섭취량 비율에 따라 캐릭터 상태 이미지 변경
    private fun updateCharacterImage() {
        val percentage = (waterAmount.toFloat() / goalAmount) * 100

        when {
            percentage >= 100 -> characterImage.setImageResource(R.drawable.happy)      // 목표 달성
            percentage >= 75 -> characterImage.setImageResource(R.drawable.normal)     // 거의 달성
            percentage >= 50 -> characterImage.setImageResource(R.drawable.low)        // 보통
            percentage >= 25 -> characterImage.setImageResource(R.drawable.thirsty)    // 목마름
            else -> characterImage.setImageResource(R.drawable.thirsty)                // 목마름
        }
    }

    // 날짜를 yyyyMMdd 형식으로 반환
    private fun getTodayKey(): String {
        val date = if (DEBUG_MODE) {
            // 테스트용 날짜 고정 (6월 17일. 배포 시 기능 사용 X)
            val calendar = Calendar.getInstance()
            calendar.set(2025, 5, 17) // 월은 0부터 시작 (5 = 6월)
            calendar.time
        } else {
            // 실제 오늘 날짜
            Date()
        }
        return SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(date)
    }

    // 현재 시간 반환
    private fun getCurrentHour(): Int {
        val calendar = Calendar.getInstance()
        if (DEBUG_MODE) {
            calendar.set(2025, 5, 17) // 테스트 날짜로 설정
        }
        return calendar.get(Calendar.HOUR_OF_DAY)
    }
}
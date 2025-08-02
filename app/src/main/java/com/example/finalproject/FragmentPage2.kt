package com.example.finalproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment


/*
====== 온보딩 3단계 화면 (FragmentPage2) ========

- 사용자가 오늘 목표 수분 섭취량(ml)을 입력
- 닉네임이 입력되어 있어야만 시작 가능
- 목표 수치를 SharedPreferences에 저장하고 메인 화면(MainActivity)으로 이동

 */

class FragmentPage2 : Fragment() {
    // 이전과 동일
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_page2, container, false)

        // UI 요소 연결 (fragment_page2.xml)
        // 사용자가 목표 수분 입력하는 칸
        val goalInput = view.findViewById<EditText>(R.id.goalInput)
        // 시작하기 버튼
        val startButton = view.findViewById<Button>(R.id.startButton)
        // 목표 수분량, 닉네임을 저장하기 위한 저장소 객체
        val prefs = requireActivity().getSharedPreferences("waterPrefs", Context.MODE_PRIVATE)

        // '시작하기' 버튼 눌렀을 때 동작
        startButton.setOnClickListener {
            val goalText = goalInput.text.toString().trim()
            val goalAmount = goalText.toIntOrNull()

            // 닉네임 미입력 시 진행 막기
            val nickname = prefs.getString("nickname", "")?.trim()
            if (nickname.isNullOrBlank()) {
                Toast.makeText(context, "닉네임을 먼저 입력해주세요!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 수분 목표값 유효성 검사
            if (goalAmount == null || goalAmount <= 0) {
                goalInput.error = "올바른 목표 수치를 입력해주세요!"
                return@setOnClickListener
            }

            if (goalAmount < 500) {
                goalInput.error = "너무 적습니다! 최소 500ml 이상 입력해주세요!"
                return@setOnClickListener
            }

            if (goalAmount > 5000) {
                goalInput.error = "너무 많습니다! 5000ml 이하로 입력해주세요!"
                return@setOnClickListener
            }

            // SharedPreferences에 저장 + 앱 최초 실행 여부 기록
            prefs.edit()
                .putInt("goalAmount", goalAmount)
                .putBoolean("hasRunBefore", true) // IntroActivity 다시 뜨지 않도록
                .apply()

            // 메인 화면으로 이동
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish() // IntroActivity 종료
        }

        return view
    }
}

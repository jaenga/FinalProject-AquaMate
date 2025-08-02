package com.example.finalproject

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment


/*
====== 온보딩 2단계 화면 (FragmentPage1) ========

- 사용자 닉네임을 입력받는 ViewPager의 두 번째 페이지
- 입력된 닉네임은 SharedPreferences에 저장됨
- 이전에 저장된 닉네임이 있으면 입력창에 자동으로 보여줌

 */

class FragmentPage1 : Fragment() {
    // 프래그먼트 뷰 생성 시 호출
    override fun onCreateView(
        // Page0과 동일
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 닉네임 입력 화면 layout 파일을 View로 생성 (fragment_page1.xml)
        val view = inflater.inflate(R.layout.fragment_page1, container, false)

        // 입력한 View 객체 가져오기
        val nicknameInput = view.findViewById<EditText>(R.id.nicknameInput)

        // SharedPreferences 객체 불러오기 (앱 데이터 저장소)
        val prefs = requireActivity().getSharedPreferences("waterPrefs", Context.MODE_PRIVATE)

        //  이전에 저장된 닉네임 있으면 자동으로 입력창에 보여주기 (viewPage 좌우로 스와이프 시 기록 저장)
        val savedNickname = prefs.getString("nickname", "")
        nicknameInput.setText(savedNickname)

        // 입력이 끝날 때 저장 (포커스 잃었을 때 자동 저장)
        nicknameInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { // 포커스 잃었을 때만 저장
                val nickname = nicknameInput.text.toString().trim()
                if (nickname.isNotEmpty()) { // 닉네임 입력 칸이 비어있지 않을 때만 저장
                    prefs.edit().putString("nickname", nickname).apply()
                }
            }
        }

        return view
    }
}

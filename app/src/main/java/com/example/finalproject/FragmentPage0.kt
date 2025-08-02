package com.example.finalproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/*
====== 온보딩 1단계 화면 (FragmentPage0) ========

- IntroActivity의 ViewPager2에 연결된 첫 번째 페이지
- 앱 이름, 로고(물방울), 소개 문구를 표시하는 단순한 화면

 */

class FragmentPage0 : Fragment() {
    // 프래그먼트 뷰 생성 시 호출
    override fun onCreateView(
        inflater: LayoutInflater, // fragment_page0.xml을 View로 바꿔줌
        container: ViewGroup?, // 프래그먼트가 붙게 될 부모 ViewGroup
        savedInstanceState: Bundle? // 상태 복원용
    ): View? {

        // fragment_page0.xml 파일을 inflate 해서 화면에 보여줄 View 생성
        return inflater.inflate(R.layout.fragment_page0, container, false)
    }
}

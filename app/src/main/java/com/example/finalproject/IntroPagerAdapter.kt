package com.example.finalproject

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.fragment.app.Fragment

/*
========= 온보딩 프래그먼터 연결 어댑터 (IntroPagerAdapter) =========

- ViewPager2에 연결되는 FragmentStateAdapter
- IntroActivity의 ViewPager에서 각 페이지에 해당하는 프래그먼트를 반환함
- 총 3개의 온보딩 페이지 구성:
    0번 → FragmentPage0: 앱 소개
    1번 → FragmentPage1: 닉네임 입력
    2번 → FragmentPage2: 목표 수분량 입력

*/

class IntroPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    // 전체 페이지 수 3
    override fun getItemCount(): Int = 3

    // position에 따라 해당 프래그먼트 생성 및 반환
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FragmentPage0() // 앱 소개 화면
            1 -> FragmentPage1() // 닉네임 입력 화면
            else -> FragmentPage2() // 목표 수분량 입력 화면
        }
    }
}

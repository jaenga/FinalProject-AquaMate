package com.example.finalproject

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2

/*
========= 앱 최초 실행 시 (IntroActivity) =========

- 앱 최초 실행 시 보여지는 온보딩 액티비티
- ViewPager2로 구성된 3단계 화면: 앱 소개 → 닉네임 입력 → 목표 수분량 입력
- SharedPreferences로 설정된 값이 이미 있으면 MainActivity로 바로 이동
- Android 13 이상에서는 알림 권한 요청 처리 포함

*/

class IntroActivity : AppCompatActivity() {

    // 페이지 인디케이터 점 3개
    private lateinit var indicator0: View
    private lateinit var indicator1: View
    private lateinit var indicator2: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // SharedPreferences 가져오기
        val prefs = getSharedPreferences("waterPrefs", Context.MODE_PRIVATE)

        // 개발 중 인트로 화면 보기 위한 초기화 옵션
        val devResetMode = true  // 배포 전에 반드시 false로 바꿔야함!
        if (devResetMode) {
            prefs.edit().clear().apply()
        }

        // 앱 최초 실행 여부 확인
        val hasRunBefore = prefs.getBoolean("hasRunBefore", false)

        // Android 13 이상에서는 알림 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this as Activity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }

        // 이미 앱 설정이 완료된 유저라면 메인으로 바로 이동
        if (hasRunBefore && !devResetMode) {
            startActivity(Intent(this, MainActivity::class.java))
            finish() // 인트로 종료
            return
        }

        // ViewPager를 포함한 온보딩 화면 보여주기
        setContentView(R.layout.activity_intro)

        // ViewPager2에 FragmentPagerAdapter 연결
        val pager = findViewById<ViewPager2>(R.id.viewPager)
        pager.adapter = IntroPagerAdapter(this)

        // 인디케이터 초기화
        setupPageIndicator(pager)
    }

    // 페이지 인디케이터 View 연결 및 동작 처리
    private fun setupPageIndicator(pager: ViewPager2) {
        // 인디케이터 View들 연결
        indicator0 = findViewById(R.id.indicator0)
        indicator1 = findViewById(R.id.indicator1)
        indicator2 = findViewById(R.id.indicator2)

        // 페이지 넘길 때마다 인디케이터 업데이트
        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicator(position)
            }
        })

        // 초기 인디케이터 설정 (첫 번째 페이지)
        updateIndicator(0)
    }

    // 현재 페이지에 맞춰 인디케이터 UI 업데이트
    private fun updateIndicator(position: Int) {
        // 모든 인디케이터를 비활성 상태로 초기화
        indicator0.setBackgroundResource(R.drawable.page_indicator_inactive)
        indicator1.setBackgroundResource(R.drawable.page_indicator_inactive)
        indicator2.setBackgroundResource(R.drawable.page_indicator_inactive)

        // 현재 페이지에 해당하는 인디케이터를 활성 상태로 변경
        when (position) {
            0 -> indicator0.setBackgroundResource(R.drawable.page_indicator_active)
            1 -> indicator1.setBackgroundResource(R.drawable.page_indicator_active)
            2 -> indicator2.setBackgroundResource(R.drawable.page_indicator_active)
        }
    }
}
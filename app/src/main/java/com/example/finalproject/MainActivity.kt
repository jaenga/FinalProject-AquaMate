package com.example.finalproject

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.finalproject.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


/*
========= 앱 메인 (MainActivity) =========

- 온보딩 완료 후 진입하는 메인 액티비티
- 홈, 통계, 설정 탭을 하단 BottomNavigationView로 구성
- 각 탭 클릭 시 해당 프래그먼트로 교체
- Android 13+ 기기에서는 알림 권한을 한 번 더 요청할 수 있음

*/

class MainActivity : AppCompatActivity() {

    // 뷰 바인딩 객체 (activity_main과 연결)
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // activity_main.xml을 바인딩해서 View 객체로 설정
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 알림 권한 요청 (Android 13+)
        requestNotificationPermission()

        // 앱 처음 실행 시 홈 프래그먼트 기본으로 보여줌
        replaceFragment(HomeFragment())

        // BottomNavigationView도 홈으로 선택 상태 설정
        binding.bottomNav.selectedItemId = R.id.nav_home

        // 탭 눌렀을 때 프래그먼트 전환
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val currentFragment =
                        supportFragmentManager.findFragmentById(R.id.fragment_container)
                    if (currentFragment is HomeFragment) {
                        // 이미 홈이면 강제로 새로고침
                        supportFragmentManager.beginTransaction()
                            .detach(currentFragment)
                            .attach(currentFragment)
                            .commit()
                    } else { // 홈이 아니면 새로 띄우기
                        replaceFragment(HomeFragment())
                    }
                }

                R.id.nav_settings -> replaceFragment(SettingsFragment())
                R.id.nav_stats -> replaceFragment(StatsFragment())
            }
            true // 클릭  이벤트 소비 완료
        }
    }

    // 프래그먼트 교체 함수 (공통 처리)
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // 알림 권한 없을 경우 요청 (Android 13+)
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }
}
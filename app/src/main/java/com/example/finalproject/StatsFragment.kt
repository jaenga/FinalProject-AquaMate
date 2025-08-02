package com.example.finalproject

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*


/*
========= 통계 화면 (StatsFragment) =========

- 주간 수분 섭취량 차트와 시간대별 섭취량 차트를 보여주는 프래그먼트
- MPAndroidChart 라이브러리를 사용하여 바 차트 구성
- SharedPreferences에 저장된 섭취 데이터를 불러와 시각화함

*/

class StatsFragment : Fragment() {

    // 테스트 모드 - 배포할 때는 false로 변경
    private val DEBUG_MODE = true

    // 차트 뷰
    private lateinit var weeklyChart: BarChart
    private lateinit var hourlyChart: BarChart

    // 날짜 포맷
    private val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    private val displayFormat = SimpleDateFormat("M/d", Locale.getDefault())

    // 통계 계산
    private lateinit var weeklyAverageText: TextView
    private lateinit var achievementRateText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stats, container, false)
        weeklyChart = view.findViewById(R.id.weeklyChart) // 주간 섭취량
        hourlyChart = view.findViewById(R.id.hourlyChart) // 시간대별 섭취량

        weeklyAverageText = view.findViewById(R.id.weeklyAverage) // 평균
        achievementRateText = view.findViewById(R.id.achievementRate) // 달성률
        // 차트 데이터 그리기
        drawWeeklyChart() // 월~ 일 7일간 섭취량
        drawHourlyChart() // 0~ 23시 시간대별 섭취량

        return view
    }

    // 주간 섭취량 차트
    private fun drawWeeklyChart() {
        val prefs = requireContext().getSharedPreferences("waterPrefs", Context.MODE_PRIVATE)
        val entries = ArrayList<BarEntry>() // Y값 (섭취량)
        val labels = ArrayList<String>() // X 라벨 (날짜)

        val calendar = Calendar.getInstance()

        // 테스트용 날짜 설정
        if (DEBUG_MODE) {
            calendar.set(2025, 5, 17) // 6월 9일
            Log.d("통계확인", "🔧 DEBUG_MODE: 6월 9일로 설정됨")
        }

        // 주의 시작을 월요일로 설정 (중요!!!!!!)
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        Log.d("통계확인", "이번 주 시작 날짜 (월): ${dateFormat.format(calendar.time)}")

        var weeklyTotal = 0 // 주간 총합 계산용

        // 월요일~일요일 7일치 데이터 수집
        for (i in 0 until 7) {
            // 현재 날짜를 기반으로 SharedPreferences 키 생성
            val dateKey = "intake_${dateFormat.format(calendar.time)}"
            // 해당 날짜에 저장된 섭취량 불러오기 (기록 없으면 0)
            val amount = prefs.getInt(dateKey, 0)
            // BarEntry(x값, y값): x는 요일 인덱스(0~6), y는 수분량
            entries.add(BarEntry(i.toFloat(), amount.toFloat()))
            // X축 라벨용 날짜 텍스트 추가
            labels.add(displayFormat.format(calendar.time))
            // 총합 누적
            weeklyTotal += amount
            // 날짜 하루 뒤로 이동 + 다음 날 데이터로 넘어감
            calendar.add(Calendar.DATE, 1) // 하루씩 이동
        }

        Log.d("통계확인", "그래프 라벨들: $labels")

        // 주간 평균, 달성률 계산
        val weeklyAverage = (weeklyTotal / 7)
        val goal = prefs.getInt("goalAmount", 2000)
        val targetTotal = goal * 7
        val achievementRate = if (targetTotal > 0) (weeklyTotal / targetTotal.toFloat() * 100).coerceAtMost(999.9f) else 0f

        // UI에 표시
        weeklyAverageText.text = "${weeklyAverage}ml"
        achievementRateText.text = String.format("%.1f%%", achievementRate)

        // 차트용 데이터셋 구성
        val dataSet = BarDataSet(entries, "일별 섭취량(ml)")
        dataSet.color = Color.parseColor("#2196F3")
        val data = BarData(dataSet)
        data.barWidth = 0.4f

        // 차트 설정
        weeklyChart.data = data
        weeklyChart.setFitBars(true) // 바 너비 자동 맞춤
        weeklyChart.description.isEnabled = false
        weeklyChart.axisRight.isEnabled = false

        // 목표 수분량을 선으로 표시함
        val limitLine = LimitLine(goal.toFloat(), "목표 ${goal}ml")
        limitLine.lineColor = Color.RED
        limitLine.lineWidth = 2f
        limitLine.textColor = Color.RED
        weeklyChart.axisLeft.addLimitLine(limitLine)

        // X축
        val xAxis = weeklyChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)

        weeklyChart.invalidate() // 차트 다시 그리기
    }


    // 하루 시간대별 섭취량 차트 구성
    private fun drawHourlyChart() {
        val prefs = requireContext().getSharedPreferences("waterPrefs", Context.MODE_PRIVATE)

        for (key in prefs.all.keys) {
            if (key.startsWith("intake_")) {
                //Log.d("확인용", "$key = ${prefs.getInt(key, 0)}")
            }
        }

        // 테스트용 날짜 설정
        val today = if (DEBUG_MODE) {
            val calendar = Calendar.getInstance()
            calendar.set(2025, 5, 17) // 6월 9일
            dateFormat.format(calendar.time)
        } else {
            dateFormat.format(Date())
        }

        val entries = ArrayList<BarEntry>()

        // 0~23시 시간대별 섭취량 불러옴
        for (hour in 0..23) {
            // SharedPreferences에 저장된 키 형식
            val hourKey = String.format("intake_%s_%02d", today, hour)
            // 시간대 섭취량 불러옴 (기록 없으면 0)
            val amount = prefs.getInt(hourKey, 0)
            // X축: 시간 / Y축: 섭취량
            entries.add(BarEntry(hour.toFloat(), amount.toFloat()))
        }

        // 데이터 셋 구성
        val dataSet = BarDataSet(entries, "시간대별 섭취량(ml)")
        dataSet.color = Color.parseColor("#9C27B0")
        val data = BarData(dataSet)
        data.barWidth = 0.9f

        // 차트 적용
        hourlyChart.data = data
        hourlyChart.setFitBars(true)
        hourlyChart.description.isEnabled = false
        hourlyChart.axisRight.isEnabled = false

        // X축 시간 라벨 설정
        val xAxis = hourlyChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter((0..23).map { "${it}시" })
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f // X값 간격 1씩 고정
        xAxis.setDrawGridLines(false)

        hourlyChart.invalidate() // 차트 다시 그리기
    }
}
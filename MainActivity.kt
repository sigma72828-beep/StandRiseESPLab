package com.example.standriseesplab

import android.app.Activity
import android.os.Bundle
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.Gravity
import android.view.View
import android.widget.*

class MainActivity : Activity() {
    private var esp = true
    private var names = true
    private var distance = true
    private var boxes = true
    private var accent = Color.rgb(53, 200, 255)
    private lateinit var scene: DemoScene

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(16, 19, 27)
        window.navigationBarColor = Color.rgb(16, 19, 27)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(14), dp(18), dp(14))
            setBackgroundColor(Color.rgb(16, 19, 27))
        }
        val title = TextView(this).apply {
            text = "STANDRISE  /  ESP LAB"
            textSize = 20f
            setTextColor(accent)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        root.addView(title)
        root.addView(label("ТЕСТОВАЯ СЦЕНА · ЛОКАЛЬНЫЕ ЦЕЛИ"))
        scene = DemoScene()
        root.addView(scene, LinearLayout.LayoutParams(-1, 0, 1f).apply {
            topMargin = dp(12); bottomMargin = dp(12)
        })

        root.addView(label("VISUALS"))
        root.addView(toggle("ESP", true) { esp = it; scene.updateOptions(esp, names, distance, boxes, accent) })
        root.addView(toggle("Names", true) { names = it; scene.updateOptions(esp, names, distance, boxes, accent) })
        root.addView(toggle("Distance", true) { distance = it; scene.updateOptions(esp, names, distance, boxes, accent) })
        root.addView(toggle("Boxes", true) { boxes = it; scene.updateOptions(esp, names, distance, boxes, accent) })

        root.addView(label("ЦВЕТ ПОДСВЕТКИ"))
        val colors = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        listOf(
            "Голубой" to Color.rgb(53, 200, 255),
            "Зелёный" to Color.rgb(80, 240, 150),
            "Фиолетовый" to Color.rgb(190, 110, 255),
            "Красный" to Color.rgb(255, 90, 100)
        ).forEach { (name, color) ->
            val b = Button(this).apply {
                text = name
                textSize = 10f
                setTextColor(Color.WHITE)
                setBackgroundColor(color)
                setOnClickListener {
                    accent = color
                    scene.updateOptions(esp, names, distance, boxes, accent)
                }
            }
            colors.addView(b, LinearLayout.LayoutParams(0, dp(44), 1f).apply {
                setMargins(dp(2), dp(4), dp(2), dp(4))
            })
        }
        root.addView(colors)
        root.addView(label("Демо-данные; это не подключение к StandRise Rework."))
        setContentView(root)
        scene.updateOptions(esp, names, distance, boxes, accent)
    }

    private fun label(text: String) = TextView(this).apply {
        this.text = text
        textSize = 11f
        setTextColor(Color.rgb(150, 165, 185))
        setPadding(0, dp(8), 0, dp(4))
    }

    private fun toggle(text: String, initial: Boolean, changed: (Boolean) -> Unit): Switch {
        return Switch(this).apply {
            this.text = text
            isChecked = initial
            textSize = 14f
            setTextColor(Color.WHITE)
            buttonTintList = android.content.res.ColorStateList.valueOf(accent)
            thumbTintList = android.content.res.ColorStateList.valueOf(accent)
            setPadding(dp(4), dp(2), dp(4), dp(2))
            setOnCheckedChangeListener { _, checked -> changed(checked) }
        }
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    inner class DemoScene : View(this@MainActivity) {
        private val boxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE; strokeWidth = 3f
        }
        private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 30f; typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        private val bgPaint = Paint().apply { color = Color.rgb(24, 29, 41) }
        private var showEsp = true
        private var showNames = true
        private var showDistance = true
        private var showBoxes = true
        private var color = accent

        fun updateOptions(e: Boolean, n: Boolean, d: Boolean, b: Boolean, c: Int) {
            showEsp = e; showNames = n; showDistance = d; showBoxes = b; color = c
            invalidate()
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            canvas.drawRoundRect(RectF(0f, 0f, width.toFloat(), height.toFloat()), 18f, 18f, bgPaint)
            textPaint.color = Color.rgb(135, 150, 173)
            textPaint.textSize = 26f
            canvas.drawText("DEMO TARGETS", 24f, 40f, textPaint)
            if (!showEsp) {
                canvas.drawText("ESP выключен", 24f, 82f, textPaint)
                return
            }
            val targets = listOf(
                Triple("TrainingBot", 18, 0.28f),
                Triple("Dummy_02", 42, 0.55f),
                Triple("NPC_Guard", 67, 0.80f)
            )
            targets.forEachIndexed { i, target ->
                val cx = width * target.third
                val top = height * (0.25f + i * 0.18f)
                val rect = RectF(cx - 65f, top, cx + 65f, top + 92f)
                boxPaint.color = color
                if (showBoxes) canvas.drawRoundRect(rect, 8f, 8f, boxPaint)
                textPaint.color = color
                textPaint.textSize = 25f
                val label = buildList {
                    if (showNames) add(target.first)
                    if (showDistance) add("${target.second} m")
                }.joinToString(" · ")
                if (label.isNotEmpty()) canvas.drawText(label, rect.left - 18f, rect.top - 12f, textPaint)
            }
        }
    }
}

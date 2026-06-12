package com.example.tts2026.task2

import android.os.Bundle
import android.os.Build
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tts2026.R

class AccountActivity : AppCompatActivity() {

    private lateinit var txtName: TextView
    private lateinit var txtPassword: TextView
    private lateinit var img: ImageView
    private lateinit var btnBack: Button
    private lateinit var btnToggleFragment: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account)

        txtName = findViewById(R.id.txtName)
        txtPassword = findViewById(R.id.txtPassword)
        img = findViewById(R.id.imageView)
        btnBack = findViewById(R.id.btnBack)
        btnToggleFragment = findViewById(R.id.btnToggleFragment)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        when (intent.getStringExtra("type")){
            "bundle" ->{
                val name = intent.extras?.getString("name")
                val pass = intent.extras?.getString("pass")

                txtName.setText(name)
                txtPassword.setText(pass)
            }
            "serializable" -> {
                val user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getSerializableExtra("user", UserSerializable::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getSerializableExtra("user") as? UserSerializable
                }

                user?.let {
                    txtName.text = it.name
                    txtPassword.text = it.pass
                }
            }
            "parcelable" -> {
                val user =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra("user", UserParcelable::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra("user") as? UserParcelable
                }

                user?.let {
                    txtName.text = it.name
                    txtPassword.text = it.pass
                }
            }

        }

        btnBack.setOnClickListener {
            finish()
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(
                    R.id.fragmentContainer,
                    LifecycleFragment(),
                    LIFECYCLE_FRAGMENT_TAG
                )
                .commit()
        }

        btnToggleFragment.setOnClickListener {
            val fragment = supportFragmentManager.findFragmentByTag(LIFECYCLE_FRAGMENT_TAG)
                ?: return@setOnClickListener

            supportFragmentManager.beginTransaction().apply {
                if (fragment.isDetached) {
                    attach(fragment)
                    btnToggleFragment.setText(R.string.detach_fragment)
                } else {
                    detach(fragment)
                    btnToggleFragment.setText(R.string.attach_fragment)
                }
            }.commit()
        }

        img.setOnClickListener {
            Toast.makeText(this, R.string.image_clicked, Toast.LENGTH_SHORT).show()
        }

        var initialX = 0f
        var initialY = 0f
        img.post {
            initialX = img.x
            initialY = img.y
        }

        var dX = 0f
        var dY = 0f
        var downX = 0f
        var downY = 0f
        var isDragging = false
        val touchSlop = ViewConfiguration.get(this).scaledTouchSlop
        val gestureDetector = GestureDetector(
            this,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onDown(event: MotionEvent): Boolean = true

                override fun onSingleTapUp(event: MotionEvent): Boolean {
                    img.performClick()
                    return true
                }

                override fun onLongPress(event: MotionEvent) {
                    img.animate()
                        .x(initialX)
                        .y(initialY)
                        .setDuration(200)
                        .start()
                    Toast.makeText(
                        this@AccountActivity,
                        R.string.image_reset,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

        img.setOnTouchListener { view, event ->
            gestureDetector.onTouchEvent(event)

            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    dX = view.x - event.rawX
                    dY = view.y - event.rawY
                    downX = event.rawX
                    downY = event.rawY
                    isDragging = false
                }

                MotionEvent.ACTION_MOVE -> {
                    if (
                        kotlin.math.abs(event.rawX - downX) > touchSlop ||
                        kotlin.math.abs(event.rawY - downY) > touchSlop
                    ) {
                        isDragging = true
                    }

                    if (isDragging) {
                        val parent = view.parent as android.view.View
                        val maxX = (parent.width - view.width).coerceAtLeast(0).toFloat()
                        val maxY = (parent.height - view.height).coerceAtLeast(0).toFloat()

                        view.x = (event.rawX + dX).coerceIn(0f, maxX)
                        view.y = (event.rawY + dY).coerceIn(0f, maxY)
                    }
                }

                MotionEvent.ACTION_UP -> {
                    if (isDragging) {
                        Toast.makeText(
                            this,
                            getString(R.string.image_dropped, view.x.toInt(), view.y.toInt()),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                MotionEvent.ACTION_CANCEL -> {
                    isDragging = false
                }
            }

            true
        }

    }
    private val tag = "AccountLifecycle"

    override fun onStart() {
        super.onStart()
        Log.d(tag, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(tag, "onResume")
    }

    override fun onPause() {
        Log.d(tag, "onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d(tag, "onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(tag, "onDestroy")
        super.onDestroy()
    }

    companion object {
        private const val LIFECYCLE_FRAGMENT_TAG = "LifecycleFragment"
    }
}

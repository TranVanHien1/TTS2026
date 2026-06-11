package com.example.tts2026.task2

import android.os.Bundle
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tts2026.R

class AccountActivity : AppCompatActivity() {

    lateinit var txtName: TextView
    lateinit var txtPassword: TextView
    lateinit var img: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account)

        txtName = findViewById(R.id.txtName)
        txtPassword = findViewById(R.id.txtPassword)
        img = findViewById(R.id.imageView)

        when (intent.getStringExtra("type")){
            "bundle" ->{
                val name = intent.extras?.getString("name")
                val pass = intent.extras?.getString("pass")

                txtName.setText(name)
                txtPassword.setText(pass)
            }
            "serializable" -> {
                val user =
                    intent.getSerializableExtra("user") as UserSerializable
                txtName.setText(user.name)
                txtPassword.setText(user.pass)
            }

        }
//    img.setOnTouchListener { _, event ->
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                val x = event.x
//                val y = event.y
//
//                Toast.makeText(
//                    this,
//                    "Touch: ($x, $y)",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//        true
//    }
        var dX = 0f
        var dY = 0f

        img.setOnTouchListener { view, event ->

            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    dX = view.x - event.rawX
                    dY = view.y - event.rawY
                }

                MotionEvent.ACTION_MOVE -> {
                    view.animate()
                        .x(event.rawX + dX)
                        .y(event.rawY + dY)
                        .setDuration(0)
                        .start()
                }
            }

            true
        }

    }
}
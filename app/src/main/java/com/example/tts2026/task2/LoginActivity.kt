package com.example.tts2026.task2

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.tts2026.R
import java.io.Serializable
//import kotlinx.parcelize.Parcelize

data class UserSerializable(
    val name: String,
    val pass: String
) : Serializable

//@Parcelize
//data class UserParcelable(
//    val name: String,
//    val pass: String
//) : Parcelable

class LoginActivity : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnBundle: Button
    //private lateinit var btnParce: Button
    private lateinit var btnSeri: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        edtName = findViewById(R.id.edtName)
        edtPassword = findViewById(R.id.edtPassword)
        btnBundle = findViewById(R.id.btnBundle)
//        btnParce = findViewById(R.id.btnParce)
        btnSeri = findViewById(R.id.btnSeri)

        btnBundle.setOnClickListener {
            val data = getLoginData() ?: return@setOnClickListener

            val (name, password) = data
            val intent = Intent(this, AccountActivity::class.java)
            val bundle = Bundle()
            intent.putExtra("type", "bundle")
            bundle.putString("name", name)
            bundle.putString("pass", password)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        btnSeri.setOnClickListener {
            val data = getLoginData() ?: return@setOnClickListener

            val (name, password) = data
            val intent = Intent(this, AccountActivity::class.java)
            var user = UserSerializable(name, password)
            intent.putExtra("type", "serializable")
            intent.putExtra("user", user)
            startActivity(intent)
        }
    }

    private fun getLoginData(): Pair<String, String>? {
        val name = edtName.text.toString().trim()
        val password = edtPassword.text.toString().trim()

        if (name.isEmpty()) {
            edtName.error = "Nhập tên"
            return null
        }

        if (password.isEmpty()) {
            edtPassword.error = "Nhập mật khẩu"
            return null
        }

        return Pair(name, password)
    }
}
package com.example.tts2026

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Task1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_task1)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

//---class, Encapsulation, Inheritance, Polymorphism---
open class User(
    var name: String,
    var age: Int,
){
    open fun showInfo() {
        println("User: $name")
    }
}
class Salary(
    name: String,
    age: Int,
    val year: Int,
    private var salaryCoe: Double,
) : User(name, age) {

    fun getSalaryCoe(): Double {
        return salaryCoe
    }

    fun updateSalaryCoe(newCoe: Double) {
        if (newCoe > 0) {
            salaryCoe = newCoe
        }
    }
    override fun showInfo() {
        println("Salary: $name - $year - $salaryCoe")
    }
}

val salary = Salary("Hien", 23, 2026, 1.1)
//---lateinit, lazy---
//private lateinit var binding: ActivityMainBinding
//binding = ActivityMainBinding.inflate(layoutInflater)

//val database by lazy {
//    FirebaseDatabase.getInstance()
//}
//database.reference


//---Abstraction---
abstract  class Admin(
    val name: String
){
    abstract fun login()
}

class Customers(
    name: String
) : Admin(name){
    override fun login() {
        TODO("Not yet implemented")
    }

}
//---interface---
interface flyable{
    fun fly()
    fun cantfly()
}
interface swimable{
    fun swim()
}
class penguin: flyable, swimable{
    override fun fly() {
        TODO("Not yet implemented")
    }

    override fun cantfly() {
        TODO("Not yet implemented")
    }

    override fun swim() {
        TODO("Not yet implemented")
    }
}
//generics

class Money<M>(
    var value: M
)

val stringMoney = Money<String>("hai muoi ngan")
val intMoney = Money<Int>(20000)

class Shoe<N, S>(
    val name: N,
    val size: S,
)
var shoe = Shoe<String, Int>("bitis", 42)

//-----Collection----
//List
val names = listOf(
    "A",
    "B",
    "A"
)
//Mutablelist - thay doi du lieu trong list
val Car = mutableListOf<String>()

//Map
val student = mapOf(
    "name" to "Hien",
    "age" to 22
)

//-----exception-----

//try {
//    val number = "abc".toInt()
//} catch (e: NumberFormatException) {
//    println("Không thể chuyển đổi sang số")
//}

//try {
//    println("Thực hiện")
//} catch (e: Exception) {
//    println("Có lỗi")
//} finally {
//    println("Đóng tài nguyên")
//}






//object



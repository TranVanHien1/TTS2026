package com.example.tts2026

//---Array--
val array = arrayOf(1, 2, 3, 4)
println(array[0]) //1

//---List---
val list = listOf(3, 5, 0, 2)
val mutableList = mutableListOf("A", "B")
mutableList.add("D")
//---Stack---
val stack = ArrayDeque<Int>()
stack.addLast("A")
stack.addLast("B")
stack.addLast("C")
println(stack.removeLast()) // C

//--queue--
val queue = ArrayDeque<Int>()
queue.addLast(2)
queue.addLast(1)
queue.addLast(4)
println(queue.removeFirst()) // 2

//---sort---
val nums = list.sorted()
println(nums) // 0, 2, 3, 5

val denums = list.sortedDescending()
println(denums) // 5,3,2,0

data class Film(
    val title: String,
    val rating: Double
)

val films = listOf(
    Film("A", 8.5),
    Film("B", 7.0),
    Film("C", 9.2)
)

val sortedFilms = films.sortedByDescending { it.rating } // c, a, b

//---search--
val found = list.find { it == 2 }

val key = "A"
val result = films.filter { it.title.lowercase().contains(key) }
println(result) // title: A, rating: 8.5

val index = nums.binarySearch(7)
println(index) // 3


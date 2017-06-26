package util

import java.io.File

println("hello from kts")

val file = File(".")
file.listFiles().forEach(::println)

println("the end")

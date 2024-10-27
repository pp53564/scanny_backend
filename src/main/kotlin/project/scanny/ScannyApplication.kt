package project.scanny

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ScannyApplication

fun main(args: Array<String>) {
	runApplication<ScannyApplication>(*args)
}

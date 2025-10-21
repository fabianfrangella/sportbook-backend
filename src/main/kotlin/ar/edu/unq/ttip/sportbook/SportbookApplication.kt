package ar.edu.unq.ttip.sportbook

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy

@SpringBootApplication
@EnableAspectJAutoProxy
class SportbookApplication

fun main(args: Array<String>) {
    runApplication<SportbookApplication>(*args)
}

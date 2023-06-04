package com.neewrobert.helidonse

import io.helidon.config.Config
import io.helidon.webserver.*
import kotlin.system.measureTimeMillis

class PrimeNumberService internal constructor(config: Config) : Service {

    override fun update(rules: Routing.Rules) {
        rules["/{range}", Handler { request: ServerRequest, response: ServerResponse ->
            getPrimeNumberInARange(request, response)
        }]
    }

    private fun getPrimeNumberInARange(request: ServerRequest, response: ServerResponse) {
        val range = request.path().param("range").toInt()
        val primeNumbers = calculatePrimesNumbersInAGivenRange(range)
        response.send("Prime numbers in range $range are $primeNumbers")
    }

    private fun calculatePrimesNumbersInAGivenRange(range: Int): List<Int> {
        if (range <= 1) return emptyList()
        val primeNumbers = mutableListOf<Int>()
        measureTimeMillis {
            for (number in 2..range) {
                if (isPrimeNumber(number)) {
                    primeNumbers.add(number)
                }
            }
        }.also { println("calculatePrimesNumbersInAGivenRange() took $it ms") }
        return primeNumbers
    }

    private fun isPrimeNumber(number: Int): Boolean {
        if (number <= 1) return false
        var isPrime = true
        for (i in 2 until number) {
            if (number % i == 0) {
                isPrime = false
                break
            }
        }
        return isPrime
    }
}
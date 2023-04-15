package app.ikd9684.android.hit_and_blow_analyzer.logic

import kotlin.math.log2

class HitAndBlowAnalyze(private val digits: Int) {

    companion object {
        private const val TAG = "HitAndBlowAnalyze"
    }

    private var allPossibleNumbers = generateNonRepeatingCombinations(digits)
    private var candidates: MutableList<List<Int>> = mutableListOf()

    private var guess: List<Int> = emptyList()

    init {
        reset()
    }

    fun reset() {
        candidates = allPossibleNumbers.toMutableList()
    }

    fun nextGuess(hits: Int? = null, blows: Int? = null): List<Int> {
        if (guess.isEmpty()) {
            // NOP
            guess = selectNextGuessWithMinimumEntropy(candidates, allPossibleNumbers)
        } else if (hits != null && blows != null && hits != digits) {
            allPossibleNumbers.removeIf { calculateHitAndBlow(it, guess) != hits to blows }
            guess = selectNextGuessWithMinimumEntropy(candidates, allPossibleNumbers)
        } else {
            // NOP
        }
        return guess
    }

    private fun generateNonRepeatingCombinations(d: Int): MutableList<List<Int>> {
        val combinations = mutableListOf<List<Int>>()
        generateCombinationsHelper(d, mutableListOf(), combinations)
        return combinations
    }

    private fun generateCombinationsHelper(
        d: Int,
        currentCombination: MutableList<Int>,
        combinations: MutableList<List<Int>>
    ) {
        if (currentCombination.size == d) {
            combinations.add(currentCombination.toList())
        } else {
            for (i in 0..9) {
                if (i !in currentCombination) {
                    currentCombination.add(i)
                    generateCombinationsHelper(d, currentCombination, combinations)
                    currentCombination.removeLast()
                }
            }
        }
    }

//    private fun selectNextGuessWithMinimax(
//        candidates: List<List<Int>>,
//        allPossibleNumbers: List<List<Int>>
//    ): List<Int> {
//        var bestGuess: List<Int>? = null
//        var minWorstCase = Int.MAX_VALUE
//
//        for (guess in allPossibleNumbers) {
//            val worstCase = calculateWorstCase(guess, candidates)
//            if (worstCase < minWorstCase) {
//                minWorstCase = worstCase
//                bestGuess = guess
//            }
//        }
//
//        return bestGuess ?: candidates.random()
//    }
//
//    private fun calculateWorstCase(guess: List<Int>, candidates: List<List<Int>>): Int {
//        val hitBlowCount = mutableMapOf<Pair<Int, Int>, Int>()
//
//        for (candidate in candidates) {
//            val hitBlow = calculateHitAndBlow(candidate, guess)
//            hitBlowCount[hitBlow] = hitBlowCount.getOrDefault(hitBlow, 0) + 1
//        }
//
//        return hitBlowCount.values.maxOrNull() ?: 0
//    }

    private fun selectNextGuessWithMinimumEntropy(
        candidates: List<List<Int>>,
        allPossibleNumbers: List<List<Int>>
    ): List<Int> {
        var bestGuess: List<Int>? = null
        var minEntropy = Double.MAX_VALUE

        for (guess in allPossibleNumbers) {
            val entropy = calculateEntropy(guess, candidates)
            if (entropy < minEntropy) {
                minEntropy = entropy
                bestGuess = guess
            }
        }

        return bestGuess ?: candidates.random()
    }

    private fun calculateEntropy(guess: List<Int>, candidates: List<List<Int>>): Double {
        val hitBlowCount = mutableMapOf<Pair<Int, Int>, Int>()

        for (candidate in candidates) {
            val hitBlow = calculateHitAndBlow(candidate, guess)
            hitBlowCount[hitBlow] = hitBlowCount.getOrDefault(hitBlow, 0) + 1
        }

        val total = candidates.size.toDouble()
        return hitBlowCount.values.sumOf { count -> -count / total * log2(count / total) }
    }

    private fun calculateHitAndBlow(secret: List<Int>, guess: List<Int>): Pair<Int, Int> {
        var hits = 0
        var blows = 0

        for (i in secret.indices) {
            if (secret[i] == guess[i]) {
                hits++
            } else if (secret.contains(guess[i])) {
                blows++
            }
        }

        return hits to blows
    }
}

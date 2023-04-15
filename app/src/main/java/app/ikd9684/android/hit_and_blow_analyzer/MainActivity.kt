package app.ikd9684.android.hit_and_blow_analyzer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.ikd9684.android.hit_and_blow_analyzer.databinding.ActivityMainBinding
import app.ikd9684.android.hit_and_blow_analyzer.logic.HitAndBlowAnalyze

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val digits = 3
    private val analyze = HitAndBlowAnalyze(digits)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNext.setOnClickListener {
            applyGuess()
        }
        binding.btnReset.setOnClickListener {
            onClickResetButton()
        }

        binding.tvGuessValue.text = analyze.nextGuess().joinToString(separator = "")
    }

    private fun applyGuess() {
        try {
            val hits = binding.etHitsValue.text.toString().toInt()
            val blows = binding.etBlowsValue.text.toString().toInt()
            binding.tvGuessValue.text = analyze.nextGuess(hits, blows).joinToString(separator = "")
        } catch (_: Exception) {
            // NOP
        }
    }

    private fun onClickResetButton() {
        analyze.reset()
    }
}

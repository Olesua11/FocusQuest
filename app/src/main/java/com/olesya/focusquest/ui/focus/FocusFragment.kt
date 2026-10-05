package com.olesya.focusquest.ui.focus

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.olesya.focusquest.MainViewModel
import com.olesya.focusquest.databinding.FragmentFocusBinding
import kotlinx.coroutines.launch

class FocusFragment : Fragment() {
    private var _binding: FragmentFocusBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    private var selectedMinutes = 25
    private var remainingSeconds = 25 * 60L
    private var timer: CountDownTimer? = null
    private var running = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _binding = FragmentFocusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupTimerUi()
        binding.startButton.setOnClickListener { if (running) pauseTimer() else startTimer() }
        binding.resetButton.setOnClickListener { resetTimer() }
        binding.min15.setOnClickListener { selectDuration(15) }
        binding.min25.setOnClickListener { selectDuration(25) }
        binding.min50.setOnClickListener { selectDuration(50) }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profile.collect { profile ->
                    val level = viewModel.levelFor(profile.xp)
                    binding.levelChip.text = "LVL $level"
                    binding.xpText.text = "${viewModel.xpIntoLevel(profile.xp)} / 100 XP"
                    binding.xpProgress.progress = viewModel.xpIntoLevel(profile.xp)
                    binding.coinsText.text = "◆ ${profile.coins}"
                    binding.todayText.text = "Всего: ${profile.totalSessions} сессий • ${profile.totalMinutes} минут • серия ${profile.streak} дней"
                }
            }
        }
    }

    private fun selectDuration(minutes: Int) {
        if (running) return
        selectedMinutes = minutes
        remainingSeconds = minutes * 60L
        setupTimerUi()
    }

    private fun setupTimerUi() {
        val min = remainingSeconds / 60
        val sec = remainingSeconds % 60
        binding.timerText.text = "%02d:%02d".format(min, sec)
        binding.timerProgress.max = selectedMinutes * 60
        binding.timerProgress.progress = remainingSeconds.toInt()
        val (xp, coins) = when {
            selectedMinutes >= 50 -> 80 to 20
            selectedMinutes >= 25 -> 40 to 10
            else -> 20 to 5
        }
        binding.rewardText.text = "+$xp XP  •  +$coins ◆"
    }

    private fun startTimer() {
        running = true
        binding.startButton.text = "ПАУЗА"
        timer = object : CountDownTimer(remainingSeconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingSeconds = millisUntilFinished / 1000
                setupTimerUi()
            }
            override fun onFinish() {
                remainingSeconds = 0
                running = false
                binding.startButton.text = "СТАРТ"
                setupTimerUi()
                viewModel.completeSession(selectedMinutes)
                Toast.makeText(requireContext(), "Квест выполнен! Награда начислена ⚔", Toast.LENGTH_LONG).show()
                remainingSeconds = selectedMinutes * 60L
                setupTimerUi()
            }
        }.start()
    }

    private fun pauseTimer() {
        timer?.cancel()
        running = false
        binding.startButton.text = "ПРОДОЛЖИТЬ"
    }

    private fun resetTimer() {
        timer?.cancel()
        running = false
        remainingSeconds = selectedMinutes * 60L
        binding.startButton.text = "СТАРТ"
        setupTimerUi()
    }

    override fun onDestroyView() {
        timer?.cancel()
        _binding = null
        super.onDestroyView()
    }
}

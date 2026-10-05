package com.olesya.focusquest.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.olesya.focusquest.MainViewModel
import com.olesya.focusquest.databinding.FragmentStatsBinding
import kotlinx.coroutines.launch

class StatsFragment : Fragment() {
    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profile.collect { p ->
                    val avg = if (p.totalSessions == 0) 0 else p.totalMinutes / p.totalSessions
                    val level = viewModel.levelFor(p.xp)
                    binding.totalSessions.text = "${p.totalSessions}\nсессий"
                    binding.totalMinutes.text = "${p.totalMinutes}\nминут"
                    binding.bestStreak.text = "🔥 Лучшая серия: ${p.bestStreak} дней"
                    binding.averageSession.text = "Средняя сессия: $avg минут"
                    binding.rankText.text = "Ранг: ${viewModel.rankFor(level)}"
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

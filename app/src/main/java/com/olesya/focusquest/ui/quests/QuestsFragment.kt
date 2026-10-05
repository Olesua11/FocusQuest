package com.olesya.focusquest.ui.quests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.olesya.focusquest.MainViewModel
import com.olesya.focusquest.databinding.FragmentQuestsBinding
import kotlinx.coroutines.launch

class QuestsFragment : Fragment() {
    private var _binding: FragmentQuestsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private val adapter = QuestAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _binding = FragmentQuestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.questsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.questsRecycler.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profile.collect { p ->
                    adapter.submitList(
                        listOf(
                            QuestUi("⚔", "Первый шаг", "Заверши 1 фокус-сессию", "+50 XP", (p.totalSessions * 100).coerceAtMost(100)),
                            QuestUi("🔥", "В огне", "Серия концентрации 3 дня", "+100 XP", (p.streak * 100 / 3).coerceAtMost(100)),
                            QuestUi("⏳", "Час силы", "Набери 60 минут фокуса", "+120 XP", (p.totalMinutes * 100 / 60).coerceAtMost(100)),
                            QuestUi("🏆", "Настоящий герой", "Заверши 10 сессий", "+250 XP", (p.totalSessions * 10).coerceAtMost(100)),
                            QuestUi("💎", "Глубокая работа", "Набери 500 минут фокуса", "+500 XP", (p.totalMinutes * 100 / 500).coerceAtMost(100))
                        )
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

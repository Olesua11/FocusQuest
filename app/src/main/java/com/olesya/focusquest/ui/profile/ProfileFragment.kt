package com.olesya.focusquest.ui.profile

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
import com.olesya.focusquest.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profile.collect { p ->
                    val level = viewModel.levelFor(p.xp)
                    binding.heroRank.text = viewModel.rankFor(level)
                    binding.profileLevel.text = "Уровень $level"
                    binding.profileXp.text = "Опыт: ${p.xp} XP"
                    binding.profileCoins.text = "◆ Монеты: ${p.coins}"
                    binding.profileSessions.text = "Завершено фокус-квестов: ${p.totalSessions}"
                    binding.avatarText.text = when {
                        level >= 20 -> "👑"
                        level >= 10 -> "🛡"
                        level >= 5 -> "⚔"
                        else -> "🗡"
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

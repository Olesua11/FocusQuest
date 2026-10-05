package com.olesya.focusquest.ui.quests

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.olesya.focusquest.databinding.ItemQuestBinding

class QuestAdapter : RecyclerView.Adapter<QuestAdapter.Holder>() {
    private var items: List<QuestUi> = emptyList()

    fun submitList(newItems: List<QuestUi>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemQuestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(items[position])
    override fun getItemCount(): Int = items.size

    class Holder(private val binding: ItemQuestBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: QuestUi) = with(binding) {
            questIcon.text = item.icon
            questTitle.text = item.title
            questSubtitle.text = item.subtitle
            questReward.text = item.reward
            questProgress.progress = item.progress.coerceIn(0, 100)
        }
    }
}

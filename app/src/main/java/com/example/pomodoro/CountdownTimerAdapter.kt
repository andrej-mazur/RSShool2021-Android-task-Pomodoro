package com.example.pomodoro

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.pomodoro.databinding.TimerItemBinding

class CountdownTimerAdapter(
    private val listener: CountdownTimerListener
) : ListAdapter<CountdownTimer, CountdownTimerViewHolder>(itemComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountdownTimerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = TimerItemBinding.inflate(layoutInflater, parent, false)
        return CountdownTimerViewHolder(binding, listener, binding.root.context.resources)
    }

    override fun onBindViewHolder(holderCountdown: CountdownTimerViewHolder, position: Int) {
        holderCountdown.bind(getItem(position))
    }

    private companion object {

        private val itemComparator = object : DiffUtil.ItemCallback<CountdownTimer>() {

            override fun areItemsTheSame(oldItem: CountdownTimer, newItem: CountdownTimer): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: CountdownTimer, newItem: CountdownTimer): Boolean {
                return oldItem.remainingTime == newItem.remainingTime
                        && oldItem.isStarted == newItem.isStarted
                        && oldItem.isFinished == newItem.isFinished
            }

            override fun getChangePayload(oldItem: CountdownTimer, newItem: CountdownTimer) = Any()
        }
    }
}
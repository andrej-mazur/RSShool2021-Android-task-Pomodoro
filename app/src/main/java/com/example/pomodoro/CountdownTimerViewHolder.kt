package com.example.pomodoro

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.pomodoro.databinding.TimerItemBinding
import com.google.android.material.color.MaterialColors

class CountdownTimerViewHolder(
    private val binding: TimerItemBinding,
    private val listener: CountdownTimerListener,
    private val resources: Resources
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(countdownTimer: CountdownTimer) {
        binding.textTimer.text = countdownTimer.remainingTime.displayTime()
        binding.circleTimer.initTime = countdownTimer.initTime
        binding.circleTimer.currentTime = countdownTimer.remainingTime

        if (countdownTimer.isStarted) {
            with(binding) {
                startPauseButton.text = resources.getString(R.string.button_timer_stop)
                blinkingIndicator.isInvisible = false
                (blinkingIndicator.background as? AnimationDrawable)?.start()
            }
        } else {
            with(binding) {
                startPauseButton.text = resources.getString(R.string.button_timer_start)
                blinkingIndicator.isInvisible = true
                (blinkingIndicator.background as? AnimationDrawable)?.stop()
            }
        }

        if (countdownTimer.isFinished) {
            binding.startPauseButton.isEnabled = false
            binding.row.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.purple_200))
        } else {
            binding.startPauseButton.isEnabled = true
            binding.row.setBackgroundColor(MaterialColors.getColor(itemView, R.attr.backgroundColor, Color.WHITE))
        }

        initListeners(countdownTimer)
    }

    private fun initListeners(countdownTimer: CountdownTimer) {
        binding.startPauseButton.setOnClickListener {
            if (countdownTimer.isStarted) {
                listener.stop(countdownTimer.id)
            } else {
                listener.start(countdownTimer.id)
            }
        }

        binding.deleteButton.setOnClickListener {
            listener.delete(countdownTimer.id)
        }
    }
}
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
        with(binding) {
            textTimer.text = countdownTimer.remainingTime.displayTime()
            circleTimer.initTime = countdownTimer.initTime
            circleTimer.currentTime = countdownTimer.remainingTime

            if (countdownTimer.isStarted) {
                startPauseButton.text = resources.getString(R.string.button_timer_stop)
                blinkingIndicator.isInvisible = false
                (blinkingIndicator.background as? AnimationDrawable)?.start()
            } else {
                startPauseButton.text = resources.getString(R.string.button_timer_start)
                blinkingIndicator.isInvisible = true
                (blinkingIndicator.background as? AnimationDrawable)?.stop()
            }

            if (countdownTimer.isFinished) {
                startPauseButton.isEnabled = false
                row.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.purple_200))
            } else {
                startPauseButton.isEnabled = true
                row.setBackgroundColor(MaterialColors.getColor(itemView, R.attr.backgroundColor, Color.WHITE))
            }
        }

        initListeners(countdownTimer)
    }

    private fun initListeners(countdownTimer: CountdownTimer) {
        with(binding) {
            startPauseButton.setOnClickListener {
                if (countdownTimer.isStarted) {
                    listener.stop(countdownTimer.id)
                } else {
                    listener.start(countdownTimer.id)
                }
            }

            deleteButton.setOnClickListener {
                listener.delete(countdownTimer.id)
            }
        }
    }
}
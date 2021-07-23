package com.example.pomodoro

import android.os.SystemClock

data class CountdownTimer(
    val id: Int,
    val initTime: Long,
    val remainingTime: Long,
    val elapsedRealtime: Long = 0L,
    val isStarted: Boolean = false,
    val isFinished: Boolean = false
) {

    fun start(): CountdownTimer {
        val elapsedRealtime = SystemClock.elapsedRealtime()
        return this.copy(
            isStarted = true,
            elapsedRealtime = elapsedRealtime
        )
    }

    fun tick(): CountdownTimer {
        val elapsedRealtime = SystemClock.elapsedRealtime()
        val remainingTime = calculateRemainingTime(elapsedRealtime)
        return if (remainingTime >= 0L) {
            this.copy(
                remainingTime = remainingTime,
                elapsedRealtime = elapsedRealtime
            )
        } else {
            finish()
        }
    }

    fun stop(): CountdownTimer {
        val elapsedRealtime = SystemClock.elapsedRealtime()
        val remainingTime = calculateRemainingTime(elapsedRealtime)
        return if (remainingTime >= 0L) {
            this.copy(
                isStarted = false,
                remainingTime = remainingTime,
                elapsedRealtime = 0L
            )
        } else {
            finish()
        }
    }

    private fun finish(): CountdownTimer {
        return this.copy(
            isStarted = false,
            isFinished = true,
            remainingTime = 0L,
            elapsedRealtime = 0L
        )
    }

    private fun calculateRemainingTime(elapsedRealtime: Long): Long {
        if (this.elapsedRealtime > 0L) {
            return this.remainingTime - (elapsedRealtime - this.elapsedRealtime)
        }
        return this.remainingTime
    }
}
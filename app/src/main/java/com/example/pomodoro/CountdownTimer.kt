package com.example.pomodoro

data class CountdownTimer(
    val id: Int,
    val initTime: Long,
    val remainingTime: Long,
    val clockTime: Long = 0L,
    val isStarted: Boolean = false,
    val isFinished: Boolean = false
) {

    fun start(): CountdownTimer {
        val clockTime = clockTime()
        return this.copy(
            isStarted = true,
            clockTime = clockTime
        )
    }

    fun tick(): CountdownTimer {
        val clockTime = clockTime()
        val remainingTime = calculateRemainingTime(clockTime)
        return if (remainingTime >= 0L) {
            this.copy(
                remainingTime = remainingTime,
                clockTime = clockTime
            )
        } else {
            finish()
        }
    }

    fun stop(): CountdownTimer {
        val clockTime = clockTime()
        val remainingTime = calculateRemainingTime(clockTime)
        return if (remainingTime >= 0L) {
            this.copy(
                isStarted = false,
                remainingTime = remainingTime,
                clockTime = 0L
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
            clockTime = 0L
        )
    }

    private fun calculateRemainingTime(clockTime: Long): Long =
        if (this.clockTime > 0L) {
            this.remainingTime - (clockTime - this.clockTime)
        } else {
            this.remainingTime
        }
}
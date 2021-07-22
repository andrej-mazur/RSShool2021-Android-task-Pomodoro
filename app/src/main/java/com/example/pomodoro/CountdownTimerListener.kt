package com.example.pomodoro

interface CountdownTimerListener {

    fun add(id: Int, initTime: Long)

    fun delete(id: Int)

    fun start(id: Int)

    fun stop(id: Int)

    fun tick(id: Int)
}
package com.example.pomodoro

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pomodoro.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), CountdownTimerListener, LifecycleObserver {

    private lateinit var binding: ActivityMainBinding

    private val timerAdapter = CountdownTimerAdapter(this)

    private val timers = mutableListOf<CountdownTimer>()

    private var nextId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = timerAdapter
        }

        binding.addTimerButton.setOnClickListener {
            val initTimeInMinutesString = binding.timerInitValue.text.toString()
            if (initTimeInMinutesString.isBlank()) {
                Toast.makeText(this, getString(R.string.timer_init_error_1), Toast.LENGTH_SHORT).show()
                binding.timerInitValue.requestFocus()
                return@setOnClickListener
            }

            val initTimeInMinutes = initTimeInMinutesString.toLong()
            if (initTimeInMinutes < MIN_MINUTES || initTimeInMinutes > MAX_MINUTES) {
                Toast.makeText(this, getString(R.string.timer_init_error_2, MIN_MINUTES, MAX_MINUTES), Toast.LENGTH_SHORT).show()
                binding.timerInitValue.requestFocus()
                return@setOnClickListener
            }

            add(nextId++, initTimeInMinutes.minutesToMillis())
        }

        lifecycleScope.launch(Dispatchers.Main) {
            while (true) {
                timers.find { it.isStarted }?.let {
                    tick(it.id)
                }
                delay(INTERVAL)
            }
        }
    }

    override fun start(id: Int) {
        timers.forEachIndexed { index, timer ->
            if (timer.id == id) {
                timers[index] = timer.start()
            } else if (timer.isStarted) {
                timers[index] = timer.stop()
            }
        }
        timerAdapter.submitList(timers.toList())
    }

    override fun tick(id: Int) {
        timers.forEachIndexed { index, timer ->
            timer.takeIf { it.id == id }?.let {
                timers[index] = timer.tick()
            }
        }
        timerAdapter.submitList(timers.toList())
    }

    override fun stop(id: Int) {
        timers.forEachIndexed { index, timer ->
            if (timer.id == id) {
                timers[index] = timer.stop()
            }
        }
        timerAdapter.submitList(timers.toList())
    }

    override fun delete(id: Int) {
        timers.remove(timers.find { it.id == id })
        timerAdapter.submitList(timers.toList())
    }

    override fun add(id: Int, initTime: Long) {
        timers.add(CountdownTimer(id, initTime, initTime))
        timerAdapter.submitList(timers.toList())
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        timers.find { it.isStarted }?.let {
            val startIntent = Intent(this, ForegroundService::class.java)
                .putExtra(COMMAND_ID, COMMAND_START)
                .putExtra(START_TIME, it.remainingTime)
                .putExtra(ELAPSED_REALTIME, it.elapsedRealtime)
            startService(startIntent)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        val stopIntent = Intent(this, ForegroundService::class.java)
            .putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }

    companion object {
        private const val MIN_MINUTES = 1
        private const val MAX_MINUTES = 1440
        private const val INTERVAL = 100L
    }
}
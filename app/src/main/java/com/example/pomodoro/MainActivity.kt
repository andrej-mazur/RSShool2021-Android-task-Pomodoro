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

    private var currentId = 0

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

            add(currentId++, initTimeInMinutes.minutesToMillis())
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CURRENT_ID, currentId)
        outState.putParcelableArrayList(TIMERS, ArrayList(timers))
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentId = savedInstanceState.getInt(CURRENT_ID)
        if (currentId > 0) {
            val savedTimers = savedInstanceState.getParcelableArrayList<CountdownTimer>(TIMERS)
            if (!savedTimers.isNullOrEmpty()) {
                timers.clear()
                timers.addAll(savedTimers)
                timerAdapter.submitList(timers.toList())
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
                .putExtra(CLOCK_TIME, it.clockTime)
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
        private const val INTERVAL = 250L

        private const val TIMERS = "TIMERS"
        private const val CURRENT_ID = "CURRENT_ID"
    }
}
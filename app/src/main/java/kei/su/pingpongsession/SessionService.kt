package kei.su.pingpongsession

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log

class SessionService : Service() {
    private lateinit var timer: CountDownTimer
    private var sessionStartTime: Long = 0
    private val mBinder = MyBinder()
    private var sessionStarted = false
    private var timeRemaining = 0L
    companion object{
        const val PING_EVENT_TIME = 600000L
        const val PONG_EVENT_TIME = 120000L
        const val NEW_SESSION_TIME = 600000L
    }

    override fun onBind(p0: Intent?): IBinder? {
        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY

    }

    class MyBinder: Binder() {
        fun getService() : SessionService{
            return SessionService()
        }
    }

    fun setEvent(event: Int){
        if (sessionStarted) {
            // ping event
            if (event == 0)
                processEvent(PING_EVENT_TIME)
            // pong event
            else if (event == 1)
                processEvent(PONG_EVENT_TIME)
        } else {
            sessionStartTime = System.currentTimeMillis()
            Log.d("session started","session started at $sessionStartTime")
            processEvent(NEW_SESSION_TIME)
        }
    }

    private fun processEvent(eventTime: Long) {
        if (sessionStarted)
            timer.cancel()

        val totalTime = timeRemaining + eventTime
        timer = object: CountDownTimer(totalTime, 1000){
            override fun onTick(msUntilFinished: Long) {
                timeRemaining = msUntilFinished
                Log.d("timer", "seconds remaining ${timeRemaining / 1000}")
            }

            override fun onFinish() {
                sessionStarted = false
                Log.d("timer", "session lasted ${(System.currentTimeMillis() - 
                sessionStartTime) / 1000}")
            }
        }
        timer.start()
        sessionStarted = true

    }
}
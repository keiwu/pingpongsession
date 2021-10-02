package kei.su.pingpongsession

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log

class SessionService : Service() {
    private var timeInSeconds: Long = 0
    private lateinit var timerCountCallBack: TimerCountCallBack
    lateinit var timer: CountDownTimer
    private var sessionStartTime: Long = 0
    private val mBinder = MyBinder()
    private var sessionStarted = false
    private var timeRemaining = 0L
    companion object{
        const val PING_EVENT_TIME = 600000L
        const val PONG_EVENT_TIME = 120000L
        const val NEW_SESSION_TIME = 600000L
    }

    enum class PingPongEvent{
        PING, PONG
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

    fun startEvent(event: PingPongEvent){
        if (sessionStarted) {
            // ping event
            if (event == PingPongEvent.PING)
                processEvent(PING_EVENT_TIME)
            // pong event
            else if (event == PingPongEvent.PONG)
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
                timeInSeconds = timeRemaining / 1000
                timerCountCallBack.onCount(timeInSeconds)
                Log.d("timer", "seconds remaining $timeInSeconds")
            }

            override fun onFinish() {
                sessionStarted = false
                val sessionDuration = (System.currentTimeMillis() - sessionStartTime) / 1000
                Log.d("timer", "session lasted $sessionDuration")
                timerCountCallBack.onCountFinished(sessionDuration)
            }
        }
        timer.start()
        sessionStarted = true

    }

    fun setCallBack(callBack: TimerCountCallBack){
        timerCountCallBack = callBack
    }

    interface TimerCountCallBack{
        fun onCount(time: Long)

        fun onCountFinished(sessionDuration: Long)
    }
}
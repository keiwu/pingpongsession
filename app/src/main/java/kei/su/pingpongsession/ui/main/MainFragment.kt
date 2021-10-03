package kei.su.pingpongsession.ui.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kei.su.pingpongsession.R
import kei.su.pingpongsession.SessionService
import kei.su.pingpongsession.databinding.MainFragmentBinding
import kei.su.pingpongsession.SessionService.PingPongEvent.*

class MainFragment : Fragment(){

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var binding: MainFragmentBinding
    private var sessionService: SessionService? = null
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        sessionService?.timer?.cancel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MainFragmentBinding.bind(view)
        binding.btnStartService.setOnClickListener{
            val intent = Intent(context, SessionService::class.java)
            requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            binding.tvStatus.text = "Service started......"
            binding.btnStartService.isEnabled = false
        }

        binding.btnPing.setOnClickListener{
            sessionService?.startEvent(PING)
        }

        binding.btnPong.setOnClickListener{
            sessionService?.startEvent(PONG)
        }


    }

    val callBack = object: SessionService.TimerCountCallBack{
        override fun onCount(time: Long) {
           binding.tvStatus.text = String.format(getString(R.string.session_time_left), time)
        }

        override fun onCountFinished(sessionDuration: Long) {
            binding.tvStatus.text = String.format(getString(R.string.session_duration), sessionDuration)
        }

    }

    private val serviceConnection = object: ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val b = binder as SessionService.MyBinder
            sessionService = b.getService()
            sessionService?.setCallBack(callBack)

        }

        override fun onServiceDisconnected(name: ComponentName?) {
            sessionService = null
        }

    }

}
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

class MainFragment : Fragment(){

    companion object {
        fun newInstance() = MainFragment()
    }

    private var sessionService: SessionService? = null
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = MainFragmentBinding.bind(view)
        binding.btnStartService.setOnClickListener{
            val intent = Intent(context, SessionService::class.java)
            requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            binding.tvStatus.text = "Service started......"
        }

        binding.btnPing.setOnClickListener{
            sessionService?.setEvent(0)
        }

        binding.btnPong.setOnClickListener{
            sessionService?.setEvent(1)
        }


    }

    private val serviceConnection = object: ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val b = binder as SessionService.MyBinder
            sessionService = b.getService()

        }

        override fun onServiceDisconnected(name: ComponentName?) {
            sessionService = null
        }

    }

}
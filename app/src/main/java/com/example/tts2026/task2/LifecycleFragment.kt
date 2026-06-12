package com.example.tts2026.task2

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.tts2026.R

class LifecycleFragment : Fragment() {

    private var txtLifecycleHistory: TextView? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        recordEvent("onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recordEvent("onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        recordEvent("onCreateView")
        return inflater.inflate(R.layout.fragment_lifecycle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        txtLifecycleHistory = view.findViewById(R.id.txtFragmentLifecycle)
        recordEvent("onViewCreated")
    }

    override fun onStart() {
        super.onStart()
        recordEvent("onStart")
    }

    override fun onResume() {
        super.onResume()
        recordEvent("onResume")
    }

    override fun onPause() {
        recordEvent("onPause")
        super.onPause()
    }

    override fun onStop() {
        recordEvent("onStop")
        super.onStop()
    }

    override fun onDestroyView() {
        recordEvent("onDestroyView")
        txtLifecycleHistory = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        recordEvent("onDestroy")
        super.onDestroy()
    }

    override fun onDetach() {
        recordEvent("onDetach")
        super.onDetach()
    }

    private fun recordEvent(event: String) {
        lifecycleHistory.add(event)
        Log.d(TAG, event)
        txtLifecycleHistory?.text = lifecycleHistory.joinToString(separator = "\n")
    }

    companion object {
        private const val TAG = "FragmentLifecycle"
        private val lifecycleHistory = mutableListOf<String>()
    }
}

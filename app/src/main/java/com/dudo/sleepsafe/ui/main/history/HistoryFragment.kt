package com.dudo.sleepsafe.ui.main.history

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer

import com.dudo.sleepsafe.R
import com.dudo.sleepsafe.di.Injector
import com.dudo.sleepsafe.utils.ViewModelFactory
import com.dudo.sleepsafe.utils.injectViewModel
import javax.inject.Inject

class HistoryFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: HistoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.history_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Injector.initHistoryComponent()
        Injector.historyComponent?.inject(this)
        viewModel = injectViewModel(viewModelFactory)
        viewModel.refresh()
        viewModel.activities.observe(this, Observer {
            Toast.makeText(context, "${it.size}", Toast.LENGTH_LONG).show()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Injector.releaseHistoryComponent()
    }

}

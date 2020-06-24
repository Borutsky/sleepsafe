package com.dudo.sleepsafe.ui.main.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.dudo.sleepsafe.R
import com.dudo.sleepsafe.di.Injector
import com.dudo.sleepsafe.utils.ViewModelFactory
import com.dudo.sleepsafe.utils.injectViewModel
import kotlinx.android.synthetic.main.history_fragment.*
import javax.inject.Inject

class HistoryFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: HistoryViewModel
    private lateinit var detectedActivitiesAdapter: DetectedActivitiesAdapter

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
        detectedActivitiesAdapter = DetectedActivitiesAdapter(requireContext())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = detectedActivitiesAdapter
        viewModel.activities.observe(viewLifecycleOwner, Observer {
            recyclerView.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
            textEmpty.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            detectedActivitiesAdapter.updateList(it.toMutableList())
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Injector.releaseHistoryComponent()
    }

}

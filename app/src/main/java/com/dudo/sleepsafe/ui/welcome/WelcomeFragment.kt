package com.dudo.sleepsafe.ui.welcome


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.dudo.sleepsafe.R
import kotlinx.android.synthetic.main.fragment_welcome.*
import java.text.FieldPosition

class WelcomeFragment : androidx.fragment.app.Fragment() {

    companion object {

        const val POSITION = "position"

        fun newInstance(position: Int) : WelcomeFragment {
            val newFragment = WelcomeFragment()
            val bundle = Bundle()
            bundle.putInt(POSITION, position)
            newFragment.arguments = bundle
            return newFragment
        }

    }

    private var position: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        position = arguments?.getInt(POSITION, 0) ?: 0
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textInfo.text = "Position $position"
        image.setImageResource(when(position){
            1 -> R.drawable.shape_two
            2 -> R.drawable.shape_three
            4 -> R.drawable.shape_two
            else -> R.drawable.shape_one
        })
    }

}

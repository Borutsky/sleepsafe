package com.dudo.sleepsafe.ui.main.settings

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.dudo.sleepsafe.R
import com.dudo.sleepsafe.di.Injector
import com.dudo.sleepsafe.utils.*
import kotlinx.android.synthetic.main.settings_fragment.*
import javax.inject.Inject

class SettingsFragment : Fragment() {

    companion object {
        private const val REQUEST_CODE = 395
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.settings_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Injector.initSettingsComponent()
        Injector.settingsComponent?.inject(this)
        viewModel = injectViewModel(viewModelFactory)
        initRadioButtons()
        initSwitches()
    }

    private fun initRadioButtons() {
        val data = arrayOf("First", "Second", "Third")
        data.forEach {
            val radioButton = RadioButton(context)
            val params =
                RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            params.setMargins(0, context!!.dpToPx(10f).toInt(), 0, 0)
            radioButton.layoutParams = params
            radioButton.text = it
            radioButton.setTextColor(Color.WHITE)
            radioGroupSounds.addView(radioButton)
        }
        radioGroupSounds.check(radioGroupSounds.getChildAt(viewModel.soundIndex.value ?: 0).id)
        radioGroupSounds.setOnCheckedChangeListener { group, checkedId ->
            val index = group.indexOfChild(group.findViewById(checkedId))
            viewModel.soundIndex.value = index
        }
    }

    private fun initSwitches(){
        switchVibration.isChecked = viewModel.vibration.value ?: false
        switchFlash.isChecked = viewModel.flashlight.value ?: false
        vibrationBlock.setOnClickListener {
            switchVibration.performClick()
        }
        flashBlock.setOnClickListener {
            switchFlash.performClick()
        }
        switchVibration.setOnCheckedChangeListener { _, isChecked ->
            viewModel.vibration.value = isChecked
        }
        switchFlash.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                if(checkCameraPermission()){
                    viewModel.flashlight.value = isChecked
                } else {
                    requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CODE)
                }
            } else {
                viewModel.flashlight.value = isChecked
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                viewModel.flashlight.value = true
            } else {
                toast(R.string.permission_not_granted)
                viewModel.flashlight.value = false
                switchFlash.isChecked = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Injector.releaseSettingsComponent()
    }

}

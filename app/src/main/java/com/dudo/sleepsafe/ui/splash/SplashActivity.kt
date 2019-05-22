package com.dudo.sleepsafe.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dudo.sleepsafe.R
import com.dudo.sleepsafe.di.Injector
import com.dudo.sleepsafe.ui.main.MainActivity
import com.dudo.sleepsafe.ui.welcome.WelcomeActivity
import com.dudo.sleepsafe.utils.ViewModelFactory
import com.dudo.sleepsafe.utils.injectViewModel
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Injector.initSplashComponent()
        Injector.splashComponent?.inject(this)
        viewModel = injectViewModel(viewModelFactory)
        val intent = if(viewModel.isFirst.value == true){
            Intent(applicationContext, WelcomeActivity::class.java)
        } else {
            Intent(applicationContext, MainActivity::class .java)
        }
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        Injector.releaseSplashComponent()
    }

}

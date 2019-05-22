package com.dudo.sleepsafe.ui.welcome

import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.dudo.sleepsafe.R
import com.dudo.sleepsafe.di.Injector
import com.dudo.sleepsafe.ui.main.MainActivity
import com.dudo.sleepsafe.utils.ViewModelFactory
import com.dudo.sleepsafe.utils.injectViewModel
import kotlinx.android.synthetic.main.activity_welcome.*
import javax.inject.Inject

class WelcomeActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var viewModel: WelcomeViewModel
    private lateinit var welcomePagerAdapter: WelcomePagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        Injector.initWelcomeComponent()
        Injector.welcomeComponent?.inject(this)
        viewModel = injectViewModel(viewModelFactory)
        initViewPager()
        initNextButton()
    }

    private fun initViewPager() {
        welcomePagerAdapter = WelcomePagerAdapter(supportFragmentManager)
        viewPager.adapter = welcomePagerAdapter
        indicator.setViewPager(viewPager)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(position: Int) {
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(position: Int) {
                buttonNext.visibility = if (position == 4) View.VISIBLE else View.GONE
            }
        })
    }

    private fun initNextButton() {
        buttonNext.setOnClickListener {
            viewModel.firstEntryDone()
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Injector.releaseWelcomeComponent()
    }

}

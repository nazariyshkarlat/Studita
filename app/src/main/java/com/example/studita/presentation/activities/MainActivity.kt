package com.example.studita.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.fragments.IncorrectTimeFragment
import com.example.studita.presentation.fragments.MainFragment
import com.example.studita.utils.*
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.view_model.MainActivityNavigationViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.frame_layout.*
import java.sql.Time

class MainActivity : DefaultActivity(){

    private lateinit var navigationViewModel : MainActivityNavigationViewModel

    companion object{
        var needsRefresh = false
        var needsRecreate = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frame_layout)

        navigationViewModel =
            ViewModelProviders.of(this).get(MainActivityNavigationViewModel::class.java)

        if(savedInstanceState == null) {
            if(TimeUtils.timeIsAutomatically(frameLayout.context))
                addFragment(MainFragment(), R.id.frameLayout)
            else
                addFragment(IncorrectTimeFragment(), R.id.frameLayout)
        }

        FirebaseApp.initializeApp(application)
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FSDF", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
                Log.d("TAG ASD", token!!)
            })

    }

    override fun onResume() {
        super.onResume()
        if (needsRefresh) {
            needsRefresh = false
            this.recreate()
        }else if(needsRecreate){
            needsRecreate = false
            startActivity<MainActivity>()
            this.finish()
        }
    }

    override fun onBackPressed() {
        Log.d("ON_BACK_PRESSED", "fsdfsdf")
        when (val currentFragment = supportFragmentManager.findFragmentById(R.id.frameLayout)) {
            is NavigatableFragment -> currentFragment.onBackClick()
            is MainFragment -> navigationViewModel.onBackPressed()
            else -> super.onBackPressed()
        }
    }

}
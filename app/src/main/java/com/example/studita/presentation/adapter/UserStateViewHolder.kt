package com.example.studita.presentation.adapter

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.studita.R
import com.example.studita.presentation.extensions.addFragment
import com.example.studita.presentation.extensions.getAppCompatActivity
import com.example.studita.presentation.extensions.navigateTo
import com.example.studita.presentation.fragments.UserStatFragment
import kotlinx.android.synthetic.main.home_layout_user_state.*
import kotlinx.android.synthetic.main.home_layout_user_state.view.*

class UserStateViewHolder(view: View) : LevelsViewHolder<Nothing>(view){

    override fun bind(model: Any) {
        itemView.homeLayoutUserStateXPLayoutMoreButton.setOnClickListener {
            itemView.getAppCompatActivity()?.navigateTo(UserStatFragment(), R.id.frameLayout)
        }
    }

}
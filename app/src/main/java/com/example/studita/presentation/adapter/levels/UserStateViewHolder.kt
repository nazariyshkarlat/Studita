package com.example.studita.presentation.adapter.levels

import android.view.View
import com.example.studita.R
import com.example.studita.presentation.extensions.getAppCompatActivity
import com.example.studita.presentation.extensions.navigateTo
import com.example.studita.presentation.fragments.UserStatFragment
import com.example.studita.presentation.model.LevelUiModel
import kotlinx.android.synthetic.main.home_layout_user_state.view.*

class UserStateViewHolder(view: View) : LevelsViewHolder<Nothing>(view){

    override fun bind(model: LevelUiModel?) {
        itemView.homeLayoutUserStateXPLayoutMoreButton.setOnClickListener {
            itemView.getAppCompatActivity()?.navigateTo(UserStatFragment(), R.id.frameLayout)
        }
    }

}
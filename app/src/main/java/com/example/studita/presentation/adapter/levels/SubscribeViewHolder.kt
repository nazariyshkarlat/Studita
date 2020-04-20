package com.example.studita.presentation.adapter.levels

import android.view.View
import com.example.studita.presentation.activities.MainMenuActivity
import com.example.studita.presentation.model.HomeRecyclerUiModel
import com.example.studita.presentation.utils.UserUtils
import com.example.studita.presentation.utils.getAppCompatActivity
import com.example.studita.presentation.utils.startActivity
import com.example.studita.presentation.view_model.HomeFragmentViewModel
import kotlinx.android.synthetic.main.level_subscribe_item.view.*

class SubscribeViewHolder(view: View, private val homeFragmentViewModel: HomeFragmentViewModel) : LevelsViewHolder<HomeRecyclerUiModel.LevelSubscribeUiModel>(view){

    override fun bind(model: HomeRecyclerUiModel) {
        model as HomeRecyclerUiModel.LevelSubscribeUiModel
        itemView.levelSubscribeItemTitle.text = model.title
        if(!UserUtils.isLoggedIn()) {
            itemView.levelSubscribeItemButton.text = model.button[0]
            itemView.levelSubscribeItemButton.setOnClickListener {
                itemView.getAppCompatActivity()?.startActivity<MainMenuActivity>()
            }
        }else{
            UserUtils.userData?.let { userData ->
                itemView.levelSubscribeItemButton.setOnClickListener {
                    UserUtils.getUserTokenIdData()?.let { userTokenIdData ->
                        homeFragmentViewModel.subscribeEmail(
                            userTokenIdData,
                            !userData.isSubscribed
                        )
                    }
                }
                itemView.levelSubscribeItemButton.text =
                    model.button[if (userData.isSubscribed) 1 else 0]
            }
        }
    }
}
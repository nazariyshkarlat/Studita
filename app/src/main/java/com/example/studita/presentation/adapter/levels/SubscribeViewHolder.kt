package com.example.studita.presentation.adapter.levels

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.example.studita.presentation.activities.MainMenuActivity
import com.example.studita.presentation.model.HomeRecyclerUiModel
import com.example.studita.presentation.view_model.HomeFragmentViewModel
import com.example.studita.utils.UserUtils
import com.example.studita.utils.getAppCompatActivity
import com.example.studita.utils.startActivity
import kotlinx.android.synthetic.main.level_subscribe_item.view.*

class SubscribeViewHolder(view: View, private val homeFragmentViewModel: HomeFragmentViewModel, private val lifecycleOwner: LifecycleOwner) :
    LevelsViewHolder<HomeRecyclerUiModel.LevelSubscribeUiModel>(view) {

    override fun bind(model: HomeRecyclerUiModel) {
        model as HomeRecyclerUiModel.LevelSubscribeUiModel
        itemView.levelSubscribeItemTitle.text = model.title
        if (!UserUtils.isLoggedIn()) {
            itemView.levelSubscribeItemButton.text = model.button[0]
            itemView.levelSubscribeItemButton.setOnClickListener {
                itemView.getAppCompatActivity()?.startActivity<MainMenuActivity>()
            }
        } else {
            UserUtils.userDataLiveData.observe(lifecycleOwner, Observer { userData->
                itemView.levelSubscribeItemButton.text =
                    model.button[if (userData.isSubscribed) 1 else 0]
                itemView.levelSubscribeItemButton.setOnClickListener {
                    userData.isSubscribed = !userData.isSubscribed
                    itemView.levelSubscribeItemButton.text =
                        model.button[if (userData.isSubscribed) 1 else 0]
                    UserUtils.getUserIDTokenData()?.let { userTokenIdData ->
                        homeFragmentViewModel.subscribeEmail(
                            userTokenIdData,
                            userData.isSubscribed
                        )
                    }
                }
            })
        }
    }
}
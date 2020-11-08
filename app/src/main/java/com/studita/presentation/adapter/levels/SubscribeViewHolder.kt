package com.studita.presentation.adapter.levels

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.studita.presentation.activities.MainMenuActivity
import com.studita.presentation.model.HomeRecyclerUiModel
import com.studita.presentation.view_model.HomeFragmentViewModel
import com.studita.utils.UserUtils
import com.studita.utils.UserUtils.observeNoNull
import com.studita.utils.getAppCompatActivity
import com.studita.utils.startActivity
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
            UserUtils.userDataLiveData.observeNoNull(lifecycleOwner, Observer { userData->
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
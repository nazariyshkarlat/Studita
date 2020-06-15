package com.example.studita.presentation.adapter.users_list

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.view.forEachIndexed
import com.example.studita.R
import com.example.studita.domain.repository.UsersRepository
import com.example.studita.presentation.model.UsersRecyclerUiModel
import com.example.studita.presentation.view_model.FriendsFragmentViewModel
import com.example.studita.utils.dpToPx
import com.example.studita.utils.getAppCompatActivity
import com.example.studita.utils.makeView
import com.example.studita.utils.showKeyboard
import kotlinx.android.synthetic.main.friends_filter_popup_layout.view.*
import kotlinx.android.synthetic.main.users_search_item.view.*


class SearchViewHolder(view: View,
                       private val updateCallback: UpdateCallback,
                       private val searchCallback: SearchCallback,
                       private var sortBy: UsersRepository.SortBy,
                       public var searchState: FriendsFragmentViewModel.SearchState,
                       private var showSearchCallback: ShowSearchCallback,
                       private val globalSearchOnly: Boolean) : UsersViewHolder<UsersRecyclerUiModel.SearchUiModel>(view) {

    var selectedPos = sortBy.ordinal + 1

    override fun bind(model: UsersRecyclerUiModel) {
        var popUp = formPopUp()
        itemView.usersSearchItemFilter.setOnClickListener {
            popUp = formPopUp()
            popUp.showAsDropDown(itemView.usersSearchItemFilter, 0, (-14).dpToPx(), Gravity.START)
            (popUp.contentView as ViewGroup).setSelectedItem(selectedPos)
        }

        (popUp.contentView as ViewGroup).setSelectedItem(selectedPos)
        itemView.usersSearchItemEditText.clearFocus()

        itemView.usersSearchItemEditText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().isNotEmpty())
                    itemView.usersSearchItemEditTextClearTextButton.visibility = View.VISIBLE
                else
                    itemView.usersSearchItemEditTextClearTextButton.visibility = View.GONE
            }

        })

        itemView.usersSearchItemEditTextClearTextButton.setOnClickListener {
            itemView.usersSearchItemEditText.text.clear()
        }

        if(searchState != FriendsFragmentViewModel.SearchState.NoSearch){
            itemView.usersSearchItemFilter.visibility = View.GONE

            if(!globalSearchOnly)
                itemView.usersSearchItemSearchRadioGroup.visibility = View.VISIBLE
            else {
                setSearchMargin()
                itemView.usersSearchItemEditText.requestFocus()
                itemView.getAppCompatActivity()?.showKeyboard()
            }


            searchState.let {
                if (it is FriendsFragmentViewModel.SearchState.GlobalSearch) {
                    itemView.usersSearchItemEditText.setText(it.startsWith)
                }else if (it is FriendsFragmentViewModel.SearchState.FriendsSearch) {
                    itemView.usersSearchItemEditText.setText(it.startsWith)
                }
                itemView.usersSearchItemEditText.setSelection(itemView.usersSearchItemEditText.text.toString().length)
            }
        }else {
            itemView.usersSearchItemFilter.visibility = View.VISIBLE
            itemView.usersSearchItemSearchRadioGroup.visibility = View.GONE
            itemView.usersSearchItemEditText.text?.clear()

            itemView.usersSearchItemEditText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    itemView.usersSearchItemFilter.visibility = View.GONE
                    itemView.usersSearchItemSearchRadioGroup.visibility = View.VISIBLE

                    if (searchState == FriendsFragmentViewModel.SearchState.NoSearch) {
                        showSearchCallback.onSearchVisibilityChanged(true)
                        searchState = FriendsFragmentViewModel.SearchState.FriendsSearch("")
                        selectRadioButton()
                    }
                }
            }
        }

        itemView.usersSearchItemEditText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchState.let {
                    if (it is FriendsFragmentViewModel.SearchState.GlobalSearch) {
                        if(it.startsWith != s.toString()) {
                            searchState = FriendsFragmentViewModel.SearchState.GlobalSearch(s.toString())
                            searchCallback.search(s.toString(), searchState)
                        }
                    }else if (it is FriendsFragmentViewModel.SearchState.FriendsSearch) {
                        if (it.startsWith != s.toString()) {
                            searchState = FriendsFragmentViewModel.SearchState.FriendsSearch(s.toString())
                            searchCallback.search(s.toString(), searchState)
                        }
                    }
                }
             }
        })

        selectRadioButton()

        itemView.usersSearchItemSearchRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            searchState = if(checkedId == R.id.usersSearchItemGlobalSearchRadioButton) {
                FriendsFragmentViewModel.SearchState.GlobalSearch(itemView.usersSearchItemEditText.text.toString())
            }else {
                FriendsFragmentViewModel.SearchState.FriendsSearch(itemView.usersSearchItemEditText.text.toString())
            }
            searchCallback.search(itemView.usersSearchItemEditText.text.toString(), searchState)
        }
    }

    interface UpdateCallback {
        fun update(sortBy: UsersRepository.SortBy)
    }

    interface SearchCallback {
        fun search(text: String, searchState: FriendsFragmentViewModel.SearchState)
    }

    interface ShowSearchCallback{
        fun onSearchVisibilityChanged(visible: Boolean)
    }

    private fun selectRadioButton(){
        if(searchState is FriendsFragmentViewModel.SearchState.GlobalSearch)
            itemView.usersSearchItemGlobalSearchRadioButton.isChecked = true
        else if(searchState is FriendsFragmentViewModel.SearchState.FriendsSearch)
            itemView.usersSearchItemFriendsRadioButton.isChecked = true
        itemView.usersSearchItemGlobalSearchRadioButton.jumpDrawablesToCurrentState()
        itemView.usersSearchItemFriendsRadioButton.jumpDrawablesToCurrentState()
    }

    private fun getSortBy(view: View) =
        when (view.id) {
            R.id.friendsFilterPopupLayoutItemAtoZ -> UsersRepository.SortBy.A_TO_Z
            R.id.friendsFilterPopupLayoutItemZtoA -> UsersRepository.SortBy.Z_TO_A
            R.id.friendsFilterPopupLayoutItemNewToOld -> UsersRepository.SortBy.NEW_TO_OLD
            R.id.friendsFilterPopupLayoutItemOldToNew -> UsersRepository.SortBy.OLD_TO_NEW
            else -> UsersRepository.SortBy.NEW_TO_OLD
        }

    private fun formPopUp() = PopupWindow(itemView.context).apply {
        contentView = (itemView as ViewGroup).makeView(R.layout.friends_filter_popup_layout) as ViewGroup
        contentView.post {
            (contentView as ViewGroup).forEachIndexed { index, child ->
                if (child is ViewGroup) {
                    child.setOnClickListener {
                        it.friendsFilterPopupLayoutItemCheckIcon.isSelected = false
                        selectedPos = index
                        (contentView as ViewGroup).setSelectedItem(selectedPos)
                        updateCallback.update(getSortBy(it))
                        dismiss()
                    }
                }
            }
        }
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isFocusable = true
        isOutsideTouchable = true
    }

    private fun ViewGroup.setSelectedItem(selectedPos: Int){
        with(this.getChildAt(selectedPos)) {
            this.friendsFilterPopupLayoutItemCheckIcon.isSelected = true
            itemView.usersSearchItemFilterText.text =
                this.friendsFilterPopupLayoutItemTextView.text
        }
    }

    private fun setSearchMargin(){
        val param = itemView.usersSearchItemEditText.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(0, 0,0, 8.dpToPx())
        itemView.usersSearchItemEditText.layoutParams = param
    }

}
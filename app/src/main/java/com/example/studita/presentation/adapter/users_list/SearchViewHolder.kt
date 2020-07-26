package com.example.studita.presentation.adapter.users_list

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.view.children
import com.example.studita.R
import com.example.studita.domain.repository.UsersRepository
import com.example.studita.presentation.model.UsersRecyclerUiModel
import com.example.studita.presentation.view_model.FriendsFragmentViewModel
import com.example.studita.utils.*
import com.example.studita.utils.TextUtils.isFullNameCharAllowed
import com.example.studita.utils.TextUtils.isSearchCharAllowed
import kotlinx.android.synthetic.main.edit_profile_layout.*
import kotlinx.android.synthetic.main.friends_filter_popup_layout.view.*
import kotlinx.android.synthetic.main.users_search_item.view.*


class SearchViewHolder(view: View,
                       private val updateCallback: UpdateCallback,
                       private val searchCallback: SearchCallback,
                       sortBy: UsersRepository.SortBy,
                       var searchState: FriendsFragmentViewModel.SearchState,
                       private var showSearchCallback: ShowSearchCallback,
                       private val globalSearchOnly: Boolean) : UsersViewHolder<UsersRecyclerUiModel.SearchUiModel>(view) {

    var selectedPos = sortBy.ordinal

    override fun bind(model: UsersRecyclerUiModel) {
        itemView.post { formPopUp() }
        itemView.usersSearchItemFilter.setOnClickListener {
            formPopUp().showAsDropDown(itemView.usersSearchItemFilter, 0, (-4).dpToPx(), Gravity.START)
        }

        itemView.usersSearchItemEditText.clearFocus()

        val searchFilter =
            InputFilter { source, start, end, dest, _, dend ->
                var keepOriginal = true
                val sb = StringBuilder(end - start)
                for (i in start until end) {
                    val c: Char = source[i]
                    if (c.isFullNameCharAllowed(dest.getOrNull(dend-1)) || dest.isEmpty())
                        sb.append(c) else keepOriginal = false
                }
                if (keepOriginal) null else {
                    if (source is Spanned) {
                        val sp = SpannableString(sb)
                        TextUtils.copySpansFrom(
                            source,
                            start,
                            end,
                            null,
                            sp,
                            0
                        )
                        sp
                    } else {
                        sb
                    }
                }
            }
        itemView.usersSearchItemEditText.filters = arrayOf(searchFilter)

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
                if(s.toString().isNotEmpty())
                    itemView.usersSearchItemEditTextClearTextButton.visibility = View.VISIBLE
                else
                    itemView.usersSearchItemEditTextClearTextButton.visibility = View.GONE

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
            (((contentView as ViewGroup).getChildAt(1) as ViewGroup).getChildAt(0) as ViewGroup).children.forEachIndexed { index, child ->
                child.setOnClickListener {
                    it.friendsFilterPopupLayoutItemCheckIcon.isSelected = false
                    selectedPos = index
                    (contentView as ViewGroup).setSelectedItem(selectedPos)
                    updateCallback.update(getSortBy(it))
                    dismiss()
                }
            }
        }
        (contentView as ViewGroup).setSelectedItem(selectedPos)
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isFocusable = true
        isOutsideTouchable = true
    }

    private fun ViewGroup.setSelectedItem(selectedPos: Int){
        with(((getChildAt(1) as ViewGroup).getChildAt(0) as ViewGroup).getChildAt(selectedPos)) {
            this.friendsFilterPopupLayoutItemCheckIcon.isSelected = true
            itemView.usersSearchItemFilterText.text =
                this.friendsFilterPopupLayoutItemTextView.text
        }
    }

    private fun setSearchMargin(){
        val param = itemView.usersSearchItemSearchLayout.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(0, 0,0, 8.dpToPx())
        itemView.usersSearchItemSearchLayout.layoutParams = param
    }

}
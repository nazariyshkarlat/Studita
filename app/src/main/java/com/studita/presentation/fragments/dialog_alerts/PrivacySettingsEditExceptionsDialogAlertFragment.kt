package com.studita.presentation.fragments.dialog_alerts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.studita.R
import com.studita.presentation.adapter.privacy_settings_duels_exceptions.PrivacySettingsDuelsExceptionsAdapter
import com.studita.presentation.fragments.base.BaseDialogFragment
import com.studita.presentation.model.PrivacySettingsDuelsExceptionsRecyclerUiModel
import com.studita.presentation.model.toUiModel
import com.studita.presentation.view_model.PrivacySettingsDuelsExceptionsViewModel
import com.studita.presentation.views.CustomSnackbar
import com.studita.utils.ThemeUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.home_layout.*
import kotlinx.android.synthetic.main.privacy_duels_exceptions_dialog_alert.*
import kotlinx.android.synthetic.main.recyclerview_layout.*

class PrivacySettingsEditExceptionsDialogAlertFragment :
    BaseDialogFragment(R.layout.privacy_duels_exceptions_dialog_alert) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel =
            ViewModelProviders.of(this).get(PrivacySettingsDuelsExceptionsViewModel::class.java)

        privacyDuelsExceptionsRecyclerView.isSaveEnabled = false

        viewModel.privacySettingsDuelsExceptionsState.observe(
            viewLifecycleOwner,
            Observer { pair ->

                val canBeMoreItems = pair.first

                when (val privacySettingsDuelsExceptionsResultState = pair.second) {
                    is PrivacySettingsDuelsExceptionsViewModel.DuelsExceptionsResultState.FirstResults -> {

                        val items =
                            if (viewModel.recyclerItems != null) viewModel.recyclerItems!! else ArrayList(
                                viewModel.getRecyclerItems(
                                    privacySettingsDuelsExceptionsResultState.results,
                                    if (canBeMoreItems) PrivacySettingsDuelsExceptionsRecyclerUiModel.ProgressUiModel else null
                                )
                            )
                        val adapter = PrivacySettingsDuelsExceptionsAdapter(
                            items,
                            viewModel
                        )
                        privacyDuelsExceptionsRecyclerView.adapter = adapter
                        viewModel.recyclerItems = adapter.items
                    }
                    is PrivacySettingsDuelsExceptionsViewModel.DuelsExceptionsResultState.MoreResults -> {

                        if (privacyDuelsExceptionsRecyclerView.adapter != null) {

                            val items = listOf(
                                *privacySettingsDuelsExceptionsResultState.results.map { it.toUiModel() }
                                    .toTypedArray(),
                                *(if (canBeMoreItems) arrayOf(
                                    PrivacySettingsDuelsExceptionsRecyclerUiModel.ProgressUiModel
                                ) else emptyArray())
                            )

                            val adapter =
                                privacyDuelsExceptionsRecyclerView.adapter as PrivacySettingsDuelsExceptionsAdapter

                            val removePos = adapter.items.lastIndex
                            adapter.items.removeAt(removePos)
                            adapter.notifyItemRemoved(removePos)

                            val insertIndex = adapter.items.size
                            adapter.items.addAll(items)
                            adapter.notifyItemRangeInserted(
                                insertIndex,
                                items.size
                            )
                        } else {
                            val adapter = PrivacySettingsDuelsExceptionsAdapter(
                                viewModel.recyclerItems!!,
                                viewModel
                            )
                            privacyDuelsExceptionsRecyclerView.adapter = adapter
                        }
                    }
                    is PrivacySettingsDuelsExceptionsViewModel.DuelsExceptionsResultState.NoMoreResultsFound -> {

                        if (privacyDuelsExceptionsRecyclerView.adapter != null) {
                            val progressIndex =
                                viewModel.recyclerItems?.indexOfFirst { it is PrivacySettingsDuelsExceptionsRecyclerUiModel.ProgressUiModel }
                            if (progressIndex != null && progressIndex != -1) {
                                viewModel.recyclerItems?.removeAt(progressIndex)
                                privacyDuelsExceptionsRecyclerView.adapter?.notifyItemRemoved(
                                    progressIndex
                                )
                            }
                        } else {
                            val adapter = PrivacySettingsDuelsExceptionsAdapter(
                                viewModel.recyclerItems!!,
                                viewModel
                            )
                            privacyDuelsExceptionsRecyclerView.adapter = adapter
                        }
                    }
                }
            })

        viewModel.progressState.observe(
            viewLifecycleOwner,
            Observer { showProgress ->
                if (showProgress) {
                    privacyDuelsExceptionsProgressBar.visibility = View.VISIBLE
                    privacyDuelsExceptionsRecyclerView.visibility = View.GONE
                } else {
                    privacyDuelsExceptionsProgressBar.visibility = View.GONE
                    privacyDuelsExceptionsRecyclerView.visibility = View.VISIBLE
                }
            })

        val context = activity as AppCompatActivity

        viewModel.privacySettingsEditDuelsExceptionsState.observe(context, Observer {
            if (it) {
                CustomSnackbar(context).show(
                    context.resources.getString(R.string.changes_are_saved),
                    ColorUtils.compositeColors(
                        ThemeUtils.getAccentLiteColor(context),
                        ContextCompat.getColor(context, R.color.white)
                    ),
                    ContextCompat.getColor(context, R.color.black)
                )
            }else{
                CustomSnackbar(context).show(
                    resources.getString(R.string.server_temporarily_unavailable),
                    ThemeUtils.getRedColor(context),
                    ContextCompat.getColor(context, R.color.white)
                )
            }
        })

        privacyDuelsExceptionsLeftButton.setOnClickListener {
            dismiss()
        }

        privacyDuelsExceptionsRightButton.setOnClickListener {
            viewModel.editDuelsExceptions()
            targetFragment?.onActivityResult(345, Activity.RESULT_OK, Intent().apply {
                if (viewModel.editedDuelsExceptionsData.isNotEmpty())
                    putExtra(
                        "CHANGED_EXCEPTIONS",
                        Gson().toJson(viewModel.editedDuelsExceptionsData)
                    )
            })
            dismiss()
        }

        privacyDuelsExceptionsRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                checkScrollY()
            }

        })
    }

    override fun onDestroyView() {
        privacyDuelsExceptionsRecyclerView.clearOnScrollListeners()
        super.onDestroyView()
    }

    private fun checkScrollY() {
        privacyDuelsExceptionsRecyclerView.post {
            if (privacyDuelsExceptionsRecyclerView.canScrollVertically(-1) || privacyDuelsExceptionsRecyclerView.canScrollVertically(
                    1
                )
            ) {
                privacyDuelsExceptionsRecyclerView.background =
                    ContextCompat.getDrawable(
                        privacyDuelsExceptionsRecyclerView.context,
                        when {
                            !privacyDuelsExceptionsRecyclerView.canScrollVertically(-1) -> R.drawable.divider_bottom_drawable
                            !privacyDuelsExceptionsRecyclerView.canScrollVertically(1) -> R.drawable.divider_top_drawable
                            else -> R.drawable.divider_top_bottom_drawable
                        }
                    )
            }
        }
    }


}
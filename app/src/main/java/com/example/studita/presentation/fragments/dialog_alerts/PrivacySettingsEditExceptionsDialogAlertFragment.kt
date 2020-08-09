package com.example.studita.presentation.fragments.dialog_alerts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.example.studita.R
import com.example.studita.presentation.adapter.privacy_settings_duels_exceptions.PrivacySettingsDuelsExceptionsAdapter
import com.example.studita.presentation.fragments.base.BaseDialogFragment
import com.example.studita.presentation.model.PrivacySettingsDuelsExceptionsRecyclerUiModel
import com.example.studita.presentation.model.toUiModel
import com.example.studita.presentation.view_model.PrivacySettingsDuelsExceptionsViewModel
import com.example.studita.presentation.views.CustomSnackbar
import com.example.studita.utils.ThemeUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.privacy_duels_exceptions_dialog_alert.*

class PrivacySettingsEditExceptionsDialogAlertFragment :
    BaseDialogFragment(R.layout.privacy_duels_exceptions_dialog_alert) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel =
            ViewModelProviders.of(this).get(PrivacySettingsDuelsExceptionsViewModel::class.java)

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

                            if (!canBeMoreItems) {
                                adapter.items.removeAt(adapter.items.lastIndex)
                                adapter.notifyItemRemoved(adapter.itemCount - 1)
                            }
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
                    ThemeUtils.getAccentColor(context)
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
package com.studita.authenticator

import android.accounts.AccountManager
import android.app.Service
import android.content.Intent

import android.os.IBinder
import com.studita.App.Companion.recreateAppEvent
import com.studita.R
import com.studita.presentation.view_model.SingleLiveEvent
import com.studita.utils.UserUtils

class AuthenticatorService : Service() {
    override fun onBind(intent: Intent?): IBinder {
        val authenticator = AccountAuthenticator(this)

        AccountManager.get(this).addOnAccountsUpdatedListener({accounts->

            var removed = true

            accounts.forEach {
                if (it.type == this.getString(R.string.account_type)) {
                    removed = false
                }
            }

            if(removed && UserUtils.isLoggedIn()) {
                UserUtils.signOut(application)
                recreateAppEvent.value = Unit
                recreateAppEvent = SingleLiveEvent()
            }

        }, null, true)

        return authenticator.iBinder
    }
}
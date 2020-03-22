package com.example.studita.authenticator

import android.accounts.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.example.studita.R
import com.example.studita.presentation.activities.MainMenuActivity


class AccountAuthenticator(private val context: Context) :
    AbstractAccountAuthenticator(context) {

    companion object{
        fun addAccount(context: Context, email: String){
            val accountManager =
                AccountManager.get(context)

            val account = Account(
                email,
                context.getString(R.string.account_type)
            )
            accountManager.addAccountExplicitly(account, null,null)
        }
    }

    override fun editProperties(
        accountAuthenticatorResponse: AccountAuthenticatorResponse,
        s: String
    ): Bundle? {
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun addAccount(
        response: AccountAuthenticatorResponse,
        accountType: String,
        authTokenType: String?,
        requiredFeatures: Array<String>?,
        options: Bundle?): Bundle?
    {
        val intent = Intent(context, MainMenuActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)
        return bundle
    }

    @Throws(NetworkErrorException::class)
    override fun confirmCredentials(
        accountAuthenticatorResponse: AccountAuthenticatorResponse,
        account: Account,
        bundle: Bundle
    ): Bundle? {
        return null
    }

    override fun getAuthTokenLabel(s: String): String {
        return "full"
    }

    @Throws(NetworkErrorException::class)
    override fun updateCredentials(
        accountAuthenticatorResponse: AccountAuthenticatorResponse,
        account: Account,
        s: String,
        bundle: Bundle
    ): Bundle? {
        return null
    }

    override fun getAuthToken(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        authTokenType: String?,
        options: Bundle?
    ): Bundle? {
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun hasFeatures(
        accountAuthenticatorResponse: AccountAuthenticatorResponse,
        account: Account,
        strings: Array<String>
    ): Bundle {
        val result = Bundle()
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false)
        return result
    }

}
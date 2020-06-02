package com.example.studita.data.repository.datasource.privacy_settings

import com.example.studita.data.repository.datasource.subscribe_mail.SubscribeEmailDataStoreFactory
import com.example.studita.data.repository.datasource.subscribe_mail.SubscribeEmailDataStoreImpl

class PrivacySettingsDataStoreFactoryImpl(
        private val privacySettingsDataStore: PrivacySettingsDataStore
) : PrivacySettingsDataStoreFactory {

    override fun create() =
            privacySettingsDataStore
}

interface PrivacySettingsDataStoreFactory{

    fun create(): PrivacySettingsDataStore

}
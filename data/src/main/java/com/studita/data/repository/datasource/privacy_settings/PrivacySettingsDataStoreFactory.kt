package com.studita.data.repository.datasource.privacy_settings

class PrivacySettingsDataStoreFactoryImpl(
    private val privacySettingsDataStore: PrivacySettingsDataStore
) : PrivacySettingsDataStoreFactory {

    override fun create() =
        privacySettingsDataStore
}

interface PrivacySettingsDataStoreFactory {

    fun create(): PrivacySettingsDataStore

}
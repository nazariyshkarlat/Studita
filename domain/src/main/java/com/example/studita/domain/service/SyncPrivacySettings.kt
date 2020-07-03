package com.example.studita.domain.service

import com.example.studita.domain.entity.EditDuelsExceptionsRequestData
import com.example.studita.domain.entity.PrivacySettingsRequestData

interface SyncPrivacySettings {

    fun scheduleEditPrivacySettings(privacySettingsRequestData: PrivacySettingsRequestData)

    fun scheduleEditDuelsExceptions(editDuelsExceptionsRequestData: EditDuelsExceptionsRequestData)

}
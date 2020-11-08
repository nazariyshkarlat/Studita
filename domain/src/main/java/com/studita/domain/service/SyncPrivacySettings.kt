package com.studita.domain.service

import com.studita.domain.entity.EditDuelsExceptionsRequestData
import com.studita.domain.entity.PrivacySettingsRequestData

interface SyncPrivacySettings {

    fun scheduleEditPrivacySettings(privacySettingsRequestData: PrivacySettingsRequestData)

    fun scheduleEditDuelsExceptions(editDuelsExceptionsRequestData: EditDuelsExceptionsRequestData)

}
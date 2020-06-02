package com.example.studita.data.entity.mapper

import com.example.studita.data.entity.PrivacySettingsEntity
import com.example.studita.data.entity.PrivacySettingsRequest
import com.example.studita.domain.entity.DuelsInvitesFrom
import com.example.studita.domain.entity.PrivacySettingsData
import com.example.studita.domain.entity.PrivacySettingsRequestData
import java.io.IOException

class PrivacySettingsEntityMapper : Mapper<PrivacySettingsEntity, PrivacySettingsData>{
    override fun map(source: PrivacySettingsEntity) = PrivacySettingsData(source.duelsInvitesFrom?.let { mapDuelsInvitesFrom(it) }, source.showInRatings, source.profileIsVisible, source.duelsExceptions)

    private fun mapDuelsInvitesFrom(char: Char) = when(char){
        'f' -> DuelsInvitesFrom.FRIENDS
        'n' -> DuelsInvitesFrom.NOBODY
        'e' -> DuelsInvitesFrom.EXCEPT
        else -> throw IOException("unknown DuelsInvitesFrom char")
    }

}

class PrivacySettingsDataMapper : Mapper<PrivacySettingsData, PrivacySettingsEntity>{
    override fun map(source: PrivacySettingsData) = PrivacySettingsEntity(source.duelsInvitesFrom?.let { mapDuelsInvitesFrom(it) }, source.showInRatings, source.profileIsVisible, source.duelsExceptions)

    private fun mapDuelsInvitesFrom(duelsInvitesFrom: DuelsInvitesFrom) = when(duelsInvitesFrom){
        DuelsInvitesFrom.FRIENDS -> 'f'
        DuelsInvitesFrom.NOBODY -> 'n'
        DuelsInvitesFrom.EXCEPT -> 'e'
    }

}

class PrivacySettingsRequestMapper(private val userIdTokenMapper: UserIdTokenMapper, private val privacySettingsDataMapper: PrivacySettingsDataMapper) : Mapper<PrivacySettingsRequestData, PrivacySettingsRequest>{
    override fun map(source: PrivacySettingsRequestData): PrivacySettingsRequest = PrivacySettingsRequest(userIdTokenMapper.map(source.userIdToken), privacySettingsDataMapper.map(source.privacySettingsEntity))


}
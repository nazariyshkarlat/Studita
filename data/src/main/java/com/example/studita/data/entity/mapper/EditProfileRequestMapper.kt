package com.example.studita.data.entity.mapper

import com.example.studita.data.entity.EditProfileEntity
import com.example.studita.data.entity.EditProfileRequest
import com.example.studita.domain.entity.EditProfileData
import com.example.studita.domain.entity.EditProfileRequestData

class EditProfileRequestMapper(private val userIdTokenMapper: UserIdTokenMapper, private val editProfileEntityMapper: EditProfileEntityMapper) : Mapper<EditProfileRequestData, EditProfileRequest> {
    override fun map(source: EditProfileRequestData): EditProfileRequest = EditProfileRequest(userIdTokenMapper.map(source.userIdTokenData), editProfileEntityMapper.map(source.editProfileData))

}

class EditProfileEntityMapper: Mapper<EditProfileData, EditProfileEntity>{
    override fun map(source: EditProfileData): EditProfileEntity = EditProfileEntity(source.userName, source.userFullName, source.avatarLink)

}
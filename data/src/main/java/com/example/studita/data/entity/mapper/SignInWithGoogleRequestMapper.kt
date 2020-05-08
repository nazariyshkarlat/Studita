package com.example.studita.data.entity.mapper

import com.example.studita.data.entity.SignInWithGoogleRequestEntity
import com.example.studita.domain.entity.authorization.SignInWithGoogleRequestData

class SignInWithGoogleRequestMapper(private val userDataEntityMapper: UserDataEntityMapper, private val userStatisticsRowEntityMapper: UserStatisticsRowEntityMapper): Mapper<SignInWithGoogleRequestData, SignInWithGoogleRequestEntity> {

    override fun map(source: SignInWithGoogleRequestData): SignInWithGoogleRequestEntity = SignInWithGoogleRequestEntity(source.idToken,
        source.userDataData?.let { userDataEntityMapper.map(it) },
        source.userStatistics?.map {  userStatisticsRowEntityMapper.map(it)})

}

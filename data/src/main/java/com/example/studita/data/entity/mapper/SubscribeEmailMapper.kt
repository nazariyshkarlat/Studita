package com.example.studita.data.entity.mapper

import com.example.studita.data.entity.SubscribeEmailResultEntity
import com.example.studita.domain.entity.SubscribeEmailResultData

class SubscribeEmailDataMapper : Mapper<SubscribeEmailResultEntity, SubscribeEmailResultData>{

    override fun map(source: SubscribeEmailResultEntity): SubscribeEmailResultData = SubscribeEmailResultData(source.subscribe, source.email)

}

class SubscribeEmailEntityMapper : Mapper<SubscribeEmailResultData, SubscribeEmailResultEntity>{

    override fun map(source: SubscribeEmailResultData): SubscribeEmailResultEntity = SubscribeEmailResultEntity(source.subscribe, source.email)

}
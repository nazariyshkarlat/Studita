package com.example.studita.data.entity.mapper

import com.example.studita.data.entity.LogInResponseEntity
import com.example.studita.domain.entity.authorization.LogInResponseData

class LogInResponseDataMapper : Mapper<LogInResponseEntity, LogInResponseData>{
    override fun map(source: LogInResponseEntity): LogInResponseData {
        return LogInResponseData(
            source.userId,
            source.userToken
        )
    }
}
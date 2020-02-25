package com.example.studita.data.entity.mapper

interface Mapper<S, R> {

    fun map(source: S): R
}
package com.studita.presentation.model

import com.studita.domain.entity.exercise.Operator
import com.studita.domain.entity.exercise.toCharacter

fun Char.fromUiToDomainOperator() = when {
    this == '÷' -> Operator.DIVIDE.toCharacter()
    this == '×' -> Operator.MULTIPLY.toCharacter()
    else -> this
}
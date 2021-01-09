package com.studita.di

import org.koin.core.module.Module
import org.koin.dsl.ModuleDeclaration

fun configModule(createdAtStart: Boolean = false, override: Boolean = false, configuration: DI.Config, moduleDeclaration: ModuleDeclaration): Module {
    val module = Module(createdAtStart, override)
    moduleDeclaration(module)
    return module
}
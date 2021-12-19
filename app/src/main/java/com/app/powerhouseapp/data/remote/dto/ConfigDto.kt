package com.app.powerhouseapp.data.remote.dto

import com.app.powerhouseapp.domain.model.Config

data class ConfigDto(
    val DO_PO: Boolean,
    val camera: Boolean,
    val code: Int,
    val count_text: List<String>,
    val enable_manual: List<Int>,
    val enable_multiply: List<Int>,
    val location: Boolean,
    val photo_count: Int
)

fun ConfigDto.toConfig(): Config {
    return Config(
        code = code,
        count_text = count_text
    )
}

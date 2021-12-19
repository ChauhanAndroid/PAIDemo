package com.app.powerhouseapp.data.remote.dto

import com.app.powerhouseapp.domain.model.ImageUpload

data class ImageUploadDto(
    val code: Int,
    val message: String
)fun ImageUploadDto.toImageUpload(): ImageUpload {
    return ImageUpload(
        code = code,
        message = message
    )
}
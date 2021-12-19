package com.app.powerhouseapp.presentation.main.viewmodel.state

data class ConfigState(
    val isLoading: Boolean = false,
    val success: String = "",
    val error: String = ""
)

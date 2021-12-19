package com.app.powerhouseapp.domain.repository

import com.app.powerhouseapp.data.remote.dto.ConfigDto

interface ConfigRepository {

    suspend fun getConfig(body: HashMap<String,String>): ConfigDto

}
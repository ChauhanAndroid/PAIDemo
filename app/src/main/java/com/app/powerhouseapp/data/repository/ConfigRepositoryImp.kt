package com.app.powerhouseapp.data.repository

import com.app.powerhouseapp.data.remote.dto.ConfigDto
import com.app.powerhouseapp.data.remote.networkservice.RetrofitService
import com.app.powerhouseapp.domain.repository.ConfigRepository
import javax.inject.Inject

class ConfigRepositoryImp @Inject constructor(
    private val api: RetrofitService
) : ConfigRepository {
    override suspend fun getConfig(body: HashMap<String, String>): ConfigDto {
        return api.configureEmail(body)
    }

}
package com.app.powerhouseapp.domain.use_case

import com.app.powerhouseapp.common.NetworkResult
import com.app.powerhouseapp.data.remote.dto.toConfig
import com.app.powerhouseapp.domain.model.Config
import com.app.powerhouseapp.domain.repository.ConfigRepository
import com.app.powerhouseapp.domain.utils.ErrorMessage.Companion.ERROR_CHECK_NETWORK_CONNECTION
import com.app.powerhouseapp.domain.utils.ErrorMessage.Companion.UNKNOWN_ERROR
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class GetConfig @Inject constructor(
    private val repository: ConfigRepository
) {

    /**
     * Operator function to invoke getConfig
     */

    operator fun invoke(body: HashMap<String, String>): Flow<NetworkResult<Config>> = flow {
        try {
            emit(NetworkResult.Loading())
            val result = repository.getConfig(body).toConfig()
            Timber.e("result${result}")
            emit(NetworkResult.Success(result))
        } catch (e: HttpException) {
            emit(NetworkResult.Error(e.localizedMessage ?: UNKNOWN_ERROR))
        } catch (e: IOException) {
            emit(NetworkResult.Error(ERROR_CHECK_NETWORK_CONNECTION))
        }
    }

}
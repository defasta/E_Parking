package apps.eduraya.e_parking.data.network

import android.content.Context
import androidx.viewbinding.BuildConfig
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class RemoteDataSource @Inject constructor(){

    companion object{
        private const val BASE_URL = "https://api.e-parkingjogja.com/api/"
//        private const val BASE_URL = "http://192.168.1.20:8000/api/"
    }

    fun <Api> buildApi(
        api: Class<Api>,
        context: Context
    ): Api {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getRetrofitClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }

//    fun <Api> buildApi(
//        api: Class<Api>,
//        context: Context
//    ): Api {
//        val authenticator = TokenAuthenticator(context, buildTokenApi())
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .client(getRetrofitClient(authenticator))
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(api)
//    }

//    private fun buildTokenApi(): AuthApi {
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .client(getRetrofitClient())
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(AuthApi::class.java)
//    }

    private fun getRetrofitClient(authenticator: Authenticator? = null): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
//                chain.proceed(chain.request().newBuilder().also {
//                    it.addHeader("Authorization", "application/json")
//                }.build())
                chain.proceed(chain.request().newBuilder().build())
            }.also { client ->
                authenticator?.let { client.authenticator(it) }
                if (BuildConfig.DEBUG) {
                    val logging = HttpLoggingInterceptor()
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                    client.addInterceptor(logging)
                }
            }.build()
    }

}
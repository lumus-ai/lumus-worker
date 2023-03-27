package ai.lumus.worker.openai

import ai.lumus.worker.openai.request.ChatModel
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration

abstract class AbstractGptClient(
    private val openaiApiKey: String,
    internal val model: ChatModel,
) {

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer $openaiApiKey")
                .build()
            chain.proceed(request)
        }
        .callTimeout(Duration.ofMinutes(1))
        .readTimeout(Duration.ofMinutes(1))
        .writeTimeout(Duration.ofMinutes(1))
        .connectTimeout(Duration.ofMinutes(1))
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openai.com/v1/")
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    protected val apiService: GptApiInterface = retrofit.create(GptApiInterface::class.java)
}

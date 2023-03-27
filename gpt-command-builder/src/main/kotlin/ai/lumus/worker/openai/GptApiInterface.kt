package ai.lumus.worker.openai

import ai.lumus.worker.openai.request.ChatRequest
import ai.lumus.worker.openai.response.ChatResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GptApiInterface {

    @Headers("Content-Type: application/json")
    @POST("chat/completions")
    fun complete(@Body request: ChatRequest): Call<ChatResponse>
}

package com.example.core.network.di

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.okHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import javax.inject.Singleton

const val ANILIST_GRAPHQL_URL = "https://graphql.anilist.co"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideApolloClient(
        authorizationInterceptor: AuthorizationInterceptor,
    ): ApolloClient {
        val okHttpClient =
            OkHttpClient
                .Builder()
                .addInterceptor(authorizationInterceptor)
                .build()

        return ApolloClient
            .Builder()
            .serverUrl(ANILIST_GRAPHQL_URL)
            .okHttpClient(okHttpClient)
            .build()
    }

    class AuthorizationInterceptor
        @javax.inject.Inject
        constructor(
            private val authTokenProvider: com.example.core.domain.auth.AuthTokenProvider,
        ) : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val token = authTokenProvider.getToken()
                val requestBuilder = chain.request().newBuilder()

                if (!token.isNullOrBlank()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }

                val request = requestBuilder.build()
                return chain.proceed(request)
            }
        }
}

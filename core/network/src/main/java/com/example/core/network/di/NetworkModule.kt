package com.example.core.network.di

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.cache.normalized.api.CacheKey
import com.apollographql.apollo.cache.normalized.api.CacheKeyGenerator
import com.apollographql.apollo.cache.normalized.api.CacheKeyGeneratorContext
import com.apollographql.apollo.cache.normalized.api.MemoryCacheFactory
import com.apollographql.apollo.cache.normalized.normalizedCache
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
        val cacheFactory = MemoryCacheFactory(maxSizeBytes = 10 * 1024 * 1024)

        val cacheKeyGenerator =
            object : CacheKeyGenerator {
                override fun cacheKeyForObject(
                    obj: Map<String, Any?>,
                    context: CacheKeyGeneratorContext,
                ): CacheKey? {
                    val id = obj["id"] ?: obj["_id"] ?: return null
                    val typename = obj["__typename"] as? String ?: return null
                    return CacheKey(typename, id.toString())
                }
            }

        val okHttpClient =
            OkHttpClient
                .Builder()
                .addInterceptor(authorizationInterceptor)
                .build()

        return ApolloClient
            .Builder()
            .serverUrl(ANILIST_GRAPHQL_URL)
            .okHttpClient(okHttpClient)
            .normalizedCache(
                normalizedCacheFactory = cacheFactory,
                cacheKeyGenerator = cacheKeyGenerator,
            ).build()
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

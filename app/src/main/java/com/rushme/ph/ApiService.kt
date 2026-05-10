    package com.rushme.ph

    import retrofit2.Retrofit
    import retrofit2.converter.gson.GsonConverterFactory
    import retrofit2.http.GET

    interface ApiService {
        // Kinukuha nito ang listahan ng products mula sa /products endpoint ng iyong Worker
        @GET("products")
        suspend fun getProducts(): List<Product>

        companion object {
            private var instance: ApiService? = null

            fun getInstance(): ApiService {
                if (instance == null) {
                    instance = Retrofit.Builder()
                        // Siguraduhing tama ang URL ng iyong Cloudflare Worker
                        .baseUrl("https://sparkling-sky-efd2.bermudez-arohn.workers.dev/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(ApiService::class.java)
                }
                return instance!!
            }
        }
    }
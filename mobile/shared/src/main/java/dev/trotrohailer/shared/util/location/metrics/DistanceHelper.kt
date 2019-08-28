package dev.trotrohailer.shared.util.location.metrics


import android.os.Environment
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dev.trotrohailer.shared.BuildConfig
import kotlinx.coroutines.Deferred
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

data class MapResult(
    @SerializedName("routes")
    @Expose
    val routes: MutableList<Route>
)

data class Route(
    @SerializedName("legs")
    @Expose
    val legs: MutableList<Leg>,
    @SerializedName("overview_polyline")
    @Expose
    val overviewPolyline: OverviewPolyline
)

data class Leg(
    @SerializedName("distance")
    @Expose
    val distance: Distance,
    @SerializedName("duration")
    @Expose
    val duration: Duration
)

data class Distance(
    @SerializedName("text")
    @Expose
    val text: String,
    @SerializedName("value")
    @Expose
    val value: Int
)

data class OverviewPolyline(
    @SerializedName("points")
    @Expose
    val points: String
)

data class Duration(
    @SerializedName("text")
    @Expose
    val text: String,
    @SerializedName("value")
    @Expose
    val value: Int
)

interface MapApi {

    companion object {
        const val BASE_URL = "https://maps.googleapis.com/maps/"
    }

    @GET("api/directions/json?key=AIzaSyA0l4h00ckGmNPsBOLQpz3zr7Xk7Q2xC4E")
    fun getDistanceAsync(
        @Query("units") units: String = "metric",
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String = "walking"
    ): Deferred<MapResult>

    @GET("api/directions/json?key=AIzaSyA0l4h00ckGmNPsBOLQpz3zr7Xk7Q2xC4E")
    fun getDistanceForDrivingAsync(
        @Query("units") units: String = "metric",
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String = "driving"
    ): Deferred<MapResult>
}

class MapService {

    companion object {
        @Volatile
        private var instance: MapService? = null

        fun getInstance(): MapService = instance ?: synchronized(this) {
            instance ?: MapService().also { instance = it }
        }
    }

    private val file by lazy {
        File("${Environment.getDataDirectory()}/${BuildConfig.LIBRARY_PACKAGE_NAME}/distance_network_cache").apply {
            if (!mkdirs()) mkdirs()
        }
    }
    private val cache by lazy { Cache(file, 60 * 1024 * 1024) }
    private val interceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    private val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .cache(cache)
            .build()
    }

    fun getService(): MapApi = Retrofit.Builder()
        .baseUrl(MapApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(client)
        .build()
        .create(MapApi::class.java)

    fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>(0)
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }

        return poly
    }

}

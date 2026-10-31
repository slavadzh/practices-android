package com.example.practices_android.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.practices_android.data.local.db.AppDatabase
import com.example.practices_android.data.remote.GhibliApi
import com.example.practices_android.data.repository.FavoritesRepositoryImpl
import com.example.practices_android.data.repository.FilmsRepositoryImpl
import com.example.practices_android.data.repository.FiltersRepositoryImpl
import com.example.practices_android.domain.repository.FavoritesRepository
import com.example.practices_android.domain.repository.FilmsRepository
import com.example.practices_android.domain.repository.FiltersRepository
import com.example.practices_android.domain.usecase.GetDirectorsUseCase
import com.example.practices_android.domain.usecase.GetFilmDetailsUseCase
import com.example.practices_android.domain.usecase.GetFilmsUseCase
import com.example.practices_android.domain.usecase.ObserveFavoritesUseCase
import com.example.practices_android.domain.usecase.ObserveFiltersUseCase
import com.example.practices_android.domain.usecase.ObserveIsFavoriteUseCase
import com.example.practices_android.domain.usecase.SaveFiltersUseCase
import com.example.practices_android.domain.usecase.ToggleFavoriteUseCase
import com.example.practices_android.ui.favorites.FavoritesViewModel
import com.example.practices_android.ui.films.details.FilmDetailsViewModel
import com.example.practices_android.ui.films.filters.FiltersBadgeCache
import com.example.practices_android.ui.films.filters.FiltersViewModel
import com.example.practices_android.ui.films.list.FilmsListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

private const val TIMEOUT_SECONDS = 15L

private val android.content.Context.filtersDataStore: DataStore<Preferences> by preferencesDataStore("filters")

val networkModule = module {
    single { Json { ignoreUnknownKeys = true } }
    single {
        OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            .addInterceptor(ChuckerInterceptor.Builder(androidContext()).build())
            .build()
    }
    single<GhibliApi> {
        Retrofit.Builder()
            .baseUrl(GhibliApi.BASE_URL)
            .client(get())
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .build()
            .create(GhibliApi::class.java)
    }
}

val storageModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, AppDatabase.NAME).build()
    }
    single { get<AppDatabase>().favoritesDao() }
    single { androidContext().filtersDataStore }
}

val dataModule = module {
    single<FilmsRepository> { FilmsRepositoryImpl(get(), get(), Dispatchers.IO) }
    singleOf(::FiltersRepositoryImpl) bind FiltersRepository::class
    singleOf(::FavoritesRepositoryImpl) bind FavoritesRepository::class
}

val domainModule = module {
    factoryOf(::GetFilmsUseCase)
    factoryOf(::GetFilmDetailsUseCase)
    factoryOf(::GetDirectorsUseCase)
    factoryOf(::ObserveFiltersUseCase)
    factoryOf(::SaveFiltersUseCase)
    factoryOf(::ObserveFavoritesUseCase)
    factoryOf(::ObserveIsFavoriteUseCase)
    factoryOf(::ToggleFavoriteUseCase)
}

val presentationModule = module {
    singleOf(::FiltersBadgeCache)
    viewModelOf(::FilmsListViewModel)
    viewModelOf(::FilmDetailsViewModel)
    viewModelOf(::FiltersViewModel)
    viewModelOf(::FavoritesViewModel)
}

val appModules = listOf(networkModule, storageModule, dataModule, domainModule, presentationModule)

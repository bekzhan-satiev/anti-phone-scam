package kg.digitalshield

import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kg.digitalshield.api.AnalyzeApi
import kg.digitalshield.auth.AuthInterceptor
import kg.digitalshield.auth.TokenAuthenticator
import kg.digitalshield.auth.TokenRepository
import kg.digitalshield.db.CallDatabase
import kg.digitalshield.api.AuthApi
import kg.digitalshield.api.CheckApi
import kg.digitalshield.api.ResetPasswordApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCallDatabase(@ApplicationContext context: Context): CallDatabase {
        return Room.databaseBuilder(
            context,
            CallDatabase::class.java,
            CallDatabase.NAME
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    fun provideCallDao(callDatabase: CallDatabase) = callDatabase.getCallDao()

    @Provides
    @Singleton
    fun provideTokenRepository(@ApplicationContext context: Context): TokenRepository {
        return TokenRepository(context)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenRepository: TokenRepository): AuthInterceptor {
        return AuthInterceptor(tokenRepository)
    }

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        tokenRepository: TokenRepository,
        authApi: AuthApi
    ): TokenAuthenticator {
        return TokenAuthenticator(tokenRepository, authApi)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://176.126.164.165:5000/")
//            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.86.116:8080/test/auth/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(@Named("auth") retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    //TODO: rename from service to api
    @Provides
    @Singleton
    fun providePhoneNumberCheckService(retrofit: Retrofit): CheckApi {
        return retrofit.create(CheckApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAnalyzeApi(retrofit: Retrofit): AnalyzeApi {
        return retrofit.create(AnalyzeApi::class.java)
    }

    @Provides
    @Singleton
    fun provideResetPassword(retrofit: Retrofit) : ResetPasswordApi {
        return retrofit.create(ResetPasswordApi::class.java)
    }

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

}

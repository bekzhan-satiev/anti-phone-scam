package kg.digitalshield.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kg.digitalshield.db.CallDatabase
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

}
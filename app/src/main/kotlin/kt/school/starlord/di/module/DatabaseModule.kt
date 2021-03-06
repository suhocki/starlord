package kt.school.starlord.di.module

import androidx.room.Room
import kt.school.starlord.BuildConfig
import kt.school.starlord.domain.repository.CategoriesCacheRepository
import kt.school.starlord.domain.repository.product.ProductsCacheRepository
import kt.school.starlord.domain.repository.SubcategoriesRepository
import kt.school.starlord.model.data.room.AppDatabase
import kt.school.starlord.model.data.room.DaoManager
import kt.school.starlord.model.repository.database.DatabaseRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.binds
import org.koin.dsl.module

/**
 * Provides instructions on how to maintain database dependencies.
 * Depends on MapperModule.
 */
val databaseModule = module {

    factory {
        val room = Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            BuildConfig.DATABASE_FILE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()

        DaoManager(
            room.categoryDao(),
            room.subcategoryDao(),
            room.productDao()
        )
    }

    single { DatabaseRepository(get(), get()) } binds arrayOf(
        SubcategoriesRepository::class,
        CategoriesCacheRepository::class,
        ProductsCacheRepository::class
    )
}

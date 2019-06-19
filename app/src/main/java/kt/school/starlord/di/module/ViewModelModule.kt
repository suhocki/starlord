package kt.school.starlord.di.module

import kt.school.starlord.ui.categories.CategoriesViewModel
import kt.school.starlord.ui.subcategories.SubcategoriesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Depends on NetworkModule and DatabaseModule
 */
val viewModelModule = module {

    viewModel { CategoriesViewModel(get(), get()) }

    viewModel { SubcategoriesViewModel(get()) }
}
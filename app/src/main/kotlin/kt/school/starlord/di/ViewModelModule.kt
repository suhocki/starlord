package kt.school.starlord.di

import kt.school.starlord.model.system.viewmodel.ErrorViewModelFeature
import kt.school.starlord.model.system.viewmodel.ProgressViewModelFeature
import kt.school.starlord.ui.categories.CategoriesViewModel
import kt.school.starlord.ui.products.ProductsViewModel
import kt.school.starlord.ui.subcategories.SubcategoriesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Depends on NetworkModule and DatabaseModule.
 */
val viewModelModule = module {

    single { ProgressViewModelFeature() }

    single { ErrorViewModelFeature() }

    viewModel { CategoriesViewModel(get(), get(), get(), get(), get()) }

    viewModel { (categoryName: String) -> SubcategoriesViewModel(get(), categoryName) }

    viewModel { (subcategoryName: String) -> ProductsViewModel(get(), get(), get(), subcategoryName) }
}

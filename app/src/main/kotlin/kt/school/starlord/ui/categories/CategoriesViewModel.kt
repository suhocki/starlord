package kt.school.starlord.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import kotlinx.coroutines.launch
import kt.school.starlord.domain.repository.CategoriesRepository
import kt.school.starlord.domain.repository.CategoriesWithSubcategoriesRepository
import kt.school.starlord.domain.repository.SubcategoriesRepository
import kt.school.starlord.entity.CategoriesWithSubcategories
import kt.school.starlord.entity.Category

/**
 * Contains logic with fetching categories asynchronously.
 */
class CategoriesViewModel(
    private val networkRepository: CategoriesWithSubcategoriesRepository,
    private val categoriesRepository: CategoriesRepository,
    private val subcategoriesRepository: SubcategoriesRepository
) : ViewModel() {

    private val categories = MutableLiveData<List<Category>>()
    private val error = LiveEvent<Throwable>()
    private val progress = MutableLiveData<Boolean>()

    init {
        categoriesRepository.getCategories().observeForever(categories::setValue)
        refreshData()
    }

    /**
     * Use for observing categories.
     */
    fun getCategories(): LiveData<List<Category>> = categories

    /**
     * LiveData for observing errors.
     */
    fun getError(): LiveData<Throwable> = error

    /**
     * LiveData for observing progress state.
     */
    fun getProgress(): LiveData<Boolean> = progress

    private fun refreshData() {
        viewModelScope.launch {
            progress.value = true

            runCatching { networkRepository.getCategoriesWithSubcategories() }
                .fold({ updateDatabase(it) }, error::setValue)

            progress.value = false
        }
    }

    private suspend fun updateDatabase(categoriesWithSubcategories: CategoriesWithSubcategories) {
        categoriesRepository.updateCategories(categoriesWithSubcategories.categories)
        subcategoriesRepository.updateSubcategories(categoriesWithSubcategories.subcategories)
    }
}

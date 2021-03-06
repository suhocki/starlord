package kt.school.starlord.ui.subcategories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import kt.school.starlord.domain.data.mapper.Mapper
import kt.school.starlord.domain.repository.SubcategoriesRepository
import kt.school.starlord.ui.subcategories.entity.UiSubcategory

/**
 * Contains logic with fetching subcategories asynchronously.
 */
class SubcategoriesViewModel(
    databaseRepository: SubcategoriesRepository,
    mapper: Mapper,
    categoryName: String
) : ViewModel() {
    private val subcategories = MutableLiveData<List<UiSubcategory>>()

    init {
        databaseRepository.getSubcategories(categoryName)
            .map { data -> data.map { mapper.map<UiSubcategory>(it) } }
            .observeForever(subcategories::setValue)
    }

    /**
     * Use for observing subcategories.
     */
    fun getSubcategories(): LiveData<List<UiSubcategory>> = subcategories
}

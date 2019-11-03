package kt.school.starlord.domain.repository

import kt.school.starlord.domain.entity.category.Category
import kt.school.starlord.domain.entity.subcategory.Subcategory

/**
 * Defines access methods for objects representing pairs of categories with subcategories.
 */
interface CategoriesWithSubcategoriesRepository {

    /**
     * Loads categories with subcategories.
     *
     * @return categories with appropriate subcategories.
     */
    suspend fun getCategoriesWithSubcategories(): Map<Category, List<Subcategory>>
}

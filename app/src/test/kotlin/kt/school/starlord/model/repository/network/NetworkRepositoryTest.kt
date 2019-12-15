package kt.school.starlord.model.repository.network

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kt.school.starlord.domain.mapper.Mapper
import kt.school.starlord.domain.entity.category.Category
import kt.school.starlord.domain.entity.product.Product
import kt.school.starlord.domain.entity.subcategory.Subcategory
import kt.school.starlord.model.data.jsoup.JsoupParser
import kt.school.starlord.ui.TestCoroutineRule
import kt.school.starlord.ui.createConverter
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers

class NetworkRepositoryTest {
    @get:Rule
    internal val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    internal val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val jsoupParser: JsoupParser = mockk()

    @Test
    fun getCategoriesWithSubcategories() = testCoroutineRule.runBlockingTest {
        // Given
        val category: Category = mockk(relaxed = true)
        val subcategory: Subcategory = mockk(relaxUnitFun = true)
        val categoryElement: Element = mockk()
        val subcategoryElement: Element = mockk()

        val mapOfElements = mapOf(categoryElement to Elements(subcategoryElement))
        val expected = mapOf(category to listOf(subcategory))
        val elementToCategoryConverter = createConverter(categoryElement to category)
        val elementToSubcategoryConverter = createConverter(subcategoryElement to subcategory)
        val mapper = Mapper(setOf(elementToCategoryConverter, elementToSubcategoryConverter))
        val networkRepository = NetworkRepository(jsoupParser, mapper)

        coEvery { jsoupParser.parseCategories() } coAnswers { mapOfElements }

        // When
        val actual = networkRepository.getCategoriesWithSubcategories()

        // Then
        assert(actual == expected)
    }

    @Test
    fun getCategoriesWithSubcategories_fillCategoryNameInSubcategories() = testCoroutineRule.runBlockingTest {
        // Given
        val categoryName = "name"
        val category = Category(categoryName)
        val subcategory: Subcategory = mockk(relaxUnitFun = true)
        val categoryElement: Element = mockk()
        val subcategoryElement: Element = mockk()

        val mapOfElements = mapOf(categoryElement to Elements(subcategoryElement))
        val elementToCategoryConverter = createConverter(categoryElement to category)
        val elementToSubcategoryConverter = createConverter(subcategoryElement to subcategory)
        val mapper = Mapper(setOf(elementToCategoryConverter, elementToSubcategoryConverter))
        val networkRepository = NetworkRepository(jsoupParser, mapper)

        coEvery { jsoupParser.parseCategories() } coAnswers { mapOfElements }

        // When
        networkRepository.getCategoriesWithSubcategories()

        // Then
        verify { subcategory.categoryName = categoryName }
    }

    @Test
    fun getProducts() = testCoroutineRule.runBlockingTest {
        // Given
        val element: Element = mockk()
        val expected: Product = mockk()
        val forumId = ArgumentMatchers.anyLong()
        val mapper = Mapper(setOf(createConverter(element to expected)))
        val networkRepository = NetworkRepository(jsoupParser, mapper)

        coEvery { jsoupParser.getProductElements(forumId) } coAnswers { Elements(element) }

        // When
        val actual = networkRepository.downloadProductsPage(forumId).first()

        // Then
        assert(actual == expected)
    }
}

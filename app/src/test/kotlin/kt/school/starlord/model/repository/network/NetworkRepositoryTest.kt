package kt.school.starlord.model.repository.network

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kt.school.starlord.BuildConfig
import kt.school.starlord.TestContextProvider
import kt.school.starlord.di.BaseConverter
import kt.school.starlord.domain.data.mapper.Converter
import kt.school.starlord.entity.CategoriesWithSubcategories
import kt.school.starlord.model.data.mapper.Mapper
import kt.school.starlord.ui.TestCoroutineRule
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.Rule
import org.junit.Test
import java.net.URL

class NetworkRepositoryTest {
    @get:Rule
    internal val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    internal val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val categoriesWithSubcategories: CategoriesWithSubcategories = mockk()
    private val converters: Set<Converter<*, *>> = setOf(
        object : BaseConverter<Document, CategoriesWithSubcategories>(
            Document::class.java, CategoriesWithSubcategories::class.java
        ) {
            override fun convert(value: Document) = categoriesWithSubcategories
        }
    )
    private val mapper = Mapper(converters)
    private val networkRepository = NetworkRepository(mapper, TestContextProvider())

    @Test
    fun `get categories with subcategories`() = testCoroutineRule.runBlockingTest {
        // Given
        val document: Document = mockk()

        mockkStatic(Jsoup::class)

        every { Jsoup.parse(URL(BuildConfig.BARAHOLKA_ONLINER_URL), BuildConfig.NETWORK_REQUEST_TIMEOUT_MILLIS) }
            .answers { document }

        // When
        val answer = networkRepository.getCategoriesWithSubcategories()

        // Then
        assert(answer == categoriesWithSubcategories)
    }
}

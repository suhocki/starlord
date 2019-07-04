package kt.school.starlord.ui.categories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kt.school.starlord.entity.Category
import kt.school.starlord.entity.Subcategory
import kt.school.starlord.model.network.NetworkRepository
import kt.school.starlord.model.room.RoomRepository
import kt.school.starlord.ui.TestCoroutineRule
import kt.school.starlord.ui.observeForTesting
import org.junit.Rule
import org.junit.Test

class CategoriesViewModelTest {

    @get:Rule
    internal val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    internal val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val networkRepository: NetworkRepository = mockk()
    private val roomRepository: RoomRepository = mockk(relaxed = true)

    @Test
    fun `load categories with subcategories from network`() = testCoroutineRule.runBlockingTest {
        // Given
        val categoriesWithSubcategories = mapOf(
            Category("categoryName3") to listOf(
                Subcategory("subcategoryName1", "categoryName3", 5, "link1"),
                Subcategory("subcategoryName2", "categoryName3", 2, "link2")
            ),
            Category("categoryName4") to listOf(
                Subcategory("subcategoryName3", "categoryName4", 3, "link3")
            )
        )
        coEvery { networkRepository.getCategoriesWithSubcategories() }.coAnswers { categoriesWithSubcategories }

        // When
        CategoriesViewModel(networkRepository, roomRepository)

        // Then
        coVerify(exactly = 1) {
            roomRepository.updateSubcategories(categoriesWithSubcategories.values.flatten())
            roomRepository.updateCategories(categoriesWithSubcategories.keys.toList())
        }
    }

    @Test
    fun `load categories from database`() = testCoroutineRule.runBlockingTest {
        // Given
        val categories = listOf(Category("categoryName1"), Category("categoryName2"))
        val categoriesLiveData = MutableLiveData(categories)
        every { roomRepository.getCategories() }.answers { categoriesLiveData }

        // When
        val viewModel = CategoriesViewModel(networkRepository, roomRepository)

        // Then
        viewModel.getCategories().observeForTesting {
            assert(viewModel.getCategories().value == categories)
        }
    }
}

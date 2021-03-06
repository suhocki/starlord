package kt.school.starlord.ui.categories

import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import kotlinx.android.synthetic.main.fragment_categories.*
import kt.school.starlord.domain.system.view.ErrorSnackbar
import kt.school.starlord.domain.system.view.ProgressSnackbar
import kt.school.starlord.domain.entity.category.Category
import kt.school.starlord.ui.global.AppRecyclerAdapter
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.test.AutoCloseKoinTest
import org.koin.test.mock.declare
import org.mockito.ArgumentMatchers.anyString

@RunWith(AndroidJUnit4::class)
class CategoriesFragmentTest : AutoCloseKoinTest() {

    private val viewModel: CategoriesViewModel = mockk(relaxed = true)
    private val progressSnackbar: ProgressSnackbar = mockk(relaxUnitFun = true)
    private val errorSnackbar: ErrorSnackbar = mockk(relaxUnitFun = true)
    private val scenario by lazy {
        FragmentScenario.launchInContainer(CategoriesFragment::class.java)
    }

    @Before
    fun setUp() {
        declare {
            viewModel { viewModel }
            single { progressSnackbar }
            single { errorSnackbar }
        }
    }

    // region Testing snackbars
    @Test
    fun `show snackbar with updating`() {
        // Given
        val progress = MutableLiveData(false)

        every { viewModel.getProgress() } returns progress

        scenario.onFragment {
            // When
            progress.value = true

            // Then
            verify { progressSnackbar.setVisibility(false) }
        }
    }

    @Test
    fun `hide snackbar with updating`() {
        // Given
        val progress = MutableLiveData<Boolean>()

        every { viewModel.getProgress() } returns progress

        scenario.onFragment {
            // When
            progress.value = false

            // Then
            verify { progressSnackbar.setVisibility(false) }
        }
    }

    @Test
    fun `hide snackbar with updating when fragment is gone`() {
        // Given
        val progressLiveData = MutableLiveData<Boolean>()

        every { viewModel.getProgress() } returns progressLiveData

        scenario.onFragment {
            // When
            progressLiveData.value = true

            scenario.moveToState(Lifecycle.State.DESTROYED)

            // Then
            verify { progressSnackbar.setVisibility(false) }
        }
    }

    @Test
    fun `show snackbar with error`() {
        // Given
        val error = Throwable(anyString())
        val errorLiveData = MutableLiveData<Throwable>()

        every { viewModel.getError() } returns errorLiveData

        scenario.onFragment {
            // When
            errorLiveData.value = error

            // Then
            verify { errorSnackbar.show(error) }
        }
    }

    @Test
    fun `hide snackbar with error when fragment is gone`() {
        // Given
        val error = Throwable(anyString())
        val errorLiveData = MutableLiveData<Throwable>()

        every { viewModel.getError() } returns errorLiveData

        scenario.onFragment {
            // When
            errorLiveData.value = error

            scenario.moveToState(Lifecycle.State.DESTROYED)

            // Then
            verify { errorSnackbar.dismiss() }
        }
    }
    // endregion

    @Test
    fun `show categories`() {
        // Given
        mockkConstructor(AppRecyclerAdapter::class)
        val categories = listOf(
            Category(anyString()),
            Category(anyString())
        )
        every { viewModel.getCategories() } returns MutableLiveData(categories)
        scenario.moveToState(Lifecycle.State.CREATED)

        // When
        scenario.moveToState(Lifecycle.State.RESUMED)

        // Then
        scenario.onFragment {
            verify { anyConstructed<AppRecyclerAdapter>().setData(categories) }
        }
    }

    @Test
    fun `navigate to subcategories`() {
        // Given
        val categoryName = "test category name"
        val categories = MutableLiveData(listOf(Category(categoryName)))
        val navController: NavController = mockk(relaxUnitFun = true)
        mockkStatic(NavHostFragment::class)
        every { viewModel.getCategories() } returns categories
        every { NavHostFragment.findNavController(any()) } returns navController

        // When
        scenario.onFragment {
            onView(withText(categoryName)).perform(ViewActions.click())
        }

        // Then
        scenario.onFragment {
            val direction = slot<NavDirections>()

            verify { navController.navigate(capture(direction)) }

            val arguments = direction.captured.arguments
            val keys = arguments.keySet()
            assert(keys.any { arguments.getString(it) == categoryName })
        }
    }

    @Test
    fun `clear adapter in recycler in onDestroy`() {
        // Given
        scenario.moveToState(Lifecycle.State.RESUMED)

        scenario.onFragment {
            val recyclerView = it.recyclerView

            // When
            scenario.moveToState(Lifecycle.State.DESTROYED)

            // Then
            assert(recyclerView.adapter == null)
        }
    }
}

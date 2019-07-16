package kt.school.starlord.ui.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_categories.*
import kotlinx.android.synthetic.main.fragment_products.*
import kotlinx.android.synthetic.main.fragment_products.recyclerView
import kt.school.starlord.R
import kt.school.starlord.domain.system.message.SystemMessageReceiver
import kt.school.starlord.ui.global.AppRecyclerAdapter
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * Contains a recycler that is filled by products.
 * Clicking on an item takes you to a product details fragment.
 */
class ProductsFragment : Fragment() {

    private val viewModel: ProductsViewModel by viewModel(parameters = {
        val safeArgs = ProductsFragmentArgs.fromBundle(requireArguments())
        parametersOf(safeArgs.subcategoryName)
    })
    private val systemMessageReceiver: SystemMessageReceiver by inject()

    private val adapter by lazy {
        AppRecyclerAdapter(
            ProductAdapterDelegate {}
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_products, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter

        viewModel.getSubcategories().observe(viewLifecycleOwner, Observer(adapter::setData))
        viewModel.getProgress().observe(viewLifecycleOwner, Observer(systemMessageReceiver::showProgress))
        viewModel.getError().observe(viewLifecycleOwner, Observer(systemMessageReceiver::showError))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView.adapter = null
    }
}

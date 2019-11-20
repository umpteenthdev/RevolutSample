package com.umpteenthdev.revolutsample.rates.outer.android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.umpteenthdev.revolutsample.core.entity.requireAllBranches
import com.umpteenthdev.revolutsample.rates.R
import com.umpteenthdev.revolutsample.rates.adapters.ui.RateUiModel
import com.umpteenthdev.revolutsample.rates.adapters.ui.RatesViewModel
import com.umpteenthdev.revolutsample.rates.adapters.ui.UiState
import kotlinx.android.synthetic.main.fragment_rates.*
import kotlinx.android.synthetic.main.layout_something_went_wrong.*
import javax.inject.Inject

class RatesFragment : Fragment() {

    @Inject lateinit var adapterFactory: RatesAdapterFactory
    private lateinit var viewModel: RatesViewModel
    private val ratesListCallback: RatesAdapter.Callback by lazy { initRatesListCallback() }
    private val adapter: RatesAdapter by lazy { adapterFactory.create(ratesListCallback) }
    private val layoutManager: LinearLayoutManager by lazy { LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return RatesViewModel() as T
                }
            }
        ).get(RatesViewModel::class.java)

        viewModel.component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_rates, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.uiStateBus.observe(viewLifecycleOwner, Observer { applyUiState(it) })
        initRecycler()
        retryLoadingButton.setOnClickListener { viewModel.onRetryLoading() }
    }

    override fun onStart() {
        super.onStart()
        viewModel.onConnect()
    }

    override fun onStop() {
        viewModel.onDisconnect()
        super.onStop()
    }

    private fun initRatesListCallback(): RatesAdapter.Callback = object : RatesAdapter.Callback {

        override fun onItemClick(item: RateUiModel) {
            viewModel.onItemClicked(item)
        }

        override fun onAmountChanged(item: RateUiModel, amount: Double?) {
            viewModel.onAmountChanged(item, amount)
        }
    }

    private fun initRecycler() {
        ratesRecyclerView.layoutManager = layoutManager
        ratesRecyclerView.adapter = adapter
        ratesRecyclerView.setHasFixedSize(true)
    }

    private fun applyUiState(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> setLoadingState()
            is UiState.Content -> setContentState(uiState)
            is UiState.ConnectionError -> setErrorState()
        }.requireAllBranches()
    }

    private fun setLoadingState() {
        ratesRecyclerView?.isVisible = false
        ratesProgressIndicator?.isVisible = true
        errorStateContainer?.isVisible = false
    }

    private fun setContentState(uiState: UiState.Content) {
        adapter.submitList(uiState.items)

        if (uiState.moveToTop) {
            layoutManager.scrollToPosition(0)
        }

        if (ratesRecyclerView?.isVisible == false) {
            ratesRecyclerView?.isVisible = true
            ratesProgressIndicator?.isVisible = false
            errorStateContainer?.isVisible = false
        }
    }

    private fun setErrorState() {
        ratesRecyclerView?.isVisible = false
        ratesProgressIndicator?.isVisible = false
        errorStateContainer?.isVisible = true
    }

    companion object {
        fun newInstance() = RatesFragment()
    }
}

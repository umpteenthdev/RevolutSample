package com.umpteenthdev.revolutsample.rates.adapters.ui

sealed class UiState {

    object Loading : UiState()

    object ConnectionError : UiState()

    data class Content(
        val items: List<RateUiModel>,
        val moveToTop: Boolean
    ) : UiState()
}


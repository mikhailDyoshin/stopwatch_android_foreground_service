package com.example.stopwatchproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class StopwatchViewModel(stopwatchStateFlowRepository: StopwatchStateFlowRepository) : ViewModel() {

    val state = stopwatchStateFlowRepository.stopwatchState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        0L
    )

}
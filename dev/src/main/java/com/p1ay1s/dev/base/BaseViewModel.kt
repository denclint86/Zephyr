package com.p1ay1s.dev.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * MVI 架构的 view model 基类
 */
abstract class BaseViewModel<Intent, State, Effect> : ViewModel() {

    private val _stateFlow = MutableStateFlow(initUiState())
    val uiStateFlow: StateFlow<State> = _stateFlow.asStateFlow()

    private val _effectFlow = MutableSharedFlow<Effect>()
    val uiEffectFlow: SharedFlow<Effect> by lazy { _effectFlow.asSharedFlow() }

    private val channel = Channel<Intent>(Channel.UNLIMITED)

    init {
        viewModelScope.launch {
            channel.consumeAsFlow().collect { handleIntent(it) }
        }
    }

    protected fun updateState(update: State.() -> State) =
        _stateFlow.update(update)

    fun sendIntent(intent: Intent) =
        viewModelScope.launch {
            channel.send(intent)
        }

    fun observeState(observe: Flow<State>.() -> Unit) = observe(uiStateFlow)

    protected fun sendEffect(builder: suspend () -> Effect?) = viewModelScope.launch {
        builder()?.let {
            _effectFlow.emit(it)
        }
    }

    protected suspend fun sendEffect(effect: Effect) = _effectFlow.emit(effect)

    protected abstract fun initUiState(): State

    protected abstract fun handleIntent(intent: Intent)
}
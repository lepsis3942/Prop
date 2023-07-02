package com.cjapps.prop.ui.detail

import androidx.lifecycle.ViewModel
import com.cjapps.prop.IDispatcherProvider
import com.cjapps.prop.data.IInvestmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InvestmentDetailViewModel @Inject constructor(
    private val dispatcherProvider: IDispatcherProvider,
    private val investmentRepository: IInvestmentRepository,
) : ViewModel() {

}
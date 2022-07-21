package com.example.homework_recyclerview.presentation.converter

import android.accounts.NetworkErrorException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.convertor.R
import com.example.homework_recyclerview.data.CurrencyRepository
import com.example.homework_recyclerview.domain.models.CurrencyRates
import com.example.homework_recyclerview.domain.repository.Currency
import com.example.homework_recyclerview.utils.Constants
import kotlinx.coroutines.*

class MainViewModel(
    private val repository: CurrencyRepository,
    private val ioDispatchers: CoroutineDispatcher = Dispatchers.IO,
    private val uiDispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private var allRates: Map<String, Double> = mapOf()
    var listOfRates: MutableLiveData<List<String>> = MutableLiveData(emptyList())

    private var data = mutableListOf<Currency>()
    private val _currencyList = MutableLiveData<List<Currency>>(data)
    val currencyList: LiveData<List<Currency>> = _currencyList
    private val _balance = MutableLiveData(0)
    val balance: LiveData<Int> = _balance
    var networkErrorException = MutableLiveData(false)


    fun addNewRate(key: String) {
        try {
            viewModelScope.launch(ioDispatchers) {
                val rate = async { loadSpecificRate(key) }
                val newCurrency =
                    Currency(Constants.id++, rate.await(), key, R.drawable.image_1_3, rate.await())
                data.add(newCurrency)
                _currencyList.postValue(data)
            }
        } catch (e: NetworkErrorException) {
            networkErrorException.postValue(true)
        }
    }

    fun setBalance(value: Int) {
        _balance.value = value
    }

    fun addCurrency(newItem: Currency) {
        data.add(newItem)
        val newData = arrayListOf<Currency>()
        newData.addAll(data)
        _currencyList.value = newData
    }

    fun deleteCurrency(currency: Currency) {
        data.remove(currency)
        val newData = arrayListOf<Currency>()
        newData.addAll(data)
        _currencyList.value = newData
    }

    fun sortByID() {
        data.sortBy { it.id }
        val newData = arrayListOf<Currency>()
        newData.addAll(data)
        _currencyList.value = data
    }

    fun sortByName() {
        data.sortBy { it.type }
        val newData = arrayListOf<Currency>()
        newData.addAll(data)
        _currencyList.value = newData
    }

    fun sortByPrice() {
        data.sortBy { it.text }
        val newData = arrayListOf<Currency>()
        newData.addAll(data)
        _currencyList.value = newData
    }

    fun moveItem(from: Int, to: Int) {
        val fromEmoji = data[from]
        data.removeAt(from)
        val newData = arrayListOf<Currency>()
        newData.addAll(data)

        if (to < from) {
            newData.add(to, fromEmoji)
        } else {
            newData.add(to - 1, fromEmoji)
        }
        _currencyList.value = newData
    }


    fun loadCurrencyRates() {
        try {
            viewModelScope.launch(ioDispatchers) {
                val results: Deferred<CurrencyRates> = async { repository.getCurrencyRates() }
                allRates = results.await().rates
                listOfRates.postValue(allRates.keys.toList())
            }
        } catch (e: NetworkErrorException) {
            networkErrorException.postValue(true)
        }
    }

    suspend fun loadSpecificRate(currentName: String): Double {
        return repository.getCurrencyRates().rates[currentName]!!
    }
}

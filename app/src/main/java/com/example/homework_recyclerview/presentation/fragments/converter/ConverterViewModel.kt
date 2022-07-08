package com.example.homework_recyclerview.presentation.fragments.converter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.convertor.R
import com.example.homework_recyclerview.data.CurrencyRepository
import com.example.homework_recyclerview.domain.repository.Currency
import com.example.homework_recyclerview.utils.Constants
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: CurrencyRepository
) : ViewModel() {

    private var rates: Map<String, Double> = mapOf()
    private var currentRate: Double = 0.0
    var listOfRates: MutableLiveData<List<String>> = MutableLiveData(emptyList())

    private var data = mutableListOf<Currency>()
    private val _currencyList = MutableLiveData<List<Currency>>(data)
    val currencyList: LiveData<List<Currency>> = _currencyList
    private val _balance = MutableLiveData(0)
    val balance: LiveData<Int> = _balance


    fun addNewRate(key: String) {
        val newCurrency = Currency(Constants.id++, rates[key]!!, key, R.drawable.image_1_3, rates[key]!!)
        data.add(newCurrency)
        _currencyList.value = data
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
        viewModelScope.launch {
            val results = repository.getCurrencyRates()
            rates = results.rates
            listOfRates.value = rates.keys.toList()
        }
    }

//    fun loadSpecificRate(currentName: String) {
//        viewModelScope.launch {
//            currentRate = repository.getCurrencyRates().rates[currentName]!!
//        }
//    }
}

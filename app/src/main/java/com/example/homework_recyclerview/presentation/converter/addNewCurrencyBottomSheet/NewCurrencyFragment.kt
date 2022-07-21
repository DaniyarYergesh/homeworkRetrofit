package com.example.homework_recyclerview.presentation.converter.addNewCurrencyBottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.convertor.databinding.DialogBottomSheetAddItemBinding
import com.example.homework_recyclerview.presentation.converter.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NewCurrencyFragment : BottomSheetDialogFragment() {

    private val viewModel: MainViewModel by sharedViewModel()
    private var _binding: DialogBottomSheetAddItemBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogBottomSheetAddItemBinding.inflate(inflater, container, false)
        viewModel.loadCurrencyRates()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = NewCurrencyAdapter(::onItemClick)
        binding.rvNames.adapter = adapter

        viewModel.listOfRates.observe(viewLifecycleOwner) {
            adapter.data = it
            adapter.notifyDataSetChanged()
        }
    }

    private fun onItemClick(name: String) {
        viewModel.addNewRate(name)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "NewCurrencyFragment"
    }
}
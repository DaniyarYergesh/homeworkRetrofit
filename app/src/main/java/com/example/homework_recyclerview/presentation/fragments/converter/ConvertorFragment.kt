package com.example.homework_recyclerview.presentation.fragments.converter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import com.example.convertor.R
import com.example.convertor.databinding.LayoutFragmentConvertorBinding
import com.example.homework_recyclerview.domain.repository.Currency
import com.example.homework_recyclerview.presentation.fragments.converter.addNewCurrencyBottomSheet.NewCurrencyFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ConvertorFragment : Fragment(), DeleteDialogCallback {

    private val viewModel: MainViewModel by sharedViewModel()
    private var _binding: LayoutFragmentConvertorBinding? = null
    private val binding get() = _binding!!

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var adapter: ConvertorAdapter
    private lateinit var kzCurrency: TextInputEditText
    private lateinit var dialog: DialogFragment
    private lateinit var deletedCurrency: Currency
    private lateinit var layoutManager: LinearLayoutManager

    private var positionOfDeletedItem = 0

    private var chosenIndex = -1


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutFragmentConvertorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = view.findViewById(R.id.toolbar)
        toolbar.inflateMenu(R.menu.main_menu)

        setupFun()
        onOptionsItemSelected1()
        setupFirstCurrency()


        viewModel.currencyList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        }

    }

    private fun setupFirstCurrency() {
        binding.currencyFlag.setImageResource(R.drawable.image_1)
    }

    private fun setupFun() {
        val myLambda: (Currency, Int) -> Unit = { item, position ->
            deletedCurrency = item
            positionOfDeletedItem = position
            toolbar.title = "$position Item Selected"
            toolbar.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.toolbarColor
                )
            )
            toolbar.menu.findItem(R.id.menu_del).isVisible = true
            toolbar.menu.findItem(R.id.sorting_by).isVisible = false
            toolbar.menu.findItem(R.id.drop_sorting).isVisible = false
            Log.i("MainActivity", "$deletedCurrency")


        }


        val myRecyclerView = binding.recyclerView
        adapter = ConvertorAdapter(
            viewModel::deleteCurrency,
            viewModel::moveItem,
            myLambda,
            viewModel.balance
        )
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        myRecyclerView.adapter = adapter
        myRecyclerView.layoutManager = layoutManager

        val itemTouchHelper = ItemTouchHelper(DragDropMove(adapter))
        itemTouchHelper.attachToRecyclerView(myRecyclerView)

        kzCurrency = binding.currencyTextKaz
        kzCurrency.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.length > Int.MAX_VALUE.toString().length - 1) return
                if (text.isNotBlank()) {
                    viewModel.setBalance(text.toInt())
                }
            }
        })


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val chosenMenuItemId = when (chosenIndex) {
            0 -> R.id.alphabet
            1 -> R.id.currency
            else -> 0
        }
        menu.findItem(chosenMenuItemId).isChecked = true
        super.onPrepareOptionsMenu(menu)
    }

    private fun onOptionsItemSelected1() {
        toolbar.setOnMenuItemClickListener {

            when (it.itemId) {
                R.id.add_new_currency -> {
                    NewCurrencyFragment().show(childFragmentManager, null)
                    true
                }
                R.id.alphabet -> {
                    chosenIndex = 0
                    viewModel.sortByName()
                    true
                }
                R.id.currency -> {
                    chosenIndex = 1
                    viewModel.sortByPrice()
                    true
                }
                R.id.drop_sorting -> {
                    chosenIndex = -1
                    viewModel.sortByID()
                    true
                }
                R.id.menu_del -> {
                    deleteItems()
                    true
                }
                else -> false
            }
        }

    }

    private fun scrollBottom(n: Int) {
        val smoothScroller = object : LinearSmoothScroller(requireContext()) {
            override fun getVerticalSnapPreference(): Int = SNAP_TO_END
        }
        smoothScroller.targetPosition = n
        layoutManager.startSmoothScroll(smoothScroller) // плавная прокрутка
    }

    private fun deleteItems() {
        dialog = DeleteDialogFragment()
        dialog.show(childFragmentManager, null)
        toolbar.title = "Converter"
        toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        toolbar.menu.findItem(R.id.menu_del).isVisible = false
        toolbar.menu.findItem(R.id.sorting_by).isVisible = true
        toolbar.menu.findItem(R.id.drop_sorting).isVisible = true
    }

    override fun onDeleteButton() {

        viewModel.deleteCurrency(deletedCurrency)
        Snackbar.make(binding.recyclerView, "Item Deleted", Snackbar.LENGTH_SHORT)
            .setAction("Undo") { viewModel.addCurrency(deletedCurrency) }
            .show()

        sortingCurrencies(chosenIndex)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun sortingCurrencies(sortBy: Int) {
        if (sortBy == -1) viewModel.sortByID()//viewModel.addNewItem(newItem, viewModel._currencyList.value!!.size)
        if (sortBy == 0) viewModel.sortByName()//viewModel.getPositionType(newItem)
        if (sortBy == 1) viewModel.sortByPrice()//viewModel.getPositionName(newItem)
    }

}
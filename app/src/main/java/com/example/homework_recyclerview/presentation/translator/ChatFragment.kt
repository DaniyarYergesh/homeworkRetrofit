package com.example.homework_recyclerview.presentation.translator

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.adapters.NumberPickerBindingAdapter.setValue
import androidx.fragment.app.Fragment
import com.example.convertor.databinding.FragmentChatBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    var database: DatabaseReference?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = Firebase.database.reference
        val myRef = database?.child("message")

        binding.iconSend.setOnClickListener{
            myRef?.setValue(binding.editMessage.text.toString())

            database?.child("message")?.get()?.addOnSuccessListener {
                binding.tvMyText.text = it.value.toString()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
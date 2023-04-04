package com.gy25m.tpkkosearchapp.fragments

import android.os.Binder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gy25m.tpkkosearchapp.databinding.FragmentPlaceListBinding

class PlaceListFragment : Fragment() {
    lateinit var binding : FragmentPlaceListBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentPlaceListBinding.inflate(inflater,container,false)
        return binding.root
    }
}
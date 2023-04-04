package com.gy25m.tpkkosearchapp.fragments

import android.os.Binder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gy25m.tpkkosearchapp.databinding.FragmentPlaceListBinding
import com.gy25m.tpkkosearchapp.databinding.FragmentPlaceMapBinding

class PlaceMapFragment : Fragment() {
    lateinit var binding : FragmentPlaceMapBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentPlaceMapBinding.inflate(inflater,container,false)
        return binding.root
    }
}
package com.gy25m.tpkkosearchapp.fragments

import android.os.Binder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gy25m.tpkkosearchapp.activities.MainActivity
import com.gy25m.tpkkosearchapp.adapters.PlaceListRecyclerAdapter
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 메인액티비티에 있는 대량의 데이터 소환
        val ma:MainActivity=requireActivity() as MainActivity
        //if (ma.searchPlaceResponse==null)return

//        ma.searchPlaceResponse ?: return
//        binding.recycler.adapter=PlaceListRecyclerAdapter(requireActivity(),ma.searchPlaceResponse!!.documents)
          ma.searchPlaceResponse?.apply {
              binding.recycler.adapter=PlaceListRecyclerAdapter(requireActivity(),this.documents) // 이 영역안에서는 this.도 생략가능
          }


    }
}
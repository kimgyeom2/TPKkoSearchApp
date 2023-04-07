package com.gy25m.tpkkosearchapp.fragments

import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gy25m.tpkkosearchapp.activities.MainActivity
import com.gy25m.tpkkosearchapp.activities.PlaceUrlActivity
import com.gy25m.tpkkosearchapp.databinding.FragmentPlaceListBinding
import com.gy25m.tpkkosearchapp.databinding.FragmentPlaceMapBinding
import com.gy25m.tpkkosearchapp.model.Place
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import net.daum.mf.map.api.MapView.POIItemEventListener

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

    val mapView:MapView by lazy { MapView(context) } // 맵뷰객체 생성

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.containerMapview.addView(mapView)

        // 마커 or 말풍선 클릭이벤트에 반응하는 리스너 등록 - 반드시 마커추가 보다 먼저등록해야 동작함!!!
        mapView.setPOIItemEventListener(markerEventListener)

        // 지도관련 설정( 지도위치, 마커추가 등)
        setMapAndMarkers()
    }

    private fun setMapAndMarkers(){
        // 맵 중심점 변경
        // 현재 내 위치 위도,경도 좌표
        var lat:Double = (activity as MainActivity).myLocation?.latitude  ?: 37.5663
        var lng:Double = (activity as MainActivity).myLocation?.longitude ?: 126.9779
        val mp=MapPoint.mapPointWithGeoCoord(lat,lng)
        mapView.setMapCenterPointAndZoomLevel(mp,5,false)
        mapView.zoomIn(true)
        mapView.zoomOut(true)

        // 내 위치 표시 마커 추가
        var marker=MapPOIItem()
        marker.apply {
            itemName="ME"
            mapPoint=mp
            markerType=MapPOIItem.MarkerType.BluePin
            selectedMarkerType=MapPOIItem.MarkerType.RedPin
        }
        mapView.addPOIItem(marker)

        // 검색 장소들의 마커 추가
        val documents:MutableList<Place>? = (activity as MainActivity).searchPlaceResponse?.documents
        documents?.forEach {
            val point:MapPoint=MapPoint.mapPointWithGeoCoord(it.y.toDouble(),it.x.toDouble())

            var marker=MapPOIItem().apply {
                mapPoint=point
                itemName=it.place_name
                markerType=MapPOIItem.MarkerType.YellowPin
                selectedMarkerType=MapPOIItem.MarkerType.RedPin
                // 마커객체에 보관하고 싶은 데이터가 있다면
                // 즉. 해당 마커에 관련된 정보를 가지고있는 객체를 마커에 저장해놓기
                userObject=it
            }
            mapView.addPOIItem(marker)
        }
    }

   // 마커 or 말풍선 클릭 이벤트 리스너
   val markerEventListener:POIItemEventListener=object : POIItemEventListener{
       override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
           // 마커를 클릭했을때 발동
       }

       override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {
           // deprecated 아래 메소드로 대체됨
       }

       override fun onCalloutBalloonOfPOIItemTouched(
           p0: MapView?,
           p1: MapPOIItem?,
           p2: MapPOIItem.CalloutBalloonButtonType?
       ) {
           // 말풍선 터치했을때
           // 두번째 파라미터 p1 : 클릭한 마커의 객체
           //if(p1?.userObject==null) return
           p1?.userObject ?: return

           var place=p1?.userObject as Place
           var intent=Intent(context,PlaceUrlActivity::class.java)
           intent.putExtra("place_url",place.place_url)
           startActivity(intent)
       }

       override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
           // 마커를 드래그했을때 발동
       }
   }///////////////////////////////////////////////////////////

}
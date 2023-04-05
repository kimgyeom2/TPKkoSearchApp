package com.gy25m.tpkkosearchapp.model

data class KakaoSearchPlaceResponse(var meta:PlaceMeta,var documents:MutableList<Place>)

data class PlaceMeta(var total_count:Int,var pageable_count:Int,var is_end:Boolean)

data class Place(
    var id:String,
    var place_name:String,
    var category_name:String,
    var phone:String,
    var address_name:String,
    var road_address_name:String,
    var x:String,   // 경도
    var y:String,   // 위도
    var place_url:String,
    var distance:String  // 요청 파라미터로 x,y,값을 줬을때만 존재. 단위는 meter
)

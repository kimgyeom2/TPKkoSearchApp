package com.gy25m.tpkkosearchapp.model

data class NidUserInfoResponse(var resultcode:String,var message:String,var response:NidUser)

data class NidUser(var id:String,var email:String)
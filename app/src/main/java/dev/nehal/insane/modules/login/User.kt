package dev.nehal.insane.modules.login

data class User(var phonNum:String ){
    var userID:String=""
    var mName:String=""
    var isApproved:Boolean=false
    var isFirstTimeUser=true
    var pin:String=""
}


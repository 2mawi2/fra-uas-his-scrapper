package com.scrapper.his_scrapper

import javax.inject.Inject


interface IHisService {
    fun login(hisUri: String, user: String, password: String)
}

class HisService @Inject constructor() : IHisService {
    override fun login(hisUri: String, user: String, password: String) {
        TODO()
    }
}
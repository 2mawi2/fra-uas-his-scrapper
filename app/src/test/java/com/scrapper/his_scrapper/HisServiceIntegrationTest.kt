package com.scrapper.his_scrapper

import com.scrapper.his_scrapper.data.remote.HisService
import org.amshove.kluent.shouldNotBeEmpty

class HisServiceIntegrationTest {

    //@Test Insert real credentials to test
    suspend fun `should scrape grades`() {
        val service = HisService()

        val result = service.requestGrades("", "")

        result.shouldNotBeEmpty()
    }
}
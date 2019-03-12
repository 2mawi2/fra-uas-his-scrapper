package com.scrapper.his_scrapper

import org.amshove.kluent.should
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotContain
import org.junit.Test

class HisServiceIntegrationTest {

    //@Test Insert real credentials to test
    fun `should scrape grades`() {
        val service = HisService()

        val result = service.requestGrades("", "")

        result.shouldNotBeEmpty()
    }
}
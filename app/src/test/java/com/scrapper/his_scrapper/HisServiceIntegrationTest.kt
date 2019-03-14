package com.scrapper.his_scrapper

import com.scrapper.his_scrapper.application.Reason
import com.scrapper.his_scrapper.data.remote.HisService
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.*
import org.junit.Test

class HisServiceIntegrationTest {

    //@Test //Insert valid HISQIS credentials to test
    fun `should scrape grades`() {
        runBlocking {
            val service = HisService()

            val result = service.requestGrades("", "")

            result.grades.shouldNotBeEmpty()
            result.success.shouldBeTrue()
        }
    }

    @Test //Insert real credentials to test
    fun `should handle invalid credentials`() {
        runBlocking {
            val service = HisService()

            val result = service.requestGrades("invalidUser", "invalidPassword")

            result.grades.shouldBeEmpty()
            result.success.shouldBeFalse()
            result.reason.shouldBe(Reason.CREDENTIALS)
        }
    }
}
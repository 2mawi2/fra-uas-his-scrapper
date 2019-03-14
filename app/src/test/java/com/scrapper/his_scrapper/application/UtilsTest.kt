package com.scrapper.his_scrapper.application

import org.amshove.kluent.shouldEqual
import org.junit.Test

class UtilsTest {

    @Test
    fun `should append query param to uri`() {
        val result = appendQueryParam("https://www.google.com?existing=3", "further=4").toString()

        result.shouldEqual("https://www.google.com?existing=3&further=4")
    }

    @Test
    fun `should append query param when no param already exists`() {
        val result = appendQueryParam("https://www.google.com", "further=4").toString()

        result.shouldEqual("https://www.google.com?further=4")
    }
}
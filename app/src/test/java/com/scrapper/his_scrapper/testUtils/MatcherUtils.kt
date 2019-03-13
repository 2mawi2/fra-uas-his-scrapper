package com.scrapper.his_scrapper.testUtils

import org.mockito.Mockito

/**
 * workaround for kotlin any null issue with mockito
 */
fun <T> anything(): T {
    Mockito.any<T>()
    return uninitialized()
}

fun <T> uninitialized(): T = null as T
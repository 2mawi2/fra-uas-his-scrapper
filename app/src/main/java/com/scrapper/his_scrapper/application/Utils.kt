package com.scrapper.his_scrapper.application

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URI

fun appendQueryParam(uri: String, queryParam: String): URI {
    val oldUri = URI(uri)

    var newQuery = oldUri.query

    if (newQuery == null) {
        newQuery = queryParam
    } else {
        newQuery += "&$queryParam"
    }

    return URI(
        oldUri.scheme, oldUri.authority,
        oldUri.path, newQuery, oldUri.fragment
    )
}


fun toast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

suspend fun <T> asyncIO(block: suspend CoroutineScope.() -> T) =
    withContext(Dispatchers.IO) {
        block()
    }
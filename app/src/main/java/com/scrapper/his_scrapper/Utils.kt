package com.scrapper.his_scrapper

import java.net.URI

fun appendUri(uri: String, appendQuery: String): URI {
    val oldUri = URI(uri)

    var newQuery = oldUri.query

    if (newQuery == null) {
        newQuery = appendQuery
    } else {
        newQuery += "&$appendQuery"
    }

    return URI(
        oldUri.scheme, oldUri.authority,
        oldUri.path, newQuery, oldUri.fragment
    )
}
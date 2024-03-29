package com.scrapper.his_scrapper.data.remote

import com.scrapper.his_scrapper.application.Grade
import com.scrapper.his_scrapper.application.HisServiceResult
import com.scrapper.his_scrapper.application.Reason
import com.scrapper.his_scrapper.application.appendQueryParam
import io.ktor.client.HttpClient
import io.ktor.client.features.cookies.AcceptAllCookiesStorage
import io.ktor.client.features.cookies.HttpCookies
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.response.HttpResponse
import io.ktor.client.response.readText
import io.ktor.http.Parameters
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.lang.Exception
import java.text.SimpleDateFormat
import javax.inject.Inject


interface IHisService {
    suspend fun requestGrades(user: String, password: String): HisServiceResult
    suspend fun checkCredentials(user: String, password: String): Boolean
}

class HisService @Inject constructor() : IHisService {

    var authRequest =
        "https://his-www.dv.fh-frankfurt.de/qisserver/rds?state=user&type=1&category=auth.requestGrades&startpage=portal.vm&breadCrumbSource=portal"
    var courseOverviewRequest =
        "https://his-www.dv.fh-frankfurt.de/qisserver/rds?state=change&type=1&moduleParameter=studyPOSMenu&nextdir=change&next=menu.vm&subdir=applications&xml=menu&purge=y&navigationPosition=functions%2CstudyPOSMenu&breadcrumb=studyPOSMenu&topitem=functions&subitem=studyPOSMenu"
    var gradingRequest =
        "https://his-www.dv.fh-frankfurt.de/qisserver/rds?state=notenspiegelStudent&next=list.vm&nextdir=qispos/notenspiegel/student&createInfos=Y&struct=auswahlBaum&nodeID=auswahlBaum%7Cabschluss%3Aabschl%3D21%2Cstgnr%3D1&expand=0"

    companion object {
        val client = HttpClient {
            install(HttpCookies) {
                storage = AcceptAllCookiesStorage()
            }
        }
    }

    override suspend fun requestGrades(user: String, password: String): HisServiceResult {
        val (page, reason) = requestGradingPage(user, password)

        if (reason != Reason.NONE) {
            return HisServiceResult(reason = reason, success = false)
        }

        val grades = Jsoup.parse(page)
            .select("table")[1]
            .select("tr")
            .map { it.children() }
            .filter { it.size > 8 }
            .filter { it.first().hasClass("qis_kontoOnTop") }
            .map { mapRowToGrade(it) }

        return HisServiceResult(grades = grades, success = true)
    }

    private fun mapRowToGrade(it: Elements): Grade =
        Grade(
            name = it[1].text(),
            semester = it[2].text(),
            grade = it[3].text().replace(",", ".").toFloatOrNull(),
            credits = it[5].text().replace(",", ".").toFloatOrNull(),
            passed = it[4].text().contains("bestanden"),
            date = if (it[8].text().isNullOrBlank()) null else SimpleDateFormat("dd.mm.yyyy").parse(it[8].text())
        )

    override suspend fun checkCredentials(user: String, password: String): Boolean {
        return try {
            !hasLoginFailed(login(user, password))
        } catch (e: Exception) {
            false
        }
    }

    suspend fun requestGradingPage(user: String, password: String): Pair<String, Reason> {
        return try {
            val authPageResult = login(user, password)
            if (hasLoginFailed(authPageResult)) {
                return Pair("", Reason.CREDENTIALS)
            }

            val gradingPage = client.get<HttpResponse>(courseOverviewRequest).readText()
            val asi = "(?<=asi=)(.*)(?=\"  title)".toRegex().findAll(gradingPage).first().value
            val rq = appendQueryParam(gradingRequest, "asi=$asi")
            val doc = client.get<HttpResponse>(rq.toASCIIString()).readText()
            Pair(doc, Reason.NONE) // success
        } catch (e: Exception) {
            Pair("", Reason.PAGE)
        }
    }

    private fun hasLoginFailed(authPageResult: String) =
        authPageResult.contains("Anmeldung fehlgeschlagen")

    private suspend fun login(user: String, password: String): String {
        return client.post<HttpResponse>(authRequest) {
            body = FormDataContent(formData = Parameters.build {
                append("asdf", user)
                append("fdsa", password)
                append("submit", "Anmelden")
            })
        }.readText()
    }
}
package com.scrapper.his_scrapper.data.remote

import com.scrapper.his_scrapper.application.Grade
import com.scrapper.his_scrapper.application.appendUri
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
import java.text.SimpleDateFormat
import javax.inject.Inject

interface IHisService {
    suspend fun requestGrades(user: String, password: String): List<Grade>
}

class HisService @Inject constructor() : IHisService {

    var authRequest =
        "https://his-www.dv.fh-frankfurt.de/qisserver/rds?state=user&type=1&category=auth.requestGrades&startpage=portal.vm&breadCrumbSource=portal"
    var courseOverviewRequest =
        "https://his-www.dv.fh-frankfurt.de/qisserver/rds?state=change&type=1&moduleParameter=studyPOSMenu&nextdir=change&next=menu.vm&subdir=applications&xml=menu&purge=y&navigationPosition=functions%2CstudyPOSMenu&breadcrumb=studyPOSMenu&topitem=functions&subitem=studyPOSMenu"
    var gradingRequest =
        "https://his-www.dv.fh-frankfurt.de/qisserver/rds?state=notenspiegelStudent&next=list.vm&nextdir=qispos/notenspiegel/student&createInfos=Y&struct=auswahlBaum&nodeID=auswahlBaum%7Cabschluss%3Aabschl%3D21%2Cstgnr%3D1&expand=0"


    override suspend fun requestGrades(user: String, password: String): List<Grade> {
        val gradingPage = requestGradingPage(user, password)

        val grades = Jsoup.parse(gradingPage)
            .select("table")[1]
            .select("tr")
            .map { it.children() }
            .filter { it.size > 8 }
            .filter { it.first().hasClass("qis_kontoOnTop") }
            .map { mapRowToGrade(it) }

        return grades
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

    suspend fun requestGradingPage(user: String, password: String): String {
        val client = HttpClient {
            install(HttpCookies) {
                storage = AcceptAllCookiesStorage()
            }
        }

        client.post<HttpResponse>(authRequest) {
            body = FormDataContent(formData = Parameters.build {
                append("asdf", user)
                append("fdsa", password)
                append("submit", "Anmelden")
            })
        }

        val gradingPage = client.get<HttpResponse>(courseOverviewRequest).readText()
        val asi = "(?<=asi=)(.*)(?=\"  title)".toRegex().findAll(gradingPage).first().value
        val rq = appendUri(gradingRequest, "asi=$asi")
        val doc = client.get<HttpResponse>(rq.toASCIIString()).readText()
        return doc
    }
}
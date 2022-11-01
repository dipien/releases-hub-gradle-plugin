package com.releaseshub.gradle.plugin.metadata

import com.google.api.services.sheets.v4.SheetsScopes
import com.jdroid.java.google.sheets.GoogleSheetsConnector

object GoogleSheetsHelper {

    fun getValues(spreadSheetsId: String, spreadSheetsRange: String): List<List<Any>> {
        val connector = GoogleSheetsConnector("releases-hub-backend", listOf(SheetsScopes.SPREADSHEETS_READONLY),
            "GOOGLE_DRIVE_CREDENTIALS_DIR")

        val response = connector.getValues(spreadSheetsId, spreadSheetsRange)
        return response.getValues()
    }
}

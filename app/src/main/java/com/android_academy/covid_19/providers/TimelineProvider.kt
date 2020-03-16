package com.android_academy.covid_19.providers

import android.os.Environment
import android.os.Environment.getExternalStoragePublicDirectory
import com.android_academy.covid_19.repository.IUsersLocationRepo
import com.android_academy.covid_19.util.kml.KmlLineString
import com.android_academy.covid_19.util.kml.KmlParser
import com.android_academy.covid_19.util.kml.KmlPlacemark
import com.android_academy.covid_19.util.kml.KmlPoint
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.BufferedInputStream
import java.io.File
import java.io.FileFilter
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface TimelineProvider {
    fun checkForExistingKMLFiles()
}

class TimelineProviderImpl(
    private val scope: CoroutineScope,
    private val usersLocationRepo: IUsersLocationRepo
) : TimelineProvider {
    override fun checkForExistingKMLFiles() {
        scope.launch(Dispatchers.IO) {

            usersLocationRepo.cleanOldTimeLineProviderLocation()

            val listFiles = getKmlFilesFromDownloads()

            listFiles?.forEach { file ->
                val fis = FileInputStream(file)
                val bis = BufferedInputStream(fis)
                bis.mark(1024)
                val xmlPullParser = createXmlParser(bis)
                val parser = KmlParser(xmlPullParser)
                parser.parseKml()
                if(parser.containers.size > 0) {
                    parser.containers[0].placemarks.forEach lit@{
                        val locationList = convertToLocationList(it)
                        saveLocations(locationList)
                    }
                }
            }
        }
    }

    private suspend fun saveLocations(locationList: List<UserLocationModel>) {
        locationList.forEach {
            usersLocationRepo.saveLocation(it.toRoomLocationEntity())
        }
    }

    private suspend fun convertToLocationList(it: KmlPlacemark): List<UserLocationModel> {
        val list = mutableListOf<UserLocationModel>()
        if (isPlacemarkDriving(it)) return list

        val name = getName(it)
        val (fromDate, toDate) = getDateFromDescription(it.getProperty("description"))

        val latLngList = getLatLng(it)

        latLngList?.forEach { latLng ->
            list.add(
                UserLocationModel(
                    lat = latLng.latitude,
                    lon = latLng.longitude,
                    accuracy = 0F,
                    speed = 0F,
                    provider = TIMELINE_PROVIDER,
                    name = name,
                    timeStart = fromDate?.time,
                    timeEnd = toDate?.time
                )
            )

        }
        return list
    }

    private fun getName(it: KmlPlacemark) =
        "${it.getProperty("name")}  ${it.getProperty("address")}"

    private fun getLatLng(it: KmlPlacemark): List<LatLng>? {
        return when (it.geometry.geometryType) {
            "Point" -> listOf((it.geometry as KmlPoint).coordinates)
            "LineString" -> (it.geometry as KmlLineString).coordinates
            else -> emptyList()
        }
    }

    private fun getDateFromDescription(property: String): Pair<Date?, Date?> {
        val dateFromString =
            property.substringAfter("from ").substringBefore(" to ")
        val fromDate =
            if (dateFromString.isNotEmpty()) SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(
                dateFromString
            ) else null
        val dateToString = property.substringAfter(" to ").substringBefore(". ")
        val toDate =
            if (dateToString.isNotEmpty()) SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(
                dateToString
            ) else null
        return Pair(fromDate, toDate)
    }

    private fun getKmlFilesFromDownloads(): Array<File>? {
        val externalFilesDir =
            getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        return externalFilesDir?.listFiles(FileFilter { pathname ->
            pathname.isFile && pathname.name.startsWith(
                "history-",
                true
            ) && pathname.extension.contains("kml")
        })
    }

    private fun isPlacemarkDriving(it: KmlPlacemark) =
        it.getProperty("name").toLowerCase(Locale.ROOT) == "driving"

    private fun createXmlParser(stream: BufferedInputStream): XmlPullParser? {
        val factory =
            XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val parser = factory.newPullParser()
        parser.setInput(stream, null as String?)
        return parser
    }

    companion object {
        const val TIMELINE_PROVIDER = "Timeline"
    }
}
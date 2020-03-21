package com.android_academy.covid_19.providers

import android.os.Environment
import android.os.Environment.getExternalStoragePublicDirectory
import com.android_academy.covid_19.repository.UsersLocationRepo
import com.android_academy.covid_19.util.TIME_ZULU_FORMAT
import com.android_academy.covid_19.util.kml.KmlLineString
import com.android_academy.covid_19.util.kml.KmlParser
import com.android_academy.covid_19.util.kml.KmlPlacemark
import com.android_academy.covid_19.util.kml.KmlPoint
import com.android_academy.covid_19.util.logTag
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.File
import java.io.FileFilter
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface TimelineProvider {
    fun checkForExistingKMLFiles()
    suspend fun shouldRequestData(): Boolean
}

class TimelineProviderImpl(
    private val scope: CoroutineScope,
    private val usersLocationRepo: UsersLocationRepo
) : TimelineProvider {
    override fun checkForExistingKMLFiles() {
        scope.launch(Dispatchers.IO) {

            if (isTimelineFresh()) {
                Timber.d("[TimelineProviderImpl], checkForExistingKMLFiles(): Timeline updated recently. No need to re-import")
                return@launch
            }

            usersLocationRepo.cleanOldTimeLineProviderLocation()

            val listFiles = getKmlFilesFromDownloads()

            listFiles?.forEach { file ->
                val fis = FileInputStream(file)
                val bis = BufferedInputStream(fis)
                bis.mark(1024)
                val xmlPullParser = createXmlParser(bis)
                val parser = KmlParser(xmlPullParser)
                parser.parseKml()
                if (parser.containers.size > 0) {
                    parser.containers[0].placemarks.forEach lit@{
                        val locationList = convertToLocationList(it)
                        saveLocations(locationList)
                    }
                }
            }
        }
    }

    private suspend fun isTimelineFresh(): Boolean {
        val lastTimeLineLocation = usersLocationRepo.getLastTimeLineLocation()
        lastTimeLineLocation?.timeEnd?.let {
            val diff = Date().time.minus(it)
            return diff < DAYS_14
        } ?: return false
    }

    override suspend fun shouldRequestData(): Boolean {
        val lastTimeLineLocation = usersLocationRepo.getLastTimeLineLocation()
        Timber.d("[$logTag], shouldRequestData(): lastTimelineLocation: $lastTimeLineLocation")
        return lastTimeLineLocation == null
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
            if (dateFromString.isNotEmpty()) SimpleDateFormat(TIME_ZULU_FORMAT).parse(
                dateFromString
            ) else null
        val dateToString = property.substringAfter(" to ").substringBefore(". ")
        val toDate =
            if (dateToString.isNotEmpty()) SimpleDateFormat(TIME_ZULU_FORMAT).parse(
                dateToString
            ) else null
        return Pair(fromDate, toDate)
    }

    private fun getKmlFilesFromDownloads(): Array<File>? {
        val externalFilesDir =
            getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
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
        const val TIMELINE_URL =
            "https://www.google.com/maps/timeline/kml?authuser=0&pb=!1m8!1m3!1i%s!2i%s!3i%s!2m3!1i%s!2i%s!3i%s"
        const val DAYS_14 = 14 * 24 * 60 * 60 * 1_000
    }
}

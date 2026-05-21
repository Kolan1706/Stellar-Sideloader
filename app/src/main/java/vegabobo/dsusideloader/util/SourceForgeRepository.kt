package vegabobo.dsusideloader.util

import android.util.Xml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import vegabobo.dsusideloader.model.StellarGsiFile
import java.io.StringReader
import java.net.HttpURLConnection
import java.net.URL

object SourceForgeRepository {

    private const val RSS_URL = "https://sourceforge.net/projects/stellar-gsi-updates/rss?path=/"

    suspend fun fetchGsiFiles(): List<StellarGsiFile> = withContext(Dispatchers.IO) {
        try {
            val url = URL(RSS_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.setRequestProperty("Accept", "application/rss+xml")

            val xml = connection.inputStream.bufferedReader().use { it.readText() }
            connection.disconnect()
            parseRss(xml)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun parseRss(xml: String): List<StellarGsiFile> {
        val items = mutableListOf<StellarGsiFile>()
        val parser = Xml.newPullParser()
        parser.setInput(StringReader(xml))
        parser.nextTag()

        var currentName = ""
        var currentLink = ""
        var currentSize = 0L
        var currentDate = ""
        var insideItem = false
        var insideMediaContent = false

        while (parser.eventType != XmlPullParser.END_DOCUMENT) {
            when (parser.eventType) {
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        "item" -> {
                            insideItem = true
                            currentName = ""
                            currentLink = ""
                            currentSize = 0L
                            currentDate = ""
                            insideMediaContent = false
                        }
                        "title" -> if (insideItem) {
                            parser.next()
                            if (parser.eventType == XmlPullParser.TEXT) {
                                currentName = parser.text.trim()
                            }
                        }
                        "link" -> if (insideItem && !insideMediaContent) {
                            parser.next()
                            if (parser.eventType == XmlPullParser.TEXT) {
                                currentLink = parser.text.trim()
                            }
                        }
                        "pubDate" -> if (insideItem) {
                            parser.next()
                            if (parser.eventType == XmlPullParser.TEXT) {
                                currentDate = parser.text.trim()
                            }
                        }
                        "content" -> {
                            if (parser.namespace == "http://video.search.yahoo.com/mrss/" && insideItem) {
                                insideMediaContent = true
                                currentSize = parser.getAttributeValue(null, "filesize")?.toLongOrNull() ?: 0L
                            }
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    when (parser.name) {
                        "item" -> {
                            if (currentName.isNotBlank() && currentLink.isNotBlank()) {
                                items.add(
                                    StellarGsiFile(
                                        name = currentName,
                                        downloadUrl = currentLink,
                                        sizeBytes = currentSize,
                                        pubDate = currentDate,
                                    )
                                )
                            }
                            insideItem = false
                        }
                        "content" -> {
                            if (insideMediaContent) insideMediaContent = false
                        }
                    }
                }
            }
            parser.next()
        }
        return items
    }
}

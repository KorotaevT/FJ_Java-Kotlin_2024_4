package service

import dto.NewsDTO
import dto.response.NewsResponse
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.io.File

class NewsService {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    private companion object {
        private const val URL = "https://kudago.com/public-api/v1.4/news/"
    }

    suspend fun fetchNews(count: Int = 100): List<NewsDTO> {
        val client = HttpClient(CIO)

        return try {
            logger.info("Fetching news with count: $count")

            val response: HttpResponse = client.get(URL) {
                parameter("page_size", count)
                parameter("order_by", "date")
                parameter("location", "spb")
                parameter(
                    "fields",
                    "id,publication_date,title,place,description,site_url,favorites_count,comments_count"
                )
            }

            val json = Json {
                ignoreUnknownKeys = true
            }

            if (response.status.isSuccess()) {
                logger.info("Successfully fetched news")
                val newsResponse = json.decodeFromString<NewsResponse>(response.bodyAsText())

                newsResponse.results
            } else {
                logger.error("Failed to fetch news. Status: ${response.status}")
                emptyList()
            }
        } catch (e: Exception) {
            logger.error("Error fetching news", e)
            emptyList()
        } finally {
            client.close()
        }
    }

    fun saveNewsToCsv(path: String, news: Collection<NewsDTO>) {
        if (path.isEmpty()) {
            logger.warn("File path is null or empty.")
            return
        }

        val file = File(path)
        logger.info("Attempting to save news to $path")

        if (!file.parentFile.exists()) {
            logger.info("Directory does not exist, creating directories: ${file.parentFile.path}")
            file.parentFile.mkdirs()
        }

        if (file.exists()) {
            logger.error("File already exists at path: $path")
            throw IllegalArgumentException("File already exists at path: $path")
        }

        file.bufferedWriter().use { writer ->
            writer.write("id,date,title,place,description,siteUrl,favoritesCount,commentsCount,rating\n")

            news.forEach { news ->
                writer.write("${news.id},${news.date},${news.title},${news.place},${news.description},${news.siteUrl},${news.favoritesCount},${news.commentsCount},${news.rating}\n")
            }
        }

        logger.info("News saved to $path")
    }

}
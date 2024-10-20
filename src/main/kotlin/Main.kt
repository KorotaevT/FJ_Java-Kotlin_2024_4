import dto.NewsDTO
import formatting.newsPrinter
import io.ktor.client.engine.cio.CIO
import java.time.LocalDate
import kotlin.time.Duration
import kotlin.time.measureTimedValue
import kotlinx.coroutines.runBlocking
import util.getMostRatedNews
import org.slf4j.LoggerFactory
import service.NewsService

fun main() = runBlocking {
    val newsService = NewsService(CIO.create())

    val logger = LoggerFactory.getLogger(this.javaClass)

    val (mostRatedNews: List<NewsDTO>, duration: Duration) = measureTimedValue {
        val newsList = newsService.fetchNews(count = 100)

        val mostRatedNews = newsList.getMostRatedNews(
            10,
            LocalDate.of(2024, 1, 1)..LocalDate.of(2024, 12, 31)
        )
        newsService.saveNews("news.csv".toNewsResourcePath(), mostRatedNews)

        mostRatedNews
    }

    val output = newsPrinter {
        addHeader(level = 1) { append("Most Rated News") }

        mostRatedNews.forEach { news ->
            news(news)
        }
    }

    logger.info(output.build())

    output.saveToFile("news.md".toNewsResourcePath())

    logger.info("The process of saving news before parallelization was performed ${duration.inWholeNanoseconds} in nanoseconds.")

    val duration2 = measureTimedValue {
        newsService.getNewsAndSaveThemUsingCoroutines(path = "news2.csv".toNewsResourcePath())
    }

    logger.info("The process of saving news after parallelization was performed ${duration2.duration.inWholeNanoseconds} in nanoseconds.")
}

private fun String.toNewsResourcePath() =
    "src/main/resources/news/$this"
import formatting.newsPrinter
import java.time.LocalDate
import kotlinx.coroutines.runBlocking
import service.NewsService
import util.getMostRatedNews

fun main() = runBlocking {
    val newsService = NewsService()

    val newsList = newsService.fetchNews(100)
    val mostRatedNews = newsList.getMostRatedNews(
        10,
        LocalDate.of(2024, 1, 1)..LocalDate.of(2024, 12, 31)
    )

    newsService.saveNewsToCsv("news.csv".toNewsResourcePath(), mostRatedNews)

    val output = newsPrinter {
        addHeader(level = 1) { append("Most Rated News") }

        mostRatedNews.forEach { news ->
            news(news)
        }
    }

    println(output.build())

    output.saveToFile("news.html".toNewsResourcePath())
}

private fun String.toNewsResourcePath() =
    "src/main/resources/news/$this"
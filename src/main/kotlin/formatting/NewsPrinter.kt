package formatting

import dto.NewsDTO
import org.slf4j.LoggerFactory
import java.io.File

class NewsPrinter {

    private val builder = StringBuilder()
    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun addHeader(level: Int, init: StringBuilder.() -> Unit) {
        builder.append("<h${level}>")
        builder.init()
        builder.append("</h${level}>\n")
    }

    private fun addText(init: StringBuilder.() -> Unit) {
        builder.init()
        builder.append("<p></p>\n")
    }

    private fun bold(text: String) = "<b>$text</b>"

    private fun link(text: String, url: String) = "<a href=\"$url\">$text</a>"

    fun news(news: NewsDTO) {
        addHeader(level = 3) { append(news.title) }

        val map = linkedMapOf(
            "Date:" to "${news.date}",
            "Description:" to news.description,
            "Site URL:" to link("Site Link", news.siteUrl),
            "Favorites Count:" to "${news.favoritesCount}",
            "Comments Count:" to "${news.commentsCount}",
            "Rating:" to "${news.rating}"
        )

        for ((key, value) in map) {
            addText { append(bold(key)).append(" ").append(value) }
        }
    }

    fun build(): String = "<html><body>$builder</body></html>"

    fun saveToFile(path: String) {
        if (path.isEmpty()) {
            logger.warn("File path is null or empty.")
            return
        }

        val file = File(path)
        logger.info("Attempting to save news to $path")

        file.bufferedWriter().use { writer ->
            writer.write(build())
        }

        logger.info("News saved to $path")
    }
}

fun newsPrinter(init: NewsPrinter.() -> Unit): NewsPrinter {
    val printer = NewsPrinter()
    printer.init()
    return printer
}
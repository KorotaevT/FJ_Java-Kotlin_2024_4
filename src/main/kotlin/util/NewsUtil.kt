package util

import dto.NewsDTO
import java.time.LocalDate

fun List<NewsDTO>.getMostRatedNews(count: Int, period: ClosedRange<LocalDate>): List<NewsDTO> {
    if (count < 0) return this

    return this
        .filter { news ->
            news.date?.let { period.contains(it) } ?: false
        }.sortedByDescending { it.rating }
        .take(count)
}
package dto.response

import dto.NewsDTO
import kotlinx.serialization.Serializable

@Serializable
data class NewsResponse(
    val results: List<NewsDTO>
)
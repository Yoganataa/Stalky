package io.github.yoganataa.stalky.domain.models

data class Page(
    val index: Int,
    val url: String = "",
    val imageUrl: String? = null,
    val status: Status = Status.QUEUE
) {
    enum class Status {
        QUEUE, LOAD_PAGE, DOWNLOAD_IMAGE, READY, ERROR
    }
}
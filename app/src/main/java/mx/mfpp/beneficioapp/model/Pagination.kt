package mx.mfpp.beneficioapp.model

data class Pagination(
    val current_page: Int,
    val total_pages: Int,
    val total_items: Int,
    val has_next: Boolean,
    val has_prev: Boolean,
    val next_page: Int?,
    val prev_page: Int?
)
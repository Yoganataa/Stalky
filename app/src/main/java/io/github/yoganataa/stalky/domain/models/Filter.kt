package io.github.yoganataa.stalky.domain.models

data class Filter(
    val name: String,
    val type: FilterType,
    val values: List<String> = emptyList(),
    val selectedValue: String? = null
)

enum class FilterType {
    SELECT,
    CHECKBOX,
    TEXT
}
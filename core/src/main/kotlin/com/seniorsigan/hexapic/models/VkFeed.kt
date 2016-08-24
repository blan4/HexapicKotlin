package com.seniorsigan.hexapic.models

data class VkFeed(
    val response: VkItems = VkItems()
)

data class VkItems(
    val items: List<VkItem> = emptyList(),
    val count: Long = 0,
    val total_count: Long = 0
)

data class VkItem(
    val id: Long = 0,
    val post_type: String = "",
    val attachments: List<VkAttachment> = emptyList(),
    val post_source: PostSource = PostSource()
)

data class PostSource(
    val type: String? = null,
    val platform: String? = null,
    val url: String? = null
)

data class VkAttachment(
    val type: String = "",
    val photo: VkPhotoAttachment? = null
)

data class VkPhotoAttachment(
    var id: Long = 0,
    var owner_id: Long = 0,
    var photo_1280: String = "",
    var photo_807: String = "",
    var photo_604: String = "",
    var photo_130: String = "",
    var photo_75: String = "",
    var width: Int = 0,
    var height: Int = 0
) {
    fun photoUrl(): String? {
        return listOf(photo_1280, photo_807, photo_604, photo_130, photo_75).firstOrNull { it.isNotBlank() }
    }
}

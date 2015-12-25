package com.seniorsigan.kuestagbot.models

import java.util.*

data class InstagramPhotosList(
    var data: List<InstagramMedia> = emptyList(),
    var meta: Meta = Meta()
)

data class Meta(
    var code: Int = 500
)

data class InstagramMedia(
    var tags: List<String> = emptyList(),
    var created_time: Date = Date(),
    var link: String = "",
    var likes: Likes = Likes(),
    var images: InstagramImages = InstagramImages(),
    var type: String = "",
    var id: String = ""
)

data class Likes(
    var count: Int = 0
)

data class InstagramImages(
    var low_resolution: InstagramImage = InstagramImage(),
    var thumbnail: InstagramImage = InstagramImage(),
    var standard_resolution: InstagramImage = InstagramImage()
)

data class InstagramImage(
    var url: String = "",
    var width: Int = 0,
    var height: Int = 0
)

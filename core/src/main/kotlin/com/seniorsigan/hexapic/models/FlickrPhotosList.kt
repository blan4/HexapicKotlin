package com.seniorsigan.hexapic.models

data class FlickrPhotosList(
    var photos: FlickrPhotos = FlickrPhotos(),
    var stat: String = ""
) {
    fun toUrls(size: String) = photos.photo.map { it.url(size) }
}

data class FlickrPhotos(
    var photo: List<FlickrPhoto> = emptyList()
)

data class FlickrPhoto(
    var id: String = "",
    var secret: String = "",
    var server: String = "",
    var farm: String = ""
) {
    fun url(size: String = "") = "https://farm$farm.staticflickr.com/$server/${id}_$secret${"_" + size}.jpg"
}

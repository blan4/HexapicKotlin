package com.seniorsigan.hexapic

interface ImagesRepository {
    fun photosUrlsByTag(tag: String): List<String>
}

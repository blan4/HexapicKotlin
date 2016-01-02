package com.seniorsigan.hexapic

interface ImagesRepository {
    fun photosUrlsByTag(tag: String): List<String>
    fun photosUrlsByUser(username: String): List<String>
}

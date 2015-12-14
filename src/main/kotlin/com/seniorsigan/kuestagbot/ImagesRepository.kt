package com.seniorsigan.kuestagbot

interface ImagesRepository {
    fun photosUrlsByTag(tag: String): List<String>
}
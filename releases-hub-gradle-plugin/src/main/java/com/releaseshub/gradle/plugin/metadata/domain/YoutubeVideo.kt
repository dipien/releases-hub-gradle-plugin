package com.releaseshub.gradle.plugin.metadata.domain

class YoutubeVideo {

    var title: String? = null
    var link: String? = null
        set(link) {
            field = link
            previewUrl = "https://img.youtube.com/vi/" + link?.replace("https://www.youtube.com/watch?v=", "") + "/mqdefault.jpg"
        }
    var previewUrl: String? = null
}

package com.github.christophpickl.urclubs.fx

import com.github.christophpickl.kpotpourri.common.logging.LOG
import javafx.scene.image.Image
import java.awt.Dimension

object Images {

    private val log = LOG {}
    private val imagesPath = "/urclubs/images"
    private val imagesMap = mutableMapOf<ImageId, Image>()
    private val widthHeight = 30

    val size = Dimension(widthHeight, widthHeight)

    init {
        ImageId.values().forEach { id ->
            imagesMap[id] = loadImage(id)
        }
    }

    operator fun get(id: ImageId) = imagesMap[id]!!

    private fun loadImage(id: ImageId): Image {
        log.debug { "loadImage(id=$id)" }
        val classPath = "$imagesPath/${id.relativePath}"
        return PictureUtil.readFxImageFromClasspath(classPath).scale(size)
    }

}

enum class ImageId(
    val relativePath: String
) {
    FAVOURITE_FULL("favourite_full.png"),
    FAVOURITE_OUTLINE("favourite_outline.png"),
    WISHLIST_FULL("wishlist_full.png"),
    WISHLIST_OUTLINE("wishlist_outline.png")
}

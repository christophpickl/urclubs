package com.github.christophpickl.urclubs.fx

import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO

object PictureUtil {

    fun readFxImageFromBytes(bytes: ByteArray): Image =
        Image(ByteArrayInputStream(bytes))

    fun readFxImageFromFileWithSize(file: File, size: Dimension): Image =
        Image(file.toURI().toString(), size.width.toDouble(), size.height.toDouble(), false, false)

    fun readFxImageFromClasspath(path: String): Image =
        Image(PictureUtil::class.java.getResource(path)?.openStream() ?: throw Exception("Could not read image from: $path"))

}


fun Image.scale(dimension: Dimension): Image {
    val swingImage = SwingFXUtils.fromFXImage(this, null)
    val scaledImage = swingImage.getScaledInstance(dimension.width, dimension.height, java.awt.Image.SCALE_DEFAULT)

    val bufferedImage = BufferedImage(scaledImage.getWidth(null), scaledImage.getHeight(null), BufferedImage.TYPE_INT_ARGB)
    val bufferedImageGraphics = bufferedImage.createGraphics()
    bufferedImageGraphics.drawImage(scaledImage, 0, 0, null)
    bufferedImageGraphics.dispose()

    return SwingFXUtils.toFXImage(bufferedImage, null)
}

fun Image.toByteArray(format: ImageFormat): ByteArray {
    val buffer = SwingFXUtils.fromFXImage(this, null)
    return ByteArrayOutputStream().use {
        ImageIO.write(buffer, format.formatName, it)
        it.toByteArray()
    }
}

enum class ImageFormat(val formatName: String) {
    PNG("png"),
    JPG("jpg");

    companion object {
        fun byName(name: String): ImageFormat? = values().firstOrNull { it.formatName == name }
    }
}

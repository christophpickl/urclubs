package com.github.christophpickl.urclubs.domain.partner

import com.github.christophpickl.kpotpourri.common.logging.LOG
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import java.awt.Dimension
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO

private val log = LOG {}

object PictureUtil {

    fun readFxImageFromBytes(bytes: ByteArray): Image = Image(ByteArrayInputStream(bytes))
    fun readFxImageFromFileAsBig(file: File): Image {
        return Image(file.toURI().toString(), ImageSize.BIG.width, ImageSize.BIG.height, false, false)
//        return Image(file.toURI().toString())
    }

    fun readFxImageFromClasspath(path: String): Image {
//        return ImageIcon(ensureSafe("Failed to read image icon! (path=$path)") {
//            log.debug { "readImageIconFromClasspath(path='$path')" }
//            ImageIO.read(Picture::class.java.getResource(path))
//        })
        return Image(Picture::class.java.getResource(path).openStream())
    }

    /*
    File file = fileChooser.showOpenDialog(stage);
    if (file != null) {
        is = new FileInputStream(file);
        Image image = new Image(is);
        imageView.setImage(image);
    }
     */
}

enum class ImageSize(val dimension: Dimension) {
    BIG(Dimension(200, 200)),
    MEDIUM(Dimension(100, 100)),
    LITTLE(Dimension(50, 50));

    val width: Double = dimension.width.toDouble()
    val height: Double = dimension.height.toDouble()

}

fun Image.toByteArray(): ByteArray {
    val buffer = SwingFXUtils.fromFXImage(this, null)
    return ByteArrayOutputStream().use {
        ImageIO.write(buffer, "png", it)
        it.toByteArray()
    }
}

sealed class Picture(val fxImage: Image) {

    abstract val saveRepresentation: ByteArray?

    companion object {
        fun readFromDb(bytes: ByteArray?): Picture {
            if (bytes == null) return DefaultPicture
            return BytesPicture(bytes)
        }
    }

    object DefaultPicture : Picture(PictureUtil.readFxImageFromClasspath("/urclubs/images/defaultPartnerPicture.png")) {
        override val saveRepresentation = null
    }

    class BytesPicture(bytes: ByteArray) : Picture(PictureUtil.readFxImageFromBytes(bytes)) {
        override val saveRepresentation = bytes
    }

    class FilePicture(file: File) : Picture(PictureUtil.readFxImageFromFileAsBig(file)) {
        override val saveRepresentation by lazy { fxImage.toByteArray() }
    }

}

package com.github.christophpickl.urclubs.domain.partner

import com.github.christophpickl.urclubs.fx.ImageFormat
import com.github.christophpickl.urclubs.fx.PictureUtil
import com.github.christophpickl.urclubs.fx.scale
import com.github.christophpickl.urclubs.fx.toByteArray
import javafx.scene.image.Image
import java.awt.Dimension
import java.io.File

enum class PartnerImageSize(val dimension: Dimension) {
    BIG(Dimension(200, 200)),
    MEDIUM(Dimension(100, 100)),
    LITTLE(Dimension(50, 50));

    val width: Double = dimension.width.toDouble()
    val height: Double = dimension.height.toDouble()

}

private fun Image.scale(size: PartnerImageSize): Image = scale(size.dimension)

sealed class PartnerImage(val fxImage: Image) {

    val fxImageBig by lazy { fxImage.scale(PartnerImageSize.BIG) }
    val fxImageMed by lazy { fxImage.scale(PartnerImageSize.MEDIUM) }
    val fxImageLil by lazy { fxImage.scale(PartnerImageSize.LITTLE) }

    abstract val saveRepresentation: ByteArray?

    companion object {
        fun readFromDb(bytes: ByteArray?): PartnerImage {
            if (bytes == null) return DefaultPicture
            return BytesPicture(bytes)
        }
    }

    object DefaultPicture : PartnerImage(PictureUtil.readFxImageFromClasspath("/urclubs/images/defaultPartnerPicture.png")) {
        override val saveRepresentation = null
    }

    class BytesPicture(bytes: ByteArray) : PartnerImage(PictureUtil.readFxImageFromBytes(bytes)) {
        override val saveRepresentation = bytes
    }

    class FilePicture(file: File, format: ImageFormat) : PartnerImage(PictureUtil.readFxImageFromFileWithSize(file, PartnerImageSize.BIG.dimension)) {
        override val saveRepresentation by lazy { fxImage.toByteArray(format) }
    }

}

package com.seniorsigan.kuestagbot

import org.springframework.stereotype.Service
import java.awt.AlphaComposite
import java.awt.RenderingHints
import java.awt.image.BufferedImage

@Service
class ImagesProcessor {
    val size = 640

    fun concatenateImages(images: List<BufferedImage>, width: Int, height: Int): BufferedImage {
        if (images.size != width * height) throw Exception("Images count isn't equals width * height")

        val image = BufferedImage(size * width, size * height, BufferedImage.TYPE_INT_RGB)
        images.forEachIndexed { i, tile ->
            val (x, y) = if (width > height) {
                Pair((i % width) * 640, (i / width) * 640)
            } else {
                Pair((i / height) * 640, (i % height) * 640)
            }

            image.getSubimage(x, y, size, size).data = normalize(tile).data
        }

        return image
    }

    fun normalize(origin: BufferedImage): BufferedImage {
        val image = cropToSquare(origin)
        if (image.width == size && image.height == size) return image
        println("Normalize images ${image.width}x${image.height}")

        val resized = BufferedImage(size, size, image.type)
        val g = resized.createGraphics()
        g.drawImage(image, 0, 0, size, size, null)
        g.dispose()
        g.composite = AlphaComposite.Src

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
            RenderingHints.VALUE_RENDER_QUALITY)
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON)

        return resized
    }

    fun cropToSquare(image: BufferedImage): BufferedImage {
        val height = image.height
        val width = image.width
        if (width == height) return image

        if (width > height) {
            val base = height
            val middleX = width / 2
            val x1 = middleX - height / 2
            val x2 = x1 + base
            val y1 = 0
            val y2 = base
            return cropImage(image, x1, y1, x2, y2)
        } else {
            val base = width
            val middleY = height / 2
            val y1 = middleY - height / 2
            val y2 = y1 + base
            val x1 = 0
            val x2 = base
            return cropImage(image, x1, y1, x2, y2)
        }
    }

    private fun cropImage(image: BufferedImage, x1: Int, y1: Int, x2: Int, y2: Int): BufferedImage {
        val width = x2 - x1
        val height = y2 - y1

        if (x1 < 0 || x2 < 0 || y1 < 0 || y2 < 0) throw Exception("Coordinates are negative [$x1, $y1] [$x2, $y2]")
        if (width < 1 || height < 1) throw Exception("Calculated width or height is negative [$x1, $y1] [$x2, $y2]")
        if (width > image.width) throw Exception("Cropped image width can't be greater than original image width")
        if (height > image.height) throw Exception("Cropped image height can't be greater than original image height")

        return image.getSubimage(x1, y1, width, height)
    }
}

package com.infiniteretry.cwackers

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import kotlin.math.floor

fun Modifier.tiledBackground(
  painter: Painter,
  tileMode: TileMode = TileMode.Grid(),
  tileAlpha: Float = 1f,
  tileSize: (Size, Size) -> Size = { _, bitmapSize -> bitmapSize },
  offset: () -> Offset = { Offset.Zero },
): Modifier {
  return drawBehind {
    val tileSize = tileSize(size, painter.intrinsicSize)

    val columnWidth = tileMode.columnWidth(density, tileSize.width)
    val rowHeight = tileMode.rowHeight(density, tileSize.height)
    val tileOffset = tileMode.offset()
    val patternOffset = offset()
    val xShift = patternOffset.x.mod(columnWidth) - columnWidth
    val yShift = patternOffset.y.mod(rowHeight) - rowHeight

    val primaryContainerDimension: Float
    val secondaryContainerDimension: Float
    val primaryAxisDimension: Float
    val secondaryAxisDimension: Float
    val initialPrimaryOffset: Float
    val initialSecondaryOffset: Float
    val primaryIndexBase: Int
    if (tileMode.isByColumn) {
      primaryContainerDimension = size.width
      secondaryContainerDimension = size.height
      primaryAxisDimension = columnWidth
      secondaryAxisDimension = rowHeight
      initialPrimaryOffset = -tileSize.width - (tileSize.width - columnWidth) / 2 + xShift
      initialSecondaryOffset = -tileSize.height - (tileSize.height - rowHeight) / 2 + yShift
      primaryIndexBase = -floor(patternOffset.x / columnWidth).toInt()
    } else {
      primaryContainerDimension = size.height
      secondaryContainerDimension = size.width
      primaryAxisDimension = rowHeight
      secondaryAxisDimension = columnWidth
      initialPrimaryOffset = -tileSize.height - (tileSize.height - rowHeight) / 2 + yShift
      initialSecondaryOffset = -tileSize.width - (tileSize.width - columnWidth) / 2 + xShift
      primaryIndexBase = -floor(patternOffset.y / rowHeight).toInt()
    }

    var primaryIndex = 0
    var primaryCoordinate = initialPrimaryOffset

    clipRect {
      while (primaryCoordinate < primaryContainerDimension) {
        val logicalPrimaryIndex = primaryIndex + primaryIndexBase
        var secondaryCoordinate =
          initialSecondaryOffset +
            (logicalPrimaryIndex * tileOffset * secondaryAxisDimension).mod(secondaryAxisDimension)

        while (secondaryCoordinate < secondaryContainerDimension) {
          translate(
            left = if (tileMode.isByColumn) primaryCoordinate else secondaryCoordinate,
            top = if (tileMode.isByColumn) secondaryCoordinate else primaryCoordinate,
          ) {
            with(painter) {
              draw(
                size = tileSize,
                alpha = tileAlpha,
              )
            }
          }
          secondaryCoordinate += secondaryAxisDimension
        }

        primaryCoordinate += primaryAxisDimension
        primaryIndex++
      }
    }
  }
}

private fun TileMode.columnWidth(density: Float, width: Float): Float {
  val tileWidth = tileDimension(density, width, horizontalSpacing)
  return if (this is TileMode.HexByColumn) tileWidth * 0.75f else tileWidth
}

private fun TileMode.rowHeight(density: Float, height: Float): Float {
  val tileHeight = tileDimension(density, height, verticalSpacing)
  return if (this is TileMode.HexByRow) tileHeight * 0.75f else tileHeight
}

private fun tileDimension(
  density: Float,
  dimensionValue: Float,
  spacing: TileSpacing,
): Float {
  return when (spacing) {
    is TileSpacing.None -> dimensionValue
    is TileSpacing.Dp -> dimensionValue + (spacing.value.value * density)
    is TileSpacing.Percent -> dimensionValue * (1 + spacing.value)
  }
}

private fun TileMode.offset(): Float {
  return when (this) {
    is TileMode.Grid -> 0f
    is TileMode.BrickByColumn -> offset
    is TileMode.BrickByRow -> offset
    is TileMode.HexByColumn -> 0.5f
    is TileMode.HexByRow -> 0.5f
  }
}

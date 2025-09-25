package com.infiniteretry.cwackers

sealed class TileMode(
  open val horizontalSpacing: TileSpacing,
  open val verticalSpacing: TileSpacing,
  internal val isByColumn: Boolean,
) {
  data class Grid(
    override val horizontalSpacing: TileSpacing = TileSpacing.None,
    override val verticalSpacing: TileSpacing = TileSpacing.None,
  ) : TileMode(horizontalSpacing, verticalSpacing, isByColumn = true)

  data class BrickByColumn(
    override val horizontalSpacing: TileSpacing = TileSpacing.None,
    override val verticalSpacing: TileSpacing = TileSpacing.None,
    val offset: Float = 0.5f,
  ) : TileMode(horizontalSpacing, verticalSpacing, isByColumn = true)

  data class BrickByRow(
    override val horizontalSpacing: TileSpacing = TileSpacing.None,
    override val verticalSpacing: TileSpacing = TileSpacing.None,
    val offset: Float = 0.5f,
  ) : TileMode(horizontalSpacing, verticalSpacing, isByColumn = false)

  data class HexByColumn(
    override val horizontalSpacing: TileSpacing = TileSpacing.None,
    override val verticalSpacing: TileSpacing = TileSpacing.None,
  ) : TileMode(horizontalSpacing, verticalSpacing, isByColumn = true)

  data class HexByRow(
    override val horizontalSpacing: TileSpacing = TileSpacing.None,
    override val verticalSpacing: TileSpacing = TileSpacing.None,
  ) : TileMode(horizontalSpacing, verticalSpacing, isByColumn = false)
}
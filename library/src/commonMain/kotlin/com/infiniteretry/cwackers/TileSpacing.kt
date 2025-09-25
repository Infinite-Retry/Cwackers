package com.infiniteretry.cwackers

sealed interface TileSpacing {
  data object None : TileSpacing
  data class Dp(val value: androidx.compose.ui.unit.Dp) : TileSpacing
  data class Percent(val value: Float) : TileSpacing
}
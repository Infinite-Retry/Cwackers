package com.infiniteretry.cwackers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Label
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cwackers.sample.generated.resources.Res
import cwackers.sample.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource

@Composable
fun App() {
  MaterialTheme {
    Column(
      modifier = Modifier
        .background(MaterialTheme.colorScheme.primaryContainer)
        .safeContentPadding()
        .fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      var tileModeDef by remember { mutableStateOf(TileModeDef.Grid) }
      var spaceXBy by remember { mutableStateOf(SpacingDef.Dp) }
      var spaceYBy by remember { mutableStateOf(SpacingDef.Dp) }
      var spacingX by remember(tileModeDef, spaceXBy) { mutableStateOf(0f) }
      var spacingY by remember(tileModeDef, spaceYBy) { mutableStateOf(0f) }
      var brickOffset by remember(tileModeDef) { mutableStateOf(0f) }
      val tileMode by produceState<TileMode>(
        initialValue = TileMode.Grid(),
        tileModeDef,
        spaceXBy,
        spaceYBy,
        spacingX,
        spacingY,
        brickOffset,
      ) {
        val horizontalSpacing = when (spaceXBy) {
          SpacingDef.Dp -> TileSpacing.Dp(spacingX.dp)
          SpacingDef.Percent -> TileSpacing.Percent(spacingX)
        }
        val verticalSpacing = when (spaceYBy) {
          SpacingDef.Dp -> TileSpacing.Dp(spacingY.dp)
          SpacingDef.Percent -> TileSpacing.Percent(spacingY)
        }
        value = when (tileModeDef) {
          TileModeDef.Grid -> TileMode.Grid(horizontalSpacing, verticalSpacing)
          TileModeDef.BrickByColumn -> TileMode.BrickByColumn(
            horizontalSpacing,
            verticalSpacing,
            brickOffset
          )

          TileModeDef.BrickByRow -> TileMode.BrickByRow(
            horizontalSpacing,
            verticalSpacing,
            brickOffset
          )

          TileModeDef.HexByColumn -> TileMode.HexByColumn(horizontalSpacing, verticalSpacing)
          TileModeDef.HexByRow -> TileMode.HexByRow(horizontalSpacing, verticalSpacing)
        }
      }

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .tiledBackground(
            painter = painterResource(Res.drawable.compose_multiplatform),
            tileMode = tileMode,
          ),
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(text = " Tile Mode: ")

        Dropdown(
          value = tileModeDef,
          description = { it.description },
          onSelected = { tileModeDef = it },
          modifier = Modifier.padding(vertical = 8.dp),
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(text = " H Spacing: ")

        Slider(
          value = spacingX,
          onValueChange = { spacingX = it },
          valueRange = if (spaceXBy == SpacingDef.Percent) -0.9f..0.9f else -40f..40f,
          modifier = Modifier.weight(1f),
        )

        Dropdown(
          value = spaceXBy,
          description = { it.description },
          onSelected = { spaceXBy = it },
          modifier = Modifier.padding(start = 8.dp),
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(text = " V Spacing: ")

        Slider(
          value = spacingY,
          onValueChange = { spacingY = it },
          valueRange = if (spaceXBy == SpacingDef.Percent) -0.9f..0.9f else -40f..40f,
          modifier = Modifier.weight(1f),
        )

        Dropdown(
          value = spaceYBy,
          description = { it.description },
          onSelected = { spaceYBy = it },
          modifier = Modifier.padding(start = 8.dp),
        )
      }

      if (tileModeDef == TileModeDef.BrickByColumn || tileModeDef == TileModeDef.BrickByRow) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(text = " Brick Offset: ")

          Slider(
            value = brickOffset,
            onValueChange = { brickOffset = it },
            valueRange = 0f..1f,
          )
        }
      }
    }
  }
}

@Composable
private inline fun <reified T : Enum<T>> Dropdown(
  value: T,
  crossinline description: (T) -> String,
  crossinline onSelected: (T) -> Unit,
  modifier: Modifier = Modifier,
) {
  val isDropDownExpanded = remember { mutableStateOf(false) }

  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {

    Box {
      Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable {
          isDropDownExpanded.value = true
        }
      ) {
        Text(text = description(value))
        Icon(
          imageVector = Icons.Filled.ArrowDropDown,
          contentDescription = null,
        )
      }
      DropdownMenu(
        expanded = isDropDownExpanded.value,
        onDismissRequest = { isDropDownExpanded.value = false },
      ) {
        enumValues<T>().forEach { value ->
          DropdownMenuItem(
            text = { Text(text = description(value)) },
            onClick = {
              isDropDownExpanded.value = false
              onSelected(value)
            },
          )
        }
      }
    }
  }
}

private enum class TileModeDef(val description: String) {
  Grid("Grid"),
  BrickByColumn("Brick by Column"),
  BrickByRow("Brick by Row"),
  HexByColumn("Hex by Column"),
  HexByRow("Hex by Row"),
}

private enum class SpacingDef(val description: String) {
  Dp("Space by Dp"),
  Percent("Space by Percent"),
}
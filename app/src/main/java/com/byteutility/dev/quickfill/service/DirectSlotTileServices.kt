package com.byteutility.dev.quickfill.service

import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import com.byteutility.dev.quickfill.R
import com.byteutility.dev.quickfill.data.repository.SnippetRepository
import com.byteutility.dev.quickfill.ui.MainActivity
import com.byteutility.dev.quickfill.util.ClipboardHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseSlotTileService(
    private val slotNumber: Int,
    private val defaultIconRes: Int
) : TileService() {

    @Inject
    lateinit var snippetRepository: SnippetRepository

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onStartListening() {
        super.onStartListening()
        updateSlotState()
    }

    private fun updateSlotState() {
        val tile = qsTile ?: return

        serviceScope.launch {
            val snippet = try {
                snippetRepository.getSnippetBySlot(slotNumber)
            } catch (_: Exception) {
                null
            }

            if (snippet != null) {
                tile.label = snippet.title.ifBlank { "Slot $slotNumber" }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    tile.subtitle = snippet.content.take(18)
                }
                tile.icon = Icon.createWithResource(this@BaseSlotTileService, defaultIconRes)
                tile.state = Tile.STATE_ACTIVE
            } else {
                tile.label = "Slot $slotNumber"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    tile.subtitle = "Unassigned"
                }
                tile.icon = Icon.createWithResource(this@BaseSlotTileService, defaultIconRes)
                tile.state = Tile.STATE_INACTIVE
            }
            tile.updateTile()
        }
    }

    override fun onClick() {
        super.onClick()
        serviceScope.launch {
            val snippet = try {
                snippetRepository.getSnippetBySlot(slotNumber)
            } catch (_: Exception) {
                null
            }

            if (snippet != null) {
                ClipboardHelper.copyToClipboard(
                    context = this@BaseSlotTileService,
                    label = snippet.title,
                    text = snippet.content,
                    showToast = true
                )
                Toast.makeText(
                    this@BaseSlotTileService,
                    "Copied: ${snippet.title}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@BaseSlotTileService,
                    "Slot $slotNumber is unassigned. Open QuickFill to set it.",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this@BaseSlotTileService, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    val pendingIntent = PendingIntent.getActivity(
                        this@BaseSlotTileService, 0, intent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    startActivityAndCollapse(pendingIntent)
                } else {
                    @Suppress("DEPRECATION")
                    startActivityAndCollapse(intent)
                }
            }
        }
    }
}

@AndroidEntryPoint
class Slot1TileService : BaseSlotTileService(1, R.drawable.ic_tile_slot1)

@AndroidEntryPoint
class Slot2TileService : BaseSlotTileService(2, R.drawable.ic_tile_slot2)

@AndroidEntryPoint
class Slot3TileService : BaseSlotTileService(3, R.drawable.ic_tile_slot3)

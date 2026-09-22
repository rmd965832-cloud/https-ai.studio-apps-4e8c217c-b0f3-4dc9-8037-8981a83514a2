package com.example.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object WhatsAppHelper {

    /**
     * Shares formatted text with YouTube link to WhatsApp.
     * Uses deep link / direct intent, with automatic graceful fallback to Android Share Sheet
     * if WhatsApp is not installed.
     */
    fun shareTextToWhatsApp(context: Context, text: String): Boolean {
        return try {
            val encoded = URLEncoder.encode(text, StandardCharsets.UTF_8.name())
            val whatsappUri = Uri.parse("whatsapp://send?text=$encoded")
            val whatsappIntent = Intent(Intent.ACTION_VIEW, whatsappUri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            // Check if WhatsApp can handle the custom scheme
            val packageManager = context.packageManager
            val canHandle = whatsappIntent.resolveActivity(packageManager) != null

            if (canHandle) {
                context.startActivity(whatsappIntent)
                true
            } else {
                // Try web link or generic share intent
                val webUri = Uri.parse("https://api.whatsapp.com/send?text=$encoded")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                try {
                    context.startActivity(webIntent)
                    true
                } catch (e: Exception) {
                    fallbackShareSheet(context, text)
                }
            }
        } catch (e: Exception) {
            fallbackShareSheet(context, text)
        }
    }

    private fun fallbackShareSheet(context: Context, text: String): Boolean {
        return try {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val chooser = Intent.createChooser(sendIntent, "Share music via").apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(chooser)
            Toast.makeText(context, "Opening share options...", Toast.LENGTH_SHORT).show()
            true
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No app available to share.", Toast.LENGTH_SHORT).show()
            false
        }
    }
}

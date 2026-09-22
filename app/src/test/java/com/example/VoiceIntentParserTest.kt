package com.example

import com.example.model.IntentAction
import com.example.util.VoiceIntentParser
import org.junit.Assert.assertEquals
import org.junit.Test

class VoiceIntentParserTest {

    @Test
    fun testAssamesePlayIntent() {
        val result = VoiceIntentParser.parse("জুবিন গাৰ্গৰ গান প্লে কৰা")
        assertEquals(IntentAction.PLAY_SONG, result.action)
        assertEquals("জুবিন গাৰ্গৰ", result.query)
    }

    @Test
    fun testAssameseWhatsAppIntent() {
        val result = VoiceIntentParser.parse("বিহু গান WhatsApp-ত পঠাই দিয়া")
        assertEquals(IntentAction.SEND_WHATSAPP, result.action)
    }

    @Test
    fun testBengaliPlayIntent() {
        val result = VoiceIntentParser.parse("অরিজিৎ সিং এর গান বাজাও")
        assertEquals(IntentAction.PLAY_SONG, result.action)
    }

    @Test
    fun testEnglishPlayIntent() {
        val result = VoiceIntentParser.parse("Play Shape of You")
        assertEquals(IntentAction.PLAY_SONG, result.action)
        assertEquals("Shape of You", result.query)
    }

    @Test
    fun testSearchOnlyIntent() {
        val result = VoiceIntentParser.parse("Dr Bhupen Hazarika")
        assertEquals(IntentAction.SEARCH_ONLY, result.action)
        assertEquals("Dr Bhupen Hazarika", result.query)
    }
}

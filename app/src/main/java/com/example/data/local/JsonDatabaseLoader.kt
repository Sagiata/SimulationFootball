package com.example.data.local

import android.content.Context
import com.example.model.Player
import com.example.model.PlayerAttributes
import com.example.model.PlayerRole
import org.json.JSONObject
import java.io.InputStream

/**
 * Utility to extract players, club images, and entities from 13299.json in assets.
 */
object JsonDatabaseLoader {

    data class PlayerJsonData(
        val id: String,
        val name: String,
        val number: Int,
        val primaryRole: String,
        val overallRating: Int,
        val potentialRating: Int,
        val age: Int,
        val nationality: String,
        val flagEmoji: String,
        val imageUrl: String,
        val pace: Int,
        val shooting: Int,
        val passing: Int,
        val dribbling: Int,
        val defending: Int,
        val physicality: Int,
        val tacticalIq: Int,
        val marketValueMillions: Double,
        val weeklyWageThousands: Int,
        val isStarter: Boolean,
        val starterSlotIndex: Int
    )

    data class ClubJsonData(
        val id: String,
        val name: String,
        val shortName: String,
        val badgeColorHex: Long,
        val secondaryBadgeColorHex: Long,
        val league: String,
        val imageUrl: String,
        val overallRating: Int,
        val attackRating: Int,
        val midfieldRating: Int,
        val defenseRating: Int,
        val managerName: String,
        val formation: String
    )

    fun loadRawJson(context: Context, fileName: String = "13299.json"): String {
        return try {
            val inputStream: InputStream = context.assets.open(fileName)
            inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            "{}"
        }
    }

    fun getPlayerImageMap(context: Context): Map<String, String> {
        val imageMap = mutableMapOf<String, String>()
        try {
            val jsonStr = loadRawJson(context)
            val root = JSONObject(jsonStr)
            val playersArray = root.optJSONArray("players") ?: return emptyMap()
            for (i in 0 until playersArray.length()) {
                val p = playersArray.getJSONObject(i)
                val id = p.optString("id")
                val img = p.optString("imageUrl")
                if (id.isNotEmpty() && img.isNotEmpty()) {
                    imageMap[id] = img
                }
            }
        } catch (_: Exception) {}
        return imageMap
    }

    fun getClubImageMap(context: Context): Map<String, String> {
        val imageMap = mutableMapOf<String, String>()
        try {
            val jsonStr = loadRawJson(context)
            val root = JSONObject(jsonStr)
            val clubsArray = root.optJSONArray("clubs") ?: return emptyMap()
            for (i in 0 until clubsArray.length()) {
                val c = clubsArray.getJSONObject(i)
                val id = c.optString("id")
                val img = c.optString("imageUrl")
                if (id.isNotEmpty() && img.isNotEmpty()) {
                    imageMap[id] = img
                }
            }
        } catch (_: Exception) {}
        return imageMap
    }

    fun loadPlayersFrom13299(context: Context): List<PlayerJsonData> {
        val result = mutableListOf<PlayerJsonData>()
        try {
            val jsonStr = loadRawJson(context)
            val root = JSONObject(jsonStr)
            val playersArray = root.optJSONArray("players") ?: return emptyList()
            for (i in 0 until playersArray.length()) {
                val p = playersArray.getJSONObject(i)
                val attrs = p.optJSONObject("attributes") ?: JSONObject()
                result.add(
                    PlayerJsonData(
                        id = p.optString("id"),
                        name = p.optString("name"),
                        number = p.optInt("number", 10),
                        primaryRole = p.optString("primaryRole", "CM"),
                        overallRating = p.optInt("overallRating", 80),
                        potentialRating = p.optInt("potentialRating", 85),
                        age = p.optInt("age", 24),
                        nationality = p.optString("nationality", "Unknown"),
                        flagEmoji = p.optString("flagEmoji", "⚽"),
                        imageUrl = p.optString("imageUrl"),
                        pace = attrs.optInt("pace", 75),
                        shooting = attrs.optInt("shooting", 75),
                        passing = attrs.optInt("passing", 75),
                        dribbling = attrs.optInt("dribbling", 75),
                        defending = attrs.optInt("defending", 75),
                        physicality = attrs.optInt("physicality", 75),
                        tacticalIq = attrs.optInt("tacticalIq", 75),
                        marketValueMillions = p.optDouble("marketValueMillions", 25.0),
                        weeklyWageThousands = p.optInt("weeklyWageThousands", 50),
                        isStarter = p.optBoolean("isStarter", false),
                        starterSlotIndex = p.optInt("starterSlotIndex", -1)
                    )
                )
            }
        } catch (_: Exception) {}
        return result
    }
}

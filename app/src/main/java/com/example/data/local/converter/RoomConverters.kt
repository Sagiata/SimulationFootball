package com.example.data.local.converter

import androidx.room.TypeConverter
import com.example.model.FormationType
import com.example.model.MatchResultType
import com.example.model.PlayerRole
import com.example.model.TrainingFocus

class RoomConverters {

    @TypeConverter
    fun fromPlayerRole(role: PlayerRole?): String? = role?.name

    @TypeConverter
    fun toPlayerRole(value: String?): PlayerRole? = value?.let {
        try {
            PlayerRole.valueOf(it)
        } catch (e: Exception) {
            PlayerRole.CM
        }
    }

    @TypeConverter
    fun fromTrainingFocus(focus: TrainingFocus?): String? = focus?.name

    @TypeConverter
    fun toTrainingFocus(value: String?): TrainingFocus? = value?.let {
        try {
            TrainingFocus.valueOf(it)
        } catch (e: Exception) {
            TrainingFocus.BALANCED
        }
    }

    @TypeConverter
    fun fromFormationType(formation: FormationType?): String? = formation?.name

    @TypeConverter
    fun toFormationType(value: String?): FormationType? = value?.let {
        try {
            FormationType.valueOf(it)
        } catch (e: Exception) {
            FormationType.F_433
        }
    }

    @TypeConverter
    fun fromMatchResultType(result: MatchResultType?): String? = result?.name

    @TypeConverter
    fun toMatchResultType(value: String?): MatchResultType? = value?.let {
        try {
            MatchResultType.valueOf(it)
        } catch (e: Exception) {
            MatchResultType.DRAW
        }
    }

    @TypeConverter
    fun fromStringList(list: List<String>?): String? = list?.joinToString(";;")

    @TypeConverter
    fun toStringList(value: String?): List<String> =
        if (value.isNullOrBlank()) emptyList() else value.split(";;").filter { it.isNotBlank() }

    @TypeConverter
    fun fromFloatList(list: List<Float>?): String? = list?.joinToString(",")

    @TypeConverter
    fun toFloatList(value: String?): List<Float> =
        if (value.isNullOrBlank()) emptyList() else value.split(",").mapNotNull { it.trim().toFloatOrNull() }

    @TypeConverter
    fun fromPlayerRoleList(list: List<PlayerRole>?): String? = list?.map { it.name }?.joinToString(",")

    @TypeConverter
    fun toPlayerRoleList(value: String?): List<PlayerRole> =
        if (value.isNullOrBlank()) emptyList() else value.split(",").mapNotNull {
            try {
                PlayerRole.valueOf(it.trim())
            } catch (e: Exception) {
                null
            }
        }
}

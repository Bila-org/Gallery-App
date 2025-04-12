package com.example.mygallery.ui.common

import android.icu.util.Calendar
import com.example.mygallery.data.MediaItem
import java.text.SimpleDateFormat
import java.util.Locale

fun groupMediaByMonth(mediaList: List<MediaItem>): Map<String, List<MediaItem>>{
    val calendar = Calendar.getInstance()
    val groupedMedia = mutableMapOf<String, MutableList<MediaItem>>()

    val monthYearFormatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    for (media in mediaList){
        calendar.timeInMillis = media.dateAdded!!
        val yearMonth = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}" // Format "YYYY-MM"

        val formattedMonthYear = monthYearFormatter.format(calendar.time)

        if(!groupedMedia.containsKey(formattedMonthYear)){
            groupedMedia[formattedMonthYear] = mutableListOf()
        }
        groupedMedia[formattedMonthYear]?.add(media)
    }

    // Sort the map keys (months) in descending order
    /*return groupedMedia.toList()
        .sortedByDescending { it.first}     // sort by yearMonth key
            .toMap()

     */

    // Sort the map keys (months) in descending order
    return groupedMedia.toList()
        .sortedByDescending { (key, _) ->
            // Parse the formatted month back to a date for sorting
            val date = monthYearFormatter.parse(key)
            date?.time ?: 0
        }
        .toMap()
}



fun formatDuration(duration: Long): String{
    val seconds = duration /1000
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

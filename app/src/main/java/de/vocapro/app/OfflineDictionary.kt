package de.vocapro.app

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import java.io.File

data class DictionaryEntry(val word: String, val meaning: String)

class OfflineDictionary(private val context: Context) {
    private val dbFile = File(context.filesDir, "en_de_dictionary.db")
    @Volatile private var database: SQLiteDatabase? = null

    private fun db(): SQLiteDatabase? {
        database?.let { if (it.isOpen) return it }
        synchronized(this) {
            database?.let { if (it.isOpen) return it }
            if (!dbFile.exists()) {
                try {
                    context.assets.open("en_de_dictionary.db").use { input ->
                        dbFile.outputStream().use { output -> input.copyTo(output) }
                    }
                } catch (_: Exception) { return null }
            }
            return try {
                SQLiteDatabase.openDatabase(dbFile.absolutePath, null, SQLiteDatabase.OPEN_READONLY).also { database = it }
            } catch (_: Exception) { null }
        }
    }

    fun count(): Int = try {
        db()?.rawQuery("SELECT COUNT(*) FROM entries", null)?.use { if (it.moveToFirst()) it.getInt(0) else 0 } ?: 0
    } catch (_: Exception) { 0 }

    fun search(query: String, limit: Int = 100): List<DictionaryEntry> {
        val database = db() ?: return emptyList()
        val q = query.trim()
        val sql = if (q.isBlank()) {
            "SELECT word, meaning FROM entries ORDER BY word COLLATE NOCASE LIMIT ?"
        } else {
            "SELECT word, meaning FROM entries WHERE word LIKE ? OR meaning LIKE ? ORDER BY CASE WHEN word = ? COLLATE NOCASE THEN 0 WHEN word LIKE ? THEN 1 ELSE 2 END, word COLLATE NOCASE LIMIT ?"
        }
        val args = if (q.isBlank()) arrayOf(limit.toString()) else arrayOf("%$q%", "%$q%", q, "$q%", limit.toString())
        return try {
            database.rawQuery(sql, args).use { cursor ->
                buildList {
                    while (cursor.moveToNext()) add(DictionaryEntry(cursor.getString(0), cursor.getString(1)))
                }
            }
        } catch (_: Exception) { emptyList() }
    }

    fun random(): DictionaryEntry? {
        val database = db() ?: return null
        return try {
            database.rawQuery("SELECT word, meaning FROM entries ORDER BY RANDOM() LIMIT 1", null).use {
                if (it.moveToFirst()) DictionaryEntry(it.getString(0), it.getString(1)) else null
            }
        } catch (_: Exception) { null }
    }
}

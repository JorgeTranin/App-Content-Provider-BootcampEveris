package com.jorgetranin.contentprovider.database

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.provider.BaseColumns._ID
import com.jorgetranin.contentprovider.database.NotesDatabaseHelper.Companion.TABLE_NOTES
import java.lang.UnsupportedOperationException

class NotesProvider : ContentProvider() {
    private lateinit var mUriMatcher: UriMatcher
    private lateinit var dbHelper: NotesDatabaseHelper

    override fun onCreate(): Boolean {

        mUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        mUriMatcher.addURI(AUTHORITY, "notes", NOTES)
        mUriMatcher.addURI(AUTHORITY, "notes/#", NOTES_BY_ID)
        if (context != null){
            dbHelper = NotesDatabaseHelper(context as Context)
        }
        return true
    }
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        if (mUriMatcher.match(uri) == NOTES_BY_ID){
            val db: SQLiteDatabase = dbHelper.writableDatabase
            val linesAffect = db.delete(TABLE_NOTES, "$_ID =?", arrayOf(uri.lastPathSegment))
            db.close()
            context?.contentResolver?.notifyChange(uri, null)
            return linesAffect

        }else  {
            throw UnsupportedOperationException("Url invalida para exclusão")
        }
    }

    override fun getType(uri: Uri): String? {
        TODO(
            "Implement this to handle requests for the MIME type of the data" +
                    "at the given URI"
        )
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (mUriMatcher.match(uri) == NOTES){
            val db: SQLiteDatabase = dbHelper.writableDatabase
            val id = db.insert(TABLE_NOTES, null, values)
            val InsertUri = Uri.withAppendedPath(BASE_URI, id.toString())
            db.close()
            context?.contentResolver?.notifyChange(uri, null)
            return InsertUri
        }else{
            throw UnsupportedOperationException("Url invalida para Inserção")
        }
    }



    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
         return when{
             mUriMatcher.match(uri) == NOTES -> {
                 val db: SQLiteDatabase = dbHelper.writableDatabase
                 val cursor = db.query(TABLE_NOTES, projection, selection, selectionArgs, null, null, sortOrder)
                 cursor.setNotificationUri(context?.contentResolver, uri)
                 cursor
             }
             mUriMatcher.match(uri) == NOTES_BY_ID ->{
                 val db: SQLiteDatabase = dbHelper.writableDatabase
                 val cursor = db.query(TABLE_NOTES, projection, "$_ID = ?", arrayOf(uri.lastPathSegment), null, null, sortOrder)
                 cursor.setNotificationUri((context as Context).contentResolver, uri)
                 cursor
             }
             else ->{
                 throw UnsupportedOperationException("Url invalida para Busca")
             }
         }
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        if (mUriMatcher.match(uri) == NOTES_BY_ID){
            val db: SQLiteDatabase = dbHelper.writableDatabase
            val linesAffect = db.update(TABLE_NOTES, values, "$_ID = ?", arrayOf(uri.lastPathSegment))
            db.close()
            context?.contentResolver?.notifyChange(uri, null)
            return linesAffect
        }
        else{
            throw UnsupportedOperationException("Url invalida para Update")
        }
    }

    companion object {
        const val AUTHORITY = "com.jorgetranin.contentprovider.provider"
        val BASE_URI = Uri.parse("content://$AUTHORITY")
        val URI_NOTES = Uri.withAppendedPath(BASE_URI, "notes")
        const val NOTES = 1
        const val NOTES_BY_ID = 2
    }
}
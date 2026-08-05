package com.svapravrithi.app.data.backup

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File as DriveFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wraps Google Sign-In + Drive REST API v3 for a single-file JSON backup.
 *
 * Uses the restrictive `drive.file` scope: the app can only see/modify files *it
 * created* via this API, not the user's whole Drive. The backup is stored as a
 * normal (visible) file named [BACKUP_FILE_NAME] in "My Drive" for transparency -
 * the user can find, move, or delete it themselves at any time.
 *
 * IMPORTANT (see README): this requires a one-time Google Cloud Console setup
 * (OAuth Client ID of type "Android", registered with this app's package name and
 * SHA-1 signing fingerprint, with the Drive API enabled) before sign-in will work.
 */
@Singleton
class GoogleDriveBackupService @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context,
) {
    private val signInOptions: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestScopes(com.google.android.gms.common.api.Scope(DriveScopes.DRIVE_FILE))
        .build()

    val signInClient: GoogleSignInClient by lazy { GoogleSignIn.getClient(context, signInOptions) }

    fun lastSignedInAccount(): GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(context)

    private fun driveServiceFor(account: GoogleSignInAccount): Drive {
        val credential = GoogleAccountCredential.usingOAuth2(context, listOf(DriveScopes.DRIVE_FILE))
        credential.selectedAccount = account.account
        return Drive.Builder(NetHttpTransport(), GsonFactory.getDefaultInstance(), credential)
            .setApplicationName("Sva-Pravrithi")
            .build()
    }

    /** Creates the backup file if it doesn't exist yet, or updates it in place if it does. */
    suspend fun upload(account: GoogleSignInAccount, jsonContent: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val drive = driveServiceFor(account)
            val existingId = findBackupFileId(drive)
            val contentBytes = com.google.api.client.http.ByteArrayContent("application/json", jsonContent.toByteArray())

            if (existingId != null) {
                drive.files().update(existingId, null, contentBytes).execute()
            } else {
                val metadata = DriveFile().setName(BACKUP_FILE_NAME).setMimeType("application/json")
                drive.files().create(metadata, contentBytes).setFields("id").execute()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Returns the backup JSON content, or null if no backup exists yet on Drive. */
    suspend fun download(account: GoogleSignInAccount): Result<String?> = withContext(Dispatchers.IO) {
        try {
            val drive = driveServiceFor(account)
            val fileId = findBackupFileId(drive) ?: return@withContext Result.success(null)
            val output = ByteArrayOutputStream()
            drive.files().get(fileId).executeMediaAndDownloadTo(output)
            Result.success(output.toString("UTF-8"))
        } catch (e: GoogleJsonResponseException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun findBackupFileId(drive: Drive): String? {
        val result = drive.files().list()
            .setQ("name = '$BACKUP_FILE_NAME' and trashed = false")
            .setSpaces("drive")
            .setFields("files(id, name, modifiedTime)")
            .execute()
        return result.files?.firstOrNull()?.id
    }
}

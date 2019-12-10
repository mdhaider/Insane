/**
 *                           MIT License
 *
 *                 Copyright (c) 2019 Amr Elghobary
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.nehal.imgupload

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.nehal.insane.R
import dev.nehal.insane.model.ContentDTO
import dev.nehal.insane.model.Users
import dev.nehal.insane.prelogin.MainActivity
import dev.nehal.insane.shared.Const
import dev.nehal.insane.shared.ModelPreferences

class NewDealWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val repository = TravelsRepository()
    private val context = applicationContext

    override suspend fun doWork(): Result {
        val title = checkNotNull(inputData.getString(KEY_TITLE))
        val imageUri = checkNotNull(inputData.getString(ImageUploaderWorker.KEY_UPLOADED_URI))

        val user = ModelPreferences(context).getObject(Const.PROF_USER, Users::class.java)

        val contentDTO = ContentDTO()
        contentDTO.uid = user?.userUID
        contentDTO.imgUrl = imageUri
        contentDTO.imgCaption = title
        contentDTO.imgUploadDate = System.currentTimeMillis()

        // TODO: add deep link, deal detail fragment, then take user to there from notification.
        val intent = Intent(context, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        when (repository.newImage(contentDTO)) {
            is dev.nehal.imgupload.Result.Success
            -> {
                showFinishedNotification(
                    title, context.getString(R.string.new_deal_success), intent
                )
                return Result.success()
            }
            is dev.nehal.imgupload.Result.Error -> {
                showFinishedNotification(
                    title, context.getString(R.string.new_deal_failure), intent
                )
                return Result.failure()
            }
            dev.nehal.imgupload.Result.Loading -> { /* do nothing club. */ }
        }

        return Result.failure()
    }

    /**
     * Show notification that the activity finished.
     */
    private fun showFinishedNotification(title: String, caption: String, intent: Intent) {

        Notifier.dismissNotification(context, title.hashCode())

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        Notifier.show(context) {
            contentTitle = title
            contentText = caption
            this.pendingIntent = pendingIntent

            // title is used to distinguish between and add as many notifications as new added deals.
            notificationId = title.hashCode()
        }
    }

    companion object {
        const val KEY_TITLE = "key-title-new-deal"
        const val KEY_DESC = "key-desc-new-deal"
    }
}
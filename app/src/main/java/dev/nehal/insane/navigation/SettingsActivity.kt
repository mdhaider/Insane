package dev.nehal.insane.navigation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.auth.FirebaseAuth
import dev.nehal.insane.BuildConfig
import dev.nehal.insane.R
import dev.nehal.insane.databinding.ActivitySettingsBinding
import dev.nehal.insane.modules.login.LoginActivity


class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)


        binding.llFeed.setOnClickListener { sendFeedback() }

        binding.llRate.setOnClickListener { showRateDialog() }

        binding.llShare.setOnClickListener { shareApp() }

        binding.btnLogOut.setOnClickListener { showlogoutDialog() }

        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.versionTxt.text = "v" + BuildConfig.VERSION_NAME

    }

    private fun showRateDialog() {

        MaterialDialog(this).show {
            title(R.string.rate_title)
            message(R.string.rate_msg)
            positiveButton(R.string.rate_pos) { dialog ->
                rateOnPlayStore()
            }
            negativeButton(R.string.rate_neg) { dialog ->
                dialog.dismiss()
            }
        }

    }

    private fun showlogoutDialog() {

        MaterialDialog(this).show {
            message(R.string.logout_msg)
            positiveButton(R.string.logout_pos) { dialog ->
                logOut()
            }
            negativeButton(R.string.logout_neg) { dialog ->
                dialog.dismiss()
            }
        }

    }

    private fun sendFeedback() {
        val intent = Intent(this, FeedbackActivity::class.java)
        startActivity(intent)
    }

    private fun rateOnPlayStore() {
        try {
            val uri: Uri = Uri.parse("market://details?id=$packageName")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                        "http://play.google.com/store/apps/details?id="
                                + packageName
                    )
                )
            )
        }
    }

    private fun logOut() {
        val mAuth = FirebaseAuth.getInstance()
        try {
            mAuth.signOut()
            Toast.makeText(this, "You are Signed out!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()

        } catch (e: Exception) {
            Log.e("ProfileFrag", "onClick: Exception " + e.message, e)
        }
    }

    private fun shareApp() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Insane")
            var shareMessage = "\nWe can’t wait to celebrate with you! Download the Insane app to share your photos with us and everyone at the wedding. Kindly share with family members only.\n\n"
            shareMessage =
                shareMessage+"Click here to download app:\n"+ "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "Share Via"))
        } catch (e: java.lang.Exception) { //e.toString();
        }
    }
}
package dev.nehal.imgupload

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.work.*
import com.bumptech.glide.Glide
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import dev.nehal.insane.R
import dev.nehal.insane.databinding.AddDealFragmentBinding
import kotlinx.android.synthetic.main.fragment_add_photo.*
import java.io.File
import java.util.*

class AddDealFragment : Fragment() {

    private lateinit var binding: AddDealFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.add_deal_fragment, container, false)
        with(binding) {
            lifecycleOwner = this@AddDealFragment
            dealModel = DealUIModel()
            fabSaveDeal.setOnClickListener {
                toast("We are doing the hard work for you...\nFeel fee to use app")
                startCreatingNewDeal() }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rootView.visibility=View.GONE
        startImagePicker()
    }

    private fun startCreatingNewDeal() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadImageWorker: OneTimeWorkRequest = OneTimeWorkRequestBuilder<ImageUploaderWorker>()
            .setConstraints(constraints)
            .setInputData(
                workDataOf(
                    NewDealWorker.KEY_TITLE to binding.dealModel?.title?.value,
                    ImageUploaderWorker.KEY_IMAGE_URI to binding.dealModel?.fileUri?.value.toString()
                )
            )
            .build()

        val newDealWorker: OneTimeWorkRequest = OneTimeWorkRequestBuilder<NewDealWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(requireContext()).let { manager ->
            manager
                .beginWith(uploadImageWorker)
                .then(newDealWorker)
                .enqueue()

            findNavController().navigateUp()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Config.RC_PICK_IMAGES) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                var list: ArrayList<Image> = ArrayList<Image>()
                list = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES)
                Log.d("TAG", list.toString())
                binding.rootView.visibility = View.VISIBLE
                Glide.with(this).load(list[0].path).into(binding.imageView)
                Log.d("path", list[0].path.toString())
                rootView.visibility = View.VISIBLE
                binding.dealModel?.fileUri?.value = Uri.fromFile(File(list[0].path))
            }
        } else {
            findNavController().navigate(R.id.action_photo_home)
            toast("Picking picture failed.")

        }
    }


    private fun startImagePicker() {
        ImagePicker.with(this)                         //  Initialize ImagePicker with activity or fragment context
            .setToolbarColor("#212121")         //  Toolbar color
            .setStatusBarColor("#000000")       //  StatusBar color (works with SDK >= 21  )
            .setToolbarTextColor("#FFFFFF")     //  Toolbar text color (Title and Done button)
            .setToolbarIconColor("#FFFFFF")     //  Toolbar icon color (Back and Camera button)
            .setProgressBarColor("#4CAF50")     //  ProgressBar color
            .setBackgroundColor("#212121")      //  Background color
            .setCameraOnly(false)               //  Camera mode
            .setMultipleMode(false)              //  Select multiple images or single image
            .setFolderMode(true)                //  Folder mode
            //  If the picker should include Videos or only Image Assets
            .setShowCamera(true)                //  Show camera button
            .setFolderTitle("Albums")           //  Folder title (works with FolderMode = true)
            .setImageTitle("Galleries")         //  Image title (works with FolderMode = false)
            .setDoneTitle("Done")               //  Done button title
            .setLimitMessage("You have reached selection limit")    // Selection limit message
            .setMaxSize(1)
            .setSavePath("ImagePicker")         //  Image capture folder name
            .setAlwaysShowDoneButton(true)      //  Set always show done button in multiple mode
            .setRequestCode(100)                //  Set request code, default Config.RC_PICK_IMAGES
            .setKeepScreenOn(true)              //  Keep screen on when selecting images
            .start()
    }
}

package dev.nehal.imgupload

import android.net.Uri
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations

class DealUIModel : ObservableViewModel() {

    val areInputsReady = MediatorLiveData<Boolean>()
    val title = MutableLiveData("")
    val fileUri = MutableLiveData<Uri>(null)
    val imageReady = Transformations.map(fileUri) { it != null }

    init {
        areInputsReady.addSource(title) { areInputsReady.value = checkInputs() }
        areInputsReady.addSource(imageReady) { areInputsReady.value = checkInputs() }
    }

    private fun checkInputs(): Boolean {
        return imageReady.value != false
    }
}
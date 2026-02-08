package com.learining.AzkarApp.UI.BotNav.Admin

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import com.learining.AzkarApp.Data.model.DawaItem
import com.learining.AzkarApp.Data.network.CloudinaryClient
import com.learining.AzkarApp.databinding.FragmentAdminAddDawaListBinding
import com.learining.AzkarApp.utils.uriToFile
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID

class AdminAddDawaListFragment : Fragment() {

    private var _binding: FragmentAdminAddDawaListBinding? = null
    private val binding get() = _binding!!
    private var selectedImageUri: Uri? = null
    private val db = FirebaseFirestore.getInstance()

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectedImageUri = it
                binding.ivFieldPreview.setImageURI(it)
                binding.tvImageStatus.text = "تم اختيار الصورة"
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminAddDawaListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardImagePicker.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSubmit.setOnClickListener {
            validateAndUpload()
        }
    }

    private fun validateAndUpload() {
        val title = binding.etTitle.text.toString().trim()
        val owner = binding.etOwner.text.toString().trim()
        val episodes = binding.etEpisodes.text.toString().toIntOrNull() ?: 0
        val link = binding.etLink.text.toString().trim()

        if (title.isEmpty() || link.isEmpty() || selectedImageUri == null) {
            Toast.makeText(
                requireContext(),
                "برجاء ملء كافة البيانات واختيار صورة",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        setLoading(true)

        // Upload to Cloudinary
        uploadImageToCloudinary(selectedImageUri!!) { imageUrl ->
            saveToFirestore(title, owner, episodes, link, imageUrl)
        }
    }

    private fun saveToFirestore(
        title: String,
        owner: String,
        episodes: Int,
        link: String,
        imageUrl: String
    ) {
        val item = DawaItem(
            title = title,
            ownerName = owner,
            episodesCount = episodes,
            playlistLink = link,
            imageUrl = imageUrl
        )

        db.collection("dawa_lists")
            .add(item)
            .addOnSuccessListener {
                setLoading(false)
                Toast.makeText(requireContext(), "تم إضافة القائمة بنجاح", Toast.LENGTH_SHORT)
                    .show()
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(requireContext(), "خطأ Firestore: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnSubmit.isEnabled = !loading
        binding.cardImagePicker.isEnabled = !loading
    }

    private fun uploadImageToCloudinary(imageUri: Uri, onSuccess: (String) -> Unit) {
        val cloudName = "deyocoino"
        val uploadPreset = "Azkar_Upload_Image"

        lifecycleScope.launch {
            try {
                val file = uriToFile(requireContext(), imageUri)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData(
                    "file",
                    file.name,
                    requestFile
                )
                val presetBody = uploadPreset.toRequestBody("text/plain".toMediaTypeOrNull())

                val response = CloudinaryClient.api.uploadImage(
                    cloudName = cloudName,
                    file = filePart,
                    uploadPreset = presetBody
                )
                
                if (response.isSuccessful) {
                    val imageUrl = response.body()?.secureUrl
                    if (!imageUrl.isNullOrEmpty()) {
                        onSuccess(imageUrl)
                    } else {
                         setLoading(false)
                         Toast.makeText(requireContext(), "Upload succeeded but no URL returned", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    setLoading(false)
                    Toast.makeText(
                        requireContext(),
                        "Upload Failed: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                setLoading(false)
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

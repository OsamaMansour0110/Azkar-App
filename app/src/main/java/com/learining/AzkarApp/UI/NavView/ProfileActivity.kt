package com.learining.AzkarApp.UI.NavView

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.learining.AzkarApp.databinding.ActivityProfileBinding
import com.learining.AzkarApp.utils.UsagePreferences
import kotlinx.coroutines.launch
import java.util.UUID

class ProfileActivity : AppCompatActivity() {

    val addedViews = mutableListOf<View>()
    private lateinit var binding: ActivityProfileBinding
    private var selectedUserImage: Uri? = null
    private lateinit var manager: UsagePreferences

    // Open Gallery to pick image and select image
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedUserImage = uri
            binding.btnSelectProfileImage.apply {
                text = "Image Selected ✅"
                setBackgroundColor(Color.parseColor("#4CAF50"))
            }
        } else
            binding.btnSelectProfileImage.apply {
                text = "Select Image"
                setBackgroundColor(Color.parseColor("#0D6F6F"))
            }
    }

    // Create View to Make Blur
    fun blurView(context: Context): View {
        return View(context).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#80FFFFFF"))
            visibility = View.VISIBLE
        }
    }

    // Insert The View 5 Times to make it blur
    fun showLoading() {
        for (i in 1..5) {
            val view = blurView(this)
            binding.profileContent.addView(view)
            addedViews.add(view)
        }
        binding.progressBarLoading.visibility = View.VISIBLE
    }

    // Clear Views MutableList And layout
    fun hideLoading() {
        for (view in addedViews) {
            binding.profileContent.removeView(view)
        }
        addedViews.clear()
        binding.progressBarLoading.visibility = View.GONE
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        manager = UsagePreferences(this)
        showLoading()

        lifecycleScope.launch {
            // manager.userId.collect { id ->
            val id = "Guest_User" // Hardcoded Guest_User ID
            Firebase.firestore.collection("users")
                .document(id)
                .get()
                .addOnSuccessListener { doc ->
                    val imageProfile = doc.getString("photo") ?: ""
                    if (imageProfile.isNotEmpty())
                        Glide.with(this@ProfileActivity)
                            .load(imageProfile)
                            .circleCrop()
                            .into(binding.profileImage)

                    binding.etUserName.setText(doc.getString("username") ?: "Guest")
                    binding.userEmail.text = doc.getString("email") ?: "guest@example.com"
                    hideLoading()
                }
                .addOnFailureListener {
                    binding.etUserName.setText("Guest")
                    binding.userEmail.text = "guest@example.com"
                    hideLoading()
                }
            // }
        }

        binding.btnSelectProfileImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSaveProfile.setOnClickListener {
            val updatedName = binding.etUserName.text.toString().trim()

            if (updatedName.isEmpty()) {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showLoading()
            val userId = "Guest_User"

            if (selectedUserImage != null) {
                // Upload Image First then update Firestore
                uploadImageAndSaveProfile(userId, updatedName)
            } else {
                // Just update Name and other fields
                updateFirestoreData(userId, hashMapOf("username" to updatedName))
            }
        }
    }

    private fun uploadImageAndSaveProfile(userId: String, username: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("users/${UUID.randomUUID()}.jpg")

        imageRef.putFile(selectedUserImage!!)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                imageRef.downloadUrl
            }.addOnSuccessListener { downloadUri ->
                val userData = hashMapOf<String, Any>(
                    "username" to username,
                    "photo" to downloadUri.toString()
                )
                updateFirestoreData(userId, userData)
            }.addOnFailureListener {
                hideLoading()
                Toast.makeText(this, "Image Upload Failed ❌", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateFirestoreData(userId: String, data: HashMap<String, Any>) {
        data["lastUpdated"] = System.currentTimeMillis()

        Firebase.firestore.collection("users")
            .document(userId)
            .set(data, com.google.firebase.firestore.SetOptions.merge())
            .addOnSuccessListener {
                hideLoading()
                Toast.makeText(this, "Profile Saved Successfully! ✅", Toast.LENGTH_SHORT).show()
                // Refresh local UI if needed
                if (data.containsKey("photo")) {
                    Glide.with(this).load(data["photo"]).circleCrop().into(binding.profileImage)
                }
            }
            .addOnFailureListener { e ->
                hideLoading()
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}


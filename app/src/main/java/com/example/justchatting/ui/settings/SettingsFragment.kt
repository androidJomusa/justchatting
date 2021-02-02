package com.example.justchatting.ui.settings


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.justchatting.R
import com.example.justchatting.base.BaseFragment
import com.example.justchatting.databinding.FragmentSettingsBinding
import com.example.justchatting.ui.login.RegisterActivity
import kotlinx.android.synthetic.main.chat_to_image_item.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    val viewModel: SettingsViewModel by viewModel()

    override fun getLayoutId(): Int = R.layout.fragment_settings

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.viewModel = viewModel


        (activity as AppCompatActivity).setSupportActionBar(binding.settingsToolbar)

        viewModel.loadMyProfileImage()

        binding.settingsNotificationConfig.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setNotificationConfig(isChecked)
        }

        binding.settingsEdit.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, RegisterActivity.PERMISSIONS_REQUEST_READ_CONTACTS)
        }
        viewModel.errorToastMessage.observe(this.viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RegisterActivity.PERMISSIONS_REQUEST_READ_CONTACTS && resultCode == Activity.RESULT_OK && data != null) {
                viewModel.profileImage.value = if (Build.VERSION.SDK_INT < 28) {
                     MediaStore.Images.Media.getBitmap(
                        requireActivity().contentResolver,
                        data.data)
                } else {
                    val source = ImageDecoder.createSource(
                        requireActivity().contentResolver,
                        data.data!!
                    )
                    ImageDecoder.decodeBitmap(source)
                }

            viewModel.saveProfileImageToCache()
            viewModel.uploadProfileImage(data.data)
        }
    }
}

package com.micabytes.storybytes.ui

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.micabytes.storybytes.PERM_READ_EXTERNAL_STORAGE
import com.micabytes.storybytes.R
import com.micabytes.storybytes.databinding.FragmentTitleBinding
import com.micabytes.storybytes.util.logX

class TitleFragment : Fragment() {
  lateinit var version: String

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    version = try {
      val pInfo = activity?.packageManager?.getPackageInfo(activity?.packageName, 0)
      pInfo?.versionName ?: ""
    } catch (e: PackageManager.NameNotFoundException) {
      logX(e)
      ""
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding = FragmentTitleBinding.inflate(inflater, container, false)
    binding.view = this
    binding.textVersion.text = version
    binding.lifecycleOwner = this
    return binding.root
  }

  fun start() {
    if (ContextCompat.checkSelfPermission(
        activity as Activity,
        Manifest.permission.READ_EXTERNAL_STORAGE
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      // Permission is not granted
      ActivityCompat.requestPermissions(
        activity as Activity,
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
        PERM_READ_EXTERNAL_STORAGE
      )
      return
    }
    if (ContextCompat.checkSelfPermission(
        activity as Activity,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      // Permission is not granted
      ActivityCompat.requestPermissions(
        activity as Activity,
        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
        PERM_READ_EXTERNAL_STORAGE
      )
      return
    }
    findNavController().navigate(R.id.action_titleFragment_to_loadFragment)
  }

  fun info() = findNavController().navigate(R.id.action_titleFragment_to_infoFragment)

}

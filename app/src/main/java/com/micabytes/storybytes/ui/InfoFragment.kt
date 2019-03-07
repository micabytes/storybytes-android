package com.micabytes.storybytes.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.micabytes.storybytes.databinding.FragmentInfoBinding

class InfoFragment : Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding = FragmentInfoBinding.inflate(inflater, container, false)
    binding.view = this
    binding.lifecycleOwner = this
    return binding.root
  }

}
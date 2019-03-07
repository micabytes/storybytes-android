package com.micabytes.storybytes.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.micabytes.storybytes.Game
import com.micabytes.storybytes.R
import com.micabytes.storybytes.databinding.FragmentStoryBinding

class StoryFragment : Fragment() {
  private lateinit var binding: FragmentStoryBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentStoryBinding.inflate(inflater, container, false)
    binding.view = this
    binding.lifecycleOwner = this
    return binding.root
  }

  override fun onResume() {
    super.onResume()
    update()
  }

  fun update() {
    val sw = Game.story ?: return
    val layout = binding.layoutStory
    layout.removeAllViews()
    sw.image?.let {
      binding.imageStory.setImageBitmap(it)
      binding.imageStory.visibility = View.VISIBLE
    } ?: run {
      binding.imageStory.visibility = View.GONE
    }
    // Story
    val inflater = layoutInflater
    var text: TextView
    sw.lines.forEach {
      text = inflater.inflate(R.layout.story_text_entry, layout, false) as TextView
      text.text = it
      layout.addView(text)
    }
    sw.choices.forEachIndexed { i, choice ->
      text = inflater.inflate(R.layout.story_option_entry, layout, false) as TextView
      text.text = choice
      text.tag = i
      text.setOnClickListener { v ->
        sw.selectChoice(v.tag as Int)
        sw.next()
        update()
      }
      layout.addView(text)
    }
  }

}
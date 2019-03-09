package com.micabytes.storybytes.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.micabytes.storybytes.Game
import com.micabytes.storybytes.R
import com.micabytes.storybytes.StoryWrapper
import com.micabytes.storybytes.databinding.FragmentLoadBinding
import java.io.File
import java.lang.Exception

private const val SLASH = '/'

class LoadFragment : Fragment() {
  private lateinit var binding: FragmentLoadBinding
  val fileAdapter = FileAdapter()
  private val selectListener = SelectListener()
  private var items = ArrayList<File>()
  var currentFile: File? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    currentFile = getFolder()
    binding = FragmentLoadBinding.inflate(inflater, container, false)
    binding.view = this
    binding.lifecycleOwner = this
    refresh()
    return binding.root
  }

  fun play() {
    try {
      currentFile?.let {
        Game.story = StoryWrapper(it)
        findNavController().navigate(R.id.action_loadFragment_to_storyFragment)
      } ?: run {
        Toast.makeText(context, context?.getString(R.string.load_txt_nofile), Toast.LENGTH_LONG).show()
      }
    } catch (e: Exception) {
      Toast.makeText(context, context?.getString(R.string.load_txt_failed, currentFile?.name ?: ""), Toast.LENGTH_LONG).show()
    }
  }

  fun refresh() {
    currentFile?.let {
      items.clear()
      items.addAll(it.listFiles())
      fileAdapter.notifyDataSetChanged()
    }
  }

  private fun getFolder(): File {
    return File(Environment.getExternalStorageDirectory(), "StoryBytes")
  }

  inner class FileAdapter internal constructor() : RecyclerView.Adapter<FileAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val inflater = LayoutInflater.from(parent.context)
      return ViewHolder(inflater.inflate(R.layout.list_file_nav, parent, false))
    }

    override fun getItemCount(): Int = (items.size + 1)

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FileAdapter.ViewHolder, position: Int) {
      if (position == 0) {
        holder.fileName.text = "../"
        holder.fileName.tag = currentFile?.parentFile
      } else {
        val item = items[position - 1]
        holder.fileName.text = item.name + if (item.isDirectory) SLASH else ""
        holder.fileName.tag = item
      }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      val fileName = itemView as TextView

      init {
        fileName.setOnClickListener(selectListener)
      }
    }

  }

  inner class SelectListener : View.OnClickListener {
    override fun onClick(v: View?) {
      val item = v?.tag as File?
      item?.let {
        currentFile = it
        if (it.isDirectory) {
          binding.textTitle.text = activity?.getText(R.string.load_txt_select)
          binding.textSynopsis.text = activity?.getText(R.string.load_txt_find_json)
          refresh()
        } else {
          binding.textTitle.text = it.name
          binding.textSynopsis.text = activity?.getText(R.string.load_txt_pressplay)
        }
      } ?: run {
        // Something went wrong with the selection - reset
        currentFile = Environment.getExternalStorageDirectory()
        refresh()
      }
    }
  }

}


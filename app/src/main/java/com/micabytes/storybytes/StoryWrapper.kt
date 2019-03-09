package com.micabytes.storybytes

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.widget.Toast
import com.bladecoder.ink.runtime.Story
import com.micabytes.storybytes.util.logX
import com.micabytes.storybytes.util.logD
import com.micabytes.storybytes.util.logE
import java.io.*
import java.util.HashMap

private const val COMMAND_MARK = '>'
private const val PARAM_SEPARATOR = ","
private const val NAME_VALUE_TAG_SEPARATOR = ':'
private const val NAME_VALUE_PARAM_SEPARATOR = '='

class StoryWrapper(fileName: File) {
  private var story: Story? = null
  val lines = ArrayList<String>()
  var image: Bitmap? = null

  init {
    try {
      story = Story(getJsonString(FileInputStream(fileName)))
      // If you want to bind external functions, do it here!
      next() // Progress the story to its first choice.
    } catch (e: Exception) {
      logX(e)
      throw e
    }
  }

  fun next() {
    story?.let {
      // We clear the lines after each choice in this implementation. Probably don't want to do this
      // for most things.
      lines.clear()
      var line: String
      val currentLineParams = HashMap<String, String?>(0)
      while (it.canContinue()) {
        try {
          line = it.Continue()
          currentLineParams.clear()
          if (!line.isEmpty()) // Remove trailing '\n'
            line = line.substring(0, line.length - 1)
          if (!line.isEmpty()) {
            logD("INK: $line")
            processParams(it.currentTags, currentLineParams)
            if (line[0] == COMMAND_MARK) {
              processCommand(currentLineParams, line)
            } else {
              processTextLine(line)
            }
          } else {
            logD("INK: EMPTY LINE!")
          }
        } catch (e: Exception) {
          logX(e)
          break
        }
        if (it.currentErrors != null && !it.currentErrors.isEmpty()) {
          logE(it.currentErrors[0])
          break
        }
      }
    }
  }

  private fun processParams(input: List<String>, output: HashMap<String, String?>) {
    for (t in input) {
      val key: String
      val value: String?
      var i = t.indexOf(NAME_VALUE_TAG_SEPARATOR)
      // support ':' and '=' as param separator
      if (i == -1)
        i = t.indexOf(NAME_VALUE_PARAM_SEPARATOR)
      if (i != -1) {
        key = t.substring(0, i).trim { it <= ' ' }
        value = t.substring(i + 1, t.length).trim { it <= ' ' }
      } else {
        key = t.trim { it <= ' ' }
        value = null
      }
      logD("PARAM: $key value: $value")
      output[key] = value
    }
  }

  private fun processCommand(params: HashMap<String, String?>, line: String) {
    val commandName: String?
    val i = line.indexOf(NAME_VALUE_TAG_SEPARATOR)
    if (i == -1) {
      commandName = line.substring(1).trim { it <= ' ' }
      command(commandName, ArrayList())
    } else {
      commandName = line.substring(1, i).trim { it <= ' ' }
      val commandParams =
        line.substring(i + 1).split(PARAM_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }
          .map { it.trim() }
      command(commandName, commandParams)
    }
    if ("debug" == commandName) {
      logD(params["text"] ?: "null")
    }
  }

  private fun processTextLine(line: String) {
    // This version just "prints" the line
    lines.add(line)
  }

  val choices: List<String>
    get () {
      story?.let { s ->
        val options = s.currentChoices
        val choices = ArrayList<String>(options.size)
        for (o in options) {
          var line = o.text
          val idx = line.indexOf(COMMAND_MARK)
          if (idx != -1) {
            line = line.substring(idx + 1).trim { it <= ' ' }
          }
          choices.add(line)
        }
        return choices
      }
      return ArrayList()
    }

  @Throws(IOException::class)
  private fun getJsonString(i: InputStream): String {
    val br = BufferedReader(InputStreamReader(i, "UTF-8"))
    br.use {
      val sb = StringBuilder()
      var line = it.readLine()
      // Replace the BOM mark
      if (line != null)
        line = line.replace('\uFEFF', ' ')
      while (line != null) {
        sb.append(line)
        sb.append("\n")
        line = it.readLine()
      }
      return sb.toString()
    }
  }

  fun selectChoice(i: Int) {
    try {
      story!!.chooseChoiceIndex(i)
    } catch (e: Exception) {
      logX(e)
    }
  }

  private fun command(name: String, params: List<String>) {
    try {
      when (name) {
        // > clear
        "clear" -> {
          lines.clear()
          image = null
        }
        // > image: <path-from-default-directory>
        "image" -> {
          val path = "${Environment.getExternalStorageDirectory()}/${params[0]}"
          image = BitmapFactory.decodeFile(path)
        }
        else -> logX(Exception("Unknown Story Command: $name"))
      }
    } catch (e: Exception) {
      logX(e)
    }
  }

}
package com.micabytes.storybytes

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.micabytes.storybytes.util.logX
import java.io.*

const val PERM_READ_EXTERNAL_STORAGE = 100
const val PERM_WRITE_EXTERNAL_STORAGE = 101

class GameViewActivity : AppCompatActivity() {

  @Override
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_game)
    generateFolder()
    copyAssets()
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    when (requestCode) {
      PERM_READ_EXTERNAL_STORAGE -> {
        run {
          // If request is cancelled, the result arrays are empty.
          if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
              ) != PackageManager.PERMISSION_GRANTED
            ) {
              // Permission is not granted
              ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERM_READ_EXTERNAL_STORAGE
              )
              return
            }

            Navigation.findNavController(this, R.id.navigation_host_fragment)
              .navigate(R.id.action_titleFragment_to_loadFragment)
          } else {
            // permission denied, boo!
            Toast.makeText(
              this,
              "We need access to read the external storage to be able to load the ink story files.",
              Toast.LENGTH_LONG
            ).show()
          }
        }
      }
      PERM_WRITE_EXTERNAL_STORAGE -> {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          Navigation.findNavController(this, R.id.navigation_host_fragment)
            .navigate(R.id.action_titleFragment_to_loadFragment)
        } else {
          // permission denied, boo!
          Toast.makeText(
            this,
            "We need write access to the external storage to download and store ink story files.",
            Toast.LENGTH_LONG
          ).show()
        }
      }
    }
  }


  private fun generateFolder() {
    val folder = File(Environment.getExternalStorageDirectory(), "StoryBytes")
    var success = true
    if (!folder.exists()) {
      success = folder.mkdir()
    }
    if (!success) {
      Toast.makeText(
        this,
        "Unable to create default folder for StoryBytes.",
        Toast.LENGTH_LONG
      ).show()
    }
  }

  private fun copyAssets() {
    val assetManager = assets
    var files: List<String>? = null
    try {
      files = assetManager.list("")?.toList()?.filter { it.endsWith(".json") }
    } catch (e: IOException) {
      logX(e)
    }
    if (files != null)
      for (filename in files) {
        var inp: InputStream? = null
        var out: OutputStream? = null
        try {
          inp = assetManager.open(filename)
          val outFile = File(Environment.getExternalStorageDirectory(), "StoryBytes/$filename")
          out = FileOutputStream(outFile)
          copyFile(inp!!, out)
        } catch (e: IOException) {
          logX(e)
        } finally {
          if (inp != null) {
            try {
              inp.close()
            } catch (e: IOException) {
              // NOOP
            }

          }
          if (out != null) {
            try {
              out.close()
            } catch (e: IOException) {
              // NOOP
            }

          }
        }
      }
  }

  private fun copyFile(inp: InputStream, out: OutputStream) {
    val buffer = ByteArray(1024)
    var read: Int = inp.read(buffer)
    while (read != -1) {
      out.write(buffer, 0, read)
      read = inp.read(buffer)
    }
  }
}

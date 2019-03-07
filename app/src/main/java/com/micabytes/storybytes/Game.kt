package com.micabytes.storybytes

import android.app.Application

/* The Game Application */
class Game : Application() {

  init {
    instance = this
  }

  companion object {
    lateinit var instance: Game
      private set
    var story: StoryWrapper? = null
  }

}

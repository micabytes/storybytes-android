package com.micabytes.storybytes.util

import android.util.Log
import com.micabytes.storybytes.BuildConfig
import java.lang.Exception
import kotlin.reflect.KClass

/* Convenient wrappers over Android Log.* static methods */

/** Wrapper over [Log.i] */
inline fun <reified T> T.logI(message: String, onlyInDebugMode: Boolean = true, enclosingClass: KClass<*>? = null) =
    log(onlyInDebugMode) { Log.i(getClassSimpleName(enclosingClass), message) }

/** Lazy wrapper over [Log.i] */
inline fun <reified T> T.logI(onlyInDebugMode: Boolean = true, enclosingClass: KClass<*>? = null, lazyMessage: () -> String) {
  log(onlyInDebugMode) { Log.i(getClassSimpleName(enclosingClass), lazyMessage.invoke()) }
}

/** Wrapper over [Log.d] */
inline fun <reified T> T.logD(message: String, onlyInDebugMode: Boolean = true, enclosingClass: KClass<*>? = null) =
    log(onlyInDebugMode) { Log.d(getClassSimpleName(enclosingClass), message) }

/** Lazy wrapper over [Log.d] */
inline fun <reified T> T.logD(onlyInDebugMode: Boolean = true, enclosingClass: KClass<*>? = null, lazyMessage: () -> String) {
  log(onlyInDebugMode) { Log.d(getClassSimpleName(enclosingClass), lazyMessage.invoke()) }
}

/** Wrapper over [Log.v] */
inline fun <reified T> T.logV(message: String, onlyInDebugMode: Boolean = true, enclosingClass: KClass<*>? = null) =
    log(onlyInDebugMode) { Log.v(getClassSimpleName(enclosingClass), message) }

/** Lazy wrapper over [Log.v] */
inline fun <reified T> T.logV(onlyInDebugMode: Boolean = true, enclosingClass: KClass<*>? = null, lazyMessage: () -> String) {
  log(onlyInDebugMode) { Log.v(getClassSimpleName(enclosingClass), lazyMessage.invoke()) }
}

/** Wrapper over [Log.e] */
inline fun <reified T> T.logE(message: String, onlyInDebugMode: Boolean = true, enclosingClass: KClass<*>? = null) =
    log(onlyInDebugMode) { Log.e(getClassSimpleName(enclosingClass), message) }

/** Lazy wrapper over [Log.e] */
inline fun <reified T> T.logE(onlyInDebugMode: Boolean = true, enclosingClass: KClass<*>? = null, lazyMessage: () -> String) {
  log(onlyInDebugMode) { Log.e(getClassSimpleName(enclosingClass), lazyMessage.invoke()) }
}

/** Log Exception */
inline fun <reified T> T.logX(e: Exception) {
  e.printStackTrace()
}

inline fun log(onlyInDebugMode: Boolean, logger: () -> Unit) {
  when {
    onlyInDebugMode && BuildConfig.DEBUG -> logger()
    !onlyInDebugMode -> logger()
  }
}

/**
 * Utility that returns the name of the class from within it is invoked.
 * It allows to handle invocations from anonymous classes given that the string returned by `T::class.java.simpleName`
 * in this case is an empty string.
 *
 * @throws IllegalArgumentException if `enclosingClass` is `null` and this function is invoked within an anonymous class
 */
inline fun <reified T> T.getClassSimpleName(enclosingClass: KClass<*>?): String =
    if (T::class.java.simpleName.isNotBlank()) {
      T::class.java.simpleName
    } else { // Enforce the caller to pass a class to retrieve its simple name
      enclosingClass?.simpleName
          ?: throw IllegalArgumentException("enclosingClass cannot be null when invoked from an anonymous class")
    }
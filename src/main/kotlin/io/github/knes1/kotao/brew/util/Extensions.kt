package io.github.knes1.kotao.brew.util

import com.google.inject.Injector
import kotlin.reflect.KClass

/**
 * Helper extensions to libraries used in Kotao
 *
 * @author knesek
 * Created on: 5/27/17
 */


/**
 * Get instance for injector. Type determined from lvalue using reified parameters,
 * which simplifies the function call
 */
inline fun <reified T> Injector.getInstance(): T = getInstance(T::class.java)

inline fun <reified T : Any> Injector.getInstance(klass: KClass<T>): T = getInstance(klass.java)


package io.github.knes1.kotao.brew.services

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.DatabindContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase
import com.fasterxml.jackson.databind.type.TypeFactory

/**
 * @author knesek
 * Created on: 5/28/16
 */
class RepositoryTypeIDResolver : TypeIdResolverBase() {

    var baseType: JavaType? = null

    override fun init(bt: JavaType?) {
        baseType = bt
    }

    override fun idFromValue(value: Any?): String? {
        throw UnsupportedOperationException()
    }

    override fun idFromValueAndType(value: Any?, suggestedType: Class<*>?): String? {
        throw UnsupportedOperationException()
    }

    override fun typeFromId(context: DatabindContext?, id: String?): JavaType? {
        val typeFactory =  TypeFactory.defaultInstance()
        val clazz = searchForClassInPackages(listOf(
                "io.github.knes1.kotao.brew.repositories.impl"
        ), id?.capitalize()?: throw IllegalArgumentException("Repository Config name/type may not be null"))
        return typeFactory.constructSpecializedType(baseType, clazz)
    }

    fun searchForClassInPackages(packages: List<String>, id: String) =
        packages.asSequence().map {
            try {
                TypeFactory.defaultInstance().findClass("$it.${id}RepositoryConfiguration")
            } catch (e: ClassNotFoundException) {
                null
            }
        }.filterNotNull().first()

    override fun getMechanism(): JsonTypeInfo.Id? = JsonTypeInfo.Id.CUSTOM;
}
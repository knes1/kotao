package io.github.knes1.kotao.brew.repositories.impl

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import io.github.knes1.kotao.brew.services.RepositoryTypeIDResolver

/**
 * @author knesek
 * Created on: 5/28/16
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CUSTOM,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "name"
)
@JsonTypeIdResolver(RepositoryTypeIDResolver::class)
abstract class RepositoryConfiguration {
    abstract val name: String
    abstract val autoCollections: Boolean
}
package io.github.knes1.kotao.brew.repositories.impl

import com.fasterxml.jackson.annotation.JsonCreator
import io.github.knes1.kotao.brew.repositories.Repository
import org.jongo.Jongo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author knesek
 * Created on: 5/26/16
 */
@Component
class MongoRepository @Autowired constructor(
        val jongo: Jongo
) : Repository {

    override fun name(): String = "mongo"

    override fun findAll(name: String): Sequence<Map<String, Any>> {
        return (jongo.getCollection(name)
                .find()
                .sort("{\"\$natural\": -1}")
                .`as`(Map::class.java) as Iterable<Map<String, Any>>).asSequence()
    }

}

class MongoRepositoryConfiguration(
        override val name: String,
        override val autoCollections: Boolean
) : RepositoryConfiguration() {
    companion object {
        @JvmStatic @JsonCreator
        fun createWithDefaults(
                name: String? = "mongo",
                autoCollections: Boolean? = false
        ) = MongoRepositoryConfiguration(
                name = name?: "mongo",
                autoCollections = autoCollections?: false
        )
    }
}
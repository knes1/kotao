package io.github.knes1.kotao.brew.repositories.impl

import com.fasterxml.jackson.annotation.JsonCreator
import io.github.knes1.kotao.brew.repositories.Repository
import org.jongo.Jongo

/**
 * @author knesek
 * Created on: 5/26/16
 */

class MongoRepository(
        val jongo: Jongo
) : Repository {

    override fun name(): String = "mongo"

    override fun findAll(name: String): Sequence<Map<String, Any>> {
        return (jongo.getCollection(name)
                .find()  //.find("{\"publishDate\" : {\$lte: \"${Instant.now().toString()}\"}}")
                .sort("{\"publishDate\": -1}")
                .`as`(Map::class.java) as Iterable<Map<String, Any>>).asSequence()
    }

    override fun count(name: String): Long = jongo.getCollection(name).count()

    override fun find(name: String, pageStart: Long, pageSize: Long): Sequence<Map<String, Any>> {
        return (jongo.getCollection(name)
                .find() //.find("{\"publishDate\" : {\$lte: \"${Instant.now().toString()}\"}}")
                .sort("{\"publishDate\": -1}")
                .skip((pageStart * pageSize).toInt())
                .limit(pageSize.toInt())
                .`as`(Map::class.java) as Iterable<Map<String, Any>>).asSequence()
    }


}

class MongoRepositoryConfiguration (
        override val name: String = "mongo",
        override val autoCollections: Boolean = false
) : RepositoryConfiguration() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as MongoRepositoryConfiguration

        if (name != other.name) return false
        if (autoCollections != other.autoCollections) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + autoCollections.hashCode()
        return result
    }

    override fun toString(): String {
        return "MongoRepositoryConfiguration(name='$name', autoCollections=$autoCollections)"
    }
}

package io.github.knes1.kotao.brew.services.impl

import io.github.knes1.kotao.brew.services.Processor
import io.github.knes1.kotao.brew.services.ProcessorResolver
import io.github.knes1.kotao.brew.services.Processors
import javax.inject.Inject

/**
 * @author knesek
 * Created on: 5/26/16
 */
class DefaultProcessorResolver @Inject constructor(
        val processorResolverMap: Map<String, Processor>
) : ProcessorResolver {

    override fun resolve(name: String): Processor? {
        val proc = try {
            Processors.valueOf(name)
        } catch(e: IllegalArgumentException) {
            Processors.None
        }
        return processorResolverMap[proc.name]
    }
}
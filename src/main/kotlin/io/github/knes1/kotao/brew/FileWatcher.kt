package io.github.knes1.kotao.brew

import com.barbarysoftware.watchservice.MacOSXWatchServiceFactory
import com.barbarysoftware.watchservice.WatchableFile
import com.google.inject.Inject
import com.sinfulspoonful.kotao.util.lazyLogger
import io.github.knes1.kotao.brew.repositories.impl.FileRepository
import io.github.knes1.kotao.brew.repositories.impl.FileRepositoryConfiguration
import io.github.knes1.kotao.brew.services.Configuration
import java.nio.file.*

/**
 * @author knesek
 * Created on: 5/27/17
 */
class FileWatcher @Inject constructor(
		val config: Configuration
) {
	val log by lazyLogger()
	val changeListeners = mutableListOf<() -> Unit>()

	fun addChangeListener(listener: () -> Unit) {
		changeListeners.add(listener)
	}

	private fun fireChange() {
		changeListeners.forEach { it.invoke() }
	}

	private fun pathsToWatch(): List<Path> =
			mutableListOf<Path>().apply {
				val fs = FileSystems.getDefault()
				addAll(config.repositories.filterIsInstance(FileRepositoryConfiguration::class.java).map { fs.getPath(it.basePath) })
				add(fs.getPath(config.structure.pathToTemplates()))
				add(fs.getPath(config.structure.pathToAssets()))
			}.filter { Files.exists(it) && Files.isDirectory(it) }


	private fun configureWatchService(): WatchService {
		val eventsToWatch = arrayOf(StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE)
		val watchService: WatchService = if (System.getProperty("os.name").toLowerCase().contains("mac")) {
			MacOSXWatchServiceFactory.newWatchService().apply {
				pathsToWatch().forEach {
					WatchableFile(it).register(this, *eventsToWatch)
				}
			}
		} else {
			FileSystems.getDefault().newWatchService().apply {
				pathsToWatch().forEach {
					it.register(this, *eventsToWatch)
				}
			}
		}
		return watchService
	}

	fun watch() {
		val watchService = configureWatchService()
		log.info("Watching for file system events")
		val modTimeCache = mutableMapOf<String, Long>()
		while (true)  {
			val key = watchService.take()
			var changed = false
			key.pollEvents().forEach {
				val path = it.context()
				if (it.kind() == StandardWatchEventKinds.OVERFLOW) {
					//do nothing
				} else if (it.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
					changed = true
					log.info("Detected deletion of ${it.context()}")
				} else if (path is Path && !Files.isDirectory(path)) {
					val cachedLastMod = modTimeCache[path.toString()]
					val lastMod = Files.getLastModifiedTime(path).toMillis()
					if (cachedLastMod == null || cachedLastMod < lastMod) {
						log.info("Detected change in ${path}")
						changed = true
						modTimeCache[path.toString()] = lastMod
					} else {
						log.info("Notification for ${path} but modified time not changed. Ignoring.")
					}
				}
			}
			if (changed) {
				fireChange()
				/*
				val time = System.currentTimeMillis()
				vertx.eventBus().send("updates", "starting")
				generator.generateAll()
				vertx.eventBus().send("updates", "finished")
				log.info("Page generation finished in ${System.currentTimeMillis() - time} ms.")
				*/
			}
			key.reset()
		}

	}
}
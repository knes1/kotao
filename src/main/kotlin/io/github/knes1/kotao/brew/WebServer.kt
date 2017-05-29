package io.github.knes1.kotao.brew

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.ext.web.handler.sockjs.BridgeOptions
import io.vertx.ext.web.handler.sockjs.SockJSHandler
import io.vertx.kotlin.ext.web.handler.sockjs.PermittedOptions
import java.io.File
import java.nio.charset.Charset


/**
 * Web server serving the generated content. It also listens on "updates" vertx event bus address for messages to
 * update the fronted. Any such messages is passed via injected SockJS to the browser which then reloads the page.
 *
 * Web server is implemented as vertx verticle.
 *
 * @author knesek
 * Created on: 5/26/17
 */
class WebServer(
		val webRoot: String
) : AbstractVerticle() {


	override fun start() {
		val server = vertx.createHttpServer()
		val router = Router.router(vertx)
		val sockJSHandler = SockJSHandler.create(vertx)
		val options = BridgeOptions().addOutboundPermitted(PermittedOptions("updates"))
		sockJSHandler.bridge(options)

		// Handler for Kotao administrative static files and scripts
		router.route("/_kotao/static/*").handler(StaticHandler.create())

		// Handler for Kotao event bus used for pushing updates
		router.route("/_kotao/eventbus/*").handler(sockJSHandler)

		// Injection handler that intercepts html files and injects Kotlin admin scripts,
		// otherwise it delegates to static handler
		router.get("/*").handler {
			val response = it.response()
			if (it.request().uri().endsWith("/")) {
				it.reroute(it.request().uri() + "index.html")
				return@handler
			}
			val file = File(webRoot + it.request().uri())
			if (file.isDirectory) {
				it.reroute(it.request().uri() + "index.html")
				return@handler
			}
			if (!it.request().uri().endsWith("html")) {
				it.next()
				return@handler
			}
			if (!file.exists()) {
				it.next()
				return@handler
			}
			it.vertx().executeBlocking({ future: Future<String> ->
				val contents = file.readText(Charset.forName("UTF-8"))
				val injected = contents.replace("<body>", """<body>
					<script src="/_kotao/static/sockjs.min.js"></script>
					<script src='/_kotao/static/vertx-eventbus.js'></script>
					<script src='/_kotao/static/spin.min.js'></script>
					<script src='/_kotao/static/kotao.js'></script>
				"""
				)
				future.complete(injected)
			}, { asyncResult ->
				if (asyncResult.succeeded()) {
					response.end(asyncResult.result(), "UTF-8")
				} else {
					// In case of any kind of failure resulting from our injection logic, we just delegate
					// to static handler to handle it.
					it.next()
				}
			})
		}

		// Static serving files from the output. Read only flag set to false to prevent caching.
		router.route("/*").handler(StaticHandler.create(webRoot).setFilesReadOnly(false))

		server.requestHandler {
			router.accept(it)
		}.listen(8080)
	}



}
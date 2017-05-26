package io.github.knes1.kotao.brew

import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.StaticHandler

/**
 * @author knesek
 * Created on: 5/26/17
 */

class WebServer(
		val webRoot: String
) : AbstractVerticle() {


	override fun start() {
		val server = vertx.createHttpServer()
		val router = Router.router(vertx)
		router.route("/*").handler(StaticHandler.create(webRoot))
		router.route("/").handler { it.response().end("Hello World") }

		server.requestHandler {
			router.accept(it)
		}.listen(8080)
	}

	fun deploy() {
		Vertx.vertx().deployVerticle(this)
	}

}
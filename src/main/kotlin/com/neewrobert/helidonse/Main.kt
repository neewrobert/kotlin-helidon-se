package com.neewrobert.helidonse

import io.helidon.common.LogConfig
import io.helidon.config.Config
import io.helidon.media.jackson.JacksonSupport
import io.helidon.webserver.Routing
import io.helidon.webserver.WebServer
import java.util.concurrent.CompletionStage

/**
 * The application main class.
 */
object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        startServer()
    }

    private fun startServer(): CompletionStage<WebServer?> {
        LogConfig.configureRuntime()
        val config = Config.create()
        val webServer = WebServer.builder(createRouting(config))
            .config(config["server"])
            .addMediaSupport(JacksonSupport.create())
            .build()

        return webServer.start()
            .thenApply { ws: WebServer ->
                println("WEB server is up! http://localhost:" + ws.port())
                ws.whenShutdown().thenRun { println("WEB server is DOWN. Good bye!") }
                ws
            }.exceptionally { t: Throwable ->
                System.err.println("Startup failed: " + t.message)
                t.printStackTrace(System.err)
                null
            }

    }

    private fun createRouting(config: Config): Routing {
        val primeNumberService = PrimeNumberService(config)
        val builder = Routing.builder()
            .register("/prime-numbers", primeNumberService)
        return builder.build()
    }
}
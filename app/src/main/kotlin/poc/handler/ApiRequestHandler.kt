package poc.handler

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import org.slf4j.LoggerFactory
import poc.config.Configuration.requestHandlers
import poc.expections.MethodNotAllowed
import poc.expections.NotFound

@Suppress("unused")
class ApiRequestHandler(
        private val handlers: Map<String, List<RequestHandlerTemplate<*, *>>>
): RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    companion object {
        private val log = LoggerFactory.getLogger(ApiRequestHandler::class.java)
    }

    constructor(): this(
            requestHandlers
    )

    override fun handleRequest(input: APIGatewayProxyRequestEvent, context: Context?): APIGatewayProxyResponseEvent {
        log.info("full request {}", input)
        return try {
            when(val method = input.httpMethod) {
                "DELETE" -> handle(input, handlers[method])
                "GET" -> handleGet(input, handlers[method])
                "POST" -> handle(input, handlers[method])
                else -> throw MethodNotAllowed(method)
            }
        } catch (t: Throwable) {
            handleException(t)
        }
    }

    private fun handleException(t: Throwable): APIGatewayProxyResponseEvent {
        log.error("handling exception:", t)
        return when(t) {
            is IllegalArgumentException -> badRequest(t)
            is MethodNotAllowed -> methodNotAllowed(t)
            is NotFound -> notFound(t)
            else -> internalError(t)
        }
    }

    private fun badRequest(t: Throwable): APIGatewayProxyResponseEvent {
        return APIGatewayProxyResponseEvent().withStatusCode(409).withBody(t.message)
    }

    private fun methodNotAllowed(t: Throwable): APIGatewayProxyResponseEvent {
        return APIGatewayProxyResponseEvent().withStatusCode(405).withBody(t.message)
    }

    private fun notFound(t: Throwable): APIGatewayProxyResponseEvent {
        return APIGatewayProxyResponseEvent().withStatusCode(404).withBody(t.message)
    }

    private fun internalError(t: Throwable): APIGatewayProxyResponseEvent {
        return APIGatewayProxyResponseEvent().withStatusCode(500).withBody(t.message)
    }

    private fun handleGet(input: APIGatewayProxyRequestEvent, handlerList: List<RequestHandlerTemplate<*, *>>?): APIGatewayProxyResponseEvent {
        return handlerList?.find { it.path == input.path || it.path == input.resource }?.doHandle(input) ?: throw IllegalArgumentException()
    }

    private fun handle(input: APIGatewayProxyRequestEvent, handlerList: List<RequestHandlerTemplate<*, *>>?): APIGatewayProxyResponseEvent {
        return handlerList?.first()?.doHandle(input) ?: throw IllegalArgumentException()
    }
}
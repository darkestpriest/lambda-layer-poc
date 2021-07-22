package poc.handler

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import poc.config.Configuration.requestHandlers
import poc.expections.MethodNotAllowed

@Suppress("unused")
class ApiRequestHandler(
        private val handlers: Map<String, List<RequestHandlerTemplate<*, *>>>
): RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    constructor(): this(
            requestHandlers
    )

    override fun handleRequest(input: APIGatewayProxyRequestEvent, context: Context?): APIGatewayProxyResponseEvent {
        return when(val method = input.httpMethod) {
            "DELETE" -> handle(input, handlers[method])
            "GET" -> handleGet(input, handlers[method])
            "POST" -> handle(input, handlers[method])
            else -> throw MethodNotAllowed(method)
        }
    }

    private fun handleGet(input: APIGatewayProxyRequestEvent, handlerList: List<RequestHandlerTemplate<*, *>>?): APIGatewayProxyResponseEvent {
        return handlerList?.find { it.path == input.path }?.doHandle(input) ?: throw IllegalArgumentException()
    }

    private fun handle(input: APIGatewayProxyRequestEvent, handlerList: List<RequestHandlerTemplate<*, *>>?): APIGatewayProxyResponseEvent {
        return handlerList?.first()?.doHandle(input) ?: throw IllegalArgumentException()
    }
}
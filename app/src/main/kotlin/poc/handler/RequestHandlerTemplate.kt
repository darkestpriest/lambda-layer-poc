package poc.handler

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent

sealed class RequestHandlerTemplate<T, U> {

    abstract val path: String

    abstract val method: String

    abstract fun apply(entity: T): U

    abstract fun parseTo(request: APIGatewayProxyRequestEvent): T

    abstract fun parseFrom(response: U): APIGatewayProxyResponseEvent

    fun doHandle(request: APIGatewayProxyRequestEvent): APIGatewayProxyResponseEvent {
        return parseFrom(apply(parseTo(request)))
    }
}
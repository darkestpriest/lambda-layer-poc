package poc.expections

class MethodNotAllowed(method: String): RuntimeException("method $method not allowed")
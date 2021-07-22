package poc.expections

class NotFound(id: String): RuntimeException("entity by $id cannot be found") {
}
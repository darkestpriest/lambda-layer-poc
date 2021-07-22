package poc.support

import poc.model.Entity
import java.time.Instant
import java.util.*

object TestSupport {
    fun randomString(): String = UUID.randomUUID().toString()

    fun randomEntity(): Entity = Entity(
            id = randomString(),
            value = randomString()
    ).withTimestamp(Instant.now())
}
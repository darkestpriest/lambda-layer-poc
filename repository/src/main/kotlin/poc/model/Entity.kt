package poc.model

import java.time.Instant

data class Entity(
        val id: String,
        val value: String
) {

    var updatedAt: Long? = null
    private set

    fun withTimestamp(timestamp: Instant): Entity {
        this.updatedAt = timestamp.toEpochMilli()
        return this
    }

    fun withTimestamp(timestamp: Long): Entity {
        this.updatedAt = timestamp
        return this
    }
}
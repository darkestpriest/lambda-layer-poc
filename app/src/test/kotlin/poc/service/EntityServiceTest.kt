package poc.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import poc.model.*
import poc.repository.EntityRepository
import poc.support.TestSupport.randomEntity
import poc.support.TestSupport.randomString
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class EntityServiceTest {

    companion object {
        private val databaseResponse: DatabaseResponse = DatabaseResponse(true)
    }

    @Mock
    private lateinit var repository: EntityRepository

    private lateinit var sut: EntityService

    @Before
    fun setup() {
        sut = EntityService(repository)
    }

    @Test
    fun `retrieves entity by id`() {
        val id = randomString()
        val expected = randomEntity()
        val response = FindByIdResponse(
                expected, databaseResponse
        )

        whenever(repository.findBy(any()))
                .thenReturn(response)

        assertEquals(expected, sut.findBy(id))

        verify(repository).findBy(id)
    }

    @Test
    fun `retrieves null for not found entity by id`() {
        val id = randomString()
        val expected = null
        val response = FindByIdResponse(
                expected, databaseResponse
        )

        whenever(repository.findBy(any()))
                .thenReturn(response)

        assertEquals(expected, sut.findBy(id))

        verify(repository).findBy(id)
    }

    @Test
    fun `retrieves all entities`() {
        val expected = 0.rangeTo(4).map {
            randomEntity()
        }
        val response = FindAllResponse(
                expected, databaseResponse
        )

        whenever(repository.findAll())
                .thenReturn(response)

        assertEquals(expected, sut.findAll())
    }

    @Test
    fun `saves entity`() {
        val toSave = Entity(
                id = randomString(), value = randomString()
        )
        val expected = toSave.copy().withTimestamp(Instant.now())
        val response = SaveResponse(
                expected, databaseResponse
        )

        whenever(repository.save(any()))
                .thenReturn(response)

        assertEquals(expected, sut.save(toSave))

        verify(repository).save(toSave)
    }

    @Test
    fun `deletes entity`() {
        val id = randomString()

        whenever(repository.delete(any())).thenReturn(
                DeleteResponse(databaseResponse)
        )

        assertTrue(sut.delete(id))

        verify(repository).delete(id)
    }

}
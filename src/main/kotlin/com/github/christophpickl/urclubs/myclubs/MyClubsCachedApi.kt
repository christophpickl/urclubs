package com.github.christophpickl.urclubs.myclubs

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.ByteBufferInputStream
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.github.christophpickl.urclubs.URCLUBS_CACHE_DIRECTORY
import com.github.christophpickl.urclubs.myclubs.parser.ActivityHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.CourseHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.FinishedActivityHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.PartnerDetailHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.PartnerHtmlModel
import org.ehcache.spi.copy.Copier
import org.ehcache.spi.serialization.Serializer
import java.nio.ByteBuffer
import javax.inject.Inject

class MyClubsCachedApi @Inject constructor(
        @HttpApi private val delegate: MyClubsApi
) : MyClubsApi {

    private val directory = URCLUBS_CACHE_DIRECTORY

    override fun loggedUser(): UserMycJson {
        return delegate.loggedUser()
    }

    override fun partners(): List<PartnerHtmlModel> {
        return delegate.partners()
    }

    override fun partner(shortName: String): PartnerDetailHtmlModel {
        return delegate.partner(shortName)
    }

    override fun courses(filter: CourseFilter): List<CourseHtmlModel> {
        return delegate.courses(filter)
    }

    override fun activity(filter: ActivityFilter): ActivityHtmlModel {
        return delegate.activity(filter)
    }

    override fun finishedActivities(): List<FinishedActivityHtmlModel> {
        return delegate.finishedActivities()
    }

}

data class CachedUserMycJson(
        val id: String,
        val email: String,
        val firstName: String,
        val lastName: String

) {
    constructor(original: UserMycJson) : this(
            id = original.id,
            email = original.email,
            firstName = original.firstName,
            lastName = original.lastName
    )
}

// http://www.ehcache.org/blog/2016/05/12/ehcache3-serializers.html#third-party-serializers
abstract class AbstractCachedSerializer<T>(@Suppress("UNUSED_PARAMETER") loader: ClassLoader) : Serializer<T> {

    private val kryo = Kryo()
    private val bufferSize = 4096

    override fun serialize(obj: T): ByteBuffer {
        val output = Output(bufferSize)
        kryo.writeObject(output, obj)
        return ByteBuffer.wrap(output.buffer)
    }

    override fun read(binary: ByteBuffer): T {
        val input = Input(ByteBufferInputStream(binary))
        return kryo.readObject(input, objectType)
    }

    override fun equals(obj: T, binary: ByteBuffer) = obj == read(binary)

    protected abstract val objectType: Class<T>
}

class CachedUserMycJsonSerializer(loader: ClassLoader) : AbstractCachedSerializer<CachedUserMycJson>(loader) {
    override val objectType = CachedUserMycJson::class.java
}

class CachedUserMycJsonCopier : Copier<CachedUserMycJson> {
    override fun copyForRead(obj: CachedUserMycJson) = obj.copy()
    override fun copyForWrite(obj: CachedUserMycJson) = obj.copy()
}

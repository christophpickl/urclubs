package com.github.christophpickl.urclubs.myclubs.cache.entities

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.ByteBufferInputStream
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.ehcache.spi.serialization.Serializer
import java.nio.ByteBuffer

// http://www.ehcache.org/blog/2016/05/12/ehcache3-serializers.html#third-party-serializers
abstract class AbstractCachedSerializer<T>(@Suppress("UNUSED_PARAMETER") loader: ClassLoader? = null) : Serializer<T> {

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
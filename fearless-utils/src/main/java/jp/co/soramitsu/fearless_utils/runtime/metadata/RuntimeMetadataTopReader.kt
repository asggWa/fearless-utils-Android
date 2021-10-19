package jp.co.soramitsu.fearless_utils.runtime.metadata

import jp.co.soramitsu.fearless_utils.extensions.fromHex
import jp.co.soramitsu.fearless_utils.runtime.metadata.v14.RuntimeMetadataSchemaV14
import jp.co.soramitsu.fearless_utils.scale.EncodableStruct
import jp.co.soramitsu.fearless_utils.scale.Schema
import jp.co.soramitsu.fearless_utils.scale.uint32
import jp.co.soramitsu.fearless_utils.scale.uint8

object Magic : Schema<Magic>() {
    val magicNumber by uint32()
    val runtimeVersion by uint8()
}

object RuntimeMetadataReader {
    private var magic: EncodableStruct<*>? = null
    private var schema: EncodableStruct<*>? = null
    fun read(s: String): RuntimeMetadataReader {
        val bytes = s.fromHex()
        val runtimeVersion = Magic.read(bytes.copyOfRange(0, 5)).let {
            magic = it
            it[Magic.runtimeVersion].toInt()
        }
        val schemaBytes = bytes.copyOfRange(5, bytes.size)
        schema = when {
            runtimeVersion < 14 -> {
                RuntimeMetadataSchema.read(schemaBytes)
            }
            else -> {
                RuntimeMetadataSchemaV14.read(schemaBytes)
            }
        }
        return this
    }

    fun getMagic() = magic!!
    fun getSchema() = schema!!
}

package vegabobo.dsusideloader.model

import kotlinx.serialization.Serializable

@Serializable
data class StellarGsiFile(
    val name: String,
    val downloadUrl: String,
    val sizeBytes: Long,
    val pubDate: String,
) {
    val sizeFormatted: String
        get() {
            val gb = sizeBytes.toDouble() / (1024 * 1024 * 1024)
            return if (gb >= 1.0) "%.2f GB".format(gb) else "%.0f MB".format(sizeBytes.toDouble() / (1024 * 1024))
        }

    val gsiName: String
        get() {
            return name
                .removePrefix("/")
                .removeSuffix(".7z")
                .removePrefix("[erofs]_")
                .removePrefix("[ext4]_")
        }

    val fsType: String
        get() = if (name.contains("[erofs]", ignoreCase = true)) "erofs" else "ext4"
}

package com.example.musicplayer.data.helper

import java.io.File

object FileLoader {


    /**
     * 扫描指定目录，找到音乐文件，并尝试匹配同名的歌词文件。
     *
     * @param directory 要扫描的目录 File 对象。
     * @return 包含音乐文件信息（文件名和歌词路径）的列表。
     */
    suspend fun scanMusicAndLyrics(directory: File): List<Map<String, String>> {

        // 检查目录是否存在且是目录
        if (!directory.isDirectory) {
            return emptyList()
        }

        val musicList = mutableListOf<Map<String, String>>()
        val allFiles = directory.listFiles() ?: return emptyList()

        // 定义常用的文件扩展名
        val musicExtensions = setOf(".mp3", ".flac", ".wav", ".ogg")
        val lyricExtension = ".lrc"

        // 1. 遍历所有文件
        for (musicFile in allFiles) {
            // 确保是文件且文件名不为空
            if (!musicFile.isFile || musicFile.name.isBlank()) {
                continue
            }

            // 2. 检查是否是音乐文件
            val fileNameLower = musicFile.name.toLowerCase()
            val isMusicFile = musicExtensions.any { fileNameLower.endsWith(it) }

            if (isMusicFile) {

                // 3. 获取不带扩展名的基础文件名 (例如 "稻香.mp3" -> "稻香")
                val baseName = musicFile.name.substringBeforeLast('.')

                // 4. 构造预期的歌词文件完整路径 (例如: ".../稻香.lrc")
                val expectedLyricFileName = "$baseName$lyricExtension"
                val lyricFile = File(directory, expectedLyricFileName)

                // 5. 检查歌词文件是否存在
                val lyricFilePath = if (lyricFile.exists() && lyricFile.isFile) {
                    // 如果歌词文件存在，则获取其绝对路径
                    lyricFile.absolutePath
                } else {
                    // 如果不存在，则返回一个空字符串或特定的标记
                    ""
                }

                // 6. 将结果添加到列表中
                // 键: 音乐文件名 (musicFile.name)
                // 值: 匹配到的歌词文件路径 (lyricFilePath)
                musicList.add(mapOf(
                    "music_name" to musicFile.name,
                    "lyric_path" to lyricFilePath
                ))
            }
        }

        return musicList
    }

    /* // 示例用法：
    fun main() {
        // 注意：在实际 Android 应用中，你需要处理权限和使用 Context
        // 假设你有权限访问这个目录
        val musicDirectory = File("/storage/emulated/0/Music")

        // 假设该目录下有：
        // - 海阔天空.mp3
        // - 海阔天空.lrc
        // - 喜欢你.mp3
        // - 纯音乐.wav
        // - 纯音乐.lrc
        // - 告白气球.mp3

        val results = scanMusicAndLyrics(musicDirectory)

        results.forEach {
            println("音乐: ${it["music_name"]}, 歌词路径: ${it["lyric_path"]}")
        }
    }
    */
}
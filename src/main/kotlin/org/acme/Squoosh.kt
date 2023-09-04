package org.acme

import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.time.measureTime

enum class ImageType {
    jpeg, png, webp
}

data class ConvertOptions(val type: ImageType,
                          val resizeWidth: Int? = null,
                          val resizeHeight: Int? = null,
                          val quality: Int? = null)

class Squoosh {
    fun convert(filepath: String, suffix: String, outputDirectory: String, options: ConvertOptions) {
        // squoosh-cli -d output input/cbbca148-6b69-492f-8bfa-70346ebc7a41.jpeg --webp "{}"
        // squoosh-cli -d output input/cbbca148-6b69-492f-8bfa-70346ebc7a41.jpeg --mozjpeg "{}"
        // squoosh-cli -d output input/cbbca148-6b69-492f-8bfa-70346ebc7a41.jpeg --mozjpeg "{}" --resize '{width:200}'

        val method = when (options.type) {
            ImageType.jpeg -> "mozjpeg"
            ImageType.png -> "oxipng"
            ImageType.webp -> "webp"
        }

        val resize = if (options.resizeWidth != null && options.resizeHeight != null) {
            "{width:${options.resizeWidth}, height:${options.resizeHeight}}"
        } else if (options.resizeWidth != null) {
            "{width:${options.resizeWidth}}"
        } else if (options.resizeHeight != null) {
            "{height:${options.resizeHeight}}"
        } else {
            null
        }

        runCommand(listOf<String?>(
                "squoosh-cli", filepath,
                "--suffix", suffix,
                "-d", outputDirectory,
                "--$method", "{${if (options.quality != null) "quality:${options.quality}" else ""} }",
                if (resize != null) "--resize" else null,
                resize,
        ).filterNotNull());
    }

    fun runCommand(command: List<String>) {
        println(command);

        try {
            val time = measureTime {
                // Create a process builder for the specified command
                val processBuilder = ProcessBuilder(command)

                // Start the process
                val process = processBuilder.start()

                // Read the output of the process
                val inputStream = process.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                var line: String?

                // Read and print the output line by line
                while (reader.readLine().also { line = it } != null) {
                    println(line)
                }

                val errorReader = BufferedReader(InputStreamReader(process.errorStream))

                // Read and print the output line by line
                while (errorReader.readLine().also { line = it } != null) {
                    println(line)
                }


                // Wait for the process to complete
                val exitCode = process.waitFor()
                println("Process exited with code $exitCode")
            }
            println("Process takes $time ms")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
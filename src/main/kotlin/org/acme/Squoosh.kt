package org.acme

import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.time.measureTime

enum class ImageType {
    jpeg, png, webp
}

class Squoosh {
    fun convert(filepath: String, outputDirectory: String, type: ImageType) {
        runCommand(listOf("squoosh-cli", filepath, "-d", outputDirectory, "--$type", "{ }"));
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
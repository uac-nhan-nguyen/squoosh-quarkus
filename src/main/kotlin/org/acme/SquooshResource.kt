package org.acme

import jakarta.ws.rs.*
import jakarta.ws.rs.core.*
import java.io.InputStream
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path as FilePath
import java.nio.file.StandardCopyOption


data class ImageResult(
        val webp: String,
        val jpg: String,
        val png: String,
)

fun createDirectoryIfNotExists(directory: String) {
    val path = FileSystems.getDefault().getPath(directory);
    if (!Files.exists(path)) {
        try {
            Files.createDirectories(path)
            println("Directory created: $path")
        } catch (e: Exception) {
            println("Error creating directory: ${e.message}")
        }
    } else {
        println("Directory already exists: $path")
    }
}

@Path("/squoosh")
class SquooshResource {
    val uploadDirectory = ".files/uploads"
    val webpDirectory = ".files/webp"
    val jpgDirectory = ".files/jpg"
    val pngDirectory = ".files/png"

    /**
     * POST method to accept image in body as binary
     * - create directory if not exists
     * - store it in a file
     * - return a JSON with the URL of the stored image
     */
    @PUT
    @Path("{filename}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MEDIA_TYPE_WILDCARD)
    fun store(filename: String, inputStream: InputStream, @Context urlInfo: UriInfo, headers: HttpHeaders): ImageResult {
        val baseUri = urlInfo.baseUri;

        // Define the path where you want to store the uploaded file
        val contentType = headers.getHeaderString("content-type")

        createDirectoryIfNotExists(uploadDirectory)

        val uploadPath = FilePath.of(uploadDirectory, filename)

        Files.copy(inputStream, uploadPath, StandardCopyOption.REPLACE_EXISTING)

        return ImageResult(
                "${baseUri}squoosh/$filename.webp",
                "${baseUri}squoosh/$filename.jpg",
                "${baseUri}squoosh/$filename.png",
        )
    }

    /**
     * GET method to return the uploaded image as jpeg
     */
    @GET
    @Path("{filename}")
    fun get(filename: String): Response {
        val path = FileSystems.getDefault().getPath(uploadDirectory, filename);

        if (Files.exists(path)) {
            return Response.ok(Files.newInputStream(path), "image/jpeg").build()
        } else {
            return Response.status(Response.Status.NOT_FOUND).build()
        }
    }


    /**
     * GET method to return the uploaded image as jpeg
     */
    @GET
    @Path("{filename}.{ext}")
    fun convert(
            filename: String, ext: String,
            @QueryParam("width") width: Int?,
            @QueryParam("height") height: Int?,
            @QueryParam("quality") quality: Int?,
    ): Response {
        val directory = when (ext) {
            "jpg" -> jpgDirectory
            "png" -> pngDirectory
            "webp" -> webpDirectory
            else -> uploadDirectory
        }

        val suffix = "${if (width != null) "-w=$width" else ""}${if (height != null) "-h=$height" else ""}${if (quality != null) "-q=$quality" else ""}"

        val convertedFilepath = FileSystems.getDefault().getPath(directory, "$filename$suffix.$ext");
        val contentType = when (ext) {
            "jpg" -> "image/jpeg"
            "png" -> "image/png"
            "webp" -> "image/webp"
            else -> "image/jpeg"
        }

        if (Files.exists(convertedFilepath)) {
            return Response.ok(Files.newInputStream(convertedFilepath), contentType).build()
        }


        val uploadPath = FileSystems.getDefault().getPath(uploadDirectory, filename);
        if (!Files.exists(uploadPath)) {
            return Response.status(Response.Status.NOT_FOUND).build()
        }

        /// convert image using squoosh cli
        val squoosh = Squoosh()
        createDirectoryIfNotExists(directory)
        squoosh.convert(uploadPath.toString(), suffix, directory, ConvertOptions(
                when (ext) {
                    "jpg" -> ImageType.jpeg
                    "png" -> ImageType.png
                    "webp" -> ImageType.webp
                    else -> ImageType.jpeg
                },
                resizeWidth = width,
        ));

        if (Files.exists(convertedFilepath)) {
            return Response.ok(Files.newInputStream(convertedFilepath), contentType).build()
        } else {
            throw Exception("Image not found after conversion");
        }
    }
}
package com.example.nhandienkhuanmat.domain.usecase

import android.graphics.Bitmap
import com.example.nhandienkhuanmat.data.model.User
import com.example.nhandienkhuanmat.data.repository.UserRepository
import com.example.nhandienkhuanmat.domain.service.FaceRecognitionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FaceRecognitionUseCase @Inject constructor(
    private val faceRecognitionService: FaceRecognitionService,
    private val userRepository: UserRepository
) {
    companion object {
        private const val SIMILARITY_THRESHOLD = 1.0f // Threshold for FaceNet L2 distance
    }

    suspend fun detectAndRecognizeFace(bitmap: Bitmap): FaceRecognitionResult {
        return withContext(Dispatchers.IO) {
            try {
                val faces = faceRecognitionService.detectFaces(bitmap)

                if (faces.isEmpty()) {
                    return@withContext FaceRecognitionResult.NoFaceDetected
                }

                val bestFace = faces.maxByOrNull { it.boundingBox.width() * it.boundingBox.height() }
                    ?: return@withContext FaceRecognitionResult.NoFaceDetected

                val faceBitmap = faceRecognitionService.cropFace(bitmap, bestFace.boundingBox)
                val embeddings = faceRecognitionService.extractFaceEmbeddings(faceBitmap)

                // Try to match with existing users
                val users = userRepository.getAllUsers().first()
                var bestMatch: Pair<User, Float>? = null

                for (user in users) {
                    user.faceEmbeddings?.let { storedEmbeddingsString ->
                        val storedEmbeddings = parseEmbeddings(storedEmbeddingsString)
                        val distance = faceRecognitionService.compareEmbeddings(embeddings, storedEmbeddings)

                        if (distance < SIMILARITY_THRESHOLD) {
                            if (bestMatch == null || distance < bestMatch!!.second) {
                                bestMatch = user to distance
                            }
                        }
                    }
                }

                if (bestMatch != null) {
                    FaceRecognitionResult.FaceRecognized(bestMatch!!.first, bestMatch!!.second)
                } else {
                    FaceRecognitionResult.FaceNotRecognized(embeddings)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                FaceRecognitionResult.Error(e.message ?: "Unknown error during recognition")
            }
        }
    }

    suspend fun registerFace(bitmap: Bitmap, userId: Long): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val faces = faceRecognitionService.detectFaces(bitmap)

                if (faces.isEmpty()) {
                    return@withContext false
                }

                val bestFace = faces.maxByOrNull { it.boundingBox.width() * it.boundingBox.height() }
                    ?: return@withContext false

                val faceBitmap = faceRecognitionService.cropFace(bitmap, bestFace.boundingBox)
                val embeddings = faceRecognitionService.extractFaceEmbeddings(faceBitmap)

                val user = userRepository.getUserById(userId) ?: return@withContext false
                val updatedUser = user.copy(faceEmbeddings = embeddingsToString(embeddings))
                userRepository.updateUser(updatedUser)

                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private fun parseEmbeddings(embeddingsString: String): FloatArray {
        if (embeddingsString.isEmpty()) return FloatArray(0)
        return embeddingsString.split(",").map { it.toFloat() }.toFloatArray()
    }

    private fun embeddingsToString(embeddings: FloatArray): String {
        return embeddings.joinToString(",")
    }
}

sealed class FaceRecognitionResult {
    object NoFaceDetected : FaceRecognitionResult()
    data class FaceRecognized(val user: User, val distance: Float) : FaceRecognitionResult()
    data class FaceNotRecognized(val embeddings: FloatArray) : FaceRecognitionResult()
    data class Error(val message: String) : FaceRecognitionResult()
} 
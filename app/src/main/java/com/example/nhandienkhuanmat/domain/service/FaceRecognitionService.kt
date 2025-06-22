package com.example.nhandienkhuanmat.domain.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.nio.ByteBuffer
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.reflect.KProperty

@Singleton
class FaceRecognitionService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val faceDetector: FaceDetector by lazy {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
            .setMinFaceSize(0.15f)
            .build()
        FaceDetection.getClient(options)
    }

    private val interpreter: Interpreter by lazy {
        val model = FileUtil.loadMappedFile(context, "facenet.tflite")
        Interpreter(model, Interpreter.Options().setNumThreads(4))
    }

    private val imageProcessor = ImageProcessor.Builder()
        .add(ResizeOp(112, 112, ResizeOp.ResizeMethod.BILINEAR))
        .add(NormalizeOp(0f, 255f))
        .build()

    suspend fun detectFaces(bitmap: Bitmap): List<Face> {
        return suspendCancellableCoroutine { continuation ->
            val image = InputImage.fromBitmap(bitmap, 0)
            faceDetector.process(image)
                .addOnSuccessListener { faces ->
                    continuation.resume(faces)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    fun extractFaceEmbeddings(faceBitmap: Bitmap): FloatArray {
        val tensorImage = TensorImage.fromBitmap(faceBitmap)
        val processedImage = imageProcessor.process(tensorImage)
        val buffer = processedImage.buffer
        
        val outputEmbeddings = Array(1) { FloatArray(128) }
        interpreter.run(buffer, outputEmbeddings)
        
        return outputEmbeddings[0]
    }

    fun compareEmbeddings(emb1: FloatArray, emb2: FloatArray): Float {
        var distance = 0.0f
        for (i in emb1.indices) {
            distance += (emb1[i] - emb2[i]).pow(2)
        }
        return sqrt(distance)
    }

    fun cropFace(bitmap: Bitmap, boundingBox: Rect): Bitmap {
        // Ensure the bounding box is within the bitmap dimensions
        val left = boundingBox.left.coerceAtLeast(0)
        val top = boundingBox.top.coerceAtLeast(0)
        val width = if (left + boundingBox.width() > bitmap.width) bitmap.width - left else boundingBox.width()
        val height = if (top + boundingBox.height() > bitmap.height) bitmap.height - top else boundingBox.height()

        return Bitmap.createBitmap(bitmap, left, top, width, height)
    }
}

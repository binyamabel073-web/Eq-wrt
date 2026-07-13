package com.example

import android.content.ContentValues
import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object EquationExporter {

    /**
     * Renders the equation into a high-quality styled card PNG and returns its Uri for sharing.
     */
    fun exportToPng(
        context: Context,
        text: String,
        theme: EquationTheme,
        isSerif: Boolean,
        scale: Float
    ): Uri? {
        val width = 1200
        val height = 600
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Draw Background Gradient
        val colors = IntArray(theme.bgGradient.size)
        for (i in theme.bgGradient.indices) {
            colors[i] = theme.bgGradient[i].toArgb()
        }
        val gradient = LinearGradient(
            0f, 0f, 0f, height.toFloat(),
            colors, null, Shader.TileMode.CLAMP
        )
        val bgPaint = Paint().apply {
            shader = gradient
            isAntiAlias = true
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // 2. Draw Decorative Border Frame
        val borderPaint = Paint().apply {
            color = theme.accentColor.toArgb()
            style = Paint.Style.STROKE
            strokeWidth = 4f
            alpha = 50 // semi-transparent
            isAntiAlias = true
        }
        val margin = 30f
        val rect = RectF(margin, margin, width - margin, height - margin)
        canvas.drawRoundRect(rect, 32f, 32f, borderPaint)

        // 3. Draw Theme Header Label
        val headerPaint = Paint().apply {
            color = theme.accentColor.toArgb()
            textSize = 24f
            typeface = Typeface.create(if (isSerif) Typeface.SERIF else Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText(theme.displayName.uppercase(), 60f, 80f, headerPaint)

        // 4. Draw Main Equation Text Centered
        val baseTextSize = 48f
        val textPaint = Paint().apply {
            color = theme.textColor.toArgb()
            textSize = baseTextSize * scale * 1.5f
            typeface = Typeface.create(if (isSerif) Typeface.SERIF else Typeface.DEFAULT, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        // Handle multi-line wrapping if equation is extremely long
        val x = width / 2f
        val y = height / 2f - (textPaint.descent() + textPaint.ascent()) / 2f
        canvas.drawText(text, x, y, textPaint)

        // 5. Draw Footer Watermark
        val footerPaint = Paint().apply {
            color = theme.textColor.toArgb()
            textSize = 20f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC)
            alpha = 100 // semi-transparent
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("Formatted with MathArt", width / 2f, height - 70f, footerPaint)

        return saveBitmapToStorage(context, bitmap, "Equation_${System.currentTimeMillis()}")
    }

    /**
     * Generates a beautifully formatted vector SVG string.
     */
    fun generateSvg(
        text: String,
        theme: EquationTheme,
        isSerif: Boolean,
        scale: Float
    ): String {
        val hexBgStart = String.format("#%06X", 0xFFFFFF and theme.bgGradient.first().toArgb())
        val hexBgEnd = String.format("#%06X", 0xFFFFFF and theme.bgGradient.last().toArgb())
        val hexText = String.format("#%06X", 0xFFFFFF and theme.textColor.toArgb())
        val hexAccent = String.format("#%06X", 0xFFFFFF and theme.accentColor.toArgb())

        val fontFamily = if (isSerif) "Georgia, serif" else "system-ui, sans-serif"
        val fontSize = (40 * scale).toInt()

        return """
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1000 500" width="100%" height="100%">
              <defs>
                <linearGradient id="mathArtGrad" x1="0%" y1="0%" x2="0%" y2="100%">
                  <stop offset="0%" stop-color="$hexBgStart" />
                  <stop offset="100%" stop-color="$hexBgEnd" />
                </linearGradient>
                <style>
                  .header { font-family: $fontFamily; font-size: 20px; fill: $hexAccent; font-weight: bold; letter-spacing: 2px; }
                  .equation { font-family: $fontFamily; font-size: ${fontSize}px; fill: $hexText; text-anchor: middle; font-weight: 500; }
                  .watermark { font-family: sans-serif; font-size: 16px; fill: $hexText; fill-opacity: 0.4; text-anchor: middle; font-style: italic; }
                  .border-frame { stroke: $hexAccent; stroke-width: 3; stroke-opacity: 0.2; fill: none; }
                </style>
              </defs>
              
              <!-- Card Background -->
              <rect width="1000" height="500" rx="32" fill="url(#mathArtGrad)" />
              
              <!-- Decorative Frame -->
              <rect x="25" y="25" width="950" height="450" rx="24" class="border-frame" />
              
              <!-- Header Label -->
              <text x="60" y="80" class="header">${theme.displayName.uppercase()}</text>
              
              <!-- Main Mathematical Equation -->
              <text x="500" y="260" class="equation">$text</text>
              
              <!-- Footer Watermark -->
              <text x="500" y="440" class="watermark">Formatted with MathArt</text>
            </svg>
        """.trimIndent()
    }

    /**
     * Saves SVG content to storage and returns its share Uri.
     */
    fun exportToSvg(context: Context, svgContent: String): Uri? {
        val fileName = "Equation_${System.currentTimeMillis()}.svg"
        val cacheFile = File(context.cacheDir, fileName)
        try {
            FileOutputStream(cacheFile).use { fos ->
                fos.write(svgContent.toByteArray())
            }
            return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", cacheFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Saves LaTeX code as a .tex file and returns its share Uri.
     */
    fun exportToTex(context: Context, latexCode: String): Uri? {
        val fileName = "Equation_${System.currentTimeMillis()}.tex"
        val cacheFile = File(context.cacheDir, fileName)
        val formattedTex = """
            \documentclass{article}
            \usepackage{amsmath}
            \usepackage{amsfonts}
            \usepackage{amssymb}
            \begin{document}
            
            \[
            $latexCode
            \]
            
            \end{document}
        """.trimIndent()

        try {
            FileOutputStream(cacheFile).use { fos ->
                fos.write(formattedTex.toByteArray())
            }
            return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", cacheFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun saveBitmapToStorage(context: Context, bitmap: Bitmap, name: String): Uri? {
        var outputStream: OutputStream? = null
        var fileUri: Uri? = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "$name.png")
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MathArt")
                }
                val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (imageUri != null) {
                    outputStream = resolver.openOutputStream(imageUri)
                    fileUri = imageUri
                }
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
                val mathArtDir = File(imagesDir, "MathArt")
                if (!mathArtDir.exists()) {
                    mathArtDir.mkdirs()
                }
                val file = File(mathArtDir, "$name.png")
                outputStream = FileOutputStream(file)
                fileUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            }

            outputStream?.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                // Also save a copy to the cache directory so it can be easily shared via share sheet
                val cacheFile = File(context.cacheDir, "$name.png")
                FileOutputStream(cacheFile).use { fos ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                }
                // Return cache Uri for share sheet since MediaStore Uris sometimes need strict permissions on other apps
                fileUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", cacheFile)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return fileUri
    }

    // Helper extension to convert Compose Color to Android Color
    private fun androidx.compose.ui.graphics.Color.toArgb(): Int {
        return (this.value shr 32).toInt()
    }
}

package com.example.mygallery.ui.screens.ImageEditorScreen

import androidx.compose.ui.graphics.ColorMatrix

sealed class ImageFilter(val name: String, val matrix: ColorMatrix) {

    object None : ImageFilter("Original", ColorMatrix())

    object Grayscale : ImageFilter("Grayscale", ColorMatrix(floatArrayOf(
        0.299f, 0.587f, 0.114f, 0f, 0f,
        0.299f, 0.587f, 0.114f, 0f, 0f,
        0.299f, 0.587f, 0.114f, 0f, 0f,
        0f,      0f,      0f,    1f, 0f
    )))

    object Sepia : ImageFilter("Sepia", ColorMatrix(floatArrayOf(
        0.393f, 0.769f, 0.189f, 0f, 0f,
        0.349f, 0.686f, 0.168f, 0f, 0f,
        0.272f, 0.534f, 0.131f, 0f, 0f,
        0f,      0f,      0f,    1f, 0f
    )))

    object Invert : ImageFilter("Invert", ColorMatrix(floatArrayOf(
        -1f,  0f,  0f, 0f, 255f,
        0f, -1f,  0f, 0f, 255f,
        0f,  0f, -1f, 0f, 255f,
        0f,  0f,  0f, 1f,   0f
    )))

    // Vintage/Retro filter (warm tones)
    object Vintage : ImageFilter("Vintage", ColorMatrix(floatArrayOf(
        1.0f, 0.1f, 0.1f, 0f, 0f,
        0.0f, 0.9f, 0.0f, 0f, 0f,
        0.0f, 0.0f, 0.8f, 0f, 0f,
        0f,   0f,   0f,   1f, 0f
    )))

    // Cool blue-tinted filter
    object Cool : ImageFilter("Cool", ColorMatrix(floatArrayOf(
        0.9f, 0f, 0f, 0f, 0f,
        0f, 1.0f, 0f, 0f, 0f,
        0f, 0f, 1.2f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )))

    // Red highlight filter
    object RedHighlight : ImageFilter("Red Highlight", ColorMatrix(floatArrayOf(
        1.5f, 0f, 0f, 0f, 0f,
        0f, 0.8f, 0f, 0f, 0f,
        0f, 0f, 0.8f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )))

    // 𝗪𝗮𝗿𝗺 𝗩𝗶𝗻𝘁𝗮𝗴𝗲 (amber tones)
    object WarmVintage : ImageFilter("Warm Vintage", ColorMatrix(floatArrayOf(
        1.2f,  0.1f,  0.0f, 0f, 0f,
        0.1f,  1.0f,  0.0f, 0f, 0f,
        0.0f,  0.0f,  0.8f, 0f, 0f,
        0f,     0f,    0f,   1f, 0f
    )))

    // 𝗡𝗲𝗼𝗻 𝗚𝗹𝗼𝘄 (cyan/magenta highlights)
    object NeonGlow : ImageFilter("Neon Glow", ColorMatrix(floatArrayOf(
        0.7f,  0.0f,  0.7f, 0f, 0f,    // Boost red/blue for magenta
        0.0f,  1.2f,  0.0f, 0f, 0f,    // Boost green
        0.7f,  0.0f,  0.7f, 0f, 0f,    // Boost red/blue again
        0f,     0f,    0f,   1f, 0f
    )))

    // 𝗖𝗶𝗻𝗲𝗺𝗮𝘁𝗶𝗰 𝗧𝗲𝗮𝗹 (ocean-inspired shadows)
    object CinematicTeal : ImageFilter("Cinematic Teal", ColorMatrix(floatArrayOf(
        0.8f,  0.0f,  0.0f, 0f, 0f,
        0.0f,  1.2f,  0.2f, 0f, 0f,    // Boost green/blue
        0.0f,  0.2f,  1.2f, 0f, 0f,
        0f,     0f,    0f,   1f, 0f
    )))

    // 𝗣𝗮𝘀𝘁𝗲𝗹 𝗗𝗿𝗲𝗮𝗺 (soft washed-out colors)
    object Pastel : ImageFilter("Pastel", ColorMatrix(floatArrayOf(
        0.8f,  0.2f,  0.2f, 0f, 0.1f, // Reduce contrast, add white tint
        0.2f,  0.8f,  0.2f, 0f, 0.1f,
        0.2f,  0.2f,  0.8f, 0f, 0.1f,
        0f,     0f,    0f,   1f, 0f
    )))

    // 𝗗𝘂𝗼𝘁𝗼𝗻𝗲 𝗣𝘂𝗿𝗽𝗹𝗲 (grayscale + purple tint)
    object DuotonePurple : ImageFilter("Duotone Purple", ColorMatrix(floatArrayOf(
        0.3f, 0.587f, 0.114f, 0f, 0f, // Grayscale mapped to red/blue
        0f,    0f,      0f,    0f, 0f, // Remove green
        0.3f, 0.587f, 0.114f, 0f, 0f, // Grayscale mapped to blue
        0f,     0f,      0f,    1f, 0f
    )))

    // 𝗖𝘆𝗯𝗲𝗿𝗽𝘂𝗻𝗸 (high-contrast neon)
    object Cyberpunk : ImageFilter("Cyberpunk", ColorMatrix(floatArrayOf(
        1.5f,  0.0f,  0.0f, 0f, -50f,  // Intense reds
        0.0f,  1.2f,  0.0f, 0f, -30f,  // Bright greens
        0.0f,  0.0f,  1.5f, 0f, -50f,  // Deep blues
        0f,     0f,    0f,   1f,  0f
    )))

    // 𝗕𝗹𝗮𝗰𝗸 & 𝗪𝗵𝗶𝘁𝗲 𝗛𝗶𝗴𝗵 𝗖𝗼𝗻𝘁𝗿𝗮𝘀𝘁
    object HighContrastBW : ImageFilter("B&W Contrast", ColorMatrix(floatArrayOf(
        1.5f, 1.5f, 1.5f, 0f, -100f, // Crush shadows
        1.5f, 1.5f, 1.5f, 0f, -100f,
        1.5f, 1.5f, 1.5f, 0f, -100f,
        0f,    0f,    0f,  1f,  0f
    )))

    // Brightness adjustment (e.g., 0.5 = darker, 1.5 = brighter)
    class Brightness(intensity: Float) : ImageFilter(
        "Brightness",
        ColorMatrix(floatArrayOf(
            intensity, 0f, 0f, 0f, 0f,
            0f, intensity, 0f, 0f, 0f,
            0f, 0f, intensity, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        ))
    )

    // Contrast adjustment (e.g., 0.5 = lower contrast, 2.0 = higher)
    class Contrast(intensity: Float) : ImageFilter(
        "Contrast",
        ColorMatrix(floatArrayOf(
            intensity, 0f, 0f, 0f, (1 - intensity) * 128,
            0f, intensity, 0f, 0f, (1 - intensity) * 128,
            0f, 0f, intensity, 0f, (1 - intensity) * 128,
            0f, 0f, 0f, 1f, 0f
        ))
    )

    // Saturation adjustment (0 = grayscale, 1 = normal, 2 = oversaturated)
  /*  class Saturation(intensity: Float) : ImageFilter(
        "Saturation",
        ColorMatrix().apply {
            setSaturation(intensity)
        }
    )*/

}
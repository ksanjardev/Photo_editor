package uz.sanjar.photoeditor.adapter

import android.opengl.GLES20
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter.NO_FILTER_FRAGMENT_SHADER


// Custom vertex shader for horizontal flip
private const val VERTEX_SHADER_FLIP_HORIZONTAL = """
    attribute vec4 position;
    attribute vec4 inputTextureCoordinate;
    varying vec2 textureCoordinate;
    void main() {
        gl_Position = position;
        // Flip the x coordinate
        textureCoordinate = vec2(1.0 - inputTextureCoordinate.x, inputTextureCoordinate.y);
    }
"""

// Custom vertex shader for vertical flip
private const val VERTEX_SHADER_FLIP_VERTICAL = """
    attribute vec4 position;
    attribute vec4 inputTextureCoordinate;
    varying vec2 textureCoordinate;
    void main() {
        gl_Position = position;
        // Flip the y coordinate
        textureCoordinate = vec2(inputTextureCoordinate.x, 1.0 - inputTextureCoordinate.y);
    }
"""

// Custom vertex shader for rotation
// The shader rotates by an angle (in radians) passed as a uniform named "angle"
private const val VERTEX_SHADER_ROTATE = """
    attribute vec4 position;
    attribute vec4 inputTextureCoordinate;
    uniform float angle;
    varying vec2 textureCoordinate;
    void main() {
        gl_Position = position;
        // Translate texture coordinates so that the center is at (0.5, 0.5)
        vec2 centered = inputTextureCoordinate.xy - vec2(0.5, 0.5);
        float s = sin(angle);
        float c = cos(angle);
        // Apply rotation
        vec2 rotated = vec2(centered.x * c - centered.y * s, centered.x * s + centered.y * c);
        // Translate back to the [0,1] coordinate space
        textureCoordinate = rotated + vec2(0.5, 0.5);
    }
"""

private const val SEPIA_FRAGMENT_SHADER = """
    varying highp vec2 textureCoordinate;
    uniform sampler2D inputImageTexture;
    
    void main() {
        highp vec4 color = texture2D(inputImageTexture, textureCoordinate);
        highp float r = color.r;
        highp float g = color.g;
        highp float b = color.b;
        gl_FragColor = vec4(
            (r * 0.393) + (g * 0.769) + (b * 0.189),
            (r * 0.349) + (g * 0.686) + (b * 0.168),
            (r * 0.272) + (g * 0.534) + (b * 0.131),
            color.a
        );
    }
"""

private const val AUTO_FIX_FRAGMENT_SHADER = """
    varying highp vec2 textureCoordinate;
    uniform sampler2D inputImageTexture;
    
    void main() {
        highp vec4 color = texture2D(inputImageTexture, textureCoordinate);
        // A very basic 'auto-fix' by adjusting contrast and brightness.
        highp vec3 enhancedColor = (color.rgb - 0.5) * 1.2 + 0.5;
        gl_FragColor = vec4(enhancedColor, color.a);
    }
"""
val DEFAULT_FRAGMENT_SHADER = NO_FILTER_FRAGMENT_SHADER

class CustomAutoEnhanceFilter : GPUImageFilter(NO_FILTER_VERTEX_SHADER, AUTO_FIX_FRAGMENT_SHADER)

class CustomSepiaFilter : GPUImageFilter(NO_FILTER_VERTEX_SHADER, SEPIA_FRAGMENT_SHADER)

class GPUImageFlipHorizontalFilter :
    GPUImageFilter(VERTEX_SHADER_FLIP_HORIZONTAL, NO_FILTER_FRAGMENT_SHADER)

// Flip Vertical Filter using the custom vertex shader for vertical flipping
class GPUImageFlipVerticalFilter :
    GPUImageFilter(VERTEX_SHADER_FLIP_VERTICAL, NO_FILTER_FRAGMENT_SHADER)

// Rotate Filter (rotates by a specified angle in radians)
class GPUImageRotateFilter(private var angle: Float) :
    GPUImageFilter(VERTEX_SHADER_ROTATE, NO_FILTER_FRAGMENT_SHADER) {

    // Will store the location of the 'angle' uniform
    private var angleLocation: Int = -1

    override fun onInit() {
        super.onInit()
        // Retrieve the uniform location for "angle" from the shader program
        angleLocation = GLES20.glGetUniformLocation(program, "angle")
        // Set the uniform using the location (note: setFloat expects an int location)
        setFloat(angleLocation, angle)
    }

    // Optionally, allow updating the angle dynamically
    fun setAngle(newAngle: Float) {
        angle = newAngle
        setFloat(angleLocation, newAngle)
    }
}
// Add to shader constants
private const val GRAIN_FRAGMENT_SHADER = """
varying highp vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
uniform highp float grainIntensity;

highp float rand(highp vec2 co) {
    return fract(sin(dot(co.xy, vec2(12.9898, 78.233))) * 43758.5453);
}

void main() {
    highp vec4 color = texture2D(inputImageTexture, textureCoordinate);
    highp float noise = rand(textureCoordinate) * grainIntensity;
    gl_FragColor = vec4(color.rgb + noise, color.a);
}
"""

// Custom Grain Filter Class
class GPUImageGrainFilter(private var intensity: Float = 0.1f) :
    GPUImageFilter(NO_FILTER_VERTEX_SHADER, GRAIN_FRAGMENT_SHADER) {
    private var intensityLocation: Int = -1

    override fun onInit() {
        super.onInit()
        intensityLocation = GLES20.glGetUniformLocation(program, "grainIntensity")
    }

    override fun onInitialized() {
        super.onInitialized()
        setFloat(intensityLocation, intensity)
    }
}
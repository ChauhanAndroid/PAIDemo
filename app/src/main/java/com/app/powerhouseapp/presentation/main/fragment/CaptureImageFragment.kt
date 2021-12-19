package com.app.powerhouseapp.presentation.main.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.app.powerhouseapp.R
import com.app.powerhouseapp.databinding.FragmentMainBinding
import com.app.powerhouseapp.domain.utils.Constants.Companion.FILENAME_FORMAT
import com.app.powerhouseapp.domain.utils.Constants.Companion.PHOTO_EXTENSION
import com.app.powerhouseapp.presentation.main.viewmodel.MainViewModel
import com.app.powerhouseapp.presentation.utils.autoCleared
import com.google.ar.core.ArCoreApk
import com.google.ar.core.ArCoreApk.*
import com.google.ar.core.Config
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import com.google.ar.core.Session
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.core.exceptions.NotYetAvailableException
import com.google.ar.core.exceptions.UnavailableException
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


@AndroidEntryPoint
class CaptureImageFragment : Fragment(), GLSurfaceView.Renderer {

    companion object {
        const val TAG = "CaptureFragment"
        private const val PERMISSIONS_REQUEST_CODE = 10
        private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)
        fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private val viewModel: MainViewModel by viewModels()
    private var captureFragmentBinding: FragmentMainBinding by autoCleared()
    private lateinit var safeContext: Context

    private lateinit var viewFinder: PreviewView
    private lateinit var btnAction: AppCompatButton

    private lateinit var outputDirectory: File
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        safeContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        captureFragmentBinding = FragmentMainBinding.inflate(inflater, container, false)
        return captureFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    /**
     * This function initialize views and output factory
     */
    private fun init() {
        viewFinder = captureFragmentBinding.previewCamera
        btnAction = captureFragmentBinding.btnAction
        btnAction.run {
            setOnClickListener {
                takePhoto()
            }
        }
        callConfigApi()
        outputDirectory = getOutputDirectory()
    }

    /**
     * Invoke configure function in view model, subscribe observers
     */
    private fun callConfigApi(){
        viewModel.getConfig()
        subscribeObservers()
    }

    /**
     * Subscribe to breeds observable, notified when the state of data object changes
     */
    private fun subscribeObservers() {
        viewModel.responseConfig.observe(viewLifecycleOwner, { result ->
            if (result.isLoading) {
               Toast.makeText(requireContext(),"Configuring...",Toast.LENGTH_SHORT).show()
            } else if (result.success.isNotEmpty()) {
                permissionChecksAndStart()
            } else {

            }
        })
    }

    /**
     * Initialize CameraX, and prepare to bind the camera use cases
     */
    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            // CameraProvider
            cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    /**
     * Declare and bind preview, capture and analysis use cases
     */
    private fun bindCameraUseCases() {
        val screenAspectRatio = AspectRatio.RATIO_16_9
        val rotation = Surface.ROTATION_0

        // CameraProvider
        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")

        // CameraSelector
        val cameraSelector = CameraSelector.Builder().build()

        // Preview
        preview = Preview.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .build()

        // ImageCapture
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .build()

        // ImageAnalysis
        imageAnalyzer = ImageAnalysis.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .build()

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageCapture, imageAnalyzer
            )
            preview?.setSurfaceProvider(viewFinder.surfaceProvider)
            btnAction.text = getString(R.string.capture_label)
        } catch (exc: Exception) {
            Log.e(TAG, "bindingCameraXFailed", exc)
        }
    }


    private fun getOutputDirectory(): File {
        val mediaDir = activity?.externalMediaDirs?.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else activity?.filesDir!!
    }


    /**
     * Function to take picture and provide saved Uri
     */
    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT,
                Locale.US
            ).format(System.currentTimeMillis()) + PHOTO_EXTENSION
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(safeContext),
            object : ImageCapture.OnImageSavedCallback {

                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "FailedTakePhoto: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val fileUri = Uri.fromFile(photoFile)
                    val file: File? = com.app.powerhouseapp.presentation.utils.FileUtils.getFileFromUri(requireContext(),fileUri)
                    Log.e(TAG, "CapturedUri==> $fileUri")
                    Log.e(TAG, "file==> $file")
                    viewModel.uploadImage(file)
                }
            })
    }

    /**
     * This function checks permissions for camera and request if required else launch cameraX
     */
    private fun permissionChecksAndStart() {
        if (!hasPermissions(requireContext())) {
            requestPermissions(
                PERMISSIONS_REQUIRED,
                PERMISSIONS_REQUEST_CODE
            )
        } else
            setUpCamera()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (PackageManager.PERMISSION_GRANTED == grantResults.firstOrNull()) {
                Toast.makeText(context, getString(R.string.permission_granted), Toast.LENGTH_SHORT)
                    .show()
                setUpCamera()
            } else {
                Toast.makeText(context, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
            }
        }
    }


    private lateinit var session : Session

    fun depthInit(){
        session = Session(requireContext())

        val config = Config(session)

        val isDepthSupported = session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)
        if (isDepthSupported) {
            config.depthMode = Config.DepthMode.AUTOMATIC
        }
        session.configure(config)

        try {
            session.resume()
        } catch (e: CameraNotAvailableException) {
            Log.e("CameraNAException",e.localizedMessage)
            return
        }
    }

    // Verify that ARCore is installed and using the current version.
    fun isARCoreSupportedAndUpToDate(): Boolean {
        return when (getInstance().checkAvailability(requireContext())) {
            Availability.SUPPORTED_INSTALLED -> true
            Availability.SUPPORTED_APK_TOO_OLD, Availability.SUPPORTED_NOT_INSTALLED -> {
                try {
                    // Request ARCore installation or update if needed.
                    when (getInstance().requestInstall(requireActivity(), true)) {
                        InstallStatus.INSTALL_REQUESTED -> {
                            Log.i(TAG, "ARCore installation requested.")
                            false
                        }
                        InstallStatus.INSTALLED -> true
                    }
                } catch (e: UnavailableException) {
                    Log.e(TAG, "ARCore not installed", e)
                    false
                }
            }

            Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE ->
                // This device is not supported for AR.
                false
            else -> {false}
        }
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
    }

    override fun onDrawFrame(gl: GL10?) {

        // Retrieve the depth image for the current frame, if available.
        try {
            session.update().acquireRawDepthImage().use { depthImage ->
                // Use the depth image here.
                Log.e("depthImage",depthImage.toString())
            }
        } catch (e: NotYetAvailableException) {
            // This means that depth data is not available yet.
            // Depth data will not be available if there are no tracked
            // feature points. This can happen when there is no motion, or when the
            // camera loses its ability to track objects in the surrounding
            // environment.
        }
    }
}
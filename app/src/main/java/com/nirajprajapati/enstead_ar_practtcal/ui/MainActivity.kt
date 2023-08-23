package com.nirajprajapati.enstead_ar_practtcal.ui

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.lifecycle.lifecycleScope
import com.google.ar.core.HitResult
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.SceneView
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.gorisse.thomas.sceneform.scene.await
import com.nirajprajapati.enstead_ar_practtcal.R
import com.nirajprajapati.enstead_ar_practtcal.databinding.ActivityMainBinding
import com.nirajprajapati.enstead_ar_practtcal.helpers.GestureHelper
import com.nirajprajapati.enstead_ar_practtcal.utils.Const
import com.nirajprajapati.enstead_ar_practtcal.utils.Utils
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var arFragment: ArFragment
    private val arSceneView get() = arFragment.arSceneView
    private val scene get() = arSceneView.scene
    private var model: Renderable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        arFragment = supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment

        setupARFragment()
        loadModels()
    }

    private fun setupARFragment() {
        arFragment.apply {
            setOnSessionConfigurationListener { _, _ ->
                // Handle session configuration if needed
            }

            setOnViewCreatedListener { arSceneView ->
                arSceneView.setFrameRateFactor(SceneView.FrameRate.FULL)
            }

            setOnTapArPlaneListener { hitResult, _, _ ->
                if (model == null) {
                    Utils.showToast(
                        context = this@MainActivity,
                        message = getString(R.string.loading)
                    )
                    return@setOnTapArPlaneListener
                }

                handleTapOnARPlane(hitResult)
            }
        }
    }

    private fun handleTapOnARPlane(hitResult: HitResult) {
        val anchorNode = AnchorNode(hitResult.createAnchor())
        scene.addChild(anchorNode)

        val transformableNode = createTransformableNode()
        anchorNode.addChild(transformableNode)

        setupGestureDetection(transformableNode)
    }

    private fun createTransformableNode(): TransformableNode {
        return TransformableNode(arFragment.transformationSystem).apply {
            renderable = model
            renderableInstance.setCulling(false)
            renderableInstance.animate(true).start()

            addChild(Node().apply {
                localPosition = Vector3(0.0f, 1f, 0.0f)
                localScale = Vector3(0.7f, 0.7f, 0.7f)
            })
        }
    }

    private fun setupGestureDetection(transformableNode: TransformableNode) {
        val gestureHelper = GestureHelper()
        val gestureDetectorCompat = GestureDetectorCompat(this@MainActivity, gestureHelper)
        transformableNode.setOnTouchListener { _, motionEvent ->
            gestureDetectorCompat.onTouchEvent(motionEvent)

            when (gestureHelper.gestureType) {
                GestureHelper.GestureType.NONE, GestureHelper.GestureType.SINGLE_TAP -> {
                    // Handle actions if needed
                }

                GestureHelper.GestureType.DOUBLE_TAP, GestureHelper.GestureType.LONG_PRESS -> {
                    scene.removeChild(transformableNode.parentNode)
                }
            }
            true
        }
    }

    private fun loadModels() {
        lifecycleScope.launch {
            model = ModelRenderable.builder()
                .setSource(this@MainActivity, Uri.parse(Const.objectFileName))
                .setIsFilamentGltf(true)
                .await()
        }
    }
}
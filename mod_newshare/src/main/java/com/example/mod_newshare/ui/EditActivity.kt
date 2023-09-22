package com.example.mod_newshare.ui

import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.mod_newshare.adapter.UploadImagesAdapter
import com.example.mod_newshare.databinding.NewshareActivityEditBinding
import com.sll.lib_common.service.ServiceManager
import com.sll.lib_framework.base.activity.BaseMvvmActivity
import com.sll.lib_framework.ext.launchIO
import com.sll.lib_framework.ext.launchOnCreated
import com.sll.lib_framework.ext.launchOnStarted
import com.sll.lib_framework.ext.view.throttleClick
import com.sll.lib_framework.util.FileUtils
import com.sll.lib_framework.util.ToastUtils
import com.therouter.router.Route
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.URI

//import com.example.mod_newshare.ui
@Route(path = "/app/edit")
class EditActivity : BaseMvvmActivity<NewshareActivityEditBinding, EditViewModel>() {
    companion object {
        const val TAG = "EditActivity"

    }

    private lateinit var file: File
    private var FLAG = true
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var imageUri: Uri


    override fun onDefCreate(savedInstanceState: Bundle?) {
        initView()
    }

    override fun initViewBinding(container: ViewGroup?) =
        NewshareActivityEditBinding.inflate(layoutInflater)

    override fun getViewModelClass() = EditViewModel::class

    fun initView() {
        init()
        /**
         * 添加图片
         */
        binding.addImg.setOnClickListener {
            checkStoragePermission()
        }
        /**
         * 返回
         */
        binding.returnImg.setOnClickListener {
            if (!binding.contentEd.text.isEmpty() || !binding.titleEd.text.isEmpty() || !viewModel.uriList.value?.isEmpty()!!) {
                //启动bottomSheetDialogFragment
                val returnFragment = ReturnFragment()
                val bundle = Bundle()
                returnFragment.arguments = bundle
                returnFragment.show(supportFragmentManager, "return_fragment")
                returnFragment.setAction(object : ReturnFragment.onDialogAction {
                    /**
                     * 点击保存后先上传图片，并通知下面调用saveShare
                     */
                    override fun saveDragt() {
                        Log.d(TAG, "saveDragt: clicked")
                        if (viewModel.uriList.value?.isEmpty() == true) {
                            ToastUtils.error("还未上传图片，无法保存")
                        } else if (binding.titleEd.text.isEmpty()) {
                            alterDialog("标题不能为空")
                        } else if (binding.contentEd.text.isEmpty()){
                            alterDialog("内容不能为空")
                        } else {
                            // 首先是上传图片
                            launchIO {
                                viewModel.uploadImages(viewModel.uriList.value!!)
                                this@EditActivity.FLAG = false
                            }
                        }
                    }
                })
            } else {
                finish()
            }

        }
        /**
         * 发布功能
         */
        binding.publish.throttleClick {
            //至少上传一张照片
            if (viewModel.uriList.value?.isEmpty() == true) {
                ToastUtils.error("请上传图片")
            } else if (binding.titleEd.text.isEmpty()) {
                alterDialog("标题不能为空")
            } else if (binding.contentEd.text.isEmpty()){
                alterDialog("内容不能为空")
            }else {
                // 首先是上传图片
                launchIO {
                    viewModel.uploadImages(viewModel.uriList.value!!)
                }
            }
        }

        launchOnCreated {
            //监听上传图片的结果
            launch {
                // 上传图片结束后发布
                viewModel.uploadState.collect { res ->
                    val title = binding.titleEd.text.toString()
                    val content = binding.contentEd.text.toString()
                    var imageCode: Long
                    Log.d(TAG, "beginCollectUpload : $res")
                    Log.d(TAG, "titleContent: " + title + " " + content)
                    res?.onSuccess { imageSet ->
                        imageCode = imageSet!!.imageCode
                        Log.d(TAG, "imageCode: $imageCode")
                        if (FLAG) {
                            viewModel.newShare(
                                title,
                                content,
                                imageCode,
                                ServiceManager.userService.getUserInfo()!!.id!!
                            )
                        } else {
                            viewModel.saveShare(
                                title,
                                content,
                                imageCode,
                                ServiceManager.userService.getUserInfo()!!.id!!
                            )
                            FLAG = true
                        }
                    }
                }
            }
            //监听新增图文的结果
            launch {
                viewModel.newShareState.collect { res ->
                    Log.d(TAG, "newShareState: $res")
                    res?.onSuccess {
                        ToastUtils.success("发布成功")
                        finish()
                    }
                }
            }
            //监听保存图文分享的结果
            launch {
                viewModel.saveShare.collect { res ->
                    Log.d(TAG, "saveShare: $res")
                    res?.onSuccess {
                        ToastUtils.success("保存成功")
                        finish()
                    }
                }
            }
        }

        updateRecycleView()
    }

    fun updateRecycleView() {
        launchOnStarted {
            //Uri列表变化时，提醒Recycleview更新
            launch {
                viewModel.uriList.collect {
                    //adapter
                    val adapter = UploadImagesAdapter(it!!)
                    adapter.setAction(object : UploadImagesAdapter.onAdapterAction {
                        override fun delete(index: Int) {
                            Log.d(TAG, "delete: recycleViewDeleteIndex = $index")
                            viewModel.removeUri(index)
                        }
                    })
                    //manager
                    val manager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
//                    val manager= StaggeredGridLayoutManager(3,LinearLayoutManager.VERTICAL)
                    binding.recycle.adapter = adapter
                    binding.recycle.layoutManager = manager
                    Log.d(TAG, "onDefCreate: recycleView")
                }
            }
        }

    }

    private fun checkStoragePermission() {
        val permission = android.Manifest.permission.READ_EXTERNAL_STORAGE
        val context = com.therouter.getApplicationContext()
        if (ContextCompat.checkSelfPermission(
                context!!,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // 已经有权限，可以执行需要权限的操作，比如打开图库
            openGallery()
//            getImage()
        } else {
            // 请求权限
            requestPermissionLauncher.launch(permission)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }

    fun init() {
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    // 权限已被授予，打开图库
                    openGallery()
//                    getImage()
                } else {
                    // 权限被拒绝
                    // 这里可以向用户解释为什么需要存储权限
                }
            }
        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    // 图片选择成功
                    val selectedImageUri = result.data?.data
                    viewModel.addUri(selectedImageUri!!)
                    // 在这里处理选择的图片
                    if (selectedImageUri != null) {
                        // 这里可以使用选定的图片URI
                    }
                }
            }
    }

    fun getImage() {
        FileUtils.PhotoRequest.with(this)
            .pick()
            .crop(FileUtils.PhotoRequest.CropOptions().aspect(1, 1).scale().ouput(500, 500))
            .build { uri, state ->
                Log.d(TAG, "Uri: $uri")
                Log.i("EditActivity", "build photo: ${uri?.toFile()?.path}, ${state}")
                if (state == FileUtils.PhotoRequest.State.SUCCESS) {
                    Log.d("success_photo", uri.toString())
                    viewModel.addUri(uri!!)
                }
                if (state == FileUtils.PhotoRequest.State.ERROR_CROP) {
                    Log.d("e_photo", uri.toString())
                }
            }
    }

    fun alterDialog(msg: String) {
        AlertDialog.Builder(this@EditActivity)
            .setMessage(msg)
            .setTitle("报错")
            .setPositiveButton("取消", null)
            .show()
    }
}

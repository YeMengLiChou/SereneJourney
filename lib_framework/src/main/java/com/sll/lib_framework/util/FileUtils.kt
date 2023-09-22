package com.sll.lib_framework.util

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.math.BigDecimal
import java.math.RoundingMode

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/17
 */
object FileUtils {
    private const val TAG = "FileUtils"

    // 包名
    private var PACKAGE_NAME = "com.sll.serenejourney"

    // 媒体模块根目录
    private val DIR_SAVE_MEDIA_ROOT = Environment.DIRECTORY_PICTURES

    // 媒体模块存储路径
    private val DIR_SAVE_MEDIA: String = "$DIR_SAVE_MEDIA_ROOT/serenejourney"


    // 头像保存路径
    private const val DIR_AVATAR = "/avatar"

    // 图片缓存等保存路径
    private const val DIR_IMAGE = "/image"

    // JPG后缀
    const val SUFFIX_JPG = ".jpg"

    // PNG后缀
    const val SUFFIX_PNG = ".png"

    // MP4后缀
    const val SUFFIX_MP4 = ".mp4"

    // YUV后缀
    const val SUFFIX_YUV = ".yuv"

    // h264后缀
    const val SUFFIX_H264 = ".h264"


    val DEFAULT_SAVE_PATH = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}${File.separator}Save"


    /**
     * 返回 File 对应的
     * @param context
     * @param file
     * */
    // 未设置 FileProvider
//    fun getUriFromFile(context: Context, file: File): Uri {
//        // 转换为uri
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            // 适配Android 7.0文件权限，通过FileProvider创建一个content类型的Uri
//            FileProvider.getUriForFile(
//                context,
//                "$PACKAGE_NAME.fileProvider",
//                file
//            )
//        } else {
//            Uri.fromFile(file)
//        }
//    }
//
//    /**
//     * 返回 [path] 对应的 Uri
//     * */
//    fun getUriFromPath(context: Context, path: String?): Uri? {
//        return path?.let { getUriFromFile(context, File(it)) }
//    }


    /**
     * 选择一个外部文件
     * @return intent
     * */
    fun pickExternalFile(): Intent {
        return Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            Intent.createChooser(this, "选择文件")
        }
    }

    /**
     * 将外部文件的 uri 复制到私有目录
     *
     * Android 11开始只能在私有目录或者共享目录创建文件
     * @param context
     * @param srcUri 源文件的uri
     * @param dstFolder 私有目录的文件夹名称
     * */
    fun copyUriToInnerStorage(context: Context, srcUri: Uri, dstFolder: String, dstFile: String): File {
        val folder = File(dstFolder)
        val exist = if (!folder.exists()) folder.mkdirs() else true
        val file = File(dstFolder, dstFile)
        if (exist) {
            if (file.exists()) file.delete()
            context.contentResolver.openInputStream(srcUri).use {
                try {
                    val fileOutputStream = FileOutputStream(file)
                    val buffer = ByteArray(1024)
                    var readCount = it?.read(buffer) ?: -1
                    while (readCount >= 0) {
                        fileOutputStream.write(buffer, 0, readCount)
                        readCount = it?.read(buffer) ?: -1
                    }
                    fileOutputStream.flush()
                    fileOutputStream.fd.sync()
                    fileOutputStream.close()
                } catch (e: IOException) {
                    Log.e(TAG, "copyUriToInnerStorage: $e")
                }
            }
        }
        return file
    }


    // =========================== 文件大小相关 ============================

    // 获取文件大小
    // Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/
    // 目录，一般放一些长时间保存的数据
    // Context.getExternalCacheDir() -->
    // SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据

    /**
     * 获取指定文件夹/文件的大小
     * @param file
     * @return 文件大小
     * */
    fun getFileSize(file: File): Long {
        var size: Long = 0
        try {
            val fileList = file.listFiles()
            val size2: Int
            if (fileList != null) {
                size2 = fileList.size
                for (i in 0 until size2) {
                    // 如果下面还有文件
                    size = if (fileList[i].isDirectory) {
                        size + getFileSize(fileList[i])
                    } else {
                        size + fileList[i].length()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }


    /**
     * 格式化单位，保留两位小数的最大单位
     *
     * @param size
     * @return
     */
    fun getFormatSizeString(size: Double): String {
        val kiloByte = size / 1024
        if (kiloByte < 1) {
            return if (kiloByte == 0.0) {
                "0KB"
            } else size.toString() + "KB"
        }
        val megaByte = kiloByte / 1024
        if (megaByte < 1) {
            val result1 = BigDecimal(kiloByte.toString())
            return result1.setScale(2, RoundingMode.HALF_UP).toPlainString() + "KB"
        }
        val gigaByte = megaByte / 1024
        if (gigaByte < 1) {
            val result2 = BigDecimal(megaByte.toString())
            return result2.setScale(2, RoundingMode.HALF_UP).toPlainString() + "MB"
        }
        val teraBytes = gigaByte / 1024
        if (teraBytes < 1) {
            val result3 = BigDecimal(gigaByte.toString())
            return result3.setScale(2, RoundingMode.HALF_UP).toPlainString() + "GB"
        }
        val result4 = BigDecimal(teraBytes)
        return (result4.setScale(2, RoundingMode.HALF_UP).toPlainString() + "TB")
    }


    // =========================== 删除文件 =================================


    /**
     * 删除空文件夹
     * @param path 文件夹地址
     * @return 是否删除成功，true为删除成功
     */
    private fun deleteFolder(path: String): Boolean {
        return try {
            val file = File(path)
            file.delete()
        } catch (e: Exception) {
            Log.e(TAG, "$e")
            false
        }
    }

    /**
     * 递归删除目录下的所有文件
     * @param path 文件夹目录
     * @return 是否删除
     */
    fun deleteAllFiles(path: String): Boolean {
        val file = File(path)
        // 文件夹不存在
        if (!file.exists()) {
            return false
        }
        // file不是文件夹
        if (!file.isDirectory) {
            return false
        }
        // 文件夹下面的文件
        val tempList = file.list() ?: return false
        // 成功删除的标志
        var flag = false
        var temp: File?
        for (i in tempList.indices) {
            temp = if (path.endsWith(File.separator)) {
                File(path + tempList[i])
            } else {
                File(path + File.separator + tempList[i])
            }
            // 文件删除
            if (temp.isFile) {
                temp.delete()
            }
            if (temp.isDirectory) {
                // 先删除文件夹里面的文件
                deleteAllFiles(path + "/" + tempList[i])
                // 再删除空文件夹
                deleteFolder(path + "/" + tempList[i])
                flag = true
            }
        }
        return flag
    }


    // =========================== 图片相关 =================================

    /**
     * 保存图片到本地
     * @param context
     * @param bitmap
     * @param prefix 文件名前缀
     * @param format 保存格式
     * @return 保存后的uri，空为保存失败
     * */
    fun saveImage(
        context: Context,
        bitmap: Bitmap,
        prefix: String = "",
        format: CompressFormat = CompressFormat.JPEG,
        savePath: String = DEFAULT_SAVE_PATH,
    ): Uri? {
        // 后缀名
        val suffix = format.toString()
        // 文件名
        val filename = "$prefix-${System.currentTimeMillis()}.$suffix"

        val values = ContentValues().apply {
            // 文件类型
            put(MediaStore.Images.Media.MIME_TYPE, "image/$suffix")
            // 文件名称
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            // 存放路径
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10 及以上不再使用 DATA 字段，使用相对路径
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/Save")
            } else {
                // Android 10 以下没有分区存储
                put(
                    MediaStore.MediaColumns.DATA,
                    "${(savePath)}${File.separator}${prefix}-${System.currentTimeMillis()}.$suffix"
                )
            }
        }
        // 插入
        val insertUri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
        // 输出
        insertUri?.let {
            context.contentResolver.openOutputStream(it).use { ops ->
                bitmap.compress(format, 100, ops)
            }
        }
        return insertUri
    }


    /**
     * 创建一个临时文件，适配 Android 10（Q）的分区存储
     * @param context
     * @param prefix 文件前缀
     * @param suffix 文件后缀
     * @return 对应文件
     * */
    private fun createTempFile(context: Context, prefix: String = "", suffix: String): File {
        val tmpFile = File(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            else
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp-${prefix}-${System.currentTimeMillis()}.${suffix}"
        )
        try {
            if (tmpFile.exists()) {
                tmpFile.delete()
            }
            tmpFile.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return tmpFile
    }

    /**
     * 照片请求，可以通过 [camera] 或者 [pick] 选取一张图片
     * */
    class PhotoRequest(private val activity: AppCompatActivity) : Fragment() {

        private var tmpUri: Uri? = null

        // 从相册中选取
        private var fromSelected = false

        // 从相机获取
        private var fromCamera = false

        // 裁剪选项
        private var cropOption: CropOptions? = null

        // 需要裁剪
        private var needCrop = false

        // 相机拿到拍摄的数据的回调
        private var cameraCallback: ((bitmap: Bitmap?, state: Int) -> Unit)? = null

        // 选择图片的数据的回调
        private var selectCallback: ((uri: Uri?, state: Int) -> Unit)? = null

        // 最后拿到数据的回调
        private var buildCallback: ((Uri?, Int) -> Unit)? = null

        // 相机
        private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            cameraCallback?.invoke(bitmap, if (bitmap == null) State.ERROR_TAKE else State.SUCCESS)
            if (bitmap != null) {
                if (needCrop) {
                    val uri = saveImage(activity, bitmap, "crop") ?: kotlin.run {
                        buildCallback?.invoke(null, State.ERROR_SAVE)
                        return@registerForActivityResult
                    }
                    cropLauncher.launch(cropIntent(activity, uri)) // 调用系统裁剪
                } else {
                    val uri = saveImage(activity, bitmap)
                    buildCallback?.invoke(uri, if (uri != null) State.SUCCESS else State.ERROR_SAVE)
                }
            }
        }

        // 选择图片
        private val pickLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            if (res.resultCode == Activity.RESULT_OK) {
                val uri = res?.data?.data
                // 选取图片的 uri
                selectCallback?.invoke(uri, if (uri != null) State.SUCCESS else State.ERROR_SELECT)
                if (uri != null) {
                    if (needCrop) { // 需要裁剪
                        cropLauncher.launch(cropIntent(activity, uri)) // 调用系统裁剪
                    } else {
                        buildCallback?.invoke(uri, State.SUCCESS) // 无需裁剪
                    }
                } else {
                    buildCallback?.invoke(null, State.ERROR_SELECT) // 选择图片失败
                }
            } else {
                selectCallback?.invoke(null, State.CANCEL_SELECT) // 取消选择
            }
        }

        // 裁剪图片
        private val cropLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { cropData ->
            if (cropData.resultCode == Activity.RESULT_OK) {
                val resUri = cropData?.data?.data
                Log.i("EditActivity", "Build resUri: $resUri")
                // TODO 转存到私有目录
                val copy = resUri?.run {
                    copyUriToInnerStorage(
                        requireContext(),
                        resUri,
                        "${requireContext().getExternalFilesDir(null)}${File.separator}Crop",
                        "crop-${System.currentTimeMillis()}.${cropOption?.outputFormat}"
                    ).toUri()
                }
                resUri?.toFile()?.delete() //删除该裁剪文件
                buildCallback?.invoke(copy, if (copy != null) State.SUCCESS else State.ERROR_CROP) // 裁剪回调
            } else if (cropData.resultCode == Activity.RESULT_CANCELED) {
                buildCallback?.invoke(null, State.CANCEL_CROP) // 取消裁剪
            }
        }

        companion object {

            /**
             * 以此开始一个 PhotoRequest
             * @param activity
             * */
            fun with(activity: AppCompatActivity) = PhotoRequest(activity)

            /**
             * 以此开始一个 PhotoRequest
             * @param fragment 宿主是 [AppCompatActivity] 的 fragment
             * */
            fun with(fragment: Fragment) = with(fragment.requireActivity() as AppCompatActivity)
        }

        // 使用 camera 拍照
        fun camera(callback: ((bitmap: Bitmap?, state: Int) -> Unit)? = null): PhotoRequest {
            this.cameraCallback = callback
            this.fromCamera = true
            return this
        }

        /**
         * 裁剪
         * @param option 裁剪的选项
         * */
        fun crop(option: CropOptions): PhotoRequest {
            this.needCrop = true
            this.cropOption = option
            return this
        }

        /**
         * 相册选择图片
         * @param callback 选择图片后的回调
         * */
        fun pick(callback: ((uri: Uri?, state: Int) -> Unit)? = null): PhotoRequest {
            this.fromSelected = true
            this.selectCallback = callback
            return this
        }

        /**
         * 开始
         * @param callback 回调
         * */
        fun build(callback: ((uri: Uri?, state: Int) -> Unit)) {
            this.buildCallback = { uri: Uri?, state: Int ->
                callback(uri, state)
                finish()
            }
            check(!(fromCamera && fromSelected)) {
                "You have called `camera()` and `select()` at the same time!"
            }
            check(fromCamera || fromSelected) {
                "Please call `camera()` or `select()`!"
            }
            activity.supportFragmentManager.commit {
                add(this@PhotoRequest, "photo request")
            }
        }

        /**
         * ActivityResult 只能在 onCreate 之前创建
         * */
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            // 相片拍摄
            if (fromCamera) {
                cameraLauncher.launch(null)
            }
            // 图片选择
            if (fromSelected) {
                pickLauncher.launch(openAlbum())
            }
        }

        private fun finish() {
            activity.supportFragmentManager.commit {
                remove(this@PhotoRequest)
            }
//            tmpUri?.toFile()?.delete()
        }


        /**
         * 获取裁剪的 intent
         * @param context
         * @param uri 需要裁剪图片对应的 uri
         * @return intent
         * */
        private fun cropIntent(context: Context, uri: Uri): Intent {
            val contentUri = Uri.fromFile(createTempFile(context, "crop", "jpeg"))
            Log.i(TAG, "cropIntent: $contentUri")
            tmpUri = contentUri // 记录缓存文件，结束时删除
            val intent = Intent("com.android.camera.action.CROP")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // Android 7.0需要临时添加读取Url的权限
                // 添加此属性是为了解决：调用裁剪框时候提示：图片无法加载或者加载图片失败或者无法加载此图片
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            intent.setDataAndType(uri, "image/*")
            // 发送裁剪信号，去掉也能进行裁剪
            intent.putExtra("crop", true)

            if (cropOption?.needAspect == true) {
                // 硬件厂商为华为的，默认是圆形裁剪框，这里让它无法成圆形
                if (Build.MANUFACTURER.contains("HUAWEI")) {
                    intent.putExtra("aspectX", 9999)
                    intent.putExtra("aspectY", 9998)
                } else {
                    // 上述两个属性控制裁剪框的缩放比例。
                    // 当用户用手拉伸裁剪框时候，裁剪框会按照上述比例缩放。
                    intent.putExtra("aspectX", cropOption?.aspectX ?: 1)
                    intent.putExtra("aspectY", cropOption?.aspectY ?: 1)
                }
            }

            // 设置裁剪区域的形状，默认为矩形，也可设置为圆形，可能无效
            intent.putExtra("circleCrop", cropOption?.isCircleCrop ?: false)
            // 设置能否缩放
            intent.putExtra("scale", cropOption?.isScale ?: false)
            // 去黑边
            intent.putExtra("scaleUpIfNeeded", true)
            // 属性控制裁剪完毕，保存的图片的大小格式。
            intent.putExtra("outputX", cropOption?.outputX ?: 400)
            intent.putExtra("outputY", cropOption?.outputY ?: 400)
            // 输出裁剪文件的格式
            intent.putExtra("outputFormat", cropOption?.outputFormat ?: CompressFormat.JPEG.toString())
            // 是否返回裁剪后图片的 Bitmap
            intent.putExtra("return-data", false) // 不返回数据，而只是保存在
            // 设置输出路径
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
            return intent
        }

        /**
         * 跳转到相册
         * */
        private fun openAlbum(): Intent {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            // 系统文件访问框架的写法
            //    val intent = Intent()
            //    intent.type = "image/*"
            //    intent.action = "android.intent.action.GET_CONTENT"
            //    intent.addCategory("android.intent.category.OPENABLE")
            return intent
        }

        /**
         * 裁剪选项
         * */
        class CropOptions {
            internal var needAspect = false
            internal var aspectX = 1
            internal var aspectY = 1
            internal var isCircleCrop = false
            internal var isScale = false
            internal var outputX = 400
            internal var outputY = 400
            internal var outputFormat = CompressFormat.JPEG.toString()

            /**
             * 裁剪比例
             * @param x x轴的比例
             * @param y y轴的缩比例
             * */
            fun aspect(x: Int, y: Int): CropOptions {
                this.needAspect = true
                this.aspectX = x
                this.aspectY = y
                return this
            }

            /**
             * 开启圆形裁剪，可能无效
             * */
            fun circleCrop(): CropOptions {
                this.isCircleCrop = true
                return this
            }

            /**
             * 支持图片缩放
             * */
            fun scale(): CropOptions {
                this.isScale = true
                return this
            }

            /**
             * 输出尺寸，像素
             * */
            fun ouput(x: Int, y: Int): CropOptions {
                this.outputX = x
                this.outputY = y
                return this
            }

            /**
             * 输出格式
             * */
            fun format(format: CompressFormat): CropOptions {
                this.outputFormat = format.toString()
                return this
            }
        }

        // 回调的状态
        object State {
            // 成功
            const val SUCCESS = 0

            // 保存失败
            const val ERROR_SAVE = 1

            // 裁剪失败
            const val ERROR_CROP = 2

            // 选择失败
            const val ERROR_SELECT = 3

            // 拍照失败
            const val ERROR_TAKE = 4

            // 取消选择
            const val CANCEL_SELECT = 5

            // 取消拍照
            const val CANCEL_TAKE = 6

            const val CANCEL_CROP = 7
        }
    }

    /**
     * 配置项
     * */
    object Config {
        /**
         * 设置包名
         * */
        fun setPackageName(packageName: String): Config {
            PACKAGE_NAME = packageName
            return this
        }
    }
}
package com.ujujzk.tryworkmanager


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.ujujzk.tryworkmanager.databinding.ActivityMainBinding
import java.io.File
import java.nio.charset.Charset
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_FILE = 100

        const val DICTIONARY_NAME_SEARCHING_ROW_NUMBER = 10
        const val DICTIONARY_NAME_TAG_IN_FILE = "#NAME"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.onSelectClick.observe {

            startActivityForResult(
                Intent(Intent.ACTION_GET_CONTENT).apply { type = "application/*" },
                REQUEST_CODE_FILE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_FILE && data != null) {

            handleFileRequestResult(data)
        }
    }


    private fun handleFileRequestResult(data: Intent) {
        val uri =
            when {
                data.clipData != null -> data.clipData?.getItemAt(0)?.uri
                data.data != null -> data.data
                else -> null
            }

        if (uri != null && uri.path?.endsWith(".dsl") == true) {
            Log.w("TAG", uri.path)
            readFile (uri)
        } else {
            Snackbar.make(binding.root, "Wrong file type", Snackbar.LENGTH_SHORT).show()
        }


    }


    private fun readFile (uri: Uri) {


        val scanner = Scanner(contentResolver.openInputStream(uri), Charset.forName("UTF-16").name())
        var line: String
        for (i in 0..DICTIONARY_NAME_SEARCHING_ROW_NUMBER){
            if (scanner.hasNext()){
                line = scanner.nextLine().trim()
                Log.w("TAG", "Line $i is $line")
                if (line.startsWith(DICTIONARY_NAME_TAG_IN_FILE)) {
                    Log.w("TAG", "Name of new dictionary is ${line.replace(DICTIONARY_NAME_TAG_IN_FILE, "").replace("\"", "").trim()}")
                    break
                }
            } else {
                break
            }
        }
        scanner.close()

    }

    fun <Y> LiveData<Y>.observe(observer: (Y) -> Unit) {
        observe(this@MainActivity, Observer { data ->
            data?.let { observer(it) }
        })
    }
}

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

class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_FILE = 100
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

            handleImageRequestResult(data)
        }
    }


    private fun handleImageRequestResult(data: Intent) {
        val uri =
            when {
                data.clipData != null -> data.clipData?.getItemAt(0)?.uri
                data.data != null -> data.data
                else -> null
            }

        if (uri != null && uri.path?.endsWith(".dsl") == true) {
            Log.w("TAG", uri.path)
        } else {
            Snackbar.make(binding.root, "Wrong file type", Snackbar.LENGTH_SHORT).show()
        }


    }

    fun <Y> LiveData<Y>.observe(observer: (Y) -> Unit) {
        observe(this@MainActivity, Observer { data ->
            data?.let { observer(it) }
        })
    }
}

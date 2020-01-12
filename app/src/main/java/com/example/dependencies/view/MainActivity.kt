package com.example.dependencies.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.dependencies.R
import com.example.dependencies.extensions.addTo
import com.example.dependencies.extensions.afterTextChanged
import com.example.dependencies.extensions.validate
import com.example.dependencies.injection.Injector
import com.example.dependencies.viewmodel.MainActivityViewModel
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainActivityViewModel

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        codeEditText.afterTextChanged {
            setCreateButtonProperties()
            codeEditText.validate("Must be a number") { s -> s.isDigitsOnly() }
        }

        valueEditText.afterTextChanged {
            setUpdateButtonProperties()
            valueEditText.validate("Must be a number") { s -> s.isDigitsOnly() }
        }

        valueEditText.visibility = View.INVISIBLE

        background.visibility = View.GONE

        setupCreateButton()
        setupUpdateButton()

        observeViewModel()
    }

    private fun setCreateButtonProperties() = createButton.apply {
        isEnabled = codeEditText.text.isNotEmpty() && codeEditText.text.isDigitsOnly()
        alpha = if (isEnabled) 1f else 0.3f
    }

    private fun setUpdateButtonProperties() = updateButton.apply {
        isEnabled = valueEditText.text.isNotEmpty() && valueEditText.text.isDigitsOnly()
        alpha = if (isEnabled) 1f else 0.3f
    }

    private fun setupCreateButton() {
        setCreateButtonProperties()

        RxView.clicks(createButton)
            .throttleFirst(500L, TimeUnit.MILLISECONDS)
            .subscribe {
                if (codeEditText.text.isNotEmpty() && codeEditText.text.isDigitsOnly())
                    viewModel.postNumber(codeEditText.text.toString().toInt())
            }.addTo(compositeDisposable)
    }

    private fun setupUpdateButton() {
        setUpdateButtonProperties()

        RxView.clicks(updateButton)
            .throttleFirst(500L, TimeUnit.MILLISECONDS)
            .subscribe {
                if (codeEditText.text.isNotEmpty() &&
                    codeEditText.text.isDigitsOnly() &&
                    valueEditText.text.isNotEmpty() &&
                    valueEditText.text.isDigitsOnly()
                ) {
                    viewModel.updateNumber(
                        codeEditText.text.toString().toInt(),
                        valueEditText.text.toString().toInt()
                    )
                }
            }.addTo(compositeDisposable)
    }

    private fun observeViewModel() {
        viewModel.number.observe(this, Observer {
            valueEditText.visibility = View.VISIBLE
            valueEditText.setText(it.toString())
        })

        viewModel.loading.observe(this, Observer {
            background.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.error.observe(this, Observer {
            errorTextView.visibility = if (it != null) View.VISIBLE else View.GONE
            errorTextView.text = it
        })

        viewModel.success.observe(this, Observer {
            if (it != null) {
                if (it)
                    Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(this, R.string.fail, Toast.LENGTH_SHORT).show()
            }
        })
    }
}

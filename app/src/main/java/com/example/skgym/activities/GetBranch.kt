package com.example.skgym.activities

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.example.skgym.R
import com.example.skgym.databinding.ActivityGetBranchBinding
import com.example.skgym.databinding.ActivityMainBinding
import com.example.skgym.di.component.DaggerFactoryComponent
import com.example.skgym.di.modules.FactoryModule
import com.example.skgym.di.modules.RepositoryModule
import com.example.skgym.mvvm.repository.MainRepository
import com.example.skgym.mvvm.viewmodles.MainViewModel
import com.google.firebase.auth.FirebaseAuth

class GetBranch : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var viewModel: MainViewModel
    private lateinit var component: DaggerFactoryComponent
    lateinit var binding: ActivityGetBranchBinding
    var branchList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetBranchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

        val handler = Handler()
        handler.postDelayed({ // Do something after 5s = 5000ms
            binding.progressBarBranch.visibility = View.GONE
        }, 3000)

        viewModel.fetchBranchNames().observe(this, {
            Log.d(TAG, "onCreateView: Size ${it.size}")
            val arrayAdapter = ArrayAdapter(
                this, R.layout.dropdownitem,
                it.toArray()
            )
            branchList = it

            arrayAdapter.notifyDataSetChanged()
            binding.branchDataGet.setAdapter(arrayAdapter)
        })


        binding.continueBranch.setOnClickListener {
            Log.d(TAG, "onCreate: ${branchList.size}")
            val branch = binding.branchDataGet.text.toString()
            if (branch.isNotEmpty()) {
                if (branchList.contains(branch)) {
                    Log.d(TAG, "onCreate: $branch")
                } else {
                    Toast.makeText(this, "Enter a Verified Branch", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Select Your Branch", Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun init() {
        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.my_statusbar_color)
        mAuth = FirebaseAuth.getInstance()
        component = DaggerFactoryComponent.builder()
            .repositoryModule(RepositoryModule(this))
            .factoryModule(FactoryModule(MainRepository(this)))
            .build() as DaggerFactoryComponent
        viewModel = ViewModelProviders.of(this, component.getFactory())
            .get(MainViewModel::class.java)
    }
}
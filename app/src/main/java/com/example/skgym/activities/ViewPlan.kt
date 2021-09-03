package com.example.skgym.activities

import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skgym.Adapters.ViewPlansAdapter
import com.example.skgym.R
import com.example.skgym.databinding.ActivityViewPlanBinding
import com.example.skgym.di.component.DaggerFactoryComponent
import com.example.skgym.di.modules.FactoryModule
import com.example.skgym.di.modules.RepositoryModule
import com.example.skgym.mvvm.repository.MainRepository
import com.example.skgym.mvvm.viewmodles.MainViewModel

class ViewPlan : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var component: DaggerFactoryComponent
    lateinit var binding: ActivityViewPlanBinding
    private var viewPlansAdapter = ViewPlansAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            recyclerViewPlans.apply {
                adapter = viewPlansAdapter
                layoutManager = LinearLayoutManager(this@ViewPlan)
                setHasFixedSize(true)
            }
        }

        init()
        setupRecycler()
    }

    private fun setupRecycler() {

        viewModel.getAllPlans().observe(this) {
            viewPlansAdapter.submitList(it)
            Log.d("TAG", "init:$it ")
            viewPlansAdapter.notifyDataSetChanged()
        }
    }

    private fun init() {
        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.my_statusbar_color)
        component = DaggerFactoryComponent.builder()
            .repositoryModule(RepositoryModule(this))
            .factoryModule(FactoryModule(MainRepository(this)))
            .build() as DaggerFactoryComponent
        viewModel = ViewModelProviders.of(this, component.getFactory())
            .get(MainViewModel::class.java)


    }
}
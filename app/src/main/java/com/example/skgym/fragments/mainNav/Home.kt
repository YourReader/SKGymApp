package com.example.skgym.fragments.mainNav


import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.skgym.Interfaces.IsMemberCallBack
import com.example.skgym.Interfaces.PlanKeyCallback
import com.example.skgym.R
import com.example.skgym.activities.GetBranch
import com.example.skgym.databinding.FragmentHomeBinding
import com.example.skgym.di.component.DaggerFactoryComponent
import com.example.skgym.di.modules.FactoryModule
import com.example.skgym.di.modules.RepositoryModule
import com.example.skgym.mvvm.repository.MainRepository
import com.example.skgym.mvvm.viewmodles.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Home : Fragment() {
    private lateinit var viewModel: MainViewModel
    private var _binding: FragmentHomeBinding? = null
    var branch = ""
    private val TAG = "Home"
    private val binding get() = _binding!!
    lateinit var currentUser: FirebaseUser
    var mAuth = FirebaseAuth.getInstance()
    var isDatatakenB = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        init()
        //Is branch Taken
        val isBranchTaken: SharedPreferences =
            requireActivity().getSharedPreferences("isBranchTaken", MODE_PRIVATE)
        val userBranch: SharedPreferences =
            requireActivity().getSharedPreferences("userBranch", MODE_PRIVATE)
        val isDataTaken: SharedPreferences =
            requireActivity().getSharedPreferences("isDataTaken", MODE_PRIVATE)
        isDatatakenB = isDataTaken.getBoolean("isDataTaken", false)


        val isBTaken = isBranchTaken.getBoolean("isBranchTaken", false)

        if (isBTaken) {
            binding.becomeMember.text = resources.getString(R.string.become_a_member)
            branch = userBranch.getString("userBranch", "").toString()
            Log.d(TAG, "onCreateView: Branch Taken")
            if (!isDatatakenB) {
                Log.d(TAG, "onCreateView: Data Not Taken")
                viewModel.isBranchExists(branch)

            } else {
                Log.d(TAG, "onCreateView: Checking Member")

                viewModel.checkUserIsMember(branch, object : IsMemberCallBack {
                    override fun onCallback(value: String?) {
                        if (value == "true") {
                            Log.d(TAG, "onCreateView: Member")
                            setUpPlanCard()
                        } else {
                            Log.d(TAG, "onCreateView: Not Member")
                            binding.nomemberLayout.visibility = View.VISIBLE
                            binding.progressbarHome.visibility = View.GONE
                            binding.memberLayout.visibility = View.GONE
                        }
                    }


                })

            }
        } else {
            Log.d(TAG, "onCreateView: Branch Not Taken")
            binding.becomeMember.text = resources.getString(R.string.selectBranch)
            val intent = Intent(requireContext(), GetBranch::class.java)
            startActivity(intent)
        }

        binding.becomeMember.setOnClickListener {
            if (binding.becomeMember.text == resources.getString(R.string.selectBranch))
                viewModel.sendUsertogetBranchActivity()
            else
                viewModel.sendUserToViewPlanActivity()
        }

        return binding.root
    }

    private fun setUpPlanCard() {
        val currentPlanPref: SharedPreferences =
            requireActivity().getSharedPreferences("currentPlan", Context.MODE_PRIVATE)
        val planEdit = currentPlanPref.edit()
        viewModel.getUserCurrentPlan(branch, object : PlanKeyCallback {
            override fun getCurrentPlanKey(planKey: String) {
                binding.nomemberLayout.visibility = View.GONE
                binding.progressbarHome.visibility = View.GONE
                binding.memberLayout.visibility = View.VISIBLE
                Log.d(TAG, "getCurrentPlanKey: Plan Key = $planKey")
                viewModel.fetchPlan(planKey).observe(this@Home) {
                    val plan = it
                    planEdit.putString("PlanName", plan.name)
                    planEdit.putString("PlanDesc", plan.desc)
                    planEdit.putString("PlanFees", plan.fees)
                    planEdit.putString("PlanKey", plan.key)
                    planEdit.putString("PlanTimeNumber", plan.timeNumber)
                    planEdit.putBoolean("PlanPT", plan.pt ?: false)
                    planEdit.putInt("PlanTotalDays", plan.totalDays)
                    planEdit.apply()
                    Log.d(TAG, "getCurrentPlan: $plan")
                    binding.cardPlanNameHome.text = plan.name
                    binding.cardDurationHome.text = plan.timeNumber
                    binding.cardFeesHome.text = plan.fees
                    val result: Int
                    val text: String
                    if (plan.pt == true) {
                        text = "PT"
                        result = View.VISIBLE
                    } else {
                        text = "Normal"
                        result = View.INVISIBLE
                    }
                    binding.isPersonalHome.text = text
                    binding.badgeGold.visibility = result
                }
            }
        })
    }


    private fun init() {
        val component: DaggerFactoryComponent = DaggerFactoryComponent.builder()
            .repositoryModule(RepositoryModule(requireContext()))
            .factoryModule(FactoryModule(MainRepository(requireContext())))
            .build() as DaggerFactoryComponent
        viewModel =
            ViewModelProviders.of(this, component.getFactory()).get(MainViewModel::class.java)
        mAuth = FirebaseAuth.getInstance()
        if (mAuth.currentUser == null)
            viewModel.sendUserToHomeAuth()

    }


}
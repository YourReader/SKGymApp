package com.example.skgym.mvvm.viewmodles

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.skgym.Interfaces.BranchInterface
import com.example.skgym.Interfaces.DataAdded
import com.example.skgym.Interfaces.IsMemberCallBack
import com.example.skgym.Interfaces.PlanKeyCallback
import com.example.skgym.data.Member
import com.example.skgym.data.Plan
import com.example.skgym.mvvm.repository.MainRepository

class MainViewModel constructor(var repository: MainRepository) : ViewModel() {


    val allCategories = repository.getCategoriesInfo()
    fun signOut() = repository.signOut()

    fun fetchBranchNames(branchInterface: BranchInterface) =
        repository.fetchBranchNames(branchInterface)


    fun checkUserIsMember(branch: String, callback: IsMemberCallBack) =
        repository.checkUserIsMember(branch, callback)


    fun sendUserToViewPlanActivity() = repository.sendUserToViewPlanActivity()

    fun checkStatsCondition() = repository.checkIfStatsExists()

    fun uploadUserdata(memberThis: Member, dataAdded: DataAdded) =
        repository.uploadUserdata(memberThis, dataAdded)

    fun sendUserToMainActivity() = repository.sendUserToMainActivity()

    fun isBranchExists(branch: String) = repository.doesUserAndBranchExists(branch)

    fun sendUserToHomeAuth() = repository.sendUserToHomeAuth()

    fun getAllPlans() = repository.fetchAllPlans()

    fun sendUsertogetBranchActivity() = repository.sendUserToGetBranchActivity()
//    fun changeMemberStatus()=repository.changeMemberToTrue()

    fun forgotPass(email: String) = repository.forgotPassword(email)

    fun loadProducts(name: String) = repository.loadAllProducts(name)

    fun addPlanToData(demo: Plan?, branch: String) = repository.addPlanToUser(demo, branch)

    fun addEndDate(context: Context, totalDays: Int, branch: String) =
        repository.pushEndDate(context, totalDays, branch)

    fun getUserCurrentPlan(branch: String, isMemberCallBack: PlanKeyCallback) {
        repository.getUserCurrentPlanKey(branch, isMemberCallBack)
    }

    fun fetchPlan(planKey: String) =
        repository.fetchPlan(planKey)


    fun pushStats(totalPrice: Int) = repository.updateStats(totalPrice)
}
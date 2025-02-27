package com.example.skgym.Room.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.skgym.Interfaces.GetNameCallback
import com.example.skgym.Room.Database.CartDatabase
import com.example.skgym.Room.repository.CartDatabaseRepository
import com.example.skgym.data.Cart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {
    //For History
    val readAllData: LiveData<List<Cart>>

    //For Cart
    val readUnpaidData: LiveData<List<Cart>>
    private val repository: CartDatabaseRepository

    init {
        val cartDao = CartDatabase.getCartDatabase(application).cartDao()
        repository = CartDatabaseRepository(cartDao, application)
        readAllData = repository.readAllData
        readUnpaidData = repository.readUnpaidCartData
    }

    fun addProductToCartDB(product: Cart) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.addProductToCart(product)
        }


    fun pushOrdersToDb(cart: Cart) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("TAG", "pushOrdersToDb: pushing ViewMidel")
            repository.setPaymentToTrue(cart)
            repository.pushOrdersDb(cart)
        }
    }

    fun decreaseQuantity(cart: Cart) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.decreaseQuantityOfProduct(cart)
        }

    fun increaseQuantity(cart: Cart) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.increaseQuantityOfProduct(cart)
        }

    fun deleteProduct(cart: Cart) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteProduct(cart)
        }

    fun getUserName(branch: String, callback: GetNameCallback) =
        repository.getUserName(branch, callback)
}
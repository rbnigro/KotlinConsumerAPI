package com.ronney.nybooks.presentation.books

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ronney.nybooks.R
import com.ronney.nybooks.data.ApiService
import com.ronney.nybooks.data.model.Book
import com.ronney.nybooks.data.response.BookBodyResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BooksViewModel : ViewModel() {

    val booksLiveData: MutableLiveData<List<Book>> = MutableLiveData()
    val viewFlipperLiveData: MutableLiveData<Pair<Int, Int?>> = MutableLiveData()

    fun getBooks() {
        ApiService.serviceBook.getBooks().enqueue(object: Callback<BookBodyResponse> {

            override fun onResponse(
                call: Call<BookBodyResponse>,
                response: Response<BookBodyResponse>
            ) {
                when {
                    response.isSuccessful -> {
                        val books: MutableList<Book> = mutableListOf()

                        response.body()?.let { bookBodyResponse ->
                            for (result in bookBodyResponse.bookResults) {
                                val book = result.bookDetailsResponse[0].getBookModel()
                                books.add(book)
                            }
                        }

                        booksLiveData.value = books
                        viewFlipperLiveData.value = Pair(VIEW_FLIPPER_BOOKS, null)
                    }
                    response.code() == 401 -> {
                        viewFlipperLiveData.value = Pair(VIEW_FLIPPER_ERROR, R.string.error_401)
                    }
                    response.code() == 404 -> {
                        viewFlipperLiveData.value = Pair(VIEW_FLIPPER_ERROR, R.string.error_404)
                    }
                    else -> {
                        viewFlipperLiveData.value = Pair(VIEW_FLIPPER_ERROR, R.string.error_400_generic)
                    }
                }
            }

            override fun onFailure(call: Call<BookBodyResponse>, t: Throwable) {
                viewFlipperLiveData.value = Pair(VIEW_FLIPPER_ERROR, R.string.error_500_generic)
            }
        })
    }

    companion object {
        private const val VIEW_FLIPPER_BOOKS = 1
        private const val VIEW_FLIPPER_ERROR = 2
    }
}
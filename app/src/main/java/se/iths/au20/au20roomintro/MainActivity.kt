package se.iths.au20.au20roomintro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.room.Room
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity() , CoroutineScope {

    private lateinit var job : Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var db : AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        job = Job()

        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "shopping-items")
            .fallbackToDestructiveMigration()
            .build()

/*
        val item = Item(0, "smör", false, "kyl")
        val item1 = Item(0, "mjölk", false, "kyl")
        val item2 = Item(0, "äpple", false, "frukt")


        saveItem(item)
        saveItem(item1)
        saveItem(item2)
        */

        //val list = loadAllItems()
        val list = loadByCategory("kyl")

        launch {
            val itemsList = list.await()
            for( item in itemsList) {
                Log.d("!!!", "item: $item")
            }
        }

    }

    fun saveItem(item : Item) {
        launch(Dispatchers.IO) {
            db.itemDao().insert(item)
        }
    }

    fun loadAllItems() : Deferred<List<Item>>  =
        async(Dispatchers.IO) {
            db.itemDao().getAll()
        }

    fun loadByCategory(category: String) =
        async(Dispatchers.IO) {
            db.itemDao().findByCategory(category)
        }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }



}
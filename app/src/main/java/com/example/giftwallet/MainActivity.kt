package com.example.giftwallet

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.giftwallet.databinding.ActivityMainBinding
import com.example.giftwallet.giftlist.db.AppDatabase
import com.example.giftwallet.giftlist.db.GiftDao
import com.example.giftwallet.giftlist.db.GiftEntity

class MainActivity : AppCompatActivity(), OnItemLongClickListener  { // ❶

    private lateinit var binding:ActivityMainBinding

    private lateinit var db : AppDatabase
    private lateinit var giftDao: GiftDao
    private lateinit var giftList: ArrayList<GiftEntity>
    private lateinit var adapter: GiftRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAdd.setOnClickListener {
            val intent = Intent(this, AddGiftActivity::class.java)
            startActivity(intent)
        }

        // DB 인스턴스를 가져오고 DB작업을 할 수 있는 DAO를 가져옵니다.
        db = AppDatabase.getInstance(this)!!
        giftDao = db.getGiftDao()

        getAllGiftList()
    }

    private fun getAllGiftList() {
        Thread {
            giftList = ArrayList(giftDao.getAll())
            setRecyclerView()
        }.start()
    }

    private fun setRecyclerView() {
        // 리사이클러뷰 설정
        runOnUiThread {
            adapter = GiftRecyclerViewAdapter(giftList, this) // ❷ 어댑터 객체 할당

//            private val giftList: ArrayList<GiftEntity>, private val listener: AdapterView.OnItemLongClickListener
            binding.recyclerView.adapter = adapter // 리사이클러뷰 어댑터로 위에서 만든 어댑터 설정
            binding.recyclerView.layoutManager = LinearLayoutManager(this) // 레이아웃 매니저 설정
        }
    }

    override fun onRestart() {
        super.onRestart()
        getAllGiftList()
    }

    /**
     * OnItemLongClickListener 인터페이스 구현부
     * */
    override fun onLongClick(position: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("할 일 삭제")
        builder.setMessage("정말 삭제하시겠습니까?")
        builder.setNegativeButton("취소", null)
        builder.setPositiveButton("네",
            object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    deleteGift(position)
                }
            }
        )
        builder.show()
    }

    private fun deleteGift(position: Int) {
        Thread {
            giftDao.deleteGift(giftList[position]) // DB에서 삭제
            giftList.removeAt(position) // 리스트에서 삭제
            runOnUiThread { // UI 관련 작업은 UI 스레드에서
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }.start()
    }




}
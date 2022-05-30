package com.example.giftwallet

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.*
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.giftwallet.databinding.ActivityMainBinding
import com.example.giftwallet.giftlist.db.AppDatabase
//import com.example.giftwallet.giftlist.db.BrandDao
import com.example.giftwallet.giftlist.db.GiftDao
import com.example.giftwallet.giftlist.db.GiftEntity

private val PERMISSIONS_REQUIREST_CODE = 1
private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE
                                            ,Manifest.permission.WRITE_EXTERNAL_STORAGE
                                            ,Manifest.permission.ACCESS_FINE_LOCATION
)



val CAMERA = arrayOf(Manifest.permission.CAMERA)
val STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
val CAMERA_CODE = 98
val STORAGE_CODE = 99




class MainActivity : AppCompatActivity(), OnItemLongClickListener  {

    private lateinit var binding:ActivityMainBinding

    private lateinit var db : AppDatabase
    private lateinit var giftDao: GiftDao
//    private lateinit var brandDao: BrandDao
    private lateinit var giftList: ArrayList<GiftEntity>
    private lateinit var adapter: GiftRecyclerViewAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAdd.setOnClickListener {
            val intent = Intent(this, AddGiftActivity::class.java).also { it.addCategory(Intent.ACTION_OPEN_DOCUMENT)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                .addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                .addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
            }
            startActivity(intent)
        }

        // DB 인스턴스를 가져오고 DB작업을 할 수 있는 DAO를 가져옵니다.
        db = AppDatabase.getInstance(this)!!

        giftDao = db.getGiftDao()
//        brandDao = db.getBrandDao()

//        if(!hasPermissions(this)){
//            requestPermissions(PERMISSIONS_REQUIRED, PERMISSIONS_REQUIREST_CODE)
//        }else{
//            getAllGiftList()
//        }

        if (checkPermission(STORAGE, STORAGE_CODE)) {
//            val itt = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            startActivityForResult(itt, STORAGE_CODE)
            getAllGiftList()
        }else{
//            권한요청
            requestPermissions(PERMISSIONS_REQUIRED, PERMISSIONS_REQUIREST_CODE)
        }
    }

    fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all{
        ContextCompat.checkSelfPermission(context,it) == PackageManager.PERMISSION_GRANTED
    }

    private fun getAllGiftList() {
        Thread {
            giftList = ArrayList(giftDao.getAllGift())
            setRecyclerView()
        }.start()
    }

    private fun setRecyclerView() {
        // 리사이클러뷰 설정
        runOnUiThread {
            adapter = GiftRecyclerViewAdapter(giftList, this) // ❷ 어댑터 객체 할당

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
//권한요청
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if(requestCode == PERMISSIONS_REQUIREST_CODE){
//            if(PackageManager.PERMISSION_GRANTED == grantResults.firstOrNull()){
//                Toast.makeText(this@MainActivity,"권한요청이 승인되었습니다.",Toast.LENGTH_LONG).show()
//            }else{
//                Toast.makeText(this@MainActivity, "권한요청이 거부되었습니다.",Toast.LENGTH_LONG).show()
//
//            }
//        }

        when (requestCode) {
//            CAMERA_CODE -> {
//                for (grant in grantResults) {
//                    if (grant != PackageManager.PERMISSION_GRANTED) {
//                        Toast.makeText(this, "카메라 권한을 승인해 주세요.", Toast.LENGTH_LONG).show()
//                    }
//                }
//            }
            STORAGE_CODE -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "저장소 권한을 승인해 주세요.", Toast.LENGTH_LONG).show()
                        //finish() 앱을 종료함
                    }
                }
            }
        }
    }



//checkPermission 메서드도 카메라, 저장소등 다른 권한들도 확인이 가능하도록 아래와 같이 수정합니다.
    fun checkPermission(permissions: Array<out String>, type: Int): Boolean
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, permissions, type)
                    return false;
                }
            }
        }

        return true;
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
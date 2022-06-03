package com.example.giftwallet

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.giftwallet.databinding.ItemGiftBinding
import com.example.giftwallet.giftlist.db.GiftEntity

class GiftRecyclerViewAdapter(
    private val giftList: ArrayList<GiftEntity>,
    private val listener: OnItemLongClickListener
) : RecyclerView.Adapter<GiftRecyclerViewAdapter.MyViewHolder>() {

    inner class MyViewHolder(binding: ItemGiftBinding) :
        RecyclerView.ViewHolder(binding.root) {
//        val tv_importance = binding.tvImportance
        val tv_info = binding.tvInfo
        //      이미지 추가
        var iv_gift_image = binding.ivGiftImage
        var tv_brand = binding.tvBrand
        var tv_validate = binding.tvValidate
        var tv_useyn = binding.tvUseyn

        val root = binding.root

    }

    //    뷰 객체 생성
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GiftRecyclerViewAdapter.MyViewHolder {

        val binding: ItemGiftBinding =
            ItemGiftBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    //    데이터 넣는 작업
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val giftData = giftList[position]

        holder.tv_info.text = giftData.info

//        이미지 추가
        holder.iv_gift_image.setImageURI(giftData.imagerurl.toUri())
        holder.tv_brand.text = giftData.brand
        holder.tv_validate.text = giftData.validate

        when(giftData.useyn){
            0->{
                holder.tv_useyn.text = "사용"
            }
            1->{
                holder.tv_useyn.text = "미사용"
            }
        }

//        https://yunaaaas.tistory.com/57
//        https://ddolcat.tistory.com/591c
//        image 클릭시 zoom fragment 실행
        holder.iv_gift_image.setOnClickListener() {
            println("Click됨됨Click됨됨Click됨됨Click됨됨Click됨됨Click됨됨Click됨됨Click됨됨")
            println("이미지clickposition:"+position)
            println(giftList[position].toString())
        }

//        나머지 영역 클릭 시 수정 fragment 실행
        holder.root.setOnClickListener(){
            println("root클릭")
            println("root클릭position:"+position)
            println(giftList[position].toString())
        }
    }



    override fun getItemCount(): Int {
        return giftList.size
    }

}


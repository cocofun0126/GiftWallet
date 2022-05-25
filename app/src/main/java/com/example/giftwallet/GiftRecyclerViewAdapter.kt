package com.example.giftwallet

import android.view.LayoutInflater
import android.view.ViewGroup
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
        val tv_importance = binding.tvImportance
        val tv_title = binding.tvTitle
        //      이미지 추가
        var iv_icon = binding.ivIcon
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

        holder.tv_title.text = giftData.title

//        이미지 추가
        holder.iv_icon.setImageURI(giftData.imagerurl.toUri())

    }

    override fun getItemCount(): Int {
        return giftList.size
    }

}


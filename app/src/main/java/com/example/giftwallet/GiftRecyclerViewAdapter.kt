package com.example.giftwallet


import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.giftwallet.databinding.ItemGiftBinding
import com.example.giftwallet.giftlist.db.GiftEntity


class GiftRecyclerViewAdapter(
//    private val giftList: ArrayList<GiftEntity>,
    private val giftList: MutableList<GiftEntity> = mutableListOf<GiftEntity>(),
    private val listener: OnItemLongClickListener

) : RecyclerView.Adapter<GiftRecyclerViewAdapter.MyViewHolder>() {


    inner class MyViewHolder(binding: ItemGiftBinding) : RecyclerView.ViewHolder(binding.root) {
//        val tv_importance = binding.tvImportance
        val tv_info = binding.tvInfo
        //      이미지 추가
        var iv_gift_image = binding.ivGiftImage
        var tv_brand = binding.tvBrand
        var tv_validate = binding.tvValidate
        var tv_useyn = binding.tvUseyn

        val root = binding.root

//        https://velog.io/@hygge/Android-Kotlin-RecyclerView-item-%ED%81%B4%EB%A6%AD-%EC%8B%9C-%ED%99%94%EB%A9%B4-%EC%A0%84%ED%99%98
        private val context = binding.root.context

        fun bind(item: GiftEntity) {
            iv_gift_image.setOnClickListener {
                val intent = Intent(context, ImageZoomActivity::class.java)
                intent.putExtra("data", item);
                intent.run { context.startActivity(this) }
            }
        }
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
    @Override
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

//        holder.iv_gift_image.setImageURI(giftList.get(position).imagerurl.toUri());

        val giftData = giftList[position]

        holder.tv_info.text = giftData.info

//        이미지 추가
        holder.iv_gift_image.setImageURI(giftData.imageurl.toUri())
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

        holder?.bind(giftList[position])

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


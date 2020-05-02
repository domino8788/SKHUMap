package com.domino.skhumap.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.domino.skhumap.R
import com.domino.skhumap.activity.MainActivity
import com.domino.skhumap.fragment.MyPageFragment
import kotlinx.android.synthetic.main.item_my_page_menu_list.view.*

class MyPageMenuAdapter: RecyclerView.Adapter<MyPageMenuAdapter.MyPageMenuViewHolder>(){
    inner class MyPageMenuViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_my_page_menu_list, parent, false)) {
        val layout = itemView.my_page_menu_layout as LinearLayout
        val img_icon = itemView.my_page_menu_icon
        val txt_title = itemView.my_page_menu_title
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPageMenuViewHolder = MyPageMenuViewHolder(parent)

    override fun getItemCount(): Int = 2

    override fun onBindViewHolder(holder: MyPageMenuViewHolder, position: Int) {
        when(position){
            0 -> {
                holder.run {
                    img_icon.setImageResource(R.drawable.ic_signs)
                    txt_title.text = "로그아웃"
                    layout.setOnClickListener {
                        android.webkit.CookieManager.getInstance().removeAllCookie()
                        MainActivity.context!!.getSharedPreferences("login_info", Context.MODE_PRIVATE).edit().run {
                            remove("id")
                            remove("name")
                            remove("password")
                            commit()
                        }
                        MyPageFragment.instance.initLoginInfo()
                    }
                }
            }
            1 -> {
                holder.run {
                    img_icon.setImageResource(R.drawable.ic_interface)
                    txt_title.text = "앱 정보"
                }
            }
        }
    }
}

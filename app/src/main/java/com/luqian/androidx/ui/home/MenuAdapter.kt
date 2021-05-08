package com.luqian.androidx.ui.home

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.luqian.androidx.R

/**
 * @author  LUQIAN
 * @date    2021/5/8
 *
 *  首页菜单
 */
class MenuAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_menu) {

    override fun convert(holder: BaseViewHolder, item: String) {
        holder.setText(R.id.tv_menu, item)
    }
}
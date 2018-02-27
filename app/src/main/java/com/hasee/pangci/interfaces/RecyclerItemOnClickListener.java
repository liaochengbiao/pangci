package com.hasee.pangci.interfaces;

/**
 * @author
 */
public interface RecyclerItemOnClickListener {
    /**
     * 列表长按的回调方法
     * @param position 长按的列表项
     */
    void onItemLongClick(int position);

    /***
     * 列表点击的回调方法
     * @param position 单击的列表item
     */
    void onItemClick(int position);

}

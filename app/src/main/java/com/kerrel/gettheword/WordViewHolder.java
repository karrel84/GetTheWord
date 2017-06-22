package com.kerrel.gettheword;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kerrel.gettheword.databinding.ItemWordBinding;

/**
 * Created by 이주영 on 2017-06-21.
 */

public class WordViewHolder extends RecyclerView.ViewHolder {
    private final ItemWordBinding mBinding;
    private WordItem data;

    public WordViewHolder(ItemWordBinding binding) {
        super(binding.getRoot());
        mBinding = binding;
    }

    public void setData(WordItem data) {
        this.data = data;

        // 단어
        mBinding.word.setText(data.word);
        // 뜻
        mBinding.mean.setText(data.meaning);
    }
}

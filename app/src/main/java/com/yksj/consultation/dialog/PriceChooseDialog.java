package com.yksj.consultation.dialog;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.SpanUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.library.base.base.BaseDialog;
import com.library.base.base.ViewHolder;
import com.library.base.listener.SimpleTextWatcher;
import com.library.base.utils.ResourceHelper;
import com.library.base.widget.DividerGridItemDecoration;
import com.library.base.widget.PriceInputFilter;
import com.library.base.widget.SuperTextView;
import com.yksj.consultation.constant.PayType;
import com.yksj.consultation.sonDoc.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 金额选择界面
 */
public class PriceChooseDialog extends BaseDialog {
    private OnPriceChooseClickListener onPriceChooseClickListener;
    private RecyclerView recyclerView;
    private SuperTextView wecharPayView;
    private SuperTextView aliPayView;
    private SimplePriceAdapter adapter;
    private List<Price> mPrices;

    private SuperTextView.OnRadioCheckChangeListener checkedListener = new SuperTextView.OnRadioCheckChangeListener() {
        private int checkId;

        @Override
        public void onCheckedChanged(SuperTextView view, boolean isChecked) {
            if (checkId != 0) {
                if (aliPayView.getId() == checkId) {
                    aliPayView.setRadioChecked(false);
                }
                if (wecharPayView.getId() == checkId) {
                    wecharPayView.setRadioChecked(false);
                }
            }
            if (checkId != view.getId()) {
                checkId = view.getId();
                view.setRadioChecked(true);
            }
        }
    };

    public static PriceChooseDialog newInstance() {
        Bundle args = new Bundle();

        PriceChooseDialog fragment = new PriceChooseDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int createContentLayoutRes() {
        return R.layout.dialog_price_choose;
    }

    @Override
    public void convertView(ViewHolder holder, BaseDialog dialog) {
        // 设置左右边距
        setMargin(ResourceHelper.getDimens(com.library.base.R.dimen.dialog_marging));
        // 设置取消监听
        holder.getView(R.id.cancel).setOnClickListener(this::onCancel);
        // 设置去支付监听
        holder.getView(R.id.ok).setOnClickListener(this::onPay);
        recyclerView = holder.getView(R.id.price_recycler);
        wecharPayView = holder.getView(R.id.wechat_pay);
        aliPayView = holder.getView(R.id.ali_pay);
        adapter = new SimplePriceAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerGridItemDecoration(SizeUtils.dp2px(12), true));
        adapter.addData(mPrices == null ? createDefaultPrice() : mPrices);
        adapter.setOnItemClickListener(this::onItemClick);
        wecharPayView.setRadioCheckedChangeListener(checkedListener);
        aliPayView.setRadioCheckedChangeListener(checkedListener);
        wecharPayView.setRadioChecked(true);
    }

    /**
     * 创建默认的金额选择数据
     * @return
     */
    private List<Price> createDefaultPrice() {
        ArrayList prices = new ArrayList();
        prices.add(new Price(2, "元", false, true));
        prices.add(new Price(5, "元", false, false));
        prices.add(new Price(10, "元", false, false));
        prices.add(new Price(20, "元", false, false));
        prices.add(new Price(50, "元", false, false));
        prices.add(new Price(0, "元", true, false));
        return prices;
    }

    /**
     * 去支付
     * @param view
     */
    private void onPay(View view) {
        Price selectedPrice = adapter.getSelectedPrice();
        if (selectedPrice == null || selectedPrice.price == 0) {
            ToastUtils.showShort("您输入的金额不正确");
            return;
        }
        if (wecharPayView.radioIsChecked()) {
            if (onPriceChooseClickListener != null) {
                onPriceChooseClickListener.onPriceClick(PayType.WECHAT, selectedPrice);
            }
            dismiss();
        } else if (aliPayView.radioIsChecked()) {
            if (onPriceChooseClickListener != null) {
                onPriceChooseClickListener.onPriceClick(PayType.ALI, selectedPrice);
            }
            dismiss();
        }
    }

    /**
     * 设置选择金额
     * @param prices
     * @return
     */
    public PriceChooseDialog setPrices(List<Price> prices) {
        this.mPrices = prices;
        return this;
    }

    /**
     * 设置金额点击监听
     * @param listener
     * @return
     */
    public PriceChooseDialog setListener(OnPriceChooseClickListener listener) {
        this.onPriceChooseClickListener = listener;
        return this;
    }

    /**
     * 取消
     * @param view
     */
    private void onCancel(View view) {
        dismiss();
    }

    /**
     * 金额点击事件
     * @param adapter
     * @param view
     * @param position
     */
    private void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        SimplePriceAdapter priceAdapter = (SimplePriceAdapter) adapter;
        // 刷新选择的状态
        priceAdapter.notifyStatusChange(position);
    }

    /**
     * 金额适配器
     */
    private class SimplePriceAdapter extends BaseQuickAdapter<Price, BaseViewHolder> {
        // 选择的金额
        private Price selectedPrice;

        public SimplePriceAdapter() {
            super(R.layout.item_price);
        }

        @Override
        protected void convert(BaseViewHolder helper, Price item) {
            helper.getView(R.id.tv_price).setSelected(item.isSelected);
            if (item.isSelected) {
                // 获取选择的金额，保存
                selectedPrice = item;
            }
            if (item.isCustom) {
                helper.setText(R.id.tv_price, "自定义");
                helper.getView(R.id.custom_price_lay).setSelected(item.isSelected);
                helper.setGone(R.id.custom_price_lay, item.isSelected);
                helper.setGone(R.id.tv_price, !item.isSelected);
                EditText customPrice = helper.getView(R.id.et_custom_price);
                customPrice.setFilters(new InputFilter[]{new PriceInputFilter()});
                //监听自定义金额输入
                customPrice.addTextChangedListener(new SimpleTextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!TextUtils.isEmpty(s)) {
                            item.price = Float.valueOf(s.toString());
                        }
                    }
                });
            } else {
                helper.setGone(R.id.custom_price_lay, false);
                helper.setGone(R.id.tv_price, true);
                helper.setText(R.id.tv_price, new SpanUtils()
                        .append(String.valueOf(item.price))
                        .setFontSize(18, true)
                        .append(item.suffix)
                        .setFontSize(14, true)
                        .create());
            }
        }

        /**
         * 刷新选择状态
         * @param position
         */
        public void notifyStatusChange(int position) {
            for (int i = 0; i < getItemCount(); i++) {
                Price price = getItem(i);
                price.isSelected = position == i;
            }
            notifyDataSetChanged();
        }

        /**
         * 获取选择的金额
         * @return
         */
        public Price getSelectedPrice() {
            return selectedPrice;
        }
    }

    /**
     * 金额实体
     */
    public static class Price implements Parcelable {
        public float price;
        public String suffix;
        public boolean isCustom;
        public boolean isSelected;
        public int position;

        public Price(int price, String suffix, boolean isCustom, boolean isSelected) {
            this.price = price;
            this.suffix = suffix;
            this.isCustom = isCustom;
            this.isSelected = isSelected;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeFloat(this.price);
            dest.writeString(this.suffix);
            dest.writeByte(this.isCustom ? (byte) 1 : (byte) 0);
            dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
            dest.writeInt(this.position);
        }

        protected Price(Parcel in) {
            this.price = in.readInt();
            this.suffix = in.readString();
            this.isCustom = in.readByte() != 0;
            this.isSelected = in.readByte() != 0;
            this.position = in.readInt();
        }

        public final Parcelable.Creator<Price> CREATOR = new Parcelable.Creator<Price>() {
            @Override
            public Price createFromParcel(Parcel source) {
                return new Price(source);
            }

            @Override
            public Price[] newArray(int size) {
                return new Price[size];
            }
        };
    }

    public interface OnPriceChooseClickListener {
        void onPriceClick(@PayType.Type int payType, Price price);
    }
}

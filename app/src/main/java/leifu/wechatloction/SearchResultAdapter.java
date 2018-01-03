package leifu.wechatloction;

/**
 * Created by Administrator on 2017/11/17.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.help.Tip;

import java.util.ArrayList;

/**
 * 自定义adpter
 */
class SearchResultAdapter extends BaseAdapter {
    ArrayList<Tip> arrayList;
    Activity activity;
    private int selectedPosition = 0;

    public SearchResultAdapter(ArrayList<Tip> arrayList, Activity activity) {
        this.arrayList = arrayList;
        this.activity = activity;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }


    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(activity).inflate(R.layout.item, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.bindView(i);

        return view;
    }


    class ViewHolder {
        TextView tv_title;
        TextView tv_details;
        ImageView iv_check;

        public ViewHolder(View view) {
            tv_title = view.findViewById(R.id.tv_title);
            tv_details = view.findViewById(R.id.tv_details);
            iv_check = view.findViewById(R.id.iv_check);
        }

        public void bindView(int position) {
            if (position >= arrayList.size())
                return;
            Tip Tip = arrayList.get(position);
            tv_title.setText(Tip.getName());
            tv_details.setText(Tip.getDistrict() + Tip.getAddress() );
            iv_check.setVisibility(position == selectedPosition ? View.INVISIBLE : View.INVISIBLE);
        }
    }
}


package leifu.wechatloction;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;

import java.util.ArrayList;
import java.util.List;

import leifu.shapelibrary.ShapeView;

/**
 * Created by Administrator on 2018/1/3.
 */

public class SearchResultActivity extends AppCompatActivity {
    private ListView listView;
    private ImageView iv_x;
    private SearchResultAdapter searchResultAdapter;
    private TextView tv_cancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchresult);
        final ShapeView et_input = (ShapeView) findViewById(R.id.et_input);
        listView = (ListView) findViewById(R.id.listView);
        iv_x = (ImageView) findViewById(R.id.iv_x);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        et_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable.toString())) {
                    iv_x.setVisibility(View.GONE);
                } else {
                    iv_x.setVisibility(View.VISIBLE);
                    //第二个参数传入null或者“”代表在全国进行检索，否则按照传入的city进行检索
                    InputtipsQuery inputquery = new InputtipsQuery(et_input.getText().toString(), "");
                    inputquery.setCityLimit(true);//限制在当前城市
                    Inputtips inputTips = new Inputtips(SearchResultActivity.this, inputquery);
                    inputTips.requestInputtipsAsyn();
                    inputTips.setInputtipsListener(new Inputtips.InputtipsListener() {
                        @Override
                        public void onGetInputtips(List<Tip> list, int i) {
                            searchResultAdapter = new SearchResultAdapter((ArrayList<Tip>) list, SearchResultActivity.this);
                            listView.setAdapter(searchResultAdapter);
                        }
                    });
                }
            }
        });
        iv_x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_input.setText("");
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Tip tip = (Tip) searchResultAdapter.getItem(i);
                Intent intent = new Intent();
                intent.putExtra("tip", tip);
                setResult(1, intent);
                finish();
            }
        });
    }
}

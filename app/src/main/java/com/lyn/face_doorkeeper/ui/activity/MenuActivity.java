package com.lyn.face_doorkeeper.ui.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.lyn.face_doorkeeper.R;
import com.lyn.face_doorkeeper.databinding.ActivityMenuBinding;
import com.lyn.face_doorkeeper.databinding.AdapterGridMenuItemBinding;
import com.lyn.face_doorkeeper.entity.Menu;
import com.lyn.face_doorkeeper.ui.adapter.MyBaseAdapter;
import com.lyn.face_doorkeeper.utils.LogUtils;
import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends BaseActivity<ActivityMenuBinding> implements AdapterView.OnItemClickListener {
    private List<Menu> menuList = new ArrayList<>();
    private MyBaseAdapter<Menu, AdapterGridMenuItemBinding> adapter;

    @Override
    protected void init(Bundle savedInstanceState) {

    }

    @Override
    protected ActivityMenuBinding getBindView(LayoutInflater layoutInflater) {
        return ActivityMenuBinding.inflate(layoutInflater);
    }

    @Override
    protected void bindData() {
        bindView.Toolbar.Title.setText(getString(R.string.str_deviceSettings));
        bindView.Toolbar.Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUI(MainActivity.class);
                finish();
            }
        });
        adapter = new MyBaseAdapter<Menu, AdapterGridMenuItemBinding>() {
            @Override
            protected AdapterGridMenuItemBinding getViewBinding(ViewGroup parent) {
                return AdapterGridMenuItemBinding.inflate(getLayoutInflater());
            }

            @Override
            protected void bindData(AdapterGridMenuItemBinding adapterMenuItemBinding, Menu menu, int position) {
                adapterMenuItemBinding.Title.setText(menu.getTitle());
                adapterMenuItemBinding.Icon.setImageDrawable(getDrawable(menu.getResID()));
            }

            @Override
            protected void bindListener(AdapterGridMenuItemBinding adapterMenuItemBinding, Menu menu, int position) {

            }
        };
        //第一行
        menuList.add(new Menu(getString(R.string.str_recognitionSettings),R.drawable.recognition_settings));
        menuList.add(new Menu(getString(R.string.str_personnelManagement),R.drawable.personnel_management));
        menuList.add(new Menu(getString(R.string.str_recordsManagement),R.drawable.records_management));
        //第二行
        menuList.add(new Menu(getString(R.string.str_ruleManagement),R.drawable.rule_management));
        menuList.add(new Menu(getString(R.string.str_accessControl),R.drawable.access_control));
        menuList.add(new Menu(getString(R.string.str_backstageManagement),R.drawable.backstage_management));
         //第三行
        menuList.add(new Menu(getString(R.string.str_logManagement),R.drawable.log_management));
        menuList.add(new Menu(getString(R.string.str_firmwareUpgrade),R.drawable.firmware_upgrade));
        menuList.add(new Menu(getString(R.string.str_restoreFactory),R.drawable.restore_factory));
        bindView.MenuGridView.setAdapter(adapter);
        adapter.setData(menuList);
    }

    @Override
    protected void initListener() {
        bindView.MenuGridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LogUtils.i(position+"");
        if (position==0){
            changeUI(FaceSettingActivity.class);
        }
        if (position==1){
            changeUI(PersonManagementActivity.class);
        }
        if (position==2){
            changeUI(RecordManagementActivity.class);
        }
        if (position==3){
            changeUI(RuleManagementActivity.class);
        }
        if (position==4){
        }
        finish();
    }
}

package com.lyn.face_doorkeeper.ui.activity;

import android.app.Dialog;
import android.content.AsyncTaskLoader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.lyn.face_doorkeeper.R;
import com.lyn.face_doorkeeper.database.Person;
import com.lyn.face_doorkeeper.database.Rule;
import com.lyn.face_doorkeeper.databinding.ActivityRuleManagementBinding;
import com.lyn.face_doorkeeper.databinding.AdapterRuleItemBinding;
import com.lyn.face_doorkeeper.databinding.DialogAddRuleBinding;
import com.lyn.face_doorkeeper.ui.adapter.MyBaseAdapter;
import com.lyn.face_doorkeeper.ui.dialog.BaseDialog;
import com.lyn.face_doorkeeper.ui.dialog.DialogUtils;
import com.lyn.face_doorkeeper.utils.TimeUtils;
import com.scwang.smart.refresh.footer.BallPulseFooter;
import com.scwang.smart.refresh.header.BezierRadarHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.util.List;

public class RuleManagementActivity extends BaseActivity<ActivityRuleManagementBinding> {

    private MyBaseAdapter<Rule, AdapterRuleItemBinding> adapter;

    @Override
    protected void init(Bundle savedInstanceState) {

    }

    @Override
    protected ActivityRuleManagementBinding getBindView(LayoutInflater layoutInflater) {
        return ActivityRuleManagementBinding.inflate(layoutInflater);
    }

    @Override
    protected void bindData() {
        bindView.Toolbar.Title.setText(getString(R.string.str_runle_management));
        bindView.Toolbar.Menu.setVisibility(View.VISIBLE);
        bindView.Toolbar.MenuIcon.setBackgroundResource(R.drawable.add);
        adapter=new MyBaseAdapter<Rule, AdapterRuleItemBinding>() {
            @Override
            protected AdapterRuleItemBinding getViewBinding(ViewGroup parent) {
                return AdapterRuleItemBinding.inflate(getLayoutInflater());
            }

            @Override
            protected void bindData(AdapterRuleItemBinding adapterRuleItemBinding, Rule rule, int position) {
                adapterRuleItemBinding.Name.setText(rule.getName());
                adapterRuleItemBinding.startTime.setText(TimeUtils.getTimeAndTimeStamp(rule.getStartTime()));
                adapterRuleItemBinding.endTime.setText(TimeUtils.getTimeAndTimeStamp(rule.getEndTime()));
            }

            @Override
            protected void bindListener(AdapterRuleItemBinding adapterRuleItemBinding, Rule rule, int position) {

            }
        };
        bindView.RuleListView.setAdapter(adapter);

    }

    @Override
    protected void initListener() {
        bindView.Toolbar.Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUI(MenuActivity.class);
                finish();
            }
        });
        bindView.Toolbar.Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRule();
            }
        });

        bindView.RefreshLayout.setRefreshHeader(new BezierRadarHeader(this).setEnableHorizontalDrag(true));
        bindView.RefreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        bindView.RefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                new RefreshRuleAsyncTask().execute();
            }
        });
        bindView.RefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                new LoadMoreRuleAsyncTask().execute();
            }
        });
    }

    /**
     * 添加规则
     */
    private void addRule(){
        BaseDialog<DialogAddRuleBinding>addRuleBindingBaseDialog=new BaseDialog<DialogAddRuleBinding>(context,R.style.common_dialog) {
            @Override
            protected DialogAddRuleBinding getViewBinding() {
                return DialogAddRuleBinding.bind(LayoutInflater.from(context).inflate(R.layout.dialog_add_rule,null,false));
            }

            @Override
            protected void bindData(DialogAddRuleBinding binding, Dialog dialog) {
                binding.Title.setText(getString(R.string.str_add_rule));
                String timeAndTimeStamp = TimeUtils.getTimeAndTimeStamp(System.currentTimeMillis());
                binding.startTime.setText(timeAndTimeStamp);
                binding.endTime.setText(timeAndTimeStamp);
            }

            @Override
            protected void bindListener(DialogAddRuleBinding binding, Dialog dialog) {
                binding.startTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtils.DialogSelectTime(context, null, new DialogUtils.CallbackTime() {
                            @Override
                            public void onTime(String time) {
                                binding.startTime.setText(time);
                            }
                        });
                    }
                });

                binding.endTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtils.DialogSelectTime(context, null, new DialogUtils.CallbackTime() {
                            @Override
                            public void onTime(String time) {
                                binding.endTime.setText(time);
                            }
                        });
                    }
                });

                binding.Cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                binding.Determine.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Rule rule=new Rule();
                        long startTime = TimeUtils.getTimeAndTimeStamp(binding.startTime.getText().toString());
                        long endTime = TimeUtils.getTimeAndTimeStamp(binding.endTime.getText().toString());
                        rule.setStartTime(startTime);
                        rule.setEndTime(endTime);
                        rule.setMonday(binding.Monday.isChecked());
                        rule.setTuesday(binding.Tuesday.isChecked());
                        rule.setWednesday(binding.Wednesday.isChecked());
                        rule.setThursday(binding.Thursday.isChecked());
                        rule.setFriday(binding.Friday.isChecked());
                        rule.setSaturday(binding.Saturday.isChecked());
                        rule.setSunday(binding.Sunday.isChecked());
                        boolean save = rule.save();
                        if (save){
                            showToast(getString(R.string.str_savedSuccessfully));
                        }else {
                            showToast(getString(R.string.str_saveFailed));
                        }
                    }
                });
            }
        };
        addRuleBindingBaseDialog.show();
    }

    private volatile int page = 1;


    private volatile int length = 20;


    private class RefreshRuleAsyncTask extends AsyncTask<Void, Integer, List<Rule>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            page = 1;
        }

        @Override
        protected List<Rule> doInBackground(Void... voids) {
            List<Rule> ruleList = LitePal.offset(page * length - length).limit(length).order("startTime desc").find(Rule.class);
            return ruleList;
        }

        @Override
        protected void onPostExecute(List<Rule> rules) {
            super.onPostExecute(rules);
            if (rules != null) {
                adapter.setData(rules);
                if (rules.size() < length) {
                    bindView.RefreshLayout.finishRefreshWithNoMoreData();
                } else {
                    bindView.RefreshLayout.finishRefresh(true);
                }
            } else {
                bindView.RefreshLayout.finishRefresh(true);
            }
        }
    }


    private class LoadMoreRuleAsyncTask extends AsyncTask<Void, Integer, List<Rule>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            page++;
        }

        @Override
        protected List<Rule> doInBackground(Void... voids) {
            List<Rule> ruleList = LitePal.offset(page * length - length).limit(length).order("time desc").find(Rule.class);
            return ruleList;
        }

        @Override
        protected void onPostExecute(List<Rule> rules) {
            super.onPostExecute(rules);
            if (rules != null) {
                adapter.addData(rules);
                if (rules.size() < length) {
                    bindView.RefreshLayout.finishLoadMoreWithNoMoreData();
                } else {
                    bindView.RefreshLayout.finishLoadMore(true);
                }
            } else {
                bindView.RefreshLayout.finishLoadMore(true);
            }

        }
    }
}

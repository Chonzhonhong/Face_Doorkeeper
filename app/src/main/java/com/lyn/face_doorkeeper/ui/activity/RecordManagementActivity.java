package com.lyn.face_doorkeeper.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.lyn.face_doorkeeper.R;
import com.lyn.face_doorkeeper.database.Record;
import com.lyn.face_doorkeeper.databinding.ActivityRecordManagementBinding;
import com.lyn.face_doorkeeper.databinding.AdapterPersonOrRecordItemBinding;
import com.lyn.face_doorkeeper.databinding.DialogConfirmOperationBinding;
import com.lyn.face_doorkeeper.databinding.DialogMenuBinding;
import com.lyn.face_doorkeeper.entity.ContentItem;
import com.lyn.face_doorkeeper.entity.Menu;
import com.lyn.face_doorkeeper.ui.adapter.MyBaseAdapter;
import com.lyn.face_doorkeeper.ui.dialog.BaseDialog;
import com.lyn.face_doorkeeper.ui.dialog.DialogUtils;
import com.lyn.face_doorkeeper.utils.FileUtils;
import com.lyn.face_doorkeeper.utils.LogUtils;
import com.lyn.face_doorkeeper.utils.TimeUtils;
import com.scwang.smart.refresh.footer.BallPulseFooter;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.BezierRadarHeader;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecordManagementActivity extends BaseActivity<ActivityRecordManagementBinding> {


    private MyBaseAdapter<Record, AdapterPersonOrRecordItemBinding> adapter;


    @Override
    protected void init(Bundle savedInstanceState) {

    }

    @Override
    protected ActivityRecordManagementBinding getBindView(LayoutInflater layoutInflater) {
        return ActivityRecordManagementBinding.inflate(layoutInflater);
    }

    @Override
    protected void bindData() {
        bindView.Toolbar.Title.setText(getString(R.string.str_recordsManagement));
        bindView.Toolbar.Menu.setVisibility(View.VISIBLE);

        adapter = new MyBaseAdapter<Record, AdapterPersonOrRecordItemBinding>() {
            @Override
            protected AdapterPersonOrRecordItemBinding getViewBinding(ViewGroup parent) {
                return AdapterPersonOrRecordItemBinding.inflate(getLayoutInflater());
            }

            @Override
            protected void bindData(AdapterPersonOrRecordItemBinding adapterPersonOrRecordItemBinding, Record record, int position) {
                if (record == null) {
                    return;
                }
                Bitmap bitmap = BitmapFactory.decodeFile(record.getPhotoFilePath());
                adapterPersonOrRecordItemBinding.Photo.setImageBitmap(bitmap);
                adapterPersonOrRecordItemBinding.Name.setText(record.getName());
                adapterPersonOrRecordItemBinding.Time.setText(TimeUtils.getDateToString4(record.getTime()));
                adapterPersonOrRecordItemBinding.Edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<Menu> menuList = new ArrayList<>();
                        menuList.add(new Menu(getString(R.string.str_cancel), R.drawable.cancel));
                        menuList.add(new Menu(getString(R.string.str_delete), R.drawable.delete));

                        DialogUtils.DialogMenu(context, getString(R.string.str_staffManagementMenu), menuList, new DialogUtils.CallbackDialogMenu() {
                            @Override
                            public void onPosition(int p, Dialog dialog) {
                                if (p == 0) {
                                    dialog.dismiss();
                                }
                                if (p == 1) {
                                    dialog.dismiss();
                                    BaseDialog<DialogConfirmOperationBinding> operationBindingBaseDialog = new BaseDialog<DialogConfirmOperationBinding>(context, R.style.common_dialog) {
                                        @Override
                                        protected DialogConfirmOperationBinding getViewBinding() {
                                            return DialogConfirmOperationBinding.bind(LayoutInflater.from(context).inflate(R.layout.dialog_confirm_operation, null, false));
                                        }

                                        @Override
                                        protected void bindData(DialogConfirmOperationBinding binding, Dialog dialog) {
                                            binding.Content.setText(getString(R.string.str_areYouSureYouWantToDeleteThisRecord));
                                        }

                                        @Override
                                        protected void bindListener(DialogConfirmOperationBinding binding, Dialog dialog) {
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
                                                    int delete = record.delete();
                                                    if (delete == 1) {
                                                        if (record != null && !TextUtils.isEmpty(record.getPhotoFilePath())) {
                                                            FileUtils.deleteFile(new File(record.getPhotoFilePath()));
                                                        }
                                                        adapter.remove(position);
                                                        showToast(getString(R.string.str_successfullyDeleted));
                                                    }else {
                                                        showToast(getString(R.string.str_failedToDelete));
                                                    }
                                                }
                                            });
                                        }
                                    };
                                    operationBindingBaseDialog.setCanceledOnTouchOutside(false);
                                    operationBindingBaseDialog.show();
                                }
                            }
                        });
                    }
                });
            }

            @Override
            protected void bindListener(AdapterPersonOrRecordItemBinding adapterPersonOrRecordItemBinding, Record record, int position) {

            }
        };

        bindView.RecordListView.setAdapter(adapter);
        new RefreshRecordAsyncTask().execute();


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
                List<Menu> menuList = new ArrayList<>();
                menuList.add(new Menu(getString(R.string.str_inquiry), R.drawable.search));
                menuList.add(new Menu(getString(R.string.str_import), R.drawable.import_export));
                menuList.add(new Menu(getString(R.string.str_export), R.drawable.import_export));

                DialogUtils.DialogMenu(context, getString(R.string.str_staffManagementMenu), menuList, new DialogUtils.CallbackDialogMenu() {
                    @Override
                    public void onPosition(int position, Dialog dialog) {
                        dialog.dismiss();
                    }
                });
            }
        });
        bindView.RefreshLayout.setRefreshHeader(new BezierRadarHeader(this).setEnableHorizontalDrag(true));
        bindView.RefreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        bindView.RefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                new RefreshRecordAsyncTask().execute();
            }
        });
        bindView.RefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                new LoadMoreRecordAsyncTask().execute();
            }
        });
        bindView.RecordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Record record = adapter.getItem(position);
                if (record == null) {
                    return;
                }
                List<ContentItem> contentItemList = new ArrayList<>();
                contentItemList.add(new ContentItem(getString(R.string.str_name), record.getName()));
                contentItemList.add(new ContentItem(getString(R.string.str_idCard), record.getIdCard()));
                contentItemList.add(new ContentItem(getString(R.string.str_icCard), record.getIcCard()));
                contentItemList.add(new ContentItem(getString(R.string.str_sex), record.isSex() ?
                        getString(R.string.str_male) : getString(R.string.str_female)));
                contentItemList.add(new ContentItem(getString(R.string.str_age), record.getAge() + ""));
                contentItemList.add(new ContentItem(getString(R.string.str_time), TimeUtils.getDateToString4(record.getTime())));
                Bitmap bitmap = BitmapFactory.decodeFile(record.getPhotoFilePath());
                contentItemList.add(new ContentItem(getString(R.string.str_comparisonScore), record.getScore() + ""));
                contentItemList.add(new ContentItem(getString(R.string.str_result), Record.getResult(context, record.isSuccess())));
                DialogUtils.DialogInfo(context, getString(R.string.str_recordInfo), bitmap, contentItemList, null);
            }
        });
    }


    private volatile int page = 1;


    private volatile int length = 20;


    private class RefreshRecordAsyncTask extends AsyncTask<Void, Integer, List<Record>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            page = 1;
        }

        @Override
        protected List<Record> doInBackground(Void... voids) {
            List<Record> recordList = LitePal.offset(0).limit(length).order("time desc").find(Record.class);
            LogUtils.i("记录数据条数:" + recordList.size());
            return recordList;
        }

        @Override
        protected void onPostExecute(List<Record> records) {
            super.onPostExecute(records);
            if (records != null) {
                adapter.setData(records);
                if (records.size() < length) {
                    bindView.RefreshLayout.finishRefreshWithNoMoreData();
                } else {
                    bindView.RefreshLayout.finishRefresh(true);
                }
            } else {
                bindView.RefreshLayout.finishRefresh(true);
            }

        }
    }

    private class LoadMoreRecordAsyncTask extends AsyncTask<Void, Integer, List<Record>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            page++;
        }

        @Override
        protected List<Record> doInBackground(Void... voids) {
            List<Record> recordList = LitePal.offset(page * length).limit(length).order("time desc").find(Record.class);
            LogUtils.i("记录数据条数:" + recordList.size());
            return recordList;
        }

        @Override
        protected void onPostExecute(List<Record> records) {
            super.onPostExecute(records);
            if (records != null) {
                adapter.addData(records);
                if (records.size() < length) {
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

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
import android.widget.AdapterView;

import androidx.annotation.NonNull;

import com.lyn.face_doorkeeper.R;
import com.lyn.face_doorkeeper.database.Person;
import com.lyn.face_doorkeeper.databinding.ActivityPersonManagementBinding;
import com.lyn.face_doorkeeper.databinding.AdapterPersonOrRecordItemBinding;
import com.lyn.face_doorkeeper.databinding.AdapterShowContentItemBinding;
import com.lyn.face_doorkeeper.databinding.DialogConfirmOperationBinding;
import com.lyn.face_doorkeeper.databinding.DialogEditPersonBinding;
import com.lyn.face_doorkeeper.databinding.DialogInfoBinding;
import com.lyn.face_doorkeeper.entity.ContentItem;
import com.lyn.face_doorkeeper.entity.Menu;
import com.lyn.face_doorkeeper.ui.adapter.MyBaseAdapter;
import com.lyn.face_doorkeeper.ui.dialog.BaseDialog;
import com.lyn.face_doorkeeper.ui.dialog.DialogUtils;
import com.lyn.face_doorkeeper.utils.FileUtils;
import com.lyn.face_doorkeeper.utils.TimeUtils;
import com.scwang.smart.refresh.footer.BallPulseFooter;
import com.scwang.smart.refresh.header.BezierRadarHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PersonManagementActivity extends BaseActivity<ActivityPersonManagementBinding> {

    private MyBaseAdapter<Person, AdapterPersonOrRecordItemBinding> personItemBindingMyBaseAdapter;

    @Override
    protected void init(Bundle savedInstanceState) {
    }

    @Override
    protected ActivityPersonManagementBinding getBindView(LayoutInflater layoutInflater) {
        return ActivityPersonManagementBinding.inflate(layoutInflater);
    }

    @Override
    protected void bindData() {
        bindView.Toolbar.Title.setText(getString(R.string.str_personnelManagement));
        bindView.Toolbar.Menu.setVisibility(View.VISIBLE);
        personItemBindingMyBaseAdapter = new MyBaseAdapter<Person, AdapterPersonOrRecordItemBinding>() {
            @Override
            protected AdapterPersonOrRecordItemBinding getViewBinding(ViewGroup parent) {
                return AdapterPersonOrRecordItemBinding.inflate(getLayoutInflater());
            }

            @Override
            protected void bindData(AdapterPersonOrRecordItemBinding adapterPersonItemBinding, Person person, int position) {
                Bitmap bitmap = null;
                if (TextUtils.isEmpty(person.getPath())) {
                    adapterPersonItemBinding.Photo.setBackground(getDrawable(R.drawable.photo));
                } else {
                    bitmap = BitmapFactory.decodeFile(person.getPath());
                    adapterPersonItemBinding.Photo.setImageBitmap(bitmap);
                }
                adapterPersonItemBinding.Name.setText(person.getName());
                adapterPersonItemBinding.Time.setText(TimeUtils.getDateToString4(person.getStartTime()));
                adapterPersonItemBinding.Edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<Menu> menuList = new ArrayList<>();
                        menuList.add(new Menu(getString(R.string.str_cancel), R.drawable.cancel));
                        menuList.add(new Menu(getString(R.string.str_edit), R.drawable.edit));
                        menuList.add(new Menu(getString(R.string.str_delete), R.drawable.delete));

                        DialogUtils.DialogMenu(context, getString(R.string.str_staffManagementMenu), menuList, new DialogUtils.CallbackDialogMenu() {
                            @Override
                            public void onPosition(int p, Dialog dialog) {
                                if (p==0){
                                    dialog.dismiss();
                                }
                                if (p==1){
                                    dialog.dismiss();
                                    Intent intent = new Intent();
                                    intent.putExtra("pid", person.getId());
                                    changeUiWithText(PersonAddActivity.class, intent);
                                    finish();
                                }
                                if (p==2){
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
                                                    int delete = person.delete();
                                                    if (delete == 1) {
                                                        personItemBindingMyBaseAdapter.remove(position);
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
            protected void bindListener(AdapterPersonOrRecordItemBinding adapterPersonItemBinding, Person person, int position) {

            }
        };
        bindView.PersonListView.setAdapter(personItemBindingMyBaseAdapter);
        new RefreshPersonAsyncTask().execute();
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
                menuList.add(new Menu(getString(R.string.str_add_person), R.drawable.add_person));
                menuList.add(new Menu(getString(R.string.str_inquiry), R.drawable.search));
                menuList.add(new Menu(getString(R.string.str_import), R.drawable.import_export));
                menuList.add(new Menu(getString(R.string.str_export), R.drawable.import_export));

                DialogUtils.DialogMenu(context, getString(R.string.str_staffManagementMenu), menuList, new DialogUtils.CallbackDialogMenu() {
                    @Override
                    public void onPosition(int position, Dialog dialog) {
                        dialog.dismiss();
                        if (position == 0) {
                            changeUI(PersonAddActivity.class);
                            finish();
                        }
                    }
                });
            }
        });


        bindView.RefreshLayout.setRefreshHeader(new BezierRadarHeader(this).setEnableHorizontalDrag(true));
        bindView.RefreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        bindView.RefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                new RefreshPersonAsyncTask().execute();
            }
        });
        bindView.RefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                new LoadMorePersonAsyncTask().execute();
            }
        });

        bindView.PersonListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Person person = personItemBindingMyBaseAdapter.getItem(position);
                if (person == null) {
                    return;
                }
                List<ContentItem> contentItemList = new ArrayList<>();
                contentItemList.add(new ContentItem(getString(R.string.str_name), person.getName()));
                contentItemList.add(new ContentItem(getString(R.string.str_idCard), person.getIdCard()));
                contentItemList.add(new ContentItem(getString(R.string.str_icCard), person.getIcCard()));
                contentItemList.add(new ContentItem(getString(R.string.str_sex), person.isSex() ?
                        getString(R.string.str_male) : getString(R.string.str_female)));
                contentItemList.add(new ContentItem(getString(R.string.str_age), person.getAge() + ""));
                contentItemList.add(new ContentItem(getString(R.string.str_time), TimeUtils.getDateToString4(person.getStartTime())));
                Bitmap bitmap = BitmapFactory.decodeFile(person.getPath());
                DialogUtils.DialogInfo(context, getString(R.string.str_personInfo), bitmap, contentItemList, null);

            }
        });
    }

    private volatile int page = 1;


    private volatile int length = 20;


    private class RefreshPersonAsyncTask extends AsyncTask<Void, Integer, List<Person>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            page = 1;
        }

        @Override
        protected List<Person> doInBackground(Void... voids) {
            List<Person> personList = LitePal.offset(page * length - length).limit(length).order("startTime desc").find(Person.class);
            return personList;
        }

        @Override
        protected void onPostExecute(List<Person> people) {
            super.onPostExecute(people);
            if (people != null) {
                personItemBindingMyBaseAdapter.setData(people);
                if (people.size() < length) {
                    bindView.RefreshLayout.finishRefreshWithNoMoreData();
                } else {
                    bindView.RefreshLayout.finishRefresh(true);
                }
            } else {
                bindView.RefreshLayout.finishRefresh(true);
            }
        }
    }


    private class LoadMorePersonAsyncTask extends AsyncTask<Void, Integer, List<Person>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            page++;
        }

        @Override
        protected List<Person> doInBackground(Void... voids) {
            List<Person> personList = LitePal.offset(page * length - length).limit(length).order("time desc").find(Person.class);
            return personList;
        }

        @Override
        protected void onPostExecute(List<Person> people) {
            super.onPostExecute(people);
            if (people != null) {
                personItemBindingMyBaseAdapter.addData(people);
                if (people.size() < length) {
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

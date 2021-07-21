package com.lyn.face_doorkeeper.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TimePicker;

import com.lyn.face_doorkeeper.R;
import com.lyn.face_doorkeeper.databinding.AdapterListMenuItemBinding;
import com.lyn.face_doorkeeper.databinding.AdapterShowContentItemBinding;
import com.lyn.face_doorkeeper.databinding.DialogInfoBinding;
import com.lyn.face_doorkeeper.databinding.DialogMenuBinding;
import com.lyn.face_doorkeeper.databinding.DialogSelectTimeBinding;
import com.lyn.face_doorkeeper.entity.ContentItem;
import com.lyn.face_doorkeeper.entity.Menu;
import com.lyn.face_doorkeeper.ui.adapter.MyBaseAdapter;
import com.lyn.face_doorkeeper.utils.LogUtils;
import com.lyn.face_doorkeeper.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class DialogUtils {

    /**
     * 显示信息详情
     *
     * @param context
     * @param title
     * @param bitmap
     * @param contentItemList
     */
    public static void DialogInfo(Context context, final String title, final Bitmap bitmap, final List<ContentItem> contentItemList, AdapterView.OnItemClickListener onItemClickListener) {
        BaseDialog<DialogInfoBinding> personInfoBindingBaseDialog =
                new BaseDialog<DialogInfoBinding>(context, R.style.common_dialog) {
                    @Override
                    protected DialogInfoBinding getViewBinding() {
                        return DialogInfoBinding.bind(LayoutInflater.from(context).inflate(R.layout.dialog_info, null, false));
                    }

                    @Override
                    protected void bindData(DialogInfoBinding binding, Dialog dialog) {
                        binding.Title.setText(title);
                        binding.HeaderPhoto.setImageBitmap(bitmap);
                        MyBaseAdapter<ContentItem, AdapterShowContentItemBinding> contentItemBindingMyBaseAdapter
                                = new MyBaseAdapter<ContentItem, AdapterShowContentItemBinding>() {
                            @Override
                            protected AdapterShowContentItemBinding getViewBinding(ViewGroup parent) {
                                return AdapterShowContentItemBinding.bind(LayoutInflater.from(context).inflate(R.layout.adapter_show_content_item, null, false));
                            }

                            @Override
                            protected void bindData(AdapterShowContentItemBinding adapterShowContentItemBinding, ContentItem contentItem, int position) {
                                adapterShowContentItemBinding.Label.setText(contentItem.getLabel());
                                adapterShowContentItemBinding.Text.setText(contentItem.getText());
                            }

                            @Override
                            protected void bindListener(AdapterShowContentItemBinding adapterShowContentItemBinding, ContentItem contentItem, int position) {

                            }
                        };
                        contentItemBindingMyBaseAdapter.setData(contentItemList);
                        binding.ContentList.setAdapter(contentItemBindingMyBaseAdapter);
                        if (onItemClickListener != null) {
                            binding.ContentList.setOnItemClickListener(onItemClickListener);
                        }
                    }

                    @Override
                    protected void bindListener(DialogInfoBinding binding, Dialog dialog) {

                    }
                };
        personInfoBindingBaseDialog.show();
    }


    /**
     * 显示操作菜单
     *
     * @param context
     * @param title
     * @param menuList
     * @param callbackDialogMenu
     */
    public static void DialogMenu(Context context, String title, List<Menu> menuList, CallbackDialogMenu callbackDialogMenu) {
        BaseDialog<DialogMenuBinding> menuBindingBaseDialog = new BaseDialog<DialogMenuBinding>(context, R.style.common_dialog) {
            @Override
            protected DialogMenuBinding getViewBinding() {
                return DialogMenuBinding.bind(LayoutInflater.from(context).inflate(R.layout.dialog_menu, null, false));
            }

            @Override
            protected void bindData(DialogMenuBinding binding, Dialog dialog) {
                MyBaseAdapter<Menu, AdapterListMenuItemBinding> itemBindingMyBaseAdapter = new MyBaseAdapter<Menu, AdapterListMenuItemBinding>() {
                    @Override
                    protected AdapterListMenuItemBinding getViewBinding(ViewGroup parent) {
                        return AdapterListMenuItemBinding.inflate(getLayoutInflater());
                    }

                    @Override
                    protected void bindData(AdapterListMenuItemBinding adapterListMenuItemBinding, Menu menu, int position) {
                        adapterListMenuItemBinding.Icon.setImageDrawable(context.getDrawable(menu.getResID()));
                        adapterListMenuItemBinding.Label.setText(menu.getTitle());
                    }

                    @Override
                    protected void bindListener(AdapterListMenuItemBinding adapterListMenuItemBinding, Menu menu, int position) {

                    }
                };
                itemBindingMyBaseAdapter.setData(menuList);
                binding.MenuListView.setAdapter(itemBindingMyBaseAdapter);


            }

            @Override
            protected void bindListener(DialogMenuBinding binding, Dialog dialog) {
                binding.MenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (callbackDialogMenu!=null){
                            callbackDialogMenu.onPosition(position,dialog);
                        }
                    }
                });
            }
        };
        menuBindingBaseDialog.show();
    }

    /**
     * 时间选择器
     *
     * @param context
     * @param title
     * @param callbackTime
     */
    public static void DialogSelectTime(Context context, String title, CallbackTime callbackTime) {
        BaseDialog<DialogSelectTimeBinding> timeBindingBaseDialog = new BaseDialog<DialogSelectTimeBinding>(context, R.style.common_dialog) {
            @Override
            protected DialogSelectTimeBinding getViewBinding() {
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_select_time, null, false);
                return DialogSelectTimeBinding.bind(view);
            }

            @Override
            protected void bindData(DialogSelectTimeBinding binding, Dialog dialog) {
                binding.timePicker.setIs24HourView(true);
                if (!TextUtils.isEmpty(title)) {
                    binding.Title.setText(title);
                }
            }

            @Override
            protected void bindListener(DialogSelectTimeBinding binding, Dialog dialog) {
                binding.timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        long time = view.getDrawingTime();
                        LogUtils.i("时间选择:" + time);
                        if (callbackTime != null) {
                            callbackTime.onTime(hourOfDay + ":" + minute);
                        }
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
                    }
                });
            }
        };
        timeBindingBaseDialog.show();
    }


    public interface CallbackTime {
        void onTime(String time);
    }


    public interface CallbackDialogMenu{
        void onPosition(int position,Dialog dialog);
    }

}

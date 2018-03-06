package com.weapp.zlf.weapp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.bean.HabitBean;
import com.weapp.zlf.weapp.common.utils.Constant;
import com.weapp.zlf.weapp.common.utils.FileUtils;
import com.weapp.zlf.weapp.common.utils.ImageUtils;
import com.weapp.zlf.weapp.common.utils.ToastUtils;
import com.weapp.zlf.weapp.common.utils.Utils;
import com.weapp.zlf.weapp.ui.widge.HabitAddDialog;
import com.weapp.zlf.weapp.ui.widge.ParallaxImageView;
import com.weapp.zlf.weapp.ui.widge.ParallaxViewHolder;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhuliangfei on 2018/2/27.
 */

@ContentView(R.layout.activity_habit_list)
public class HabitListActivity extends BaseActivity{
    @ViewInject(R.id.tv_title_name)
    private TextView mTvTitle;
    @ViewInject(R.id.iv_title_right)
    private ImageView mIvRight;
    @ViewInject(R.id.rv_list)
    private RecyclerView mRvList;

    public static void launch(Context context) {
        context.startActivity(new Intent(context, HabitListActivity.class));
    }

    @Override
    protected void initView() {
        super.initView();
        mTvTitle.setText(getString(R.string.habit));
        mIvRight.setImageResource(R.drawable.icon_add_todo);
        initList();
        getData();
    }

    private void getData() {
        Observable.create(new ObservableOnSubscribe<List<HabitBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<HabitBean>> observableEmitter) throws Exception {
                List<HabitBean> list = Utils.getContext().getDbManager().selector(HabitBean.class).findAll();
                if (list == null)
                    list = new ArrayList<>();
                observableEmitter.onNext(list);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<HabitBean>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {

                    }

                    @Override
                    public void onNext(List<HabitBean> habitBeans) {
                        if (habitBeans.isEmpty()) {
                            ToastUtils.showShortToast("没有习惯，你是猪吗？");
                        } else {
                            mRvList.setAdapter(new MyAdapter(HabitListActivity.this, habitBeans));
//                            mAdapter.setNewData(habitBeans);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void initList() {
        mRvList.setLayoutManager(new LinearLayoutManager(this));

        /*
          mAdapter = new HabitListAdapter(null);
        mRvList.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final BaseQuickAdapter adapter, View view, final int position) {

                Observable.create(new ObservableOnSubscribe<HabitUserBean>() {
                    @Override
                    public void subscribe(ObservableEmitter<HabitUserBean> observableEmitter) throws Exception {
                        HabitUserBean bean = Utils.getContext().getDbManager().selector(HabitUserBean.class)
                                .where("habit_id", "=", ((HabitBean) adapter.getItem(position)).getId())
                                .findFirst();
                        if (bean == null)
                            bean = new HabitUserBean();
                        observableEmitter.onNext(bean);
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<HabitUserBean>() {
                            @Override
                            public void onSubscribe(Disposable disposable) {

                            }

                            @Override
                            public void onNext(HabitUserBean habitUserBean) {
                                HabitDevelopingActivity.launch(HabitListActivity.this);
                                HabitUserDataDialog dialog = new HabitUserDataDialog(HabitListActivity.this, habitUserBean);
                                dialog.show();
                            }

                            @Override
                            public void onError(Throwable throwable) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });

            }
        });*/
    }

    @Event(R.id.iv_title_right)
    private void rightClick(View view) {

        HabitAddDialog dialog = new HabitAddDialog(this);
        dialog.show();
    }

    @Event(R.id.iv_title_left)
    private void leftClick(View view) {
        finish();
    }

    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final Context context;
        private final List<HabitBean> list;
        private final List<File> mImages;
        private Integer[] mPics = new Integer[] {R.drawable.car_1,R.drawable.car_2,R.drawable.car_3,R.drawable.car_4,R.drawable.car_5};

        public MyAdapter(Context context, List<HabitBean> list) {
            this.context = context;
            this.list = list;
            mImages = FileUtils.listFilesInDir(Constant.DIR_WALLPAPER);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, null));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MyViewHolder myHolder = (MyViewHolder) holder;
            HabitBean item = list.get(position);
           /* if (!mImages.isEmpty()) {

                Glide.with(context)
                        .load(mImages.get(position % mImages.size()).getAbsolutePath())
                        .into(myHolder.img);
            } else {
                Glide.with(context).load(R.color.colorPrimary).into(myHolder.img);
            }*/
//            myHolder.img.setImageDrawable(context.getResources().getDrawable(R.drawable.test3));
            ;
//            myHolder.img.setImageDrawable(BitmapDrawable.createFromPath(mImages.get(0).getAbsolutePath()));
//            Glide.with(context).load(mImages.get(0)).into(myHolder.img);
            /*try {
                myHolder.img.setImageBitmap(Glide.with(context).asBitmap().load(mImages.get(0)).submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }*/

//            x.image().bind(myHolder.img, mImages.get(position % mImages.size()).toString());
            myHolder.img.setImageResource(mPics[position % mPics.length]);

            myHolder.tv_min.setText(item.getMinutePerTime() + " 分钟");
            myHolder.tv_name.setText(item.getTitle());
            myHolder.img.reuse();
            myHolder.img.setParallaxValue(10f);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public class MyViewHolder extends ParallaxViewHolder {
        public ParallaxImageView img;
        public  TextView tv_name;
        public  TextView tv_min;

        @Override
        public int getParallaxImageId() {
            return R.id.iv;
        }

        public MyViewHolder(View itemView) {
            super(itemView);
            img = (ParallaxImageView) itemView.findViewById(R.id.iv);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_min = (TextView) itemView.findViewById(R.id.tv_min);
        }
    }

}

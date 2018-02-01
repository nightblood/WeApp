package com.weapp.zlf.weapp.ui.widge.popupwindow;

import android.content.Context;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.common.utils.SizeUtils;

/**
 * Created by zhuliangfei on 2017/12/21.
 */

public class MenuPopup extends EasyPopup {
    public MenuPopup(Context context, String content) {
        super(context);
        init(content);
    }

    private void init(String str) {
        setContentView(R.layout.layout_right_pop);
        setAnimationStyle(R.style.QQPopAnim);
        setFocusAndOutsideEnable(true);
        setBackgroundDimEnable(true);
        setDimValue(0.5f);
        createPopup();
        TextView content = (TextView) mContentView.findViewById(R.id.tv_content);
        if (TextUtils.isEmpty(str)) {
            content.setText("没有填写内容哦。。。");
        } else {
            content.setText(str);
        }
       /* mContentView.findViewById(R.id.ll_menu_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommunicationBookActivity.launch(getContext());
                dismiss();
            }
        });
        mContentView.findViewById(R.id.ll_menu_chat_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoticeMsgSetting.launch(getContext());
                dismiss();
            }
        });*/
    }

    public void show(View view) {
        showAtAnchorView(view, VerticalGravity.BELOW, HorizontalGravity.CENTER, 0, -SizeUtils.dp2px(5));
    }
}

package com.ormediagroup.bpo.receiver;

import android.content.Context;
import android.util.Log;

import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

/**
 * Created by Lau on 2018/7/24.
 */

public class MessageReceiver extends XGPushBaseReceiver {
    @Override
    public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {
        if (context == null || xgPushRegisterResult == null) {
            return;
        }
        String text = "";
        if (i == XGPushBaseReceiver.SUCCESS) {
            text = i + "注册成功";
            // 在这里拿token
            String token = xgPushRegisterResult.getToken();
//            MainActivity.token = token;
            Log.i("ORM", "onRegisterResult: token => "+token);
        } else {
            text = xgPushRegisterResult + "注册失败错误码：" + i;
        }
    }

    @Override
    public void onUnregisterResult(Context context, int i) {

    }

    @Override
    public void onSetTagResult(Context context, int i, String s) {

    }

    @Override
    public void onDeleteTagResult(Context context, int i, String s) {

    }

    @Override
    public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {

    }

    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {
        Log.i("ORM", "onNotifactionClickedResult: " + xgPushClickedResult.toString());
    }

    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {
        Log.i("ORM", xgPushShowedResult.toString());
    }

}

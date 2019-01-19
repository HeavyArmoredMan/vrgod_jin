package com.app360.app360.wxapi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.app360.app360.R;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class WXPayActivity extends AppCompatActivity {
    OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
    private static final String TAG = "PayActivity";
    public static final String APP_ID = "wxd723e26c73ec86a8";
    public static final String PARTNER_ID = "1520174131";
    public static final String PACKAGE_VALUE = "Sign=WXPay";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxpay);
        final IWXAPI wxapi = WXAPIFactory.createWXAPI(this, APP_ID, false);
        final Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setEnabled(false);
                Log.i("jin", "111111111111");

                RequestBody body = new FormBody.Builder()
                        .add("userId", "1")
                        .add("totalFee", "1")
                        .build();
                String url = "http://39.105.189.61/v1/weixin/apppay.json";
                Request request = new Request.Builder().url(url).post(body).build();
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                button.setEnabled(true);
                                Toast.makeText(WXPayActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            try {
                                String body = response.body().string();
                                Log.i("jin", "response.body().string() = " + body);
                                JSONObject jsonObject = new JSONObject(body);
                                Log.i(TAG, jsonObject.toString());
                                //nt code = jsonObject.getInt("code");
                                //if (code == 0) {


                                // JSONObject data = jsonObject.getJSONObject("data");
                                String appId = jsonObject.getString("appid");
                                String partnerId = jsonObject.getString("partnerid");
                                String prepayId = jsonObject.getString("prepayid");
                                String packageValue = jsonObject.getString("package");
                                String nonceStr = jsonObject.getString("noncestr");
                                String timeStamp = jsonObject.getString("timestamp");
                                String extData = jsonObject.getString("extdata");
                                String sign = jsonObject.getString("sign");
                                PayReq req = new PayReq();
                                req.appId = appId;
                                req.partnerId = partnerId;
                                req.prepayId = prepayId;
                                req.packageValue = packageValue;
                                req.nonceStr = nonceStr;
                                req.timeStamp = timeStamp;
                                req.extData = extData;
                                req.sign = sign;
                                final boolean result = wxapi.sendReq(req);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        Toast.makeText(WXPayActivity.this, "调起支付结果:" + result, Toast.LENGTH_LONG).show();
                                    }
                                });

//
                                // } else {
                                //Toast.makeText(WXPayActivity.this, "数据出错", Toast.LENGTH_LONG).show();
                                // }
                            } catch (JSONException e) {
                                Log.i("jin", "err", e);
                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                button.setEnabled(true);
                            }
                        });

                    }
                });
            }
        });
    }
}

package com.ormediagroup.bpo.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ormediagroup.bpo.R;
import com.ormediagroup.bpo.network.JSONResponse;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by Lau on 2018/9/6.
 */

public class LoginFragment extends BaseFragment {
    private LinearLayout login_layout;
    private View view;
    private EditText login_email, login_pass;
    private Button login;
    private CheckBox remember_info;

    private String LOGIN_URL = "http://120.79.165.149/bpo/app-login/";
    private SharedPreferences sp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, null);
        initView();
        initData();
        return view;
    }

    private void initData() {
        sp = mActivity.getSharedPreferences("user_info", Context.MODE_PRIVATE);

        login_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(INPUT_METHOD_SERVICE);
                return imm.hideSoftInputFromWindow(login_layout.getWindowToken(), 0);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = login_email.getText().toString().trim();
                final String password = login_pass.getText().toString().trim();
                Log.i("ORM", "onClick: userinfo " + username + " " + password);
                if (username.length() > 0 && password.length() > 0) {
                    final String param = "action=login&login_email=" + username + "&login_psw=" + password;
                    new JSONResponse(mActivity, LOGIN_URL, param, new JSONResponse.onComplete() {
                        @Override
                        public void onComplete(JSONObject json) {
                            Log.i("ORM", "onComplete: login res => " + json);
                            try {
                                String error = json.get("error").toString();
                                if (error == "false") {
                                    int userid = json.getInt("orm_id");

                                    SharedPreferences.Editor editor = sp.edit();
                                    // ...保存用户id
                                    editor.putInt("userid", userid);
                                    editor.putString("username", username);
                                    if (remember_info.isChecked()) {
                                        editor.putBoolean("remember", true);
                                    } else {
                                        editor.putBoolean("remember", false);
                                    }
                                    editor.commit();

                                    Toast.makeText(mActivity, getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                                    Log.i("ORM", "onComplete: userid=>" + sp.getInt("userid", 0) + " username=>" + sp.getString("username", "") + " remember=>" + sp.getBoolean("remember", false));

                                    mActivity.recreate();
//                                    setOnLoginFragmentListener solfl = (setOnLoginFragmentListener) mActivity;
//                                    if (solfl!=null){
//                                        solfl.toPage(1);
//                                    }
                                } else {
                                    Toast.makeText(mActivity, getResources().getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } else {
                    Toast.makeText(mActivity, getResources().getString(R.string.login_notnull), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initView() {
        login_layout = view.findViewById(R.id.login_layout);
        login_email = view.findViewById(R.id.login_email);
        login_pass = view.findViewById(R.id.login_pass);
        login = view.findViewById(R.id.btn_login);
        remember_info = view.findViewById(R.id.cb_remember);
    }

//    public interface setOnLoginFragmentListener{
//        void toPage(int p);
//    }

}

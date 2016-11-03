package com.subzero.jinmoaz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private TextView apkPathText;
    private String apk_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apkPathText = (TextView) findViewById(R.id.apkPathText);


    }

    /**
     * 选择文件
     *
     * @param view
     */
    public void onChooseApkFile(View view) {
        Intent intent = new Intent(this, FileExplorerActivity.class);
        startActivityForResult(intent, 0);
    }

    /**
     * 秒装
     *
     * @param view
     */
    public void onSilentInstall(View view) {
        if (!isRoot()) {
            Toast.makeText(MainActivity.this, "您没有获取ROOT权限，请使用智能安装", Toast.LENGTH_SHORT).show();
        } else {
            if (TextUtils.isEmpty(apk_path)) {
                Toast.makeText(MainActivity.this, "请选择您的安装包", Toast.LENGTH_SHORT).show();
            } else {
                final Button button = (Button) view;
                button.setText("安装中");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SilentInstall installHelper = new SilentInstall();
                        final boolean result = installHelper.install(apk_path);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (result) {
                                    Toast.makeText(MainActivity.this, "安装成功！", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "安装失败！", Toast.LENGTH_SHORT).show();
                                }
                                button.setText("秒装");
                            }
                        });

                    }
                }).start();
            }
        }
    }

    /**
     * 开启智能服务
     *
     * @param view
     */
    public void onForwardToAccessibility(View view) {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }

    /**
     * 智能安装
     *
     * @param view
     */
    public void onSmartInstall(View view) {
        if (TextUtils.isEmpty(apk_path)) {
            Toast.makeText(this, "请选择安装包！", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri uri = Uri.fromFile(new File(apk_path));
        Intent localIntent = new Intent(Intent.ACTION_VIEW);
        localIntent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(localIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            apk_path = data.getStringExtra("apk_path");
            apkPathText.setText(apk_path);
        }
    }


    /**
     * 判断手机是否拥有Root权限。
     *
     * @return 有root权限返回true，否则返回false。
     */
    public boolean isRoot() {
        boolean bool = false;
        try {
            bool = new File("/system/bin/su").exists() || new File("/system/xbin/su").exists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bool;
    }

}

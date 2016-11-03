package com.subzero.jinmoaz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fengfeng on 2016/11/3.
 */
public class FileExplorerActivity extends AppCompatActivity {

    private List<Map<String, Object>> mapList = new ArrayList<>();
    private SimpleAdapter adapter;

    private String rootPath = Environment.getExternalStorageDirectory().getPath();
    private String currentPath = rootPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_explorer);

        ListView listView = (ListView) findViewById(R.id.list_view);
        adapter = new SimpleAdapter(this, mapList, R.layout.list_item, new String[]{"name", "img"}, new int[]{R.id.name, R.id.img});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentPath = (String) mapList.get(position).get("currentPath");
                File file = new File(currentPath);
                if (file.isDirectory()) {
                    refreshListItems(currentPath);
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("apk_path", file.getPath());
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        });

        refreshListItems(currentPath);
    }

    private void refreshListItems(String path) {
        setTitle(path);
        File[] files = new File(path).listFiles();
        mapList.clear();

        if (files != null) {
            for (File file : files) {
                Map<String, Object> map = new HashMap<>();
                if (file.isDirectory()) {
                    map.put("img", R.drawable.file_explorer);
                } else {
                    map.put("img", R.drawable.file_doc);
                }
                map.put("name", file.getName());
                map.put("currentPath", file.getPath());

                mapList.add(map);

            }

            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onBackPressed() {

        if (rootPath.equals(currentPath)) {
            super.onBackPressed();
        } else {
            File file = new File(currentPath);
            currentPath = file.getParentFile().getPath();
            refreshListItems(currentPath);
        }

    }
}

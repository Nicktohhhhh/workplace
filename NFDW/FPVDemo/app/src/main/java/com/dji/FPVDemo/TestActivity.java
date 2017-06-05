package com.dji.FPVDemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dji.FPVDemo.model.MenuDialogFragment;
import com.dji.FPVDemo.model.Test;
import com.dji.FPVDemo.utils.ChangeOrientationHandler;
import com.dji.FPVDemo.utils.MyOpenHelper;
import com.dji.FPVDemo.utils.OrientationSensorListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class TestActivity extends AppCompatActivity {
    private MyOpenHelper myOpenHelper;
    //20170221
//    private List<Test> list;


    private ListView lv;

    //20140221 begin
    private SimpleAdapter simpleAdapter;
    private Map<String, Object> map;
    List<Map<String, Object>> list;
    //end
    private List id_delete;
    private SensorManager sensorManager;
    private Sensor sensor;
    private Handler handler;
    private OrientationSensorListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hugo set full screen 20161028
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //end 20161028

        setContentView(R.layout.testactivity_data);
        myOpenHelper = new MyOpenHelper(getApplicationContext());
        lv = (ListView) findViewById(R.id.lv);
        list = new ArrayList<Map<String, Object>>();
        id_delete=new LinkedList();

        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        handler = new ChangeOrientationHandler(this);
        listener = new OrientationSensorListener(handler);
        sensorManager.registerListener(listener,sensor,sensorManager.SENSOR_DELAY_UI);

        show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView sign_csg = (TextView) view.findViewById(R.id.sign_csg);

                String sign = String.valueOf(sign_csg.getText());
                Intent intent = new Intent(TestActivity.this, ShowDataActivity.class);
                intent.putExtra("sign_csg", sign);
                startActivity(intent);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                          @Override
                                          public boolean onItemLongClick(AdapterView<?> parent, View view,
                                                                         final int position, long id) {
                                              AlertDialog.Builder builder = new AlertDialog.Builder(TestActivity.this);
                                              builder.setMessage("确认删除？");
                                              builder.setTitle("提示");
                                              TextView sign_csg = (TextView) view.findViewById(R.id.sign_csg);
                                              final String sign = String.valueOf(sign_csg.getText());

                                              builder.setPositiveButton("确定？", new DialogInterface.OnClickListener() {
                                                  @Override
                                                  public void onClick(DialogInterface dialog, int which) {
                                                      if (list.remove(position) != null) {
                                                          System.out.println("success");

                                                          id_delete.add(sign);

                                                      } else {
                                                          System.out.println("fail");
                                                      }
                                                      simpleAdapter.notifyDataSetChanged();
//                                                      Toast.makeText(getBaseContext()
//                                                              , "删除列表项", Toast.LENGTH_SHORT).show();
                                                  }
                                              });
                                              builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                  @Override
                                                  public void onClick(DialogInterface dialog, int which) {
                                                  }
                                              });
                                              builder.create().show();
                                              return false;
                                          }
                                      }

        );

    }

    public void show() {
        SQLiteDatabase db = myOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("catalog", null,
                null, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            list.clear();
            cursor.moveToNext();
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String time = cursor.getString(1);
                String dist1 = cursor.getString(2);
                String dist2 = cursor.getString(3);
                String place = cursor.getString(4);
                String sign_number = cursor.getString(5);
                String shortest_dis = cursor.getString(6);

                Log.d("A", id + "" + time + " " + dist1 + " " + dist2 + " " + place + " " + sign_number + " " + shortest_dis);

                map = new HashMap<String, Object>();
                map.put("id", id);
                map.put("time", time);
                map.put("dist1", dist1);
                map.put("dist2", dist2);
                map.put("place", place);
                map.put("sign_number", sign_number);
                map.put("shortest_dis", shortest_dis);
                list.add(map);
                Log.d("A", "cursor has data");
            }
//        lv.setAdapter(new TestActivity.MyAdapter());
            simpleAdapter = new SimpleAdapter(this, list, R.layout.item1,
                    new String[]{"sign_number", "place", "time"}, new int[]{R.id.sign_csg, R.id.tv_title, R.id.tv_time});
            lv.setAdapter(simpleAdapter);
        }
        db.close();//可能需要注释
        Log.d("A", "click2");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SQLiteDatabase db = myOpenHelper.getReadableDatabase();
        for(int i=0;i<id_delete.size();i++){
            String temp_sign=String.valueOf(id_delete.get(i));
            db.delete("catalog","sign=?",new String[]{temp_sign});
            db.delete("info","sign=?",new String[]{temp_sign});
        }
        db.close();
    }

    @Override
    public void onPause(){
//        sensorManager.unregisterListener(sensorEventListener);
        sensorManager.unregisterListener(listener);
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        sensorManager.registerListener(listener,sensor,sensorManager.SENSOR_DELAY_UI);
        super.onResume();
    }

}
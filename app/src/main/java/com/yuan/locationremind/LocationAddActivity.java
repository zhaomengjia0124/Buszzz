package com.yuan.locationremind;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yuan.locationremind.db.LocationDao;
import com.yuan.locationremind.entity.LocationEntity;
import com.yuan.locationremind.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Yuan on 20/10/2016:1:03 PM.
 * <p/>
 * Description:com.yuan.locationremind.LocationPointActivity
 */

public class LocationAddActivity extends AppCompatActivity {


    private static int REQUEST_CODE = 1;
    @BindView(R.id.addressEt)
    EditText mAddressEt;
    @BindView(R.id.latitudeEt)
    EditText mLatitudeEt;
    @BindView(R.id.longitudeEt)
    EditText mLongitudeEt;
    LocationEntity entity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("添加提醒");
        setContentView(R.layout.activity_location_address);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, 1, 1, "搜索").setIcon(R.mipmap.ic_search).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(2, 1, 1, "确定").setIcon(R.mipmap.ic_save).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getGroupId()) {
            case 1:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivityForResult(intent, REQUEST_CODE);

                break;
            case 2:
                saveLocation();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            entity = (LocationEntity) data.getSerializableExtra("entity");
            if (entity != null) {
                mLatitudeEt.setText(String.valueOf(entity.getLatitude()));
                mLongitudeEt.setText(String.valueOf(entity.getLongitude()));
                mAddressEt.setText(String.valueOf(entity.getAddress()));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveLocation() {
        VerifyInfo verifyInfo = new VerifyInfo().invoke();
        if (verifyInfo.is()) return;
        final double la = verifyInfo.getLa();
        final double lo = verifyInfo.getLo();
        setName(la, lo);

    }

    private void setName(final double la, final double lo) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("请输入标签");

        LinearLayout inputLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.input_name, null);
        builder.setView(inputLayout);
        final EditText editText = (EditText) inputLayout.findViewById(R.id.inputNameEt);
        final String address = mAddressEt.getText().toString();
        editText.setText(address);
        editText.setSelection(0, address.length());
        editText.requestFocus();
        builder.setPositiveButton("确定", null);
        builder.setNegativeButton("取消", null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = editText.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(LocationAddActivity.this, "标签不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }

                alertDialog.dismiss();
                updateDao(la, lo, editText.getText().toString());
                setResult(RESULT_OK);
                finish();
            }
        });



    }



    private void updateDao(double la, double lo, String name) {
        if (entity == null) {
            entity = new LocationEntity();
        }
        entity.setLatitude(la);
        entity.setLongitude(lo);
        entity.setName(name);
        entity.setAddress(mAddressEt.getText().toString());
        entity.setInterval(Constants.REFRESH_INTERVAL);
        entity.setRadius(Constants.RADIUS);

        LocationDao dao = new LocationDao(this);
        dao.insert(entity);
    }

    private class VerifyInfo {
        private boolean myResult;
        private double la;
        private double lo;

        boolean is() {
            return myResult;
        }

        double getLa() {
            return la;
        }

        double getLo() {
            return lo;
        }

        VerifyInfo invoke() {
            String latitude = mLatitudeEt.getText().toString();
            String longitude = mLongitudeEt.getText().toString();
            if (TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude)) {
                Toast.makeText(LocationAddActivity.this, "经度或纬度不能为空", Toast.LENGTH_SHORT).show();
                myResult = true;
                return this;
            }
            la = Double.parseDouble(latitude);
            lo = Double.parseDouble(longitude);
            if (la > 360 || la < 0) {
                Toast.makeText(LocationAddActivity.this, "纬度超出范围", Toast.LENGTH_SHORT).show();
                myResult = true;
                return this;
            }
            if (lo > 360 || lo < 0) {
                Toast.makeText(LocationAddActivity.this, "经度超出范围", Toast.LENGTH_SHORT).show();
                myResult = true;
                return this;
            }
            myResult = false;
            return this;
        }
    }
}

/**
 *
 */
package com.yuan.locationremind;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class CheckPermissionsActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.SYSTEM_ALERT_WINDOW
    };
    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;
    private PermissionListener mPermissionListener;

    @Override
    protected void onResume() {
        super.onResume();
        if (isNeedCheck) {
            mPermissionListener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {
                    isNeedCheck = false;
                }

                @Override
                public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                    Toast.makeText(CheckPermissionsActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                }
            };
            checkPermissions();
        }
    }

    private void checkPermissions() {
        new TedPermission(this)
                .setPermissionListener(mPermissionListener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(needPermissions)
                .check();
    }
}

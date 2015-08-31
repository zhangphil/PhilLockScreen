package zhangfei.lockscreen;

import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import zhangfei.lockscreen.R;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private final String SharedPreferencesName = "PhilLockScreen";
	private final String FIRST_STARTUP = "first_startup";
	private final int ACTIVE_REQUEST_CODE = 100;

	private DevicePolicyManager policyManager;
	private ComponentName componentName;

	private ImageView imageView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);

		componentName = new ComponentName(this, PhilDeviceAdminReceiver.class);
		policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

		if (!isFirstStartup() && policyManager.isAdminActive(componentName)) {
			lock();

			return;
		}

		imageView = (ImageView) findViewById(R.id.imageView);
		imageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (policyManager.isAdminActive(componentName)) {
					lock();
				} else {
					Log.d(this.getClass().getName(), "激活...");
					activeManager();
				}
			}
		});

		TextView tv = (TextView) findViewById(R.id.detail);
		tv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				String tips = "Designed by ZHANG FEI";
				String mail = "zhang.fei@msn.com";
				String[] items = { "Version Name: " + Tool.getVersionName(getApplication()),
						"Version Code: " + Tool.getVersionCode(getApplication()), tips, mail };

				AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle(R.string.app_name)
						.setItems(items, null).setPositiveButton("确 定", null).create();

				dialog.show();

			}
		});

		UmengUpdateAgent.update(this);
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (policyManager.isAdminActive(componentName)) {
			// 已经激活设备
			TextView tip = (TextView) findViewById(R.id.tip);
			tip.setVisibility(View.VISIBLE);
			imageView.setImageResource(R.drawable.lock);
			TextView tv = (TextView) findViewById(R.id.need_active);
			tv.setVisibility(View.GONE);
		} else {
			TextView tip = (TextView) findViewById(R.id.tip);
			tip.setVisibility(View.GONE);
			TextView tv = (TextView) findViewById(R.id.need_active);
			tv.setVisibility(View.VISIBLE);
			imageView.setImageResource(R.drawable.setting);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private void notAnyMoreFirstStartup() {
		SharedPreferences sp = this.getSharedPreferences(SharedPreferencesName, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean(FIRST_STARTUP, false);
		editor.commit();
	}

	private boolean isFirstStartup() {
		boolean first = false;
		SharedPreferences sp = this.getSharedPreferences(SharedPreferencesName, Context.MODE_PRIVATE);
		first = sp.getBoolean(FIRST_STARTUP, true);
		return first;
	}

	private void lock() {
		Log.d(this.getClass().getName(), "锁屏!");
		policyManager.lockNow();
		this.finish();
	}

	private void activeManager() {
		// 使用隐式意图调用系统方法来激活指定的设备管理器
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, R.string.app_name);
		startActivityForResult(intent, ACTIVE_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		requestCode = (short) requestCode;
		resultCode = (short) resultCode;

		Log.d(this.getClass().getName(), "requestCode:" + requestCode + " resultCode:" + resultCode);

		if (policyManager.isAdminActive(componentName) && requestCode == ACTIVE_REQUEST_CODE) {
			notAnyMoreFirstStartup();
		}
	}
}

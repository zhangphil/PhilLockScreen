package zhangfei.lockscreen;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

public class PhilDeviceAdminReceiver extends DeviceAdminReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		System.out.println("PhilDeviceAdminReceiver");
	}

	@Override
	public void onEnabled(Context context, Intent intent) {
		super.onEnabled(context, intent);
		System.out.println("����");
	}

	@Override
	public void onDisabled(Context context, Intent intent) {
		super.onDisabled(context, intent);

		System.out.println("ȡ������");
	}
}

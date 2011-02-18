package com.hiapk.fuckgwf.ui;

import com.hiapk.fuckgwf.R;
import com.hiapk.fuckgwf.utils.HelperUtils;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class CheckPopupAct extends Activity implements OnClickListener {

	private static final String LOGTAG = "CheckPopupAct";
	private TextView tvContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		Log.v(LOGTAG, "onCreate");
		int msgId = getIntent().getIntExtra(
				getString(R.string.checkpopup_extra), -1);
		if (msgId == -1)
			return;
		setContentView(R.layout.checkpopupact);
		View v = null;
		tvContent = (TextView) findViewById(R.id.txtContent);
		switch (msgId) {
		case R.string.notification_no_update:
		case R.string.notification_update_failed:
		case R.string.rollback_fail:
		case R.string.rollback_not_need:
			v = findViewById(R.id.btnOk);
			v.setOnClickListener(this);
			v.setVisibility(View.VISIBLE);
			tvContent.setText(getText(msgId));
			break;
		case R.string.notification_update_successfully:
		case R.string.rollback_success:
			v = findViewById(R.id.btnRebootNow);
			v.setOnClickListener(this);
			v.setVisibility(View.VISIBLE);
			v = findViewById(R.id.btnRebootLater);
			v.setOnClickListener(this);
			v.setVisibility(View.VISIBLE);
			tvContent.setText(getText(msgId));
			break;
		default:
			break;
		}
		v = null;
	}

	@Override
	public void onClick(View v) {
		Log.v(LOGTAG, "onClick");
		switch (v.getId()) {
		case R.id.btnOk:
			finish();
			break;
		case R.id.btnRebootLater:
			finish();
			break;
		case R.id.btnRebootNow:
			HelperUtils.rootCMD("reboot");
			finish();
			break;
		default:
			break;
		}
	}

}

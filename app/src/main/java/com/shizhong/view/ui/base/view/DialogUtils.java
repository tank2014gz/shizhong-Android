package com.shizhong.view.ui.base.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.shizhong.view.ui.R;
import com.shizhong.view.ui.base.utils.UIUtils;

/**
 * Created by yuliyan on 16/1/10.
 */
public class DialogUtils {

	/**
	 * 确认对话框
	 *
	 * @param context
	 * @param content
	 */
	public static Dialog confirmDialog(Context context, String content, String txtOk, String txtCancle,
			final ConfirmDialog callBack) {
		View view = LayoutInflater.from(context).inflate(R.layout.confirm_dialog, null);
		final Dialog dialog = new Dialog(context, R.style.Dialog);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(view);
		Button btnOk = (Button) view.findViewById(R.id.btnOk);
		Button btnCancle = (Button) view.findViewById(R.id.btnCancle);
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				callBack.onOKClick(dialog);
				dialog.dismiss();

			}
		});
		btnCancle.setText(txtCancle);
		btnOk.setText(txtOk);
		btnCancle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				callBack.onCancleClick(dialog);
				dialog.dismiss();
			}
		});
		TextView txt = (TextView) view.findViewById(R.id.dialogContent);
		txt.setText(content);
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.CENTER_VERTICAL);
		/* * 将对话框的大小按屏幕大小的百分比设置 */

		WindowManager.LayoutParams p = dialogWindow.getAttributes(); //
		// 获取对话框当前的参数值//
		p.width = (int) (UIUtils.getScreenWidthPixels(context) * 0.8); //
		// 宽度设置为屏幕的0.8//
		p.y = -10;
		dialogWindow.setAttributes(p);
		return dialog;

	}

	/**
	 * 确认对话框
	 *
	 * @param context
	 * @param content
	 */
	public static Dialog versionUpdateDialog(Context context, String title, String content, String txtOk,
			String txtCancle, final ConfirmDialog callBack) {
		View view = LayoutInflater.from(context).inflate(R.layout.version_upload_dialog, null);
		final Dialog dialog = new Dialog(context, R.style.Dialog);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(view);
		Button btnOk = (Button) view.findViewById(R.id.btnOk);
		Button btnCancle = (Button) view.findViewById(R.id.btnCancle);
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				callBack.onOKClick(dialog);
				dialog.dismiss();

			}
		});
		btnCancle.setText(txtCancle);
		btnOk.setText(txtOk);
		btnCancle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				callBack.onCancleClick(dialog);
				dialog.dismiss();
			}
		});
		TextView txt = (TextView) view.findViewById(R.id.dialogContent);
		txt.setText(content);
		TextView titleView = (TextView) view.findViewById(R.id.title);
		titleView.setText(title);
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.CENTER_VERTICAL);
		/* * 将对话框的大小按屏幕大小的百分比设置 */

		WindowManager.LayoutParams p = dialogWindow.getAttributes(); //
		// 获取对话框当前的参数值//
		p.width = (int) (UIUtils.getScreenWidthPixels(context) * 0.8); //
		// 宽度设置为屏幕的0.8//
		p.y = -10;
		dialogWindow.setAttributes(p);
		return dialog;

	}

	/**
	 * 确认对话框 确认、取消按钮接口
	 *
	 * @author teeker_bin
	 */
	public static interface ConfirmDialog {
		void onOKClick(Dialog dialog);

		void onCancleClick(Dialog dialog);
	}

}

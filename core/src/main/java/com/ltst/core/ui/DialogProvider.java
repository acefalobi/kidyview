package com.ltst.core.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.widget.SimpleAdapter;

import com.ltst.core.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DialogProvider {

    private final AlertDialog.Builder dialogBuilder;
    private final OnCancelClick onCancelListener;

    public DialogProvider(Context context) {
        dialogBuilder = new AlertDialog.Builder(context, R.style.DialogTheme);
        onCancelListener = new OnCancelClick();
    }

    public Context getContext() {
        return dialogBuilder.getContext();
    }

    public void showNetError(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
        builder.setTitle(R.string.network_error_title)
                .setMessage(R.string.error_no_network_connection)
                .setPositiveButton(R.string.ok, onCancelListener)
                .setNegativeButton(null, null)
                .show();
    }

    public void showNetError(Context context, AlertDialog.OnClickListener onCancelClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
        builder.setTitle(R.string.network_error_title)
                .setMessage(R.string.error_no_network_connection)
                .setPositiveButton(R.string.ok, onCancelClick)
                .setNegativeButton(null, null)
                .show();
    }

    public void showFileManagerError() {
        dialogBuilder.setTitle(R.string.error_title)
                .setMessage(R.string.error_not_found_file_manager)
                .setPositiveButton(R.string.ok, onCancelListener)
                .setNegativeButton(null, null)
                .show();
    }

    public void showSuccess(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
        builder.setTitle(R.string.dialog_title_success)
                .setMessage(message)
                .setPositiveButton(R.string.ok, onCancelListener)
                .show();
    }

    public void showDeniedWriteExternalPermission() {
        dialogBuilder.setMessage(R.string.setup_permissions)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    public void showDeniedCameraPermsiions() {
        dialogBuilder.setMessage(R.string.setup_permissions)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    public void showPhotoWay(final PhotoWayCallBack photoWayCallBack) {
        final String ATTRIBUTE_NAME_TEXT = "text";
        final Resources resources = dialogBuilder.getContext().getResources();
        final String[] items = {resources.getString(R.string.profile_camera), resources.getString(R.string.profile_gallery)};
        final ArrayList<Map<String, Object>> data = new ArrayList<>(items.length);
        Map<String, Object> map;
        for (int i = 0; i < items.length; i++) {
            map = new HashMap<>();
            map.put(ATTRIBUTE_NAME_TEXT, items[i]);
            data.add(map);
        }
        String[] from = {ATTRIBUTE_NAME_TEXT};
        int[] to = {R.id.chooser_text_view};
        SimpleAdapter adapter = new SimpleAdapter(dialogBuilder.getContext(), data,
                R.layout.photo_way_choose_tem, from, to);
        dialogBuilder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String item = items[which];
                if (item.equals(resources.getString(R.string.profile_camera))) {
                    photoWayCallBack.camera();
                } else if (item.equals(resources.getString(R.string.profile_gallery))) {
                    photoWayCallBack.gallery();
                }
                dialog.cancel();
            }
        });
        dialogBuilder.setTitle(R.string.photo);
        AlertDialog dialog = dialogBuilder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogBuilder.show();
    }

    public void showLoginError(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
        builder.setTitle(R.string.server_error_title)
                .setMessage(R.string.login_error_message)
                .setPositiveButton(R.string.ok, onCancelListener)
                .show();
    }

    public void userNotFoundError(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
        builder.setTitle(R.string.server_error_title)
                .setMessage(R.string.login_user_not_found_error)
                .setPositiveButton(R.string.ok, onCancelListener)
                .show();
    }

    public void logoutAlert(Context context, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme)
                .setTitle(R.string.logout_title)
                .setMessage(R.string.logout_message)
                .setPositiveButton(R.string.ok, onClickListener)
                .setNegativeButton(R.string.cancel, onCancelListener);
        builder.show();
    }

    public void showWarning(String warning) {
        dialogBuilder.setTitle(R.string.dialog_warning_title)
                .setMessage(warning)
                .setPositiveButton(R.string.ok, onCancelListener)
                .show();
    }

    public void showError(Context context, String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme)
                .setTitle(R.string.error_title)
                .setMessage(error)
                .setPositiveButton(R.string.ok, onCancelListener);
        builder.show();
    }

    public void showSendNewCodeMessage(DialogInterface.OnClickListener onClickListener) {
        dialogBuilder.setTitle(R.string.dialog_warning_title)
                .setMessage(R.string.dialog_new_code_text)
                .setPositiveButton(R.string.ok, onClickListener)
                .setCancelable(false)
                .show();
    }

    public void emptyGroupsWarning(String message, boolean needShowCancelButton,
                                   DialogInterface.OnClickListener onClickListener) {
        dialogBuilder.setTitle(R.string.dialog_warning_title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, onClickListener);
        if (needShowCancelButton) {
            dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        dialogBuilder.show();
    }

    public void emptyError(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
        builder.setTitle(R.string.dialog_warning_title)
                .setMessage(R.string.empty_event_error)
                .setPositiveButton(R.string.ok, onCancelListener)
                .show();
    }

    public void eventTimeError(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
        builder.setTitle(R.string.dialog_warning_title)
                .setMessage(R.string.time_event_error)
                .setPositiveButton(R.string.ok, onCancelListener)
                .show();
    }

    public void parentRegistrationError(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
        builder.setTitle(R.string.error_title)
                .setMessage(R.string.parent_registration_error_message)
                .setPositiveButton(R.string.ok, onCancelListener)
                .show();
    }

    public void showLogoutFromServerPopup(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
        builder.setTitle(R.string.dialog_warning_title)
                .setMessage(R.string.dialog_logout_from_server)
                .setPositiveButton(R.string.ok, onCancelListener)
                .show();
    }

    public void layerSwipePopup(Context context, DialogInterface.OnClickListener positive,
                                DialogInterface.OnClickListener negative) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
        builder.setTitle(R.string.chats_delete_chat_warning_title)
                .setMessage(R.string.chats_delete_chat_message)
                .setPositiveButton(R.string.ok, positive)
                .setNegativeButton(R.string.cancel, negative)
                .show();
    }

    public void needCameraPermission(Context context, Dialog.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
        builder.setTitle(R.string.dialog_warning_title)
                .setMessage(R.string.dialog_camera_permissions_warning)
                .setPositiveButton(R.string.ok, onClickListener)
                .show();
    }

    public void needStoragePermission(Context context, Dialog.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
        builder.setTitle(R.string.dialog_warning_title)
                .setMessage(R.string.dialog_storage_permission_warning)
                .setPositiveButton(R.string.ok, onClickListener)
                .show();
    }


    public void permissionRationale(Context context,
                                    String explanationText,
                                    Dialog.OnClickListener okListener,
                                    Dialog.OnClickListener cancelLister) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
        builder.setTitle(R.string.dialog_warning_title)
                .setMessage(explanationText)
                .setPositiveButton(R.string.ok, okListener)
                .setNegativeButton(R.string.cancel, cancelLister)
                .show();
    }


    public interface PhotoWayCallBack {

        void camera();

        void gallery();
    }

    private class OnCancelClick implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

}

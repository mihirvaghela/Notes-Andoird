package com.example.mihirvaghela.notes.utils;

import android.content.Context;
import android.content.DialogInterface;

import com.afollestad.materialdialogs.AlertDialogWrapper;

/**
 * Created by King on 2016.1.17..
 */
public class DialogUtil {

    public interface OnOkayEvent {
        public void onOkay();
        public void onNo();
    }

    public static void showSimpleDialog(Context context, String title, String msg, final OnOkayEvent func) {
        AlertDialogWrapper.Builder builder = new AlertDialogWrapper.Builder(context);
        builder.setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (func != null) func.onOkay();
                    }
                });
        builder.create().show();
    }

    public static void showDialog(Context context, String title, String msg, final OnOkayEvent func) {
        AlertDialogWrapper.Builder builder = new AlertDialogWrapper.Builder(context);
        builder.setTitle(title)
                .setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (func != null) func.onOkay();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (func != null) func.onNo();
                    }
                });
        builder.create().show();
    }

    public static void showDialogWithNamedButton(Context context, String title, String msg, String OkButton, String NoButton, final OnOkayEvent func) {
        AlertDialogWrapper.Builder builder = new AlertDialogWrapper.Builder(context);
        builder.setTitle(title)
                .setMessage(msg)
                .setPositiveButton(OkButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (func != null) func.onOkay();
                    }
                })
                .setNegativeButton(NoButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (func != null) func.onNo();
                    }
                });
        builder.create().show();
    }
}

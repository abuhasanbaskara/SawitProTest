package com.example.sawitprotest.util

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object AlertDialog {
    fun showAlertDialog(
        context: Context,
        title: String,
        message: String,
        positiveButtonText: String,
        negativeButtonText: String,
        positiveButtonAction: () -> Unit,
        negativeButtonAction: () -> Unit
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setNegativeButton(negativeButtonText) { dialog, _ ->
                negativeButtonAction()
                dialog.dismiss()
            }
            .setPositiveButton(positiveButtonText) { dialog, _ ->
                positiveButtonAction()
                dialog.dismiss()
            }
            .show()
    }
}
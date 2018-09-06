package com.jacdemanec.parole;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class AddHashtagDialogFragment extends DialogFragment {

    AddHasthagListener addHashtagListener;

    public interface AddHasthagListener {
        public void onDialogPositivieClick(String hashtag, String text);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            addHashtagListener = (AddHasthagListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement AddHashtagListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_new_hashtag, null);
        final EditText hashtagEditText = view.findViewById(R.id.dialog_hashtag);
        hashtagEditText.setText("#");
        hashtagEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                if (!editable.toString().startsWith("#")) {
                    hashtagEditText.setText("#");
                    Selection.setSelection(hashtagEditText.getText(), hashtagEditText.getText().length());
                }
            }
        });
        final EditText textEditText = view.findViewById(R.id.dialog_text);
        builder.setTitle("New Hashtag")
                .setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String aux = hashtagEditText.getText().toString();
                        if (aux.length() == 1) {
                            return;
                        }
                        String hashtag = aux.substring(1);
                        String text = textEditText.getText().toString();
                        addHashtagListener.onDialogPositivieClick(hashtag, text);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AddHashtagDialogFragment.this.getDialog().cancel();
            }
        });
        return builder.create();
    }
}

package com.example.txwu.personalbest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.Locale;

public class EnterNewGoalDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // get current goal here, need modification
        int currentGoal = 5000;

        final int newGoal = Goal.suggestNextGoal(currentGoal);
        String suggested = String.valueOf(newGoal);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_change_goal, null);

        builder.setTitle(R.string.dialog_choose_goal)
                .setView(dialogView)
                .setNegativeButton(R.string.dialog_not_now, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // dialog cancelled
                        EnterNewGoalDialogFragment.this.getDialog().cancel();
                    }
                })
                .setPositiveButton(suggested, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // use suggested goal here
                        EnterNewGoalDialogFragment.this.getDialog().cancel();
                    }
                })
                .setNeutralButton(R.string.dialog_custom, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        EditText customInput = dialogView.findViewById(R.id.custom_goal);
                        int goal = newGoal;

                        String input = customInput.getText().toString();
                        try {
                            goal = Integer.parseInt(input);
                        }
                        catch (NumberFormatException e) {
                            goal = newGoal;
                        }

                        EnterNewGoalDialogFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }
}

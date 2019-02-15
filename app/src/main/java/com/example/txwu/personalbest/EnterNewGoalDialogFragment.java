package com.example.txwu.personalbest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class EnterNewGoalDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // gets goal from text field directly
        int currentGoal = Integer.parseInt(((TextView)getActivity().findViewById(R.id.text_goal)).getText().toString());

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
                        Log.d("Changing Goal ", "User Cancelled change");
                        EnterNewGoalDialogFragment.this.getDialog().cancel();
                    }
                })
                .setPositiveButton(suggested, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // use suggested goal here
                        Log.d("Changing Goal", "User used suggested goal: " + newGoal);
                        EnterNewGoalDialogFragment.this.getDialog().cancel();
                    }
                })
                .setNeutralButton(R.string.dialog_custom, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        EditText customInput = dialogView.findViewById(R.id.custom_goal);
                        String input = customInput.getText().toString();
                        try {
                            int goal = Integer.parseInt(input);
                            ((TextView) getActivity().findViewById(R.id.text_goal)).setText("" + goal);
                            Log.d("Changing Goal", "New custom goal: " + goal);
                            EnterNewGoalDialogFragment.this.getDialog().cancel();
                        } catch (NumberFormatException e) {
                            Toast.makeText(getActivity(), "Enter a valid number.", Toast.LENGTH_SHORT);
                        }
                    }
                });

        return builder.create();
    }
}

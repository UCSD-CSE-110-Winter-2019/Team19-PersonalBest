package com.example.txwu.personalbest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EnterNewGoalDialogFragment extends DialogFragment {

    public interface NoticeDialogListener {
        public void onDialogNeutralClick(DialogFragment dialog);
        public void onDialogPositiveClick(DialogFragment dialog);
    }

    NoticeDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // get current goal here, need modification
        final Goal goal = new Goal(getActivity(), new Date());
        int currentGoal = goal.getGoal();

        final int newGoal = Goal.suggestNextGoal(currentGoal);
        final String suggested = String.valueOf(newGoal);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_change_goal, null);

        final String date = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(new Date());

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

                        goal.setGoal(newGoal);
                        goal.setGoal(newGoal, date);
                        // use suggested goal here
                        Log.d("Changing Goal", "User used suggested goal: " + newGoal);
                        listener.onDialogPositiveClick(EnterNewGoalDialogFragment.this);
                    }
                })
                .setNeutralButton(R.string.dialog_custom, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        EditText customInput = dialogView.findViewById(R.id.custom_goal);
                        int goalCustom = newGoal;

                        String input = customInput.getText().toString();
                        try {
                            goalCustom = Integer.parseInt(input);
                        }
                        catch (NumberFormatException e) {
                            Toast.makeText(getActivity(),"Not a valid input, suggested goal used!", Toast.LENGTH_SHORT).show();
                            goalCustom = newGoal;
                        }

                        goal.setGoal(goalCustom);
                        goal.setGoal(goalCustom, date);
                        Log.d("Changing Goal", "New custom goal: " + goalCustom);
                        listener.onDialogNeutralClick(EnterNewGoalDialogFragment.this);

                    }
                });

        return builder.create();
    }
}

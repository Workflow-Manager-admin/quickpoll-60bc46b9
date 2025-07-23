package com.example.androidfrontend;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for creating a new poll.
 */
public class CreatePollFragment extends Fragment {
    private EditText titleField;
    private LinearLayout optionsLayout;
    private Button addOptionBtn;
    private Button createBtn;

    public CreatePollFragment() {}

    // PUBLIC_INTERFACE
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         * Inflates poll creation UI and handles form logic.
         */
        View view = inflater.inflate(R.layout.fragment_create_poll, container, false);
        titleField = view.findViewById(R.id.poll_title_input);
        optionsLayout = view.findViewById(R.id.options_layout);
        addOptionBtn = view.findViewById(R.id.add_option_btn);
        createBtn = view.findViewById(R.id.create_poll_btn);

        addOptionBtn.setOnClickListener(v -> addOptionInput(null));
        createBtn.setOnClickListener(v -> submitPoll());

        // Add two default options
        addOptionInput(null);
        addOptionInput(null);
        return view;
    }

    private void addOptionInput(String text) {
        EditText option = new EditText(getContext());
        option.setHint("Option");
        if (text != null) option.setText(text);
        optionsLayout.addView(option);
    }

    private void submitPoll() {
        String title = titleField.getText().toString().trim();
        List<String> options = new ArrayList<>();
        for (int i = 0; i < optionsLayout.getChildCount(); i++) {
            View v = optionsLayout.getChildAt(i);
            if (v instanceof EditText) {
                String opt = ((EditText) v).getText().toString().trim();
                if (!TextUtils.isEmpty(opt)) options.add(opt);
            }
        }
        if (TextUtils.isEmpty(title) || options.size() < 2) {
            Toast.makeText(getContext(), "Add a title and at least two options.", Toast.LENGTH_SHORT).show();
            return;
        }

        PollService.createPoll(title, options, (poll, err) -> {
            if (err != null || poll == null) {
                Toast.makeText(getContext(), "Failed to create poll.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Poll created!", Toast.LENGTH_SHORT).show();
                titleField.setText("");
                optionsLayout.removeAllViews();
                addOptionInput(null);
                addOptionInput(null);
            }
        });
    }
}

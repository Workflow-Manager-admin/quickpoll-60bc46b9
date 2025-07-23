package com.example.androidfrontend;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import java.util.List;

/**
 * Activity showing poll details, voting, sharing, and result visualization.
 */
public class PollDetailActivity extends AppCompatActivity {

    private TextView pollTitle;
    private RadioGroup optionsGroup;
    private Button voteBtn, shareBtn;
    private LinearLayout resultsLayout;
    private String pollId;

    // PUBLIC_INTERFACE
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        /**
         * Displays poll, lets user vote, see results, and share poll link.
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_detail);

        pollTitle = findViewById(R.id.poll_detail_title);
        optionsGroup = findViewById(R.id.options_radio_group);
        voteBtn = findViewById(R.id.vote_button);
        shareBtn = findViewById(R.id.share_button);
        resultsLayout = findViewById(R.id.results_layout);
        pollId = getIntent().getStringExtra("POLL_ID");

        loadPoll();

        voteBtn.setOnClickListener(v -> sendVote());
        shareBtn.setOnClickListener(v -> sharePoll());
    }

    private void loadPoll() {
        PollService.getPolls((polls, err) -> {
            if (polls == null || err != null) {
                Toast.makeText(this, "Could not fetch poll", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            for (Poll p : polls) {
                if (p.getId().equals(pollId)) {
                    displayPoll(p);
                    return;
                }
            }
            Toast.makeText(this, "Poll not found", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void displayPoll(Poll poll) {
        pollTitle.setText(poll.getTitle());
        optionsGroup.removeAllViews();
        List<String> opts = poll.getOptions();
        for (int i = 0; i < opts.size(); i++) {
            RadioButton rb = new RadioButton(this);
            rb.setText(opts.get(i));
            rb.setId(i);
            optionsGroup.addView(rb);
        }
        if (poll.getResults() != null && !poll.getResults().isEmpty()) {
            showResults(poll.getOptions(), poll.getResults());
        }
    }

    private void sendVote() {
        int idx = optionsGroup.getCheckedRadioButtonId();
        if (idx < 0) {
            Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show();
            return;
        }
        PollService.votePoll(pollId, idx, (success, err) -> {
            if (!success) {
                Toast.makeText(this, "Vote failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Voted!", Toast.LENGTH_SHORT).show();
                loadPoll();
            }
        });
    }

    private void showResults(List<String> options, List<Integer> results) {
        resultsLayout.removeAllViews();
        int sum = 0;
        for (int count : results) sum += count;
        for (int i = 0; i < options.size(); i++) {
            String opt = options.get(i);
            int cnt = results.get(i);
            float percent = sum == 0 ? 0 : (cnt * 100f / sum);
            TextView tv = new TextView(this);
            tv.setText(String.format("%s: %d (%.1f%%)", opt, cnt, percent));
            resultsLayout.addView(tv);
        }
    }

    private void sharePoll() {
        String url = "http://quickpoll.example.com/poll/" + pollId; // adapt for prod
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Poll Link", url);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Poll link copied to clipboard", Toast.LENGTH_SHORT).show();
    }
}

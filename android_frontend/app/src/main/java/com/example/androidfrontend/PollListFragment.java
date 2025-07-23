package com.example.androidfrontend;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.util.List;

/**
 * Fragment that lists available polls.
 */
public class PollListFragment extends Fragment implements PollListAdapter.OnPollClickListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    public PollListFragment() {}

    // PUBLIC_INTERFACE
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         * Inflates the layout for the poll list fragment.
         */
        View view = inflater.inflate(R.layout.fragment_poll_list, container, false);
        recyclerView = view.findViewById(R.id.poll_list_recycler);
        progressBar = view.findViewById(R.id.progress_bar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchPolls();

        return view;
    }

    private void fetchPolls() {
        progressBar.setVisibility(View.VISIBLE);

        // Fetch polls from backend
        PollService.getPolls((polls, error) -> {
            progressBar.setVisibility(View.GONE);
            if (error != null) {
                Toast.makeText(getContext(), "Failed to load polls.", Toast.LENGTH_SHORT).show();
                return;
            }
            PollListAdapter adapter = new PollListAdapter(polls, this);
            recyclerView.setAdapter(adapter);
        });
    }

    @Override
    public void onPollClick(Poll poll) {
        Intent intent = new Intent(getActivity(), PollDetailActivity.class);
        intent.putExtra("POLL_ID", poll.getId());
        startActivity(intent);
    }
}

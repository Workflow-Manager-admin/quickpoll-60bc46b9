package com.example.androidfrontend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * Adapter for poll listing RecyclerView.
 */
public class PollListAdapter extends RecyclerView.Adapter<PollListAdapter.PollViewHolder> {
    private List<Poll> pollList;
    private OnPollClickListener listener;

    public interface OnPollClickListener {
        void onPollClick(Poll poll);
    }

    public PollListAdapter(List<Poll> pollList, OnPollClickListener listener) {
        this.pollList = pollList;
        this.listener = listener;
    }

    @Override
    public PollViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_poll, parent, false);
        return new PollViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PollViewHolder holder, int position) {
        Poll poll = pollList.get(position);
        holder.title.setText(poll.getTitle());
        holder.itemView.setOnClickListener(v -> listener.onPollClick(poll));
    }

    @Override
    public int getItemCount() {
        return pollList.size();
    }

    public static class PollViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        public PollViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.poll_title);
        }
    }
}

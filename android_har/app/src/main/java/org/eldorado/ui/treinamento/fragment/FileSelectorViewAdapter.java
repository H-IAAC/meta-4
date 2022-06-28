package org.eldorado.ui.treinamento.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.tensorflow.lite.examples.transfer.R;

import java.io.File;

public class FileSelectorViewAdapter extends RecyclerView.Adapter<FileSelectorViewAdapter.ViewHolder> {
    private final LayoutInflater mInflater;
    FileSelectedListener mListener;
    private File[] files;

    public FileSelectorViewAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.file_selector_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CheckBox checkBox = holder.getCheckBox();
        checkBox.setText(files[position].getName());
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onFileChecked(files[holder.getAdapterPosition()], checkBox.isChecked());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return files != null ? files.length : 0;
    }

    public void setFiles(File[] files) {
        this.files = files;
        notifyDataSetChanged();
    }

    public void setListener(FileSelectedListener listener) {
        mListener = listener;
    }

    public interface FileSelectedListener {
        void onFileChecked(File file, boolean ischecked);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.file_checkbox);
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }
    }
}

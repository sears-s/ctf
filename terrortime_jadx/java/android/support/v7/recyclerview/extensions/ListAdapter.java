package android.support.v7.recyclerview.extensions;

import android.support.v7.recyclerview.extensions.AsyncDifferConfig.Builder;
import android.support.v7.util.AdapterListUpdateCallback;
import android.support.v7.util.DiffUtil.ItemCallback;
import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import java.util.List;

public abstract class ListAdapter<T, VH extends ViewHolder> extends Adapter<VH> {
    private final AsyncListDiffer<T> mHelper;

    protected ListAdapter(ItemCallback<T> diffCallback) {
        this.mHelper = new AsyncListDiffer<>((ListUpdateCallback) new AdapterListUpdateCallback(this), new Builder(diffCallback).build());
    }

    protected ListAdapter(AsyncDifferConfig<T> config) {
        this.mHelper = new AsyncListDiffer<>((ListUpdateCallback) new AdapterListUpdateCallback(this), config);
    }

    public void submitList(List<T> list) {
        this.mHelper.submitList(list);
    }

    /* access modifiers changed from: protected */
    public T getItem(int position) {
        return this.mHelper.getCurrentList().get(position);
    }

    public int getItemCount() {
        return this.mHelper.getCurrentList().size();
    }
}

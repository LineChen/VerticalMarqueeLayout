package com.line.marqueelayout;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

/**
 * Created by linechen on 2019-09-11.
 */
public abstract class VerticalMarqueeAdapter<VH extends VerticalMarqueeLayout.ViewHolder> {

    AdapterDataObserver adapterDataObserver;

    void setAdapterDataObserver(AdapterDataObserver adapterDataObserver) {
        this.adapterDataObserver = adapterDataObserver;
    }

    @NonNull
    public abstract VH onCreateViewHolder(@NonNull ViewGroup parent);

    public abstract void onBindViewHolder(@NonNull VH holder, int position);

    public abstract int getItemCount();

    public final void notifyDataSetChanged() {
        this.adapterDataObserver.notifyChanged();
    }

}

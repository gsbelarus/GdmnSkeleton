package com.gsbelarus.gedemin.skeleton.base.view.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BaseSimpleAdapter<ItemType, ViewHolder extends BaseSimpleAdapter.ItemViewHolder>
        extends RecyclerView.Adapter<ViewHolder> {

    private List<ItemType> items = new ArrayList<>();
    private OnItemClickListener<ItemType, ViewHolder> onItemClickListener;
    private OnItemLongClickListener<ItemType, ViewHolder> onItemLongClickListener;

    @Override
    public int getItemCount() {
        return items.size();
    }

    protected View inflateView(@LayoutRes int resource, @NonNull ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
    }

    public ItemType getItem(int position) {
        return items.get(position);
    }

    public List<ItemType> getItems() {
        return items;
    }

    public void setItems(List<ItemType> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void addItem(ItemType item) {
        addItem(item, items.size());
    }

    public void addItem(ItemType item, int position) {
        items.add(position, item);
        notifyItemInserted(position);
    }

    public void removeItem(ItemType item) {
        removeItem(items.indexOf(item));
    }

    public void removeItem(int position) {
        this.items.remove(position);
        notifyItemRemoved(position);
    }

    public void removeAll() {
        this.items.clear();
        notifyDataSetChanged();
    }

    public void move(int fromPosition, int toPosition) {
        Collections.swap(items, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public OnItemClickListener<ItemType, ViewHolder> getOnItemClickListener() {
        return onItemClickListener;
    }

    public BaseSimpleAdapter setOnItemClickListener(OnItemClickListener<ItemType, ViewHolder> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    public OnItemLongClickListener<ItemType, ViewHolder> getOnItemLongClickListener() {
        return onItemLongClickListener;
    }

    public BaseSimpleAdapter setOnItemLongClickListener(OnItemLongClickListener<ItemType, ViewHolder> onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
        return this;
    }

    public interface OnItemClickListener<ItemType, ViewHolder> {
        void onClick(View view, ItemType item, ViewHolder viewHolder);
    }

    public interface OnItemLongClickListener<ItemType, ViewHolder> {
        boolean onLongClick(View view, ItemType item, ViewHolder viewHolder);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        public ItemViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifyClickListener(v);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return notifyLongClickListener(v);
                }
            });
        }

        public void notifyClickListener(View view) {
            if (onItemClickListener != null && !items.isEmpty())
                onItemClickListener.onClick(view, items.get(getLayoutPosition()), (ViewHolder) this);
        }

        public boolean notifyLongClickListener(View view) {
            return onItemLongClickListener != null && !items.isEmpty() &&
                    onItemLongClickListener.onLongClick(view, items.get(getLayoutPosition()), (ViewHolder) this);
        }
    }
}

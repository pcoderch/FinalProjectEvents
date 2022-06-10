package com.pp2ex.finalprojectevents.Activities;

import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class RequestAdaptor extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private List<RequestList> nData;
    private LayoutInflater nInflater;
    private Context context;

    public ListAdapter(List<RequestList> itemList, Context context){
        this.nInflater = LayoutInflater.from(context);
        this.context = context;
        this.nData = itemList;
    }

    @Override
    public int getItemCount(){return nData.size();}

    @Override
    public ListAdapter.ViewHolder(ViewGroup parent, int viewType){
        View view = nInflater.inflate(R.layout.list_element, null);
        return new ListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ListAdapter.ViewHolder holdeer, final int position){
        holder.bindData(nData.get(position));
    }

    public void setItems(List<RequestList> items) {nData = items; }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iconImage;
        TextView name, email;

        ViewHolder(View itemView){
            super(itemView);
            iconImage = itemView.findViewById(R.id.iconViewImage);
            name = itemView.findViewById(R.id.nameTextView);
            email = itemView.findViewById(R.id.emailTextView);
        }

        void bindData(final ListElement item){
            iconImage.setColorFilter(ColorFilter(Color.perseColor(item.getColor()), PorterDuff.Mode.SRC_IN));
            name.setText(item.getName());
            email.setText(item.getEmail());
        }
    }
}

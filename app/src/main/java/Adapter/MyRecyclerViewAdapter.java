package Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.raghuprojects.contacts.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import POJO.Contact;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> implements Filterable {

     List<Contact> mycontacts;
     List<Contact> contacts;
    public MyRecyclerViewAdapter(List<Contact> contactList){
        this.mycontacts=contactList;
        this.contacts=contactList;
    }

    @NonNull
    @Override
    public MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View myview=layoutInflater.inflate(R.layout.recycler_view_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(myview);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecyclerViewAdapter.ViewHolder holder, int position) {

        holder.name.setText(mycontacts.get(position).getName());
        holder.mobile_number.setText(mycontacts.get(position).getMobilenumber());
    }

    @Override
    public int getItemCount() {
        return mycontacts.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Contact> filteredList=new ArrayList<>();
            if(charSequence.toString().isEmpty()){
                filteredList.addAll(mycontacts);
            }else{
                for(Contact contact : mycontacts){
                    if(contact.getName().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        filteredList.add(contact);
                    }
                }
            }
          FilterResults filterResults=new FilterResults();
            filterResults.values=filteredList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            contacts.clear();
            contacts.addAll((Collection<? extends Contact>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView name,mobile_number;

        public ViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            mobile_number=itemView.findViewById(R.id.mobile_number);
        }
    }
}

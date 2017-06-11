package android.example.com.booklisting;


import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;

import android.example.com.booklisting.databinding.BookListItemBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;




public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    private List<Book> items;


    public BookAdapter(List<Book> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // was using ViewDataBinding binding but you can't assign handlers to the base
        BookListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.book_list_item, parent, false);
        binding.setHandlers(new Handlers()); //use my handlers class
        return new ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding binding;

        /**
         * @param binding of type ViewDataBinding which is an
         *                abstract Base Class for generated binding classes
         */
        public ViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Object obj) {
            binding.setVariable(android.example.com.booklisting.BR.book, obj);
            binding.executePendingBindings();
        }
    }

}

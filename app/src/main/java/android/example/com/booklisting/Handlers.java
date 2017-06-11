package android.example.com.booklisting;

import android.content.Context;
import android.content.Intent;

import android.view.View;


/**
 * Created by ByteTonight on 05.06.2017.
 */

public class Handlers {

    public void onClickViewBookDetails(View v, Book book) {
        Context context = v.getContext();
        Intent bookDetails = new Intent(context, DetailsActivity.class);
        bookDetails.putExtra(Utils.ARG_BOOK, book);
        context.startActivity(bookDetails);
    }


}

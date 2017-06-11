package android.example.com.booklisting;



import android.content.Context;
import android.databinding.BindingAdapter;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import java.util.Arrays;
import java.util.List;


/**
 * Created by ByteTonight on 10.06.2017.
 */

public class Book implements Parcelable{

    public static final Creator<Book> CREATOR =
            new Creator<Book>() {

                @Override
                public Book createFromParcel(Parcel source) {
                    return new Book(source);
                }

                @Override
                public Book[] newArray(int size) {
                    return new Book[size];
                }
            };

    private Context context;
    private String title;
    private List<String> authors;
    private String description;
    private String thumbnailLink;


    public Book(Context context, String title, List<String> authors,
                String description, String thumbnailLink) {
        this.context = context;
        this.title = title;
        this.authors = authors;
        this.description = description;
        this.thumbnailLink = thumbnailLink;
    }


//region getters

    public String getTitle() {
        return title;
    }

    public List<String> getAuthors() {
        if (authors.isEmpty())
            return Arrays.asList( context.getString(R.string.no_authors) );
        return authors;
    }

    public String getAuthorsString()
    {
        if (authors.isEmpty())
            return  context.getString(R.string.no_authors) ;

        String out = context.getString(R.string.written_by) + " ";
        String komma = "";
        for (String author : authors)
        {
            out += komma + author;
            komma = ", ";
        }
        return out;
    }

    public String getDescription() {
        if (description.isEmpty())
            return context.getString(R.string.no_description);
        return description;
    }

    public String getThumbnailLink() {
        return thumbnailLink;
    }

    //endregion

//region Parcelable implementation

    //Unwrap the parcel and populate instance
    public Book(Parcel parcel) {
        title = parcel.readString();
        parcel.readStringList(authors);
        description = parcel.readString();
        thumbnailLink = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeStringList(authors);
        dest.writeString(description);
        dest.writeString(thumbnailLink);
    }
//endregion


    @BindingAdapter("bind:imageUrl")
    public static void getPreviewImage(ImageView imageView, String imageUrl) {
        if (imageUrl.isEmpty())
            return;
        Glide.with(imageView.getContext()).load(imageUrl).into(imageView);
    }


    @Override
    public String toString() {
        return super.toString();
    }
}

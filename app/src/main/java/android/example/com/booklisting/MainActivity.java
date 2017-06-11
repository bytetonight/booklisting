package android.example.com.booklisting;

import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    //"https://www.googleapis.com/books/v1/volumes?q=";
    private static final String API_ENDPOINT = "https://www.googleapis.com/books/v1/volumes";
    private static final String BOOK_LIST = "booklist";
    private static final int LOADER_ID = 0;
    private static boolean firstRun = true;
    private List<Book> items = new ArrayList<>();
    private BookAdapter adapter;
    private RecyclerView recyclerView;
    private EditText searchField;
    private ImageView imageViewSearch;
    private ProgressBar loadingIndicator;
    private TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchField = (EditText) findViewById(R.id.search_text);
        imageViewSearch = (ImageView) findViewById(R.id.searchButton);
        loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        emptyText = (TextView) findViewById(R.id.empty);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        imageViewSearch.setOnClickListener(clickListener);
        if (firstRun)
            emptyText.setText(R.string.please_enter_search_term);
        else
            emptyText.setText(R.string.strNoRecordsFound);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(BOOK_LIST, new ArrayList<Parcelable>(items));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        items = savedInstanceState.getParcelableArrayList(BOOK_LIST);
        prepareRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String needle = searchField.getText().toString().trim();
            if (needle.isEmpty())
                return;
            needle = needle.replace(" ", "+");

            if (Utils.hasConnection(MainActivity.this)) {
                emptyText.setText("");
                firstRun = false;
                startTheLoadingStuff(needle);
            } else {
                loadingIndicator.setVisibility(View.GONE);
                items.clear();
                adapter.notifyDataSetChanged();
                emptyText.setText(R.string.noInternetConnection);
            }
        }
    };


    private void startTheLoadingStuff(String needle) {


        Uri baseUri = Uri.parse(API_ENDPOINT);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("q", needle);


        Bundle args = new Bundle();
        args.putString("Uri", uriBuilder.toString());

        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(LOADER_ID, args, MainActivity.this);
        if (loaderManager.getLoader(LOADER_ID).isStarted())
            loaderManager.restartLoader(LOADER_ID, args, MainActivity.this);


    }

    private void prepareRecyclerView()
    {
        adapter = new BookAdapter(items);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        if (args.isEmpty())
            return null;
        loadingIndicator.setVisibility(View.VISIBLE);
        return new BookLoader(MainActivity.this, args.getString("Uri"));
    }


    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {
        loadingIndicator.setVisibility(View.GONE);
        if (data == null || data.isEmpty()) {
            items.clear();
            emptyText.setText(R.string.strNoRecordsFound);
        }
        else {
            this.items = data;
            prepareRecyclerView();
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        prepareRecyclerView();
    }
}

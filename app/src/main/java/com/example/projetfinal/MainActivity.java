package com.example.projetfinal;

import static android.content.Intent.EXTRA_RETURN_RESULT;

import static com.example.projetfinal.Options_activity.SHARED_PREFS;
import static com.example.projetfinal.Options_activity.SWITCH1;
import static com.example.projetfinal.Options_activity.SWITCH2;
import static com.example.projetfinal.Options_activity.SWITCH3;
import static com.example.projetfinal.Options_activity.SWITCH4;
import static com.example.projetfinal.Options_activity.SWITCH5;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import org.knowm.xchange.currency.Currency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import io.vavr.collection.Array;

/**
 * The type Main activity.
 * From the top 5 exchanges:
 * Binance
 * CoinbasePro
 * Kraken
 * GateIO
 * UpBit
 */
public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewTop = null;
    private RecyclerView recyclerViewBottom = null;
    private MyAdapter myAdapterTop, myAdapterBottom;

    private ListView listView;
    private ArrayList<String> name = null;
    private ArrayAdapter<String> arrayAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private LiveData<ArrayList<TickerWithExchange>> highestPercentage;
    private ArrayList<Integer> validExchanges;

    private long startTime = System.currentTimeMillis();
    private int aa,bb,cc,dd,ee,ff;
    private LiveData<ArrayList<TickerWithExchange>> lowestPercentage;
    private LiveData<ArrayList<Currency>> allCurrencies;

    Menu activityMenu;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        this.setTitle(R.string.title);

        update();

        swipeRefreshLayout = findViewById(R.id.refreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                long elapsedTime = System.currentTimeMillis() - startTime;
                long timeTillNextDisplayChange = 60000 - (elapsedTime % 60000);
                if (elapsedTime > 60000){
                    update();
                    Toast.makeText(getApplicationContext(),"Updating", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                    startTime =0;
                } else {
                    Toast.makeText(getApplicationContext(),"wait " + Long.toString(timeTillNextDisplayChange/ 1000L) + " s", Toast.LENGTH_SHORT).show();
                }

                swipeRefreshLayout.setRefreshing(false);
            }
        });
        // 2e page:
        int index = 0;
        // index gotten from search/click. we can also just take what was clicked on and put
        // it in getArbitrage
        // in [0] is the top, in [1] is losers. All are already in order
        // ArrayList<TickerWithExchange>[] arbitrage = registry.getArbitrage(allCurrencies.get(index));


    }
    //part for menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_menu,menu);
        //search bar
        SearchView searchView= (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("Search...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                arrayAdapter.getFilter().filter(newText);

                return false;
            }
        });
        searchView.setClickable(false);
        this.activityMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.options:
                openActivity_options();
                return true;
            case R.id.propos:
                openActivity_AboutMe();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
    //ouvre la page en gros pour options
    public void openActivity_options()
    {
        Intent intent = new Intent(this,Options_activity.class);
        startActivity(intent);
    }

    public void openActivity_AboutMe()
    {
        Intent intent = new Intent(this,AboutMe_activity.class);
        startActivity(intent);
    }
    //part pour menu arrete ici
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void update(){
        SharedPreferences mPreferences =  getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        Boolean a = mPreferences.getBoolean(SWITCH1,true);
        aa = (a) ? 1:0;
        Boolean b = mPreferences.getBoolean(SWITCH2,true);
        bb = (b) ? 1:0;
        Boolean c = mPreferences.getBoolean(SWITCH3,true);
        cc = (c) ? 1:0;
        Boolean d = mPreferences.getBoolean(SWITCH4,true);
        dd = (d) ? 1:0;
        Boolean f = mPreferences.getBoolean(SWITCH5,true);
        ff = (f) ? 1:0;

        validExchanges = new ArrayList<>(Arrays.asList(aa, bb, cc, ff, dd)); // changer les valid exchanges ici selon les settings.

        recyclerViewTop = findViewById(R.id.recyclerViewTop);

        recyclerViewBottom = findViewById(R.id.recyclerViewBottom);


        Registry registry = null; // here we would pass the exchanges
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            registry = new Registry(validExchanges);
        }

        // dans la 1ere partie du recycler view dans le main
        highestPercentage = registry.getMaxGainers();

        // dans la 2e partie du recycler view dans le main
        lowestPercentage = registry.getMinGainers();

        allCurrencies = registry.getAllCurrencies();

        allCurrencies.observe(this, e->{
            if (name == null){
                name = new ArrayList<>();
            }

            for (Currency currency : e){
                name.add(currency.toString());
                name.add(currency.getDisplayName());
            }

            // search bar

            // ton search bar met juste absolument tout dans un listView, listView qui est dans le recycler. jsp trop pk. On ne veut pas chercher
            // dans le recyclerview, on veut chercher dans une base de données textes que tu as sous format string
            // donc quand tu fais ton query, tu veux tout enlever et mettre le list view

            // listView = findViewById(R.id.listview);
            // arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, name);
            // listView.setAdapter(arrayAdapter);

            activityMenu.findItem(R.id.search).getActionView().setClickable(true);
        });


        //Mettre les éléments dans des ArrayList pour la premiere partie du recyclerView
        Registry finalRegistry = registry;
        highestPercentage.observe(this, e->{
            finalRegistry.getArbitrage(Currency.USDT); // to get prices in USD

            ArrayList<String> instruments = new ArrayList<>();
            ArrayList<String> exchanges = new ArrayList<>();
            ArrayList<String> percentChanges = new ArrayList<>();
            ArrayList<String> prices = new ArrayList<>();
            ArrayList<String> instrumentNames = new ArrayList<>();

            for(TickerWithExchange ticker:e){
                instruments.add(ticker.getInstrument().toString());

                exchanges.add(ticker.getExchange().toString().split("#")[0]);

                percentChanges.add(Double.toString(ticker.getPercentChange()));

                double price = ticker.getPriceInUSD();
                prices.add(Double.toString(ticker.getPriceInUSD()));

                instrumentNames.add(ticker.getName());
            }

            myAdapterTop = new MyAdapter(instruments, exchanges, percentChanges, prices, instrumentNames);
            recyclerViewTop.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewTop.setAdapter(myAdapterTop);
            recyclerViewTop.getAdapter().notifyDataSetChanged();

            Toast.makeText(this,getString(R.string.finish),Toast.LENGTH_SHORT).show();
        });

        //Mettre les éléments dans des ArrayList pour la deuxieme partie du recyclerView
        lowestPercentage.observe(this, e->{

            ArrayList<String> instruments = new ArrayList<>();
            ArrayList<String> exchanges = new ArrayList<>();
            ArrayList<String> percentChanges = new ArrayList<>();
            ArrayList<String> prices = new ArrayList<>();
            ArrayList<String> instrumentNames = new ArrayList<>();

            for(TickerWithExchange ticker: e){
                instruments.add(ticker.getInstrument().toString());

                exchanges.add(ticker.getExchange().toString().split("#")[0]);

                percentChanges.add(Double.toString(ticker.getPercentChange()));

                prices.add(Double.toString(ticker.getPriceInUSD()));

                instrumentNames.add(ticker.getName());
            }

            myAdapterBottom = new MyAdapter(instruments, exchanges, percentChanges, prices, instrumentNames);
            recyclerViewBottom.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewBottom.setAdapter(myAdapterBottom);
            recyclerViewBottom.getAdapter().notifyDataSetChanged();
        });
    }

}

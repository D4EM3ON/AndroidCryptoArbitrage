package com.example.projetfinal;

import static com.example.projetfinal.Options_activity.SHARED_PREFS;
import static com.example.projetfinal.Options_activity.SWITCH1;
import static com.example.projetfinal.Options_activity.SWITCH2;
import static com.example.projetfinal.Options_activity.SWITCH3;
import static com.example.projetfinal.Options_activity.SWITCH4;
import static com.example.projetfinal.Options_activity.SWITCH5;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.widget.SearchView;

import android.widget.TextView;
import android.widget.Toast;

import org.knowm.xchange.currency.Currency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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

    private ArrayAdapter<String> arrayAdapter;

    private ArrayList<TickerWithExchange>[] opportunities;
    private Registry registry;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MyAdapter.RecyclerViewClickListener listener;
    private LiveData<ArrayList<TickerWithExchange>> highestPercentage;
    private ArrayList<Integer> validExchanges;
    private long startTime = System.currentTimeMillis();
    private int aa,bb,cc,dd,ff;
    private LiveData<ArrayList<TickerWithExchange>> lowestPercentage;
    private LiveData<ArrayList<Currency>> allCurrencies;
    private ArrayList<String> top,bot;
    private Menu activityMenu;
    private List<String> name;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * déclaration d'un nouveau arrayAdapter en fonction d'une list d'item.
         */
        arrayAdapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

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
    }

    private void setOnClickListener() {
        listener = new MyAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
             Intent intent = new Intent(getApplicationContext(),MainActivity2.class);
                View view = (View) v.getParent();

                if (view.getId()== R.id.recyclerViewTop) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        opportunities = registry.getArbitrage(new Currency(top.get(position)));
                    }
                }
                else if(view.getId()== R.id.recyclerViewBottom){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        opportunities = registry.getArbitrage(new Currency(bot.get(position)));
                    }
                }else{
                    Log.i("id",v.getId()+""+R.id.recyclerViewTop+""+R.id.recyclerViewBottom);
                }
                ArrayList<ArrayList<String>> stringOpps = new ArrayList<>();

                for (int i = 0; i < 2; i++){
                    stringOpps.add(new ArrayList<>());
                    for (TickerWithExchange ticker : opportunities[i]){
                        ticker.setToUSD(registry.setTickerUSD(ticker));
                        stringOpps.get(i).add(ticker.getInstrument().toString());
                        stringOpps.get(i).add(ticker.getName());
                        stringOpps.get(i).add(ticker.getExchange().toString().split("#")[0]);
                        stringOpps.get(i).add(Double.toString(ticker.getPriceInUSD()));
                        stringOpps.get(i).add(Double.toString(ticker.getPercentChange()));
                    }
                }

             intent.putExtra("opps",stringOpps);

             startActivity(intent);
            }
        };
    }
    //part for menu


    @Override
    /**
     * création d'un menu option et d'un searchView
     *
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_menu,menu);

        /**
         * création du searchView
         */
        MenuItem menuItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        /**
         * quand on click sur l'icon du searchView
         * ouverture = expand
         * fermeture collapse
         *
         */
        menu.findItem(R.id.action_search).setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {

                return true;
            }

            @Override
            /**
             * fermeture du menu search.
             * réinitialisation de la front page au données de base
             *
             * retour au titre
             * retour au recyclerView high/low
             *
             * disparition du listView
             */
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                Log.i("Retour","done");

                ListView listView=findViewById(R.id.listview1);
                listView.setAlpha(0);


                TextView titreHighest=findViewById(R.id.titreHighest);
                TextView titreLowest=findViewById(R.id.titreLowest);

                titreHighest.setVisibility(View.VISIBLE);
                titreLowest.setVisibility(View.VISIBLE);
                recyclerViewBottom.setVisibility(View.VISIBLE);
                recyclerViewTop.setVisibility(View.VISIBLE);

                return true;
            }
        });

        /**
         * mettre le searcheView Visible
         */
        searchView.setAlpha(1);
        activityMenu = menu;

        /**
         * action lorsque l'utilisateur click sur l'icon search
         *
         * Disparition des titres
         * Disparition des recyclerView
         *
         * apparition du ListView
         *
         * initialisation du hint
         */
        searchView.setOnSearchClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Log.i("fonction","load");

               ListView listView=findViewById(R.id.listview1);

               listView.setAlpha(1);

               TextView titreHighest=findViewById(R.id.titreHighest);
               TextView titreLowest=findViewById(R.id.titreLowest);

               titreHighest.setVisibility(View.GONE);
               titreLowest.setVisibility(View.GONE);
               recyclerViewBottom.setVisibility(View.GONE);
               recyclerViewTop.setVisibility(View.GONE);

               SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();


               searchView.setQueryHint("Search...");

               searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                   @Override
                   /**
                    * prend les informations rechercher par l'utilisateur
                    * ouverture de l'activity 2
                    */
                   public boolean onQueryTextSubmit(String query) {
                       try{
                           toSecondPage(query.toUpperCase());
                       } catch (NullPointerException e){

                       }
                       return false;
                   }

                   @Override
                   /**
                    * listView qui s'adapte selon ce qui est recherché
                    *
                    * @param newtText newText
                    */
                   public boolean onQueryTextChange(String newText) {

                       arrayAdapter.getFilter().filter(newText);

                       return false;
                   }

               });
           };
       });

       activityMenu = menu;

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    /**
     * ouverture de chaque item(menu) selon ce qui a été sélectionné
     *
     * @param item item
     * @return true
     */
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



        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            registry = new Registry(validExchanges);
        }

        // dans la 1ere partie du recycler view dans le main
        highestPercentage = registry.getMaxGainers();

        // dans la 2e partie du recycler view dans le main
        lowestPercentage = registry.getMinGainers();

        allCurrencies = registry.getAllCurrencies();

        /**
         * extration des live data dans listView
         * utilisation et initialisation du arrayAdapter
         *
         * @param listView listView
         * @param name name
         *
         * search icon clickable
         */
        allCurrencies.observe(this, e->{

            ListView listView;
            name=new ArrayList<>();

            for (Currency currency : e){
                if (!name.contains(currency.toString())){
                    name.add(currency.toString());
                }
            }
            setOnClickListener();
            // search bar

            listView=findViewById(R.id.listview1);
            arrayAdapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, name);
            listView.setAdapter(arrayAdapter);

            listView.setAlpha(0);

            activityMenu.findItem(R.id.action_search).getActionView().setClickable(true);
        });


        //Mettre les éléments dans des ArrayList pour la premiere partie du recyclerView
        /**
         * extraction des liva data en list, pour top 5 positif
         *
         * @param instruments instruments
         * @param exchanges exchanges
         * @param percentChanges percentChanges
         * @param prices prices
         * @param instrumentNames instrumentNames
         *
         *  Nouveau recyclerView
         */
        highestPercentage.observe(this, e->{
            top = new ArrayList<String>();
            ArrayList<String> instruments = new ArrayList<>();
            ArrayList<String> exchanges = new ArrayList<>();
            ArrayList<String> percentChanges = new ArrayList<>();
            ArrayList<String> prices = new ArrayList<>();
            ArrayList<String> instrumentNames = new ArrayList<>();
            top.clear();
            for(TickerWithExchange ticker:e){
                ticker.setToUSD(registry.setTickerUSD(ticker));

                instruments.add(ticker.getInstrument().toString());

                exchanges.add(ticker.getExchange().toString().split("#")[0]);

                percentChanges.add(Double.toString(ticker.getPercentChange()));

                prices.add(Double.toString(ticker.getPriceInUSD()));

                instrumentNames.add(ticker.getName());

                top.add(ticker.getInstrument().toString().split("/")[0]);

            }

            myAdapterTop = new MyAdapter(instruments, exchanges, percentChanges, prices, instrumentNames,listener);
            recyclerViewTop.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewTop.setAdapter(myAdapterTop);
            recyclerViewTop.getAdapter().notifyDataSetChanged();

            Toast.makeText(this,getString(R.string.finish),Toast.LENGTH_SHORT).show();
        });

        //Mettre les éléments dans des ArrayList pour la deuxieme partie du recyclerView
        /**
         * extraction des liva data en list, pour top 5 négatif
         *
         * @param instruments instruments
         * @param exchanges exchanges
         * @param percentChanges percentChanges
         * @param prices prices
         * @param instrumentNames instrumentNames
         *
         *  Nouveau recyclerView
         */
        lowestPercentage.observe(this, e->{
            bot = new ArrayList<String>();
            ArrayList<String> instruments = new ArrayList<>();
            ArrayList<String> exchanges = new ArrayList<>();
            ArrayList<String> percentChanges = new ArrayList<>();
            ArrayList<String> prices = new ArrayList<>();
            ArrayList<String> instrumentNames = new ArrayList<>();
            bot.clear();
            for(TickerWithExchange ticker: e){
                ticker.setToUSD(registry.setTickerUSD(ticker));

                instruments.add(ticker.getInstrument().toString());

                exchanges.add(ticker.getExchange().toString().split("#")[0]);

                percentChanges.add(Double.toString(ticker.getPercentChange()));

                prices.add(Double.toString(ticker.getPriceInUSD()));

                instrumentNames.add(ticker.getName());

                bot.add(ticker.getInstrument().toString().split("/")[0]);


            }

            myAdapterBottom = new MyAdapter(instruments, exchanges, percentChanges, prices, instrumentNames,listener);
            recyclerViewBottom.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewBottom.setAdapter(myAdapterBottom);
            recyclerViewBottom.getAdapter().notifyDataSetChanged();
        });
    }
    private void toSecondPage(String currency){
        if (!this.name.contains(currency)){
            throw new NullPointerException();
        }

        Intent intent = new Intent(getApplicationContext(),MainActivity2.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            opportunities = registry.getArbitrage(new Currency(currency));
        }

        ArrayList<ArrayList<String>> stringOpps = new ArrayList<>();

        for (int i = 0; i < 2; i++){
            stringOpps.add(new ArrayList<>());
            for (TickerWithExchange ticker : opportunities[i]){
                ticker.setToUSD(registry.setTickerUSD(ticker));
                stringOpps.get(i).add(ticker.getInstrument().toString());
                stringOpps.get(i).add(ticker.getName());
                stringOpps.get(i).add(ticker.getExchange().toString().split("#")[0]);
                stringOpps.get(i).add(Double.toString(ticker.getPriceInUSD()));
                stringOpps.get(i).add(Double.toString(ticker.getPercentChange()));
            }
        }

        intent.putExtra("opps",stringOpps);

        startActivity(intent);
    }

}

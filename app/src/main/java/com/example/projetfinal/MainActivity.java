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
import android.widget.Toast;

import org.knowm.xchange.currency.Currency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import io.vavr.collection.Array;

/**
 * The type Main activity.
 * From the top 6 exchanges:
 * Binance
 * CoinbasePro
 * Kraken
 * GateIO  ???? idk why
 *
 * UpBit
 */
public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerViewTop;
    MyAdapter myAdapter;

    List<String> s1, s2, s3, s4;
    SwipeRefreshLayout swipeRefreshLayout;
    Context context;
    LiveData<ArrayList<TickerWithExchange>> highestPercentage;
    ArrayList<Integer> validExchanges;
    long startTime = System.currentTimeMillis();
    int aa,bb,cc,dd,ee,ff;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();

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
        s1=new ArrayList<>();
        s2=new ArrayList<>();
        s3=new ArrayList<>();
        s4=new ArrayList<>();

        recyclerViewTop= findViewById(R.id.recyclerViewTop);

        validExchanges = new ArrayList<>(Arrays.asList(aa, bb, cc, ff, dd)); // changer les valid exchanges ici selon les settings.
        // changer les valid exchanges dans le futur:
        // registry.setExchanges(validExchanges) avec validExchanges comme plus haut. dans l'ordre: binance, coinbasepro, kraken, upbit, gateio

        Registry registry = new Registry(validExchanges); // here we would pass the exchanges
        this.setTitle(R.string.title);

        // dans le 1er recycler view dans le main
        highestPercentage = registry.getMaxGainers();

        // dans le 2e recycler view dans le main
        LiveData<ArrayList<TickerWithExchange>> lowestPercentage = registry.getMinGainers();

        // POUR UTILISER TickerWithExchange
        // TickerWithExchange ticker = highestPercentage.get(0)
        // nom du exchange : ticker.getExchange().toString();
        // nom ticker : ticker.getName();
        // prix : ticker.getPrice();
        // prix en USD : ticker.getPriceToUSD();
        // percentage : ticker.getPercentage();


        // tous les currencies possible qu'on peut chercher, symboles et noms
        // pour get symboles: allCurrencies.get(index).toString()
        // pour get noms : allCurrencies.get(index).getDisplayName();
        // since it is a LiveData, we want to create default values for start of application (they can be empty) and update them in the observe method.
        // e will be like what you want to call. So, for allCurrencies, e would be an ArrayList<Currency>

        LiveData<ArrayList<Currency>> allCurrencies = registry.getAllCurrencies();

        allCurrencies.observe(this, e->{
            // do the code needed for, ie the search bar in here
        });

        //Mettres les éléments dans des strings pour le recyclerView top
        highestPercentage.observe(this, e->{

            for(TickerWithExchange ticker:e){
                s1.add(ticker.getName());
            }

            for(TickerWithExchange ticker:e){
                String temp;
                String temp2[];

                temp = ticker.getExchange().toString();
                temp2 = temp.split("#");

                s2.add(temp2[0]);
            }

            for(TickerWithExchange ticker:e){
                s3.add(Double.toString(ticker.getPercentChange()));
            }

            for(TickerWithExchange ticker:e){
                s4.add(Double.toString(ticker.getPrice()));
            }

            myAdapter = new MyAdapter(s1,s2,s3,s4);
            recyclerViewTop.setLayoutManager(new LinearLayoutManager(this));
            context = this;
            recyclerViewTop.setAdapter(myAdapter);


            // do the code to insert the ArrayList<TickerWithExchange> in the 1st recycler view here
        });

        //Mettres les éléments dans des strings pour le recyclerView Low
        lowestPercentage.observe(this, e->{

            for(TickerWithExchange ticker: e){
                s1.add(ticker.getName());
            }

            for(TickerWithExchange ticker:e){
                String temp;
                String temp2[];

                temp = ticker.getExchange().toString();
                temp2 = temp.split("#");

                s2.add(temp2[0]);
            }

            for(TickerWithExchange ticker:e){
                s3.add(Double.toString(ticker.getPercentChange()));
            }

            for(TickerWithExchange ticker:e){
                s4.add(Double.toString(ticker.getPrice()));
            }
            // do the code to insert the ArrayList<TickerWithExchange> in the 2nd recycler view here

        });

        swipeRefreshLayout = findViewById(R.id.refreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                long elapsedTime = System.currentTimeMillis() - startTime;
                long timeTillNextDisplayChange = 60000 - (elapsedTime % 60000);
                if( elapsedTime >60000){
                    update();
                    Toast.makeText(context,"Updating", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                    startTime =0;
                }else{
                    Toast.makeText(context,"wait "+Long.toString(timeTillNextDisplayChange/1000) +"s", Toast.LENGTH_SHORT).show();
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.options:
                Toast.makeText(this,"options", Toast.LENGTH_SHORT).show();
                //fonction juste en bas
                openActivity_options();
                return true;
            case R.id.propos:
                //j'ai pas faite de pages pour mais je pourrais
                Toast.makeText(this,"À propos", Toast.LENGTH_SHORT).show();
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
        s1.clear();
        s2.clear();
        s3.clear();
        s4.clear();
        validExchanges = new ArrayList<>(Arrays.asList(aa, bb, cc, dd, ff));

        Registry registry = null; // here we would pass the exchanges
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            registry = new Registry(validExchanges);
        }
        LiveData<ArrayList<TickerWithExchange>> highestPercentage = registry.getMaxGainers();

        // dans le 2e recycler view dans le main
        LiveData<ArrayList<TickerWithExchange>> lowestPercentage = registry.getMinGainers();

        LiveData<ArrayList<Currency>> allCurrencies = registry.getAllCurrencies();

        allCurrencies.observe(this, e->{
            // do the code needed for, ie the search bar in here
        });

        //Mettres les éléments dans des strings pour le recyclerView top
        highestPercentage.observe(this, e->{

            for(TickerWithExchange ticker:e){
                s1.add(ticker.getName());
            }

            for(TickerWithExchange ticker:e){
                String temp;
                String temp2[];

                temp = ticker.getExchange().toString();
                temp2 = temp.split("#");

                s2.add(temp2[0]);
            }

            for(TickerWithExchange ticker:e){
                s3.add(Double.toString(ticker.getPercentChange()));
            }

            for(TickerWithExchange ticker:e){
                s4.add(Double.toString(ticker.getPrice()));
            }

            myAdapter = new MyAdapter(s1,s2,s3,s4);
            recyclerViewTop.getAdapter().notifyDataSetChanged();
            recyclerViewTop.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewTop.setAdapter(myAdapter);

            Toast.makeText(this,getString(R.string.finish),Toast.LENGTH_SHORT).show();
            // do the code to insert the ArrayList<TickerWithExchange> in the 1st recycler view here
        });

        //Mettres les éléments dans des strings pour le recyclerView Low
        lowestPercentage.observe(this, e->{

            for(TickerWithExchange ticker: e){
                s1.add(ticker.getName());
            }

            for(TickerWithExchange ticker:e){
                String temp;
                String temp2[];

                temp = ticker.getExchange().toString();
                temp2 = temp.split("#");

                s2.add(temp2[0]);
            }

            for(TickerWithExchange ticker:e){
                s3.add(Double.toString(ticker.getPercentChange()));
            }

            for(TickerWithExchange ticker:e){
                s4.add(Double.toString(ticker.getPrice()));
            }
            // do the code to insert the ArrayList<TickerWithExchange> in the 2nd recycler view here
        });
    }

}

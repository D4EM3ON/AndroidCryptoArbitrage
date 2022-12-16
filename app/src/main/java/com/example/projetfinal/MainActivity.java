package com.example.projetfinal;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import org.knowm.xchange.currency.Currency;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The type Main activity.
 * From the top 6 exchanges:
 * Binance
 * CoinbasePro
 * Kraken
 * GateIO
 * UpBit
 */
public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerViewTop;
    MyAdapter myAdapter;

    List<String> s1, s2, s3, s4;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        s1=new ArrayList<>();
        s2=new ArrayList<>();
        s3=new ArrayList<>();
        s4=new ArrayList<>();

        recyclerViewTop=findViewById(R.id.recyclerViewTop);

        ArrayList<Integer> validExchanges = new ArrayList<>(Arrays.asList(1, 1, 1, 1, 1)); // changer les valid exchanges ici selon les settings.
        // changer les valid exchanges dans le futur:
        // registry.setExchanges(validExchanges) avec validExchanges comme plus haut. dans l'ordre: binance, coinbasepro, kraken, upbit, gateio

        Registry registry = new Registry(validExchanges); // here we would pass the exchanges
        this.setTitle(R.string.title);

        // dans le 1er recycler view dans le main
        LiveData<ArrayList<TickerWithExchange>> highestPercentage = registry.getMaxGainers();

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
                s2.add(ticker.getExchange().toString());
            }

            for(TickerWithExchange ticker:e){
                s3.add(Double.toString(ticker.getPercentChange()));
            }

            for(TickerWithExchange ticker:e){
                s4.add(Double.toString(ticker.getPrice()));
            }

            myAdapter = new MyAdapter(s1,s2,s3,s4);
            recyclerViewTop.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewTop.setAdapter(myAdapter);

            // do the code to insert the ArrayList<TickerWithExchange> in the 1st recycler view here
        });

        //Mettres les éléments dans des strings pour le recyclerView Low
        lowestPercentage.observe(this, e->{

            for(TickerWithExchange ticker: e){
                s1.add(ticker.getName());
            }

            for(TickerWithExchange ticker:e){
                s2.add(ticker.getExchange().toString());
            }

            for(TickerWithExchange ticker:e){
                s3.add(Double.toString(ticker.getPercentChange()));
            }

            for(TickerWithExchange ticker:e){
                s4.add(Double.toString(ticker.getPrice()));
            }
            // do the code to insert the ArrayList<TickerWithExchange> in the 2nd recycler view here
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
    //part pour menu arrete ici
}
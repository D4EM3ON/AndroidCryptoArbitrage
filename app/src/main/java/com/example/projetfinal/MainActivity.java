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

    RecyclerView recyclerViewTop = null;
    MyAdapter myAdapter;

    ArrayList<String> s1 = null, s2 = null, s3 = null, s4 = null;
    SwipeRefreshLayout swipeRefreshLayout;
    Context context;
    LiveData<ArrayList<TickerWithExchange>> highestPercentage;
    ArrayList<Integer> validExchanges;
    long startTime = System.currentTimeMillis();
    int aa,bb,cc,dd,ee,ff;
    private LiveData<ArrayList<TickerWithExchange>> lowestPercentage;
    private LiveData<ArrayList<Currency>> allCurrencies;

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

        validExchanges = new ArrayList<>(Arrays.asList(aa, bb, cc, dd, ff)); // changer les valid exchanges ici selon les settings.

        if (s1 == null){
            s1 = new ArrayList<>();
        }
        if (s2 == null){
            s2 = new ArrayList<>();
        }
        if (s3 == null){
            s3 = new ArrayList<>();
        }
        if (s4 == null){
            s4 = new ArrayList<>();
        }

        if (recyclerViewTop == null){
            recyclerViewTop = findViewById(R.id.recyclerViewTop);
        }

        s1.clear();
        s2.clear();
        s3.clear();
        s4.clear();

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
            // do the code needed for, ie the search bar in here
        });

        //Mettre les éléments dans des ArrayList pour la premiere partie du recyclerView
        highestPercentage.observe(this, e->{

            for(TickerWithExchange ticker:e){
                s1.add(ticker.getName());

                String temp;
                String temp2[];

                temp = ticker.getExchange().toString();
                temp2 = temp.split("#");

                s2.add(temp2[0]);

                s3.add(Double.toString(ticker.getPercentChange()));

                s4.add(Double.toString(ticker.getPrice()));
            }

            myAdapter = new MyAdapter(s1,s2,s3,s4);
            recyclerViewTop.getAdapter().notifyDataSetChanged();
            recyclerViewTop.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewTop.setAdapter(myAdapter);

            Toast.makeText(this,getString(R.string.finish),Toast.LENGTH_SHORT).show();
        });

        //Mettre les éléments dans des ArrayList pour la deuxieme partie du recyclerView
        lowestPercentage.observe(this, e->{

            for(TickerWithExchange ticker: e){
                s1.add(ticker.getName());

                String temp;
                String temp2[];

                temp = ticker.getExchange().toString();
                temp2 = temp.split("#");

                s2.add(temp2[0]);

                s3.add(Double.toString(ticker.getPercentChange()));

                s4.add(Double.toString(ticker.getPrice()));
            }
        });
    }

}

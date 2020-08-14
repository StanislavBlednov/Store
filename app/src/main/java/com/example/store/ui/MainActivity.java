package com.example.store.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.store.App;
import com.example.store.R;
import com.example.store.databinding.ActivityMainBinding;
import com.example.store.db.Product;
import com.example.store.ui.fragments.back.BackEndFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {
    private static final String TITLE = "MAIN_ACTIVITY_TITLE";
    private NavController navController;
    private MainActivityViewModel mainModel;
    private NavController.OnDestinationChangedListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(binding.mainAppBarLayout.appBarToolbar);

        App app = (App) getApplication();
        navController = Navigation.findNavController(this, R.id.main__fragment);

        NavigationUI.setupWithNavController(binding.mainAppBarLayout.appBarToolbar, navController);
        NavigationUI.setupWithNavController(binding.mainBottomNavLayout.bottomNavBottomNavView, navController);

        mainModel = new MainActivityViewModel(app.getAppAnimations(), app.getNavService());
        listener = app.getNavService().getNavListener();

        binding.setMainModel(mainModel);
        binding.executePendingBindings();
    }

    @Override
    protected void onStart() {
        super.onStart();
        navController.addOnDestinationChangedListener(listener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        navController.removeOnDestinationChangedListener(listener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.toolbarMenu__add){
            navController.navigate(R.id.productDetailFragment);
            return super.onOptionsItemSelected(item);
        } else if (item.getItemId() == R.id.toolbarMenu__save) {
            // TODO: 12.08.2020 save data
            Product product =
                    navController.getCurrentBackStackEntry() != null ?
                            navController.getCurrentBackStackEntry().getArguments() != null ?
                                    navController.getCurrentBackStackEntry().getArguments().getParcelable(BackEndFragment.PRODUCT) : null : null;
            if (product != null) {
                Log.d("TEST",  String.format("save: %s | %s | %f | %d", product.getId(), product.getName(), product.getPrice(), product.getPcs()));
            }
            navController.navigateUp();
            return super.onOptionsItemSelected(item);
        } else  {
            return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TITLE,
                getSupportActionBar() != null ?
                        getSupportActionBar().getTitle() != null ?
                                getSupportActionBar().getTitle().toString() : "" : "");
        if (mainModel != null) mainModel.save(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(savedInstanceState.getString(TITLE));
        }
        if (mainModel != null) mainModel.restore(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mainModel != null) mainModel.dispose();
    }

}
    package com.example.note;

    import android.content.Intent;
    import android.graphics.drawable.ColorDrawable;
    import android.os.Bundle;
    import android.view.MenuItem;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.appcompat.app.AppCompatDelegate;
    import androidx.core.view.GravityCompat;
    import androidx.drawerlayout.widget.DrawerLayout;
    import androidx.navigation.NavController;
    import androidx.navigation.Navigation;
    import androidx.navigation.ui.AppBarConfiguration;
    import androidx.navigation.ui.NavigationUI;
    import androidx.work.Data;
    import androidx.work.OneTimeWorkRequest;
    import androidx.work.WorkManager;

    import com.example.note.Data.UserData;
    import com.example.note.WorkManagerment.MyWork;
    import com.example.note.databinding.ActivityMainBinding;
    import com.google.android.material.navigation.NavigationView;

    import java.time.Duration;
    import java.time.ZoneId;
    import java.time.ZonedDateTime;
    import java.util.concurrent.TimeUnit;

    public class MainActivity extends AppCompatActivity {

        private AppBarConfiguration mAppBarConfiguration;
        private ActivityMainBinding binding;
        NavController navController;

        private final int FRAGMENT_HOME = 1;
        private final int FRAGMENT_ACCOUNT = 2;
        private final int FRAGMENT_TRASH = 3;
        private final int FRAGMENT_LOGOUT = 5;
        private  int curentFragment = FRAGMENT_HOME;

        public String idSinhVien;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action)));

            Intent intent = getIntent();
            idSinhVien = ((Intent) intent).getStringExtra("ID_SINHVIEN");
            UserData.setIdSinhVien(idSinhVien);

            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            DrawerLayout drawer = binding.drawerLayout;
            NavigationView navigationView = binding.navView;

            workExcute();


            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top-level destinations.
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_account, R.id.nav_settings, R.id.nav_logout, R.id.nav_trash)
                    .setOpenableLayout(drawer)
                    .build();

            navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);


            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();

                    if (id == R.id.nav_home) {

                        if (curentFragment != FRAGMENT_HOME) {
                            curentFragment = FRAGMENT_HOME;
                            navController.navigate(R.id.nav_home);
                        }
                    } else if (id == R.id.nav_account) {

                        if (curentFragment != FRAGMENT_ACCOUNT) {
                            curentFragment = FRAGMENT_ACCOUNT;
                            navController.navigate(R.id.nav_account);
                        }
                    } else if (id == R.id.nav_trash) {

                        if (curentFragment != FRAGMENT_TRASH) {
                            curentFragment = FRAGMENT_TRASH;
                            navController.navigate(R.id.nav_trash);
                        }
                    } else if (id == R.id.nav_logout) {

                        if (curentFragment != FRAGMENT_LOGOUT) {
                            logoutApp();
                        }
                    }
                    DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);

                    return true;
                }
            });

        }

        private void workExcute() {
            ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
            ZonedDateTime now = ZonedDateTime.now(zoneId);

            int targetHour = 7;
            int targetMinute = 0;
            int targetSecond = 0;

            ZonedDateTime nextExecutionTime = now.withHour(targetHour).withMinute(targetMinute).withSecond(targetSecond);

            if (now.compareTo(nextExecutionTime) >= 0) {
                nextExecutionTime = nextExecutionTime.plusDays(1);
            }

            // tinh thoi gian can de dat lich
            Duration duration = Duration.between(now, nextExecutionTime);

            Data inputData = new Data.Builder()
                    .putString("idSinhVien", idSinhVien)
                    .build();

            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(MyWork.class)
                    .setInputData(inputData)
                    .setInitialDelay(duration.toMillis(), TimeUnit.MILLISECONDS)
                    .build();

            WorkManager.getInstance().enqueue(workRequest);
        }

        @Override
        public boolean onSupportNavigateUp() {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
        }

        public void setNightMode(int mode) {
            AppCompatDelegate.setDefaultNightMode(mode);
            recreate();
        }

        private void logoutApp() {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
        }
    }

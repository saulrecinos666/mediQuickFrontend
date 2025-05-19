package com.example.mediquick.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.mediquick.R;
import com.example.mediquick.utils.SessionManager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton menuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();

            if (id == R.id.nav_logout) {
                logoutUser(); // <-- Método que elimina el token y redirige
                return true;
            }

            // Puedes manejar otros ítems también aquí
            return false;
        });


        setupDrawerMenu();
        setupQuickCard(
                R.id.card1,
                "Chat",
                "Empieza una conversación",
                R.drawable.chat_round_dots,
                ChatListActivity.class
        );

        // Puedes llamar a más tarjetas reutilizando setupQuickCard:
        // setupQuickCard(R.id.card2, "Citas", "Ver próximas citas", R.drawable.calendar, AppointmentsActivity.class);
    }

    /**
     * Configura el menú lateral
     */
    private void setupDrawerMenu() {
        drawerLayout = findViewById(R.id.drawer_layout);
        menuButton = findViewById(R.id.button);

        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
    }

    /**
     * Método reutilizable para configurar una tarjeta rápida
     */
    private void setupQuickCard(int cardIncludeId, String titleText, String subtitleText, int iconResId, Class<?> targetActivity) {
        View quickCardView = findViewById(cardIncludeId);

        TextView title = quickCardView.findViewById(R.id.quick_card_title);
        TextView subtitle = quickCardView.findViewById(R.id.quick_card_subtitle);
        ImageView icon = quickCardView.findViewById(R.id.quick_card_icon);
        MaterialCardView card = quickCardView.findViewById(R.id.quick_access_card);

        title.setText(titleText);
        subtitle.setText(subtitleText);
        icon.setImageResource(iconResId);

        card.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, targetActivity);
            startActivity(intent);
        });
    }

    private void logoutUser() {
        SessionManager sessionManager = new SessionManager(this);
        sessionManager.clearSession();

        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Borra el back stack
        startActivity(intent);
        finish(); // Finaliza HomeActivity
    }

}

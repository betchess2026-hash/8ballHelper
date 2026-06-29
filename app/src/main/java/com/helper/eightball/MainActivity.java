package com.helper.eightball;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50); // Malo razmaka da izgleda urednije

        Button btnOverlay = new Button(this);
        btnOverlay.setText("1. Dopusti crtanje preko zaslona");
        btnOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Pokušaj otvaranja postavki izravno za vašu aplikaciju
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                } catch (Exception e) {
                    // Ako gornja metoda baci grešku, otvaramo opću listu svih aplikacija
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    startActivity(intent);
                    Toast.makeText(MainActivity.this, "Pronađite '8Ball Helper' na popisu i dopustite crtanje.", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button btnService = new Button(this);
        btnService.setText("2. Aktiviraj 8Ball Helper");
        btnService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Otvara glavni izbornik za pristupačnost
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                    Toast.makeText(MainActivity.this, "Potražite '8Ball Helper' pod 'Preuzete aplikacije' ili 'Instalirane usluge'.", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Greška pri otvaranju postavki.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        layout.addView(btnOverlay);
        
        // Dodajemo malo razmaka između gumba
        View spacer = new View(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 40);
        spacer.setLayoutParams(params);
        layout.addView(spacer);
        
        layout.addView(btnService);
        
        setContentView(layout);
    }
}

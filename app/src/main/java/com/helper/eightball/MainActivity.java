package com.helper.eightball;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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
        layout.setPadding(50, 50, 50, 50);

        Button btnOverlay = new Button(this);
        btnOverlay.setText("1. Dopusti crtanje preko zaslona");
        
        // Vizualni indikator ako je dozvola već aktivna
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
            btnOverlay.setText("1. Crtanje preko zaslona: ODOBRENO");
            btnOverlay.setEnabled(false);
        }

        btnOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(MainActivity.this)) {
                        try {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + getPackageName()));
                            startActivity(intent);
                        } catch (Exception e) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                            startActivity(intent);
                            Toast.makeText(MainActivity.this, "Pronađite '8Ball Helper' na popisu i dopustite crtanje.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Dozvola za crtanje je već odobrena.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        Button btnService = new Button(this);
        btnService.setText("2. Aktiviraj 8Ball Helper");
        btnService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sigurnosna provjera: Ne dopuštaj paljenje servisa ako crtanje nije odobreno
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, "Prvo morate odobriti korak 1 (Crtanje preko zaslona)!", Toast.LENGTH_LONG).show();
                    return;
                }
                
                try {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                    Toast.makeText(MainActivity.this, "Potražite '8Ball Helper' pod 'Preuzete aplikacije' ili 'Instalirane usluge'.", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Greška pri otvaranju postavki.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        layout.addView(btnOverlay);
        
        View spacer = new View(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 40);
        spacer.setLayoutParams(params);
        layout.addView(spacer);
        
        layout.addView(btnService);
        
        setContentView(layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Osvježavanje ekrana kada se vratiš iz postavki mobitela u aplikaciju
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
            recreate(); // Ponovno iscrtaj sučelje da gumb promijeni stanje u "ODOBRENO"
        }
    }
}

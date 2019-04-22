package tw.com.voarlese.centeraligntitleactionbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    TitleCenterAlignActionBar actionBar;
    EditText e;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actionBar = findViewById(R.id.t);
        e = findViewById(R.id.edit);
        
        actionBar.findViewById(R.id.myTitle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
            }
        });
    }
    
    public void onChange(View view) {
        actionBar.setText(e.getText().toString());
        
    }
    
    public void onHideClick(View view) {
        //        if (actionBar.findViewById(R.id.imgDelete).getVisibility() == View.GONE) {
        //            actionBar.findViewById(R.id.imgDelete).setVisibility(View.VISIBLE);
        //        } else {
        //            actionBar.findViewById(R.id.imgDelete).setVisibility(View.GONE);
        //        }
        
        actionBar.setEndIconVisibility(View.GONE);
    }
    
    public void onShowClick(View view) {
        actionBar.setEndIconVisibility(View.VISIBLE);
    }
}


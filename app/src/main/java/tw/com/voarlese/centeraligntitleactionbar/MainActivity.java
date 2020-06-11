package tw.com.voarlese.centeraligntitleactionbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    TitleCenterAlignActionBar actionBar;
    EditText input;
    private View btnLeftIconMenu;
    private View btnRightIconMenu;
    private PopupMenu leftMenu;
    private PopupMenu rightMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actionBar = findViewById(R.id.actionBar);
        input = findViewById(R.id.edit);
        btnLeftIconMenu = findViewById(R.id.btnLeftIconMenu);
        btnRightIconMenu = findViewById(R.id.btnRightIconMenu);
        btnLeftIconMenu.setOnClickListener(this);
        btnRightIconMenu.setOnClickListener(this);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                actionBar.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        actionBar.setTitle(input.getText().toString());
    }

    public void showRightMenu(int menuLayout, View v, PopupMenu.OnMenuItemClickListener menuItemClickListener) {
        if (rightMenu == null) {
            rightMenu = new PopupMenu(this, v);
            init(rightMenu, menuLayout, menuItemClickListener);
        }
        rightMenu.show();
    }

    public void showLefttMenu(int menuLayout, View v, PopupMenu.OnMenuItemClickListener menuItemClickListener) {
        if (leftMenu == null) {
            leftMenu = new PopupMenu(this, v);
            init(leftMenu, menuLayout, menuItemClickListener);
        }
        leftMenu.show();
    }

    private void init(PopupMenu popupMenu, int menuLayout, PopupMenu.OnMenuItemClickListener menuItemClickListener) {
        popupMenu.setOnMenuItemClickListener(menuItemClickListener);
        popupMenu.inflate(menuLayout);
        setForceShowIcon(popupMenu);

    }

    public static void setForceShowIcon(PopupMenu popupMenu) {
        try {
            Field field = popupMenu.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            Object menuPopupHelper = field.get(popupMenu);
            Class<?> classPopupHelper = Class.forName(menuPopupHelper
                    .getClass().getName()); // 可能會是不同lib 所以要取得該 class
            Method setForceIcons = classPopupHelper.getMethod(
                    "setForceShowIcon", boolean.class);
            setForceIcons.invoke(menuPopupHelper, true);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void onHideClick(View view) {
        actionBar.setEndIconVisibility(View.GONE);
    }

    public void onShowClick(View view) {
        actionBar.setEndIconVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLeftIconMenu:
                showLefttMenu(R.menu.left_icon, v, leftListener);
                break;
            case R.id.btnRightIconMenu:
                showRightMenu(R.menu.right_icon, v, rightListener);
                break;
        }
    }

    private PopupMenu.OnMenuItemClickListener leftListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            setChecked(item);
            return false;
        }
    };
    private PopupMenu.OnMenuItemClickListener rightListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            setChecked(item);
            return false;
        }
    };

    private void setChecked(MenuItem item) {
        if (item.isChecked()) {
            item.setTitle(item.getTitle().toString().replace(" ✔️", ""));
        } else {
            item.setTitle(item.getTitle() + " ✔️");
        }
        item.setChecked(!item.isChecked());
        switchIcon(item.isChecked(), item.getTitle().toString());
    }

    private void switchIcon(boolean isSelected, String name) {
        name = name.replace(" ✔️", "");
        switch (name) {
            case "back":
                actionBar.findViewById(R.id.btnBack).setVisibility(isSelected ? View.VISIBLE : View.GONE);
                break;
            case "close":
                actionBar.findViewById(R.id.btnClose).setVisibility(isSelected ? View.VISIBLE : View.GONE);
                break;
            case "undo":
                actionBar.findViewById(R.id.btnUndo).setVisibility(isSelected ? View.VISIBLE : View.GONE);
                break;
            case "horiz":
                actionBar.findViewById(R.id.btnHoriz).setVisibility(isSelected ? View.VISIBLE : View.GONE);
                break;
            case "save":
                actionBar.findViewById(R.id.btnSave).setVisibility(isSelected ? View.VISIBLE : View.GONE);
                break;
            case "search":
                actionBar.findViewById(R.id.btnSearch).setVisibility(isSelected ? View.VISIBLE : View.GONE);
                break;
            case "share":
                actionBar.findViewById(R.id.btnShare).setVisibility(isSelected ? View.VISIBLE : View.GONE);
                break;
        }
    }
}


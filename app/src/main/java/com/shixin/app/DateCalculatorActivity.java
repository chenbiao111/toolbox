package com.shixin.app;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.TransitionManager;

import com.google.android.material.card.MaterialCardView;
import com.gyf.immersionbar.ImmersionBar;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DateCalculatorActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.cardview1)
    MaterialCardView cardView1;
    @BindView(R.id.cardview2)
    MaterialCardView cardView2;
    @BindView(R.id.textview1)
    TextView textView1;
    @BindView(R.id.textview2)
    TextView textView2;
    @BindView(R.id.textview3)
    TextView textView3;

    @BindView(R.id.cardview3)
    MaterialCardView cardView3;
    @BindView(R.id.cardview4)
    MaterialCardView cardView4;
    @BindView(R.id.textview4)
    TextView textView4;
    @BindView(R.id.textview5)
    EditText textView5;
    @BindView(R.id.textview6)
    TextView textView6;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_calculator);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.日期计算器));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        cardView1.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            @SuppressLint("SetTextI18n")
            DatePickerDialog datePickerDialog = new DatePickerDialog(DateCalculatorActivity.this, (view, year, monthOfYear, dayOfMonth) -> {
                DecimalFormat df = new DecimalFormat("00");
                textView1.setText(df.format((year)) + "-" + df.format((monthOfYear + 1)) + "-" + df.format(dayOfMonth));
                date();
            }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        cardView2.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            @SuppressLint("SetTextI18n")
            DatePickerDialog datePickerDialog = new DatePickerDialog(DateCalculatorActivity.this, (view, year, monthOfYear, dayOfMonth) -> {
                DecimalFormat df = new DecimalFormat("00");
                textView2.setText(df.format((year)) + "-" + df.format((monthOfYear + 1)) + "-" + df.format(dayOfMonth));
                date();
            }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        cardView3.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            @SuppressLint("SetTextI18n")
            DatePickerDialog datePickerDialog = new DatePickerDialog(DateCalculatorActivity.this, (view, year, monthOfYear, dayOfMonth) -> {
                DecimalFormat df = new DecimalFormat("00");
                textView4.setText(df.format((year)) + "-" + df.format((monthOfYear + 1)) + "-" + df.format(dayOfMonth));
                date1();
            }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        textView5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                date1();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void date1 () {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        try {
            Date date = sdf.parse(String.valueOf(textView4.getText()));
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(date);
            rightNow.add(Calendar.DAY_OF_YEAR, + Integer.parseInt(String.valueOf(textView5.getText())));
            Date time = rightNow.getTime();
            String format = sdf.format(time);
            TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
            textView6.setVisibility(View.VISIBLE);
            textView6.setText(textView5.getText() + "天后为：" + format);
        } catch (Exception e) {
            e.printStackTrace();
            TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
            textView6.setVisibility(View.GONE);
        }

    }
    private void date () {
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = null;
            try {
                date1 = simpleDateFormat.parse(String.valueOf(textView1.getText()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Date date2 = null;
            try {
                date2 = simpleDateFormat.parse(String.valueOf(textView2.getText()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            GregorianCalendar cal1 = new GregorianCalendar();
            GregorianCalendar cal2 = new GregorianCalendar();
            cal1.setTime(date1);
            cal2.setTime(date2);
            double dayCount = (cal2.getTimeInMillis()-cal1.getTimeInMillis())/(1000*3600*24);//从间隔毫秒变成间隔天数
            TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
            textView3.setVisibility(View.VISIBLE);
            textView3.setText("日期相差："+dayCount+"天");
        } catch (Exception e) {
            TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
            textView3.setVisibility(View.GONE);
        }

    }
}
package com.r4sh33d.medmanager;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.r4sh33d.medmanager.database.MedicationDBContract;
import com.r4sh33d.medmanager.database.MedicationsListLoader;
import com.r4sh33d.medmanager.datepickers.DatePickerFragment;
import com.r4sh33d.medmanager.models.Medication;
import com.r4sh33d.medmanager.utility.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class MonthlyMedicationsFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Medication>> {
    @BindView(R.id.fab)
    FloatingActionButton changeMonthFab;

    @BindView(R.id.recyclerview)
    RecyclerView monthlyRecyclerView;

    @BindView(R.id.month_label)
    TextView monthLabel;
    MedicationsListAdapter medicationsListAdapter;
    private String MONTHLY_INTERVAL_SELECTION = "";
    Calendar calendar;

    public MonthlyMedicationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_monthly_medications, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        medicationsListAdapter = new MedicationsListAdapter(new ArrayList<Medication>());
        monthlyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        monthlyRecyclerView.setAdapter(medicationsListAdapter);
        calendar = Calendar.getInstance();
        Utils.setCalenderDefault(calendar);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Calendar thisMonthCalender = Calendar.getInstance();
        calendar.set(Calendar.YEAR , thisMonthCalender.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH , thisMonthCalender.get(Calendar.MONTH));
        monthLabel.setText(getFormattedDateFormantLabel(calendar));
        MONTHLY_INTERVAL_SELECTION = getMonthIntervalSelection(calendar);
        getLoaderManager().initLoader(0, null, this);
    }


    @NonNull
    @Override
    public Loader<ArrayList<Medication>> onCreateLoader(int id, Bundle args) {
        return new MedicationsListLoader(getContext(), MONTHLY_INTERVAL_SELECTION , null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Medication>> loader, ArrayList<Medication> data) {
        medicationsListAdapter.updateData(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Medication>> loader) {
    }

    public void onDateSet (boolean isStartDate, DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH , month);
        monthLabel.setText(getFormattedDateFormantLabel(calendar));
        MONTHLY_INTERVAL_SELECTION = getMonthIntervalSelection(calendar);
        getLoaderManager().restartLoader(0, null, this);
    }

    String getFormattedDateFormantLabel(Calendar calendar){
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        return  month_date.format(calendar.getTime());
    }

    String getMonthIntervalSelection (Calendar calendar) {
        long startInterval = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, 1);
        long endInterval = calendar.getTimeInMillis();
        return  startInterval + " >= " + MedicationDBContract.MedicationEntry.COLUMN_MEDICATION_START_TIME
                + "AND " + endInterval + " <= " + MedicationDBContract.MedicationEntry.COLUMN_MEDICATION_END_TIME;
    }


    @OnClick(R.id.fab)
    void onCLickFab() {
        DialogFragment newFragment = DatePickerFragment.newInstance(true, false);
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "datePicker");
    }
}

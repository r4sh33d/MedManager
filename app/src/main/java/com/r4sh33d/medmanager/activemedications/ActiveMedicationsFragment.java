package com.r4sh33d.medmanager.activemedications;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.r4sh33d.medmanager.base.BaseFragment;
import com.r4sh33d.medmanager.R;
import com.r4sh33d.medmanager.activities.MainActivity;
import com.r4sh33d.medmanager.addmedication.AddMedicationFragment;
import com.r4sh33d.medmanager.database.MedicationDao;
import com.r4sh33d.medmanager.database.MedicationsListLoader;
import com.r4sh33d.medmanager.models.Medication;
import com.r4sh33d.medmanager.recycleradapters.MedicationsListAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class ActiveMedicationsFragment extends BaseFragment implements ActiveMedicationsContract.View,
        LoaderManager.LoaderCallbacks<ArrayList<Medication>> {
    private static final String TAG = ActiveMedicationsFragment.class.getSimpleName();
    @BindView(R.id.recyclerview)
    RecyclerView medsListRecyclerView;
    @BindView(R.id.empty_med_textView)
    TextView emptyMedTextView;
    MedicationsListAdapter activeMedicationsListAdapter;

    public ActiveMedicationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(100, null, this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_medications, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setToolbarTitle("Active Medications");
        ((MainActivity) getActivity()).setDrawerIconToHome();
        activeMedicationsListAdapter = new MedicationsListAdapter(new ArrayList<Medication>(),
                false);
        medsListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        medsListRecyclerView.setAdapter(activeMedicationsListAdapter);
        getLoaderManager().restartLoader(100, null, this);
    }

    @OnClick(R.id.fab)
    void onClickFab() {
        navigateToFragment(AddMedicationFragment.newInstance(null));
    }

    void navigateToFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @NonNull
    @Override
    public Loader<ArrayList<Medication>> onCreateLoader(int id, Bundle args) {
        return new MedicationsListLoader(getContext(), MedicationDao.ACTIVE_MEDICATION_SELECTION, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Medication>> loader, ArrayList<Medication> data) {
        Log.d(TAG, data+ "");
        if (!(data.size() > 0 )){
            emptyMedTextView.setVisibility(View.VISIBLE);
            medsListRecyclerView.setVisibility(View.GONE);
        }else {
            emptyMedTextView.setVisibility(View.GONE);
            medsListRecyclerView.setVisibility(View.VISIBLE);
        }
        activeMedicationsListAdapter.updateData(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Medication>> loader) {
    }

    @Override
    public void moveToNextStep() {
    }
}

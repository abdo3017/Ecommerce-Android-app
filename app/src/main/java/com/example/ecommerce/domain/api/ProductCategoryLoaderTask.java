/*
 * Copyright (c) 2017. http://hiteshsahu.com- All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * If you use or distribute this project then you MUST ADD A COPY OF LICENCE
 * along with the project.
 *  Written by Hitesh Sahu <hiteshkrsahu@Gmail.com>, 2017.
 */

package com.example.ecommerce.domain.api;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.example.ecommerce.domain.mock.FakeWebServer;
import com.example.ecommerce.model.CenterRepository;
import com.example.ecommerce.model.entities.ProductCategoryModel;
import com.example.ecommerce.util.AppConstants;
import com.example.ecommerce.util.Utils;
import com.example.ecommerce.util.Utils.AnimationType;
import com.example.ecommerce.view.activities.ECartHomeActivity;
import com.example.ecommerce.view.adapter.CategoryListAdapter;
import com.example.ecommerce.view.adapter.CategoryListAdapter.OnItemClickListener;
import com.example.ecommerce.view.fragment.ProductOverviewFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * The Class ImageLoaderTask.
 */
public class ProductCategoryLoaderTask extends AsyncTask<String, Void, Void> {

    private static final int NUMBER_OF_COLUMNS = 2;
    private Context context;
    private RecyclerView recyclerView;

    public ProductCategoryLoaderTask(RecyclerView listView, Context context) {
        this.recyclerView = listView;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();

        if (null != ((ECartHomeActivity) context).getProgressBar())
            ((ECartHomeActivity) context).getProgressBar().setVisibility(
                    View.VISIBLE);

    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        if (null != ((ECartHomeActivity) context).getProgressBar())
            ((ECartHomeActivity) context).getProgressBar().setVisibility(
                    View.GONE);

        if (recyclerView != null) {

            FirebaseFirestore firestore ;
            final ArrayList<ProductCategoryModel>listOfCategory = new ArrayList<ProductCategoryModel>();
            firestore  = FirebaseFirestore.getInstance();
            Query query = firestore.collection("Categories");
            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    for(DocumentSnapshot snapshot : queryDocumentSnapshots)
                    {
                        ProductCategoryModel productCategoryModel = snapshot.toObject(ProductCategoryModel.class);
                        listOfCategory.add(productCategoryModel);
                    }

                    CategoryListAdapter simpleRecyclerAdapter = new CategoryListAdapter(context,listOfCategory);
                    recyclerView.setAdapter(simpleRecyclerAdapter);
                    simpleRecyclerAdapter
                            .SetOnItemClickListener(new OnItemClickListener() {

                                @Override
                                public void onItemClick(View view, int position) {

                                    AppConstants.CURRENT_CATEGORY = position;

                                    Utils.switchFragmentWithAnimation(
                                            R.id.frag_container,
                                            new ProductOverviewFragment(),
                                            ((ECartHomeActivity) context), null,
                                            AnimationType.SLIDE_LEFT);

                                }
                            });



                }
            });



        }

    }



    @Override
    protected Void doInBackground(String... params) {

        try {
            Thread.sleep(2000);
        }catch (Exception e)
        {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return null;
    }

}
/*
 * Copyright (c) 2017. http://hiteshsahu.com- All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * If you use or distribute this project then you MUST ADD A COPY OF LICENCE
 * along with the project.
 *  Written by Hitesh Sahu <hiteshkrsahu@Gmail.com>, 2017.
 */

package com.example.ecommerce.view.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ecommerce.model.entities.Product;
import com.example.ecommerce.model.entities.myData;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.example.ecommerce.R;
import com.example.ecommerce.domain.mock.FakeWebServer;
import com.example.ecommerce.model.CenterRepository;
import com.example.ecommerce.util.AppConstants;
import com.example.ecommerce.util.Utils;
import com.example.ecommerce.util.Utils.AnimationType;
import com.example.ecommerce.view.activities.ECartHomeActivity;
import com.example.ecommerce.view.adapter.ProductsInCategoryPagerAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ProductOverviewFragment extends Fragment {

    // SimpleRecyclerAdapter adapter;

    private ArrayList<Product> productlist;
    private FirebaseFirestore firestore ;
    public static ConcurrentHashMap<String, ArrayList<Product>> productMap;

    private KenBurnsView header;
    private Bitmap bitmap;
    private Toolbar mToolbar;
    private ViewPager viewPager;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_category_details,
                container, false);

        productMap = new ConcurrentHashMap<String, ArrayList<Product>>();
        firestore = FirebaseFirestore.getInstance();
        productlist = new ArrayList<>();
        getActivity().setTitle("Products");


        // Simulate Web service calls
//        FakeWebServer.getFakeWebServer().getAllProducts(AppConstants.CURRENT_CATEGORY);

        // TODO We Can use Async task But pallete creation is problemitic job
        // will
        // get back to it later

        // new ProductLoaderTask(null, getActivity(), viewPager, tabLayout);

        // Volley can be used here very efficiently but Fake JSON creation is
        // time consuming Leain it now

        viewPager = (ViewPager) view.findViewById(R.id.htab_viewpager);

        collapsingToolbarLayout = (CollapsingToolbarLayout) view
                .findViewById(R.id.htab_collapse_toolbar);
        collapsingToolbarLayout.setTitleEnabled(false);

        header = (KenBurnsView) view.findViewById(R.id.htab_header);
        header.setImageResource(R.drawable.header);

        tabLayout = (TabLayout) view.findViewById(R.id.htab_tabs);

        mToolbar = (Toolbar) view.findViewById(R.id.htab_toolbar);
        if (mToolbar != null) {
            ((ECartHomeActivity) getActivity()).setSupportActionBar(mToolbar);
        }

        if (mToolbar != null) {
            ((ECartHomeActivity) getActivity()).getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);

            mToolbar.setNavigationIcon(R.drawable.ic_drawer);

        }

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ECartHomeActivity) getActivity()).getmDrawerLayout()
                        .openDrawer(GravityCompat.START);
            }
        });

        //setUpUi();
        getAllProducts();
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP
                        && keyCode == KeyEvent.KEYCODE_BACK) {

                    Utils.switchContent(R.id.frag_container,
                            Utils.HOME_FRAGMENT,
                            ((ECartHomeActivity) (getContext())),
                            AnimationType.SLIDE_RIGHT);

                }
                return true;
            }
        });

        return view;
    }

    public void getAllProducts() {

        if (AppConstants.CURRENT_CATEGORY== 0) {
            getAllCarsFirestore();

        } else if(AppConstants.CURRENT_CATEGORY== 1) {
            getAllClothesFirestore();


        } else if(AppConstants.CURRENT_CATEGORY== 2) {
            getAllElectronicsFirestore();

        }
        else
        {
            getAllFurnituresFirestore();
        }


    }

    public void getAllFurnituresFirestore()
    {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                setUpUi();
            }
        },2000);

        Query query =  firestore.collection("Categories")
                .document("Furnitures").collection("Products");

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for(DocumentSnapshot snapshot: queryDocumentSnapshots) {
                    final myData myData = snapshot.toObject(myData.class);
                    //Toast.makeText(getContext(), myData.getProductid(), Toast.LENGTH_SHORT).show();
                    Query query1 = firestore.collection("Categories")
                            .document("Furnitures")
                            .collection("Products")
                            .document(myData.getProductid())
                            .collection("ProductList");
                    query1.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                            productlist = new ArrayList<>();
                            Toast.makeText(getContext(), queryDocumentSnapshots.size()+"  sizeof"+myData.getProductid(), Toast.LENGTH_SHORT).show();
                            for (DocumentSnapshot snapshot1 : queryDocumentSnapshots)
                            {
                                Product product = snapshot1.toObject(Product.class);
                                productlist.add(product);
                            }

                            productMap.put(myData.getProductid(),productlist);
                            Toast.makeText(getContext(), productlist.size()+"  sas", Toast.LENGTH_SHORT).show();


                        }
                    });


                }
            }
        });
    }
    public void getAllElectronicsFirestore()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(getContext(), "sssssddfff", Toast.LENGTH_SHORT).show();
                setUpUi();
            }
        },2000);



        Query query =  firestore.collection("Categories")
                .document("Electronic").collection("Products");

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for(DocumentSnapshot snapshot: queryDocumentSnapshots) {
                    final myData myData = snapshot.toObject(myData.class);
                    //Toast.makeText(getContext(), myData.getProductid(), Toast.LENGTH_SHORT).show();
                 Query query1 = firestore.collection("Categories")
                            .document("Electronic")
                            .collection("Products")
                            .document(myData.getProductid())
                            .collection("ProductList");
                 query1.addSnapshotListener(new EventListener<QuerySnapshot>() {
                     @Override
                     public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                         productlist = new ArrayList<>();
                         Toast.makeText(getContext(), queryDocumentSnapshots.size()+"  sizeof"+myData.getProductid(), Toast.LENGTH_SHORT).show();
                         for (DocumentSnapshot snapshot1 : queryDocumentSnapshots)
                         {
                             Product product = snapshot1.toObject(Product.class);
                             productlist.add(product);
                         }

                         productMap.put(myData.getProductid(),productlist);
                         Toast.makeText(getContext(), productlist.size()+"  sas", Toast.LENGTH_SHORT).show();

                     }
                 });

                }
            }
        });
    }
    public void getAllCarsFirestore()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                setUpUi();
            }
        },2000);



        Query query =  firestore.collection("Categories")
                .document("Cars").collection("Products");

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for(DocumentSnapshot snapshot: queryDocumentSnapshots) {
                    final myData myData = snapshot.toObject(myData.class);
                    //Toast.makeText(getContext(), myData.getProductid(), Toast.LENGTH_SHORT).show();
                    Query query1 = firestore.collection("Categories")
                            .document("Cars")
                            .collection("Products")
                            .document(myData.getProductid())
                            .collection("ProductList");
                    query1.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                            productlist = new ArrayList<>();
                            Toast.makeText(getContext(), queryDocumentSnapshots.size()+"  sizeof"+myData.getProductid(), Toast.LENGTH_SHORT).show();
                            for (DocumentSnapshot snapshot1 : queryDocumentSnapshots)
                            {
                                Product product = snapshot1.toObject(Product.class);
                                productlist.add(product);
                            }

                            productMap.put(myData.getProductid(),productlist);
                            Toast.makeText(getContext(), productlist.size()+"  sas", Toast.LENGTH_SHORT).show();

                        }
                    });

                }
            }
        });
    }
    public void getAllClothesFirestore()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(getContext(), "sssssddfff", Toast.LENGTH_SHORT).show();
                setUpUi();
            }
        },2000);



        Query query =  firestore.collection("Categories")
                .document("Clothes").collection("Products");

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for(DocumentSnapshot snapshot: queryDocumentSnapshots) {
                    final myData myData = snapshot.toObject(myData.class);
                    //Toast.makeText(getContext(), myData.getProductid(), Toast.LENGTH_SHORT).show();
                    Query query1 = firestore.collection("Categories")
                            .document("Clothes")
                            .collection("Products")
                            .document(myData.getProductid())
                            .collection("ProductList");
                    query1.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                            productlist = new ArrayList<>();
                            Toast.makeText(getContext(), queryDocumentSnapshots.size()+"  sizeof"+myData.getProductid(), Toast.LENGTH_SHORT).show();
                            for (DocumentSnapshot snapshot1 : queryDocumentSnapshots)
                            {
                                Product product = snapshot1.toObject(Product.class);
                                productlist.add(product);
                            }

                            productMap.put(myData.getProductid(),productlist);
                            Toast.makeText(getContext(), productlist.size()+"  sas", Toast.LENGTH_SHORT).show();

                        }
                    });

                }
            }
        });
    }


    private void setUpUi() {

        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);

        bitmap = BitmapFactory
                .decodeResource(getResources(), R.drawable.header);

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @SuppressWarnings("ResourceType")
            @Override
            public void onGenerated(Palette palette) {

                int vibrantColor = palette.getVibrantColor(R.color.primary_500);
                int vibrantDarkColor = palette
                        .getDarkVibrantColor(R.color.primary_700);
                collapsingToolbarLayout.setContentScrimColor(vibrantColor);
                collapsingToolbarLayout
                        .setStatusBarScrimColor(vibrantDarkColor);
            }
        });

        tabLayout
                .setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {

                        viewPager.setCurrentItem(tab.getPosition());

                        switch (tab.getPosition()) {
                            case 0:

                                header.setImageResource(R.drawable.header);

                                bitmap = BitmapFactory.decodeResource(
                                        getResources(), R.drawable.header);

                                Palette.from(bitmap).generate(
                                        new Palette.PaletteAsyncListener() {
                                            @SuppressWarnings("ResourceType")
                                            @Override
                                            public void onGenerated(Palette palette) {

                                                int vibrantColor = palette
                                                        .getVibrantColor(R.color.primary_500);
                                                int vibrantDarkColor = palette
                                                        .getDarkVibrantColor(R.color.primary_700);
                                                collapsingToolbarLayout
                                                        .setContentScrimColor(vibrantColor);
                                                collapsingToolbarLayout
                                                        .setStatusBarScrimColor(vibrantDarkColor);
                                            }
                                        });
                                break;
                            case 1:

                                header.setImageResource(R.drawable.header_1);

                                bitmap = BitmapFactory.decodeResource(
                                        getResources(), R.drawable.header_1);

                                Palette.from(bitmap).generate(
                                        new Palette.PaletteAsyncListener() {
                                            @SuppressWarnings("ResourceType")
                                            @Override
                                            public void onGenerated(Palette palette) {

                                                int vibrantColor = palette
                                                        .getVibrantColor(R.color.primary_500);
                                                int vibrantDarkColor = palette
                                                        .getDarkVibrantColor(R.color.primary_700);
                                                collapsingToolbarLayout
                                                        .setContentScrimColor(vibrantColor);
                                                collapsingToolbarLayout
                                                        .setStatusBarScrimColor(vibrantDarkColor);
                                            }
                                        });

                                break;
                            case 2:

                                header.setImageResource(R.drawable.header2);

                                Bitmap bitmap = BitmapFactory.decodeResource(
                                        getResources(), R.drawable.header2);

                                Palette.from(bitmap).generate(
                                        new Palette.PaletteAsyncListener() {
                                            @SuppressWarnings("ResourceType")
                                            @Override
                                            public void onGenerated(Palette palette) {

                                                int vibrantColor = palette
                                                        .getVibrantColor(R.color.primary_500);
                                                int vibrantDarkColor = palette
                                                        .getDarkVibrantColor(R.color.primary_700);
                                                collapsingToolbarLayout
                                                        .setContentScrimColor(vibrantColor);
                                                collapsingToolbarLayout
                                                        .setStatusBarScrimColor(vibrantDarkColor);
                                            }
                                        });

                                break;
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });

    }

    private void setupViewPager(ViewPager viewPager) {
        ProductsInCategoryPagerAdapter adapter = new ProductsInCategoryPagerAdapter(
                getActivity().getSupportFragmentManager());
        //Set<String> keys = CenterRepository.getCenterRepository().getMapOfProductsInCategory().keySet();
        Set<String> keys =productMap.keySet();

        Toast.makeText(getContext(), keys.size()+"   Keys", Toast.LENGTH_SHORT).show();
        for (String string : keys) {
            adapter.addFrag(new ProductListFragment(string), string);
        }

        viewPager.setAdapter(adapter);
//		viewPager.setPageTransformer(true,
//				Utils.currentPageTransformer(getActivity()));
    }


    // TODO
    //Below Code Work Well But requires JSOn to work
    // Below line of code does caching for offline usage

	
	/*void fillProductMapFromCache() {

		String cached_ProductMapJSON = PreferenceHelper
				.getPrefernceHelperInstace().getString(
						PreferenceHelper.ALL_PRODUCT_LIST_RESPONSE_JSON, null);

		if (null != cached_ProductMapJSON) {
			new JSONParser(NetworkConstants.GET_ALL_PRODUCT,
					cached_ProductMapJSON).parse();

			adapter.notifyDataSetChanged();

		}

	}

	public void fillCategoryData() {

		loadingIndicator.setVisibility(View.VISIBLE);

		JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET,
				NetworkConstants.URL_GET_PRODUCTS_MAP,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {

						if (getView() != null && getView().isShown()) {

							new JSONParser(NetworkConstants.GET_ALL_PRODUCT,
									response.toString()).parse();

							PreferenceHelper
									.getPrefernceHelperInstace()
									.setString(
											PreferenceHelper.ALL_PRODUCT_LIST_RESPONSE_JSON,
											response.toString());
							
							setUpPager();


							if (null != loadingIndicator) {
								loadingIndicator.setVisibility(View.GONE);
							}

						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

						fillProductMapFromCache();


						if (null != loadingIndicator) {
							loadingIndicator.setVisibility(View.GONE);
						}
						if (error instanceof TimeoutError
								|| error instanceof NoConnectionError) {


							if (null != getActivity())
								((ECartHomeActivity) getActivity())
										.ShowErrorMessage(Errorhandler.OFFLINE_MODE, true);

						} else if (error instanceof AuthFailureError) {
							// TODO
						} else if (error instanceof ServerError) {

							
							if (null != getActivity())
								((ECartHomeActivity) getActivity())
										.ShowErrorMessage(Errorhandler.SERVER_ERROR, true);
							// TODO
						} else if (error instanceof NetworkError) {

							
							if (null != getActivity())
								((ECartHomeActivity) getActivity())
										.ShowErrorMessage(Errorhandler.NETWORK_ERROR, true);

						} else if (error instanceof ParseError) {

							if (null != getActivity())
								Toast.makeText(
										getActivity(),
										"Parsing Error" + error.networkResponse
												+ error.getLocalizedMessage(),
										Toast.LENGTH_LONG).show();

						}
					}

				}) {

		};

		// jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(60000 * 2, 0, 0));

		jsonObjReq.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS
				.toMillis(60), DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		AppController.getInstance().addToRequestQueue(jsonObjReq, tagJSONReq);

	}
*/
}
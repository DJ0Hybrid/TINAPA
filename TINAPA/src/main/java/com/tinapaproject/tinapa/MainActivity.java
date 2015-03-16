package com.tinapaproject.tinapa;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.tinapaproject.tinapa.database.key.DexKeyValues;
import com.tinapaproject.tinapa.database.key.OwnedKeyValues;
import com.tinapaproject.tinapa.database.provider.TinapaContentProvider;
import com.tinapaproject.tinapa.fragments.DexDetailFragment;
import com.tinapaproject.tinapa.fragments.DexDetailFragment.DexDetailListener;
import com.tinapaproject.tinapa.fragments.DexListFragment;
import com.tinapaproject.tinapa.fragments.DexListFragment.DexListListener;
import com.tinapaproject.tinapa.fragments.OwnedAddDialogFragment;
import com.tinapaproject.tinapa.fragments.OwnedAddDialogFragment.OwnedAddFragmentListener;
import com.tinapaproject.tinapa.fragments.OwnedListFragment;
import com.tinapaproject.tinapa.fragments.OwnedListFragment.OwnedListListener;
import com.tinapaproject.tinapa.fragments.PlannedListFragment;
import com.tinapaproject.tinapa.fragments.PlannedListFragment.PlannedListListener;

public class MainActivity extends Activity implements DexListListener, DexDetailListener, OwnedListListener, OwnedAddFragmentListener, PlannedListListener {

    private String temp_id;
    private ImageView temp_imageView;
    private String temp_column;

    public static int RESULT_LOAD_DEX_LIST_ICON = 100;

    public static final String SAVE_STATE_SELECTED_TAB_INDEX = "SAVE_STATE_SELECTED_TAB_INDEX";

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);

        Tab dexTab = actionBar.newTab()
                .setText(R.string.tab_pokedex)
                .setTabListener(new TabListener<DexListFragment>("Pokedex" /*TODO: Needs to be a field. */, new DexListFragment()));
        actionBar.addTab(dexTab, true);

        Tab ownedTab = actionBar.newTab()
                .setText(R.string.tab_owned_pokemon)
                .setTabListener(new TabListener<OwnedListFragment>("Owned" /*TODO: Needs to be a field. */, new OwnedListFragment()));
        actionBar.addTab(ownedTab);

        Tab plannedTab = actionBar.newTab()
                .setText(R.string.tab_planned_pokemon)
                .setTabListener(new TabListener<PlannedListFragment>("Planned" /*TODO: Needs to be a field. */, new PlannedListFragment()));
        actionBar.addTab(plannedTab);

        // TODO Teams
        Log.i(TAG, "All tabs have been added.");

        if (savedInstanceState != null) {
            actionBar.setSelectedNavigationItem(savedInstanceState.getInt(SAVE_STATE_SELECTED_TAB_INDEX, 0));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_DEX_LIST_ICON && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            BitmapFactory.Options imageOptions = new BitmapFactory.Options();
            imageOptions.outHeight = temp_imageView.getHeight();
            imageOptions.outWidth = temp_imageView.getWidth();
            temp_imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath, imageOptions));

            // Now store the path
            ContentValues values = new ContentValues();
            values.put(temp_column, picturePath);
            getContentResolver().update(TinapaContentProvider.POKEDEX_POKEMON_IMAGE_URI, values, DexKeyValues.insertIntoImageColumnWhereCreation(temp_id), null);

            temp_id = null;
            temp_imageView = null;
            temp_column = null;
        }
    }

    // From DexCursorAdapter
    @Override
    public void onDexItemClicked(String id) {
        // TODO: Pull information using the ID based off of the topic.
//        Cursor pokemonCursor = getContentResolver().query(TinapaContentProvider.POKEDEX_URI, null, "pokemon.id = " + id, null, null);
//        Cursor movesCursor = getContentResolver().query(TinapaContentProvider.POKEDEX_POKEMON_MOVES_URI, null, "pokemon_moves.pokemon_id = " + id, null, null);

        FrameLayout fragmentView = (FrameLayout) findViewById(R.id.mainActivityFragment2);
        if (fragmentView == null) {
            fragmentView = (FrameLayout) findViewById(R.id.mainActivityFragment1);
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(fragmentView.getId(), DexDetailFragment.newInstance(id), "TAG HERE");
        ft.addToBackStack("DexDetail");
        ft.commit();

    }

    // From DexCursorAdapter
    @Override
    public void onDexImageLongClicked(String id, ImageView imageView, String column) {
        loadImage(id, imageView, column);
    }

    // From DexDetailFragment
    @Override
    public void onDexDetailImageLongClicked(String id, ImageView imageView, String column) {
        loadImage(id, imageView, column);
    }

    // From OwnedListFragment
    @Override
    public void onOwnedItemClicked(String id) {
        FrameLayout fragmentView = (FrameLayout) findViewById(R.id.mainActivityFragment2);
        if (fragmentView == null) {
            fragmentView = (FrameLayout) findViewById(R.id.mainActivityFragment1);
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(fragmentView.getId(), OwnedAddDialogFragment.newInstance(id), "TAG HERE");
        ft.addToBackStack("OwnedDetail");
        ft.commit();
    }

    // From OwnedListFragment
    @Override
    public void onAddOwnedClicked() {
        OwnedAddDialogFragment dialogFragment = OwnedAddDialogFragment.newInstance();
        dialogFragment.show(getFragmentManager(), OwnedAddDialogFragment.TAG);
    }

    // From OwnedAddDialog
    @Override
    public void onPositiveClicked(int level, String nickname, boolean shinny, String speciesId, String abilityId, String natureId, String genderId, String move1Id, String move2Id, String move3Id, String move4Id, int ivHP, int ivAtt, int ivDef, int ivSAtt, int ivSDef, int ivSpd, int evHP, int evAtt, int evDef, int evSAtt, int evSDef, int evSpd, String notes, String planId) {
        // TODO
        ContentValues contentValues = new ContentValues();
        contentValues.put(OwnedKeyValues.LEVEL, level);
        contentValues.put(OwnedKeyValues.NICKNAME, nickname);
        contentValues.put(OwnedKeyValues.SHINNY, shinny);
        contentValues.put(OwnedKeyValues.POKEMON_ID, speciesId);
        contentValues.put(OwnedKeyValues.ABILITY_ID, abilityId);
        contentValues.put(OwnedKeyValues.NATURE_ID, natureId);
        contentValues.put(OwnedKeyValues.GENDER_ID, genderId);
        contentValues.put(OwnedKeyValues.MOVE1_ID, move1Id);
        contentValues.put(OwnedKeyValues.MOVE2_ID, move2Id);
        contentValues.put(OwnedKeyValues.MOVE3_ID, move3Id);
        contentValues.put(OwnedKeyValues.MOVE4_ID, move4Id);
        contentValues.put(OwnedKeyValues.IV_HP, ivHP);
        contentValues.put(OwnedKeyValues.IV_ATT, ivAtt);
        contentValues.put(OwnedKeyValues.IV_DEF, ivDef);
        contentValues.put(OwnedKeyValues.IV_SATT, ivSAtt);
        contentValues.put(OwnedKeyValues.IV_SDEF, ivSDef);
        contentValues.put(OwnedKeyValues.IV_SPD, ivSpd);
        contentValues.put(OwnedKeyValues.EV_HP, evHP);
        contentValues.put(OwnedKeyValues.EV_ATT, evAtt);
        contentValues.put(OwnedKeyValues.EV_DEF, evDef);
        contentValues.put(OwnedKeyValues.EV_SATT, evSAtt);
        contentValues.put(OwnedKeyValues.EV_SDEF, evSDef);
        contentValues.put(OwnedKeyValues.EV_SPD, evSpd);
        contentValues.put(OwnedKeyValues.NOTE, notes);
        contentValues.put(OwnedKeyValues.PLAN_ID, planId);
        Uri uri = getContentResolver().insert(TinapaContentProvider.OWNED_POKEMON_URI, contentValues);
        Log.d(TAG, "Added an owned Pokemon with ID of " + uri.getLastPathSegment());
    }

    private void loadImage(String id, ImageView imageView, String column) {
        temp_id = id;
        temp_imageView = imageView;
        temp_column = column;

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.setType("image/*");
        startActivityForResult(intent, RESULT_LOAD_DEX_LIST_ICON);
    }

    @Override
    public void plannedItemClicked(String id) {
        // TODO
    }

    @Override
    public void plannedItemLongClicked(String id) {
        // TODO
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            outState.putInt(SAVE_STATE_SELECTED_TAB_INDEX, actionBar.getSelectedNavigationIndex());
        } else {
            Log.e(TAG, "ActionBar is null for some reason.");
        }
    }

    private static class TabListener<T extends Fragment> implements ActionBar.TabListener {
        private Fragment mFragment;
        private final String mTag;

        public TabListener(String tag, Fragment fragment) {
            this.mTag = tag;
            this.mFragment = fragment;
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            ft.replace(R.id.mainActivityFragment1, mFragment, mTag);
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            // TODO: Probably not needed.
            ft.remove(mFragment);
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            // Do nothing.
        }
    }
}

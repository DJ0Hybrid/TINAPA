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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.tinapaproject.tinapa.database.key.DexKeyValues;
import com.tinapaproject.tinapa.database.key.OwnedKeyValues;
import com.tinapaproject.tinapa.database.key.PlannedKeyValues;
import com.tinapaproject.tinapa.database.key.TeamKeyValues;
import com.tinapaproject.tinapa.database.provider.TinapaContentProvider;
import com.tinapaproject.tinapa.events.CreatePlannedPokemonEvent;
import com.tinapaproject.tinapa.events.DeleteOwnedPokemonEvent;
import com.tinapaproject.tinapa.events.DeletePlannedPokemonEvent;
import com.tinapaproject.tinapa.events.SaveTeamEvent;
import com.tinapaproject.tinapa.events.StartNewTeamEvent;
import com.tinapaproject.tinapa.events.TeamListSelectedEvent;
import com.tinapaproject.tinapa.fragments.DexDetailFragment;
import com.tinapaproject.tinapa.fragments.DexDetailFragment.DexDetailListener;
import com.tinapaproject.tinapa.fragments.DexListFragment;
import com.tinapaproject.tinapa.fragments.DexListFragment.DexListListener;
import com.tinapaproject.tinapa.fragments.OwnedAddDialogFragment;
import com.tinapaproject.tinapa.fragments.OwnedAddDialogFragment.OwnedAddFragmentListener;
import com.tinapaproject.tinapa.fragments.OwnedListFragment;
import com.tinapaproject.tinapa.fragments.OwnedListFragment.OwnedListListener;
import com.tinapaproject.tinapa.fragments.PlannedAddDialogFragment;
import com.tinapaproject.tinapa.fragments.PlannedListFragment;
import com.tinapaproject.tinapa.fragments.PlannedListFragment.PlannedListListener;
import com.tinapaproject.tinapa.fragments.TeamAddDialogFragment;
import com.tinapaproject.tinapa.fragments.TeamListFragment;

public class MainActivity extends Activity implements DexListListener, DexDetailListener, OwnedListListener, OwnedAddFragmentListener, PlannedListListener {

    private String temp_id;
    private ImageView temp_imageView;
    private boolean temp_isDefault;
    private boolean temp_isShiny;
    private boolean temp_isIcon;

    private Bus bus;

    public static int RESULT_LOAD_DEX_LIST_ICON = 100;

    public static final String SAVE_STATE_SELECTED_TAB_INDEX = "SAVE_STATE_SELECTED_TAB_INDEX";

    public static final String DEX_DETAILS_FRAGMENT = "DEX_DETAILS_FRAGMENT";
    public static final String OWNED_DETAILS_FRAGMENT = "OWNED_DETAILS_FRAGMENT";
    public static final String PLANNED_DETAILS_FRAGMENT = "PLANNED_DETAILS_FRAGMENT";
    public static final String TEAM_DETAILS_FRAGMENT = "TEAM_DETAILS_FRAGMENT";

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bus = TinapaApplication.bus;
        bus.register(this);

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

        Tab teamTab = actionBar.newTab()
                .setText(R.string.tab_teams)
                .setTabListener(new TabListener<TeamListFragment>("Team" /*TODO: Needs to be a field. */, new TeamListFragment()));
        actionBar.addTab(teamTab);
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
    protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
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
            Cursor imageQuery = null;
            if (!TextUtils.isEmpty(temp_id)) {
                imageQuery = getContentResolver().query(TinapaContentProvider.POKEDEX_POKEMON_IMAGE_URI, null, "pokemon_id = " + temp_id + " AND is_default = " + (temp_isDefault ? 1 : 0) + " AND is_shinny = " + (temp_isShiny ? 1 : 0) + " AND is_icon = " + (temp_isIcon ? 1 : 0), null, null);

                ContentValues values = new ContentValues();
                values.put(DexKeyValues.imageUri, picturePath);
                values.put(DexKeyValues.isDefault, temp_isDefault ? 1 : 0);
                values.put(DexKeyValues.isShiny, temp_isShiny ? 1 : 0);
                values.put(DexKeyValues.isIcon, temp_isIcon ? 1 : 0);
                if (imageQuery.getCount() > 0) {
                    getContentResolver().update(TinapaContentProvider.POKEDEX_POKEMON_IMAGE_URI, values, DexKeyValues.insertIntoImageColumnWhereCreation(temp_id), null);
                } else {
                    values.put("pokemon_id", temp_id);
                    getContentResolver().insert(TinapaContentProvider.POKEDEX_POKEMON_IMAGE_URI, values);
                }
            }

            temp_id = null;
            temp_imageView = null;
            temp_isDefault = false;
            temp_isShiny = false;
            temp_isIcon = false;
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
        ft.replace(fragmentView.getId(), DexDetailFragment.newInstance(id), DEX_DETAILS_FRAGMENT);
        ft.addToBackStack("DexDetail");
        ft.commit();

    }

    // From DexCursorAdapter
    @Override
    public void onDexImageLongClicked(String id, ImageView imageView, boolean isDefault, boolean isShiny, boolean isIcon) {
        loadImage(id, imageView, isDefault, isShiny, isIcon);
    }

    // From DexDetailFragment
    @Override
    public void onDexDetailImageLongClicked(String id, ImageView imageView, boolean isDefault, boolean isShiny, boolean isIcon) {
        loadImage(id, imageView, isDefault, isShiny, isIcon);
    }

    // From OwnedListFragment
    @Override
    public void onOwnedItemClicked(String id) {
        FrameLayout fragmentView = (FrameLayout) findViewById(R.id.mainActivityFragment2);
        if (fragmentView == null) {
            fragmentView = (FrameLayout) findViewById(R.id.mainActivityFragment1);
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(fragmentView.getId(), OwnedAddDialogFragment.newInstance(id), OWNED_DETAILS_FRAGMENT);
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

    // From OwnedAddDialog
    @Override
    public void onUpdateClicked(String ownedId, int level, String nickname, boolean shinny, String speciesId, String abilityId, String natureId, String genderId, String move1Id, String move2Id, String move3Id, String move4Id, int ivHP, int ivAtt, int ivDef, int ivSAtt, int ivSDef, int ivSpd, int evHP, int evAtt, int evDef, int evSAtt, int evSDef, int evSpd, String notes, String planId) {
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
        getContentResolver().update(TinapaContentProvider.OWNED_POKEMON_URI, contentValues, "owned_pokemons.id == " + ownedId, null);
        Log.d(TAG, "Update an owned Pokemon with ID of " + ownedId);
        onBackPressed();
    }

    @Subscribe
    public void deleteOwnedPokemon(DeleteOwnedPokemonEvent event) {
        getContentResolver().delete(TinapaContentProvider.OWNED_POKEMON_URI, event.getOwnedId(), null);
        Log.d(TAG, "The column for " + event.getOwnedId() + " was deleted.");
        onBackPressed();
    }

    private void loadImage(String id, ImageView imageView, boolean isDefault, boolean isShiny, boolean isIcon) {
        temp_id = id;
        temp_imageView = imageView;
        temp_isDefault = isDefault;
        temp_isShiny = isShiny;
        temp_isIcon = isIcon;

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.setType("image/*");
        startActivityForResult(intent, RESULT_LOAD_DEX_LIST_ICON);
    }

    @Override
    public void plannedItemAddClicked() {
        PlannedAddDialogFragment dialogFragment = PlannedAddDialogFragment.newInstance();
        dialogFragment.show(getFragmentManager(), PlannedAddDialogFragment.TAG);
    }

    @Override
    public void plannedItemClicked(String id) {
        FrameLayout fragmentView = (FrameLayout) findViewById(R.id.mainActivityFragment2);
        if (fragmentView == null) {
            fragmentView = (FrameLayout) findViewById(R.id.mainActivityFragment1);
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(fragmentView.getId(), PlannedAddDialogFragment.newInstance(id), PLANNED_DETAILS_FRAGMENT);
        ft.addToBackStack("PlannedDetail");
        ft.commit();
    }

    @Override
    public void plannedItemLongClicked(String id) {
        // TODO
    }

    @Subscribe
    public void onPlannedPokemonAdded(CreatePlannedPokemonEvent event) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PlannedKeyValues.POKEMON_ID, event.getSpeciesId());
        contentValues.put(PlannedKeyValues.ABILITY_ID, event.getAbilityId());
        contentValues.put(PlannedKeyValues.ITEM_ID, event.getItemId());
        contentValues.put(PlannedKeyValues.NATURE_ID, event.getNatureId());
        contentValues.put(PlannedKeyValues.MOVE1_ID, event.getMove1Id());
        contentValues.put(PlannedKeyValues.MOVE2_ID, event.getMove2Id());
        contentValues.put(PlannedKeyValues.MOVE3_ID, event.getMove3Id());
        contentValues.put(PlannedKeyValues.MOVE4_ID, event.getMove4Id());
        contentValues.put(PlannedKeyValues.IV_HP, event.getIvHP());
        contentValues.put(PlannedKeyValues.IV_ATT, event.getIvAtt());
        contentValues.put(PlannedKeyValues.IV_DEF, event.getIvDef());
        contentValues.put(PlannedKeyValues.IV_SATT, event.getIvSAtt());
        contentValues.put(PlannedKeyValues.IV_SDEF, event.getIvSDef());
        contentValues.put(PlannedKeyValues.IV_SPD, event.getIvSpd());
        contentValues.put(PlannedKeyValues.EV_HP, event.getEvHP());
        contentValues.put(PlannedKeyValues.EV_ATT, event.getEvAtt());
        contentValues.put(PlannedKeyValues.EV_DEF, event.getEvDef());
        contentValues.put(PlannedKeyValues.EV_SATT, event.getEvSAtt());
        contentValues.put(PlannedKeyValues.EV_SDEF, event.getEvSDef());
        contentValues.put(PlannedKeyValues.EV_SPD, event.getEvSpd());
        contentValues.put(PlannedKeyValues.NOTE, event.getNotes());
        if (event.isNewSave()) {
            Uri uri = getContentResolver().insert(TinapaContentProvider.PLANNED_POKEMON_URI, contentValues);
            Log.d(TAG, "Added a planned Pokemon with ID of " + uri.getLastPathSegment());
        } else {
            int columnsChanged = getContentResolver().update(TinapaContentProvider.PLANNED_POKEMON_URI, contentValues, "planned_pokemons.id == " + event.getPlannedId(), null);
            Log.d(TAG, "A total of " + columnsChanged + "columns changed for updating " + event.getPlannedId());
            onBackPressed();
        }

    }

    @Subscribe
    public void deletePlannedPokemon(DeletePlannedPokemonEvent event) {
        getContentResolver().delete(TinapaContentProvider.PLANNED_POKEMON_URI, event.getPlannedId(), null);
        Log.d(TAG, "The column for " + event.getPlannedId() + " was deleted.");
        onBackPressed();
    }

    @Subscribe
    public void openNewAddTeamFragment(StartNewTeamEvent event) {
        TeamAddDialogFragment.newInstance("").show(getFragmentManager(), TeamAddDialogFragment.TAG);
    }

    @Subscribe
    public void saveTeam(SaveTeamEvent event) {
        boolean addingTeam = TextUtils.isEmpty(event.getTeamId());

        ContentValues teamValues = new ContentValues();
        teamValues.put(TeamKeyValues.TEAM_NAME, event.getTeamName());


        ContentValues pokemon1Values = new ContentValues();
        pokemon1Values.put(PlannedKeyValues.POKEMON_ID, event.getPokemon1SpeciesId());
        pokemon1Values.put(PlannedKeyValues.ABILITY_ID, event.getPokemon1AbilityId());
        pokemon1Values.put(PlannedKeyValues.ITEM_ID, event.getPokemon1ItemId());
        pokemon1Values.put(PlannedKeyValues.NATURE_ID, event.getPokemon1NatureId());
        pokemon1Values.put(PlannedKeyValues.MOVE1_ID, event.getPokemon1Move1Id());
        pokemon1Values.put(PlannedKeyValues.MOVE2_ID, event.getPokemon1Move2Id());
        pokemon1Values.put(PlannedKeyValues.MOVE3_ID, event.getPokemon1Move3Id());
        pokemon1Values.put(PlannedKeyValues.MOVE4_ID, event.getPokemon1Move4Id());
        pokemon1Values.put(PlannedKeyValues.IV_HP, event.getPokemon1IvHp());
        pokemon1Values.put(PlannedKeyValues.IV_ATT, event.getPokemon1IvAtt());
        pokemon1Values.put(PlannedKeyValues.IV_DEF, event.getPokemon1IvDef());
        pokemon1Values.put(PlannedKeyValues.IV_SATT, event.getPokemon1IvSAtt());
        pokemon1Values.put(PlannedKeyValues.IV_SDEF, event.getPokemon1IvSDef());
        pokemon1Values.put(PlannedKeyValues.IV_SPD, event.getPokemon1IvSpd());
        pokemon1Values.put(PlannedKeyValues.EV_HP, event.getPokemon1EvHp());
        pokemon1Values.put(PlannedKeyValues.EV_ATT, event.getPokemon1EvAtt());
        pokemon1Values.put(PlannedKeyValues.EV_DEF, event.getPokemon1EvDef());
        pokemon1Values.put(PlannedKeyValues.EV_SATT, event.getPokemon1EvSAtt());
        pokemon1Values.put(PlannedKeyValues.EV_SDEF, event.getPokemon1EvSDef());
        pokemon1Values.put(PlannedKeyValues.EV_SPD, event.getPokemon1EvSpd());
        pokemon1Values.put(PlannedKeyValues.NOTE, event.getPokemon1Notes());
        Uri pokemon1Uri;
        String pokemon1Id = "";
        if (addingTeam) {
            pokemon1Uri = getContentResolver().insert(TinapaContentProvider.PLANNED_POKEMON_URI, pokemon1Values);
            pokemon1Id = pokemon1Uri.getLastPathSegment();
        } else {
            // TODO
        }

        ContentValues pokemon2Values = new ContentValues();
        pokemon2Values.put(PlannedKeyValues.POKEMON_ID, event.getPokemon2SpeciesId());
        pokemon2Values.put(PlannedKeyValues.ABILITY_ID, event.getPokemon2AbilityId());
        pokemon2Values.put(PlannedKeyValues.ITEM_ID, event.getPokemon2ItemId());
        pokemon2Values.put(PlannedKeyValues.NATURE_ID, event.getPokemon2NatureId());
        pokemon2Values.put(PlannedKeyValues.MOVE1_ID, event.getPokemon2Move1Id());
        pokemon2Values.put(PlannedKeyValues.MOVE2_ID, event.getPokemon2Move2Id());
        pokemon2Values.put(PlannedKeyValues.MOVE3_ID, event.getPokemon2Move3Id());
        pokemon2Values.put(PlannedKeyValues.MOVE4_ID, event.getPokemon2Move4Id());
        pokemon2Values.put(PlannedKeyValues.IV_HP, event.getPokemon2IvHp());
        pokemon2Values.put(PlannedKeyValues.IV_ATT, event.getPokemon2IvAtt());
        pokemon2Values.put(PlannedKeyValues.IV_DEF, event.getPokemon2IvDef());
        pokemon2Values.put(PlannedKeyValues.IV_SATT, event.getPokemon2IvSAtt());
        pokemon2Values.put(PlannedKeyValues.IV_SDEF, event.getPokemon2IvSDef());
        pokemon2Values.put(PlannedKeyValues.IV_SPD, event.getPokemon2IvSpd());
        pokemon2Values.put(PlannedKeyValues.EV_HP, event.getPokemon2EvHp());
        pokemon2Values.put(PlannedKeyValues.EV_ATT, event.getPokemon2EvAtt());
        pokemon2Values.put(PlannedKeyValues.EV_DEF, event.getPokemon2EvDef());
        pokemon2Values.put(PlannedKeyValues.EV_SATT, event.getPokemon2EvSAtt());
        pokemon2Values.put(PlannedKeyValues.EV_SDEF, event.getPokemon2EvSDef());
        pokemon2Values.put(PlannedKeyValues.EV_SPD, event.getPokemon2EvSpd());
        pokemon2Values.put(PlannedKeyValues.NOTE, event.getPokemon2Notes());
        Uri pokemon2Uri;
        String pokemon2Id = "";
        if (addingTeam) {
            pokemon2Uri = getContentResolver().insert(TinapaContentProvider.PLANNED_POKEMON_URI, pokemon2Values);
            pokemon2Id = pokemon2Uri.getLastPathSegment();
        } else {
            // TODO
        }

        ContentValues pokemon3Values = new ContentValues();
        pokemon3Values.put(PlannedKeyValues.POKEMON_ID, event.getPokemon3SpeciesId());
        pokemon3Values.put(PlannedKeyValues.ABILITY_ID, event.getPokemon3AbilityId());
        pokemon3Values.put(PlannedKeyValues.ITEM_ID, event.getPokemon3ItemId());
        pokemon3Values.put(PlannedKeyValues.NATURE_ID, event.getPokemon3NatureId());
        pokemon3Values.put(PlannedKeyValues.MOVE1_ID, event.getPokemon3Move1Id());
        pokemon3Values.put(PlannedKeyValues.MOVE2_ID, event.getPokemon3Move2Id());
        pokemon3Values.put(PlannedKeyValues.MOVE3_ID, event.getPokemon3Move3Id());
        pokemon3Values.put(PlannedKeyValues.MOVE4_ID, event.getPokemon3Move4Id());
        pokemon3Values.put(PlannedKeyValues.IV_HP, event.getPokemon3IvHp());
        pokemon3Values.put(PlannedKeyValues.IV_ATT, event.getPokemon3IvAtt());
        pokemon3Values.put(PlannedKeyValues.IV_DEF, event.getPokemon3IvDef());
        pokemon3Values.put(PlannedKeyValues.IV_SATT, event.getPokemon3IvSAtt());
        pokemon3Values.put(PlannedKeyValues.IV_SDEF, event.getPokemon3IvSDef());
        pokemon3Values.put(PlannedKeyValues.IV_SPD, event.getPokemon3IvSpd());
        pokemon3Values.put(PlannedKeyValues.EV_HP, event.getPokemon3EvHp());
        pokemon3Values.put(PlannedKeyValues.EV_ATT, event.getPokemon3EvAtt());
        pokemon3Values.put(PlannedKeyValues.EV_DEF, event.getPokemon3EvDef());
        pokemon3Values.put(PlannedKeyValues.EV_SATT, event.getPokemon3EvSAtt());
        pokemon3Values.put(PlannedKeyValues.EV_SDEF, event.getPokemon3EvSDef());
        pokemon3Values.put(PlannedKeyValues.EV_SPD, event.getPokemon3EvSpd());
        pokemon3Values.put(PlannedKeyValues.NOTE, event.getPokemon3Notes());
        Uri pokemon3Uri;
        String pokemon3Id = "";
        if (addingTeam) {
            pokemon3Uri = getContentResolver().insert(TinapaContentProvider.PLANNED_POKEMON_URI, pokemon3Values);
            pokemon3Id = pokemon3Uri.getLastPathSegment();
        } else {
            // TODO
        }

        ContentValues pokemon4Values = new ContentValues();
        pokemon4Values.put(PlannedKeyValues.POKEMON_ID, event.getPokemon4SpeciesId());
        pokemon4Values.put(PlannedKeyValues.ABILITY_ID, event.getPokemon4AbilityId());
        pokemon4Values.put(PlannedKeyValues.ITEM_ID, event.getPokemon4ItemId());
        pokemon4Values.put(PlannedKeyValues.NATURE_ID, event.getPokemon4NatureId());
        pokemon4Values.put(PlannedKeyValues.MOVE1_ID, event.getPokemon4Move1Id());
        pokemon4Values.put(PlannedKeyValues.MOVE2_ID, event.getPokemon4Move2Id());
        pokemon4Values.put(PlannedKeyValues.MOVE3_ID, event.getPokemon4Move3Id());
        pokemon4Values.put(PlannedKeyValues.MOVE4_ID, event.getPokemon4Move4Id());
        pokemon4Values.put(PlannedKeyValues.IV_HP, event.getPokemon4IvHp());
        pokemon4Values.put(PlannedKeyValues.IV_ATT, event.getPokemon4IvAtt());
        pokemon4Values.put(PlannedKeyValues.IV_DEF, event.getPokemon4IvDef());
        pokemon4Values.put(PlannedKeyValues.IV_SATT, event.getPokemon4IvSAtt());
        pokemon4Values.put(PlannedKeyValues.IV_SDEF, event.getPokemon4IvSDef());
        pokemon4Values.put(PlannedKeyValues.IV_SPD, event.getPokemon4IvSpd());
        pokemon4Values.put(PlannedKeyValues.EV_HP, event.getPokemon4EvHp());
        pokemon4Values.put(PlannedKeyValues.EV_ATT, event.getPokemon4EvAtt());
        pokemon4Values.put(PlannedKeyValues.EV_DEF, event.getPokemon4EvDef());
        pokemon4Values.put(PlannedKeyValues.EV_SATT, event.getPokemon4EvSAtt());
        pokemon4Values.put(PlannedKeyValues.EV_SDEF, event.getPokemon4EvSDef());
        pokemon4Values.put(PlannedKeyValues.EV_SPD, event.getPokemon4EvSpd());
        pokemon4Values.put(PlannedKeyValues.NOTE, event.getPokemon4Notes());
        Uri pokemon4Uri;
        String pokemon4Id = "";
        if (addingTeam) {
            pokemon4Uri = getContentResolver().insert(TinapaContentProvider.PLANNED_POKEMON_URI, pokemon4Values);
            pokemon4Id = pokemon4Uri.getLastPathSegment();
        } else {
            // TODO
        }

        ContentValues pokemon5Values = new ContentValues();
        pokemon5Values.put(PlannedKeyValues.POKEMON_ID, event.getPokemon5SpeciesId());
        pokemon5Values.put(PlannedKeyValues.ABILITY_ID, event.getPokemon5AbilityId());
        pokemon5Values.put(PlannedKeyValues.ITEM_ID, event.getPokemon5ItemId());
        pokemon5Values.put(PlannedKeyValues.NATURE_ID, event.getPokemon5NatureId());
        pokemon5Values.put(PlannedKeyValues.MOVE1_ID, event.getPokemon5Move1Id());
        pokemon5Values.put(PlannedKeyValues.MOVE2_ID, event.getPokemon5Move2Id());
        pokemon5Values.put(PlannedKeyValues.MOVE3_ID, event.getPokemon5Move3Id());
        pokemon5Values.put(PlannedKeyValues.MOVE4_ID, event.getPokemon5Move4Id());
        pokemon5Values.put(PlannedKeyValues.IV_HP, event.getPokemon5IvHp());
        pokemon5Values.put(PlannedKeyValues.IV_ATT, event.getPokemon5IvAtt());
        pokemon5Values.put(PlannedKeyValues.IV_DEF, event.getPokemon5IvDef());
        pokemon5Values.put(PlannedKeyValues.IV_SATT, event.getPokemon5IvSAtt());
        pokemon5Values.put(PlannedKeyValues.IV_SDEF, event.getPokemon5IvSDef());
        pokemon5Values.put(PlannedKeyValues.IV_SPD, event.getPokemon5IvSpd());
        pokemon5Values.put(PlannedKeyValues.EV_HP, event.getPokemon5EvHp());
        pokemon5Values.put(PlannedKeyValues.EV_ATT, event.getPokemon5EvAtt());
        pokemon5Values.put(PlannedKeyValues.EV_DEF, event.getPokemon5EvDef());
        pokemon5Values.put(PlannedKeyValues.EV_SATT, event.getPokemon5EvSAtt());
        pokemon5Values.put(PlannedKeyValues.EV_SDEF, event.getPokemon5EvSDef());
        pokemon5Values.put(PlannedKeyValues.EV_SPD, event.getPokemon5EvSpd());
        pokemon5Values.put(PlannedKeyValues.NOTE, event.getPokemon5Notes());
        Uri pokemon5Uri;
        String pokemon5Id = "";
        if (addingTeam) {
            pokemon5Uri = getContentResolver().insert(TinapaContentProvider.PLANNED_POKEMON_URI, pokemon5Values);
            pokemon5Id = pokemon5Uri.getLastPathSegment();
        } else {
            // TODO
        }

        ContentValues pokemon6Values = new ContentValues();
        pokemon6Values.put(PlannedKeyValues.POKEMON_ID, event.getPokemon6SpeciesId());
        pokemon6Values.put(PlannedKeyValues.ABILITY_ID, event.getPokemon6AbilityId());
        pokemon6Values.put(PlannedKeyValues.ITEM_ID, event.getPokemon6ItemId());
        pokemon6Values.put(PlannedKeyValues.NATURE_ID, event.getPokemon6NatureId());
        pokemon6Values.put(PlannedKeyValues.MOVE1_ID, event.getPokemon6Move1Id());
        pokemon6Values.put(PlannedKeyValues.MOVE2_ID, event.getPokemon6Move2Id());
        pokemon6Values.put(PlannedKeyValues.MOVE3_ID, event.getPokemon6Move3Id());
        pokemon6Values.put(PlannedKeyValues.MOVE4_ID, event.getPokemon6Move4Id());
        pokemon6Values.put(PlannedKeyValues.IV_HP, event.getPokemon6IvHp());
        pokemon6Values.put(PlannedKeyValues.IV_ATT, event.getPokemon6IvAtt());
        pokemon6Values.put(PlannedKeyValues.IV_DEF, event.getPokemon6IvDef());
        pokemon6Values.put(PlannedKeyValues.IV_SATT, event.getPokemon6IvSAtt());
        pokemon6Values.put(PlannedKeyValues.IV_SDEF, event.getPokemon6IvSDef());
        pokemon6Values.put(PlannedKeyValues.IV_SPD, event.getPokemon6IvSpd());
        pokemon6Values.put(PlannedKeyValues.EV_HP, event.getPokemon6EvHp());
        pokemon6Values.put(PlannedKeyValues.EV_ATT, event.getPokemon6EvAtt());
        pokemon6Values.put(PlannedKeyValues.EV_DEF, event.getPokemon6EvDef());
        pokemon6Values.put(PlannedKeyValues.EV_SATT, event.getPokemon6EvSAtt());
        pokemon6Values.put(PlannedKeyValues.EV_SDEF, event.getPokemon6EvSDef());
        pokemon6Values.put(PlannedKeyValues.EV_SPD, event.getPokemon6EvSpd());
        pokemon6Values.put(PlannedKeyValues.NOTE, event.getPokemon6Notes());
        Uri pokemon6Uri;
        String pokemon6Id = "";
        if (addingTeam) {
            pokemon6Uri = getContentResolver().insert(TinapaContentProvider.PLANNED_POKEMON_URI, pokemon6Values);
            pokemon6Id = pokemon6Uri.getLastPathSegment();
        } else {
            // TODO
        }

        teamValues.put(TeamKeyValues.POKEMON1_PLANNED_ID, pokemon1Id);
        teamValues.put(TeamKeyValues.POKEMON2_PLANNED_ID, pokemon2Id);
        teamValues.put(TeamKeyValues.POKEMON3_PLANNED_ID, pokemon3Id);
        teamValues.put(TeamKeyValues.POKEMON4_PLANNED_ID, pokemon4Id);
        teamValues.put(TeamKeyValues.POKEMON5_PLANNED_ID, pokemon5Id);
        teamValues.put(TeamKeyValues.POKEMON6_PLANNED_ID, pokemon6Id);

        if (addingTeam) {
            getContentResolver().insert(TinapaContentProvider.PLANNED_TEAM_URI, teamValues);
        } else {
            // TODO Update
        }
    }

    @Subscribe
    public void teamListSelected(TeamListSelectedEvent event) {
        String teamId = event.getTeamId();

        FrameLayout fragmentView = (FrameLayout) findViewById(R.id.mainActivityFragment2);
        if (fragmentView == null) {
            fragmentView = (FrameLayout) findViewById(R.id.mainActivityFragment1);
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(fragmentView.getId(), TeamAddDialogFragment.newInstance(teamId), TEAM_DETAILS_FRAGMENT);
        ft.addToBackStack("TeamDetails");
        ft.commit();
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

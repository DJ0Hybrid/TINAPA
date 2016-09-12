package com.tinapaproject.tinapa.utils;

import android.app.Activity;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.tinapaproject.tinapa.R;
import com.tinapaproject.tinapa.database.key.DexKeyValues;
import com.tinapaproject.tinapa.database.key.ItemKeyValues;
import com.tinapaproject.tinapa.database.key.NatureKeyValues;
import com.tinapaproject.tinapa.database.provider.TinapaContentProvider;

public class PlannedPokemonUtils {
    public static void setupSpeciesSpinners(final Activity activity, Spinner speciesSpinner, int speciesId) {
        Cursor speciesCursor = activity.getContentResolver().query(TinapaContentProvider.POKEDEX_ALL_SHORT_URI, null, null, null, null);
        String[] from = {DexKeyValues.name};
        int[] to = {R.id.simple_cell_name};
        CursorAdapter speciesCursorAdapter = new SimpleCursorAdapter(activity, R.layout.cell_simple_name, speciesCursor, from, to, 0);
        speciesSpinner.setAdapter(speciesCursorAdapter);

        if (speciesId > 0) {
            speciesSpinner.setSelection(speciesId -1, false);
        }
    }

    public static void setupItemSpinner(final Activity activity, Spinner itemSpinner, int itemId) {
        Cursor itemCursor = activity.getContentResolver().query(TinapaContentProvider.ITEM_BATTLE_URI, null, null, null, null);
        String[] itemFrom = {ItemKeyValues.name};
        int[] itemTo = {R.id.simple_cell_name};
        CursorAdapter itemCursorAdapter = new SimpleCursorAdapter(activity, R.layout.cell_simple_name, itemCursor, itemFrom, itemTo, 0);
        itemSpinner.setAdapter(itemCursorAdapter);

        if (itemId > 0) {
            itemSpinner.setSelection(CursorUtils.getPositionOfRowById(itemId, itemSpinner), false);
        }
    }

    public static void setupNatureSpinner(final Activity activity, Spinner natureSpinner, int natureId) {
        Cursor natureCursor = activity.getContentResolver().query(TinapaContentProvider.NATURE_URI, null, null, null, null);
        String[] natureFrom = {NatureKeyValues.NATURE_NAME};
        int[] natureTo = {R.id.simple_cell_name};
        CursorAdapter natureCursorAdapter = new SimpleCursorAdapter(activity, R.layout.cell_simple_name, natureCursor, natureFrom, natureTo, 0) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(activity).inflate(R.layout.cell_simple_name, parent, false);
                }
                this.getCursor().moveToPosition(position);
                String natureName = this.getCursor().getString(this.getCursor().getColumnIndex(NatureKeyValues.NATURE_NAME));
                String increasedStatName = this.getCursor().getString(this.getCursor().getColumnIndex(NatureKeyValues.INCREASED_STAT_NAME));
                String decreasedStatName = this.getCursor().getString(this.getCursor().getColumnIndex(NatureKeyValues.DECREASED_STAT_NAME));

                StringBuilder builder = new StringBuilder(natureName);

                if (!TextUtils.isEmpty(increasedStatName) && !TextUtils.isEmpty(decreasedStatName) && !increasedStatName.equalsIgnoreCase(decreasedStatName)) {
                    builder.append(" / + " + increasedStatName);
                    builder.append(" / - " + decreasedStatName);
                } else {
                    builder.append(" / = / =");
                }

                TextView textView = (TextView) convertView.findViewById(R.id.simple_cell_name);
                textView.setText(builder.toString());

                return convertView;
            }
        };
        natureSpinner.setAdapter(natureCursorAdapter);

        if (natureId > 0) {
            natureSpinner.setSelection(natureId -1, false);
        }
    }

    public static void setupAbilityForPokemon(final Activity activity, Spinner abilitySpinner, int pokemonId, int abilityId) {
        Cursor abilitiesCursor = activity.getContentResolver().query(TinapaContentProvider.POKEDEX_POKEMON_ABILITIES_URI, null, "pokemon_id = " + pokemonId, null, null);

        String[] from = {"name"};
        int[] to = {R.id.simple_cell_name};
        CursorAdapter abilitiesCursorAdapter = new SimpleCursorAdapter(activity, R.layout.cell_simple_name, abilitiesCursor, from, to, 0);
        abilitySpinner.setAdapter(abilitiesCursorAdapter);
        if (abilityId >= 0) {
            int abilityPosition = CursorUtils.getPositionOfRowById(abilityId, abilitySpinner);
            if (abilityPosition >= 0) {
                abilitySpinner.setSelection(abilityPosition);
            }
        }
    }

    public static void setupAllMovesForPokemon(final Activity activity, Spinner move1Spinner, Spinner move2Spinner, Spinner move3Spinner, Spinner move4Spinner, int pokemonId, int move1Id, int move2Id, int move3Id, int move4Id) {
        setupMoveForPokemon(activity, move1Spinner, pokemonId, move1Id);
        setupMoveForPokemon(activity, move2Spinner, pokemonId, move2Id);
        setupMoveForPokemon(activity, move3Spinner, pokemonId, move3Id);
        setupMoveForPokemon(activity, move4Spinner, pokemonId, move4Id);
    }

    public static void setupMoveForPokemon(final Activity activity, Spinner moveSpinner, int pokemonId, int moveId) {
        Cursor movesCursor = activity.getContentResolver().query(TinapaContentProvider.POKEDEX_POKEMON_MOVES_URI, null, String.valueOf(pokemonId), null, null);

        String[] from = {"name"};
        int[] to = {R.id.simple_cell_name};
        CursorAdapter spinnerAdapter = new SimpleCursorAdapter(moveSpinner.getContext(), R.layout.cell_simple_name, movesCursor, from, to, 0);
        moveSpinner.setAdapter(spinnerAdapter);
        if (moveId >= 0) {
            int selectionPosition = CursorUtils.getPositionOfRowById(moveId, moveSpinner);
            if (selectionPosition >= 0) {
                moveSpinner.setSelection(selectionPosition);
            }
        }
    }
}

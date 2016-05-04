package com.tinapaproject.tinapa.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tinapaproject.tinapa.R;
import com.tinapaproject.tinapa.database.key.DexKeyValues;
import com.tinapaproject.tinapa.database.key.EvolutionKeyValues;
import com.tinapaproject.tinapa.database.provider.TinapaContentProvider;
import com.tinapaproject.tinapa.utils.ImageUtils;

import java.util.HashMap;


public class DexDetailFragment extends Fragment {
    private DexDetailListener listener;

    private static final String ARG_POKEMON_ID = "ARG_POKEMON_ID";

    public static final String TAG = "DexDetailFragment";

    public static DexDetailFragment newInstance(String id) {
        DexDetailFragment fragment = new DexDetailFragment();
        Bundle arg = new Bundle();
        arg.putString(ARG_POKEMON_ID, id);
        fragment.setArguments(arg);
        return fragment;
    }

    public DexDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof DexDetailListener) {
            listener = (DexDetailListener) activity;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dex_detail, container, false);

        final String id = getArguments().getString(ARG_POKEMON_ID);
        Cursor pokemonCursor = getActivity().getContentResolver().query(TinapaContentProvider.POKEDEX_URI, null, "pokemon.id = " + id, null, null);
        if (pokemonCursor != null && pokemonCursor.moveToFirst()) {
            // Images
            ImageView normalImageView = (ImageView) view.findViewById(R.id.dexDetailNormalImage);
            String normalImagePath = pokemonCursor.getString(pokemonCursor.getColumnIndex(DexKeyValues.image));
            ImageUtils.loadImage(normalImageView, normalImagePath, true);
            normalImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    if (listener != null) {
                        listener.onDexDetailImageLongClicked(id, (ImageView) v, true, false, false);
                        return true;
                    }
                    return false;
                }
            });

            ImageView shinnyImageView = (ImageView) view.findViewById(R.id.dexDetailShinnyImage);
            String shinnyImagePath = pokemonCursor.getString(pokemonCursor.getColumnIndex(DexKeyValues.shinnyImage));
            ImageUtils.loadImage(shinnyImageView, shinnyImagePath, true);
            shinnyImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    if (listener != null) {
                        listener.onDexDetailImageLongClicked(id, (ImageView) v, false, true, false);
                        return true;
                    }
                    return false;
                }
            });

            // Basic Info
            TextView numberView = (TextView) view.findViewById(R.id.dexDetailNumber);
            numberView.setText(pokemonCursor.getString(pokemonCursor.getColumnIndex(DexKeyValues.number)));

            TextView nameView = (TextView) view.findViewById(R.id.dexDetailName);
            nameView.setText(pokemonCursor.getString(pokemonCursor.getColumnIndex(DexKeyValues.name)));

            TextView type1View = (TextView) view.findViewById(R.id.dexDetailType1);
            type1View.setText(pokemonCursor.getString(pokemonCursor.getColumnIndex(DexKeyValues.type1)));

            TextView type2View = (TextView) view.findViewById(R.id.dexDetailType2);
            type2View.setText(pokemonCursor.getString(pokemonCursor.getColumnIndex(DexKeyValues.type2)));

            // Abilities
            TextView ability1NameView = (TextView) view.findViewById(R.id.dexDetailAbilityName1);
            ability1NameView.setText(pokemonCursor.getString(pokemonCursor.getColumnIndex(DexKeyValues.ability1Name)));
            // TODO
            TextView ability1DescView = (TextView) view.findViewById(R.id.dexDetailAbilityDescription1);

            String ability1ID = pokemonCursor.getString(pokemonCursor.getColumnIndex(DexKeyValues.ability1ID));
            String ability2ID = pokemonCursor.getString(pokemonCursor.getColumnIndex(DexKeyValues.ability2ID));
            if (!TextUtils.isEmpty(ability2ID) && !ability1ID.equalsIgnoreCase(ability2ID)) {
                TextView ability2NameView = (TextView) view.findViewById(R.id.dexDetailAbilityName2);
                ability2NameView.setText(pokemonCursor.getString(pokemonCursor.getColumnIndex(DexKeyValues.ability2Name)));
                // TODO
                TextView ability2DescView = (TextView) view.findViewById(R.id.dexDetailAbilityDescription2);
            } else {
                View ability2TableRow = view.findViewById(R.id.dexDetailAbility2TableRow);
                ability2TableRow.setVisibility(View.GONE);
            }

            TextView ability3NameView = (TextView) view.findViewById(R.id.dexDetailAbilityName3);
            ability3NameView.setText(pokemonCursor.getString(pokemonCursor.getColumnIndex(DexKeyValues.ability3Name)));
            // TODO
            TextView ability3DescView = (TextView) view.findViewById(R.id.dexDetailAbilityDescription3);

            // Stats
            TextView hpView = (TextView) view.findViewById(R.id.dex_detail_base_hp);
            TextView attView = (TextView) view.findViewById(R.id.dex_detail_base_att);
            TextView defView = (TextView) view.findViewById(R.id.dex_detail_base_def);
            TextView sattView = (TextView) view.findViewById(R.id.dex_detail_base_satt);
            TextView sdefView = (TextView) view.findViewById(R.id.dex_detail_base_sdef);
            TextView spdView = (TextView) view.findViewById(R.id.dex_detail_base_spd);

            hpView.setText(pokemonCursor.getString(pokemonCursor.getColumnIndex(DexKeyValues.baseHP)));
            attView.setText(pokemonCursor.getString(pokemonCursor.getColumnIndex(DexKeyValues.baseAttack)));
            defView.setText(pokemonCursor.getString(pokemonCursor.getColumnIndex(DexKeyValues.baseDefense)));
            sattView.setText(pokemonCursor.getString(pokemonCursor.getColumnIndex(DexKeyValues.baseSpecialAttack)));
            sdefView.setText(pokemonCursor.getString(pokemonCursor.getColumnIndex(DexKeyValues.baseSpecialDefense)));
            spdView.setText(pokemonCursor.getString(pokemonCursor.getColumnIndex(DexKeyValues.baseSpeed)));

            // Level-up Moves
            Cursor levelUpMoves = getActivity().getContentResolver().query(TinapaContentProvider.POKEDEX_POKEMON_MOVES_URI, null, "pokemon_id = " + id + " AND pokemon_move_methods.id = 1", null, null);
            if (levelUpMoves != null && levelUpMoves.moveToFirst() && levelUpMoves.getCount() > 0) {
                final ViewGroup levelUpList = (ViewGroup) view.findViewById(R.id.dex_detail_moves_level_up_list);

                loadLevelUpMovesIntoTableLayout(levelUpMoves, levelUpList);

                View levelUpToggle = view.findViewById(R.id.dex_detail_moves_level_up_switch);
                levelUpToggle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (View.VISIBLE == levelUpList.getVisibility()) {
                            levelUpList.setVisibility(View.GONE);
                        } else if (View.GONE == levelUpList.getVisibility()) {
                            levelUpList.setVisibility(View.VISIBLE);
                        }
                    }
                });
            } else {
                view.findViewById(R.id.dex_detail_moves_level_up_body).setVisibility(View.GONE);
            }

            // Machine Moves
            Cursor machineMoves = getActivity().getContentResolver().query(TinapaContentProvider.POKEDEX_POKEMON_MOVES_URI, null, "pokemon_id = " + id + " AND pokemon_move_methods.id = 4", null, null);
            if (machineMoves != null && machineMoves.moveToFirst() && machineMoves.getCount() > 0) {
                final ViewGroup machineList = (ViewGroup) view.findViewById(R.id.dex_detail_moves_machine_list);

                loadMachineMovesIntoTableLayout(machineMoves, machineList);

                View machineToggle = view.findViewById(R.id.dex_detail_moves_machine_switch);
                machineToggle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (View.VISIBLE == machineList.getVisibility()) {
                            machineList.setVisibility(View.GONE);
                        } else if (View.GONE == machineList.getVisibility()) {
                            machineList.setVisibility(View.VISIBLE);
                        }
                    }
                });
            } else {
                view.findViewById(R.id.dex_detail_moves_machine_body).setVisibility(View.GONE);
            }

            // Egg Moves
            Cursor eggMoves = getActivity().getContentResolver().query(TinapaContentProvider.POKEDEX_POKEMON_MOVES_URI, null, "pokemon_id = " + id + " AND pokemon_move_methods.id = 2", null, null);
            if (eggMoves != null && eggMoves.moveToFirst() && eggMoves.getCount() > 0) {
                final ViewGroup eggList = (ViewGroup) view.findViewById(R.id.dex_detail_moves_egg_list);

                loadEggMovesIntoTableLayout(eggMoves, eggList);

                View machineToggle = view.findViewById(R.id.dex_detail_moves_egg_switch);
                machineToggle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (View.VISIBLE == eggList.getVisibility()) {
                            eggList.setVisibility(View.GONE);
                        } else if (View.GONE == eggList.getVisibility()) {
                            eggList.setVisibility(View.VISIBLE);
                        }
                    }
                });
            } else {
                view.findViewById(R.id.dex_detail_moves_egg_body).setVisibility(View.GONE);
            }

            // TODO: Tutor Moves

            // Evolution Chain
            ViewGroup evolutionView = (ViewGroup) view.findViewById(R.id.dex_detail_evolution_chain);
            Cursor evolutionChainCursor = getActivity().getContentResolver().query(TinapaContentProvider.POKEDEX_POKEMON_EVOLUTION_URI, null, id, null, null);
            if (evolutionChainCursor != null && evolutionChainCursor.moveToFirst() && evolutionChainCursor.getCount() > 0) {
                HashMap<Integer, ViewGroup> pokemonViewGroup = new HashMap<Integer, ViewGroup>(evolutionChainCursor.getColumnCount());
                do {
                    View baseView = inflater.inflate(R.layout.evolution_chain, null, false);
                    ImageView baseImageView = (ImageView) baseView.findViewById(R.id.evolution_chain_image);
                    String imagePath = evolutionChainCursor.getString(evolutionChainCursor.getColumnIndex(EvolutionKeyValues.POKEMON_IMAGE_URI));
                    ImageUtils.loadImage(baseImageView, imagePath, true);

                    ViewGroup nextEvolutionView = (ViewGroup) baseView.findViewById(R.id.evolution_chain_next_stage);

                    if (evolutionChainCursor.isNull(evolutionChainCursor.getColumnIndex(EvolutionKeyValues.EVOLVES_FROM_SPECIES_ID))) {

                        View evolutionMethod = baseView.findViewById(R.id.evolution_chain_method);
                        evolutionMethod.setVisibility(View.GONE);
                        evolutionView.addView(baseView);
                    } else {
                        // TODO Show how evolution happens
                        ViewGroup evolutionMethodView = (ViewGroup) baseView.findViewById(R.id.evolution_chain_method);
                        // In the future, this can be changed to something besides text.
                        StringBuilder evolutionMethodText = new StringBuilder();
                        evolutionMethodText.append(evolutionChainCursor.getString(evolutionChainCursor.getColumnIndex(EvolutionKeyValues.EVOLUTION_METHOD_PROSE)));
                        // Level
                        if (!evolutionChainCursor.isNull(evolutionChainCursor.getColumnIndex(EvolutionKeyValues.EVOLUTION_MIN_LEVEL))) {
                            evolutionMethodText.append("\n").append(getString(R.string.evolution_level)).append(evolutionChainCursor.getString(evolutionChainCursor.getColumnIndex(EvolutionKeyValues.EVOLUTION_MIN_LEVEL)));
                        }
                        // Use Item
                        if (!evolutionChainCursor.isNull(evolutionChainCursor.getColumnIndex(EvolutionKeyValues.EVOLUTION_USE_ITEM_NAME))) {
                            evolutionMethodText.append('\n').append(evolutionChainCursor.getString(evolutionChainCursor.getColumnIndex(EvolutionKeyValues.EVOLUTION_USE_ITEM_NAME)));
                        }
                        // Time of Day
                        if (!evolutionChainCursor.isNull(evolutionChainCursor.getColumnIndex(EvolutionKeyValues.EVOLUTION_TIME_OF_DAY))) {
                            evolutionMethodText.append('\n').append(getString(R.string.evolution_time)).append(' ').append(evolutionChainCursor.getString(evolutionChainCursor.getColumnIndex(EvolutionKeyValues.EVOLUTION_TIME_OF_DAY)));
                        }
                        // Happiness
                        if (!evolutionChainCursor.isNull(evolutionChainCursor.getColumnIndex(EvolutionKeyValues.EVOLUTION_MINIMUM_HAPPINESS))) {
                            evolutionMethodText.append('\n').append(getString(R.string.evolution_happiness)).append(evolutionChainCursor.getString(evolutionChainCursor.getColumnIndex(EvolutionKeyValues.EVOLUTION_MINIMUM_HAPPINESS)));
                        }
                        // Location
                        if (!evolutionChainCursor.isNull(evolutionChainCursor.getColumnIndex(EvolutionKeyValues.EVOLUTION_LOCATION_NAME))) {
                            evolutionMethodText.append('\n').append(getString(R.string.evolution_location)).append(' ').append(evolutionChainCursor.getString(evolutionChainCursor.getColumnIndex(EvolutionKeyValues.EVOLUTION_LOCATION_NAME)));
                        }
                        // Minimum Affection
                        if (!evolutionChainCursor.isNull(evolutionChainCursor.getColumnIndex(EvolutionKeyValues.EVOLUTION_MINIMUM_AFFECTION))) {
                            evolutionMethodText.append('\n').append(getString(R.string.evolution_affection)).append(' ').append(evolutionChainCursor.getString(evolutionChainCursor.getColumnIndex(EvolutionKeyValues.EVOLUTION_MINIMUM_AFFECTION)));
                        }
                        // Known Move Type
                        if (!evolutionChainCursor.isNull(evolutionChainCursor.getColumnIndex(EvolutionKeyValues.EVOLUTION_KNOWN_MOVE_TYPE))) {
                            evolutionMethodText.append('\n').append(getString(R.string.evolution_move_type)).append(' ').append(evolutionChainCursor.getString(evolutionChainCursor.getColumnIndex(EvolutionKeyValues.EVOLUTION_KNOWN_MOVE_TYPE)));
                        }
                        // Held Item
                        if (!evolutionChainCursor.isNull(evolutionChainCursor.getColumnIndex(EvolutionKeyValues.EVOLUTION_HELD_ITEM_NAME))) {
                            evolutionMethodText.append('\n').append(getString(R.string.evolution_held_item)).append(' ').append(evolutionChainCursor.getString(evolutionChainCursor.getColumnIndex(EvolutionKeyValues.EVOLUTION_HELD_ITEM_NAME)));
                        }
                        // TODO Gender

                        // Known Specific Move
                        if (!evolutionChainCursor.isNull(evolutionChainCursor.getColumnIndex(EvolutionKeyValues.EVOLUTION_KNOWN_MOVE))) {
                            evolutionMethodText.append('\n').append(getString(R.string.evolution_move)).append(' ').append(evolutionChainCursor.getString(evolutionChainCursor.getColumnIndex(EvolutionKeyValues.EVOLUTION_KNOWN_MOVE)));
                        }
                        // TODO Beauty Requirement
                        if (!evolutionChainCursor.isNull(evolutionChainCursor.getColumnIndex(EvolutionKeyValues.EVOLUTION_MINIMUM_BEAUTY))) {
                            evolutionMethodText.append('\n').append(getString(R.string.evolution_beauty)).append(' ').append(evolutionChainCursor.getString(evolutionChainCursor.getColumnIndex(EvolutionKeyValues.EVOLUTION_MINIMUM_BEAUTY)));
                        }
                        // TODO Upside Down Device

                        // TODO All the other methods

                        TextView evolutionTextView = new TextView(getActivity());
                        evolutionTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        evolutionTextView.setText(evolutionMethodText.toString());
                        evolutionMethodView.addView(evolutionTextView);


                        ViewGroup parentViewGroup = pokemonViewGroup.get(evolutionChainCursor.getInt(evolutionChainCursor.getColumnIndex(EvolutionKeyValues.EVOLVES_FROM_SPECIES_ID)));
                        if (parentViewGroup != null) {
                            parentViewGroup.addView(baseView);
                        }
                    }

                    pokemonViewGroup.put(evolutionChainCursor.getInt(evolutionChainCursor.getColumnIndex(EvolutionKeyValues.SPECIES_ID)), nextEvolutionView);
                } while (evolutionChainCursor.moveToNext());
                evolutionChainCursor.close();
            } else {
                evolutionView.setVisibility(View.GONE);
            }

        }

        return view;
    }

    private static void loadLevelUpMovesIntoTableLayout(Cursor movesCursor, ViewGroup table) {
        while (!movesCursor.isAfterLast()) {
            View moveView = LayoutInflater.from(table.getContext()).inflate(R.layout.cell_move, table, false);
            String name = movesCursor.getString(movesCursor.getColumnIndex("name"));
            TextView nameView = (TextView) moveView.findViewById(R.id.cell_move_name);
            nameView.setText(name);

            String flavorText = movesCursor.getString(movesCursor.getColumnIndex("flavor_text"));
            TextView flavorTextView = (TextView) moveView.findViewById(R.id.cell_move_flavor_text);
            flavorTextView.setText(flavorText);

            // TODO: Can still provide more information on the moves.

            table.addView(moveView);
            movesCursor.moveToNext();
        }
    }

    private static void loadMachineMovesIntoTableLayout(Cursor movesCursor, ViewGroup table) {
        while (!movesCursor.isAfterLast()) {
            View moveView = LayoutInflater.from(table.getContext()).inflate(R.layout.cell_move, table, false);
            String name = movesCursor.getString(movesCursor.getColumnIndex("name"));
            TextView nameView = (TextView) moveView.findViewById(R.id.cell_move_name);
            nameView.setText(name);

            String flavorText = movesCursor.getString(movesCursor.getColumnIndex("flavor_text"));
            TextView flavorTextView = (TextView) moveView.findViewById(R.id.cell_move_flavor_text);
            flavorTextView.setText(flavorText);

            // TODO: Can still provide more information on the moves.

            table.addView(moveView);
            movesCursor.moveToNext();
        }
    }

    private static void loadEggMovesIntoTableLayout(Cursor movesCursor, ViewGroup table) {
        while (!movesCursor.isAfterLast()) {
            View moveView = LayoutInflater.from(table.getContext()).inflate(R.layout.cell_move, table, false);
            String name = movesCursor.getString(movesCursor.getColumnIndex("name"));
            TextView nameView = (TextView) moveView.findViewById(R.id.cell_move_name);
            nameView.setText(name);

            String flavorText = movesCursor.getString(movesCursor.getColumnIndex("flavor_text"));
            TextView flavorTextView = (TextView) moveView.findViewById(R.id.cell_move_flavor_text);
            flavorTextView.setText(flavorText);

            // TODO: Can still provide more information on the moves.

            table.addView(moveView);
            movesCursor.moveToNext();
        }
    }

    public interface DexDetailListener {
        public void onDexDetailImageLongClicked(String id, ImageView imageView, boolean isDefault, boolean isShiny, boolean isIcon);
    }
}

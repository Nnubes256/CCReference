package es.nnub.ccreference.descriptions;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.VideoView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

import es.nnub.ccreference.CustomArrayAdapter;
import es.nnub.ccreference.R;
import es.nnub.ccreference.services.FloatingWidgetService;

public class EnemyDescription extends BaseDescriptionAdapter {

    private NamePathPair[] soundArray;

    protected class NamePathPair {

        public final String name;
        public final int path;

        protected NamePathPair(String soundName, int soundPath) {
            name = soundName;
            path = soundPath;
        }
    }

    private Drawable image;
    private int summary;
    private int hp;
    private int attack;
    private int defense;
    private int focus;
    private int description;
    private int video;

    private String resName;
    private TextSwitcher hpView;
    private TextSwitcher attackView;
    private TextSwitcher defenseView;
    private TextSwitcher focusView;
    private SharedPreferences settings;
    //private MediaPlayer medPlayer;
    private int currVideoSeekMilis = 0;
    private int name;
    private boolean hasShowedControllerAtLeastOneTime = true;
    private MediaController videoController;
    private ArrayMap<String, MediaPlayer> soundPlayers;

    public EnemyDescription(@NonNull Context context) {
        super(context);
    }

    public ViewSwitcher.ViewFactory getViewSwitcherFactory(@NonNull final Context context) {
        return new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView () {
                LayoutInflater li = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                TextView convertView = (TextView) li.inflate(R.layout.stat_corner_right_textview, null);
                return convertView;
            }
        };
    }

    @Override
    public void loadResourcesFromName(String resourceName) {
        Context context = getContext();
        Resources res = context.getResources();

        settings = context.getApplicationContext().getSharedPreferences("es.nnub.ccreference.options.enemies", Context.MODE_PRIVATE);
        String boosterString = settings.getBoolean("show_booster_stats", false) ? "_booster" : "";

        String packageName = context.getPackageName();
        name = res.getIdentifier(resourceName + "_name", "string", packageName);
        image = res.getDrawable(
                res.getIdentifier(resourceName + "_icon", "drawable", packageName),
                null
        );
        image.setFilterBitmap(false);
        summary = res.getIdentifier(resourceName + "_summary", "string", packageName);
        hp = res.getIdentifier(resourceName + boosterString + "_hp", "string", packageName);
        attack = res.getIdentifier(resourceName + boosterString + "_attack", "string", packageName);
        defense = res.getIdentifier(resourceName + boosterString + "_defense", "string", packageName);
        focus = res.getIdentifier(resourceName + boosterString + "_focus", "string", packageName);
        description = res.getIdentifier(resourceName + "_description", "string", packageName);
        video = res.getIdentifier(resourceName + "_video", "raw", packageName);
        String[] soundNames = res.getStringArray(res.getIdentifier(resourceName + "_soundnames", "array", packageName));
        String[] soundPaths = res.getStringArray(res.getIdentifier(resourceName + "_soundpaths", "array", packageName));

        if(soundNames.length != soundPaths.length) {
            throw new RuntimeException("assertion failed: " + soundNames.length + " != " + soundPaths.length);
        }

        ArrayList<NamePathPair> sounds = new ArrayList<>();
        for (int i = 0; i < soundNames.length; i++) {
            int soundResId = res.getIdentifier(resourceName + "_sound_" + soundPaths[i], "raw", packageName);
            if (soundResId == 0) {
                throw new RuntimeException("SOUND NOT FOUND: " + resourceName + "_sound_" + soundPaths[i]);
            }
            NamePathPair sound = new NamePathPair(
                    soundNames[i],
                    soundResId
            );
            sounds.add(sound);
        }

        soundArray = new NamePathPair[sounds.size()];
        sounds.toArray(soundArray);

        resName = resourceName;
    }

    @Override
    public View initializeView(@NonNull final Context context, @NonNull ViewGroup container, final FloatingWidgetService.FloatingWidgetCallbacks floatingWidgetCallbacks) {
        LayoutInflater li = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View convertView = li.inflate(R.layout.enemy_description, container, true);

        ImageView imageView = convertView.findViewById(R.id.image1);
        TextView summaryView = convertView.findViewById(R.id.text_summary);
        hpView = convertView.findViewById(R.id.hp_text_switcher);
        attackView = convertView.findViewById(R.id.attack_text_switcher);
        defenseView = convertView.findViewById(R.id.defense_text_switcher);
        focusView = convertView.findViewById(R.id.focus_text_switcher);
        TextView descriptionView = convertView.findViewById(R.id.text_description);
        Button searchButton = convertView.findViewById(R.id.searchButton);
        final ToggleButton videoButton = convertView.findViewById(R.id.videoButton);
        final ToggleButton soundsButton = convertView.findViewById(R.id.soundsButton);
        final View videoContainer = convertView.findViewById(R.id.videoContainer);
        final VideoView videoView = convertView.findViewById(R.id.videoView);
        final LinearLayout soundsContainer = convertView.findViewById(R.id.soundContainer);

        hpView.setFactory(getViewSwitcherFactory(context));
        attackView.setFactory(getViewSwitcherFactory(context));
        defenseView.setFactory(getViewSwitcherFactory(context));
        focusView.setFactory(getViewSwitcherFactory(context));

        final boolean isOverlay = (floatingWidgetCallbacks != null);

        if (isOverlay) {
            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) imageView.getLayoutParams();
            lp.width *= 0.75;
            imageView.setLayoutParams(lp);
        }
        imageView.setImageDrawable(image);
        summaryView.setText(summary);
        hpView.setText(getContext().getResources().getString(hp));
        attackView.setText(getContext().getResources().getString(attack));
        defenseView.setText(getContext().getResources().getString(defense));
        focusView.setText(getContext().getResources().getString(focus));
        descriptionView.setText(description);

        soundPlayers = new ArrayMap<>();

        CustomArrayAdapter<NamePathPair> soundListAdapter = new CustomArrayAdapter<NamePathPair>(getContext(), R.layout.sound_single_item, soundArray) {
            @NonNull
            @Override
            public View getFilledLayoutForData(NamePathPair item, @NonNull View convertView, int position) {
                TextView tv = convertView.findViewById(R.id.text1);
                ImageButton playButton = convertView.findViewById(R.id.soundButton);
                final ProgressBar soundProgressBar = convertView.findViewById(R.id.soundSeekBar);
                try {
                    tv.setText(item.name);
                    final MediaPlayer soundPlayer = MediaPlayer.create(context, item.path);
                    soundPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            soundPlayer.seekTo(0);
                        }
                    });
                    soundPlayers.put(item.name, soundPlayer);
                    soundProgressBar.setProgress(0);
                    final Runnable setSeekBarProgress = new Runnable() {
                        int currentSeek = 0;
                        @Override
                        public void run() {
                            int progress = Math.round(
                                    ((float) soundPlayer.getCurrentPosition() / (float) soundPlayer.getDuration()) * soundProgressBar.getMax()
                            );
                            if (currentSeek != progress) {
                                currentSeek = progress;
                                soundProgressBar.setProgress(currentSeek);
                            }
                            soundProgressBar.postOnAnimation(this);
                        }
                    };
                    soundProgressBar.postOnAnimation(setSeekBarProgress);
                    playButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (soundPlayer.isPlaying()) {
                                soundPlayer.pause();
                                soundPlayer.seekTo(0);
                            } else {
                                soundPlayer.start();
                            }
                        }
                    });
                } catch (NullPointerException e) {
                    tv.setText(R.string.customarrayadapter_error);
                }

                return convertView;
            }
        };

        final int soundListAdapterItemCount = soundListAdapter.getCount();

        for (int i = 0; i < soundListAdapterItemCount; i++) {
            View item = soundListAdapter.getView(i, null, soundsContainer);
            soundsContainer.addView(item);
        }

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                if (videoController != null)
                    videoController.hide();
            }
        });


        searchButton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String query = "crosscode " + context.getString(name);
                    if (isOverlay) {
                        floatingWidgetCallbacks.openSearchLink(query);
                    } else {
                        Intent i = new Intent(Intent.ACTION_WEB_SEARCH);
                        i.putExtra(SearchManager.QUERY, query);
                        if (i.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivity(i);
                        }
                    }
                }
        });

        videoButton.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                videoContainer.setVisibility(isChecked ? View.VISIBLE : View.GONE);

                if (isChecked) {
                    if (soundsButton.isChecked()) {
                        soundsButton.setChecked(false);
                    }
                    videoView.setVisibility(View.VISIBLE);
                    if (videoController == null) {
                        if (isOverlay) {
                            // TODO doesn't work at all. But eh, at least it doesn't crash!
                            videoController = floatingWidgetCallbacks.onMediaControllerRequested();
                        } else {
                            videoController = new MediaController(context);
                        }
                    } else {
                        videoController.setVisibility(View.GONE);
                    }
                    videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mp, int what, int extra) {
                            System.out.println("Error: " + what + " " + extra);
                            return false;
                        }
                    });
                    videoView.setMediaController(videoController);
                    videoView.setVideoURI(Uri.parse("android.resource://" + context.getPackageName() + "/" + video));
                    videoView.requestFocus();
                    videoView.seekTo(currVideoSeekMilis);
                    videoView.start();
                    if (!isOverlay) {
                        videoController.hide();
                    } else {
                        videoController.show();
                    }
                } else {
                    currVideoSeekMilis = videoView.getCurrentPosition();
                    videoView.seekTo(0);
                    videoView.stopPlayback();
                    videoView.setVisibility(View.GONE);
                    hasShowedControllerAtLeastOneTime = false;
                }
            }
        });

        soundsButton.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                soundsContainer.setVisibility(isChecked ? View.VISIBLE : View.GONE);

                if (isChecked) {
                    if (videoButton.isChecked()) {
                        videoButton.setChecked(false);
                    }
                }
            }
        });

        videoView.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoController != null && !videoController.isShowing() && !hasShowedControllerAtLeastOneTime) {
                    videoController.setVisibility(View.VISIBLE);
                    videoController.show();
                    hasShowedControllerAtLeastOneTime = true;
                }
            }
        });

        return convertView;
    }

    @Override
    public boolean addExtras(@NonNull final AppCompatActivity activity, Menu menu) {
        MenuInflater inflater = activity.getMenuInflater();
        inflater.inflate(R.menu.enemy_menu, menu);

        boolean isBoosterStatsOptionChecked = settings.getBoolean("show_booster_stats", false);
        MenuItem boosterItem = menu.findItem(R.id.showBoosterStats);
        boosterItem.setChecked(isBoosterStatsOptionChecked);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Context context = getContext();
        Resources res = context.getResources();

        switch (item.getItemId()) {
            case R.id.showBoosterStats:
                boolean isChecked = !item.isChecked();

                if (isChecked) {
                    hp = res.getIdentifier(resName + "_booster_hp", "string", context.getPackageName());
                    attack = res.getIdentifier(resName + "_booster_attack", "string", context.getPackageName());
                    defense = res.getIdentifier(resName + "_booster_defense", "string", context.getPackageName());
                    focus = res.getIdentifier(resName + "_booster_focus", "string", context.getPackageName());
                } else {
                    hp = res.getIdentifier(resName + "_hp", "string", context.getPackageName());
                    attack = res.getIdentifier(resName + "_attack", "string", context.getPackageName());
                    defense = res.getIdentifier(resName + "_defense", "string", context.getPackageName());
                    focus = res.getIdentifier(resName + "_focus", "string", context.getPackageName());
                }

                item.setChecked(isChecked);
                SharedPreferences.Editor prefEditor = settings.edit();
                prefEditor.putBoolean("show_booster_stats", isChecked);
                prefEditor.apply();

                hpView.setText(getContext().getResources().getString(hp));
                attackView.setText(getContext().getResources().getString(attack));
                defenseView.setText(getContext().getResources().getString(defense));
                focusView.setText(getContext().getResources().getString(focus));

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.ltst.schoolapp.teacher.ui.activities.add.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreEnterFragment;
import com.ltst.core.data.model.ChildActivity;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.ui.DialogProvider;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.activities.add.AddPostActivityScope.AddPostActivityComponent;

import javax.inject.Inject;

import butterknife.BindDimen;
import butterknife.BindView;

public class AddPostFragment extends CoreEnterFragment implements AddPostContract.View {

    public static final int SPAN_COUNT = 2;

    private int selectedFoodType = 0;

    @Inject
    AddPostPresenter presenter;
    @Inject
    DialogProvider dialogProvider;

    @BindView(R.id.add_post_progress_bar) ProgressBar progressBar;
    @BindView(R.id.add_post_select_person) View selectPersonContainer;
    @BindView(R.id.select_person_text) TextView selectPersonText;
    @BindView(R.id.add_post_current_activity_image) ImageView currentActivityImage;
    @BindView(R.id.add_post_current_activity_text) TextView currentActivityText;
    @BindView(R.id.add_post_food_type_spinner) Spinner foodTypeSpinner;
    @BindView(R.id.add_post_text) EditText content;
    @BindView(R.id.add_post_child_activity_header) TextView activitiesHeader;
    @BindView(R.id.add_post_recycler) RecyclerView childActivitiesRecycler;
    @BindView(R.id.add_post_photo_container) ViewGroup photos;
    @BindView(R.id.add_post_camera) View camera;


    private Toolbar toolbar;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected int getBackgroundColorId() {
        return android.R.color.white;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_add_post;
    }

    @Override
    protected void onCreateComponent(HasSubComponents rootComponent) {
        AddPostActivityComponent component = (AddPostActivityComponent) rootComponent.getComponent();
        component.addPostComponent(new AddPostScope.AddPostModule(this, this)).inject(this);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        RecyclerView.LayoutManager manager = new GridLayoutManager(getContext(), SPAN_COUNT,
                LinearLayoutManager.HORIZONTAL, false);
        childActivitiesRecycler.setLayoutManager(manager);
        return view;
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        this.toolbar.setVisibility(View.VISIBLE);
        this.toolbar.setTitle(R.string.add_post_title);
        this.toolbar.inflateMenu(R.menu.menu_done);
    }

    @Override
    public void initToolbar(int icon, View.OnClickListener onClickListener,
                            Toolbar.OnMenuItemClickListener onMenuItemClickListener) {
        toolbar.setNavigationIcon(icon);
        toolbar.setNavigationOnClickListener(onClickListener);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
    }

    @Override
    public void bindData(View.OnClickListener onSelectPersonListener,
                         RecyclerView.Adapter adapter,
                         View.OnClickListener onAddPhotoClick) {
        selectPersonContainer.setOnClickListener(onSelectPersonListener);
        childActivitiesRecycler.setAdapter(adapter);
        camera.setOnClickListener(onAddPhotoClick);
        ArrayAdapter<CharSequence> foodTypeAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.add_post_food_types, android.R.layout.simple_spinner_item);
        foodTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        foodTypeSpinner.setAdapter(foodTypeAdapter);
        foodTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedFoodType = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedFoodType = 0;
            }
        });
    }

    @Override
    public void setPersons(String text, @ColorRes int color) {
        if (text.equals("null")) {
            text = null;
        }
        selectPersonText.setText(!StringUtils.isBlank(text) ? text
                : getString(R.string.select_person_group_default));
        selectPersonText.setTextColor(ContextCompat.getColor(getContext(), color));
    }

    @Override
    public void setCurrentChildActivity(ChildActivity currentChildActivity) {
        ((View) currentActivityImage.getParent()).setVisibility(View.VISIBLE);
        Glide.with(getContext())
                .load(currentChildActivity.getIconUrl())
                .thumbnail(0.5f)
                .into(currentActivityImage);
        currentActivityText.setText(StringUtils.capitalize(currentChildActivity.getTitle()));
        if (currentActivityText.getText().equals("Food")) {
            foodTypeSpinner.setVisibility(View.VISIBLE);
        } else {
            foodTypeSpinner.setVisibility(View.GONE);
        }
        content.requestFocus();
        /*show keyboard*/
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.showSoftInput(content, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void chosePhotoWay(DialogProvider.PhotoWayCallBack photoWayCallBack) {
        dialogProvider.showPhotoWay(photoWayCallBack);
    }

    @Override
    public String getContent() {
        String foodType = getResources().getStringArray(R.array.add_post_food_types)[selectedFoodType];
        if (currentActivityText.getText().equals("Food")) {
            Toast.makeText(getContext(), foodType, Toast.LENGTH_SHORT).show();
            return "(" + foodType + ") " + content.getText().toString();
        } else {
            return content.getText().toString();
        }
    }

    @BindDimen(R.dimen.activity_horizontal_margin_16) int marginForFirstPhoto;
    @BindDimen(R.dimen.add_post_photo_size) int photoSize;

    @Override
    public void addPhoto(Uri photoPath, View.OnClickListener onClickListener) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View newPhoto = inflater.inflate(R.layout.fragment_add_post_photo, photos, false);
        ImageView imageView = (ImageView) newPhoto;
        if (photos.getChildCount() == 1) {
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(photoSize, photoSize);
            params.setMargins(marginForFirstPhoto, 0, 0, 0);
            imageView.setLayoutParams(params);
        }
        Glide.with(getContext())
                .load(photoPath)
                .centerCrop()
                .into(imageView);
        imageView.setOnClickListener(onClickListener);
        photos.addView(newPhoto);
    }

    @Override
    public void removePhoto(View view) {
        photos.removeView(view);
    }

    @Override
    public void setPhotoButtonEnabled(boolean enabled) {
        camera.setEnabled(enabled);
        camera.setClickable(enabled);
    }

    @Override
    public void showActivityError() {
        activitiesHeader.setTextColor(ContextCompat.getColor(getContext(), R.color.error_color));
    }

    @Override
    public void showLoading(boolean isShow) {
        progressBar.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }


    public static class Screen extends FragmentScreen {

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        protected Fragment createFragment() {
            return new AddPostFragment();
        }
    }
}

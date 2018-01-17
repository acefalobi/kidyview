package com.ltst.core.base;

import android.Manifest;
import android.animation.LayoutTransition;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.layer.atlas.util.Log;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.R;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.permission.PermissionsHandler;
import com.ltst.core.ui.DialogProvider;

import java.security.InvalidParameterException;

import butterknife.ButterKnife;

public abstract class CoreFragment extends Fragment implements PermissionsHandler {

    private boolean afterClearStack = false;
    private boolean isFirstStart = true;
    private static final String STATE_KEY = "CoreFragment.state";
    private Bundle state = new Bundle();
    private PermissionsHandler.Callback permissionCallback;

    public void setAfterClearStack(boolean afterClearStack) {
        this.afterClearStack = afterClearStack;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            HasSubComponents hasSubComponents = (HasSubComponents) getActivity();
            onCreateComponent(hasSubComponents);
        } catch (Exception e) {
            throw new InvalidParameterException(getActivity().getClass().getSimpleName() +
                    " must implements HasSubComponents interface or Error:" + e.getMessage());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_KEY)) {
            state = savedInstanceState.getBundle(STATE_KEY);
        }
    }

    @Override
    public void requestPermission(int requestCode, final String permission, Callback callback) {
        this.permissionCallback = callback;
        String explanationText = null;
        switch (permission) {
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                explanationText = getString(R.string.dialog_storage_permission_warning);
                break;
            case Manifest.permission.CAMERA:
                explanationText = getString(R.string.dialog_camera_permissions_warning);
                break;
            default:
                explanationText = getString(R.string.dialog_default_permission_warning);
                break;
        }
        if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
                DialogProvider dialogProvider = ((CoreActivity) getActivity()).getDialogProvider();
                dialogProvider.permissionRationale(getActivity(),
                        explanationText,
                        (dialog, which) -> {
                            dialog.dismiss();
                            requestPermissions(new String[]{permission}, requestCode);
                        }, (dialog, which) -> dialog.dismiss());
            } else {
                requestPermissions(new String[]{permission}, requestCode);
            }
        } else {
            permissionCallback.resultOk();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionCallback != null) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionCallback.resultOk();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (state != null) {
            getPresenter().onRestore(state);
            state.clear();
        }
        initToolbar(((CoreActivity) getActivity()).getToolbar());
        if (isFirstStart) {
            getPresenter().firstStart();
            isFirstStart = false;
        }
        getPresenter().start();
    }

    @Override
    public void onStop() {
        super.onStop();
        permissionCallback = null;
        getPresenter().onSave(state);
        getPresenter().stop();
        Toolbar toolbar = ((CoreActivity) getActivity()).getToolbar();
        if (toolbar != null) {
            toolbar.setTitle(StringUtils.EMPTY);
            toolbar.getMenu().clear();
            toolbar.setNavigationIcon(null);
            toolbar.setVisibility(View.GONE);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(getResLayoutId(), container, false);
        ButterKnife.bind(this, fragmentView);
        fragmentView.setBackgroundColor(ContextCompat.getColor(getContext(), getBackgroundColorId()));
        fragmentView.setClickable(true);
        ((ViewGroup) fragmentView).setLayoutTransition(new LayoutTransition());
        return fragmentView;
    }

    protected void hideKeyboard() {
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getView();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_KEY, state);
    }

    protected abstract BasePresenter getPresenter();

    protected int getBackgroundColorId() {
        return android.R.color.white;
    }

    ;

    protected abstract int getResLayoutId();

    protected abstract void onCreateComponent(HasSubComponents rootComponent);

    protected abstract void initToolbar(Toolbar toolbar);

}

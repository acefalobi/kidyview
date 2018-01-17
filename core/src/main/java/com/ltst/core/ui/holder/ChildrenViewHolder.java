package com.ltst.core.ui.holder;

import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.danil.recyclerbindableadapter.library.view.BindableViewHolder;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.R;
import com.ltst.core.data.model.Child;
import com.ltst.core.ui.AvatarView;
import com.ltst.core.ui.adapter.ChildrenAdapter;

public class ChildrenViewHolder extends BindableViewHolder<ChildrenAdapter.ChildWrapper,
        ChildrenViewHolder.ChildrenClickListener> {

    private AvatarView avatarView;
    private TextView firstNameField;
    private TextView lastNameField;
    private TextView ageField;
    private ImageView menu;
    private ViewGroup root;
    //    private TextView missed;
    private boolean teacherApp;
    private ChildrenClickListener childrenClickListener;

    public ChildrenViewHolder(View itemView) {
        super(itemView);
//        ButterKnife.bind(this, itemView);
        avatarView = ((AvatarView) itemView.findViewById(R.id.child_view_holder_avatar));
        firstNameField = ((TextView) itemView.findViewById(R.id.child_view_holder_name));
        lastNameField = ((TextView) itemView.findViewById(R.id.child_view_holder_last_name));
        ageField = ((TextView) itemView.findViewById(R.id.child_view_holder_age));
        menu = ((ImageView) itemView.findViewById(R.id.child_view_holder_menu));
        root = ((ViewGroup) itemView.findViewById(R.id.child_view_holder_root));

    }

    @Override
    public void bindView(int position, ChildrenAdapter.ChildWrapper item, ChildrenClickListener actionListener) {
        this.childrenClickListener = actionListener;
        root.setOnClickListener(view -> {
            childrenClickListener.onItemClickListener(position, item.getChild());
        });
        Child child = item.getChild();
        String itemFooter = item.getAgeAndGender();
        String avatarUrl = child.getAvatarUrl();
        if (!StringUtils.isBlank(avatarUrl)) {
            avatarView.setAvatar(avatarUrl);
            avatarView.setClickAvatarCallBack(() -> {
                actionListener.onPhotoClickListener(child.getAvatarUrl());
            });
        } else avatarView.clearAvatar();

        firstNameField.setText(child.getFirstName());
        lastNameField.setText(child.getLastName());
        if (!StringUtils.isBlank(itemFooter)) {
            ageField.setText(itemFooter);
            ageField.setVisibility(View.VISIBLE);
        } else {
            ageField.setVisibility(View.GONE);
        }
        menu.setOnClickListener(v -> {
            showMenu(position, item);
        });
        menu.setVisibility(teacherApp ? View.VISIBLE : View.GONE);
//        if (!teacherApp) {
//            if (StringUtils.isBlank(child.getBloodGroup())
//                    || StringUtils.isBlank(child.getGenotype())
//                    || StringUtils.isBlank(child.getAllergies())) {
//                missed.setVisibility(View.VISIBLE);
//            } else missed.setVisibility(View.GONE);
//        } //// TODO: 12.01.17 (alexeenkoff)

    }

    private void showMenu(int position, ChildrenAdapter.ChildWrapper childWrapper) {
        PopupMenu popupMenu = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            popupMenu = new PopupMenu(root.getContext(), menu, Gravity.LEFT);
        } else {
            popupMenu = new PopupMenu(root.getContext(), menu);
        }
        popupMenu.inflate(R.menu.menu_item_child);
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_edit) {
                childrenClickListener.onEditChildListener(position, childWrapper.getChild());
            } else if (item.getItemId() == R.id.action_delete) {
                childrenClickListener.onDeleteChildListener(position, childWrapper.getChild());
            }
            return false;
        });
        popupMenu.show();
    }

    public void setTeacherApp(boolean teacherApp) {
        this.teacherApp = teacherApp;
    }

    public interface ChildrenClickListener extends BindableViewHolder.ActionListener<ChildrenAdapter.ChildWrapper> {

        void onItemClickListener(int position, Child item);

        void onEditChildListener(int position, Child item);

        void onDeleteChildListener(int position, Child item);

        void onPhotoClickListener(String avatarUrl);

    }
}

package com.ltst.schoolapp.teacher.ui.main.chats;


import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.layer.atlas.AtlasConversationsRecyclerView;
import com.layer.atlas.adapters.AtlasConversationsAdapter;
import com.layer.atlas.util.views.SwipeableItem;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreFragment;
import com.ltst.core.layer.LayerModule;
import com.ltst.core.navigation.BottomNavigationFragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.ui.DialogProvider;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.main.BottomScreen;
import com.ltst.schoolapp.teacher.ui.main.MainScope;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;

public class ChatsFragment extends CoreFragment implements ChatsContract.View {

    @Inject ChatsPresenter presenter;
    @Inject DialogProvider dialogProvider;

    @BindView(R.id.conversations_list) AtlasConversationsRecyclerView chatList;
    @BindView(R.id.chats_progress_bar) ProgressBar progressBar;

    @Override protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override protected int getResLayoutId() {
        return R.layout.fragment_chats;
    }

    @Override protected void onCreateComponent(HasSubComponents rootComponent) {
        MainScope.MainComponent component = ((MainScope.MainComponent) rootComponent.getComponent());
        component.chatsComponent(new ChatsScope.ChatsModule(this)).inject(this);
    }

    @Override protected void initToolbar(Toolbar toolbar) {
//        Spinner spinner = (Spinner) toolbar.findViewById(R.id.feed_toolbar_spinner);
//        spinner.setVisibility(View.GONE);
        ImageView groupIcon = ((ImageView) toolbar.findViewById(R.id.main_toolbar_icon));
        groupIcon.setVisibility(View.GONE);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.inflateMenu(R.menu.menu_plus);
        toolbar.setOnMenuItemClickListener(item -> {
            presenter.openAddDialogMemberScreen();
            return false;
        });
        toolbar.setTitle(getString(R.string.chats_title));
    }

    @Override public void bindView(LayerClient layerClient) {
        chatList.init(layerClient, Picasso.with(getContext()))
                .setInitialHistoricMessagesToFetch(20)
                .setOnConversationClickListener(new AtlasConversationsAdapter.OnConversationClickListener() {
                    @Override public void onConversationClick(AtlasConversationsAdapter adapter, Conversation conversation) {
                        presenter.openSingleConversation(conversation);
                    }

                    @Override public boolean onConversationLongClick(AtlasConversationsAdapter adapter, Conversation conversation) {
                        return false;
                    }
                }).setOnConversationSwipeListener(new SwipeableItem.OnSwipeListener<Conversation>() {
            @Override public void onSwipe(Conversation conversation, int direction) {
                dialogProvider.layerSwipePopup(getContext(),
                        (dialog, which) -> {
                            conversation.delete(LayerClient.DeletionMode.ALL_MY_DEVICES);
                        }, (dialog, which) -> {
                            chatList.getAdapter().notifyDataSetChanged();
                            dialog.dismiss();
                        });
            }
        });
    }

    @Override public void startLoad() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override public void stopLoad() {
        progressBar.setVisibility(View.GONE);
    }

    public static final class Screen extends BottomNavigationFragmentScreen {

        @Override public String getName() {
            return BottomScreen.CHATS.toString();
        }

        @Override protected Fragment createFragment() {
            return new ChatsFragment();
        }

        @Override public int unselectedIconId() {
            return R.drawable.ic_chats;
        }

        @Override public int selectedIconId() {
            return R.drawable.ic_chats_active;
        }
    }
}

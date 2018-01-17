package com.ltst.schoolapp.parent.ui.conversation.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.layer.atlas.AtlasAddressBar;
import com.layer.atlas.AtlasHistoricMessagesFetchLayout;
import com.layer.atlas.AtlasMessageComposer;
import com.layer.atlas.AtlasMessagesRecyclerView;
import com.layer.atlas.AtlasTypingIndicator;
import com.layer.atlas.messagetypes.generic.GenericCellFactory;
import com.layer.atlas.messagetypes.location.LocationCellFactory;
import com.layer.atlas.messagetypes.singlepartimage.SinglePartImageCellFactory;
import com.layer.atlas.messagetypes.text.TextCellFactory;
import com.layer.atlas.messagetypes.text.TextSender;
import com.layer.atlas.messagetypes.threepartimage.CameraSender;
import com.layer.atlas.messagetypes.threepartimage.GallerySender;
import com.layer.atlas.messagetypes.threepartimage.ThreePartImageCellFactory;
import com.layer.atlas.typingindicators.BubbleTypingIndicatorFactory;
import com.layer.atlas.util.LayerUtils;
import com.layer.atlas.util.Log;
import com.layer.atlas.util.picasso.requesthandlers.MessagePartRequestHandler;
import com.layer.sdk.LayerClient;
import com.layer.sdk.exceptions.LayerConversationException;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.ConversationOptions;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreFragment;
import com.ltst.core.layer.atlas.FixedCameraSender;
import com.ltst.core.layer.atlas.GalleryCloudSender;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ui.conversation.ConversationScope;
import com.squareup.picasso.Picasso;

import java.util.HashSet;

import javax.inject.Inject;

import butterknife.BindView;

public class ConversationFragment extends CoreFragment implements ConversationContract.View {
    @Inject ConversationPresenter presenter;

    @BindView(R.id.conversation_launcher) AtlasAddressBar addressBar;
    @BindView(R.id.historic_sync_layout) AtlasHistoricMessagesFetchLayout fetchLayout;
    @BindView(R.id.messages_list) AtlasMessagesRecyclerView messagesList;
    @BindView(R.id.message_composer) AtlasMessageComposer messageComposer;

    private FixedCameraSender gallerySender;

    private AtlasTypingIndicator typingIndicator;
    private Toolbar toolbar;

    private UiState uiState;
    private Conversation conversation;
    private String screenTitle;

    @Override protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override protected int getResLayoutId() {
        return R.layout.fragment_conversation;
    }

    @Override protected void onCreateComponent(HasSubComponents rootComponent) {
        ConversationScope.ConversationComponent component =
                (ConversationScope.ConversationComponent) rootComponent.getComponent();
        component.chatComponent(new ChatScope.ChatModule(this)).inject(this);
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override protected void initToolbar(Toolbar toolbar) {
        toolbar.setVisibility(View.VISIBLE);
        this.toolbar = toolbar;
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> presenter.goBack());
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        messageComposer.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        messageComposer.onActivityResult(getActivity(), requestCode, resultCode, data);
    }


    private void setUiState(UiState state) {
        if (uiState == state) return;
        uiState = state;
        switch (state) {
            case ADDRESS:
                addressBar.setVisibility(View.VISIBLE);
                addressBar.setSuggestionsVisibility(View.VISIBLE);
                fetchLayout.setVisibility(View.GONE);
                messageComposer.setVisibility(View.GONE);
                break;

            case ADDRESS_COMPOSER:
                addressBar.setVisibility(View.VISIBLE);
                addressBar.setSuggestionsVisibility(View.VISIBLE);
                fetchLayout.setVisibility(View.GONE);
                messageComposer.setVisibility(View.VISIBLE);
                break;

            case ADDRESS_CONVERSATION_COMPOSER:
                addressBar.setVisibility(View.VISIBLE);
                addressBar.setSuggestionsVisibility(View.GONE);
                fetchLayout.setVisibility(View.VISIBLE);
                messageComposer.setVisibility(View.VISIBLE);
                break;

            case CONVERSATION_COMPOSER:
                addressBar.setVisibility(View.GONE);
                addressBar.setSuggestionsVisibility(View.GONE);
                fetchLayout.setVisibility(View.VISIBLE);
                messageComposer.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override public void onStart() {
        super.onStart();
        if (gallerySender!=null && !gallerySender.photoPathIsEmpty()) {
            gallerySender.sendMessage(getActivity());
        }
    }

    public void initView(LayerClient layerClient, String[] participantIds, String screenTitle) {
        this.screenTitle = screenTitle;
        Picasso picasso = new Picasso.Builder(getContext())
                .addRequestHandler(new MessagePartRequestHandler(layerClient)).build();
        addressBar.init(layerClient, picasso)
                .setOnConversationClickListener((conversationLauncher, conversation) -> {
                    setConversation(conversation, true);
                    setTitle(layerClient, true);
                })
                .setOnParticipantSelectionChangeListener((conversationLauncher, participants) -> {
                    if (participants.isEmpty()) {
                        setConversation(null, false);
                        return;
                    }
                    try {
                        setConversation(layerClient.newConversation(new ConversationOptions().distinct(true), new HashSet<>(participants)), false);
                    } catch (LayerConversationException e) {
                        setConversation(e.getConversation(), false);
                    }
                })
                .addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        if (uiState == UiState.ADDRESS_CONVERSATION_COMPOSER) {
                            addressBar.setSuggestionsVisibility(s.toString().isEmpty() ? View.GONE : View.VISIBLE);
                        }
                    }

                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override public void afterTextChanged(Editable s) {

                    }
                })
                .setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        setUiState(UiState.CONVERSATION_COMPOSER);
                        setTitle(layerClient, true);
                        return true;
                    }
                    return false;
                });
        fetchLayout.init(layerClient).setHistoricMessagesPerFetch(20);
        messagesList.init(layerClient, picasso)
                .addCellFactories(new TextCellFactory(),
                        new TextCellFactory(),
                        new ThreePartImageCellFactory(getActivity(), layerClient, picasso),
                        new LocationCellFactory(getActivity(), picasso),
                        new SinglePartImageCellFactory(getActivity(), layerClient, picasso),
                        new GenericCellFactory());
//                .setOnMessageSwipeListener() // TODO: 31.01.17 (alexeenkoff)
        typingIndicator = new AtlasTypingIndicator(getContext())
                .init(layerClient)
                .setTypingIndicatorFactory(new BubbleTypingIndicatorFactory())
                .setTypingActivityListener((typingIndicator1, active) -> {
                    messagesList.setFooterView(active ? typingIndicator1 : null);
                });

        Conversation conversation = null;
        try {
            conversation = layerClient.newConversationWithUserIds(new ConversationOptions().distinct(true), participantIds);
        } catch (LayerConversationException e) {
            conversation = e.getConversation();
        }
        setConversation(conversation, conversation != null);
        setTitle(layerClient, true);


    }

    @Override public void initMessageComposer(LayerClient layerClient) {
        String fileProviderAuthority = getActivity().getApplicationContext().getPackageName() + ".file_provider";
        GalleryCloudSender galleryCloudSender = new GalleryCloudSender(R.string.attachment_menu_gallery, R.drawable.ic_attachment_photo, getActivity());
        gallerySender = new FixedCameraSender(R.string.attachment_menu_camera, R.drawable.ic_attacment_camera, getActivity(), fileProviderAuthority);
        messageComposer.init(layerClient)
                .setTextSender(new TextSender())
                .addAttachmentSenders(gallerySender)
                .addAttachmentSenders(galleryCloudSender)
                .setOnMessageEditTextFocusChangeListener((v, hasFocus) -> {
                    if (hasFocus) {
                        setUiState(UiState.CONVERSATION_COMPOSER);
                        setTitle(layerClient, true);
                    }
                });
    }

    @Override public void onResume() {
        super.onResume();
    }

    private void setConversation(Conversation conversation, boolean hideLauncher) {
        this.conversation = conversation;
        fetchLayout.setConversation(conversation);
        messagesList.setConversation(conversation);
        typingIndicator.setConversation(conversation);
        messageComposer.setConversation(conversation);
        if (conversation != null) {
            presenter.clearNotification(conversation);
        }

        // UI state
        if (conversation == null) {
            setUiState(UiState.ADDRESS);
            return;
        }

        if (hideLauncher) {
            setUiState(UiState.CONVERSATION_COMPOSER);
            return;
        }

        if (conversation.getHistoricSyncStatus() == Conversation.HistoricSyncStatus.INVALID) {
            // New "temporary" conversation
            setUiState(UiState.ADDRESS_COMPOSER);
        } else {
            setUiState(UiState.ADDRESS_CONVERSATION_COMPOSER);
        }
    }

    public void setTitle(LayerClient layerClient, boolean useConversation) {
        if (!useConversation) {
            toolbar.setTitle(R.string.title_select_conversation);
        } else {
            if (!StringUtils.isBlank(screenTitle)) {
                toolbar.setTitle(screenTitle);
            } else {
                String conversationTitle = LayerUtils.getConversationTitle(layerClient, this.conversation);
                toolbar.setTitle(conversationTitle);
            }
        }
    }


    private enum UiState {
        ADDRESS,
        ADDRESS_COMPOSER,
        ADDRESS_CONVERSATION_COMPOSER,
        CONVERSATION_COMPOSER
    }

    public static final class Screen extends FragmentScreen {

        @Override public String getName() {
            return getClass().getName();
        }

        @Override protected Fragment createFragment() {
            return new ConversationFragment();
        }
    }
}

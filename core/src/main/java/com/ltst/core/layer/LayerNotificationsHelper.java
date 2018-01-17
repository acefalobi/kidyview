package com.ltst.core.layer;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.layer.atlas.util.LayerUtils;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Identity;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;
import com.layer.sdk.messaging.PushNotificationPayload;
import com.layer.sdk.query.Queryable;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.R;
import com.ltst.core.data.GetUserByLayerIdentityService;
import com.ltst.core.data.response.LayerProfileResponse;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LayerNotificationsHelper {

    public final static String ACTION_PUSH = "com.layer.sdk.PUSH";

    public final static String LAYER_CONVERSATION_KEY = "layer-conversation-id";
    public final static String LAYER_MESSAGE_KEY = "layer-message-id";
    private final static int MAX_MESSAGES = 5;
    public final static int MESSAGE_ID = 1;
    public static final String FROM_NOTIFICATION_KEY = "LayerNoytificationHelper.FromNotification";

    private LayerClient layerClient;
    private Context context;
    private NotificationManager notificationManager;
    private Class singleConversationClass;
    private GetUserByLayerIdentityService layerService;
    private final CircleTransform avatarCircleTransform = new CircleTransform();

    private static final @IdRes int SMALL_ICON_RES_ID = R.drawable.ic_notification_small;
    private int largeIconResId;

    public LayerNotificationsHelper(Context context, LayerClient layerClient, GetUserByLayerIdentityService layerService) {
        this.context = context;
        this.layerClient = layerClient;
        notificationManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
        this.layerService = layerService;
    }

    public void setLargeIconResId(int largeIconResId) {
        this.largeIconResId = largeIconResId;
    }

    public void setSingleConversationActivity(Class conversationActivity) {
        this.singleConversationClass = conversationActivity;
    }

    public void newAction(Intent intent) {
        Bundle extras = intent.getExtras();
        final PushNotificationPayload payload = PushNotificationPayload.fromLayerPushExtras(extras);
        final Uri conversationId = extras.getParcelable(LAYER_CONVERSATION_KEY);
        final Uri messageId = extras.getParcelable(LAYER_MESSAGE_KEY);

        if (intent.getAction().equals(ACTION_PUSH)) {
            layerClient.waitForContent(messageId, new LayerClient.ContentAvailableCallback() {
                @Override public void onContentAvailable(LayerClient layerClient, @NonNull Queryable queryable) {
                    prepareNotification(((Message) queryable), payload.getText());
                }

                @Override public void onContentFailed(LayerClient layerClient, Uri uri, Exception e) {

                }
            });
        }
    }

    private void prepareNotification(Message message, String messageText) {
        Conversation conversation = message.getConversation();
        Set<Identity> participants = conversation.getParticipants();
        Identity authenticatedUser = layerClient.getAuthenticatedUser();

        String avatarUrl = null;
        if (participants.size() == 2) {
            avatarUrl = getTetATetNotificationAvatar(authenticatedUser, participants);
        }

        for (MessagePart messagePart : message.getMessageParts()) {
            if (messagePart.getMimeType().contains(StringUtils.IMAGE)) {
                messageText = context.getString(R.string.notifications_layer_image);
            }
        }
        String title = LayerUtils.getNullableConversationTitle(layerClient, conversation);
        if (!StringUtils.isBlank(title)) {
            loadAvatarAndNotify(messageText, conversation, avatarUrl, title);
        } else {
            List<String> identities = getParticipantsWithNoAuthenticatedUser(authenticatedUser, participants);
            String finalAvatarUrl = avatarUrl;
            String finalMessageText = messageText;
            Observable.from(identities)
                    .flatMap(layerIdentity -> layerService.getLayerProfile(layerIdentity))
                    .toList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(responses -> {
                        String restTitle = prepareTitleFromRest(responses);
                        loadAvatarAndNotify(finalMessageText, conversation, finalAvatarUrl, restTitle);
                    }, Throwable::printStackTrace);
        }

    }

    @Nullable
    private String getTetATetNotificationAvatar(Identity authenticatedUser, Set<Identity> participants) {
        for (Iterator<Identity> iterator = participants.iterator(); iterator.hasNext(); ) {
            Identity identity = iterator.next();
            if (!authenticatedUser.getUserId().equals(identity.getUserId())) {
                return identity.getAvatarImageUrl();
            }
        }
        return null;
    }

    private List<String> getParticipantsWithNoAuthenticatedUser(Identity authenticatedUser,
                                                                Set<Identity> participants) {
        List<String> result = new ArrayList<>(participants.size() - 1);
        for (Identity participant : participants) {
            if (participant.getId().equals(authenticatedUser.getId())) continue;
            result.add(participant.getUserId());
        }
        return result;

    }


    private String prepareTitleFromRest(List<LayerProfileResponse> responses) {
        StringBuilder result = new StringBuilder();
        Iterator<LayerProfileResponse> iterator = responses.iterator();
        while (iterator.hasNext()) {
            LayerProfileResponse next = iterator.next();
            if (responses.size() == 1) {
                result.append(next.displayName);
            } else {
                String firstName = next.firstName;
                String lastName = next.lastName;
                result.append(firstName.substring(0, 1).toUpperCase());
                result.append(lastName.substring(0, 1).toUpperCase());
                if (iterator.hasNext()) {
                    result.append(StringUtils.COMMA)
                            .append(StringUtils.SPACE);
                }
            }
        }
        return result.toString();
    }

    private void loadAvatarAndNotify(String messageText, Conversation conversation, String avatarUrl, String title) {
        String conversationId = conversation.getId().toString();
        if (!StringUtils.isBlank(avatarUrl)) {
            loadLargeIconBeforeNotify(conversationId, title, messageText, avatarUrl);
        } else {
            NotificationCompat.Builder builder = prepareBuilder(title, messageText);
            Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), largeIconResId);
            builder.setLargeIcon(largeIcon);
            notify(conversationId, builder, title);
        }
    }

    private void loadLargeIconBeforeNotify(String conversationId, String title, String messageText, String avatarUrl) {
        NotificationCompat.Builder builder = prepareBuilder(title, messageText);
        try {
            Bitmap bitmap = Picasso.with(context).load(avatarUrl).transform(avatarCircleTransform).get();
            builder.setLargeIcon(bitmap);
            notify(conversationId, builder, title);
        } catch (IOException e) {
            notify(conversationId, builder, title);
        }
    }


    private NotificationCompat.Builder prepareBuilder(String title, String messageText) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(SMALL_ICON_RES_ID)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        if (messageText != null) {
            int colonPosition = messageText.indexOf(StringUtils.COLON);
            messageText = messageText.substring(colonPosition + 1).trim();
            builder.setContentText(messageText);
        }
        return builder;
    }

    private void notify(String conversationId, NotificationCompat.Builder builder, String title) {
        builder.setContentIntent(configurePendingIntent(conversationId, title));
        notificationManager.cancel(conversationId, MESSAGE_ID);
        notificationManager.notify(conversationId, MESSAGE_ID, builder.build());
    }

    public void clearNotificationOfConversation(String conversationId) {
        notificationManager.cancel(conversationId, MESSAGE_ID);
    }

    private PendingIntent configurePendingIntent(String conversationId, String title) {
        String[] conversationIdentities = getParticipantIdsFromConversation(conversationId);
        Intent intent = new Intent(context, singleConversationClass)
                .setPackage(context.getApplicationContext().getPackageName())
                .putExtra(LayerModule.LAYER_IDENTITIES_KEY, conversationIdentities)
                .putExtra(FROM_NOTIFICATION_KEY, true)
                .putExtra(LayerModule.LAYER_SCREEN_TITLE_KEY, title)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private String[] getParticipantIdsFromConversation(String conversationId) {
        Uri conversationUriId = Uri.parse(conversationId);
        Conversation conversation = layerClient.getConversation(conversationUriId);
        Set<Identity> participants = conversation.getParticipants();
        List<String> participantIdentities = new ArrayList<>(participants.size());
        for (Iterator<Identity> iterator = participants.iterator(); iterator.hasNext(); ) {
            Identity identity = iterator.next();
            participantIdentities.add(identity.getUserId());
        }
        return participantIdentities.toArray(new String[participantIdentities.size()]);
    }

    private static class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }

}

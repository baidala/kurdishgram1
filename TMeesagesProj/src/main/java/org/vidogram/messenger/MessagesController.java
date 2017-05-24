package org.vidogram.messenger;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.widget.Toast;
import itman.Vidofilm.b;
import itman.Vidofilm.d.d;
import java.security.SecureRandom;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.ArrayList<Lorg.vidogram.tgnet.TLRPC.Chat;>;
import java.util.ArrayList<Lorg.vidogram.tgnet.TLRPC.User;>;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import org.vidogram.SQLite.SQLiteCursor;
import org.vidogram.SQLite.SQLiteDatabase;
import org.vidogram.messenger.query.BotQuery;
import org.vidogram.messenger.query.DraftQuery;
import org.vidogram.messenger.query.MessagesQuery;
import org.vidogram.messenger.query.SearchQuery;
import org.vidogram.messenger.query.StickersQuery;
import org.vidogram.messenger.voip.VoIPService;
import org.vidogram.tgnet.AbstractSerializedData;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.SerializedData;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.BotInfo;
import org.vidogram.tgnet.TLRPC.ChannelParticipant;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.ChatFull;
import org.vidogram.tgnet.TLRPC.ChatParticipant;
import org.vidogram.tgnet.TLRPC.ChatParticipants;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.DraftMessage;
import org.vidogram.tgnet.TLRPC.EncryptedChat;
import org.vidogram.tgnet.TLRPC.EncryptedMessage;
import org.vidogram.tgnet.TLRPC.ExportedChatInvite;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.InputChannel;
import org.vidogram.tgnet.TLRPC.InputFile;
import org.vidogram.tgnet.TLRPC.InputPeer;
import org.vidogram.tgnet.TLRPC.InputPhoto;
import org.vidogram.tgnet.TLRPC.InputUser;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.MessageAction;
import org.vidogram.tgnet.TLRPC.MessageEntity;
import org.vidogram.tgnet.TLRPC.MessageMedia;
import org.vidogram.tgnet.TLRPC.NotifyPeer;
import org.vidogram.tgnet.TLRPC.Peer;
import org.vidogram.tgnet.TLRPC.PeerNotifySettings;
import org.vidogram.tgnet.TLRPC.PhoneCall;
import org.vidogram.tgnet.TLRPC.Photo;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.tgnet.TLRPC.SendMessageAction;
import org.vidogram.tgnet.TLRPC.TL_account_registerDevice;
import org.vidogram.tgnet.TLRPC.TL_account_unregisterDevice;
import org.vidogram.tgnet.TLRPC.TL_account_updateStatus;
import org.vidogram.tgnet.TLRPC.TL_auth_logOut;
import org.vidogram.tgnet.TLRPC.TL_boolTrue;
import org.vidogram.tgnet.TLRPC.TL_botInfo;
import org.vidogram.tgnet.TLRPC.TL_channel;
import org.vidogram.tgnet.TLRPC.TL_channelForbidden;
import org.vidogram.tgnet.TLRPC.TL_channelParticipantSelf;
import org.vidogram.tgnet.TLRPC.TL_channelParticipantsRecent;
import org.vidogram.tgnet.TLRPC.TL_channelRoleEditor;
import org.vidogram.tgnet.TLRPC.TL_channels_channelParticipant;
import org.vidogram.tgnet.TLRPC.TL_channels_channelParticipants;
import org.vidogram.tgnet.TLRPC.TL_channels_createChannel;
import org.vidogram.tgnet.TLRPC.TL_channels_deleteChannel;
import org.vidogram.tgnet.TLRPC.TL_channels_deleteUserHistory;
import org.vidogram.tgnet.TLRPC.TL_channels_editAbout;
import org.vidogram.tgnet.TLRPC.TL_channels_editAdmin;
import org.vidogram.tgnet.TLRPC.TL_channels_editPhoto;
import org.vidogram.tgnet.TLRPC.TL_channels_editTitle;
import org.vidogram.tgnet.TLRPC.TL_channels_getFullChannel;
import org.vidogram.tgnet.TLRPC.TL_channels_getMessages;
import org.vidogram.tgnet.TLRPC.TL_channels_getParticipant;
import org.vidogram.tgnet.TLRPC.TL_channels_getParticipants;
import org.vidogram.tgnet.TLRPC.TL_channels_inviteToChannel;
import org.vidogram.tgnet.TLRPC.TL_channels_joinChannel;
import org.vidogram.tgnet.TLRPC.TL_channels_kickFromChannel;
import org.vidogram.tgnet.TLRPC.TL_channels_leaveChannel;
import org.vidogram.tgnet.TLRPC.TL_channels_readHistory;
import org.vidogram.tgnet.TLRPC.TL_channels_toggleInvites;
import org.vidogram.tgnet.TLRPC.TL_channels_toggleSignatures;
import org.vidogram.tgnet.TLRPC.TL_channels_updatePinnedMessage;
import org.vidogram.tgnet.TLRPC.TL_channels_updateUsername;
import org.vidogram.tgnet.TLRPC.TL_chat;
import org.vidogram.tgnet.TLRPC.TL_chatFull;
import org.vidogram.tgnet.TLRPC.TL_chatInviteEmpty;
import org.vidogram.tgnet.TLRPC.TL_chatParticipant;
import org.vidogram.tgnet.TLRPC.TL_chatParticipants;
import org.vidogram.tgnet.TLRPC.TL_chatPhotoEmpty;
import org.vidogram.tgnet.TLRPC.TL_config;
import org.vidogram.tgnet.TLRPC.TL_contactBlocked;
import org.vidogram.tgnet.TLRPC.TL_contactLinkContact;
import org.vidogram.tgnet.TLRPC.TL_contacts_block;
import org.vidogram.tgnet.TLRPC.TL_contacts_getBlocked;
import org.vidogram.tgnet.TLRPC.TL_contacts_resolveUsername;
import org.vidogram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.vidogram.tgnet.TLRPC.TL_contacts_unblock;
import org.vidogram.tgnet.TLRPC.TL_dialog;
import org.vidogram.tgnet.TLRPC.TL_disabledFeature;
import org.vidogram.tgnet.TLRPC.TL_draftMessage;
import org.vidogram.tgnet.TLRPC.TL_encryptedChat;
import org.vidogram.tgnet.TLRPC.TL_encryptedChatRequested;
import org.vidogram.tgnet.TLRPC.TL_encryptedChatWaiting;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_help_getAppChangelog;
import org.vidogram.tgnet.TLRPC.TL_inputChannel;
import org.vidogram.tgnet.TLRPC.TL_inputChannelEmpty;
import org.vidogram.tgnet.TLRPC.TL_inputChatPhotoEmpty;
import org.vidogram.tgnet.TLRPC.TL_inputChatUploadedPhoto;
import org.vidogram.tgnet.TLRPC.TL_inputDocument;
import org.vidogram.tgnet.TLRPC.TL_inputEncryptedChat;
import org.vidogram.tgnet.TLRPC.TL_inputMessagesFilterChatPhotos;
import org.vidogram.tgnet.TLRPC.TL_inputPeerChannel;
import org.vidogram.tgnet.TLRPC.TL_inputPeerChat;
import org.vidogram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.vidogram.tgnet.TLRPC.TL_inputPeerUser;
import org.vidogram.tgnet.TLRPC.TL_inputPhoneCall;
import org.vidogram.tgnet.TLRPC.TL_inputPhotoEmpty;
import org.vidogram.tgnet.TLRPC.TL_inputUser;
import org.vidogram.tgnet.TLRPC.TL_inputUserEmpty;
import org.vidogram.tgnet.TLRPC.TL_inputUserSelf;
import org.vidogram.tgnet.TLRPC.TL_message;
import org.vidogram.tgnet.TLRPC.TL_messageActionChannelCreate;
import org.vidogram.tgnet.TLRPC.TL_messageActionChatAddUser;
import org.vidogram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
import org.vidogram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
import org.vidogram.tgnet.TLRPC.TL_messageActionCreatedBroadcastList;
import org.vidogram.tgnet.TLRPC.TL_messageActionHistoryClear;
import org.vidogram.tgnet.TLRPC.TL_messageActionUserJoined;
import org.vidogram.tgnet.TLRPC.TL_messageEntityMentionName;
import org.vidogram.tgnet.TLRPC.TL_messageFwdHeader;
import org.vidogram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.vidogram.tgnet.TLRPC.TL_messageMediaUnsupported;
import org.vidogram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.vidogram.tgnet.TLRPC.TL_messageService;
import org.vidogram.tgnet.TLRPC.TL_messages_addChatUser;
import org.vidogram.tgnet.TLRPC.TL_messages_affectedHistory;
import org.vidogram.tgnet.TLRPC.TL_messages_affectedMessages;
import org.vidogram.tgnet.TLRPC.TL_messages_channelMessages;
import org.vidogram.tgnet.TLRPC.TL_messages_chatFull;
import org.vidogram.tgnet.TLRPC.TL_messages_createChat;
import org.vidogram.tgnet.TLRPC.TL_messages_deleteChatUser;
import org.vidogram.tgnet.TLRPC.TL_messages_deleteHistory;
import org.vidogram.tgnet.TLRPC.TL_messages_dialogs;
import org.vidogram.tgnet.TLRPC.TL_messages_editChatAdmin;
import org.vidogram.tgnet.TLRPC.TL_messages_editChatPhoto;
import org.vidogram.tgnet.TLRPC.TL_messages_editChatTitle;
import org.vidogram.tgnet.TLRPC.TL_messages_getDialogs;
import org.vidogram.tgnet.TLRPC.TL_messages_getFullChat;
import org.vidogram.tgnet.TLRPC.TL_messages_getHistory;
import org.vidogram.tgnet.TLRPC.TL_messages_getMessages;
import org.vidogram.tgnet.TLRPC.TL_messages_getMessagesViews;
import org.vidogram.tgnet.TLRPC.TL_messages_getPeerDialogs;
import org.vidogram.tgnet.TLRPC.TL_messages_getPeerSettings;
import org.vidogram.tgnet.TLRPC.TL_messages_getPinnedDialogs;
import org.vidogram.tgnet.TLRPC.TL_messages_getWebPagePreview;
import org.vidogram.tgnet.TLRPC.TL_messages_hideReportSpam;
import org.vidogram.tgnet.TLRPC.TL_messages_messages;
import org.vidogram.tgnet.TLRPC.TL_messages_migrateChat;
import org.vidogram.tgnet.TLRPC.TL_messages_peerDialogs;
import org.vidogram.tgnet.TLRPC.TL_messages_readEncryptedHistory;
import org.vidogram.tgnet.TLRPC.TL_messages_readHistory;
import org.vidogram.tgnet.TLRPC.TL_messages_readMessageContents;
import org.vidogram.tgnet.TLRPC.TL_messages_receivedQueue;
import org.vidogram.tgnet.TLRPC.TL_messages_reportEncryptedSpam;
import org.vidogram.tgnet.TLRPC.TL_messages_reportSpam;
import org.vidogram.tgnet.TLRPC.TL_messages_saveGif;
import org.vidogram.tgnet.TLRPC.TL_messages_saveRecentSticker;
import org.vidogram.tgnet.TLRPC.TL_messages_search;
import org.vidogram.tgnet.TLRPC.TL_messages_setEncryptedTyping;
import org.vidogram.tgnet.TLRPC.TL_messages_setTyping;
import org.vidogram.tgnet.TLRPC.TL_messages_startBot;
import org.vidogram.tgnet.TLRPC.TL_messages_toggleChatAdmins;
import org.vidogram.tgnet.TLRPC.TL_notifyPeer;
import org.vidogram.tgnet.TLRPC.TL_peerChannel;
import org.vidogram.tgnet.TLRPC.TL_peerChat;
import org.vidogram.tgnet.TLRPC.TL_peerNotifySettings;
import org.vidogram.tgnet.TLRPC.TL_peerNotifySettingsEmpty;
import org.vidogram.tgnet.TLRPC.TL_peerSettings;
import org.vidogram.tgnet.TLRPC.TL_peerUser;
import org.vidogram.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy;
import org.vidogram.tgnet.TLRPC.TL_phoneCallRequested;
import org.vidogram.tgnet.TLRPC.TL_phone_discardCall;
import org.vidogram.tgnet.TLRPC.TL_photoEmpty;
import org.vidogram.tgnet.TLRPC.TL_photos_deletePhotos;
import org.vidogram.tgnet.TLRPC.TL_photos_getUserPhotos;
import org.vidogram.tgnet.TLRPC.TL_photos_photo;
import org.vidogram.tgnet.TLRPC.TL_photos_photos;
import org.vidogram.tgnet.TLRPC.TL_photos_updateProfilePhoto;
import org.vidogram.tgnet.TLRPC.TL_photos_uploadProfilePhoto;
import org.vidogram.tgnet.TLRPC.TL_privacyKeyChatInvite;
import org.vidogram.tgnet.TLRPC.TL_privacyKeyPhoneCall;
import org.vidogram.tgnet.TLRPC.TL_privacyKeyStatusTimestamp;
import org.vidogram.tgnet.TLRPC.TL_replyKeyboardHide;
import org.vidogram.tgnet.TLRPC.TL_sendMessageCancelAction;
import org.vidogram.tgnet.TLRPC.TL_sendMessageGamePlayAction;
import org.vidogram.tgnet.TLRPC.TL_sendMessageRecordAudioAction;
import org.vidogram.tgnet.TLRPC.TL_sendMessageRecordVideoAction;
import org.vidogram.tgnet.TLRPC.TL_sendMessageTypingAction;
import org.vidogram.tgnet.TLRPC.TL_sendMessageUploadAudioAction;
import org.vidogram.tgnet.TLRPC.TL_sendMessageUploadDocumentAction;
import org.vidogram.tgnet.TLRPC.TL_sendMessageUploadPhotoAction;
import org.vidogram.tgnet.TLRPC.TL_sendMessageUploadVideoAction;
import org.vidogram.tgnet.TLRPC.TL_updateChannel;
import org.vidogram.tgnet.TLRPC.TL_updateChannelMessageViews;
import org.vidogram.tgnet.TLRPC.TL_updateChannelPinnedMessage;
import org.vidogram.tgnet.TLRPC.TL_updateChannelTooLong;
import org.vidogram.tgnet.TLRPC.TL_updateChannelWebPage;
import org.vidogram.tgnet.TLRPC.TL_updateChatAdmins;
import org.vidogram.tgnet.TLRPC.TL_updateChatParticipantAdd;
import org.vidogram.tgnet.TLRPC.TL_updateChatParticipantAdmin;
import org.vidogram.tgnet.TLRPC.TL_updateChatParticipantDelete;
import org.vidogram.tgnet.TLRPC.TL_updateChatParticipants;
import org.vidogram.tgnet.TLRPC.TL_updateChatUserTyping;
import org.vidogram.tgnet.TLRPC.TL_updateConfig;
import org.vidogram.tgnet.TLRPC.TL_updateContactLink;
import org.vidogram.tgnet.TLRPC.TL_updateContactRegistered;
import org.vidogram.tgnet.TLRPC.TL_updateDcOptions;
import org.vidogram.tgnet.TLRPC.TL_updateDeleteChannelMessages;
import org.vidogram.tgnet.TLRPC.TL_updateDeleteMessages;
import org.vidogram.tgnet.TLRPC.TL_updateDialogPinned;
import org.vidogram.tgnet.TLRPC.TL_updateDraftMessage;
import org.vidogram.tgnet.TLRPC.TL_updateEditChannelMessage;
import org.vidogram.tgnet.TLRPC.TL_updateEditMessage;
import org.vidogram.tgnet.TLRPC.TL_updateEncryptedChatTyping;
import org.vidogram.tgnet.TLRPC.TL_updateEncryptedMessagesRead;
import org.vidogram.tgnet.TLRPC.TL_updateEncryption;
import org.vidogram.tgnet.TLRPC.TL_updateMessageID;
import org.vidogram.tgnet.TLRPC.TL_updateNewChannelMessage;
import org.vidogram.tgnet.TLRPC.TL_updateNewEncryptedMessage;
import org.vidogram.tgnet.TLRPC.TL_updateNewGeoChatMessage;
import org.vidogram.tgnet.TLRPC.TL_updateNewMessage;
import org.vidogram.tgnet.TLRPC.TL_updateNewStickerSet;
import org.vidogram.tgnet.TLRPC.TL_updateNotifySettings;
import org.vidogram.tgnet.TLRPC.TL_updatePhoneCall;
import org.vidogram.tgnet.TLRPC.TL_updatePinnedDialogs;
import org.vidogram.tgnet.TLRPC.TL_updatePrivacy;
import org.vidogram.tgnet.TLRPC.TL_updateReadChannelInbox;
import org.vidogram.tgnet.TLRPC.TL_updateReadChannelOutbox;
import org.vidogram.tgnet.TLRPC.TL_updateReadFeaturedStickers;
import org.vidogram.tgnet.TLRPC.TL_updateReadHistoryInbox;
import org.vidogram.tgnet.TLRPC.TL_updateReadHistoryOutbox;
import org.vidogram.tgnet.TLRPC.TL_updateReadMessagesContents;
import org.vidogram.tgnet.TLRPC.TL_updateRecentStickers;
import org.vidogram.tgnet.TLRPC.TL_updateSavedGifs;
import org.vidogram.tgnet.TLRPC.TL_updateServiceNotification;
import org.vidogram.tgnet.TLRPC.TL_updateShort;
import org.vidogram.tgnet.TLRPC.TL_updateShortChatMessage;
import org.vidogram.tgnet.TLRPC.TL_updateShortMessage;
import org.vidogram.tgnet.TLRPC.TL_updateStickerSets;
import org.vidogram.tgnet.TLRPC.TL_updateStickerSetsOrder;
import org.vidogram.tgnet.TLRPC.TL_updateUserBlocked;
import org.vidogram.tgnet.TLRPC.TL_updateUserName;
import org.vidogram.tgnet.TLRPC.TL_updateUserPhone;
import org.vidogram.tgnet.TLRPC.TL_updateUserPhoto;
import org.vidogram.tgnet.TLRPC.TL_updateUserStatus;
import org.vidogram.tgnet.TLRPC.TL_updateUserTyping;
import org.vidogram.tgnet.TLRPC.TL_updateWebPage;
import org.vidogram.tgnet.TLRPC.TL_updates;
import org.vidogram.tgnet.TLRPC.TL_updatesCombined;
import org.vidogram.tgnet.TLRPC.TL_updatesTooLong;
import org.vidogram.tgnet.TLRPC.TL_updates_channelDifference;
import org.vidogram.tgnet.TLRPC.TL_updates_channelDifferenceEmpty;
import org.vidogram.tgnet.TLRPC.TL_updates_channelDifferenceTooLong;
import org.vidogram.tgnet.TLRPC.TL_updates_difference;
import org.vidogram.tgnet.TLRPC.TL_updates_differenceEmpty;
import org.vidogram.tgnet.TLRPC.TL_updates_differenceSlice;
import org.vidogram.tgnet.TLRPC.TL_updates_getDifference;
import org.vidogram.tgnet.TLRPC.TL_updates_getState;
import org.vidogram.tgnet.TLRPC.TL_updates_state;
import org.vidogram.tgnet.TLRPC.TL_userForeign_old2;
import org.vidogram.tgnet.TLRPC.TL_userFull;
import org.vidogram.tgnet.TLRPC.TL_userProfilePhoto;
import org.vidogram.tgnet.TLRPC.TL_userProfilePhotoEmpty;
import org.vidogram.tgnet.TLRPC.TL_userStatusLastMonth;
import org.vidogram.tgnet.TLRPC.TL_userStatusLastWeek;
import org.vidogram.tgnet.TLRPC.TL_userStatusRecently;
import org.vidogram.tgnet.TLRPC.TL_users_getFullUser;
import org.vidogram.tgnet.TLRPC.TL_webPage;
import org.vidogram.tgnet.TLRPC.TL_webPageEmpty;
import org.vidogram.tgnet.TLRPC.TL_webPagePending;
import org.vidogram.tgnet.TLRPC.TL_webPageUrlPending;
import org.vidogram.tgnet.TLRPC.Update;
import org.vidogram.tgnet.TLRPC.Updates;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserProfilePhoto;
import org.vidogram.tgnet.TLRPC.UserStatus;
import org.vidogram.tgnet.TLRPC.Vector;
import org.vidogram.tgnet.TLRPC.WebPage;
import org.vidogram.tgnet.TLRPC.contacts_Blocked;
import org.vidogram.tgnet.TLRPC.messages_Dialogs;
import org.vidogram.tgnet.TLRPC.messages_Messages;
import org.vidogram.tgnet.TLRPC.photos_Photos;
import org.vidogram.tgnet.TLRPC.updates_ChannelDifference;
import org.vidogram.tgnet.TLRPC.updates_Difference;
import org.vidogram.ui.ActionBar.AlertDialog;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ChatActivity;
import org.vidogram.ui.Components.AlertsCreator;
import org.vidogram.ui.ProfileActivity;

public class MessagesController
  implements NotificationCenter.NotificationCenterDelegate
{
  private static volatile MessagesController Instance = null;
  public static final int UPDATE_MASK_ALL = 1535;
  public static final int UPDATE_MASK_AVATAR = 2;
  public static final int UPDATE_MASK_CHANNEL = 8192;
  public static final int UPDATE_MASK_CHAT_ADMINS = 16384;
  public static final int UPDATE_MASK_CHAT_AVATAR = 8;
  public static final int UPDATE_MASK_CHAT_MEMBERS = 32;
  public static final int UPDATE_MASK_CHAT_NAME = 16;
  public static final int UPDATE_MASK_NAME = 1;
  public static final int UPDATE_MASK_NEW_MESSAGE = 2048;
  public static final int UPDATE_MASK_PHONE = 1024;
  public static final int UPDATE_MASK_READ_DIALOG_MESSAGE = 256;
  public static final int UPDATE_MASK_SELECT_DIALOG = 512;
  public static final int UPDATE_MASK_SEND_STATE = 4096;
  public static final int UPDATE_MASK_STATUS = 4;
  public static final int UPDATE_MASK_USER_PHONE = 128;
  public static final int UPDATE_MASK_USER_PRINT = 64;
  public static TLObject req;
  public int allUnread_count = 0;
  public ConcurrentHashMap<String, String> allUsers = new ConcurrentHashMap();
  public boolean allowBigEmoji;
  public ArrayList<Integer> blockedUsers = new ArrayList();
  public int botsUnread_count = 0;
  public int callConnectTimeout = 30000;
  public int callPacketTimeout = 10000;
  public int callReceiveTimeout = 20000;
  public int callRingTimeout = 90000;
  public boolean callsEnabled;
  private SparseArray<ArrayList<Integer>> channelViewsToReload = new SparseArray();
  private SparseArray<ArrayList<Integer>> channelViewsToSend = new SparseArray();
  private HashMap<Integer, Integer> channelsPts = new HashMap();
  public int channelsUnread_count = 0;
  private ConcurrentHashMap<Integer, TLRPC.Chat> chats = new ConcurrentHashMap(100, 1.0F, 2);
  private HashMap<Integer, Boolean> checkingLastMessagesDialogs = new HashMap();
  private ArrayList<Long> createdDialogIds = new ArrayList();
  private Runnable currentDeleteTaskRunnable;
  private ArrayList<Integer> currentDeletingTaskMids;
  private int currentDeletingTaskTime;
  private final Comparator<TLRPC.TL_dialog> dialogComparator = new Comparator()
  {
    public int compare(TLRPC.TL_dialog paramTL_dialog1, TLRPC.TL_dialog paramTL_dialog2)
    {
      if ((!paramTL_dialog1.pinned) && (paramTL_dialog2.pinned))
        return 1;
      if ((paramTL_dialog1.pinned) && (!paramTL_dialog2.pinned))
        return -1;
      if ((paramTL_dialog1.pinned) && (paramTL_dialog2.pinned))
      {
        if (paramTL_dialog1.pinnedNum < paramTL_dialog2.pinnedNum)
          return 1;
        if (paramTL_dialog1.pinnedNum > paramTL_dialog2.pinnedNum)
          return -1;
        return 0;
      }
      TLRPC.DraftMessage localDraftMessage = DraftQuery.getDraft(paramTL_dialog1.id);
      int i;
      if ((localDraftMessage != null) && (localDraftMessage.date >= paramTL_dialog1.last_message_date))
      {
        i = localDraftMessage.date;
        paramTL_dialog1 = DraftQuery.getDraft(paramTL_dialog2.id);
        if ((paramTL_dialog1 == null) || (paramTL_dialog1.date < paramTL_dialog2.last_message_date))
          break label151;
      }
      label151: for (int j = paramTL_dialog1.date; ; j = paramTL_dialog2.last_message_date)
      {
        if (i >= j)
          break label160;
        return 1;
        i = paramTL_dialog1.last_message_date;
        break;
      }
      label160: if (i > j)
        return -1;
      return 0;
    }
  };
  public HashMap<Long, MessageObject> dialogMessage = new HashMap();
  public HashMap<Integer, MessageObject> dialogMessagesByIds = new HashMap();
  public HashMap<Long, MessageObject> dialogMessagesByRandomIds = new HashMap();
  public ArrayList<TLRPC.TL_dialog> dialogs = new ArrayList();
  public ArrayList<TLRPC.TL_dialog> dialogsBotOnly = new ArrayList();
  public ArrayList<TLRPC.TL_dialog> dialogsChannelOnly = new ArrayList();
  public boolean dialogsEndReached;
  public ArrayList<TLRPC.TL_dialog> dialogsFavoriteOnly = new ArrayList();
  public ArrayList<TLRPC.TL_dialog> dialogsGroupsOnly = new ArrayList();
  public ArrayList<TLRPC.TL_dialog> dialogsServerOnly = new ArrayList();
  public ArrayList<TLRPC.TL_dialog> dialogsUserOnly = new ArrayList();
  public ArrayList<TLRPC.TL_dialog> dialogsVidogramOnly = new ArrayList();
  public ConcurrentHashMap<Long, TLRPC.TL_dialog> dialogs_dict = new ConcurrentHashMap(100, 1.0F, 2);
  public ConcurrentHashMap<Long, Integer> dialogs_read_inbox_max = new ConcurrentHashMap(100, 1.0F, 2);
  public ConcurrentHashMap<Long, Integer> dialogs_read_outbox_max = new ConcurrentHashMap(100, 1.0F, 2);
  private ArrayList<TLRPC.TL_disabledFeature> disabledFeatures = new ArrayList();
  public boolean enableJoined = true;
  private ConcurrentHashMap<Integer, TLRPC.EncryptedChat> encryptedChats = new ConcurrentHashMap(10, 1.0F, 2);
  private HashMap<Integer, TLRPC.ExportedChatInvite> exportedChats = new HashMap();
  public boolean firstGettingTask;
  public int fontSize = AndroidUtilities.dp(16.0F);
  private HashMap<Integer, TLRPC.TL_userFull> fullUsers = new HashMap();
  public boolean gettingDifference;
  private HashMap<Integer, Boolean> gettingDifferenceChannels = new HashMap();
  private boolean gettingNewDeleteTask;
  private HashMap<Integer, Boolean> gettingUnknownChannels = new HashMap();
  public int groupBigSize;
  public int groupsUnread_count = 0;
  public int isAllMute = 0;
  public int isBotsMute = 0;
  public int isChannelsMute = 0;
  public int isGroupsMute = 0;
  public int isUsersMute = 0;
  private ArrayList<Integer> joiningToChannels = new ArrayList();
  private int lastPrintingStringCount;
  private long lastStatusUpdateTime;
  private long lastViewsCheckTime;
  public String linkPrefix = "t.me";
  private ArrayList<Integer> loadedFullChats = new ArrayList();
  private ArrayList<Integer> loadedFullParticipants = new ArrayList();
  private ArrayList<Integer> loadedFullUsers = new ArrayList();
  public boolean loadingBlockedUsers = false;
  public boolean loadingDialogs;
  private ArrayList<Integer> loadingFullChats = new ArrayList();
  private ArrayList<Integer> loadingFullParticipants = new ArrayList();
  private ArrayList<Integer> loadingFullUsers = new ArrayList();
  private HashMap<Long, Boolean> loadingPeerSettings = new HashMap();
  public int maxBroadcastCount = 100;
  public int maxEditTime = 172800;
  public int maxGroupCount = 200;
  public int maxMegagroupCount = 5000;
  public int maxPinnedDialogsCount = 5;
  public int maxRecentGifsCount = 200;
  public int maxRecentStickersCount = 30;
  private boolean migratingDialogs;
  public int minGroupConvertSize = 200;
  private SparseIntArray needShortPollChannels = new SparseIntArray();
  public int nextDialogsCacheOffset;
  private boolean offlineSent;
  public ConcurrentHashMap<Integer, Integer> onlinePrivacy = new ConcurrentHashMap(20, 1.0F, 2);
  public HashMap<Long, CharSequence> printingStrings = new HashMap();
  public HashMap<Long, Integer> printingStringsTypes = new HashMap();
  public ConcurrentHashMap<Long, ArrayList<PrintingUser>> printingUsers = new ConcurrentHashMap(20, 1.0F, 2);
  public int ratingDecay;
  public boolean registeringForPush;
  private HashMap<Long, ArrayList<Integer>> reloadingMessages = new HashMap();
  private HashMap<String, ArrayList<MessageObject>> reloadingWebpages = new HashMap();
  private HashMap<Long, ArrayList<MessageObject>> reloadingWebpagesPending = new HashMap();
  public int secretWebpagePreview = 2;
  public HashMap<Integer, HashMap<Long, Boolean>> sendingTypings = new HashMap();
  public boolean serverDialogsEndReached;
  private SparseIntArray shortPollChannels = new SparseIntArray();
  private int statusRequest;
  private int statusSettingState;
  private final Comparator<TLRPC.Update> updatesComparator = new Comparator()
  {
    public int compare(TLRPC.Update paramUpdate1, TLRPC.Update paramUpdate2)
    {
      int i = MessagesController.this.getUpdateType(paramUpdate1);
      int j = MessagesController.this.getUpdateType(paramUpdate2);
      if (i != j)
        return AndroidUtilities.compare(i, j);
      if (i == 0)
        return AndroidUtilities.compare(paramUpdate1.pts, paramUpdate2.pts);
      if (i == 1)
        return AndroidUtilities.compare(paramUpdate1.qts, paramUpdate2.qts);
      if (i == 2)
      {
        i = MessagesController.this.getUpdateChannelId(paramUpdate1);
        j = MessagesController.this.getUpdateChannelId(paramUpdate2);
        if (i == j)
          return AndroidUtilities.compare(paramUpdate1.pts, paramUpdate2.pts);
        return AndroidUtilities.compare(i, j);
      }
      return 0;
    }
  };
  private HashMap<Integer, ArrayList<TLRPC.Updates>> updatesQueueChannels = new HashMap();
  private ArrayList<TLRPC.Updates> updatesQueuePts = new ArrayList();
  private ArrayList<TLRPC.Updates> updatesQueueQts = new ArrayList();
  private ArrayList<TLRPC.Updates> updatesQueueSeq = new ArrayList();
  private HashMap<Integer, Long> updatesStartWaitTimeChannels = new HashMap();
  private long updatesStartWaitTimePts;
  private long updatesStartWaitTimeQts;
  private long updatesStartWaitTimeSeq;
  public boolean updatingState;
  private String uploadingAvatar;
  public boolean useSystemEmoji;
  private ConcurrentHashMap<Integer, TLRPC.User> users = new ConcurrentHashMap(100, 1.0F, 2);
  private ConcurrentHashMap<String, TLRPC.User> usersByUsernames = new ConcurrentHashMap(100, 1.0F, 2);
  public int usersUnread_count = 0;

  public MessagesController()
  {
    ImageLoader.getInstance();
    MessagesStorage.getInstance();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidUpload);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailUpload);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailedLoad);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByServer);
    addSupportUser();
    this.enableJoined = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("EnableContactJoined", true);
    Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
    this.secretWebpagePreview = ((SharedPreferences)localObject).getInt("secretWebpage2", 2);
    this.maxGroupCount = ((SharedPreferences)localObject).getInt("maxGroupCount", 200);
    this.maxMegagroupCount = ((SharedPreferences)localObject).getInt("maxMegagroupCount", 1000);
    this.maxRecentGifsCount = ((SharedPreferences)localObject).getInt("maxRecentGifsCount", 200);
    this.maxRecentStickersCount = ((SharedPreferences)localObject).getInt("maxRecentStickersCount", 30);
    this.maxEditTime = ((SharedPreferences)localObject).getInt("maxEditTime", 3600);
    this.groupBigSize = ((SharedPreferences)localObject).getInt("groupBigSize", 10);
    this.ratingDecay = ((SharedPreferences)localObject).getInt("ratingDecay", 2419200);
    int i;
    if (AndroidUtilities.isTablet())
      i = 18;
    while (true)
    {
      this.fontSize = ((SharedPreferences)localObject).getInt("fons_size", i);
      this.allowBigEmoji = ((SharedPreferences)localObject).getBoolean("allowBigEmoji", false);
      this.useSystemEmoji = ((SharedPreferences)localObject).getBoolean("useSystemEmoji", false);
      this.callsEnabled = ((SharedPreferences)localObject).getBoolean("callsEnabled", false);
      this.linkPrefix = ((SharedPreferences)localObject).getString("linkPrefix", "t.me");
      this.callReceiveTimeout = ((SharedPreferences)localObject).getInt("callReceiveTimeout", 20000);
      this.callRingTimeout = ((SharedPreferences)localObject).getInt("callRingTimeout", 90000);
      this.callConnectTimeout = ((SharedPreferences)localObject).getInt("callConnectTimeout", 30000);
      this.callPacketTimeout = ((SharedPreferences)localObject).getInt("callPacketTimeout", 10000);
      this.maxPinnedDialogsCount = ((SharedPreferences)localObject).getInt("maxPinnedDialogsCount", 5);
      localObject = ((SharedPreferences)localObject).getString("disabledFeatures", null);
      if ((localObject != null) && (((String)localObject).length() != 0))
        try
        {
          localObject = Base64.decode((String)localObject, 0);
          if (localObject != null)
          {
            localObject = new SerializedData(localObject);
            int j = ((SerializedData)localObject).readInt32(false);
            i = 0;
            while (i < j)
            {
              TLRPC.TL_disabledFeature localTL_disabledFeature = TLRPC.TL_disabledFeature.TLdeserialize((AbstractSerializedData)localObject, ((SerializedData)localObject).readInt32(false), false);
              if ((localTL_disabledFeature != null) && (localTL_disabledFeature.feature != null) && (localTL_disabledFeature.description != null))
                this.disabledFeatures.add(localTL_disabledFeature);
              i += 1;
              continue;
              i = 16;
            }
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
    }
  }

  private void applyDialogNotificationsSettings(long paramLong, TLRPC.PeerNotifySettings paramPeerNotifySettings)
  {
    int i = 1;
    int j = 1;
    Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
    int k = ((SharedPreferences)localObject).getInt("notify2_" + paramLong, 0);
    int m = ((SharedPreferences)localObject).getInt("notifyuntil_" + paramLong, 0);
    localObject = ((SharedPreferences)localObject).edit();
    TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)this.dialogs_dict.get(Long.valueOf(paramLong));
    if (localTL_dialog != null)
      localTL_dialog.notify_settings = paramPeerNotifySettings;
    ((SharedPreferences.Editor)localObject).putBoolean("silent_" + paramLong, paramPeerNotifySettings.silent);
    if (paramPeerNotifySettings.mute_until > ConnectionsManager.getInstance().getCurrentTime())
      if (paramPeerNotifySettings.mute_until > ConnectionsManager.getInstance().getCurrentTime() + 31536000)
      {
        if (k == 2)
          break label475;
        ((SharedPreferences.Editor)localObject).putInt("notify2_" + paramLong, 2);
        if (localTL_dialog == null)
          break label469;
        localTL_dialog.notify_settings.mute_until = 2147483647;
        i = 0;
      }
    while (true)
    {
      MessagesStorage.getInstance().setDialogFlags(paramLong, i << 32 | 1L);
      NotificationsController.getInstance().removeNotificationsForDialog(paramLong);
      i = j;
      ((SharedPreferences.Editor)localObject).commit();
      if (i != 0)
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
      return;
      if ((k != 3) || (m != paramPeerNotifySettings.mute_until))
      {
        ((SharedPreferences.Editor)localObject).putInt("notify2_" + paramLong, 3);
        ((SharedPreferences.Editor)localObject).putInt("notifyuntil_" + paramLong, paramPeerNotifySettings.mute_until);
        if (localTL_dialog != null)
          localTL_dialog.notify_settings.mute_until = 0;
      }
      for (i = 1; ; i = 0)
      {
        k = paramPeerNotifySettings.mute_until;
        j = i;
        i = k;
        break;
        if ((k != 0) && (k != 1))
        {
          if (localTL_dialog != null)
            localTL_dialog.notify_settings.mute_until = 0;
          ((SharedPreferences.Editor)localObject).remove("notify2_" + paramLong);
        }
        while (true)
        {
          MessagesStorage.getInstance().setDialogFlags(paramLong, 0L);
          break;
          i = 0;
        }
      }
      label469: i = 0;
      continue;
      label475: i = 0;
      j = 0;
    }
  }

  private void applyDialogsNotificationsSettings(ArrayList<TLRPC.TL_dialog> paramArrayList)
  {
    Object localObject1 = null;
    int j = 0;
    if (j < paramArrayList.size())
    {
      TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)paramArrayList.get(j);
      Object localObject2 = localObject1;
      int i;
      if (localTL_dialog.peer != null)
      {
        localObject2 = localObject1;
        if ((localTL_dialog.notify_settings instanceof TLRPC.TL_peerNotifySettings))
        {
          localObject2 = localObject1;
          if (localObject1 == null)
            localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
          if (localTL_dialog.peer.user_id == 0)
            break label215;
          i = localTL_dialog.peer.user_id;
          label96: ((SharedPreferences.Editor)localObject2).putBoolean("silent_" + i, localTL_dialog.notify_settings.silent);
          if (localTL_dialog.notify_settings.mute_until == 0)
            break label320;
          if (localTL_dialog.notify_settings.mute_until <= ConnectionsManager.getInstance().getCurrentTime() + 31536000)
            break label252;
          ((SharedPreferences.Editor)localObject2).putInt("notify2_" + i, 2);
          localTL_dialog.notify_settings.mute_until = 2147483647;
        }
      }
      while (true)
      {
        j += 1;
        localObject1 = localObject2;
        break;
        label215: if (localTL_dialog.peer.chat_id != 0)
        {
          i = -localTL_dialog.peer.chat_id;
          break label96;
        }
        i = -localTL_dialog.peer.channel_id;
        break label96;
        label252: ((SharedPreferences.Editor)localObject2).putInt("notify2_" + i, 3);
        ((SharedPreferences.Editor)localObject2).putInt("notifyuntil_" + i, localTL_dialog.notify_settings.mute_until);
        continue;
        label320: ((SharedPreferences.Editor)localObject2).remove("notify2_" + i);
      }
    }
    if (localObject1 != null)
      localObject1.commit();
  }

  public static boolean checkCanOpenChat(Bundle paramBundle, BaseFragment paramBaseFragment)
  {
    Object localObject = null;
    if ((paramBundle == null) || (paramBaseFragment == null));
    label128: 
    while (true)
    {
      return true;
      int i = paramBundle.getInt("user_id", 0);
      int j = paramBundle.getInt("chat_id", 0);
      TLRPC.User localUser;
      if (i != 0)
      {
        localUser = getInstance().getUser(Integer.valueOf(i));
        paramBundle = null;
      }
      while (true)
      {
        if ((localUser == null) && (paramBundle == null))
          break label128;
        if (paramBundle != null)
          paramBundle = getRestrictionReason(paramBundle.restriction_reason);
        while (true)
        {
          if (paramBundle == null)
            break label120;
          showCantOpenAlert(paramBaseFragment, paramBundle);
          return false;
          if (j == 0)
            break label122;
          paramBundle = getInstance().getChat(Integer.valueOf(j));
          localUser = null;
          break;
          paramBundle = localObject;
          if (localUser == null)
            continue;
          paramBundle = getRestrictionReason(localUser.restriction_reason);
        }
        label120: break;
        label122: paramBundle = null;
        localUser = null;
      }
    }
  }

  private void checkChannelError(String paramString, int paramInt)
  {
    int i = -1;
    switch (paramString.hashCode())
    {
    default:
    case -795226617:
    case -471086771:
    case -1809401834:
    }
    while (true)
      switch (i)
      {
      default:
        return;
        if (!paramString.equals("CHANNEL_PRIVATE"))
          continue;
        i = 0;
        continue;
        if (!paramString.equals("CHANNEL_PUBLIC_GROUP_NA"))
          continue;
        i = 1;
        continue;
        if (!paramString.equals("USER_BANNED_IN_CHANNEL"))
          continue;
        i = 2;
      case 0:
      case 1:
      case 2:
      }
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatInfoCantLoad, new Object[] { Integer.valueOf(paramInt), Integer.valueOf(0) });
    return;
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatInfoCantLoad, new Object[] { Integer.valueOf(paramInt), Integer.valueOf(1) });
    return;
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatInfoCantLoad, new Object[] { Integer.valueOf(paramInt), Integer.valueOf(2) });
  }

  private boolean checkDeletingTask(boolean paramBoolean)
  {
    int k = 0;
    int i = ConnectionsManager.getInstance().getCurrentTime();
    int j = k;
    if (this.currentDeletingTaskMids != null)
      if (!paramBoolean)
      {
        j = k;
        if (this.currentDeletingTaskTime != 0)
        {
          j = k;
          if (this.currentDeletingTaskTime > i);
        }
      }
      else
      {
        this.currentDeletingTaskTime = 0;
        if ((this.currentDeleteTaskRunnable != null) && (!paramBoolean))
          Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
        this.currentDeleteTaskRunnable = null;
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            MessagesController.this.deleteMessages(MessagesController.this.currentDeletingTaskMids, null, null, 0, false);
            Utilities.stageQueue.postRunnable(new Runnable()
            {
              public void run()
              {
                MessagesController.this.getNewDeleteTask(MessagesController.this.currentDeletingTaskMids);
                MessagesController.access$3102(MessagesController.this, 0);
                MessagesController.access$2902(MessagesController.this, null);
              }
            });
          }
        });
        j = 1;
      }
    return j;
  }

  private void deleteDialog(long paramLong, boolean paramBoolean, int paramInt1, int paramInt2)
  {
    int j = (int)paramLong;
    int k = (int)(paramLong >> 32);
    if (paramInt1 == 2)
      MessagesStorage.getInstance().deleteDialog(paramLong, paramInt1);
    int i;
    Object localObject2;
    Object localObject1;
    while (true)
    {
      return;
      if ((paramInt1 == 0) || (paramInt1 == 3))
        AndroidUtilities.uninstallShortcut(paramLong);
      i = paramInt2;
      if (paramBoolean)
      {
        MessagesStorage.getInstance().deleteDialog(paramLong, paramInt1);
        localObject2 = (TLRPC.TL_dialog)this.dialogs_dict.get(Long.valueOf(paramLong));
        i = paramInt2;
        if (localObject2 != null)
        {
          i = paramInt2;
          if (paramInt2 == 0)
            i = Math.max(0, ((TLRPC.TL_dialog)localObject2).top_message);
          if ((paramInt1 != 0) && (paramInt1 != 3))
            break;
          this.dialogs.remove(localObject2);
          if ((this.dialogsServerOnly.remove(localObject2)) && (DialogObject.isChannel((TLRPC.TL_dialog)localObject2)))
            Utilities.stageQueue.postRunnable(new Runnable(paramLong)
            {
              public void run()
              {
                MessagesController.this.channelsPts.remove(Integer.valueOf(-(int)this.val$did));
                MessagesController.this.shortPollChannels.delete(-(int)this.val$did);
                MessagesController.this.needShortPollChannels.delete(-(int)this.val$did);
              }
            });
          this.dialogsGroupsOnly.remove(localObject2);
          this.dialogsUserOnly.remove(localObject2);
          this.dialogsVidogramOnly.remove(localObject2);
          this.dialogsBotOnly.remove(localObject2);
          this.dialogsChannelOnly.remove(localObject2);
          this.dialogs_dict.remove(Long.valueOf(paramLong));
          this.dialogs_read_inbox_max.remove(Long.valueOf(paramLong));
          this.dialogs_read_outbox_max.remove(Long.valueOf(paramLong));
          this.nextDialogsCacheOffset -= 1;
          localObject1 = (MessageObject)this.dialogMessage.remove(Long.valueOf(((TLRPC.TL_dialog)localObject2).id));
          if (localObject1 == null)
            break label708;
          paramInt2 = ((MessageObject)localObject1).getId();
          this.dialogMessagesByIds.remove(Integer.valueOf(((MessageObject)localObject1).getId()));
          label304: if ((localObject1 != null) && (((MessageObject)localObject1).messageOwner.random_id != 0L))
            this.dialogMessagesByRandomIds.remove(Long.valueOf(((MessageObject)localObject1).messageOwner.random_id));
          if ((paramInt1 != 1) || (j == 0) || (paramInt2 <= 0))
            break label806;
          localObject1 = new TLRPC.TL_messageService();
          ((TLRPC.TL_messageService)localObject1).id = ((TLRPC.TL_dialog)localObject2).top_message;
          ((TLRPC.TL_messageService)localObject1).out = false;
          ((TLRPC.TL_messageService)localObject1).from_id = UserConfig.getClientUserId();
          ((TLRPC.TL_messageService)localObject1).flags |= 256;
          ((TLRPC.TL_messageService)localObject1).action = new TLRPC.TL_messageActionHistoryClear();
          ((TLRPC.TL_messageService)localObject1).date = ((TLRPC.TL_dialog)localObject2).last_message_date;
          if (j <= 0)
            break label738;
          ((TLRPC.TL_messageService)localObject1).to_id = new TLRPC.TL_peerUser();
          ((TLRPC.TL_messageService)localObject1).to_id.user_id = j;
          label453: Object localObject3 = new MessageObject((TLRPC.Message)localObject1, null, this.createdDialogIds.contains(Long.valueOf(((TLRPC.TL_messageService)localObject1).dialog_id)));
          localObject2 = new ArrayList();
          ((ArrayList)localObject2).add(localObject3);
          localObject3 = new ArrayList();
          ((ArrayList)localObject3).add(localObject1);
          updateInterfaceWithMessages(paramLong, (ArrayList)localObject2);
          MessagesStorage.getInstance().putMessages((ArrayList)localObject3, false, true, false, 0);
        }
        else
        {
          label533: NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.removeAllMessagesFromDialog, new Object[] { Long.valueOf(paramLong), Boolean.valueOf(false) });
          MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramLong)
          {
            public void run()
            {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  NotificationsController.getInstance().removeNotificationsForDialog(MessagesController.37.this.val$did);
                }
              });
            }
          });
        }
      }
      else
      {
        if ((k == 1) || (paramInt1 == 3))
          continue;
        if (j == 0)
          break label827;
        localObject1 = getInputPeer(j);
        if ((localObject1 == null) || ((localObject1 instanceof TLRPC.TL_inputPeerChannel)))
          continue;
        localObject2 = new TLRPC.TL_messages_deleteHistory();
        ((TLRPC.TL_messages_deleteHistory)localObject2).peer = ((TLRPC.InputPeer)localObject1);
        if (paramInt1 != 0)
          break label815;
        paramInt2 = 2147483647;
        label654: ((TLRPC.TL_messages_deleteHistory)localObject2).max_id = paramInt2;
        if (paramInt1 == 0)
          break label822;
      }
    }
    label806: label815: label822: for (paramBoolean = true; ; paramBoolean = false)
    {
      ((TLRPC.TL_messages_deleteHistory)localObject2).just_clear = paramBoolean;
      ConnectionsManager.getInstance().sendRequest((TLObject)localObject2, new RequestDelegate(paramLong, paramInt1, i)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          if (paramTL_error == null)
          {
            paramTLObject = (TLRPC.TL_messages_affectedHistory)paramTLObject;
            if (paramTLObject.offset > 0)
              MessagesController.this.deleteDialog(this.val$did, false, this.val$onlyHistory, this.val$max_id_delete_final);
            MessagesController.this.processNewDifferenceParams(-1, paramTLObject.pts, -1, paramTLObject.pts_count);
          }
        }
      }
      , 64);
      return;
      ((TLRPC.TL_dialog)localObject2).unread_count = 0;
      break;
      label708: paramInt2 = ((TLRPC.TL_dialog)localObject2).top_message;
      localObject1 = (MessageObject)this.dialogMessagesByIds.remove(Integer.valueOf(((TLRPC.TL_dialog)localObject2).top_message));
      break label304;
      label738: if (ChatObject.isChannel(getChat(Integer.valueOf(-j))))
      {
        ((TLRPC.TL_messageService)localObject1).to_id = new TLRPC.TL_peerChannel();
        ((TLRPC.TL_messageService)localObject1).to_id.channel_id = (-j);
        break label453;
      }
      ((TLRPC.TL_messageService)localObject1).to_id = new TLRPC.TL_peerChat();
      ((TLRPC.TL_messageService)localObject1).to_id.chat_id = (-j);
      break label453;
      ((TLRPC.TL_dialog)localObject2).top_message = 0;
      break label533;
      paramInt2 = i;
      break label654;
    }
    label827: if (paramInt1 == 1)
    {
      SecretChatHelper.getInstance().sendClearHistoryMessage(getEncryptedChat(Integer.valueOf(k)), null);
      return;
    }
    SecretChatHelper.getInstance().declineSecretChat(k);
  }

  private void getChannelDifference(int paramInt)
  {
    getChannelDifference(paramInt, 0, 0L, null);
  }

  public static TLRPC.InputChannel getInputChannel(int paramInt)
  {
    return getInputChannel(getInstance().getChat(Integer.valueOf(paramInt)));
  }

  public static TLRPC.InputChannel getInputChannel(TLRPC.Chat paramChat)
  {
    if (((paramChat instanceof TLRPC.TL_channel)) || ((paramChat instanceof TLRPC.TL_channelForbidden)))
    {
      TLRPC.TL_inputChannel localTL_inputChannel = new TLRPC.TL_inputChannel();
      localTL_inputChannel.channel_id = paramChat.id;
      localTL_inputChannel.access_hash = paramChat.access_hash;
      return localTL_inputChannel;
    }
    return new TLRPC.TL_inputChannelEmpty();
  }

  public static TLRPC.InputPeer getInputPeer(int paramInt)
  {
    Object localObject2;
    Object localObject1;
    if (paramInt < 0)
    {
      localObject2 = getInstance().getChat(Integer.valueOf(-paramInt));
      if (ChatObject.isChannel((TLRPC.Chat)localObject2))
      {
        localObject1 = new TLRPC.TL_inputPeerChannel();
        ((TLRPC.InputPeer)localObject1).channel_id = (-paramInt);
        ((TLRPC.InputPeer)localObject1).access_hash = ((TLRPC.Chat)localObject2).access_hash;
      }
    }
    TLRPC.User localUser;
    do
    {
      return localObject1;
      localObject1 = new TLRPC.TL_inputPeerChat();
      ((TLRPC.InputPeer)localObject1).chat_id = (-paramInt);
      return localObject1;
      localUser = getInstance().getUser(Integer.valueOf(paramInt));
      localObject2 = new TLRPC.TL_inputPeerUser();
      ((TLRPC.InputPeer)localObject2).user_id = paramInt;
      localObject1 = localObject2;
    }
    while (localUser == null);
    ((TLRPC.InputPeer)localObject2).access_hash = localUser.access_hash;
    return (TLRPC.InputPeer)(TLRPC.InputPeer)localObject2;
  }

  public static TLRPC.InputUser getInputUser(int paramInt)
  {
    return getInputUser(getInstance().getUser(Integer.valueOf(paramInt)));
  }

  public static TLRPC.InputUser getInputUser(TLRPC.User paramUser)
  {
    if (paramUser == null)
      return new TLRPC.TL_inputUserEmpty();
    if (paramUser.id == UserConfig.getClientUserId())
      return new TLRPC.TL_inputUserSelf();
    TLRPC.TL_inputUser localTL_inputUser = new TLRPC.TL_inputUser();
    localTL_inputUser.user_id = paramUser.id;
    localTL_inputUser.access_hash = paramUser.access_hash;
    return localTL_inputUser;
  }

  public static MessagesController getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      monitorenter;
      try
      {
        MessagesController localMessagesController = Instance;
        localObject1 = localMessagesController;
        if (localMessagesController == null)
        {
          localObject1 = new MessagesController();
          Instance = (MessagesController)localObject1;
        }
        return localObject1;
      }
      finally
      {
        monitorexit;
      }
    }
    return (MessagesController)localObject2;
  }

  public static TLRPC.Peer getPeer(int paramInt)
  {
    if (paramInt < 0)
    {
      localObject = getInstance().getChat(Integer.valueOf(-paramInt));
      if (((localObject instanceof TLRPC.TL_channel)) || ((localObject instanceof TLRPC.TL_channelForbidden)))
      {
        localObject = new TLRPC.TL_peerChannel();
        ((TLRPC.Peer)localObject).channel_id = (-paramInt);
        return localObject;
      }
      localObject = new TLRPC.TL_peerChat();
      ((TLRPC.Peer)localObject).chat_id = (-paramInt);
      return localObject;
    }
    getInstance().getUser(Integer.valueOf(paramInt));
    Object localObject = new TLRPC.TL_peerUser();
    ((TLRPC.Peer)localObject).user_id = paramInt;
    return (TLRPC.Peer)localObject;
  }

  private static String getRestrictionReason(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0));
    int i;
    String str;
    do
    {
      do
      {
        return null;
        i = paramString.indexOf(": ");
      }
      while (i <= 0);
      str = paramString.substring(0, i);
    }
    while ((!str.contains("-all")) && (!str.contains("-android")));
    return paramString.substring(i + 2);
  }

  private int getUpdateChannelId(TLRPC.Update paramUpdate)
  {
    if ((paramUpdate instanceof TLRPC.TL_updateNewChannelMessage))
      return ((TLRPC.TL_updateNewChannelMessage)paramUpdate).message.to_id.channel_id;
    if ((paramUpdate instanceof TLRPC.TL_updateEditChannelMessage))
      return ((TLRPC.TL_updateEditChannelMessage)paramUpdate).message.to_id.channel_id;
    return paramUpdate.channel_id;
  }

  private int getUpdateSeq(TLRPC.Updates paramUpdates)
  {
    if ((paramUpdates instanceof TLRPC.TL_updatesCombined))
      return paramUpdates.seq_start;
    return paramUpdates.seq;
  }

  private int getUpdateType(TLRPC.Update paramUpdate)
  {
    if (((paramUpdate instanceof TLRPC.TL_updateNewMessage)) || ((paramUpdate instanceof TLRPC.TL_updateReadMessagesContents)) || ((paramUpdate instanceof TLRPC.TL_updateReadHistoryInbox)) || ((paramUpdate instanceof TLRPC.TL_updateReadHistoryOutbox)) || ((paramUpdate instanceof TLRPC.TL_updateDeleteMessages)) || ((paramUpdate instanceof TLRPC.TL_updateWebPage)) || ((paramUpdate instanceof TLRPC.TL_updateEditMessage)))
      return 0;
    if ((paramUpdate instanceof TLRPC.TL_updateNewEncryptedMessage))
      return 1;
    if (((paramUpdate instanceof TLRPC.TL_updateNewChannelMessage)) || ((paramUpdate instanceof TLRPC.TL_updateDeleteChannelMessages)) || ((paramUpdate instanceof TLRPC.TL_updateEditChannelMessage)) || ((paramUpdate instanceof TLRPC.TL_updateChannelWebPage)))
      return 2;
    return 3;
  }

  private String getUserNameForTyping(TLRPC.User paramUser)
  {
    if (paramUser == null)
      return "";
    if ((paramUser.first_name != null) && (paramUser.first_name.length() > 0))
      return paramUser.first_name;
    if ((paramUser.last_name != null) && (paramUser.last_name.length() > 0))
      return paramUser.last_name;
    return "";
  }

  public static boolean isFeatureEnabled(String paramString, BaseFragment paramBaseFragment)
  {
    if ((paramString == null) || (paramString.length() == 0) || (getInstance().disabledFeatures.isEmpty()) || (paramBaseFragment == null))
      return true;
    Iterator localIterator = getInstance().disabledFeatures.iterator();
    while (localIterator.hasNext())
    {
      TLRPC.TL_disabledFeature localTL_disabledFeature = (TLRPC.TL_disabledFeature)localIterator.next();
      if (!localTL_disabledFeature.feature.equals(paramString))
        continue;
      if (paramBaseFragment.getParentActivity() != null)
      {
        paramString = new AlertDialog.Builder(paramBaseFragment.getParentActivity());
        paramString.setTitle("Oops!");
        paramString.setPositiveButton(LocaleController.getString("OK", 2131166153), null);
        paramString.setMessage(localTL_disabledFeature.description);
        paramBaseFragment.showDialog(paramString.create());
      }
      return false;
    }
    return true;
  }

  private boolean isNotifySettingsMuted(TLRPC.PeerNotifySettings paramPeerNotifySettings)
  {
    return ((paramPeerNotifySettings instanceof TLRPC.TL_peerNotifySettings)) && (paramPeerNotifySettings.mute_until > ConnectionsManager.getInstance().getCurrentTime());
  }

  private int isValidUpdate(TLRPC.Updates paramUpdates, int paramInt)
  {
    int i = 1;
    int j;
    if (paramInt == 0)
    {
      j = getUpdateSeq(paramUpdates);
      if ((MessagesStorage.lastSeqValue + 1 == j) || (MessagesStorage.lastSeqValue == j))
        paramInt = 0;
    }
    while (true)
    {
      return paramInt;
      paramInt = i;
      if (MessagesStorage.lastSeqValue >= j)
      {
        return 2;
        if (paramInt == 1)
        {
          if (paramUpdates.pts <= MessagesStorage.lastPtsValue)
            return 2;
          paramInt = i;
          if (MessagesStorage.lastPtsValue + paramUpdates.pts_count == paramUpdates.pts)
            return 0;
        }
        if (paramInt != 2)
          break;
        if (paramUpdates.pts <= MessagesStorage.lastQtsValue)
          return 2;
        paramInt = i;
        if (MessagesStorage.lastQtsValue + paramUpdates.updates.size() == paramUpdates.pts)
          return 0;
      }
    }
    return 0;
  }

  private void migrateDialogs(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, long paramLong)
  {
    if ((this.migratingDialogs) || (paramInt1 == -1))
      return;
    this.migratingDialogs = true;
    TLRPC.TL_messages_getDialogs localTL_messages_getDialogs = new TLRPC.TL_messages_getDialogs();
    localTL_messages_getDialogs.limit = 100;
    localTL_messages_getDialogs.offset_id = paramInt1;
    localTL_messages_getDialogs.offset_date = paramInt2;
    if (paramInt1 == 0)
    {
      localTL_messages_getDialogs.offset_peer = new TLRPC.TL_inputPeerEmpty();
      ConnectionsManager.getInstance().sendRequest(localTL_messages_getDialogs, new RequestDelegate()
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          if (paramTL_error == null)
          {
            paramTLObject = (TLRPC.messages_Dialogs)paramTLObject;
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramTLObject)
            {
              public void run()
              {
                Object localObject1 = null;
                int i;
                Object localObject4;
                int k;
                int j;
                label125: Object localObject2;
                while (true)
                {
                  try
                  {
                    if (this.val$dialogsRes.dialogs.size() != 100)
                      break label939;
                    i = 0;
                    if (i >= this.val$dialogsRes.messages.size())
                      continue;
                    localObject4 = (TLRPC.Message)this.val$dialogsRes.messages.get(i);
                    localObject3 = localObject4;
                    if (localObject1 == null)
                      break label914;
                    if (((TLRPC.Message)localObject4).date >= ((TLRPC.Message)localObject1).date)
                      break label910;
                    localObject3 = localObject4;
                    break label914;
                    k = ((TLRPC.Message)localObject1).id;
                    UserConfig.migrateOffsetDate = ((TLRPC.Message)localObject1).date;
                    if (((TLRPC.Message)localObject1).to_id.channel_id == 0)
                      continue;
                    UserConfig.migrateOffsetChannelId = ((TLRPC.Message)localObject1).to_id.channel_id;
                    UserConfig.migrateOffsetChatId = 0;
                    UserConfig.migrateOffsetUserId = 0;
                    j = 0;
                    i = k;
                    if (j >= this.val$dialogsRes.chats.size())
                      continue;
                    localObject1 = (TLRPC.Chat)this.val$dialogsRes.chats.get(j);
                    if (((TLRPC.Chat)localObject1).id != UserConfig.migrateOffsetChannelId)
                      break label925;
                    UserConfig.migrateOffsetAccess = ((TLRPC.Chat)localObject1).access_hash;
                    i = k;
                    label178: localObject3 = new StringBuilder(this.val$dialogsRes.dialogs.size() * 12);
                    localObject1 = new HashMap();
                    j = 0;
                    if (j >= this.val$dialogsRes.dialogs.size())
                      break;
                    localObject4 = (TLRPC.TL_dialog)this.val$dialogsRes.dialogs.get(j);
                    if (((TLRPC.TL_dialog)localObject4).peer.channel_id == 0)
                      break label521;
                    ((TLRPC.TL_dialog)localObject4).id = (-((TLRPC.TL_dialog)localObject4).peer.channel_id);
                    if (((StringBuilder)localObject3).length() <= 0)
                      continue;
                    ((StringBuilder)localObject3).append(",");
                    ((StringBuilder)localObject3).append(((TLRPC.TL_dialog)localObject4).id);
                    ((HashMap)localObject1).put(Long.valueOf(((TLRPC.TL_dialog)localObject4).id), localObject4);
                    j += 1;
                    continue;
                    if (((TLRPC.Message)localObject1).to_id.chat_id == 0)
                      break label431;
                    UserConfig.migrateOffsetChatId = ((TLRPC.Message)localObject1).to_id.chat_id;
                    UserConfig.migrateOffsetChannelId = 0;
                    UserConfig.migrateOffsetUserId = 0;
                    j = 0;
                    i = k;
                    if (j >= this.val$dialogsRes.chats.size())
                      continue;
                    localObject1 = (TLRPC.Chat)this.val$dialogsRes.chats.get(j);
                    if (((TLRPC.Chat)localObject1).id == UserConfig.migrateOffsetChatId)
                    {
                      UserConfig.migrateOffsetAccess = ((TLRPC.Chat)localObject1).access_hash;
                      i = k;
                      continue;
                    }
                  }
                  catch (Exception localException)
                  {
                    FileLog.e(localException);
                    AndroidUtilities.runOnUIThread(new Runnable()
                    {
                      public void run()
                      {
                        MessagesController.access$4502(MessagesController.this, false);
                      }
                    });
                    return;
                  }
                  j += 1;
                  continue;
                  label431: i = k;
                  if (localException.to_id.user_id == 0)
                    continue;
                  UserConfig.migrateOffsetUserId = localException.to_id.user_id;
                  UserConfig.migrateOffsetChatId = 0;
                  UserConfig.migrateOffsetChannelId = 0;
                  j = 0;
                  label465: i = k;
                  if (j >= this.val$dialogsRes.users.size())
                    continue;
                  localObject2 = (TLRPC.User)this.val$dialogsRes.users.get(j);
                  if (((TLRPC.User)localObject2).id != UserConfig.migrateOffsetUserId)
                    break label932;
                  UserConfig.migrateOffsetAccess = ((TLRPC.User)localObject2).access_hash;
                  i = k;
                  continue;
                  label521: if (((TLRPC.TL_dialog)localObject4).peer.chat_id != 0)
                  {
                    ((TLRPC.TL_dialog)localObject4).id = (-((TLRPC.TL_dialog)localObject4).peer.chat_id);
                    continue;
                  }
                  ((TLRPC.TL_dialog)localObject4).id = ((TLRPC.TL_dialog)localObject4).peer.user_id;
                }
                Object localObject3 = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs WHERE did IN (%s)", new Object[] { ((StringBuilder)localObject3).toString() }), new Object[0]);
                label602: 
                while (((SQLiteCursor)localObject3).next())
                {
                  long l = ((SQLiteCursor)localObject3).longValue(0);
                  localObject4 = (TLRPC.TL_dialog)((HashMap)localObject2).remove(Long.valueOf(l));
                  if (localObject4 == null)
                    continue;
                  this.val$dialogsRes.dialogs.remove(localObject4);
                  j = 0;
                  if (j >= this.val$dialogsRes.messages.size())
                    break label949;
                  TLRPC.Message localMessage = (TLRPC.Message)this.val$dialogsRes.messages.get(j);
                  if (MessageObject.getDialogId(localMessage) != l)
                    break label944;
                  this.val$dialogsRes.messages.remove(j);
                  if (localMessage.id == ((TLRPC.TL_dialog)localObject4).top_message)
                    ((TLRPC.TL_dialog)localObject4).top_message = 0;
                  if (((TLRPC.TL_dialog)localObject4).top_message == 0)
                    continue;
                  j -= 1;
                  break label944;
                }
                label653: ((SQLiteCursor)localObject3).dispose();
                localObject3 = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT min(date) FROM dialogs WHERE date != 0 AND did >> 32 IN (0, -1)", new Object[0]);
                if (((SQLiteCursor)localObject3).next())
                {
                  k = Math.max(1441062000, ((SQLiteCursor)localObject3).intValue(0));
                  j = 0;
                  label787: if (j >= this.val$dialogsRes.messages.size())
                    break label964;
                  localObject4 = (TLRPC.Message)this.val$dialogsRes.messages.get(j);
                  if (((TLRPC.Message)localObject4).date < k)
                  {
                    this.val$dialogsRes.messages.remove(j);
                    localObject4 = (TLRPC.TL_dialog)((HashMap)localObject2).remove(Long.valueOf(MessageObject.getDialogId((TLRPC.Message)localObject4)));
                    if (localObject4 == null)
                      break label951;
                    this.val$dialogsRes.dialogs.remove(localObject4);
                    break label951;
                  }
                }
                label910: label914: label925: label932: label939: label944: label949: label951: label957: label964: 
                while (true)
                {
                  ((SQLiteCursor)localObject3).dispose();
                  MessagesController.this.processLoadedDialogs(this.val$dialogsRes, null, i, 0, 0, false, true, false);
                  return;
                  break label957;
                  continue;
                  localObject3 = localObject2;
                  i += 1;
                  localObject2 = localObject3;
                  break;
                  j += 1;
                  break label125;
                  j += 1;
                  break label465;
                  i = -1;
                  break label178;
                  j += 1;
                  break label653;
                  break label602;
                  j -= 1;
                  i = -1;
                  j += 1;
                  break label787;
                }
              }
            });
            return;
          }
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              MessagesController.access$4502(MessagesController.this, false);
            }
          });
        }
      });
      return;
    }
    if (paramInt5 != 0)
    {
      localTL_messages_getDialogs.offset_peer = new TLRPC.TL_inputPeerChannel();
      localTL_messages_getDialogs.offset_peer.channel_id = paramInt5;
    }
    while (true)
    {
      localTL_messages_getDialogs.offset_peer.access_hash = paramLong;
      break;
      if (paramInt3 != 0)
      {
        localTL_messages_getDialogs.offset_peer = new TLRPC.TL_inputPeerUser();
        localTL_messages_getDialogs.offset_peer.user_id = paramInt3;
        continue;
      }
      localTL_messages_getDialogs.offset_peer = new TLRPC.TL_inputPeerChat();
      localTL_messages_getDialogs.offset_peer.chat_id = paramInt4;
    }
  }

  public static void openByUserName(String paramString, BaseFragment paramBaseFragment, int paramInt)
  {
    if ((paramString == null) || (paramBaseFragment == null));
    do
    {
      return;
      localObject = getInstance().getUser(paramString);
      if (localObject == null)
        continue;
      openChatOrProfileWith((TLRPC.User)localObject, null, paramBaseFragment, paramInt, false);
      return;
    }
    while (paramBaseFragment.getParentActivity() == null);
    Object localObject = new AlertDialog(paramBaseFragment.getParentActivity(), 1);
    ((AlertDialog)localObject).setMessage(LocaleController.getString("Loading", 2131165920));
    ((AlertDialog)localObject).setCanceledOnTouchOutside(false);
    ((AlertDialog)localObject).setCancelable(false);
    TLRPC.TL_contacts_resolveUsername localTL_contacts_resolveUsername = new TLRPC.TL_contacts_resolveUsername();
    localTL_contacts_resolveUsername.username = paramString;
    paramInt = ConnectionsManager.getInstance().sendRequest(localTL_contacts_resolveUsername, new RequestDelegate((AlertDialog)localObject, paramBaseFragment, paramInt)
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
        {
          public void run()
          {
            do
              try
              {
                MessagesController.115.this.val$progressDialog.dismiss();
                MessagesController.115.this.val$fragment.setVisibleDialog(null);
                if (this.val$error != null)
                  continue;
                TLRPC.TL_contacts_resolvedPeer localTL_contacts_resolvedPeer = (TLRPC.TL_contacts_resolvedPeer)this.val$response;
                MessagesController.getInstance().putUsers(localTL_contacts_resolvedPeer.users, false);
                MessagesController.getInstance().putChats(localTL_contacts_resolvedPeer.chats, false);
                MessagesStorage.getInstance().putUsersAndChats(localTL_contacts_resolvedPeer.users, localTL_contacts_resolvedPeer.chats, false, true);
                if (!localTL_contacts_resolvedPeer.chats.isEmpty())
                {
                  MessagesController.openChatOrProfileWith(null, (TLRPC.Chat)localTL_contacts_resolvedPeer.chats.get(0), MessagesController.115.this.val$fragment, 1, false);
                  return;
                }
              }
              catch (Exception localException1)
              {
                do
                  while (true)
                    FileLog.e(localException1);
                while (localException1.users.isEmpty());
                MessagesController.openChatOrProfileWith((TLRPC.User)localException1.users.get(0), null, MessagesController.115.this.val$fragment, MessagesController.115.this.val$type, false);
                return;
              }
            while ((MessagesController.115.this.val$fragment == null) || (MessagesController.115.this.val$fragment.getParentActivity() == null));
            try
            {
              Toast.makeText(MessagesController.115.this.val$fragment.getParentActivity(), LocaleController.getString("NoUsernameFound", 2131166054), 0).show();
              return;
            }
            catch (Exception localException2)
            {
              FileLog.e(localException2);
            }
          }
        });
      }
    });
    ((AlertDialog)localObject).setButton(-2, LocaleController.getString("Cancel", 2131165427), new DialogInterface.OnClickListener(paramInt, paramBaseFragment)
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        ConnectionsManager.getInstance().cancelRequest(this.val$reqId, true);
        try
        {
          paramDialogInterface.dismiss();
          if (this.val$fragment != null)
            this.val$fragment.setVisibleDialog(null);
          return;
        }
        catch (Exception paramDialogInterface)
        {
          while (true)
            FileLog.e(paramDialogInterface);
        }
      }
    });
    paramBaseFragment.setVisibleDialog((Dialog)localObject);
    ((AlertDialog)localObject).show();
  }

  public static void openChatOrProfileWith(TLRPC.User paramUser, TLRPC.Chat paramChat, BaseFragment paramBaseFragment, int paramInt, boolean paramBoolean)
  {
    if (((paramUser == null) && (paramChat == null)) || (paramBaseFragment == null))
      return;
    Object localObject = null;
    boolean bool;
    int i;
    if (paramChat != null)
    {
      localObject = getRestrictionReason(paramChat.restriction_reason);
      bool = paramBoolean;
      i = paramInt;
    }
    while (localObject != null)
    {
      showCantOpenAlert(paramBaseFragment, (String)localObject);
      return;
      i = paramInt;
      bool = paramBoolean;
      if (paramUser == null)
        continue;
      String str = getRestrictionReason(paramUser.restriction_reason);
      localObject = str;
      i = paramInt;
      bool = paramBoolean;
      if (!paramUser.bot)
        continue;
      bool = true;
      i = 1;
      localObject = str;
    }
    localObject = new Bundle();
    if (paramChat != null)
      ((Bundle)localObject).putInt("chat_id", paramChat.id);
    while (i == 0)
    {
      paramBaseFragment.presentFragment(new ProfileActivity((Bundle)localObject));
      return;
      ((Bundle)localObject).putInt("user_id", paramUser.id);
    }
    paramBaseFragment.presentFragment(new ChatActivity((Bundle)localObject), bool);
  }

  private void processChannelsUpdatesQueue(int paramInt1, int paramInt2)
  {
    Object localObject = (ArrayList)this.updatesQueueChannels.get(Integer.valueOf(paramInt1));
    if (localObject == null)
      return;
    Integer localInteger = (Integer)this.channelsPts.get(Integer.valueOf(paramInt1));
    if ((((ArrayList)localObject).isEmpty()) || (localInteger == null))
    {
      this.updatesQueueChannels.remove(Integer.valueOf(paramInt1));
      return;
    }
    Collections.sort((List)localObject, new Comparator()
    {
      public int compare(TLRPC.Updates paramUpdates1, TLRPC.Updates paramUpdates2)
      {
        return AndroidUtilities.compare(paramUpdates1.pts, paramUpdates2.pts);
      }
    });
    if (paramInt2 == 2)
      this.channelsPts.put(Integer.valueOf(paramInt1), Integer.valueOf(((TLRPC.Updates)((ArrayList)localObject).get(0)).pts));
    paramInt2 = 0;
    label111: if (((ArrayList)localObject).size() > 0)
    {
      TLRPC.Updates localUpdates = (TLRPC.Updates)((ArrayList)localObject).get(0);
      int i;
      if (localUpdates.pts <= localInteger.intValue())
      {
        i = 2;
        if (i != 0)
          break label197;
        processUpdates(localUpdates, true);
        ((ArrayList)localObject).remove(0);
        paramInt2 = 1;
      }
      while (true)
      {
        break label111;
        if (localInteger.intValue() + localUpdates.pts_count == localUpdates.pts)
        {
          i = 0;
          break label145;
        }
        i = 1;
        break label145;
        if (i == 1)
        {
          localObject = (Long)this.updatesStartWaitTimeChannels.get(Integer.valueOf(paramInt1));
          if ((localObject != null) && ((paramInt2 != 0) || (Math.abs(System.currentTimeMillis() - ((Long)localObject).longValue()) <= 1500L)))
          {
            FileLog.e("HOLE IN CHANNEL " + paramInt1 + " UPDATES QUEUE - will wait more time");
            if (paramInt2 == 0)
              break;
            this.updatesStartWaitTimeChannels.put(Integer.valueOf(paramInt1), Long.valueOf(System.currentTimeMillis()));
            return;
          }
          FileLog.e("HOLE IN CHANNEL " + paramInt1 + " UPDATES QUEUE - getChannelDifference ");
          this.updatesStartWaitTimeChannels.remove(Integer.valueOf(paramInt1));
          this.updatesQueueChannels.remove(Integer.valueOf(paramInt1));
          getChannelDifference(paramInt1);
          return;
        }
        ((ArrayList)localObject).remove(0);
      }
    }
    label145: label197: this.updatesQueueChannels.remove(Integer.valueOf(paramInt1));
    this.updatesStartWaitTimeChannels.remove(Integer.valueOf(paramInt1));
    FileLog.e("UPDATES CHANNEL " + paramInt1 + " QUEUE PROCEED - OK");
  }

  private void processUpdatesQueue(int paramInt1, int paramInt2)
  {
    ArrayList localArrayList;
    if (paramInt1 == 0)
    {
      localArrayList = this.updatesQueueSeq;
      Collections.sort(localArrayList, new Comparator()
      {
        public int compare(TLRPC.Updates paramUpdates1, TLRPC.Updates paramUpdates2)
        {
          return AndroidUtilities.compare(MessagesController.this.getUpdateSeq(paramUpdates1), MessagesController.this.getUpdateSeq(paramUpdates2));
        }
      });
    }
    while (true)
    {
      if ((localArrayList != null) && (!localArrayList.isEmpty()))
      {
        TLRPC.Updates localUpdates;
        label65: label67: int i;
        if (paramInt2 == 2)
        {
          localUpdates = (TLRPC.Updates)localArrayList.get(0);
          if (paramInt1 == 0)
            MessagesStorage.lastSeqValue = getUpdateSeq(localUpdates);
        }
        else
        {
          paramInt2 = 0;
          if (localArrayList.size() <= 0)
            break label287;
          localUpdates = (TLRPC.Updates)localArrayList.get(0);
          i = isValidUpdate(localUpdates, paramInt1);
          if (i != 0)
            break label198;
          processUpdates(localUpdates, true);
          localArrayList.remove(0);
          paramInt2 = 1;
        }
        while (true)
        {
          break label67;
          if (paramInt1 == 1)
          {
            localArrayList = this.updatesQueuePts;
            Collections.sort(localArrayList, new Comparator()
            {
              public int compare(TLRPC.Updates paramUpdates1, TLRPC.Updates paramUpdates2)
              {
                return AndroidUtilities.compare(paramUpdates1.pts, paramUpdates2.pts);
              }
            });
            break;
          }
          if (paramInt1 != 2)
            break label305;
          localArrayList = this.updatesQueueQts;
          Collections.sort(localArrayList, new Comparator()
          {
            public int compare(TLRPC.Updates paramUpdates1, TLRPC.Updates paramUpdates2)
            {
              return AndroidUtilities.compare(paramUpdates1.pts, paramUpdates2.pts);
            }
          });
          break;
          if (paramInt1 == 1)
          {
            MessagesStorage.lastPtsValue = localUpdates.pts;
            break label65;
          }
          MessagesStorage.lastQtsValue = localUpdates.pts;
          break label65;
          label198: if (i == 1)
          {
            if ((getUpdatesStartTime(paramInt1) != 0L) && ((paramInt2 != 0) || (Math.abs(System.currentTimeMillis() - getUpdatesStartTime(paramInt1)) <= 1500L)))
            {
              FileLog.e("HOLE IN UPDATES QUEUE - will wait more time");
              if (paramInt2 != 0)
                setUpdatesStartTime(paramInt1, System.currentTimeMillis());
              return;
            }
            FileLog.e("HOLE IN UPDATES QUEUE - getDifference");
            setUpdatesStartTime(paramInt1, 0L);
            localArrayList.clear();
            getDifference();
            return;
          }
          localArrayList.remove(0);
        }
        label287: localArrayList.clear();
        FileLog.e("UPDATES QUEUE PROCEED - OK");
      }
      setUpdatesStartTime(paramInt1, 0L);
      return;
      label305: localArrayList = null;
    }
  }

  private void reloadDialogsReadValue(ArrayList<TLRPC.TL_dialog> paramArrayList, long paramLong)
  {
    if ((paramLong == 0L) && ((paramArrayList == null) || (paramArrayList.isEmpty())));
    TLRPC.TL_messages_getPeerDialogs localTL_messages_getPeerDialogs;
    do
      while (true)
      {
        return;
        localTL_messages_getPeerDialogs = new TLRPC.TL_messages_getPeerDialogs();
        if (paramArrayList != null)
        {
          int i = 0;
          if (i >= paramArrayList.size())
            break;
          TLRPC.InputPeer localInputPeer = getInputPeer((int)((TLRPC.TL_dialog)paramArrayList.get(i)).id);
          if (((localInputPeer instanceof TLRPC.TL_inputPeerChannel)) && (localInputPeer.access_hash == 0L));
          while (true)
          {
            i += 1;
            break;
            localTL_messages_getPeerDialogs.peers.add(localInputPeer);
          }
        }
        else
        {
          paramArrayList = getInputPeer((int)paramLong);
          if (((paramArrayList instanceof TLRPC.TL_inputPeerChannel)) && (paramArrayList.access_hash == 0L))
            continue;
          localTL_messages_getPeerDialogs.peers.add(paramArrayList);
        }
      }
    while (localTL_messages_getPeerDialogs.peers.isEmpty());
    ConnectionsManager.getInstance().sendRequest(localTL_messages_getPeerDialogs, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        if (paramTLObject != null)
        {
          TLRPC.TL_messages_peerDialogs localTL_messages_peerDialogs = (TLRPC.TL_messages_peerDialogs)paramTLObject;
          ArrayList localArrayList = new ArrayList();
          int i = 0;
          if (i < localTL_messages_peerDialogs.dialogs.size())
          {
            TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)localTL_messages_peerDialogs.dialogs.get(i);
            if (localTL_dialog.read_inbox_max_id == 0)
              localTL_dialog.read_inbox_max_id = 1;
            if (localTL_dialog.read_outbox_max_id == 0)
              localTL_dialog.read_outbox_max_id = 1;
            if ((localTL_dialog.id == 0L) && (localTL_dialog.peer != null))
            {
              if (localTL_dialog.peer.user_id != 0)
                localTL_dialog.id = localTL_dialog.peer.user_id;
            }
            else
            {
              label118: paramTL_error = (Integer)MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(localTL_dialog.id));
              paramTLObject = paramTL_error;
              if (paramTL_error == null)
                paramTLObject = Integer.valueOf(0);
              MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(localTL_dialog.id), Integer.valueOf(Math.max(localTL_dialog.read_inbox_max_id, paramTLObject.intValue())));
              if (paramTLObject.intValue() == 0)
              {
                if (localTL_dialog.peer.channel_id == 0)
                  break label425;
                paramTLObject = new TLRPC.TL_updateReadChannelInbox();
                paramTLObject.channel_id = localTL_dialog.peer.channel_id;
                paramTLObject.max_id = localTL_dialog.read_inbox_max_id;
                localArrayList.add(paramTLObject);
              }
              label239: paramTL_error = (Integer)MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(localTL_dialog.id));
              paramTLObject = paramTL_error;
              if (paramTL_error == null)
                paramTLObject = Integer.valueOf(0);
              MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(localTL_dialog.id), Integer.valueOf(Math.max(localTL_dialog.read_outbox_max_id, paramTLObject.intValue())));
              if (paramTLObject.intValue() == 0)
              {
                if (localTL_dialog.peer.channel_id == 0)
                  break label461;
                paramTLObject = new TLRPC.TL_updateReadChannelOutbox();
                paramTLObject.channel_id = localTL_dialog.peer.channel_id;
                paramTLObject.max_id = localTL_dialog.read_outbox_max_id;
                localArrayList.add(paramTLObject);
              }
            }
            while (true)
            {
              i += 1;
              break;
              if (localTL_dialog.peer.chat_id != 0)
              {
                localTL_dialog.id = (-localTL_dialog.peer.chat_id);
                break label118;
              }
              if (localTL_dialog.peer.channel_id == 0)
                break label118;
              localTL_dialog.id = (-localTL_dialog.peer.channel_id);
              break label118;
              label425: paramTLObject = new TLRPC.TL_updateReadHistoryInbox();
              paramTLObject.peer = localTL_dialog.peer;
              paramTLObject.max_id = localTL_dialog.read_inbox_max_id;
              localArrayList.add(paramTLObject);
              break label239;
              label461: paramTLObject = new TLRPC.TL_updateReadHistoryOutbox();
              paramTLObject.peer = localTL_dialog.peer;
              paramTLObject.max_id = localTL_dialog.read_outbox_max_id;
              localArrayList.add(paramTLObject);
            }
          }
          if (!localArrayList.isEmpty())
            MessagesController.this.processUpdateArray(localArrayList, null, null, false);
        }
      }
    });
  }

  private void reloadMessages(ArrayList<Integer> paramArrayList, long paramLong)
  {
    if (paramArrayList.isEmpty());
    ArrayList localArrayList2;
    TLRPC.Chat localChat;
    Object localObject;
    ArrayList localArrayList1;
    label76: 
    do
    {
      return;
      localArrayList2 = new ArrayList();
      localChat = ChatObject.getChatByDialog(paramLong);
      int i;
      Integer localInteger;
      if (ChatObject.isChannel(localChat))
      {
        localObject = new TLRPC.TL_channels_getMessages();
        ((TLRPC.TL_channels_getMessages)localObject).channel = getInputChannel(localChat);
        ((TLRPC.TL_channels_getMessages)localObject).id = localArrayList2;
        localArrayList1 = (ArrayList)this.reloadingMessages.get(Long.valueOf(paramLong));
        i = 0;
        if (i >= paramArrayList.size())
          continue;
        localInteger = (Integer)paramArrayList.get(i);
        if ((localArrayList1 == null) || (!localArrayList1.contains(localInteger)))
          break label139;
      }
      while (true)
      {
        i += 1;
        break label76;
        localObject = new TLRPC.TL_messages_getMessages();
        ((TLRPC.TL_messages_getMessages)localObject).id = localArrayList2;
        break;
        localArrayList2.add(localInteger);
      }
    }
    while (localArrayList2.isEmpty());
    label139: paramArrayList = localArrayList1;
    if (localArrayList1 == null)
    {
      paramArrayList = new ArrayList();
      this.reloadingMessages.put(Long.valueOf(paramLong), paramArrayList);
    }
    paramArrayList.addAll(localArrayList2);
    ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate(paramLong, localChat, localArrayList2)
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        if (paramTL_error == null)
        {
          TLRPC.messages_Messages localmessages_Messages = (TLRPC.messages_Messages)paramTLObject;
          HashMap localHashMap1 = new HashMap();
          int i = 0;
          while (i < localmessages_Messages.users.size())
          {
            paramTLObject = (TLRPC.User)localmessages_Messages.users.get(i);
            localHashMap1.put(Integer.valueOf(paramTLObject.id), paramTLObject);
            i += 1;
          }
          HashMap localHashMap2 = new HashMap();
          i = 0;
          while (i < localmessages_Messages.chats.size())
          {
            paramTLObject = (TLRPC.Chat)localmessages_Messages.chats.get(i);
            localHashMap2.put(Integer.valueOf(paramTLObject.id), paramTLObject);
            i += 1;
          }
          paramTL_error = (Integer)MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(this.val$dialog_id));
          paramTLObject = paramTL_error;
          if (paramTL_error == null)
          {
            paramTLObject = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(false, this.val$dialog_id));
            MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(this.val$dialog_id), paramTLObject);
          }
          Object localObject = (Integer)MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(this.val$dialog_id));
          paramTL_error = (TLRPC.TL_error)localObject;
          if (localObject == null)
          {
            paramTL_error = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, this.val$dialog_id));
            MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(this.val$dialog_id), paramTL_error);
          }
          ArrayList localArrayList = new ArrayList();
          i = 0;
          if (i < localmessages_Messages.messages.size())
          {
            TLRPC.Message localMessage = (TLRPC.Message)localmessages_Messages.messages.get(i);
            if ((this.val$chat != null) && (this.val$chat.megagroup))
              localMessage.flags |= -2147483648;
            localMessage.dialog_id = this.val$dialog_id;
            if (localMessage.out)
            {
              localObject = paramTL_error;
              label336: if (((Integer)localObject).intValue() >= localMessage.id)
                break label392;
            }
            label392: for (boolean bool = true; ; bool = false)
            {
              localMessage.unread = bool;
              localArrayList.add(new MessageObject(localMessage, localHashMap1, localHashMap2, true));
              i += 1;
              break;
              localObject = paramTLObject;
              break label336;
            }
          }
          ImageLoader.saveMessagesThumbs(localmessages_Messages.messages);
          MessagesStorage.getInstance().putMessages(localmessages_Messages, this.val$dialog_id, -1, 0, false);
          AndroidUtilities.runOnUIThread(new Runnable(localArrayList)
          {
            public void run()
            {
              Object localObject = (ArrayList)MessagesController.this.reloadingMessages.get(Long.valueOf(MessagesController.12.this.val$dialog_id));
              if (localObject != null)
              {
                ((ArrayList)localObject).removeAll(MessagesController.12.this.val$result);
                if (((ArrayList)localObject).isEmpty())
                  MessagesController.this.reloadingMessages.remove(Long.valueOf(MessagesController.12.this.val$dialog_id));
              }
              localObject = (MessageObject)MessagesController.this.dialogMessage.get(Long.valueOf(MessagesController.12.this.val$dialog_id));
              int i;
              if (localObject != null)
                i = 0;
              while (true)
              {
                if (i < this.val$objects.size())
                {
                  MessageObject localMessageObject = (MessageObject)this.val$objects.get(i);
                  if ((localObject != null) && (((MessageObject)localObject).getId() == localMessageObject.getId()))
                  {
                    MessagesController.this.dialogMessage.put(Long.valueOf(MessagesController.12.this.val$dialog_id), localMessageObject);
                    if (localMessageObject.messageOwner.to_id.channel_id == 0)
                    {
                      localObject = (MessageObject)MessagesController.this.dialogMessagesByIds.remove(Integer.valueOf(localMessageObject.getId()));
                      if (localObject != null)
                        MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(((MessageObject)localObject).getId()), localObject);
                    }
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                  }
                }
                else
                {
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.replaceMessagesObjects, new Object[] { Long.valueOf(MessagesController.12.this.val$dialog_id), this.val$objects });
                  return;
                }
                i += 1;
              }
            }
          });
        }
      }
    });
  }

  private void setUpdatesStartTime(int paramInt, long paramLong)
  {
    if (paramInt == 0)
      this.updatesStartWaitTimeSeq = paramLong;
    do
    {
      return;
      if (paramInt != 1)
        continue;
      this.updatesStartWaitTimePts = paramLong;
      return;
    }
    while (paramInt != 2);
    this.updatesStartWaitTimeQts = paramLong;
  }

  private static void showCantOpenAlert(BaseFragment paramBaseFragment, String paramString)
  {
    if ((paramBaseFragment == null) || (paramBaseFragment.getParentActivity() == null))
      return;
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramBaseFragment.getParentActivity());
    localBuilder.setTitle(LocaleController.getString("AppName", 2131165319));
    localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166153), null);
    localBuilder.setMessage(paramString);
    paramBaseFragment.showDialog(localBuilder.create());
  }

  private void updatePrintingStrings()
  {
    HashMap localHashMap1 = new HashMap();
    HashMap localHashMap2 = new HashMap();
    new ArrayList(this.printingUsers.keySet());
    Iterator localIterator1 = this.printingUsers.entrySet().iterator();
    label771: label1062: label1065: label1068: label1069: 
    while (true)
    {
      Object localObject1;
      long l;
      Object localObject2;
      int i;
      if (localIterator1.hasNext())
      {
        localObject1 = (Map.Entry)localIterator1.next();
        l = ((Long)((Map.Entry)localObject1).getKey()).longValue();
        localObject2 = (ArrayList)((Map.Entry)localObject1).getValue();
        i = (int)l;
        if ((i > 0) || (i == 0) || (((ArrayList)localObject2).size() == 1))
        {
          localObject1 = (PrintingUser)((ArrayList)localObject2).get(0);
          localObject2 = getUser(Integer.valueOf(((PrintingUser)localObject1).userId));
          if (localObject2 == null)
            return;
          if ((((PrintingUser)localObject1).action instanceof TLRPC.TL_sendMessageRecordAudioAction))
          {
            if (i < 0)
              localHashMap1.put(Long.valueOf(l), LocaleController.formatString("IsRecordingAudio", 2131165851, new Object[] { getUserNameForTyping((TLRPC.User)localObject2) }));
            while (true)
            {
              localHashMap2.put(Long.valueOf(l), Integer.valueOf(1));
              break;
              localHashMap1.put(Long.valueOf(l), LocaleController.getString("RecordingAudio", 2131166316));
            }
          }
          if ((((PrintingUser)localObject1).action instanceof TLRPC.TL_sendMessageUploadAudioAction))
          {
            if (i < 0)
              localHashMap1.put(Long.valueOf(l), LocaleController.formatString("IsSendingAudio", 2131165852, new Object[] { getUserNameForTyping((TLRPC.User)localObject2) }));
            while (true)
            {
              localHashMap2.put(Long.valueOf(l), Integer.valueOf(2));
              break;
              localHashMap1.put(Long.valueOf(l), LocaleController.getString("SendingAudio", 2131166423));
            }
          }
          if (((((PrintingUser)localObject1).action instanceof TLRPC.TL_sendMessageUploadVideoAction)) || ((((PrintingUser)localObject1).action instanceof TLRPC.TL_sendMessageRecordVideoAction)))
          {
            if (i < 0)
              localHashMap1.put(Long.valueOf(l), LocaleController.formatString("IsSendingVideo", 2131165856, new Object[] { getUserNameForTyping((TLRPC.User)localObject2) }));
            while (true)
            {
              localHashMap2.put(Long.valueOf(l), Integer.valueOf(2));
              break;
              localHashMap1.put(Long.valueOf(l), LocaleController.getString("SendingVideoStatus", 2131166429));
            }
          }
          if ((((PrintingUser)localObject1).action instanceof TLRPC.TL_sendMessageUploadDocumentAction))
          {
            if (i < 0)
              localHashMap1.put(Long.valueOf(l), LocaleController.formatString("IsSendingFile", 2131165853, new Object[] { getUserNameForTyping((TLRPC.User)localObject2) }));
            while (true)
            {
              localHashMap2.put(Long.valueOf(l), Integer.valueOf(2));
              break;
              localHashMap1.put(Long.valueOf(l), LocaleController.getString("SendingFile", 2131166424));
            }
          }
          if ((((PrintingUser)localObject1).action instanceof TLRPC.TL_sendMessageUploadPhotoAction))
          {
            if (i < 0)
              localHashMap1.put(Long.valueOf(l), LocaleController.formatString("IsSendingPhoto", 2131165855, new Object[] { getUserNameForTyping((TLRPC.User)localObject2) }));
            while (true)
            {
              localHashMap2.put(Long.valueOf(l), Integer.valueOf(2));
              break;
              localHashMap1.put(Long.valueOf(l), LocaleController.getString("SendingPhoto", 2131166427));
            }
          }
          if ((((PrintingUser)localObject1).action instanceof TLRPC.TL_sendMessageGamePlayAction))
          {
            if (i < 0)
              localHashMap1.put(Long.valueOf(l), LocaleController.formatString("IsSendingGame", 2131165854, new Object[] { getUserNameForTyping((TLRPC.User)localObject2) }));
            while (true)
            {
              localHashMap2.put(Long.valueOf(l), Integer.valueOf(3));
              break;
              localHashMap1.put(Long.valueOf(l), LocaleController.getString("SendingGame", 2131166425));
            }
          }
          if (i < 0)
            localHashMap1.put(Long.valueOf(l), String.format("%s %s", new Object[] { getUserNameForTyping((TLRPC.User)localObject2), LocaleController.getString("IsTyping", 2131165857) }));
          while (true)
          {
            localHashMap2.put(Long.valueOf(l), Integer.valueOf(0));
            break;
            localHashMap1.put(Long.valueOf(l), LocaleController.getString("Typing", 2131166529));
          }
        }
        localObject1 = "";
        Iterator localIterator2 = ((ArrayList)localObject2).iterator();
        i = 0;
        if (!localIterator2.hasNext())
          break label1068;
        TLRPC.User localUser = getUser(Integer.valueOf(((PrintingUser)localIterator2.next()).userId));
        if (localUser == null)
          break label1065;
        if (((String)localObject1).length() == 0)
          break label1062;
        localObject1 = (String)localObject1 + ", ";
        label839: localObject1 = (String)localObject1 + getUserNameForTyping(localUser);
        i += 1;
        label869: if (i != 2);
      }
      while (true)
      {
        if (((String)localObject1).length() == 0)
          break label1069;
        if (i == 1)
          localHashMap1.put(Long.valueOf(l), String.format("%s %s", new Object[] { localObject1, LocaleController.getString("IsTyping", 2131165857) }));
        while (true)
        {
          localHashMap2.put(Long.valueOf(l), Integer.valueOf(0));
          break;
          break label771;
          if (((ArrayList)localObject2).size() > 2)
          {
            localHashMap1.put(Long.valueOf(l), String.format("%s %s", new Object[] { localObject1, LocaleController.formatPluralString("AndMoreTyping", ((ArrayList)localObject2).size() - 2) }));
            continue;
          }
          localHashMap1.put(Long.valueOf(l), String.format("%s %s", new Object[] { localObject1, LocaleController.getString("AreTyping", 2131165334) }));
        }
        this.lastPrintingStringCount = localHashMap1.size();
        AndroidUtilities.runOnUIThread(new Runnable(localHashMap1, localHashMap2)
        {
          public void run()
          {
            MessagesController.this.printingStrings = this.val$newPrintingStrings;
            MessagesController.this.printingStringsTypes = this.val$newPrintingStringsTypes;
          }
        });
        return;
        break label839;
        break label869;
      }
    }
  }

  private boolean updatePrintingUsersWithNewMessages(long paramLong, ArrayList<MessageObject> paramArrayList)
  {
    int j;
    label141: int k;
    if (paramLong > 0L)
    {
      if ((ArrayList)this.printingUsers.get(Long.valueOf(paramLong)) != null)
      {
        this.printingUsers.remove(Long.valueOf(paramLong));
        return true;
      }
    }
    else if (paramLong < 0L)
    {
      ArrayList localArrayList = new ArrayList();
      paramArrayList = paramArrayList.iterator();
      while (paramArrayList.hasNext())
      {
        MessageObject localMessageObject = (MessageObject)paramArrayList.next();
        if (localArrayList.contains(Integer.valueOf(localMessageObject.messageOwner.from_id)))
          continue;
        localArrayList.add(Integer.valueOf(localMessageObject.messageOwner.from_id));
      }
      paramArrayList = (ArrayList)this.printingUsers.get(Long.valueOf(paramLong));
      if (paramArrayList != null)
      {
        j = 0;
        int i = 0;
        k = i;
        if (j >= paramArrayList.size())
          break label224;
        if (!localArrayList.contains(Integer.valueOf(((PrintingUser)paramArrayList.get(j)).userId)))
          break label231;
        paramArrayList.remove(j);
        if (paramArrayList.isEmpty())
          this.printingUsers.remove(Long.valueOf(paramLong));
        j -= 1;
        i = 1;
      }
    }
    label224: label231: 
    while (true)
    {
      j += 1;
      break label141;
      k = 0;
      if (k != 0)
        break;
      return false;
    }
  }

  public void addSupportUser()
  {
    TLRPC.TL_userForeign_old2 localTL_userForeign_old2 = new TLRPC.TL_userForeign_old2();
    localTL_userForeign_old2.phone = "333";
    localTL_userForeign_old2.id = 333000;
    localTL_userForeign_old2.first_name = "Telegram";
    localTL_userForeign_old2.last_name = "";
    localTL_userForeign_old2.status = null;
    localTL_userForeign_old2.photo = new TLRPC.TL_userProfilePhotoEmpty();
    putUser(localTL_userForeign_old2, true);
    localTL_userForeign_old2 = new TLRPC.TL_userForeign_old2();
    localTL_userForeign_old2.phone = "42777";
    localTL_userForeign_old2.id = 777000;
    localTL_userForeign_old2.first_name = "Telegram";
    localTL_userForeign_old2.last_name = "Notifications";
    localTL_userForeign_old2.status = null;
    localTL_userForeign_old2.photo = new TLRPC.TL_userProfilePhotoEmpty();
    putUser(localTL_userForeign_old2, true);
  }

  public void addToViewsQueue(TLRPC.Message paramMessage, boolean paramBoolean)
  {
    ArrayList localArrayList = new ArrayList();
    long l2 = paramMessage.id;
    long l1 = l2;
    if (paramMessage.to_id.channel_id != 0)
      l1 = l2 | paramMessage.to_id.channel_id << 32;
    localArrayList.add(Long.valueOf(l1));
    MessagesStorage.getInstance().markMessagesContentAsRead(localArrayList);
    Utilities.stageQueue.postRunnable(new Runnable(paramMessage)
    {
      public void run()
      {
        SparseArray localSparseArray = MessagesController.this.channelViewsToSend;
        int i;
        if (this.val$message.to_id.channel_id != 0)
          i = -this.val$message.to_id.channel_id;
        while (true)
        {
          ArrayList localArrayList2 = (ArrayList)localSparseArray.get(i);
          ArrayList localArrayList1 = localArrayList2;
          if (localArrayList2 == null)
          {
            localArrayList1 = new ArrayList();
            localSparseArray.put(i, localArrayList1);
          }
          if (!localArrayList1.contains(Integer.valueOf(this.val$message.id)))
            localArrayList1.add(Integer.valueOf(this.val$message.id));
          return;
          if (this.val$message.to_id.chat_id != 0)
          {
            i = -this.val$message.to_id.chat_id;
            continue;
          }
          i = this.val$message.to_id.user_id;
        }
      }
    });
  }

  public void addUserToChat(int paramInt1, TLRPC.User paramUser, TLRPC.ChatFull paramChatFull, int paramInt2, String paramString, BaseFragment paramBaseFragment)
  {
    boolean bool1 = true;
    if (paramUser == null);
    label146: label227: label255: 
    do
    {
      boolean bool2;
      TLRPC.InputUser localInputUser;
      while (true)
      {
        return;
        if (paramInt1 <= 0)
          break label324;
        bool2 = ChatObject.isChannel(paramInt1);
        if ((!bool2) || (!getChat(Integer.valueOf(paramInt1)).megagroup))
          break;
        localInputUser = getInputUser(paramUser);
        if ((paramString != null) && ((!bool2) || (bool1)))
          break label255;
        if (!bool2)
          break label227;
        if (!(localInputUser instanceof TLRPC.TL_inputUserSelf))
          break label146;
        if (this.joiningToChannels.contains(Integer.valueOf(paramInt1)))
          continue;
        paramUser = new TLRPC.TL_channels_joinChannel();
        paramUser.channel = getInputChannel(paramInt1);
        this.joiningToChannels.add(Integer.valueOf(paramInt1));
      }
      while (true)
      {
        ConnectionsManager.getInstance().sendRequest(paramUser, new RequestDelegate(bool2, localInputUser, paramInt1, paramBaseFragment, paramUser, bool1)
        {
          public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
          {
            if ((this.val$isChannel) && ((this.val$inputUser instanceof TLRPC.TL_inputUserSelf)))
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  MessagesController.this.joiningToChannels.remove(Integer.valueOf(MessagesController.78.this.val$chat_id));
                }
              });
            if (paramTL_error != null)
            {
              AndroidUtilities.runOnUIThread(new Runnable(paramTL_error)
              {
                public void run()
                {
                  boolean bool = true;
                  TLRPC.TL_error localTL_error = this.val$error;
                  BaseFragment localBaseFragment = MessagesController.78.this.val$fragment;
                  TLObject localTLObject = MessagesController.78.this.val$request;
                  if ((MessagesController.78.this.val$isChannel) && (!MessagesController.78.this.val$isMegagroup));
                  while (true)
                  {
                    AlertsCreator.processError(localTL_error, localBaseFragment, localTLObject, new Object[] { Boolean.valueOf(bool) });
                    return;
                    bool = false;
                  }
                }
              });
              return;
            }
            paramTLObject = (TLRPC.Updates)paramTLObject;
            int i = 0;
            label52: if (i < paramTLObject.updates.size())
            {
              paramTL_error = (TLRPC.Update)paramTLObject.updates.get(i);
              if ((!(paramTL_error instanceof TLRPC.TL_updateNewChannelMessage)) || (!(((TLRPC.TL_updateNewChannelMessage)paramTL_error).message.action instanceof TLRPC.TL_messageActionChatAddUser)));
            }
            for (i = 1; ; i = 0)
            {
              MessagesController.this.processUpdates(paramTLObject, false);
              if (this.val$isChannel)
              {
                if ((i == 0) && ((this.val$inputUser instanceof TLRPC.TL_inputUserSelf)))
                  MessagesController.this.generateJoinMessage(this.val$chat_id, true);
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    MessagesController.this.loadFullChat(MessagesController.78.this.val$chat_id, 0, true);
                  }
                }
                , 1000L);
              }
              if ((!this.val$isChannel) || (!(this.val$inputUser instanceof TLRPC.TL_inputUserSelf)))
                break;
              MessagesStorage.getInstance().updateDialogsWithDeletedMessages(new ArrayList(), null, true, this.val$chat_id);
              return;
              i += 1;
              break label52;
            }
          }
        });
        return;
        bool1 = false;
        break;
        if ((paramUser.bot) && (!bool1))
        {
          paramChatFull = new TLRPC.TL_channels_editAdmin();
          paramChatFull.channel = getInputChannel(paramInt1);
          paramChatFull.user_id = getInputUser(paramUser);
          paramChatFull.role = new TLRPC.TL_channelRoleEditor();
          paramUser = paramChatFull;
          continue;
        }
        paramUser = new TLRPC.TL_channels_inviteToChannel();
        paramUser.channel = getInputChannel(paramInt1);
        paramUser.users.add(localInputUser);
        continue;
        paramUser = new TLRPC.TL_messages_addChatUser();
        paramUser.chat_id = paramInt1;
        paramUser.fwd_limit = paramInt2;
        paramUser.user_id = localInputUser;
      }
      paramUser = new TLRPC.TL_messages_startBot();
      paramUser.bot = localInputUser;
      if (bool2)
        paramUser.peer = getInputPeer(-paramInt1);
      while (true)
      {
        paramUser.start_param = paramString;
        paramUser.random_id = Utilities.random.nextLong();
        break;
        paramUser.peer = new TLRPC.TL_inputPeerChat();
        paramUser.peer.chat_id = paramInt1;
      }
    }
    while (!(paramChatFull instanceof TLRPC.TL_chatFull));
    label324: paramInt2 = 0;
    while (true)
    {
      if (paramInt2 >= paramChatFull.participants.participants.size())
        break label383;
      if (((TLRPC.ChatParticipant)paramChatFull.participants.participants.get(paramInt2)).user_id == paramUser.id)
        break;
      paramInt2 += 1;
    }
    label383: paramString = getChat(Integer.valueOf(paramInt1));
    paramString.participants_count += 1;
    paramBaseFragment = new ArrayList();
    paramBaseFragment.add(paramString);
    MessagesStorage.getInstance().putUsersAndChats(null, paramBaseFragment, true, true);
    paramString = new TLRPC.TL_chatParticipant();
    paramString.user_id = paramUser.id;
    paramString.inviter_id = UserConfig.getClientUserId();
    paramString.date = ConnectionsManager.getInstance().getCurrentTime();
    paramChatFull.participants.participants.add(0, paramString);
    MessagesStorage.getInstance().updateChatInfo(paramChatFull, true);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatInfoDidLoaded, new Object[] { paramChatFull, Integer.valueOf(0), Boolean.valueOf(false), null });
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(32) });
  }

  public void addUsersToChannel(int paramInt, ArrayList<TLRPC.InputUser> paramArrayList, BaseFragment paramBaseFragment)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty()))
      return;
    TLRPC.TL_channels_inviteToChannel localTL_channels_inviteToChannel = new TLRPC.TL_channels_inviteToChannel();
    localTL_channels_inviteToChannel.channel = getInputChannel(paramInt);
    localTL_channels_inviteToChannel.users = paramArrayList;
    ConnectionsManager.getInstance().sendRequest(localTL_channels_inviteToChannel, new RequestDelegate(paramBaseFragment, localTL_channels_inviteToChannel)
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        if (paramTL_error != null)
        {
          AndroidUtilities.runOnUIThread(new Runnable(paramTL_error)
          {
            public void run()
            {
              AlertsCreator.processError(this.val$error, MessagesController.70.this.val$fragment, MessagesController.70.this.val$req, new Object[] { Boolean.valueOf(true) });
            }
          });
          return;
        }
        MessagesController.this.processUpdates((TLRPC.Updates)paramTLObject, false);
      }
    });
  }

  public void blockUser(int paramInt)
  {
    TLRPC.User localUser = getUser(Integer.valueOf(paramInt));
    if ((localUser == null) || (this.blockedUsers.contains(Integer.valueOf(paramInt))))
      return;
    this.blockedUsers.add(Integer.valueOf(paramInt));
    if (localUser.bot)
      SearchQuery.removeInline(paramInt);
    while (true)
    {
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
      TLRPC.TL_contacts_block localTL_contacts_block = new TLRPC.TL_contacts_block();
      localTL_contacts_block.id = getInputUser(localUser);
      ConnectionsManager.getInstance().sendRequest(localTL_contacts_block, new RequestDelegate(localUser)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          if (paramTL_error == null)
          {
            paramTLObject = new ArrayList();
            paramTLObject.add(Integer.valueOf(this.val$user.id));
            MessagesStorage.getInstance().putBlockedUsers(paramTLObject, false);
          }
        }
      });
      return;
      SearchQuery.removePeer(paramInt);
    }
  }

  public boolean canPinDialog(boolean paramBoolean)
  {
    boolean bool = false;
    int i = 0;
    int j = 0;
    if (i < this.dialogs.size())
    {
      TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)this.dialogs.get(i);
      int m = (int)localTL_dialog.id;
      int k;
      if (paramBoolean)
      {
        k = j;
        if (m != 0);
      }
      else
      {
        if ((paramBoolean) || (m != 0))
          break label73;
        k = j;
      }
      while (true)
      {
        i += 1;
        j = k;
        break;
        label73: k = j;
        if (!localTL_dialog.pinned)
          continue;
        k = j + 1;
      }
    }
    paramBoolean = bool;
    if (j < this.maxPinnedDialogsCount)
      paramBoolean = true;
    return paramBoolean;
  }

  public void cancelLoadFullChat(int paramInt)
  {
    this.loadingFullChats.remove(Integer.valueOf(paramInt));
  }

  public void cancelLoadFullUser(int paramInt)
  {
    this.loadingFullUsers.remove(Integer.valueOf(paramInt));
  }

  public void cancelTyping(int paramInt, long paramLong)
  {
    HashMap localHashMap = (HashMap)this.sendingTypings.get(Integer.valueOf(paramInt));
    if (localHashMap != null)
      localHashMap.remove(Long.valueOf(paramLong));
  }

  public void changeChatAvatar(int paramInt, TLRPC.InputFile paramInputFile)
  {
    Object localObject;
    if (ChatObject.isChannel(paramInt))
    {
      localObject = new TLRPC.TL_channels_editPhoto();
      ((TLRPC.TL_channels_editPhoto)localObject).channel = getInputChannel(paramInt);
      if (paramInputFile != null)
      {
        ((TLRPC.TL_channels_editPhoto)localObject).photo = new TLRPC.TL_inputChatUploadedPhoto();
        ((TLRPC.TL_channels_editPhoto)localObject).photo.file = paramInputFile;
        paramInputFile = (TLRPC.InputFile)localObject;
      }
    }
    while (true)
    {
      ConnectionsManager.getInstance().sendRequest(paramInputFile, new RequestDelegate()
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          if (paramTL_error != null)
            return;
          MessagesController.this.processUpdates((TLRPC.Updates)paramTLObject, false);
        }
      }
      , 64);
      return;
      ((TLRPC.TL_channels_editPhoto)localObject).photo = new TLRPC.TL_inputChatPhotoEmpty();
      paramInputFile = (TLRPC.InputFile)localObject;
      continue;
      localObject = new TLRPC.TL_messages_editChatPhoto();
      ((TLRPC.TL_messages_editChatPhoto)localObject).chat_id = paramInt;
      if (paramInputFile != null)
      {
        ((TLRPC.TL_messages_editChatPhoto)localObject).photo = new TLRPC.TL_inputChatUploadedPhoto();
        ((TLRPC.TL_messages_editChatPhoto)localObject).photo.file = paramInputFile;
        paramInputFile = (TLRPC.InputFile)localObject;
        continue;
      }
      ((TLRPC.TL_messages_editChatPhoto)localObject).photo = new TLRPC.TL_inputChatPhotoEmpty();
      paramInputFile = (TLRPC.InputFile)localObject;
    }
  }

  public void changeChatTitle(int paramInt, String paramString)
  {
    if (paramInt > 0)
    {
      if (ChatObject.isChannel(paramInt))
      {
        localObject = new TLRPC.TL_channels_editTitle();
        ((TLRPC.TL_channels_editTitle)localObject).channel = getInputChannel(paramInt);
        ((TLRPC.TL_channels_editTitle)localObject).title = paramString;
      }
      for (paramString = (String)localObject; ; paramString = (String)localObject)
      {
        ConnectionsManager.getInstance().sendRequest(paramString, new RequestDelegate()
        {
          public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
          {
            if (paramTL_error != null)
              return;
            MessagesController.this.processUpdates((TLRPC.Updates)paramTLObject, false);
          }
        }
        , 64);
        return;
        localObject = new TLRPC.TL_messages_editChatTitle();
        ((TLRPC.TL_messages_editChatTitle)localObject).chat_id = paramInt;
        ((TLRPC.TL_messages_editChatTitle)localObject).title = paramString;
      }
    }
    Object localObject = getChat(Integer.valueOf(paramInt));
    ((TLRPC.Chat)localObject).title = paramString;
    paramString = new ArrayList();
    paramString.add(localObject);
    MessagesStorage.getInstance().putUsersAndChats(null, paramString, true, true);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(16) });
  }

  public void checkChannelInviter(int paramInt)
  {
    AndroidUtilities.runOnUIThread(new Runnable(paramInt)
    {
      public void run()
      {
        TLRPC.Chat localChat = MessagesController.this.getChat(Integer.valueOf(this.val$chat_id));
        if ((localChat == null) || (!ChatObject.isChannel(this.val$chat_id)) || (localChat.creator))
          return;
        TLRPC.TL_channels_getParticipant localTL_channels_getParticipant = new TLRPC.TL_channels_getParticipant();
        localTL_channels_getParticipant.channel = MessagesController.getInputChannel(this.val$chat_id);
        localTL_channels_getParticipant.user_id = new TLRPC.TL_inputUserSelf();
        ConnectionsManager.getInstance().sendRequest(localTL_channels_getParticipant, new RequestDelegate(localChat)
        {
          public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
          {
            paramTLObject = (TLRPC.TL_channels_channelParticipant)paramTLObject;
            if ((paramTLObject == null) || (!(paramTLObject.participant instanceof TLRPC.TL_channelParticipantSelf)) || (paramTLObject.participant.inviter_id == UserConfig.getClientUserId()) || ((this.val$chat.megagroup) && (MessagesStorage.getInstance().isMigratedChat(this.val$chat.id))))
              return;
            AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
            {
              public void run()
              {
                MessagesController.this.putUsers(this.val$res.users, false);
              }
            });
            MessagesStorage.getInstance().putUsersAndChats(paramTLObject.users, null, true, true);
            paramTL_error = new TLRPC.TL_messageService();
            paramTL_error.media_unread = true;
            paramTL_error.unread = true;
            paramTL_error.flags = 256;
            paramTL_error.post = true;
            if (this.val$chat.megagroup)
              paramTL_error.flags |= -2147483648;
            int i = UserConfig.getNewMessageId();
            paramTL_error.id = i;
            paramTL_error.local_id = i;
            paramTL_error.date = paramTLObject.participant.date;
            paramTL_error.action = new TLRPC.TL_messageActionChatAddUser();
            paramTL_error.from_id = paramTLObject.participant.inviter_id;
            paramTL_error.action.users.add(Integer.valueOf(UserConfig.getClientUserId()));
            paramTL_error.to_id = new TLRPC.TL_peerChannel();
            paramTL_error.to_id.channel_id = MessagesController.99.this.val$chat_id;
            paramTL_error.dialog_id = (-MessagesController.99.this.val$chat_id);
            UserConfig.saveConfig(false);
            ArrayList localArrayList1 = new ArrayList();
            ArrayList localArrayList2 = new ArrayList();
            ConcurrentHashMap localConcurrentHashMap = new ConcurrentHashMap();
            i = 0;
            while (i < paramTLObject.users.size())
            {
              TLRPC.User localUser = (TLRPC.User)paramTLObject.users.get(i);
              localConcurrentHashMap.put(Integer.valueOf(localUser.id), localUser);
              i += 1;
            }
            localArrayList2.add(paramTL_error);
            localArrayList1.add(new MessageObject(paramTL_error, localConcurrentHashMap, true));
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(localArrayList1)
            {
              public void run()
              {
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    NotificationsController.getInstance().processNewMessages(MessagesController.99.1.2.this.val$pushMessages, true);
                  }
                });
              }
            });
            MessagesStorage.getInstance().putMessages(localArrayList2, true, true, false, MediaController.getInstance().getAutodownloadMask());
            AndroidUtilities.runOnUIThread(new Runnable(localArrayList1)
            {
              public void run()
              {
                MessagesController.this.updateInterfaceWithMessages(-MessagesController.99.this.val$chat_id, this.val$pushMessages);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
              }
            });
          }
        });
      }
    });
  }

  // ERROR //
  protected void checkLastDialogMessage(TLRPC.TL_dialog paramTL_dialog, TLRPC.InputPeer paramInputPeer, long paramLong)
  {
    // Byte code:
    //   0: aload_1
    //   1: getfield 1337	org/vidogram/tgnet/TLRPC$TL_dialog:id	J
    //   4: l2i
    //   5: istore 5
    //   7: iload 5
    //   9: ifeq +18 -> 27
    //   12: aload_0
    //   13: getfield 685	org/vidogram/messenger/MessagesController:checkingLastMessagesDialogs	Ljava/util/HashMap;
    //   16: iload 5
    //   18: invokestatic 1246	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   21: invokevirtual 2246	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   24: ifeq +4 -> 28
    //   27: return
    //   28: new 2248	org/vidogram/tgnet/TLRPC$TL_messages_getHistory
    //   31: dup
    //   32: invokespecial 2249	org/vidogram/tgnet/TLRPC$TL_messages_getHistory:<init>	()V
    //   35: astore 8
    //   37: aload_2
    //   38: ifnonnull +205 -> 243
    //   41: iload 5
    //   43: invokestatic 1427	org/vidogram/messenger/MessagesController:getInputPeer	(I)Lorg/vidogram/tgnet/TLRPC$InputPeer;
    //   46: astore 6
    //   48: aload 8
    //   50: aload 6
    //   52: putfield 2250	org/vidogram/tgnet/TLRPC$TL_messages_getHistory:peer	Lorg/vidogram/tgnet/TLRPC$InputPeer;
    //   55: aload 8
    //   57: getfield 2250	org/vidogram/tgnet/TLRPC$TL_messages_getHistory:peer	Lorg/vidogram/tgnet/TLRPC$InputPeer;
    //   60: ifnull -33 -> 27
    //   63: aload 8
    //   65: iconst_1
    //   66: putfield 2251	org/vidogram/tgnet/TLRPC$TL_messages_getHistory:limit	I
    //   69: aload_0
    //   70: getfield 685	org/vidogram/messenger/MessagesController:checkingLastMessagesDialogs	Ljava/util/HashMap;
    //   73: iload 5
    //   75: invokestatic 1246	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   78: iconst_1
    //   79: invokestatic 1418	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   82: invokevirtual 1800	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   85: pop
    //   86: lload_3
    //   87: lconst_0
    //   88: lcmp
    //   89: ifne +172 -> 261
    //   92: new 2253	org/vidogram/tgnet/NativeByteBuffer
    //   95: dup
    //   96: aload_2
    //   97: invokevirtual 2256	org/vidogram/tgnet/TLRPC$InputPeer:getObjectSize	()I
    //   100: bipush 48
    //   102: iadd
    //   103: invokespecial 2258	org/vidogram/tgnet/NativeByteBuffer:<init>	(I)V
    //   106: astore 6
    //   108: aload 6
    //   110: iconst_5
    //   111: invokevirtual 2261	org/vidogram/tgnet/NativeByteBuffer:writeInt32	(I)V
    //   114: aload 6
    //   116: aload_1
    //   117: getfield 1337	org/vidogram/tgnet/TLRPC$TL_dialog:id	J
    //   120: invokevirtual 2264	org/vidogram/tgnet/NativeByteBuffer:writeInt64	(J)V
    //   123: aload 6
    //   125: aload_1
    //   126: getfield 1310	org/vidogram/tgnet/TLRPC$TL_dialog:top_message	I
    //   129: invokevirtual 2261	org/vidogram/tgnet/NativeByteBuffer:writeInt32	(I)V
    //   132: aload 6
    //   134: aload_1
    //   135: getfield 2267	org/vidogram/tgnet/TLRPC$TL_dialog:read_inbox_max_id	I
    //   138: invokevirtual 2261	org/vidogram/tgnet/NativeByteBuffer:writeInt32	(I)V
    //   141: aload 6
    //   143: aload_1
    //   144: getfield 2270	org/vidogram/tgnet/TLRPC$TL_dialog:read_outbox_max_id	I
    //   147: invokevirtual 2261	org/vidogram/tgnet/NativeByteBuffer:writeInt32	(I)V
    //   150: aload 6
    //   152: aload_1
    //   153: getfield 1451	org/vidogram/tgnet/TLRPC$TL_dialog:unread_count	I
    //   156: invokevirtual 2261	org/vidogram/tgnet/NativeByteBuffer:writeInt32	(I)V
    //   159: aload 6
    //   161: aload_1
    //   162: getfield 1381	org/vidogram/tgnet/TLRPC$TL_dialog:last_message_date	I
    //   165: invokevirtual 2261	org/vidogram/tgnet/NativeByteBuffer:writeInt32	(I)V
    //   168: aload 6
    //   170: aload_1
    //   171: getfield 2271	org/vidogram/tgnet/TLRPC$TL_dialog:pts	I
    //   174: invokevirtual 2261	org/vidogram/tgnet/NativeByteBuffer:writeInt32	(I)V
    //   177: aload 6
    //   179: aload_1
    //   180: getfield 2272	org/vidogram/tgnet/TLRPC$TL_dialog:flags	I
    //   183: invokevirtual 2261	org/vidogram/tgnet/NativeByteBuffer:writeInt32	(I)V
    //   186: aload 6
    //   188: aload_1
    //   189: getfield 2192	org/vidogram/tgnet/TLRPC$TL_dialog:pinned	Z
    //   192: invokevirtual 2275	org/vidogram/tgnet/NativeByteBuffer:writeBool	(Z)V
    //   195: aload 6
    //   197: aload_1
    //   198: getfield 2278	org/vidogram/tgnet/TLRPC$TL_dialog:pinnedNum	I
    //   201: invokevirtual 2261	org/vidogram/tgnet/NativeByteBuffer:writeInt32	(I)V
    //   204: aload_2
    //   205: aload 6
    //   207: invokevirtual 2282	org/vidogram/tgnet/TLRPC$InputPeer:serializeToStream	(Lorg/vidogram/tgnet/AbstractSerializedData;)V
    //   210: aload 6
    //   212: astore_2
    //   213: invokestatic 808	org/vidogram/messenger/MessagesStorage:getInstance	()Lorg/vidogram/messenger/MessagesStorage;
    //   216: aload_2
    //   217: invokevirtual 2286	org/vidogram/messenger/MessagesStorage:createPendingTask	(Lorg/vidogram/tgnet/NativeByteBuffer;)J
    //   220: lstore_3
    //   221: invokestatic 1167	org/vidogram/tgnet/ConnectionsManager:getInstance	()Lorg/vidogram/tgnet/ConnectionsManager;
    //   224: aload 8
    //   226: new 214	org/vidogram/messenger/MessagesController$58
    //   229: dup
    //   230: aload_0
    //   231: aload_1
    //   232: lload_3
    //   233: iload 5
    //   235: invokespecial 2289	org/vidogram/messenger/MessagesController$58:<init>	(Lorg/vidogram/messenger/MessagesController;Lorg/vidogram/tgnet/TLRPC$TL_dialog;JI)V
    //   238: invokevirtual 1714	org/vidogram/tgnet/ConnectionsManager:sendRequest	(Lorg/vidogram/tgnet/TLObject;Lorg/vidogram/tgnet/RequestDelegate;)I
    //   241: pop
    //   242: return
    //   243: aload_2
    //   244: astore 6
    //   246: goto -198 -> 48
    //   249: astore 6
    //   251: aconst_null
    //   252: astore_2
    //   253: aload 6
    //   255: invokestatic 946	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   258: goto -45 -> 213
    //   261: goto -40 -> 221
    //   264: astore 7
    //   266: aload 6
    //   268: astore_2
    //   269: aload 7
    //   271: astore 6
    //   273: goto -20 -> 253
    //
    // Exception table:
    //   from	to	target	type
    //   92	108	249	java/lang/Exception
    //   108	210	264	java/lang/Exception
  }

  public void cleanup()
  {
    ContactsController.getInstance().cleanup();
    MediaController.getInstance().cleanup();
    NotificationsController.getInstance().cleanup();
    SendMessagesHelper.getInstance().cleanup();
    SecretChatHelper.getInstance().cleanup();
    StickersQuery.cleanup();
    SearchQuery.cleanup();
    DraftQuery.cleanup();
    this.reloadingWebpages.clear();
    this.reloadingWebpagesPending.clear();
    this.dialogs_dict.clear();
    this.dialogs_read_inbox_max.clear();
    this.dialogs_read_outbox_max.clear();
    this.exportedChats.clear();
    this.fullUsers.clear();
    this.dialogs.clear();
    this.joiningToChannels.clear();
    this.channelViewsToSend.clear();
    this.channelViewsToReload.clear();
    this.dialogsServerOnly.clear();
    this.dialogsGroupsOnly.clear();
    this.allUsers.clear();
    this.dialogsChannelOnly.clear();
    this.dialogsUserOnly.clear();
    this.dialogsBotOnly.clear();
    this.dialogsVidogramOnly.clear();
    this.dialogMessagesByIds.clear();
    this.dialogMessagesByRandomIds.clear();
    this.users.clear();
    this.usersByUsernames.clear();
    this.chats.clear();
    this.dialogMessage.clear();
    this.printingUsers.clear();
    this.printingStrings.clear();
    this.printingStringsTypes.clear();
    this.onlinePrivacy.clear();
    this.loadingPeerSettings.clear();
    this.lastPrintingStringCount = 0;
    this.nextDialogsCacheOffset = 0;
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        MessagesController.this.updatesQueueSeq.clear();
        MessagesController.this.updatesQueuePts.clear();
        MessagesController.this.updatesQueueQts.clear();
        MessagesController.this.gettingUnknownChannels.clear();
        MessagesController.access$702(MessagesController.this, 0L);
        MessagesController.access$802(MessagesController.this, 0L);
        MessagesController.access$902(MessagesController.this, 0L);
        MessagesController.this.createdDialogIds.clear();
        MessagesController.this.gettingDifference = false;
      }
    });
    this.blockedUsers.clear();
    this.sendingTypings.clear();
    this.loadingFullUsers.clear();
    this.loadedFullUsers.clear();
    this.reloadingMessages.clear();
    this.loadingFullChats.clear();
    this.loadingFullParticipants.clear();
    this.loadedFullParticipants.clear();
    this.loadedFullChats.clear();
    this.currentDeletingTaskTime = 0;
    this.currentDeletingTaskMids = null;
    this.gettingNewDeleteTask = false;
    this.loadingDialogs = false;
    this.dialogsEndReached = false;
    this.serverDialogsEndReached = false;
    this.loadingBlockedUsers = false;
    this.firstGettingTask = false;
    this.updatingState = false;
    this.lastStatusUpdateTime = 0L;
    this.offlineSent = false;
    this.registeringForPush = false;
    this.uploadingAvatar = null;
    this.statusRequest = 0;
    this.statusSettingState = 0;
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        ConnectionsManager.getInstance().setIsUpdating(false);
        MessagesController.this.updatesQueueChannels.clear();
        MessagesController.this.updatesStartWaitTimeChannels.clear();
        MessagesController.this.gettingDifferenceChannels.clear();
        MessagesController.this.channelsPts.clear();
        MessagesController.this.shortPollChannels.clear();
        MessagesController.this.needShortPollChannels.clear();
      }
    });
    if (this.currentDeleteTaskRunnable != null)
    {
      Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
      this.currentDeleteTaskRunnable = null;
    }
    addSupportUser();
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
  }

  protected void clearFullUsers()
  {
    this.loadedFullUsers.clear();
    this.loadedFullChats.clear();
  }

  public void convertGroup()
  {
  }

  public void convertToMegaGroup(Context paramContext, int paramInt)
  {
    TLRPC.TL_messages_migrateChat localTL_messages_migrateChat = new TLRPC.TL_messages_migrateChat();
    localTL_messages_migrateChat.chat_id = paramInt;
    AlertDialog localAlertDialog = new AlertDialog(paramContext, 1);
    localAlertDialog.setMessage(LocaleController.getString("Loading", 2131165920));
    localAlertDialog.setCanceledOnTouchOutside(false);
    localAlertDialog.setCancelable(false);
    paramInt = ConnectionsManager.getInstance().sendRequest(localTL_messages_migrateChat, new RequestDelegate(paramContext, localAlertDialog)
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        if (paramTL_error == null)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (!((Activity)MessagesController.68.this.val$context).isFinishing());
              try
              {
                MessagesController.68.this.val$progressDialog.dismiss();
                return;
              }
              catch (Exception localException)
              {
                FileLog.e(localException);
              }
            }
          });
          paramTL_error = (TLRPC.Updates)paramTLObject;
          MessagesController.this.processUpdates((TLRPC.Updates)paramTLObject, false);
          return;
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            if (!((Activity)MessagesController.68.this.val$context).isFinishing());
            try
            {
              MessagesController.68.this.val$progressDialog.dismiss();
              AlertDialog.Builder localBuilder = new AlertDialog.Builder(MessagesController.68.this.val$context);
              localBuilder.setTitle(LocaleController.getString("AppName", 2131165319));
              localBuilder.setMessage(LocaleController.getString("ErrorOccurred", 2131165701));
              localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166153), null);
              localBuilder.show().setCanceledOnTouchOutside(true);
              return;
            }
            catch (Exception localException)
            {
              while (true)
                FileLog.e(localException);
            }
          }
        });
      }
    });
    localAlertDialog.setButton(-2, LocaleController.getString("Cancel", 2131165427), new DialogInterface.OnClickListener(paramInt)
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        ConnectionsManager.getInstance().cancelRequest(this.val$reqId, true);
        try
        {
          paramDialogInterface.dismiss();
          return;
        }
        catch (Exception paramDialogInterface)
        {
          FileLog.e(paramDialogInterface);
        }
      }
    });
    try
    {
      localAlertDialog.show();
      return;
    }
    catch (Exception paramContext)
    {
    }
  }

  public int createChat(String paramString1, ArrayList<Integer> paramArrayList, String paramString2, int paramInt, BaseFragment paramBaseFragment)
  {
    if (paramInt == 1)
    {
      paramString2 = new TLRPC.TL_chat();
      paramString2.id = UserConfig.lastBroadcastId;
      paramString2.title = paramString1;
      paramString2.photo = new TLRPC.TL_chatPhotoEmpty();
      paramString2.participants_count = paramArrayList.size();
      paramString2.date = (int)(System.currentTimeMillis() / 1000L);
      paramString2.version = 1;
      UserConfig.lastBroadcastId -= 1;
      putChat(paramString2, false);
      paramString1 = new ArrayList();
      paramString1.add(paramString2);
      MessagesStorage.getInstance().putUsersAndChats(null, paramString1, true, true);
      paramString1 = new TLRPC.TL_chatFull();
      paramString1.id = paramString2.id;
      paramString1.chat_photo = new TLRPC.TL_photoEmpty();
      paramString1.notify_settings = new TLRPC.TL_peerNotifySettingsEmpty();
      paramString1.exported_invite = new TLRPC.TL_chatInviteEmpty();
      paramString1.participants = new TLRPC.TL_chatParticipants();
      paramString1.participants.chat_id = paramString2.id;
      paramString1.participants.admin_id = UserConfig.getClientUserId();
      paramString1.participants.version = 1;
      paramInt = 0;
      while (paramInt < paramArrayList.size())
      {
        paramBaseFragment = new TLRPC.TL_chatParticipant();
        paramBaseFragment.user_id = ((Integer)paramArrayList.get(paramInt)).intValue();
        paramBaseFragment.inviter_id = UserConfig.getClientUserId();
        paramBaseFragment.date = (int)(System.currentTimeMillis() / 1000L);
        paramString1.participants.participants.add(paramBaseFragment);
        paramInt += 1;
      }
      MessagesStorage.getInstance().updateChatInfo(paramString1, false);
      paramString1 = new TLRPC.TL_messageService();
      paramString1.action = new TLRPC.TL_messageActionCreatedBroadcastList();
      paramInt = UserConfig.getNewMessageId();
      paramString1.id = paramInt;
      paramString1.local_id = paramInt;
      paramString1.from_id = UserConfig.getClientUserId();
      paramString1.dialog_id = AndroidUtilities.makeBroadcastId(paramString2.id);
      paramString1.to_id = new TLRPC.TL_peerChat();
      paramString1.to_id.chat_id = paramString2.id;
      paramString1.date = ConnectionsManager.getInstance().getCurrentTime();
      paramString1.random_id = 0L;
      paramString1.flags |= 256;
      UserConfig.saveConfig(false);
      paramBaseFragment = new MessageObject(paramString1, this.users, true);
      paramBaseFragment.messageOwner.send_state = 0;
      paramArrayList = new ArrayList();
      paramArrayList.add(paramBaseFragment);
      paramBaseFragment = new ArrayList();
      paramBaseFragment.add(paramString1);
      MessagesStorage.getInstance().putMessages(paramBaseFragment, false, true, false, 0);
      updateInterfaceWithMessages(paramString1.dialog_id, paramArrayList);
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatDidCreated, new Object[] { Integer.valueOf(paramString2.id) });
      return 0;
    }
    if (paramInt == 0)
    {
      paramString2 = new TLRPC.TL_messages_createChat();
      paramString2.title = paramString1;
      paramInt = 0;
      if (paramInt < paramArrayList.size())
      {
        paramString1 = getUser((Integer)paramArrayList.get(paramInt));
        if (paramString1 == null);
        while (true)
        {
          paramInt += 1;
          break;
          paramString2.users.add(getInputUser(paramString1));
        }
      }
      return ConnectionsManager.getInstance().sendRequest(paramString2, new RequestDelegate(paramBaseFragment, paramString2)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          if (paramTL_error != null)
          {
            AndroidUtilities.runOnUIThread(new Runnable(paramTL_error)
            {
              public void run()
              {
                AlertsCreator.processError(this.val$error, MessagesController.66.this.val$fragment, MessagesController.66.this.val$req, new Object[0]);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
              }
            });
            return;
          }
          paramTLObject = (TLRPC.Updates)paramTLObject;
          MessagesController.this.processUpdates(paramTLObject, false);
          AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
          {
            public void run()
            {
              MessagesController.this.putUsers(this.val$updates.users, false);
              MessagesController.this.putChats(this.val$updates.chats, false);
              if ((this.val$updates.chats != null) && (!this.val$updates.chats.isEmpty()))
              {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatDidCreated, new Object[] { Integer.valueOf(((TLRPC.Chat)this.val$updates.chats.get(0)).id) });
                return;
              }
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
            }
          });
        }
      }
      , 2);
    }
    if ((paramInt == 2) || (paramInt == 4))
    {
      paramArrayList = new TLRPC.TL_channels_createChannel();
      paramArrayList.title = paramString1;
      paramArrayList.about = paramString2;
      if (paramInt == 4)
        paramArrayList.megagroup = true;
      while (true)
      {
        return ConnectionsManager.getInstance().sendRequest(paramArrayList, new RequestDelegate(paramBaseFragment, paramArrayList)
        {
          public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
          {
            if (paramTL_error != null)
            {
              AndroidUtilities.runOnUIThread(new Runnable(paramTL_error)
              {
                public void run()
                {
                  AlertsCreator.processError(this.val$error, MessagesController.67.this.val$fragment, MessagesController.67.this.val$req, new Object[0]);
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
                }
              });
              return;
            }
            paramTLObject = (TLRPC.Updates)paramTLObject;
            MessagesController.this.processUpdates(paramTLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
            {
              public void run()
              {
                MessagesController.this.putUsers(this.val$updates.users, false);
                MessagesController.this.putChats(this.val$updates.chats, false);
                if ((this.val$updates.chats != null) && (!this.val$updates.chats.isEmpty()))
                {
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatDidCreated, new Object[] { Integer.valueOf(((TLRPC.Chat)this.val$updates.chats.get(0)).id) });
                  return;
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
              }
            });
          }
        }
        , 2);
        paramArrayList.broadcast = true;
      }
    }
    return 0;
  }

  public void deleteDialog(long paramLong, int paramInt)
  {
    deleteDialog(paramLong, true, paramInt, 0);
  }

  public void deleteMessages(ArrayList<Integer> paramArrayList, ArrayList<Long> paramArrayList1, TLRPC.EncryptedChat paramEncryptedChat, int paramInt, boolean paramBoolean)
  {
    deleteMessages(paramArrayList, paramArrayList1, paramEncryptedChat, paramInt, paramBoolean, 0L, null);
  }

  // ERROR //
  public void deleteMessages(ArrayList<Integer> paramArrayList, ArrayList<Long> paramArrayList1, TLRPC.EncryptedChat paramEncryptedChat, int paramInt, boolean paramBoolean, long paramLong, TLObject paramTLObject)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnull +10 -> 11
    //   4: aload_1
    //   5: invokevirtual 1617	java/util/ArrayList:isEmpty	()Z
    //   8: ifeq +9 -> 17
    //   11: aload 8
    //   13: ifnonnull +4 -> 17
    //   16: return
    //   17: lload 6
    //   19: lconst_0
    //   20: lcmp
    //   21: ifne +431 -> 452
    //   24: iload 4
    //   26: ifne +60 -> 86
    //   29: iconst_0
    //   30: istore 9
    //   32: iload 9
    //   34: aload_1
    //   35: invokevirtual 1206	java/util/ArrayList:size	()I
    //   38: if_icmpge +55 -> 93
    //   41: aload_1
    //   42: iload 9
    //   44: invokevirtual 1209	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   47: checkcast 1243	java/lang/Integer
    //   50: astore 10
    //   52: aload_0
    //   53: getfield 641	org/vidogram/messenger/MessagesController:dialogMessagesByIds	Ljava/util/HashMap;
    //   56: aload 10
    //   58: invokevirtual 1789	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   61: checkcast 1340	org/vidogram/messenger/MessageObject
    //   64: astore 10
    //   66: aload 10
    //   68: ifnull +9 -> 77
    //   71: aload 10
    //   73: iconst_1
    //   74: putfield 2458	org/vidogram/messenger/MessageObject:deleted	Z
    //   77: iload 9
    //   79: iconst_1
    //   80: iadd
    //   81: istore 9
    //   83: goto -51 -> 32
    //   86: aload_0
    //   87: aload_1
    //   88: iload 4
    //   90: invokevirtual 2462	org/vidogram/messenger/MessagesController:markChannelDialogMessageAsDeleted	(Ljava/util/ArrayList;I)V
    //   93: new 615	java/util/ArrayList
    //   96: dup
    //   97: invokespecial 616	java/util/ArrayList:<init>	()V
    //   100: astore 10
    //   102: iconst_0
    //   103: istore 9
    //   105: iload 9
    //   107: aload_1
    //   108: invokevirtual 1206	java/util/ArrayList:size	()I
    //   111: if_icmpge +39 -> 150
    //   114: aload_1
    //   115: iload 9
    //   117: invokevirtual 1209	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   120: checkcast 1243	java/lang/Integer
    //   123: astore 11
    //   125: aload 11
    //   127: invokevirtual 1803	java/lang/Integer:intValue	()I
    //   130: ifle +11 -> 141
    //   133: aload 10
    //   135: aload 11
    //   137: invokevirtual 940	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   140: pop
    //   141: iload 9
    //   143: iconst_1
    //   144: iadd
    //   145: istore 9
    //   147: goto -42 -> 105
    //   150: invokestatic 808	org/vidogram/messenger/MessagesStorage:getInstance	()Lorg/vidogram/messenger/MessagesStorage;
    //   153: aload_1
    //   154: iconst_1
    //   155: iload 4
    //   157: invokevirtual 2466	org/vidogram/messenger/MessagesStorage:markMessagesAsDeleted	(Ljava/util/ArrayList;ZI)Ljava/util/ArrayList;
    //   160: pop
    //   161: invokestatic 808	org/vidogram/messenger/MessagesStorage:getInstance	()Lorg/vidogram/messenger/MessagesStorage;
    //   164: aload_1
    //   165: aconst_null
    //   166: iconst_1
    //   167: iload 4
    //   169: invokevirtual 2470	org/vidogram/messenger/MessagesStorage:updateDialogsWithDeletedMessages	(Ljava/util/ArrayList;Ljava/util/ArrayList;ZI)V
    //   172: invokestatic 813	org/vidogram/messenger/NotificationCenter:getInstance	()Lorg/vidogram/messenger/NotificationCenter;
    //   175: getstatic 2473	org/vidogram/messenger/NotificationCenter:messagesDeleted	I
    //   178: iconst_2
    //   179: anewarray 4	java/lang/Object
    //   182: dup
    //   183: iconst_0
    //   184: aload_1
    //   185: aastore
    //   186: dup
    //   187: iconst_1
    //   188: iload 4
    //   190: invokestatic 1246	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   193: aastore
    //   194: invokevirtual 1199	org/vidogram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   197: aload 10
    //   199: astore_1
    //   200: iload 4
    //   202: ifeq +113 -> 315
    //   205: aload 8
    //   207: ifnull +30 -> 237
    //   210: aload 8
    //   212: checkcast 2475	org/vidogram/tgnet/TLRPC$TL_channels_deleteMessages
    //   215: astore_1
    //   216: invokestatic 1167	org/vidogram/tgnet/ConnectionsManager:getInstance	()Lorg/vidogram/tgnet/ConnectionsManager;
    //   219: aload_1
    //   220: new 130	org/vidogram/messenger/MessagesController$32
    //   223: dup
    //   224: aload_0
    //   225: iload 4
    //   227: lload 6
    //   229: invokespecial 2478	org/vidogram/messenger/MessagesController$32:<init>	(Lorg/vidogram/messenger/MessagesController;IJ)V
    //   232: invokevirtual 1714	org/vidogram/tgnet/ConnectionsManager:sendRequest	(Lorg/vidogram/tgnet/TLObject;Lorg/vidogram/tgnet/RequestDelegate;)I
    //   235: pop
    //   236: return
    //   237: new 2475	org/vidogram/tgnet/TLRPC$TL_channels_deleteMessages
    //   240: dup
    //   241: invokespecial 2479	org/vidogram/tgnet/TLRPC$TL_channels_deleteMessages:<init>	()V
    //   244: astore_3
    //   245: aload_3
    //   246: aload_1
    //   247: putfield 2480	org/vidogram/tgnet/TLRPC$TL_channels_deleteMessages:id	Ljava/util/ArrayList;
    //   250: aload_3
    //   251: iload 4
    //   253: invokestatic 2068	org/vidogram/messenger/MessagesController:getInputChannel	(I)Lorg/vidogram/tgnet/TLRPC$InputChannel;
    //   256: putfield 2481	org/vidogram/tgnet/TLRPC$TL_channels_deleteMessages:channel	Lorg/vidogram/tgnet/TLRPC$InputChannel;
    //   259: new 2253	org/vidogram/tgnet/NativeByteBuffer
    //   262: dup
    //   263: aload_3
    //   264: invokevirtual 2482	org/vidogram/tgnet/TLRPC$TL_channels_deleteMessages:getObjectSize	()I
    //   267: bipush 8
    //   269: iadd
    //   270: invokespecial 2258	org/vidogram/tgnet/NativeByteBuffer:<init>	(I)V
    //   273: astore_1
    //   274: aload_1
    //   275: bipush 7
    //   277: invokevirtual 2261	org/vidogram/tgnet/NativeByteBuffer:writeInt32	(I)V
    //   280: aload_1
    //   281: iload 4
    //   283: invokevirtual 2261	org/vidogram/tgnet/NativeByteBuffer:writeInt32	(I)V
    //   286: aload_3
    //   287: aload_1
    //   288: invokevirtual 2483	org/vidogram/tgnet/TLRPC$TL_channels_deleteMessages:serializeToStream	(Lorg/vidogram/tgnet/AbstractSerializedData;)V
    //   291: invokestatic 808	org/vidogram/messenger/MessagesStorage:getInstance	()Lorg/vidogram/messenger/MessagesStorage;
    //   294: aload_1
    //   295: invokevirtual 2286	org/vidogram/messenger/MessagesStorage:createPendingTask	(Lorg/vidogram/tgnet/NativeByteBuffer;)J
    //   298: lstore 6
    //   300: aload_3
    //   301: astore_1
    //   302: goto -86 -> 216
    //   305: astore_2
    //   306: aconst_null
    //   307: astore_1
    //   308: aload_2
    //   309: invokestatic 946	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   312: goto -21 -> 291
    //   315: aload_2
    //   316: ifnull +23 -> 339
    //   319: aload_3
    //   320: ifnull +19 -> 339
    //   323: aload_2
    //   324: invokevirtual 1617	java/util/ArrayList:isEmpty	()Z
    //   327: ifne +12 -> 339
    //   330: invokestatic 1467	org/vidogram/messenger/SecretChatHelper:getInstance	()Lorg/vidogram/messenger/SecretChatHelper;
    //   333: aload_3
    //   334: aload_2
    //   335: aconst_null
    //   336: invokevirtual 2487	org/vidogram/messenger/SecretChatHelper:sendMessagesDeleteMessage	(Lorg/vidogram/tgnet/TLRPC$EncryptedChat;Ljava/util/ArrayList;Lorg/vidogram/tgnet/TLRPC$Message;)V
    //   339: aload 8
    //   341: ifnull +28 -> 369
    //   344: aload 8
    //   346: checkcast 2489	org/vidogram/tgnet/TLRPC$TL_messages_deleteMessages
    //   349: astore_1
    //   350: invokestatic 1167	org/vidogram/tgnet/ConnectionsManager:getInstance	()Lorg/vidogram/tgnet/ConnectionsManager;
    //   353: aload_1
    //   354: new 132	org/vidogram/messenger/MessagesController$33
    //   357: dup
    //   358: aload_0
    //   359: lload 6
    //   361: invokespecial 2490	org/vidogram/messenger/MessagesController$33:<init>	(Lorg/vidogram/messenger/MessagesController;J)V
    //   364: invokevirtual 1714	org/vidogram/tgnet/ConnectionsManager:sendRequest	(Lorg/vidogram/tgnet/TLObject;Lorg/vidogram/tgnet/RequestDelegate;)I
    //   367: pop
    //   368: return
    //   369: new 2489	org/vidogram/tgnet/TLRPC$TL_messages_deleteMessages
    //   372: dup
    //   373: invokespecial 2491	org/vidogram/tgnet/TLRPC$TL_messages_deleteMessages:<init>	()V
    //   376: astore_3
    //   377: aload_3
    //   378: aload_1
    //   379: putfield 2492	org/vidogram/tgnet/TLRPC$TL_messages_deleteMessages:id	Ljava/util/ArrayList;
    //   382: aload_3
    //   383: iload 5
    //   385: putfield 2495	org/vidogram/tgnet/TLRPC$TL_messages_deleteMessages:revoke	Z
    //   388: new 2253	org/vidogram/tgnet/NativeByteBuffer
    //   391: dup
    //   392: aload_3
    //   393: invokevirtual 2496	org/vidogram/tgnet/TLRPC$TL_messages_deleteMessages:getObjectSize	()I
    //   396: bipush 8
    //   398: iadd
    //   399: invokespecial 2258	org/vidogram/tgnet/NativeByteBuffer:<init>	(I)V
    //   402: astore_1
    //   403: aload_1
    //   404: bipush 7
    //   406: invokevirtual 2261	org/vidogram/tgnet/NativeByteBuffer:writeInt32	(I)V
    //   409: aload_1
    //   410: iload 4
    //   412: invokevirtual 2261	org/vidogram/tgnet/NativeByteBuffer:writeInt32	(I)V
    //   415: aload_3
    //   416: aload_1
    //   417: invokevirtual 2497	org/vidogram/tgnet/TLRPC$TL_messages_deleteMessages:serializeToStream	(Lorg/vidogram/tgnet/AbstractSerializedData;)V
    //   420: invokestatic 808	org/vidogram/messenger/MessagesStorage:getInstance	()Lorg/vidogram/messenger/MessagesStorage;
    //   423: aload_1
    //   424: invokevirtual 2286	org/vidogram/messenger/MessagesStorage:createPendingTask	(Lorg/vidogram/tgnet/NativeByteBuffer;)J
    //   427: lstore 6
    //   429: aload_3
    //   430: astore_1
    //   431: goto -81 -> 350
    //   434: astore_2
    //   435: aconst_null
    //   436: astore_1
    //   437: aload_2
    //   438: invokestatic 946	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   441: goto -21 -> 420
    //   444: astore_2
    //   445: goto -8 -> 437
    //   448: astore_2
    //   449: goto -141 -> 308
    //   452: aconst_null
    //   453: astore_1
    //   454: goto -254 -> 200
    //
    // Exception table:
    //   from	to	target	type
    //   259	274	305	java/lang/Exception
    //   388	403	434	java/lang/Exception
    //   403	420	444	java/lang/Exception
    //   274	291	448	java/lang/Exception
  }

  public void deleteUserChannelHistory(TLRPC.Chat paramChat, TLRPC.User paramUser, int paramInt)
  {
    if (paramInt == 0)
      MessagesStorage.getInstance().deleteUserChannelHistory(paramChat.id, paramUser.id);
    TLRPC.TL_channels_deleteUserHistory localTL_channels_deleteUserHistory = new TLRPC.TL_channels_deleteUserHistory();
    localTL_channels_deleteUserHistory.channel = getInputChannel(paramChat);
    localTL_channels_deleteUserHistory.user_id = getInputUser(paramUser);
    ConnectionsManager.getInstance().sendRequest(localTL_channels_deleteUserHistory, new RequestDelegate(paramChat, paramUser)
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        if (paramTL_error == null)
        {
          paramTLObject = (TLRPC.TL_messages_affectedHistory)paramTLObject;
          if (paramTLObject.offset > 0)
            MessagesController.this.deleteUserChannelHistory(this.val$chat, this.val$user, paramTLObject.offset);
          MessagesController.this.processNewChannelDifferenceParams(paramTLObject.pts, paramTLObject.pts_count, this.val$chat.id);
        }
      }
    });
  }

  public void deleteUserFromChat(int paramInt, TLRPC.User paramUser, TLRPC.ChatFull paramChatFull)
  {
    if (paramUser == null);
    do
    {
      return;
      if (paramInt <= 0)
        continue;
      localObject1 = getInputUser(paramUser);
      localObject2 = getChat(Integer.valueOf(paramInt));
      boolean bool = ChatObject.isChannel((TLRPC.Chat)localObject2);
      if (bool)
        if ((localObject1 instanceof TLRPC.TL_inputUserSelf))
          if (((TLRPC.Chat)localObject2).creator)
          {
            paramChatFull = new TLRPC.TL_channels_deleteChannel();
            paramChatFull.channel = getInputChannel((TLRPC.Chat)localObject2);
          }
      while (true)
      {
        ConnectionsManager.getInstance().sendRequest(paramChatFull, new RequestDelegate(paramUser, paramInt, bool, (TLRPC.InputUser)localObject1)
        {
          public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
          {
            if (this.val$user.id == UserConfig.getClientUserId())
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  MessagesController.this.deleteDialog(-MessagesController.79.this.val$chat_id, 0);
                }
              });
            if (paramTL_error != null);
            do
            {
              return;
              paramTLObject = (TLRPC.Updates)paramTLObject;
              MessagesController.this.processUpdates(paramTLObject, false);
            }
            while ((!this.val$isChannel) || ((this.val$inputUser instanceof TLRPC.TL_inputUserSelf)));
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MessagesController.this.loadFullChat(MessagesController.79.this.val$chat_id, 0, true);
              }
            }
            , 1000L);
          }
        }
        , 64);
        return;
        paramChatFull = new TLRPC.TL_channels_leaveChannel();
        paramChatFull.channel = getInputChannel((TLRPC.Chat)localObject2);
        continue;
        paramChatFull = new TLRPC.TL_channels_kickFromChannel();
        paramChatFull.channel = getInputChannel((TLRPC.Chat)localObject2);
        paramChatFull.user_id = ((TLRPC.InputUser)localObject1);
        paramChatFull.kicked = true;
        continue;
        paramChatFull = new TLRPC.TL_messages_deleteChatUser();
        paramChatFull.chat_id = paramInt;
        paramChatFull.user_id = getInputUser(paramUser);
      }
    }
    while (!(paramChatFull instanceof TLRPC.TL_chatFull));
    Object localObject1 = getChat(Integer.valueOf(paramInt));
    ((TLRPC.Chat)localObject1).participants_count -= 1;
    Object localObject2 = new ArrayList();
    ((ArrayList)localObject2).add(localObject1);
    MessagesStorage.getInstance().putUsersAndChats(null, (ArrayList)localObject2, true, true);
    paramInt = 0;
    if (paramInt < paramChatFull.participants.participants.size())
      if (((TLRPC.ChatParticipant)paramChatFull.participants.participants.get(paramInt)).user_id == paramUser.id)
        paramChatFull.participants.participants.remove(paramInt);
    for (paramInt = 1; ; paramInt = 0)
    {
      if (paramInt != 0)
      {
        MessagesStorage.getInstance().updateChatInfo(paramChatFull, true);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatInfoDidLoaded, new Object[] { paramChatFull, Integer.valueOf(0), Boolean.valueOf(false), null });
      }
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(32) });
      return;
      paramInt += 1;
      break;
    }
  }

  public void deleteUserPhoto(TLRPC.InputPhoto paramInputPhoto)
  {
    if (paramInputPhoto == null)
    {
      TLRPC.TL_photos_updateProfilePhoto localTL_photos_updateProfilePhoto = new TLRPC.TL_photos_updateProfilePhoto();
      localTL_photos_updateProfilePhoto.id = new TLRPC.TL_inputPhotoEmpty();
      UserConfig.getCurrentUser().photo = new TLRPC.TL_userProfilePhotoEmpty();
      localObject = getUser(Integer.valueOf(UserConfig.getClientUserId()));
      paramInputPhoto = (TLRPC.InputPhoto)localObject;
      if (localObject == null)
        paramInputPhoto = UserConfig.getCurrentUser();
      if (paramInputPhoto == null)
        return;
      paramInputPhoto.photo = UserConfig.getCurrentUser().photo;
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(1535) });
      ConnectionsManager.getInstance().sendRequest(localTL_photos_updateProfilePhoto, new RequestDelegate()
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          if (paramTL_error == null)
          {
            paramTL_error = MessagesController.this.getUser(Integer.valueOf(UserConfig.getClientUserId()));
            if (paramTL_error != null)
              break label41;
            paramTL_error = UserConfig.getCurrentUser();
            MessagesController.this.putUser(paramTL_error, false);
          }
          while (paramTL_error == null)
          {
            return;
            label41: UserConfig.setCurrentUser(paramTL_error);
          }
          MessagesStorage.getInstance().clearUserPhotos(paramTL_error.id);
          ArrayList localArrayList = new ArrayList();
          localArrayList.add(paramTL_error);
          MessagesStorage.getInstance().putUsersAndChats(localArrayList, null, false, true);
          paramTL_error.photo = ((TLRPC.UserProfilePhoto)paramTLObject);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(1535) });
              UserConfig.saveConfig(true);
            }
          });
        }
      });
      return;
    }
    Object localObject = new TLRPC.TL_photos_deletePhotos();
    ((TLRPC.TL_photos_deletePhotos)localObject).id.add(paramInputPhoto);
    ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
      }
    });
  }

  public void didAddedNewTask(int paramInt, SparseArray<ArrayList<Integer>> paramSparseArray)
  {
    Utilities.stageQueue.postRunnable(new Runnable(paramInt)
    {
      public void run()
      {
        if (((MessagesController.this.currentDeletingTaskMids == null) && (!MessagesController.this.gettingNewDeleteTask)) || ((MessagesController.this.currentDeletingTaskTime != 0) && (this.val$minDate < MessagesController.this.currentDeletingTaskTime)))
          MessagesController.this.getNewDeleteTask(null);
      }
    });
    AndroidUtilities.runOnUIThread(new Runnable(paramSparseArray)
    {
      public void run()
      {
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.didCreatedNewDeleteTask, new Object[] { this.val$mids });
      }
    });
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    Object localObject;
    if (paramInt == NotificationCenter.FileDidUpload)
    {
      localObject = (String)paramArrayOfObject[0];
      paramArrayOfObject = (TLRPC.InputFile)paramArrayOfObject[1];
      if ((this.uploadingAvatar != null) && (this.uploadingAvatar.equals(localObject)))
      {
        localObject = new TLRPC.TL_photos_uploadProfilePhoto();
        ((TLRPC.TL_photos_uploadProfilePhoto)localObject).file = paramArrayOfObject;
        ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
        {
          public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
          {
            if (paramTL_error == null)
            {
              paramTL_error = MessagesController.this.getUser(Integer.valueOf(UserConfig.getClientUserId()));
              if (paramTL_error != null)
                break label41;
              paramTL_error = UserConfig.getCurrentUser();
              MessagesController.this.putUser(paramTL_error, true);
            }
            while (paramTL_error == null)
            {
              return;
              label41: UserConfig.setCurrentUser(paramTL_error);
            }
            paramTLObject = (TLRPC.TL_photos_photo)paramTLObject;
            Object localObject = paramTLObject.photo.sizes;
            TLRPC.PhotoSize localPhotoSize = FileLoader.getClosestPhotoSizeWithSize((ArrayList)localObject, 100);
            localObject = FileLoader.getClosestPhotoSizeWithSize((ArrayList)localObject, 1000);
            paramTL_error.photo = new TLRPC.TL_userProfilePhoto();
            paramTL_error.photo.photo_id = paramTLObject.photo.id;
            if (localPhotoSize != null)
              paramTL_error.photo.photo_small = localPhotoSize.location;
            if (localObject != null)
              paramTL_error.photo.photo_big = ((TLRPC.PhotoSize)localObject).location;
            while (true)
            {
              MessagesStorage.getInstance().clearUserPhotos(paramTL_error.id);
              paramTLObject = new ArrayList();
              paramTLObject.add(paramTL_error);
              MessagesStorage.getInstance().putUsersAndChats(paramTLObject, null, false, true);
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(2) });
                  UserConfig.saveConfig(true);
                }
              });
              return;
              if (localPhotoSize == null)
                continue;
              paramTL_error.photo.photo_small = localPhotoSize.location;
            }
          }
        });
      }
    }
    do
    {
      do
        while (true)
        {
          return;
          if (paramInt != NotificationCenter.FileDidFailUpload)
            break;
          paramArrayOfObject = (String)paramArrayOfObject[0];
          if ((this.uploadingAvatar == null) || (!this.uploadingAvatar.equals(paramArrayOfObject)))
            continue;
          this.uploadingAvatar = null;
          return;
        }
      while (paramInt != NotificationCenter.messageReceivedByServer);
      Integer localInteger = (Integer)paramArrayOfObject[0];
      localObject = (Integer)paramArrayOfObject[1];
      paramArrayOfObject = (Long)paramArrayOfObject[3];
      MessageObject localMessageObject = (MessageObject)this.dialogMessage.get(paramArrayOfObject);
      if ((localMessageObject != null) && (localMessageObject.getId() == localInteger.intValue()))
      {
        localMessageObject.messageOwner.id = ((Integer)localObject).intValue();
        localMessageObject.messageOwner.send_state = 0;
        paramArrayOfObject = (TLRPC.TL_dialog)this.dialogs_dict.get(paramArrayOfObject);
        if ((paramArrayOfObject != null) && (paramArrayOfObject.top_message == localInteger.intValue()))
          paramArrayOfObject.top_message = ((Integer)localObject).intValue();
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
      }
      paramArrayOfObject = (MessageObject)this.dialogMessagesByIds.remove(localInteger);
    }
    while (paramArrayOfObject == null);
    this.dialogMessagesByIds.put(localObject, paramArrayOfObject);
  }

  public void generateJoinMessage(int paramInt, boolean paramBoolean)
  {
    Object localObject = getChat(Integer.valueOf(paramInt));
    if ((localObject == null) || (!ChatObject.isChannel(paramInt)) || (((((TLRPC.Chat)localObject).left) || (((TLRPC.Chat)localObject).kicked)) && (!paramBoolean)))
      return;
    TLRPC.TL_messageService localTL_messageService = new TLRPC.TL_messageService();
    localTL_messageService.flags = 256;
    int i = UserConfig.getNewMessageId();
    localTL_messageService.id = i;
    localTL_messageService.local_id = i;
    localTL_messageService.date = ConnectionsManager.getInstance().getCurrentTime();
    localTL_messageService.from_id = UserConfig.getClientUserId();
    localTL_messageService.to_id = new TLRPC.TL_peerChannel();
    localTL_messageService.to_id.channel_id = paramInt;
    localTL_messageService.dialog_id = (-paramInt);
    localTL_messageService.post = true;
    localTL_messageService.action = new TLRPC.TL_messageActionChatAddUser();
    localTL_messageService.action.users.add(Integer.valueOf(UserConfig.getClientUserId()));
    if (((TLRPC.Chat)localObject).megagroup)
      localTL_messageService.flags |= -2147483648;
    UserConfig.saveConfig(false);
    localObject = new ArrayList();
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(localTL_messageService);
    ((ArrayList)localObject).add(new MessageObject(localTL_messageService, null, true));
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable((ArrayList)localObject)
    {
      public void run()
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            NotificationsController.getInstance().processNewMessages(MessagesController.97.this.val$pushMessages, true);
          }
        });
      }
    });
    MessagesStorage.getInstance().putMessages(localArrayList, true, true, false, MediaController.getInstance().getAutodownloadMask());
    AndroidUtilities.runOnUIThread(new Runnable(paramInt, (ArrayList)localObject)
    {
      public void run()
      {
        MessagesController.this.updateInterfaceWithMessages(-this.val$chat_id, this.val$pushMessages);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
      }
    });
  }

  public void generateUpdateMessage()
  {
    if ((BuildVars.DEBUG_VERSION) || (UserConfig.lastUpdateVersion == null) || (UserConfig.lastUpdateVersion.equals(BuildVars.BUILD_VERSION_STRING)))
      return;
    TLRPC.TL_help_getAppChangelog localTL_help_getAppChangelog = new TLRPC.TL_help_getAppChangelog();
    localTL_help_getAppChangelog.prev_app_version = UserConfig.lastUpdateVersion;
    ConnectionsManager.getInstance().sendRequest(localTL_help_getAppChangelog, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        if (paramTL_error == null)
        {
          UserConfig.lastUpdateVersion = BuildVars.BUILD_VERSION_STRING;
          UserConfig.saveConfig(false);
        }
        if ((paramTLObject instanceof TLRPC.Updates))
          MessagesController.this.processUpdates((TLRPC.Updates)paramTLObject, false);
      }
    });
  }

  public void getBlockedUsers(boolean paramBoolean)
  {
    if ((!UserConfig.isClientActivated()) || (this.loadingBlockedUsers))
      return;
    this.loadingBlockedUsers = true;
    if (paramBoolean)
    {
      MessagesStorage.getInstance().getBlockedUsers();
      return;
    }
    TLRPC.TL_contacts_getBlocked localTL_contacts_getBlocked = new TLRPC.TL_contacts_getBlocked();
    localTL_contacts_getBlocked.offset = 0;
    localTL_contacts_getBlocked.limit = 200;
    ConnectionsManager.getInstance().sendRequest(localTL_contacts_getBlocked, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        ArrayList localArrayList = new ArrayList();
        if (paramTL_error == null)
        {
          paramTL_error = (TLRPC.contacts_Blocked)paramTLObject;
          paramTLObject = paramTL_error.blocked.iterator();
          while (paramTLObject.hasNext())
            localArrayList.add(Integer.valueOf(((TLRPC.TL_contactBlocked)paramTLObject.next()).user_id));
          paramTLObject = paramTL_error.users;
          MessagesStorage.getInstance().putUsersAndChats(paramTL_error.users, null, true, true);
          MessagesStorage.getInstance().putBlockedUsers(localArrayList, true);
        }
        while (true)
        {
          MessagesController.this.processLoadedBlockedUsers(localArrayList, paramTLObject, false);
          return;
          paramTLObject = null;
        }
      }
    });
  }

  // ERROR //
  protected void getChannelDifference(int paramInt1, int paramInt2, long paramLong, TLRPC.InputChannel paramInputChannel)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 681	org/vidogram/messenger/MessagesController:gettingDifferenceChannels	Ljava/util/HashMap;
    //   4: iload_1
    //   5: invokestatic 1246	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   8: invokevirtual 1789	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   11: checkcast 1415	java/lang/Boolean
    //   14: astore 9
    //   16: aload 9
    //   18: astore 8
    //   20: aload 9
    //   22: ifnonnull +9 -> 31
    //   25: iconst_0
    //   26: invokestatic 1418	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   29: astore 8
    //   31: aload 8
    //   33: invokevirtual 2639	java/lang/Boolean:booleanValue	()Z
    //   36: ifeq +4 -> 40
    //   39: return
    //   40: iload_2
    //   41: iconst_1
    //   42: if_icmpne +73 -> 115
    //   45: aload_0
    //   46: getfield 679	org/vidogram/messenger/MessagesController:channelsPts	Ljava/util/HashMap;
    //   49: iload_1
    //   50: invokestatic 1246	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   53: invokevirtual 1789	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   56: checkcast 1243	java/lang/Integer
    //   59: ifnonnull -20 -> 39
    //   62: iconst_1
    //   63: invokestatic 1246	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   66: astore 8
    //   68: iconst_1
    //   69: istore 6
    //   71: aload 5
    //   73: astore 9
    //   75: aload 5
    //   77: ifnonnull +9 -> 86
    //   80: iload_1
    //   81: invokestatic 2068	org/vidogram/messenger/MessagesController:getInputChannel	(I)Lorg/vidogram/tgnet/TLRPC$InputChannel;
    //   84: astore 9
    //   86: aload 9
    //   88: ifnull +13 -> 101
    //   91: aload 9
    //   93: getfield 1501	org/vidogram/tgnet/TLRPC$InputChannel:access_hash	J
    //   96: lconst_0
    //   97: lcmp
    //   98: ifne +117 -> 215
    //   101: lload_3
    //   102: lconst_0
    //   103: lcmp
    //   104: ifeq -65 -> 39
    //   107: invokestatic 808	org/vidogram/messenger/MessagesStorage:getInstance	()Lorg/vidogram/messenger/MessagesStorage;
    //   110: lload_3
    //   111: invokevirtual 2642	org/vidogram/messenger/MessagesStorage:removePendingTask	(J)V
    //   114: return
    //   115: aload_0
    //   116: getfield 679	org/vidogram/messenger/MessagesController:channelsPts	Ljava/util/HashMap;
    //   119: iload_1
    //   120: invokestatic 1246	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   123: invokevirtual 1789	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   126: checkcast 1243	java/lang/Integer
    //   129: astore 9
    //   131: aload 9
    //   133: astore 8
    //   135: aload 9
    //   137: ifnonnull +63 -> 200
    //   140: invokestatic 808	org/vidogram/messenger/MessagesStorage:getInstance	()Lorg/vidogram/messenger/MessagesStorage;
    //   143: iload_1
    //   144: invokevirtual 2646	org/vidogram/messenger/MessagesStorage:getChannelPtsSync	(I)I
    //   147: invokestatic 1246	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   150: astore 9
    //   152: aload 9
    //   154: invokevirtual 1803	java/lang/Integer:intValue	()I
    //   157: ifeq +17 -> 174
    //   160: aload_0
    //   161: getfield 679	org/vidogram/messenger/MessagesController:channelsPts	Ljava/util/HashMap;
    //   164: iload_1
    //   165: invokestatic 1246	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   168: aload 9
    //   170: invokevirtual 1800	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   173: pop
    //   174: aload 9
    //   176: astore 8
    //   178: aload 9
    //   180: invokevirtual 1803	java/lang/Integer:intValue	()I
    //   183: ifne +17 -> 200
    //   186: iload_2
    //   187: iconst_2
    //   188: if_icmpeq -149 -> 39
    //   191: iload_2
    //   192: iconst_3
    //   193: if_icmpeq -154 -> 39
    //   196: aload 9
    //   198: astore 8
    //   200: aload 8
    //   202: invokevirtual 1803	java/lang/Integer:intValue	()I
    //   205: ifeq -166 -> 39
    //   208: bipush 100
    //   210: istore 6
    //   212: goto -141 -> 71
    //   215: lload_3
    //   216: lconst_0
    //   217: lcmp
    //   218: ifne +199 -> 417
    //   221: new 2253	org/vidogram/tgnet/NativeByteBuffer
    //   224: dup
    //   225: aload 9
    //   227: invokevirtual 2647	org/vidogram/tgnet/TLRPC$InputChannel:getObjectSize	()I
    //   230: bipush 12
    //   232: iadd
    //   233: invokespecial 2258	org/vidogram/tgnet/NativeByteBuffer:<init>	(I)V
    //   236: astore 5
    //   238: aload 5
    //   240: bipush 6
    //   242: invokevirtual 2261	org/vidogram/tgnet/NativeByteBuffer:writeInt32	(I)V
    //   245: aload 5
    //   247: iload_1
    //   248: invokevirtual 2261	org/vidogram/tgnet/NativeByteBuffer:writeInt32	(I)V
    //   251: aload 5
    //   253: iload_2
    //   254: invokevirtual 2261	org/vidogram/tgnet/NativeByteBuffer:writeInt32	(I)V
    //   257: aload 9
    //   259: aload 5
    //   261: invokevirtual 2648	org/vidogram/tgnet/TLRPC$InputChannel:serializeToStream	(Lorg/vidogram/tgnet/AbstractSerializedData;)V
    //   264: invokestatic 808	org/vidogram/messenger/MessagesStorage:getInstance	()Lorg/vidogram/messenger/MessagesStorage;
    //   267: aload 5
    //   269: invokevirtual 2286	org/vidogram/messenger/MessagesStorage:createPendingTask	(Lorg/vidogram/tgnet/NativeByteBuffer;)J
    //   272: lstore_3
    //   273: aload_0
    //   274: getfield 681	org/vidogram/messenger/MessagesController:gettingDifferenceChannels	Ljava/util/HashMap;
    //   277: iload_1
    //   278: invokestatic 1246	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   281: iconst_1
    //   282: invokestatic 1418	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   285: invokevirtual 1800	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   288: pop
    //   289: new 2650	org/vidogram/tgnet/TLRPC$TL_updates_getChannelDifference
    //   292: dup
    //   293: invokespecial 2651	org/vidogram/tgnet/TLRPC$TL_updates_getChannelDifference:<init>	()V
    //   296: astore 5
    //   298: aload 5
    //   300: aload 9
    //   302: putfield 2652	org/vidogram/tgnet/TLRPC$TL_updates_getChannelDifference:channel	Lorg/vidogram/tgnet/TLRPC$InputChannel;
    //   305: aload 5
    //   307: new 2654	org/vidogram/tgnet/TLRPC$TL_channelMessagesFilterEmpty
    //   310: dup
    //   311: invokespecial 2655	org/vidogram/tgnet/TLRPC$TL_channelMessagesFilterEmpty:<init>	()V
    //   314: putfield 2659	org/vidogram/tgnet/TLRPC$TL_updates_getChannelDifference:filter	Lorg/vidogram/tgnet/TLRPC$ChannelMessagesFilter;
    //   317: aload 5
    //   319: aload 8
    //   321: invokevirtual 1803	java/lang/Integer:intValue	()I
    //   324: putfield 2660	org/vidogram/tgnet/TLRPC$TL_updates_getChannelDifference:pts	I
    //   327: aload 5
    //   329: iload 6
    //   331: putfield 2661	org/vidogram/tgnet/TLRPC$TL_updates_getChannelDifference:limit	I
    //   334: iload_2
    //   335: iconst_3
    //   336: if_icmpeq +84 -> 420
    //   339: iconst_1
    //   340: istore 7
    //   342: aload 5
    //   344: iload 7
    //   346: putfield 2664	org/vidogram/tgnet/TLRPC$TL_updates_getChannelDifference:force	Z
    //   349: new 1110	java/lang/StringBuilder
    //   352: dup
    //   353: invokespecial 1111	java/lang/StringBuilder:<init>	()V
    //   356: ldc_w 2666
    //   359: invokevirtual 1117	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   362: aload 8
    //   364: invokevirtual 2669	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   367: ldc_w 2671
    //   370: invokevirtual 1117	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   373: iload_1
    //   374: invokevirtual 1223	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   377: invokevirtual 1124	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   380: invokestatic 1831	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;)V
    //   383: invokestatic 1167	org/vidogram/tgnet/ConnectionsManager:getInstance	()Lorg/vidogram/tgnet/ConnectionsManager;
    //   386: aload 5
    //   388: new 334	org/vidogram/messenger/MessagesController$93
    //   391: dup
    //   392: aload_0
    //   393: iload_1
    //   394: iload_2
    //   395: lload_3
    //   396: invokespecial 2674	org/vidogram/messenger/MessagesController$93:<init>	(Lorg/vidogram/messenger/MessagesController;IIJ)V
    //   399: invokevirtual 1714	org/vidogram/tgnet/ConnectionsManager:sendRequest	(Lorg/vidogram/tgnet/TLObject;Lorg/vidogram/tgnet/RequestDelegate;)I
    //   402: pop
    //   403: return
    //   404: astore 10
    //   406: aconst_null
    //   407: astore 5
    //   409: aload 10
    //   411: invokestatic 946	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   414: goto -150 -> 264
    //   417: goto -144 -> 273
    //   420: iconst_0
    //   421: istore 7
    //   423: goto -81 -> 342
    //   426: astore 10
    //   428: goto -19 -> 409
    //
    // Exception table:
    //   from	to	target	type
    //   221	238	404	java/lang/Exception
    //   238	264	426	java/lang/Exception
  }

  public TLRPC.Chat getChat(Integer paramInteger)
  {
    return (TLRPC.Chat)this.chats.get(paramInteger);
  }

  public void getDifference()
  {
    getDifference(MessagesStorage.lastPtsValue, MessagesStorage.lastDateValue, MessagesStorage.lastQtsValue, false);
  }

  public void getDifference(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    registerForPush(UserConfig.pushString);
    if (MessagesStorage.lastPtsValue == 0)
      loadCurrentState();
    do
      return;
    while ((!paramBoolean) && (this.gettingDifference));
    this.gettingDifference = true;
    TLRPC.TL_updates_getDifference localTL_updates_getDifference = new TLRPC.TL_updates_getDifference();
    localTL_updates_getDifference.pts = paramInt1;
    localTL_updates_getDifference.date = paramInt2;
    localTL_updates_getDifference.qts = paramInt3;
    if (localTL_updates_getDifference.date == 0)
      localTL_updates_getDifference.date = ConnectionsManager.getInstance().getCurrentTime();
    FileLog.e("start getDifference with date = " + MessagesStorage.lastDateValue + " pts = " + MessagesStorage.lastPtsValue + " seq = " + MessagesStorage.lastSeqValue);
    ConnectionsManager.getInstance().setIsUpdating(true);
    ConnectionsManager.getInstance().sendRequest(localTL_updates_getDifference, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        int j = 0;
        if (paramTL_error == null)
        {
          paramTLObject = (TLRPC.updates_Difference)paramTLObject;
          if ((paramTLObject instanceof TLRPC.TL_updates_differenceSlice))
            MessagesController.this.getDifference(paramTLObject.intermediate_state.pts, paramTLObject.intermediate_state.date, paramTLObject.intermediate_state.qts, true);
          paramTL_error = new HashMap();
          HashMap localHashMap = new HashMap();
          int i = 0;
          while (i < paramTLObject.users.size())
          {
            localObject = (TLRPC.User)paramTLObject.users.get(i);
            paramTL_error.put(Integer.valueOf(((TLRPC.User)localObject).id), localObject);
            i += 1;
          }
          i = 0;
          while (i < paramTLObject.chats.size())
          {
            localObject = (TLRPC.Chat)paramTLObject.chats.get(i);
            localHashMap.put(Integer.valueOf(((TLRPC.Chat)localObject).id), localObject);
            i += 1;
          }
          Object localObject = new ArrayList();
          if (!paramTLObject.other_updates.isEmpty())
            for (i = j; i < paramTLObject.other_updates.size(); i = j + 1)
            {
              TLRPC.Update localUpdate = (TLRPC.Update)paramTLObject.other_updates.get(i);
              j = i;
              if (!(localUpdate instanceof TLRPC.TL_updateMessageID))
                continue;
              ((ArrayList)localObject).add((TLRPC.TL_updateMessageID)localUpdate);
              paramTLObject.other_updates.remove(i);
              j = i - 1;
            }
          AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
          {
            public void run()
            {
              MessagesController.this.putUsers(this.val$res.users, false);
              MessagesController.this.putChats(this.val$res.chats, false);
            }
          });
          MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramTLObject, (ArrayList)localObject, paramTL_error, localHashMap)
          {
            public void run()
            {
              MessagesStorage.getInstance().putUsersAndChats(this.val$res.users, this.val$res.chats, true, false);
              if (!this.val$msgUpdates.isEmpty())
              {
                HashMap localHashMap = new HashMap();
                int i = 0;
                while (i < this.val$msgUpdates.size())
                {
                  TLRPC.TL_updateMessageID localTL_updateMessageID = (TLRPC.TL_updateMessageID)this.val$msgUpdates.get(i);
                  long[] arrayOfLong = MessagesStorage.getInstance().updateMessageStateAndId(localTL_updateMessageID.random_id, null, localTL_updateMessageID.id, 0, false, 0);
                  if (arrayOfLong != null)
                    localHashMap.put(Integer.valueOf(localTL_updateMessageID.id), arrayOfLong);
                  i += 1;
                }
                if (!localHashMap.isEmpty())
                  AndroidUtilities.runOnUIThread(new Runnable(localHashMap)
                  {
                    public void run()
                    {
                      Iterator localIterator = this.val$corrected.entrySet().iterator();
                      while (localIterator.hasNext())
                      {
                        Object localObject = (Map.Entry)localIterator.next();
                        Integer localInteger1 = (Integer)((Map.Entry)localObject).getKey();
                        localObject = (long[])((Map.Entry)localObject).getValue();
                        Integer localInteger2 = Integer.valueOf((int)localObject[1]);
                        SendMessagesHelper.getInstance().processSentMessage(localInteger2.intValue());
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageReceivedByServer, new Object[] { localInteger2, localInteger1, null, Long.valueOf(localObject[0]) });
                      }
                    }
                  });
              }
              Utilities.stageQueue.postRunnable(new Runnable()
              {
                public void run()
                {
                  int m = 0;
                  int k = 0;
                  int i;
                  if ((!MessagesController.94.2.this.val$res.new_messages.isEmpty()) || (!MessagesController.94.2.this.val$res.new_encrypted_messages.isEmpty()))
                  {
                    HashMap localHashMap = new HashMap();
                    i = 0;
                    Object localObject1;
                    Object localObject2;
                    while (i < MessagesController.94.2.this.val$res.new_encrypted_messages.size())
                    {
                      localObject1 = (TLRPC.EncryptedMessage)MessagesController.94.2.this.val$res.new_encrypted_messages.get(i);
                      localObject1 = SecretChatHelper.getInstance().decryptMessage((TLRPC.EncryptedMessage)localObject1);
                      if ((localObject1 != null) && (!((ArrayList)localObject1).isEmpty()))
                      {
                        j = 0;
                        while (j < ((ArrayList)localObject1).size())
                        {
                          localObject2 = (TLRPC.Message)((ArrayList)localObject1).get(j);
                          MessagesController.94.2.this.val$res.new_messages.add(localObject2);
                          j += 1;
                        }
                      }
                      i += 1;
                    }
                    ImageLoader.saveMessagesThumbs(MessagesController.94.2.this.val$res.new_messages);
                    ArrayList localArrayList = new ArrayList();
                    int j = UserConfig.getClientUserId();
                    i = 0;
                    if (i < MessagesController.94.2.this.val$res.new_messages.size())
                    {
                      TLRPC.Message localMessage = (TLRPC.Message)MessagesController.94.2.this.val$res.new_messages.get(i);
                      if (localMessage.dialog_id == 0L)
                        if (localMessage.to_id.chat_id == 0)
                          break label533;
                      Object localObject3;
                      for (localMessage.dialog_id = (-localMessage.to_id.chat_id); ; localMessage.dialog_id = localMessage.to_id.user_id)
                      {
                        if ((int)localMessage.dialog_id != 0)
                        {
                          if ((localMessage.action instanceof TLRPC.TL_messageActionChatDeleteUser))
                          {
                            localObject1 = (TLRPC.User)MessagesController.94.2.this.val$usersDict.get(Integer.valueOf(localMessage.action.user_id));
                            if ((localObject1 != null) && (((TLRPC.User)localObject1).bot))
                              localMessage.reply_markup = new TLRPC.TL_replyKeyboardHide();
                          }
                          if ((!(localMessage.action instanceof TLRPC.TL_messageActionChatMigrateTo)) && (!(localMessage.action instanceof TLRPC.TL_messageActionChannelCreate)))
                            break label577;
                          localMessage.unread = false;
                          localMessage.media_unread = false;
                        }
                        if (localMessage.dialog_id == j)
                        {
                          localMessage.unread = false;
                          localMessage.media_unread = false;
                          localMessage.out = true;
                        }
                        localObject3 = new MessageObject(localMessage, MessagesController.94.2.this.val$usersDict, MessagesController.94.2.this.val$chatsDict, MessagesController.this.createdDialogIds.contains(Long.valueOf(localMessage.dialog_id)));
                        if ((!((MessageObject)localObject3).isOut()) && (((MessageObject)localObject3).isUnread()))
                          localArrayList.add(localObject3);
                        localObject2 = (ArrayList)localHashMap.get(Long.valueOf(localMessage.dialog_id));
                        localObject1 = localObject2;
                        if (localObject2 == null)
                        {
                          localObject1 = new ArrayList();
                          localHashMap.put(Long.valueOf(localMessage.dialog_id), localObject1);
                        }
                        ((ArrayList)localObject1).add(localObject3);
                        i += 1;
                        break;
                        label533: if (localMessage.to_id.user_id != UserConfig.getClientUserId())
                          continue;
                        localMessage.to_id.user_id = localMessage.from_id;
                      }
                      label577: if (localMessage.out)
                      {
                        localObject1 = MessagesController.this.dialogs_read_outbox_max;
                        label600: localObject3 = (Integer)((ConcurrentHashMap)localObject1).get(Long.valueOf(localMessage.dialog_id));
                        localObject2 = localObject3;
                        if (localObject3 == null)
                        {
                          localObject2 = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(localMessage.out, localMessage.dialog_id));
                          ((ConcurrentHashMap)localObject1).put(Long.valueOf(localMessage.dialog_id), localObject2);
                        }
                        if (((Integer)localObject2).intValue() >= localMessage.id)
                          break label708;
                      }
                      label708: for (boolean bool = true; ; bool = false)
                      {
                        localMessage.unread = bool;
                        break;
                        localObject1 = MessagesController.this.dialogs_read_inbox_max;
                        break label600;
                      }
                    }
                    AndroidUtilities.runOnUIThread(new Runnable(localHashMap)
                    {
                      public void run()
                      {
                        Iterator localIterator = this.val$messages.entrySet().iterator();
                        while (localIterator.hasNext())
                        {
                          Object localObject = (Map.Entry)localIterator.next();
                          Long localLong = (Long)((Map.Entry)localObject).getKey();
                          localObject = (ArrayList)((Map.Entry)localObject).getValue();
                          MessagesController.this.updateInterfaceWithMessages(localLong.longValue(), (ArrayList)localObject);
                        }
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                      }
                    });
                    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(localArrayList)
                    {
                      public void run()
                      {
                        if (!this.val$pushMessages.isEmpty())
                          AndroidUtilities.runOnUIThread(new Runnable()
                          {
                            public void run()
                            {
                              NotificationsController localNotificationsController = NotificationsController.getInstance();
                              ArrayList localArrayList = MessagesController.94.2.2.2.this.val$pushMessages;
                              if (!(MessagesController.94.2.this.val$res instanceof TLRPC.TL_updates_differenceSlice));
                              for (boolean bool = true; ; bool = false)
                              {
                                localNotificationsController.processNewMessages(localArrayList, bool);
                                return;
                              }
                            }
                          });
                        MessagesStorage.getInstance().putMessages(MessagesController.94.2.this.val$res.new_messages, true, false, false, MediaController.getInstance().getAutodownloadMask());
                      }
                    });
                    SecretChatHelper.getInstance().processPendingEncMessages();
                  }
                  if (!MessagesController.94.2.this.val$res.other_updates.isEmpty())
                    MessagesController.this.processUpdateArray(MessagesController.94.2.this.val$res.other_updates, MessagesController.94.2.this.val$res.users, MessagesController.94.2.this.val$res.chats, true);
                  if ((MessagesController.94.2.this.val$res instanceof TLRPC.TL_updates_difference))
                  {
                    MessagesController.this.gettingDifference = false;
                    MessagesStorage.lastSeqValue = MessagesController.94.2.this.val$res.state.seq;
                    MessagesStorage.lastDateValue = MessagesController.94.2.this.val$res.state.date;
                    MessagesStorage.lastPtsValue = MessagesController.94.2.this.val$res.state.pts;
                    MessagesStorage.lastQtsValue = MessagesController.94.2.this.val$res.state.qts;
                    ConnectionsManager.getInstance().setIsUpdating(false);
                    i = k;
                    while (i < 3)
                    {
                      MessagesController.this.processUpdatesQueue(i, 1);
                      i += 1;
                    }
                  }
                  if ((MessagesController.94.2.this.val$res instanceof TLRPC.TL_updates_differenceSlice))
                  {
                    MessagesStorage.lastDateValue = MessagesController.94.2.this.val$res.intermediate_state.date;
                    MessagesStorage.lastPtsValue = MessagesController.94.2.this.val$res.intermediate_state.pts;
                    MessagesStorage.lastQtsValue = MessagesController.94.2.this.val$res.intermediate_state.qts;
                  }
                  while (true)
                  {
                    MessagesStorage.getInstance().saveDiffParams(MessagesStorage.lastSeqValue, MessagesStorage.lastPtsValue, MessagesStorage.lastDateValue, MessagesStorage.lastQtsValue);
                    FileLog.e("received difference with date = " + MessagesStorage.lastDateValue + " pts = " + MessagesStorage.lastPtsValue + " seq = " + MessagesStorage.lastSeqValue + " messages = " + MessagesController.94.2.this.val$res.new_messages.size() + " users = " + MessagesController.94.2.this.val$res.users.size() + " chats = " + MessagesController.94.2.this.val$res.chats.size() + " other updates = " + MessagesController.94.2.this.val$res.other_updates.size());
                    return;
                    if (!(MessagesController.94.2.this.val$res instanceof TLRPC.TL_updates_differenceEmpty))
                      continue;
                    MessagesController.this.gettingDifference = false;
                    MessagesStorage.lastSeqValue = MessagesController.94.2.this.val$res.seq;
                    MessagesStorage.lastDateValue = MessagesController.94.2.this.val$res.date;
                    ConnectionsManager.getInstance().setIsUpdating(false);
                    i = m;
                    while (i < 3)
                    {
                      MessagesController.this.processUpdatesQueue(i, 1);
                      i += 1;
                    }
                  }
                }
              });
            }
          });
          return;
        }
        MessagesController.this.gettingDifference = false;
        ConnectionsManager.getInstance().setIsUpdating(false);
      }
    });
  }

  public TLRPC.EncryptedChat getEncryptedChat(Integer paramInteger)
  {
    return (TLRPC.EncryptedChat)this.encryptedChats.get(paramInteger);
  }

  public TLRPC.EncryptedChat getEncryptedChatDB(int paramInt, boolean paramBoolean)
  {
    Object localObject2 = (TLRPC.EncryptedChat)this.encryptedChats.get(Integer.valueOf(paramInt));
    if (localObject2 != null)
    {
      localObject1 = localObject2;
      if (!paramBoolean)
        break label126;
      if (!(localObject2 instanceof TLRPC.TL_encryptedChatWaiting))
      {
        localObject1 = localObject2;
        if (!(localObject2 instanceof TLRPC.TL_encryptedChatRequested))
          break label126;
      }
    }
    Object localObject1 = new Semaphore(0);
    ArrayList localArrayList = new ArrayList();
    MessagesStorage.getInstance().getEncryptedChat(paramInt, (Semaphore)localObject1, localArrayList);
    try
    {
      ((Semaphore)localObject1).acquire();
      localObject1 = localObject2;
      if (localArrayList.size() == 2)
      {
        localObject1 = (TLRPC.EncryptedChat)localArrayList.get(0);
        localObject2 = (TLRPC.User)localArrayList.get(1);
        putEncryptedChat((TLRPC.EncryptedChat)localObject1, false);
        putUser((TLRPC.User)localObject2, true);
      }
      label126: return localObject1;
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  public TLRPC.ExportedChatInvite getExportedInvite(int paramInt)
  {
    return (TLRPC.ExportedChatInvite)this.exportedChats.get(Integer.valueOf(paramInt));
  }

  public String getFullName(TLRPC.User paramUser)
  {
    String str1 = "";
    if (paramUser.first_name != null)
      str1 = "" + paramUser.first_name + " ";
    String str2 = str1;
    if (paramUser.last_name != null)
      str2 = str1 + paramUser.last_name;
    return str2;
  }

  public void getNewDeleteTask(ArrayList<Integer> paramArrayList)
  {
    Utilities.stageQueue.postRunnable(new Runnable(paramArrayList)
    {
      public void run()
      {
        MessagesController.access$3002(MessagesController.this, true);
        MessagesStorage.getInstance().getNewTask(this.val$oldTask);
      }
    });
  }

  public long getUpdatesStartTime(int paramInt)
  {
    if (paramInt == 0)
      return this.updatesStartWaitTimeSeq;
    if (paramInt == 1)
      return this.updatesStartWaitTimePts;
    if (paramInt == 2)
      return this.updatesStartWaitTimeQts;
    return 0L;
  }

  public TLRPC.User getUser(Integer paramInteger)
  {
    return (TLRPC.User)this.users.get(paramInteger);
  }

  public TLRPC.User getUser(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0))
      return null;
    return (TLRPC.User)this.usersByUsernames.get(paramString.toLowerCase());
  }

  public TLRPC.TL_userFull getUserFull(int paramInt)
  {
    return (TLRPC.TL_userFull)this.fullUsers.get(Integer.valueOf(paramInt));
  }

  public ConcurrentHashMap<Integer, TLRPC.User> getUsers()
  {
    return this.users;
  }

  public void hideReportSpam(long paramLong, TLRPC.User paramUser, TLRPC.Chat paramChat)
  {
    if ((paramUser == null) && (paramChat == null));
    do
    {
      return;
      localObject = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
      ((SharedPreferences.Editor)localObject).putInt("spam3_" + paramLong, 1);
      ((SharedPreferences.Editor)localObject).commit();
    }
    while ((int)paramLong == 0);
    Object localObject = new TLRPC.TL_messages_hideReportSpam();
    if (paramUser != null)
      ((TLRPC.TL_messages_hideReportSpam)localObject).peer = getInputPeer(paramUser.id);
    while (true)
    {
      ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
        }
      });
      return;
      if (paramChat == null)
        continue;
      ((TLRPC.TL_messages_hideReportSpam)localObject).peer = getInputPeer(-paramChat.id);
    }
  }

  public boolean isDialogMuted(long paramLong)
  {
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
    int i = localSharedPreferences.getInt("notify2_" + paramLong, 0);
    if (i == 2);
    do
      return true;
    while ((i == 3) && (localSharedPreferences.getInt("notifyuntil_" + paramLong, 0) >= ConnectionsManager.getInstance().getCurrentTime()));
    return false;
  }

  public void loadChannelParticipants(Integer paramInteger)
  {
    if ((this.loadingFullParticipants.contains(paramInteger)) || (this.loadedFullParticipants.contains(paramInteger)))
      return;
    this.loadingFullParticipants.add(paramInteger);
    TLRPC.TL_channels_getParticipants localTL_channels_getParticipants = new TLRPC.TL_channels_getParticipants();
    localTL_channels_getParticipants.channel = getInputChannel(paramInteger.intValue());
    localTL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsRecent();
    localTL_channels_getParticipants.offset = 0;
    localTL_channels_getParticipants.limit = 32;
    ConnectionsManager.getInstance().sendRequest(localTL_channels_getParticipants, new RequestDelegate(paramInteger)
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
        {
          public void run()
          {
            if (this.val$error == null)
            {
              TLRPC.TL_channels_channelParticipants localTL_channels_channelParticipants = (TLRPC.TL_channels_channelParticipants)this.val$response;
              MessagesController.this.putUsers(localTL_channels_channelParticipants.users, false);
              MessagesStorage.getInstance().putUsersAndChats(localTL_channels_channelParticipants.users, null, true, true);
              MessagesStorage.getInstance().updateChannelUsers(MessagesController.41.this.val$chat_id.intValue(), localTL_channels_channelParticipants.participants);
              MessagesController.this.loadedFullParticipants.add(MessagesController.41.this.val$chat_id);
            }
            MessagesController.this.loadingFullParticipants.remove(MessagesController.41.this.val$chat_id);
          }
        });
      }
    });
  }

  public void loadChatInfo(int paramInt, Semaphore paramSemaphore, boolean paramBoolean)
  {
    MessagesStorage.getInstance().loadChatInfo(paramInt, paramSemaphore, paramBoolean, false);
  }

  public void loadCurrentState()
  {
    if (this.updatingState)
      return;
    this.updatingState = true;
    TLRPC.TL_updates_getState localTL_updates_getState = new TLRPC.TL_updates_getState();
    ConnectionsManager.getInstance().sendRequest(localTL_updates_getState, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        int i = 0;
        MessagesController.this.updatingState = false;
        if (paramTL_error == null)
        {
          paramTLObject = (TLRPC.TL_updates_state)paramTLObject;
          MessagesStorage.lastDateValue = paramTLObject.date;
          MessagesStorage.lastPtsValue = paramTLObject.pts;
          MessagesStorage.lastSeqValue = paramTLObject.seq;
          MessagesStorage.lastQtsValue = paramTLObject.qts;
          while (i < 3)
          {
            MessagesController.this.processUpdatesQueue(i, 2);
            i += 1;
          }
          MessagesStorage.getInstance().saveDiffParams(MessagesStorage.lastSeqValue, MessagesStorage.lastPtsValue, MessagesStorage.lastDateValue, MessagesStorage.lastQtsValue);
        }
        do
          return;
        while (paramTL_error.code == 401);
        MessagesController.this.loadCurrentState();
      }
    });
  }

  public void loadDialogPhotos(int paramInt1, int paramInt2, int paramInt3, long paramLong, boolean paramBoolean, int paramInt4)
  {
    if (paramBoolean)
      MessagesStorage.getInstance().getDialogPhotos(paramInt1, paramInt2, paramInt3, paramLong, paramInt4);
    do
      while (true)
      {
        return;
        if (paramInt1 <= 0)
          break;
        localObject = getUser(Integer.valueOf(paramInt1));
        if (localObject == null)
          continue;
        TLRPC.TL_photos_getUserPhotos localTL_photos_getUserPhotos = new TLRPC.TL_photos_getUserPhotos();
        localTL_photos_getUserPhotos.limit = paramInt3;
        localTL_photos_getUserPhotos.offset = paramInt2;
        localTL_photos_getUserPhotos.max_id = (int)paramLong;
        localTL_photos_getUserPhotos.user_id = getInputUser((TLRPC.User)localObject);
        paramInt1 = ConnectionsManager.getInstance().sendRequest(localTL_photos_getUserPhotos, new RequestDelegate(paramInt1, paramInt2, paramInt3, paramLong, paramInt4)
        {
          public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
          {
            if (paramTL_error == null)
            {
              paramTLObject = (TLRPC.photos_Photos)paramTLObject;
              MessagesController.this.processLoadedUserPhotos(paramTLObject, this.val$did, this.val$offset, this.val$count, this.val$max_id, false, this.val$classGuid);
            }
          }
        });
        ConnectionsManager.getInstance().bindRequestToGuid(paramInt1, paramInt4);
        return;
      }
    while (paramInt1 >= 0);
    Object localObject = new TLRPC.TL_messages_search();
    ((TLRPC.TL_messages_search)localObject).filter = new TLRPC.TL_inputMessagesFilterChatPhotos();
    ((TLRPC.TL_messages_search)localObject).limit = paramInt3;
    ((TLRPC.TL_messages_search)localObject).offset = paramInt2;
    ((TLRPC.TL_messages_search)localObject).max_id = (int)paramLong;
    ((TLRPC.TL_messages_search)localObject).q = "";
    ((TLRPC.TL_messages_search)localObject).peer = getInputPeer(paramInt1);
    paramInt1 = ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate(paramInt1, paramInt2, paramInt3, paramLong, paramInt4)
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        if (paramTL_error == null)
        {
          paramTLObject = (TLRPC.messages_Messages)paramTLObject;
          paramTL_error = new TLRPC.TL_photos_photos();
          paramTL_error.count = paramTLObject.count;
          paramTL_error.users.addAll(paramTLObject.users);
          int i = 0;
          if (i < paramTLObject.messages.size())
          {
            TLRPC.Message localMessage = (TLRPC.Message)paramTLObject.messages.get(i);
            if ((localMessage.action == null) || (localMessage.action.photo == null));
            while (true)
            {
              i += 1;
              break;
              paramTL_error.photos.add(localMessage.action.photo);
            }
          }
          MessagesController.this.processLoadedUserPhotos(paramTL_error, this.val$did, this.val$offset, this.val$count, this.val$max_id, false, this.val$classGuid);
        }
      }
    });
    ConnectionsManager.getInstance().bindRequestToGuid(paramInt1, paramInt4);
  }

  public void loadDialogs(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int j = 0;
    if (this.loadingDialogs)
      return;
    this.loadingDialogs = true;
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    FileLog.e("load cacheOffset = " + paramInt1 + " count = " + paramInt2 + " cache = " + paramBoolean);
    if (paramBoolean)
    {
      localObject1 = MessagesStorage.getInstance();
      if (paramInt1 == 0);
      for (paramInt1 = 0; ; paramInt1 = this.nextDialogsCacheOffset)
      {
        ((MessagesStorage)localObject1).getDialogs(paramInt1, paramInt2);
        return;
      }
    }
    Object localObject1 = new TLRPC.TL_messages_getDialogs();
    ((TLRPC.TL_messages_getDialogs)localObject1).limit = paramInt2;
    ((TLRPC.TL_messages_getDialogs)localObject1).exclude_pinned = true;
    paramInt1 = this.dialogs.size() - 1;
    int i = j;
    Object localObject2;
    if (paramInt1 >= 0)
    {
      localObject2 = (TLRPC.TL_dialog)this.dialogs.get(paramInt1);
      if (((TLRPC.TL_dialog)localObject2).pinned);
      do
      {
        int k;
        do
        {
          paramInt1 -= 1;
          break;
          i = (int)((TLRPC.TL_dialog)localObject2).id;
          k = (int)(((TLRPC.TL_dialog)localObject2).id >> 32);
        }
        while ((i == 0) || (k == 1) || (((TLRPC.TL_dialog)localObject2).top_message <= 0));
        localObject2 = (MessageObject)this.dialogMessage.get(Long.valueOf(((TLRPC.TL_dialog)localObject2).id));
      }
      while ((localObject2 == null) || (((MessageObject)localObject2).getId() <= 0));
      ((TLRPC.TL_messages_getDialogs)localObject1).offset_date = ((MessageObject)localObject2).messageOwner.date;
      ((TLRPC.TL_messages_getDialogs)localObject1).offset_id = ((MessageObject)localObject2).messageOwner.id;
      if (((MessageObject)localObject2).messageOwner.to_id.channel_id == 0)
        break label342;
      paramInt1 = -((MessageObject)localObject2).messageOwner.to_id.channel_id;
    }
    while (true)
    {
      ((TLRPC.TL_messages_getDialogs)localObject1).offset_peer = getInputPeer(paramInt1);
      i = 1;
      if (i == 0)
        ((TLRPC.TL_messages_getDialogs)localObject1).offset_peer = new TLRPC.TL_inputPeerEmpty();
      ConnectionsManager.getInstance().sendRequest((TLObject)localObject1, new RequestDelegate(paramInt2)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          if (paramTL_error == null)
          {
            paramTLObject = (TLRPC.messages_Dialogs)paramTLObject;
            MessagesController.this.processLoadedDialogs(paramTLObject, null, 0, this.val$count, 0, false, false, false);
          }
        }
      });
      return;
      label342: if (((MessageObject)localObject2).messageOwner.to_id.chat_id != 0)
      {
        paramInt1 = -((MessageObject)localObject2).messageOwner.to_id.chat_id;
        continue;
      }
      paramInt1 = ((MessageObject)localObject2).messageOwner.to_id.user_id;
    }
  }

  public void loadFullChat(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if ((this.loadingFullChats.contains(Integer.valueOf(paramInt1))) || ((!paramBoolean) && (this.loadedFullChats.contains(Integer.valueOf(paramInt1)))))
      return;
    this.loadingFullChats.add(Integer.valueOf(paramInt1));
    long l = -paramInt1;
    TLRPC.Chat localChat = getChat(Integer.valueOf(paramInt1));
    Object localObject;
    if (ChatObject.isChannel(paramInt1))
    {
      localObject = new TLRPC.TL_channels_getFullChannel();
      ((TLRPC.TL_channels_getFullChannel)localObject).channel = getInputChannel(paramInt1);
    }
    while (true)
    {
      paramInt1 = ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate(localChat, l, paramInt1, paramInt2)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          if (paramTL_error == null)
          {
            TLRPC.TL_messages_chatFull localTL_messages_chatFull = (TLRPC.TL_messages_chatFull)paramTLObject;
            MessagesStorage.getInstance().putUsersAndChats(localTL_messages_chatFull.users, localTL_messages_chatFull.chats, true, true);
            MessagesStorage.getInstance().updateChatInfo(localTL_messages_chatFull.full_chat, false);
            if (ChatObject.isChannel(this.val$chat))
            {
              paramTL_error = (Integer)MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(this.val$dialog_id));
              paramTLObject = paramTL_error;
              if (paramTL_error == null)
                paramTLObject = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(false, this.val$dialog_id));
              MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(this.val$dialog_id), Integer.valueOf(Math.max(localTL_messages_chatFull.full_chat.read_inbox_max_id, paramTLObject.intValue())));
              if (paramTLObject.intValue() == 0)
              {
                paramTLObject = new ArrayList();
                paramTL_error = new TLRPC.TL_updateReadChannelInbox();
                paramTL_error.channel_id = this.val$chat_id;
                paramTL_error.max_id = localTL_messages_chatFull.full_chat.read_inbox_max_id;
                paramTLObject.add(paramTL_error);
                MessagesController.this.processUpdateArray(paramTLObject, null, null, false);
              }
              paramTL_error = (Integer)MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(this.val$dialog_id));
              paramTLObject = paramTL_error;
              if (paramTL_error == null)
                paramTLObject = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, this.val$dialog_id));
              MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(this.val$dialog_id), Integer.valueOf(Math.max(localTL_messages_chatFull.full_chat.read_outbox_max_id, paramTLObject.intValue())));
              if (paramTLObject.intValue() == 0)
              {
                paramTLObject = new ArrayList();
                paramTL_error = new TLRPC.TL_updateReadChannelOutbox();
                paramTL_error.channel_id = this.val$chat_id;
                paramTL_error.max_id = localTL_messages_chatFull.full_chat.read_outbox_max_id;
                paramTLObject.add(paramTL_error);
                MessagesController.this.processUpdateArray(paramTLObject, null, null, false);
              }
            }
            AndroidUtilities.runOnUIThread(new Runnable(localTL_messages_chatFull)
            {
              public void run()
              {
                MessagesController.this.applyDialogNotificationsSettings(-MessagesController.10.this.val$chat_id, this.val$res.full_chat.notify_settings);
                int i = 0;
                while (i < this.val$res.full_chat.bot_info.size())
                {
                  BotQuery.putBotInfo((TLRPC.BotInfo)this.val$res.full_chat.bot_info.get(i));
                  i += 1;
                }
                MessagesController.this.exportedChats.put(Integer.valueOf(MessagesController.10.this.val$chat_id), this.val$res.full_chat.exported_invite);
                MessagesController.this.loadingFullChats.remove(Integer.valueOf(MessagesController.10.this.val$chat_id));
                MessagesController.this.loadedFullChats.add(Integer.valueOf(MessagesController.10.this.val$chat_id));
                if (!this.val$res.chats.isEmpty())
                  ((TLRPC.Chat)this.val$res.chats.get(0)).address = this.val$res.full_chat.about;
                MessagesController.this.putUsers(this.val$res.users, false);
                MessagesController.this.putChats(this.val$res.chats, false);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatInfoDidLoaded, new Object[] { this.val$res.full_chat, Integer.valueOf(MessagesController.10.this.val$classGuid), Boolean.valueOf(false), null });
              }
            });
            return;
          }
          AndroidUtilities.runOnUIThread(new Runnable(paramTL_error)
          {
            public void run()
            {
              MessagesController.this.checkChannelError(this.val$error.text, MessagesController.10.this.val$chat_id);
              MessagesController.this.loadingFullChats.remove(Integer.valueOf(MessagesController.10.this.val$chat_id));
            }
          });
        }
      });
      if (paramInt2 == 0)
        break;
      ConnectionsManager.getInstance().bindRequestToGuid(paramInt1, paramInt2);
      return;
      TLRPC.TL_messages_getFullChat localTL_messages_getFullChat = new TLRPC.TL_messages_getFullChat();
      localTL_messages_getFullChat.chat_id = paramInt1;
      if (this.dialogs_read_inbox_max.get(Long.valueOf(l)) != null)
      {
        localObject = localTL_messages_getFullChat;
        if (this.dialogs_read_outbox_max.get(Long.valueOf(l)) != null)
          continue;
      }
      reloadDialogsReadValue(null, l);
      localObject = localTL_messages_getFullChat;
    }
  }

  public void loadFullUser(TLRPC.User paramUser, int paramInt, boolean paramBoolean)
  {
    if ((paramUser == null) || (this.loadingFullUsers.contains(Integer.valueOf(paramUser.id))) || ((!paramBoolean) && (this.loadedFullUsers.contains(Integer.valueOf(paramUser.id)))))
      return;
    this.loadingFullUsers.add(Integer.valueOf(paramUser.id));
    TLRPC.TL_users_getFullUser localTL_users_getFullUser = new TLRPC.TL_users_getFullUser();
    localTL_users_getFullUser.id = getInputUser(paramUser);
    long l = paramUser.id;
    if ((this.dialogs_read_inbox_max.get(Long.valueOf(l)) == null) || (this.dialogs_read_outbox_max.get(Long.valueOf(l)) == null))
      reloadDialogsReadValue(null, l);
    int i = ConnectionsManager.getInstance().sendRequest(localTL_users_getFullUser, new RequestDelegate(paramUser, paramInt)
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        if (paramTL_error == null)
        {
          AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
          {
            public void run()
            {
              TLRPC.TL_userFull localTL_userFull = (TLRPC.TL_userFull)this.val$response;
              MessagesController.this.applyDialogNotificationsSettings(MessagesController.11.this.val$user.id, localTL_userFull.notify_settings);
              if ((localTL_userFull.bot_info instanceof TLRPC.TL_botInfo))
                BotQuery.putBotInfo(localTL_userFull.bot_info);
              MessagesController.this.fullUsers.put(Integer.valueOf(MessagesController.11.this.val$user.id), localTL_userFull);
              MessagesController.this.loadingFullUsers.remove(Integer.valueOf(MessagesController.11.this.val$user.id));
              MessagesController.this.loadedFullUsers.add(Integer.valueOf(MessagesController.11.this.val$user.id));
              String str = MessagesController.11.this.val$user.first_name + MessagesController.11.this.val$user.last_name + MessagesController.11.this.val$user.username;
              ArrayList localArrayList = new ArrayList();
              localArrayList.add(localTL_userFull.user);
              MessagesController.this.putUsers(localArrayList, false);
              MessagesStorage.getInstance().putUsersAndChats(localArrayList, null, false, true);
              if ((str != null) && (!str.equals(localTL_userFull.user.first_name + localTL_userFull.user.last_name + localTL_userFull.user.username)))
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(1) });
              if ((localTL_userFull.bot_info instanceof TLRPC.TL_botInfo))
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.botInfoDidLoaded, new Object[] { localTL_userFull.bot_info, Integer.valueOf(MessagesController.11.this.val$classGuid) });
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.userInfoDidLoaded, new Object[] { Integer.valueOf(MessagesController.11.this.val$user.id), localTL_userFull });
            }
          });
          return;
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            MessagesController.this.loadingFullUsers.remove(Integer.valueOf(MessagesController.11.this.val$user.id));
          }
        });
      }
    });
    ConnectionsManager.getInstance().bindRequestToGuid(i, paramInt);
  }

  public void loadMessages(long paramLong, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, int paramInt4, int paramInt5, int paramInt6, int paramInt7, boolean paramBoolean2, int paramInt8)
  {
    loadMessages(paramLong, paramInt1, paramInt2, paramInt3, paramBoolean1, paramInt4, paramInt5, paramInt6, paramInt7, paramBoolean2, paramInt8, 0, 0, 0, false);
  }

  public void loadMessages(long paramLong, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, int paramInt4, int paramInt5, int paramInt6, int paramInt7, boolean paramBoolean2, int paramInt8, int paramInt9, int paramInt10, int paramInt11, boolean paramBoolean3)
  {
    FileLog.e("load messages in chat " + paramLong + " count " + paramInt1 + " max_id " + paramInt2 + " cache " + paramBoolean1 + " mindate = " + paramInt4 + " guid " + paramInt5 + " load_type " + paramInt6 + " last_message_id " + paramInt7 + " index " + paramInt8 + " firstUnread " + paramInt9 + " underad count " + paramInt10 + " last_date " + paramInt11 + " queryFromServer " + paramBoolean3);
    int i = (int)paramLong;
    if ((paramBoolean1) || (i == 0))
    {
      MessagesStorage.getInstance().getMessages(paramLong, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramBoolean2, paramInt8);
      return;
    }
    TLRPC.TL_messages_getHistory localTL_messages_getHistory = new TLRPC.TL_messages_getHistory();
    localTL_messages_getHistory.peer = getInputPeer(i);
    if (paramInt6 == 4)
      localTL_messages_getHistory.add_offset = (-paramInt1 + 5);
    while (true)
    {
      localTL_messages_getHistory.limit = paramInt1;
      localTL_messages_getHistory.offset_id = paramInt2;
      localTL_messages_getHistory.offset_date = paramInt3;
      paramInt1 = ConnectionsManager.getInstance().sendRequest(localTL_messages_getHistory, new RequestDelegate(paramInt1, paramInt2, paramInt3, paramLong, paramInt5, paramInt9, paramInt7, paramInt10, paramInt11, paramInt6, paramBoolean2, paramInt8, paramBoolean3)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          int j;
          int i;
          int k;
          if (paramTLObject != null)
          {
            paramTLObject = (TLRPC.messages_Messages)paramTLObject;
            if (paramTLObject.messages.size() > this.val$count)
              paramTLObject.messages.remove(0);
            j = this.val$max_id;
            i = j;
            if (this.val$offset_date != 0)
            {
              i = j;
              if (!paramTLObject.messages.isEmpty())
              {
                k = ((TLRPC.Message)paramTLObject.messages.get(paramTLObject.messages.size() - 1)).id;
                j = paramTLObject.messages.size() - 1;
              }
            }
          }
          while (true)
          {
            i = k;
            if (j >= 0)
            {
              paramTL_error = (TLRPC.Message)paramTLObject.messages.get(j);
              if (paramTL_error.date > this.val$offset_date)
                i = paramTL_error.id;
            }
            else
            {
              MessagesController.this.processLoadedMessages(paramTLObject, this.val$dialog_id, this.val$count, i, this.val$offset_date, false, this.val$classGuid, this.val$first_unread, this.val$last_message_id, this.val$unread_count, this.val$last_date, this.val$load_type, this.val$isChannel, false, this.val$loadIndex, this.val$queryFromServer);
              return;
            }
            j -= 1;
          }
        }
      });
      ConnectionsManager.getInstance().bindRequestToGuid(paramInt1, paramInt5);
      return;
      if (paramInt6 == 3)
      {
        localTL_messages_getHistory.add_offset = (-paramInt1 / 2);
        continue;
      }
      if (paramInt6 == 1)
      {
        localTL_messages_getHistory.add_offset = (-paramInt1 - 1);
        continue;
      }
      if ((paramInt6 == 2) && (paramInt2 != 0))
      {
        localTL_messages_getHistory.add_offset = (-paramInt1 + 6);
        continue;
      }
      if ((i >= 0) || (paramInt2 == 0) || (!ChatObject.isChannel(getChat(Integer.valueOf(-i)))))
        continue;
      localTL_messages_getHistory.add_offset = -1;
      localTL_messages_getHistory.limit += 1;
    }
  }

  public void loadPeerSettings(TLRPC.User paramUser, TLRPC.Chat paramChat)
  {
    if ((paramUser == null) && (paramChat == null));
    long l;
    while (true)
    {
      return;
      if (paramUser == null)
        break;
      l = paramUser.id;
      label19: if (this.loadingPeerSettings.containsKey(Long.valueOf(l)))
        break label176;
      this.loadingPeerSettings.put(Long.valueOf(l), Boolean.valueOf(true));
      localObject = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
      if (((SharedPreferences)localObject).getInt("spam3_" + l, 0) == 1)
        continue;
      if (!((SharedPreferences)localObject).getBoolean("spam_" + l, false))
        break label198;
      localObject = new TLRPC.TL_messages_hideReportSpam();
      if (paramUser == null)
        break label178;
      ((TLRPC.TL_messages_hideReportSpam)localObject).peer = getInputPeer(paramUser.id);
    }
    while (true)
    {
      ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate(l)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              MessagesController.this.loadingPeerSettings.remove(Long.valueOf(MessagesController.16.this.val$dialogId));
              SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
              localEditor.remove("spam_" + MessagesController.16.this.val$dialogId);
              localEditor.putInt("spam3_" + MessagesController.16.this.val$dialogId, 1);
              localEditor.commit();
            }
          });
        }
      });
      return;
      l = -paramChat.id;
      break label19;
      label176: break;
      label178: if (paramChat == null)
        continue;
      ((TLRPC.TL_messages_hideReportSpam)localObject).peer = getInputPeer(-paramChat.id);
    }
    label198: Object localObject = new TLRPC.TL_messages_getPeerSettings();
    if (paramUser != null)
      ((TLRPC.TL_messages_getPeerSettings)localObject).peer = getInputPeer(paramUser.id);
    while (true)
    {
      ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate(l)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
          {
            public void run()
            {
              MessagesController.this.loadingPeerSettings.remove(Long.valueOf(MessagesController.17.this.val$dialogId));
              SharedPreferences.Editor localEditor;
              if (this.val$response != null)
              {
                TLRPC.TL_peerSettings localTL_peerSettings = (TLRPC.TL_peerSettings)this.val$response;
                localEditor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                if (localTL_peerSettings.report_spam)
                  break label102;
                localEditor.putInt("spam3_" + MessagesController.17.this.val$dialogId, 1);
              }
              while (true)
              {
                localEditor.commit();
                return;
                label102: localEditor.putInt("spam3_" + MessagesController.17.this.val$dialogId, 2);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.peerSettingsDidLoaded, new Object[] { Long.valueOf(MessagesController.17.this.val$dialogId) });
              }
            }
          });
        }
      });
      return;
      if (paramChat == null)
        continue;
      ((TLRPC.TL_messages_getPeerSettings)localObject).peer = getInputPeer(-paramChat.id);
    }
  }

  public void loadPinnedDialogs(long paramLong, ArrayList<Long> paramArrayList)
  {
    if (UserConfig.pinnedDialogsLoaded)
      return;
    TLRPC.TL_messages_getPinnedDialogs localTL_messages_getPinnedDialogs = new TLRPC.TL_messages_getPinnedDialogs();
    ConnectionsManager.getInstance().sendRequest(localTL_messages_getPinnedDialogs, new RequestDelegate(paramArrayList, paramLong)
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        if (paramTLObject != null)
        {
          TLRPC.TL_messages_peerDialogs localTL_messages_peerDialogs = (TLRPC.TL_messages_peerDialogs)paramTLObject;
          TLRPC.TL_messages_dialogs localTL_messages_dialogs = new TLRPC.TL_messages_dialogs();
          localTL_messages_dialogs.users.addAll(localTL_messages_peerDialogs.users);
          localTL_messages_dialogs.chats.addAll(localTL_messages_peerDialogs.chats);
          localTL_messages_dialogs.dialogs.addAll(localTL_messages_peerDialogs.dialogs);
          localTL_messages_dialogs.messages.addAll(localTL_messages_peerDialogs.messages);
          HashMap localHashMap1 = new HashMap();
          paramTLObject = new HashMap();
          HashMap localHashMap2 = new HashMap();
          ArrayList localArrayList = new ArrayList();
          int i = 0;
          while (i < localTL_messages_peerDialogs.users.size())
          {
            paramTL_error = (TLRPC.User)localTL_messages_peerDialogs.users.get(i);
            paramTLObject.put(Integer.valueOf(paramTL_error.id), paramTL_error);
            i += 1;
          }
          i = 0;
          while (i < localTL_messages_peerDialogs.chats.size())
          {
            paramTL_error = (TLRPC.Chat)localTL_messages_peerDialogs.chats.get(i);
            localHashMap2.put(Integer.valueOf(paramTL_error.id), paramTL_error);
            i += 1;
          }
          i = 0;
          Object localObject;
          if (i < localTL_messages_peerDialogs.messages.size())
          {
            paramTL_error = (TLRPC.Message)localTL_messages_peerDialogs.messages.get(i);
            if (paramTL_error.to_id.channel_id != 0)
            {
              localObject = (TLRPC.Chat)localHashMap2.get(Integer.valueOf(paramTL_error.to_id.channel_id));
              if ((localObject == null) || (!((TLRPC.Chat)localObject).left))
                break label325;
            }
            while (true)
            {
              i += 1;
              break;
              if (paramTL_error.to_id.chat_id != 0)
              {
                localObject = (TLRPC.Chat)localHashMap2.get(Integer.valueOf(paramTL_error.to_id.chat_id));
                if ((localObject != null) && (((TLRPC.Chat)localObject).migrated_to != null))
                  continue;
              }
              label325: paramTL_error = new MessageObject(paramTL_error, paramTLObject, localHashMap2, false);
              localHashMap1.put(Long.valueOf(paramTL_error.getDialogId()), paramTL_error);
            }
          }
          i = 0;
          if (i < localTL_messages_peerDialogs.dialogs.size())
          {
            localObject = (TLRPC.TL_dialog)localTL_messages_peerDialogs.dialogs.get(i);
            if (((TLRPC.TL_dialog)localObject).id == 0L)
            {
              if (((TLRPC.TL_dialog)localObject).peer.user_id != 0)
                ((TLRPC.TL_dialog)localObject).id = ((TLRPC.TL_dialog)localObject).peer.user_id;
            }
            else
            {
              label418: localArrayList.add(Long.valueOf(((TLRPC.TL_dialog)localObject).id));
              if (!DialogObject.isChannel((TLRPC.TL_dialog)localObject))
                break label535;
              paramTLObject = (TLRPC.Chat)localHashMap2.get(Integer.valueOf(-(int)((TLRPC.TL_dialog)localObject).id));
              if ((paramTLObject == null) || (!paramTLObject.left))
                break label574;
            }
            while (true)
            {
              i += 1;
              break;
              if (((TLRPC.TL_dialog)localObject).peer.chat_id != 0)
              {
                ((TLRPC.TL_dialog)localObject).id = (-((TLRPC.TL_dialog)localObject).peer.chat_id);
                break label418;
              }
              if (((TLRPC.TL_dialog)localObject).peer.channel_id == 0)
                break label418;
              ((TLRPC.TL_dialog)localObject).id = (-((TLRPC.TL_dialog)localObject).peer.channel_id);
              break label418;
              label535: if ((int)((TLRPC.TL_dialog)localObject).id < 0)
              {
                paramTLObject = (TLRPC.Chat)localHashMap2.get(Integer.valueOf(-(int)((TLRPC.TL_dialog)localObject).id));
                if ((paramTLObject != null) && (paramTLObject.migrated_to != null))
                  continue;
              }
              label574: if (((TLRPC.TL_dialog)localObject).last_message_date == 0)
              {
                paramTLObject = (MessageObject)localHashMap1.get(Long.valueOf(((TLRPC.TL_dialog)localObject).id));
                if (paramTLObject != null)
                  ((TLRPC.TL_dialog)localObject).last_message_date = paramTLObject.messageOwner.date;
              }
              paramTL_error = (Integer)MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(((TLRPC.TL_dialog)localObject).id));
              paramTLObject = paramTL_error;
              if (paramTL_error == null)
                paramTLObject = Integer.valueOf(0);
              MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(((TLRPC.TL_dialog)localObject).id), Integer.valueOf(Math.max(paramTLObject.intValue(), ((TLRPC.TL_dialog)localObject).read_inbox_max_id)));
              paramTL_error = (Integer)MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(((TLRPC.TL_dialog)localObject).id));
              paramTLObject = paramTL_error;
              if (paramTL_error == null)
                paramTLObject = Integer.valueOf(0);
              MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(((TLRPC.TL_dialog)localObject).id), Integer.valueOf(Math.max(paramTLObject.intValue(), ((TLRPC.TL_dialog)localObject).read_outbox_max_id)));
            }
          }
          MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(localTL_messages_peerDialogs, localArrayList, localHashMap1, localTL_messages_dialogs)
          {
            public void run()
            {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  MessagesController.this.applyDialogsNotificationsSettings(MessagesController.96.1.this.val$res.dialogs);
                  int n = 0;
                  int m = 0;
                  HashMap localHashMap = new HashMap();
                  ArrayList localArrayList1 = new ArrayList();
                  int i = 0;
                  int k = 0;
                  int j = 0;
                  while (j < MessagesController.this.dialogs.size())
                  {
                    localObject1 = (TLRPC.TL_dialog)MessagesController.this.dialogs.get(j);
                    if ((int)((TLRPC.TL_dialog)localObject1).id == 0)
                    {
                      j += 1;
                      continue;
                    }
                    if (((TLRPC.TL_dialog)localObject1).pinned)
                      break label202;
                  }
                  ArrayList localArrayList2 = new ArrayList();
                  if (MessagesController.96.this.val$order != null);
                  for (Object localObject1 = MessagesController.96.this.val$order; ; localObject1 = MessagesController.96.1.this.val$newPinnedOrder)
                  {
                    if (((ArrayList)localObject1).size() < localArrayList1.size())
                      ((ArrayList)localObject1).add(Long.valueOf(0L));
                    while (localArrayList1.size() < ((ArrayList)localObject1).size())
                      localArrayList1.add(0, Long.valueOf(0L));
                    label202: k = Math.max(((TLRPC.TL_dialog)localObject1).pinnedNum, k);
                    localHashMap.put(Long.valueOf(((TLRPC.TL_dialog)localObject1).id), Integer.valueOf(((TLRPC.TL_dialog)localObject1).pinnedNum));
                    localArrayList1.add(Long.valueOf(((TLRPC.TL_dialog)localObject1).id));
                    ((TLRPC.TL_dialog)localObject1).pinned = false;
                    ((TLRPC.TL_dialog)localObject1).pinnedNum = 0;
                    i = 1;
                    break;
                  }
                  if (!MessagesController.96.1.this.val$res.dialogs.isEmpty())
                  {
                    MessagesController.this.putUsers(MessagesController.96.1.this.val$res.users, false);
                    MessagesController.this.putChats(MessagesController.96.1.this.val$res.chats, false);
                    j = i;
                    n = 0;
                    i = m;
                    m = n;
                    n = j;
                    j = i;
                    if (m < MessagesController.96.1.this.val$res.dialogs.size())
                    {
                      TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)MessagesController.96.1.this.val$res.dialogs.get(m);
                      Object localObject2;
                      if (MessagesController.96.this.val$newDialogId != 0L)
                      {
                        localObject2 = (Integer)localHashMap.get(Long.valueOf(localTL_dialog.id));
                        if (localObject2 != null)
                          localTL_dialog.pinnedNum = ((Integer)localObject2).intValue();
                        label444: if (localTL_dialog.pinnedNum == 0)
                          localTL_dialog.pinnedNum = (MessagesController.96.1.this.val$res.dialogs.size() - m + k);
                        localArrayList2.add(Long.valueOf(localTL_dialog.id));
                        localObject2 = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(Long.valueOf(localTL_dialog.id));
                        if (localObject2 == null)
                          break label692;
                        ((TLRPC.TL_dialog)localObject2).pinned = true;
                        ((TLRPC.TL_dialog)localObject2).pinnedNum = localTL_dialog.pinnedNum;
                        MessagesStorage.getInstance().setDialogPinned(localTL_dialog.id, localTL_dialog.pinnedNum);
                      }
                      while (true)
                      {
                        m += 1;
                        j = 1;
                        break;
                        j = localArrayList1.indexOf(Long.valueOf(localTL_dialog.id));
                        n = ((ArrayList)localObject1).indexOf(Long.valueOf(localTL_dialog.id));
                        if ((j == -1) || (n == -1))
                          break label444;
                        if (j == n)
                        {
                          localObject2 = (Integer)localHashMap.get(Long.valueOf(localTL_dialog.id));
                          if (localObject2 == null)
                            break label444;
                          localTL_dialog.pinnedNum = ((Integer)localObject2).intValue();
                          break label444;
                        }
                        localObject2 = (Integer)localHashMap.get(Long.valueOf(((Long)localArrayList1.get(n)).longValue()));
                        if (localObject2 == null)
                          break label444;
                        localTL_dialog.pinnedNum = ((Integer)localObject2).intValue();
                        break label444;
                        label692: MessagesController.this.dialogs_dict.put(Long.valueOf(localTL_dialog.id), localTL_dialog);
                        localObject2 = (MessageObject)MessagesController.96.1.this.val$new_dialogMessage.get(Long.valueOf(localTL_dialog.id));
                        MessagesController.this.dialogMessage.put(Long.valueOf(localTL_dialog.id), localObject2);
                        if ((localObject2 != null) && (((MessageObject)localObject2).messageOwner.to_id.channel_id == 0))
                        {
                          MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(((MessageObject)localObject2).getId()), localObject2);
                          if (((MessageObject)localObject2).messageOwner.random_id != 0L)
                            MessagesController.this.dialogMessagesByRandomIds.put(Long.valueOf(((MessageObject)localObject2).messageOwner.random_id), localObject2);
                        }
                        i = 1;
                      }
                    }
                  }
                  else
                  {
                    j = n;
                    n = i;
                  }
                  if (n != 0)
                  {
                    if (j != 0)
                    {
                      MessagesController.this.dialogs.clear();
                      MessagesController.this.dialogs.addAll(MessagesController.this.dialogs_dict.values());
                    }
                    MessagesController.this.sortDialogs(null);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                  }
                  MessagesStorage.getInstance().unpinAllDialogsExceptNew(localArrayList2);
                  MessagesStorage.getInstance().putDialogs(MessagesController.96.1.this.val$toCache, true);
                  UserConfig.pinnedDialogsLoaded = true;
                  UserConfig.saveConfig(false);
                }
              });
            }
          });
        }
      }
    });
  }

  // ERROR //
  protected void loadUnknownChannel(TLRPC.Chat paramChat, long paramLong)
  {
    // Byte code:
    //   0: aload_1
    //   1: instanceof 1488
    //   4: ifeq +20 -> 24
    //   7: aload_0
    //   8: getfield 683	org/vidogram/messenger/MessagesController:gettingUnknownChannels	Ljava/util/HashMap;
    //   11: aload_1
    //   12: getfield 1494	org/vidogram/tgnet/TLRPC$Chat:id	I
    //   15: invokestatic 1246	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   18: invokevirtual 2246	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   21: ifeq +4 -> 25
    //   24: return
    //   25: aload_1
    //   26: getfield 1500	org/vidogram/tgnet/TLRPC$Chat:access_hash	J
    //   29: lconst_0
    //   30: lcmp
    //   31: ifne +17 -> 48
    //   34: lload_2
    //   35: lconst_0
    //   36: lcmp
    //   37: ifeq -13 -> 24
    //   40: invokestatic 808	org/vidogram/messenger/MessagesStorage:getInstance	()Lorg/vidogram/messenger/MessagesStorage;
    //   43: lload_2
    //   44: invokevirtual 2642	org/vidogram/messenger/MessagesStorage:removePendingTask	(J)V
    //   47: return
    //   48: new 1429	org/vidogram/tgnet/TLRPC$TL_inputPeerChannel
    //   51: dup
    //   52: invokespecial 1505	org/vidogram/tgnet/TLRPC$TL_inputPeerChannel:<init>	()V
    //   55: astore 6
    //   57: aload 6
    //   59: aload_1
    //   60: getfield 1494	org/vidogram/tgnet/TLRPC$Chat:id	I
    //   63: putfield 2930	org/vidogram/tgnet/TLRPC$TL_inputPeerChannel:channel_id	I
    //   66: aload 6
    //   68: aload_1
    //   69: getfield 1500	org/vidogram/tgnet/TLRPC$Chat:access_hash	J
    //   72: putfield 2931	org/vidogram/tgnet/TLRPC$TL_inputPeerChannel:access_hash	J
    //   75: aload_0
    //   76: getfield 683	org/vidogram/messenger/MessagesController:gettingUnknownChannels	Ljava/util/HashMap;
    //   79: aload_1
    //   80: getfield 1494	org/vidogram/tgnet/TLRPC$Chat:id	I
    //   83: invokestatic 1246	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   86: iconst_1
    //   87: invokestatic 1418	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   90: invokevirtual 1800	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   93: pop
    //   94: new 1864	org/vidogram/tgnet/TLRPC$TL_messages_getPeerDialogs
    //   97: dup
    //   98: invokespecial 1865	org/vidogram/tgnet/TLRPC$TL_messages_getPeerDialogs:<init>	()V
    //   101: astore 8
    //   103: aload 8
    //   105: getfield 1868	org/vidogram/tgnet/TLRPC$TL_messages_getPeerDialogs:peers	Ljava/util/ArrayList;
    //   108: aload 6
    //   110: invokevirtual 940	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   113: pop
    //   114: lload_2
    //   115: lstore 4
    //   117: lload_2
    //   118: lconst_0
    //   119: lcmp
    //   120: ifne +40 -> 160
    //   123: new 2253	org/vidogram/tgnet/NativeByteBuffer
    //   126: dup
    //   127: aload_1
    //   128: invokevirtual 2932	org/vidogram/tgnet/TLRPC$Chat:getObjectSize	()I
    //   131: iconst_4
    //   132: iadd
    //   133: invokespecial 2258	org/vidogram/tgnet/NativeByteBuffer:<init>	(I)V
    //   136: astore 6
    //   138: aload 6
    //   140: iconst_0
    //   141: invokevirtual 2261	org/vidogram/tgnet/NativeByteBuffer:writeInt32	(I)V
    //   144: aload_1
    //   145: aload 6
    //   147: invokevirtual 2933	org/vidogram/tgnet/TLRPC$Chat:serializeToStream	(Lorg/vidogram/tgnet/AbstractSerializedData;)V
    //   150: invokestatic 808	org/vidogram/messenger/MessagesStorage:getInstance	()Lorg/vidogram/messenger/MessagesStorage;
    //   153: aload 6
    //   155: invokevirtual 2286	org/vidogram/messenger/MessagesStorage:createPendingTask	(Lorg/vidogram/tgnet/NativeByteBuffer;)J
    //   158: lstore 4
    //   160: invokestatic 1167	org/vidogram/tgnet/ConnectionsManager:getInstance	()Lorg/vidogram/tgnet/ConnectionsManager;
    //   163: aload 8
    //   165: new 330	org/vidogram/messenger/MessagesController$91
    //   168: dup
    //   169: aload_0
    //   170: lload 4
    //   172: aload_1
    //   173: invokespecial 2936	org/vidogram/messenger/MessagesController$91:<init>	(Lorg/vidogram/messenger/MessagesController;JLorg/vidogram/tgnet/TLRPC$Chat;)V
    //   176: invokevirtual 1714	org/vidogram/tgnet/ConnectionsManager:sendRequest	(Lorg/vidogram/tgnet/TLObject;Lorg/vidogram/tgnet/RequestDelegate;)I
    //   179: pop
    //   180: return
    //   181: astore 7
    //   183: aconst_null
    //   184: astore 6
    //   186: aload 7
    //   188: invokestatic 946	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   191: goto -41 -> 150
    //   194: astore 7
    //   196: goto -10 -> 186
    //
    // Exception table:
    //   from	to	target	type
    //   123	138	181	java/lang/Exception
    //   138	150	194	java/lang/Exception
  }

  public void markChannelDialogMessageAsDeleted(ArrayList<Integer> paramArrayList, int paramInt)
  {
    MessageObject localMessageObject = (MessageObject)this.dialogMessage.get(Long.valueOf(-paramInt));
    if (localMessageObject != null)
      paramInt = 0;
    while (true)
    {
      if (paramInt < paramArrayList.size())
      {
        Integer localInteger = (Integer)paramArrayList.get(paramInt);
        if (localMessageObject.getId() == localInteger.intValue())
          localMessageObject.deleted = true;
      }
      else
      {
        return;
      }
      paramInt += 1;
    }
  }

  public void markDialogAsRead(long paramLong, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = (int)paramLong;
    int j = (int)(paramLong >> 32);
    if (i != 0)
      if ((paramInt2 != 0) && (j != 1));
    Object localObject1;
    do
    {
      Object localObject2;
      do
      {
        return;
        localObject1 = getInputPeer(i);
        long l = paramInt2;
        if ((localObject1 instanceof TLRPC.TL_inputPeerChannel))
        {
          localObject1 = new TLRPC.TL_channels_readHistory();
          ((TLRPC.TL_channels_readHistory)localObject1).channel = getInputChannel(-i);
          ((TLRPC.TL_channels_readHistory)localObject1).max_id = paramInt2;
          req = (TLObject)localObject1;
          l |= -i << 32;
        }
        while (true)
        {
          localObject2 = (Integer)this.dialogs_read_inbox_max.get(Long.valueOf(paramLong));
          localObject1 = localObject2;
          if (localObject2 == null)
            localObject1 = Integer.valueOf(0);
          this.dialogs_read_inbox_max.put(Long.valueOf(paramLong), Integer.valueOf(Math.max(((Integer)localObject1).intValue(), paramInt2)));
          MessagesStorage.getInstance().processPendingRead(paramLong, l, paramInt3);
          MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramLong, paramBoolean2, paramInt2)
          {
            public void run()
            {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  Object localObject = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(Long.valueOf(MessagesController.62.this.val$dialog_id));
                  if (localObject != null)
                  {
                    int i = ((TLRPC.TL_dialog)localObject).unread_count;
                    ((TLRPC.TL_dialog)localObject).unread_count = 0;
                    MessagesController.this.updateUnreadMessage((TLRPC.TL_dialog)localObject, i);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(256) });
                  }
                  if (!MessagesController.62.this.val$popup)
                  {
                    NotificationsController.getInstance().processReadMessages(null, MessagesController.62.this.val$dialog_id, 0, MessagesController.62.this.val$max_positive_id, false);
                    localObject = new HashMap();
                    ((HashMap)localObject).put(Long.valueOf(MessagesController.62.this.val$dialog_id), Integer.valueOf(0));
                    NotificationsController.getInstance().processDialogsUpdateRead((HashMap)localObject);
                    return;
                  }
                  NotificationsController.getInstance().processReadMessages(null, MessagesController.62.this.val$dialog_id, 0, MessagesController.62.this.val$max_positive_id, true);
                  localObject = new HashMap();
                  ((HashMap)localObject).put(Long.valueOf(MessagesController.62.this.val$dialog_id), Integer.valueOf(-1));
                  NotificationsController.getInstance().processDialogsUpdateRead((HashMap)localObject);
                }
              });
            }
          });
          localObject1 = b.a(ApplicationLoader.applicationContext);
          if ((paramInt2 == 2147483647) || (((b)localObject1).b()))
            break;
          ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate()
          {
            public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
            {
              if ((paramTL_error == null) && ((paramTLObject instanceof TLRPC.TL_messages_affectedMessages)))
              {
                paramTLObject = (TLRPC.TL_messages_affectedMessages)paramTLObject;
                MessagesController.this.processNewDifferenceParams(-1, paramTLObject.pts, -1, paramTLObject.pts_count);
              }
            }
          });
          return;
          localObject2 = new TLRPC.TL_messages_readHistory();
          ((TLRPC.TL_messages_readHistory)localObject2).peer = ((TLRPC.InputPeer)localObject1);
          ((TLRPC.TL_messages_readHistory)localObject2).max_id = paramInt2;
          req = (TLObject)localObject2;
        }
      }
      while (paramInt3 == 0);
      localObject1 = getEncryptedChat(Integer.valueOf(j));
      if ((((TLRPC.EncryptedChat)localObject1).auth_key != null) && (((TLRPC.EncryptedChat)localObject1).auth_key.length > 1) && ((localObject1 instanceof TLRPC.TL_encryptedChat)))
      {
        localObject2 = new TLRPC.TL_messages_readEncryptedHistory();
        ((TLRPC.TL_messages_readEncryptedHistory)localObject2).peer = new TLRPC.TL_inputEncryptedChat();
        ((TLRPC.TL_messages_readEncryptedHistory)localObject2).peer.chat_id = ((TLRPC.EncryptedChat)localObject1).id;
        ((TLRPC.TL_messages_readEncryptedHistory)localObject2).peer.access_hash = ((TLRPC.EncryptedChat)localObject1).access_hash;
        ((TLRPC.TL_messages_readEncryptedHistory)localObject2).max_date = paramInt3;
        ConnectionsManager.getInstance().sendRequest((TLObject)localObject2, new RequestDelegate()
        {
          public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
          {
          }
        });
      }
      MessagesStorage.getInstance().processPendingRead(paramLong, paramInt1, paramInt3);
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramLong, paramInt3, paramBoolean2)
      {
        public void run()
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              NotificationsController.getInstance().processReadMessages(null, MessagesController.65.this.val$dialog_id, MessagesController.65.this.val$max_date, 0, MessagesController.65.this.val$popup);
              Object localObject = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(Long.valueOf(MessagesController.65.this.val$dialog_id));
              if (localObject != null)
              {
                int i = ((TLRPC.TL_dialog)localObject).unread_count;
                ((TLRPC.TL_dialog)localObject).unread_count = 0;
                MessagesController.this.updateUnreadMessage((TLRPC.TL_dialog)localObject, i);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(256) });
              }
              localObject = new HashMap();
              ((HashMap)localObject).put(Long.valueOf(MessagesController.65.this.val$dialog_id), Integer.valueOf(0));
              NotificationsController.getInstance().processDialogsUpdateRead((HashMap)localObject);
            }
          });
        }
      });
    }
    while ((((TLRPC.EncryptedChat)localObject1).ttl <= 0) || (!paramBoolean1));
    paramInt1 = Math.max(ConnectionsManager.getInstance().getCurrentTime(), paramInt3);
    MessagesStorage.getInstance().createTaskForSecretChat(((TLRPC.EncryptedChat)localObject1).id, paramInt1, paramInt1, 0, null);
  }

  public void markMessageAsRead(long paramLong1, long paramLong2, int paramInt)
  {
    if ((paramLong2 == 0L) || (paramLong1 == 0L) || ((paramInt <= 0) && (paramInt != -2147483648)));
    TLRPC.EncryptedChat localEncryptedChat;
    ArrayList localArrayList;
    do
    {
      do
      {
        int i;
        int j;
        do
        {
          return;
          i = (int)paramLong1;
          j = (int)(paramLong1 >> 32);
        }
        while (i != 0);
        localEncryptedChat = getEncryptedChat(Integer.valueOf(j));
      }
      while (localEncryptedChat == null);
      localArrayList = new ArrayList();
      localArrayList.add(Long.valueOf(paramLong2));
      SecretChatHelper.getInstance().sendMessagesReadMessage(localEncryptedChat, localArrayList, null);
    }
    while (paramInt <= 0);
    paramInt = ConnectionsManager.getInstance().getCurrentTime();
    MessagesStorage.getInstance().createTaskForSecretChat(localEncryptedChat.id, paramInt, paramInt, 0, localArrayList);
  }

  public void markMessageContentAsRead(MessageObject paramMessageObject)
  {
    Object localObject = new ArrayList();
    long l2 = paramMessageObject.getId();
    long l1 = l2;
    if (paramMessageObject.messageOwner.to_id.channel_id != 0)
      l1 = l2 | paramMessageObject.messageOwner.to_id.channel_id << 32;
    ((ArrayList)localObject).add(Long.valueOf(l1));
    MessagesStorage.getInstance().markMessagesContentAsRead((ArrayList)localObject);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagesReadContent, new Object[] { localObject });
    if (paramMessageObject.getId() < 0)
    {
      markMessageAsRead(paramMessageObject.getDialogId(), paramMessageObject.messageOwner.random_id, -2147483648);
      return;
    }
    localObject = new TLRPC.TL_messages_readMessageContents();
    ((TLRPC.TL_messages_readMessageContents)localObject).id.add(Integer.valueOf(paramMessageObject.getId()));
    ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        if (paramTL_error == null)
        {
          paramTLObject = (TLRPC.TL_messages_affectedMessages)paramTLObject;
          MessagesController.this.processNewDifferenceParams(-1, paramTLObject.pts, -1, paramTLObject.pts_count);
        }
      }
    });
  }

  public void performLogout(boolean paramBoolean)
  {
    d.a(ApplicationLoader.applicationContext).a();
    ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().clear().commit();
    ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit().putLong("lastGifLoadTime", 0L).putLong("lastStickersLoadTime", 0L).commit();
    ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().remove("gifhint").commit();
    if (paramBoolean)
    {
      unregistedPush();
      TLRPC.TL_auth_logOut localTL_auth_logOut = new TLRPC.TL_auth_logOut();
      ConnectionsManager.getInstance().sendRequest(localTL_auth_logOut, new RequestDelegate()
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          ConnectionsManager.getInstance().cleanup();
        }
      });
    }
    while (true)
    {
      UserConfig.clearConfig();
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.appDidLogout, new Object[0]);
      MessagesStorage.getInstance().cleanup(false);
      cleanup();
      ContactsController.getInstance().deleteAllAppAccounts();
      return;
      ConnectionsManager.getInstance().cleanup();
    }
  }

  public void pinChannelMessage(TLRPC.Chat paramChat, int paramInt, boolean paramBoolean)
  {
    TLRPC.TL_channels_updatePinnedMessage localTL_channels_updatePinnedMessage = new TLRPC.TL_channels_updatePinnedMessage();
    localTL_channels_updatePinnedMessage.channel = getInputChannel(paramChat);
    localTL_channels_updatePinnedMessage.id = paramInt;
    if (!paramBoolean);
    for (paramBoolean = true; ; paramBoolean = false)
    {
      localTL_channels_updatePinnedMessage.silent = paramBoolean;
      ConnectionsManager.getInstance().sendRequest(localTL_channels_updatePinnedMessage, new RequestDelegate()
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          if (paramTL_error == null)
          {
            paramTLObject = (TLRPC.Updates)paramTLObject;
            MessagesController.this.processUpdates(paramTLObject, false);
          }
        }
      });
      return;
    }
  }

  // ERROR //
  public boolean pinDialog(long paramLong1, boolean paramBoolean, TLRPC.InputPeer paramInputPeer, long paramLong2)
  {
    // Byte code:
    //   0: lload_1
    //   1: l2i
    //   2: istore 9
    //   4: aload_0
    //   5: getfield 635	org/vidogram/messenger/MessagesController:dialogs_dict	Ljava/util/concurrent/ConcurrentHashMap;
    //   8: lload_1
    //   9: invokestatic 1136	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   12: invokevirtual 1140	java/util/concurrent/ConcurrentHashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   15: checkcast 1142	org/vidogram/tgnet/TLRPC$TL_dialog
    //   18: astore 13
    //   20: aload 13
    //   22: ifnull +12 -> 34
    //   25: aload 13
    //   27: getfield 2192	org/vidogram/tgnet/TLRPC$TL_dialog:pinned	Z
    //   30: iload_3
    //   31: if_icmpne +12 -> 43
    //   34: aload 13
    //   36: ifnull +5 -> 41
    //   39: iconst_1
    //   40: ireturn
    //   41: iconst_0
    //   42: ireturn
    //   43: aload 13
    //   45: iload_3
    //   46: putfield 2192	org/vidogram/tgnet/TLRPC$TL_dialog:pinned	Z
    //   49: iload_3
    //   50: ifeq +188 -> 238
    //   53: iconst_0
    //   54: istore 8
    //   56: iconst_0
    //   57: istore 7
    //   59: iload 7
    //   61: aload_0
    //   62: getfield 625	org/vidogram/messenger/MessagesController:dialogs	Ljava/util/ArrayList;
    //   65: invokevirtual 1206	java/util/ArrayList:size	()I
    //   68: if_icmpge +25 -> 93
    //   71: aload_0
    //   72: getfield 625	org/vidogram/messenger/MessagesController:dialogs	Ljava/util/ArrayList;
    //   75: iload 7
    //   77: invokevirtual 1209	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   80: checkcast 1142	org/vidogram/tgnet/TLRPC$TL_dialog
    //   83: astore 12
    //   85: aload 12
    //   87: getfield 2192	org/vidogram/tgnet/TLRPC$TL_dialog:pinned	Z
    //   90: ifne +127 -> 217
    //   93: aload 13
    //   95: iload 8
    //   97: iconst_1
    //   98: iadd
    //   99: putfield 2278	org/vidogram/tgnet/TLRPC$TL_dialog:pinnedNum	I
    //   102: aload_0
    //   103: aconst_null
    //   104: invokevirtual 3078	org/vidogram/messenger/MessagesController:sortDialogs	(Ljava/util/HashMap;)V
    //   107: iload_3
    //   108: ifne +41 -> 149
    //   111: aload_0
    //   112: getfield 625	org/vidogram/messenger/MessagesController:dialogs	Ljava/util/ArrayList;
    //   115: aload_0
    //   116: getfield 625	org/vidogram/messenger/MessagesController:dialogs	Ljava/util/ArrayList;
    //   119: invokevirtual 1206	java/util/ArrayList:size	()I
    //   122: iconst_1
    //   123: isub
    //   124: invokevirtual 1209	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   127: aload 13
    //   129: if_acmpne +20 -> 149
    //   132: aload_0
    //   133: getfield 625	org/vidogram/messenger/MessagesController:dialogs	Ljava/util/ArrayList;
    //   136: aload_0
    //   137: getfield 625	org/vidogram/messenger/MessagesController:dialogs	Ljava/util/ArrayList;
    //   140: invokevirtual 1206	java/util/ArrayList:size	()I
    //   143: iconst_1
    //   144: isub
    //   145: invokevirtual 1809	java/util/ArrayList:remove	(I)Ljava/lang/Object;
    //   148: pop
    //   149: invokestatic 813	org/vidogram/messenger/NotificationCenter:getInstance	()Lorg/vidogram/messenger/NotificationCenter;
    //   152: getstatic 1410	org/vidogram/messenger/NotificationCenter:dialogsNeedReload	I
    //   155: iconst_0
    //   156: anewarray 4	java/lang/Object
    //   159: invokevirtual 1199	org/vidogram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   162: iload 9
    //   164: ifeq +172 -> 336
    //   167: lload 5
    //   169: ldc2_w 3079
    //   172: lcmp
    //   173: ifeq +163 -> 336
    //   176: new 3082	org/vidogram/tgnet/TLRPC$TL_messages_toggleDialogPin
    //   179: dup
    //   180: invokespecial 3083	org/vidogram/tgnet/TLRPC$TL_messages_toggleDialogPin:<init>	()V
    //   183: astore 14
    //   185: aload 14
    //   187: iload_3
    //   188: putfield 3084	org/vidogram/tgnet/TLRPC$TL_messages_toggleDialogPin:pinned	Z
    //   191: aload 4
    //   193: astore 12
    //   195: aload 4
    //   197: ifnonnull +10 -> 207
    //   200: iload 9
    //   202: invokestatic 1427	org/vidogram/messenger/MessagesController:getInputPeer	(I)Lorg/vidogram/tgnet/TLRPC$InputPeer;
    //   205: astore 12
    //   207: aload 12
    //   209: instanceof 1706
    //   212: ifeq +35 -> 247
    //   215: iconst_0
    //   216: ireturn
    //   217: aload 12
    //   219: getfield 2278	org/vidogram/tgnet/TLRPC$TL_dialog:pinnedNum	I
    //   222: iload 8
    //   224: invokestatic 1316	java/lang/Math:max	(II)I
    //   227: istore 8
    //   229: iload 7
    //   231: iconst_1
    //   232: iadd
    //   233: istore 7
    //   235: goto -176 -> 59
    //   238: aload 13
    //   240: iconst_0
    //   241: putfield 2278	org/vidogram/tgnet/TLRPC$TL_dialog:pinnedNum	I
    //   244: goto -142 -> 102
    //   247: aload 14
    //   249: aload 12
    //   251: putfield 3085	org/vidogram/tgnet/TLRPC$TL_messages_toggleDialogPin:peer	Lorg/vidogram/tgnet/TLRPC$InputPeer;
    //   254: lload 5
    //   256: lstore 10
    //   258: lload 5
    //   260: lconst_0
    //   261: lcmp
    //   262: ifne +55 -> 317
    //   265: new 2253	org/vidogram/tgnet/NativeByteBuffer
    //   268: dup
    //   269: aload 12
    //   271: invokevirtual 2256	org/vidogram/tgnet/TLRPC$InputPeer:getObjectSize	()I
    //   274: bipush 16
    //   276: iadd
    //   277: invokespecial 2258	org/vidogram/tgnet/NativeByteBuffer:<init>	(I)V
    //   280: astore 4
    //   282: aload 4
    //   284: iconst_1
    //   285: invokevirtual 2261	org/vidogram/tgnet/NativeByteBuffer:writeInt32	(I)V
    //   288: aload 4
    //   290: lload_1
    //   291: invokevirtual 2264	org/vidogram/tgnet/NativeByteBuffer:writeInt64	(J)V
    //   294: aload 4
    //   296: iload_3
    //   297: invokevirtual 2275	org/vidogram/tgnet/NativeByteBuffer:writeBool	(Z)V
    //   300: aload 12
    //   302: aload 4
    //   304: invokevirtual 2282	org/vidogram/tgnet/TLRPC$InputPeer:serializeToStream	(Lorg/vidogram/tgnet/AbstractSerializedData;)V
    //   307: invokestatic 808	org/vidogram/messenger/MessagesStorage:getInstance	()Lorg/vidogram/messenger/MessagesStorage;
    //   310: aload 4
    //   312: invokevirtual 2286	org/vidogram/messenger/MessagesStorage:createPendingTask	(Lorg/vidogram/tgnet/NativeByteBuffer;)J
    //   315: lstore 10
    //   317: invokestatic 1167	org/vidogram/tgnet/ConnectionsManager:getInstance	()Lorg/vidogram/tgnet/ConnectionsManager;
    //   320: aload 14
    //   322: new 368	org/vidogram/messenger/MessagesController$95
    //   325: dup
    //   326: aload_0
    //   327: lload 10
    //   329: invokespecial 3086	org/vidogram/messenger/MessagesController$95:<init>	(Lorg/vidogram/messenger/MessagesController;J)V
    //   332: invokevirtual 1714	org/vidogram/tgnet/ConnectionsManager:sendRequest	(Lorg/vidogram/tgnet/TLObject;Lorg/vidogram/tgnet/RequestDelegate;)I
    //   335: pop
    //   336: invokestatic 808	org/vidogram/messenger/MessagesStorage:getInstance	()Lorg/vidogram/messenger/MessagesStorage;
    //   339: lload_1
    //   340: aload 13
    //   342: getfield 2278	org/vidogram/tgnet/TLRPC$TL_dialog:pinnedNum	I
    //   345: invokevirtual 3089	org/vidogram/messenger/MessagesStorage:setDialogPinned	(JI)V
    //   348: iconst_1
    //   349: ireturn
    //   350: astore 12
    //   352: aconst_null
    //   353: astore 4
    //   355: aload 12
    //   357: invokestatic 946	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   360: goto -53 -> 307
    //   363: astore 12
    //   365: goto -10 -> 355
    //
    // Exception table:
    //   from	to	target	type
    //   265	282	350	java/lang/Exception
    //   282	307	363	java/lang/Exception
  }

  public void processChatInfo(int paramInt, TLRPC.ChatFull paramChatFull, ArrayList<TLRPC.User> paramArrayList, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, MessageObject paramMessageObject)
  {
    if ((paramBoolean1) && (paramInt > 0) && (!paramBoolean3))
      loadFullChat(paramInt, 0, paramBoolean2);
    if (paramChatFull != null)
      AndroidUtilities.runOnUIThread(new Runnable(paramArrayList, paramBoolean1, paramChatFull, paramBoolean3, paramMessageObject)
      {
        public void run()
        {
          MessagesController.this.putUsers(this.val$usersArr, this.val$fromCache);
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatInfoDidLoaded, new Object[] { this.val$info, Integer.valueOf(0), Boolean.valueOf(this.val$byChannelUsers), this.val$pinnedMessageObject });
        }
      });
  }

  public void processDialogsUpdate(TLRPC.messages_Dialogs parammessages_Dialogs, ArrayList<TLRPC.EncryptedChat> paramArrayList)
  {
    Utilities.stageQueue.postRunnable(new Runnable(parammessages_Dialogs)
    {
      public void run()
      {
        HashMap localHashMap1 = new HashMap();
        HashMap localHashMap2 = new HashMap();
        Object localObject1 = new HashMap();
        HashMap localHashMap3 = new HashMap();
        HashMap localHashMap4 = new HashMap();
        int i = 0;
        Object localObject2;
        while (i < this.val$dialogsRes.users.size())
        {
          localObject2 = (TLRPC.User)this.val$dialogsRes.users.get(i);
          ((HashMap)localObject1).put(Integer.valueOf(((TLRPC.User)localObject2).id), localObject2);
          i += 1;
        }
        i = 0;
        while (i < this.val$dialogsRes.chats.size())
        {
          localObject2 = (TLRPC.Chat)this.val$dialogsRes.chats.get(i);
          localHashMap3.put(Integer.valueOf(((TLRPC.Chat)localObject2).id), localObject2);
          i += 1;
        }
        i = 0;
        Object localObject3;
        if (i < this.val$dialogsRes.messages.size())
        {
          localObject2 = (TLRPC.Message)this.val$dialogsRes.messages.get(i);
          if (((TLRPC.Message)localObject2).to_id.channel_id != 0)
          {
            localObject3 = (TLRPC.Chat)localHashMap3.get(Integer.valueOf(((TLRPC.Message)localObject2).to_id.channel_id));
            if ((localObject3 == null) || (!((TLRPC.Chat)localObject3).left))
              break label271;
          }
          while (true)
          {
            i += 1;
            break;
            if (((TLRPC.Message)localObject2).to_id.chat_id != 0)
            {
              localObject3 = (TLRPC.Chat)localHashMap3.get(Integer.valueOf(((TLRPC.Message)localObject2).to_id.chat_id));
              if ((localObject3 != null) && (((TLRPC.Chat)localObject3).migrated_to != null))
                continue;
            }
            label271: localObject2 = new MessageObject((TLRPC.Message)localObject2, (AbstractMap)localObject1, localHashMap3, false);
            localHashMap2.put(Long.valueOf(((MessageObject)localObject2).getDialogId()), localObject2);
          }
        }
        i = 0;
        if (i < this.val$dialogsRes.dialogs.size())
        {
          localObject3 = (TLRPC.TL_dialog)this.val$dialogsRes.dialogs.get(i);
          if (((TLRPC.TL_dialog)localObject3).id == 0L)
          {
            if (((TLRPC.TL_dialog)localObject3).peer.user_id != 0)
              ((TLRPC.TL_dialog)localObject3).id = ((TLRPC.TL_dialog)localObject3).peer.user_id;
          }
          else
          {
            label368: if (!DialogObject.isChannel((TLRPC.TL_dialog)localObject3))
              break label471;
            localObject1 = (TLRPC.Chat)localHashMap3.get(Integer.valueOf(-(int)((TLRPC.TL_dialog)localObject3).id));
            if ((localObject1 == null) || (!((TLRPC.Chat)localObject1).left))
              break label510;
          }
          while (true)
          {
            i += 1;
            break;
            if (((TLRPC.TL_dialog)localObject3).peer.chat_id != 0)
            {
              ((TLRPC.TL_dialog)localObject3).id = (-((TLRPC.TL_dialog)localObject3).peer.chat_id);
              break label368;
            }
            if (((TLRPC.TL_dialog)localObject3).peer.channel_id == 0)
              break label368;
            ((TLRPC.TL_dialog)localObject3).id = (-((TLRPC.TL_dialog)localObject3).peer.channel_id);
            break label368;
            label471: if ((int)((TLRPC.TL_dialog)localObject3).id < 0)
            {
              localObject1 = (TLRPC.Chat)localHashMap3.get(Integer.valueOf(-(int)((TLRPC.TL_dialog)localObject3).id));
              if ((localObject1 != null) && (((TLRPC.Chat)localObject1).migrated_to != null))
                continue;
            }
            label510: if (((TLRPC.TL_dialog)localObject3).last_message_date == 0)
            {
              localObject1 = (MessageObject)localHashMap2.get(Long.valueOf(((TLRPC.TL_dialog)localObject3).id));
              if (localObject1 != null)
                ((TLRPC.TL_dialog)localObject3).last_message_date = ((MessageObject)localObject1).messageOwner.date;
            }
            localHashMap1.put(Long.valueOf(((TLRPC.TL_dialog)localObject3).id), localObject3);
            localHashMap4.put(Long.valueOf(((TLRPC.TL_dialog)localObject3).id), Integer.valueOf(((TLRPC.TL_dialog)localObject3).unread_count));
            localObject2 = (Integer)MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(((TLRPC.TL_dialog)localObject3).id));
            localObject1 = localObject2;
            if (localObject2 == null)
              localObject1 = Integer.valueOf(0);
            MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(((TLRPC.TL_dialog)localObject3).id), Integer.valueOf(Math.max(((Integer)localObject1).intValue(), ((TLRPC.TL_dialog)localObject3).read_inbox_max_id)));
            localObject2 = (Integer)MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(((TLRPC.TL_dialog)localObject3).id));
            localObject1 = localObject2;
            if (localObject2 == null)
              localObject1 = Integer.valueOf(0);
            MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(((TLRPC.TL_dialog)localObject3).id), Integer.valueOf(Math.max(((Integer)localObject1).intValue(), ((TLRPC.TL_dialog)localObject3).read_outbox_max_id)));
          }
        }
        AndroidUtilities.runOnUIThread(new Runnable(localHashMap1, localHashMap2, localHashMap4)
        {
          public void run()
          {
            MessagesController.this.putUsers(MessagesController.59.this.val$dialogsRes.users, true);
            MessagesController.this.putChats(MessagesController.59.this.val$dialogsRes.chats, true);
            Iterator localIterator = this.val$new_dialogs_dict.entrySet().iterator();
            while (localIterator.hasNext())
            {
              Object localObject1 = (Map.Entry)localIterator.next();
              Long localLong = (Long)((Map.Entry)localObject1).getKey();
              localObject1 = (TLRPC.TL_dialog)((Map.Entry)localObject1).getValue();
              Object localObject3 = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(localLong);
              if (localObject3 == null)
              {
                localObject2 = MessagesController.this;
                ((MessagesController)localObject2).nextDialogsCacheOffset += 1;
                MessagesController.this.dialogs_dict.put(localLong, localObject1);
                localObject1 = (MessageObject)this.val$new_dialogMessage.get(Long.valueOf(((TLRPC.TL_dialog)localObject1).id));
                MessagesController.this.dialogMessage.put(localLong, localObject1);
                if ((localObject1 == null) || (((MessageObject)localObject1).messageOwner.to_id.channel_id != 0))
                  continue;
                MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(((MessageObject)localObject1).getId()), localObject1);
                if (((MessageObject)localObject1).messageOwner.random_id == 0L)
                  continue;
                MessagesController.this.dialogMessagesByRandomIds.put(Long.valueOf(((MessageObject)localObject1).messageOwner.random_id), localObject1);
                continue;
              }
              ((TLRPC.TL_dialog)localObject3).unread_count = ((TLRPC.TL_dialog)localObject1).unread_count;
              Object localObject2 = (MessageObject)MessagesController.this.dialogMessage.get(localLong);
              if ((localObject2 == null) || (((TLRPC.TL_dialog)localObject3).top_message > 0))
              {
                if (((localObject2 == null) || (!((MessageObject)localObject2).deleted)) && (((TLRPC.TL_dialog)localObject1).top_message <= ((TLRPC.TL_dialog)localObject3).top_message))
                  continue;
                MessagesController.this.dialogs_dict.put(localLong, localObject1);
                localObject3 = (MessageObject)this.val$new_dialogMessage.get(Long.valueOf(((TLRPC.TL_dialog)localObject1).id));
                MessagesController.this.dialogMessage.put(localLong, localObject3);
                if ((localObject3 != null) && (((MessageObject)localObject3).messageOwner.to_id.channel_id == 0))
                {
                  MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(((MessageObject)localObject3).getId()), localObject3);
                  if (((MessageObject)localObject3).messageOwner.random_id != 0L)
                    MessagesController.this.dialogMessagesByRandomIds.put(Long.valueOf(((MessageObject)localObject3).messageOwner.random_id), localObject3);
                }
                if (localObject2 != null)
                {
                  MessagesController.this.dialogMessagesByIds.remove(Integer.valueOf(((MessageObject)localObject2).getId()));
                  if (((MessageObject)localObject2).messageOwner.random_id != 0L)
                    MessagesController.this.dialogMessagesByRandomIds.remove(Long.valueOf(((MessageObject)localObject2).messageOwner.random_id));
                }
                if (localObject3 != null)
                  continue;
                MessagesController.this.checkLastDialogMessage((TLRPC.TL_dialog)localObject1, null, 0L);
                continue;
              }
              localObject3 = (MessageObject)this.val$new_dialogMessage.get(Long.valueOf(((TLRPC.TL_dialog)localObject1).id));
              if ((!((MessageObject)localObject2).deleted) && (localObject3 != null) && (((MessageObject)localObject3).messageOwner.date <= ((MessageObject)localObject2).messageOwner.date))
                continue;
              MessagesController.this.dialogs_dict.put(localLong, localObject1);
              MessagesController.this.dialogMessage.put(localLong, localObject3);
              if ((localObject3 != null) && (((MessageObject)localObject3).messageOwner.to_id.channel_id == 0))
              {
                MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(((MessageObject)localObject3).getId()), localObject3);
                if (((MessageObject)localObject3).messageOwner.random_id != 0L)
                  MessagesController.this.dialogMessagesByRandomIds.put(Long.valueOf(((MessageObject)localObject3).messageOwner.random_id), localObject3);
              }
              MessagesController.this.dialogMessagesByIds.remove(Integer.valueOf(((MessageObject)localObject2).getId()));
              if (((MessageObject)localObject2).messageOwner.random_id == 0L)
                continue;
              MessagesController.this.dialogMessagesByRandomIds.remove(Long.valueOf(((MessageObject)localObject2).messageOwner.random_id));
            }
            MessagesController.this.dialogs.clear();
            MessagesController.this.dialogs.addAll(MessagesController.this.dialogs_dict.values());
            MessagesController.this.sortDialogs(null);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            NotificationsController.getInstance().processDialogsUpdateRead(this.val$dialogsToUpdate);
          }
        });
      }
    });
  }

  public void processDialogsUpdateRead(HashMap<Long, Integer> paramHashMap)
  {
    AndroidUtilities.runOnUIThread(new Runnable(paramHashMap)
    {
      public void run()
      {
        Iterator localIterator = this.val$dialogsToUpdate.entrySet().iterator();
        while (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(localEntry.getKey());
          if (localTL_dialog == null)
            continue;
          int i = localTL_dialog.unread_count;
          localTL_dialog.unread_count = ((Integer)localEntry.getValue()).intValue();
          MessagesController.this.updateUnreadMessage(localTL_dialog, i);
        }
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(256) });
        NotificationsController.getInstance().processDialogsUpdateRead(this.val$dialogsToUpdate);
      }
    });
  }

  public void processLoadedBlockedUsers(ArrayList<Integer> paramArrayList, ArrayList<TLRPC.User> paramArrayList1, boolean paramBoolean)
  {
    AndroidUtilities.runOnUIThread(new Runnable(paramArrayList1, paramBoolean, paramArrayList)
    {
      public void run()
      {
        if (this.val$users != null)
          MessagesController.this.putUsers(this.val$users, this.val$cache);
        MessagesController.this.loadingBlockedUsers = false;
        if ((this.val$ids.isEmpty()) && (this.val$cache) && (!UserConfig.blockedUsersLoaded))
        {
          MessagesController.this.getBlockedUsers(false);
          return;
        }
        if (!this.val$cache)
        {
          UserConfig.blockedUsersLoaded = true;
          UserConfig.saveConfig(false);
        }
        MessagesController.this.blockedUsers = this.val$ids;
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
      }
    });
  }

  public void processLoadedDeleteTask(int paramInt, ArrayList<Integer> paramArrayList)
  {
    Utilities.stageQueue.postRunnable(new Runnable(paramArrayList, paramInt)
    {
      public void run()
      {
        MessagesController.access$3002(MessagesController.this, false);
        if (this.val$messages != null)
        {
          MessagesController.access$3102(MessagesController.this, this.val$taskTime);
          MessagesController.access$2902(MessagesController.this, this.val$messages);
          if (MessagesController.this.currentDeleteTaskRunnable != null)
          {
            Utilities.stageQueue.cancelRunnable(MessagesController.this.currentDeleteTaskRunnable);
            MessagesController.access$3202(MessagesController.this, null);
          }
          if (!MessagesController.this.checkDeletingTask(false))
          {
            MessagesController.access$3202(MessagesController.this, new Runnable()
            {
              public void run()
              {
                MessagesController.this.checkDeletingTask(true);
              }
            });
            int i = ConnectionsManager.getInstance().getCurrentTime();
            Utilities.stageQueue.postRunnable(MessagesController.this.currentDeleteTaskRunnable, Math.abs(i - MessagesController.this.currentDeletingTaskTime) * 1000L);
          }
          return;
        }
        MessagesController.access$3102(MessagesController.this, 0);
        MessagesController.access$2902(MessagesController.this, null);
      }
    });
  }

  public void processLoadedDialogs(TLRPC.messages_Dialogs parammessages_Dialogs, ArrayList<TLRPC.EncryptedChat> paramArrayList, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    Utilities.stageQueue.postRunnable(new Runnable(paramInt3, parammessages_Dialogs, paramBoolean1, paramInt2, paramInt1, paramArrayList, paramBoolean2, paramBoolean3)
    {
      public void run()
      {
        if (!MessagesController.this.firstGettingTask)
        {
          MessagesController.this.getNewDeleteTask(null);
          MessagesController.this.firstGettingTask = true;
        }
        FileLog.e("loaded loadType " + this.val$loadType + " count " + this.val$dialogsRes.dialogs.size());
        if ((this.val$loadType == 1) && (this.val$dialogsRes.dialogs.size() == 0))
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              MessagesController.this.putUsers(MessagesController.56.this.val$dialogsRes.users, true);
              MessagesController.this.loadingDialogs = false;
              if (MessagesController.56.this.val$resetEnd)
              {
                MessagesController.this.dialogsEndReached = false;
                MessagesController.this.serverDialogsEndReached = false;
              }
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
              MessagesController.this.loadDialogs(0, MessagesController.56.this.val$count, false);
            }
          });
          return;
        }
        HashMap localHashMap1 = new HashMap();
        HashMap localHashMap2 = new HashMap();
        HashMap localHashMap4 = new HashMap();
        HashMap localHashMap3 = new HashMap();
        int i = 0;
        Object localObject1;
        while (i < this.val$dialogsRes.users.size())
        {
          localObject1 = (TLRPC.User)this.val$dialogsRes.users.get(i);
          localHashMap4.put(Integer.valueOf(((TLRPC.User)localObject1).id), localObject1);
          i += 1;
        }
        i = 0;
        while (i < this.val$dialogsRes.chats.size())
        {
          localObject1 = (TLRPC.Chat)this.val$dialogsRes.chats.get(i);
          localHashMap3.put(Integer.valueOf(((TLRPC.Chat)localObject1).id), localObject1);
          i += 1;
        }
        if (this.val$loadType == 1)
          MessagesController.this.nextDialogsCacheOffset = (this.val$offset + this.val$count);
        i = 0;
        Object localObject2;
        if (i < this.val$dialogsRes.messages.size())
        {
          localObject1 = (TLRPC.Message)this.val$dialogsRes.messages.get(i);
          if (((TLRPC.Message)localObject1).to_id.channel_id != 0)
          {
            localObject2 = (TLRPC.Chat)localHashMap3.get(Integer.valueOf(((TLRPC.Message)localObject1).to_id.channel_id));
            if ((localObject2 == null) || (!((TLRPC.Chat)localObject2).left));
          }
          while (true)
          {
            i += 1;
            break;
            if ((localObject2 != null) && (((TLRPC.Chat)localObject2).megagroup))
              ((TLRPC.Message)localObject1).flags |= -2147483648;
            do
            {
              do
              {
                if ((this.val$loadType != 1) && (((TLRPC.Message)localObject1).post) && (!((TLRPC.Message)localObject1).out))
                  ((TLRPC.Message)localObject1).media_unread = true;
                localObject1 = new MessageObject((TLRPC.Message)localObject1, localHashMap4, localHashMap3, false);
                localHashMap2.put(Long.valueOf(((MessageObject)localObject1).getDialogId()), localObject1);
                break;
              }
              while (((TLRPC.Message)localObject1).to_id.chat_id == 0);
              localObject2 = (TLRPC.Chat)localHashMap3.get(Integer.valueOf(((TLRPC.Message)localObject1).to_id.chat_id));
            }
            while ((localObject2 == null) || (((TLRPC.Chat)localObject2).migrated_to == null));
          }
        }
        ArrayList localArrayList = new ArrayList();
        int j = 0;
        Object localObject3;
        if (j < this.val$dialogsRes.dialogs.size())
        {
          localObject3 = (TLRPC.TL_dialog)this.val$dialogsRes.dialogs.get(j);
          if ((((TLRPC.TL_dialog)localObject3).id == 0L) && (((TLRPC.TL_dialog)localObject3).peer != null))
          {
            if (((TLRPC.TL_dialog)localObject3).peer.user_id != 0)
              ((TLRPC.TL_dialog)localObject3).id = ((TLRPC.TL_dialog)localObject3).peer.user_id;
          }
          else
            label579: if (((TLRPC.TL_dialog)localObject3).id != 0L)
              break label654;
          while (true)
          {
            j += 1;
            break;
            if (((TLRPC.TL_dialog)localObject3).peer.chat_id != 0)
            {
              ((TLRPC.TL_dialog)localObject3).id = (-((TLRPC.TL_dialog)localObject3).peer.chat_id);
              break label579;
            }
            if (((TLRPC.TL_dialog)localObject3).peer.channel_id == 0)
              break label579;
            ((TLRPC.TL_dialog)localObject3).id = (-((TLRPC.TL_dialog)localObject3).peer.channel_id);
            break label579;
            label654: if (((TLRPC.TL_dialog)localObject3).last_message_date == 0)
            {
              localObject1 = (MessageObject)localHashMap2.get(Long.valueOf(((TLRPC.TL_dialog)localObject3).id));
              if (localObject1 != null)
                ((TLRPC.TL_dialog)localObject3).last_message_date = ((MessageObject)localObject1).messageOwner.date;
            }
            i = 1;
            int m = 1;
            int k = 1;
            if (DialogObject.isChannel((TLRPC.TL_dialog)localObject3))
            {
              localObject1 = (TLRPC.Chat)localHashMap3.get(Integer.valueOf(-(int)((TLRPC.TL_dialog)localObject3).id));
              if (localObject1 != null)
              {
                i = k;
                if (!((TLRPC.Chat)localObject1).megagroup)
                  i = 0;
                if (((TLRPC.Chat)localObject1).left)
                  continue;
              }
              MessagesController.this.channelsPts.put(Integer.valueOf(-(int)((TLRPC.TL_dialog)localObject3).id), Integer.valueOf(((TLRPC.TL_dialog)localObject3).pts));
            }
            do
            {
              do
              {
                do
                {
                  localHashMap1.put(Long.valueOf(((TLRPC.TL_dialog)localObject3).id), localObject3);
                  if ((i != 0) && (this.val$loadType == 1) && ((((TLRPC.TL_dialog)localObject3).read_outbox_max_id == 0) || (((TLRPC.TL_dialog)localObject3).read_inbox_max_id == 0)) && (((TLRPC.TL_dialog)localObject3).top_message != 0))
                    localArrayList.add(localObject3);
                  localObject2 = (Integer)MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(((TLRPC.TL_dialog)localObject3).id));
                  localObject1 = localObject2;
                  if (localObject2 == null)
                    localObject1 = Integer.valueOf(0);
                  MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(((TLRPC.TL_dialog)localObject3).id), Integer.valueOf(Math.max(((Integer)localObject1).intValue(), ((TLRPC.TL_dialog)localObject3).read_inbox_max_id)));
                  localObject2 = (Integer)MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(((TLRPC.TL_dialog)localObject3).id));
                  localObject1 = localObject2;
                  if (localObject2 == null)
                    localObject1 = Integer.valueOf(0);
                  MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(((TLRPC.TL_dialog)localObject3).id), Integer.valueOf(Math.max(((Integer)localObject1).intValue(), ((TLRPC.TL_dialog)localObject3).read_outbox_max_id)));
                  break;
                  i = m;
                }
                while ((int)((TLRPC.TL_dialog)localObject3).id >= 0);
                localObject1 = (TLRPC.Chat)localHashMap3.get(Integer.valueOf(-(int)((TLRPC.TL_dialog)localObject3).id));
                i = m;
              }
              while (localObject1 == null);
              i = m;
            }
            while (((TLRPC.Chat)localObject1).migrated_to == null);
          }
        }
        if (this.val$loadType != 1)
        {
          ImageLoader.saveMessagesThumbs(this.val$dialogsRes.messages);
          i = 0;
          while (i < this.val$dialogsRes.messages.size())
          {
            TLRPC.Message localMessage = (TLRPC.Message)this.val$dialogsRes.messages.get(i);
            if ((localMessage.action instanceof TLRPC.TL_messageActionChatDeleteUser))
            {
              localObject1 = (TLRPC.User)localHashMap4.get(Integer.valueOf(localMessage.action.user_id));
              if ((localObject1 != null) && (((TLRPC.User)localObject1).bot))
                localMessage.reply_markup = new TLRPC.TL_replyKeyboardHide();
            }
            if (((localMessage.action instanceof TLRPC.TL_messageActionChatMigrateTo)) || ((localMessage.action instanceof TLRPC.TL_messageActionChannelCreate)))
            {
              localMessage.unread = false;
              localMessage.media_unread = false;
              i += 1;
              continue;
            }
            if (localMessage.out)
            {
              localObject1 = MessagesController.this.dialogs_read_outbox_max;
              label1215: localObject3 = (Integer)((ConcurrentHashMap)localObject1).get(Long.valueOf(localMessage.dialog_id));
              localObject2 = localObject3;
              if (localObject3 == null)
              {
                localObject2 = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(localMessage.out, localMessage.dialog_id));
                ((ConcurrentHashMap)localObject1).put(Long.valueOf(localMessage.dialog_id), localObject2);
              }
              if (((Integer)localObject2).intValue() >= localMessage.id)
                break label1317;
            }
            label1317: for (boolean bool = true; ; bool = false)
            {
              localMessage.unread = bool;
              break;
              localObject1 = MessagesController.this.dialogs_read_inbox_max;
              break label1215;
            }
          }
          MessagesStorage.getInstance().putDialogs(this.val$dialogsRes, false);
        }
        if (this.val$loadType == 2)
        {
          localObject1 = (TLRPC.Chat)this.val$dialogsRes.chats.get(0);
          MessagesController.this.getChannelDifference(((TLRPC.Chat)localObject1).id);
          MessagesController.this.checkChannelInviter(((TLRPC.Chat)localObject1).id);
        }
        AndroidUtilities.runOnUIThread(new Runnable(localHashMap1, localHashMap2, localHashMap3, localArrayList)
        {
          public void run()
          {
            if (MessagesController.56.this.val$loadType != 1)
            {
              MessagesController.this.applyDialogsNotificationsSettings(MessagesController.56.this.val$dialogsRes.dialogs);
              if (!UserConfig.draftsLoaded)
                DraftQuery.loadDrafts();
            }
            Object localObject1 = MessagesController.this;
            Object localObject2 = MessagesController.56.this.val$dialogsRes.users;
            if (MessagesController.56.this.val$loadType == 1)
            {
              bool = true;
              ((MessagesController)localObject1).putUsers((ArrayList)localObject2, bool);
              localObject1 = MessagesController.this;
              localObject2 = MessagesController.56.this.val$dialogsRes.chats;
              if (MessagesController.56.this.val$loadType != 1)
                break label221;
            }
            int i;
            label221: for (boolean bool = true; ; bool = false)
            {
              ((MessagesController)localObject1).putChats((ArrayList)localObject2, bool);
              if (MessagesController.56.this.val$encChats == null)
                break label226;
              i = 0;
              while (i < MessagesController.56.this.val$encChats.size())
              {
                localObject1 = (TLRPC.EncryptedChat)MessagesController.56.this.val$encChats.get(i);
                if (((localObject1 instanceof TLRPC.TL_encryptedChat)) && (AndroidUtilities.getMyLayerVersion(((TLRPC.EncryptedChat)localObject1).layer) < 46))
                  SecretChatHelper.getInstance().sendNotifyLayerMessage((TLRPC.EncryptedChat)localObject1, null);
                MessagesController.this.putEncryptedChat((TLRPC.EncryptedChat)localObject1, true);
                i += 1;
              }
              bool = false;
              break;
            }
            label226: if (!MessagesController.56.this.val$migrate)
              MessagesController.this.loadingDialogs = false;
            int j;
            if ((MessagesController.56.this.val$migrate) && (!MessagesController.this.dialogs.isEmpty()))
            {
              j = ((TLRPC.TL_dialog)MessagesController.this.dialogs.get(MessagesController.this.dialogs.size() - 1)).last_message_date;
              localObject1 = this.val$new_dialogs_dict.entrySet().iterator();
              i = 0;
            }
            label324: 
            while (((Iterator)localObject1).hasNext())
            {
              Object localObject3 = (Map.Entry)((Iterator)localObject1).next();
              localObject2 = (Long)((Map.Entry)localObject3).getKey();
              Object localObject4 = (TLRPC.TL_dialog)((Map.Entry)localObject3).getValue();
              if ((MessagesController.56.this.val$migrate) && (j != 0) && (((TLRPC.TL_dialog)localObject4).last_message_date < j))
                continue;
              Object localObject5 = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(localObject2);
              if ((MessagesController.56.this.val$loadType != 1) && ((((TLRPC.TL_dialog)localObject4).draft instanceof TLRPC.TL_draftMessage)))
                DraftQuery.saveDraft(((TLRPC.TL_dialog)localObject4).id, ((TLRPC.TL_dialog)localObject4).draft, null, false);
              if (localObject5 == null)
              {
                MessagesController.this.dialogs_dict.put(localObject2, localObject4);
                localObject3 = (MessageObject)this.val$new_dialogMessage.get(Long.valueOf(((TLRPC.TL_dialog)localObject4).id));
                MessagesController.this.dialogMessage.put(localObject2, localObject3);
                if ((localObject3 != null) && (((MessageObject)localObject3).messageOwner.to_id.channel_id == 0))
                {
                  MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(((MessageObject)localObject3).getId()), localObject3);
                  if (((MessageObject)localObject3).messageOwner.random_id != 0L)
                    MessagesController.this.dialogMessagesByRandomIds.put(Long.valueOf(((MessageObject)localObject3).messageOwner.random_id), localObject3);
                }
                i = 1;
              }
              while (true)
              {
                break label324;
                j = 0;
                break;
                if (MessagesController.56.this.val$loadType != 1)
                  ((TLRPC.TL_dialog)localObject5).notify_settings = ((TLRPC.TL_dialog)localObject4).notify_settings;
                ((TLRPC.TL_dialog)localObject5).pinned = ((TLRPC.TL_dialog)localObject4).pinned;
                ((TLRPC.TL_dialog)localObject5).pinnedNum = ((TLRPC.TL_dialog)localObject4).pinnedNum;
                localObject3 = (MessageObject)MessagesController.this.dialogMessage.get(localObject2);
                if (((localObject3 != null) && (((MessageObject)localObject3).deleted)) || (localObject3 == null) || (((TLRPC.TL_dialog)localObject5).top_message > 0))
                {
                  if (((TLRPC.TL_dialog)localObject4).top_message >= ((TLRPC.TL_dialog)localObject5).top_message)
                  {
                    MessagesController.this.dialogs_dict.put(localObject2, localObject4);
                    localObject4 = (MessageObject)this.val$new_dialogMessage.get(Long.valueOf(((TLRPC.TL_dialog)localObject4).id));
                    MessagesController.this.dialogMessage.put(localObject2, localObject4);
                    if ((localObject4 != null) && (((MessageObject)localObject4).messageOwner.to_id.channel_id == 0))
                    {
                      MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(((MessageObject)localObject4).getId()), localObject4);
                      if ((localObject4 != null) && (((MessageObject)localObject4).messageOwner.random_id != 0L))
                        MessagesController.this.dialogMessagesByRandomIds.put(Long.valueOf(((MessageObject)localObject4).messageOwner.random_id), localObject4);
                    }
                    if (localObject3 != null)
                    {
                      MessagesController.this.dialogMessagesByIds.remove(Integer.valueOf(((MessageObject)localObject3).getId()));
                      if (((MessageObject)localObject3).messageOwner.random_id != 0L)
                        MessagesController.this.dialogMessagesByRandomIds.remove(Long.valueOf(((MessageObject)localObject3).messageOwner.random_id));
                    }
                    continue;
                  }
                }
                else
                {
                  localObject5 = (MessageObject)this.val$new_dialogMessage.get(Long.valueOf(((TLRPC.TL_dialog)localObject4).id));
                  if ((((MessageObject)localObject3).deleted) || (localObject5 == null) || (((MessageObject)localObject5).messageOwner.date > ((MessageObject)localObject3).messageOwner.date))
                  {
                    MessagesController.this.dialogs_dict.put(localObject2, localObject4);
                    MessagesController.this.dialogMessage.put(localObject2, localObject5);
                    if ((localObject5 != null) && (((MessageObject)localObject5).messageOwner.to_id.channel_id == 0))
                    {
                      MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(((MessageObject)localObject5).getId()), localObject5);
                      if ((localObject5 != null) && (((MessageObject)localObject5).messageOwner.random_id != 0L))
                        MessagesController.this.dialogMessagesByRandomIds.put(Long.valueOf(((MessageObject)localObject5).messageOwner.random_id), localObject5);
                    }
                    MessagesController.this.dialogMessagesByIds.remove(Integer.valueOf(((MessageObject)localObject3).getId()));
                    if (((MessageObject)localObject3).messageOwner.random_id != 0L)
                      MessagesController.this.dialogMessagesByRandomIds.remove(Long.valueOf(((MessageObject)localObject3).messageOwner.random_id));
                  }
                }
              }
            }
            MessagesController.this.dialogs.clear();
            MessagesController.this.dialogs.addAll(MessagesController.this.dialogs_dict.values());
            localObject2 = MessagesController.this;
            if (MessagesController.56.this.val$migrate)
            {
              localObject1 = this.val$chatsDict;
              ((MessagesController)localObject2).sortDialogs((HashMap)localObject1);
              if ((MessagesController.56.this.val$loadType != 2) && (!MessagesController.56.this.val$migrate))
              {
                localObject1 = MessagesController.this;
                if (((MessagesController.56.this.val$dialogsRes.dialogs.size() != 0) && (MessagesController.56.this.val$dialogsRes.dialogs.size() == MessagesController.56.this.val$count)) || (MessagesController.56.this.val$loadType != 0))
                  break label1512;
                bool = true;
                label1308: ((MessagesController)localObject1).dialogsEndReached = bool;
                if (!MessagesController.56.this.val$fromCache)
                {
                  localObject1 = MessagesController.this;
                  if (((MessagesController.56.this.val$dialogsRes.dialogs.size() != 0) && (MessagesController.56.this.val$dialogsRes.dialogs.size() == MessagesController.56.this.val$count)) || (MessagesController.56.this.val$loadType != 0))
                    break label1517;
                  bool = true;
                  label1384: ((MessagesController)localObject1).serverDialogsEndReached = bool;
                }
              }
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
              if (!MessagesController.56.this.val$migrate)
                break label1522;
              UserConfig.migrateOffsetId = MessagesController.56.this.val$offset;
              UserConfig.saveConfig(false);
              MessagesController.access$4502(MessagesController.this, false);
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[0]);
            }
            while (true)
            {
              MessagesController.this.migrateDialogs(UserConfig.migrateOffsetId, UserConfig.migrateOffsetDate, UserConfig.migrateOffsetUserId, UserConfig.migrateOffsetChatId, UserConfig.migrateOffsetChannelId, UserConfig.migrateOffsetAccess);
              if (!this.val$dialogsToReload.isEmpty())
                MessagesController.this.reloadDialogsReadValue(this.val$dialogsToReload, 0L);
              return;
              localObject1 = null;
              break;
              label1512: bool = false;
              break label1308;
              label1517: bool = false;
              break label1384;
              label1522: MessagesController.this.generateUpdateMessage();
              if ((i != 0) || (MessagesController.56.this.val$loadType != 1))
                continue;
              MessagesController.this.loadDialogs(0, MessagesController.56.this.val$count, false);
            }
          }
        });
      }
    });
  }

  public void processLoadedMessages(TLRPC.messages_Messages parammessages_Messages, long paramLong, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, boolean paramBoolean2, boolean paramBoolean3, int paramInt10, boolean paramBoolean4)
  {
    FileLog.e("processLoadedMessages size " + parammessages_Messages.messages.size() + " in chat " + paramLong + " count " + paramInt1 + " max_id " + paramInt2 + " cache " + paramBoolean1 + " guid " + paramInt4 + " load_type " + paramInt9 + " last_message_id " + paramInt6 + " isChannel " + paramBoolean2 + " index " + paramInt10 + " firstUnread " + paramInt5 + " underad count " + paramInt7 + " last_date " + paramInt8 + " queryFromServer " + paramBoolean4);
    Utilities.stageQueue.postRunnable(new Runnable(parammessages_Messages, paramLong, paramBoolean1, paramInt1, paramInt9, paramBoolean4, paramInt5, paramInt2, paramInt3, paramInt4, paramInt6, paramBoolean2, paramInt10, paramInt7, paramInt8, paramBoolean3)
    {
      public void run()
      {
        int j;
        boolean bool1;
        int i;
        label121: Object localObject1;
        boolean bool3;
        boolean bool2;
        if ((this.val$messagesRes instanceof TLRPC.TL_messages_channelMessages))
        {
          j = -(int)this.val$dialog_id;
          if (((Integer)MessagesController.this.channelsPts.get(Integer.valueOf(j)) == null) && (Integer.valueOf(MessagesStorage.getInstance().getChannelPtsSync(j)).intValue() == 0))
          {
            MessagesController.this.channelsPts.put(Integer.valueOf(j), Integer.valueOf(this.val$messagesRes.pts));
            if ((MessagesController.this.needShortPollChannels.indexOfKey(j) >= 0) && (MessagesController.this.shortPollChannels.indexOfKey(j) < 0))
            {
              MessagesController.this.getChannelDifference(j, 2, 0L, null);
              bool1 = true;
              i = 0;
              if (i >= this.val$messagesRes.chats.size())
                break label1124;
              localObject1 = (TLRPC.Chat)this.val$messagesRes.chats.get(i);
              if (((TLRPC.Chat)localObject1).id != j)
                break label259;
              bool3 = ((TLRPC.Chat)localObject1).megagroup;
              bool2 = bool1;
              bool1 = bool3;
            }
          }
        }
        while (true)
        {
          i = (int)this.val$dialog_id;
          j = (int)(this.val$dialog_id >> 32);
          if (!this.val$isCache)
            ImageLoader.saveMessagesThumbs(this.val$messagesRes.messages);
          if ((j != 1) && (i != 0) && (this.val$isCache) && (this.val$messagesRes.messages.size() == 0))
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MessagesController localMessagesController = MessagesController.this;
                long l = MessagesController.53.this.val$dialog_id;
                int j = MessagesController.53.this.val$count;
                if ((MessagesController.53.this.val$load_type == 2) && (MessagesController.53.this.val$queryFromServer));
                for (int i = MessagesController.53.this.val$first_unread; ; i = MessagesController.53.this.val$max_id)
                {
                  localMessagesController.loadMessages(l, j, i, MessagesController.53.this.val$offset_date, false, 0, MessagesController.53.this.val$classGuid, MessagesController.53.this.val$load_type, MessagesController.53.this.val$last_message_id, MessagesController.53.this.val$isChannel, MessagesController.53.this.val$loadIndex, MessagesController.53.this.val$first_unread, MessagesController.53.this.val$unread_count, MessagesController.53.this.val$last_date, MessagesController.53.this.val$queryFromServer);
                  return;
                }
              }
            });
            return;
            MessagesController.this.getChannelDifference(j);
            bool1 = true;
            break;
            label259: i += 1;
            break label121;
          }
          HashMap localHashMap1 = new HashMap();
          HashMap localHashMap2 = new HashMap();
          i = 0;
          while (i < this.val$messagesRes.users.size())
          {
            localObject1 = (TLRPC.User)this.val$messagesRes.users.get(i);
            localHashMap1.put(Integer.valueOf(((TLRPC.User)localObject1).id), localObject1);
            i += 1;
          }
          i = 0;
          while (i < this.val$messagesRes.chats.size())
          {
            localObject1 = (TLRPC.Chat)this.val$messagesRes.chats.get(i);
            localHashMap2.put(Integer.valueOf(((TLRPC.Chat)localObject1).id), localObject1);
            i += 1;
          }
          j = this.val$messagesRes.messages.size();
          Object localObject2;
          if (!this.val$isCache)
          {
            localObject2 = (Integer)MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(this.val$dialog_id));
            localObject1 = localObject2;
            if (localObject2 == null)
            {
              localObject1 = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(false, this.val$dialog_id));
              MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(this.val$dialog_id), localObject1);
            }
            localObject3 = (Integer)MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(this.val$dialog_id));
            localObject2 = localObject3;
            if (localObject3 == null)
            {
              localObject2 = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, this.val$dialog_id));
              MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(this.val$dialog_id), localObject2);
            }
            i = 0;
            while (i < j)
            {
              localObject4 = (TLRPC.Message)this.val$messagesRes.messages.get(i);
              if ((!this.val$isCache) && (((TLRPC.Message)localObject4).post) && (!((TLRPC.Message)localObject4).out))
                ((TLRPC.Message)localObject4).media_unread = true;
              if (bool1)
                ((TLRPC.Message)localObject4).flags |= -2147483648;
              if ((((TLRPC.Message)localObject4).action instanceof TLRPC.TL_messageActionChatDeleteUser))
              {
                localObject3 = (TLRPC.User)localHashMap1.get(Integer.valueOf(((TLRPC.Message)localObject4).action.user_id));
                if ((localObject3 != null) && (((TLRPC.User)localObject3).bot))
                  ((TLRPC.Message)localObject4).reply_markup = new TLRPC.TL_replyKeyboardHide();
              }
              if (((((TLRPC.Message)localObject4).action instanceof TLRPC.TL_messageActionChatMigrateTo)) || ((((TLRPC.Message)localObject4).action instanceof TLRPC.TL_messageActionChannelCreate)))
              {
                ((TLRPC.Message)localObject4).unread = false;
                ((TLRPC.Message)localObject4).media_unread = false;
                i += 1;
                continue;
              }
              if (((TLRPC.Message)localObject4).out)
              {
                localObject3 = localObject2;
                label725: if (((Integer)localObject3).intValue() >= ((TLRPC.Message)localObject4).id)
                  break label758;
              }
              label758: for (bool3 = true; ; bool3 = false)
              {
                ((TLRPC.Message)localObject4).unread = bool3;
                break;
                localObject3 = localObject1;
                break label725;
              }
            }
            MessagesStorage.getInstance().putMessages(this.val$messagesRes, this.val$dialog_id, this.val$load_type, this.val$max_id, bool2);
          }
          Object localObject3 = new ArrayList();
          Object localObject4 = new ArrayList();
          HashMap localHashMap3 = new HashMap();
          i = 0;
          if (i < j)
          {
            TLRPC.Message localMessage = (TLRPC.Message)this.val$messagesRes.messages.get(i);
            localMessage.dialog_id = this.val$dialog_id;
            MessageObject localMessageObject = new MessageObject(localMessage, localHashMap1, localHashMap2, true);
            ((ArrayList)localObject3).add(localMessageObject);
            if (this.val$isCache)
            {
              if (!(localMessage.media instanceof TLRPC.TL_messageMediaUnsupported))
                break label961;
              if ((localMessage.media.bytes != null) && ((localMessage.media.bytes.length == 0) || ((localMessage.media.bytes.length == 1) && (localMessage.media.bytes[0] < 65))))
                ((ArrayList)localObject4).add(Integer.valueOf(localMessage.id));
            }
            while (true)
            {
              i += 1;
              break;
              label961: if (!(localMessage.media instanceof TLRPC.TL_messageMediaWebPage))
                continue;
              if (((localMessage.media.webpage instanceof TLRPC.TL_webPagePending)) && (localMessage.media.webpage.date <= ConnectionsManager.getInstance().getCurrentTime()))
              {
                ((ArrayList)localObject4).add(Integer.valueOf(localMessage.id));
                continue;
              }
              if (!(localMessage.media.webpage instanceof TLRPC.TL_webPageUrlPending))
                continue;
              localObject2 = (ArrayList)localHashMap3.get(localMessage.media.webpage.url);
              localObject1 = localObject2;
              if (localObject2 == null)
              {
                localObject1 = new ArrayList();
                localHashMap3.put(localMessage.media.webpage.url, localObject1);
              }
              ((ArrayList)localObject1).add(localMessageObject);
            }
          }
          AndroidUtilities.runOnUIThread(new Runnable((ArrayList)localObject3, (ArrayList)localObject4, localHashMap3)
          {
            public void run()
            {
              MessagesController.this.putUsers(MessagesController.53.this.val$messagesRes.users, MessagesController.53.this.val$isCache);
              MessagesController.this.putChats(MessagesController.53.this.val$messagesRes.chats, MessagesController.53.this.val$isCache);
              int j;
              int i;
              if ((MessagesController.53.this.val$queryFromServer) && (MessagesController.53.this.val$load_type == 2))
              {
                j = 0;
                int k;
                for (i = 2147483647; j < MessagesController.53.this.val$messagesRes.messages.size(); i = k)
                {
                  TLRPC.Message localMessage = (TLRPC.Message)MessagesController.53.this.val$messagesRes.messages.get(j);
                  k = i;
                  if (!localMessage.out)
                  {
                    k = i;
                    if (localMessage.id > MessagesController.53.this.val$first_unread)
                    {
                      k = i;
                      if (localMessage.id < i)
                        k = localMessage.id;
                    }
                  }
                  j += 1;
                }
              }
              while (true)
              {
                j = i;
                if (i == 2147483647)
                  j = MessagesController.53.this.val$first_unread;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagesDidLoaded, new Object[] { Long.valueOf(MessagesController.53.this.val$dialog_id), Integer.valueOf(MessagesController.53.this.val$count), this.val$objects, Boolean.valueOf(MessagesController.53.this.val$isCache), Integer.valueOf(j), Integer.valueOf(MessagesController.53.this.val$last_message_id), Integer.valueOf(MessagesController.53.this.val$unread_count), Integer.valueOf(MessagesController.53.this.val$last_date), Integer.valueOf(MessagesController.53.this.val$load_type), Boolean.valueOf(MessagesController.53.this.val$isEnd), Integer.valueOf(MessagesController.53.this.val$classGuid), Integer.valueOf(MessagesController.53.this.val$loadIndex), Integer.valueOf(MessagesController.53.this.val$max_id) });
                if (!this.val$messagesToReload.isEmpty())
                  MessagesController.this.reloadMessages(this.val$messagesToReload, MessagesController.53.this.val$dialog_id);
                if (!this.val$webpagesToReload.isEmpty())
                  MessagesController.this.reloadWebPages(MessagesController.53.this.val$dialog_id, this.val$webpagesToReload);
                return;
                i = 2147483647;
              }
            }
          });
          return;
          label1124: bool3 = false;
          bool2 = bool1;
          bool1 = bool3;
          continue;
          bool1 = false;
          break;
          bool1 = false;
          bool2 = false;
        }
      }
    });
  }

  public void processLoadedUserPhotos(TLRPC.photos_Photos paramphotos_Photos, int paramInt1, int paramInt2, int paramInt3, long paramLong, boolean paramBoolean, int paramInt4)
  {
    if (!paramBoolean)
    {
      MessagesStorage.getInstance().putUsersAndChats(paramphotos_Photos.users, null, true, true);
      MessagesStorage.getInstance().putDialogPhotos(paramInt1, paramphotos_Photos);
    }
    do
    {
      AndroidUtilities.runOnUIThread(new Runnable(paramphotos_Photos, paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4)
      {
        public void run()
        {
          MessagesController.this.putUsers(this.val$res.users, this.val$fromCache);
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogPhotosLoaded, new Object[] { Integer.valueOf(this.val$did), Integer.valueOf(this.val$offset), Integer.valueOf(this.val$count), Boolean.valueOf(this.val$fromCache), Integer.valueOf(this.val$classGuid), this.val$res.photos });
        }
      });
      return;
    }
    while ((paramphotos_Photos != null) && (!paramphotos_Photos.photos.isEmpty()));
    loadDialogPhotos(paramInt1, paramInt2, paramInt3, paramLong, false, paramInt4);
  }

  protected void processNewChannelDifferenceParams(int paramInt1, int paramInt2, int paramInt3)
  {
    FileLog.e("processNewChannelDifferenceParams pts = " + paramInt1 + " pts_count = " + paramInt2 + " channeldId = " + paramInt3);
    if (!DialogObject.isChannel((TLRPC.TL_dialog)this.dialogs_dict.get(Long.valueOf(-paramInt3))));
    do
    {
      return;
      localObject2 = (Integer)this.channelsPts.get(Integer.valueOf(paramInt3));
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject2 = Integer.valueOf(MessagesStorage.getInstance().getChannelPtsSync(paramInt3));
        localObject1 = localObject2;
        if (((Integer)localObject2).intValue() == 0)
          localObject1 = Integer.valueOf(1);
        this.channelsPts.put(Integer.valueOf(paramInt3), localObject1);
      }
      if (((Integer)localObject1).intValue() + paramInt2 != paramInt1)
        continue;
      FileLog.e("APPLY CHANNEL PTS");
      this.channelsPts.put(Integer.valueOf(paramInt3), Integer.valueOf(paramInt1));
      MessagesStorage.getInstance().saveChannelPts(paramInt3, paramInt1);
      return;
    }
    while (((Integer)localObject1).intValue() == paramInt1);
    Object localObject3 = (Long)this.updatesStartWaitTimeChannels.get(Integer.valueOf(paramInt3));
    Object localObject2 = (Boolean)this.gettingDifferenceChannels.get(Integer.valueOf(paramInt3));
    Object localObject1 = localObject2;
    if (localObject2 == null)
      localObject1 = Boolean.valueOf(false);
    if ((((Boolean)localObject1).booleanValue()) || (localObject3 == null) || (Math.abs(System.currentTimeMillis() - ((Long)localObject3).longValue()) <= 1500L))
    {
      FileLog.e("ADD CHANNEL UPDATE TO QUEUE pts = " + paramInt1 + " pts_count = " + paramInt2);
      if (localObject3 == null)
        this.updatesStartWaitTimeChannels.put(Integer.valueOf(paramInt3), Long.valueOf(System.currentTimeMillis()));
      localObject3 = new UserActionUpdatesPts(null);
      ((UserActionUpdatesPts)localObject3).pts = paramInt1;
      ((UserActionUpdatesPts)localObject3).pts_count = paramInt2;
      ((UserActionUpdatesPts)localObject3).chat_id = paramInt3;
      localObject2 = (ArrayList)this.updatesQueueChannels.get(Integer.valueOf(paramInt3));
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject1 = new ArrayList();
        this.updatesQueueChannels.put(Integer.valueOf(paramInt3), localObject1);
      }
      ((ArrayList)localObject1).add(localObject3);
      return;
    }
    getChannelDifference(paramInt3);
  }

  protected void processNewDifferenceParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    FileLog.e("processNewDifferenceParams seq = " + paramInt1 + " pts = " + paramInt2 + " date = " + paramInt3 + " pts_count = " + paramInt4);
    if (paramInt2 != -1)
    {
      if (MessagesStorage.lastPtsValue + paramInt4 != paramInt2)
        break label149;
      FileLog.e("APPLY PTS");
      MessagesStorage.lastPtsValue = paramInt2;
      MessagesStorage.getInstance().saveDiffParams(MessagesStorage.lastSeqValue, MessagesStorage.lastPtsValue, MessagesStorage.lastDateValue, MessagesStorage.lastQtsValue);
    }
    label149: Object localObject;
    do
      while (true)
      {
        if (paramInt1 != -1)
        {
          if (MessagesStorage.lastSeqValue + 1 != paramInt1)
            break;
          FileLog.e("APPLY SEQ");
          MessagesStorage.lastSeqValue = paramInt1;
          if (paramInt3 != -1)
            MessagesStorage.lastDateValue = paramInt3;
          MessagesStorage.getInstance().saveDiffParams(MessagesStorage.lastSeqValue, MessagesStorage.lastPtsValue, MessagesStorage.lastDateValue, MessagesStorage.lastQtsValue);
        }
        return;
        if (MessagesStorage.lastPtsValue == paramInt2)
          continue;
        if ((this.gettingDifference) || (this.updatesStartWaitTimePts == 0L) || (Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimePts) <= 1500L))
        {
          FileLog.e("ADD UPDATE TO QUEUE pts = " + paramInt2 + " pts_count = " + paramInt4);
          if (this.updatesStartWaitTimePts == 0L)
            this.updatesStartWaitTimePts = System.currentTimeMillis();
          localObject = new UserActionUpdatesPts(null);
          ((UserActionUpdatesPts)localObject).pts = paramInt2;
          ((UserActionUpdatesPts)localObject).pts_count = paramInt4;
          this.updatesQueuePts.add(localObject);
          continue;
        }
        getDifference();
      }
    while (MessagesStorage.lastSeqValue == paramInt1);
    if ((this.gettingDifference) || (this.updatesStartWaitTimeSeq == 0L) || (Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimeSeq) <= 1500L))
    {
      FileLog.e("ADD UPDATE TO QUEUE seq = " + paramInt1);
      if (this.updatesStartWaitTimeSeq == 0L)
        this.updatesStartWaitTimeSeq = System.currentTimeMillis();
      localObject = new UserActionUpdatesSeq(null);
      ((UserActionUpdatesSeq)localObject).seq = paramInt1;
      this.updatesQueueSeq.add(localObject);
      return;
    }
    getDifference();
  }

  public boolean processUpdateArray(ArrayList<TLRPC.Update> paramArrayList, ArrayList<TLRPC.User> paramArrayList1, ArrayList<TLRPC.Chat> paramArrayList2, boolean paramBoolean)
  {
    if (paramArrayList.isEmpty())
    {
      if ((paramArrayList1 != null) || (paramArrayList2 != null))
        AndroidUtilities.runOnUIThread(new Runnable(paramArrayList1, paramArrayList2)
        {
          public void run()
          {
            MessagesController.this.putUsers(this.val$usersArr, false);
            MessagesController.this.putChats(this.val$chatsArr, false);
          }
        });
      return true;
    }
    long l2 = System.currentTimeMillis();
    HashMap localHashMap1 = new HashMap();
    HashMap localHashMap2 = new HashMap();
    ArrayList localArrayList6 = new ArrayList();
    ArrayList localArrayList7 = new ArrayList();
    HashMap localHashMap3 = new HashMap();
    SparseArray localSparseArray2 = new SparseArray();
    SparseArray localSparseArray3 = new SparseArray();
    SparseArray localSparseArray4 = new SparseArray();
    ArrayList localArrayList2 = new ArrayList();
    HashMap localHashMap4 = new HashMap();
    SparseArray localSparseArray1 = new SparseArray();
    ArrayList localArrayList3 = new ArrayList();
    ArrayList localArrayList4 = new ArrayList();
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList5 = new ArrayList();
    int j = 1;
    Object localObject1;
    int i;
    Object localObject2;
    if (paramArrayList1 != null)
    {
      localObject1 = new ConcurrentHashMap();
      i = 0;
      while (i < paramArrayList1.size())
      {
        localObject2 = (TLRPC.User)paramArrayList1.get(i);
        ((ConcurrentHashMap)localObject1).put(Integer.valueOf(((TLRPC.User)localObject2).id), localObject2);
        i += 1;
      }
      i = j;
    }
    Object localObject3;
    while (paramArrayList2 != null)
    {
      localObject2 = new ConcurrentHashMap();
      j = 0;
      while (true)
        if (j < paramArrayList2.size())
        {
          localObject3 = (TLRPC.Chat)paramArrayList2.get(j);
          ((ConcurrentHashMap)localObject2).put(Integer.valueOf(((TLRPC.Chat)localObject3).id), localObject3);
          j += 1;
          continue;
          i = 0;
          localObject1 = this.users;
          break;
        }
      if (!paramBoolean)
        break label6353;
    }
    label427: label454: label5711: label6353: for (int m = 0; ; m = i)
    {
      if ((paramArrayList1 != null) || (paramArrayList2 != null))
        AndroidUtilities.runOnUIThread(new Runnable(paramArrayList1, paramArrayList2)
        {
          public void run()
          {
            MessagesController.this.putUsers(this.val$usersArr, false);
            MessagesController.this.putChats(this.val$chatsArr, false);
          }
        });
      int n = 0;
      i = 0;
      boolean bool1 = false;
      Object localObject4;
      int i1;
      int k;
      if (n < paramArrayList.size())
      {
        localObject4 = (TLRPC.Update)paramArrayList.get(n);
        FileLog.d("process update " + localObject4);
        if (((localObject4 instanceof TLRPC.TL_updateNewMessage)) || ((localObject4 instanceof TLRPC.TL_updateNewChannelMessage)))
          if ((localObject4 instanceof TLRPC.TL_updateNewMessage))
          {
            localObject3 = ((TLRPC.TL_updateNewMessage)localObject4).message;
            i1 = 0;
            j = 0;
            if (((TLRPC.Message)localObject3).to_id.channel_id == 0)
              break label641;
            k = ((TLRPC.Message)localObject3).to_id.channel_id;
            if (k == 0)
              break label6348;
            paramArrayList2 = (TLRPC.Chat)((ConcurrentHashMap)localObject2).get(Integer.valueOf(k));
            paramArrayList1 = paramArrayList2;
            if (paramArrayList2 == null)
              paramArrayList1 = getChat(Integer.valueOf(k));
            paramArrayList2 = paramArrayList1;
            if (paramArrayList1 == null)
            {
              paramArrayList2 = MessagesStorage.getInstance().getChatSync(k);
              putChat(paramArrayList2, true);
            }
          }
      }
      while (true)
      {
        int i2;
        if (m != 0)
        {
          if ((k != 0) && (paramArrayList2 == null))
          {
            FileLog.d("not found chat " + k);
            return false;
            localObject2 = this.chats;
            i = 0;
            break;
            localObject3 = ((TLRPC.TL_updateNewChannelMessage)localObject4).message;
            if (BuildVars.DEBUG_VERSION)
              FileLog.d(localObject4 + " channelId = " + ((TLRPC.Message)localObject3).to_id.channel_id);
            if ((!((TLRPC.Message)localObject3).out) && (((TLRPC.Message)localObject3).from_id == UserConfig.getClientUserId()))
              ((TLRPC.Message)localObject3).out = true;
            break label427;
            if (((TLRPC.Message)localObject3).to_id.chat_id != 0)
            {
              k = ((TLRPC.Message)localObject3).to_id.chat_id;
              break label454;
            }
            k = i1;
            if (((TLRPC.Message)localObject3).to_id.user_id == 0)
              break label454;
            j = ((TLRPC.Message)localObject3).to_id.user_id;
            k = i1;
            break label454;
          }
          int i3 = ((TLRPC.Message)localObject3).entities.size();
          k = 0;
          i1 = j;
          j = i;
          i = i1;
          while (true)
            if (k < i3 + 3)
            {
              i1 = 0;
              if (k == 0)
                break label6342;
              if (k == 1)
              {
                i2 = ((TLRPC.Message)localObject3).from_id;
                i = i2;
                if (((TLRPC.Message)localObject3).post)
                {
                  i1 = 1;
                  i = i2;
                }
                if (i <= 0)
                  break;
                localObject4 = (TLRPC.User)((ConcurrentHashMap)localObject1).get(Integer.valueOf(i));
                if (localObject4 != null)
                {
                  paramArrayList1 = (ArrayList<TLRPC.User>)localObject4;
                  if (i1 == 0)
                  {
                    paramArrayList1 = (ArrayList<TLRPC.User>)localObject4;
                    if (!((TLRPC.User)localObject4).min);
                  }
                }
                else
                {
                  paramArrayList1 = getUser(Integer.valueOf(i));
                }
                if (paramArrayList1 != null)
                {
                  localObject4 = paramArrayList1;
                  if (i1 == 0)
                  {
                    localObject4 = paramArrayList1;
                    if (!paramArrayList1.min);
                  }
                }
                else
                {
                  localObject4 = MessagesStorage.getInstance().getUserSync(i);
                  paramArrayList1 = (ArrayList<TLRPC.User>)localObject4;
                  if (localObject4 != null)
                  {
                    paramArrayList1 = (ArrayList<TLRPC.User>)localObject4;
                    if (i1 == 0)
                    {
                      paramArrayList1 = (ArrayList<TLRPC.User>)localObject4;
                      if (((TLRPC.User)localObject4).min)
                        paramArrayList1 = null;
                    }
                  }
                  putUser(paramArrayList1, true);
                  localObject4 = paramArrayList1;
                }
                if (localObject4 == null)
                {
                  FileLog.d("not found user " + i);
                  return false;
                }
              }
              else
              {
                if (k == 2)
                {
                  if (((TLRPC.Message)localObject3).fwd_from != null);
                  for (i = ((TLRPC.Message)localObject3).fwd_from.from_id; ; i = 0)
                    break;
                }
                paramArrayList1 = (TLRPC.MessageEntity)((TLRPC.Message)localObject3).entities.get(k - 3);
                if ((paramArrayList1 instanceof TLRPC.TL_messageEntityMentionName));
                for (i = ((TLRPC.TL_messageEntityMentionName)paramArrayList1).user_id; ; i = 0)
                  break;
              }
              if ((k != 1) || (((TLRPC.User)localObject4).status == null) || (((TLRPC.User)localObject4).status.expires > 0))
                break;
              this.onlinePrivacy.put(Integer.valueOf(i), Integer.valueOf(ConnectionsManager.getInstance().getCurrentTime()));
              j |= 4;
              k += 1;
              continue;
            }
            else
            {
              i = j;
            }
        }
        while (true)
        {
          if ((paramArrayList2 != null) && (paramArrayList2.megagroup))
            ((TLRPC.Message)localObject3).flags |= -2147483648;
          label1146: boolean bool2;
          if ((((TLRPC.Message)localObject3).action instanceof TLRPC.TL_messageActionChatDeleteUser))
          {
            paramArrayList1 = (TLRPC.User)((ConcurrentHashMap)localObject1).get(Integer.valueOf(((TLRPC.Message)localObject3).action.user_id));
            if ((paramArrayList1 != null) && (paramArrayList1.bot))
              ((TLRPC.Message)localObject3).reply_markup = new TLRPC.TL_replyKeyboardHide();
          }
          else
          {
            localArrayList7.add(localObject3);
            ImageLoader.saveMessageThumbs((TLRPC.Message)localObject3);
            j = UserConfig.getClientUserId();
            if (((TLRPC.Message)localObject3).to_id.chat_id == 0)
              break label1512;
            ((TLRPC.Message)localObject3).dialog_id = (-((TLRPC.Message)localObject3).to_id.chat_id);
            if (!((TLRPC.Message)localObject3).out)
              break label1584;
            paramArrayList1 = this.dialogs_read_outbox_max;
            Integer localInteger = (Integer)paramArrayList1.get(Long.valueOf(((TLRPC.Message)localObject3).dialog_id));
            localObject4 = localInteger;
            if (localInteger == null)
            {
              localObject4 = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(((TLRPC.Message)localObject3).out, ((TLRPC.Message)localObject3).dialog_id));
              paramArrayList1.put(Long.valueOf(((TLRPC.Message)localObject3).dialog_id), localObject4);
            }
            if ((((Integer)localObject4).intValue() >= ((TLRPC.Message)localObject3).id) || ((paramArrayList2 != null) && (ChatObject.isNotInChat(paramArrayList2))) || ((((TLRPC.Message)localObject3).action instanceof TLRPC.TL_messageActionChatMigrateTo)) || ((((TLRPC.Message)localObject3).action instanceof TLRPC.TL_messageActionChannelCreate)))
              break label1592;
            bool2 = true;
            ((TLRPC.Message)localObject3).unread = bool2;
            if (((TLRPC.Message)localObject3).dialog_id == j)
            {
              ((TLRPC.Message)localObject3).unread = false;
              ((TLRPC.Message)localObject3).media_unread = false;
              ((TLRPC.Message)localObject3).out = true;
            }
            localObject4 = new MessageObject((TLRPC.Message)localObject3, (AbstractMap)localObject1, (AbstractMap)localObject2, this.createdDialogIds.contains(Long.valueOf(((TLRPC.Message)localObject3).dialog_id)));
            if (((MessageObject)localObject4).type != 11)
              break label1598;
            i |= 8;
          }
          while (true)
          {
            label1398: paramArrayList2 = (ArrayList)localHashMap1.get(Long.valueOf(((TLRPC.Message)localObject3).dialog_id));
            paramArrayList1 = paramArrayList2;
            if (paramArrayList2 == null)
            {
              paramArrayList1 = new ArrayList();
              localHashMap1.put(Long.valueOf(((TLRPC.Message)localObject3).dialog_id), paramArrayList1);
            }
            paramArrayList1.add(localObject4);
            if ((!((MessageObject)localObject4).isOut()) && (((MessageObject)localObject4).isUnread()))
              localArrayList6.add(localObject4);
            label1512: long l1;
            while (true)
            {
              n += 1;
              break;
              if ((((TLRPC.Message)localObject3).from_id != UserConfig.getClientUserId()) || (((TLRPC.Message)localObject3).action.user_id != UserConfig.getClientUserId()))
                break label1146;
              continue;
              if (((TLRPC.Message)localObject3).to_id.channel_id != 0)
              {
                ((TLRPC.Message)localObject3).dialog_id = (-((TLRPC.Message)localObject3).to_id.channel_id);
                break label1190;
              }
              if (((TLRPC.Message)localObject3).to_id.user_id == j)
                ((TLRPC.Message)localObject3).to_id.user_id = ((TLRPC.Message)localObject3).from_id;
              ((TLRPC.Message)localObject3).dialog_id = ((TLRPC.Message)localObject3).to_id.user_id;
              break label1190;
              paramArrayList1 = this.dialogs_read_inbox_max;
              break label1203;
              bool2 = false;
              break label1314;
              if (((MessageObject)localObject4).type != 10)
                break label6336;
              i |= 16;
              break label1398;
              if ((localObject4 instanceof TLRPC.TL_updateReadMessagesContents))
              {
                j = 0;
                while (j < ((TLRPC.Update)localObject4).messages.size())
                {
                  localArrayList2.add(Long.valueOf(((Integer)((TLRPC.Update)localObject4).messages.get(j)).intValue()));
                  j += 1;
                }
                continue;
              }
              if (((localObject4 instanceof TLRPC.TL_updateReadHistoryInbox)) || ((localObject4 instanceof TLRPC.TL_updateReadHistoryOutbox)))
              {
                if ((localObject4 instanceof TLRPC.TL_updateReadHistoryInbox))
                {
                  paramArrayList1 = ((TLRPC.TL_updateReadHistoryInbox)localObject4).peer;
                  if (paramArrayList1.chat_id != 0)
                  {
                    localSparseArray3.put(-paramArrayList1.chat_id, Long.valueOf(((TLRPC.Update)localObject4).max_id));
                    l1 = -paramArrayList1.chat_id;
                  }
                  while (true)
                  {
                    paramArrayList1 = this.dialogs_read_inbox_max;
                    localObject3 = (Integer)paramArrayList1.get(Long.valueOf(l1));
                    paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject3;
                    if (localObject3 == null)
                      paramArrayList2 = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(localObject4 instanceof TLRPC.TL_updateReadHistoryOutbox, l1));
                    paramArrayList1.put(Long.valueOf(l1), Integer.valueOf(Math.max(paramArrayList2.intValue(), ((TLRPC.Update)localObject4).max_id)));
                    break;
                    localSparseArray3.put(paramArrayList1.user_id, Long.valueOf(((TLRPC.Update)localObject4).max_id));
                    l1 = paramArrayList1.user_id;
                  }
                }
                paramArrayList1 = ((TLRPC.TL_updateReadHistoryOutbox)localObject4).peer;
                if (paramArrayList1.chat_id != 0)
                {
                  localSparseArray4.put(-paramArrayList1.chat_id, Long.valueOf(((TLRPC.Update)localObject4).max_id));
                  l1 = -paramArrayList1.chat_id;
                }
                while (true)
                {
                  paramArrayList1 = this.dialogs_read_outbox_max;
                  break;
                  localSparseArray4.put(paramArrayList1.user_id, Long.valueOf(((TLRPC.Update)localObject4).max_id));
                  l1 = paramArrayList1.user_id;
                }
              }
              if (!(localObject4 instanceof TLRPC.TL_updateDeleteMessages))
                break label1978;
              paramArrayList2 = (ArrayList)localSparseArray1.get(0);
              paramArrayList1 = paramArrayList2;
              if (paramArrayList2 == null)
              {
                paramArrayList1 = new ArrayList();
                localSparseArray1.put(0, paramArrayList1);
              }
              paramArrayList1.addAll(((TLRPC.Update)localObject4).messages);
            }
            if (((localObject4 instanceof TLRPC.TL_updateUserTyping)) || ((localObject4 instanceof TLRPC.TL_updateChatUserTyping)))
            {
              if (((TLRPC.Update)localObject4).user_id == UserConfig.getClientUserId())
                break label5711;
              l1 = -((TLRPC.Update)localObject4).chat_id;
              if (l1 != 0L)
                break label6333;
              l1 = ((TLRPC.Update)localObject4).user_id;
            }
            while (true)
            {
              paramArrayList1 = (ArrayList)this.printingUsers.get(Long.valueOf(l1));
              if ((((TLRPC.Update)localObject4).action instanceof TLRPC.TL_sendMessageCancelAction))
              {
                bool2 = bool1;
                if (paramArrayList1 != null)
                  j = 0;
                while (true)
                {
                  boolean bool3 = bool1;
                  if (j < paramArrayList1.size())
                  {
                    if (((PrintingUser)paramArrayList1.get(j)).userId == ((TLRPC.Update)localObject4).user_id)
                    {
                      paramArrayList1.remove(j);
                      bool3 = true;
                    }
                  }
                  else
                  {
                    bool2 = bool3;
                    if (paramArrayList1.isEmpty())
                    {
                      this.printingUsers.remove(Long.valueOf(l1));
                      bool2 = bool3;
                    }
                    label2138: this.onlinePrivacy.put(Integer.valueOf(((TLRPC.Update)localObject4).user_id), Integer.valueOf(ConnectionsManager.getInstance().getCurrentTime()));
                    bool1 = bool2;
                    break;
                  }
                  j += 1;
                }
              }
              if (paramArrayList1 == null)
              {
                paramArrayList1 = new ArrayList();
                this.printingUsers.put(Long.valueOf(l1), paramArrayList1);
              }
              while (true)
              {
                paramArrayList2 = paramArrayList1.iterator();
                while (paramArrayList2.hasNext())
                {
                  localObject3 = (PrintingUser)paramArrayList2.next();
                  if (((PrintingUser)localObject3).userId != ((TLRPC.Update)localObject4).user_id)
                    continue;
                  ((PrintingUser)localObject3).lastTime = l2;
                  if (((PrintingUser)localObject3).action.getClass() != ((TLRPC.Update)localObject4).action.getClass())
                    bool1 = true;
                  ((PrintingUser)localObject3).action = ((TLRPC.Update)localObject4).action;
                }
                for (j = 1; ; j = 0)
                {
                  bool2 = bool1;
                  if (j != 0)
                    break label2138;
                  paramArrayList2 = new PrintingUser();
                  paramArrayList2.userId = ((TLRPC.Update)localObject4).user_id;
                  paramArrayList2.lastTime = l2;
                  paramArrayList2.action = ((TLRPC.Update)localObject4).action;
                  paramArrayList1.add(paramArrayList2);
                  bool2 = true;
                  break label2138;
                  if ((localObject4 instanceof TLRPC.TL_updateChatParticipants))
                  {
                    localArrayList3.add(((TLRPC.Update)localObject4).participants);
                    i |= 32;
                    break;
                  }
                  if ((localObject4 instanceof TLRPC.TL_updateUserStatus))
                  {
                    localArrayList4.add(localObject4);
                    i |= 4;
                    break;
                  }
                  if ((localObject4 instanceof TLRPC.TL_updateUserName))
                  {
                    localArrayList4.add(localObject4);
                    i |= 1;
                    break;
                  }
                  if ((localObject4 instanceof TLRPC.TL_updateUserPhoto))
                  {
                    MessagesStorage.getInstance().clearUserPhotos(((TLRPC.Update)localObject4).user_id);
                    localArrayList4.add(localObject4);
                    i |= 2;
                    break;
                  }
                  if ((localObject4 instanceof TLRPC.TL_updateUserPhone))
                  {
                    localArrayList4.add(localObject4);
                    i |= 1024;
                    break;
                  }
                  if ((localObject4 instanceof TLRPC.TL_updateContactRegistered))
                  {
                    if ((this.enableJoined) && (((ConcurrentHashMap)localObject1).containsKey(Integer.valueOf(((TLRPC.Update)localObject4).user_id))) && (!MessagesStorage.getInstance().isDialogHasMessages(((TLRPC.Update)localObject4).user_id)))
                    {
                      localObject3 = new TLRPC.TL_messageService();
                      ((TLRPC.TL_messageService)localObject3).action = new TLRPC.TL_messageActionUserJoined();
                      j = UserConfig.getNewMessageId();
                      ((TLRPC.TL_messageService)localObject3).id = j;
                      ((TLRPC.TL_messageService)localObject3).local_id = j;
                      UserConfig.saveConfig(false);
                      ((TLRPC.TL_messageService)localObject3).unread = false;
                      ((TLRPC.TL_messageService)localObject3).flags = 256;
                      ((TLRPC.TL_messageService)localObject3).date = ((TLRPC.Update)localObject4).date;
                      ((TLRPC.TL_messageService)localObject3).from_id = ((TLRPC.Update)localObject4).user_id;
                      ((TLRPC.TL_messageService)localObject3).to_id = new TLRPC.TL_peerUser();
                      ((TLRPC.TL_messageService)localObject3).to_id.user_id = UserConfig.getClientUserId();
                      ((TLRPC.TL_messageService)localObject3).dialog_id = ((TLRPC.Update)localObject4).user_id;
                      localArrayList7.add(localObject3);
                      localObject4 = new MessageObject((TLRPC.Message)localObject3, (AbstractMap)localObject1, (AbstractMap)localObject2, this.createdDialogIds.contains(Long.valueOf(((TLRPC.TL_messageService)localObject3).dialog_id)));
                      paramArrayList2 = (ArrayList)localHashMap1.get(Long.valueOf(((TLRPC.TL_messageService)localObject3).dialog_id));
                      paramArrayList1 = paramArrayList2;
                      if (paramArrayList2 == null)
                      {
                        paramArrayList1 = new ArrayList();
                        localHashMap1.put(Long.valueOf(((TLRPC.TL_messageService)localObject3).dialog_id), paramArrayList1);
                      }
                      paramArrayList1.add(localObject4);
                      break;
                    }
                  }
                  else
                  {
                    if ((localObject4 instanceof TLRPC.TL_updateContactLink))
                    {
                      if ((((TLRPC.Update)localObject4).my_link instanceof TLRPC.TL_contactLinkContact))
                      {
                        j = localArrayList5.indexOf(Integer.valueOf(-((TLRPC.Update)localObject4).user_id));
                        if (j != -1)
                          localArrayList5.remove(j);
                        if (!localArrayList5.contains(Integer.valueOf(((TLRPC.Update)localObject4).user_id)))
                          localArrayList5.add(Integer.valueOf(((TLRPC.Update)localObject4).user_id));
                        break;
                      }
                      j = localArrayList5.indexOf(Integer.valueOf(((TLRPC.Update)localObject4).user_id));
                      if (j != -1)
                        localArrayList5.remove(j);
                      if (!localArrayList5.contains(Integer.valueOf(((TLRPC.Update)localObject4).user_id)))
                        localArrayList5.add(Integer.valueOf(-((TLRPC.Update)localObject4).user_id));
                      break;
                    }
                    if ((localObject4 instanceof TLRPC.TL_updateNewGeoChatMessage))
                      break;
                    if ((localObject4 instanceof TLRPC.TL_updateNewEncryptedMessage))
                    {
                      paramArrayList2 = SecretChatHelper.getInstance().decryptMessage(((TLRPC.TL_updateNewEncryptedMessage)localObject4).message);
                      if ((paramArrayList2 != null) && (!paramArrayList2.isEmpty()))
                      {
                        l1 = ((TLRPC.TL_updateNewEncryptedMessage)localObject4).message.chat_id << 32;
                        paramArrayList1 = (ArrayList)localHashMap1.get(Long.valueOf(l1));
                        if (paramArrayList1 != null)
                          break label6321;
                        paramArrayList1 = new ArrayList();
                        localHashMap1.put(Long.valueOf(l1), paramArrayList1);
                      }
                    }
                  }
                  while (true)
                  {
                    j = 0;
                    while (j < paramArrayList2.size())
                    {
                      localObject3 = (TLRPC.Message)paramArrayList2.get(j);
                      ImageLoader.saveMessageThumbs((TLRPC.Message)localObject3);
                      localArrayList7.add(localObject3);
                      localObject3 = new MessageObject((TLRPC.Message)localObject3, (AbstractMap)localObject1, (AbstractMap)localObject2, this.createdDialogIds.contains(Long.valueOf(l1)));
                      paramArrayList1.add(localObject3);
                      localArrayList6.add(localObject3);
                      j += 1;
                    }
                    break;
                    if ((localObject4 instanceof TLRPC.TL_updateEncryptedChatTyping))
                    {
                      paramArrayList1 = getEncryptedChatDB(((TLRPC.Update)localObject4).chat_id, true);
                      bool2 = bool1;
                      if (paramArrayList1 != null)
                      {
                        ((TLRPC.Update)localObject4).user_id = paramArrayList1.user_id;
                        l1 = ((TLRPC.Update)localObject4).chat_id << 32;
                        paramArrayList1 = (ArrayList)this.printingUsers.get(Long.valueOf(l1));
                        if (paramArrayList1 != null)
                          break label6318;
                        paramArrayList1 = new ArrayList();
                        this.printingUsers.put(Long.valueOf(l1), paramArrayList1);
                      }
                    }
                    while (true)
                    {
                      paramArrayList2 = paramArrayList1.iterator();
                      while (paramArrayList2.hasNext())
                      {
                        localObject3 = (PrintingUser)paramArrayList2.next();
                        if (((PrintingUser)localObject3).userId != ((TLRPC.Update)localObject4).user_id)
                          continue;
                        ((PrintingUser)localObject3).lastTime = l2;
                        ((PrintingUser)localObject3).action = new TLRPC.TL_sendMessageTypingAction();
                      }
                      for (j = 1; ; j = 0)
                      {
                        if (j == 0)
                        {
                          paramArrayList2 = new PrintingUser();
                          paramArrayList2.userId = ((TLRPC.Update)localObject4).user_id;
                          paramArrayList2.lastTime = l2;
                          paramArrayList2.action = new TLRPC.TL_sendMessageTypingAction();
                          paramArrayList1.add(paramArrayList2);
                          bool1 = true;
                        }
                        this.onlinePrivacy.put(Integer.valueOf(((TLRPC.Update)localObject4).user_id), Integer.valueOf(ConnectionsManager.getInstance().getCurrentTime()));
                        bool2 = bool1;
                        bool1 = bool2;
                        break;
                        if ((localObject4 instanceof TLRPC.TL_updateEncryptedMessagesRead))
                        {
                          localHashMap4.put(Integer.valueOf(((TLRPC.Update)localObject4).chat_id), Integer.valueOf(Math.max(((TLRPC.Update)localObject4).max_date, ((TLRPC.Update)localObject4).date)));
                          localArrayList1.add((TLRPC.TL_updateEncryptedMessagesRead)localObject4);
                          break;
                        }
                        if ((localObject4 instanceof TLRPC.TL_updateChatParticipantAdd))
                        {
                          MessagesStorage.getInstance().updateChatInfo(((TLRPC.Update)localObject4).chat_id, ((TLRPC.Update)localObject4).user_id, 0, ((TLRPC.Update)localObject4).inviter_id, ((TLRPC.Update)localObject4).version);
                          break;
                        }
                        if ((localObject4 instanceof TLRPC.TL_updateChatParticipantDelete))
                        {
                          MessagesStorage.getInstance().updateChatInfo(((TLRPC.Update)localObject4).chat_id, ((TLRPC.Update)localObject4).user_id, 1, 0, ((TLRPC.Update)localObject4).version);
                          break;
                        }
                        if (((localObject4 instanceof TLRPC.TL_updateDcOptions)) || ((localObject4 instanceof TLRPC.TL_updateConfig)))
                        {
                          ConnectionsManager.getInstance().updateDcSettings();
                          break;
                        }
                        if ((localObject4 instanceof TLRPC.TL_updateEncryption))
                        {
                          SecretChatHelper.getInstance().processUpdateEncryption((TLRPC.TL_updateEncryption)localObject4, (ConcurrentHashMap)localObject1);
                          break;
                        }
                        if ((localObject4 instanceof TLRPC.TL_updateUserBlocked))
                        {
                          paramArrayList1 = (TLRPC.TL_updateUserBlocked)localObject4;
                          if (paramArrayList1.blocked)
                          {
                            paramArrayList2 = new ArrayList();
                            paramArrayList2.add(Integer.valueOf(paramArrayList1.user_id));
                            MessagesStorage.getInstance().putBlockedUsers(paramArrayList2, false);
                          }
                          while (true)
                          {
                            MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramArrayList1)
                            {
                              public void run()
                              {
                                AndroidUtilities.runOnUIThread(new Runnable()
                                {
                                  public void run()
                                  {
                                    if (MessagesController.107.this.val$finalUpdate.blocked)
                                      if (!MessagesController.this.blockedUsers.contains(Integer.valueOf(MessagesController.107.this.val$finalUpdate.user_id)))
                                        MessagesController.this.blockedUsers.add(Integer.valueOf(MessagesController.107.this.val$finalUpdate.user_id));
                                    while (true)
                                    {
                                      NotificationCenter.getInstance().postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
                                      return;
                                      MessagesController.this.blockedUsers.remove(Integer.valueOf(MessagesController.107.this.val$finalUpdate.user_id));
                                    }
                                  }
                                });
                              }
                            });
                            break;
                            MessagesStorage.getInstance().deleteBlockedUser(paramArrayList1.user_id);
                          }
                        }
                        if ((localObject4 instanceof TLRPC.TL_updateNotifySettings))
                        {
                          localArrayList4.add(localObject4);
                          break;
                        }
                        if ((localObject4 instanceof TLRPC.TL_updateServiceNotification))
                        {
                          paramArrayList1 = (TLRPC.TL_updateServiceNotification)localObject4;
                          if ((paramArrayList1.popup) && (paramArrayList1.message != null) && (paramArrayList1.message.length() > 0))
                            AndroidUtilities.runOnUIThread(new Runnable(paramArrayList1)
                            {
                              public void run()
                              {
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.needShowAlert, new Object[] { Integer.valueOf(2), this.val$notification.message });
                              }
                            });
                          if ((paramArrayList1.flags & 0x2) != 0)
                          {
                            localObject3 = new TLRPC.TL_message();
                            j = UserConfig.getNewMessageId();
                            ((TLRPC.TL_message)localObject3).id = j;
                            ((TLRPC.TL_message)localObject3).local_id = j;
                            UserConfig.saveConfig(false);
                            ((TLRPC.TL_message)localObject3).unread = true;
                            ((TLRPC.TL_message)localObject3).flags = 256;
                            ((TLRPC.TL_message)localObject3).date = paramArrayList1.inbox_date;
                            ((TLRPC.TL_message)localObject3).from_id = 777000;
                            ((TLRPC.TL_message)localObject3).to_id = new TLRPC.TL_peerUser();
                            ((TLRPC.TL_message)localObject3).to_id.user_id = UserConfig.getClientUserId();
                            ((TLRPC.TL_message)localObject3).dialog_id = 777000L;
                            if (((TLRPC.Update)localObject4).media != null)
                            {
                              ((TLRPC.TL_message)localObject3).media = ((TLRPC.Update)localObject4).media;
                              ((TLRPC.TL_message)localObject3).flags |= 512;
                            }
                            ((TLRPC.TL_message)localObject3).message = paramArrayList1.message;
                            if (paramArrayList1.entities != null)
                              ((TLRPC.TL_message)localObject3).entities = paramArrayList1.entities;
                            localArrayList7.add(localObject3);
                            localObject4 = new MessageObject((TLRPC.Message)localObject3, (AbstractMap)localObject1, (AbstractMap)localObject2, this.createdDialogIds.contains(Long.valueOf(((TLRPC.TL_message)localObject3).dialog_id)));
                            paramArrayList2 = (ArrayList)localHashMap1.get(Long.valueOf(((TLRPC.TL_message)localObject3).dialog_id));
                            paramArrayList1 = paramArrayList2;
                            if (paramArrayList2 == null)
                            {
                              paramArrayList1 = new ArrayList();
                              localHashMap1.put(Long.valueOf(((TLRPC.TL_message)localObject3).dialog_id), paramArrayList1);
                            }
                            paramArrayList1.add(localObject4);
                            localArrayList6.add(localObject4);
                          }
                          break;
                        }
                        if ((localObject4 instanceof TLRPC.TL_updateDialogPinned))
                        {
                          localArrayList4.add(localObject4);
                          break;
                        }
                        if ((localObject4 instanceof TLRPC.TL_updatePinnedDialogs))
                        {
                          localArrayList4.add(localObject4);
                          break;
                        }
                        if ((localObject4 instanceof TLRPC.TL_updatePrivacy))
                        {
                          localArrayList4.add(localObject4);
                          break;
                        }
                        if ((localObject4 instanceof TLRPC.TL_updateWebPage))
                        {
                          localHashMap2.put(Long.valueOf(((TLRPC.Update)localObject4).webpage.id), ((TLRPC.Update)localObject4).webpage);
                          break;
                        }
                        if ((localObject4 instanceof TLRPC.TL_updateChannelWebPage))
                        {
                          localHashMap2.put(Long.valueOf(((TLRPC.Update)localObject4).webpage.id), ((TLRPC.Update)localObject4).webpage);
                          break;
                        }
                        if ((localObject4 instanceof TLRPC.TL_updateChannelTooLong))
                        {
                          if (BuildVars.DEBUG_VERSION)
                            FileLog.d(localObject4 + " channelId = " + ((TLRPC.Update)localObject4).channel_id);
                          paramArrayList2 = (Integer)this.channelsPts.get(Integer.valueOf(((TLRPC.Update)localObject4).channel_id));
                          paramArrayList1 = paramArrayList2;
                          if (paramArrayList2 == null)
                          {
                            paramArrayList2 = Integer.valueOf(MessagesStorage.getInstance().getChannelPtsSync(((TLRPC.Update)localObject4).channel_id));
                            if (paramArrayList2.intValue() == 0)
                            {
                              localObject3 = (TLRPC.Chat)((ConcurrentHashMap)localObject2).get(Integer.valueOf(((TLRPC.Update)localObject4).channel_id));
                              if (localObject3 != null)
                              {
                                paramArrayList1 = (ArrayList<TLRPC.User>)localObject3;
                                if (!((TLRPC.Chat)localObject3).min);
                              }
                              else
                              {
                                paramArrayList1 = getChat(Integer.valueOf(((TLRPC.Update)localObject4).channel_id));
                              }
                              if (paramArrayList1 != null)
                              {
                                localObject3 = paramArrayList1;
                                if (!paramArrayList1.min);
                              }
                              else
                              {
                                localObject3 = MessagesStorage.getInstance().getChatSync(((TLRPC.Update)localObject4).channel_id);
                                putChat((TLRPC.Chat)localObject3, true);
                              }
                              if ((localObject3 != null) && (!((TLRPC.Chat)localObject3).min))
                                loadUnknownChannel((TLRPC.Chat)localObject3, 0L);
                              paramArrayList1 = paramArrayList2;
                            }
                          }
                          else
                          {
                            label4206: if (paramArrayList1.intValue() != 0)
                            {
                              if ((((TLRPC.Update)localObject4).flags & 0x1) == 0)
                                break label4269;
                              if (((TLRPC.Update)localObject4).pts > paramArrayList1.intValue())
                                getChannelDifference(((TLRPC.Update)localObject4).channel_id);
                            }
                          }
                          while (true)
                          {
                            break;
                            this.channelsPts.put(Integer.valueOf(((TLRPC.Update)localObject4).channel_id), paramArrayList2);
                            paramArrayList1 = paramArrayList2;
                            break label4206;
                            getChannelDifference(((TLRPC.Update)localObject4).channel_id);
                          }
                        }
                        if (((localObject4 instanceof TLRPC.TL_updateReadChannelInbox)) || ((localObject4 instanceof TLRPC.TL_updateReadChannelOutbox)))
                        {
                          l1 = ((TLRPC.Update)localObject4).max_id;
                          l1 = ((TLRPC.Update)localObject4).channel_id << 32 | l1;
                          long l3 = -((TLRPC.Update)localObject4).channel_id;
                          if ((localObject4 instanceof TLRPC.TL_updateReadChannelInbox))
                          {
                            paramArrayList1 = this.dialogs_read_inbox_max;
                            localSparseArray3.put(-((TLRPC.Update)localObject4).channel_id, Long.valueOf(l1));
                          }
                          while (true)
                          {
                            localObject3 = (Integer)paramArrayList1.get(Long.valueOf(l3));
                            paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject3;
                            if (localObject3 == null)
                              paramArrayList2 = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(localObject4 instanceof TLRPC.TL_updateReadChannelOutbox, l3));
                            paramArrayList1.put(Long.valueOf(l3), Integer.valueOf(Math.max(paramArrayList2.intValue(), ((TLRPC.Update)localObject4).max_id)));
                            break;
                            paramArrayList1 = this.dialogs_read_outbox_max;
                            localSparseArray4.put(-((TLRPC.Update)localObject4).channel_id, Long.valueOf(l1));
                          }
                        }
                        if ((localObject4 instanceof TLRPC.TL_updateDeleteChannelMessages))
                        {
                          if (BuildVars.DEBUG_VERSION)
                            FileLog.d(localObject4 + " channelId = " + ((TLRPC.Update)localObject4).channel_id);
                          paramArrayList2 = (ArrayList)localSparseArray1.get(((TLRPC.Update)localObject4).channel_id);
                          paramArrayList1 = paramArrayList2;
                          if (paramArrayList2 == null)
                          {
                            paramArrayList1 = new ArrayList();
                            localSparseArray1.put(((TLRPC.Update)localObject4).channel_id, paramArrayList1);
                          }
                          paramArrayList1.addAll(((TLRPC.Update)localObject4).messages);
                          break;
                        }
                        if ((localObject4 instanceof TLRPC.TL_updateChannel))
                        {
                          if (BuildVars.DEBUG_VERSION)
                            FileLog.d(localObject4 + " channelId = " + ((TLRPC.Update)localObject4).channel_id);
                          localArrayList4.add(localObject4);
                          break;
                        }
                        if ((localObject4 instanceof TLRPC.TL_updateChannelMessageViews))
                        {
                          if (BuildVars.DEBUG_VERSION)
                            FileLog.d(localObject4 + " channelId = " + ((TLRPC.Update)localObject4).channel_id);
                          localObject3 = (TLRPC.TL_updateChannelMessageViews)localObject4;
                          paramArrayList2 = (SparseIntArray)localSparseArray2.get(((TLRPC.Update)localObject4).channel_id);
                          paramArrayList1 = paramArrayList2;
                          if (paramArrayList2 == null)
                          {
                            paramArrayList1 = new SparseIntArray();
                            localSparseArray2.put(((TLRPC.Update)localObject4).channel_id, paramArrayList1);
                          }
                          paramArrayList1.put(((TLRPC.TL_updateChannelMessageViews)localObject3).id, ((TLRPC.Update)localObject4).views);
                          break;
                        }
                        if ((localObject4 instanceof TLRPC.TL_updateChatParticipantAdmin))
                        {
                          paramArrayList1 = MessagesStorage.getInstance();
                          k = ((TLRPC.Update)localObject4).chat_id;
                          i1 = ((TLRPC.Update)localObject4).user_id;
                          if (((TLRPC.Update)localObject4).is_admin);
                          for (j = 1; ; j = 0)
                          {
                            paramArrayList1.updateChatInfo(k, i1, 2, j, ((TLRPC.Update)localObject4).version);
                            break;
                          }
                        }
                        if ((localObject4 instanceof TLRPC.TL_updateChatAdmins))
                        {
                          localArrayList4.add(localObject4);
                          break;
                        }
                        if ((localObject4 instanceof TLRPC.TL_updateStickerSets))
                        {
                          localArrayList4.add(localObject4);
                          break;
                        }
                        if ((localObject4 instanceof TLRPC.TL_updateStickerSetsOrder))
                        {
                          localArrayList4.add(localObject4);
                          break;
                        }
                        if ((localObject4 instanceof TLRPC.TL_updateNewStickerSet))
                        {
                          localArrayList4.add(localObject4);
                          break;
                        }
                        if ((localObject4 instanceof TLRPC.TL_updateDraftMessage))
                        {
                          localArrayList4.add(localObject4);
                          break;
                        }
                        if ((localObject4 instanceof TLRPC.TL_updateSavedGifs))
                        {
                          localArrayList4.add(localObject4);
                          break;
                        }
                        if (((localObject4 instanceof TLRPC.TL_updateEditChannelMessage)) || ((localObject4 instanceof TLRPC.TL_updateEditMessage)))
                        {
                          k = UserConfig.getClientUserId();
                          if ((localObject4 instanceof TLRPC.TL_updateEditChannelMessage))
                          {
                            localObject4 = ((TLRPC.TL_updateEditChannelMessage)localObject4).message;
                            paramArrayList2 = (TLRPC.Chat)((ConcurrentHashMap)localObject2).get(Integer.valueOf(((TLRPC.Message)localObject4).to_id.channel_id));
                            paramArrayList1 = paramArrayList2;
                            if (paramArrayList2 == null)
                              paramArrayList1 = getChat(Integer.valueOf(((TLRPC.Message)localObject4).to_id.channel_id));
                            localObject3 = paramArrayList1;
                            if (paramArrayList1 == null)
                            {
                              localObject3 = MessagesStorage.getInstance().getChatSync(((TLRPC.Message)localObject4).to_id.channel_id);
                              putChat((TLRPC.Chat)localObject3, true);
                            }
                            paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject4;
                            if (localObject3 != null)
                            {
                              paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject4;
                              if (((TLRPC.Chat)localObject3).megagroup)
                              {
                                ((TLRPC.Message)localObject4).flags |= -2147483648;
                                paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject4;
                              }
                            }
                            if ((!paramArrayList2.out) && (paramArrayList2.from_id == UserConfig.getClientUserId()))
                              paramArrayList2.out = true;
                            if (!paramBoolean)
                            {
                              i1 = paramArrayList2.entities.size();
                              j = 0;
                            }
                          }
                          else
                          {
                            while (true)
                            {
                              if (j >= i1)
                                break label5260;
                              paramArrayList1 = (TLRPC.MessageEntity)paramArrayList2.entities.get(j);
                              if ((paramArrayList1 instanceof TLRPC.TL_messageEntityMentionName))
                              {
                                i2 = ((TLRPC.TL_messageEntityMentionName)paramArrayList1).user_id;
                                localObject3 = (TLRPC.User)((ConcurrentHashMap)localObject1).get(Integer.valueOf(i2));
                                if (localObject3 != null)
                                {
                                  paramArrayList1 = (ArrayList<TLRPC.User>)localObject3;
                                  if (!((TLRPC.User)localObject3).min);
                                }
                                else
                                {
                                  paramArrayList1 = getUser(Integer.valueOf(i2));
                                }
                                if (paramArrayList1 != null)
                                {
                                  localObject3 = paramArrayList1;
                                  if (!paramArrayList1.min);
                                }
                                else
                                {
                                  localObject3 = MessagesStorage.getInstance().getUserSync(i2);
                                  paramArrayList1 = (ArrayList<TLRPC.User>)localObject3;
                                  if (localObject3 != null)
                                  {
                                    paramArrayList1 = (ArrayList<TLRPC.User>)localObject3;
                                    if (((TLRPC.User)localObject3).min)
                                      paramArrayList1 = null;
                                  }
                                  putUser(paramArrayList1, true);
                                  localObject3 = paramArrayList1;
                                }
                                if (localObject3 == null)
                                {
                                  return false;
                                  paramArrayList2 = ((TLRPC.TL_updateEditMessage)localObject4).message;
                                  if (paramArrayList2.dialog_id == k)
                                  {
                                    paramArrayList2.unread = false;
                                    paramArrayList2.media_unread = false;
                                    paramArrayList2.out = true;
                                  }
                                  break;
                                }
                              }
                              j += 1;
                            }
                          }
                          if (paramArrayList2.to_id.chat_id != 0)
                          {
                            paramArrayList2.dialog_id = (-paramArrayList2.to_id.chat_id);
                            if (!paramArrayList2.out)
                              break label5592;
                            paramArrayList1 = this.dialogs_read_outbox_max;
                            localObject4 = (Integer)paramArrayList1.get(Long.valueOf(paramArrayList2.dialog_id));
                            localObject3 = localObject4;
                            if (localObject4 == null)
                            {
                              localObject3 = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(paramArrayList2.out, paramArrayList2.dialog_id));
                              paramArrayList1.put(Long.valueOf(paramArrayList2.dialog_id), localObject3);
                            }
                            if (((Integer)localObject3).intValue() >= paramArrayList2.id)
                              break label5600;
                          }
                          label5592: label5600: for (bool2 = true; ; bool2 = false)
                          {
                            paramArrayList2.unread = bool2;
                            if (paramArrayList2.dialog_id == k)
                            {
                              paramArrayList2.out = true;
                              paramArrayList2.unread = false;
                              paramArrayList2.media_unread = false;
                            }
                            if ((paramArrayList2.out) && ((paramArrayList2.message == null) || (paramArrayList2.message.length() == 0)))
                            {
                              paramArrayList2.message = "-1";
                              paramArrayList2.attachPath = "";
                            }
                            ImageLoader.saveMessageThumbs(paramArrayList2);
                            localObject4 = new MessageObject(paramArrayList2, (AbstractMap)localObject1, (AbstractMap)localObject2, this.createdDialogIds.contains(Long.valueOf(paramArrayList2.dialog_id)));
                            localObject3 = (ArrayList)localHashMap3.get(Long.valueOf(paramArrayList2.dialog_id));
                            paramArrayList1 = (ArrayList<TLRPC.User>)localObject3;
                            if (localObject3 == null)
                            {
                              paramArrayList1 = new ArrayList();
                              localHashMap3.put(Long.valueOf(paramArrayList2.dialog_id), paramArrayList1);
                            }
                            paramArrayList1.add(localObject4);
                            break;
                            if (paramArrayList2.to_id.channel_id != 0)
                            {
                              paramArrayList2.dialog_id = (-paramArrayList2.to_id.channel_id);
                              break label5283;
                            }
                            if (paramArrayList2.to_id.user_id == UserConfig.getClientUserId())
                              paramArrayList2.to_id.user_id = paramArrayList2.from_id;
                            paramArrayList2.dialog_id = paramArrayList2.to_id.user_id;
                            break label5283;
                            paramArrayList1 = this.dialogs_read_inbox_max;
                            break label5295;
                          }
                        }
                        if ((localObject4 instanceof TLRPC.TL_updateChannelPinnedMessage))
                        {
                          if (BuildVars.DEBUG_VERSION)
                            FileLog.d(localObject4 + " channelId = " + ((TLRPC.Update)localObject4).channel_id);
                          paramArrayList1 = (TLRPC.TL_updateChannelPinnedMessage)localObject4;
                          MessagesStorage.getInstance().updateChannelPinnedMessage(((TLRPC.Update)localObject4).channel_id, paramArrayList1.id);
                          break;
                        }
                        if ((localObject4 instanceof TLRPC.TL_updateReadFeaturedStickers))
                        {
                          localArrayList4.add(localObject4);
                          break;
                        }
                        if ((localObject4 instanceof TLRPC.TL_updatePhoneCall))
                          localArrayList4.add(localObject4);
                        break;
                        paramBoolean = bool1;
                        if (!localHashMap1.isEmpty())
                        {
                          paramArrayList = localHashMap1.entrySet().iterator();
                          while (true)
                          {
                            paramBoolean = bool1;
                            if (!paramArrayList.hasNext())
                              break;
                            paramArrayList2 = (Map.Entry)paramArrayList.next();
                            paramArrayList1 = (Long)paramArrayList2.getKey();
                            paramArrayList2 = (ArrayList)paramArrayList2.getValue();
                            if (!updatePrintingUsersWithNewMessages(paramArrayList1.longValue(), paramArrayList2))
                              continue;
                            bool1 = true;
                          }
                        }
                        if (paramBoolean)
                          updatePrintingStrings();
                        if (!localArrayList5.isEmpty())
                          ContactsController.getInstance().processContactsUpdates(localArrayList5, (ConcurrentHashMap)localObject1);
                        if (!localArrayList6.isEmpty())
                          MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(localArrayList6)
                          {
                            public void run()
                            {
                              AndroidUtilities.runOnUIThread(new Runnable()
                              {
                                public void run()
                                {
                                  NotificationsController.getInstance().processNewMessages(MessagesController.109.this.val$pushMessages, true);
                                }
                              });
                            }
                          });
                        if (!localArrayList7.isEmpty())
                        {
                          StatsController.getInstance().incrementReceivedItemsCount(ConnectionsManager.getCurrentNetworkType(), 1, localArrayList7.size());
                          MessagesStorage.getInstance().putMessages(localArrayList7, true, true, false, MediaController.getInstance().getAutodownloadMask());
                        }
                        if (!localHashMap3.isEmpty())
                        {
                          paramArrayList = localHashMap3.entrySet().iterator();
                          while (paramArrayList.hasNext())
                          {
                            paramArrayList1 = (Map.Entry)paramArrayList.next();
                            paramArrayList2 = new TLRPC.TL_messages_messages();
                            localObject1 = (ArrayList)paramArrayList1.getValue();
                            j = 0;
                            while (j < ((ArrayList)localObject1).size())
                            {
                              paramArrayList2.messages.add(((MessageObject)((ArrayList)localObject1).get(j)).messageOwner);
                              j += 1;
                            }
                            MessagesStorage.getInstance().putMessages(paramArrayList2, ((Long)paramArrayList1.getKey()).longValue(), -2, 0, false);
                          }
                        }
                        if (localSparseArray2.size() != 0)
                          MessagesStorage.getInstance().putChannelViews(localSparseArray2, true);
                        AndroidUtilities.runOnUIThread(new Runnable(i, localArrayList4, localHashMap2, localHashMap1, localHashMap3, paramBoolean, localArrayList5, localArrayList3, localSparseArray2)
                        {
                          public void run()
                          {
                            int i = this.val$interfaceUpdateMaskFinal;
                            int k = 0;
                            int n = 0;
                            int j = i;
                            Object localObject3;
                            Object localObject4;
                            Object localObject1;
                            Object localObject5;
                            Object localObject2;
                            Object localObject6;
                            long l;
                            if (!this.val$updatesOnMainThread.isEmpty())
                            {
                              localObject3 = new ArrayList();
                              localObject4 = new ArrayList();
                              localObject1 = null;
                              int m = 0;
                              j = i;
                              i = n;
                              label952: label980: label1140: if (m < this.val$updatesOnMainThread.size())
                              {
                                localObject5 = (TLRPC.Update)this.val$updatesOnMainThread.get(m);
                                localObject2 = new TLRPC.User();
                                ((TLRPC.User)localObject2).id = ((TLRPC.Update)localObject5).user_id;
                                localObject6 = MessagesController.this.getUser(Integer.valueOf(((TLRPC.Update)localObject5).user_id));
                                if ((localObject5 instanceof TLRPC.TL_updatePrivacy))
                                  if ((((TLRPC.Update)localObject5).key instanceof TLRPC.TL_privacyKeyStatusTimestamp))
                                    ContactsController.getInstance().setPrivacyRules(((TLRPC.Update)localObject5).rules, 0);
                                while (true)
                                {
                                  m += 1;
                                  break;
                                  if ((((TLRPC.Update)localObject5).key instanceof TLRPC.TL_privacyKeyChatInvite))
                                  {
                                    ContactsController.getInstance().setPrivacyRules(((TLRPC.Update)localObject5).rules, 1);
                                    continue;
                                  }
                                  if (!(((TLRPC.Update)localObject5).key instanceof TLRPC.TL_privacyKeyPhoneCall))
                                    break label2245;
                                  ContactsController.getInstance().setPrivacyRules(((TLRPC.Update)localObject5).rules, 2);
                                  continue;
                                  if ((localObject5 instanceof TLRPC.TL_updateUserStatus))
                                  {
                                    if ((((TLRPC.Update)localObject5).status instanceof TLRPC.TL_userStatusRecently))
                                      ((TLRPC.Update)localObject5).status.expires = -100;
                                    while (true)
                                    {
                                      if (localObject6 != null)
                                      {
                                        ((TLRPC.User)localObject6).id = ((TLRPC.Update)localObject5).user_id;
                                        ((TLRPC.User)localObject6).status = ((TLRPC.Update)localObject5).status;
                                      }
                                      ((TLRPC.User)localObject2).status = ((TLRPC.Update)localObject5).status;
                                      ((ArrayList)localObject4).add(localObject2);
                                      if (((TLRPC.Update)localObject5).user_id != UserConfig.getClientUserId())
                                        break label2245;
                                      NotificationsController.getInstance().setLastOnlineFromOtherDevice(((TLRPC.Update)localObject5).status.expires);
                                      break;
                                      if ((((TLRPC.Update)localObject5).status instanceof TLRPC.TL_userStatusLastWeek))
                                      {
                                        ((TLRPC.Update)localObject5).status.expires = -101;
                                        continue;
                                      }
                                      if (!(((TLRPC.Update)localObject5).status instanceof TLRPC.TL_userStatusLastMonth))
                                        continue;
                                      ((TLRPC.Update)localObject5).status.expires = -102;
                                    }
                                  }
                                  if ((localObject5 instanceof TLRPC.TL_updateUserName))
                                  {
                                    if (localObject6 != null)
                                    {
                                      if (!UserObject.isContact((TLRPC.User)localObject6))
                                      {
                                        ((TLRPC.User)localObject6).first_name = ((TLRPC.Update)localObject5).first_name;
                                        ((TLRPC.User)localObject6).last_name = ((TLRPC.Update)localObject5).last_name;
                                      }
                                      if ((((TLRPC.User)localObject6).username != null) && (((TLRPC.User)localObject6).username.length() > 0))
                                        MessagesController.this.usersByUsernames.remove(((TLRPC.User)localObject6).username);
                                      if ((((TLRPC.Update)localObject5).username != null) && (((TLRPC.Update)localObject5).username.length() > 0))
                                        MessagesController.this.usersByUsernames.put(((TLRPC.Update)localObject5).username, localObject6);
                                      ((TLRPC.User)localObject6).username = ((TLRPC.Update)localObject5).username;
                                    }
                                    ((TLRPC.User)localObject2).first_name = ((TLRPC.Update)localObject5).first_name;
                                    ((TLRPC.User)localObject2).last_name = ((TLRPC.Update)localObject5).last_name;
                                    ((TLRPC.User)localObject2).username = ((TLRPC.Update)localObject5).username;
                                    ((ArrayList)localObject3).add(localObject2);
                                    continue;
                                  }
                                  if ((localObject5 instanceof TLRPC.TL_updateDialogPinned))
                                  {
                                    localObject2 = (TLRPC.TL_updateDialogPinned)localObject5;
                                    if ((((TLRPC.TL_updateDialogPinned)localObject2).peer instanceof TLRPC.TL_peerUser))
                                      l = ((TLRPC.TL_updateDialogPinned)localObject2).peer.user_id;
                                    while (true)
                                    {
                                      if (!MessagesController.this.pinDialog(l, ((TLRPC.TL_updateDialogPinned)localObject2).pinned, null, -1L))
                                      {
                                        UserConfig.pinnedDialogsLoaded = false;
                                        UserConfig.saveConfig(false);
                                        MessagesController.this.loadPinnedDialogs(l, null);
                                      }
                                      break;
                                      if ((((TLRPC.TL_updateDialogPinned)localObject2).peer instanceof TLRPC.TL_peerChat))
                                      {
                                        l = -((TLRPC.TL_updateDialogPinned)localObject2).peer.chat_id;
                                        continue;
                                      }
                                      l = -((TLRPC.TL_updateDialogPinned)localObject2).peer.channel_id;
                                    }
                                  }
                                  if ((localObject5 instanceof TLRPC.TL_updatePinnedDialogs))
                                  {
                                    UserConfig.pinnedDialogsLoaded = false;
                                    UserConfig.saveConfig(false);
                                    if ((((TLRPC.Update)localObject5).flags & 0x1) != 0)
                                    {
                                      localObject2 = new ArrayList();
                                      localObject5 = ((TLRPC.TL_updatePinnedDialogs)localObject5).order;
                                      k = 0;
                                      if (k < ((ArrayList)localObject5).size())
                                      {
                                        localObject6 = (TLRPC.Peer)((ArrayList)localObject5).get(k);
                                        if (((TLRPC.Peer)localObject6).user_id != 0)
                                          l = ((TLRPC.Peer)localObject6).user_id;
                                        while (true)
                                        {
                                          ((ArrayList)localObject2).add(Long.valueOf(l));
                                          k += 1;
                                          break;
                                          if (((TLRPC.Peer)localObject6).chat_id != 0)
                                          {
                                            l = -((TLRPC.Peer)localObject6).chat_id;
                                            continue;
                                          }
                                          l = -((TLRPC.Peer)localObject6).channel_id;
                                        }
                                      }
                                    }
                                    while (true)
                                    {
                                      MessagesController.this.loadPinnedDialogs(0L, (ArrayList)localObject2);
                                      break;
                                      localObject2 = null;
                                    }
                                  }
                                  if ((localObject5 instanceof TLRPC.TL_updateUserPhoto))
                                  {
                                    if (localObject6 != null)
                                      ((TLRPC.User)localObject6).photo = ((TLRPC.Update)localObject5).photo;
                                    ((TLRPC.User)localObject2).photo = ((TLRPC.Update)localObject5).photo;
                                    ((ArrayList)localObject3).add(localObject2);
                                    continue;
                                  }
                                  if (!(localObject5 instanceof TLRPC.TL_updateUserPhone))
                                    break label893;
                                  if (localObject6 != null)
                                  {
                                    ((TLRPC.User)localObject6).phone = ((TLRPC.Update)localObject5).phone;
                                    Utilities.phoneBookQueue.postRunnable(new Runnable((TLRPC.User)localObject6)
                                    {
                                      public void run()
                                      {
                                        ContactsController.getInstance().addContactToPhoneBook(this.val$currentUser, true);
                                      }
                                    });
                                  }
                                  ((TLRPC.User)localObject2).phone = ((TLRPC.Update)localObject5).phone;
                                  ((ArrayList)localObject3).add(localObject2);
                                }
                                label893: if ((localObject5 instanceof TLRPC.TL_updateNotifySettings))
                                {
                                  localObject2 = (TLRPC.TL_updateNotifySettings)localObject5;
                                  if ((!(((TLRPC.Update)localObject5).notify_settings instanceof TLRPC.TL_peerNotifySettings)) || (!(((TLRPC.TL_updateNotifySettings)localObject2).peer instanceof TLRPC.TL_notifyPeer)))
                                    break label3109;
                                  if (localObject1 != null)
                                    break label3106;
                                  localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                                  if (((TLRPC.TL_updateNotifySettings)localObject2).peer.peer.user_id != 0)
                                  {
                                    l = ((TLRPC.TL_updateNotifySettings)localObject2).peer.peer.user_id;
                                    localObject2 = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(Long.valueOf(l));
                                    if (localObject2 != null)
                                      ((TLRPC.TL_dialog)localObject2).notify_settings = ((TLRPC.Update)localObject5).notify_settings;
                                    ((SharedPreferences.Editor)localObject1).putBoolean("silent_" + l, ((TLRPC.Update)localObject5).notify_settings.silent);
                                    n = ConnectionsManager.getInstance().getCurrentTime();
                                    if (((TLRPC.Update)localObject5).notify_settings.mute_until <= n)
                                      break label1317;
                                    k = 0;
                                    if (((TLRPC.Update)localObject5).notify_settings.mute_until <= n + 31536000)
                                      break label1216;
                                    ((SharedPreferences.Editor)localObject1).putInt("notify2_" + l, 2);
                                    if (localObject2 == null)
                                      break label1314;
                                    ((TLRPC.TL_dialog)localObject2).notify_settings.mute_until = 2147483647;
                                    k = 0;
                                    MessagesStorage.getInstance().setDialogFlags(l, k << 32 | 1L);
                                    NotificationsController.getInstance().removeNotificationsForDialog(l);
                                  }
                                }
                              }
                            }
                            label1314: label3109: 
                            while (true)
                            {
                              break;
                              if (((TLRPC.TL_updateNotifySettings)localObject2).peer.peer.chat_id != 0)
                              {
                                l = -((TLRPC.TL_updateNotifySettings)localObject2).peer.peer.chat_id;
                                break label980;
                              }
                              l = -((TLRPC.TL_updateNotifySettings)localObject2).peer.peer.channel_id;
                              break label980;
                              label1216: n = ((TLRPC.Update)localObject5).notify_settings.mute_until;
                              ((SharedPreferences.Editor)localObject1).putInt("notify2_" + l, 3);
                              ((SharedPreferences.Editor)localObject1).putInt("notifyuntil_" + l, ((TLRPC.Update)localObject5).notify_settings.mute_until);
                              k = n;
                              if (localObject2 != null)
                              {
                                ((TLRPC.TL_dialog)localObject2).notify_settings.mute_until = n;
                                k = n;
                              }
                              break label1140;
                              label1317: if (localObject2 != null)
                                ((TLRPC.TL_dialog)localObject2).notify_settings.mute_until = 0;
                              ((SharedPreferences.Editor)localObject1).remove("notify2_" + l);
                              MessagesStorage.getInstance().setDialogFlags(l, 0L);
                              continue;
                              if ((localObject5 instanceof TLRPC.TL_updateChannel))
                              {
                                localObject2 = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(Long.valueOf(-((TLRPC.Update)localObject5).channel_id));
                                localObject6 = MessagesController.this.getChat(Integer.valueOf(((TLRPC.Update)localObject5).channel_id));
                                if (localObject6 != null)
                                {
                                  if ((localObject2 != null) || (!(localObject6 instanceof TLRPC.TL_channel)) || (((TLRPC.Chat)localObject6).left))
                                    break label1487;
                                  Utilities.stageQueue.postRunnable(new Runnable((TLRPC.Update)localObject5)
                                  {
                                    public void run()
                                    {
                                      MessagesController.this.getChannelDifference(this.val$update.channel_id, 1, 0L, null);
                                    }
                                  });
                                }
                                while (true)
                                {
                                  MessagesController.this.loadFullChat(((TLRPC.Update)localObject5).channel_id, 0, true);
                                  j |= 8192;
                                  break;
                                  label1487: if ((!((TLRPC.Chat)localObject6).left) || (localObject2 == null))
                                    continue;
                                  MessagesController.this.deleteDialog(((TLRPC.TL_dialog)localObject2).id, 0);
                                }
                              }
                              if ((localObject5 instanceof TLRPC.TL_updateChatAdmins))
                              {
                                j |= 16384;
                                break;
                              }
                              if ((localObject5 instanceof TLRPC.TL_updateStickerSets))
                              {
                                if (((TLRPC.Update)localObject5).masks);
                                for (k = 1; ; k = 0)
                                {
                                  StickersQuery.loadStickers(k, false, true);
                                  break;
                                }
                              }
                              if ((localObject5 instanceof TLRPC.TL_updateStickerSetsOrder))
                              {
                                if (((TLRPC.Update)localObject5).masks);
                                for (k = 1; ; k = 0)
                                {
                                  StickersQuery.reorderStickers(k, ((TLRPC.TL_updateStickerSetsOrder)localObject5).order);
                                  break;
                                }
                              }
                              if ((localObject5 instanceof TLRPC.TL_updateNewStickerSet))
                              {
                                StickersQuery.addNewStickerSet(((TLRPC.Update)localObject5).stickerset);
                                break;
                              }
                              if ((localObject5 instanceof TLRPC.TL_updateSavedGifs))
                              {
                                ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit().putLong("lastGifLoadTime", 0L).commit();
                                break;
                              }
                              if ((localObject5 instanceof TLRPC.TL_updateRecentStickers))
                              {
                                ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit().putLong("lastStickersLoadTime", 0L).commit();
                                break;
                              }
                              if ((localObject5 instanceof TLRPC.TL_updateDraftMessage))
                              {
                                localObject2 = ((TLRPC.TL_updateDraftMessage)localObject5).peer;
                                if (((TLRPC.Peer)localObject2).user_id != 0)
                                  l = ((TLRPC.Peer)localObject2).user_id;
                                while (true)
                                {
                                  DraftQuery.saveDraft(l, ((TLRPC.Update)localObject5).draft, null, true);
                                  i = 1;
                                  break;
                                  if (((TLRPC.Peer)localObject2).channel_id != 0)
                                  {
                                    l = -((TLRPC.Peer)localObject2).channel_id;
                                    continue;
                                  }
                                  l = -((TLRPC.Peer)localObject2).chat_id;
                                }
                              }
                              if ((localObject5 instanceof TLRPC.TL_updateReadFeaturedStickers))
                              {
                                StickersQuery.markFaturedStickersAsRead(false);
                                break;
                              }
                              if ((localObject5 instanceof TLRPC.TL_updatePhoneCall))
                              {
                                localObject2 = ((TLRPC.TL_updatePhoneCall)localObject5).phone_call;
                                localObject5 = VoIPService.getSharedInstance();
                                if (BuildVars.DEBUG_VERSION)
                                {
                                  FileLog.d("Received call in update: " + localObject2);
                                  FileLog.d("call id " + ((TLRPC.PhoneCall)localObject2).id);
                                }
                                if ((localObject2 instanceof TLRPC.TL_phoneCallRequested))
                                {
                                  if (((TLRPC.PhoneCall)localObject2).date + MessagesController.this.callRingTimeout / 1000 < ConnectionsManager.getInstance().getCurrentTime())
                                  {
                                    if (BuildVars.DEBUG_VERSION)
                                    {
                                      FileLog.d("ignoring too old call");
                                      break;
                                    }
                                  }
                                  else
                                  {
                                    localObject6 = (TelephonyManager)ApplicationLoader.applicationContext.getSystemService("phone");
                                    if ((localObject5 != null) || (VoIPService.callIShouldHavePutIntoIntent != null) || (((TelephonyManager)localObject6).getCallState() != 0))
                                    {
                                      if (BuildVars.DEBUG_VERSION)
                                        FileLog.d("Auto-declining call " + ((TLRPC.PhoneCall)localObject2).id + " because there's already active one");
                                      localObject5 = new TLRPC.TL_phone_discardCall();
                                      ((TLRPC.TL_phone_discardCall)localObject5).peer = new TLRPC.TL_inputPhoneCall();
                                      ((TLRPC.TL_phone_discardCall)localObject5).peer.access_hash = ((TLRPC.PhoneCall)localObject2).access_hash;
                                      ((TLRPC.TL_phone_discardCall)localObject5).peer.id = ((TLRPC.PhoneCall)localObject2).id;
                                      ((TLRPC.TL_phone_discardCall)localObject5).reason = new TLRPC.TL_phoneCallDiscardReasonBusy();
                                      ConnectionsManager.getInstance().sendRequest((TLObject)localObject5, new RequestDelegate()
                                      {
                                        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
                                        {
                                          if (paramTLObject != null)
                                          {
                                            paramTLObject = (TLRPC.Updates)paramTLObject;
                                            MessagesController.this.processUpdates(paramTLObject, false);
                                          }
                                        }
                                      });
                                      break;
                                    }
                                    if (BuildVars.DEBUG_VERSION)
                                      FileLog.d("Starting service for call " + ((TLRPC.PhoneCall)localObject2).id);
                                    VoIPService.callIShouldHavePutIntoIntent = (TLRPC.PhoneCall)localObject2;
                                    localObject5 = new Intent(ApplicationLoader.applicationContext, VoIPService.class);
                                    ((Intent)localObject5).putExtra("is_outgoing", false);
                                    if (((TLRPC.PhoneCall)localObject2).participant_id == UserConfig.getClientUserId());
                                    for (k = ((TLRPC.PhoneCall)localObject2).admin_id; ; k = ((TLRPC.PhoneCall)localObject2).participant_id)
                                    {
                                      ((Intent)localObject5).putExtra("user_id", k);
                                      ApplicationLoader.applicationContext.startService((Intent)localObject5);
                                      break;
                                    }
                                  }
                                }
                                else
                                {
                                  if ((localObject5 != null) && (localObject2 != null))
                                  {
                                    ((VoIPService)localObject5).onCallUpdated((TLRPC.PhoneCall)localObject2);
                                    break;
                                  }
                                  if (VoIPService.callIShouldHavePutIntoIntent != null)
                                  {
                                    FileLog.d("Updated the call while the service is starting");
                                    if (((TLRPC.PhoneCall)localObject2).id == VoIPService.callIShouldHavePutIntoIntent.id)
                                      VoIPService.callIShouldHavePutIntoIntent = (TLRPC.PhoneCall)localObject2;
                                  }
                                }
                              }
                              label2245: break;
                              if (localObject1 != null)
                              {
                                ((SharedPreferences.Editor)localObject1).commit();
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
                              }
                              MessagesStorage.getInstance().updateUsers((ArrayList)localObject4, true, true, true);
                              MessagesStorage.getInstance().updateUsers((ArrayList)localObject3, false, true, true);
                              k = i;
                              if (!this.val$webPages.isEmpty())
                              {
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didReceivedWebpagesInUpdates, new Object[] { this.val$webPages });
                                localObject1 = this.val$webPages.entrySet().iterator();
                                label2599: 
                                while (((Iterator)localObject1).hasNext())
                                {
                                  localObject3 = (Map.Entry)((Iterator)localObject1).next();
                                  localObject2 = (ArrayList)MessagesController.this.reloadingWebpagesPending.remove(((Map.Entry)localObject3).getKey());
                                  if (localObject2 == null)
                                    continue;
                                  localObject3 = (TLRPC.WebPage)((Map.Entry)localObject3).getValue();
                                  localObject4 = new ArrayList();
                                  l = 0L;
                                  if (((localObject3 instanceof TLRPC.TL_webPage)) || ((localObject3 instanceof TLRPC.TL_webPageEmpty)))
                                  {
                                    i = 0;
                                    while (i < ((ArrayList)localObject2).size())
                                    {
                                      ((MessageObject)((ArrayList)localObject2).get(i)).messageOwner.media.webpage = ((TLRPC.WebPage)localObject3);
                                      if (i == 0)
                                      {
                                        l = ((MessageObject)((ArrayList)localObject2).get(i)).getDialogId();
                                        ImageLoader.saveMessageThumbs(((MessageObject)((ArrayList)localObject2).get(i)).messageOwner);
                                      }
                                      ((ArrayList)localObject4).add(((MessageObject)((ArrayList)localObject2).get(i)).messageOwner);
                                      i += 1;
                                    }
                                  }
                                  while (true)
                                  {
                                    if (((ArrayList)localObject4).isEmpty())
                                      break label2599;
                                    MessagesStorage.getInstance().putMessages((ArrayList)localObject4, true, true, false, MediaController.getInstance().getAutodownloadMask());
                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.replaceMessagesObjects, new Object[] { Long.valueOf(l), localObject2 });
                                    break;
                                    MessagesController.this.reloadingWebpagesPending.put(Long.valueOf(((TLRPC.WebPage)localObject3).id), localObject2);
                                    l = 0L;
                                  }
                                }
                              }
                              i = 0;
                              if (!this.val$messages.isEmpty())
                              {
                                localObject1 = this.val$messages.entrySet().iterator();
                                while (((Iterator)localObject1).hasNext())
                                {
                                  localObject3 = (Map.Entry)((Iterator)localObject1).next();
                                  localObject2 = (Long)((Map.Entry)localObject3).getKey();
                                  localObject3 = (ArrayList)((Map.Entry)localObject3).getValue();
                                  MessagesController.this.updateInterfaceWithMessages(((Long)localObject2).longValue(), (ArrayList)localObject3);
                                }
                                i = 1;
                                if (this.val$editingMessages.isEmpty())
                                  break label2944;
                                localObject1 = this.val$editingMessages.entrySet().iterator();
                                k = i;
                                if (!((Iterator)localObject1).hasNext())
                                  break label2946;
                                localObject3 = (Map.Entry)((Iterator)localObject1).next();
                                localObject2 = (Long)((Map.Entry)localObject3).getKey();
                                localObject3 = (ArrayList)((Map.Entry)localObject3).getValue();
                                localObject4 = (MessageObject)MessagesController.this.dialogMessage.get(localObject2);
                                if (localObject4 == null)
                                  break label3103;
                                k = 0;
                                label2788: if (k >= ((ArrayList)localObject3).size())
                                  break label3103;
                                localObject5 = (MessageObject)((ArrayList)localObject3).get(k);
                                if (((MessageObject)localObject4).getId() != ((MessageObject)localObject5).getId())
                                  break label2937;
                                MessagesController.this.dialogMessage.put(localObject2, localObject5);
                                if ((((MessageObject)localObject5).messageOwner.to_id != null) && (((MessageObject)localObject5).messageOwner.to_id.channel_id == 0))
                                  MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(((MessageObject)localObject5).getId()), localObject5);
                                i = 1;
                              }
                              while (true)
                              {
                                MessagesQuery.loadReplyMessagesForMessages((ArrayList)localObject3, ((Long)localObject2).longValue());
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.replaceMessagesObjects, new Object[] { localObject2, localObject3 });
                                break label2716;
                                if (k == 0)
                                  break;
                                MessagesController.this.sortDialogs(null);
                                i = 1;
                                break;
                                label2937: k += 1;
                                break label2788;
                                k = i;
                                if (k != 0)
                                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                                i = j;
                                if (this.val$printChangedArg)
                                  i = j | 0x40;
                                j = i;
                                if (!this.val$contactsIds.isEmpty())
                                  j = i | 0x1 | 0x80;
                                if (!this.val$chatInfoToUpdate.isEmpty())
                                {
                                  i = 0;
                                  while (i < this.val$chatInfoToUpdate.size())
                                  {
                                    localObject1 = (TLRPC.ChatParticipants)this.val$chatInfoToUpdate.get(i);
                                    MessagesStorage.getInstance().updateChatParticipants((TLRPC.ChatParticipants)localObject1);
                                    i += 1;
                                  }
                                }
                                if (this.val$channelViews.size() != 0)
                                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.didUpdatedMessagesViews, new Object[] { this.val$channelViews });
                                if (j != 0)
                                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(j) });
                                return;
                              }
                              break label952;
                            }
                          }
                        });
                        MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(localSparseArray3, localSparseArray4, localHashMap4, localArrayList2, localSparseArray1)
                        {
                          public void run()
                          {
                            AndroidUtilities.runOnUIThread(new Runnable()
                            {
                              public void run()
                              {
                                int i;
                                int i1;
                                Object localObject1;
                                int n;
                                if ((MessagesController.111.this.val$markAsReadMessagesInbox.size() != 0) || (MessagesController.111.this.val$markAsReadMessagesOutbox.size() != 0))
                                {
                                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagesRead, new Object[] { MessagesController.111.this.val$markAsReadMessagesInbox, MessagesController.111.this.val$markAsReadMessagesOutbox });
                                  NotificationsController.getInstance().processReadMessages(MessagesController.111.this.val$markAsReadMessagesInbox, 0L, 0, 0, false);
                                  k = 0;
                                  for (i = 0; k < MessagesController.111.this.val$markAsReadMessagesInbox.size(); i = n)
                                  {
                                    int m = MessagesController.111.this.val$markAsReadMessagesInbox.keyAt(k);
                                    i1 = (int)((Long)MessagesController.111.this.val$markAsReadMessagesInbox.get(m)).longValue();
                                    localObject1 = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(Long.valueOf(m));
                                    m = i;
                                    if (localObject1 != null)
                                    {
                                      m = i;
                                      if (((TLRPC.TL_dialog)localObject1).top_message <= i1)
                                      {
                                        localObject1 = (MessageObject)MessagesController.this.dialogMessage.get(Long.valueOf(((TLRPC.TL_dialog)localObject1).id));
                                        m = i;
                                        if (localObject1 != null)
                                        {
                                          m = i;
                                          if (!((MessageObject)localObject1).isOut())
                                          {
                                            ((MessageObject)localObject1).setIsRead();
                                            n = i | 0x100;
                                          }
                                        }
                                      }
                                    }
                                    k += 1;
                                  }
                                  n = 0;
                                  k = i;
                                }
                                int j;
                                while (true)
                                {
                                  i = k;
                                  if (n >= MessagesController.111.this.val$markAsReadMessagesOutbox.size())
                                    break;
                                  i = MessagesController.111.this.val$markAsReadMessagesOutbox.keyAt(n);
                                  i1 = (int)((Long)MessagesController.111.this.val$markAsReadMessagesOutbox.get(i)).longValue();
                                  localObject1 = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(Long.valueOf(i));
                                  i = k;
                                  if (localObject1 != null)
                                  {
                                    i = k;
                                    if (((TLRPC.TL_dialog)localObject1).top_message <= i1)
                                    {
                                      localObject1 = (MessageObject)MessagesController.this.dialogMessage.get(Long.valueOf(((TLRPC.TL_dialog)localObject1).id));
                                      i = k;
                                      if (localObject1 != null)
                                      {
                                        i = k;
                                        if (((MessageObject)localObject1).isOut())
                                        {
                                          ((MessageObject)localObject1).setIsRead();
                                          j = k | 0x100;
                                        }
                                      }
                                    }
                                  }
                                  n += 1;
                                  k = j;
                                  continue;
                                  j = 0;
                                }
                                int k = j;
                                Object localObject2;
                                if (!MessagesController.111.this.val$markAsReadEncrypted.isEmpty())
                                {
                                  localObject1 = MessagesController.111.this.val$markAsReadEncrypted.entrySet().iterator();
                                  while (true)
                                  {
                                    k = j;
                                    if (!((Iterator)localObject1).hasNext())
                                      break;
                                    localObject2 = (Map.Entry)((Iterator)localObject1).next();
                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagesReadEncrypted, new Object[] { ((Map.Entry)localObject2).getKey(), ((Map.Entry)localObject2).getValue() });
                                    long l = ((Integer)((Map.Entry)localObject2).getKey()).intValue() << 32;
                                    if ((TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(Long.valueOf(l)) == null)
                                      continue;
                                    MessageObject localMessageObject = (MessageObject)MessagesController.this.dialogMessage.get(Long.valueOf(l));
                                    if ((localMessageObject == null) || (localMessageObject.messageOwner.date > ((Integer)((Map.Entry)localObject2).getValue()).intValue()))
                                      continue;
                                    localMessageObject.setIsRead();
                                    j |= 256;
                                  }
                                }
                                if (!MessagesController.111.this.val$markAsReadMessages.isEmpty())
                                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagesReadContent, new Object[] { MessagesController.111.this.val$markAsReadMessages });
                                if (MessagesController.111.this.val$deletedMessages.size() != 0)
                                {
                                  j = 0;
                                  if (j < MessagesController.111.this.val$deletedMessages.size())
                                  {
                                    n = MessagesController.111.this.val$deletedMessages.keyAt(j);
                                    localObject1 = (ArrayList)MessagesController.111.this.val$deletedMessages.get(n);
                                    if (localObject1 == null);
                                    label856: 
                                    while (true)
                                    {
                                      j += 1;
                                      break;
                                      NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagesDeleted, new Object[] { localObject1, Integer.valueOf(n) });
                                      if (n == 0)
                                      {
                                        n = 0;
                                        while (n < ((ArrayList)localObject1).size())
                                        {
                                          localObject2 = (Integer)((ArrayList)localObject1).get(n);
                                          localObject2 = (MessageObject)MessagesController.this.dialogMessagesByIds.get(localObject2);
                                          if (localObject2 != null)
                                            ((MessageObject)localObject2).deleted = true;
                                          n += 1;
                                        }
                                        continue;
                                      }
                                      localObject2 = (MessageObject)MessagesController.this.dialogMessage.get(Long.valueOf(-n));
                                      if (localObject2 == null)
                                        continue;
                                      n = 0;
                                      while (true)
                                      {
                                        if (n >= ((ArrayList)localObject1).size())
                                          break label856;
                                        if (((MessageObject)localObject2).getId() == ((Integer)((ArrayList)localObject1).get(n)).intValue())
                                        {
                                          ((MessageObject)localObject2).deleted = true;
                                          break;
                                        }
                                        n += 1;
                                      }
                                    }
                                  }
                                  NotificationsController.getInstance().removeDeletedMessagesFromNotifications(MessagesController.111.this.val$deletedMessages);
                                }
                                if (k != 0)
                                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(k) });
                              }
                            });
                          }
                        });
                        if (!localHashMap2.isEmpty())
                          MessagesStorage.getInstance().putWebPages(localHashMap2);
                        if ((localSparseArray3.size() != 0) || (localSparseArray4.size() != 0) || (!localHashMap4.isEmpty()))
                        {
                          if (localSparseArray3.size() != 0)
                            MessagesStorage.getInstance().updateDialogsWithReadMessages(localSparseArray3, localSparseArray4, true);
                          MessagesStorage.getInstance().markMessagesAsRead(localSparseArray3, localSparseArray4, localHashMap4, true);
                        }
                        if (!localArrayList2.isEmpty())
                          MessagesStorage.getInstance().markMessagesContentAsRead(localArrayList2);
                        if (localSparseArray1.size() != 0)
                        {
                          i = 0;
                          while (i < localSparseArray1.size())
                          {
                            j = localSparseArray1.keyAt(i);
                            paramArrayList = (ArrayList)localSparseArray1.get(j);
                            MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramArrayList, j)
                            {
                              public void run()
                              {
                                ArrayList localArrayList = MessagesStorage.getInstance().markMessagesAsDeleted(this.val$arrayList, false, this.val$key);
                                MessagesStorage.getInstance().updateDialogsWithDeletedMessages(this.val$arrayList, localArrayList, false, this.val$key);
                              }
                            });
                            i += 1;
                          }
                        }
                        if (!localArrayList1.isEmpty())
                        {
                          i = 0;
                          while (i < localArrayList1.size())
                          {
                            paramArrayList = (TLRPC.TL_updateEncryptedMessagesRead)localArrayList1.get(i);
                            MessagesStorage.getInstance().createTaskForSecretChat(paramArrayList.chat_id, paramArrayList.max_date, paramArrayList.date, 1, null);
                            i += 1;
                          }
                        }
                        return true;
                      }
                    }
                  }
                }
              }
            }
          }
          break label1054;
          break label771;
        }
        paramArrayList2 = null;
      }
    }
  }

  public void processUpdates(TLRPC.Updates paramUpdates, boolean paramBoolean)
  {
    Object localObject6 = null;
    int j = 0;
    int m = 0;
    Object localObject1;
    int i;
    int k;
    Object localObject2;
    label127: Object localObject5;
    label163: Object localObject4;
    Object localObject3;
    if ((paramUpdates instanceof TLRPC.TL_updateShort))
    {
      localObject1 = new ArrayList();
      ((ArrayList)localObject1).add(paramUpdates.update);
      processUpdateArray((ArrayList)localObject1, null, null, false);
      i = 0;
      paramUpdates = localObject6;
      k = m;
      SecretChatHelper.getInstance().processPendingEncMessages();
      if (!paramBoolean)
      {
        localObject1 = new ArrayList(this.updatesQueueChannels.keySet());
        m = 0;
        while (true)
        {
          if (m >= ((ArrayList)localObject1).size())
            break label3993;
          localObject2 = (Integer)((ArrayList)localObject1).get(m);
          if ((paramUpdates == null) || (!paramUpdates.contains(localObject2)))
            break;
          getChannelDifference(((Integer)localObject2).intValue());
          m += 1;
        }
      }
    }
    else if (((paramUpdates instanceof TLRPC.TL_updateShortChatMessage)) || ((paramUpdates instanceof TLRPC.TL_updateShortMessage)))
    {
      if ((paramUpdates instanceof TLRPC.TL_updateShortChatMessage))
      {
        k = paramUpdates.from_id;
        localObject1 = getUser(Integer.valueOf(k));
        localObject2 = null;
        localObject5 = null;
        if (localObject1 != null)
        {
          localObject4 = localObject1;
          if (!((TLRPC.User)localObject1).min);
        }
        else
        {
          localObject3 = MessagesStorage.getInstance().getUserSync(k);
          localObject1 = localObject3;
          if (localObject3 != null)
          {
            localObject1 = localObject3;
            if (((TLRPC.User)localObject3).min)
              localObject1 = null;
          }
          putUser((TLRPC.User)localObject1, true);
          localObject4 = localObject1;
        }
        j = 0;
        i = 0;
        if (paramUpdates.fwd_from == null)
          break label4150;
        localObject1 = localObject2;
        if (paramUpdates.fwd_from.from_id != 0)
        {
          localObject2 = getUser(Integer.valueOf(paramUpdates.fwd_from.from_id));
          localObject1 = localObject2;
          if (localObject2 == null)
          {
            localObject1 = MessagesStorage.getInstance().getUserSync(paramUpdates.fwd_from.from_id);
            putUser((TLRPC.User)localObject1, true);
          }
          i = 1;
        }
        if (paramUpdates.fwd_from.channel_id == 0)
          break label4140;
        localObject3 = getChat(Integer.valueOf(paramUpdates.fwd_from.channel_id));
        localObject2 = localObject3;
        if (localObject3 == null)
        {
          localObject2 = MessagesStorage.getInstance().getChatSync(paramUpdates.fwd_from.channel_id);
          putChat((TLRPC.Chat)localObject2, true);
        }
        localObject3 = localObject2;
        i = 1;
        localObject2 = localObject1;
        localObject1 = localObject3;
      }
    }
    while (true)
    {
      j = 0;
      localObject3 = localObject5;
      if (paramUpdates.via_bot_id != 0)
      {
        localObject5 = getUser(Integer.valueOf(paramUpdates.via_bot_id));
        localObject3 = localObject5;
        if (localObject5 == null)
        {
          localObject3 = MessagesStorage.getInstance().getUserSync(paramUpdates.via_bot_id);
          putUser((TLRPC.User)localObject3, true);
        }
        j = 1;
      }
      label487: label504: int n;
      if ((paramUpdates instanceof TLRPC.TL_updateShortMessage))
        if ((localObject4 == null) || ((i != 0) && (localObject2 == null) && (localObject1 == null)) || ((j != 0) && (localObject3 == null)))
        {
          i = 1;
          if ((i != 0) || (paramUpdates.entities.isEmpty()))
            break label4137;
          j = 0;
          if (j >= paramUpdates.entities.size())
            break label4137;
          localObject1 = (TLRPC.MessageEntity)paramUpdates.entities.get(j);
          if (!(localObject1 instanceof TLRPC.TL_messageEntityMentionName))
            break label798;
          n = ((TLRPC.TL_messageEntityMentionName)localObject1).user_id;
          localObject1 = getUser(Integer.valueOf(n));
          if ((localObject1 != null) && (!((TLRPC.User)localObject1).min))
            break label798;
          localObject2 = MessagesStorage.getInstance().getUserSync(n);
          localObject1 = localObject2;
          if (localObject2 != null)
          {
            localObject1 = localObject2;
            if (((TLRPC.User)localObject2).min)
              localObject1 = null;
          }
          if (localObject1 != null)
            break label790;
          i = 1;
        }
      label790: label798: label4137: 
      while (true)
      {
        if ((localObject4 != null) && (((TLRPC.User)localObject4).status != null) && (((TLRPC.User)localObject4).status.expires <= 0))
          this.onlinePrivacy.put(Integer.valueOf(((TLRPC.User)localObject4).id), Integer.valueOf(ConnectionsManager.getInstance().getCurrentTime()));
        for (j = 1; ; j = 0)
        {
          if (i != 0)
            i = 1;
          while (true)
          {
            k = i;
            i = j;
            j = k;
            k = m;
            paramUpdates = localObject6;
            break;
            k = paramUpdates.user_id;
            break label163;
            i = 0;
            break label487;
            TLRPC.Chat localChat = getChat(Integer.valueOf(paramUpdates.chat_id));
            localObject5 = localChat;
            if (localChat == null)
            {
              localObject5 = MessagesStorage.getInstance().getChatSync(paramUpdates.chat_id);
              putChat((TLRPC.Chat)localObject5, true);
            }
            if ((localObject5 == null) || (localObject4 == null) || ((i != 0) && (localObject2 == null) && (localObject1 == null)) || ((j != 0) && (localObject3 == null)));
            for (i = 1; ; i = 0)
              break;
            putUser((TLRPC.User)localObject4, true);
            j += 1;
            break label504;
            if (MessagesStorage.lastPtsValue + paramUpdates.pts_count == paramUpdates.pts)
            {
              localObject4 = new TLRPC.TL_message();
              ((TLRPC.TL_message)localObject4).id = paramUpdates.id;
              i = UserConfig.getClientUserId();
              label864: label894: label1023: boolean bool;
              if ((paramUpdates instanceof TLRPC.TL_updateShortMessage))
                if (paramUpdates.out)
                {
                  ((TLRPC.TL_message)localObject4).from_id = i;
                  ((TLRPC.TL_message)localObject4).to_id = new TLRPC.TL_peerUser();
                  ((TLRPC.TL_message)localObject4).to_id.user_id = k;
                  ((TLRPC.TL_message)localObject4).dialog_id = k;
                  ((TLRPC.TL_message)localObject4).fwd_from = paramUpdates.fwd_from;
                  ((TLRPC.TL_message)localObject4).silent = paramUpdates.silent;
                  ((TLRPC.TL_message)localObject4).out = paramUpdates.out;
                  ((TLRPC.TL_message)localObject4).mentioned = paramUpdates.mentioned;
                  ((TLRPC.TL_message)localObject4).media_unread = paramUpdates.media_unread;
                  ((TLRPC.TL_message)localObject4).entities = paramUpdates.entities;
                  ((TLRPC.TL_message)localObject4).message = paramUpdates.message;
                  ((TLRPC.TL_message)localObject4).date = paramUpdates.date;
                  ((TLRPC.TL_message)localObject4).via_bot_id = paramUpdates.via_bot_id;
                  ((TLRPC.TL_message)localObject4).flags = (paramUpdates.flags | 0x100);
                  ((TLRPC.TL_message)localObject4).reply_to_msg_id = paramUpdates.reply_to_msg_id;
                  ((TLRPC.TL_message)localObject4).media = new TLRPC.TL_messageMediaEmpty();
                  if (!((TLRPC.TL_message)localObject4).out)
                    break label1363;
                  localObject1 = this.dialogs_read_outbox_max;
                  localObject3 = (Integer)((ConcurrentHashMap)localObject1).get(Long.valueOf(((TLRPC.TL_message)localObject4).dialog_id));
                  localObject2 = localObject3;
                  if (localObject3 == null)
                  {
                    localObject2 = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(((TLRPC.TL_message)localObject4).out, ((TLRPC.TL_message)localObject4).dialog_id));
                    ((ConcurrentHashMap)localObject1).put(Long.valueOf(((TLRPC.TL_message)localObject4).dialog_id), localObject2);
                  }
                  if (((Integer)localObject2).intValue() >= ((TLRPC.TL_message)localObject4).id)
                    break label1372;
                  bool = true;
                  label1103: ((TLRPC.TL_message)localObject4).unread = bool;
                  if (((TLRPC.TL_message)localObject4).dialog_id == i)
                  {
                    ((TLRPC.TL_message)localObject4).unread = false;
                    ((TLRPC.TL_message)localObject4).media_unread = false;
                    ((TLRPC.TL_message)localObject4).out = true;
                  }
                  MessagesStorage.lastPtsValue = paramUpdates.pts;
                  localObject1 = new MessageObject((TLRPC.Message)localObject4, null, this.createdDialogIds.contains(Long.valueOf(((TLRPC.TL_message)localObject4).dialog_id)));
                  localObject2 = new ArrayList();
                  ((ArrayList)localObject2).add(localObject1);
                  localObject3 = new ArrayList();
                  ((ArrayList)localObject3).add(localObject4);
                  if (!(paramUpdates instanceof TLRPC.TL_updateShortMessage))
                    break label1384;
                  if ((paramUpdates.out) || (!updatePrintingUsersWithNewMessages(paramUpdates.user_id, (ArrayList)localObject2)))
                    break label1378;
                  bool = true;
                  label1238: if (bool)
                    updatePrintingStrings();
                  AndroidUtilities.runOnUIThread(new Runnable(bool, k, (ArrayList)localObject2)
                  {
                    public void run()
                    {
                      if (this.val$printUpdate)
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(64) });
                      MessagesController.this.updateInterfaceWithMessages(this.val$user_id, this.val$objArr);
                      NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                    }
                  });
                }
              while (true)
              {
                if (!((MessageObject)localObject1).isOut())
                  MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable((ArrayList)localObject2)
                  {
                    public void run()
                    {
                      AndroidUtilities.runOnUIThread(new Runnable()
                      {
                        public void run()
                        {
                          NotificationsController.getInstance().processNewMessages(MessagesController.102.this.val$objArr, true);
                        }
                      });
                    }
                  });
                MessagesStorage.getInstance().putMessages((ArrayList)localObject3, false, true, false, 0);
                i = 0;
                break;
                ((TLRPC.TL_message)localObject4).from_id = k;
                break label864;
                ((TLRPC.TL_message)localObject4).from_id = k;
                ((TLRPC.TL_message)localObject4).to_id = new TLRPC.TL_peerChat();
                ((TLRPC.TL_message)localObject4).to_id.chat_id = paramUpdates.chat_id;
                ((TLRPC.TL_message)localObject4).dialog_id = (-paramUpdates.chat_id);
                break label894;
                label1363: localObject1 = this.dialogs_read_inbox_max;
                break label1023;
                label1372: bool = false;
                break label1103;
                label1378: bool = false;
                break label1238;
                label1384: bool = updatePrintingUsersWithNewMessages(-paramUpdates.chat_id, (ArrayList)localObject2);
                if (bool)
                  updatePrintingStrings();
                AndroidUtilities.runOnUIThread(new Runnable(bool, paramUpdates, (ArrayList)localObject2)
                {
                  public void run()
                  {
                    if (this.val$printUpdate)
                      NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(64) });
                    MessagesController.this.updateInterfaceWithMessages(-this.val$updates.chat_id, this.val$objArr);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                  }
                });
              }
            }
            if (MessagesStorage.lastPtsValue != paramUpdates.pts)
            {
              FileLog.e("need get diff short message, pts: " + MessagesStorage.lastPtsValue + " " + paramUpdates.pts + " count = " + paramUpdates.pts_count);
              if ((this.gettingDifference) || (this.updatesStartWaitTimePts == 0L) || (Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimePts) <= 1500L))
              {
                if (this.updatesStartWaitTimePts == 0L)
                  this.updatesStartWaitTimePts = System.currentTimeMillis();
                FileLog.e("add to queue");
                this.updatesQueuePts.add(paramUpdates);
                i = 0;
                continue;
              }
              i = 1;
              continue;
              if (((paramUpdates instanceof TLRPC.TL_updatesCombined)) || ((paramUpdates instanceof TLRPC.TL_updates)))
              {
                localObject1 = null;
                i = 0;
                while (i < paramUpdates.chats.size())
                {
                  localObject4 = (TLRPC.Chat)paramUpdates.chats.get(i);
                  localObject3 = localObject1;
                  if ((localObject4 instanceof TLRPC.TL_channel))
                  {
                    localObject3 = localObject1;
                    if (((TLRPC.Chat)localObject4).min)
                    {
                      localObject3 = getChat(Integer.valueOf(((TLRPC.Chat)localObject4).id));
                      if (localObject3 != null)
                      {
                        localObject2 = localObject3;
                        if (!((TLRPC.Chat)localObject3).min);
                      }
                      else
                      {
                        localObject2 = MessagesStorage.getInstance().getChatSync(paramUpdates.chat_id);
                        if (localObject3 == null)
                          putChat((TLRPC.Chat)localObject2, true);
                      }
                      if (localObject2 != null)
                      {
                        localObject3 = localObject1;
                        if (!((TLRPC.Chat)localObject2).min);
                      }
                      else
                      {
                        localObject2 = localObject1;
                        if (localObject1 == null)
                          localObject2 = new HashMap();
                        ((HashMap)localObject2).put(Integer.valueOf(((TLRPC.Chat)localObject4).id), localObject4);
                        localObject3 = localObject2;
                      }
                    }
                  }
                  i += 1;
                  localObject1 = localObject3;
                }
                if (localObject1 == null)
                  break label4121;
                i = 0;
                if (i >= paramUpdates.updates.size())
                  break label4121;
                localObject2 = (TLRPC.Update)paramUpdates.updates.get(i);
                if ((localObject2 instanceof TLRPC.TL_updateNewChannelMessage))
                {
                  j = ((TLRPC.TL_updateNewChannelMessage)localObject2).message.to_id.channel_id;
                  if (((HashMap)localObject1).containsKey(Integer.valueOf(j)))
                    FileLog.e("need get diff because of min channel " + j);
                }
              }
              for (i = 1; ; i = 0)
              {
                if (i == 0)
                {
                  MessagesStorage.getInstance().putUsersAndChats(paramUpdates.users, paramUpdates.chats, true, true);
                  Collections.sort(paramUpdates.updates, this.updatesComparator);
                  j = 0;
                  localObject1 = null;
                  if (paramUpdates.updates.size() > 0)
                  {
                    localObject3 = (TLRPC.Update)paramUpdates.updates.get(0);
                    if (getUpdateType((TLRPC.Update)localObject3) == 0)
                    {
                      localObject2 = new TLRPC.TL_updates();
                      ((TLRPC.TL_updates)localObject2).updates.add(localObject3);
                      ((TLRPC.TL_updates)localObject2).pts = ((TLRPC.Update)localObject3).pts;
                      ((TLRPC.TL_updates)localObject2).pts_count = ((TLRPC.Update)localObject3).pts_count;
                      while (true)
                        if (1 < paramUpdates.updates.size())
                        {
                          localObject4 = (TLRPC.Update)paramUpdates.updates.get(1);
                          if ((getUpdateType((TLRPC.Update)localObject4) == 0) && (((TLRPC.TL_updates)localObject2).pts + ((TLRPC.Update)localObject4).pts_count == ((TLRPC.Update)localObject4).pts))
                          {
                            ((TLRPC.TL_updates)localObject2).updates.add(localObject4);
                            ((TLRPC.TL_updates)localObject2).pts = ((TLRPC.Update)localObject4).pts;
                            k = ((TLRPC.TL_updates)localObject2).pts_count;
                            ((TLRPC.TL_updates)localObject2).pts_count = (((TLRPC.Update)localObject4).pts_count + k);
                            paramUpdates.updates.remove(1);
                            continue;
                            i += 1;
                            break;
                          }
                        }
                      if (MessagesStorage.lastPtsValue + ((TLRPC.TL_updates)localObject2).pts_count == ((TLRPC.TL_updates)localObject2).pts)
                        if (!processUpdateArray(((TLRPC.TL_updates)localObject2).updates, paramUpdates.users, paramUpdates.chats, false))
                        {
                          FileLog.e("need get diff inner TL_updates, seq: " + MessagesStorage.lastSeqValue + " " + paramUpdates.seq);
                          i = 1;
                          label2143: k = j;
                          j = i;
                          i = k;
                        }
                    }
                    while (true)
                    {
                      paramUpdates.updates.remove(0);
                      k = j;
                      j = i;
                      i = k;
                      break;
                      MessagesStorage.lastPtsValue = ((TLRPC.TL_updates)localObject2).pts;
                      break label2143;
                      if (MessagesStorage.lastPtsValue == ((TLRPC.TL_updates)localObject2).pts)
                        break label4106;
                      FileLog.e(localObject3 + " need get diff, pts: " + MessagesStorage.lastPtsValue + " " + ((TLRPC.TL_updates)localObject2).pts + " count = " + ((TLRPC.TL_updates)localObject2).pts_count);
                      if ((this.gettingDifference) || (this.updatesStartWaitTimePts == 0L) || ((this.updatesStartWaitTimePts != 0L) && (Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimePts) <= 1500L)))
                      {
                        if (this.updatesStartWaitTimePts == 0L)
                          this.updatesStartWaitTimePts = System.currentTimeMillis();
                        FileLog.e("add to queue");
                        this.updatesQueuePts.add(localObject2);
                        break label2143;
                      }
                      i = 1;
                      break label2143;
                      if (getUpdateType((TLRPC.Update)localObject3) == 1)
                      {
                        localObject2 = new TLRPC.TL_updates();
                        ((TLRPC.TL_updates)localObject2).updates.add(localObject3);
                        ((TLRPC.TL_updates)localObject2).pts = ((TLRPC.Update)localObject3).qts;
                        while (1 < paramUpdates.updates.size())
                        {
                          localObject4 = (TLRPC.Update)paramUpdates.updates.get(1);
                          if ((getUpdateType((TLRPC.Update)localObject4) != 1) || (((TLRPC.TL_updates)localObject2).pts + 1 != ((TLRPC.Update)localObject4).qts))
                            break;
                          ((TLRPC.TL_updates)localObject2).updates.add(localObject4);
                          ((TLRPC.TL_updates)localObject2).pts = ((TLRPC.Update)localObject4).qts;
                          paramUpdates.updates.remove(1);
                        }
                        if ((MessagesStorage.lastQtsValue == 0) || (MessagesStorage.lastQtsValue + ((TLRPC.TL_updates)localObject2).updates.size() == ((TLRPC.TL_updates)localObject2).pts))
                        {
                          processUpdateArray(((TLRPC.TL_updates)localObject2).updates, paramUpdates.users, paramUpdates.chats, false);
                          MessagesStorage.lastQtsValue = ((TLRPC.TL_updates)localObject2).pts;
                          k = 1;
                          j = i;
                          i = k;
                          continue;
                        }
                        if (MessagesStorage.lastPtsValue != ((TLRPC.TL_updates)localObject2).pts)
                        {
                          FileLog.e(localObject3 + " need get diff, qts: " + MessagesStorage.lastQtsValue + " " + ((TLRPC.TL_updates)localObject2).pts);
                          if ((this.gettingDifference) || (this.updatesStartWaitTimeQts == 0L) || ((this.updatesStartWaitTimeQts != 0L) && (Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimeQts) <= 1500L)))
                          {
                            if (this.updatesStartWaitTimeQts == 0L)
                              this.updatesStartWaitTimeQts = System.currentTimeMillis();
                            FileLog.e("add to queue");
                            this.updatesQueueQts.add(localObject2);
                            k = i;
                            i = j;
                            j = k;
                            continue;
                          }
                          i = j;
                          j = 1;
                          continue;
                        }
                      }
                      else
                      {
                        if (getUpdateType((TLRPC.Update)localObject3) != 2)
                          break label3498;
                        m = getUpdateChannelId((TLRPC.Update)localObject3);
                        k = 0;
                        localObject2 = (Integer)this.channelsPts.get(Integer.valueOf(m));
                        if (localObject2 != null)
                          break label4103;
                        localObject2 = Integer.valueOf(MessagesStorage.getInstance().getChannelPtsSync(m));
                        if (((Integer)localObject2).intValue() == 0)
                        {
                          k = 0;
                          if (k >= paramUpdates.chats.size())
                            break label4097;
                          localObject4 = (TLRPC.Chat)paramUpdates.chats.get(k);
                          if (((TLRPC.Chat)localObject4).id == m)
                          {
                            loadUnknownChannel((TLRPC.Chat)localObject4, 0L);
                            k = 1;
                          }
                        }
                        while (true)
                        {
                          label2790: localObject4 = new TLRPC.TL_updates();
                          ((TLRPC.TL_updates)localObject4).updates.add(localObject3);
                          ((TLRPC.TL_updates)localObject4).pts = ((TLRPC.Update)localObject3).pts;
                          ((TLRPC.TL_updates)localObject4).pts_count = ((TLRPC.Update)localObject3).pts_count;
                          while (1 < paramUpdates.updates.size())
                          {
                            localObject5 = (TLRPC.Update)paramUpdates.updates.get(1);
                            if ((getUpdateType((TLRPC.Update)localObject5) != 2) || (m != getUpdateChannelId((TLRPC.Update)localObject5)) || (((TLRPC.TL_updates)localObject4).pts + ((TLRPC.Update)localObject5).pts_count != ((TLRPC.Update)localObject5).pts))
                              break;
                            ((TLRPC.TL_updates)localObject4).updates.add(localObject5);
                            ((TLRPC.TL_updates)localObject4).pts = ((TLRPC.Update)localObject5).pts;
                            n = ((TLRPC.TL_updates)localObject4).pts_count;
                            ((TLRPC.TL_updates)localObject4).pts_count = (((TLRPC.Update)localObject5).pts_count + n);
                            paramUpdates.updates.remove(1);
                          }
                          k += 1;
                          break;
                          this.channelsPts.put(Integer.valueOf(m), localObject2);
                        }
                        if (k == 0)
                        {
                          if (((Integer)localObject2).intValue() + ((TLRPC.TL_updates)localObject4).pts_count == ((TLRPC.TL_updates)localObject4).pts)
                          {
                            if (!processUpdateArray(((TLRPC.TL_updates)localObject4).updates, paramUpdates.users, paramUpdates.chats, false))
                            {
                              FileLog.e("need get channel diff inner TL_updates, channel_id = " + m);
                              if (localObject1 == null)
                              {
                                localObject1 = new ArrayList();
                                k = i;
                                i = j;
                                j = k;
                                continue;
                              }
                              if (!((ArrayList)localObject1).contains(Integer.valueOf(m)))
                              {
                                ((ArrayList)localObject1).add(Integer.valueOf(m));
                                k = i;
                                i = j;
                                j = k;
                                continue;
                              }
                            }
                            else
                            {
                              this.channelsPts.put(Integer.valueOf(m), Integer.valueOf(((TLRPC.TL_updates)localObject4).pts));
                              MessagesStorage.getInstance().saveChannelPts(m, ((TLRPC.TL_updates)localObject4).pts);
                              k = i;
                              i = j;
                              j = k;
                              continue;
                            }
                          }
                          else if (((Integer)localObject2).intValue() != ((TLRPC.TL_updates)localObject4).pts)
                          {
                            FileLog.e(localObject3 + " need get channel diff, pts: " + localObject2 + " " + ((TLRPC.TL_updates)localObject4).pts + " count = " + ((TLRPC.TL_updates)localObject4).pts_count + " channelId = " + m);
                            localObject5 = (Long)this.updatesStartWaitTimeChannels.get(Integer.valueOf(m));
                            localObject3 = (Boolean)this.gettingDifferenceChannels.get(Integer.valueOf(m));
                            localObject2 = localObject3;
                            if (localObject3 == null)
                              localObject2 = Boolean.valueOf(false);
                            if ((((Boolean)localObject2).booleanValue()) || (localObject5 == null) || (Math.abs(System.currentTimeMillis() - ((Long)localObject5).longValue()) <= 1500L))
                            {
                              if (localObject5 == null)
                                this.updatesStartWaitTimeChannels.put(Integer.valueOf(m), Long.valueOf(System.currentTimeMillis()));
                              FileLog.e("add to queue");
                              localObject3 = (ArrayList)this.updatesQueueChannels.get(Integer.valueOf(m));
                              localObject2 = localObject3;
                              if (localObject3 == null)
                              {
                                localObject2 = new ArrayList();
                                this.updatesQueueChannels.put(Integer.valueOf(m), localObject2);
                              }
                              ((ArrayList)localObject2).add(localObject4);
                            }
                            while (true)
                            {
                              k = i;
                              i = j;
                              j = k;
                              break;
                              if (localObject1 == null)
                              {
                                localObject1 = new ArrayList();
                                continue;
                              }
                              if (!((ArrayList)localObject1).contains(Integer.valueOf(m)))
                                ((ArrayList)localObject1).add(Integer.valueOf(m));
                            }
                          }
                        }
                        else
                          FileLog.e("need load unknown channel = " + m);
                      }
                      k = i;
                      i = j;
                      j = k;
                    }
                  }
                  label3498: if ((paramUpdates instanceof TLRPC.TL_updatesCombined))
                    if ((MessagesStorage.lastSeqValue + 1 == paramUpdates.seq_start) || (MessagesStorage.lastSeqValue == paramUpdates.seq_start))
                    {
                      k = 1;
                      label3530: if (k == 0)
                        break label3666;
                      processUpdateArray(paramUpdates.updates, paramUpdates.users, paramUpdates.chats, false);
                      if (paramUpdates.date != 0)
                        MessagesStorage.lastDateValue = paramUpdates.date;
                      localObject2 = localObject1;
                      m = j;
                      k = i;
                      if (paramUpdates.seq != 0)
                      {
                        MessagesStorage.lastSeqValue = paramUpdates.seq;
                        k = i;
                        m = j;
                        localObject2 = localObject1;
                      }
                    }
                }
                while (true)
                {
                  i = 0;
                  j = k;
                  paramUpdates = (TLRPC.Updates)localObject2;
                  k = m;
                  break;
                  k = 0;
                  break label3530;
                  if ((MessagesStorage.lastSeqValue + 1 == paramUpdates.seq) || (paramUpdates.seq == 0) || (paramUpdates.seq == MessagesStorage.lastSeqValue))
                  {
                    k = 1;
                    break label3530;
                  }
                  k = 0;
                  break label3530;
                  label3666: if ((paramUpdates instanceof TLRPC.TL_updatesCombined))
                    FileLog.e("need get diff TL_updatesCombined, seq: " + MessagesStorage.lastSeqValue + " " + paramUpdates.seq_start);
                  while (true)
                  {
                    if ((!this.gettingDifference) && (this.updatesStartWaitTimeSeq != 0L) && (Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimeSeq) > 1500L))
                      break label3831;
                    if (this.updatesStartWaitTimeSeq == 0L)
                      this.updatesStartWaitTimeSeq = System.currentTimeMillis();
                    FileLog.e("add TL_updates/Combined to queue");
                    this.updatesQueueSeq.add(paramUpdates);
                    localObject2 = localObject1;
                    m = j;
                    k = i;
                    break;
                    FileLog.e("need get diff TL_updates, seq: " + MessagesStorage.lastSeqValue + " " + paramUpdates.seq);
                  }
                  label3831: k = 1;
                  localObject2 = localObject1;
                  m = j;
                  continue;
                  if ((paramUpdates instanceof TLRPC.TL_updatesTooLong))
                  {
                    FileLog.e("need get diff TL_updatesTooLong");
                    j = 1;
                    i = 0;
                    k = m;
                    paramUpdates = localObject6;
                    break;
                  }
                  if ((paramUpdates instanceof UserActionUpdatesSeq))
                  {
                    MessagesStorage.lastSeqValue = paramUpdates.seq;
                    i = 0;
                    k = m;
                    paramUpdates = localObject6;
                    break;
                  }
                  if ((paramUpdates instanceof UserActionUpdatesPts))
                  {
                    if (paramUpdates.chat_id != 0)
                    {
                      this.channelsPts.put(Integer.valueOf(paramUpdates.chat_id), Integer.valueOf(paramUpdates.pts));
                      MessagesStorage.getInstance().saveChannelPts(paramUpdates.chat_id, paramUpdates.pts);
                      i = 0;
                      k = m;
                      paramUpdates = localObject6;
                      break;
                    }
                    MessagesStorage.lastPtsValue = paramUpdates.pts;
                  }
                  i = 0;
                  k = m;
                  paramUpdates = localObject6;
                  break;
                  processChannelsUpdatesQueue(((Integer)localObject2).intValue(), 0);
                  break label127;
                  if (j != 0)
                    getDifference();
                  while (true)
                  {
                    if (k != 0)
                    {
                      paramUpdates = new TLRPC.TL_messages_receivedQueue();
                      paramUpdates.max_qts = MessagesStorage.lastQtsValue;
                      ConnectionsManager.getInstance().sendRequest(paramUpdates, new RequestDelegate()
                      {
                        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
                        {
                        }
                      });
                    }
                    if (i != 0)
                      AndroidUtilities.runOnUIThread(new Runnable()
                      {
                        public void run()
                        {
                          NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(4) });
                        }
                      });
                    MessagesStorage.getInstance().saveDiffParams(MessagesStorage.lastSeqValue, MessagesStorage.lastPtsValue, MessagesStorage.lastDateValue, MessagesStorage.lastQtsValue);
                    return;
                    j = 0;
                    while (j < 3)
                    {
                      processUpdatesQueue(j, 0);
                      j += 1;
                    }
                  }
                  k = 0;
                  break label2790;
                  break label2790;
                  break label2143;
                  m = 0;
                  localObject2 = null;
                  k = i;
                }
              }
            }
            i = 0;
          }
        }
      }
      label3993: label4121: label4140: localObject2 = localObject1;
      label4097: label4103: label4106: localObject1 = null;
      continue;
      label4150: localObject2 = null;
      localObject1 = null;
      i = j;
    }
  }

  public void putChat(TLRPC.Chat paramChat, boolean paramBoolean)
  {
    if (paramChat == null);
    TLRPC.Chat localChat;
    label147: 
    do
    {
      while (true)
      {
        return;
        localChat = (TLRPC.Chat)this.chats.get(Integer.valueOf(paramChat.id));
        if (!paramChat.min)
          break label147;
        if (localChat == null)
          break;
        if (paramBoolean)
          continue;
        localChat.title = paramChat.title;
        localChat.photo = paramChat.photo;
        localChat.broadcast = paramChat.broadcast;
        localChat.verified = paramChat.verified;
        localChat.megagroup = paramChat.megagroup;
        localChat.democracy = paramChat.democracy;
        if (paramChat.username != null)
        {
          localChat.username = paramChat.username;
          localChat.flags |= 64;
          return;
        }
        localChat.username = null;
        localChat.flags &= -65;
        return;
      }
      this.chats.put(Integer.valueOf(paramChat.id), paramChat);
      return;
      if (!paramBoolean)
      {
        if ((localChat != null) && (paramChat.version != localChat.version))
          this.loadedFullChats.remove(Integer.valueOf(paramChat.id));
        this.chats.put(Integer.valueOf(paramChat.id), paramChat);
        return;
      }
      if (localChat != null)
        continue;
      this.chats.put(Integer.valueOf(paramChat.id), paramChat);
      return;
    }
    while (!localChat.min);
    paramChat.min = false;
    paramChat.title = localChat.title;
    paramChat.photo = localChat.photo;
    paramChat.broadcast = localChat.broadcast;
    paramChat.verified = localChat.verified;
    paramChat.megagroup = localChat.megagroup;
    paramChat.democracy = localChat.democracy;
    if (localChat.username != null)
    {
      paramChat.username = localChat.username;
      paramChat.flags |= 64;
    }
    while (true)
    {
      this.chats.put(Integer.valueOf(paramChat.id), paramChat);
      return;
      paramChat.username = null;
      paramChat.flags &= -65;
    }
  }

  public void putChats(ArrayList<TLRPC.Chat> paramArrayList, boolean paramBoolean)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty()));
    while (true)
    {
      return;
      int j = paramArrayList.size();
      int i = 0;
      while (i < j)
      {
        putChat((TLRPC.Chat)paramArrayList.get(i), paramBoolean);
        i += 1;
      }
    }
  }

  public void putEncryptedChat(TLRPC.EncryptedChat paramEncryptedChat, boolean paramBoolean)
  {
    if (paramEncryptedChat == null)
      return;
    if (paramBoolean)
    {
      this.encryptedChats.putIfAbsent(Integer.valueOf(paramEncryptedChat.id), paramEncryptedChat);
      return;
    }
    this.encryptedChats.put(Integer.valueOf(paramEncryptedChat.id), paramEncryptedChat);
  }

  public void putEncryptedChats(ArrayList<TLRPC.EncryptedChat> paramArrayList, boolean paramBoolean)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty()));
    while (true)
    {
      return;
      int j = paramArrayList.size();
      int i = 0;
      while (i < j)
      {
        putEncryptedChat((TLRPC.EncryptedChat)paramArrayList.get(i), paramBoolean);
        i += 1;
      }
    }
  }

  public boolean putUser(TLRPC.User paramUser, boolean paramBoolean)
  {
    if (paramUser == null);
    TLRPC.User localUser;
    label194: label216: label237: label255: 
    do
    {
      while (true)
      {
        return false;
        int i;
        if ((paramBoolean) && (paramUser.id / 1000 != 333) && (paramUser.id != 777000))
        {
          i = 1;
          localUser = (TLRPC.User)this.users.get(Integer.valueOf(paramUser.id));
          if ((localUser != null) && (!TextUtils.isEmpty(localUser.username)))
            this.usersByUsernames.remove(localUser.username.toLowerCase());
          if (!TextUtils.isEmpty(paramUser.username))
            this.usersByUsernames.put(paramUser.username.toLowerCase(), paramUser);
          if (!paramUser.min)
            break label255;
          if (localUser == null)
            break label237;
          if (i != 0)
            continue;
          if (paramUser.username == null)
            break label194;
          localUser.username = paramUser.username;
          localUser.flags |= 8;
        }
        while (true)
        {
          if (paramUser.photo == null)
            break label216;
          localUser.photo = paramUser.photo;
          localUser.flags |= 32;
          return false;
          i = 0;
          break;
          localUser.username = null;
          localUser.flags &= -9;
        }
        localUser.photo = null;
        localUser.flags &= -33;
        return false;
        this.users.put(Integer.valueOf(paramUser.id), paramUser);
        return false;
        if (i != 0)
          break;
        this.users.put(Integer.valueOf(paramUser.id), paramUser);
        if (paramUser.id == UserConfig.getClientUserId())
        {
          UserConfig.setCurrentUser(paramUser);
          UserConfig.saveConfig(true);
        }
        if ((localUser != null) && (paramUser.status != null) && (localUser.status != null) && (paramUser.status.expires != localUser.status.expires))
          return true;
      }
      if (localUser != null)
        continue;
      this.users.put(Integer.valueOf(paramUser.id), paramUser);
      return false;
    }
    while (!localUser.min);
    paramUser.min = false;
    if (localUser.username != null)
    {
      paramUser.username = localUser.username;
      paramUser.flags |= 8;
      if (localUser.photo == null)
        break label462;
      paramUser.photo = localUser.photo;
      paramUser.flags |= 32;
    }
    while (true)
    {
      this.users.put(Integer.valueOf(paramUser.id), paramUser);
      return false;
      paramUser.username = null;
      paramUser.flags &= -9;
      break;
      label462: paramUser.photo = null;
      paramUser.flags &= -33;
    }
  }

  public void putUsers(ArrayList<TLRPC.User> paramArrayList, boolean paramBoolean)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty()))
      return;
    int k = paramArrayList.size();
    int j = 0;
    int i = 0;
    label23: if (j < k)
    {
      if (!putUser((TLRPC.User)paramArrayList.get(j), paramBoolean))
        break label74;
      i = 1;
    }
    label74: 
    while (true)
    {
      j += 1;
      break label23;
      if (i == 0)
        break;
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(4) });
        }
      });
      return;
    }
  }

  public void registerForPush(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0) || (this.registeringForPush) || (UserConfig.getClientUserId() == 0));
    do
      return;
    while ((UserConfig.registeredForPush) && (paramString.equals(UserConfig.pushString)));
    this.registeringForPush = true;
    TLRPC.TL_account_registerDevice localTL_account_registerDevice = new TLRPC.TL_account_registerDevice();
    localTL_account_registerDevice.token_type = 2;
    localTL_account_registerDevice.token = paramString;
    ConnectionsManager.getInstance().sendRequest(localTL_account_registerDevice, new RequestDelegate(paramString)
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        if ((paramTLObject instanceof TLRPC.TL_boolTrue))
        {
          FileLog.e("registered for push");
          UserConfig.registeredForPush = true;
          UserConfig.pushString = this.val$regid;
          UserConfig.saveConfig(false);
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            MessagesController.this.registeringForPush = false;
          }
        });
      }
    });
  }

  public void reloadWebPages(long paramLong, HashMap<String, ArrayList<MessageObject>> paramHashMap)
  {
    Iterator localIterator = paramHashMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      paramHashMap = (Map.Entry)localIterator.next();
      String str = (String)paramHashMap.getKey();
      ArrayList localArrayList2 = (ArrayList)paramHashMap.getValue();
      ArrayList localArrayList1 = (ArrayList)this.reloadingWebpages.get(str);
      paramHashMap = localArrayList1;
      if (localArrayList1 == null)
      {
        paramHashMap = new ArrayList();
        this.reloadingWebpages.put(str, paramHashMap);
      }
      paramHashMap.addAll(localArrayList2);
      paramHashMap = new TLRPC.TL_messages_getWebPagePreview();
      paramHashMap.message = str;
      ConnectionsManager.getInstance().sendRequest(paramHashMap, new RequestDelegate(str, paramLong)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
          {
            public void run()
            {
              ArrayList localArrayList = (ArrayList)MessagesController.this.reloadingWebpages.remove(MessagesController.52.this.val$url);
              if (localArrayList == null);
              TLRPC.TL_messages_messages localTL_messages_messages;
              do
              {
                return;
                localTL_messages_messages = new TLRPC.TL_messages_messages();
                int i;
                if (!(this.val$response instanceof TLRPC.TL_messageMediaWebPage))
                {
                  i = 0;
                  while (i < localArrayList.size())
                  {
                    ((MessageObject)localArrayList.get(i)).messageOwner.media.webpage = new TLRPC.TL_webPageEmpty();
                    localTL_messages_messages.messages.add(((MessageObject)localArrayList.get(i)).messageOwner);
                    i += 1;
                  }
                }
                TLRPC.TL_messageMediaWebPage localTL_messageMediaWebPage = (TLRPC.TL_messageMediaWebPage)this.val$response;
                if (((localTL_messageMediaWebPage.webpage instanceof TLRPC.TL_webPage)) || ((localTL_messageMediaWebPage.webpage instanceof TLRPC.TL_webPageEmpty)))
                  i = 0;
                while (i < localArrayList.size())
                {
                  ((MessageObject)localArrayList.get(i)).messageOwner.media.webpage = localTL_messageMediaWebPage.webpage;
                  if (i == 0)
                    ImageLoader.saveMessageThumbs(((MessageObject)localArrayList.get(i)).messageOwner);
                  localTL_messages_messages.messages.add(((MessageObject)localArrayList.get(i)).messageOwner);
                  i += 1;
                  continue;
                  MessagesController.this.reloadingWebpagesPending.put(Long.valueOf(localTL_messageMediaWebPage.webpage.id), localArrayList);
                }
              }
              while (localTL_messages_messages.messages.isEmpty());
              MessagesStorage.getInstance().putMessages(localTL_messages_messages, MessagesController.52.this.val$dialog_id, -2, 0, false);
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.replaceMessagesObjects, new Object[] { Long.valueOf(MessagesController.52.this.val$dialog_id), localArrayList });
            }
          });
        }
      });
    }
  }

  public void reportSpam(long paramLong, TLRPC.User paramUser, TLRPC.Chat paramChat, TLRPC.EncryptedChat paramEncryptedChat)
  {
    if ((paramUser == null) && (paramChat == null) && (paramEncryptedChat == null));
    while (true)
    {
      return;
      SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
      localEditor.putInt("spam3_" + paramLong, 1);
      localEditor.commit();
      if ((int)paramLong != 0)
        break;
      if ((paramEncryptedChat == null) || (paramEncryptedChat.access_hash == 0L))
        continue;
      paramUser = new TLRPC.TL_messages_reportEncryptedSpam();
      paramUser.peer = new TLRPC.TL_inputEncryptedChat();
      paramUser.peer.chat_id = paramEncryptedChat.id;
      paramUser.peer.access_hash = paramEncryptedChat.access_hash;
      ConnectionsManager.getInstance().sendRequest(paramUser, new RequestDelegate()
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
        }
      }
      , 2);
      return;
    }
    paramEncryptedChat = new TLRPC.TL_messages_reportSpam();
    if (paramChat != null)
      paramEncryptedChat.peer = getInputPeer(-paramChat.id);
    while (true)
    {
      ConnectionsManager.getInstance().sendRequest(paramEncryptedChat, new RequestDelegate()
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
        }
      }
      , 2);
      return;
      if (paramUser == null)
        continue;
      paramEncryptedChat.peer = getInputPeer(paramUser.id);
    }
  }

  public void saveGif(TLRPC.Document paramDocument)
  {
    TLRPC.TL_messages_saveGif localTL_messages_saveGif = new TLRPC.TL_messages_saveGif();
    localTL_messages_saveGif.id = new TLRPC.TL_inputDocument();
    localTL_messages_saveGif.id.id = paramDocument.id;
    localTL_messages_saveGif.id.access_hash = paramDocument.access_hash;
    localTL_messages_saveGif.unsave = false;
    ConnectionsManager.getInstance().sendRequest(localTL_messages_saveGif, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
      }
    });
  }

  public void saveRecentSticker(TLRPC.Document paramDocument, boolean paramBoolean)
  {
    TLRPC.TL_messages_saveRecentSticker localTL_messages_saveRecentSticker = new TLRPC.TL_messages_saveRecentSticker();
    localTL_messages_saveRecentSticker.id = new TLRPC.TL_inputDocument();
    localTL_messages_saveRecentSticker.id.id = paramDocument.id;
    localTL_messages_saveRecentSticker.id.access_hash = paramDocument.access_hash;
    localTL_messages_saveRecentSticker.unsave = false;
    localTL_messages_saveRecentSticker.attached = paramBoolean;
    ConnectionsManager.getInstance().sendRequest(localTL_messages_saveRecentSticker, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
      }
    });
  }

  public void sendBotStart(TLRPC.User paramUser, String paramString)
  {
    if (paramUser == null)
      return;
    TLRPC.TL_messages_startBot localTL_messages_startBot = new TLRPC.TL_messages_startBot();
    localTL_messages_startBot.bot = getInputUser(paramUser);
    localTL_messages_startBot.peer = getInputPeer(paramUser.id);
    localTL_messages_startBot.start_param = paramString;
    localTL_messages_startBot.random_id = Utilities.random.nextLong();
    ConnectionsManager.getInstance().sendRequest(localTL_messages_startBot, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        if (paramTL_error != null)
          return;
        MessagesController.this.processUpdates((TLRPC.Updates)paramTLObject, false);
      }
    });
  }

  public void sendMessageReaad()
  {
    if (req != null)
      ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate()
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          if ((paramTL_error == null) && ((paramTLObject instanceof TLRPC.TL_messages_affectedMessages)))
          {
            paramTLObject = (TLRPC.TL_messages_affectedMessages)paramTLObject;
            MessagesController.this.processNewDifferenceParams(-1, paramTLObject.pts, -1, paramTLObject.pts_count);
          }
        }
      });
  }

  public void sendTyping(long paramLong, int paramInt1, int paramInt2)
  {
    if (b.a(ApplicationLoader.applicationContext).b());
    label12: 
    do
    {
      Object localObject2;
      Object localObject1;
      do
      {
        int j;
        do
          while (true)
          {
            break label12;
            break label12;
            break label12;
            break label12;
            break label12;
            break label12;
            do
              return;
            while (paramLong == 0L);
            localObject2 = (HashMap)this.sendingTypings.get(Integer.valueOf(paramInt1));
            if ((localObject2 != null) && (((HashMap)localObject2).get(Long.valueOf(paramLong)) != null))
              continue;
            localObject1 = localObject2;
            if (localObject2 == null)
            {
              localObject1 = new HashMap();
              this.sendingTypings.put(Integer.valueOf(paramInt1), localObject1);
            }
            int i = (int)paramLong;
            j = (int)(paramLong >> 32);
            if (i == 0)
              break;
            if (j == 1)
              continue;
            localObject2 = new TLRPC.TL_messages_setTyping();
            ((TLRPC.TL_messages_setTyping)localObject2).peer = getInputPeer(i);
            if ((((TLRPC.TL_messages_setTyping)localObject2).peer instanceof TLRPC.TL_inputPeerChannel))
            {
              localObject3 = getChat(Integer.valueOf(((TLRPC.TL_messages_setTyping)localObject2).peer.channel_id));
              if ((localObject3 == null) || (!((TLRPC.Chat)localObject3).megagroup))
                continue;
            }
            if (((TLRPC.TL_messages_setTyping)localObject2).peer == null)
              continue;
            if (paramInt1 == 0)
              ((TLRPC.TL_messages_setTyping)localObject2).action = new TLRPC.TL_sendMessageTypingAction();
            while (true)
            {
              ((HashMap)localObject1).put(Long.valueOf(paramLong), Boolean.valueOf(true));
              paramInt1 = ConnectionsManager.getInstance().sendRequest((TLObject)localObject2, new RequestDelegate(paramInt1, paramLong)
              {
                public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
                {
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      HashMap localHashMap = (HashMap)MessagesController.this.sendingTypings.get(Integer.valueOf(MessagesController.49.this.val$action));
                      if (localHashMap != null)
                        localHashMap.remove(Long.valueOf(MessagesController.49.this.val$dialog_id));
                    }
                  });
                }
              }
              , 2);
              if (paramInt2 == 0)
                break;
              ConnectionsManager.getInstance().bindRequestToGuid(paramInt1, paramInt2);
              return;
              if (paramInt1 == 1)
              {
                ((TLRPC.TL_messages_setTyping)localObject2).action = new TLRPC.TL_sendMessageRecordAudioAction();
                continue;
              }
              if (paramInt1 == 2)
              {
                ((TLRPC.TL_messages_setTyping)localObject2).action = new TLRPC.TL_sendMessageCancelAction();
                continue;
              }
              if (paramInt1 == 3)
              {
                ((TLRPC.TL_messages_setTyping)localObject2).action = new TLRPC.TL_sendMessageUploadDocumentAction();
                continue;
              }
              if (paramInt1 == 4)
              {
                ((TLRPC.TL_messages_setTyping)localObject2).action = new TLRPC.TL_sendMessageUploadPhotoAction();
                continue;
              }
              if (paramInt1 == 5)
              {
                ((TLRPC.TL_messages_setTyping)localObject2).action = new TLRPC.TL_sendMessageUploadVideoAction();
                continue;
              }
              if (paramInt1 != 6)
                continue;
              ((TLRPC.TL_messages_setTyping)localObject2).action = new TLRPC.TL_sendMessageGamePlayAction();
            }
          }
        while (paramInt1 != 0);
        localObject2 = getEncryptedChat(Integer.valueOf(j));
      }
      while ((((TLRPC.EncryptedChat)localObject2).auth_key == null) || (((TLRPC.EncryptedChat)localObject2).auth_key.length <= 1) || (!(localObject2 instanceof TLRPC.TL_encryptedChat)));
      Object localObject3 = new TLRPC.TL_messages_setEncryptedTyping();
      ((TLRPC.TL_messages_setEncryptedTyping)localObject3).peer = new TLRPC.TL_inputEncryptedChat();
      ((TLRPC.TL_messages_setEncryptedTyping)localObject3).peer.chat_id = ((TLRPC.EncryptedChat)localObject2).id;
      ((TLRPC.TL_messages_setEncryptedTyping)localObject3).peer.access_hash = ((TLRPC.EncryptedChat)localObject2).access_hash;
      ((TLRPC.TL_messages_setEncryptedTyping)localObject3).typing = true;
      ((HashMap)localObject1).put(Long.valueOf(paramLong), Boolean.valueOf(true));
      paramInt1 = ConnectionsManager.getInstance().sendRequest((TLObject)localObject3, new RequestDelegate(paramInt1, paramLong)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              HashMap localHashMap = (HashMap)MessagesController.this.sendingTypings.get(Integer.valueOf(MessagesController.50.this.val$action));
              if (localHashMap != null)
                localHashMap.remove(Long.valueOf(MessagesController.50.this.val$dialog_id));
            }
          });
        }
      }
      , 2);
    }
    while (paramInt2 == 0);
    ConnectionsManager.getInstance().bindRequestToGuid(paramInt1, paramInt2);
  }

  public void setLastCreatedDialogId(long paramLong, boolean paramBoolean)
  {
    Utilities.stageQueue.postRunnable(new Runnable(paramBoolean, paramLong)
    {
      public void run()
      {
        if (this.val$set)
        {
          MessagesController.this.createdDialogIds.add(Long.valueOf(this.val$dialog_id));
          return;
        }
        MessagesController.this.createdDialogIds.remove(Long.valueOf(this.val$dialog_id));
      }
    });
  }

  public void sortDialogs(HashMap<Integer, TLRPC.Chat> paramHashMap)
  {
    this.dialogsServerOnly.clear();
    this.dialogsGroupsOnly.clear();
    this.dialogsChannelOnly.clear();
    this.dialogsUserOnly.clear();
    this.dialogsVidogramOnly.clear();
    this.dialogsBotOnly.clear();
    this.groupsUnread_count = 0;
    this.channelsUnread_count = 0;
    this.usersUnread_count = 0;
    this.botsUnread_count = 0;
    this.allUnread_count = 0;
    this.isGroupsMute = 0;
    this.isChannelsMute = 0;
    this.isAllMute = 0;
    this.isBotsMute = 0;
    this.isUsersMute = 0;
    Collections.sort(this.dialogs, this.dialogComparator);
    int i = 0;
    if (i < this.dialogs.size())
    {
      TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)this.dialogs.get(i);
      this.allUnread_count += localTL_dialog.unread_count;
      if ((!isDialogMuted(localTL_dialog.id)) && (localTL_dialog.unread_count > 0))
        this.isAllMute += 1;
      int k = (int)(localTL_dialog.id >> 32);
      int m = (int)localTL_dialog.id;
      int j;
      if (m == 0)
      {
        this.dialogsUserOnly.add(localTL_dialog);
        this.usersUnread_count += localTL_dialog.unread_count;
        j = i;
        if (!isDialogMuted(localTL_dialog.id))
        {
          j = i;
          if (localTL_dialog.unread_count > 0)
          {
            this.isUsersMute += 1;
            j = i;
          }
        }
      }
      while (true)
      {
        i = j + 1;
        break;
        j = i;
        if (m == 0)
          continue;
        j = i;
        if (k == 1)
          continue;
        this.dialogsServerOnly.add(localTL_dialog);
        TLRPC.Chat localChat;
        if (DialogObject.isChannel(localTL_dialog))
        {
          localChat = getChat(Integer.valueOf(-m));
          if ((localChat != null) && ((localChat.id < 0) || ((ChatObject.isChannel(localChat)) && (!localChat.megagroup))))
          {
            this.dialogsChannelOnly.add(localTL_dialog);
            this.channelsUnread_count += localTL_dialog.unread_count;
            j = i;
            if (isDialogMuted(localTL_dialog.id))
              continue;
            j = i;
            if (localTL_dialog.unread_count <= 0)
              continue;
            this.isChannelsMute += 1;
            j = i;
            continue;
          }
          this.dialogsGroupsOnly.add(localTL_dialog);
          this.groupsUnread_count += localTL_dialog.unread_count;
          j = i;
          if (isDialogMuted(localTL_dialog.id))
            continue;
          j = i;
          if (localTL_dialog.unread_count <= 0)
            continue;
          this.isGroupsMute += 1;
          j = i;
          continue;
        }
        if (m < 0)
        {
          if (paramHashMap != null)
          {
            localChat = (TLRPC.Chat)paramHashMap.get(Integer.valueOf(-m));
            if ((localChat != null) && (localChat.migrated_to != null))
            {
              this.dialogs.remove(i);
              j = i - 1;
              continue;
            }
          }
          this.dialogsGroupsOnly.add(localTL_dialog);
          this.groupsUnread_count += localTL_dialog.unread_count;
          j = i;
          if (isDialogMuted(localTL_dialog.id))
            continue;
          j = i;
          if (localTL_dialog.unread_count <= 0)
            continue;
          this.isGroupsMute += 1;
          j = i;
          continue;
        }
        if (getInstance().getUser(Integer.valueOf(m)).bot)
        {
          this.dialogsBotOnly.add(localTL_dialog);
          this.botsUnread_count += localTL_dialog.unread_count;
          j = i;
          if (isDialogMuted(localTL_dialog.id))
            continue;
          j = i;
          if (localTL_dialog.unread_count <= 0)
            continue;
          this.isBotsMute += 1;
          j = i;
          continue;
        }
        this.dialogsUserOnly.add(localTL_dialog);
        this.usersUnread_count += localTL_dialog.unread_count;
        j = i;
        if (isDialogMuted(localTL_dialog.id))
          continue;
        j = i;
        if (localTL_dialog.unread_count <= 0)
          continue;
        this.isUsersMute += 1;
        j = i;
      }
    }
  }

  public void startShortPoll(int paramInt, boolean paramBoolean)
  {
    Utilities.stageQueue.postRunnable(new Runnable(paramBoolean, paramInt)
    {
      public void run()
      {
        if (this.val$stop)
          MessagesController.this.needShortPollChannels.delete(this.val$channelId);
        do
        {
          return;
          MessagesController.this.needShortPollChannels.put(this.val$channelId, 0);
        }
        while (MessagesController.this.shortPollChannels.indexOfKey(this.val$channelId) >= 0);
        MessagesController.this.getChannelDifference(this.val$channelId, 3, 0L, null);
      }
    });
  }

  public void toggleAdminMode(int paramInt, boolean paramBoolean)
  {
    TLRPC.TL_messages_toggleChatAdmins localTL_messages_toggleChatAdmins = new TLRPC.TL_messages_toggleChatAdmins();
    localTL_messages_toggleChatAdmins.chat_id = paramInt;
    localTL_messages_toggleChatAdmins.enabled = paramBoolean;
    ConnectionsManager.getInstance().sendRequest(localTL_messages_toggleChatAdmins, new RequestDelegate(paramInt)
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        if (paramTL_error == null)
        {
          MessagesController.this.processUpdates((TLRPC.Updates)paramTLObject, false);
          MessagesController.this.loadFullChat(this.val$chat_id, 0, true);
        }
      }
    });
  }

  public void toggleUserAdmin(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    TLRPC.TL_messages_editChatAdmin localTL_messages_editChatAdmin = new TLRPC.TL_messages_editChatAdmin();
    localTL_messages_editChatAdmin.chat_id = paramInt1;
    localTL_messages_editChatAdmin.user_id = getInputUser(paramInt2);
    localTL_messages_editChatAdmin.is_admin = paramBoolean;
    ConnectionsManager.getInstance().sendRequest(localTL_messages_editChatAdmin, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
      }
    });
  }

  public void toogleChannelInvites(int paramInt, boolean paramBoolean)
  {
    TLRPC.TL_channels_toggleInvites localTL_channels_toggleInvites = new TLRPC.TL_channels_toggleInvites();
    localTL_channels_toggleInvites.channel = getInputChannel(paramInt);
    localTL_channels_toggleInvites.enabled = paramBoolean;
    ConnectionsManager.getInstance().sendRequest(localTL_channels_toggleInvites, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        if (paramTLObject != null)
          MessagesController.this.processUpdates((TLRPC.Updates)paramTLObject, false);
      }
    }
    , 64);
  }

  public void toogleChannelSignatures(int paramInt, boolean paramBoolean)
  {
    TLRPC.TL_channels_toggleSignatures localTL_channels_toggleSignatures = new TLRPC.TL_channels_toggleSignatures();
    localTL_channels_toggleSignatures.channel = getInputChannel(paramInt);
    localTL_channels_toggleSignatures.enabled = paramBoolean;
    ConnectionsManager.getInstance().sendRequest(localTL_channels_toggleSignatures, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        if (paramTLObject != null)
        {
          MessagesController.this.processUpdates((TLRPC.Updates)paramTLObject, false);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(8192) });
            }
          });
        }
      }
    }
    , 64);
  }

  public void unblockUser(int paramInt)
  {
    TLRPC.TL_contacts_unblock localTL_contacts_unblock = new TLRPC.TL_contacts_unblock();
    TLRPC.User localUser = getUser(Integer.valueOf(paramInt));
    if (localUser == null)
      return;
    this.blockedUsers.remove(Integer.valueOf(localUser.id));
    localTL_contacts_unblock.id = getInputUser(localUser);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
    ConnectionsManager.getInstance().sendRequest(localTL_contacts_unblock, new RequestDelegate(localUser)
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        MessagesStorage.getInstance().deleteBlockedUser(this.val$user.id);
      }
    });
  }

  public void unregistedPush()
  {
    if ((UserConfig.registeredForPush) && (UserConfig.pushString.length() == 0))
    {
      TLRPC.TL_account_unregisterDevice localTL_account_unregisterDevice = new TLRPC.TL_account_unregisterDevice();
      localTL_account_unregisterDevice.token = UserConfig.pushString;
      localTL_account_unregisterDevice.token_type = 2;
      ConnectionsManager.getInstance().sendRequest(localTL_account_unregisterDevice, new RequestDelegate()
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
        }
      });
    }
  }

  public void updateChannelAbout(int paramInt, String paramString, TLRPC.ChatFull paramChatFull)
  {
    if (paramChatFull == null)
      return;
    TLRPC.TL_channels_editAbout localTL_channels_editAbout = new TLRPC.TL_channels_editAbout();
    localTL_channels_editAbout.channel = getInputChannel(paramInt);
    localTL_channels_editAbout.about = paramString;
    ConnectionsManager.getInstance().sendRequest(localTL_channels_editAbout, new RequestDelegate(paramChatFull, paramString)
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        if ((paramTLObject instanceof TLRPC.TL_boolTrue))
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              MessagesController.73.this.val$info.about = MessagesController.73.this.val$about;
              MessagesStorage.getInstance().updateChatInfo(MessagesController.73.this.val$info, false);
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatInfoDidLoaded, new Object[] { MessagesController.73.this.val$info, Integer.valueOf(0), Boolean.valueOf(false), null });
            }
          });
      }
    }
    , 64);
  }

  public void updateChannelUserName(int paramInt, String paramString)
  {
    TLRPC.TL_channels_updateUsername localTL_channels_updateUsername = new TLRPC.TL_channels_updateUsername();
    localTL_channels_updateUsername.channel = getInputChannel(paramInt);
    localTL_channels_updateUsername.username = paramString;
    ConnectionsManager.getInstance().sendRequest(localTL_channels_updateUsername, new RequestDelegate(paramInt, paramString)
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        if ((paramTLObject instanceof TLRPC.TL_boolTrue))
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              TLRPC.Chat localChat = MessagesController.this.getChat(Integer.valueOf(MessagesController.74.this.val$chat_id));
              if (MessagesController.74.this.val$userName.length() != 0)
                localChat.flags |= 64;
              while (true)
              {
                localChat.username = MessagesController.74.this.val$userName;
                ArrayList localArrayList = new ArrayList();
                localArrayList.add(localChat);
                MessagesStorage.getInstance().putUsersAndChats(null, localArrayList, true, true);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(8192) });
                return;
                localChat.flags &= -65;
              }
            }
          });
      }
    }
    , 64);
  }

  public void updateConfig(TLRPC.TL_config paramTL_config)
  {
    AndroidUtilities.runOnUIThread(new Runnable(paramTL_config)
    {
      public void run()
      {
        MessagesController.this.maxMegagroupCount = this.val$config.megagroup_size_max;
        MessagesController.this.maxGroupCount = this.val$config.chat_size_max;
        MessagesController.this.groupBigSize = this.val$config.chat_big_size;
        MessagesController.access$202(MessagesController.this, this.val$config.disabled_features);
        MessagesController.this.maxEditTime = this.val$config.edit_time_limit;
        MessagesController.this.ratingDecay = this.val$config.rating_e_decay;
        MessagesController.this.maxRecentGifsCount = this.val$config.saved_gifs_limit;
        MessagesController.this.maxRecentStickersCount = this.val$config.stickers_recent_limit;
        boolean bool = MessagesController.this.callsEnabled;
        MessagesController.this.callsEnabled = this.val$config.phonecalls_enabled;
        MessagesController.this.linkPrefix = this.val$config.me_url_prefix;
        if (MessagesController.this.linkPrefix.endsWith("/"))
          MessagesController.this.linkPrefix = MessagesController.this.linkPrefix.substring(0, MessagesController.this.linkPrefix.length() - 1);
        SharedPreferences.Editor localEditor;
        if (MessagesController.this.linkPrefix.startsWith("https://"))
        {
          MessagesController.this.linkPrefix = MessagesController.this.linkPrefix.substring(8);
          MessagesController.this.callReceiveTimeout = this.val$config.call_receive_timeout_ms;
          MessagesController.this.callRingTimeout = this.val$config.call_ring_timeout_ms;
          MessagesController.this.callConnectTimeout = this.val$config.call_connect_timeout_ms;
          MessagesController.this.callPacketTimeout = this.val$config.call_packet_timeout_ms;
          MessagesController.this.maxPinnedDialogsCount = this.val$config.pinned_dialogs_count_max;
          localEditor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
          localEditor.putInt("maxGroupCount", MessagesController.this.maxGroupCount);
          localEditor.putInt("maxMegagroupCount", MessagesController.this.maxMegagroupCount);
          localEditor.putInt("groupBigSize", MessagesController.this.groupBigSize);
          localEditor.putInt("maxEditTime", MessagesController.this.maxEditTime);
          localEditor.putInt("ratingDecay", MessagesController.this.ratingDecay);
          localEditor.putInt("maxRecentGifsCount", MessagesController.this.maxRecentGifsCount);
          localEditor.putInt("maxRecentStickersCount", MessagesController.this.maxRecentStickersCount);
          localEditor.putInt("callReceiveTimeout", MessagesController.this.callReceiveTimeout);
          localEditor.putInt("callRingTimeout", MessagesController.this.callRingTimeout);
          localEditor.putInt("callConnectTimeout", MessagesController.this.callConnectTimeout);
          localEditor.putInt("callPacketTimeout", MessagesController.this.callPacketTimeout);
          if (!b.a(ApplicationLoader.applicationContext).z())
            break label688;
          localEditor.putBoolean("callsEnabled", true);
          label511: localEditor.putString("linkPrefix", MessagesController.this.linkPrefix);
          localEditor.putInt("maxPinnedDialogsCount", MessagesController.this.maxPinnedDialogsCount);
          try
          {
            SerializedData localSerializedData = new SerializedData();
            localSerializedData.writeInt32(MessagesController.this.disabledFeatures.size());
            Iterator localIterator = MessagesController.this.disabledFeatures.iterator();
            while (localIterator.hasNext())
              ((TLRPC.TL_disabledFeature)localIterator.next()).serializeToStream(localSerializedData);
          }
          catch (Exception localException)
          {
            localEditor.remove("disabledFeatures");
            FileLog.e(localException);
          }
        }
        while (true)
        {
          localEditor.commit();
          if (MessagesController.this.callsEnabled != bool)
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
          return;
          if (!MessagesController.this.linkPrefix.startsWith("http://"))
            break;
          MessagesController.this.linkPrefix = MessagesController.this.linkPrefix.substring(7);
          break;
          label688: localEditor.putBoolean("callsEnabled", MessagesController.this.callsEnabled);
          break label511;
          String str = Base64.encodeToString(localException.toByteArray(), 0);
          if (str.length() == 0)
            continue;
          localEditor.putString("disabledFeatures", str);
        }
      }
    });
  }

  protected void updateInterfaceWithMessages(long paramLong, ArrayList<MessageObject> paramArrayList)
  {
    updateInterfaceWithMessages(paramLong, paramArrayList, false);
  }

  protected void updateInterfaceWithMessages(long paramLong, ArrayList<MessageObject> paramArrayList, boolean paramBoolean)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty()))
      return;
    int j;
    label20: Object localObject2;
    int n;
    int i;
    int k;
    label32: MessageObject localMessageObject;
    int m;
    Object localObject1;
    if ((int)paramLong == 0)
    {
      j = 1;
      localObject2 = null;
      n = 0;
      i = 0;
      k = 0;
      if (k >= paramArrayList.size())
        break label297;
      localMessageObject = (MessageObject)paramArrayList.get(k);
      if ((localObject2 != null) && ((j != 0) || (localMessageObject.getId() <= localObject2.getId())) && (((j == 0) && ((localMessageObject.getId() >= 0) || (localObject2.getId() >= 0))) || (localMessageObject.getId() >= localObject2.getId())))
      {
        m = n;
        localObject1 = localObject2;
        if (localMessageObject.messageOwner.date <= localObject2.messageOwner.date);
      }
      else
      {
        if (localMessageObject.messageOwner.to_id.channel_id == 0)
          break label1110;
        m = localMessageObject.messageOwner.to_id.channel_id;
        localObject1 = localMessageObject;
      }
    }
    while (true)
    {
      if ((localMessageObject.isOut()) && (!localMessageObject.isSending()) && (!localMessageObject.isForwarded()))
      {
        if (localMessageObject.isNewGif())
          StickersQuery.addRecentGif(localMessageObject.messageOwner.media.document, localMessageObject.messageOwner.date);
      }
      else
      {
        label221: if ((!localMessageObject.isOut()) || (!localMessageObject.isSent()))
          break label1107;
        i = 1;
      }
      label297: label1101: label1107: 
      while (true)
      {
        k += 1;
        n = m;
        localObject2 = localObject1;
        break label32;
        j = 0;
        break label20;
        if (!localMessageObject.isSticker())
          break label221;
        StickersQuery.addRecentSticker(0, localMessageObject.messageOwner.media.document, localMessageObject.messageOwner.date);
        break label221;
        MessagesQuery.loadReplyMessagesForMessages(paramArrayList, paramLong);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.didReceivedNewMessages, new Object[] { Long.valueOf(paramLong), paramArrayList });
        if (localObject2 == null)
          break;
        paramArrayList = (TLRPC.TL_dialog)this.dialogs_dict.get(Long.valueOf(paramLong));
        if ((localObject2.messageOwner.action instanceof TLRPC.TL_messageActionChatMigrateTo))
        {
          if (paramArrayList == null)
            break;
          this.dialogs.remove(paramArrayList);
          this.dialogsServerOnly.remove(paramArrayList);
          this.dialogsGroupsOnly.remove(paramArrayList);
          this.dialogsUserOnly.remove(paramArrayList);
          this.dialogsVidogramOnly.remove(paramArrayList);
          this.dialogsBotOnly.remove(paramArrayList);
          this.dialogsChannelOnly.remove(paramArrayList);
          this.dialogs_dict.remove(Long.valueOf(paramArrayList.id));
          this.dialogs_read_inbox_max.remove(Long.valueOf(paramArrayList.id));
          this.dialogs_read_outbox_max.remove(Long.valueOf(paramArrayList.id));
          this.nextDialogsCacheOffset -= 1;
          this.dialogMessage.remove(Long.valueOf(paramArrayList.id));
          localObject1 = (MessageObject)this.dialogMessagesByIds.remove(Integer.valueOf(paramArrayList.top_message));
          if ((localObject1 != null) && (((MessageObject)localObject1).messageOwner.random_id != 0L))
            this.dialogMessagesByRandomIds.remove(Long.valueOf(((MessageObject)localObject1).messageOwner.random_id));
          paramArrayList.top_message = 0;
          NotificationsController.getInstance().removeNotificationsForDialog(paramArrayList.id);
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[0]);
          return;
        }
        if (paramArrayList == null)
        {
          if (paramBoolean)
            break label1101;
          paramArrayList = getChat(Integer.valueOf(n));
          if (((n != 0) && (paramArrayList == null)) || ((paramArrayList != null) && (paramArrayList.left)))
            break;
          localObject1 = new TLRPC.TL_dialog();
          ((TLRPC.TL_dialog)localObject1).id = paramLong;
          ((TLRPC.TL_dialog)localObject1).unread_count = 0;
          ((TLRPC.TL_dialog)localObject1).top_message = localObject2.getId();
          ((TLRPC.TL_dialog)localObject1).last_message_date = localObject2.messageOwner.date;
          if (ChatObject.isChannel(paramArrayList))
          {
            j = 1;
            ((TLRPC.TL_dialog)localObject1).flags = j;
            this.dialogs_dict.put(Long.valueOf(paramLong), localObject1);
            this.dialogs.add(localObject1);
            this.dialogMessage.put(Long.valueOf(paramLong), localObject2);
            if (localObject2.messageOwner.to_id.channel_id == 0)
            {
              this.dialogMessagesByIds.put(Integer.valueOf(localObject2.getId()), localObject2);
              if (localObject2.messageOwner.random_id != 0L)
                this.dialogMessagesByRandomIds.put(Long.valueOf(localObject2.messageOwner.random_id), localObject2);
            }
            this.nextDialogsCacheOffset += 1;
            j = 1;
          }
        }
        while (true)
        {
          if (j != 0)
            sortDialogs(null);
          if (i == 0)
            break;
          SearchQuery.increasePeerRaiting(paramLong);
          return;
          j = 0;
          break label675;
          if (((paramArrayList.top_message > 0) && (localObject2.getId() > 0) && (localObject2.getId() > paramArrayList.top_message)) || ((paramArrayList.top_message < 0) && (localObject2.getId() < 0) && (localObject2.getId() < paramArrayList.top_message)) || (!this.dialogMessage.containsKey(Long.valueOf(paramLong))) || (paramArrayList.top_message < 0) || (paramArrayList.last_message_date <= localObject2.messageOwner.date))
          {
            localObject1 = (MessageObject)this.dialogMessagesByIds.remove(Integer.valueOf(paramArrayList.top_message));
            if ((localObject1 != null) && (((MessageObject)localObject1).messageOwner.random_id != 0L))
              this.dialogMessagesByRandomIds.remove(Long.valueOf(((MessageObject)localObject1).messageOwner.random_id));
            paramArrayList.top_message = localObject2.getId();
            if (!paramBoolean)
              paramArrayList.last_message_date = localObject2.messageOwner.date;
            for (k = 1; ; k = 0)
            {
              this.dialogMessage.put(Long.valueOf(paramLong), localObject2);
              j = k;
              if (localObject2.messageOwner.to_id.channel_id != 0)
                break;
              this.dialogMessagesByIds.put(Integer.valueOf(localObject2.getId()), localObject2);
              j = k;
              if (localObject2.messageOwner.random_id == 0L)
                break;
              this.dialogMessagesByRandomIds.put(Long.valueOf(localObject2.messageOwner.random_id), localObject2);
              j = k;
              break;
            }
          }
          j = 0;
        }
      }
      label675: label1110: localObject1 = localMessageObject;
      m = n;
    }
  }

  public void updateTimerProc()
  {
    long l = System.currentTimeMillis();
    checkDeletingTask(false);
    Object localObject1;
    label177: int i;
    int j;
    Object localObject2;
    if (UserConfig.isClientActivated())
    {
      if (!b.a(ApplicationLoader.applicationContext).b())
      {
        if ((ConnectionsManager.getInstance().getPauseTime() != 0L) || (!ApplicationLoader.isScreenOn) || (ApplicationLoader.mainInterfacePausedStageQueue))
          break label305;
        if ((ApplicationLoader.mainInterfacePausedStageQueueTime != 0L) && (Math.abs(ApplicationLoader.mainInterfacePausedStageQueueTime - System.currentTimeMillis()) > 1000L) && (this.statusSettingState != 1) && ((this.lastStatusUpdateTime == 0L) || (Math.abs(System.currentTimeMillis() - this.lastStatusUpdateTime) >= 55000L) || (this.offlineSent)))
        {
          this.statusSettingState = 1;
          if (this.statusRequest != 0)
            ConnectionsManager.getInstance().cancelRequest(this.statusRequest, true);
          localObject1 = new TLRPC.TL_account_updateStatus();
          ((TLRPC.TL_account_updateStatus)localObject1).offline = false;
        }
      }
      for (this.statusRequest = ConnectionsManager.getInstance().sendRequest((TLObject)localObject1, new RequestDelegate()
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          if (paramTL_error == null)
          {
            MessagesController.access$3702(MessagesController.this, System.currentTimeMillis());
            MessagesController.access$3802(MessagesController.this, false);
            MessagesController.access$3902(MessagesController.this, 0);
          }
          while (true)
          {
            MessagesController.access$4002(MessagesController.this, 0);
            return;
            if (MessagesController.this.lastStatusUpdateTime == 0L)
              continue;
            MessagesController.access$3702(MessagesController.this, MessagesController.this.lastStatusUpdateTime + 5000L);
          }
        }
      }); !this.updatesQueueChannels.isEmpty(); this.statusRequest = ConnectionsManager.getInstance().sendRequest((TLObject)localObject1, new RequestDelegate()
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          if (paramTL_error == null)
            MessagesController.access$3802(MessagesController.this, true);
          while (true)
          {
            MessagesController.access$4002(MessagesController.this, 0);
            return;
            if (MessagesController.this.lastStatusUpdateTime == 0L)
              continue;
            MessagesController.access$3702(MessagesController.this, MessagesController.this.lastStatusUpdateTime + 5000L);
          }
        }
      }))
      {
        localObject1 = new ArrayList(this.updatesQueueChannels.keySet());
        i = 0;
        while (i < ((ArrayList)localObject1).size())
        {
          j = ((Integer)((ArrayList)localObject1).get(i)).intValue();
          localObject2 = (Long)this.updatesStartWaitTimeChannels.get(Integer.valueOf(j));
          if ((localObject2 != null) && (((Long)localObject2).longValue() + 1500L < l))
          {
            FileLog.e("QUEUE CHANNEL " + j + " UPDATES WAIT TIMEOUT - CHECK QUEUE");
            processChannelsUpdatesQueue(j, 0);
          }
          i += 1;
        }
        label305: if ((this.statusSettingState == 2) || (this.offlineSent) || (Math.abs(System.currentTimeMillis() - ConnectionsManager.getInstance().getPauseTime()) < 2000L))
          break label177;
        this.statusSettingState = 2;
        if (this.statusRequest != 0)
          ConnectionsManager.getInstance().cancelRequest(this.statusRequest, true);
        localObject1 = new TLRPC.TL_account_updateStatus();
        ((TLRPC.TL_account_updateStatus)localObject1).offline = true;
      }
      i = 0;
      while (i < 3)
      {
        if ((getUpdatesStartTime(i) != 0L) && (getUpdatesStartTime(i) + 1500L < l))
        {
          FileLog.e(i + " QUEUE UPDATES WAIT TIMEOUT - CHECK QUEUE");
          processUpdatesQueue(i, 0);
        }
        i += 1;
      }
    }
    label531: label555: int k;
    if (((this.channelViewsToSend.size() != 0) || (this.channelViewsToReload.size() != 0)) && (Math.abs(System.currentTimeMillis() - this.lastViewsCheckTime) >= 5000L))
    {
      this.lastViewsCheckTime = System.currentTimeMillis();
      i = 0;
      if (i < 2)
      {
        if (i == 0)
        {
          localObject1 = this.channelViewsToSend;
          if (((SparseArray)localObject1).size() != 0)
            break label555;
        }
        while (true)
        {
          i += 1;
          break;
          localObject1 = this.channelViewsToReload;
          break label531;
          j = 0;
          if (j < ((SparseArray)localObject1).size())
          {
            k = ((SparseArray)localObject1).keyAt(j);
            localObject2 = new TLRPC.TL_messages_getMessagesViews();
            ((TLRPC.TL_messages_getMessagesViews)localObject2).peer = getInputPeer(k);
            ((TLRPC.TL_messages_getMessagesViews)localObject2).id = ((ArrayList)((SparseArray)localObject1).get(k));
            if (j == 0);
            for (boolean bool = true; ; bool = false)
            {
              ((TLRPC.TL_messages_getMessagesViews)localObject2).increment = bool;
              ConnectionsManager.getInstance().sendRequest((TLObject)localObject2, new RequestDelegate(k, (TLRPC.TL_messages_getMessagesViews)localObject2)
              {
                public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
                {
                  SparseArray localSparseArray;
                  if (paramTL_error == null)
                  {
                    paramTL_error = (TLRPC.Vector)paramTLObject;
                    localSparseArray = new SparseArray();
                    paramTLObject = (SparseIntArray)localSparseArray.get(this.val$key);
                    if (paramTLObject != null)
                      break label154;
                    paramTLObject = new SparseIntArray();
                    localSparseArray.put(this.val$key, paramTLObject);
                  }
                  label154: 
                  while (true)
                  {
                    int i = 0;
                    while (true)
                    {
                      if ((i >= this.val$req.id.size()) || (i >= paramTL_error.objects.size()))
                      {
                        MessagesStorage.getInstance().putChannelViews(localSparseArray, this.val$req.peer instanceof TLRPC.TL_inputPeerChannel);
                        AndroidUtilities.runOnUIThread(new Runnable(localSparseArray)
                        {
                          public void run()
                          {
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.didUpdatedMessagesViews, new Object[] { this.val$channelViews });
                          }
                        });
                        return;
                      }
                      paramTLObject.put(((Integer)this.val$req.id.get(i)).intValue(), ((Integer)paramTL_error.objects.get(i)).intValue());
                      i += 1;
                    }
                  }
                }
              });
              j += 1;
              break;
            }
          }
          ((SparseArray)localObject1).clear();
        }
      }
    }
    Object localObject3;
    if (!this.onlinePrivacy.isEmpty())
    {
      localObject1 = null;
      i = ConnectionsManager.getInstance().getCurrentTime();
      localObject2 = this.onlinePrivacy.entrySet().iterator();
      if (((Iterator)localObject2).hasNext())
      {
        localObject3 = (Map.Entry)((Iterator)localObject2).next();
        if (((Integer)((Map.Entry)localObject3).getValue()).intValue() >= i - 30)
          break label1133;
        if (localObject1 != null)
          break label1130;
        localObject1 = new ArrayList();
        label750: ((ArrayList)localObject1).add(((Map.Entry)localObject3).getKey());
      }
    }
    label1130: label1133: 
    while (true)
    {
      break;
      if (localObject1 != null)
      {
        localObject1 = ((ArrayList)localObject1).iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (Integer)((Iterator)localObject1).next();
          this.onlinePrivacy.remove(localObject2);
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(4) });
          }
        });
      }
      if (this.shortPollChannels.size() != 0)
      {
        i = 0;
        while (i < this.shortPollChannels.size())
        {
          j = this.shortPollChannels.keyAt(i);
          if (this.shortPollChannels.get(j) < System.currentTimeMillis() / 1000L)
          {
            this.shortPollChannels.delete(j);
            if (this.needShortPollChannels.indexOfKey(j) >= 0)
              getChannelDifference(j);
          }
          i += 1;
        }
      }
      if ((!this.printingUsers.isEmpty()) || (this.lastPrintingStringCount != this.printingUsers.size()))
      {
        localObject1 = new ArrayList(this.printingUsers.keySet());
        i = 0;
        j = 0;
        while (i < ((ArrayList)localObject1).size())
        {
          localObject2 = (Long)((ArrayList)localObject1).get(i);
          localObject3 = (ArrayList)this.printingUsers.get(localObject2);
          k = 0;
          if (k < ((ArrayList)localObject3).size())
          {
            PrintingUser localPrintingUser = (PrintingUser)((ArrayList)localObject3).get(k);
            if ((localPrintingUser.action instanceof TLRPC.TL_sendMessageGamePlayAction));
            for (int m = 30000; ; m = 5900)
            {
              int n = k;
              if (localPrintingUser.lastTime + m < l)
              {
                j = 1;
                ((ArrayList)localObject3).remove(localPrintingUser);
                n = k - 1;
              }
              k = n + 1;
              break;
            }
          }
          k = i;
          if (((ArrayList)localObject3).isEmpty())
          {
            this.printingUsers.remove(localObject2);
            ((ArrayList)localObject1).remove(i);
            k = i - 1;
          }
          i = k + 1;
        }
        updatePrintingStrings();
        if (j != 0)
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(64) });
            }
          });
      }
      return;
      break label750;
    }
  }

  public void updateTimerProcInSecretMode()
  {
    System.currentTimeMillis();
    checkDeletingTask(false);
    if (UserConfig.isClientActivated())
    {
      this.statusSettingState = 1;
      if (this.statusRequest != 0)
        ConnectionsManager.getInstance().cancelRequest(this.statusRequest, true);
      TLRPC.TL_account_updateStatus localTL_account_updateStatus = new TLRPC.TL_account_updateStatus();
      localTL_account_updateStatus.offline = false;
      this.statusRequest = ConnectionsManager.getInstance().sendRequest(localTL_account_updateStatus, new RequestDelegate()
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          if (paramTL_error == null)
          {
            MessagesController.access$3702(MessagesController.this, System.currentTimeMillis());
            MessagesController.access$3802(MessagesController.this, false);
            MessagesController.access$3902(MessagesController.this, 0);
            MessagesController.access$3902(MessagesController.this, 2);
            if (MessagesController.this.statusRequest != 0)
              ConnectionsManager.getInstance().cancelRequest(MessagesController.this.statusRequest, true);
            paramTLObject = new TLRPC.TL_account_updateStatus();
            paramTLObject.offline = true;
            MessagesController.access$4002(MessagesController.this, ConnectionsManager.getInstance().sendRequest(paramTLObject, new RequestDelegate()
            {
              public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
              {
                if (paramTL_error == null)
                  MessagesController.access$3802(MessagesController.this, true);
                while (true)
                {
                  MessagesController.access$4002(MessagesController.this, 0);
                  return;
                  if (MessagesController.this.lastStatusUpdateTime == 0L)
                    continue;
                  MessagesController.access$3702(MessagesController.this, MessagesController.this.lastStatusUpdateTime + 5000L);
                }
              }
            }));
          }
          while (true)
          {
            MessagesController.access$4002(MessagesController.this, 0);
            return;
            if (MessagesController.this.lastStatusUpdateTime == 0L)
              continue;
            MessagesController.access$3702(MessagesController.this, MessagesController.this.lastStatusUpdateTime + 5000L);
          }
        }
      });
    }
  }

  public void updateUnreadMessage(TLRPC.TL_dialog paramTL_dialog, int paramInt)
  {
    int i = paramTL_dialog.unread_count - paramInt;
    this.allUnread_count += i;
    if ((paramInt == 0) && (!isDialogMuted(paramTL_dialog.id)) && (i > 0))
      this.isAllMute += 1;
    label107: 
    do
    {
      while (true)
      {
        break label107;
        break label107;
        break label107;
        break label107;
        break label107;
        break label107;
        int j;
        int k;
        while (true)
        {
          j = (int)(paramTL_dialog.id >> 32);
          k = (int)paramTL_dialog.id;
          if (k == 0)
          {
            this.usersUnread_count += i;
            if ((paramInt == 0) && (!isDialogMuted(paramTL_dialog.id)) && (i > 0))
            {
              this.isUsersMute += 1;
              return;
              if ((paramTL_dialog.unread_count != 0) || (paramInt <= 0) || (isDialogMuted(paramTL_dialog.id)))
                continue;
              this.isAllMute -= 1;
              continue;
            }
            if ((paramTL_dialog.unread_count != 0) || (paramInt <= 0) || (isDialogMuted(paramTL_dialog.id)))
              break;
            this.isUsersMute -= 1;
            return;
          }
        }
        if ((k == 0) || (j == 1))
          continue;
        if (DialogObject.isChannel(paramTL_dialog))
        {
          TLRPC.Chat localChat = getChat(Integer.valueOf(-k));
          if ((localChat != null) && ((localChat.id < 0) || ((ChatObject.isChannel(localChat)) && (!localChat.megagroup))))
          {
            this.channelsUnread_count += i;
            if ((paramInt == 0) && (!isDialogMuted(paramTL_dialog.id)) && (i > 0))
            {
              this.isChannelsMute += 1;
              return;
            }
            if ((paramTL_dialog.unread_count != 0) || (paramInt <= 0) || (isDialogMuted(paramTL_dialog.id)))
              continue;
            this.isChannelsMute -= 1;
            return;
          }
          this.groupsUnread_count += i;
          if ((paramInt == 0) && (!isDialogMuted(paramTL_dialog.id)) && (i > 0))
          {
            this.isGroupsMute += 1;
            return;
          }
          if ((paramTL_dialog.unread_count != 0) || (paramInt <= 0) || (isDialogMuted(paramTL_dialog.id)))
            continue;
          this.isGroupsMute -= 1;
          return;
        }
        if (k < 0)
        {
          this.groupsUnread_count += i;
          if ((paramInt == 0) && (!isDialogMuted(paramTL_dialog.id)) && (i > 0))
          {
            this.isGroupsMute += 1;
            return;
          }
          if ((paramTL_dialog.unread_count != 0) || (paramInt <= 0) || (isDialogMuted(paramTL_dialog.id)))
            continue;
          this.isGroupsMute -= 1;
          return;
        }
        if (!getInstance().getUser(Integer.valueOf(k)).bot)
          break;
        this.botsUnread_count += i;
        if ((paramInt == 0) && (!isDialogMuted(paramTL_dialog.id)) && (i > 0))
        {
          this.isBotsMute += 1;
          return;
        }
        if ((paramTL_dialog.unread_count != 0) || (paramInt <= 0) || (isDialogMuted(paramTL_dialog.id)))
          continue;
        this.isBotsMute -= 1;
        return;
      }
      this.usersUnread_count += i;
      if ((paramInt != 0) || (isDialogMuted(paramTL_dialog.id)) || (i <= 0))
        continue;
      this.isUsersMute += 1;
      return;
    }
    while ((paramTL_dialog.unread_count != 0) || (paramInt <= 0) || (isDialogMuted(paramTL_dialog.id)));
    this.isUsersMute -= 1;
  }

  public void uploadAndApplyUserAvatar(TLRPC.PhotoSize paramPhotoSize)
  {
    if (paramPhotoSize != null)
    {
      this.uploadingAvatar = (FileLoader.getInstance().getDirectory(4) + "/" + paramPhotoSize.location.volume_id + "_" + paramPhotoSize.location.local_id + ".jpg");
      FileLoader.getInstance().uploadFile(this.uploadingAvatar, false, true, 16777216);
    }
  }

  public static class PrintingUser
  {
    public TLRPC.SendMessageAction action;
    public long lastTime;
    public int userId;
  }

  private class UserActionUpdatesPts extends TLRPC.Updates
  {
    private UserActionUpdatesPts()
    {
    }
  }

  private class UserActionUpdatesSeq extends TLRPC.Updates
  {
    private UserActionUpdatesSeq()
    {
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.MessagesController
 * JD-Core Version:    0.6.0
 */
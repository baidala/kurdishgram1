package org.vidogram.ui.ActionBar;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Build.VERSION;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.StateSet;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.DispatchQueue;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.Utilities;
import org.vidogram.ui.Components.CombinedDrawable;
import org.vidogram.ui.Components.ThemeEditorView;

public class Theme
{
  public static final int ACTION_BAR_AUDIO_SELECTOR_COLOR = 788529152;
  public static final int ACTION_BAR_MEDIA_PICKER_COLOR = -13421773;
  public static final int ACTION_BAR_PHOTO_VIEWER_COLOR = 2130706432;
  public static final int ACTION_BAR_PICKER_SELECTOR_COLOR = -12763843;
  public static final int ACTION_BAR_PLAYER_COLOR = -1;
  public static final int ACTION_BAR_VIDEO_EDIT_COLOR = -16777216;
  public static final int ACTION_BAR_WHITE_SELECTOR_COLOR = 1090519039;
  public static final int ARTICLE_VIEWER_MEDIA_PROGRESS_COLOR = -1;
  private static Field BitmapDrawable_mColorFilter;
  private static Method StateListDrawable_getStateDrawableMethod;
  public static Paint avatar_backgroundPaint;
  public static Drawable avatar_broadcastDrawable;
  public static Drawable avatar_photoDrawable;
  public static Paint chat_actionBackgroundPaint;
  public static TextPaint chat_actionTextPaint;
  public static Drawable[] chat_attachButtonDrawables;
  public static TextPaint chat_audioPerformerPaint;
  public static TextPaint chat_audioTimePaint;
  public static TextPaint chat_audioTitlePaint;
  public static TextPaint chat_botButtonPaint;
  public static Drawable chat_botInlineDrawable;
  public static Drawable chat_botLinkDrawalbe;
  public static Paint chat_botProgressPaint;
  public static Paint chat_composeBackgroundPaint;
  public static Drawable chat_composeShadowDrawable;
  public static Drawable[] chat_contactDrawable;
  public static TextPaint chat_contactNamePaint;
  public static TextPaint chat_contactPhonePaint;
  public static TextPaint chat_contextResult_descriptionTextPaint;
  public static Drawable chat_contextResult_shadowUnderSwitchDrawable;
  public static TextPaint chat_contextResult_titleTextPaint;
  public static Drawable[] chat_cornerInner;
  public static Drawable[] chat_cornerOuter;
  public static Paint chat_deleteProgressPaint;
  public static Paint chat_docBackPaint;
  public static TextPaint chat_docNamePaint;
  public static TextPaint chat_durationPaint;
  public static Drawable[][] chat_fileStatesDrawable;
  public static TextPaint chat_forwardNamePaint;
  public static TextPaint chat_gamePaint;
  public static TextPaint chat_infoPaint;
  public static Drawable chat_inlineResultAudio;
  public static Drawable chat_inlineResultFile;
  public static Drawable chat_inlineResultLocation;
  public static TextPaint chat_instantViewPaint;
  public static Paint chat_instantViewRectPaint;
  public static TextPaint chat_locationAddressPaint;
  public static Drawable[] chat_locationDrawable;
  public static TextPaint chat_locationTitlePaint;
  public static Drawable chat_lockIconDrawable;
  public static TextPaint chat_msgBotButtonPaint;
  public static Drawable chat_msgBroadcastDrawable;
  public static Drawable chat_msgBroadcastMediaDrawable;
  public static Drawable chat_msgCallDownGreenDrawable;
  public static Drawable chat_msgCallDownRedDrawable;
  public static Drawable chat_msgCallUpGreenDrawable;
  public static Drawable chat_msgCallUpRedDrawable;
  public static Drawable chat_msgErrorDrawable;
  public static Paint chat_msgErrorPaint;
  public static TextPaint chat_msgGameTextPaint;
  public static Drawable chat_msgInCallDrawable;
  public static Drawable chat_msgInCallSelectedDrawable;
  public static Drawable chat_msgInClockDrawable;
  public static Drawable chat_msgInDrawable;
  public static Drawable chat_msgInInstantDrawable;
  public static Drawable chat_msgInInstantSelectedDrawable;
  public static Drawable chat_msgInMediaDrawable;
  public static Drawable chat_msgInMediaSelectedDrawable;
  public static Drawable chat_msgInMediaShadowDrawable;
  public static Drawable chat_msgInMenuDrawable;
  public static Drawable chat_msgInMenuSelectedDrawable;
  public static Drawable chat_msgInSelectedClockDrawable;
  public static Drawable chat_msgInSelectedDrawable;
  public static Drawable chat_msgInShadowDrawable;
  public static Drawable chat_msgInViewsDrawable;
  public static Drawable chat_msgInViewsSelectedDrawable;
  public static Drawable chat_msgMediaBroadcastDrawable;
  public static Drawable chat_msgMediaCheckDrawable;
  public static Drawable chat_msgMediaClockDrawable;
  public static Drawable chat_msgMediaHalfCheckDrawable;
  public static Drawable chat_msgMediaMenuDrawable;
  public static Drawable chat_msgMediaViewsDrawable;
  public static Drawable chat_msgOutBroadcastDrawable;
  public static Drawable chat_msgOutCallDrawable;
  public static Drawable chat_msgOutCallSelectedDrawable;
  public static Drawable chat_msgOutCheckDrawable;
  public static Drawable chat_msgOutCheckSelectedDrawable;
  public static Drawable chat_msgOutClockDrawable;
  public static Drawable chat_msgOutDrawable;
  public static Drawable chat_msgOutHalfCheckDrawable;
  public static Drawable chat_msgOutHalfCheckSelectedDrawable;
  public static Drawable chat_msgOutInstantDrawable;
  public static Drawable chat_msgOutInstantSelectedDrawable;
  public static Drawable chat_msgOutLocationDrawable;
  public static Drawable chat_msgOutMediaDrawable;
  public static Drawable chat_msgOutMediaSelectedDrawable;
  public static Drawable chat_msgOutMediaShadowDrawable;
  public static Drawable chat_msgOutMenuDrawable;
  public static Drawable chat_msgOutMenuSelectedDrawable;
  public static Drawable chat_msgOutSelectedClockDrawable;
  public static Drawable chat_msgOutSelectedDrawable;
  public static Drawable chat_msgOutShadowDrawable;
  public static Drawable chat_msgOutViewsDrawable;
  public static Drawable chat_msgOutViewsSelectedDrawable;
  public static Drawable chat_msgStickerCheckDrawable;
  public static Drawable chat_msgStickerClockDrawable;
  public static Drawable chat_msgStickerHalfCheckDrawable;
  public static Drawable chat_msgStickerViewsDrawable;
  public static TextPaint chat_msgTextPaint;
  public static TextPaint chat_msgTextPaintOneEmoji;
  public static TextPaint chat_msgTextPaintThreeEmoji;
  public static TextPaint chat_msgTextPaintTwoEmoji;
  public static Drawable chat_muteIconDrawable;
  public static TextPaint chat_namePaint;
  public static Drawable[][] chat_photoStatesDrawables;
  public static Paint chat_replyLinePaint;
  public static TextPaint chat_replyNamePaint;
  public static TextPaint chat_replyTextPaint;
  public static Drawable chat_shareDrawable;
  public static Drawable chat_shareIconDrawable;
  public static TextPaint chat_shipmentPaint;
  public static Paint chat_statusPaint;
  public static Paint chat_statusRecordPaint;
  public static Drawable chat_systemDrawable;
  public static Paint chat_textSearchSelectionPaint;
  public static Drawable chat_timeBackgroundDrawable;
  public static TextPaint chat_timePaint;
  public static Drawable chat_timeStickerBackgroundDrawable;
  public static Paint chat_urlPaint;
  public static Paint checkboxSquare_backgroundPaint;
  public static Paint checkboxSquare_checkPaint;
  public static Paint checkboxSquare_eraserPaint;
  public static PorterDuffColorFilter colorFilter;
  public static PorterDuffColorFilter colorPressedFilter;
  private static int currentColor = 0;
  private static HashMap<String, Integer> currentColors;
  private static int currentSelectedColor = 0;
  private static ThemeInfo currentTheme;
  private static HashMap<String, Integer> defaultColors;
  private static ThemeInfo defaultTheme;
  public static Drawable dialogs_botDrawable;
  public static Drawable dialogs_broadcastDrawable;
  public static Drawable dialogs_checkDrawable;
  public static Drawable dialogs_clockDrawable;
  public static Paint dialogs_countGrayPaint;
  public static Paint dialogs_countPaint;
  public static TextPaint dialogs_countTextPaint;
  public static Drawable dialogs_errorDrawable;
  public static Drawable dialogs_errorIconDrawable;
  public static Paint dialogs_errorPaint;
  public static Drawable dialogs_groupDrawable;
  public static Drawable dialogs_halfCheckDrawable;
  public static Drawable dialogs_lockDrawable;
  public static TextPaint dialogs_messagePaint;
  public static TextPaint dialogs_messagePrintingPaint;
  public static Drawable dialogs_muteDrawable;
  public static TextPaint dialogs_nameEncryptedPaint;
  public static TextPaint dialogs_namePaint;
  public static TextPaint dialogs_offlinePaint;
  public static TextPaint dialogs_onlinePaint;
  public static Drawable dialogs_pinnedDrawable;
  public static Paint dialogs_pinnedPaint;
  public static Paint dialogs_tabletSeletedPaint;
  public static TextPaint dialogs_timePaint;
  public static Drawable dialogs_verifiedCheckDrawable;
  public static Drawable dialogs_verifiedDrawable;
  public static Paint dividerPaint;
  private static boolean isCustomTheme = false;
  public static final String key_actionBarActionModeDefault = "actionBarActionModeDefault";
  public static final String key_actionBarActionModeDefaultIcon = "actionBarActionModeDefaultIcon";
  public static final String key_actionBarActionModeDefaultSelector = "actionBarActionModeDefaultSelector";
  public static final String key_actionBarActionModeDefaultTop = "actionBarActionModeDefaultTop";
  public static final String key_actionBarDefault = "actionBarDefault";
  public static final String key_actionBarDefaultIcon = "actionBarDefaultIcon";
  public static final String key_actionBarDefaultSearch = "actionBarDefaultSearch";
  public static final String key_actionBarDefaultSearchPlaceholder = "actionBarDefaultSearchPlaceholder";
  public static final String key_actionBarDefaultSelector = "actionBarDefaultSelector";
  public static final String key_actionBarDefaultSubmenuBackground = "actionBarDefaultSubmenuBackground";
  public static final String key_actionBarDefaultSubmenuItem = "actionBarDefaultSubmenuItem";
  public static final String key_actionBarDefaultSubtitle = "actionBarDefaultSubtitle";
  public static final String key_actionBarDefaultTitle = "actionBarDefaultTitle";
  public static final String key_actionBarWhiteSelector = "actionBarWhiteSelector";
  public static final String key_avatar_actionBarIconBlue = "avatar_actionBarIconBlue";
  public static final String key_avatar_actionBarIconCyan = "avatar_actionBarIconCyan";
  public static final String key_avatar_actionBarIconGreen = "avatar_actionBarIconGreen";
  public static final String key_avatar_actionBarIconOrange = "avatar_actionBarIconOrange";
  public static final String key_avatar_actionBarIconPink = "avatar_actionBarIconPink";
  public static final String key_avatar_actionBarIconRed = "avatar_actionBarIconRed";
  public static final String key_avatar_actionBarIconViolet = "avatar_actionBarIconViolet";
  public static final String key_avatar_actionBarSelectorBlue = "avatar_actionBarSelectorBlue";
  public static final String key_avatar_actionBarSelectorCyan = "avatar_actionBarSelectorCyan";
  public static final String key_avatar_actionBarSelectorGreen = "avatar_actionBarSelectorGreen";
  public static final String key_avatar_actionBarSelectorOrange = "avatar_actionBarSelectorOrange";
  public static final String key_avatar_actionBarSelectorPink = "avatar_actionBarSelectorPink";
  public static final String key_avatar_actionBarSelectorRed = "avatar_actionBarSelectorRed";
  public static final String key_avatar_actionBarSelectorViolet = "avatar_actionBarSelectorViolet";
  public static final String key_avatar_backgroundActionBarBlue = "avatar_backgroundActionBarBlue";
  public static final String key_avatar_backgroundActionBarCyan = "avatar_backgroundActionBarCyan";
  public static final String key_avatar_backgroundActionBarGreen = "avatar_backgroundActionBarGreen";
  public static final String key_avatar_backgroundActionBarOrange = "avatar_backgroundActionBarOrange";
  public static final String key_avatar_backgroundActionBarPink = "avatar_backgroundActionBarPink";
  public static final String key_avatar_backgroundActionBarRed = "avatar_backgroundActionBarRed";
  public static final String key_avatar_backgroundActionBarViolet = "avatar_backgroundActionBarViolet";
  public static final String key_avatar_backgroundBlue = "avatar_backgroundBlue";
  public static final String key_avatar_backgroundCyan = "avatar_backgroundCyan";
  public static final String key_avatar_backgroundGreen = "avatar_backgroundGreen";
  public static final String key_avatar_backgroundGroupCreateSpanBlue = "avatar_backgroundGroupCreateSpanBlue";
  public static final String key_avatar_backgroundInProfileBlue = "avatar_backgroundInProfileBlue";
  public static final String key_avatar_backgroundInProfileCyan = "avatar_backgroundInProfileCyan";
  public static final String key_avatar_backgroundInProfileGreen = "avatar_backgroundInProfileGreen";
  public static final String key_avatar_backgroundInProfileOrange = "avatar_backgroundInProfileOrange";
  public static final String key_avatar_backgroundInProfilePink = "avatar_backgroundInProfilePink";
  public static final String key_avatar_backgroundInProfileRed = "avatar_backgroundInProfileRed";
  public static final String key_avatar_backgroundInProfileViolet = "avatar_backgroundInProfileViolet";
  public static final String key_avatar_backgroundOrange = "avatar_backgroundOrange";
  public static final String key_avatar_backgroundPink = "avatar_backgroundPink";
  public static final String key_avatar_backgroundRed = "avatar_backgroundRed";
  public static final String key_avatar_backgroundViolet = "avatar_backgroundViolet";
  public static final String key_avatar_nameInMessageBlue = "avatar_nameInMessageBlue";
  public static final String key_avatar_nameInMessageCyan = "avatar_nameInMessageCyan";
  public static final String key_avatar_nameInMessageGreen = "avatar_nameInMessageGreen";
  public static final String key_avatar_nameInMessageOrange = "avatar_nameInMessageOrange";
  public static final String key_avatar_nameInMessagePink = "avatar_nameInMessagePink";
  public static final String key_avatar_nameInMessageRed = "avatar_nameInMessageRed";
  public static final String key_avatar_nameInMessageViolet = "avatar_nameInMessageViolet";
  public static final String key_avatar_subtitleInProfileBlue = "avatar_subtitleInProfileBlue";
  public static final String key_avatar_subtitleInProfileCyan = "avatar_subtitleInProfileCyan";
  public static final String key_avatar_subtitleInProfileGreen = "avatar_subtitleInProfileGreen";
  public static final String key_avatar_subtitleInProfileOrange = "avatar_subtitleInProfileOrange";
  public static final String key_avatar_subtitleInProfilePink = "avatar_subtitleInProfilePink";
  public static final String key_avatar_subtitleInProfileRed = "avatar_subtitleInProfileRed";
  public static final String key_avatar_subtitleInProfileViolet = "avatar_subtitleInProfileViolet";
  public static final String key_avatar_text = "avatar_text";
  public static final String key_calls_callReceivedGreenIcon = "calls_callReceivedGreenIcon";
  public static final String key_calls_callReceivedRedIcon = "calls_callReceivedRedIcon";
  public static final String key_calls_ratingStar = "calls_ratingStar";
  public static final String key_calls_ratingStarSelected = "calls_ratingStarSelected";
  public static final String key_changephoneinfo_image = "changephoneinfo_image";
  public static final String key_chat_addContact = "chat_addContact";
  public static final String key_chat_botButtonText = "chat_botButtonText";
  public static final String key_chat_botKeyboardButtonBackground = "chat_botKeyboardButtonBackground";
  public static final String key_chat_botKeyboardButtonBackgroundPressed = "chat_botKeyboardButtonBackgroundPressed";
  public static final String key_chat_botKeyboardButtonText = "chat_botKeyboardButtonText";
  public static final String key_chat_botProgress = "chat_botProgress";
  public static final String key_chat_botSwitchToInlineText = "chat_botSwitchToInlineText";
  public static final String key_chat_editDoneIcon = "chat_editDoneIcon";
  public static final String key_chat_emojiPanelBackground = "chat_emojiPanelBackground";
  public static final String key_chat_emojiPanelBackspace = "chat_emojiPanelBackspace";
  public static final String key_chat_emojiPanelEmptyText = "chat_emojiPanelEmptyText";
  public static final String key_chat_emojiPanelIcon = "chat_emojiPanelIcon";
  public static final String key_chat_emojiPanelIconSelected = "chat_emojiPanelIconSelected";
  public static final String key_chat_emojiPanelIconSelector = "chat_emojiPanelIconSelector";
  public static final String key_chat_emojiPanelMasksIcon = "chat_emojiPanelMasksIcon";
  public static final String key_chat_emojiPanelMasksIconSelected = "chat_emojiPanelMasksIconSelected";
  public static final String key_chat_emojiPanelNewTrending = "chat_emojiPanelNewTrending";
  public static final String key_chat_emojiPanelShadowLine = "chat_emojiPanelShadowLine";
  public static final String key_chat_emojiPanelStickerPackSelector = "chat_emojiPanelStickerPackSelector";
  public static final String key_chat_emojiPanelTrendingDescription = "chat_emojiPanelTrendingDescription";
  public static final String key_chat_emojiPanelTrendingTitle = "chat_emojiPanelTrendingTitle";
  public static final String key_chat_fieldOverlayText = "chat_fieldOverlayText";
  public static final String key_chat_gifSaveHintBackground = "chat_gifSaveHintBackground";
  public static final String key_chat_gifSaveHintText = "chat_gifSaveHintText";
  public static final String key_chat_goDownButton = "chat_goDownButton";
  public static final String key_chat_goDownButtonCounter = "chat_goDownButtonCounter";
  public static final String key_chat_goDownButtonCounterBackground = "chat_goDownButtonCounterBackground";
  public static final String key_chat_goDownButtonIcon = "chat_goDownButtonIcon";
  public static final String key_chat_goDownButtonShadow = "chat_goDownButtonShadow";
  public static final String key_chat_inAudioDurationSelectedText = "chat_inAudioDurationSelectedText";
  public static final String key_chat_inAudioDurationText = "chat_inAudioDurationText";
  public static final String key_chat_inAudioPerfomerText = "chat_inAudioPerfomerText";
  public static final String key_chat_inAudioProgress = "chat_inAudioProgress";
  public static final String key_chat_inAudioSeekbar = "chat_inAudioSeekbar";
  public static final String key_chat_inAudioSeekbarFill = "chat_inAudioSeekbarFill";
  public static final String key_chat_inAudioSeekbarSelected = "chat_inAudioSeekbarSelected";
  public static final String key_chat_inAudioSelectedProgress = "chat_inAudioSelectedProgress";
  public static final String key_chat_inAudioTitleText = "chat_inAudioTitleText";
  public static final String key_chat_inBubble = "chat_inBubble";
  public static final String key_chat_inBubbleSelected = "chat_inBubbleSelected";
  public static final String key_chat_inBubbleShadow = "chat_inBubbleShadow";
  public static final String key_chat_inContactBackground = "chat_inContactBackground";
  public static final String key_chat_inContactIcon = "chat_inContactIcon";
  public static final String key_chat_inContactNameText = "chat_inContactNameText";
  public static final String key_chat_inContactPhoneText = "chat_inContactPhoneText";
  public static final String key_chat_inFileBackground = "chat_inFileBackground";
  public static final String key_chat_inFileBackgroundSelected = "chat_inFileBackgroundSelected";
  public static final String key_chat_inFileIcon = "chat_inFileIcon";
  public static final String key_chat_inFileInfoSelectedText = "chat_inFileInfoSelectedText";
  public static final String key_chat_inFileInfoText = "chat_inFileInfoText";
  public static final String key_chat_inFileNameText = "chat_inFileNameText";
  public static final String key_chat_inFileProgress = "chat_inFileProgress";
  public static final String key_chat_inFileProgressSelected = "chat_inFileProgressSelected";
  public static final String key_chat_inFileSelectedIcon = "chat_inFileSelectedIcon";
  public static final String key_chat_inForwardedNameText = "chat_inForwardedNameText";
  public static final String key_chat_inInstant = "chat_inInstant";
  public static final String key_chat_inInstantSelected = "chat_inInstantSelected";
  public static final String key_chat_inLoader = "chat_inLoader";
  public static final String key_chat_inLoaderPhoto = "chat_inLoaderPhoto";
  public static final String key_chat_inLoaderPhotoIcon = "chat_inLoaderPhotoIcon";
  public static final String key_chat_inLoaderPhotoIconSelected = "chat_inLoaderPhotoIconSelected";
  public static final String key_chat_inLoaderPhotoSelected = "chat_inLoaderPhotoSelected";
  public static final String key_chat_inLoaderSelected = "chat_inLoaderSelected";
  public static final String key_chat_inLocationBackground = "chat_inLocationBackground";
  public static final String key_chat_inLocationIcon = "chat_inLocationIcon";
  public static final String key_chat_inMenu = "chat_inMenu";
  public static final String key_chat_inMenuSelected = "chat_inMenuSelected";
  public static final String key_chat_inPreviewInstantSelectedText = "chat_inPreviewInstantSelectedText";
  public static final String key_chat_inPreviewInstantText = "chat_inPreviewInstantText";
  public static final String key_chat_inPreviewLine = "chat_inPreviewLine";
  public static final String key_chat_inReplyLine = "chat_inReplyLine";
  public static final String key_chat_inReplyMediaMessageSelectedText = "chat_inReplyMediaMessageSelectedText";
  public static final String key_chat_inReplyMediaMessageText = "chat_inReplyMediaMessageText";
  public static final String key_chat_inReplyMessageText = "chat_inReplyMessageText";
  public static final String key_chat_inReplyNameText = "chat_inReplyNameText";
  public static final String key_chat_inSentClock = "chat_inSentClock";
  public static final String key_chat_inSentClockSelected = "chat_inSentClockSelected";
  public static final String key_chat_inSiteNameText = "chat_inSiteNameText";
  public static final String key_chat_inTimeSelectedText = "chat_inTimeSelectedText";
  public static final String key_chat_inTimeText = "chat_inTimeText";
  public static final String key_chat_inVenueInfoSelectedText = "chat_inVenueInfoSelectedText";
  public static final String key_chat_inVenueInfoText = "chat_inVenueInfoText";
  public static final String key_chat_inVenueNameText = "chat_inVenueNameText";
  public static final String key_chat_inViaBotNameText = "chat_inViaBotNameText";
  public static final String key_chat_inViews = "chat_inViews";
  public static final String key_chat_inViewsSelected = "chat_inViewsSelected";
  public static final String key_chat_inVoiceSeekbar = "chat_inVoiceSeekbar";
  public static final String key_chat_inVoiceSeekbarFill = "chat_inVoiceSeekbarFill";
  public static final String key_chat_inVoiceSeekbarSelected = "chat_inVoiceSeekbarSelected";
  public static final String key_chat_inlineResultIcon = "chat_inlineResultIcon";
  public static final String key_chat_linkSelectBackground = "chat_linkSelectBackground";
  public static final String key_chat_lockIcon = "chat_lockIcon";
  public static final String key_chat_mediaBroadcast = "chat_mediaBroadcast";
  public static final String key_chat_mediaInfoText = "chat_mediaInfoText";
  public static final String key_chat_mediaLoaderPhoto = "chat_mediaLoaderPhoto";
  public static final String key_chat_mediaLoaderPhotoIcon = "chat_mediaLoaderPhotoIcon";
  public static final String key_chat_mediaLoaderPhotoIconSelected = "chat_mediaLoaderPhotoIconSelected";
  public static final String key_chat_mediaLoaderPhotoSelected = "chat_mediaLoaderPhotoSelected";
  public static final String key_chat_mediaMenu = "chat_mediaMenu";
  public static final String key_chat_mediaProgress = "chat_mediaProgress";
  public static final String key_chat_mediaSentCheck = "chat_mediaSentCheck";
  public static final String key_chat_mediaSentClock = "chat_mediaSentClock";
  public static final String key_chat_mediaTimeText = "chat_mediaTimeText";
  public static final String key_chat_mediaViews = "chat_mediaViews";
  public static final String key_chat_messageLinkIn = "chat_messageLinkIn";
  public static final String key_chat_messageLinkOut = "chat_messageLinkOut";
  public static final String key_chat_messagePanelBackground = "chat_messagePanelBackground";
  public static final String key_chat_messagePanelCancelInlineBot = "chat_messagePanelCancelInlineBot";
  public static final String key_chat_messagePanelHint = "chat_messagePanelHint";
  public static final String key_chat_messagePanelIcons = "chat_messagePanelIcons";
  public static final String key_chat_messagePanelSend = "chat_messagePanelSend";
  public static final String key_chat_messagePanelShadow = "chat_messagePanelShadow";
  public static final String key_chat_messagePanelText = "chat_messagePanelText";
  public static final String key_chat_messagePanelVoiceBackground = "chat_messagePanelVoiceBackground";
  public static final String key_chat_messagePanelVoiceDelete = "chat_messagePanelVoiceDelete";
  public static final String key_chat_messagePanelVoiceDuration = "chat_messagePanelVoiceDuration";
  public static final String key_chat_messagePanelVoicePressed = "chat_messagePanelVoicePressed";
  public static final String key_chat_messagePanelVoiceShadow = "chat_messagePanelVoiceShadow";
  public static final String key_chat_messageTextIn = "chat_messageTextIn";
  public static final String key_chat_messageTextOut = "chat_messageTextOut";
  public static final String key_chat_muteIcon = "chat_muteIcon";
  public static final String key_chat_outAudioDurationSelectedText = "chat_outAudioDurationSelectedText";
  public static final String key_chat_outAudioDurationText = "chat_outAudioDurationText";
  public static final String key_chat_outAudioPerfomerText = "chat_outAudioPerfomerText";
  public static final String key_chat_outAudioProgress = "chat_outAudioProgress";
  public static final String key_chat_outAudioSeekbar = "chat_outAudioSeekbar";
  public static final String key_chat_outAudioSeekbarFill = "chat_outAudioSeekbarFill";
  public static final String key_chat_outAudioSeekbarSelected = "chat_outAudioSeekbarSelected";
  public static final String key_chat_outAudioSelectedProgress = "chat_outAudioSelectedProgress";
  public static final String key_chat_outAudioTitleText = "chat_outAudioTitleText";
  public static final String key_chat_outBroadcast = "chat_outBroadcast";
  public static final String key_chat_outBubble = "chat_outBubble";
  public static final String key_chat_outBubbleSelected = "chat_outBubbleSelected";
  public static final String key_chat_outBubbleShadow = "chat_outBubbleShadow";
  public static final String key_chat_outContactBackground = "chat_outContactBackground";
  public static final String key_chat_outContactIcon = "chat_outContactIcon";
  public static final String key_chat_outContactNameText = "chat_outContactNameText";
  public static final String key_chat_outContactPhoneText = "chat_outContactPhoneText";
  public static final String key_chat_outFileBackground = "chat_outFileBackground";
  public static final String key_chat_outFileBackgroundSelected = "chat_outFileBackgroundSelected";
  public static final String key_chat_outFileIcon = "chat_outFileIcon";
  public static final String key_chat_outFileInfoSelectedText = "chat_outFileInfoSelectedText";
  public static final String key_chat_outFileInfoText = "chat_outFileInfoText";
  public static final String key_chat_outFileNameText = "chat_outFileNameText";
  public static final String key_chat_outFileProgress = "chat_outFileProgress";
  public static final String key_chat_outFileProgressSelected = "chat_outFileProgressSelected";
  public static final String key_chat_outFileSelectedIcon = "chat_outFileSelectedIcon";
  public static final String key_chat_outForwardedNameText = "chat_outForwardedNameText";
  public static final String key_chat_outInstant = "chat_outInstant";
  public static final String key_chat_outInstantSelected = "chat_outInstantSelected";
  public static final String key_chat_outLoader = "chat_outLoader";
  public static final String key_chat_outLoaderPhoto = "chat_outLoaderPhoto";
  public static final String key_chat_outLoaderPhotoIcon = "chat_outLoaderPhotoIcon";
  public static final String key_chat_outLoaderPhotoIconSelected = "chat_outLoaderPhotoIconSelected";
  public static final String key_chat_outLoaderPhotoSelected = "chat_outLoaderPhotoSelected";
  public static final String key_chat_outLoaderSelected = "chat_outLoaderSelected";
  public static final String key_chat_outLocationBackground = "chat_outLocationBackground";
  public static final String key_chat_outLocationIcon = "chat_outLocationIcon";
  public static final String key_chat_outMenu = "chat_outMenu";
  public static final String key_chat_outMenuSelected = "chat_outMenuSelected";
  public static final String key_chat_outPreviewInstantSelectedText = "chat_outPreviewInstantSelectedText";
  public static final String key_chat_outPreviewInstantText = "chat_outPreviewInstantText";
  public static final String key_chat_outPreviewLine = "chat_outPreviewLine";
  public static final String key_chat_outReplyLine = "chat_outReplyLine";
  public static final String key_chat_outReplyMediaMessageSelectedText = "chat_outReplyMediaMessageSelectedText";
  public static final String key_chat_outReplyMediaMessageText = "chat_outReplyMediaMessageText";
  public static final String key_chat_outReplyMessageText = "chat_outReplyMessageText";
  public static final String key_chat_outReplyNameText = "chat_outReplyNameText";
  public static final String key_chat_outSentCheck = "chat_outSentCheck";
  public static final String key_chat_outSentCheckSelected = "chat_outSentCheckSelected";
  public static final String key_chat_outSentClock = "chat_outSentClock";
  public static final String key_chat_outSentClockSelected = "chat_outSentClockSelected";
  public static final String key_chat_outSiteNameText = "chat_outSiteNameText";
  public static final String key_chat_outTimeSelectedText = "chat_outTimeSelectedText";
  public static final String key_chat_outTimeText = "chat_outTimeText";
  public static final String key_chat_outVenueInfoSelectedText = "chat_outVenueInfoSelectedText";
  public static final String key_chat_outVenueInfoText = "chat_outVenueInfoText";
  public static final String key_chat_outVenueNameText = "chat_outVenueNameText";
  public static final String key_chat_outViaBotNameText = "chat_outViaBotNameText";
  public static final String key_chat_outViews = "chat_outViews";
  public static final String key_chat_outViewsSelected = "chat_outViewsSelected";
  public static final String key_chat_outVoiceSeekbar = "chat_outVoiceSeekbar";
  public static final String key_chat_outVoiceSeekbarFill = "chat_outVoiceSeekbarFill";
  public static final String key_chat_outVoiceSeekbarSelected = "chat_outVoiceSeekbarSelected";
  public static final String key_chat_previewDurationText = "chat_previewDurationText";
  public static final String key_chat_previewGameText = "chat_previewGameText";
  public static final String key_chat_recordTime = "chat_recordTime";
  public static final String key_chat_recordVoiceCancel = "chat_recordVoiceCancel";
  public static final String key_chat_recordedVoiceBackground = "chat_recordedVoiceBackground";
  public static final String key_chat_recordedVoiceDot = "chat_recordedVoiceDot";
  public static final String key_chat_recordedVoicePlayPause = "chat_recordedVoicePlayPause";
  public static final String key_chat_recordedVoicePlayPausePressed = "chat_recordedVoicePlayPausePressed";
  public static final String key_chat_recordedVoiceProgress = "chat_recordedVoiceProgress";
  public static final String key_chat_recordedVoiceProgressInner = "chat_recordedVoiceProgressInner";
  public static final String key_chat_replyPanelClose = "chat_replyPanelClose";
  public static final String key_chat_replyPanelIcons = "chat_replyPanelIcons";
  public static final String key_chat_replyPanelLine = "chat_replyPanelLine";
  public static final String key_chat_replyPanelMessage = "chat_replyPanelMessage";
  public static final String key_chat_replyPanelName = "chat_replyPanelName";
  public static final String key_chat_reportSpam = "chat_reportSpam";
  public static final String key_chat_searchPanelIcons = "chat_searchPanelIcons";
  public static final String key_chat_searchPanelText = "chat_searchPanelText";
  public static final String key_chat_secretChatStatusText = "chat_secretChatStatusText";
  public static final String key_chat_secretTimeText = "chat_secretTimeText";
  public static final String key_chat_secretTimerBackground = "chat_secretTimerBackground";
  public static final String key_chat_secretTimerText = "chat_secretTimerText";
  public static final String key_chat_selectedBackground = "chat_selectedBackground";
  public static final String key_chat_sentError = "chat_sentError";
  public static final String key_chat_sentErrorIcon = "chat_sentErrorIcon";
  public static final String key_chat_serviceBackground = "chat_serviceBackground";
  public static final String key_chat_serviceBackgroundSelected = "chat_serviceBackgroundSelected";
  public static final String key_chat_serviceIcon = "chat_serviceIcon";
  public static final String key_chat_serviceLink = "chat_serviceLink";
  public static final String key_chat_serviceText = "chat_serviceText";
  public static final String key_chat_stickerNameText = "chat_stickerNameText";
  public static final String key_chat_stickerReplyLine = "chat_stickerReplyLine";
  public static final String key_chat_stickerReplyMessageText = "chat_stickerReplyMessageText";
  public static final String key_chat_stickerReplyNameText = "chat_stickerReplyNameText";
  public static final String key_chat_stickerViaBotNameText = "chat_stickerViaBotNameText";
  public static final String key_chat_stickersHintPanel = "chat_stickersHintPanel";
  public static final String key_chat_textSelectBackground = "chat_textSelectBackground";
  public static final String key_chat_topPanelBackground = "chat_topPanelBackground";
  public static final String key_chat_topPanelClose = "chat_topPanelClose";
  public static final String key_chat_topPanelLine = "chat_topPanelLine";
  public static final String key_chat_topPanelMessage = "chat_topPanelMessage";
  public static final String key_chat_topPanelTitle = "chat_topPanelTitle";
  public static final String key_chat_unreadMessagesStartArrowIcon = "chat_unreadMessagesStartArrowIcon";
  public static final String key_chat_unreadMessagesStartBackground = "chat_unreadMessagesStartBackground";
  public static final String key_chat_unreadMessagesStartText = "chat_unreadMessagesStartText";
  public static final String key_chat_wallpaper = "chat_wallpaper";
  public static final String key_chats_actionBackground = "chats_actionBackground";
  public static final String key_chats_actionIcon = "chats_actionIcon";
  public static final String key_chats_actionMessage = "chats_actionMessage";
  public static final String key_chats_actionPressedBackground = "chats_actionPressedBackground";
  public static final String key_chats_attachMessage = "chats_attachMessage";
  public static final String key_chats_date = "chats_date";
  public static final String key_chats_draft = "chats_draft";
  public static final String key_chats_menuBackground = "chats_menuBackground";
  public static final String key_chats_menuCloud = "chats_menuCloud";
  public static final String key_chats_menuCloudBackgroundCats = "chats_menuCloudBackgroundCats";
  public static final String key_chats_menuItemIcon = "chats_menuItemIcon";
  public static final String key_chats_menuItemText = "chats_menuItemText";
  public static final String key_chats_menuName = "chats_menuName";
  public static final String key_chats_menuPhone = "chats_menuPhone";
  public static final String key_chats_menuPhoneCats = "chats_menuPhoneCats";
  public static final String key_chats_menuTopShadow = "chats_menuTopShadow";
  public static final String key_chats_message = "chats_message";
  public static final String key_chats_muteIcon = "chats_muteIcon";
  public static final String key_chats_name = "chats_name";
  public static final String key_chats_nameIcon = "chats_nameIcon";
  public static final String key_chats_nameMessage = "chats_nameMessage";
  public static final String key_chats_pinnedIcon = "chats_pinnedIcon";
  public static final String key_chats_pinnedOverlay = "chats_pinnedOverlay";
  public static final String key_chats_secretIcon = "chats_secretIcon";
  public static final String key_chats_secretName = "chats_secretName";
  public static final String key_chats_sentCheck = "chats_sentCheck";
  public static final String key_chats_sentClock = "chats_sentClock";
  public static final String key_chats_sentError = "chats_sentError";
  public static final String key_chats_sentErrorIcon = "chats_sentErrorIcon";
  public static final String key_chats_tabletSelectedOverlay = "chats_tabletSelectedOverlay";
  public static final String key_chats_unreadCounter = "chats_unreadCounter";
  public static final String key_chats_unreadCounterMuted = "chats_unreadCounterMuted";
  public static final String key_chats_unreadCounterText = "chats_unreadCounterText";
  public static final String key_chats_verifiedBackground = "chats_verifiedBackground";
  public static final String key_chats_verifiedCheck = "chats_verifiedCheck";
  public static final String key_checkbox = "checkbox";
  public static final String key_checkboxCheck = "checkboxCheck";
  public static final String key_checkboxSquareBackground = "checkboxSquareBackground";
  public static final String key_checkboxSquareCheck = "checkboxSquareCheck";
  public static final String key_checkboxSquareDisabled = "checkboxSquareDisabled";
  public static final String key_checkboxSquareUnchecked = "checkboxSquareUnchecked";
  public static final String key_contextProgressInner1 = "contextProgressInner1";
  public static final String key_contextProgressInner2 = "contextProgressInner2";
  public static final String key_contextProgressInner3 = "contextProgressInner3";
  public static final String key_contextProgressOuter1 = "contextProgressOuter1";
  public static final String key_contextProgressOuter2 = "contextProgressOuter2";
  public static final String key_contextProgressOuter3 = "contextProgressOuter3";
  public static final String key_dialogBackground = "dialogBackground";
  public static final String key_dialogBadgeBackground = "dialogBadgeBackground";
  public static final String key_dialogBadgeText = "dialogBadgeText";
  public static final String key_dialogButton = "dialogButton";
  public static final String key_dialogButtonSelector = "dialogButtonSelector";
  public static final String key_dialogCheckboxSquareBackground = "dialogCheckboxSquareBackground";
  public static final String key_dialogCheckboxSquareCheck = "dialogCheckboxSquareCheck";
  public static final String key_dialogCheckboxSquareDisabled = "dialogCheckboxSquareDisabled";
  public static final String key_dialogCheckboxSquareUnchecked = "dialogCheckboxSquareUnchecked";
  public static final String key_dialogGrayLine = "dialogGrayLine";
  public static final String key_dialogIcon = "dialogIcon";
  public static final String key_dialogInputField = "dialogInputField";
  public static final String key_dialogInputFieldActivated = "dialogInputFieldActivated";
  public static final String key_dialogLineProgress = "dialogLineProgress";
  public static final String key_dialogLineProgressBackground = "dialogLineProgressBackground";
  public static final String key_dialogLinkSelection = "dialogLinkSelection";
  public static final String key_dialogProgressCircle = "dialogProgressCircle";
  public static final String key_dialogRadioBackground = "dialogRadioBackground";
  public static final String key_dialogRadioBackgroundChecked = "dialogRadioBackgroundChecked";
  public static final String key_dialogRoundCheckBox = "dialogRoundCheckBox";
  public static final String key_dialogRoundCheckBoxCheck = "dialogRoundCheckBoxCheck";
  public static final String key_dialogScrollGlow = "dialogScrollGlow";
  public static final String key_dialogTextBlack = "dialogTextBlack";
  public static final String key_dialogTextBlue = "dialogTextBlue";
  public static final String key_dialogTextBlue2 = "dialogTextBlue2";
  public static final String key_dialogTextBlue3 = "dialogTextBlue3";
  public static final String key_dialogTextBlue4 = "dialogTextBlue4";
  public static final String key_dialogTextGray = "dialogTextGray";
  public static final String key_dialogTextGray2 = "dialogTextGray2";
  public static final String key_dialogTextGray3 = "dialogTextGray3";
  public static final String key_dialogTextGray4 = "dialogTextGray4";
  public static final String key_dialogTextHint = "dialogTextHint";
  public static final String key_dialogTextLink = "dialogTextLink";
  public static final String key_dialogTextRed = "dialogTextRed";
  public static final String key_divider = "divider";
  public static final String key_emptyListPlaceholder = "emptyListPlaceholder";
  public static final String key_fastScrollActive = "fastScrollActive";
  public static final String key_fastScrollInactive = "fastScrollInactive";
  public static final String key_fastScrollText = "fastScrollText";
  public static final String key_featuredStickers_addButton = "featuredStickers_addButton";
  public static final String key_featuredStickers_addButtonPressed = "featuredStickers_addButtonPressed";
  public static final String key_featuredStickers_addedIcon = "featuredStickers_addedIcon";
  public static final String key_featuredStickers_buttonProgress = "featuredStickers_buttonProgress";
  public static final String key_featuredStickers_buttonText = "featuredStickers_buttonText";
  public static final String key_featuredStickers_delButton = "featuredStickers_delButton";
  public static final String key_featuredStickers_delButtonPressed = "featuredStickers_delButtonPressed";
  public static final String key_featuredStickers_unread = "featuredStickers_unread";
  public static final String key_files_folderIcon = "files_folderIcon";
  public static final String key_files_folderIconBackground = "files_folderIconBackground";
  public static final String key_files_iconText = "files_iconText";
  public static final String key_graySection = "graySection";
  public static final String key_groupcreate_checkbox = "groupcreate_checkbox";
  public static final String key_groupcreate_checkboxCheck = "groupcreate_checkboxCheck";
  public static final String key_groupcreate_cursor = "groupcreate_cursor";
  public static final String key_groupcreate_hintText = "groupcreate_hintText";
  public static final String key_groupcreate_offlineText = "groupcreate_offlineText";
  public static final String key_groupcreate_onlineText = "groupcreate_onlineText";
  public static final String key_groupcreate_sectionShadow = "groupcreate_sectionShadow";
  public static final String key_groupcreate_sectionText = "groupcreate_sectionText";
  public static final String key_groupcreate_spanBackground = "groupcreate_spanBackground";
  public static final String key_groupcreate_spanText = "groupcreate_spanText";
  public static final String key_inappPlayerBackground = "inappPlayerBackground";
  public static final String key_inappPlayerClose = "inappPlayerClose";
  public static final String key_inappPlayerPerformer = "inappPlayerPerformer";
  public static final String key_inappPlayerPlayPause = "inappPlayerPlayPause";
  public static final String key_inappPlayerTitle = "inappPlayerTitle";
  public static final String key_listSelector = "listSelectorSDK21";
  public static final String key_location_markerX = "location_markerX";
  public static final String key_location_sendLocationBackground = "location_sendLocationBackground";
  public static final String key_location_sendLocationIcon = "location_sendLocationIcon";
  public static final String key_login_progressInner = "login_progressInner";
  public static final String key_login_progressOuter = "login_progressOuter";
  public static final String key_musicPicker_buttonBackground = "musicPicker_buttonBackground";
  public static final String key_musicPicker_buttonIcon = "musicPicker_buttonIcon";
  public static final String key_musicPicker_checkbox = "musicPicker_checkbox";
  public static final String key_musicPicker_checkboxCheck = "musicPicker_checkboxCheck";
  public static final String key_picker_badge = "picker_badge";
  public static final String key_picker_badgeText = "picker_badgeText";
  public static final String key_picker_disabledButton = "picker_disabledButton";
  public static final String key_picker_enabledButton = "picker_enabledButton";
  public static final String key_player_actionBar = "player_actionBar";
  public static final String key_player_actionBarItems = "player_actionBarItems";
  public static final String key_player_actionBarSelector = "player_actionBarSelector";
  public static final String key_player_actionBarSubtitle = "player_actionBarSubtitle";
  public static final String key_player_actionBarTitle = "player_actionBarTitle";
  public static final String key_player_actionBarTop = "player_actionBarTop";
  public static final String key_player_button = "player_button";
  public static final String key_player_buttonActive = "player_buttonActive";
  public static final String key_player_duration = "player_duration";
  public static final String key_player_placeholder = "player_placeholder";
  public static final String key_player_progress = "player_progress";
  public static final String key_player_progressBackground = "player_progressBackground";
  public static final String key_player_seekBarBackground = "player_seekBarBackground";
  public static final String key_player_time = "player_time";
  public static final String key_profile_actionBackground = "profile_actionBackground";
  public static final String key_profile_actionIcon = "profile_actionIcon";
  public static final String key_profile_actionPressedBackground = "profile_actionPressedBackground";
  public static final String key_profile_adminIcon = "profile_adminIcon";
  public static final String key_profile_creatorIcon = "profile_creatorIcon";
  public static final String key_profile_title = "profile_title";
  public static final String key_profile_verifiedBackground = "profile_verifiedBackground";
  public static final String key_profile_verifiedCheck = "profile_verifiedCheck";
  public static final String key_progressCircle = "progressCircle";
  public static final String key_radioBackground = "radioBackground";
  public static final String key_radioBackgroundChecked = "radioBackgroundChecked";
  public static final String key_returnToCallBackground = "returnToCallBackground";
  public static final String key_returnToCallText = "returnToCallText";
  public static final String key_sessions_devicesImage = "sessions_devicesImage";
  public static final String key_sharedMedia_linkPlaceholder = "sharedMedia_linkPlaceholder";
  public static final String key_sharedMedia_linkPlaceholderText = "sharedMedia_linkPlaceholderText";
  public static final String key_sharedMedia_startStopLoadIcon = "sharedMedia_startStopLoadIcon";
  public static final String key_stickers_menu = "stickers_menu";
  public static final String key_stickers_menuSelector = "stickers_menuSelector";
  public static final String key_switchThumb = "switchThumb";
  public static final String key_switchThumbChecked = "switchThumbChecked";
  public static final String key_switchTrack = "switchTrack";
  public static final String key_switchTrackChecked = "switchTrackChecked";
  public static final String key_windowBackgroundGray = "windowBackgroundGray";
  public static final String key_windowBackgroundGrayShadow = "windowBackgroundGrayShadow";
  public static final String key_windowBackgroundWhite = "windowBackgroundWhite";
  public static final String key_windowBackgroundWhiteBlackText = "windowBackgroundWhiteBlackText";
  public static final String key_windowBackgroundWhiteBlueHeader = "windowBackgroundWhiteBlueHeader";
  public static final String key_windowBackgroundWhiteBlueText = "windowBackgroundWhiteBlueText";
  public static final String key_windowBackgroundWhiteBlueText2 = "windowBackgroundWhiteBlueText2";
  public static final String key_windowBackgroundWhiteBlueText3 = "windowBackgroundWhiteBlueText3";
  public static final String key_windowBackgroundWhiteBlueText4 = "windowBackgroundWhiteBlueText4";
  public static final String key_windowBackgroundWhiteBlueText5 = "windowBackgroundWhiteBlueText5";
  public static final String key_windowBackgroundWhiteBlueText6 = "windowBackgroundWhiteBlueText6";
  public static final String key_windowBackgroundWhiteBlueText7 = "windowBackgroundWhiteBlueText7";
  public static final String key_windowBackgroundWhiteGrayIcon = "windowBackgroundWhiteGrayIcon";
  public static final String key_windowBackgroundWhiteGrayLine = "windowBackgroundWhiteGrayLine";
  public static final String key_windowBackgroundWhiteGrayText = "windowBackgroundWhiteGrayText";
  public static final String key_windowBackgroundWhiteGrayText2 = "windowBackgroundWhiteGrayText2";
  public static final String key_windowBackgroundWhiteGrayText3 = "windowBackgroundWhiteGrayText3";
  public static final String key_windowBackgroundWhiteGrayText4 = "windowBackgroundWhiteGrayText4";
  public static final String key_windowBackgroundWhiteGrayText5 = "windowBackgroundWhiteGrayText5";
  public static final String key_windowBackgroundWhiteGrayText6 = "windowBackgroundWhiteGrayText6";
  public static final String key_windowBackgroundWhiteGrayText7 = "windowBackgroundWhiteGrayText7";
  public static final String key_windowBackgroundWhiteGrayText8 = "windowBackgroundWhiteGrayText8";
  public static final String key_windowBackgroundWhiteGreenText = "windowBackgroundWhiteGreenText";
  public static final String key_windowBackgroundWhiteGreenText2 = "windowBackgroundWhiteGreenText2";
  public static final String key_windowBackgroundWhiteHintText = "windowBackgroundWhiteHintText";
  public static final String key_windowBackgroundWhiteInputField = "windowBackgroundWhiteInputField";
  public static final String key_windowBackgroundWhiteInputFieldActivated = "windowBackgroundWhiteInputFieldActivated";
  public static final String key_windowBackgroundWhiteLinkSelection = "windowBackgroundWhiteLinkSelection";
  public static final String key_windowBackgroundWhiteLinkText = "windowBackgroundWhiteLinkText";
  public static final String key_windowBackgroundWhiteRedText = "windowBackgroundWhiteRedText";
  public static final String key_windowBackgroundWhiteRedText2 = "windowBackgroundWhiteRedText2";
  public static final String key_windowBackgroundWhiteRedText3 = "windowBackgroundWhiteRedText3";
  public static final String key_windowBackgroundWhiteRedText4 = "windowBackgroundWhiteRedText4";
  public static final String key_windowBackgroundWhiteRedText5 = "windowBackgroundWhiteRedText5";
  public static final String key_windowBackgroundWhiteRedText6 = "windowBackgroundWhiteRedText6";
  public static final String key_windowBackgroundWhiteValueText = "windowBackgroundWhiteValueText";
  public static String[] keys_avatar_actionBarIcon;
  public static String[] keys_avatar_actionBarSelector;
  public static String[] keys_avatar_background;
  public static String[] keys_avatar_backgroundActionBar;
  public static String[] keys_avatar_backgroundInProfile;
  public static String[] keys_avatar_nameInMessage;
  public static String[] keys_avatar_subtitleInProfile;
  public static Paint linkSelectionPaint;
  public static Drawable listSelector;
  private static Paint maskPaint;
  private static ArrayList<ThemeInfo> otherThemes;
  private static ThemeInfo previousTheme;
  public static TextPaint profile_aboutTextPaint;
  public static Drawable profile_verifiedCheckDrawable;
  public static Drawable profile_verifiedDrawable;
  private static int selectedColor;
  private static int serviceMessageColor;
  private static int serviceSelectedMessageColor;
  private static final Object sync = new Object();
  private static Drawable themedWallpaper;
  private static int themedWallpaperFileOffset;
  public static ArrayList<ThemeInfo> themes;
  private static HashMap<String, ThemeInfo> themesDict;
  private static Drawable wallpaper;
  private static final Object wallpaperSync = new Object();

  static
  {
    maskPaint = new Paint(1);
    chat_attachButtonDrawables = new Drawable[8];
    chat_locationDrawable = new Drawable[2];
    chat_contactDrawable = new Drawable[2];
    chat_cornerOuter = new Drawable[4];
    chat_cornerInner = new Drawable[4];
    chat_fileStatesDrawable = (Drawable[][])Array.newInstance(Drawable.class, new int[] { 10, 2 });
    chat_photoStatesDrawables = (Drawable[][])Array.newInstance(Drawable.class, new int[] { 13, 2 });
    keys_avatar_background = new String[] { "avatar_backgroundRed", "avatar_backgroundOrange", "avatar_backgroundViolet", "avatar_backgroundGreen", "avatar_backgroundCyan", "avatar_backgroundBlue", "avatar_backgroundPink" };
    keys_avatar_backgroundInProfile = new String[] { "avatar_backgroundInProfileRed", "avatar_backgroundInProfileOrange", "avatar_backgroundInProfileViolet", "avatar_backgroundInProfileGreen", "avatar_backgroundInProfileCyan", "avatar_backgroundInProfileBlue", "avatar_backgroundInProfilePink" };
    keys_avatar_backgroundActionBar = new String[] { "avatar_backgroundActionBarRed", "avatar_backgroundActionBarOrange", "avatar_backgroundActionBarViolet", "avatar_backgroundActionBarGreen", "avatar_backgroundActionBarCyan", "avatar_backgroundActionBarBlue", "avatar_backgroundActionBarPink" };
    keys_avatar_subtitleInProfile = new String[] { "avatar_subtitleInProfileRed", "avatar_subtitleInProfileOrange", "avatar_subtitleInProfileViolet", "avatar_subtitleInProfileGreen", "avatar_subtitleInProfileCyan", "avatar_subtitleInProfileBlue", "avatar_subtitleInProfilePink" };
    keys_avatar_nameInMessage = new String[] { "avatar_nameInMessageRed", "avatar_nameInMessageOrange", "avatar_nameInMessageViolet", "avatar_nameInMessageGreen", "avatar_nameInMessageCyan", "avatar_nameInMessageBlue", "avatar_nameInMessagePink" };
    keys_avatar_actionBarSelector = new String[] { "avatar_actionBarSelectorRed", "avatar_actionBarSelectorOrange", "avatar_actionBarSelectorViolet", "avatar_actionBarSelectorGreen", "avatar_actionBarSelectorCyan", "avatar_actionBarSelectorBlue", "avatar_actionBarSelectorPink" };
    keys_avatar_actionBarIcon = new String[] { "avatar_actionBarIconRed", "avatar_actionBarIconOrange", "avatar_actionBarIconViolet", "avatar_actionBarIconGreen", "avatar_actionBarIconCyan", "avatar_actionBarIconBlue", "avatar_actionBarIconPink" };
    defaultColors = new HashMap();
    defaultColors.put("dialogBackground", Integer.valueOf(-1));
    defaultColors.put("dialogTextBlack", Integer.valueOf(-14606047));
    defaultColors.put("dialogTextLink", Integer.valueOf(-14255946));
    defaultColors.put("dialogLinkSelection", Integer.valueOf(862104035));
    defaultColors.put("dialogTextRed", Integer.valueOf(-3319206));
    defaultColors.put("dialogTextBlue", Integer.valueOf(-13660983));
    defaultColors.put("dialogTextBlue2", Integer.valueOf(-12940081));
    defaultColors.put("dialogTextBlue3", Integer.valueOf(-12664327));
    defaultColors.put("dialogTextBlue4", Integer.valueOf(-15095832));
    defaultColors.put("dialogTextGray", Integer.valueOf(-13333567));
    defaultColors.put("dialogTextGray2", Integer.valueOf(-9079435));
    defaultColors.put("dialogTextGray3", Integer.valueOf(-6710887));
    defaultColors.put("dialogTextGray4", Integer.valueOf(-5000269));
    defaultColors.put("dialogTextHint", Integer.valueOf(-6842473));
    defaultColors.put("dialogIcon", Integer.valueOf(-7697782));
    defaultColors.put("dialogGrayLine", Integer.valueOf(-2960686));
    defaultColors.put("dialogInputField", Integer.valueOf(-2368549));
    defaultColors.put("dialogInputFieldActivated", Integer.valueOf(-13129232));
    defaultColors.put("dialogCheckboxSquareBackground", Integer.valueOf(-12345121));
    defaultColors.put("dialogCheckboxSquareCheck", Integer.valueOf(-1));
    defaultColors.put("dialogCheckboxSquareUnchecked", Integer.valueOf(-9211021));
    defaultColors.put("dialogCheckboxSquareDisabled", Integer.valueOf(-5197648));
    defaultColors.put("dialogRadioBackground", Integer.valueOf(-5000269));
    defaultColors.put("dialogRadioBackgroundChecked", Integer.valueOf(-13129232));
    defaultColors.put("dialogProgressCircle", Integer.valueOf(-11371101));
    defaultColors.put("dialogLineProgress", Integer.valueOf(-11371101));
    defaultColors.put("dialogLineProgressBackground", Integer.valueOf(-2368549));
    defaultColors.put("dialogButton", Integer.valueOf(-11955764));
    defaultColors.put("dialogButtonSelector", Integer.valueOf(251658240));
    defaultColors.put("dialogScrollGlow", Integer.valueOf(-657673));
    defaultColors.put("dialogRoundCheckBox", Integer.valueOf(-12664327));
    defaultColors.put("dialogRoundCheckBoxCheck", Integer.valueOf(-1));
    defaultColors.put("dialogBadgeBackground", Integer.valueOf(-12664327));
    defaultColors.put("dialogBadgeText", Integer.valueOf(-1));
    defaultColors.put("windowBackgroundWhite", Integer.valueOf(-1));
    defaultColors.put("progressCircle", Integer.valueOf(-11371101));
    defaultColors.put("windowBackgroundWhiteGrayIcon", Integer.valueOf(-9211021));
    defaultColors.put("windowBackgroundWhiteBlueText", Integer.valueOf(-12876608));
    defaultColors.put("windowBackgroundWhiteBlueText2", Integer.valueOf(-13333567));
    defaultColors.put("windowBackgroundWhiteBlueText3", Integer.valueOf(-14255946));
    defaultColors.put("windowBackgroundWhiteBlueText4", Integer.valueOf(-11697229));
    defaultColors.put("windowBackgroundWhiteBlueText5", Integer.valueOf(-11759926));
    defaultColors.put("windowBackgroundWhiteBlueText6", Integer.valueOf(-12940081));
    defaultColors.put("windowBackgroundWhiteBlueText7", Integer.valueOf(-13141330));
    defaultColors.put("windowBackgroundWhiteGreenText", Integer.valueOf(-14248148));
    defaultColors.put("windowBackgroundWhiteGreenText2", Integer.valueOf(-13129447));
    defaultColors.put("windowBackgroundWhiteRedText", Integer.valueOf(-3319206));
    defaultColors.put("windowBackgroundWhiteRedText2", Integer.valueOf(-2404015));
    defaultColors.put("windowBackgroundWhiteRedText3", Integer.valueOf(-2995895));
    defaultColors.put("windowBackgroundWhiteRedText4", Integer.valueOf(-3198928));
    defaultColors.put("windowBackgroundWhiteRedText5", Integer.valueOf(-1229511));
    defaultColors.put("windowBackgroundWhiteRedText6", Integer.valueOf(-39322));
    defaultColors.put("windowBackgroundWhiteGrayText", Integer.valueOf(-5723992));
    defaultColors.put("windowBackgroundWhiteGrayText2", Integer.valueOf(-7697782));
    defaultColors.put("windowBackgroundWhiteGrayText3", Integer.valueOf(-6710887));
    defaultColors.put("windowBackgroundWhiteGrayText4", Integer.valueOf(-8355712));
    defaultColors.put("windowBackgroundWhiteGrayText5", Integer.valueOf(-6052957));
    defaultColors.put("windowBackgroundWhiteGrayText6", Integer.valueOf(-9079435));
    defaultColors.put("windowBackgroundWhiteGrayText7", Integer.valueOf(-3750202));
    defaultColors.put("windowBackgroundWhiteGrayText8", Integer.valueOf(-9605774));
    defaultColors.put("windowBackgroundWhiteGrayLine", Integer.valueOf(-2368549));
    defaultColors.put("windowBackgroundWhiteBlackText", Integer.valueOf(-14606047));
    defaultColors.put("windowBackgroundWhiteHintText", Integer.valueOf(-6842473));
    defaultColors.put("windowBackgroundWhiteValueText", Integer.valueOf(-13660983));
    defaultColors.put("windowBackgroundWhiteLinkText", Integer.valueOf(-14255946));
    defaultColors.put("windowBackgroundWhiteLinkSelection", Integer.valueOf(862104035));
    defaultColors.put("windowBackgroundWhiteBlueHeader", Integer.valueOf(-12676913));
    defaultColors.put("windowBackgroundWhiteInputField", Integer.valueOf(-2368549));
    defaultColors.put("windowBackgroundWhiteInputFieldActivated", Integer.valueOf(-13129232));
    defaultColors.put("switchThumb", Integer.valueOf(-1184275));
    defaultColors.put("switchTrack", Integer.valueOf(-3684409));
    defaultColors.put("switchThumbChecked", Integer.valueOf(-12211217));
    defaultColors.put("switchTrackChecked", Integer.valueOf(-6236422));
    defaultColors.put("checkboxSquareBackground", Integer.valueOf(-12345121));
    defaultColors.put("checkboxSquareCheck", Integer.valueOf(-1));
    defaultColors.put("checkboxSquareUnchecked", Integer.valueOf(-9211021));
    defaultColors.put("checkboxSquareDisabled", Integer.valueOf(-5197648));
    defaultColors.put("listSelectorSDK21", Integer.valueOf(251658240));
    defaultColors.put("radioBackground", Integer.valueOf(-5000269));
    defaultColors.put("radioBackgroundChecked", Integer.valueOf(-13129232));
    defaultColors.put("windowBackgroundGray", Integer.valueOf(-986896));
    defaultColors.put("windowBackgroundGrayShadow", Integer.valueOf(-16777216));
    defaultColors.put("emptyListPlaceholder", Integer.valueOf(-6974059));
    defaultColors.put("divider", Integer.valueOf(-2500135));
    defaultColors.put("graySection", Integer.valueOf(-855310));
    defaultColors.put("contextProgressInner1", Integer.valueOf(-4202506));
    defaultColors.put("contextProgressOuter1", Integer.valueOf(-13920542));
    defaultColors.put("contextProgressInner2", Integer.valueOf(-4202506));
    defaultColors.put("contextProgressOuter2", Integer.valueOf(-1));
    defaultColors.put("contextProgressInner3", Integer.valueOf(-5000269));
    defaultColors.put("contextProgressOuter3", Integer.valueOf(-1));
    defaultColors.put("fastScrollActive", Integer.valueOf(-11361317));
    defaultColors.put("fastScrollInactive", Integer.valueOf(-10263709));
    defaultColors.put("fastScrollText", Integer.valueOf(-1));
    defaultColors.put("avatar_text", Integer.valueOf(-1));
    defaultColors.put("avatar_backgroundRed", Integer.valueOf(-1743531));
    defaultColors.put("avatar_backgroundOrange", Integer.valueOf(-881592));
    defaultColors.put("avatar_backgroundViolet", Integer.valueOf(-7436818));
    defaultColors.put("avatar_backgroundGreen", Integer.valueOf(-8992691));
    defaultColors.put("avatar_backgroundCyan", Integer.valueOf(-10502443));
    defaultColors.put("avatar_backgroundBlue", Integer.valueOf(-11232035));
    defaultColors.put("avatar_backgroundPink", Integer.valueOf(-887654));
    defaultColors.put("avatar_backgroundGroupCreateSpanBlue", Integer.valueOf(-4204822));
    defaultColors.put("avatar_backgroundInProfileRed", Integer.valueOf(-2592923));
    defaultColors.put("avatar_backgroundInProfileOrange", Integer.valueOf(-615071));
    defaultColors.put("avatar_backgroundInProfileViolet", Integer.valueOf(-7570990));
    defaultColors.put("avatar_backgroundInProfileGreen", Integer.valueOf(-9981091));
    defaultColors.put("avatar_backgroundInProfileCyan", Integer.valueOf(-11099461));
    defaultColors.put("avatar_backgroundInProfileBlue", Integer.valueOf(-11500111));
    defaultColors.put("avatar_backgroundInProfilePink", Integer.valueOf(-819290));
    defaultColors.put("avatar_backgroundActionBarRed", Integer.valueOf(-3514282));
    defaultColors.put("avatar_backgroundActionBarOrange", Integer.valueOf(-947900));
    defaultColors.put("avatar_backgroundActionBarViolet", Integer.valueOf(-8557884));
    defaultColors.put("avatar_backgroundActionBarGreen", Integer.valueOf(-11099828));
    defaultColors.put("avatar_backgroundActionBarCyan", Integer.valueOf(-12283220));
    defaultColors.put("avatar_backgroundActionBarBlue", Integer.valueOf(-10907718));
    defaultColors.put("avatar_backgroundActionBarPink", Integer.valueOf(-11762506));
    defaultColors.put("avatar_subtitleInProfileRed", Integer.valueOf(-406587));
    defaultColors.put("avatar_subtitleInProfileOrange", Integer.valueOf(-139832));
    defaultColors.put("avatar_subtitleInProfileViolet", Integer.valueOf(-3291923));
    defaultColors.put("avatar_subtitleInProfileGreen", Integer.valueOf(-4133446));
    defaultColors.put("avatar_subtitleInProfileCyan", Integer.valueOf(-4660496));
    defaultColors.put("avatar_subtitleInProfileBlue", Integer.valueOf(-2626822));
    defaultColors.put("avatar_subtitleInProfilePink", Integer.valueOf(-4990985));
    defaultColors.put("avatar_nameInMessageRed", Integer.valueOf(-3516848));
    defaultColors.put("avatar_nameInMessageOrange", Integer.valueOf(-2589911));
    defaultColors.put("avatar_nameInMessageViolet", Integer.valueOf(-11627828));
    defaultColors.put("avatar_nameInMessageGreen", Integer.valueOf(-11488718));
    defaultColors.put("avatar_nameInMessageCyan", Integer.valueOf(-12406360));
    defaultColors.put("avatar_nameInMessageBlue", Integer.valueOf(-11627828));
    defaultColors.put("avatar_nameInMessagePink", Integer.valueOf(-11627828));
    defaultColors.put("avatar_actionBarSelectorRed", Integer.valueOf(-4437183));
    defaultColors.put("avatar_actionBarSelectorOrange", Integer.valueOf(-1674199));
    defaultColors.put("avatar_actionBarSelectorViolet", Integer.valueOf(-9216066));
    defaultColors.put("avatar_actionBarSelectorGreen", Integer.valueOf(-12020419));
    defaultColors.put("avatar_actionBarSelectorCyan", Integer.valueOf(-13007715));
    defaultColors.put("avatar_actionBarSelectorBlue", Integer.valueOf(-11959891));
    defaultColors.put("avatar_actionBarSelectorPink", Integer.valueOf(-2863493));
    defaultColors.put("avatar_actionBarIconRed", Integer.valueOf(-1));
    defaultColors.put("avatar_actionBarIconOrange", Integer.valueOf(-1));
    defaultColors.put("avatar_actionBarIconViolet", Integer.valueOf(-1));
    defaultColors.put("avatar_actionBarIconGreen", Integer.valueOf(-1));
    defaultColors.put("avatar_actionBarIconCyan", Integer.valueOf(-1));
    defaultColors.put("avatar_actionBarIconBlue", Integer.valueOf(-1));
    defaultColors.put("avatar_actionBarIconPink", Integer.valueOf(-1));
    defaultColors.put("actionBarDefault", Integer.valueOf(-11371101));
    defaultColors.put("actionBarDefaultIcon", Integer.valueOf(-1));
    defaultColors.put("actionBarActionModeDefault", Integer.valueOf(-1));
    defaultColors.put("actionBarActionModeDefaultTop", Integer.valueOf(-1728053248));
    defaultColors.put("actionBarActionModeDefaultIcon", Integer.valueOf(-9211021));
    defaultColors.put("actionBarDefaultTitle", Integer.valueOf(-1));
    defaultColors.put("actionBarDefaultSubtitle", Integer.valueOf(-2758409));
    defaultColors.put("actionBarDefaultSelector", Integer.valueOf(-12554860));
    defaultColors.put("actionBarWhiteSelector", Integer.valueOf(788529152));
    defaultColors.put("actionBarDefaultSearch", Integer.valueOf(-1));
    defaultColors.put("actionBarDefaultSearchPlaceholder", Integer.valueOf(-1996488705));
    defaultColors.put("actionBarDefaultSubmenuItem", Integer.valueOf(-14606047));
    defaultColors.put("actionBarDefaultSubmenuBackground", Integer.valueOf(-1));
    defaultColors.put("actionBarActionModeDefaultSelector", Integer.valueOf(-986896));
    defaultColors.put("chats_unreadCounter", Integer.valueOf(-11613090));
    defaultColors.put("chats_unreadCounterMuted", Integer.valueOf(-3684409));
    defaultColors.put("chats_unreadCounterText", Integer.valueOf(-1));
    defaultColors.put("chats_name", Integer.valueOf(-14606047));
    defaultColors.put("chats_secretName", Integer.valueOf(-16734706));
    defaultColors.put("chats_secretIcon", Integer.valueOf(-15093466));
    defaultColors.put("chats_nameIcon", Integer.valueOf(-14408668));
    defaultColors.put("chats_pinnedIcon", Integer.valueOf(-5723992));
    defaultColors.put("chats_message", Integer.valueOf(-7368817));
    defaultColors.put("chats_draft", Integer.valueOf(-2274503));
    defaultColors.put("chats_nameMessage", Integer.valueOf(-11697229));
    defaultColors.put("chats_attachMessage", Integer.valueOf(-11697229));
    defaultColors.put("chats_actionMessage", Integer.valueOf(-11697229));
    defaultColors.put("chats_date", Integer.valueOf(-6710887));
    defaultColors.put("chats_pinnedOverlay", Integer.valueOf(134217728));
    defaultColors.put("chats_tabletSelectedOverlay", Integer.valueOf(251658240));
    defaultColors.put("chats_sentCheck", Integer.valueOf(-12146122));
    defaultColors.put("chats_sentClock", Integer.valueOf(-9061026));
    defaultColors.put("chats_sentError", Integer.valueOf(-2796974));
    defaultColors.put("chats_sentErrorIcon", Integer.valueOf(-1));
    defaultColors.put("chats_verifiedBackground", Integer.valueOf(-13391642));
    defaultColors.put("chats_verifiedCheck", Integer.valueOf(-1));
    defaultColors.put("chats_muteIcon", Integer.valueOf(-5723992));
    defaultColors.put("chats_menuBackground", Integer.valueOf(-1));
    defaultColors.put("chats_menuItemText", Integer.valueOf(-12303292));
    defaultColors.put("chats_menuItemIcon", Integer.valueOf(-9211021));
    defaultColors.put("chats_menuName", Integer.valueOf(-1));
    defaultColors.put("chats_menuPhone", Integer.valueOf(-1));
    defaultColors.put("chats_menuPhoneCats", Integer.valueOf(-4004353));
    defaultColors.put("chats_menuCloud", Integer.valueOf(-1));
    defaultColors.put("chats_menuCloudBackgroundCats", Integer.valueOf(-12420183));
    defaultColors.put("chats_actionIcon", Integer.valueOf(-1));
    defaultColors.put("chats_actionBackground", Integer.valueOf(-9788978));
    defaultColors.put("chats_actionPressedBackground", Integer.valueOf(-11038014));
    defaultColors.put("chat_lockIcon", Integer.valueOf(-1));
    defaultColors.put("chat_muteIcon", Integer.valueOf(-5124893));
    defaultColors.put("chat_inBubble", Integer.valueOf(-1));
    defaultColors.put("chat_inBubbleSelected", Integer.valueOf(-1902337));
    defaultColors.put("chat_inBubbleShadow", Integer.valueOf(-14862509));
    defaultColors.put("chat_outBubble", Integer.valueOf(-1048610));
    defaultColors.put("chat_outBubbleSelected", Integer.valueOf(-2820676));
    defaultColors.put("chat_outBubbleShadow", Integer.valueOf(-14781172));
    defaultColors.put("chat_messageTextIn", Integer.valueOf(-16777216));
    defaultColors.put("chat_messageTextOut", Integer.valueOf(-16777216));
    defaultColors.put("chat_messageLinkIn", Integer.valueOf(-14255946));
    defaultColors.put("chat_messageLinkOut", Integer.valueOf(-14255946));
    defaultColors.put("chat_serviceText", Integer.valueOf(-1));
    defaultColors.put("chat_serviceLink", Integer.valueOf(-1));
    defaultColors.put("chat_serviceIcon", Integer.valueOf(-1));
    defaultColors.put("chat_outSentCheck", Integer.valueOf(-10637232));
    defaultColors.put("chat_outSentCheckSelected", Integer.valueOf(-10637232));
    defaultColors.put("chat_outSentClock", Integer.valueOf(-9061026));
    defaultColors.put("chat_outSentClockSelected", Integer.valueOf(-9061026));
    defaultColors.put("chat_inSentClock", Integer.valueOf(-6182221));
    defaultColors.put("chat_inSentClockSelected", Integer.valueOf(-7094838));
    defaultColors.put("chat_mediaSentCheck", Integer.valueOf(-1));
    defaultColors.put("chat_mediaSentClock", Integer.valueOf(-1));
    defaultColors.put("chat_inViews", Integer.valueOf(-6182221));
    defaultColors.put("chat_inViewsSelected", Integer.valueOf(-7094838));
    defaultColors.put("chat_outViews", Integer.valueOf(-9522601));
    defaultColors.put("chat_outViewsSelected", Integer.valueOf(-9522601));
    defaultColors.put("chat_mediaViews", Integer.valueOf(-1));
    defaultColors.put("chat_inMenu", Integer.valueOf(-4801083));
    defaultColors.put("chat_inMenuSelected", Integer.valueOf(-6766130));
    defaultColors.put("chat_outMenu", Integer.valueOf(-7221634));
    defaultColors.put("chat_outMenuSelected", Integer.valueOf(-7221634));
    defaultColors.put("chat_mediaMenu", Integer.valueOf(-1));
    defaultColors.put("chat_outInstant", Integer.valueOf(-11162801));
    defaultColors.put("chat_outInstantSelected", Integer.valueOf(-12019389));
    defaultColors.put("chat_inInstant", Integer.valueOf(-12940081));
    defaultColors.put("chat_inInstantSelected", Integer.valueOf(-13600331));
    defaultColors.put("chat_sentError", Integer.valueOf(-2411211));
    defaultColors.put("chat_sentErrorIcon", Integer.valueOf(-1));
    defaultColors.put("chat_selectedBackground", Integer.valueOf(1714664933));
    defaultColors.put("chat_previewDurationText", Integer.valueOf(-1));
    defaultColors.put("chat_previewGameText", Integer.valueOf(-1));
    defaultColors.put("chat_inPreviewInstantText", Integer.valueOf(-12940081));
    defaultColors.put("chat_outPreviewInstantText", Integer.valueOf(-11162801));
    defaultColors.put("chat_inPreviewInstantSelectedText", Integer.valueOf(-13600331));
    defaultColors.put("chat_outPreviewInstantSelectedText", Integer.valueOf(-12019389));
    defaultColors.put("chat_secretTimeText", Integer.valueOf(-1776928));
    defaultColors.put("chat_stickerNameText", Integer.valueOf(-1));
    defaultColors.put("chat_botButtonText", Integer.valueOf(-1));
    defaultColors.put("chat_botProgress", Integer.valueOf(-1));
    defaultColors.put("chat_inForwardedNameText", Integer.valueOf(-13072697));
    defaultColors.put("chat_outForwardedNameText", Integer.valueOf(-11162801));
    defaultColors.put("chat_inViaBotNameText", Integer.valueOf(-12940081));
    defaultColors.put("chat_outViaBotNameText", Integer.valueOf(-11162801));
    defaultColors.put("chat_stickerViaBotNameText", Integer.valueOf(-1));
    defaultColors.put("chat_inReplyLine", Integer.valueOf(-9390872));
    defaultColors.put("chat_outReplyLine", Integer.valueOf(-7812741));
    defaultColors.put("chat_stickerReplyLine", Integer.valueOf(-1));
    defaultColors.put("chat_inReplyNameText", Integer.valueOf(-12940081));
    defaultColors.put("chat_outReplyNameText", Integer.valueOf(-11162801));
    defaultColors.put("chat_stickerReplyNameText", Integer.valueOf(-1));
    defaultColors.put("chat_inReplyMessageText", Integer.valueOf(-16777216));
    defaultColors.put("chat_outReplyMessageText", Integer.valueOf(-16777216));
    defaultColors.put("chat_inReplyMediaMessageText", Integer.valueOf(-6182221));
    defaultColors.put("chat_outReplyMediaMessageText", Integer.valueOf(-10112933));
    defaultColors.put("chat_inReplyMediaMessageSelectedText", Integer.valueOf(-7752511));
    defaultColors.put("chat_outReplyMediaMessageSelectedText", Integer.valueOf(-10112933));
    defaultColors.put("chat_stickerReplyMessageText", Integer.valueOf(-1));
    defaultColors.put("chat_inPreviewLine", Integer.valueOf(-9390872));
    defaultColors.put("chat_outPreviewLine", Integer.valueOf(-7812741));
    defaultColors.put("chat_inSiteNameText", Integer.valueOf(-12940081));
    defaultColors.put("chat_outSiteNameText", Integer.valueOf(-11162801));
    defaultColors.put("chat_inContactNameText", Integer.valueOf(-11625772));
    defaultColors.put("chat_outContactNameText", Integer.valueOf(-11162801));
    defaultColors.put("chat_inContactPhoneText", Integer.valueOf(-13683656));
    defaultColors.put("chat_outContactPhoneText", Integer.valueOf(-13286860));
    defaultColors.put("chat_mediaProgress", Integer.valueOf(-1));
    defaultColors.put("chat_inAudioProgress", Integer.valueOf(-1));
    defaultColors.put("chat_outAudioProgress", Integer.valueOf(-1048610));
    defaultColors.put("chat_inAudioSelectedProgress", Integer.valueOf(-1902337));
    defaultColors.put("chat_outAudioSelectedProgress", Integer.valueOf(-2820676));
    defaultColors.put("chat_mediaTimeText", Integer.valueOf(-1));
    defaultColors.put("chat_inTimeText", Integer.valueOf(-6182221));
    defaultColors.put("chat_outTimeText", Integer.valueOf(-9391780));
    defaultColors.put("chat_inTimeSelectedText", Integer.valueOf(-7752511));
    defaultColors.put("chat_outTimeSelectedText", Integer.valueOf(-9391780));
    defaultColors.put("chat_inAudioPerfomerText", Integer.valueOf(-13683656));
    defaultColors.put("chat_outAudioPerfomerText", Integer.valueOf(-13286860));
    defaultColors.put("chat_inAudioTitleText", Integer.valueOf(-11625772));
    defaultColors.put("chat_outAudioTitleText", Integer.valueOf(-11162801));
    defaultColors.put("chat_inAudioDurationText", Integer.valueOf(-6182221));
    defaultColors.put("chat_outAudioDurationText", Integer.valueOf(-10112933));
    defaultColors.put("chat_inAudioDurationSelectedText", Integer.valueOf(-7752511));
    defaultColors.put("chat_outAudioDurationSelectedText", Integer.valueOf(-10112933));
    defaultColors.put("chat_inAudioSeekbar", Integer.valueOf(-1774864));
    defaultColors.put("chat_outAudioSeekbar", Integer.valueOf(-4463700));
    defaultColors.put("chat_inAudioSeekbarSelected", Integer.valueOf(-4399384));
    defaultColors.put("chat_outAudioSeekbarSelected", Integer.valueOf(-5644906));
    defaultColors.put("chat_inAudioSeekbarFill", Integer.valueOf(-9259544));
    defaultColors.put("chat_outAudioSeekbarFill", Integer.valueOf(-8863118));
    defaultColors.put("chat_inVoiceSeekbar", Integer.valueOf(-2169365));
    defaultColors.put("chat_outVoiceSeekbar", Integer.valueOf(-4463700));
    defaultColors.put("chat_inVoiceSeekbarSelected", Integer.valueOf(-4399384));
    defaultColors.put("chat_outVoiceSeekbarSelected", Integer.valueOf(-5644906));
    defaultColors.put("chat_inVoiceSeekbarFill", Integer.valueOf(-9259544));
    defaultColors.put("chat_outVoiceSeekbarFill", Integer.valueOf(-8863118));
    defaultColors.put("chat_inFileProgress", Integer.valueOf(-1314571));
    defaultColors.put("chat_outFileProgress", Integer.valueOf(-2427453));
    defaultColors.put("chat_inFileProgressSelected", Integer.valueOf(-3413258));
    defaultColors.put("chat_outFileProgressSelected", Integer.valueOf(-3806041));
    defaultColors.put("chat_inFileNameText", Integer.valueOf(-11625772));
    defaultColors.put("chat_outFileNameText", Integer.valueOf(-11162801));
    defaultColors.put("chat_inFileInfoText", Integer.valueOf(-6182221));
    defaultColors.put("chat_outFileInfoText", Integer.valueOf(-10112933));
    defaultColors.put("chat_inFileInfoSelectedText", Integer.valueOf(-7752511));
    defaultColors.put("chat_outFileInfoSelectedText", Integer.valueOf(-10112933));
    defaultColors.put("chat_inFileBackground", Integer.valueOf(-1314571));
    defaultColors.put("chat_outFileBackground", Integer.valueOf(-2427453));
    defaultColors.put("chat_inFileBackgroundSelected", Integer.valueOf(-3413258));
    defaultColors.put("chat_outFileBackgroundSelected", Integer.valueOf(-3806041));
    defaultColors.put("chat_inVenueNameText", Integer.valueOf(-11625772));
    defaultColors.put("chat_outVenueNameText", Integer.valueOf(-11162801));
    defaultColors.put("chat_inVenueInfoText", Integer.valueOf(-6182221));
    defaultColors.put("chat_outVenueInfoText", Integer.valueOf(-10112933));
    defaultColors.put("chat_inVenueInfoSelectedText", Integer.valueOf(-7752511));
    defaultColors.put("chat_outVenueInfoSelectedText", Integer.valueOf(-10112933));
    defaultColors.put("chat_mediaInfoText", Integer.valueOf(-1));
    defaultColors.put("chat_linkSelectBackground", Integer.valueOf(862104035));
    defaultColors.put("chat_textSelectBackground", Integer.valueOf(1717742051));
    defaultColors.put("chat_emojiPanelBackground", Integer.valueOf(-657673));
    defaultColors.put("chat_emojiPanelShadowLine", Integer.valueOf(-1907225));
    defaultColors.put("chat_emojiPanelEmptyText", Integer.valueOf(-7829368));
    defaultColors.put("chat_emojiPanelIcon", Integer.valueOf(-5723992));
    defaultColors.put("chat_emojiPanelIconSelected", Integer.valueOf(-13920542));
    defaultColors.put("chat_emojiPanelStickerPackSelector", Integer.valueOf(-1907225));
    defaultColors.put("chat_emojiPanelIconSelector", Integer.valueOf(-13920542));
    defaultColors.put("chat_emojiPanelBackspace", Integer.valueOf(-5723992));
    defaultColors.put("chat_emojiPanelMasksIcon", Integer.valueOf(-1));
    defaultColors.put("chat_emojiPanelMasksIconSelected", Integer.valueOf(-10305560));
    defaultColors.put("chat_emojiPanelTrendingTitle", Integer.valueOf(-14606047));
    defaultColors.put("chat_emojiPanelTrendingDescription", Integer.valueOf(-7697782));
    defaultColors.put("chat_botKeyboardButtonText", Integer.valueOf(-13220017));
    defaultColors.put("chat_botKeyboardButtonBackground", Integer.valueOf(-1775639));
    defaultColors.put("chat_botKeyboardButtonBackgroundPressed", Integer.valueOf(-3354156));
    defaultColors.put("chat_unreadMessagesStartArrowIcon", Integer.valueOf(-6113849));
    defaultColors.put("chat_unreadMessagesStartText", Integer.valueOf(-11102772));
    defaultColors.put("chat_unreadMessagesStartBackground", Integer.valueOf(-1));
    defaultColors.put("chat_editDoneIcon", Integer.valueOf(-11420173));
    defaultColors.put("chat_inFileIcon", Integer.valueOf(-6113849));
    defaultColors.put("chat_inFileSelectedIcon", Integer.valueOf(-7883067));
    defaultColors.put("chat_outFileIcon", Integer.valueOf(-8011912));
    defaultColors.put("chat_outFileSelectedIcon", Integer.valueOf(-8011912));
    defaultColors.put("chat_inLocationBackground", Integer.valueOf(-1314571));
    defaultColors.put("chat_inLocationIcon", Integer.valueOf(-6113849));
    defaultColors.put("chat_outLocationBackground", Integer.valueOf(-2427453));
    defaultColors.put("chat_outLocationIcon", Integer.valueOf(-7880840));
    defaultColors.put("chat_inContactBackground", Integer.valueOf(-9259544));
    defaultColors.put("chat_inContactIcon", Integer.valueOf(-1));
    defaultColors.put("chat_outContactBackground", Integer.valueOf(-8863118));
    defaultColors.put("chat_outContactIcon", Integer.valueOf(-1048610));
    defaultColors.put("chat_outBroadcast", Integer.valueOf(-12146122));
    defaultColors.put("chat_mediaBroadcast", Integer.valueOf(-1));
    defaultColors.put("chat_searchPanelIcons", Integer.valueOf(-10639908));
    defaultColors.put("chat_searchPanelText", Integer.valueOf(-11625772));
    defaultColors.put("chat_secretChatStatusText", Integer.valueOf(-8421505));
    defaultColors.put("chat_fieldOverlayText", Integer.valueOf(-12940081));
    defaultColors.put("chat_stickersHintPanel", Integer.valueOf(-1));
    defaultColors.put("chat_replyPanelIcons", Integer.valueOf(-11032346));
    defaultColors.put("chat_replyPanelClose", Integer.valueOf(-5723992));
    defaultColors.put("chat_replyPanelName", Integer.valueOf(-12940081));
    defaultColors.put("chat_replyPanelMessage", Integer.valueOf(-14540254));
    defaultColors.put("chat_replyPanelLine", Integer.valueOf(-1513240));
    defaultColors.put("chat_messagePanelBackground", Integer.valueOf(-1));
    defaultColors.put("chat_messagePanelText", Integer.valueOf(-16777216));
    defaultColors.put("chat_messagePanelHint", Integer.valueOf(-5066062));
    defaultColors.put("chat_messagePanelShadow", Integer.valueOf(-16777216));
    defaultColors.put("chat_messagePanelIcons", Integer.valueOf(-5723992));
    defaultColors.put("chat_recordedVoicePlayPause", Integer.valueOf(-1));
    defaultColors.put("chat_recordedVoicePlayPausePressed", Integer.valueOf(-2495749));
    defaultColors.put("chat_recordedVoiceDot", Integer.valueOf(-2468275));
    defaultColors.put("chat_recordedVoiceBackground", Integer.valueOf(-11165981));
    defaultColors.put("chat_recordedVoiceProgress", Integer.valueOf(-6107400));
    defaultColors.put("chat_recordedVoiceProgressInner", Integer.valueOf(-1));
    defaultColors.put("chat_recordVoiceCancel", Integer.valueOf(-6710887));
    defaultColors.put("chat_messagePanelSend", Integer.valueOf(-10309397));
    defaultColors.put("chat_recordTime", Integer.valueOf(-11711413));
    defaultColors.put("chat_emojiPanelNewTrending", Integer.valueOf(-11688214));
    defaultColors.put("chat_gifSaveHintText", Integer.valueOf(-1));
    defaultColors.put("chat_gifSaveHintBackground", Integer.valueOf(-871296751));
    defaultColors.put("chat_goDownButton", Integer.valueOf(-1));
    defaultColors.put("chat_goDownButtonShadow", Integer.valueOf(-16777216));
    defaultColors.put("chat_goDownButtonIcon", Integer.valueOf(-5723992));
    defaultColors.put("chat_goDownButtonCounter", Integer.valueOf(-1));
    defaultColors.put("chat_goDownButtonCounterBackground", Integer.valueOf(-11689240));
    defaultColors.put("chat_messagePanelCancelInlineBot", Integer.valueOf(-5395027));
    defaultColors.put("chat_messagePanelVoicePressed", Integer.valueOf(-1));
    defaultColors.put("chat_messagePanelVoiceBackground", Integer.valueOf(-11037236));
    defaultColors.put("chat_messagePanelVoiceShadow", Integer.valueOf(218103808));
    defaultColors.put("chat_messagePanelVoiceDelete", Integer.valueOf(-9211021));
    defaultColors.put("chat_messagePanelVoiceDuration", Integer.valueOf(-1));
    defaultColors.put("chat_inlineResultIcon", Integer.valueOf(-11037236));
    defaultColors.put("chat_topPanelBackground", Integer.valueOf(-1));
    defaultColors.put("chat_topPanelClose", Integer.valueOf(-5723992));
    defaultColors.put("chat_topPanelLine", Integer.valueOf(-9658414));
    defaultColors.put("chat_topPanelTitle", Integer.valueOf(-12940081));
    defaultColors.put("chat_topPanelMessage", Integer.valueOf(-6710887));
    defaultColors.put("chat_reportSpam", Integer.valueOf(-3188393));
    defaultColors.put("chat_addContact", Integer.valueOf(-11894091));
    defaultColors.put("chat_inLoader", Integer.valueOf(-9259544));
    defaultColors.put("chat_inLoaderSelected", Integer.valueOf(-10114080));
    defaultColors.put("chat_outLoader", Integer.valueOf(-8863118));
    defaultColors.put("chat_outLoaderSelected", Integer.valueOf(-9783964));
    defaultColors.put("chat_inLoaderPhoto", Integer.valueOf(-6113080));
    defaultColors.put("chat_inLoaderPhotoSelected", Integer.valueOf(-6113849));
    defaultColors.put("chat_inLoaderPhotoIcon", Integer.valueOf(-197380));
    defaultColors.put("chat_inLoaderPhotoIconSelected", Integer.valueOf(-1314571));
    defaultColors.put("chat_outLoaderPhoto", Integer.valueOf(-8011912));
    defaultColors.put("chat_outLoaderPhotoSelected", Integer.valueOf(-8538000));
    defaultColors.put("chat_outLoaderPhotoIcon", Integer.valueOf(-2427453));
    defaultColors.put("chat_outLoaderPhotoIconSelected", Integer.valueOf(-4134748));
    defaultColors.put("chat_mediaLoaderPhoto", Integer.valueOf(1711276032));
    defaultColors.put("chat_mediaLoaderPhotoSelected", Integer.valueOf(2130706432));
    defaultColors.put("chat_mediaLoaderPhotoIcon", Integer.valueOf(-1));
    defaultColors.put("chat_mediaLoaderPhotoIconSelected", Integer.valueOf(-2500135));
    defaultColors.put("chat_secretTimerBackground", Integer.valueOf(-868326258));
    defaultColors.put("chat_secretTimerText", Integer.valueOf(-1));
    defaultColors.put("profile_creatorIcon", Integer.valueOf(-11888682));
    defaultColors.put("profile_adminIcon", Integer.valueOf(-8026747));
    defaultColors.put("profile_actionIcon", Integer.valueOf(-9211021));
    defaultColors.put("profile_actionBackground", Integer.valueOf(-1));
    defaultColors.put("profile_actionPressedBackground", Integer.valueOf(-855310));
    defaultColors.put("profile_verifiedBackground", Integer.valueOf(-5056776));
    defaultColors.put("profile_verifiedCheck", Integer.valueOf(-11959368));
    defaultColors.put("profile_title", Integer.valueOf(-1));
    defaultColors.put("player_actionBar", Integer.valueOf(-1));
    defaultColors.put("player_actionBarSelector", Integer.valueOf(788529152));
    defaultColors.put("player_actionBarTitle", Integer.valueOf(-14606047));
    defaultColors.put("player_actionBarTop", Integer.valueOf(-1728053248));
    defaultColors.put("player_actionBarSubtitle", Integer.valueOf(-7697782));
    defaultColors.put("player_actionBarItems", Integer.valueOf(-7697782));
    defaultColors.put("player_seekBarBackground", Integer.valueOf(-436207617));
    defaultColors.put("player_time", Integer.valueOf(-15095832));
    defaultColors.put("player_duration", Integer.valueOf(-7697782));
    defaultColors.put("player_progressBackground", Integer.valueOf(419430400));
    defaultColors.put("player_progress", Integer.valueOf(-14438417));
    defaultColors.put("player_placeholder", Integer.valueOf(-2500135));
    defaultColors.put("player_button", Integer.valueOf(-7697782));
    defaultColors.put("player_buttonActive", Integer.valueOf(-14438417));
    defaultColors.put("files_folderIcon", Integer.valueOf(-6710887));
    defaultColors.put("files_folderIconBackground", Integer.valueOf(-986896));
    defaultColors.put("files_iconText", Integer.valueOf(-1));
    defaultColors.put("sessions_devicesImage", Integer.valueOf(-6908266));
    defaultColors.put("location_markerX", Integer.valueOf(-8355712));
    defaultColors.put("location_sendLocationBackground", Integer.valueOf(-9592620));
    defaultColors.put("location_sendLocationIcon", Integer.valueOf(-1));
    defaultColors.put("calls_callReceivedGreenIcon", Integer.valueOf(-16725933));
    defaultColors.put("calls_callReceivedRedIcon", Integer.valueOf(-47032));
    defaultColors.put("featuredStickers_addedIcon", Integer.valueOf(-11491093));
    defaultColors.put("featuredStickers_buttonProgress", Integer.valueOf(-1));
    defaultColors.put("featuredStickers_addButton", Integer.valueOf(-11491093));
    defaultColors.put("featuredStickers_addButtonPressed", Integer.valueOf(-12346402));
    defaultColors.put("featuredStickers_delButton", Integer.valueOf(-2533545));
    defaultColors.put("featuredStickers_delButtonPressed", Integer.valueOf(-3782327));
    defaultColors.put("featuredStickers_buttonText", Integer.valueOf(-1));
    defaultColors.put("featuredStickers_unread", Integer.valueOf(-11688214));
    defaultColors.put("inappPlayerPerformer", Integer.valueOf(-13683656));
    defaultColors.put("inappPlayerTitle", Integer.valueOf(-13683656));
    defaultColors.put("inappPlayerBackground", Integer.valueOf(-1));
    defaultColors.put("inappPlayerPlayPause", Integer.valueOf(-10309397));
    defaultColors.put("inappPlayerClose", Integer.valueOf(-5723992));
    defaultColors.put("returnToCallBackground", Integer.valueOf(-12279325));
    defaultColors.put("returnToCallText", Integer.valueOf(-1));
    defaultColors.put("sharedMedia_startStopLoadIcon", Integer.valueOf(-13196562));
    defaultColors.put("sharedMedia_linkPlaceholder", Integer.valueOf(-986896));
    defaultColors.put("sharedMedia_linkPlaceholderText", Integer.valueOf(-1));
    defaultColors.put("checkbox", Integer.valueOf(-10567099));
    defaultColors.put("checkboxCheck", Integer.valueOf(-1));
    defaultColors.put("stickers_menu", Integer.valueOf(-4801083));
    defaultColors.put("stickers_menuSelector", Integer.valueOf(788529152));
    defaultColors.put("changephoneinfo_image", Integer.valueOf(-5723992));
    defaultColors.put("groupcreate_hintText", Integer.valueOf(-6182221));
    defaultColors.put("groupcreate_cursor", Integer.valueOf(-11361317));
    defaultColors.put("groupcreate_sectionShadow", Integer.valueOf(-16777216));
    defaultColors.put("groupcreate_sectionText", Integer.valueOf(-8617336));
    defaultColors.put("groupcreate_onlineText", Integer.valueOf(-12545331));
    defaultColors.put("groupcreate_offlineText", Integer.valueOf(-8156010));
    defaultColors.put("groupcreate_checkbox", Integer.valueOf(-10567099));
    defaultColors.put("groupcreate_checkboxCheck", Integer.valueOf(-1));
    defaultColors.put("groupcreate_spanText", Integer.valueOf(-14606047));
    defaultColors.put("groupcreate_spanBackground", Integer.valueOf(-855310));
    defaultColors.put("login_progressInner", Integer.valueOf(-1971470));
    defaultColors.put("login_progressOuter", Integer.valueOf(-10313520));
    defaultColors.put("musicPicker_checkbox", Integer.valueOf(-14043401));
    defaultColors.put("musicPicker_checkboxCheck", Integer.valueOf(-1));
    defaultColors.put("musicPicker_buttonBackground", Integer.valueOf(-10702870));
    defaultColors.put("musicPicker_buttonIcon", Integer.valueOf(-1));
    defaultColors.put("picker_enabledButton", Integer.valueOf(-15095832));
    defaultColors.put("picker_disabledButton", Integer.valueOf(-6710887));
    defaultColors.put("picker_badge", Integer.valueOf(-14043401));
    defaultColors.put("picker_badgeText", Integer.valueOf(-1));
    defaultColors.put("chat_botSwitchToInlineText", Integer.valueOf(-12348980));
    defaultColors.put("calls_ratingStar", Integer.valueOf(-2147483648));
    defaultColors.put("calls_ratingStarSelected", Integer.valueOf(-11888682));
    themes = new ArrayList();
    otherThemes = new ArrayList();
    themesDict = new HashMap();
    currentColors = new HashMap();
    Object localObject1 = new ThemeInfo();
    ((ThemeInfo)localObject1).name = LocaleController.getString("Default", 2131165626);
    Object localObject4 = themes;
    defaultTheme = (ThemeInfo)localObject1;
    currentTheme = (ThemeInfo)localObject1;
    ((ArrayList)localObject4).add(localObject1);
    themesDict.put("Default", defaultTheme);
    localObject1 = new ThemeInfo();
    ((ThemeInfo)localObject1).name = "Dark";
    ((ThemeInfo)localObject1).assetName = "dark.attheme";
    themes.add(localObject1);
    themesDict.put("Dark", localObject1);
    localObject1 = new ThemeInfo();
    ((ThemeInfo)localObject1).name = "Blue";
    ((ThemeInfo)localObject1).assetName = "bluebubbles.attheme";
    themes.add(localObject1);
    themesDict.put("Blue", localObject1);
    localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
    localObject4 = ((SharedPreferences)localObject1).getString("themes2", null);
    int i;
    if (!TextUtils.isEmpty((CharSequence)localObject4))
      try
      {
        localObject1 = new JSONArray((String)localObject4);
        i = 0;
        while (i < ((JSONArray)localObject1).length())
        {
          localObject4 = ThemeInfo.createWithJson(((JSONArray)localObject1).getJSONObject(i));
          if (localObject4 != null)
          {
            otherThemes.add(localObject4);
            themes.add(localObject4);
            themesDict.put(((ThemeInfo)localObject4).name, localObject4);
          }
          i += 1;
        }
      }
      catch (Exception localException1)
      {
        FileLog.e(localException1);
      }
    while (true)
    {
      sortThemes();
      try
      {
        Object localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getString("theme", null);
        if (localObject2 != null)
        {
          localObject2 = (ThemeInfo)themesDict.get(localObject2);
          localObject4 = localObject2;
          if (localObject2 == null)
            localObject4 = defaultTheme;
          applyTheme((ThemeInfo)localObject4, false, false);
          return;
          localObject4 = ((SharedPreferences)localObject2).getString("themes", null);
          if (!TextUtils.isEmpty((CharSequence)localObject4))
          {
            localObject4 = ((String)localObject4).split("&");
            i = 0;
            while (i < localObject4.length)
            {
              ThemeInfo localThemeInfo = ThemeInfo.createWithString(localObject4[i]);
              if (localThemeInfo != null)
              {
                otherThemes.add(localThemeInfo);
                themes.add(localThemeInfo);
                themesDict.put(localThemeInfo.name, localThemeInfo);
              }
              i += 1;
            }
          }
          saveOtherThemes();
          ((SharedPreferences)localObject2).edit().remove("themes").commit();
        }
      }
      catch (Exception localObject3)
      {
        while (true)
        {
          FileLog.e(localException2);
          Object localObject3 = null;
        }
      }
    }
  }

  public static void applyChatServiceMessageColor()
  {
    if (chat_actionBackgroundPaint == null);
    Object localObject2;
    do
    {
      return;
      localObject2 = (Integer)currentColors.get("chat_serviceBackground");
      Integer localInteger = (Integer)currentColors.get("chat_serviceBackgroundSelected");
      Object localObject1 = localObject2;
      if (localObject2 == null)
        localObject1 = Integer.valueOf(serviceMessageColor);
      localObject2 = localInteger;
      if (localInteger == null)
        localObject2 = Integer.valueOf(serviceSelectedMessageColor);
      if (currentColor == ((Integer)localObject1).intValue())
        continue;
      chat_actionBackgroundPaint.setColor(((Integer)localObject1).intValue());
      colorFilter = new PorterDuffColorFilter(((Integer)localObject1).intValue(), PorterDuff.Mode.MULTIPLY);
      currentColor = ((Integer)localObject1).intValue();
      if (chat_timeStickerBackgroundDrawable == null)
        continue;
      int i = 0;
      while (i < 4)
      {
        chat_cornerOuter[i].setColorFilter(colorFilter);
        chat_cornerInner[i].setColorFilter(colorFilter);
        i += 1;
      }
      chat_timeStickerBackgroundDrawable.setColorFilter(colorFilter);
    }
    while (currentSelectedColor == ((Integer)localObject2).intValue());
    currentSelectedColor = ((Integer)localObject2).intValue();
    colorPressedFilter = new PorterDuffColorFilter(((Integer)localObject2).intValue(), PorterDuff.Mode.MULTIPLY);
  }

  public static void applyChatTheme(boolean paramBoolean)
  {
    if (chat_msgTextPaint == null);
    do
      return;
    while ((chat_msgInDrawable == null) || (paramBoolean));
    chat_gamePaint.setColor(getColor("chat_previewGameText"));
    chat_durationPaint.setColor(getColor("chat_previewDurationText"));
    chat_botButtonPaint.setColor(getColor("chat_botButtonText"));
    chat_urlPaint.setColor(getColor("chat_linkSelectBackground"));
    chat_botProgressPaint.setColor(getColor("chat_botProgress"));
    chat_deleteProgressPaint.setColor(getColor("chat_secretTimeText"));
    chat_textSearchSelectionPaint.setColor(getColor("chat_textSelectBackground"));
    chat_msgErrorPaint.setColor(getColor("chat_sentError"));
    chat_statusPaint.setColor(getColor("actionBarDefaultSubtitle"));
    chat_statusRecordPaint.setColor(getColor("actionBarDefaultSubtitle"));
    chat_actionTextPaint.setColor(getColor("chat_serviceText"));
    chat_actionTextPaint.linkColor = getColor("chat_serviceLink");
    chat_contextResult_titleTextPaint.setColor(getColor("windowBackgroundWhiteBlackText"));
    chat_composeBackgroundPaint.setColor(getColor("chat_messagePanelBackground"));
    setDrawableColorByKey(chat_msgInDrawable, "chat_inBubble");
    setDrawableColorByKey(chat_msgInSelectedDrawable, "chat_inBubbleSelected");
    setDrawableColorByKey(chat_msgInShadowDrawable, "chat_inBubbleShadow");
    setDrawableColorByKey(chat_msgOutDrawable, "chat_outBubble");
    setDrawableColorByKey(chat_msgOutSelectedDrawable, "chat_outBubbleSelected");
    setDrawableColorByKey(chat_msgOutShadowDrawable, "chat_outBubbleShadow");
    setDrawableColorByKey(chat_msgInMediaDrawable, "chat_inBubble");
    setDrawableColorByKey(chat_msgInMediaSelectedDrawable, "chat_inBubbleSelected");
    setDrawableColorByKey(chat_msgInMediaShadowDrawable, "chat_inBubbleShadow");
    setDrawableColorByKey(chat_msgOutMediaDrawable, "chat_outBubble");
    setDrawableColorByKey(chat_msgOutMediaSelectedDrawable, "chat_outBubbleSelected");
    setDrawableColorByKey(chat_msgOutMediaShadowDrawable, "chat_outBubbleShadow");
    setDrawableColorByKey(chat_msgOutCheckDrawable, "chat_outSentCheck");
    setDrawableColorByKey(chat_msgOutCheckSelectedDrawable, "chat_outSentCheckSelected");
    setDrawableColorByKey(chat_msgOutHalfCheckDrawable, "chat_outSentCheck");
    setDrawableColorByKey(chat_msgOutHalfCheckSelectedDrawable, "chat_outSentCheckSelected");
    setDrawableColorByKey(chat_msgOutClockDrawable, "chat_outSentClock");
    setDrawableColorByKey(chat_msgOutSelectedClockDrawable, "chat_outSentClockSelected");
    setDrawableColorByKey(chat_msgInClockDrawable, "chat_inSentClock");
    setDrawableColorByKey(chat_msgInSelectedClockDrawable, "chat_inSentClockSelected");
    setDrawableColorByKey(chat_msgMediaCheckDrawable, "chat_mediaSentCheck");
    setDrawableColorByKey(chat_msgMediaHalfCheckDrawable, "chat_mediaSentCheck");
    setDrawableColorByKey(chat_msgMediaClockDrawable, "chat_mediaSentClock");
    setDrawableColorByKey(chat_msgStickerCheckDrawable, "chat_serviceText");
    setDrawableColorByKey(chat_msgStickerHalfCheckDrawable, "chat_serviceText");
    setDrawableColorByKey(chat_msgStickerClockDrawable, "chat_serviceText");
    setDrawableColorByKey(chat_msgStickerViewsDrawable, "chat_serviceText");
    setDrawableColorByKey(chat_shareIconDrawable, "chat_serviceIcon");
    setDrawableColorByKey(chat_botInlineDrawable, "chat_serviceIcon");
    setDrawableColorByKey(chat_botLinkDrawalbe, "chat_serviceIcon");
    setDrawableColorByKey(chat_msgInViewsDrawable, "chat_inViews");
    setDrawableColorByKey(chat_msgInViewsSelectedDrawable, "chat_inViewsSelected");
    setDrawableColorByKey(chat_msgOutViewsDrawable, "chat_outViews");
    setDrawableColorByKey(chat_msgOutViewsSelectedDrawable, "chat_outViewsSelected");
    setDrawableColorByKey(chat_msgMediaViewsDrawable, "chat_mediaViews");
    setDrawableColorByKey(chat_msgInMenuDrawable, "chat_inMenu");
    setDrawableColorByKey(chat_msgInMenuSelectedDrawable, "chat_inMenuSelected");
    setDrawableColorByKey(chat_msgOutMenuDrawable, "chat_outMenu");
    setDrawableColorByKey(chat_msgOutMenuSelectedDrawable, "chat_outMenuSelected");
    setDrawableColorByKey(chat_msgMediaMenuDrawable, "chat_mediaMenu");
    setDrawableColorByKey(chat_msgOutInstantDrawable, "chat_outInstant");
    setDrawableColorByKey(chat_msgOutInstantSelectedDrawable, "chat_outInstantSelected");
    setDrawableColorByKey(chat_msgInInstantDrawable, "chat_inInstant");
    setDrawableColorByKey(chat_msgInInstantSelectedDrawable, "chat_inInstantSelected");
    setDrawableColorByKey(chat_msgErrorDrawable, "chat_sentErrorIcon");
    setDrawableColorByKey(chat_muteIconDrawable, "chat_muteIcon");
    setDrawableColorByKey(chat_lockIconDrawable, "chat_lockIcon");
    setDrawableColorByKey(chat_msgBroadcastDrawable, "chat_outBroadcast");
    setDrawableColorByKey(chat_msgBroadcastMediaDrawable, "chat_mediaBroadcast");
    setDrawableColorByKey(chat_inlineResultFile, "chat_inlineResultIcon");
    setDrawableColorByKey(chat_inlineResultAudio, "chat_inlineResultIcon");
    setDrawableColorByKey(chat_inlineResultLocation, "chat_inlineResultIcon");
    setDrawableColorByKey(chat_msgInCallDrawable, "chat_inInstant");
    setDrawableColorByKey(chat_msgInCallSelectedDrawable, "chat_inInstantSelected");
    setDrawableColorByKey(chat_msgOutCallDrawable, "chat_outInstant");
    setDrawableColorByKey(chat_msgOutCallSelectedDrawable, "chat_outInstantSelected");
    setDrawableColorByKey(chat_msgCallUpRedDrawable, "calls_callReceivedRedIcon");
    setDrawableColorByKey(chat_msgCallUpGreenDrawable, "calls_callReceivedGreenIcon");
    setDrawableColorByKey(chat_msgCallDownRedDrawable, "calls_callReceivedRedIcon");
    setDrawableColorByKey(chat_msgCallDownGreenDrawable, "calls_callReceivedGreenIcon");
    int i = 0;
    while (i < 5)
    {
      setCombinedDrawableColor(chat_fileStatesDrawable[i][0], getColor("chat_outLoader"), false);
      setCombinedDrawableColor(chat_fileStatesDrawable[i][0], getColor("chat_outBubble"), true);
      setCombinedDrawableColor(chat_fileStatesDrawable[i][1], getColor("chat_outLoaderSelected"), false);
      setCombinedDrawableColor(chat_fileStatesDrawable[i][1], getColor("chat_outBubbleSelected"), true);
      setCombinedDrawableColor(chat_fileStatesDrawable[(i + 5)][0], getColor("chat_inLoader"), false);
      setCombinedDrawableColor(chat_fileStatesDrawable[(i + 5)][0], getColor("chat_inBubble"), true);
      setCombinedDrawableColor(chat_fileStatesDrawable[(i + 5)][1], getColor("chat_inLoaderSelected"), false);
      setCombinedDrawableColor(chat_fileStatesDrawable[(i + 5)][1], getColor("chat_inBubbleSelected"), true);
      i += 1;
    }
    i = 0;
    while (i < 4)
    {
      setCombinedDrawableColor(chat_photoStatesDrawables[i][0], getColor("chat_mediaLoaderPhoto"), false);
      setCombinedDrawableColor(chat_photoStatesDrawables[i][0], getColor("chat_mediaLoaderPhotoIcon"), true);
      setCombinedDrawableColor(chat_photoStatesDrawables[i][1], getColor("chat_mediaLoaderPhotoSelected"), false);
      setCombinedDrawableColor(chat_photoStatesDrawables[i][1], getColor("chat_mediaLoaderPhotoIconSelected"), true);
      i += 1;
    }
    i = 0;
    while (i < 2)
    {
      setCombinedDrawableColor(chat_photoStatesDrawables[(i + 7)][0], getColor("chat_outLoaderPhoto"), false);
      setCombinedDrawableColor(chat_photoStatesDrawables[(i + 7)][0], getColor("chat_outLoaderPhotoIcon"), true);
      setCombinedDrawableColor(chat_photoStatesDrawables[(i + 7)][1], getColor("chat_outLoaderPhotoSelected"), false);
      setCombinedDrawableColor(chat_photoStatesDrawables[(i + 7)][1], getColor("chat_outLoaderPhotoIconSelected"), true);
      setCombinedDrawableColor(chat_photoStatesDrawables[(i + 10)][0], getColor("chat_inLoaderPhoto"), false);
      setCombinedDrawableColor(chat_photoStatesDrawables[(i + 10)][0], getColor("chat_inLoaderPhotoIcon"), true);
      setCombinedDrawableColor(chat_photoStatesDrawables[(i + 10)][1], getColor("chat_inLoaderPhotoSelected"), false);
      setCombinedDrawableColor(chat_photoStatesDrawables[(i + 10)][1], getColor("chat_inLoaderPhotoIconSelected"), true);
      i += 1;
    }
    setDrawableColorByKey(chat_photoStatesDrawables[9][0], "chat_outFileIcon");
    setDrawableColorByKey(chat_photoStatesDrawables[9][1], "chat_outFileSelectedIcon");
    setDrawableColorByKey(chat_photoStatesDrawables[12][0], "chat_inFileIcon");
    setDrawableColorByKey(chat_photoStatesDrawables[12][1], "chat_inFileSelectedIcon");
    setCombinedDrawableColor(chat_contactDrawable[0], getColor("chat_inContactBackground"), false);
    setCombinedDrawableColor(chat_contactDrawable[0], getColor("chat_inContactIcon"), true);
    setCombinedDrawableColor(chat_contactDrawable[1], getColor("chat_outContactBackground"), false);
    setCombinedDrawableColor(chat_contactDrawable[1], getColor("chat_outContactIcon"), true);
    setCombinedDrawableColor(chat_locationDrawable[0], getColor("chat_inLocationBackground"), false);
    setCombinedDrawableColor(chat_locationDrawable[0], getColor("chat_inLocationIcon"), true);
    setCombinedDrawableColor(chat_locationDrawable[1], getColor("chat_outLocationBackground"), false);
    setCombinedDrawableColor(chat_locationDrawable[1], getColor("chat_outLocationIcon"), true);
    setDrawableColorByKey(chat_composeShadowDrawable, "chat_messagePanelShadow");
    applyChatServiceMessageColor();
  }

  public static void applyCommonTheme()
  {
    if (dividerPaint == null)
      return;
    dividerPaint.setColor(getColor("divider"));
    linkSelectionPaint.setColor(getColor("windowBackgroundWhiteLinkSelection"));
    setDrawableColorByKey(avatar_broadcastDrawable, "avatar_text");
    setDrawableColorByKey(avatar_photoDrawable, "avatar_text");
  }

  public static void applyDialogsTheme()
  {
    if (dialogs_namePaint == null)
      return;
    dialogs_namePaint.setColor(getColor("chats_name"));
    dialogs_nameEncryptedPaint.setColor(getColor("chats_secretName"));
    TextPaint localTextPaint1 = dialogs_messagePaint;
    TextPaint localTextPaint2 = dialogs_messagePaint;
    int i = getColor("chats_message");
    localTextPaint2.linkColor = i;
    localTextPaint1.setColor(i);
    dialogs_tabletSeletedPaint.setColor(getColor("chats_tabletSelectedOverlay"));
    dialogs_pinnedPaint.setColor(getColor("chats_pinnedOverlay"));
    dialogs_timePaint.setColor(getColor("chats_date"));
    dialogs_countTextPaint.setColor(getColor("chats_unreadCounterText"));
    dialogs_messagePrintingPaint.setColor(getColor("chats_actionMessage"));
    dialogs_countPaint.setColor(getColor("chats_unreadCounter"));
    dialogs_countGrayPaint.setColor(getColor("chats_unreadCounterMuted"));
    dialogs_errorPaint.setColor(getColor("chats_sentError"));
    dialogs_onlinePaint.setColor(getColor("windowBackgroundWhiteBlueText3"));
    dialogs_offlinePaint.setColor(getColor("windowBackgroundWhiteGrayText3"));
    setDrawableColorByKey(dialogs_lockDrawable, "chats_secretIcon");
    setDrawableColorByKey(dialogs_checkDrawable, "chats_sentCheck");
    setDrawableColorByKey(dialogs_halfCheckDrawable, "chats_sentCheck");
    setDrawableColorByKey(dialogs_clockDrawable, "chats_sentClock");
    setDrawableColorByKey(dialogs_errorDrawable, "chats_sentErrorIcon");
    setDrawableColorByKey(dialogs_groupDrawable, "chats_nameIcon");
    setDrawableColorByKey(dialogs_broadcastDrawable, "chats_nameIcon");
    setDrawableColorByKey(dialogs_botDrawable, "chats_nameIcon");
    setDrawableColorByKey(dialogs_pinnedDrawable, "chats_pinnedIcon");
    setDrawableColorByKey(dialogs_muteDrawable, "chats_muteIcon");
    setDrawableColorByKey(dialogs_verifiedDrawable, "chats_verifiedBackground");
    setDrawableColorByKey(dialogs_verifiedCheckDrawable, "chats_verifiedCheck");
  }

  public static void applyPreviousTheme()
  {
    if (previousTheme == null)
      return;
    applyTheme(previousTheme, true, false);
    previousTheme = null;
  }

  public static void applyProfileTheme()
  {
    if (profile_verifiedDrawable == null)
      return;
    profile_aboutTextPaint.setColor(getColor("windowBackgroundWhiteBlackText"));
    profile_aboutTextPaint.linkColor = getColor("windowBackgroundWhiteLinkText");
    setDrawableColorByKey(profile_verifiedDrawable, "profile_verifiedBackground");
    setDrawableColorByKey(profile_verifiedCheckDrawable, "profile_verifiedCheck");
  }

  public static void applyTheme(ThemeInfo paramThemeInfo)
  {
    applyTheme(paramThemeInfo, true, true);
  }

  public static void applyTheme(ThemeInfo paramThemeInfo, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramThemeInfo == null)
      return;
    Object localObject = ThemeEditorView.getInstance();
    if (localObject != null)
      ((ThemeEditorView)localObject).destroy();
    while (true)
    {
      try
      {
        if ((paramThemeInfo.pathToFile == null) && (paramThemeInfo.assetName == null))
          break label152;
        if (!paramBoolean1)
          continue;
        localObject = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
        ((SharedPreferences.Editor)localObject).putString("theme", paramThemeInfo.name);
        if (!paramBoolean2)
          continue;
        ((SharedPreferences.Editor)localObject).remove("overrideThemeWallpaper");
        ((SharedPreferences.Editor)localObject).commit();
        if (paramThemeInfo.assetName != null)
        {
          currentColors = getThemeFileValues(null, paramThemeInfo.assetName);
          currentTheme = paramThemeInfo;
          reloadWallpaper();
          applyCommonTheme();
          applyDialogsTheme();
          applyProfileTheme();
          applyChatTheme(false);
          return;
        }
      }
      catch (Exception paramThemeInfo)
      {
        FileLog.e(paramThemeInfo);
        return;
      }
      currentColors = getThemeFileValues(new File(paramThemeInfo.pathToFile), null);
      continue;
      label152: if (paramBoolean1)
      {
        localObject = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
        ((SharedPreferences.Editor)localObject).remove("theme");
        if (paramBoolean2)
          ((SharedPreferences.Editor)localObject).remove("overrideThemeWallpaper");
        ((SharedPreferences.Editor)localObject).commit();
      }
      currentColors.clear();
      wallpaper = null;
      themedWallpaper = null;
    }
  }

  public static ThemeInfo applyThemeFile(File paramFile, String paramString, boolean paramBoolean)
  {
    boolean bool = true;
    while (true)
    {
      int i;
      try
      {
        if ((paramString.equals("Default")) || (paramString.equals("Dark")) || (paramString.equals("Blue")))
          break label178;
        File localFile = new File(ApplicationLoader.getFilesDirFixed(), paramString);
        if (!AndroidUtilities.copyFile(paramFile, localFile))
          return null;
        paramFile = (ThemeInfo)themesDict.get(paramString);
        if (paramFile != null)
          break label173;
        paramFile = new ThemeInfo();
        paramFile.name = paramString;
        paramFile.pathToFile = localFile.getAbsolutePath();
        i = 1;
        if (!paramBoolean)
        {
          if (i == 0)
            break label180;
          themes.add(paramFile);
          themesDict.put(paramFile.name, paramFile);
          otherThemes.add(paramFile);
          sortThemes();
          saveOtherThemes();
          break label180;
          label144: applyTheme(paramFile, paramBoolean, true);
          return paramFile;
        }
      }
      catch (Exception paramFile)
      {
        FileLog.e(paramFile);
        return null;
      }
      previousTheme = currentTheme;
      label173: label178: label180: 
      while (paramBoolean)
      {
        paramBoolean = false;
        break label144;
        i = 0;
        break;
        return null;
      }
      paramBoolean = bool;
    }
  }

  private static void calcBackgroundColor(Drawable paramDrawable, int paramInt)
  {
    if (paramInt != 2)
    {
      paramDrawable = AndroidUtilities.calcDrawableColor(paramDrawable);
      serviceMessageColor = paramDrawable[0];
      serviceSelectedMessageColor = paramDrawable[1];
    }
  }

  public static void createChatResources(Context paramContext, boolean paramBoolean)
  {
    synchronized (sync)
    {
      if (chat_msgTextPaint == null)
      {
        chat_msgTextPaint = new TextPaint(1);
        chat_msgGameTextPaint = new TextPaint(1);
        chat_msgTextPaintOneEmoji = new TextPaint(1);
        chat_msgTextPaintTwoEmoji = new TextPaint(1);
        chat_msgTextPaintThreeEmoji = new TextPaint(1);
        chat_msgBotButtonPaint = new TextPaint(1);
        chat_msgBotButtonPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      }
      if ((!paramBoolean) && (chat_msgInDrawable == null))
      {
        chat_infoPaint = new TextPaint(1);
        chat_docNamePaint = new TextPaint(1);
        chat_docNamePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        chat_docBackPaint = new Paint(1);
        chat_deleteProgressPaint = new Paint(1);
        chat_botProgressPaint = new Paint(1);
        chat_botProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        chat_botProgressPaint.setStyle(Paint.Style.STROKE);
        chat_locationTitlePaint = new TextPaint(1);
        chat_locationTitlePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        chat_locationAddressPaint = new TextPaint(1);
        chat_urlPaint = new Paint();
        chat_textSearchSelectionPaint = new Paint();
        chat_audioTimePaint = new TextPaint(1);
        chat_audioTitlePaint = new TextPaint(1);
        chat_audioTitlePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        chat_audioPerformerPaint = new TextPaint(1);
        chat_botButtonPaint = new TextPaint(1);
        chat_botButtonPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        chat_contactNamePaint = new TextPaint(1);
        chat_contactNamePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        chat_contactPhonePaint = new TextPaint(1);
        chat_durationPaint = new TextPaint(1);
        chat_gamePaint = new TextPaint(1);
        chat_gamePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        chat_shipmentPaint = new TextPaint(1);
        chat_timePaint = new TextPaint(1);
        chat_namePaint = new TextPaint(1);
        chat_namePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        chat_forwardNamePaint = new TextPaint(1);
        chat_replyNamePaint = new TextPaint(1);
        chat_replyNamePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        chat_replyTextPaint = new TextPaint(1);
        chat_instantViewPaint = new TextPaint(1);
        chat_instantViewPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        chat_instantViewRectPaint = new Paint(1);
        chat_instantViewRectPaint.setStyle(Paint.Style.STROKE);
        chat_replyLinePaint = new Paint();
        chat_msgErrorPaint = new Paint(1);
        chat_statusPaint = new Paint(1);
        chat_statusRecordPaint = new Paint(1);
        chat_statusRecordPaint.setStyle(Paint.Style.STROKE);
        chat_statusRecordPaint.setStrokeCap(Paint.Cap.ROUND);
        chat_actionTextPaint = new TextPaint(1);
        chat_actionTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        chat_actionBackgroundPaint = new Paint(1);
        chat_contextResult_titleTextPaint = new TextPaint(1);
        chat_contextResult_titleTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        chat_contextResult_descriptionTextPaint = new TextPaint(1);
        chat_composeBackgroundPaint = new Paint();
        ??? = paramContext.getResources();
        chat_msgInDrawable = ((Resources)???).getDrawable(2130837931).mutate();
        chat_msgInSelectedDrawable = ((Resources)???).getDrawable(2130837931).mutate();
        chat_msgOutDrawable = ((Resources)???).getDrawable(2130837935).mutate();
        chat_msgOutSelectedDrawable = ((Resources)???).getDrawable(2130837935).mutate();
        chat_msgInMediaDrawable = ((Resources)???).getDrawable(2130837941).mutate();
        chat_msgInMediaSelectedDrawable = ((Resources)???).getDrawable(2130837941).mutate();
        chat_msgOutMediaDrawable = ((Resources)???).getDrawable(2130837941).mutate();
        chat_msgOutMediaSelectedDrawable = ((Resources)???).getDrawable(2130837941).mutate();
        chat_msgOutCheckDrawable = ((Resources)???).getDrawable(2130837927).mutate();
        chat_msgOutCheckSelectedDrawable = ((Resources)???).getDrawable(2130837927).mutate();
        chat_msgMediaCheckDrawable = ((Resources)???).getDrawable(2130837927).mutate();
        chat_msgStickerCheckDrawable = ((Resources)???).getDrawable(2130837927).mutate();
        chat_msgOutHalfCheckDrawable = ((Resources)???).getDrawable(2130837930).mutate();
        chat_msgOutHalfCheckSelectedDrawable = ((Resources)???).getDrawable(2130837930).mutate();
        chat_msgMediaHalfCheckDrawable = ((Resources)???).getDrawable(2130837930).mutate();
        chat_msgStickerHalfCheckDrawable = ((Resources)???).getDrawable(2130837930).mutate();
        chat_msgOutClockDrawable = ((Resources)???).getDrawable(2130837928).mutate();
        chat_msgOutSelectedClockDrawable = ((Resources)???).getDrawable(2130837928).mutate();
        chat_msgInClockDrawable = ((Resources)???).getDrawable(2130837928).mutate();
        chat_msgInSelectedClockDrawable = ((Resources)???).getDrawable(2130837928).mutate();
        chat_msgMediaClockDrawable = ((Resources)???).getDrawable(2130837928).mutate();
        chat_msgStickerClockDrawable = ((Resources)???).getDrawable(2130837928).mutate();
        chat_msgInViewsDrawable = ((Resources)???).getDrawable(2130837956).mutate();
        chat_msgInViewsSelectedDrawable = ((Resources)???).getDrawable(2130837956).mutate();
        chat_msgOutViewsDrawable = ((Resources)???).getDrawable(2130837956).mutate();
        chat_msgOutViewsSelectedDrawable = ((Resources)???).getDrawable(2130837956).mutate();
        chat_msgMediaViewsDrawable = ((Resources)???).getDrawable(2130837956).mutate();
        chat_msgStickerViewsDrawable = ((Resources)???).getDrawable(2130837956).mutate();
        chat_msgInMenuDrawable = ((Resources)???).getDrawable(2130837926).mutate();
        chat_msgInMenuSelectedDrawable = ((Resources)???).getDrawable(2130837926).mutate();
        chat_msgOutMenuDrawable = ((Resources)???).getDrawable(2130837926).mutate();
        chat_msgOutMenuSelectedDrawable = ((Resources)???).getDrawable(2130837926).mutate();
        chat_msgMediaMenuDrawable = ((Resources)???).getDrawable(2130838117);
        chat_msgInInstantDrawable = ((Resources)???).getDrawable(2130837933).mutate();
        chat_msgInInstantSelectedDrawable = ((Resources)???).getDrawable(2130837933).mutate();
        chat_msgOutInstantDrawable = ((Resources)???).getDrawable(2130837933).mutate();
        chat_msgOutInstantSelectedDrawable = ((Resources)???).getDrawable(2130837933).mutate();
        chat_msgErrorDrawable = ((Resources)???).getDrawable(2130837957);
        chat_muteIconDrawable = ((Resources)???).getDrawable(2130837892).mutate();
        chat_lockIconDrawable = ((Resources)???).getDrawable(2130837793);
        chat_msgBroadcastDrawable = ((Resources)???).getDrawable(2130837652).mutate();
        chat_msgBroadcastMediaDrawable = ((Resources)???).getDrawable(2130837652).mutate();
        chat_msgInCallDrawable = ((Resources)???).getDrawable(2130837758).mutate();
        chat_msgInCallSelectedDrawable = ((Resources)???).getDrawable(2130837758).mutate();
        chat_msgOutCallDrawable = ((Resources)???).getDrawable(2130837758).mutate();
        chat_msgOutCallSelectedDrawable = ((Resources)???).getDrawable(2130837758).mutate();
        chat_msgCallUpRedDrawable = ((Resources)???).getDrawable(2130837754).mutate();
        chat_msgCallUpGreenDrawable = ((Resources)???).getDrawable(2130837754).mutate();
        chat_msgCallDownRedDrawable = ((Resources)???).getDrawable(2130837755).mutate();
        chat_msgCallDownGreenDrawable = ((Resources)???).getDrawable(2130837755).mutate();
        chat_inlineResultFile = ((Resources)???).getDrawable(2130837643);
        chat_inlineResultAudio = ((Resources)???).getDrawable(2130837649);
        chat_inlineResultLocation = ((Resources)???).getDrawable(2130837648);
        chat_msgInShadowDrawable = ((Resources)???).getDrawable(2130837932);
        chat_msgOutShadowDrawable = ((Resources)???).getDrawable(2130837936);
        chat_msgInMediaShadowDrawable = ((Resources)???).getDrawable(2130837942);
        chat_msgOutMediaShadowDrawable = ((Resources)???).getDrawable(2130837942);
        chat_botLinkDrawalbe = ((Resources)???).getDrawable(2130837647);
        chat_botInlineDrawable = ((Resources)???).getDrawable(2130837646);
        chat_timeBackgroundDrawable = ((Resources)???).getDrawable(2130838017);
        chat_timeStickerBackgroundDrawable = ((Resources)???).getDrawable(2130838016);
        chat_systemDrawable = ((Resources)???).getDrawable(2130838081);
        chat_contextResult_shadowUnderSwitchDrawable = ((Resources)???).getDrawable(2130837728).mutate();
        chat_attachButtonDrawables[0] = ((Resources)???).getDrawable(2130837597);
        chat_attachButtonDrawables[1] = ((Resources)???).getDrawable(2130837606);
        chat_attachButtonDrawables[2] = ((Resources)???).getDrawable(2130837620);
        chat_attachButtonDrawables[3] = ((Resources)???).getDrawable(2130837594);
        chat_attachButtonDrawables[4] = ((Resources)???).getDrawable(2130837603);
        chat_attachButtonDrawables[5] = ((Resources)???).getDrawable(2130837600);
        chat_attachButtonDrawables[6] = ((Resources)???).getDrawable(2130837613);
        chat_attachButtonDrawables[7] = ((Resources)???).getDrawable(2130837610);
        chat_cornerOuter[0] = ((Resources)???).getDrawable(2130837701);
        chat_cornerOuter[1] = ((Resources)???).getDrawable(2130837702);
        chat_cornerOuter[2] = ((Resources)???).getDrawable(2130837700);
        chat_cornerOuter[3] = ((Resources)???).getDrawable(2130837699);
        chat_cornerInner[0] = ((Resources)???).getDrawable(2130837698);
        chat_cornerInner[1] = ((Resources)???).getDrawable(2130837697);
        chat_cornerInner[2] = ((Resources)???).getDrawable(2130837696);
        chat_cornerInner[3] = ((Resources)???).getDrawable(2130837695);
        chat_shareDrawable = ((Resources)???).getDrawable(2130838060);
        chat_shareIconDrawable = ((Resources)???).getDrawable(2130838059);
        chat_fileStatesDrawable[0][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), 2130837954);
        chat_fileStatesDrawable[0][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), 2130837954);
        chat_fileStatesDrawable[1][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), 2130837951);
        chat_fileStatesDrawable[1][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), 2130837951);
        chat_fileStatesDrawable[2][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), 2130837949);
        chat_fileStatesDrawable[2][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), 2130837949);
        chat_fileStatesDrawable[3][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), 2130837946);
        chat_fileStatesDrawable[3][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), 2130837946);
        chat_fileStatesDrawable[4][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), 2130837944);
        chat_fileStatesDrawable[4][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), 2130837944);
        chat_fileStatesDrawable[5][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), 2130837954);
        chat_fileStatesDrawable[5][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), 2130837954);
        chat_fileStatesDrawable[6][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), 2130837951);
        chat_fileStatesDrawable[6][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), 2130837951);
        chat_fileStatesDrawable[7][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), 2130837949);
        chat_fileStatesDrawable[7][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), 2130837949);
        chat_fileStatesDrawable[8][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), 2130837946);
        chat_fileStatesDrawable[8][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), 2130837946);
        chat_fileStatesDrawable[9][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), 2130837944);
        chat_fileStatesDrawable[9][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), 2130837944);
        chat_photoStatesDrawables[0][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), 2130837949);
        chat_photoStatesDrawables[0][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), 2130837949);
        chat_photoStatesDrawables[1][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), 2130837944);
        chat_photoStatesDrawables[1][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), 2130837944);
        chat_photoStatesDrawables[2][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), 2130837947);
        chat_photoStatesDrawables[2][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), 2130837947);
        chat_photoStatesDrawables[3][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), 2130837954);
        chat_photoStatesDrawables[3][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), 2130837954);
        Drawable[] arrayOfDrawable1 = chat_photoStatesDrawables[4];
        Drawable[] arrayOfDrawable2 = chat_photoStatesDrawables[4];
        Drawable localDrawable = ((Resources)???).getDrawable(2130837657);
        arrayOfDrawable2[1] = localDrawable;
        arrayOfDrawable1[0] = localDrawable;
        arrayOfDrawable1 = chat_photoStatesDrawables[5];
        arrayOfDrawable2 = chat_photoStatesDrawables[5];
        localDrawable = ((Resources)???).getDrawable(2130837670);
        arrayOfDrawable2[1] = localDrawable;
        arrayOfDrawable1[0] = localDrawable;
        arrayOfDrawable1 = chat_photoStatesDrawables[6];
        arrayOfDrawable2 = chat_photoStatesDrawables[6];
        localDrawable = ((Resources)???).getDrawable(2130838014);
        arrayOfDrawable2[1] = localDrawable;
        arrayOfDrawable1[0] = localDrawable;
        chat_photoStatesDrawables[7][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), 2130837949);
        chat_photoStatesDrawables[7][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), 2130837949);
        chat_photoStatesDrawables[8][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), 2130837944);
        chat_photoStatesDrawables[8][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), 2130837944);
        chat_photoStatesDrawables[9][0] = ((Resources)???).getDrawable(2130837705).mutate();
        chat_photoStatesDrawables[9][1] = ((Resources)???).getDrawable(2130837705).mutate();
        chat_photoStatesDrawables[10][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), 2130837949);
        chat_photoStatesDrawables[10][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), 2130837949);
        chat_photoStatesDrawables[11][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), 2130837944);
        chat_photoStatesDrawables[11][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), 2130837944);
        chat_photoStatesDrawables[12][0] = ((Resources)???).getDrawable(2130837705).mutate();
        chat_photoStatesDrawables[12][1] = ((Resources)???).getDrawable(2130837705).mutate();
        chat_contactDrawable[0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), 2130837929);
        chat_contactDrawable[1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), 2130837929);
        chat_locationDrawable[0] = createRoundRectDrawableWithIcon(AndroidUtilities.dp(2.0F), 2130837934);
        chat_locationDrawable[1] = createRoundRectDrawableWithIcon(AndroidUtilities.dp(2.0F), 2130837934);
        chat_composeShadowDrawable = paramContext.getResources().getDrawable(2130837694);
        applyChatTheme(paramBoolean);
      }
      chat_msgTextPaintOneEmoji.setTextSize(AndroidUtilities.dp(28.0F));
      chat_msgTextPaintTwoEmoji.setTextSize(AndroidUtilities.dp(24.0F));
      chat_msgTextPaintThreeEmoji.setTextSize(AndroidUtilities.dp(20.0F));
      chat_msgTextPaint.setTextSize(AndroidUtilities.dp(MessagesController.getInstance().fontSize));
      chat_msgGameTextPaint.setTextSize(AndroidUtilities.dp(14.0F));
      chat_msgBotButtonPaint.setTextSize(AndroidUtilities.dp(15.0F));
      if ((!paramBoolean) && (chat_botProgressPaint != null))
      {
        chat_botProgressPaint.setStrokeWidth(AndroidUtilities.dp(2.0F));
        chat_infoPaint.setTextSize(AndroidUtilities.dp(12.0F));
        chat_docNamePaint.setTextSize(AndroidUtilities.dp(15.0F));
        chat_locationTitlePaint.setTextSize(AndroidUtilities.dp(15.0F));
        chat_locationAddressPaint.setTextSize(AndroidUtilities.dp(13.0F));
        chat_audioTimePaint.setTextSize(AndroidUtilities.dp(12.0F));
        chat_audioTitlePaint.setTextSize(AndroidUtilities.dp(16.0F));
        chat_audioPerformerPaint.setTextSize(AndroidUtilities.dp(15.0F));
        chat_botButtonPaint.setTextSize(AndroidUtilities.dp(15.0F));
        chat_contactNamePaint.setTextSize(AndroidUtilities.dp(15.0F));
        chat_contactPhonePaint.setTextSize(AndroidUtilities.dp(13.0F));
        chat_durationPaint.setTextSize(AndroidUtilities.dp(12.0F));
        chat_timePaint.setTextSize(AndroidUtilities.dp(12.0F));
        chat_namePaint.setTextSize(AndroidUtilities.dp(14.0F));
        chat_forwardNamePaint.setTextSize(AndroidUtilities.dp(14.0F));
        chat_replyNamePaint.setTextSize(AndroidUtilities.dp(14.0F));
        chat_replyTextPaint.setTextSize(AndroidUtilities.dp(14.0F));
        chat_gamePaint.setTextSize(AndroidUtilities.dp(13.0F));
        chat_shipmentPaint.setTextSize(AndroidUtilities.dp(13.0F));
        chat_instantViewPaint.setTextSize(AndroidUtilities.dp(13.0F));
        chat_instantViewRectPaint.setStrokeWidth(AndroidUtilities.dp(1.0F));
        chat_statusRecordPaint.setStrokeWidth(AndroidUtilities.dp(2.0F));
        chat_actionTextPaint.setTextSize(AndroidUtilities.dp(MessagesController.getInstance().fontSize - 2));
        chat_contextResult_titleTextPaint.setTextSize(AndroidUtilities.dp(15.0F));
        chat_contextResult_descriptionTextPaint.setTextSize(AndroidUtilities.dp(13.0F));
      }
      return;
    }
  }

  public static Drawable createCircleDrawable(int paramInt1, int paramInt2)
  {
    Object localObject = new OvalShape();
    ((OvalShape)localObject).resize(paramInt1, paramInt1);
    localObject = new ShapeDrawable((Shape)localObject);
    ((ShapeDrawable)localObject).getPaint().setColor(paramInt2);
    return (Drawable)localObject;
  }

  public static Drawable createCircleDrawableWithIcon(int paramInt1, int paramInt2)
  {
    Object localObject = new OvalShape();
    ((OvalShape)localObject).resize(paramInt1, paramInt1);
    localObject = new ShapeDrawable((Shape)localObject);
    ((ShapeDrawable)localObject).getPaint().setColor(-1);
    localObject = new CombinedDrawable((Drawable)localObject, ApplicationLoader.applicationContext.getResources().getDrawable(paramInt2).mutate());
    ((CombinedDrawable)localObject).setCustomSize(paramInt1, paramInt1);
    return (Drawable)localObject;
  }

  public static void createCommonResources(Context paramContext)
  {
    if (dividerPaint == null)
    {
      dividerPaint = new Paint();
      dividerPaint.setStrokeWidth(1.0F);
      avatar_backgroundPaint = new Paint(1);
      checkboxSquare_checkPaint = new Paint(1);
      checkboxSquare_checkPaint.setStyle(Paint.Style.STROKE);
      checkboxSquare_checkPaint.setStrokeWidth(AndroidUtilities.dp(2.0F));
      checkboxSquare_eraserPaint = new Paint(1);
      checkboxSquare_eraserPaint.setColor(0);
      checkboxSquare_eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
      checkboxSquare_backgroundPaint = new Paint(1);
      linkSelectionPaint = new Paint();
      paramContext = paramContext.getResources();
      avatar_broadcastDrawable = paramContext.getDrawable(2130837653);
      avatar_photoDrawable = paramContext.getDrawable(2130838011);
      applyCommonTheme();
    }
  }

  public static void createDialogsResources(Context paramContext)
  {
    createCommonResources(paramContext);
    if (dialogs_namePaint == null)
    {
      paramContext = paramContext.getResources();
      dialogs_namePaint = new TextPaint(1);
      dialogs_namePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      dialogs_nameEncryptedPaint = new TextPaint(1);
      dialogs_nameEncryptedPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      dialogs_messagePaint = new TextPaint(1);
      dialogs_messagePrintingPaint = new TextPaint(1);
      dialogs_timePaint = new TextPaint(1);
      dialogs_countTextPaint = new TextPaint(1);
      dialogs_countTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      dialogs_onlinePaint = new TextPaint(1);
      dialogs_offlinePaint = new TextPaint(1);
      dialogs_tabletSeletedPaint = new Paint();
      dialogs_pinnedPaint = new Paint();
      dialogs_countPaint = new Paint(1);
      dialogs_countGrayPaint = new Paint(1);
      dialogs_errorPaint = new Paint(1);
      dialogs_lockDrawable = paramContext.getDrawable(2130837894);
      dialogs_checkDrawable = paramContext.getDrawable(2130837887);
      dialogs_halfCheckDrawable = paramContext.getDrawable(2130837891);
      dialogs_clockDrawable = paramContext.getDrawable(2130837928).mutate();
      dialogs_errorDrawable = paramContext.getDrawable(2130837896);
      dialogs_groupDrawable = paramContext.getDrawable(2130837890);
      dialogs_broadcastDrawable = paramContext.getDrawable(2130837886);
      dialogs_muteDrawable = paramContext.getDrawable(2130837892).mutate();
      dialogs_verifiedDrawable = paramContext.getDrawable(2130838110);
      dialogs_verifiedCheckDrawable = paramContext.getDrawable(2130838111);
      dialogs_botDrawable = paramContext.getDrawable(2130837885);
      dialogs_pinnedDrawable = paramContext.getDrawable(2130837893);
      applyDialogsTheme();
    }
    dialogs_namePaint.setTextSize(AndroidUtilities.dp(17.0F));
    dialogs_nameEncryptedPaint.setTextSize(AndroidUtilities.dp(17.0F));
    dialogs_messagePaint.setTextSize(AndroidUtilities.dp(16.0F));
    dialogs_messagePrintingPaint.setTextSize(AndroidUtilities.dp(16.0F));
    dialogs_timePaint.setTextSize(AndroidUtilities.dp(13.0F));
    dialogs_countTextPaint.setTextSize(AndroidUtilities.dp(13.0F));
    dialogs_onlinePaint.setTextSize(AndroidUtilities.dp(16.0F));
    dialogs_offlinePaint.setTextSize(AndroidUtilities.dp(16.0F));
  }

  public static Drawable createEditTextDrawable(Context paramContext, boolean paramBoolean)
  {
    Object localObject = paramContext.getResources();
    Drawable localDrawable = ((Resources)localObject).getDrawable(2130838048).mutate();
    if (paramBoolean)
    {
      paramContext = "dialogInputField";
      localDrawable.setColorFilter(new PorterDuffColorFilter(getColor(paramContext), PorterDuff.Mode.MULTIPLY));
      localObject = ((Resources)localObject).getDrawable(2130838049).mutate();
      if (!paramBoolean)
        break label138;
    }
    label138: for (paramContext = "dialogInputFieldActivated"; ; paramContext = "windowBackgroundWhiteInputFieldActivated")
    {
      ((Drawable)localObject).setColorFilter(new PorterDuffColorFilter(getColor(paramContext), PorterDuff.Mode.MULTIPLY));
      paramContext = new StateListDrawable()
      {
        public boolean selectDrawable(int paramInt)
        {
          if (Build.VERSION.SDK_INT < 21)
          {
            Drawable localDrawable = Theme.access$000(this, paramInt);
            ColorFilter localColorFilter = null;
            if ((localDrawable instanceof BitmapDrawable))
              localColorFilter = ((BitmapDrawable)localDrawable).getPaint().getColorFilter();
            while (true)
            {
              boolean bool = super.selectDrawable(paramInt);
              if (localColorFilter != null)
                localDrawable.setColorFilter(localColorFilter);
              return bool;
              if (!(localDrawable instanceof NinePatchDrawable))
                continue;
              localColorFilter = ((NinePatchDrawable)localDrawable).getPaint().getColorFilter();
            }
          }
          return super.selectDrawable(paramInt);
        }
      };
      paramContext.addState(new int[] { 16842910, 16842908 }, (Drawable)localObject);
      paramContext.addState(new int[] { 16842908 }, (Drawable)localObject);
      paramContext.addState(StateSet.WILD_CARD, localDrawable);
      return paramContext;
      paramContext = "windowBackgroundWhiteInputField";
      break;
    }
  }

  public static Drawable createEmojiIconSelectorDrawable(Context paramContext, int paramInt1, int paramInt2, int paramInt3)
  {
    Object localObject = paramContext.getResources();
    paramContext = ((Resources)localObject).getDrawable(paramInt1).mutate();
    if (paramInt2 != 0)
      paramContext.setColorFilter(new PorterDuffColorFilter(paramInt2, PorterDuff.Mode.MULTIPLY));
    localObject = ((Resources)localObject).getDrawable(paramInt1).mutate();
    if (paramInt3 != 0)
      ((Drawable)localObject).setColorFilter(new PorterDuffColorFilter(paramInt3, PorterDuff.Mode.MULTIPLY));
    1 local1 = new StateListDrawable()
    {
      public boolean selectDrawable(int paramInt)
      {
        if (Build.VERSION.SDK_INT < 21)
        {
          Drawable localDrawable = Theme.access$000(this, paramInt);
          ColorFilter localColorFilter = null;
          if ((localDrawable instanceof BitmapDrawable))
            localColorFilter = ((BitmapDrawable)localDrawable).getPaint().getColorFilter();
          while (true)
          {
            boolean bool = super.selectDrawable(paramInt);
            if (localColorFilter != null)
              localDrawable.setColorFilter(localColorFilter);
            return bool;
            if (!(localDrawable instanceof NinePatchDrawable))
              continue;
            localColorFilter = ((NinePatchDrawable)localDrawable).getPaint().getColorFilter();
          }
        }
        return super.selectDrawable(paramInt);
      }
    };
    local1.setEnterFadeDuration(1);
    local1.setExitFadeDuration(200);
    local1.addState(new int[] { 16842913 }, (Drawable)localObject);
    local1.addState(new int[0], paramContext);
    return (Drawable)local1;
  }

  public static void createProfileResources(Context paramContext)
  {
    if (profile_verifiedDrawable == null)
    {
      profile_aboutTextPaint = new TextPaint(1);
      paramContext = paramContext.getResources();
      profile_verifiedDrawable = paramContext.getDrawable(2130838110).mutate();
      profile_verifiedCheckDrawable = paramContext.getDrawable(2130838111).mutate();
      applyProfileTheme();
    }
    profile_aboutTextPaint.setTextSize(AndroidUtilities.dp(16.0F));
  }

  public static Drawable createRoundRectDrawable(int paramInt1, int paramInt2)
  {
    ShapeDrawable localShapeDrawable = new ShapeDrawable(new RoundRectShape(new float[] { paramInt1, paramInt1, paramInt1, paramInt1, paramInt1, paramInt1, paramInt1, paramInt1 }, null, null));
    localShapeDrawable.getPaint().setColor(paramInt2);
    return localShapeDrawable;
  }

  public static Drawable createRoundRectDrawableWithIcon(int paramInt1, int paramInt2)
  {
    ShapeDrawable localShapeDrawable = new ShapeDrawable(new RoundRectShape(new float[] { paramInt1, paramInt1, paramInt1, paramInt1, paramInt1, paramInt1, paramInt1, paramInt1 }, null, null));
    localShapeDrawable.getPaint().setColor(-1);
    return new CombinedDrawable(localShapeDrawable, ApplicationLoader.applicationContext.getResources().getDrawable(paramInt2).mutate());
  }

  public static Drawable createSelectorDrawable(int paramInt)
  {
    return createSelectorDrawable(paramInt, 1);
  }

  public static Drawable createSelectorDrawable(int paramInt1, int paramInt2)
  {
    Object localObject;
    if (Build.VERSION.SDK_INT >= 21)
      if (paramInt2 == 1)
      {
        maskPaint.setColor(-1);
        localObject = new Drawable()
        {
          public void draw(Canvas paramCanvas)
          {
            Rect localRect = getBounds();
            paramCanvas.drawCircle(localRect.centerX(), localRect.centerY(), AndroidUtilities.dp(18.0F), Theme.maskPaint);
          }

          public int getOpacity()
          {
            return 0;
          }

          public void setAlpha(int paramInt)
          {
          }

          public void setColorFilter(ColorFilter paramColorFilter)
          {
          }
        };
      }
    while (true)
    {
      return new RippleDrawable(new ColorStateList(new int[][] { StateSet.WILD_CARD }, new int[] { paramInt1 }), null, (Drawable)localObject);
      if (paramInt2 == 2)
      {
        localObject = new ColorDrawable(-1);
        continue;
        localObject = new StateListDrawable();
        ColorDrawable localColorDrawable = new ColorDrawable(paramInt1);
        ((StateListDrawable)localObject).addState(new int[] { 16842919 }, localColorDrawable);
        localColorDrawable = new ColorDrawable(paramInt1);
        ((StateListDrawable)localObject).addState(new int[] { 16842913 }, localColorDrawable);
        ((StateListDrawable)localObject).addState(StateSet.WILD_CARD, new ColorDrawable(0));
        return localObject;
      }
      localObject = null;
    }
  }

  public static Drawable createSimpleSelectorCircleDrawable(int paramInt1, int paramInt2, int paramInt3)
  {
    Object localObject = new OvalShape();
    ((OvalShape)localObject).resize(paramInt1, paramInt1);
    ShapeDrawable localShapeDrawable = new ShapeDrawable((Shape)localObject);
    localShapeDrawable.getPaint().setColor(paramInt2);
    localObject = new ShapeDrawable((Shape)localObject);
    if (Build.VERSION.SDK_INT >= 21)
    {
      ((ShapeDrawable)localObject).getPaint().setColor(-1);
      return new RippleDrawable(new ColorStateList(new int[][] { StateSet.WILD_CARD }, new int[] { paramInt3 }), localShapeDrawable, (Drawable)localObject);
    }
    ((ShapeDrawable)localObject).getPaint().setColor(paramInt3);
    StateListDrawable localStateListDrawable = new StateListDrawable();
    localStateListDrawable.addState(new int[] { 16842919 }, (Drawable)localObject);
    localStateListDrawable.addState(new int[] { 16842908 }, (Drawable)localObject);
    localStateListDrawable.addState(StateSet.WILD_CARD, localShapeDrawable);
    return (Drawable)localStateListDrawable;
  }

  public static Drawable createSimpleSelectorDrawable(Context paramContext, int paramInt1, int paramInt2, int paramInt3)
  {
    Object localObject = paramContext.getResources();
    paramContext = ((Resources)localObject).getDrawable(paramInt1).mutate();
    if (paramInt2 != 0)
      paramContext.setColorFilter(new PorterDuffColorFilter(paramInt2, PorterDuff.Mode.MULTIPLY));
    localObject = ((Resources)localObject).getDrawable(paramInt1).mutate();
    if (paramInt3 != 0)
      ((Drawable)localObject).setColorFilter(new PorterDuffColorFilter(paramInt3, PorterDuff.Mode.MULTIPLY));
    3 local3 = new StateListDrawable()
    {
      public boolean selectDrawable(int paramInt)
      {
        if (Build.VERSION.SDK_INT < 21)
        {
          Drawable localDrawable = Theme.access$000(this, paramInt);
          ColorFilter localColorFilter = null;
          if ((localDrawable instanceof BitmapDrawable))
            localColorFilter = ((BitmapDrawable)localDrawable).getPaint().getColorFilter();
          while (true)
          {
            boolean bool = super.selectDrawable(paramInt);
            if (localColorFilter != null)
              localDrawable.setColorFilter(localColorFilter);
            return bool;
            if (!(localDrawable instanceof NinePatchDrawable))
              continue;
            localColorFilter = ((NinePatchDrawable)localDrawable).getPaint().getColorFilter();
          }
        }
        return super.selectDrawable(paramInt);
      }
    };
    local3.addState(new int[] { 16842919 }, (Drawable)localObject);
    local3.addState(new int[] { 16842913 }, (Drawable)localObject);
    local3.addState(StateSet.WILD_CARD, paramContext);
    return (Drawable)local3;
  }

  public static Drawable createSimpleSelectorRoundRectDrawable(int paramInt1, int paramInt2, int paramInt3)
  {
    ShapeDrawable localShapeDrawable1 = new ShapeDrawable(new RoundRectShape(new float[] { paramInt1, paramInt1, paramInt1, paramInt1, paramInt1, paramInt1, paramInt1, paramInt1 }, null, null));
    localShapeDrawable1.getPaint().setColor(paramInt2);
    ShapeDrawable localShapeDrawable2 = new ShapeDrawable(new RoundRectShape(new float[] { paramInt1, paramInt1, paramInt1, paramInt1, paramInt1, paramInt1, paramInt1, paramInt1 }, null, null));
    localShapeDrawable2.getPaint().setColor(paramInt3);
    StateListDrawable localStateListDrawable = new StateListDrawable();
    localStateListDrawable.addState(new int[] { 16842919 }, localShapeDrawable2);
    localStateListDrawable.addState(new int[] { 16842913 }, localShapeDrawable2);
    localStateListDrawable.addState(StateSet.WILD_CARD, localShapeDrawable1);
    return localStateListDrawable;
  }

  public static boolean deleteTheme(ThemeInfo paramThemeInfo)
  {
    int i = 1;
    if (paramThemeInfo.pathToFile == null)
      return false;
    if (currentTheme == paramThemeInfo)
      applyTheme(defaultTheme, true, false);
    while (true)
    {
      otherThemes.remove(paramThemeInfo);
      themesDict.remove(paramThemeInfo.name);
      themes.remove(paramThemeInfo);
      new File(paramThemeInfo.pathToFile).delete();
      saveOtherThemes();
      return i;
      i = 0;
    }
  }

  public static void destroyResources()
  {
    int i = 0;
    while (i < chat_attachButtonDrawables.length)
    {
      if (chat_attachButtonDrawables[i] != null)
        chat_attachButtonDrawables[i].setCallback(null);
      i += 1;
    }
  }

  // ERROR //
  public static File getAssetFile(String paramString)
  {
    // Byte code:
    //   0: new 2522	java/io/File
    //   3: dup
    //   4: invokestatic 2535	org/vidogram/messenger/ApplicationLoader:getFilesDirFixed	()Ljava/io/File;
    //   7: aload_0
    //   8: invokespecial 2538	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   11: astore_3
    //   12: aload_3
    //   13: invokevirtual 2901	java/io/File:exists	()Z
    //   16: ifne +36 -> 52
    //   19: aconst_null
    //   20: astore_2
    //   21: aconst_null
    //   22: astore_1
    //   23: getstatic 2084	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   26: invokevirtual 2905	android/content/Context:getAssets	()Landroid/content/res/AssetManager;
    //   29: aload_0
    //   30: invokevirtual 2911	android/content/res/AssetManager:open	(Ljava/lang/String;)Ljava/io/InputStream;
    //   33: astore_0
    //   34: aload_0
    //   35: astore_1
    //   36: aload_0
    //   37: astore_2
    //   38: aload_0
    //   39: aload_3
    //   40: invokestatic 2914	org/vidogram/messenger/AndroidUtilities:copyFile	(Ljava/io/InputStream;Ljava/io/File;)Z
    //   43: pop
    //   44: aload_0
    //   45: ifnull +7 -> 52
    //   48: aload_0
    //   49: invokevirtual 2919	java/io/InputStream:close	()V
    //   52: aload_3
    //   53: areturn
    //   54: astore_0
    //   55: aload_1
    //   56: astore_2
    //   57: aload_0
    //   58: invokestatic 2128	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   61: aload_1
    //   62: ifnull -10 -> 52
    //   65: aload_1
    //   66: invokevirtual 2919	java/io/InputStream:close	()V
    //   69: aload_3
    //   70: areturn
    //   71: astore_0
    //   72: aload_3
    //   73: areturn
    //   74: astore_0
    //   75: aload_2
    //   76: ifnull +7 -> 83
    //   79: aload_2
    //   80: invokevirtual 2919	java/io/InputStream:close	()V
    //   83: aload_0
    //   84: athrow
    //   85: astore_0
    //   86: aload_3
    //   87: areturn
    //   88: astore_1
    //   89: goto -6 -> 83
    //
    // Exception table:
    //   from	to	target	type
    //   23	34	54	java/lang/Exception
    //   38	44	54	java/lang/Exception
    //   65	69	71	java/lang/Exception
    //   23	34	74	finally
    //   38	44	74	finally
    //   57	61	74	finally
    //   48	52	85	java/lang/Exception
    //   79	83	88	java/lang/Exception
  }

  public static Drawable getCachedWallpaper()
  {
    synchronized (wallpaperSync)
    {
      if (themedWallpaper != null)
      {
        localDrawable = themedWallpaper;
        return localDrawable;
      }
      Drawable localDrawable = wallpaper;
      return localDrawable;
    }
  }

  public static int getColor(String paramString)
  {
    return getColor(paramString, null);
  }

  public static int getColor(String paramString, boolean[] paramArrayOfBoolean)
  {
    Integer localInteger = (Integer)currentColors.get(paramString);
    if (localInteger == null)
    {
      if (paramArrayOfBoolean != null)
        paramArrayOfBoolean[0] = true;
      if (paramString.equals("chat_serviceBackground"))
        return serviceMessageColor;
      if (paramString.equals("chat_serviceBackgroundSelected"))
        return serviceSelectedMessageColor;
      return getDefaultColor(paramString);
    }
    return localInteger.intValue();
  }

  public static Integer getColorOrNull(String paramString)
  {
    Integer localInteger2 = (Integer)currentColors.get(paramString);
    Integer localInteger1 = localInteger2;
    if (localInteger2 == null)
      localInteger1 = (Integer)defaultColors.get(paramString);
    return localInteger1;
  }

  public static ThemeInfo getCurrentTheme()
  {
    if (currentTheme != null)
      return currentTheme;
    return defaultTheme;
  }

  public static String getCurrentThemeName()
  {
    String str2 = currentTheme.name;
    String str1 = str2;
    if (str2.endsWith(".attheme"))
      str1 = str2.substring(0, str2.lastIndexOf('.'));
    return str1;
  }

  public static int getDefaultColor(String paramString)
  {
    Integer localInteger = (Integer)defaultColors.get(paramString);
    if (localInteger == null)
    {
      if (paramString.equals("chats_menuTopShadow"))
        return 0;
      return -65536;
    }
    return localInteger.intValue();
  }

  public static HashMap<String, Integer> getDefaultColors()
  {
    return defaultColors;
  }

  public static Drawable getRoundRectSelectorDrawable()
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      localObject1 = createRoundRectDrawable(AndroidUtilities.dp(3.0F), -1);
      localObject2 = StateSet.WILD_CARD;
      int i = getColor("dialogButtonSelector");
      return new RippleDrawable(new ColorStateList(new int[][] { localObject2 }, new int[] { i }), null, (Drawable)localObject1);
    }
    Object localObject1 = new StateListDrawable();
    Object localObject2 = createRoundRectDrawable(AndroidUtilities.dp(3.0F), getColor("dialogButtonSelector"));
    ((StateListDrawable)localObject1).addState(new int[] { 16842919 }, (Drawable)localObject2);
    localObject2 = createRoundRectDrawable(AndroidUtilities.dp(3.0F), getColor("dialogButtonSelector"));
    ((StateListDrawable)localObject1).addState(new int[] { 16842913 }, (Drawable)localObject2);
    ((StateListDrawable)localObject1).addState(StateSet.WILD_CARD, new ColorDrawable(0));
    return (Drawable)(Drawable)localObject1;
  }

  public static int getSelectedColor()
  {
    return selectedColor;
  }

  public static Drawable getSelectorDrawable(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      if (Build.VERSION.SDK_INT >= 21)
      {
        localObject1 = new ColorDrawable(-1);
        localObject2 = StateSet.WILD_CARD;
        i = getColor("listSelectorSDK21");
        return new RippleDrawable(new ColorStateList(new int[][] { localObject2 }, new int[] { i }), new ColorDrawable(getColor("windowBackgroundWhite")), (Drawable)localObject1);
      }
      int i = getColor("listSelectorSDK21");
      Object localObject1 = new StateListDrawable();
      Object localObject2 = new ColorDrawable(i);
      ((StateListDrawable)localObject1).addState(new int[] { 16842919 }, (Drawable)localObject2);
      localObject2 = new ColorDrawable(i);
      ((StateListDrawable)localObject1).addState(new int[] { 16842913 }, (Drawable)localObject2);
      ((StateListDrawable)localObject1).addState(StateSet.WILD_CARD, new ColorDrawable(getColor("windowBackgroundWhite")));
      return localObject1;
    }
    return (Drawable)(Drawable)createSelectorDrawable(getColor("listSelectorSDK21"), 2);
  }

  public static int getServiceMessageColor()
  {
    Integer localInteger = (Integer)currentColors.get("chat_serviceBackground");
    if (localInteger == null)
      return serviceMessageColor;
    return localInteger.intValue();
  }

  private static Drawable getStateDrawable(Drawable paramDrawable, int paramInt)
  {
    if (StateListDrawable_getStateDrawableMethod == null);
    try
    {
      StateListDrawable_getStateDrawableMethod = StateListDrawable.class.getDeclaredMethod("getStateDrawable", new Class[] { Integer.TYPE });
      label28: if (StateListDrawable_getStateDrawableMethod == null)
        return null;
      try
      {
        paramDrawable = (Drawable)StateListDrawable_getStateDrawableMethod.invoke(paramDrawable, new Object[] { Integer.valueOf(paramInt) });
        return paramDrawable;
      }
      catch (Exception paramDrawable)
      {
        return null;
      }
    }
    catch (Throwable localThrowable)
    {
      break label28;
    }
  }

  // ERROR //
  private static HashMap<String, Integer> getThemeFileValues(File paramFile, String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 11
    //   3: new 1800	java/util/HashMap
    //   6: dup
    //   7: invokespecial 1801	java/util/HashMap:<init>	()V
    //   10: astore 12
    //   12: sipush 1024
    //   15: newarray byte
    //   17: astore 13
    //   19: iconst_0
    //   20: istore_2
    //   21: aload_1
    //   22: ifnull +8 -> 30
    //   25: aload_1
    //   26: invokestatic 2981	org/vidogram/ui/ActionBar/Theme:getAssetFile	(Ljava/lang/String;)Ljava/io/File;
    //   29: astore_0
    //   30: new 2983	java/io/FileInputStream
    //   33: dup
    //   34: aload_0
    //   35: invokespecial 2986	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   38: astore_1
    //   39: iconst_0
    //   40: istore_3
    //   41: iconst_m1
    //   42: putstatic 2196	org/vidogram/ui/ActionBar/Theme:themedWallpaperFileOffset	I
    //   45: aload_1
    //   46: aload 13
    //   48: invokevirtual 2990	java/io/FileInputStream:read	([B)I
    //   51: istore 9
    //   53: iload 9
    //   55: iconst_m1
    //   56: if_icmpeq +93 -> 149
    //   59: iconst_0
    //   60: istore 6
    //   62: iconst_0
    //   63: istore 7
    //   65: iload_2
    //   66: istore 4
    //   68: iload 6
    //   70: iload 9
    //   72: if_icmpge +297 -> 369
    //   75: iload 7
    //   77: istore 8
    //   79: iload 4
    //   81: istore 5
    //   83: aload 13
    //   85: iload 6
    //   87: baload
    //   88: bipush 10
    //   90: if_icmpne +296 -> 386
    //   93: iload 6
    //   95: iload 7
    //   97: isub
    //   98: iconst_1
    //   99: iadd
    //   100: istore 10
    //   102: new 1784	java/lang/String
    //   105: dup
    //   106: aload 13
    //   108: iload 7
    //   110: iload 10
    //   112: iconst_1
    //   113: isub
    //   114: ldc_w 2992
    //   117: invokespecial 2995	java/lang/String:<init>	([BIILjava/lang/String;)V
    //   120: astore 11
    //   122: aload 11
    //   124: ldc_w 2997
    //   127: invokevirtual 3000	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   130: ifeq +30 -> 160
    //   133: iload 4
    //   135: iload 10
    //   137: iadd
    //   138: putstatic 2196	org/vidogram/ui/ActionBar/Theme:themedWallpaperFileOffset	I
    //   141: iconst_1
    //   142: istore_3
    //   143: iload_2
    //   144: iload 4
    //   146: if_icmpne +122 -> 268
    //   149: aload_1
    //   150: ifnull +7 -> 157
    //   153: aload_1
    //   154: invokevirtual 3001	java/io/FileInputStream:close	()V
    //   157: aload 12
    //   159: areturn
    //   160: aload 11
    //   162: bipush 61
    //   164: invokevirtual 3004	java/lang/String:indexOf	(I)I
    //   167: istore 5
    //   169: iload 5
    //   171: iconst_m1
    //   172: if_icmpeq +200 -> 372
    //   175: aload 11
    //   177: iconst_0
    //   178: iload 5
    //   180: invokevirtual 2944	java/lang/String:substring	(II)Ljava/lang/String;
    //   183: astore_0
    //   184: aload 11
    //   186: iload 5
    //   188: iconst_1
    //   189: iadd
    //   190: invokevirtual 3007	java/lang/String:substring	(I)Ljava/lang/String;
    //   193: astore 11
    //   195: aload 11
    //   197: invokevirtual 3008	java/lang/String:length	()I
    //   200: ifle +55 -> 255
    //   203: aload 11
    //   205: iconst_0
    //   206: invokevirtual 3012	java/lang/String:charAt	(I)C
    //   209: istore 5
    //   211: iload 5
    //   213: bipush 35
    //   215: if_icmpne +40 -> 255
    //   218: aload 11
    //   220: invokestatic 3017	android/graphics/Color:parseColor	(Ljava/lang/String;)I
    //   223: istore 5
    //   225: aload 12
    //   227: aload_0
    //   228: iload 5
    //   230: invokestatic 1809	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   233: invokevirtual 1813	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   236: pop
    //   237: goto +135 -> 372
    //   240: astore 14
    //   242: aload 11
    //   244: invokestatic 3022	org/vidogram/messenger/Utilities:parseInt	(Ljava/lang/String;)Ljava/lang/Integer;
    //   247: invokevirtual 2218	java/lang/Integer:intValue	()I
    //   250: istore 5
    //   252: goto -27 -> 225
    //   255: aload 11
    //   257: invokestatic 3022	org/vidogram/messenger/Utilities:parseInt	(Ljava/lang/String;)Ljava/lang/Integer;
    //   260: invokevirtual 2218	java/lang/Integer:intValue	()I
    //   263: istore 5
    //   265: goto -40 -> 225
    //   268: aload_1
    //   269: invokevirtual 3026	java/io/FileInputStream:getChannel	()Ljava/nio/channels/FileChannel;
    //   272: iload 4
    //   274: i2l
    //   275: invokevirtual 3032	java/nio/channels/FileChannel:position	(J)Ljava/nio/channels/FileChannel;
    //   278: pop
    //   279: iload_3
    //   280: ifne -131 -> 149
    //   283: iload 4
    //   285: istore_2
    //   286: goto -241 -> 45
    //   289: astore_0
    //   290: aload_0
    //   291: invokestatic 2128	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   294: aload 12
    //   296: areturn
    //   297: astore_1
    //   298: aload 11
    //   300: astore_0
    //   301: aload_1
    //   302: invokestatic 2128	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   305: aload_0
    //   306: ifnull -149 -> 157
    //   309: aload_0
    //   310: invokevirtual 3001	java/io/FileInputStream:close	()V
    //   313: aload 12
    //   315: areturn
    //   316: astore_0
    //   317: aload_0
    //   318: invokestatic 2128	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   321: aload 12
    //   323: areturn
    //   324: astore_0
    //   325: aconst_null
    //   326: astore_1
    //   327: aload_1
    //   328: ifnull +7 -> 335
    //   331: aload_1
    //   332: invokevirtual 3001	java/io/FileInputStream:close	()V
    //   335: aload_0
    //   336: athrow
    //   337: astore_1
    //   338: aload_1
    //   339: invokestatic 2128	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   342: goto -7 -> 335
    //   345: astore_0
    //   346: goto -19 -> 327
    //   349: astore 11
    //   351: aload_0
    //   352: astore_1
    //   353: aload 11
    //   355: astore_0
    //   356: goto -29 -> 327
    //   359: astore 11
    //   361: aload_1
    //   362: astore_0
    //   363: aload 11
    //   365: astore_1
    //   366: goto -65 -> 301
    //   369: goto -226 -> 143
    //   372: iload 7
    //   374: iload 10
    //   376: iadd
    //   377: istore 8
    //   379: iload 4
    //   381: iload 10
    //   383: iadd
    //   384: istore 5
    //   386: iload 6
    //   388: iconst_1
    //   389: iadd
    //   390: istore 6
    //   392: iload 8
    //   394: istore 7
    //   396: iload 5
    //   398: istore 4
    //   400: goto -332 -> 68
    //
    // Exception table:
    //   from	to	target	type
    //   218	225	240	java/lang/Exception
    //   153	157	289	java/lang/Exception
    //   12	19	297	java/lang/Throwable
    //   25	30	297	java/lang/Throwable
    //   30	39	297	java/lang/Throwable
    //   309	313	316	java/lang/Exception
    //   12	19	324	finally
    //   25	30	324	finally
    //   30	39	324	finally
    //   331	335	337	java/lang/Exception
    //   41	45	345	finally
    //   45	53	345	finally
    //   102	141	345	finally
    //   160	169	345	finally
    //   175	211	345	finally
    //   218	225	345	finally
    //   225	237	345	finally
    //   242	252	345	finally
    //   255	265	345	finally
    //   268	279	345	finally
    //   301	305	349	finally
    //   41	45	359	java/lang/Throwable
    //   45	53	359	java/lang/Throwable
    //   102	141	359	java/lang/Throwable
    //   160	169	359	java/lang/Throwable
    //   175	211	359	java/lang/Throwable
    //   218	225	359	java/lang/Throwable
    //   225	237	359	java/lang/Throwable
    //   242	252	359	java/lang/Throwable
    //   255	265	359	java/lang/Throwable
    //   268	279	359	java/lang/Throwable
  }

  public static Drawable getThemedDrawable(Context paramContext, int paramInt, String paramString)
  {
    paramContext = paramContext.getResources().getDrawable(paramInt).mutate();
    paramContext.setColorFilter(new PorterDuffColorFilter(getColor(paramString), PorterDuff.Mode.MULTIPLY));
    return paramContext;
  }

  // ERROR //
  public static Drawable getThemedWallpaper(boolean paramBoolean)
  {
    // Byte code:
    //   0: getstatic 2046	org/vidogram/ui/ActionBar/Theme:currentColors	Ljava/util/HashMap;
    //   3: ldc_w 1111
    //   6: invokevirtual 2139	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   9: checkcast 1805	java/lang/Integer
    //   12: astore 6
    //   14: aload 6
    //   16: ifnull +20 -> 36
    //   19: new 2874	android/graphics/drawable/ColorDrawable
    //   22: dup
    //   23: aload 6
    //   25: invokevirtual 2218	java/lang/Integer:intValue	()I
    //   28: invokespecial 2875	android/graphics/drawable/ColorDrawable:<init>	(I)V
    //   31: astore 6
    //   33: aload 6
    //   35: areturn
    //   36: getstatic 2196	org/vidogram/ui/ActionBar/Theme:themedWallpaperFileOffset	I
    //   39: ifle +208 -> 247
    //   42: getstatic 2063	org/vidogram/ui/ActionBar/Theme:currentTheme	Lorg/vidogram/ui/ActionBar/Theme$ThemeInfo;
    //   45: getfield 2499	org/vidogram/ui/ActionBar/Theme$ThemeInfo:pathToFile	Ljava/lang/String;
    //   48: ifnonnull +12 -> 60
    //   51: getstatic 2063	org/vidogram/ui/ActionBar/Theme:currentTheme	Lorg/vidogram/ui/ActionBar/Theme$ThemeInfo;
    //   54: getfield 2074	org/vidogram/ui/ActionBar/Theme$ThemeInfo:assetName	Ljava/lang/String;
    //   57: ifnull +190 -> 247
    //   60: getstatic 2063	org/vidogram/ui/ActionBar/Theme:currentTheme	Lorg/vidogram/ui/ActionBar/Theme$ThemeInfo;
    //   63: getfield 2074	org/vidogram/ui/ActionBar/Theme$ThemeInfo:assetName	Ljava/lang/String;
    //   66: ifnull +139 -> 205
    //   69: getstatic 2063	org/vidogram/ui/ActionBar/Theme:currentTheme	Lorg/vidogram/ui/ActionBar/Theme$ThemeInfo;
    //   72: getfield 2074	org/vidogram/ui/ActionBar/Theme$ThemeInfo:assetName	Ljava/lang/String;
    //   75: invokestatic 2981	org/vidogram/ui/ActionBar/Theme:getAssetFile	(Ljava/lang/String;)Ljava/io/File;
    //   78: astore 6
    //   80: new 2983	java/io/FileInputStream
    //   83: dup
    //   84: aload 6
    //   86: invokespecial 2986	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   89: astore 7
    //   91: aload 7
    //   93: astore 6
    //   95: aload 7
    //   97: invokevirtual 3026	java/io/FileInputStream:getChannel	()Ljava/nio/channels/FileChannel;
    //   100: getstatic 2196	org/vidogram/ui/ActionBar/Theme:themedWallpaperFileOffset	I
    //   103: i2l
    //   104: invokevirtual 3032	java/nio/channels/FileChannel:position	(J)Ljava/nio/channels/FileChannel;
    //   107: pop
    //   108: aload 7
    //   110: astore 6
    //   112: new 3038	android/graphics/BitmapFactory$Options
    //   115: dup
    //   116: invokespecial 3039	android/graphics/BitmapFactory$Options:<init>	()V
    //   119: astore 8
    //   121: iload_0
    //   122: ifeq +127 -> 249
    //   125: aload 7
    //   127: astore 6
    //   129: aload 8
    //   131: iconst_1
    //   132: putfield 3042	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
    //   135: aload 7
    //   137: astore 6
    //   139: aload 8
    //   141: getfield 3045	android/graphics/BitmapFactory$Options:outWidth	I
    //   144: i2f
    //   145: fstore_2
    //   146: aload 7
    //   148: astore 6
    //   150: aload 8
    //   152: getfield 3048	android/graphics/BitmapFactory$Options:outHeight	I
    //   155: i2f
    //   156: fstore_1
    //   157: aload 7
    //   159: astore 6
    //   161: ldc_w 3049
    //   164: invokestatic 2709	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   167: istore 5
    //   169: iconst_1
    //   170: istore_3
    //   171: fload_2
    //   172: iload 5
    //   174: i2f
    //   175: fcmpl
    //   176: ifgt +14 -> 190
    //   179: iload_3
    //   180: istore 4
    //   182: fload_1
    //   183: iload 5
    //   185: i2f
    //   186: fcmpl
    //   187: ifle +65 -> 252
    //   190: fload_2
    //   191: fconst_2
    //   192: fdiv
    //   193: fstore_2
    //   194: fload_1
    //   195: fconst_2
    //   196: fdiv
    //   197: fstore_1
    //   198: iload_3
    //   199: iconst_2
    //   200: imul
    //   201: istore_3
    //   202: goto -31 -> 171
    //   205: new 2522	java/io/File
    //   208: dup
    //   209: getstatic 2063	org/vidogram/ui/ActionBar/Theme:currentTheme	Lorg/vidogram/ui/ActionBar/Theme$ThemeInfo;
    //   212: getfield 2499	org/vidogram/ui/ActionBar/Theme$ThemeInfo:pathToFile	Ljava/lang/String;
    //   215: invokespecial 2523	java/io/File:<init>	(Ljava/lang/String;)V
    //   218: astore 6
    //   220: goto -140 -> 80
    //   223: astore 8
    //   225: aconst_null
    //   226: astore 7
    //   228: aload 7
    //   230: astore 6
    //   232: aload 8
    //   234: invokestatic 2128	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   237: aload 7
    //   239: ifnull +8 -> 247
    //   242: aload 7
    //   244: invokevirtual 3001	java/io/FileInputStream:close	()V
    //   247: aconst_null
    //   248: areturn
    //   249: iconst_1
    //   250: istore 4
    //   252: aload 7
    //   254: astore 6
    //   256: aload 8
    //   258: iconst_0
    //   259: putfield 3042	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
    //   262: aload 7
    //   264: astore 6
    //   266: aload 8
    //   268: iload 4
    //   270: putfield 3052	android/graphics/BitmapFactory$Options:inSampleSize	I
    //   273: aload 7
    //   275: astore 6
    //   277: aload 7
    //   279: aconst_null
    //   280: aload 8
    //   282: invokestatic 3058	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   285: astore 8
    //   287: aload 8
    //   289: ifnull +45 -> 334
    //   292: aload 7
    //   294: astore 6
    //   296: new 3060	android/graphics/drawable/BitmapDrawable
    //   299: dup
    //   300: aload 8
    //   302: invokespecial 3063	android/graphics/drawable/BitmapDrawable:<init>	(Landroid/graphics/Bitmap;)V
    //   305: astore 8
    //   307: aload 8
    //   309: astore 6
    //   311: aload 7
    //   313: ifnull -280 -> 33
    //   316: aload 7
    //   318: invokevirtual 3001	java/io/FileInputStream:close	()V
    //   321: aload 8
    //   323: areturn
    //   324: astore 6
    //   326: aload 6
    //   328: invokestatic 2128	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   331: aload 8
    //   333: areturn
    //   334: aload 7
    //   336: ifnull -89 -> 247
    //   339: aload 7
    //   341: invokevirtual 3001	java/io/FileInputStream:close	()V
    //   344: goto -97 -> 247
    //   347: astore 6
    //   349: aload 6
    //   351: invokestatic 2128	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   354: goto -107 -> 247
    //   357: astore 6
    //   359: aload 6
    //   361: invokestatic 2128	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   364: goto -117 -> 247
    //   367: astore 7
    //   369: aconst_null
    //   370: astore 6
    //   372: aload 6
    //   374: ifnull +8 -> 382
    //   377: aload 6
    //   379: invokevirtual 3001	java/io/FileInputStream:close	()V
    //   382: aload 7
    //   384: athrow
    //   385: astore 6
    //   387: aload 6
    //   389: invokestatic 2128	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   392: goto -10 -> 382
    //   395: astore 7
    //   397: goto -25 -> 372
    //   400: astore 8
    //   402: goto -174 -> 228
    //
    // Exception table:
    //   from	to	target	type
    //   60	80	223	java/lang/Throwable
    //   80	91	223	java/lang/Throwable
    //   205	220	223	java/lang/Throwable
    //   316	321	324	java/lang/Exception
    //   339	344	347	java/lang/Exception
    //   242	247	357	java/lang/Exception
    //   60	80	367	finally
    //   80	91	367	finally
    //   205	220	367	finally
    //   377	382	385	java/lang/Exception
    //   95	108	395	finally
    //   112	121	395	finally
    //   129	135	395	finally
    //   139	146	395	finally
    //   150	157	395	finally
    //   161	169	395	finally
    //   232	237	395	finally
    //   256	262	395	finally
    //   266	273	395	finally
    //   277	287	395	finally
    //   296	307	395	finally
    //   95	108	400	java/lang/Throwable
    //   112	121	400	java/lang/Throwable
    //   129	135	400	java/lang/Throwable
    //   139	146	400	java/lang/Throwable
    //   150	157	400	java/lang/Throwable
    //   161	169	400	java/lang/Throwable
    //   256	262	400	java/lang/Throwable
    //   266	273	400	java/lang/Throwable
    //   277	287	400	java/lang/Throwable
    //   296	307	400	java/lang/Throwable
  }

  public static boolean hasThemeKey(String paramString)
  {
    return currentColors.containsKey(paramString);
  }

  public static boolean hasWallpaperFromTheme()
  {
    return (currentColors.containsKey("chat_wallpaper")) || (themedWallpaperFileOffset > 0);
  }

  public static boolean isCustomTheme()
  {
    return isCustomTheme;
  }

  public static void loadWallpaper()
  {
    if (wallpaper != null)
      return;
    Utilities.searchQueue.postRunnable(new Runnable()
    {
      // ERROR //
      public void run()
      {
        // Byte code:
        //   0: invokestatic 26	org/vidogram/ui/ActionBar/Theme:access$200	()Ljava/lang/Object;
        //   3: astore 8
        //   5: aload 8
        //   7: monitorenter
        //   8: getstatic 32	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   11: ldc 34
        //   13: iconst_0
        //   14: invokevirtual 40	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
        //   17: ldc 42
        //   19: iconst_0
        //   20: invokeinterface 48 3 0
        //   25: ifne +42 -> 67
        //   28: invokestatic 52	org/vidogram/ui/ActionBar/Theme:access$300	()Ljava/util/HashMap;
        //   31: ldc 54
        //   33: invokevirtual 60	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
        //   36: checkcast 62	java/lang/Integer
        //   39: astore 4
        //   41: aload 4
        //   43: ifnull +151 -> 194
        //   46: new 64	android/graphics/drawable/ColorDrawable
        //   49: dup
        //   50: aload 4
        //   52: invokevirtual 68	java/lang/Integer:intValue	()I
        //   55: invokespecial 71	android/graphics/drawable/ColorDrawable:<init>	(I)V
        //   58: invokestatic 75	org/vidogram/ui/ActionBar/Theme:access$402	(Landroid/graphics/drawable/Drawable;)Landroid/graphics/drawable/Drawable;
        //   61: pop
        //   62: iconst_1
        //   63: invokestatic 79	org/vidogram/ui/ActionBar/Theme:access$502	(Z)Z
        //   66: pop
        //   67: invokestatic 83	org/vidogram/ui/ActionBar/Theme:access$400	()Landroid/graphics/drawable/Drawable;
        //   70: astore 4
        //   72: aload 4
        //   74: ifnonnull +98 -> 172
        //   77: getstatic 32	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   80: ldc 34
        //   82: iconst_0
        //   83: invokevirtual 40	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
        //   86: astore 4
        //   88: aload 4
        //   90: ldc 85
        //   92: ldc 86
        //   94: invokeinterface 90 3 0
        //   99: istore_3
        //   100: aload 4
        //   102: ldc 92
        //   104: iconst_0
        //   105: invokeinterface 90 3 0
        //   110: istore_2
        //   111: iload_2
        //   112: istore_1
        //   113: iload_2
        //   114: ifne +31 -> 145
        //   117: iload_3
        //   118: ldc 86
        //   120: if_icmpne +325 -> 445
        //   123: getstatic 32	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   126: invokevirtual 96	android/content/Context:getResources	()Landroid/content/res/Resources;
        //   129: ldc 97
        //   131: invokevirtual 103	android/content/res/Resources:getDrawable	(I)Landroid/graphics/drawable/Drawable;
        //   134: invokestatic 75	org/vidogram/ui/ActionBar/Theme:access$402	(Landroid/graphics/drawable/Drawable;)Landroid/graphics/drawable/Drawable;
        //   137: pop
        //   138: iconst_0
        //   139: invokestatic 79	org/vidogram/ui/ActionBar/Theme:access$502	(Z)Z
        //   142: pop
        //   143: iload_2
        //   144: istore_1
        //   145: invokestatic 83	org/vidogram/ui/ActionBar/Theme:access$400	()Landroid/graphics/drawable/Drawable;
        //   148: ifnonnull +24 -> 172
        //   151: iload_1
        //   152: istore_2
        //   153: iload_1
        //   154: ifne +6 -> 160
        //   157: ldc 104
        //   159: istore_2
        //   160: new 64	android/graphics/drawable/ColorDrawable
        //   163: dup
        //   164: iload_2
        //   165: invokespecial 71	android/graphics/drawable/ColorDrawable:<init>	(I)V
        //   168: invokestatic 75	org/vidogram/ui/ActionBar/Theme:access$402	(Landroid/graphics/drawable/Drawable;)Landroid/graphics/drawable/Drawable;
        //   171: pop
        //   172: invokestatic 83	org/vidogram/ui/ActionBar/Theme:access$400	()Landroid/graphics/drawable/Drawable;
        //   175: iconst_1
        //   176: invokestatic 108	org/vidogram/ui/ActionBar/Theme:access$900	(Landroid/graphics/drawable/Drawable;I)V
        //   179: new 13	org/vidogram/ui/ActionBar/Theme$6$1
        //   182: dup
        //   183: aload_0
        //   184: invokespecial 111	org/vidogram/ui/ActionBar/Theme$6$1:<init>	(Lorg/vidogram/ui/ActionBar/Theme$6;)V
        //   187: invokestatic 117	org/vidogram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;)V
        //   190: aload 8
        //   192: monitorexit
        //   193: return
        //   194: invokestatic 120	org/vidogram/ui/ActionBar/Theme:access$600	()I
        //   197: ifle -130 -> 67
        //   200: invokestatic 124	org/vidogram/ui/ActionBar/Theme:access$700	()Lorg/vidogram/ui/ActionBar/Theme$ThemeInfo;
        //   203: getfield 130	org/vidogram/ui/ActionBar/Theme$ThemeInfo:pathToFile	Ljava/lang/String;
        //   206: ifnonnull +16 -> 222
        //   209: invokestatic 124	org/vidogram/ui/ActionBar/Theme:access$700	()Lorg/vidogram/ui/ActionBar/Theme$ThemeInfo;
        //   212: getfield 133	org/vidogram/ui/ActionBar/Theme$ThemeInfo:assetName	Ljava/lang/String;
        //   215: astore 4
        //   217: aload 4
        //   219: ifnull -152 -> 67
        //   222: aconst_null
        //   223: astore 7
        //   225: aconst_null
        //   226: astore 6
        //   228: aload 7
        //   230: astore 4
        //   232: invokestatic 124	org/vidogram/ui/ActionBar/Theme:access$700	()Lorg/vidogram/ui/ActionBar/Theme$ThemeInfo;
        //   235: getfield 133	org/vidogram/ui/ActionBar/Theme$ThemeInfo:assetName	Ljava/lang/String;
        //   238: ifnull +110 -> 348
        //   241: aload 7
        //   243: astore 4
        //   245: invokestatic 124	org/vidogram/ui/ActionBar/Theme:access$700	()Lorg/vidogram/ui/ActionBar/Theme$ThemeInfo;
        //   248: getfield 133	org/vidogram/ui/ActionBar/Theme$ThemeInfo:assetName	Ljava/lang/String;
        //   251: invokestatic 137	org/vidogram/ui/ActionBar/Theme:getAssetFile	(Ljava/lang/String;)Ljava/io/File;
        //   254: astore 5
        //   256: aload 7
        //   258: astore 4
        //   260: new 139	java/io/FileInputStream
        //   263: dup
        //   264: aload 5
        //   266: invokespecial 142	java/io/FileInputStream:<init>	(Ljava/io/File;)V
        //   269: astore 5
        //   271: aload 5
        //   273: invokevirtual 146	java/io/FileInputStream:getChannel	()Ljava/nio/channels/FileChannel;
        //   276: invokestatic 120	org/vidogram/ui/ActionBar/Theme:access$600	()I
        //   279: i2l
        //   280: invokevirtual 152	java/nio/channels/FileChannel:position	(J)Ljava/nio/channels/FileChannel;
        //   283: pop
        //   284: aload 5
        //   286: invokestatic 158	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;)Landroid/graphics/Bitmap;
        //   289: astore 4
        //   291: aload 4
        //   293: ifnull +24 -> 317
        //   296: new 160	android/graphics/drawable/BitmapDrawable
        //   299: dup
        //   300: aload 4
        //   302: invokespecial 163	android/graphics/drawable/BitmapDrawable:<init>	(Landroid/graphics/Bitmap;)V
        //   305: invokestatic 75	org/vidogram/ui/ActionBar/Theme:access$402	(Landroid/graphics/drawable/Drawable;)Landroid/graphics/drawable/Drawable;
        //   308: invokestatic 166	org/vidogram/ui/ActionBar/Theme:access$802	(Landroid/graphics/drawable/Drawable;)Landroid/graphics/drawable/Drawable;
        //   311: pop
        //   312: iconst_1
        //   313: invokestatic 79	org/vidogram/ui/ActionBar/Theme:access$502	(Z)Z
        //   316: pop
        //   317: aload 5
        //   319: ifnull -252 -> 67
        //   322: aload 5
        //   324: invokevirtual 169	java/io/FileInputStream:close	()V
        //   327: goto -260 -> 67
        //   330: astore 4
        //   332: aload 4
        //   334: invokestatic 175	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   337: goto -270 -> 67
        //   340: astore 4
        //   342: aload 8
        //   344: monitorexit
        //   345: aload 4
        //   347: athrow
        //   348: aload 7
        //   350: astore 4
        //   352: new 177	java/io/File
        //   355: dup
        //   356: invokestatic 124	org/vidogram/ui/ActionBar/Theme:access$700	()Lorg/vidogram/ui/ActionBar/Theme$ThemeInfo;
        //   359: getfield 130	org/vidogram/ui/ActionBar/Theme$ThemeInfo:pathToFile	Ljava/lang/String;
        //   362: invokespecial 180	java/io/File:<init>	(Ljava/lang/String;)V
        //   365: astore 5
        //   367: goto -111 -> 256
        //   370: astore 4
        //   372: aload 6
        //   374: astore 5
        //   376: aload 4
        //   378: astore 6
        //   380: aload 5
        //   382: astore 4
        //   384: aload 6
        //   386: invokestatic 175	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   389: aload 5
        //   391: ifnull -324 -> 67
        //   394: aload 5
        //   396: invokevirtual 169	java/io/FileInputStream:close	()V
        //   399: goto -332 -> 67
        //   402: astore 4
        //   404: aload 4
        //   406: invokestatic 175	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   409: goto -342 -> 67
        //   412: astore 6
        //   414: aload 4
        //   416: astore 5
        //   418: aload 6
        //   420: astore 4
        //   422: aload 5
        //   424: ifnull +8 -> 432
        //   427: aload 5
        //   429: invokevirtual 169	java/io/FileInputStream:close	()V
        //   432: aload 4
        //   434: athrow
        //   435: astore 5
        //   437: aload 5
        //   439: invokestatic 175	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   442: goto -10 -> 432
        //   445: new 177	java/io/File
        //   448: dup
        //   449: invokestatic 184	org/vidogram/messenger/ApplicationLoader:getFilesDirFixed	()Ljava/io/File;
        //   452: ldc 186
        //   454: invokespecial 189	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
        //   457: astore 4
        //   459: aload 4
        //   461: invokevirtual 193	java/io/File:exists	()Z
        //   464: ifeq +25 -> 489
        //   467: aload 4
        //   469: invokevirtual 197	java/io/File:getAbsolutePath	()Ljava/lang/String;
        //   472: invokestatic 203	android/graphics/drawable/Drawable:createFromPath	(Ljava/lang/String;)Landroid/graphics/drawable/Drawable;
        //   475: invokestatic 75	org/vidogram/ui/ActionBar/Theme:access$402	(Landroid/graphics/drawable/Drawable;)Landroid/graphics/drawable/Drawable;
        //   478: pop
        //   479: iconst_1
        //   480: invokestatic 79	org/vidogram/ui/ActionBar/Theme:access$502	(Z)Z
        //   483: pop
        //   484: iload_2
        //   485: istore_1
        //   486: goto -341 -> 145
        //   489: getstatic 32	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   492: invokevirtual 96	android/content/Context:getResources	()Landroid/content/res/Resources;
        //   495: ldc 97
        //   497: invokevirtual 103	android/content/res/Resources:getDrawable	(I)Landroid/graphics/drawable/Drawable;
        //   500: invokestatic 75	org/vidogram/ui/ActionBar/Theme:access$402	(Landroid/graphics/drawable/Drawable;)Landroid/graphics/drawable/Drawable;
        //   503: pop
        //   504: iconst_0
        //   505: invokestatic 79	org/vidogram/ui/ActionBar/Theme:access$502	(Z)Z
        //   508: pop
        //   509: iload_2
        //   510: istore_1
        //   511: goto -366 -> 145
        //   514: astore 4
        //   516: iconst_0
        //   517: istore_1
        //   518: goto -373 -> 145
        //   521: astore 4
        //   523: goto -101 -> 422
        //   526: astore 6
        //   528: goto -148 -> 380
        //   531: astore 4
        //   533: iload_2
        //   534: istore_1
        //   535: goto -390 -> 145
        //
        // Exception table:
        //   from	to	target	type
        //   322	327	330	java/lang/Exception
        //   8	41	340	finally
        //   46	67	340	finally
        //   67	72	340	finally
        //   77	111	340	finally
        //   123	143	340	finally
        //   145	151	340	finally
        //   160	172	340	finally
        //   172	193	340	finally
        //   194	217	340	finally
        //   322	327	340	finally
        //   332	337	340	finally
        //   342	345	340	finally
        //   394	399	340	finally
        //   404	409	340	finally
        //   427	432	340	finally
        //   432	435	340	finally
        //   437	442	340	finally
        //   445	484	340	finally
        //   489	509	340	finally
        //   232	241	370	java/lang/Throwable
        //   245	256	370	java/lang/Throwable
        //   260	271	370	java/lang/Throwable
        //   352	367	370	java/lang/Throwable
        //   394	399	402	java/lang/Exception
        //   232	241	412	finally
        //   245	256	412	finally
        //   260	271	412	finally
        //   352	367	412	finally
        //   384	389	412	finally
        //   427	432	435	java/lang/Exception
        //   77	111	514	java/lang/Throwable
        //   271	291	521	finally
        //   296	317	521	finally
        //   271	291	526	java/lang/Throwable
        //   296	317	526	java/lang/Throwable
        //   123	143	531	java/lang/Throwable
        //   445	484	531	java/lang/Throwable
        //   489	509	531	java/lang/Throwable
      }
    });
  }

  public static void reloadWallpaper()
  {
    wallpaper = null;
    themedWallpaper = null;
    loadWallpaper();
  }

  // ERROR //
  public static void saveCurrentTheme(String paramString, boolean paramBoolean)
  {
    // Byte code:
    //   0: new 3086	java/lang/StringBuilder
    //   3: dup
    //   4: invokespecial 3087	java/lang/StringBuilder:<init>	()V
    //   7: astore 4
    //   9: getstatic 2046	org/vidogram/ui/ActionBar/Theme:currentColors	Ljava/util/HashMap;
    //   12: invokevirtual 3091	java/util/HashMap:entrySet	()Ljava/util/Set;
    //   15: invokeinterface 3097 1 0
    //   20: astore_2
    //   21: aload_2
    //   22: invokeinterface 3102 1 0
    //   27: ifeq +52 -> 79
    //   30: aload_2
    //   31: invokeinterface 3105 1 0
    //   36: checkcast 3107	java/util/Map$Entry
    //   39: astore_3
    //   40: aload 4
    //   42: aload_3
    //   43: invokeinterface 3110 1 0
    //   48: checkcast 1784	java/lang/String
    //   51: invokevirtual 3114	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   54: ldc_w 3116
    //   57: invokevirtual 3114	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   60: aload_3
    //   61: invokeinterface 3119 1 0
    //   66: invokevirtual 3122	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   69: ldc_w 3124
    //   72: invokevirtual 3114	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   75: pop
    //   76: goto -55 -> 21
    //   79: new 2522	java/io/File
    //   82: dup
    //   83: invokestatic 2535	org/vidogram/messenger/ApplicationLoader:getFilesDirFixed	()Ljava/io/File;
    //   86: aload_0
    //   87: invokespecial 2538	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   90: astore 6
    //   92: new 3126	java/io/FileOutputStream
    //   95: dup
    //   96: aload 6
    //   98: invokespecial 3127	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   101: astore_3
    //   102: aload_3
    //   103: astore_2
    //   104: aload_3
    //   105: aload 4
    //   107: invokevirtual 3130	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   110: invokevirtual 3134	java/lang/String:getBytes	()[B
    //   113: invokevirtual 3138	java/io/FileOutputStream:write	([B)V
    //   116: aload_3
    //   117: astore_2
    //   118: getstatic 2201	org/vidogram/ui/ActionBar/Theme:themedWallpaper	Landroid/graphics/drawable/Drawable;
    //   121: instanceof 3060
    //   124: ifeq +128 -> 252
    //   127: aload_3
    //   128: astore_2
    //   129: getstatic 2201	org/vidogram/ui/ActionBar/Theme:themedWallpaper	Landroid/graphics/drawable/Drawable;
    //   132: checkcast 3060	android/graphics/drawable/BitmapDrawable
    //   135: invokevirtual 3142	android/graphics/drawable/BitmapDrawable:getBitmap	()Landroid/graphics/Bitmap;
    //   138: astore 4
    //   140: aload 4
    //   142: ifnull +89 -> 231
    //   145: aload_3
    //   146: astore_2
    //   147: aload_3
    //   148: iconst_4
    //   149: newarray byte
    //   151: dup
    //   152: iconst_0
    //   153: ldc_w 3143
    //   156: bastore
    //   157: dup
    //   158: iconst_1
    //   159: ldc_w 3144
    //   162: bastore
    //   163: dup
    //   164: iconst_2
    //   165: ldc_w 3145
    //   168: bastore
    //   169: dup
    //   170: iconst_3
    //   171: ldc_w 3146
    //   174: bastore
    //   175: invokevirtual 3138	java/io/FileOutputStream:write	([B)V
    //   178: aload_3
    //   179: astore_2
    //   180: aload 4
    //   182: getstatic 3152	android/graphics/Bitmap$CompressFormat:JPEG	Landroid/graphics/Bitmap$CompressFormat;
    //   185: bipush 87
    //   187: aload_3
    //   188: invokevirtual 3158	android/graphics/Bitmap:compress	(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z
    //   191: pop
    //   192: aload_3
    //   193: astore_2
    //   194: aload_3
    //   195: iconst_5
    //   196: newarray byte
    //   198: dup
    //   199: iconst_0
    //   200: ldc_w 3146
    //   203: bastore
    //   204: dup
    //   205: iconst_1
    //   206: ldc_w 3143
    //   209: bastore
    //   210: dup
    //   211: iconst_2
    //   212: ldc_w 3144
    //   215: bastore
    //   216: dup
    //   217: iconst_3
    //   218: ldc_w 3159
    //   221: bastore
    //   222: dup
    //   223: iconst_4
    //   224: ldc_w 3146
    //   227: bastore
    //   228: invokevirtual 3138	java/io/FileOutputStream:write	([B)V
    //   231: iload_1
    //   232: ifeq +20 -> 252
    //   235: aload_3
    //   236: astore_2
    //   237: getstatic 2201	org/vidogram/ui/ActionBar/Theme:themedWallpaper	Landroid/graphics/drawable/Drawable;
    //   240: putstatic 2187	org/vidogram/ui/ActionBar/Theme:wallpaper	Landroid/graphics/drawable/Drawable;
    //   243: aload_3
    //   244: astore_2
    //   245: getstatic 2187	org/vidogram/ui/ActionBar/Theme:wallpaper	Landroid/graphics/drawable/Drawable;
    //   248: iconst_2
    //   249: invokestatic 2206	org/vidogram/ui/ActionBar/Theme:calcBackgroundColor	(Landroid/graphics/drawable/Drawable;I)V
    //   252: aload_3
    //   253: astore_2
    //   254: getstatic 2044	org/vidogram/ui/ActionBar/Theme:themesDict	Ljava/util/HashMap;
    //   257: aload_0
    //   258: invokevirtual 2139	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   261: checkcast 20	org/vidogram/ui/ActionBar/Theme$ThemeInfo
    //   264: astore 5
    //   266: aload 5
    //   268: astore 4
    //   270: aload 5
    //   272: ifnonnull +82 -> 354
    //   275: aload_3
    //   276: astore_2
    //   277: new 20	org/vidogram/ui/ActionBar/Theme$ThemeInfo
    //   280: dup
    //   281: invokespecial 2047	org/vidogram/ui/ActionBar/Theme$ThemeInfo:<init>	()V
    //   284: astore 4
    //   286: aload_3
    //   287: astore_2
    //   288: aload 4
    //   290: aload 6
    //   292: invokevirtual 2548	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   295: putfield 2499	org/vidogram/ui/ActionBar/Theme$ThemeInfo:pathToFile	Ljava/lang/String;
    //   298: aload_3
    //   299: astore_2
    //   300: aload 4
    //   302: aload_0
    //   303: putfield 2059	org/vidogram/ui/ActionBar/Theme$ThemeInfo:name	Ljava/lang/String;
    //   306: aload_3
    //   307: astore_2
    //   308: getstatic 2040	org/vidogram/ui/ActionBar/Theme:themes	Ljava/util/ArrayList;
    //   311: aload 4
    //   313: invokevirtual 2067	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   316: pop
    //   317: aload_3
    //   318: astore_2
    //   319: getstatic 2044	org/vidogram/ui/ActionBar/Theme:themesDict	Ljava/util/HashMap;
    //   322: aload 4
    //   324: getfield 2059	org/vidogram/ui/ActionBar/Theme$ThemeInfo:name	Ljava/lang/String;
    //   327: aload 4
    //   329: invokevirtual 1813	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   332: pop
    //   333: aload_3
    //   334: astore_2
    //   335: getstatic 2042	org/vidogram/ui/ActionBar/Theme:otherThemes	Ljava/util/ArrayList;
    //   338: aload 4
    //   340: invokevirtual 2067	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   343: pop
    //   344: aload_3
    //   345: astore_2
    //   346: invokestatic 2157	org/vidogram/ui/ActionBar/Theme:saveOtherThemes	()V
    //   349: aload_3
    //   350: astore_2
    //   351: invokestatic 2131	org/vidogram/ui/ActionBar/Theme:sortThemes	()V
    //   354: aload_3
    //   355: astore_2
    //   356: aload 4
    //   358: putstatic 2063	org/vidogram/ui/ActionBar/Theme:currentTheme	Lorg/vidogram/ui/ActionBar/Theme$ThemeInfo;
    //   361: aload_3
    //   362: astore_2
    //   363: getstatic 2084	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   366: ldc_w 2133
    //   369: iconst_0
    //   370: invokevirtual 2092	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
    //   373: invokeinterface 2161 1 0
    //   378: astore_0
    //   379: aload_3
    //   380: astore_2
    //   381: aload_0
    //   382: ldc_w 2135
    //   385: getstatic 2063	org/vidogram/ui/ActionBar/Theme:currentTheme	Lorg/vidogram/ui/ActionBar/Theme$ThemeInfo;
    //   388: getfield 2059	org/vidogram/ui/ActionBar/Theme$ThemeInfo:name	Ljava/lang/String;
    //   391: invokeinterface 2503 3 0
    //   396: pop
    //   397: aload_3
    //   398: astore_2
    //   399: aload_0
    //   400: invokeinterface 2171 1 0
    //   405: pop
    //   406: aload_3
    //   407: ifnull +7 -> 414
    //   410: aload_3
    //   411: invokevirtual 3160	java/io/FileOutputStream:close	()V
    //   414: return
    //   415: astore_0
    //   416: ldc_w 3162
    //   419: aload_0
    //   420: invokestatic 3165	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   423: return
    //   424: astore 4
    //   426: aconst_null
    //   427: astore_0
    //   428: aload_0
    //   429: astore_2
    //   430: aload 4
    //   432: invokestatic 2128	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   435: aload_0
    //   436: ifnull -22 -> 414
    //   439: aload_0
    //   440: invokevirtual 3160	java/io/FileOutputStream:close	()V
    //   443: return
    //   444: astore_0
    //   445: ldc_w 3162
    //   448: aload_0
    //   449: invokestatic 3165	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   452: return
    //   453: astore_0
    //   454: aconst_null
    //   455: astore_2
    //   456: aload_2
    //   457: ifnull +7 -> 464
    //   460: aload_2
    //   461: invokevirtual 3160	java/io/FileOutputStream:close	()V
    //   464: aload_0
    //   465: athrow
    //   466: astore_2
    //   467: ldc_w 3162
    //   470: aload_2
    //   471: invokestatic 3165	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   474: goto -10 -> 464
    //   477: astore_0
    //   478: goto -22 -> 456
    //   481: astore 4
    //   483: aload_3
    //   484: astore_0
    //   485: goto -57 -> 428
    //
    // Exception table:
    //   from	to	target	type
    //   410	414	415	java/lang/Exception
    //   92	102	424	java/lang/Exception
    //   439	443	444	java/lang/Exception
    //   92	102	453	finally
    //   460	464	466	java/lang/Exception
    //   104	116	477	finally
    //   118	127	477	finally
    //   129	140	477	finally
    //   147	178	477	finally
    //   180	192	477	finally
    //   194	231	477	finally
    //   237	243	477	finally
    //   245	252	477	finally
    //   254	266	477	finally
    //   277	286	477	finally
    //   288	298	477	finally
    //   300	306	477	finally
    //   308	317	477	finally
    //   319	333	477	finally
    //   335	344	477	finally
    //   346	349	477	finally
    //   351	354	477	finally
    //   356	361	477	finally
    //   363	379	477	finally
    //   381	397	477	finally
    //   399	406	477	finally
    //   430	435	477	finally
    //   104	116	481	java/lang/Exception
    //   118	127	481	java/lang/Exception
    //   129	140	481	java/lang/Exception
    //   147	178	481	java/lang/Exception
    //   180	192	481	java/lang/Exception
    //   194	231	481	java/lang/Exception
    //   237	243	481	java/lang/Exception
    //   245	252	481	java/lang/Exception
    //   254	266	481	java/lang/Exception
    //   277	286	481	java/lang/Exception
    //   288	298	481	java/lang/Exception
    //   300	306	481	java/lang/Exception
    //   308	317	481	java/lang/Exception
    //   319	333	481	java/lang/Exception
    //   335	344	481	java/lang/Exception
    //   346	349	481	java/lang/Exception
    //   351	354	481	java/lang/Exception
    //   356	361	481	java/lang/Exception
    //   363	379	481	java/lang/Exception
    //   381	397	481	java/lang/Exception
    //   399	406	481	java/lang/Exception
  }

  private static void saveOtherThemes()
  {
    SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
    JSONArray localJSONArray = new JSONArray();
    int i = 0;
    while (i < otherThemes.size())
    {
      JSONObject localJSONObject = ((ThemeInfo)otherThemes.get(i)).getSaveJson();
      if (localJSONObject != null)
        localJSONArray.put(localJSONObject);
      i += 1;
    }
    localEditor.putString("themes2", localJSONArray.toString());
    localEditor.commit();
  }

  public static void setColor(String paramString, int paramInt, boolean paramBoolean)
  {
    int i = paramInt;
    if (paramString.equals("chat_wallpaper"))
      i = paramInt | 0xFF000000;
    if (paramBoolean)
    {
      currentColors.remove(paramString);
      if ((!paramString.equals("chat_serviceBackground")) && (!paramString.equals("chat_serviceBackgroundSelected")))
        break label68;
      applyChatServiceMessageColor();
    }
    label68: 
    do
    {
      return;
      currentColors.put(paramString, Integer.valueOf(i));
      break;
    }
    while (!paramString.equals("chat_wallpaper"));
    reloadWallpaper();
  }

  public static void setCombinedDrawableColor(Drawable paramDrawable, int paramInt, boolean paramBoolean)
  {
    if (!(paramDrawable instanceof CombinedDrawable))
      return;
    if (paramBoolean);
    for (paramDrawable = ((CombinedDrawable)paramDrawable).getIcon(); ; paramDrawable = ((CombinedDrawable)paramDrawable).getBackground())
    {
      paramDrawable.setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
      return;
    }
  }

  public static void setDrawableColor(Drawable paramDrawable, int paramInt)
  {
    paramDrawable.setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
  }

  public static void setDrawableColorByKey(Drawable paramDrawable, String paramString)
  {
    paramDrawable.setColorFilter(new PorterDuffColorFilter(getColor(paramString), PorterDuff.Mode.MULTIPLY));
  }

  public static void setSelectorDrawableColor(Drawable paramDrawable, int paramInt, boolean paramBoolean)
  {
    if ((!(paramDrawable instanceof StateListDrawable)) || (paramBoolean));
    try
    {
      Drawable localDrawable = getStateDrawable(paramDrawable, 0);
      if ((localDrawable instanceof ShapeDrawable))
        ((ShapeDrawable)localDrawable).getPaint().setColor(paramInt);
      while (true)
      {
        paramDrawable = getStateDrawable(paramDrawable, 1);
        if (!(paramDrawable instanceof ShapeDrawable))
          break;
        ((ShapeDrawable)paramDrawable).getPaint().setColor(paramInt);
        return;
        localDrawable.setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
      }
      paramDrawable.setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
      return;
      paramDrawable = getStateDrawable(paramDrawable, 2);
      if ((paramDrawable instanceof ShapeDrawable))
      {
        ((ShapeDrawable)paramDrawable).getPaint().setColor(paramInt);
        return;
      }
      paramDrawable.setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
      return;
      if ((Build.VERSION.SDK_INT >= 21) && ((paramDrawable instanceof RippleDrawable)))
      {
        paramDrawable = (RippleDrawable)paramDrawable;
        if (paramBoolean)
        {
          paramDrawable.setColor(new ColorStateList(new int[][] { StateSet.WILD_CARD }, new int[] { paramInt }));
          return;
        }
        if (paramDrawable.getNumberOfLayers() > 0)
        {
          paramDrawable = paramDrawable.getDrawable(0);
          if ((paramDrawable instanceof ShapeDrawable))
          {
            ((ShapeDrawable)paramDrawable).getPaint().setColor(paramInt);
            return;
          }
          paramDrawable.setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
        }
      }
      return;
    }
    catch (Throwable paramDrawable)
    {
    }
  }

  public static void setThemeWallpaper(String paramString, Bitmap paramBitmap, File paramFile)
  {
    currentColors.remove("chat_wallpaper");
    ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().remove("overrideThemeWallpaper").commit();
    if (paramBitmap != null)
    {
      themedWallpaper = new BitmapDrawable(paramBitmap);
      saveCurrentTheme(paramString, false);
      calcBackgroundColor(themedWallpaper, 0);
      applyChatServiceMessageColor();
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.didSetNewWallpapper, new Object[0]);
      return;
    }
    themedWallpaper = null;
    wallpaper = null;
    saveCurrentTheme(paramString, false);
    reloadWallpaper();
  }

  private static void sortThemes()
  {
    Collections.sort(themes, new Comparator()
    {
      public int compare(Theme.ThemeInfo paramThemeInfo1, Theme.ThemeInfo paramThemeInfo2)
      {
        if ((paramThemeInfo1.pathToFile == null) && (paramThemeInfo1.assetName == null))
          return -1;
        if ((paramThemeInfo2.pathToFile == null) && (paramThemeInfo2.assetName == null))
          return 1;
        return paramThemeInfo1.name.compareTo(paramThemeInfo2.name);
      }
    });
  }

  public static class ThemeInfo
  {
    public String assetName;
    public String name;
    public String pathToFile;

    public static ThemeInfo createWithJson(JSONObject paramJSONObject)
    {
      if (paramJSONObject == null)
        return null;
      try
      {
        ThemeInfo localThemeInfo = new ThemeInfo();
        localThemeInfo.name = paramJSONObject.getString("name");
        localThemeInfo.pathToFile = paramJSONObject.getString("path");
        return localThemeInfo;
      }
      catch (Exception paramJSONObject)
      {
        FileLog.e(paramJSONObject);
      }
      return null;
    }

    public static ThemeInfo createWithString(String paramString)
    {
      if (TextUtils.isEmpty(paramString));
      do
      {
        return null;
        paramString = paramString.split("\\|");
      }
      while (paramString.length != 2);
      ThemeInfo localThemeInfo = new ThemeInfo();
      localThemeInfo.name = paramString[0];
      localThemeInfo.pathToFile = paramString[1];
      return localThemeInfo;
    }

    public JSONObject getSaveJson()
    {
      try
      {
        JSONObject localJSONObject = new JSONObject();
        localJSONObject.put("name", this.name);
        localJSONObject.put("path", this.pathToFile);
        return localJSONObject;
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
      }
      return null;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.ActionBar.Theme
 * JD-Core Version:    0.6.0
 */
package org.vidogram.messenger;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.net.Uri.Builder;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;
import android.util.SparseArray;
import com.google.firebase.crash.FirebaseCrash;
import itman.Vidofilm.a.u;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashMap<Ljava.lang.Integer;Lorg.vidogram.messenger.ContactsController.Contact;>;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.InputUser;
import org.vidogram.tgnet.TLRPC.PrivacyRule;
import org.vidogram.tgnet.TLRPC.TL_accountDaysTTL;
import org.vidogram.tgnet.TLRPC.TL_account_getAccountTTL;
import org.vidogram.tgnet.TLRPC.TL_account_getPrivacy;
import org.vidogram.tgnet.TLRPC.TL_account_privacyRules;
import org.vidogram.tgnet.TLRPC.TL_contact;
import org.vidogram.tgnet.TLRPC.TL_contactStatus;
import org.vidogram.tgnet.TLRPC.TL_contacts_contactsNotModified;
import org.vidogram.tgnet.TLRPC.TL_contacts_deleteContacts;
import org.vidogram.tgnet.TLRPC.TL_contacts_getContacts;
import org.vidogram.tgnet.TLRPC.TL_contacts_getStatuses;
import org.vidogram.tgnet.TLRPC.TL_contacts_importContacts;
import org.vidogram.tgnet.TLRPC.TL_contacts_importedContacts;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_help_getInviteText;
import org.vidogram.tgnet.TLRPC.TL_help_inviteText;
import org.vidogram.tgnet.TLRPC.TL_importedContact;
import org.vidogram.tgnet.TLRPC.TL_inputPhoneContact;
import org.vidogram.tgnet.TLRPC.TL_inputPrivacyKeyChatInvite;
import org.vidogram.tgnet.TLRPC.TL_inputPrivacyKeyPhoneCall;
import org.vidogram.tgnet.TLRPC.TL_inputPrivacyKeyStatusTimestamp;
import org.vidogram.tgnet.TLRPC.TL_userStatusLastMonth;
import org.vidogram.tgnet.TLRPC.TL_userStatusLastWeek;
import org.vidogram.tgnet.TLRPC.TL_userStatusRecently;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.Vector;
import org.vidogram.tgnet.TLRPC.contacts_Contacts;

public class ContactsController
{
  private static volatile ContactsController Instance;
  private static final Object loadContactsSync = new Object();
  private ArrayList<u> addContacts = new ArrayList();
  private ArrayList<TLRPC.PrivacyRule> callPrivacyRules = null;
  private int completedRequestsCount;
  public ArrayList<TLRPC.TL_contact> contacts = new ArrayList();
  public HashMap<Integer, Contact> contactsBook = new HashMap();
  private boolean contactsBookLoaded = false;
  public HashMap<String, Contact> contactsBookSPhones = new HashMap();
  public HashMap<String, TLRPC.TL_contact> contactsByPhone = new HashMap();
  public SparseArray<TLRPC.TL_contact> contactsDict = new SparseArray();
  public boolean contactsLoaded = false;
  private boolean contactsSyncInProgress = false;
  private Account currentAccount;
  private ArrayList<Integer> delayedContactsUpdate = new ArrayList();
  private int deleteAccountTTL;
  private ArrayList<String> deletedContacts = new ArrayList();
  private ArrayList<TLRPC.User> getUserFromServer;
  private ArrayList<TLRPC.PrivacyRule> groupPrivacyRules = null;
  private boolean ignoreChanges = false;
  private String inviteText;
  private String lastContactsVersions = "";
  private int loadingCallsInfo;
  private boolean loadingContacts = false;
  private int loadingDeleteInfo;
  private int loadingGroupInfo;
  private int loadingLastSeenInfo;
  private final Object observerLock = new Object();
  public ArrayList<Contact> phoneBookContacts = new ArrayList();
  private ArrayList<TLRPC.PrivacyRule> privacyRules = null;
  private String[] projectionNames = { "contact_id", "data2", "data3", "display_name", "data5" };
  private String[] projectionPhones = { "contact_id", "data1", "data2", "data3" };
  private HashMap<String, String> sectionsToReplace = new HashMap();
  public ArrayList<String> sortedUsersMutualSectionsArray = new ArrayList();
  public ArrayList<String> sortedUsersSectionsArray = new ArrayList();
  private HashMap<String, Integer> toImportContact = new HashMap();
  private ArrayList<u> updatedContacts = new ArrayList();
  private boolean updatingInviteText = false;
  public HashMap<String, ArrayList<TLRPC.TL_contact>> usersMutualSectionsDict = new HashMap();
  public HashMap<String, ArrayList<TLRPC.TL_contact>> usersSectionsDict = new HashMap();
  private u vContact;

  static
  {
    Instance = null;
  }

  public ContactsController()
  {
    if (ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("needGetStatuses", false))
      reloadContactsStatuses();
    this.sectionsToReplace.put("À", "A");
    this.sectionsToReplace.put("Á", "A");
    this.sectionsToReplace.put("Ä", "A");
    this.sectionsToReplace.put("Ù", "U");
    this.sectionsToReplace.put("Ú", "U");
    this.sectionsToReplace.put("Ü", "U");
    this.sectionsToReplace.put("Ì", "I");
    this.sectionsToReplace.put("Í", "I");
    this.sectionsToReplace.put("Ï", "I");
    this.sectionsToReplace.put("È", "E");
    this.sectionsToReplace.put("É", "E");
    this.sectionsToReplace.put("Ê", "E");
    this.sectionsToReplace.put("Ë", "E");
    this.sectionsToReplace.put("Ò", "O");
    this.sectionsToReplace.put("Ó", "O");
    this.sectionsToReplace.put("Ö", "O");
    this.sectionsToReplace.put("Ç", "C");
    this.sectionsToReplace.put("Ñ", "N");
    this.sectionsToReplace.put("Ÿ", "Y");
    this.sectionsToReplace.put("Ý", "Y");
    this.sectionsToReplace.put("Ţ", "Y");
  }

  private void applyContactsUpdates(ArrayList<Integer> paramArrayList1, ConcurrentHashMap<Integer, TLRPC.User> paramConcurrentHashMap, ArrayList<TLRPC.TL_contact> paramArrayList, ArrayList<Integer> paramArrayList2)
  {
    Object localObject1;
    if (paramArrayList != null)
    {
      localObject1 = paramArrayList2;
      if (paramArrayList2 != null);
    }
    else
    {
      paramArrayList2 = new ArrayList();
      localObject2 = new ArrayList();
      i = 0;
      paramArrayList = paramArrayList2;
      localObject1 = localObject2;
      if (i < paramArrayList1.size())
      {
        paramArrayList = (Integer)paramArrayList1.get(i);
        if (paramArrayList.intValue() > 0)
        {
          localObject1 = new TLRPC.TL_contact();
          ((TLRPC.TL_contact)localObject1).user_id = paramArrayList.intValue();
          paramArrayList2.add(localObject1);
        }
        while (true)
        {
          i += 1;
          break;
          if (paramArrayList.intValue() >= 0)
            continue;
          ((ArrayList)localObject2).add(Integer.valueOf(-paramArrayList.intValue()));
        }
      }
    }
    FileLog.e("process update - contacts add = " + paramArrayList.size() + " delete = " + ((ArrayList)localObject1).size());
    paramArrayList2 = new StringBuilder();
    Object localObject2 = new StringBuilder();
    this.addContacts = org.vidogram.VidogramUi.b.a(ApplicationLoader.applicationContext).b();
    this.updatedContacts = org.vidogram.VidogramUi.b.a(ApplicationLoader.applicationContext).c();
    this.deletedContacts = org.vidogram.VidogramUi.b.a(ApplicationLoader.applicationContext).d();
    int j = 0;
    int i = 0;
    Object localObject3;
    if (j < paramArrayList.size())
    {
      localObject3 = (TLRPC.TL_contact)paramArrayList.get(j);
      paramArrayList1 = null;
      if (paramConcurrentHashMap != null)
        paramArrayList1 = (TLRPC.User)paramConcurrentHashMap.get(Integer.valueOf(((TLRPC.TL_contact)localObject3).user_id));
      if (paramArrayList1 == null)
      {
        paramArrayList1 = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_contact)localObject3).user_id));
        label290: if ((paramArrayList1 != null) && (!TextUtils.isEmpty(paramArrayList1.phone)))
          break label328;
        i = 1;
      }
      while (true)
      {
        j += 1;
        break;
        MessagesController.getInstance().putUser(paramArrayList1, true);
        break label290;
        label328: localObject3 = (Contact)this.contactsBookSPhones.get(paramArrayList1.phone);
        if (localObject3 != null)
        {
          k = ((Contact)localObject3).shortPhones.indexOf(paramArrayList1.phone);
          if (k != -1)
            ((Contact)localObject3).phoneDeleted.set(k, Integer.valueOf(0));
        }
        if (paramArrayList2.length() != 0)
          paramArrayList2.append(",");
        paramArrayList2.append(paramArrayList1.phone);
        this.vContact = new u();
        this.vContact.c(paramArrayList1.first_name);
        this.vContact.d(paramArrayList1.last_name);
        this.vContact.a(paramArrayList1.phone);
        this.vContact.b(paramArrayList1.id + "");
        this.addContacts.add(this.vContact);
      }
    }
    int k = 0;
    j = i;
    i = k;
    if (i < ((ArrayList)localObject1).size())
    {
      localObject3 = (Integer)((ArrayList)localObject1).get(i);
      Utilities.phoneBookQueue.postRunnable(new Runnable((Integer)localObject3)
      {
        public void run()
        {
          ContactsController.this.deleteContactFromPhoneBook(this.val$uid.intValue());
        }
      });
      paramArrayList1 = null;
      if (paramConcurrentHashMap != null)
        paramArrayList1 = (TLRPC.User)paramConcurrentHashMap.get(localObject3);
      if (paramArrayList1 == null)
      {
        paramArrayList1 = MessagesController.getInstance().getUser((Integer)localObject3);
        label577: if (paramArrayList1 != null)
          break label609;
        k = 1;
      }
      while (true)
      {
        i += 1;
        j = k;
        break;
        MessagesController.getInstance().putUser(paramArrayList1, true);
        break label577;
        label609: k = j;
        if (TextUtils.isEmpty(paramArrayList1.phone))
          continue;
        localObject3 = (Contact)this.contactsBookSPhones.get(paramArrayList1.phone);
        if (localObject3 != null)
        {
          k = ((Contact)localObject3).shortPhones.indexOf(paramArrayList1.phone);
          if (k != -1)
            ((Contact)localObject3).phoneDeleted.set(k, Integer.valueOf(1));
        }
        if (((StringBuilder)localObject2).length() != 0)
          ((StringBuilder)localObject2).append(",");
        ((StringBuilder)localObject2).append(paramArrayList1.phone);
        this.deletedContacts.add(paramArrayList1.phone);
        k = j;
      }
    }
    if ((paramArrayList2.length() != 0) || (((StringBuilder)localObject2).length() != 0))
    {
      MessagesStorage.getInstance().applyPhoneBookUpdates(paramArrayList2.toString(), ((StringBuilder)localObject2).toString());
      org.vidogram.VidogramUi.b.a(ApplicationLoader.applicationContext).a(this.addContacts, this.deletedContacts, this.updatedContacts);
    }
    if (j != 0)
    {
      Utilities.stageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          ContactsController.this.loadContacts(false, true);
        }
      });
      return;
    }
    AndroidUtilities.runOnUIThread(new Runnable(paramArrayList, (ArrayList)localObject1)
    {
      public void run()
      {
        int i = 0;
        while (i < this.val$newContacts.size())
        {
          localObject = (TLRPC.TL_contact)this.val$newContacts.get(i);
          if (ContactsController.this.contactsDict.get(((TLRPC.TL_contact)localObject).user_id) == null)
          {
            ContactsController.this.contacts.add(localObject);
            ContactsController.this.contactsDict.put(((TLRPC.TL_contact)localObject).user_id, localObject);
          }
          i += 1;
        }
        i = 0;
        while (i < this.val$contactsToDelete.size())
        {
          localObject = (Integer)this.val$contactsToDelete.get(i);
          TLRPC.TL_contact localTL_contact = (TLRPC.TL_contact)ContactsController.this.contactsDict.get(((Integer)localObject).intValue());
          if (localTL_contact != null)
          {
            ContactsController.this.contacts.remove(localTL_contact);
            ContactsController.this.contactsDict.remove(((Integer)localObject).intValue());
          }
          i += 1;
        }
        if (!this.val$newContacts.isEmpty())
        {
          ContactsController.this.updateUnregisteredContacts(ContactsController.this.contacts);
          ContactsController.this.performWriteContactsToPhoneBook();
        }
        ContactsController.this.performSyncPhoneBook(ContactsController.this.getContactsCopy(ContactsController.this.contactsBook), false, false, false, false);
        Object localObject = ContactsController.this;
        if (!this.val$newContacts.isEmpty());
        for (boolean bool = true; ; bool = false)
        {
          ((ContactsController)localObject).buildContactsSectionsArrays(bool);
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
          return;
        }
      }
    });
  }

  private void buildContactsSectionsArrays(boolean paramBoolean)
  {
    if (paramBoolean)
      Collections.sort(this.contacts, new Comparator()
      {
        public int compare(TLRPC.TL_contact paramTL_contact1, TLRPC.TL_contact paramTL_contact2)
        {
          paramTL_contact1 = MessagesController.getInstance().getUser(Integer.valueOf(paramTL_contact1.user_id));
          paramTL_contact2 = MessagesController.getInstance().getUser(Integer.valueOf(paramTL_contact2.user_id));
          return UserObject.getFirstName(paramTL_contact1).compareTo(UserObject.getFirstName(paramTL_contact2));
        }
      });
    StringBuilder localStringBuilder = new StringBuilder();
    HashMap localHashMap = new HashMap();
    ArrayList localArrayList2 = new ArrayList();
    Iterator localIterator = this.contacts.iterator();
    while (localIterator.hasNext())
    {
      TLRPC.TL_contact localTL_contact = (TLRPC.TL_contact)localIterator.next();
      Object localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(localTL_contact.user_id));
      if (localObject1 == null)
        continue;
      Object localObject2 = UserObject.getFirstName((TLRPC.User)localObject1);
      localObject1 = localObject2;
      if (((String)localObject2).length() > 1)
        localObject1 = ((String)localObject2).substring(0, 1);
      if (((String)localObject1).length() == 0);
      for (localObject1 = "#"; ; localObject1 = ((String)localObject1).toUpperCase())
      {
        localObject2 = (String)this.sectionsToReplace.get(localObject1);
        if (localObject2 != null)
          localObject1 = localObject2;
        ArrayList localArrayList1 = (ArrayList)localHashMap.get(localObject1);
        localObject2 = localArrayList1;
        if (localArrayList1 == null)
        {
          localObject2 = new ArrayList();
          localHashMap.put(localObject1, localObject2);
          localArrayList2.add(localObject1);
        }
        ((ArrayList)localObject2).add(localTL_contact);
        if (localStringBuilder.length() != 0)
          localStringBuilder.append(",");
        localStringBuilder.append(localTL_contact.user_id);
        break;
      }
    }
    UserConfig.contactsHash = Utilities.MD5(localStringBuilder.toString());
    UserConfig.saveConfig(false);
    Collections.sort(localArrayList2, new Comparator()
    {
      public int compare(String paramString1, String paramString2)
      {
        int i = paramString1.charAt(0);
        int j = paramString2.charAt(0);
        if (i == 35)
          return 1;
        if (j == 35)
          return -1;
        return paramString1.compareTo(paramString2);
      }
    });
    this.usersSectionsDict = localHashMap;
    this.sortedUsersSectionsArray = localArrayList2;
  }

  // ERROR //
  private boolean checkContactsInternal()
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 5
    //   3: iconst_0
    //   4: istore_1
    //   5: iconst_0
    //   6: istore_3
    //   7: iconst_0
    //   8: istore 7
    //   10: iconst_0
    //   11: istore 6
    //   13: iconst_0
    //   14: istore 4
    //   16: iload 7
    //   18: istore_2
    //   19: aload_0
    //   20: invokespecial 690	org/vidogram/messenger/ContactsController:hasContactsPermission	()Z
    //   23: ifne +5 -> 28
    //   26: iconst_0
    //   27: ireturn
    //   28: iload 7
    //   30: istore_2
    //   31: getstatic 270	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   34: invokevirtual 694	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   37: astore 8
    //   39: aload 8
    //   41: getstatic 700	android/provider/ContactsContract$RawContacts:CONTENT_URI	Landroid/net/Uri;
    //   44: iconst_1
    //   45: anewarray 225	java/lang/String
    //   48: dup
    //   49: iconst_0
    //   50: ldc_w 702
    //   53: aastore
    //   54: aconst_null
    //   55: aconst_null
    //   56: aconst_null
    //   57: invokevirtual 708	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   60: astore 8
    //   62: aload 8
    //   64: ifnull +167 -> 231
    //   67: iload 4
    //   69: istore_2
    //   70: iload 6
    //   72: istore_3
    //   73: new 487	java/lang/StringBuilder
    //   76: dup
    //   77: invokespecial 488	java/lang/StringBuilder:<init>	()V
    //   80: astore 9
    //   82: iload 4
    //   84: istore_2
    //   85: iload 6
    //   87: istore_3
    //   88: aload 8
    //   90: invokeinterface 713 1 0
    //   95: ifeq +71 -> 166
    //   98: iload 4
    //   100: istore_2
    //   101: iload 6
    //   103: istore_3
    //   104: aload 9
    //   106: aload 8
    //   108: aload 8
    //   110: ldc_w 702
    //   113: invokeinterface 717 2 0
    //   118: invokeinterface 721 2 0
    //   123: invokevirtual 494	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   126: pop
    //   127: goto -45 -> 82
    //   130: astore 9
    //   132: iload_2
    //   133: istore_1
    //   134: aload 9
    //   136: invokestatic 724	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   139: iload_1
    //   140: istore_2
    //   141: aload 8
    //   143: ifnull +159 -> 302
    //   146: iload_1
    //   147: istore_2
    //   148: aload 8
    //   150: invokeinterface 727 1 0
    //   155: iload_1
    //   156: ireturn
    //   157: astore 8
    //   159: aload 8
    //   161: invokestatic 724	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   164: iload_2
    //   165: ireturn
    //   166: iload 4
    //   168: istore_2
    //   169: iload 6
    //   171: istore_3
    //   172: aload 9
    //   174: invokevirtual 503	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   177: astore 9
    //   179: iload 4
    //   181: istore_2
    //   182: iload 5
    //   184: istore_1
    //   185: iload 6
    //   187: istore_3
    //   188: aload_0
    //   189: getfield 197	org/vidogram/messenger/ContactsController:lastContactsVersions	Ljava/lang/String;
    //   192: invokevirtual 663	java/lang/String:length	()I
    //   195: ifeq +26 -> 221
    //   198: iload 4
    //   200: istore_2
    //   201: iload 5
    //   203: istore_1
    //   204: iload 6
    //   206: istore_3
    //   207: aload_0
    //   208: getfield 197	org/vidogram/messenger/ContactsController:lastContactsVersions	Ljava/lang/String;
    //   211: aload 9
    //   213: invokevirtual 730	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   216: ifne +5 -> 221
    //   219: iconst_1
    //   220: istore_1
    //   221: iload_1
    //   222: istore_2
    //   223: iload_1
    //   224: istore_3
    //   225: aload_0
    //   226: aload 9
    //   228: putfield 197	org/vidogram/messenger/ContactsController:lastContactsVersions	Ljava/lang/String;
    //   231: iload_1
    //   232: istore_2
    //   233: aload 8
    //   235: ifnull +67 -> 302
    //   238: iload_1
    //   239: istore_2
    //   240: aload 8
    //   242: invokeinterface 727 1 0
    //   247: iload_1
    //   248: ireturn
    //   249: astore 9
    //   251: aconst_null
    //   252: astore 8
    //   254: iload_3
    //   255: istore_1
    //   256: aload 8
    //   258: ifnull +12 -> 270
    //   261: iload_1
    //   262: istore_2
    //   263: aload 8
    //   265: invokeinterface 727 1 0
    //   270: iload_1
    //   271: istore_2
    //   272: aload 9
    //   274: athrow
    //   275: astore 8
    //   277: goto -118 -> 159
    //   280: astore 9
    //   282: iload_3
    //   283: istore_1
    //   284: goto -28 -> 256
    //   287: astore 9
    //   289: goto -33 -> 256
    //   292: astore 9
    //   294: aconst_null
    //   295: astore 8
    //   297: iconst_0
    //   298: istore_1
    //   299: goto -165 -> 134
    //   302: iload_2
    //   303: ireturn
    //
    // Exception table:
    //   from	to	target	type
    //   73	82	130	java/lang/Exception
    //   88	98	130	java/lang/Exception
    //   104	127	130	java/lang/Exception
    //   172	179	130	java/lang/Exception
    //   188	198	130	java/lang/Exception
    //   207	219	130	java/lang/Exception
    //   225	231	130	java/lang/Exception
    //   148	155	157	java/lang/Exception
    //   240	247	157	java/lang/Exception
    //   39	62	249	finally
    //   19	26	275	java/lang/Exception
    //   31	39	275	java/lang/Exception
    //   263	270	275	java/lang/Exception
    //   272	275	275	java/lang/Exception
    //   73	82	280	finally
    //   88	98	280	finally
    //   104	127	280	finally
    //   172	179	280	finally
    //   188	198	280	finally
    //   207	219	280	finally
    //   225	231	280	finally
    //   134	139	287	finally
    //   39	62	292	java/lang/Exception
  }

  private void deleteContactFromPhoneBook(int paramInt)
  {
    if (!hasContactsPermission())
      return;
    synchronized (this.observerLock)
    {
      this.ignoreChanges = true;
    }
    try
    {
      ApplicationLoader.applicationContext.getContentResolver().delete(ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").appendQueryParameter("account_name", this.currentAccount.name).appendQueryParameter("account_type", this.currentAccount.type).build(), "sync2 = " + paramInt, null);
      synchronized (this.observerLock)
      {
        this.ignoreChanges = false;
        return;
      }
      localObject3 = finally;
      monitorexit;
      throw localObject3;
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  public static String formatName(String paramString1, String paramString2)
  {
    int j = 0;
    String str = paramString1;
    if (paramString1 != null)
      str = paramString1.trim();
    paramString1 = paramString2;
    if (paramString2 != null)
      paramString1 = paramString2.trim();
    int i;
    if (str != null)
    {
      i = str.length();
      if (paramString1 != null)
        j = paramString1.length();
      paramString2 = new StringBuilder(j + i + 1);
      if (LocaleController.nameDisplayOrder != 1)
        break label141;
      if ((str == null) || (str.length() <= 0))
        break label121;
      paramString2.append(str);
      if ((paramString1 != null) && (paramString1.length() > 0))
      {
        paramString2.append(" ");
        paramString2.append(paramString1);
      }
    }
    while (true)
    {
      return paramString2.toString();
      i = 0;
      break;
      label121: if ((paramString1 == null) || (paramString1.length() <= 0))
        continue;
      paramString2.append(paramString1);
      continue;
      label141: if ((paramString1 != null) && (paramString1.length() > 0))
      {
        paramString2.append(paramString1);
        if ((str == null) || (str.length() <= 0))
          continue;
        paramString2.append(" ");
        paramString2.append(str);
        continue;
      }
      if ((str == null) || (str.length() <= 0))
        continue;
      paramString2.append(str);
    }
  }

  public static ContactsController getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      monitorenter;
      try
      {
        ContactsController localContactsController = Instance;
        localObject1 = localContactsController;
        if (localContactsController == null)
        {
          localObject1 = new ContactsController();
          Instance = (ContactsController)localObject1;
        }
        return localObject1;
      }
      finally
      {
        monitorexit;
      }
    }
    return (ContactsController)localObject2;
  }

  // ERROR //
  private boolean hasContactsPermission()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: getstatic 793	android/os/Build$VERSION:SDK_INT	I
    //   5: bipush 23
    //   7: if_icmplt +19 -> 26
    //   10: getstatic 270	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   13: ldc_w 795
    //   16: invokevirtual 798	android/content/Context:checkSelfPermission	(Ljava/lang/String;)I
    //   19: ifne +5 -> 24
    //   22: iconst_1
    //   23: ireturn
    //   24: iconst_0
    //   25: ireturn
    //   26: getstatic 270	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   29: invokevirtual 694	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   32: getstatic 801	android/provider/ContactsContract$CommonDataKinds$Phone:CONTENT_URI	Landroid/net/Uri;
    //   35: aload_0
    //   36: getfield 235	org/vidogram/messenger/ContactsController:projectionPhones	[Ljava/lang/String;
    //   39: aconst_null
    //   40: aconst_null
    //   41: aconst_null
    //   42: invokevirtual 708	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   45: astore_2
    //   46: aload_2
    //   47: astore_3
    //   48: aload_3
    //   49: ifnull +16 -> 65
    //   52: aload_3
    //   53: astore_2
    //   54: aload_3
    //   55: invokeinterface 804 1 0
    //   60: istore_1
    //   61: iload_1
    //   62: ifne +23 -> 85
    //   65: aload_3
    //   66: ifnull +9 -> 75
    //   69: aload_3
    //   70: invokeinterface 727 1 0
    //   75: iconst_0
    //   76: ireturn
    //   77: astore_2
    //   78: aload_2
    //   79: invokestatic 724	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   82: goto -7 -> 75
    //   85: aload_3
    //   86: ifnull +9 -> 95
    //   89: aload_3
    //   90: invokeinterface 727 1 0
    //   95: iconst_1
    //   96: ireturn
    //   97: astore_2
    //   98: aload_2
    //   99: invokestatic 724	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   102: goto -7 -> 95
    //   105: astore 4
    //   107: aconst_null
    //   108: astore_3
    //   109: aload_3
    //   110: astore_2
    //   111: aload 4
    //   113: invokestatic 724	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   116: aload_3
    //   117: ifnull -22 -> 95
    //   120: aload_3
    //   121: invokeinterface 727 1 0
    //   126: goto -31 -> 95
    //   129: astore_2
    //   130: aload_2
    //   131: invokestatic 724	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   134: goto -39 -> 95
    //   137: astore_2
    //   138: aload_3
    //   139: ifnull +9 -> 148
    //   142: aload_3
    //   143: invokeinterface 727 1 0
    //   148: aload_2
    //   149: athrow
    //   150: astore_3
    //   151: aload_3
    //   152: invokestatic 724	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   155: goto -7 -> 148
    //   158: astore 4
    //   160: aload_2
    //   161: astore_3
    //   162: aload 4
    //   164: astore_2
    //   165: goto -27 -> 138
    //   168: astore 4
    //   170: goto -61 -> 109
    //
    // Exception table:
    //   from	to	target	type
    //   69	75	77	java/lang/Exception
    //   89	95	97	java/lang/Exception
    //   26	46	105	java/lang/Throwable
    //   120	126	129	java/lang/Exception
    //   26	46	137	finally
    //   142	148	150	java/lang/Exception
    //   54	61	158	finally
    //   111	116	158	finally
    //   54	61	168	java/lang/Throwable
  }

  private void performWriteContactsToPhoneBook()
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.addAll(this.contacts);
    Utilities.phoneBookQueue.postRunnable(new Runnable(localArrayList)
    {
      public void run()
      {
        ContactsController.this.performWriteContactsToPhoneBookInternal(this.val$contactsArray);
      }
    });
  }

  private void performWriteContactsToPhoneBookInternal(ArrayList<TLRPC.TL_contact> paramArrayList)
  {
    Object localObject1;
    Object localObject2;
    try
    {
      if (!hasContactsPermission())
        return;
      localObject1 = ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("account_name", this.currentAccount.name).appendQueryParameter("account_type", this.currentAccount.type).build();
      localObject2 = ApplicationLoader.applicationContext.getContentResolver().query((Uri)localObject1, new String[] { "_id", "sync2" }, null, null, null);
      localObject1 = new HashMap();
      if (localObject2 == null)
        return;
      while (((Cursor)localObject2).moveToNext())
        ((HashMap)localObject1).put(Integer.valueOf(((Cursor)localObject2).getInt(1)), Long.valueOf(((Cursor)localObject2).getLong(0)));
    }
    catch (Exception paramArrayList)
    {
      FileLog.e(paramArrayList);
      return;
    }
    ((Cursor)localObject2).close();
    int i = 0;
    while (i < paramArrayList.size())
    {
      localObject2 = (TLRPC.TL_contact)paramArrayList.get(i);
      if (!((HashMap)localObject1).containsKey(Integer.valueOf(((TLRPC.TL_contact)localObject2).user_id)))
        addContactToPhoneBook(MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_contact)localObject2).user_id)), false);
      i += 1;
    }
  }

  private HashMap<Integer, Contact> readContactsFromPhoneBook()
  {
    HashMap localHashMap = new HashMap();
    while (true)
    {
      Object localObject3;
      Object localObject4;
      Object localObject5;
      Cursor localCursor;
      int i;
      Object localObject2;
      try
      {
        if (!hasContactsPermission())
          return localHashMap;
        localObject3 = ApplicationLoader.applicationContext.getContentResolver();
        localObject4 = new HashMap();
        localObject5 = new ArrayList();
        localCursor = ((ContentResolver)localObject3).query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, this.projectionPhones, null, null, null);
        if (localCursor == null)
          break label468;
        if (localCursor.getCount() <= 0)
          break label461;
        if (!localCursor.moveToNext())
          break label461;
        String str1 = localCursor.getString(1);
        if (TextUtils.isEmpty(str1))
          continue;
        str2 = org.vidogram.a.b.a(str1, true);
        if (str2.length() == 0)
          continue;
        if (!str2.startsWith("+"))
          break label797;
        str1 = str2.substring(1);
        if (((HashMap)localObject4).containsKey(str1))
          continue;
        Object localObject6 = Integer.valueOf(localCursor.getInt(0));
        if (((ArrayList)localObject5).contains(localObject6))
          continue;
        ((ArrayList)localObject5).add(localObject6);
        i = localCursor.getInt(2);
        localObject2 = (Contact)localHashMap.get(localObject6);
        if (localObject2 != null)
          break label794;
        localObject2 = new Contact();
        ((Contact)localObject2).first_name = "";
        ((Contact)localObject2).last_name = "";
        ((Contact)localObject2).id = ((Integer)localObject6).intValue();
        localHashMap.put(localObject6, localObject2);
        ((Contact)localObject2).shortPhones.add(str1);
        ((Contact)localObject2).phones.add(str2);
        ((Contact)localObject2).phoneDeleted.add(Integer.valueOf(0));
        if (i != 0)
          break label340;
        str2 = localCursor.getString(3);
        localObject6 = ((Contact)localObject2).phoneTypes;
        if (str2 != null)
        {
          ((ArrayList)localObject6).add(str2);
          ((HashMap)localObject4).put(str1, localObject2);
          continue;
        }
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
        localHashMap.clear();
        return localHashMap;
      }
      String str2 = LocaleController.getString("PhoneMobile", 2131166265);
      continue;
      label340: if (i == 1)
      {
        ((Contact)localObject2).phoneTypes.add(LocaleController.getString("PhoneHome", 2131166263));
        continue;
      }
      if (i == 2)
      {
        ((Contact)localObject2).phoneTypes.add(LocaleController.getString("PhoneMobile", 2131166265));
        continue;
      }
      if (i == 3)
      {
        ((Contact)localObject2).phoneTypes.add(LocaleController.getString("PhoneWork", 2131166271));
        continue;
      }
      if (i == 12)
      {
        ((Contact)localObject2).phoneTypes.add(LocaleController.getString("PhoneMain", 2131166264));
        continue;
      }
      ((Contact)localObject2).phoneTypes.add(LocaleController.getString("PhoneOther", 2131166270));
      continue;
      label461: localCursor.close();
      label468: Object localObject1 = TextUtils.join(",", (Iterable)localObject5);
      localObject1 = ((ContentResolver)localObject3).query(ContactsContract.Data.CONTENT_URI, this.projectionNames, "contact_id IN (" + (String)localObject1 + ") AND " + "mimetype" + " = '" + "vnd.android.cursor.item/name" + "'", null, null);
      if (localObject1 == null)
        continue;
      while (((Cursor)localObject1).moveToNext())
      {
        i = ((Cursor)localObject1).getInt(0);
        localObject2 = ((Cursor)localObject1).getString(1);
        str2 = ((Cursor)localObject1).getString(2);
        localObject3 = ((Cursor)localObject1).getString(3);
        localObject4 = ((Cursor)localObject1).getString(4);
        localObject5 = (Contact)localHashMap.get(Integer.valueOf(i));
        if ((localObject5 == null) || (!TextUtils.isEmpty(((Contact)localObject5).first_name)) || (!TextUtils.isEmpty(((Contact)localObject5).last_name)))
          continue;
        ((Contact)localObject5).first_name = ((String)localObject2);
        ((Contact)localObject5).last_name = str2;
        if (((Contact)localObject5).first_name == null)
          ((Contact)localObject5).first_name = "";
        if (!TextUtils.isEmpty((CharSequence)localObject4))
          if (((Contact)localObject5).first_name.length() == 0)
            break label775;
        label775: for (((Contact)localObject5).first_name = (((Contact)localObject5).first_name + " " + (String)localObject4); ; ((Contact)localObject5).first_name = ((String)localObject4))
        {
          if (((Contact)localObject5).last_name == null)
            ((Contact)localObject5).last_name = "";
          if ((!TextUtils.isEmpty(((Contact)localObject5).last_name)) || (!TextUtils.isEmpty(((Contact)localObject5).first_name)) || (TextUtils.isEmpty((CharSequence)localObject3)))
            break;
          ((Contact)localObject5).first_name = ((String)localObject3);
          break;
        }
      }
      ((Cursor)localObject1).close();
      continue;
      label794: continue;
      label797: localObject1 = str2;
    }
  }

  private void reloadContactsStatusesMaybe()
  {
    try
    {
      if (ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getLong("lastReloadStatusTime", 0L) < System.currentTimeMillis() - 86400000L)
        reloadContactsStatuses();
      return;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
  }

  private void saveContactsLoadTime()
  {
    try
    {
      ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putLong("lastReloadStatusTime", System.currentTimeMillis()).commit();
      return;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
  }

  private void updateUnregisteredContacts(ArrayList<TLRPC.TL_contact> paramArrayList)
  {
    HashMap localHashMap = new HashMap();
    int i = 0;
    Object localObject2;
    if (i < paramArrayList.size())
    {
      localObject1 = (TLRPC.TL_contact)paramArrayList.get(i);
      localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_contact)localObject1).user_id));
      if ((localObject2 == null) || (TextUtils.isEmpty(((TLRPC.User)localObject2).phone)));
      while (true)
      {
        i += 1;
        break;
        localHashMap.put(((TLRPC.User)localObject2).phone, localObject1);
      }
    }
    paramArrayList = new ArrayList();
    Object localObject1 = this.contactsBook.entrySet().iterator();
    label154: label245: label248: 
    while (true)
    {
      Contact localContact;
      if (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Map.Entry)((Iterator)localObject1).next();
        localContact = (Contact)((Map.Entry)localObject2).getValue();
        ((Integer)((Map.Entry)localObject2).getKey()).intValue();
        i = 0;
        if (i >= localContact.phones.size())
          break label245;
        if ((!localHashMap.containsKey((String)localContact.shortPhones.get(i))) && (((Integer)localContact.phoneDeleted.get(i)).intValue() != 1));
      }
      for (i = 1; ; i = 0)
      {
        if (i != 0)
          break label248;
        paramArrayList.add(localContact);
        break;
        i += 1;
        break label154;
        Collections.sort(paramArrayList, new Comparator()
        {
          public int compare(ContactsController.Contact paramContact1, ContactsController.Contact paramContact2)
          {
            String str2 = paramContact1.first_name;
            String str1 = str2;
            if (str2.length() == 0)
              str1 = paramContact1.last_name;
            str2 = paramContact2.first_name;
            paramContact1 = str2;
            if (str2.length() == 0)
              paramContact1 = paramContact2.last_name;
            return str1.compareTo(paramContact1);
          }
        });
        this.phoneBookContacts = paramArrayList;
        return;
      }
    }
  }

  public void SendContactsToserver()
  {
    Utilities.globalQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        Object localObject1 = MessagesStorage.getInstance().getPhoneBookContact();
        Object localObject2 = MessagesStorage.getInstance().getTelegramContacts();
        ContactsController.this.addContacts.clear();
        ContactsController.this.updatedContacts.clear();
        ContactsController.this.deletedContacts.clear();
        ContactsController.this.toImportContact.clear();
        localObject2 = ((ArrayList)localObject2).iterator();
        Object localObject3;
        while (((Iterator)localObject2).hasNext())
        {
          localObject3 = (TLRPC.User)((Iterator)localObject2).next();
          if ((((TLRPC.User)localObject3).phone == null) || (((TLRPC.User)localObject3).id == UserConfig.getCurrentUser().id))
            continue;
          ContactsController.access$1002(ContactsController.this, new u());
          ContactsController.this.vContact.b(((TLRPC.User)localObject3).id + "");
          ContactsController.this.vContact.c(((TLRPC.User)localObject3).first_name);
          ContactsController.this.vContact.d(((TLRPC.User)localObject3).last_name);
          ContactsController.this.vContact.a(((TLRPC.User)localObject3).phone);
          ContactsController.this.addContacts.add(ContactsController.this.vContact);
          ContactsController.this.toImportContact.put(((TLRPC.User)localObject3).phone, Integer.valueOf(((TLRPC.User)localObject3).id));
        }
        if (!((HashMap)localObject1).isEmpty())
        {
          localObject1 = ((HashMap)localObject1).entrySet().iterator();
          if (((Iterator)localObject1).hasNext())
          {
            localObject2 = (ContactsController.Contact)((Map.Entry)((Iterator)localObject1).next()).getValue();
            int i = 0;
            label285: if (i < ((ContactsController.Contact)localObject2).phones.size())
            {
              localObject3 = (String)((ContactsController.Contact)localObject2).shortPhones.get(i);
              if ((ContactsController.this.toImportContact.get(((ContactsController.Contact)localObject2).shortPhones.get(i)) == null) && ((((String)((ContactsController.Contact)localObject2).shortPhones.get(i)).length() <= 0) || (((String)((ContactsController.Contact)localObject2).shortPhones.get(i)).charAt(0) != '0') || (ContactsController.this.toImportContact.get("98" + ((String)((ContactsController.Contact)localObject2).shortPhones.get(i)).substring(1)) == null)))
                break label420;
            }
            while (true)
            {
              i += 1;
              break label285;
              break;
              label420: ContactsController.access$1002(ContactsController.this, new u());
              ContactsController.this.vContact.c(((ContactsController.Contact)localObject2).first_name);
              ContactsController.this.vContact.d(((ContactsController.Contact)localObject2).last_name);
              ContactsController.this.vContact.a((String)((ContactsController.Contact)localObject2).shortPhones.get(i));
              ContactsController.this.vContact.b("");
              ContactsController.this.addContacts.add(ContactsController.this.vContact);
            }
          }
        }
        org.vidogram.VidogramUi.b.a(ApplicationLoader.applicationContext).a(ContactsController.this.addContacts, ContactsController.this.deletedContacts, ContactsController.this.updatedContacts);
      }
    });
  }

  public void addContact(TLRPC.User paramUser)
  {
    if ((paramUser == null) || (TextUtils.isEmpty(paramUser.phone)))
      return;
    TLRPC.TL_contacts_importContacts localTL_contacts_importContacts = new TLRPC.TL_contacts_importContacts();
    ArrayList localArrayList = new ArrayList();
    TLRPC.TL_inputPhoneContact localTL_inputPhoneContact = new TLRPC.TL_inputPhoneContact();
    localTL_inputPhoneContact.phone = paramUser.phone;
    if (!localTL_inputPhoneContact.phone.startsWith("+"))
      localTL_inputPhoneContact.phone = ("+" + localTL_inputPhoneContact.phone);
    localTL_inputPhoneContact.first_name = paramUser.first_name;
    localTL_inputPhoneContact.last_name = paramUser.last_name;
    localTL_inputPhoneContact.client_id = 0L;
    localArrayList.add(localTL_inputPhoneContact);
    localTL_contacts_importContacts.contacts = localArrayList;
    localTL_contacts_importContacts.replace = false;
    ConnectionsManager.getInstance().sendRequest(localTL_contacts_importContacts, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        if (paramTL_error != null)
          return;
        paramTLObject = (TLRPC.TL_contacts_importedContacts)paramTLObject;
        MessagesStorage.getInstance().putUsersAndChats(paramTLObject.users, null, true, true);
        ContactsController.access$302(ContactsController.this, org.vidogram.VidogramUi.b.a(ApplicationLoader.applicationContext).b());
        ContactsController.access$402(ContactsController.this, org.vidogram.VidogramUi.b.a(ApplicationLoader.applicationContext).c());
        ContactsController.access$502(ContactsController.this, org.vidogram.VidogramUi.b.a(ApplicationLoader.applicationContext).d());
        int i = 0;
        while (i < paramTLObject.users.size())
        {
          paramTL_error = (TLRPC.User)paramTLObject.users.get(i);
          Utilities.phoneBookQueue.postRunnable(new Runnable(paramTL_error)
          {
            public void run()
            {
              ContactsController.this.addContactToPhoneBook(this.val$u, true);
            }
          });
          ContactsController.access$1002(ContactsController.this, new u());
          ContactsController.this.vContact.c(paramTL_error.first_name);
          ContactsController.this.vContact.d(paramTL_error.last_name);
          ContactsController.this.vContact.a(paramTL_error.phone);
          ContactsController.this.vContact.b(paramTL_error.id + "");
          ContactsController.this.addContacts.add(ContactsController.this.vContact);
          Object localObject = new TLRPC.TL_contact();
          ((TLRPC.TL_contact)localObject).user_id = paramTL_error.id;
          ArrayList localArrayList = new ArrayList();
          localArrayList.add(localObject);
          MessagesStorage.getInstance().putContacts(localArrayList, false);
          if (!TextUtils.isEmpty(paramTL_error.phone))
          {
            ContactsController.formatName(paramTL_error.first_name, paramTL_error.last_name);
            MessagesStorage.getInstance().applyPhoneBookUpdates(paramTL_error.phone, "");
            localObject = (ContactsController.Contact)ContactsController.this.contactsBookSPhones.get(paramTL_error.phone);
            if (localObject != null)
            {
              int j = ((ContactsController.Contact)localObject).shortPhones.indexOf(paramTL_error.phone);
              if (j != -1)
                ((ContactsController.Contact)localObject).phoneDeleted.set(j, Integer.valueOf(0));
            }
          }
          i += 1;
        }
        if (paramTLObject.users.size() > 0)
          org.vidogram.VidogramUi.b.a(ApplicationLoader.applicationContext).a(ContactsController.this.addContacts, ContactsController.this.deletedContacts, ContactsController.this.updatedContacts);
        AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
        {
          public void run()
          {
            Iterator localIterator = this.val$res.users.iterator();
            while (localIterator.hasNext())
            {
              TLRPC.User localUser = (TLRPC.User)localIterator.next();
              MessagesController.getInstance().putUser(localUser, false);
              if (ContactsController.this.contactsDict.get(localUser.id) != null)
                continue;
              TLRPC.TL_contact localTL_contact = new TLRPC.TL_contact();
              localTL_contact.user_id = localUser.id;
              ContactsController.this.contacts.add(localTL_contact);
              ContactsController.this.contactsDict.put(localTL_contact.user_id, localTL_contact);
            }
            ContactsController.this.buildContactsSectionsArrays(true);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
          }
        });
      }
    }
    , 6);
  }

  public long addContactToPhoneBook(TLRPC.User arg1, boolean paramBoolean)
  {
    long l2 = -1L;
    if ((this.currentAccount == null) || (??? == null) || (TextUtils.isEmpty(???.phone)));
    do
      return -1L;
    while (!hasContactsPermission());
    synchronized (this.observerLock)
    {
      this.ignoreChanges = true;
      ??? = ApplicationLoader.applicationContext.getContentResolver();
      if (!paramBoolean);
    }
    try
    {
      ((ContentResolver)???).delete(ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").appendQueryParameter("account_name", this.currentAccount.name).appendQueryParameter("account_type", this.currentAccount.type).build(), "sync2 = " + ???.id, null);
      localArrayList = new ArrayList();
      ContentProviderOperation.Builder localBuilder = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI);
      localBuilder.withValue("account_name", this.currentAccount.name);
      localBuilder.withValue("account_type", this.currentAccount.type);
      localBuilder.withValue("sync1", ???.phone);
      localBuilder.withValue("sync2", Integer.valueOf(???.id));
      localArrayList.add(localBuilder.build());
      localBuilder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
      localBuilder.withValueBackReference("raw_contact_id", 0);
      localBuilder.withValue("mimetype", "vnd.android.cursor.item/name");
      localBuilder.withValue("data2", ???.first_name);
      localBuilder.withValue("data3", ???.last_name);
      localArrayList.add(localBuilder.build());
      localBuilder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
      localBuilder.withValueBackReference("raw_contact_id", 0);
      localBuilder.withValue("mimetype", "vnd.android.cursor.item/vnd.org.telegram.messenger.android.profile");
      localBuilder.withValue("data1", Integer.valueOf(???.id));
      localBuilder.withValue("data2", "Telegram Profile");
      localBuilder.withValue("data3", "+" + ???.phone);
      localBuilder.withValue("data4", Integer.valueOf(???.id));
      localArrayList.add(localBuilder.build());
    }
    catch (Exception localException)
    {
      try
      {
        ArrayList localArrayList;
        ??? = ((ContentResolver)???).applyBatch("com.android.contacts", localArrayList);
        l1 = l2;
        if (??? != null)
        {
          l1 = l2;
          if (???.length > 0)
          {
            l1 = l2;
            if (???[0].uri != null)
              l1 = Long.parseLong(???[0].uri.getLastPathSegment());
          }
        }
        synchronized (this.observerLock)
        {
          this.ignoreChanges = false;
          return l1;
        }
        ??? = finally;
        monitorexit;
        throw ???;
        localException = localException;
        FileLog.e(localException);
      }
      catch (Exception )
      {
        while (true)
        {
          FileLog.e(???);
          long l1 = l2;
        }
      }
    }
  }

  public void checkAppAccount()
  {
    int m = 1;
    int k = 1;
    int j = 0;
    AccountManager localAccountManager = AccountManager.get(ApplicationLoader.applicationContext);
    try
    {
      Account[] arrayOfAccount1 = localAccountManager.getAccountsByType("org.telegram.account");
      if ((arrayOfAccount1 != null) && (arrayOfAccount1.length > 0))
      {
        i = 0;
        while (i < arrayOfAccount1.length)
        {
          localAccountManager.removeAccount(arrayOfAccount1[i], null, null);
          i += 1;
        }
      }
    }
    catch (Exception localException3)
    {
      int i;
      FileLog.e(localException2);
      Account[] arrayOfAccount2 = localAccountManager.getAccountsByType("org.telegram.messenger");
      Account localAccount;
      if (UserConfig.isClientActivated())
      {
        i = k;
        if (arrayOfAccount2.length == 1)
        {
          localAccount = arrayOfAccount2[0];
          if (localAccount.name.equals("" + UserConfig.getClientUserId()))
            break label172;
          i = k;
        }
      }
      while (true)
      {
        readContacts();
        label140: if (i != 0)
        {
          i = j;
          try
          {
            while (true)
              if (i < arrayOfAccount2.length)
              {
                localAccountManager.removeAccount(arrayOfAccount2[i], null, null);
                i += 1;
                continue;
                label172: this.currentAccount = localAccount;
                i = 0;
                break;
                i = m;
                if (arrayOfAccount2.length > 0)
                  break label140;
                i = 0;
              }
          }
          catch (Exception localException3)
          {
            FileLog.e(localException3);
            if (!UserConfig.isClientActivated());
          }
        }
      }
      try
      {
        this.currentAccount = new Account("" + UserConfig.getClientUserId(), "org.telegram.messenger");
        localAccountManager.addAccountExplicitly(this.currentAccount, "", null);
        return;
      }
      catch (Exception localException1)
      {
        FileLog.e(localException1);
      }
    }
  }

  public void checkContacts()
  {
    Utilities.globalQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        String str = itman.Vidofilm.b.a(ApplicationLoader.applicationContext).l();
        if ((str != null) && (str.equals("-1")))
          ContactsController.this.SendContactsToserver();
        do
        {
          return;
          if (ContactsController.this.checkContactsInternal())
          {
            FileLog.e("detected contacts change");
            ContactsController.getInstance().performSyncPhoneBook(ContactsController.getInstance().getContactsCopy(ContactsController.getInstance().contactsBook), true, false, true, false);
            return;
          }
          ContactsController.access$302(ContactsController.this, org.vidogram.VidogramUi.b.a(ApplicationLoader.applicationContext).b());
          ContactsController.access$402(ContactsController.this, org.vidogram.VidogramUi.b.a(ApplicationLoader.applicationContext).c());
          ContactsController.access$502(ContactsController.this, org.vidogram.VidogramUi.b.a(ApplicationLoader.applicationContext).d());
        }
        while ((ContactsController.this.addContacts.size() <= 0) && (ContactsController.this.updatedContacts.size() <= 0) && (ContactsController.this.deletedContacts.size() <= 0));
        org.vidogram.VidogramUi.b.a(ApplicationLoader.applicationContext).a(ContactsController.this.addContacts, ContactsController.this.deletedContacts, ContactsController.this.updatedContacts);
      }
    });
  }

  public void checkInviteText()
  {
    Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
    this.inviteText = ((SharedPreferences)localObject).getString("invitetext", null);
    int i = ((SharedPreferences)localObject).getInt("invitetexttime", 0);
    if ((!this.updatingInviteText) && ((this.inviteText == null) || (i + 86400 < (int)(System.currentTimeMillis() / 1000L))))
    {
      this.updatingInviteText = true;
      localObject = new TLRPC.TL_help_getInviteText();
      ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          if (paramTLObject != null)
          {
            paramTLObject = (TLRPC.TL_help_inviteText)paramTLObject;
            if (paramTLObject.message.length() != 0)
              AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
              {
                public void run()
                {
                  ContactsController.access$102(ContactsController.this, false);
                  SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
                  localEditor.putString("invitetext", this.val$res.message);
                  localEditor.putInt("invitetexttime", (int)(System.currentTimeMillis() / 1000L));
                  localEditor.commit();
                }
              });
          }
        }
      }
      , 2);
    }
  }

  public void cleanup()
  {
    this.contactsBook.clear();
    this.contactsBookSPhones.clear();
    this.phoneBookContacts.clear();
    this.contacts.clear();
    this.contactsDict.clear();
    this.usersSectionsDict.clear();
    this.usersMutualSectionsDict.clear();
    this.sortedUsersSectionsArray.clear();
    this.sortedUsersMutualSectionsArray.clear();
    this.delayedContactsUpdate.clear();
    this.contactsByPhone.clear();
    this.addContacts.clear();
    this.updatedContacts.clear();
    this.deletedContacts.clear();
    this.toImportContact.clear();
    this.loadingContacts = false;
    this.contactsSyncInProgress = false;
    this.contactsLoaded = false;
    this.contactsBookLoaded = false;
    this.lastContactsVersions = "";
    this.loadingDeleteInfo = 0;
    this.deleteAccountTTL = 0;
    this.loadingLastSeenInfo = 0;
    this.loadingGroupInfo = 0;
    this.loadingCallsInfo = 0;
    Utilities.globalQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        ContactsController.access$002(ContactsController.this, 0);
      }
    });
    this.privacyRules = null;
  }

  public void deleteAllAppAccounts()
  {
    try
    {
      AccountManager localAccountManager = AccountManager.get(ApplicationLoader.applicationContext);
      Account[] arrayOfAccount = localAccountManager.getAccountsByType("org.telegram.messenger");
      int i = 0;
      while (i < arrayOfAccount.length)
      {
        localAccountManager.removeAccount(arrayOfAccount[i], null, null);
        i += 1;
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public void deleteContact(ArrayList<TLRPC.User> paramArrayList)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty()))
      return;
    TLRPC.TL_contacts_deleteContacts localTL_contacts_deleteContacts = new TLRPC.TL_contacts_deleteContacts();
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = paramArrayList.iterator();
    while (localIterator.hasNext())
    {
      TLRPC.User localUser = (TLRPC.User)localIterator.next();
      TLRPC.InputUser localInputUser = MessagesController.getInputUser(localUser);
      if (localInputUser == null)
        continue;
      localArrayList.add(Integer.valueOf(localUser.id));
      localTL_contacts_deleteContacts.id.add(localInputUser);
    }
    ConnectionsManager.getInstance().sendRequest(localTL_contacts_deleteContacts, new RequestDelegate(localArrayList, paramArrayList)
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        if (paramTL_error != null)
          return;
        MessagesStorage.getInstance().deleteContacts(this.val$uids);
        Utilities.phoneBookQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            Iterator localIterator = ContactsController.18.this.val$users.iterator();
            while (localIterator.hasNext())
            {
              TLRPC.User localUser = (TLRPC.User)localIterator.next();
              ContactsController.this.deleteContactFromPhoneBook(localUser.id);
            }
          }
        });
        int i = 0;
        if (i < this.val$users.size())
        {
          paramTLObject = (TLRPC.User)this.val$users.get(i);
          if (TextUtils.isEmpty(paramTLObject.phone));
          while (true)
          {
            i += 1;
            break;
            UserObject.getUserName(paramTLObject);
            MessagesStorage.getInstance().applyPhoneBookUpdates(paramTLObject.phone, "");
            paramTL_error = (ContactsController.Contact)ContactsController.this.contactsBookSPhones.get(paramTLObject.phone);
            if (paramTL_error == null)
              continue;
            int j = paramTL_error.shortPhones.indexOf(paramTLObject.phone);
            if (j == -1)
              continue;
            paramTL_error.phoneDeleted.set(j, Integer.valueOf(1));
          }
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            Iterator localIterator = ContactsController.18.this.val$users.iterator();
            int i = 0;
            if (localIterator.hasNext())
            {
              TLRPC.User localUser = (TLRPC.User)localIterator.next();
              TLRPC.TL_contact localTL_contact = (TLRPC.TL_contact)ContactsController.this.contactsDict.get(localUser.id);
              if (localTL_contact == null)
                break label146;
              ContactsController.this.contacts.remove(localTL_contact);
              ContactsController.this.contactsDict.remove(localUser.id);
              i = 1;
            }
            label146: 
            while (true)
            {
              break;
              if (i != 0)
                ContactsController.this.buildContactsSectionsArrays(false);
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(1) });
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
              return;
            }
          }
        });
      }
    });
  }

  public void forceImportContacts()
  {
    Utilities.globalQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        ContactsController.getInstance().performSyncPhoneBook(new HashMap(), true, true, true, true);
      }
    });
  }

  public HashMap<Integer, Contact> getContactsCopy(HashMap<Integer, Contact> paramHashMap)
  {
    HashMap localHashMap = new HashMap();
    paramHashMap = paramHashMap.entrySet().iterator();
    while (paramHashMap.hasNext())
    {
      Object localObject = (Map.Entry)paramHashMap.next();
      Contact localContact = new Contact();
      localObject = (Contact)((Map.Entry)localObject).getValue();
      localContact.phoneDeleted.addAll(((Contact)localObject).phoneDeleted);
      localContact.phones.addAll(((Contact)localObject).phones);
      localContact.phoneTypes.addAll(((Contact)localObject).phoneTypes);
      localContact.shortPhones.addAll(((Contact)localObject).shortPhones);
      localContact.first_name = ((Contact)localObject).first_name;
      localContact.last_name = ((Contact)localObject).last_name;
      localContact.id = ((Contact)localObject).id;
      localHashMap.put(Integer.valueOf(localContact.id), localContact);
    }
    return (HashMap<Integer, Contact>)localHashMap;
  }

  public int getDeleteAccountTTL()
  {
    return this.deleteAccountTTL;
  }

  public String getInviteText()
  {
    String[] arrayOfString = itman.Vidofilm.b.a(ApplicationLoader.applicationContext).y().split("//Url//");
    String str1 = LocaleController.getString("CallFailureMessage", 2131166760);
    int j = arrayOfString.length;
    int i = 0;
    while (i < j)
    {
      String str2 = arrayOfString[i];
      str1 = str1 + "\n" + str2;
      i += 1;
    }
    return str1;
  }

  public boolean getLoadingCallsInfo()
  {
    return this.loadingCallsInfo != 2;
  }

  public boolean getLoadingDeleteInfo()
  {
    return this.loadingDeleteInfo != 2;
  }

  public boolean getLoadingGroupInfo()
  {
    return this.loadingGroupInfo != 2;
  }

  public boolean getLoadingLastSeenInfo()
  {
    return this.loadingLastSeenInfo != 2;
  }

  public ArrayList<TLRPC.PrivacyRule> getPrivacyRules(int paramInt)
  {
    if (paramInt == 2)
      return this.callPrivacyRules;
    if (paramInt == 1)
      return this.groupPrivacyRules;
    return this.privacyRules;
  }

  public boolean isLoadingContacts()
  {
    synchronized (loadContactsSync)
    {
      boolean bool = this.loadingContacts;
      return bool;
    }
  }

  public void loadContacts(boolean paramBoolean1, boolean paramBoolean2)
  {
    synchronized (loadContactsSync)
    {
      this.loadingContacts = true;
      if (paramBoolean1)
      {
        FileLog.e("load contacts from cache");
        MessagesStorage.getInstance().getContacts();
        return;
      }
    }
    FileLog.e("load contacts from server");
    TLRPC.TL_contacts_getContacts localTL_contacts_getContacts = new TLRPC.TL_contacts_getContacts();
    if (paramBoolean2);
    for (??? = ""; ; ??? = UserConfig.contactsHash)
    {
      localTL_contacts_getContacts.hash = ((String)???);
      ConnectionsManager.getInstance().sendRequest(localTL_contacts_getContacts, new RequestDelegate()
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          if (paramTL_error == null)
          {
            paramTLObject = (TLRPC.contacts_Contacts)paramTLObject;
            if ((paramTLObject instanceof TLRPC.TL_contacts_contactsNotModified))
            {
              ContactsController.this.contactsLoaded = true;
              if ((!ContactsController.this.delayedContactsUpdate.isEmpty()) && (ContactsController.this.contactsBookLoaded))
              {
                ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                ContactsController.this.delayedContactsUpdate.clear();
              }
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  synchronized (ContactsController.loadContactsSync)
                  {
                    ContactsController.access$702(ContactsController.this, false);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
                    return;
                  }
                }
              });
              FileLog.e("load contacts don't change");
            }
          }
          else
          {
            return;
          }
          if (UserConfig.contactsHash == "")
            ContactsController.access$902(ContactsController.this, paramTLObject.users);
          ContactsController.this.processLoadedContacts(paramTLObject.contacts, paramTLObject.users, 0);
        }
      });
      return;
    }
  }

  public void loadPrivacySettings()
  {
    Object localObject;
    if (this.loadingDeleteInfo == 0)
    {
      this.loadingDeleteInfo = 1;
      localObject = new TLRPC.TL_account_getAccountTTL();
      ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
          {
            public void run()
            {
              if (this.val$error == null)
              {
                TLRPC.TL_accountDaysTTL localTL_accountDaysTTL = (TLRPC.TL_accountDaysTTL)this.val$response;
                ContactsController.access$2402(ContactsController.this, localTL_accountDaysTTL.days);
                ContactsController.access$2502(ContactsController.this, 2);
              }
              while (true)
              {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
                return;
                ContactsController.access$2502(ContactsController.this, 0);
              }
            }
          });
        }
      });
    }
    if (this.loadingLastSeenInfo == 0)
    {
      this.loadingLastSeenInfo = 1;
      localObject = new TLRPC.TL_account_getPrivacy();
      ((TLRPC.TL_account_getPrivacy)localObject).key = new TLRPC.TL_inputPrivacyKeyStatusTimestamp();
      ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
          {
            public void run()
            {
              if (this.val$error == null)
              {
                TLRPC.TL_account_privacyRules localTL_account_privacyRules = (TLRPC.TL_account_privacyRules)this.val$response;
                MessagesController.getInstance().putUsers(localTL_account_privacyRules.users, false);
                ContactsController.access$2602(ContactsController.this, localTL_account_privacyRules.rules);
                ContactsController.access$2702(ContactsController.this, 2);
              }
              while (true)
              {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
                return;
                ContactsController.access$2702(ContactsController.this, 0);
              }
            }
          });
        }
      });
    }
    if (this.loadingCallsInfo == 0)
    {
      this.loadingCallsInfo = 1;
      localObject = new TLRPC.TL_account_getPrivacy();
      ((TLRPC.TL_account_getPrivacy)localObject).key = new TLRPC.TL_inputPrivacyKeyPhoneCall();
      ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
          {
            public void run()
            {
              if (this.val$error == null)
              {
                TLRPC.TL_account_privacyRules localTL_account_privacyRules = (TLRPC.TL_account_privacyRules)this.val$response;
                MessagesController.getInstance().putUsers(localTL_account_privacyRules.users, false);
                ContactsController.access$2802(ContactsController.this, localTL_account_privacyRules.rules);
                ContactsController.access$2902(ContactsController.this, 2);
              }
              while (true)
              {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
                return;
                ContactsController.access$2902(ContactsController.this, 0);
              }
            }
          });
        }
      });
    }
    if (this.loadingGroupInfo == 0)
    {
      this.loadingGroupInfo = 1;
      localObject = new TLRPC.TL_account_getPrivacy();
      ((TLRPC.TL_account_getPrivacy)localObject).key = new TLRPC.TL_inputPrivacyKeyChatInvite();
      ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
          {
            public void run()
            {
              if (this.val$error == null)
              {
                TLRPC.TL_account_privacyRules localTL_account_privacyRules = (TLRPC.TL_account_privacyRules)this.val$response;
                MessagesController.getInstance().putUsers(localTL_account_privacyRules.users, false);
                ContactsController.access$3002(ContactsController.this, localTL_account_privacyRules.rules);
                ContactsController.access$3102(ContactsController.this, 2);
              }
              while (true)
              {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
                return;
                ContactsController.access$3102(ContactsController.this, 0);
              }
            }
          });
        }
      });
    }
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
  }

  protected void markAsContacted(String paramString)
  {
    if (paramString == null)
      return;
    Utilities.phoneBookQueue.postRunnable(new Runnable(paramString)
    {
      public void run()
      {
        Uri localUri = Uri.parse(this.val$contactId);
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("last_time_contacted", Long.valueOf(System.currentTimeMillis()));
        ApplicationLoader.applicationContext.getContentResolver().update(localUri, localContentValues, null, null);
      }
    });
  }

  protected void performSyncPhoneBook(HashMap<Integer, Contact> paramHashMap, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    if ((!paramBoolean2) && (!this.contactsBookLoaded))
      return;
    Utilities.globalQueue.postRunnable(new Runnable(paramHashMap, paramBoolean3, paramBoolean1, paramBoolean2, paramBoolean4)
    {
      public void run()
      {
        Object localObject5;
        HashMap localHashMap4;
        Object localObject1;
        int i;
        HashMap localHashMap2;
        HashMap localHashMap3;
        int m;
        ArrayList localArrayList;
        try
        {
          ContactsController.access$302(ContactsController.this, org.vidogram.VidogramUi.b.a(ApplicationLoader.applicationContext).b());
          ContactsController.access$402(ContactsController.this, org.vidogram.VidogramUi.b.a(ApplicationLoader.applicationContext).c());
          ContactsController.access$502(ContactsController.this, org.vidogram.VidogramUi.b.a(ApplicationLoader.applicationContext).d());
          ContactsController.this.toImportContact.clear();
          if (ContactsController.this.getUserFromServer != null)
          {
            Iterator localIterator1 = ContactsController.this.getUserFromServer.iterator();
            while (localIterator1.hasNext())
            {
              localObject5 = (TLRPC.User)localIterator1.next();
              if ((((TLRPC.User)localObject5).phone == null) || (((TLRPC.User)localObject5).id == UserConfig.getClientUserId()))
                continue;
              ContactsController.access$1002(ContactsController.this, new u());
              ContactsController.this.vContact.b(((TLRPC.User)localObject5).id + "");
              ContactsController.this.vContact.c(((TLRPC.User)localObject5).first_name);
              ContactsController.this.vContact.d(((TLRPC.User)localObject5).last_name);
              ContactsController.this.vContact.a(((TLRPC.User)localObject5).phone);
              ContactsController.this.addContacts.add(ContactsController.this.vContact);
            }
          }
        }
        catch (Exception localObject1)
        {
          FileLog.e("tmessages", localException1);
          while (true)
          {
            localHashMap4 = new HashMap();
            localObject1 = this.val$contactHashMap.entrySet().iterator();
            while (((Iterator)localObject1).hasNext())
            {
              localObject5 = (ContactsController.Contact)((Map.Entry)((Iterator)localObject1).next()).getValue();
              i = 0;
              while (i < ((ContactsController.Contact)localObject5).shortPhones.size())
              {
                localHashMap4.put(((ContactsController.Contact)localObject5).shortPhones.get(i), localObject5);
                i += 1;
              }
            }
            ContactsController.access$902(ContactsController.this, null);
          }
          FileLog.e("start read contacts from phone");
          if (!this.val$schedule)
            ContactsController.this.checkContactsInternal();
          localHashMap2 = ContactsController.this.readContactsFromPhoneBook();
          localHashMap3 = new HashMap();
          m = this.val$contactHashMap.size();
          localArrayList = new ArrayList();
          if (this.val$contactHashMap.isEmpty())
            break label2627;
        }
        Iterator localIterator2 = localHashMap2.entrySet().iterator();
        Object localObject6;
        label608: int j;
        label619: int k;
        Object localObject7;
        label725: label730: label1125: label1512: Object localObject8;
        while (localIterator2.hasNext())
        {
          localObject1 = (Map.Entry)localIterator2.next();
          localObject6 = (Integer)((Map.Entry)localObject1).getKey();
          ContactsController.Contact localContact = (ContactsController.Contact)((Map.Entry)localObject1).getValue();
          localObject5 = (ContactsController.Contact)this.val$contactHashMap.get(localObject6);
          if (localObject5 != null)
            break label3361;
          i = 0;
          if (i >= localContact.shortPhones.size())
            break label3361;
          localObject1 = (ContactsController.Contact)localHashMap4.get(localContact.shortPhones.get(i));
          if (localObject1 != null)
          {
            i = ((ContactsController.Contact)localObject1).id;
            localObject5 = localObject1;
            localObject6 = Integer.valueOf(i);
            if ((localObject5 == null) || (((TextUtils.isEmpty(localContact.first_name)) || (((ContactsController.Contact)localObject5).first_name.equals(localContact.first_name))) && ((TextUtils.isEmpty(localContact.last_name)) || (((ContactsController.Contact)localObject5).last_name.equals(localContact.last_name)))))
              break label725;
            i = 1;
            if ((localObject5 != null) && (i == 0))
              break label1365;
            j = 0;
            if (j >= localContact.phones.size())
              break label1347;
            localObject1 = (String)localContact.shortPhones.get(j);
            localHashMap3.put(localObject1, localContact);
            if (localObject5 == null)
              break label730;
            k = ((ContactsController.Contact)localObject5).shortPhones.indexOf(localObject1);
            if (k == -1)
              break label730;
            localObject7 = (Integer)((ContactsController.Contact)localObject5).phoneDeleted.get(k);
            localContact.phoneDeleted.set(j, localObject7);
            if (((Integer)localObject7).intValue() != 1)
              break label730;
          }
          label1179: Object localObject2;
          while (true)
          {
            j += 1;
            break label619;
            i += 1;
            break;
            i = 0;
            break label608;
            if ((!this.val$request) || ((i == 0) && (ContactsController.this.contactsByPhone.containsKey(localObject1))))
              continue;
            localObject1 = new TLRPC.TL_inputPhoneContact();
            ((TLRPC.TL_inputPhoneContact)localObject1).client_id = localContact.id;
            ((TLRPC.TL_inputPhoneContact)localObject1).client_id |= j << 32;
            ((TLRPC.TL_inputPhoneContact)localObject1).first_name = localContact.first_name;
            ((TLRPC.TL_inputPhoneContact)localObject1).last_name = localContact.last_name;
            ((TLRPC.TL_inputPhoneContact)localObject1).phone = ((String)localContact.phones.get(j));
            localArrayList.add(localObject1);
            try
            {
              ContactsController.access$1002(ContactsController.this, new u());
              localObject7 = (TLRPC.TL_contact)ContactsController.this.contactsByPhone.get(localContact.shortPhones.get(j));
              localObject1 = localObject7;
              if (localObject7 == null)
              {
                localObject1 = localObject7;
                if (((String)localContact.shortPhones.get(j)).length() > 0)
                {
                  localObject1 = localObject7;
                  if (((String)localContact.shortPhones.get(j)).charAt(0) == '0')
                    localObject1 = (TLRPC.TL_contact)ContactsController.this.contactsByPhone.get("98" + ((String)localContact.shortPhones.get(j)).substring(1));
                }
              }
              if (localObject1 != null)
                break label1179;
              ContactsController.this.vContact.c(localContact.first_name);
              ContactsController.this.vContact.d(localContact.last_name);
              ContactsController.this.vContact.a((String)localContact.shortPhones.get(j));
              ContactsController.this.vContact.b("");
              if (i == 0)
                break label1125;
              ContactsController.this.toImportContact.put(localContact.shortPhones.get(j), Integer.valueOf(ContactsController.this.updatedContacts.size() * -1 - 1));
              ContactsController.this.updatedContacts.add(ContactsController.this.vContact);
            }
            catch (Exception localException2)
            {
              FileLog.e("tmessages", localException2);
            }
            continue;
            ContactsController.this.toImportContact.put(localContact.shortPhones.get(j), Integer.valueOf(ContactsController.this.addContacts.size()));
            ContactsController.this.addContacts.add(ContactsController.this.vContact);
            continue;
            if (i == 0)
              continue;
            localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(localException2.user_id));
            if (localObject2 != null)
              ContactsController.this.vContact.b(((TLRPC.User)localObject2).id + "");
            ContactsController.this.vContact.c(localContact.first_name);
            ContactsController.this.vContact.d(localContact.last_name);
            ContactsController.this.vContact.a((String)localContact.shortPhones.get(j));
            ContactsController.this.toImportContact.put(localContact.shortPhones.get(j), Integer.valueOf(ContactsController.this.updatedContacts.size() * -1 - 1));
            ContactsController.this.updatedContacts.add(ContactsController.this.vContact);
          }
          label1347: if (localObject5 == null)
            continue;
          this.val$contactHashMap.remove(localObject6);
          continue;
          label1365: i = 0;
          if (i < localContact.phones.size())
          {
            localObject2 = (String)localContact.shortPhones.get(i);
            localHashMap3.put(localObject2, localContact);
            j = ((ContactsController.Contact)localObject5).shortPhones.indexOf(localObject2);
            if (!this.val$request)
              break label3356;
            localObject7 = (TLRPC.TL_contact)ContactsController.this.contactsByPhone.get(localObject2);
            if (localObject7 == null)
              break label3356;
            localObject7 = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_contact)localObject7).user_id));
            if ((localObject7 == null) || (!TextUtils.isEmpty(((TLRPC.User)localObject7).first_name)) || (!TextUtils.isEmpty(((TLRPC.User)localObject7).last_name)) || ((TextUtils.isEmpty(localContact.first_name)) && (TextUtils.isEmpty(localContact.last_name))))
              break label3356;
            k = 1;
            j = -1;
            if (j == -1)
              if (this.val$request)
              {
                localObject7 = (TLRPC.TL_contact)ContactsController.this.contactsByPhone.get(localObject2);
                localObject2 = localObject7;
                if (localObject7 == null)
                  localObject2 = localObject7;
              }
            while (true)
            {
              Object localObject3;
              try
              {
                if (((String)localContact.shortPhones.get(i)).length() <= 0)
                  continue;
                localObject2 = localObject7;
                if (((String)localContact.shortPhones.get(i)).charAt(0) != '0')
                  continue;
                localObject2 = (TLRPC.TL_contact)ContactsController.this.contactsByPhone.get("98" + ((String)localContact.shortPhones.get(i)).substring(1));
                if ((k != 0) || (localObject2 == null))
                  continue;
                localObject8 = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_contact)localObject2).user_id));
                if (localObject8 == null)
                  continue;
                if (((TLRPC.User)localObject8).first_name == null)
                  continue;
                localObject7 = ((TLRPC.User)localObject8).first_name;
                if (((TLRPC.User)localObject8).last_name == null)
                  continue;
                localObject8 = ((TLRPC.User)localObject8).last_name;
                if (((!((String)localObject7).equals(localContact.first_name)) || (!((String)localObject8).equals(localContact.last_name))) && ((!TextUtils.isEmpty(localContact.first_name)) || (!TextUtils.isEmpty(localContact.last_name))))
                  continue;
                i += 1;
              }
              catch (Exception localObject3)
              {
                FileLog.e("tmessages", localException3);
                localObject3 = localObject7;
                continue;
                localObject7 = "";
                continue;
                localObject8 = "";
                continue;
                localObject7 = new TLRPC.TL_inputPhoneContact();
                ((TLRPC.TL_inputPhoneContact)localObject7).client_id = localContact.id;
                ((TLRPC.TL_inputPhoneContact)localObject7).client_id |= i << 32;
                ((TLRPC.TL_inputPhoneContact)localObject7).first_name = localContact.first_name;
                ((TLRPC.TL_inputPhoneContact)localObject7).last_name = localContact.last_name;
                ((TLRPC.TL_inputPhoneContact)localObject7).phone = ((String)localContact.phones.get(i));
                localArrayList.add(localObject7);
                if (localObject3 != null)
                  break label3353;
              }
              try
              {
                if ((((String)localContact.shortPhones.get(i)).length() <= 0) || (((String)localContact.shortPhones.get(i)).charAt(0) != '0'))
                  break label3353;
                localObject3 = (TLRPC.TL_contact)ContactsController.this.contactsByPhone.get("98" + ((String)localContact.shortPhones.get(i)).substring(1));
                label1967: ContactsController.access$1002(ContactsController.this, new u());
                ContactsController.this.vContact.c(localContact.first_name);
                ContactsController.this.vContact.d(localContact.last_name);
                if (localObject3 != null)
                  continue;
                ContactsController.this.vContact.b("");
                ContactsController.this.vContact.a((String)localContact.shortPhones.get(i));
                ContactsController.this.toImportContact.put(localContact.shortPhones.get(i), Integer.valueOf(ContactsController.this.addContacts.size()));
                ContactsController.this.addContacts.add(ContactsController.this.vContact);
              }
              catch (Exception localException4)
              {
                FileLog.e("tmessages", localException4);
              }
              continue;
              localContact.phoneDeleted.set(i, ((ContactsController.Contact)localObject5).phoneDeleted.get(j));
              ((ContactsController.Contact)localObject5).phones.remove(j);
              ((ContactsController.Contact)localObject5).shortPhones.remove(j);
              ((ContactsController.Contact)localObject5).phoneDeleted.remove(j);
              ((ContactsController.Contact)localObject5).phoneTypes.remove(j);
            }
          }
          if (!((ContactsController.Contact)localObject5).phones.isEmpty())
            continue;
          this.val$contactHashMap.remove(localObject6);
        }
        while (true)
        {
          try
          {
            HashMap localHashMap1 = org.vidogram.VidogramUi.b.a(ApplicationLoader.applicationContext).e();
            localObject5 = this.val$contactHashMap.entrySet().iterator();
            if (!((Iterator)localObject5).hasNext())
              continue;
            localObject6 = (ContactsController.Contact)((Map.Entry)((Iterator)localObject5).next()).getValue();
            i = 0;
            if (i >= ((ContactsController.Contact)localObject6).phones.size())
              continue;
            if ((localHashMap3.get(((ContactsController.Contact)localObject6).shortPhones.get(i)) != null) || (((Integer)((ContactsController.Contact)localObject6).phoneDeleted.get(i)).intValue() == 1) || (localHashMap1.get(((ContactsController.Contact)localObject6).shortPhones.get(i)) != null))
              break label3364;
            MessagesStorage.getInstance().deleteCachedPhoneBook((String)((ContactsController.Contact)localObject6).shortPhones.get(i));
            localObject7 = (String)((ContactsController.Contact)localObject6).shortPhones.get(i);
            ContactsController.this.deletedContacts.add(localObject7);
          }
          catch (Exception localException5)
          {
            FileLog.e("tmessages", localException5);
            if (this.val$first)
              break label2427;
          }
          if ((this.val$contactHashMap.isEmpty()) && (localArrayList.isEmpty()) && (m == localHashMap2.size()))
            FileLog.e("contacts not changed!");
          while (true)
          {
            return;
            label2427: if ((this.val$request) && (!this.val$contactHashMap.isEmpty()) && (!localHashMap2.isEmpty()) && (localArrayList.isEmpty()))
              MessagesStorage.getInstance().putCachedPhoneBook(localHashMap2);
            while (true)
            {
              FileLog.e("done processing contacts");
              if (!this.val$request)
                break label3316;
              if (localArrayList.isEmpty())
                break;
              Object localObject4 = new HashMap(localHashMap2);
              ContactsController.access$002(ContactsController.this, 0);
              j = (int)Math.ceil(localArrayList.size() / 500.0F);
              i = 0;
              while (i < j)
              {
                localObject5 = new ArrayList();
                ((ArrayList)localObject5).addAll(localArrayList.subList(i * 500, Math.min((i + 1) * 500, localArrayList.size())));
                localObject6 = new TLRPC.TL_contacts_importContacts();
                ((TLRPC.TL_contacts_importContacts)localObject6).contacts = ((ArrayList)localObject5);
                ((TLRPC.TL_contacts_importContacts)localObject6).replace = false;
                ConnectionsManager.getInstance().sendRequest((TLObject)localObject6, new RequestDelegate((HashMap)localObject4, j, localHashMap3, localHashMap2)
                {
                  public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
                  {
                    int j = 0;
                    ContactsController.access$008(ContactsController.this);
                    int i;
                    if (paramTL_error == null)
                    {
                      FileLog.e("contacts imported");
                      paramTLObject = (TLRPC.TL_contacts_importedContacts)paramTLObject;
                      if (!paramTLObject.retry_contacts.isEmpty())
                      {
                        i = 0;
                        while (i < paramTLObject.retry_contacts.size())
                        {
                          long l = ((Long)paramTLObject.retry_contacts.get(i)).longValue();
                          this.val$contactsMapToSave.remove(Integer.valueOf((int)l));
                          i += 1;
                        }
                      }
                      if ((ContactsController.this.completedRequestsCount == this.val$count) && (!this.val$contactsMapToSave.isEmpty()))
                        MessagesStorage.getInstance().putCachedPhoneBook(this.val$contactsMapToSave);
                      MessagesStorage.getInstance().putUsersAndChats(paramTLObject.users, null, true, true);
                      paramTL_error = new ArrayList();
                      i = 0;
                      while (i < paramTLObject.imported.size())
                      {
                        TLRPC.TL_contact localTL_contact = new TLRPC.TL_contact();
                        localTL_contact.user_id = ((TLRPC.TL_importedContact)paramTLObject.imported.get(i)).user_id;
                        paramTL_error.add(localTL_contact);
                        i += 1;
                      }
                      ContactsController.this.processLoadedContacts(paramTL_error, paramTLObject.users, 2);
                      i = j;
                    }
                    while (true)
                    {
                      if (i < paramTLObject.users.size())
                      {
                        try
                        {
                          if (ContactsController.this.toImportContact.get(((TLRPC.User)paramTLObject.users.get(i)).phone) != null)
                          {
                            j = ((Integer)ContactsController.this.toImportContact.get(((TLRPC.User)paramTLObject.users.get(i)).phone)).intValue();
                            if (j >= 0)
                            {
                              ((u)ContactsController.this.addContacts.get(j)).b(((TLRPC.User)paramTLObject.users.get(i)).id + "");
                              break label908;
                            }
                            ((u)ContactsController.this.updatedContacts.get((j + 1) * -1)).b(((TLRPC.User)paramTLObject.users.get(i)).id + "");
                          }
                        }
                        catch (Exception paramTL_error)
                        {
                          ContactsController.access$1002(ContactsController.this, new u());
                          ContactsController.this.vContact.c(((TLRPC.User)paramTLObject.users.get(i)).first_name);
                          ContactsController.this.vContact.d(((TLRPC.User)paramTLObject.users.get(i)).last_name);
                          ContactsController.this.vContact.b(((TLRPC.User)paramTLObject.users.get(i)).id + "");
                          ContactsController.this.vContact.a(((TLRPC.User)paramTLObject.users.get(i)).phone);
                          ContactsController.this.addContacts.add(ContactsController.this.vContact);
                          FirebaseCrash.a(paramTL_error);
                        }
                        ContactsController.access$1002(ContactsController.this, new u());
                        ContactsController.this.vContact.c(((TLRPC.User)paramTLObject.users.get(i)).first_name);
                        ContactsController.this.vContact.d(((TLRPC.User)paramTLObject.users.get(i)).last_name);
                        ContactsController.this.vContact.b(((TLRPC.User)paramTLObject.users.get(i)).id + "");
                        ContactsController.this.vContact.a(((TLRPC.User)paramTLObject.users.get(i)).phone);
                        ContactsController.this.addContacts.add(ContactsController.this.vContact);
                        break label908;
                        FileLog.e("import contacts error " + paramTL_error.text);
                      }
                      else
                      {
                        if (ContactsController.this.completedRequestsCount == this.val$count)
                          Utilities.stageQueue.postRunnable(new Runnable()
                          {
                            public void run()
                            {
                              ContactsController.this.contactsBookSPhones = ContactsController.6.2.this.val$contactsBookShort;
                              ContactsController.this.contactsBook = ContactsController.6.2.this.val$contactsMap;
                              ContactsController.access$1202(ContactsController.this, false);
                              ContactsController.access$1302(ContactsController.this, true);
                              if (ContactsController.6.this.val$first)
                                ContactsController.this.contactsLoaded = true;
                              if ((!ContactsController.this.delayedContactsUpdate.isEmpty()) && (ContactsController.this.contactsLoaded))
                              {
                                ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                                ContactsController.this.delayedContactsUpdate.clear();
                              }
                            }
                          });
                        if ((ContactsController.this.addContacts.size() > 0) || (ContactsController.this.updatedContacts.size() > 0) || (ContactsController.this.deletedContacts.size() > 0))
                          org.vidogram.VidogramUi.b.a(ApplicationLoader.applicationContext).a(ContactsController.this.addContacts, ContactsController.this.deletedContacts, ContactsController.this.updatedContacts);
                        return;
                      }
                      label908: i += 1;
                    }
                  }
                }
                , 6);
                i += 1;
              }
              label2627: if (!this.val$request)
                continue;
              localObject6 = localHashMap2.entrySet().iterator();
              label2646: if (((Iterator)localObject6).hasNext())
              {
                localObject4 = (Map.Entry)((Iterator)localObject6).next();
                localObject7 = (ContactsController.Contact)((Map.Entry)localObject4).getValue();
                j = ((Integer)((Map.Entry)localObject4).getKey()).intValue();
                i = 0;
                if (i < ((ContactsController.Contact)localObject7).phones.size())
                {
                  ContactsController.access$1002(ContactsController.this, new u());
                  ContactsController.this.vContact.c(((ContactsController.Contact)localObject7).first_name);
                  ContactsController.this.vContact.d(((ContactsController.Contact)localObject7).last_name);
                  ContactsController.this.vContact.a((String)((ContactsController.Contact)localObject7).shortPhones.get(i));
                  if (this.val$force)
                    break label3133;
                  localObject4 = (String)((ContactsController.Contact)localObject7).shortPhones.get(i);
                  localObject5 = (TLRPC.TL_contact)ContactsController.this.contactsByPhone.get(localObject4);
                  localObject4 = localObject5;
                  if (localObject5 == null)
                  {
                    localObject4 = localObject5;
                    if (((String)((ContactsController.Contact)localObject7).shortPhones.get(i)).length() > 0)
                    {
                      localObject4 = localObject5;
                      if (((String)((ContactsController.Contact)localObject7).shortPhones.get(i)).charAt(0) == '0')
                        localObject4 = (TLRPC.TL_contact)ContactsController.this.contactsByPhone.get("98" + ((String)((ContactsController.Contact)localObject7).shortPhones.get(i)).substring(1));
                    }
                  }
                  if (localObject4 == null)
                    break label3082;
                  localObject8 = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_contact)localObject4).user_id));
                  if (localObject8 == null)
                    break label3133;
                  ContactsController.this.vContact.b(((TLRPC.User)localObject8).id + "");
                  if (((TLRPC.User)localObject8).first_name == null)
                    break label3068;
                  localObject4 = ((TLRPC.User)localObject8).first_name;
                  if (((TLRPC.User)localObject8).last_name == null)
                    break label3075;
                  localObject5 = ((TLRPC.User)localObject8).last_name;
                  if ((localObject8 == null) || (((!((String)localObject4).equals(((ContactsController.Contact)localObject7).first_name)) || (!((String)localObject5).equals(((ContactsController.Contact)localObject7).last_name))) && ((!TextUtils.isEmpty(((ContactsController.Contact)localObject7).first_name)) || (!TextUtils.isEmpty(((ContactsController.Contact)localObject7).last_name)))))
                    break label3133;
                }
              }
              while (true)
              {
                label2696: label2993: label3008: i += 1;
                break label2696;
                break label2646;
                break;
                label3068: localObject4 = "";
                break label2993;
                label3075: localObject5 = "";
                break label3008;
                label3082: ContactsController.this.toImportContact.put(((ContactsController.Contact)localObject7).shortPhones.get(i), Integer.valueOf(ContactsController.this.addContacts.size()));
                ContactsController.this.addContacts.add(ContactsController.this.vContact);
                label3133: localObject4 = new TLRPC.TL_inputPhoneContact();
                ((TLRPC.TL_inputPhoneContact)localObject4).client_id = j;
                ((TLRPC.TL_inputPhoneContact)localObject4).client_id |= i << 32;
                ((TLRPC.TL_inputPhoneContact)localObject4).first_name = ((ContactsController.Contact)localObject7).first_name;
                ((TLRPC.TL_inputPhoneContact)localObject4).last_name = ((ContactsController.Contact)localObject7).last_name;
                ((TLRPC.TL_inputPhoneContact)localObject4).phone = ((String)((ContactsController.Contact)localObject7).phones.get(i));
                localArrayList.add(localObject4);
              }
            }
            Utilities.stageQueue.postRunnable(new Runnable(localHashMap3, localHashMap2)
            {
              public void run()
              {
                ContactsController.this.contactsBookSPhones = this.val$contactsBookShort;
                ContactsController.this.contactsBook = this.val$contactsMap;
                ContactsController.access$1202(ContactsController.this, false);
                ContactsController.access$1302(ContactsController.this, true);
                if (ContactsController.6.this.val$first)
                  ContactsController.this.contactsLoaded = true;
                if ((!ContactsController.this.delayedContactsUpdate.isEmpty()) && (ContactsController.this.contactsLoaded))
                {
                  ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                  ContactsController.this.delayedContactsUpdate.clear();
                }
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    ContactsController.this.updateUnregisteredContacts(ContactsController.this.contacts);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
                  }
                });
              }
            });
            while (((ContactsController.this.addContacts.size() > 0) || (ContactsController.this.updatedContacts.size() > 0) || (ContactsController.this.deletedContacts.size() > 0)) && ((!this.val$request) || (localArrayList.size() == 0)))
            {
              org.vidogram.VidogramUi.b.a(ApplicationLoader.applicationContext).a(ContactsController.this.addContacts, ContactsController.this.deletedContacts, ContactsController.this.updatedContacts);
              return;
              label3316: Utilities.stageQueue.postRunnable(new Runnable(localHashMap3, localHashMap2)
              {
                public void run()
                {
                  ContactsController.this.contactsBookSPhones = this.val$contactsBookShort;
                  ContactsController.this.contactsBook = this.val$contactsMap;
                  ContactsController.access$1202(ContactsController.this, false);
                  ContactsController.access$1302(ContactsController.this, true);
                  if (ContactsController.6.this.val$first)
                    ContactsController.this.contactsLoaded = true;
                  if ((!ContactsController.this.delayedContactsUpdate.isEmpty()) && (ContactsController.this.contactsLoaded) && (ContactsController.this.contactsBookLoaded))
                  {
                    ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                    ContactsController.this.delayedContactsUpdate.clear();
                  }
                }
              });
              if (localHashMap2.isEmpty())
                continue;
              MessagesStorage.getInstance().putCachedPhoneBook(localHashMap2);
            }
          }
          label3353: break label1967;
          label3356: k = 0;
          break label1512;
          label3361: break;
          label3364: i += 1;
        }
      }
    });
  }

  public void processContactsUpdates(ArrayList<Integer> paramArrayList, ConcurrentHashMap<Integer, TLRPC.User> paramConcurrentHashMap)
  {
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    Iterator localIterator = paramArrayList.iterator();
    while (localIterator.hasNext())
    {
      Integer localInteger = (Integer)localIterator.next();
      if (localInteger.intValue() > 0)
      {
        TLRPC.TL_contact localTL_contact = new TLRPC.TL_contact();
        localTL_contact.user_id = localInteger.intValue();
        localArrayList1.add(localTL_contact);
        if (this.delayedContactsUpdate.isEmpty())
          continue;
        i = this.delayedContactsUpdate.indexOf(Integer.valueOf(-localInteger.intValue()));
        if (i == -1)
          continue;
        this.delayedContactsUpdate.remove(i);
        continue;
      }
      if (localInteger.intValue() >= 0)
        continue;
      localArrayList2.add(Integer.valueOf(-localInteger.intValue()));
      if (this.delayedContactsUpdate.isEmpty())
        continue;
      int i = this.delayedContactsUpdate.indexOf(Integer.valueOf(-localInteger.intValue()));
      if (i == -1)
        continue;
      this.delayedContactsUpdate.remove(i);
    }
    if (!localArrayList2.isEmpty())
      MessagesStorage.getInstance().deleteContacts(localArrayList2);
    if (!localArrayList1.isEmpty())
      MessagesStorage.getInstance().putContacts(localArrayList1, false);
    if ((!this.contactsLoaded) || (!this.contactsBookLoaded))
    {
      this.delayedContactsUpdate.addAll(paramArrayList);
      FileLog.e("delay update - contacts add = " + localArrayList1.size() + " delete = " + localArrayList2.size());
      return;
    }
    applyContactsUpdates(paramArrayList, paramConcurrentHashMap, localArrayList1, localArrayList2);
  }

  public void processLoadedContacts(ArrayList<TLRPC.TL_contact> paramArrayList, ArrayList<TLRPC.User> paramArrayList1, int paramInt)
  {
    AndroidUtilities.runOnUIThread(new Runnable(paramArrayList1, paramInt, paramArrayList)
    {
      public void run()
      {
        boolean bool = true;
        int k = 0;
        Object localObject1 = MessagesController.getInstance();
        Object localObject2 = this.val$usersArr;
        if (this.val$from == 1);
        int i;
        while (true)
        {
          ((MessagesController)localObject1).putUsers((ArrayList)localObject2, bool);
          localObject1 = new HashMap();
          bool = this.val$contactsArr.isEmpty();
          i = k;
          if (ContactsController.this.contacts.isEmpty())
            break;
          i = 0;
          while (true)
            if (i < this.val$contactsArr.size())
            {
              localObject2 = (TLRPC.TL_contact)this.val$contactsArr.get(i);
              int j = i;
              if (ContactsController.this.contactsDict.get(((TLRPC.TL_contact)localObject2).user_id) != null)
              {
                this.val$contactsArr.remove(i);
                j = i - 1;
              }
              i = j + 1;
              continue;
              bool = false;
              break;
            }
          this.val$contactsArr.addAll(ContactsController.this.contacts);
          i = k;
        }
        while (i < this.val$contactsArr.size())
        {
          localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_contact)this.val$contactsArr.get(i)).user_id));
          if (localObject2 != null)
            ((HashMap)localObject1).put(Integer.valueOf(((TLRPC.User)localObject2).id), localObject2);
          i += 1;
        }
        Utilities.stageQueue.postRunnable(new Runnable((HashMap)localObject1, bool)
        {
          public void run()
          {
            FileLog.e("done loading contacts");
            if ((ContactsController.8.this.val$from == 1) && ((ContactsController.8.this.val$contactsArr.isEmpty()) || (Math.abs(System.currentTimeMillis() / 1000L - UserConfig.lastContactsSyncTime) >= 86400L)))
            {
              ContactsController.this.loadContacts(false, true);
              return;
            }
            if (ContactsController.8.this.val$from == 0)
            {
              UserConfig.lastContactsSyncTime = (int)(System.currentTimeMillis() / 1000L);
              UserConfig.saveConfig(false);
            }
            Object localObject1 = ContactsController.8.this.val$contactsArr.iterator();
            while (((Iterator)localObject1).hasNext())
            {
              localObject2 = (TLRPC.TL_contact)((Iterator)localObject1).next();
              if ((this.val$usersDict.get(Integer.valueOf(((TLRPC.TL_contact)localObject2).user_id)) != null) || (((TLRPC.TL_contact)localObject2).user_id == UserConfig.getClientUserId()))
                continue;
              ContactsController.this.loadContacts(false, true);
              FileLog.e("contacts are broken, load from server");
              return;
            }
            Object localObject3;
            if (ContactsController.8.this.val$from != 1)
            {
              MessagesStorage.getInstance().putUsersAndChats(ContactsController.8.this.val$usersArr, null, true, true);
              localObject1 = MessagesStorage.getInstance();
              localObject2 = ContactsController.8.this.val$contactsArr;
              if (ContactsController.8.this.val$from != 2);
              for (boolean bool = true; ; bool = false)
              {
                ((MessagesStorage)localObject1).putContacts((ArrayList)localObject2, bool);
                Collections.sort(ContactsController.8.this.val$contactsArr, new Comparator()
                {
                  public int compare(TLRPC.TL_contact paramTL_contact1, TLRPC.TL_contact paramTL_contact2)
                  {
                    if (paramTL_contact1.user_id > paramTL_contact2.user_id)
                      return 1;
                    if (paramTL_contact1.user_id < paramTL_contact2.user_id)
                      return -1;
                    return 0;
                  }
                });
                localObject1 = new StringBuilder();
                localObject2 = ContactsController.8.this.val$contactsArr.iterator();
                while (((Iterator)localObject2).hasNext())
                {
                  localObject3 = (TLRPC.TL_contact)((Iterator)localObject2).next();
                  if (((StringBuilder)localObject1).length() != 0)
                    ((StringBuilder)localObject1).append(",");
                  ((StringBuilder)localObject1).append(((TLRPC.TL_contact)localObject3).user_id);
                }
              }
              UserConfig.contactsHash = Utilities.MD5(((StringBuilder)localObject1).toString());
              UserConfig.saveConfig(false);
            }
            Collections.sort(ContactsController.8.this.val$contactsArr, new Comparator()
            {
              public int compare(TLRPC.TL_contact paramTL_contact1, TLRPC.TL_contact paramTL_contact2)
              {
                paramTL_contact1 = (TLRPC.User)ContactsController.8.1.this.val$usersDict.get(Integer.valueOf(paramTL_contact1.user_id));
                paramTL_contact2 = (TLRPC.User)ContactsController.8.1.this.val$usersDict.get(Integer.valueOf(paramTL_contact2.user_id));
                return UserObject.getFirstName(paramTL_contact1).compareTo(UserObject.getFirstName(paramTL_contact2));
              }
            });
            SparseArray localSparseArray = new SparseArray();
            HashMap localHashMap1 = new HashMap();
            HashMap localHashMap2 = new HashMap();
            ArrayList localArrayList2 = new ArrayList();
            ArrayList localArrayList3 = new ArrayList();
            if (!ContactsController.this.contactsBookLoaded);
            for (Object localObject2 = new HashMap(); ; localObject2 = null)
            {
              int i = 0;
              while (i < ContactsController.8.this.val$contactsArr.size())
              {
                TLRPC.TL_contact localTL_contact = (TLRPC.TL_contact)ContactsController.8.this.val$contactsArr.get(i);
                TLRPC.User localUser = (TLRPC.User)this.val$usersDict.get(Integer.valueOf(localTL_contact.user_id));
                if (localUser == null)
                {
                  i += 1;
                  continue;
                }
                localSparseArray.put(localTL_contact.user_id, localTL_contact);
                if ((localObject2 != null) && (!TextUtils.isEmpty(localUser.phone)))
                  ((HashMap)localObject2).put(localUser.phone, localTL_contact);
                localObject3 = UserObject.getFirstName(localUser);
                localObject1 = localObject3;
                if (((String)localObject3).length() > 1)
                  localObject1 = ((String)localObject3).substring(0, 1);
                if (((String)localObject1).length() == 0);
                for (localObject1 = "#"; ; localObject1 = ((String)localObject1).toUpperCase())
                {
                  localObject3 = (String)ContactsController.this.sectionsToReplace.get(localObject1);
                  if (localObject3 != null)
                    localObject1 = localObject3;
                  ArrayList localArrayList1 = (ArrayList)localHashMap1.get(localObject1);
                  localObject3 = localArrayList1;
                  if (localArrayList1 == null)
                  {
                    localObject3 = new ArrayList();
                    localHashMap1.put(localObject1, localObject3);
                    localArrayList2.add(localObject1);
                  }
                  ((ArrayList)localObject3).add(localTL_contact);
                  if (!localUser.mutual_contact)
                    break;
                  localArrayList1 = (ArrayList)localHashMap2.get(localObject1);
                  localObject3 = localArrayList1;
                  if (localArrayList1 == null)
                  {
                    localObject3 = new ArrayList();
                    localHashMap2.put(localObject1, localObject3);
                    localArrayList3.add(localObject1);
                  }
                  ((ArrayList)localObject3).add(localTL_contact);
                  break;
                }
              }
              Collections.sort(localArrayList2, new Comparator()
              {
                public int compare(String paramString1, String paramString2)
                {
                  int i = paramString1.charAt(0);
                  int j = paramString2.charAt(0);
                  if (i == 35)
                    return 1;
                  if (j == 35)
                    return -1;
                  return paramString1.compareTo(paramString2);
                }
              });
              Collections.sort(localArrayList3, new Comparator()
              {
                public int compare(String paramString1, String paramString2)
                {
                  int i = paramString1.charAt(0);
                  int j = paramString2.charAt(0);
                  if (i == 35)
                    return 1;
                  if (j == 35)
                    return -1;
                  return paramString1.compareTo(paramString2);
                }
              });
              AndroidUtilities.runOnUIThread(new Runnable(localSparseArray, localHashMap1, localHashMap2, localArrayList2, localArrayList3)
              {
                public void run()
                {
                  ContactsController.this.contacts = ContactsController.8.this.val$contactsArr;
                  ContactsController.this.contactsDict = this.val$contactsDictionary;
                  ContactsController.this.usersSectionsDict = this.val$sectionsDict;
                  ContactsController.this.usersMutualSectionsDict = this.val$sectionsDictMutual;
                  ContactsController.this.sortedUsersSectionsArray = this.val$sortedSectionsArray;
                  ContactsController.this.sortedUsersMutualSectionsArray = this.val$sortedSectionsArrayMutual;
                  if (ContactsController.8.this.val$from != 2);
                  synchronized (ContactsController.loadContactsSync)
                  {
                    ContactsController.access$702(ContactsController.this, false);
                    ContactsController.this.performWriteContactsToPhoneBook();
                    ContactsController.this.updateUnregisteredContacts(ContactsController.8.this.val$contactsArr);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
                    if ((ContactsController.8.this.val$from != 1) && (!ContactsController.8.1.this.val$isEmpty))
                    {
                      ContactsController.this.saveContactsLoadTime();
                      return;
                    }
                  }
                  ContactsController.this.reloadContactsStatusesMaybe();
                }
              });
              if ((!ContactsController.this.delayedContactsUpdate.isEmpty()) && (ContactsController.this.contactsLoaded) && (ContactsController.this.contactsBookLoaded))
              {
                ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                ContactsController.this.delayedContactsUpdate.clear();
              }
              if (localObject2 != null)
              {
                AndroidUtilities.runOnUIThread(new Runnable((HashMap)localObject2)
                {
                  public void run()
                  {
                    Utilities.globalQueue.postRunnable(new Runnable()
                    {
                      public void run()
                      {
                        ContactsController.this.contactsByPhone = ContactsController.8.1.6.this.val$contactsByPhonesDictFinal;
                      }
                    });
                    if (ContactsController.this.contactsSyncInProgress)
                      return;
                    ContactsController.access$1202(ContactsController.this, true);
                    MessagesStorage.getInstance().getCachedPhoneBook();
                  }
                });
                return;
              }
              ContactsController.this.contactsLoaded = true;
              return;
            }
          }
        });
      }
    });
  }

  public void readContacts()
  {
    synchronized (loadContactsSync)
    {
      if (this.loadingContacts)
        return;
      this.loadingContacts = true;
      Utilities.stageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          if ((!ContactsController.this.contacts.isEmpty()) || (ContactsController.this.contactsLoaded))
            synchronized (ContactsController.loadContactsSync)
            {
              ContactsController.access$702(ContactsController.this, false);
              return;
            }
          ContactsController.this.loadContacts(true, false);
        }
      });
      return;
    }
  }

  public void reloadContactsStatuses()
  {
    saveContactsLoadTime();
    MessagesController.getInstance().clearFullUsers();
    SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
    localEditor.putBoolean("needGetStatuses", true).commit();
    TLRPC.TL_contacts_getStatuses localTL_contacts_getStatuses = new TLRPC.TL_contacts_getStatuses();
    ConnectionsManager.getInstance().sendRequest(localTL_contacts_getStatuses, new RequestDelegate(localEditor)
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        if (paramTL_error == null)
          AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
          {
            public void run()
            {
              ContactsController.19.this.val$editor.remove("needGetStatuses").commit();
              Object localObject1 = (TLRPC.Vector)this.val$response;
              if (!((TLRPC.Vector)localObject1).objects.isEmpty())
              {
                ArrayList localArrayList = new ArrayList();
                localObject1 = ((TLRPC.Vector)localObject1).objects.iterator();
                while (((Iterator)localObject1).hasNext())
                {
                  Object localObject2 = ((Iterator)localObject1).next();
                  TLRPC.User localUser1 = new TLRPC.User();
                  localObject2 = (TLRPC.TL_contactStatus)localObject2;
                  if (localObject2 == null)
                    continue;
                  if ((((TLRPC.TL_contactStatus)localObject2).status instanceof TLRPC.TL_userStatusRecently))
                    ((TLRPC.TL_contactStatus)localObject2).status.expires = -100;
                  while (true)
                  {
                    TLRPC.User localUser2 = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_contactStatus)localObject2).user_id));
                    if (localUser2 != null)
                      localUser2.status = ((TLRPC.TL_contactStatus)localObject2).status;
                    localUser1.status = ((TLRPC.TL_contactStatus)localObject2).status;
                    localArrayList.add(localUser1);
                    break;
                    if ((((TLRPC.TL_contactStatus)localObject2).status instanceof TLRPC.TL_userStatusLastWeek))
                    {
                      ((TLRPC.TL_contactStatus)localObject2).status.expires = -101;
                      continue;
                    }
                    if (!(((TLRPC.TL_contactStatus)localObject2).status instanceof TLRPC.TL_userStatusLastMonth))
                      continue;
                    ((TLRPC.TL_contactStatus)localObject2).status.expires = -102;
                  }
                }
                MessagesStorage.getInstance().updateUsers(localArrayList, true, true, true);
              }
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(4) });
            }
          });
      }
    });
  }

  public void setDeleteAccountTTL(int paramInt)
  {
    this.deleteAccountTTL = paramInt;
  }

  public void setPrivacyRules(ArrayList<TLRPC.PrivacyRule> paramArrayList, int paramInt)
  {
    if (paramInt == 2)
      this.callPrivacyRules = paramArrayList;
    while (true)
    {
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
      reloadContactsStatuses();
      return;
      if (paramInt == 1)
      {
        this.groupPrivacyRules = paramArrayList;
        continue;
      }
      this.privacyRules = paramArrayList;
    }
  }

  public static class Contact
  {
    public String first_name;
    public int id;
    public String last_name;
    public ArrayList<Integer> phoneDeleted = new ArrayList();
    public ArrayList<String> phoneTypes = new ArrayList();
    public ArrayList<String> phones = new ArrayList();
    public ArrayList<String> shortPhones = new ArrayList();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.ContactsController
 * JD-Core Version:    0.6.0
 */
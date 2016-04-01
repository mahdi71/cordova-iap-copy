package ir.adPlay.bazaarIAB;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import ir.adPlay.bazaarIAB.util.IabHelper;
import ir.adPlay.bazaarIAB.util.IabHelper.OnConsumeFinishedListener;
import ir.adPlay.bazaarIAB.util.IabHelper.OnIabPurchaseFinishedListener;
import ir.adPlay.bazaarIAB.util.IabHelper.OnIabSetupFinishedListener;
import ir.adPlay.bazaarIAB.util.IabHelper.QueryInventoryFinishedListener;
import ir.adPlay.bazaarIAB.util.IabResult;
import ir.adPlay.bazaarIAB.util.Inventory;
import ir.adPlay.bazaarIAB.util.Purchase;
import java.util.ArrayList;
import java.util.StringTokenizer;










public class adPlayIAB
{
  public static String TAG = "adPlay";
  static String mPayLoad = "";
  
  public static ArrayList<String> consumables = new ArrayList();
  
  public static ArrayList<String> nonConsumables = new ArrayList();
  





  public static final int RC_REQUEST = 10001;
  




  public static String base64EncodedPublicKey = "";
  



  public static IabHelper mHelper = null;
  public static adPlayIabListener mListenr = null;
  
  public adPlayIAB() {}
  
  public static void init(Activity mActivity, String _base64EncodedPublicKey, String _consumable, String _nonConsumable, adPlayIabListener _mListener) {
    Log.d(TAG, "init begin rsa key =" + _base64EncodedPublicKey);
    Log.d(TAG, "init begin consumables =" + _consumable);
    Log.d(TAG, "init begin non consumables" + _nonConsumable);
    if (_base64EncodedPublicKey.length() < 20) return;
    mListenr = _mListener;
    consumables = new ArrayList();
    nonConsumables = new ArrayList();
    base64EncodedPublicKey = _base64EncodedPublicKey;
    StringTokenizer stc = new StringTokenizer(_consumable, ",");
    StringTokenizer stn = new StringTokenizer(_nonConsumable, ",");
    while (stc.hasMoreTokens()) {
      consumables.add(stc.nextToken());
    }
    while (stn.hasMoreTokens()) {
      nonConsumables.add(stn.nextToken());
    }
    
    mHelper = new IabHelper(mActivity.getApplicationContext(), base64EncodedPublicKey);
    mHelper.enableDebugLogging(false);
    mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
      public void onIabSetupFinished(IabResult result) {
        Log.d(adPlayIAB.TAG, "Setup finished.");
        
        if (!result.isSuccess())
        {

          return;
        }
        

        if (adPlayIAB.mHelper == null) { return;
        }
        
        Log.d(adPlayIAB.TAG, "Setup successful. Querying inventory.");
        adPlayIAB.mHelper.queryInventoryAsync(adPlayIAB.mGotInventoryListener);
      }
    });
  }
  

  public static IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener()
  {
    public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
      Log.d(adPlayIAB.TAG, "Query inventory finished.");
      

      if (adPlayIAB.mHelper == null) { return;
      }
      
      if (result.isFailure())
      {
        return;
      }
      
      Log.d(adPlayIAB.TAG, "Query inventory was successful.");
      



      for (int i = 0; i < adPlayIAB.nonConsumables.size(); i++) {
        Purchase mPurchase = inventory.getPurchase((String)adPlayIAB.nonConsumables.get(i));
        boolean isPurchased = mPurchase != null;
        if (isPurchased) {
          adPlayIAB.mListenr.onNonConsumablePurchased(mPurchase.getSku());
        }
      }
      for (int i = 0; i < adPlayIAB.consumables.size(); i++) {
        Purchase mPurchase = inventory.getPurchase((String)adPlayIAB.consumables.get(i));
        boolean isPurchased = mPurchase != null;
        if (isPurchased) {
          adPlayIAB.mHelper.consumeAsync(inventory.getPurchase((String)adPlayIAB.consumables.get(i)), adPlayIAB.mConsumeFinishedListener);
          adPlayIAB.mListenr.onConsumablePurchased(mPurchase.getSku());
        }
      }
      Log.d(adPlayIAB.TAG, "Initial inventory query finished; enabling main UI.");
    }
  };
  
  public static void startIAB(Activity mActivity, String element, String payLoad)
  {
    Log.d(TAG, "start iab with" + element + " , " + payLoad);
    mPayLoad = payLoad;
    Intent i = new Intent(mActivity, IABActivity.class);
    i.putExtra("element", element);
    i.putExtra("payLoad", payLoad);
    i.setFlags(268435456);
    mActivity.startActivity(i);
  }
  



  public static void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
    if (mHelper == null) { return;
    }
    
    if (mHelper.handleActivityResult(requestCode, resultCode, data))
    {





      Log.d(TAG, "onActivityResult handled by IABUtil.");
    }
  }
  

  static boolean verifyDeveloperPayload(Purchase p, String _payLoad)
  {
    String payload = p.getDeveloperPayload();
    return payload.equals(_payLoad);
  }
  


  static IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener()
  {
    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
      Log.d(adPlayIAB.TAG, "Purchase finished: " + result + ", purchase: " + purchase);
      

      if (adPlayIAB.mHelper == null) { return;
      }
      if (result.isFailure())
      {
        adPlayIAB.mListenr.onPurchaseFail();
        return;
      }
      if (!adPlayIAB.verifyDeveloperPayload(purchase, adPlayIAB.mPayLoad)) {
        adPlayIAB.mListenr.onPurchaseFail();
        return;
      }
      
      Log.d(adPlayIAB.TAG, "Purchase successful.");
      for (int i = 0; i < adPlayIAB.consumables.size(); i++)
      {
        if (purchase.getSku().equals(adPlayIAB.consumables.get(i)))
        {
          adPlayIAB.mListenr.onConsumablePurchased((String)adPlayIAB.consumables.get(i));
          adPlayIAB.mHelper.consumeAsync(purchase, adPlayIAB.mConsumeFinishedListener);
          return;
        }
      }
      for (int i = 0; i < adPlayIAB.nonConsumables.size(); i++) {
        if (purchase.getSku().equals(adPlayIAB.nonConsumables.get(i)))
        {
          adPlayIAB.mListenr.onNonConsumablePurchased((String)adPlayIAB.nonConsumables.get(i));
          return;
        }
      }
    }
  };
  


  static IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener()
  {
    public void onConsumeFinished(Purchase purchase, IabResult result) {
      Log.d(adPlayIAB.TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);
      

      if (adPlayIAB.mHelper == null) { return;
      }
      


      if (result.isSuccess())
      {

        Log.d(adPlayIAB.TAG, "Consumption successful. Provisioning.");
      }
      




      Log.d(adPlayIAB.TAG, "End consumption flow.");
    }
  };
  




  public static void destroy()
  {
    Log.d(TAG, "Destroying helper.");
    if (mHelper != null) {
      mHelper.dispose();
      mHelper = null;
    }
  }
}

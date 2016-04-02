package ir.adPlay.bazaarIAB;

public abstract class adPlayIabListener
{
  public abstract void onConsumablePurchased(String paramString);
  
  public abstract void onNonConsumablePurchased(String paramString);
  
  public abstract void onPurchaseFail();
}

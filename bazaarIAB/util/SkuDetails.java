package ir.adPlay.bazaarIAB.util;

import org.json.JSONException;
import org.json.JSONObject;

public class SkuDetails
{
  String mItemType;
  String mSku;
  String mType;
  String mPrice;
  String mTitle;
  String mDescription;
  String mJson;
  
  public SkuDetails(String jsonSkuDetails)
    throws JSONException
  {
    this("inapp", jsonSkuDetails);
  }
  
  public SkuDetails(String itemType, String jsonSkuDetails)
    throws JSONException
  {
    this.mItemType = itemType;
    this.mJson = jsonSkuDetails;
    JSONObject o = new JSONObject(this.mJson);
    this.mSku = o.optString("productId");
    this.mType = o.optString("type");
    this.mPrice = o.optString("price");
    this.mTitle = o.optString("title");
    this.mDescription = o.optString("description");
  }
  
  public String getSku()
  {
    return this.mSku;
  }
  
  public String getType()
  {
    return this.mType;
  }
  
  public String getPrice()
  {
    return this.mPrice;
  }
  
  public String getTitle()
  {
    return this.mTitle;
  }
  
  public String getDescription()
  {
    return this.mDescription;
  }
  
  public String toString()
  {
    return "SkuDetails:" + this.mJson;
  }
}

package ir.adPlay.bazaarIAB;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.RelativeLayout;
import ir.adPlay.bazaarIAB.util.IabHelper;

public class IABActivity
  extends Activity
{
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    requestWindowFeature(1);
    getWindow().setFlags(1024, 1024);
    
    RelativeLayout relativeLayout_709 = new RelativeLayout(this);
    WindowManager.LayoutParams layout_928 = new WindowManager.LayoutParams();
    layout_928.width = -1;
    layout_928.height = -1;
    relativeLayout_709.setLayoutParams(layout_928);
    setContentView(relativeLayout_709);
    Intent i = getIntent();
    String element = i.getStringExtra("element");
    String payLoad = i.getStringExtra("payLoad");
    if (adPlayIAB.base64EncodedPublicKey.length() < 20) {
      return;
    }
    adPlayIAB.mHelper.launchPurchaseFlow(this, element, 10001, adPlayIAB.mPurchaseFinishedListener, payLoad);
  }
  
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    adPlayIAB.onActivityResult(requestCode, resultCode, data);
    finish();
  }
}

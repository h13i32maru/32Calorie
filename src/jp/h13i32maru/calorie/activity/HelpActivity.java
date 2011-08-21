package jp.h13i32maru.calorie.activity;

import jp.h13i32maru.calorie.R;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

public class HelpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.help);
        
        PackageManager pm = getPackageManager();
        PackageInfo pi;
        try {
            pi = pm.getPackageInfo(getPackageName(), 0);
            TextView t = (TextView)findViewById(R.id.help_version);
            t.setText(pi.versionName);
            
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

}

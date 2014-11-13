package marctan.com.hellonotifications;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;


public class ReplyActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        ((TextView)findViewById(R.id.response)).setText(getMessageText(getIntent()));
    }

    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return "Your response to the message: "+remoteInput.getCharSequence(MainActivity.EXTRA_VOICE_REPLY);
        }

        return "No response";
    }


}

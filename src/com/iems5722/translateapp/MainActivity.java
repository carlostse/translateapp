package com.iems5722.translateapp;

import java.util.Locale;
import com.iems5722.translateapp.task.TranslateHttpTask;
import com.iems5722.translateapp.task.TranslateTcpTask;
import com.iems5722.translateapp.task.TranslateAPICallback;
import com.iems5722.translateapp.util.Util;
import com.iems5722.translateapp.util.WordDictionary;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class MainActivity extends Activity implements TranslateAPICallback {

    private static final String TAG = "MainActivity";

    // SelfAPI is self-implemented server API
    // and it is the only one used in this assignment
    private enum TranlateMethod {LocalDict, TCP, HTTP, SelfAPI}

    private final TranlateMethod method = TranlateMethod.LocalDict;

    private WordDictionary dict;
    private EditText txtIn;
//    private TextView txtOut;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get references to layout objects
        txtIn = (EditText) findViewById(R.id.txt_input);
//        txtOut = (TextView) findViewById(R.id.txt_output);

        // add click listener to buttons to call translateText()
        ((Button) findViewById(R.id.btn_submit))
        .setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                submitButtonClicked(method);
            }
        });

        // translate the text when user click done on the keyboard
        txtIn.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
                    (actionId == EditorInfo.IME_ACTION_DONE)) {
                    translateText(method);
                }
                return false;
            }
        });
    }

    private void submitButtonClicked(TranlateMethod method){
        // hide the keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txtIn.getWindowToken(), 0);
        // translate
        translateText(method);
    }

    /**
     * translate look up
     */
    private void translateText(TranlateMethod method) {
        // get user input
        String input = txtIn.getText().toString();
        Log.i(TAG, "input: " + input);

        if (Util.isMissing(input)) {
            Log.e(TAG, "missing input");
            toastMissingText();
            return;
        }

        // check network
        if (!Util.isNetworkAvailable(this)){
            Log.e(TAG, "no network");
            new AlertDialog.Builder(this)
            .setMessage(R.string.err_network)
            .setPositiveButton(R.string.btn_ok,
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            })
            .create().show();
            return;
        }

        // trim and to lower case
        input = input.trim().toLowerCase(Locale.ENGLISH);

        // try get word from different APIs
        // in this assignment, only self-implemented API is used
        // and it is the only one which supports multiple words
        switch (method){
        case SelfAPI:

            break;
        case TCP:
            new TranslateTcpTask(this).execute(input);
            break;
        case HTTP:
            new TranslateHttpTask(this).execute(input);
            break;
        case LocalDict:
            if (dict == null) dict = new WordDictionary();
            translated(new String[]{input, dict.getDictionary().get(input)});
            break;
        }
    }

    private void toastMissingText(){
        Toast.makeText(this, getText(R.string.msg_without_input), Toast.LENGTH_SHORT).show();
    }

    /**
     * Share the translated text to other applications
     */
    private void openShare() {
        String out = "";/*txtOut.getText().toString()*/;
        if (Util.isMissing(out)) {
            toastMissingText();
            return;
        }
        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_TEXT, out);
        i.setType("text/plain");
        startActivity(Intent.createChooser(i, getText(R.string.title_share_to)));
    }

    /*
     * show share icon in action bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*
     * respond to action buttons
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
        case R.id.action_share:
            openShare();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showLoading(boolean show){
        if (dialog != null){
            dialog.dismiss();
            dialog = null;
        }
        if (show){
            dialog = ProgressDialog.show(this, null, getText(R.string.msg_loading));
            dialog.show();
        }
    }

    @Override
    public void translated(String[] result) {
        if (Util.isMissing(result) || result.length != 2 ||
                Util.isMissing(result[0]) || Util.isMissing(result[1]) ||
                Util.isTranslationError(this, result[1])){
            Log.e(TAG, "missing result");
//            txtOut.setText(null);
            new AlertDialog.Builder(this)
            .setTitle(R.string.err_translate)
            .setMessage(R.string.msg_not_in_dict)
            .setPositiveButton(R.string.btn_ok,
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            })
            .create().show();
            return;
        }
        Log.d(TAG, "result: " + result[0] + " -> " + result[1]);
        // display result
//        txtOut.setText(result[1]);
        // save result
        Util.saveHistory(this, result[0], result[1]);
    }
}

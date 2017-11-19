package pdn.yingchiuluk.openweatherapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class QueryErrorDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        TextView errorText = (TextView) getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_error_text, null);
        errorText.setMovementMethod(new ScrollingMovementMethod());

        Bundle bundle = getArguments();
        if (bundle != null) {
            errorText.setText(bundle.getString(QueryIntentService.KEY_QUERY_ERROR));
        }

        builder.setView(errorText);
        return  builder.create();
    }
}

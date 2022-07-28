package paita.stream_app_final.Tafa.Shared;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector {

    Context context;

    public ConnectionDetector(Context context) {

        this.context = context;
    }

    public boolean isConnected() {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {

            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null) {

                if (info.getState() == NetworkInfo.State.CONNECTED) {

                    return true;
                }
            }
        }

        return false;
    }

    public void parseStringBuilder(StringBuilder mStringBuilder) {

        String[] rows = mStringBuilder.toString().split(":");

        for (int i = 0; i < rows.length; i++) {

            String[] columns = rows[i].split(",");


            try {
                double x = Double.parseDouble(columns[0]);
                double y = Double.parseDouble(columns[1]);

                String cellInfo = "(x,y): (" + x + "," + y + ")";

            } catch (NumberFormatException e) {
            }
        }
    }
}

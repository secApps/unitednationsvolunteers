package volunteers.un.unitednationsvolunteers;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by jarman on 12/28/17.
 */

public class KnowledgehubApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

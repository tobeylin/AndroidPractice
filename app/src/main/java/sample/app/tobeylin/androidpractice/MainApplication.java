package sample.app.tobeylin.androidpractice;

import android.app.Application;

import com.raizlabs.android.dbflow.config.DatabaseConfig;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseHelperListener;
import com.raizlabs.android.dbflow.structure.database.OpenHelper;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import sample.app.tobeylin.androidpractice.database.CustomerDBHelper;
import sample.app.tobeylin.androidpractice.database.ExternalOrderDBHelper;
import sample.app.tobeylin.androidpractice.media.sync.database.TrackDBHelper;
import sample.app.tobeylin.androidpractice.media.sync.database.TrackDatabase;

public class MainApplication extends Application {

    private RefWatcher refWatcher;
    private CustomerDBHelper customerDBHelper;
    private ExternalOrderDBHelper externalOrderDBHelper;

    public static RefWatcher getRefWatcher(Application application) {
        return ((MainApplication) application).getRefWatcher();
    }

    public RefWatcher getRefWatcher() {
        return refWatcher;
    }

    public CustomerDBHelper getCustomerDBHelper() {
        return customerDBHelper;
    }

    public ExternalOrderDBHelper getExternalOrderDBHelper() {
        return externalOrderDBHelper;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initLeakCanary();
        initDBFlow();
        initInternalCustomerDatabase();
        initExternalOrderDatabase();

    }

    private void initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        refWatcher = LeakCanary.install(this);
    }

    private void initDBFlow() {
        FlowConfig flowConfig = new FlowConfig.Builder(this)
                .addDatabaseConfig(
                        new DatabaseConfig.Builder(TrackDatabase.class)
                        .openHelper(new DatabaseConfig.OpenHelperCreator() {
                            @Override
                            public OpenHelper createHelper(DatabaseDefinition databaseDefinition, DatabaseHelperListener helperListener) {
                                return new TrackDBHelper(databaseDefinition, helperListener, MainApplication.this);
                            }
                        }).build()
                )
                .openDatabasesOnInit(true)
                .build();
        FlowManager.init(flowConfig);
    }

    private void initInternalCustomerDatabase() {
        customerDBHelper = new CustomerDBHelper(this);
        customerDBHelper.getWritableDatabase();
    }

    private void initExternalOrderDatabase() {
        externalOrderDBHelper = new ExternalOrderDBHelper(this);
        externalOrderDBHelper.getWritableDatabase();
    }

}

package sample.app.tobeylin.androidpractice;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class MainApplication extends Application {

    private RefWatcher refWatcher;

    public static RefWatcher getRefWatcher(Application application) {
        return ((MainApplication) application).getRefWatcher();
    }

    public RefWatcher getRefWatcher() {
        return refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initLeakCanary();
//        initDBFlow();
    }

    private void initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        refWatcher = LeakCanary.install(this);
    }

//    private void initDBFlow() {
//        FlowConfig flowConfig = new FlowConfig.Builder(this)
//                .addDatabaseConfig(
//                        new DatabaseConfig.Builder(TrackDatabase.class)
//                        .openHelper(new DatabaseConfig.OpenHelperCreator() {
//                            @Override
//                            public OpenHelper createHelper(DatabaseDefinition databaseDefinition, DatabaseHelperListener helperListener) {
//                                return new TrackDBHelper(databaseDefinition, helperListener, MainApplication.this);
//                            }
//                        }).build()
//                )
//                .openDatabasesOnInit(true)
//                .build();
//        FlowManager.init(flowConfig);
//    }

}

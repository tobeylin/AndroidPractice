package sample.app.tobeylin.androidpractice;

import android.app.Application;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

//        initDBFlow();
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

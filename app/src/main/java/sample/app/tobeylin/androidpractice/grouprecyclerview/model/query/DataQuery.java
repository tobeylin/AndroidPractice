package sample.app.tobeylin.androidpractice.grouprecyclerview.model.query;

public interface DataQuery<Q, R> {

    Q getQuery();

    R getQueryResult();

}

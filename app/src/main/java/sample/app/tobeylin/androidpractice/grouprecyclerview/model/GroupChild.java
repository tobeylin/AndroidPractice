package sample.app.tobeylin.androidpractice.grouprecyclerview.model;

public class GroupChild<T> {

    private T childData;

    public GroupChild(T childData) {
        this.childData = childData;
    }

    public T getChildData() {
        return childData;
    }

    public void setChildData(T childData) {
        this.childData = childData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroupChild<?> that = (GroupChild<?>) o;

        return childData != null ? childData.equals(that.childData) : that.childData == null;

    }

    @Override
    public int hashCode() {
        return childData != null ? childData.hashCode() : 0;
    }

}

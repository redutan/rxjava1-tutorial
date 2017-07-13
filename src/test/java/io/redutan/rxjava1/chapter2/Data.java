package io.redutan.rxjava1.chapter2;

/**
 * @author myeongju.jung
 */
public class Data {
    private Integer id;
    private String title;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "id = " + id + ", title = " + title;
    }
}
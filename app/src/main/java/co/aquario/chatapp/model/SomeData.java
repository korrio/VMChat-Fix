package co.aquario.chatapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by matthewlogan on 9/3/14.
 */
public class SomeData implements Serializable {

    @Expose
    public int id;
    @Expose
    public String author;
    @SerializedName("image_src")
    @Expose
    public String src;
    @Expose
    public String color;
    @Expose
    public String date;
    @SerializedName("modified_date")
    @Expose
    public String modifiedDate;
    @Expose
    public int width;
    @Expose
    public int height;
    @Expose
    public float ratio;
    @Expose
    public int featured;
    @Expose
    public int category;
    @Expose
    public int corrupt;

}

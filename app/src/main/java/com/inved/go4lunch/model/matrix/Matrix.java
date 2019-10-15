
package com.inved.go4lunch.model.matrix;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Matrix {

    @SerializedName("rows")
    @Expose
    private List<Row> rows = null;


    public List<Row> getRows() {
        return rows;
    }


}

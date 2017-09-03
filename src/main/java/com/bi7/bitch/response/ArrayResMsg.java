package com.bi7.bitch.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

/**
 * Created by foxer on 2017/8/21.
 */
public class ArrayResMsg extends Msg {
    private List<? extends Object> data;

    private int count;

    public List<? extends Object> getArray() {
        return data;
    }

    public void setArray(List<? extends Object> array) {
        if (Objects.isNull(array)) {
            return;
        }
        this.data = array;
        this.count = array.size();
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

package com.xy.dframe;

public interface DataFrame {
    
    double getDouble(String name, int row);

    public abstract void addColumn(String name, double[] values);

    double sum(String name);
    
}

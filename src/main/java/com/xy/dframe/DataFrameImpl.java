package com.xy.dframe;

import java.util.ArrayList;
import java.util.List;

import net.openhft.koloboke.collect.map.IntObjMap;
import net.openhft.koloboke.collect.map.ObjIntMap;
import net.openhft.koloboke.collect.map.hash.HashIntObjMaps;
import net.openhft.koloboke.collect.map.hash.HashObjIntMaps;

public class DataFrameImpl implements DataFrame {
    
    private int nextCol; 
    
    private final ObjIntMap<String> nameIndex;
    
    private final IntObjMap<double[]> doubleValues;
    
    public DataFrameImpl() {
        nameIndex = HashObjIntMaps.newMutableMap();
        doubleValues = HashIntObjMaps.newMutableMap();
    }

    @Override
    public double getDouble(String name, int row) {
        if (name == null) throw new NullPointerException("Column name is null");
        int col = nameIndex.getOrDefault(name, -1);
        if (col == -1) throw new IllegalArgumentException("Unknown column name: " + name);
        double[] values = doubleValues.get(col);
        if (values == null) throw new IllegalStateException();
        return values[row];
    }
    
    @Override
    public void addColumn(String name, double[] values) {
        int col = nameIndex.getOrDefault(name, -1);
        if (col >= 0) throw new IllegalArgumentException("Column [" + name + "] already exists");
        nameIndex.put(name, nextCol);
        doubleValues.put(nextCol++, values);
    }

    @Override
    public double sum(String name) {
        int col = nameIndex.getOrDefault(name, -1);
        if (col == -1) throw new IllegalArgumentException("Unknown column name: " + name);
        double[] values = doubleValues.get(col);
        double sum = 0;
        for (int i = 0; i < values.length; i++) {
            sum += values[i];
        }
        return sum;
    }
    
    

}

package com.xy.dframe.sample;

import java.beans.Expression;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import com.xy.dframe.DataFrame;
import com.xy.dframe.DataFrameImpl;

public class FooMain {

    private static final int C = 8;
    private static final int N = 100_000;

    public static void main(String[] args) throws Exception {
        Method[] stringSetters = new Method[C];
        Method[] intSetters = new Method[C];
        Method[] doubleSetters = new Method[C];
        for (int c = 1; c <= C; c++) {
            stringSetters[c - 1] = Foo.class.getMethod("setString" + c,
                    String.class);
            intSetters[c - 1] = Foo.class.getMethod("setInt" + c, int.class);
            doubleSetters[c - 1] = Foo.class.getMethod("setDouble" + c,
                    double.class);
        }

        List<Foo> list = new ArrayList<>();
        Random generator = new Random(0);
        byte[] bytes = new byte[6];
        for (int i = 0; i < N; i++) {
            FooImpl item = new FooImpl();
            for (int c = 0; c < C; c++) {
                generator.nextBytes(bytes);
                stringSetters[c].invoke(item,Base64.getEncoder().encodeToString(bytes));
                intSetters[c].invoke(item, generator.nextInt());
                doubleSetters[c].invoke(item, generator.nextDouble());
            }
            list.add(item);
        }
        
        DataFrame dataFrame = new DataFrameImpl();
        Method[] doubleGetters = new Method[C];
        for (int c = 0; c < C; c++) {
            doubleGetters[c] = Foo.class.getMethod("getDouble" + (c + 1));
        }
        double[] double1 = new double[N];
        for (int i = 0; i < N; i++) {
            double1[i] = (double) doubleGetters[0].invoke(list.get(0));
        }
        dataFrame.addColumn("double1", double1);
        
        for (String m : new String[] { "getString", "getInt", "getDouble" }) {
            StringBuilder message = new StringBuilder();
            for (int c = 1; c <= 8; c++) {
                message.append(new Expression(list.get(0), m + c, new Object[0])
                        .getValue()).append(",");
            }
            System.out.println(message.substring(0, message.length() - 1));
        }

        System.out.println("beans");
        for (int lap = 0; lap < 25; lap++) {
            long t1 = System.nanoTime();
            double sum1 = 0;
            for (Foo item : list) {
                sum1 += item.getDouble1();
            }
            System.out.println(sum1 + ": " + (System.nanoTime() - t1));
        }
        
        System.out.println("dataFrame");
        for (int lap = 0; lap < 25; lap++) {
            long t1 = System.nanoTime();
            double sum1 = dataFrame.sum("double1");
            System.out.println(sum1 + ": " + (System.nanoTime() - t1));
        }
    }

}

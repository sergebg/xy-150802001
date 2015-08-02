package com.xy.dframe;

import com.xy.dframe.column.DoubleColumnBuilder;
import com.xy.dframe.column.DoubleColumnBuilderImpl;
import com.xy.dframe.column.GenericColumnBuilder;
import com.xy.dframe.sample.Foo;
import com.xy.dframe.sample.FooImpl;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

/**
 * Created by sergey on 17/07/15.
 */
public class Demo {

    public static void main(String[] args) {

        // Prepare data
        int N = 4_000_000;
        List<Foo> data = new ArrayList<>(N);
        Random random = new Random(0);
        byte[] bytes = new byte[3 * 9 * 4];
        Base64.Encoder encoder = Base64.getEncoder();
        for (int i = 0; i < N; i++) {
            FooImpl item = new FooImpl();
            item.setDouble1(random.nextDouble());
            item.setDouble2(random.nextDouble());
            item.setDouble3(random.nextDouble());
            item.setDouble4(random.nextDouble());
            item.setDouble5(random.nextDouble());
            item.setDouble6(random.nextDouble());
            item.setDouble7(random.nextDouble());
            item.setDouble8(random.nextDouble());
            item.setInt1(random.nextInt());
            item.setInt2(random.nextInt());
            item.setInt3(random.nextInt());
            item.setInt4(random.nextInt());
            item.setInt5(random.nextInt());
            item.setInt6(random.nextInt());
            item.setInt7(random.nextInt());
            item.setInt8(random.nextInt());

            random.nextBytes(bytes);
            String s = encoder.encodeToString(bytes);
            item.setString1(s.substring(0, 4));
            item.setString2(s.substring(4, 4 + 8));
            item.setString3(s.substring(12, 12 + 12));
            item.setString4(s.substring(24, 24 + 16));
            item.setString5(s.substring(40, 40 + 20));
            item.setString6(s.substring(60, 60 + 24));
            item.setString7(s.substring(84, 84 + 28));
            item.setString8(s.substring(112, 112 + 32));

            data.add(item);
        }

        // build columns
        DoubleColumnBuilder<Foo> double1 = new DoubleColumnBuilderImpl<Foo>(Foo::getDouble1, N);
        DoubleColumnBuilder<Foo> double2 = new DoubleColumnBuilderImpl<Foo>(Foo::getDouble2, N);
        DoubleColumnBuilder<Foo> double3 = new DoubleColumnBuilderImpl<Foo>(Foo::getDouble3, N);
        DoubleColumnBuilder<Foo> double4 = new DoubleColumnBuilderImpl<Foo>(Foo::getDouble4, N);
        DoubleColumnBuilder<Foo> double5 = new DoubleColumnBuilderImpl<Foo>(Foo::getDouble5, N);
        DoubleColumnBuilder<Foo> double6 = new DoubleColumnBuilderImpl<Foo>(Foo::getDouble6, N);
        DoubleColumnBuilder<Foo> double7 = new DoubleColumnBuilderImpl<Foo>(Foo::getDouble7, N);
        DoubleColumnBuilder<Foo> double8 = new DoubleColumnBuilderImpl<Foo>(Foo::getDouble8, N);
        List<GenericColumnBuilder<Foo, ?>> columnBuilders = new ArrayList<>();
        columnBuilders.add(double1);
        columnBuilders.add(double2);
        columnBuilders.add(double3);
        columnBuilders.add(double4);
        columnBuilders.add(double5);
        columnBuilders.add(double6);
        columnBuilders.add(double7);
        columnBuilders.add(double8);
        for (Foo item : data) {
            columnBuilders.stream().forEach(b -> b.add(item));
        }

        // build data frame
        DataFrameImpl dataFrame = new DataFrameImpl();
        dataFrame.addColumn("double1", double1.toArray());
        dataFrame.addColumn("double2", double2.toArray());
        dataFrame.addColumn("double3", double3.toArray());
        dataFrame.addColumn("double4", double4.toArray());
        dataFrame.addColumn("double5", double5.toArray());
        dataFrame.addColumn("double6", double6.toArray());
        dataFrame.addColumn("double7", double7.toArray());
        dataFrame.addColumn("double8", double8.toArray());

        // compare sum calculation
        long frameTime = 0;
        for (int i = 0; i < 100; i++) {
            long t1 = System.nanoTime();
            double sum1 = dataFrame.sum("double1");
            long t2 = System.nanoTime();
            frameTime += t2 - t1;
            if (i == 99) {
                System.out.println(sum1);
            }
        }
        System.out.println(frameTime);

        long beanTime = 0;
        for (int i = 0; i < 100; i++) {
            long t1 = System.nanoTime();
            double sum = 0;
            for (int k = 0; k < N; k++) {
                sum += data.get(k).getDouble1();
            }
            long t2 = System.nanoTime();
            beanTime += t2 - t1;
            if (i == 99) {
                System.out.println(sum);
            }
        }
        System.out.println(beanTime);
    }

}

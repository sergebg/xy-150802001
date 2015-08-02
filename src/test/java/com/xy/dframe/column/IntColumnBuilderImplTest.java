package com.xy.dframe.column;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.ToIntFunction;

import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.xy.dframe.sample.Foo;
import com.xy.dframe.sample.FooImpl;

public class IntColumnBuilderImplTest {
    
    public static void main(String[] args) throws Exception {
        Random random = new Random(0);
        List<Foo> list = new ArrayList<>(10_000_000);
        for (int i = 0; i < 10_000_000; i++) {
            FooImpl item = new FooImpl();
            item.setInt1(random.nextInt());
            item.setInt2(random.nextInt());
            item.setInt3(random.nextInt());
            item.setInt4(random.nextInt());
            list.add(item);
        }
        Collections.shuffle(list, random);
        warmup(list, i -> i.getInt1());
        Collections.shuffle(list, random);
        runTest(list, i -> i.getInt2());
    }
    
    private static void warmup(List<Foo> list, ToIntFunction<Foo> function) {
        System.out.println("warmup");
        for (int lap = 0; lap < 10; lap++) {
            runPureLogic(list, i -> i.getInt1());
            runBuilder(list, function);
            runList(list, function);
        }
    }

    private static void runTest(List<Foo> list, ToIntFunction<Foo> function) {
        System.out.println("test");
        for (int lap = 0; lap < 25; lap++) {
            runPureLogic(list, function);
        }
        for (int lap = 0; lap < 25; lap++) {
            runBuilder(list, function);
        }
        for (int lap = 0; lap < 25; lap++) {
            runList(list, function);
        }
    }
    
    private static void runBuilder(List<Foo> list, ToIntFunction<Foo> function) {
        int n = list.size();
        IntColumnBuilderImpl<Foo> builder = new IntColumnBuilderImpl<>(function, n);
        long t1 = System.nanoTime();
        for (int i = 0; i < n; i++) {
            Foo item = list.get(i);
            builder.add(item);
        }
        int[] result = builder.toArray();
        System.out.println("B\t" + (System.nanoTime() - t1) + "\t" + result[n - 1]);
    }

    private static void runPureLogic(List<Foo> list, ToIntFunction<Foo> function) {
        int n = list.size();
        int[] array = new int[n];
        long t1 = System.nanoTime();
        for (int i = 0; i < n; i++) {
            Foo item = list.get(i);
            array[i] = function.applyAsInt(item);
        }
        int[] result = new int[n];
        System.arraycopy(array, 0, result, 0, n);
        System.out.println("P\t" + (System.nanoTime() - t1) + "\t" + result[n - 1]);
    }
    
    private static void runList(List<Foo> list, ToIntFunction<Foo> function) {
        int n = list.size();
        List<Integer> col = new ArrayList<>(n);
        long t1 = System.nanoTime();
        for (int i = 0; i < n; i++) {
            Foo item = list.get(i);
            col.add(function.applyAsInt(item));
        }
        System.out.println("L\t" + (System.nanoTime() - t1) + "\t" + col.get(n - 1));
    }
    
    @Test
    public void testPartitionLessThenData() throws Exception {
        int partitionSize = 3;
        int[] data = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        IntColumnBuilderImpl<Foo> builder = new IntColumnBuilderImpl<>(i -> i.getInt1(), partitionSize);
        for (int i = 0; i < data.length; i++) {
            FooImpl item = new FooImpl();
            item.setInt1(data[i]);
            builder.add(item);
        }
        Assert.assertArrayEquals(data, (int[]) builder.toArray());
    }

    @Test
    public void testDataLessThenPartition() throws Exception {
        int partitionSize = 100;
        int[] data = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        IntColumnBuilderImpl<Foo> builder = new IntColumnBuilderImpl<>(i -> i.getInt1(), partitionSize);
        for (int i = 0; i < data.length; i++) {
            FooImpl item = new FooImpl();
            item.setInt1(data[i]);
            builder.add(item);
        }
        Assert.assertArrayEquals(data, (int[]) builder.toArray());
    }

    @Test
    public void testNoData() throws Exception {
        IntColumnBuilderImpl<Foo> builder = new IntColumnBuilderImpl<>(i -> i.getInt1(), 100);
        Assert.assertArrayEquals(new int[0], (int[]) builder.toArray());
    }
    
    @Rule
    public final ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void testNegativePartition() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(new IsEqual<String>("Value of [expectedSize] can't be negative, but was [-1]"));
        new IntColumnBuilderImpl<Foo>(i -> i.getInt1(), -1);
        Assert.fail();
    }
    
    @Test
    public void testNullValueFunction() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage(new IsEqual<String>("Value of [valueFunction] was null"));
        new IntColumnBuilderImpl<Foo>(null, 1);
        Assert.fail();
    }
    
}

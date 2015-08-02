package com.xy.dframe.challenge;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

/**
 * Created by sergey on 08/07/15.
 */
public class ListContent {

    private static final int N = 1_000_000;

    public static void main(String[] args) throws InterruptedException {
        Random random = new Random(1);
        LinkedList<Long> linkedList = new LinkedList<>();

        long[] values = new long[N];
        for (int i = 0; i < N; i++) {
            values[i] = random.nextLong();
        }

        int[] addRange = {1_000, 10_000, 100_000, 1_000_000};
        int[] removeRange = {100_000, 100_000, 100_000, 100_000};

        Thread.sleep(10000);

        run("ArrayList.add.1", m -> runAdd(new ArrayList<>(m), values, m), 100, addRange);
        run("ArrayList.add.2", m -> runAdd(new ArrayList<>(m), values, m), 100, addRange);
        run("ArrayList.add.3", m -> runAdd(new ArrayList<>(m), values, m), 100, addRange);

        Thread.sleep(10000);

        List<Long> al1 = runAdd(new ArrayList<>(N), values, N);
        run("ArrayList.remove.1", m -> runRemove(al1, m, l -> l.size() / 2), 1, removeRange);

        Thread.sleep(10000);

        run("LinkedList.add.1", m -> runAdd(new LinkedList<>(), values, m), 100, addRange);
        run("LinkedList.add.2", m -> runAdd(new LinkedList<>(), values, m), 100, addRange);
        run("LinkedList.add.3", m -> runAdd(new LinkedList<>(), values, m), 100, addRange);

        Thread.sleep(10000);

        List<Long> ll1 = runAdd(new LinkedList<>(), values, N);
        run("LinkedList.remove.1", m -> runRemove(ll1, m, l -> l.size() / 2), 1, removeRange);
    }

    private static void run(String name, IntFunction<List<Long>> test, int laps, int[] range) {
        System.out.println(name);
        System.out.println(String.format("%15s%15s%15s%15s%15s%15s%15s", "n", "m", "avg", "min", "max", "o(m)", "o(n)"));
        for (int m : range) {
            long min = Long.MAX_VALUE;
            long max = Long.MIN_VALUE;
            long sum = 0;
            int n = 0;
            for (int i = 0; i < laps; i++) {
                long t1 = System.nanoTime();
                List<Long> list = test.apply(m);
                long t2 = System.nanoTime();
                assert list.size() > 0;
                n = list.size();
                long t = t2 - t1;
                min = Math.min(min, t);
                max = Math.max(max, t);
                sum += t;
            }
            System.out.println(String.format("%15d%15d%15.0f%15d%15d%15.4f%15.4f", n, m, (double) sum / laps, min, max, (double) sum / laps / m, (double) sum / laps / n));
        }
        System.out.println();
    }

    private static List<Long> runAdd(List<Long> list, long[] values, int m) {
        for (int i = 0; i < m; i++) {
            list.add(values[i]);
        }
        return list;
    }

    private static List<Long> runRemove(List<Long> list, int m, ToIntFunction<List<Long>> indexFunction) {
        for (int i = 0; i < m; i++) {
            int index = indexFunction.applyAsInt(list);
            list.remove(index);
        }
        return list;
    }
}

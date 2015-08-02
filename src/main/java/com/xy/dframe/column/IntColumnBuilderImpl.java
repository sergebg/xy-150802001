package com.xy.dframe.column;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

public class IntColumnBuilderImpl<I> implements IntColumnBuilder<I> {

    private static final int MIN_PARTITION_SIZE = 16;

    private final ToIntFunction<I> valueFunction;

    /** Here full partitions are stored. A proper {@code expectedSize} will help to avoid using this list. */
    private List<int[]> buffer;

    private int[] partition;

    /** Number of elements in the {@code partition} */
    private int size;

    private int partitionSize;

    public IntColumnBuilderImpl(ToIntFunction<I> valueFunction, int expectedSize) {
        if (valueFunction == null) throw new NullPointerException("Value of [valueFunction] was null");
        if (expectedSize < 0)
            throw new IllegalArgumentException("Value of [expectedSize] can't be negative, but was [" + expectedSize + "]");

        this.valueFunction = valueFunction;
        this.partitionSize = Math.max(MIN_PARTITION_SIZE, expectedSize);
        this.partition = new int[partitionSize];
    }

    @Override
    public void add(I item) {
        int value = valueFunction.applyAsInt(item);
        try {
            partition[size] = value;
            size++;
        } catch (IndexOutOfBoundsException e) {
            assert size == partitionSize;
            if (buffer == null) buffer = new ArrayList<>();
            buffer.add(partition);
            partition = new int[partitionSize];
            partition[0] = value;
            size = 1;
        }
    }

    @Override
    public int[] toArray() {
        int bufferSize = buffer == null ? 0 : buffer.size();
        int n = size + bufferSize * partitionSize;
        int[] array = new int[n];
        if (n > 0) {
            int offset = 0;
            for (int i = 0; i < bufferSize; i++) {
                int[] p = buffer.get(i);
                System.arraycopy(p, 0, array, offset, partitionSize);
                offset += partitionSize;
            }
            System.arraycopy(partition, 0, array, offset, size);
        }
        return array;
    }

}
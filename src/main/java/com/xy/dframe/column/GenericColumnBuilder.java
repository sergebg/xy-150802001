package com.xy.dframe.column;

/**
 * Generic interface for column builders. Sub-interfaces constrict the type of array.
 * The builder instance extracts some value from the item and adds it to the resulting array.
 * That could be some property, which values are stored into the column.
 * The type of array should be consistent with extracted values.
 * @param <I> item type
 * @param <A> array type
 */
public interface GenericColumnBuilder<I, A> {

    void add(I item);

    A toArray();

}

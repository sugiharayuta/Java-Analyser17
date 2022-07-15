package org.posl.util.functions;

import org.posl.data.tree.ParsingException;

@FunctionalInterface
public interface CEConsumer<T> {
    public void accept(T t) throws ParsingException;
}
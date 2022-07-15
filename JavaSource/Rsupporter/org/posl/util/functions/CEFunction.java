package org.posl.util.functions;

import org.posl.data.tree.ParsingException;

@FunctionalInterface
public interface CEFunction<T, R> {
    public R apply(T t) throws ParsingException;
}

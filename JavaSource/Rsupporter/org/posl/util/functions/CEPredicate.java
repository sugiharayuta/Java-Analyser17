package org.posl.util.functions;

import org.posl.data.tree.ParsingException;

@FunctionalInterface
public interface CEPredicate<T> {
    public boolean test(T t) throws ParsingException;
}
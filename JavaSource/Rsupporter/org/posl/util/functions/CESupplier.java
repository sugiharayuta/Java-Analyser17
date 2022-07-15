package org.posl.util.functions;

import org.posl.data.tree.ParsingException;

@FunctionalInterface
public interface CESupplier<R> {
    public R get() throws ParsingException;
}

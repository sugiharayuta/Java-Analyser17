package org.posl.data.tokens;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.data.tree.TypeTree;

public abstract class Literal extends Token{
    TypeTree type;

    public Literal(String text, Reference ref) {
        super(text, ref);
    }

    public Literal(String text, Reference ref, JavaTS resolution) {
        super(text, ref, resolution);
    }

}

package org.posl.data.tokens;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.data.tree.TypeTree;

public class TextBlock extends Literal{
    public final String space;

    public TextBlock(String space, String text, Reference ref) {
        super(text, ref, JavaTS.TEXT_BLOCK);
        this.space = space;
        this.type = TypeTree.STRING;
    }

    @Override
    public String toString(){
        return "\"\"\"" + space + text + "\"\"\"";
    }
    
}

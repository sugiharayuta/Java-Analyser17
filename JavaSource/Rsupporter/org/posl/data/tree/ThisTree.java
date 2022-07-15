package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.test.Tested;
import org.posl.test.Tested.Status;

@Tested(date = "2022/7/8", tester = "me", confidence = Status.PROBABLY_OK)
public record ThisTree(Tree qualifier)implements ExpressionTree{

    static ThisTree parse(Tree qualifier, JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.THIS);
        return new ThisTree(qualifier);
    }
    
}

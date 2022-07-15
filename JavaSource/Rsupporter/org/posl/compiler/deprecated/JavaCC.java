package org.posl.compiler.deprecated;

import org.posl.compiler.syntax.Syntax;
import org.posl.compiler.syntax.javalang.Java17Syntax;
import org.posl.data.tree.ParsingException;

public class JavaCC extends LALRParser{

    final int version;

    public JavaCC(int version) throws ParsingException{
        super(JavaLanguage.get(version).syntax());
        this.version = version;
    }

    @Override
    public StateMachine getStateMachine() throws ParsingException{
        return new JavaStateMachine(numOfState, syntax, state0);
    }

    enum JavaLanguage{
        JAVA_SE_17(17, new Java17Syntax());
        private final int version;
        private final Syntax syntax;

        private JavaLanguage(int version, Syntax syntax){
            this.version = version;
            this.syntax = syntax;
        }

        static JavaLanguage get(int version) throws ParsingException{
            for(JavaLanguage v : JavaLanguage.values()){
                if(v.version == version){
                    return v;
                }
            }
            throw new ParsingException("Illegal Java version \""+version+"\".");
        }

        public Syntax syntax(){
            return syntax;
        }
    }

}

package org.posl.data.tree;

import java.util.Collection;

import org.posl.compiler.TokenList;
import org.posl.compiler.TokenList.TokenPointer;
import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.data.tokens.InputElement;
import org.posl.data.tokens.Token;

public class JavaTokenManager{

    public static final boolean ENABLE_PARSING_TRACE = false;

    private final TokenList l;

    public JavaTokenManager(TokenList l){
        this.l = l;
    }

    public final Token lookAhead() throws ParsingException{
        return lookAhead(0);
    }
    
    public final Token lookAhead(int pos) throws ParsingException{
        var ptr = getPointer();
        for(int i = 0; i < pos && ptr.hasNext(); i++){
            ptr.next();
        }
        return ptr.element();
    }

    public final boolean match(int pos, TokenPointer ptr, JavaTS... symbols) throws ParsingException{
        for(int i = 0; i < pos; i++){
            if(!ptr.hasNext()){
                return false;
            }
            ptr.next();
        }
        for(int i = 0, n = symbols.length; i < n && ptr.hasNext(); i++, ptr.next()){
            if(symbols[i] != ptr.element().resolution){
                return false;
            }
        }
        return true;
    }

    public final boolean match(int pos, JavaTS... symbols) throws ParsingException{
        return match(pos, getPointer(), symbols);
    }
    
    public final boolean match(TokenPointer ptr, JavaTS... symbols) throws ParsingException{
        return match(0, ptr, symbols);
    }

    public final boolean match(JavaTS... symbols) throws ParsingException{
        return match(0, symbols);   
    }

    /**
     * Note that {@code pos} starts from 0.
     * @param pos
     * @param symbol
     * @return
     * @throws ParsingException
     */

    public final boolean match(int pos, JavaTS symbol) throws ParsingException{
        return lookAhead(pos).resolution == symbol;
    }

    /**
     * Note that {@code pos} starts from 0.
     * @param pos
     * @param symbols
     * @return
     * @throws ParsingException
     */
    public final boolean match(int pos, Collection<JavaTS> symbols) throws ParsingException{
        return symbols.contains(lookAhead(pos).resolution);
    }

    public final boolean match(Collection<JavaTS> symbols) throws ParsingException{
        return match(0, symbols);
    }

    public final Token read() throws ParsingException{
        while(!(l.refer() instanceof Token) && hasRest()){
            l.get();
        }
        if(!hasRest()){
            throw new ParsingException("Read out of bounds.");
        }
        Token t = (Token)l.get();
        if(ENABLE_PARSING_TRACE){
            System.out.print(t);
        }
        return t;
    }

    public final void skip(JavaTS... tokens) throws ParsingException{
        for(int i = 0; i < tokens.length; i++){
            InputElement e;
            while(!((e = l.get()) instanceof Token) && hasRest());
            Token t = (Token)e;
            if(ENABLE_PARSING_TRACE){
                System.out.print(t);
            }
            if(tokens[i] != t.resolution){
                throw new IllegalTokenException(t, tokens[i].key());
            }
        }
    }

    public final boolean hasRest() throws ParsingException{
        return lookAhead() != Token.EOF;
    }

    public final void formatGenericsClose(TokenPointer ptr) throws ParsingException{
        Token t;
        switch((t = ptr.element()).resolution){
            case BITWISE_SIGNED_RIGHT_SHIFT -> l.split(ptr, new Token(">", t.ref.fix(-1)), new Token(">", t.ref));
            case BITWISE_UNSIGNED_RIGHT_SHIFT -> l.split(ptr, new Token(">", t.ref.fix(-1)), new Token(">>", t.ref));
            case GREATER_THAN -> {}
            default -> throw new IllegalTokenException(lookAhead(), "\">\""); 
        }
    }

    public final void formatGenericsClose() throws ParsingException{
        Token t;
        switch((t = lookAhead()).resolution){
            case BITWISE_SIGNED_RIGHT_SHIFT -> l.split(new Token(">", t.ref.fix(-1)), new Token(">", t.ref));
            case BITWISE_UNSIGNED_RIGHT_SHIFT -> l.split(new Token(">", t.ref.fix(-1)), new Token(">>", t.ref));
            case GREATER_THAN -> {}
            default -> throw new IllegalTokenException(lookAhead(), "\">\""); 
        }
    }

    public final TokenPointer getPointer() throws ParsingException{
        return l.new TokenPointer();
    }

}
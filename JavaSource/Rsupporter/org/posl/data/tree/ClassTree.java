package org.posl.data.tree;

import java.util.ArrayList;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.data.resolution.TypeIdentifier;
import org.posl.data.resolution.ExpressionIdentifier;

/**
 * A tree node for a class, interface, enum, record, or annotation
 * type declaration.
 *
 * For example:
 * <pre>
 *   <em>modifiers</em> class <em>simpleName</em> <em>typeParameters</em>
 *       implements <em>implementsClause</em>
 *       implements <em>implementsClause</em>
 *   {
 *       <em>members</em>
 *   }
 * </pre>
 *
 * @jls 8.1 Class Declarations
 * @jls 8.9 Enum Types
 * @jls 8.10 Record Types
 * @jls 9.1 Interface Declarations
 * @jls 9.6 Annotation Types
 *
 * @author me
 */

public record ClassTree(ModifiersTree modifiers, DeclarationType declType, IdentifierTree name, ArrayList<VariableTree> recordComponent, 
                        ArrayList<TypeParameterTree> typeParameters, Tree extendsClause, ArrayList<Tree> implementsClause,
                        ArrayList<Tree> permitsClause, ArrayList<EnumConstantTree> enumConstants, ArrayList<Tree> members)implements StatementTree, TypeIdentifier{
                            
    static ClassTree parse(DeclarationType declType, JavaTokenManager src) throws ParsingException{

        var modifiers = ModifiersTree.parse(src);
        if(!declType.isTypeDeclaration){
            throw new FatalParserError("Executed \"ClassTree.parse()\", but there is no class declaration.");
        }
        src.skip(declType.toTokens());
        var name = IdentifierTree.parse(src);
        ArrayList<VariableTree> recordComponents = null;
        var typeParameters = new ArrayList<TypeParameterTree>();
        Tree extendsClause = null;
        var implementsClause = new ArrayList<Tree>();
        var permitsClause = new ArrayList<Tree>();
        ArrayList<EnumConstantTree> enumConstants = null;

        if(src.match(JavaTS.LESS_THAN)){
            src.skip(JavaTS.LESS_THAN);
            if(declType.hasTypeParameterClause){
                typeParameters = Tree.getList(TypeParameterTree::parse, JavaTS.GREATER_THAN, src);
            }else{
                throw new ParsingException(String.format("A %s declaration cannot have type parameters", declType.name()));
            }
            src.skip(JavaTS.GREATER_THAN);
        }
        if(src.match(JavaTS.LEFT_ROUND_BRACKET)){
            src.skip(JavaTS.LEFT_ROUND_BRACKET);
            if(declType == DeclarationType.RECORD){
                recordComponents = Tree.getList(VariableTree::resolveSingleDeclaration, JavaTS.RIGHT_ROUND_BRACKET, src);
            }else{
                throw new ParsingException(String.format("Only records have record header."));
            }
            src.skip(JavaTS.RIGHT_ROUND_BRACKET);
        }
        if(src.match(JavaTS.EXTENDS)){
            src.skip(JavaTS.EXTENDS);
            if(declType == DeclarationType.CLASS){
                extendsClause = NameTree.resolveNonArrayTypeOrName(src);
            }else if(declType == DeclarationType.INTERFACE){
                implementsClause = Tree.getListWithoutBracket(NameTree::resolveNonArrayTypeOrName, JavaTS.COMMA, src);
            }else{
                throw new ParsingException(String.format("A %s declaration cannot have implements clause.", declType.name()));
            }
        }
        if(src.match(JavaTS.IMPLEMENTS)){
            src.skip(JavaTS.IMPLEMENTS);
            if(declType.hasImplementsClause){
                implementsClause = Tree.getListWithoutBracket(NameTree::resolveNonArrayTypeOrName, JavaTS.COMMA, src);
            }else{
                throw new ParsingException(String.format("A %s declaration cannot have implements clause.", declType.name()));
            }
        }
        if(src.match(JavaTS.PERMITS)){
            src.skip(JavaTS.PERMITS);
            if(declType.hasPermitsClause){
                permitsClause = Tree.getListWithoutBracket(NameTree::resolveTypeOrName, JavaTS.COMMA, src);
            }else{
                throw new ParsingException(String.format("A %s declaration cannot have implements clause.", declType.name()));
            }
        }

        src.skip(JavaTS.LEFT_CURLY_BRACKET);
        ArrayList<Tree> members = new ArrayList<>();
        if(declType == DeclarationType.ENUM){
            enumConstants = new ArrayList<>();
            while(!src.match(JavaTS.RIGHT_CURLY_BRACKET) && !src.match(JavaTS.SEMICOLON)){
                enumConstants.add(EnumConstantTree.parse(src));
                if(src.match(JavaTS.COMMA)){
                    src.skip(JavaTS.COMMA);
                }else{
                    break;
                }
            }
            if(src.match(JavaTS.SEMICOLON)){
                src.skip(JavaTS.SEMICOLON);
            }
        }
        while(!src.match(JavaTS.RIGHT_CURLY_BRACKET)){
            members.add(resolveMember(declType, src));
        }
        src.skip(JavaTS.RIGHT_CURLY_BRACKET);

        return new ClassTree(modifiers, declType, name, recordComponents, typeParameters, 
                                        extendsClause, implementsClause, permitsClause, enumConstants, members);

    }

    static ClassTree parse(Tree superType, JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.LEFT_CURLY_BRACKET);

        ArrayList<Tree> members = new ArrayList<>();
        while(!src.match(JavaTS.RIGHT_CURLY_BRACKET)){
            members.add(resolveMember(DeclarationType.CLASS, src));
        }
        src.skip(JavaTS.RIGHT_CURLY_BRACKET);
        return new ClassTree(ModifiersTree.EMPTY, DeclarationType.CLASS, IdentifierTree.EMPTY, null,
                                new ArrayList<>(), superType, new ArrayList<>(), new ArrayList<>(), null, members);
    }

    static Tree resolveMember(DeclarationType declType, JavaTokenManager src) throws ParsingException{
        if(src.match(JavaTS.SEMICOLON)){
            return EmptyStatementTree.parse(src);
        }else if(src.match(JavaTS.LEFT_CURLY_BRACKET) || src.match(JavaTS.STATIC, JavaTS.LEFT_CURLY_BRACKET)){
            return BlockTree.parse(src);
        }else{
            DeclarationType typeDecl = DeclarationType.lookAheadDeclType(src);
            return switch(typeDecl){
                case VARIABLE_DECLARATION -> VariableTree.resolveDeclarationStatement(src);
                case METHOD_DECLARATION -> MethodTree.parse(src);
                case NOT_DECLARATION -> throw new ParsingException("Illegal statement in the class body: \""+src.read()+"\"");
                default -> parse(typeDecl, src);
            };
        }
    }

    @Override
    public IdentifierTree simpleName(){
        return name;
    }

    @Override
    public TypeIdentifier getCorrespondingType(IdentifierTree id){
        return null;
    }

    @Override
    public ExpressionIdentifier getCorrespondingField(IdentifierTree id){
        return null;
    }

    @Override
    public MethodTree getCorrespondingMethod(IdentifierTree id, ArrayList<Type> argumentTypes){
        ArrayList<MethodTree> methods = new ArrayList<>();
        for(Tree member : members){
            if(member instanceof MethodTree m && m.name().equals(id)){
                methods.add(m);
            }
        }
        
    }

}

/*
 * Copyright (c) 1997, 2021, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package jdk.javadoc.internal.doclets.formats.html;

import java.util.Arrays;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import jdk.javadoc.internal.doclets.formats.html.markup.ContentBuilder;
import jdk.javadoc.internal.doclets.formats.html.markup.Entity;
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlStyle;
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlTree;
import jdk.javadoc.internal.doclets.formats.html.markup.Text;
import jdk.javadoc.internal.doclets.toolkit.Content;
import jdk.javadoc.internal.doclets.toolkit.MemberSummaryWriter;

/**
 * Writes nested class documentation in HTML format.
 *
 *  <p><b>This is NOT part of any supported API.
 *  If you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 */
public class NestedClassWriterImpl extends AbstractMemberWriter
    implements MemberSummaryWriter {

    public NestedClassWriterImpl(SubWriterHolderWriter writer, TypeElement typeElement) {
        super(writer, typeElement);
    }

    public NestedClassWriterImpl(SubWriterHolderWriter writer) {
        super(writer);
    }

    @Override
    public Content getMemberSummaryHeader(TypeElement typeElement,
            Content memberSummaryTree) {
        memberSummaryTree.add(MarkerComments.START_OF_NESTED_CLASS_SUMMARY);
        Content memberTree = new ContentBuilder();
        writer.addSummaryHeader(this, memberTree);
        return memberTree;
    }

    @Override
    public void addSummary(Content summariesList, Content content) {
        writer.addSummary(HtmlStyle.nestedClassSummary,
                HtmlIds.NESTED_CLASS_SUMMARY, summariesList, content);
    }

    @Override
    public void addSummaryLabel(Content memberTree) {
        Content label = HtmlTree.HEADING(Headings.TypeDeclaration.SUMMARY_HEADING,
                contents.nestedClassSummary);
        memberTree.add(label);
    }

    @Override
    public TableHeader getSummaryTableHeader(Element member) {
        Content label = utils.isInterface(member) ?
                contents.interfaceLabel : contents.classLabel;

        return new TableHeader(contents.modifierAndTypeLabel, label, contents.descriptionLabel);
    }

    @Override
    protected Table createSummaryTable() {
        List<HtmlStyle> bodyRowStyles = Arrays.asList(HtmlStyle.colFirst, HtmlStyle.colSecond,
                HtmlStyle.colLast);

        return new Table(HtmlStyle.summaryTable)
                .setCaption(contents.getContent("doclet.Nested_Classes"))
                .setHeader(getSummaryTableHeader(typeElement))
                .setColumnStyles(bodyRowStyles);
    }

    @Override
    public void addInheritedSummaryLabel(TypeElement typeElement, Content inheritedTree) {
        Content classLink = writer.getPreQualifiedClassLink(HtmlLinkInfo.Kind.MEMBER, typeElement);
        Content label;
        if (options.summarizeOverriddenMethods()) {
            label = Text.of(utils.isInterface(typeElement)
                    ? resources.getText("doclet.Nested_Classes_Interfaces_Declared_In_Interface")
                    : resources.getText("doclet.Nested_Classes_Interfaces_Declared_In_Class"));
        } else {
            label = Text.of(utils.isInterface(typeElement)
                    ? resources.getText("doclet.Nested_Classes_Interfaces_Inherited_From_Interface")
                    : resources.getText("doclet.Nested_Classes_Interfaces_Inherited_From_Class"));
        }
        HtmlTree labelHeading = HtmlTree.HEADING(Headings.TypeDeclaration.SUMMARY_HEADING, label);
        labelHeading.setId(htmlIds.forInheritedClasses(typeElement));
        labelHeading.add(Entity.NO_BREAK_SPACE);
        labelHeading.add(classLink);
        inheritedTree.add(labelHeading);
    }

    @Override
    protected void addSummaryLink(HtmlLinkInfo.Kind context, TypeElement typeElement, Element member,
                                  Content tdSummary) {
        Content memberLink = writer.getLink(new HtmlLinkInfo(configuration, context, (TypeElement)member)
                .style(HtmlStyle.typeNameLink));
        Content code = HtmlTree.CODE(memberLink);
        tdSummary.add(code);
    }

    @Override
    protected void addInheritedSummaryLink(TypeElement typeElement, Element member, Content linksTree) {
        linksTree.add(
                writer.getLink(new HtmlLinkInfo(configuration, HtmlLinkInfo.Kind.MEMBER,
                        (TypeElement)member)));
    }

    @Override
    protected void addSummaryType(Element member, Content tdSummaryType) {
        addModifierAndType(member, null, tdSummaryType);
    }

    @Override
    protected Content getSummaryLink(Element member) {
        return writer.getQualifiedClassLink(HtmlLinkInfo.Kind.MEMBER_DEPRECATED_PREVIEW, member);
    }
}

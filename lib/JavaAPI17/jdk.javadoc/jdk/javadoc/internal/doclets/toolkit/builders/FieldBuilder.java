/*
 * Copyright (c) 2003, 2021, Oracle and/or its affiliates. All rights reserved.
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

package jdk.javadoc.internal.doclets.toolkit.builders;

import java.util.*;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import jdk.javadoc.internal.doclets.toolkit.BaseOptions;
import jdk.javadoc.internal.doclets.toolkit.Content;
import jdk.javadoc.internal.doclets.toolkit.DocletException;
import jdk.javadoc.internal.doclets.toolkit.FieldWriter;

import static jdk.javadoc.internal.doclets.toolkit.util.VisibleMemberTable.Kind.*;

/**
 * Builds documentation for a field.
 *
 *  <p><b>This is NOT part of any supported API.
 *  If you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 */
public class FieldBuilder extends AbstractMemberBuilder {

    /**
     * The writer to output the field documentation.
     */
    private final FieldWriter writer;

    /**
     * The list of fields being documented.
     */
    private final List<? extends Element> fields;

    /**
     * The index of the current field that is being documented at this point
     * in time.
     */
    private VariableElement currentElement;

    /**
     * Construct a new FieldBuilder.
     *
     * @param context  the build context.
     * @param typeElement the class whose members are being documented.
     * @param writer the doclet specific writer.
     */
    private FieldBuilder(Context context,
                         TypeElement typeElement,
                         FieldWriter writer) {
        super(context, typeElement);
        this.writer = Objects.requireNonNull(writer);
        fields = getVisibleMembers(FIELDS);
    }

    /**
     * Construct a new FieldBuilder.
     *
     * @param context  the build context.
     * @param typeElement the class whose members are being documented.
     * @param writer the doclet specific writer.
     * @return the new FieldBuilder
     */
    public static FieldBuilder getInstance(Context context,
            TypeElement typeElement,
            FieldWriter writer) {
        return new FieldBuilder(context, typeElement, writer);
    }

    /**
     * Returns whether or not there are members to document.
     *
     * @return whether or not there are members to document
     */
    @Override
    public boolean hasMembersToDocument() {
        return !fields.isEmpty();
    }

    @Override
    public void build(Content contentTree) throws DocletException {
        buildFieldDoc(contentTree);
    }

    /**
     * Build the field documentation.
     *
     * @param detailsList the content tree to which the documentation will be added
     * @throws DocletException if there is a problem while building the documentation
     */
    protected void buildFieldDoc(Content detailsList) throws DocletException {
        if (!fields.isEmpty()) {
            Content fieldDetailsTreeHeader = writer.getFieldDetailsTreeHeader(detailsList);
            Content memberList = writer.getMemberList();

            for (Element element : fields) {
                currentElement = (VariableElement)element;
                Content fieldDocTree = writer.getFieldDocTreeHeader(currentElement);

                buildSignature(fieldDocTree);
                buildDeprecationInfo(fieldDocTree);
                buildPreviewInfo(fieldDocTree);
                buildFieldComments(fieldDocTree);
                buildTagInfo(fieldDocTree);

                memberList.add(writer.getMemberListItem(fieldDocTree));
            }
            Content fieldDetails = writer.getFieldDetails(fieldDetailsTreeHeader, memberList);
            detailsList.add(fieldDetails);
        }
    }

    /**
     * Build the signature.
     *
     * @param fieldDocTree the content tree to which the documentation will be added
     */
    protected void buildSignature(Content fieldDocTree) {
        fieldDocTree.add(writer.getSignature(currentElement));
    }

    /**
     * Build the deprecation information.
     *
     * @param fieldDocTree the content tree to which the documentation will be added
     */
    protected void buildDeprecationInfo(Content fieldDocTree) {
        writer.addDeprecated(currentElement, fieldDocTree);
    }

    /**
     * Build the preview information.
     *
     * @param fieldDocTree the content tree to which the documentation will be added
     */
    protected void buildPreviewInfo(Content fieldDocTree) {
        writer.addPreview(currentElement, fieldDocTree);
    }

    /**
     * Build the comments for the field.  Do nothing if
     * {@link BaseOptions#noComment()} is set to true.
     *
     * @param fieldDocTree the content tree to which the documentation will be added
     */
    protected void buildFieldComments(Content fieldDocTree) {
        if (!options.noComment()) {
            writer.addComments(currentElement, fieldDocTree);
        }
    }

    /**
     * Build the tag information.
     *
     * @param fieldDocTree the content tree to which the documentation will be added
     */
    protected void buildTagInfo(Content fieldDocTree) {
        writer.addTags(currentElement, fieldDocTree);
    }

    /**
     * Return the field writer for this builder.
     *
     * @return the field writer for this builder.
     */
    public FieldWriter getWriter() {
        return writer;
    }
}

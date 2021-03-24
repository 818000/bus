/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2021 aoju.org and other contributors.                      *
 *                                                                               *
 * Permission is hereby granted, free of charge, to any person obtaining a copy  *
 * of this software and associated documentation files (the "Software"), to deal *
 * in the Software without restriction, including without limitation the rights  *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     *
 * copies of the Software, and to permit persons to whom the Software is         *
 * furnished to do so, subject to the following conditions:                      *
 *                                                                               *
 * The above copyright notice and this permission notice shall be included in    *
 * all copies or substantial portions of the Software.                           *
 *                                                                               *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     *
 * THE SOFTWARE.                                                                 *
 *                                                                               *
 ********************************************************************************/
package org.aoju.bus.image.galaxy.io;

import org.aoju.bus.image.galaxy.data.Attributes;
import org.aoju.bus.image.galaxy.data.AttributesCoercion;

import javax.xml.transform.Templates;

/**
 * @author Kimi Liu
 * @version 6.2.1
 * @since JDK 1.8+
 */
public class XSLTAttributesCoercion implements AttributesCoercion {

    private final Templates templates;
    private final AttributesCoercion next;
    private boolean includeNameSpaceDeclaration;
    private boolean includeKeyword;
    private SAXTransformer.SetupTransformer setupTransformer;

    public XSLTAttributesCoercion(Templates templates, AttributesCoercion next) {
        this.templates = templates;
        this.next = next;
    }

    @Override
    public String remapUID(String uid) {
        return null != next ? next.remapUID(uid) : uid;
    }

    public boolean isIncludeNameSpaceDeclaration() {
        return includeNameSpaceDeclaration;
    }

    public void setIncludeNameSpaceDeclaration(boolean includeNameSpaceDeclaration) {
        this.includeNameSpaceDeclaration = includeNameSpaceDeclaration;
    }

    public XSLTAttributesCoercion includeNameSpaceDeclaration(boolean includeNameSpaceDeclaration) {
        setIncludeNameSpaceDeclaration(includeNameSpaceDeclaration);
        return this;
    }

    public boolean isIncludeKeyword() {
        return includeKeyword;
    }

    public void setIncludeKeyword(boolean includeKeyword) {
        this.includeKeyword = includeKeyword;
    }

    public XSLTAttributesCoercion includeKeyword(boolean includeKeyword) {
        setIncludeKeyword(includeKeyword);
        return this;
    }

    public SAXTransformer.SetupTransformer getSetupTransformer() {
        return setupTransformer;
    }

    public void setSetupTransformer(SAXTransformer.SetupTransformer setupTransformer) {
        this.setupTransformer = setupTransformer;
    }

    public XSLTAttributesCoercion setupTransformer(SAXTransformer.SetupTransformer setupTransformer) {
        setSetupTransformer(setupTransformer);
        return this;
    }

    @Override
    public void coerce(Attributes attrs, Attributes modified) {
        Attributes newAttrs;
        try {
            newAttrs = SAXTransformer.transform(
                    attrs, templates, includeNameSpaceDeclaration, includeKeyword, setupTransformer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (null != modified) {
            attrs.update(Attributes.UpdatePolicy.OVERWRITE, newAttrs, modified);
        } else {
            attrs.addAll(newAttrs);
        }
        if (null != next)
            next.coerce(attrs, modified);
    }

}

/*
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 ~                                                                               ~
 ~ The MIT License (MIT)                                                         ~
 ~                                                                               ~
 ~ Copyright (c) 2015-2025 miaixz.org and other contributors.                    ~
 ~                                                                               ~
 ~ Permission is hereby granted, free of charge, to any person obtaining a copy  ~
 ~ of this software and associated documentation files (the "Software"), to deal ~
 ~ in the Software without restriction, including without limitation the rights  ~
 ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     ~
 ~ copies of the Software, and to permit persons to whom the Software is         ~
 ~ furnished to do so, subject to the following conditions:                      ~
 ~                                                                               ~
 ~ The above copyright notice and this permission notice shall be included in    ~
 ~ all copies or substantial portions of the Software.                           ~
 ~                                                                               ~
 ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    ~
 ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      ~
 ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   ~
 ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        ~
 ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, ~
 ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     ~
 ~ THE SOFTWARE.                                                                 ~
 ~                                                                               ~
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
*/
package org.miaixz.bus.image.galaxy.dict.SIEMENS_MED_SMS_USG_ANTARES;

import org.miaixz.bus.image.galaxy.data.ElementDictionary;
import org.miaixz.bus.image.galaxy.data.VR;

/**
 * @author Kimi Liu
 * @since Java 17+
 */
public class PrivateElementDictionary extends ElementDictionary {

    public static final String PrivateCreator = "";

    public PrivateElementDictionary() {
        super("", PrivateTag.class);
    }

    @Override
    public String keywordOf(int tag) {
        return PrivateKeyword.valueOf(tag);
    }

    @Override
    public VR vrOf(int tag) {

        switch (tag & 0xFFFF00FF) {

            case PrivateTag._0019_xx03_:
            case PrivateTag._0019_xx21_:
            case PrivateTag._0019_xx22_:
            case PrivateTag._0019_xx43_:
            case PrivateTag._0019_xx54_:
            case PrivateTag._0019_xx60_:
            case PrivateTag._0019_xx61_:
            case PrivateTag._0019_xx62_:
            case PrivateTag._0019_xx63_:
            case PrivateTag._0019_xx80_:
            case PrivateTag._0019_xx81_:
                return VR.FD;

            case PrivateTag._0019_xx3B_:
            case PrivateTag._0019_xxA0_:
                return VR.LT;

            case PrivateTag._0019_xx00_:
            case PrivateTag._0019_xx0D_:
            case PrivateTag._0019_xx20_:
            case PrivateTag._0019_xx2E_:
            case PrivateTag._0019_xx40_:
            case PrivateTag._0019_xx42_:
            case PrivateTag._0019_xx66_:
            case PrivateTag._0019_xx87_:
                return VR.SH;

            case PrivateTag._0019_xx0C_:
            case PrivateTag._0019_xx0E_:
            case PrivateTag._0019_xx95_:
            case PrivateTag._0019_xx23_:
            case PrivateTag._0019_xx24_:
            case PrivateTag._0019_xx25_:
            case PrivateTag._0019_xx26_:
            case PrivateTag._0019_xx27_:
            case PrivateTag._0019_xx28_:
            case PrivateTag._0019_xx29_:
            case PrivateTag._0019_xx2A_:
            case PrivateTag._0019_xx2D_:
            case PrivateTag._0019_xx31_:
            case PrivateTag._0019_xx3A_:
            case PrivateTag._0019_xx41_:
            case PrivateTag._0019_xx44_:
            case PrivateTag._0019_xx46_:
            case PrivateTag._0019_xx47_:
            case PrivateTag._0019_xx48_:
            case PrivateTag._0019_xx49_:
            case PrivateTag._0019_xx65_:
            case PrivateTag._0019_xx67_:
            case PrivateTag._0019_xx69_:
            case PrivateTag._0019_xx6A_:
            case PrivateTag._0019_xx6C_:
            case PrivateTag._0019_xx72_:
            case PrivateTag._0019_xx82_:
            case PrivateTag._0019_xx83_:
            case PrivateTag._0019_xx86_:
            case PrivateTag._0019_xx88_:
                return VR.US;
        }
        return VR.UN;
    }

}

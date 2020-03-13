/*********************************************************************************
 *                                                                               *
 * The MIT License                                                               *
 *                                                                               *
 * Copyright (c) 2015-2020 aoju.org and other contributors.                      *
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
 ********************************************************************************/
package org.aoju.bus.http.secure;

import org.aoju.bus.http.Request;
import org.aoju.bus.http.Response;
import org.aoju.bus.http.Route;
import org.aoju.bus.http.UnoUrl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.List;

/**
 * @author Kimi Liu
 * @version 5.6.9
 * @since JDK 1.8+
 */
public final class NetAuthenticator implements Authenticator {

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        List<Challenge> challenges = response.challenges();
        Request request = response.request();
        UnoUrl url = request.url();
        boolean proxyAuthorization = response.code() == 407;
        Proxy proxy = route.proxy();

        for (int i = 0, size = challenges.size(); i < size; i++) {
            Challenge challenge = challenges.get(i);
            if (!"Basic".equalsIgnoreCase(challenge.scheme())) continue;

            PasswordAuthentication auth;
            if (proxyAuthorization) {
                InetSocketAddress proxyAddress = (InetSocketAddress) proxy.address();
                auth = java.net.Authenticator.requestPasswordAuthentication(
                        proxyAddress.getHostName(), getConnectToInetAddress(proxy, url), proxyAddress.getPort(),
                        url.scheme(), challenge.realm(), challenge.scheme(), url.url(),
                        java.net.Authenticator.RequestorType.PROXY);
            } else {
                auth = java.net.Authenticator.requestPasswordAuthentication(
                        url.host(), getConnectToInetAddress(proxy, url), url.port(), url.scheme(),
                        challenge.realm(), challenge.scheme(), url.url(), java.net.Authenticator.RequestorType.SERVER);
            }

            if (auth != null) {
                String credential = Credentials.basic(
                        auth.getUserName(), new String(auth.getPassword()), challenge.charset());
                return request.newBuilder()
                        .header(proxyAuthorization ? "Proxy-Authorization" : "Authorization", credential)
                        .build();
            }
        }

        return null;
    }

    private InetAddress getConnectToInetAddress(Proxy proxy, UnoUrl url) throws IOException {
        return (proxy != null && proxy.type() != Proxy.Type.DIRECT)
                ? ((InetSocketAddress) proxy.address()).getAddress()
                : InetAddress.getByName(url.host());
    }

}
/*******************************************************************************
 * Copyright (c) 2017 Red Hat Inc and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jens Reimann - initial API and implementation
 *******************************************************************************/
package de.dentrassi.hono.simulator.http;

import static de.dentrassi.hono.demo.common.Register.shouldRegister;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dentrassi.hono.demo.common.Register;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Response;

public abstract class Device {

    private static final Logger logger = LoggerFactory.getLogger(Device.class);

    protected static final MediaType JSON = MediaType.parse("application/json");

    protected static final String HONO_HTTP_PROTO = System.getenv("HONO_HTTP_PROTO");
    protected static final String HONO_HTTP_HOST = System.getenv("HONO_HTTP_HOST");
    protected static final String HONO_HTTP_PORT = System.getenv("HONO_HTTP_PORT");
    protected static final HttpUrl HONO_HTTP_URL;

    protected static final String METHOD = System.getenv().get("HTTP_METHOD");

    protected static final boolean AUTO_REGISTER = Boolean
            .parseBoolean(System.getenv().getOrDefault("AUTO_REGISTER", "true"));

    protected static final boolean NOAUTH = Boolean.parseBoolean(System.getenv().getOrDefault("HTTP_NOAUTH", "false"));

    static {
        String url = System.getenv("HONO_HTTP_URL");

        if (url == null && HONO_HTTP_HOST != null && HONO_HTTP_PORT != null) {
            final String proto = HONO_HTTP_PROTO != null ? HONO_HTTP_PROTO : "http";
            url = String.format("%s://%s:%s", proto, HONO_HTTP_HOST, HONO_HTTP_PORT);
        }

        if (url != null) {
            HONO_HTTP_URL = HttpUrl.parse(url);
        } else {
            HONO_HTTP_URL = null;
        }
    }

    protected final String auth;

    protected final Register register;

    protected final String user;

    protected final String deviceId;

    protected final String password;

    protected final String tenant;

    protected final Statistics telemetryStatistics;

    protected final Statistics eventStatistics;

    public Device(final String user, final String deviceId, final String tenant, final String password,
            final Register register, final Statistics telemetryStatistics,
            final Statistics eventStatistics) {

        this.register = register;
        this.user = user;
        this.deviceId = deviceId;
        this.tenant = tenant;
        this.password = password;
        this.telemetryStatistics = telemetryStatistics;
        this.eventStatistics = eventStatistics;

        this.auth = Credentials.basic(user + "@" + tenant, password);

    }

    public void register() throws Exception {
        if (shouldRegister()) {
            this.register.device(this.deviceId, this.user, this.password);
        }
    }

    public abstract void tickTelemetry();

    public abstract void tickEvent();

    protected void handleSuccess(final Response response, final Statistics statistics) {
    }

    protected void handleFailure(final Response response, final Statistics statistics) {
        final int code = response.code();

        statistics.error(code);

        try {
            switch (code) {
            case 401:
            case 403: //$FALL-THROUGH$
                if (AUTO_REGISTER && shouldRegister()) {
                    register();
                }
                break;
            }
        } catch (final Exception e) {
            logger.warn("Failed to handle failure", e);
        }
    }
}

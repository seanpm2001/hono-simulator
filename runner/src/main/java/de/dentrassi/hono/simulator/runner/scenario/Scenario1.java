/*******************************************************************************
 * Copyright (c) 2018 Red Hat Inc and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jens Reimann - initial API and implementation
 *******************************************************************************/
package de.dentrassi.hono.simulator.runner.scenario;

import de.dentrassi.hono.simulator.runner.Metrics;

public class Scenario1 extends AbstractSimpleScaleUpScenario {

    private static final int MAX_ADAPTER_INSTANCES = 16;

    private static final int MAX_SIMULATOR_INSTANCES = 48;

    public Scenario1(final Metrics metrics) {
        super(metrics, "scenario1");
    }

    @Override
    protected int getMaximumAdapterInstances() {
        return MAX_ADAPTER_INSTANCES;
    }

    @Override
    protected int getMaximumSimulatorInstances() {
        return MAX_SIMULATOR_INSTANCES;
    }

}
